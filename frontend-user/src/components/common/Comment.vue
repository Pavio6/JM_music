<template>
  <div class="comment-container">
    <!-- 评论输入区域 -->
    <Card class="mb-4" noPadding>
      <div class="flex flex-col gap-2">
        <div class="border-blue-500 border-1 border-style-solid rounded-xl">
          <TextArea
            v-model="commentContent"
            :placeholder="t('common.comment.placeholder')"
            class="w-full"
          />
        </div>
        <div class="flex justify-end">
          <Button
            type="primary"
            @click="submitComment"
            :disabled="!commentContent.trim()"
            :loading="submitLoading"
          >
            {{ t('common.comment.postComment') }}
          </Button>
        </div>
      </div>
    </Card>

    <!-- 评论列表 -->
    <div v-if="comments.length > 0" class="comment-list">
      <div v-for="comment in comments" :key="comment.commentId" class="mb-4">
        <CommentItem :comment="comment" @reply="handleReply" />
      </div>
    </div>
    <div v-else class="text-center text-white/60 py-8">
      {{ t('common.comment.noComments') }}
    </div>

    <!-- 分页 -->
    <div v-if="total > 0" class="mt-4">
      <Pagination
        :total="total"
        v-model:currentPage="currentPage"
        v-model:pageSize="pageSize"
        @change="handlePageChange"
      />
    </div>

    <!-- 回复弹窗 -->
    <Modal ref="replyModalRef">
      <div class="w-full flex flex-col gap-2">
        <div class="text-white/60 mb-2">
          {{ $t('common.comment.replyFor', { name: replyToComment?.userName || '' }) }}：
          <div class="text-white/80 mt-1">{{ replyToComment?.content || '' }}</div>
        </div>
        <div class="border-blue-500 border-1 border-style-solid rounded-xl">
          <TextArea
            v-model="replyContent"
            :placeholder="t('common.comment.placeholder')"
            class="w-full"
            :maxlength="200"
          />
        </div>
        <div class="flex justify-end mt-2">
          <Button
            type="primary"
            @click="submitReply"
            :loading="submitLoading"
            :disabled="!replyContent.trim()"
          >
            {{ t('common.comment.submit') }}
          </Button>
        </div>
      </div>
    </Modal>
  </div>
</template>

<script setup lang="ts">
  import { ref, onMounted, watch } from 'vue'
  import { getCommentList, addComment } from '@/api/modules/common/common'
  import type { CommentItem, CommentRequest, TargetType } from '@/api/modules/common/common.d'
  import type { Modal } from '.'
  import TextArea from '@/components/common/TextArea.vue'
  import { toast } from '@/utile'
  import { useI18n } from 'vue-i18n'

  defineOptions({
    name: 'Comment'
  })

  interface Props {
    /** 目标id */
    targetId?: string
    /** 类型 */
    targetType?: (typeof TargetType)[keyof typeof TargetType]
  }

  const props = defineProps<Props>()

  const { t } = useI18n()

  // 评论列表数据
  const comments = ref<CommentItem[]>([])
  const total = ref(0)
  const currentPage = ref(1)
  const pageSize = ref(10)

  // 评论内容
  const commentContent = ref('')

  // 回复相关
  const showReplyModal = ref(false)
  const replyContent = ref('')
  const replyToComment = ref<CommentItem | null>(null)
  const replyModalRef = ref<InstanceType<typeof Modal> | null>(null)
  const submitLoading = ref(false)

  // 加载评论列表
  const loadComments = async () => {
    try {
      const params: CommentRequest = {
        targetId: props.targetId,
        targetType: props.targetType,
        pageNum: currentPage.value,
        pageSize: pageSize.value
      }

      const res = await getCommentList(params)
      comments.value = res.data.records || []
      total.value = res.data.total || 0
    } catch (error) {
      console.error('获取评论列表失败', error)
    }
  }

  // 提交评论
  const submitComment = async () => {
    if (!commentContent.value.trim()) {
      toast.info(t('common.comment.empty'))
      return
    }

    try {
      submitLoading.value = true
      const params = {
        targetId: props.targetId,
        targetType: props.targetType,
        content: commentContent.value.trim()
      }

      const res = await addComment(params)
      if (res.data) {
        toast.success(t('common.comment.success'))
        commentContent.value = ''
        // 重新加载评论列表
        loadComments()
      } else {
        throw new Error(res.message)
      }
    } catch (error) {
      if (error instanceof Error) {
        toast.error(error.message)
      }
      console.error('提交评论失败', error)
    } finally {
      setTimeout(() => {
        submitLoading.value = false
      }, 200)
    }
  }

  // 处理回复
  const handleReply = (comment: CommentItem) => {
    replyToComment.value = comment
    replyModalRef.value?.open({
      title: t('common.comment.replyTitle'),
      showConfirm: true,
      showFoot: false
    })
  }

  // 提交回复
  const submitReply = async () => {
    if (!replyContent.value.trim() || !replyToComment.value?.commentId) return

    try {
      submitLoading.value = true
      const params = {
        targetId: props.targetId,
        targetType: props.targetType,
        content: replyContent.value.trim(),
        parentCommentId: replyToComment.value.commentId.toString()
      }

      const res = await addComment(params)
      if (res.data) {
        replyContent.value = ''
        showReplyModal.value = false
        // 重新加载评论列表
        loadComments()
        replyModalRef.value?.close()
      } else {
        throw new Error(res.message)
      }
    } catch (error) {
      if (error instanceof Error) {
        toast.error(error.message)
      }
      console.error('评论失败', error)
    } finally {
      setTimeout(() => {
        submitLoading.value = false
      }, 200)
    }
  }

  // 分页变化
  const handlePageChange = (page: number, size: number) => {
    currentPage.value = page
    pageSize.value = size
    loadComments()
  }

  // 监听属性变化，重新加载评论
  watch(
    () => [props.targetId, props.targetType],
    () => {
      currentPage.value = 1
      loadComments()
    }
  )

  // 组件挂载时加载评论
  onMounted(() => {
    loadComments()
  })
</script>

<style scoped>
  .comment-container {
    width: 100%;
  }
</style>
