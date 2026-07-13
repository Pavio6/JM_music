<template>
  <div>
    <h2 class="text-xl font-bold mb-4">{{ t('user.privacy') }}</h2>
    <div class="grid grid-cols-2 gap-4">
      <div class="setting-item">
        <label class="block mb-2">{{ t('user.profileVisibility') }}</label>
        <Button
          @click="
            () => {
              userInfo.userPrivacy.profileVisibility =
                userInfo.userPrivacy.profileVisibility === Visibility.Public
                  ? Visibility.Private
                  : Visibility.Public
              updatePrivacySettings()
            }
          "
          :type="
            userInfo.userPrivacy.profileVisibility === Visibility.Public ? 'primary' : 'default'
          "
        >
          <Icon
            :icon="
              userInfo.userPrivacy.profileVisibility === Visibility.Public
                ? 'mdi:lock-open'
                : 'mdi:lock'
            "
            class="mr-2"
          />
          {{
            userInfo.userPrivacy.profileVisibility === Visibility.Public
              ? t('user.public')
              : t('user.private')
          }}
        </Button>
      </div>
      <div class="setting-item">
        <label class="block mb-2">{{ t('user.playlistVisibility') }}</label>
        <Button
          @click="
            () => {
              userInfo.userPrivacy.playlistVisibility =
                userInfo.userPrivacy.playlistVisibility === Visibility.Public
                  ? Visibility.Private
                  : Visibility.Public
              updatePrivacySettings()
            }
          "
          :type="
            userInfo.userPrivacy.playlistVisibility === Visibility.Public ? 'primary' : 'default'
          "
        >
          <Icon
            :icon="
              userInfo.userPrivacy.playlistVisibility === Visibility.Public
                ? 'mdi:lock-open'
                : 'mdi:lock'
            "
            class="mr-2"
          />
          {{
            userInfo.userPrivacy.playlistVisibility === Visibility.Public
              ? t('user.public')
              : t('user.private')
          }}
        </Button>
      </div>
      <div class="setting-item">
        <label class="block mb-2">{{ t('user.followingVisibility') }}</label>
        <Button
          @click="
            () => {
              userInfo.userPrivacy.followingVisibility =
                userInfo.userPrivacy.followingVisibility === Visibility.Public
                  ? Visibility.Private
                  : Visibility.Public
              updatePrivacySettings()
            }
          "
          :type="
            userInfo.userPrivacy.followingVisibility === Visibility.Public ? 'primary' : 'default'
          "
        >
          <Icon
            :icon="
              userInfo.userPrivacy.followingVisibility === Visibility.Public
                ? 'mdi:lock-open'
                : 'mdi:lock'
            "
            class="mr-2"
          />
          {{
            userInfo.userPrivacy.followingVisibility === Visibility.Public
              ? t('user.public')
              : t('user.private')
          }}
        </Button>
      </div>
      <div class="setting-item">
        <label class="block mb-2">{{ t('user.followersVisibility') }}</label>
        <Button
          @click="
            () => {
              userInfo.userPrivacy.followersVisibility =
                userInfo.userPrivacy.followersVisibility === Visibility.Public
                  ? Visibility.Private
                  : Visibility.Public
              updatePrivacySettings()
            }
          "
          :type="
            userInfo.userPrivacy.followersVisibility === Visibility.Public ? 'primary' : 'default'
          "
        >
          <Icon
            :icon="
              userInfo.userPrivacy.followersVisibility === Visibility.Public
                ? 'mdi:lock-open'
                : 'mdi:lock'
            "
            class="mr-2"
          />
          {{
            userInfo.userPrivacy.followersVisibility === Visibility.Public
              ? t('user.public')
              : t('user.private')
          }}
        </Button>
      </div>
      <div class="setting-item">
        <label class="block mb-2">{{ t('user.messagePermission') }}</label>
        <Button
          @click="
            () => {
              userInfo.userPrivacy.messagePermission =
                userInfo.userPrivacy.messagePermission === MessagePermission.All
                  ? MessagePermission.Followers
                  : MessagePermission.All
              updatePrivacySettings()
            }
          "
          :type="
            userInfo.userPrivacy.messagePermission === MessagePermission.All ? 'primary' : 'default'
          "
        >
          <Icon
            :icon="
              userInfo.userPrivacy.messagePermission === MessagePermission.All
                ? 'mdi:account-multiple'
                : 'mdi:account-group'
            "
            class="mr-2"
          />
          {{
            userInfo.userPrivacy.messagePermission === MessagePermission.All
              ? t('user.all')
              : t('user.followers')
          }}
        </Button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { storeToRefs } from 'pinia'
  import { useUserStore } from '@/stores/user'
  import { Visibility, MessagePermission } from '@/api/modules/user/user.d'
  import { updatePrivacy } from '@/api/modules/user/user'
  import { toast } from '@/utile'
  import { useI18n } from 'vue-i18n'

  const { t } = useI18n()

  const { userInfo } = storeToRefs(useUserStore())

  async function updatePrivacySettings() {
    try {
      const {
        profileVisibility,
        playlistVisibility,
        followersVisibility,
        followingVisibility,
        messagePermission
      } = userInfo.value.userPrivacy
      const { data } = await updatePrivacy({
        profileVisibility,
        playlistVisibility,
        followersVisibility,
        followingVisibility,
        messagePermission
      })

      if (data) {
        toast.success(t('user.privacyUpdateSuccess'))
      }
    } catch (error) {
      console.error(t('user.privacyUpdateError'), error)
    }
  }
</script>

<style scoped></style>
