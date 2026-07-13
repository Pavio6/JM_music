<template>
  <div class="feedback-page space-y-6">
    <div class="flex">
      <h2 class="text-xl font-bold">{{ t('common.feedback.myFeedback') }}</h2>
    </div>
    <Card size="lg">
      <div class="flex justify-between items-center mb-4">
        <Button type="primary" @click="openAddModal">{{ t('common.feedback.addFeedback') }}</Button>
      </div>
      <Table :columns="columns" :data="feedbackList">
        <template #cell-feedbackType="{ row }">
          <span>{{ formatType(row.feedbackType) }}</span>
        </template>
        <template #cell-status="{ row }">
          <span :class="statusClass(row.status)">{{ statusText(row.status) }}</span>
        </template>
        <template #cell-action="{ row }">
          <div class="flex flex-row space-x-2">
            <Button type="default" @click="viewDetail(row)">{{
              t('common.feedback.detail')
            }}</Button>
            <Button v-if="row.status === 'PENDING'" type="primary" @click="openEditModal(row)">{{
              t('common.feedback.edit')
            }}</Button>
            <Button
              v-if="row.status === 'PENDING' || row.status === 'REJECTED'"
              type="danger"
              @click="openDeleteModal(row.feedbackId)"
              >{{ t('common.feedback.delete') }}</Button
            >
          </div>
        </template>
      </Table>
      <div class="mt-4 flex justify-end">
        <Pagination
          :total="feedbackList.length"
          v-model:currentPage="currentPage"
          v-model:pageSize="pageSize"
        />
      </div>
    </Card>
    <!-- 新增/编辑弹窗 -->
    <Modal ref="editModalRef">
      <form @submit.prevent="() => {}" class="w-full">
        <div class="mb-2">
          <Input
            v-model="form.feedbackTitle"
            :placeholder="t('common.feedback.feedbackTitle')"
            required
            :maxlength="50"
          />
        </div>
        <div class="mb-2">
          <Select v-model="form.feedbackType">
            <option v-for="option in FeedBackTypeOptions" :key="option.value" :value="option.value">
              {{ option.label }}
            </option>
          </Select>
        </div>
        <div class="mb-2">
          <TextArea
            v-model="form.feedbackContent"
            class="input"
            rows="4"
            required
            :maxlength="500"
            :placeholder="t('common.feedback.feedbackContent')"
          />
        </div>
        <div class="flex justify-end gap-2 mt-4">
          <Button type="default" @click="closeEditModal">{{ t('common.feedback.cancel') }}</Button>
          <Button type="primary" :loading="formLoading" @click="submitForm">{{
            isEdit ? t('common.feedback.save') : t('common.feedback.submit')
          }}</Button>
        </div>
      </form>
    </Modal>
    <!-- 详情弹窗 -->
    <Modal ref="detailModalRef">
      <div class="w-full flex flex-col gap-4 text-white/80 leading-relaxed">
        <div>
          <b class="text-white/90">{{ t('common.feedback.title') }}：</b>{{ detail?.feedbackTitle }}
        </div>
        <div>
          <b class="text-white/90">{{ t('common.feedback.type') }}：</b
          >{{ formatType(detail?.feedbackType) }}
        </div>
        <div>
          <b class="text-white/90">{{ t('common.feedback.content') }}：</b
          >{{ detail?.feedbackContent }}
        </div>
        <div>
          <b class="text-white/90">{{ t('common.feedback.status') }}：</b
          >{{ statusText(detail?.status) }}
        </div>
        <div>
          <b class="text-white/90">{{ t('common.feedback.createTime') }}：</b
          >{{ detail?.createTime }}
        </div>
        <div v-if="detail?.adminReply">
          <b class="text-white/90">{{ t('common.feedback.adminReply') }}：</b
          >{{ detail.adminReply }}
        </div>
        <div v-if="detail?.replyTime">
          <b class="text-white/90">{{ t('common.feedback.replyTime') }}：</b>{{ detail.replyTime }}
        </div>
      </div>
    </Modal>
    <Modal ref="deleteModalRef">
      <div class="text-center w-full">
        <p class="mb-4 text-lg font-bold text-white">{{ t('common.feedback.confirmDelete') }}</p>
        <div class="flex justify-center gap-4">
          <Button type="default" @click="closeDeleteModal">{{
            t('common.feedback.cancel')
          }}</Button>
          <Button type="danger" @click="handleDelete">{{ t('common.feedback.delete') }}</Button>
        </div>
      </div>
    </Modal>
  </div>
</template>

<script lang="ts" setup>
  import { ref, reactive, onMounted, computed } from 'vue'
  import Table from '@/components/common/Table.vue'
  import Modal from '@/components/common/Modal.vue'
  import Button from '@/components/common/Button.vue'
  import Card from '@/components/common/Card.vue'
  import Pagination from '@/components/common/Pagination.vue'
  import {
    addFeedback,
    editFeedback,
    getFeedbackList,
    getFeedbackDetail,
    deleteFeedback
  } from '@/api/modules/feedback/feedback'
  import type {
    FeedbackResponse,
    FeecbackRequest,
    FeedbackDetail,
    FeedbackType,
    FeedbackStatus
  } from '@/api/modules/feedback/feedback.d'
  import { useRouter } from 'vue-router'
  import { toast } from '@/utile'
  import { useI18n } from 'vue-i18n'

  const { t } = useI18n()
  const router = useRouter()
  const back = () => {
    router.back()
  }

  const FeedBackTypeOptions: {
    label: string
    value: (typeof FeedbackType)[keyof typeof FeedbackType]
  }[] = [
    { label: t('common.feedback.bug'), value: 'BUG' },
    { label: t('common.feedback.complaint'), value: 'COMPLAINT' },
    { label: t('common.feedback.suggestion'), value: 'SUGGESTION' },
    { label: t('common.feedback.featureRequest'), value: 'FEATURE' },
    { label: t('common.feedback.other'), value: 'OTHER' }
  ]

  const formatType = (type?: string) => {
    return FeedBackTypeOptions.find((option) => option.value === type)?.label || type
  }

  const statusText = (status?: (typeof FeedbackStatus)[keyof typeof FeedbackStatus]) => {
    if (status === 'PENDING') return t('common.feedback.pending')
    if (status === 'PROCESSING') return t('common.feedback.processing')
    if (status === 'RESOLVED') return t('common.feedback.resolved')
    if (status === 'REJECTED') return t('common.feedback.rejected')
    return status || '-'
  }
  const statusClass = (status?: (typeof FeedbackStatus)[keyof typeof FeedbackStatus]) => {
    if (status === 'PENDING') return 'text-orange-500'
    if (status === 'PROCESSING') return 'text-green-500'
    if (status === 'RESOLVED') return 'text-green-500'
    if (status === 'REJECTED') return 'text-red-500'
    return ''
  }

  const columns = [
    { key: 'feedbackTitle', title: t('common.feedback.title') },
    { key: 'feedbackType', title: t('common.feedback.type') },
    { key: 'status', title: t('common.feedback.status') },
    { key: 'createTime', title: t('common.feedback.createTime') },
    { key: 'action', title: t('common.feedback.action'), cellClass: 'min-w-60' }
  ]

  const feedbackList = ref<FeedbackResponse[]>([])
  const currentPage = ref(1)
  const pageSize = ref(10)

  const editModalRef = ref<InstanceType<typeof Modal> | null>(null)
  const detailModalRef = ref<InstanceType<typeof Modal> | null>(null)
  const isEdit = ref(false)
  const formLoading = ref(false)
  const form = reactive<FeecbackRequest>({
    feedbackTitle: '',
    feedbackType: 'BUG',
    feedbackContent: ''
  })
  const editingId = ref<number | null>(null)
  const detail = ref<FeedbackDetail | null>(null)

  const fetchList = async () => {
    const res = await getFeedbackList()
    if (res && res.data) {
      feedbackList.value = res.data
    }
  }

  const openAddModal = () => {
    isEdit.value = false
    Object.assign(form, { feedbackTitle: '', feedbackType: 'BUG', feedbackContent: '' })
    editModalRef.value?.open({
      title: t('common.feedback.addFeedback'),
      showConfirm: false,
      showCancel: false
    })
  }
  const openEditModal = (row: FeedbackResponse) => {
    isEdit.value = true
    Object.assign(form, {
      feedbackTitle: row.feedbackTitle,
      feedbackType: row.feedbackType,
      feedbackContent: row.adminReply || ''
    })
    editingId.value = row.feedbackId
    editModalRef.value?.open({
      title: t('common.feedback.editFeedback'),
      showConfirm: false,
      showCancel: false
    })
  }
  const closeEditModal = () => {
    editModalRef.value?.close()
    editingId.value = null
  }
  const submitForm = async () => {
    formLoading.value = true
    try {
      if (isEdit.value && editingId.value) {
        await editFeedback(editingId.value, form)
      } else {
        await addFeedback(form)
      }
      await fetchList()
      closeEditModal()
    } finally {
      formLoading.value = false
    }
  }

  const deleteModalRef = ref<InstanceType<typeof Modal> | null>(null)
  const deleteId = ref<number | null>(null)
  const openDeleteModal = (id: number) => {
    deleteId.value = id
    deleteModalRef.value?.open({
      title: t('common.feedback.deleteConfirm'),
      showConfirm: false,
      showCancel: false
    })
  }
  const closeDeleteModal = () => {
    deleteModalRef.value?.close()
    deleteId.value = null
  }

  const handleDelete = async () => {
    if (deleteId.value) {
      deleteFeedback(deleteId.value)
        .then(() => {
          fetchList()
          closeDeleteModal()
        })
        .catch((err) => {
          console.log(err)
          toast.error(t('common.feedback.deleteFailed'))
        })
    }
  }

  const viewDetail = async (row: FeedbackResponse) => {
    const res = await getFeedbackDetail(row.feedbackId)
    if (res && res.data) {
      detail.value = res.data
      detailModalRef.value?.open({
        title: t('common.feedback.feedbackDetail'),
        showConfirm: false,
        cancelText: t('common.feedback.close')
      })
    }
  }

  onMounted(fetchList)
</script>

<style scoped>
  .feedback-page .input {
    width: 100%;
    padding: 0.5rem 0.75rem;
    border-radius: 0.5rem;
    background: #23272f;
    border: 1px solid #3b4252;
    color: #fff;
    outline: none;
  }
  .feedback-page .input:focus {
    border-color: #2563eb;
  }
</style>
