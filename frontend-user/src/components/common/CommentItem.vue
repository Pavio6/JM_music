<template>
  <Card class="comment-item" noPadding>
    <div class="flex gap-3">
      <!-- 用户头像 -->
      <div class="w-10 h-10 rounded-full overflow-hidden flex-shrink-0">
        <Image
          :src="comment.userAvatar"
          type="avatar"
          :alt="t('common.userAvatar')"
          class="w-full h-full object-cover"
        />
      </div>

      <!-- 评论内容 -->
      <div class="flex-1">
        <!-- 用户信息和评论时间 -->
        <div class="flex justify-between items-center mb-1">
          <span
            class="font-medium text-white cursor-pointer"
            @click="toUserDetail(comment.userId)"
            >{{ comment.userName }}</span
          >
          <span class="text-sm text-white/50">{{ comment.createTime }}</span>
        </div>

        <!-- 评论内容 -->
        <p class="text-white/80 mb-2">{{ comment.content }}</p>

        <!-- 操作按钮 -->
        <div class="flex justify-between items-center">
          <div class="flex items-center gap-2">
            <span
              v-if="level < 3"
              class="text-sm text-white/50 cursor-pointer hover:text-blue-400"
              @click="$emit('reply', comment)"
            >
              {{ t('common.comment.reply') }}
            </span>
            <span
              v-if="comment.children && comment.children.length > 0"
              class="text-sm text-white/50 cursor-pointer hover:text-blue-400"
              @click="isExpanded = !isExpanded"
            >
              {{
                isExpanded
                  ? t('common.foldUp')
                  : `${t('common.expand')}(${comment.children.length})`
              }}
            </span>
          </div>
          <div class="flex items-center gap-1 text-white/50">
            <Icon
              :icon="comment.isLike ? 'prime:thumbs-up-fill' : 'prime:thumbs-up'"
              class="text-size-xl cursor-pointer"
              :class="comment.isLike ? 'text-blue-400' : ''"
              @click="handleLike(comment)"
            />
            <span class="text-sm">{{ comment.likeCount || 0 }}</span>
          </div>
        </div>

        <!-- 子评论递归渲染，最多三级 -->
        <Transition name="slide">
          <div
            v-if="comment.children && comment.children.length > 0 && isExpanded"
            class="mt-4 pr-0 border-l border-gray-700"
          >
            <CommentItem
              v-for="childComment in comment.children"
              :key="childComment.commentId"
              :comment="childComment"
              :level="level + 1"
              @reply="$emit('reply', $event)"
            />
          </div>
        </Transition>
      </div>
    </div>
  </Card>
</template>

<script lang="ts" setup>
  import type { CommentItem } from '@/api/modules/common/common.d'
  import { defineProps, defineEmits, ref } from 'vue'
  import { likeComment } from '@/api/modules/common/common'
  import { toast } from '@/utile'
  import { useRouter } from 'vue-router'
  import { useI18n } from 'vue-i18n'

  defineOptions({
    name: 'CommentItem'
  })

  interface Props {
    comment: CommentItem
    level?: number
  }

  const props = withDefaults(defineProps<Props>(), {
    comment: () => ({}),
    level: 1
  })

  defineEmits<{
    (e: 'reply', comment: CommentItem): void
  }>()

  const { t } = useI18n()
  const isExpanded = ref(false)

  // 点赞处理
  const handleLike = async (comment: CommentItem) => {
    if (!comment.commentId && comment.commentId !== 0) {
      toast.error(t('common.likeFail'))
      return
    }
    try {
      const res = await likeComment(Number(comment.commentId), !(comment?.isLike ?? false))
      if (res.data) {
        if (comment.isLike) {
          comment.likeCount = (comment.likeCount || 1) - 1
        } else {
          comment.likeCount = (comment.likeCount || 0) + 1
        }
        comment.isLike = !(comment?.isLike ?? false)
        toast.success(
          comment.isLike ? t('common.likeSuccessful') : t('common.cancelLikeSuccessful')
        )
      } else {
        toast.error(res.message || t('common.likeFail'))
      }
    } catch (e) {
      toast.error(t('common.likeFail'))
    }
  }

  const router = useRouter()

  const toUserDetail = (userId?: number | string) => {
    if (!userId) return
    router.push({
      path: `/user/${userId}`
    })
  }
</script>

<style scoped></style>
