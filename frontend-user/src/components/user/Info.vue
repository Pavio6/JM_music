<template>
  <form @submit.prevent="handleUpdateProfile" class="w-full space-y-4">
    <div class="form-group">
      <label class="block mb-1 text-gray-700 font-medium">{{ t('common.user.name') }}</label>
      <Input v-model="editForm.userName" required />
    </div>
    <div class="form-group">
      <label class="block mb-1 text-gray-700 font-medium">{{ t('common.user.userBio') }}</label>
      <TextArea v-model="editForm.userBio" rows="3" />
    </div>
    <div class="form-group">
      <label class="block mb-1 text-gray-700 font-medium">{{ t('common.user.userSex') }}</label>
      <Select v-model="editForm.userSex" class="" required>
        <option :value="0">{{ t('common.user.girl') }}</option>
        <option :value="1">{{ t('common.user.boy') }}</option>
      </Select>
    </div>
    <div class="form-group">
      <label class="block mb-1 text-gray-700 font-medium">{{ t('common.user.userBirth') }}</label>
      <Input v-model="editForm.userBirth" type="date" required :max="today" />
    </div>
    <div class="form-group">
      <label class="block mb-1 text-gray-700 font-medium">{{ t('common.user.userAvatar') }}</label>
      <FileInput @change="handleAvatarUpload" accept="image/*" />
      <div v-if="avatarPreview" class="mt-2">
        <Image
          :src="avatarPreview"
          type="avatar"
          :alt="t('common.user.avatarPreview')"
          class="w-20 h-20 rounded-full object-cover"
        />
      </div>
    </div>
    <div class="flex justify-end gap-2">
      <Button type="default" @click="$emit('close')"> {{ t('common.cancel') }} </Button>
      <Button type="primary" @click="handleUpdateProfile"> {{ t('common.save') }} </Button>
    </div>
  </form>
</template>

<script setup lang="ts">
  import { ref, computed } from 'vue'
  import { storeToRefs } from 'pinia'
  import { useUserStore } from '@/stores/user'
  import { useI18n } from 'vue-i18n'

  const { t } = useI18n()
  const emit = defineEmits(['close', 'update'])
  const userStore = useUserStore()
  const { userInfo } = storeToRefs(userStore)

  // 编辑表单相关
  const avatarPreview = ref('')
  const editForm = ref({
    userName: userInfo.value.userName,
    userBio: userInfo.value.userBio,
    userSex: userInfo.value.userSex,
    userBirth: userInfo.value.userBirth,
    userAvatar: '' as File | string
  })

  // 计算属性
  const today = computed(() => {
    const date = new Date()
    return date.toISOString().split('T')[0]
  })

  // 方法
  function handleAvatarUpload(event: Event) {
    const input = event.target as HTMLInputElement
    if (input.files && input.files[0]) {
      editForm.value.userAvatar = input.files[0]
      avatarPreview.value = URL.createObjectURL(input.files[0])
    }
  }

  async function handleUpdateProfile() {
    const success = await userStore.updateUserInfo({
      userName: editForm.value.userName,
      userBio: editForm.value.userBio,
      userSex: editForm.value.userSex,
      userBirth: editForm.value.userBirth,
      userAvatar: editForm.value.userAvatar
    })

    if (success) {
      emit('update')
      emit('close')
    }
  }
</script>

<style scoped></style>
