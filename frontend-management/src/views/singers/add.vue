<template>
  <div class="add-singer-container">
    <h1 class="page-title">{{ isEdit ? '编辑歌手' : '新增歌手' }}</h1>
    <div class="content-box">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px" class="singer-form">
        <el-form-item label="歌手头像" prop="singerAvatar">
          <div class="avatar-container">
            <el-upload class="avatar-uploader" :show-file-list="false" :before-upload="beforeAvatarUpload"
              :http-request="customUpload">
              <img v-if="form.singerAvatar" :src="form.singerAvatar" class="avatar" />
              <el-icon v-else class="avatar-uploader-icon">
                <Plus />
              </el-icon>
            </el-upload>
            <div v-if="form.singerAvatar" class="delete-icon" @click="handleDeleteAvatar">
              <el-icon>
                <Close />
              </el-icon>
            </div>
          </div>
        </el-form-item>

        <el-form-item label="歌手名称" prop="singerName">
          <el-input v-model="form.singerName" placeholder="请输入歌手名称" :disabled="isEdit" />
        </el-form-item>

        <el-form-item label="国籍" prop="singerNat">
          <el-select v-model="form.singerNat" filterable placeholder="请选择国籍" class="country-select">
            <el-option v-for="country in countries" :key="country.code" :label="country.name" :value="country.name" />
          </el-select>
        </el-form-item>

        <el-form-item label="性别" prop="singerSex">
          <el-radio-group v-model="form.singerSex">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="0">女</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="出生日期" prop="singerBirth">
          <div class="demo-date-picker">
            <div class="block">
              <el-date-picker v-model="form.singerBirth" type="date" placeholder="请选择出生日期" :size="size"
                format="YYYY年MM月DD日" value-format="YYYY-MM-DD" :disabled-date="disableFutureDates" />
            </div>
          </div>
        </el-form-item>

        <el-form-item label="出道日期" prop="singerDebutDate">
          <div class="demo-date-picker">
            <div class="block">
              <el-date-picker v-model="form.singerDebutDate" type="date" placeholder="请选择出道日期" :size="size"
                format="YYYY年MM月DD日" value-format="YYYY-MM-DD" :disabled-date="disableFutureDates" />
            </div>
          </div>
        </el-form-item>

        <el-form-item label="个人简介" prop="singerBio">
          <el-input v-model="form.singerBio" type="textarea" rows="4" placeholder="请输入个人简介" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm">提交</el-button>
          <el-button @click="goBack">返回</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
  import { ref, onMounted } from 'vue';
  import { ElMessage } from 'element-plus';
  import { Plus, Close } from '@element-plus/icons-vue';
  import { useRouter, useRoute } from 'vue-router';
  import { uploadFile, addSinger, deleteFile } from '@/api/auth';
  import { getAllCountries } from '@/utils/countries';
  import { getSingerDetail } from '@/api/singer';

  const router = useRouter();
  const route = useRoute();
  const formRef = ref(null);
  const countries = ref([]);
  const size = ref('large');
  const isEdit = ref(false);

  // 初始化表单数据
  const form = ref({
    singerName: '',
    singerNat: '',
    singerBio: '',
    singerBirth: '',
    singerDebutDate: '',
    singerSex: 1,
    singerAvatar: ''
  });

  // 初始化数据
  const initData = async () => {
    const id = route.params.id;
    if (id) {
      isEdit.value = true;
      try {
        const res = await getSingerDetail(id);
        if (res.state) {
          const singerData = res.data;
          form.value = {
            ...singerData,
            singerBirth: singerData.singerBirth || '',
            singerDebutDate: singerData.singerDebutDate || '',
            singerBio: singerData.singerBio || '',
            singerId: id
          };
        } else {
          ElMessage.error(res.message || '获取歌手信息失败');
        }
      } catch (error) {
        ElMessage.error('获取歌手信息失败');
      }
    }
  };

  // 初始化国家列表和歌手数据
  onMounted(async () => {
    countries.value = getAllCountries();
    await initData();
  });

  const rules = {
    singerName: [{ required: true, message: '请输入歌手名称', trigger: 'blur' }],
    singerNat: [{ required: true, message: '请选择国籍', trigger: 'change' }],
    singerBirth: [{ required: true, message: '请选择出生日期', trigger: 'change' }],
    singerSex: [{ required: true, message: '请选择性别', trigger: 'change' }]
  };

  const beforeAvatarUpload = (file) => {
    const isImage = file.type.startsWith('image/');
    const isLt10M = file.size / 1024 / 1024 < 10;

    if (!isImage) {
      ElMessage.error('上传头像图片只能是图片格式!');
      return false;
    }
    if (!isLt10M) {
      ElMessage.error('上传头像图片大小不能超过 10MB!');
      return false;
    }
    return true;
  };

  const customUpload = async (options) => {
    try {
      const res = await uploadFile(options.file, 'SINGER_AVATAR');
      if (res.state) {
        form.value.singerAvatar = res.data;
        ElMessage.success('头像上传成功');
      } else {
        ElMessage.error(res.message || '上传失败');
      }
    } catch (error) {
      ElMessage.error('上传失败');
    }
  };

  const handleDeleteAvatar = async (e) => {
    e.preventDefault();
    try {
      const res = await deleteFile('SINGER_AVATAR', form.value.singerAvatar);
      if (res.state) {
        form.value.singerAvatar = '';
        ElMessage.success('删除成功');
      } else {
        ElMessage.error(res.message || '删除失败');
      }
    } catch (error) {
      ElMessage.error('删除失败');
    }
  };

  const submitForm = async () => {
    if (!formRef.value) return;

    await formRef.value.validate(async (valid) => {
      if (valid) {
        try {
          const submitData = {
            ...form.value,
            ...(isEdit.value ? { singerId: route.params.id } : {})
          };
          const res = await addSinger(submitData);
          if (res.state) {
            ElMessage.success(isEdit.value ? '修改成功' : '添加成功');
            goBack();
          } else {
            ElMessage.error(res.message || (isEdit.value ? '修改失败' : '添加失败'));
          }
        } catch (error) {
          ElMessage.error(isEdit.value ? '修改失败' : '添加失败');
        }
      }
    });
  };

  const goBack = () => {
    router.push('/home/singers');
  };

  // 禁用未来日期
  const disableFutureDates = (time) => {
    return time > Date.now();
  };
</script>

<style scoped>
  .add-singer-container {
    padding: 20px;
  }

  .page-title {
    margin-bottom: 20px;
    font-size: 24px;
    color: white;
  }

  .content-box {
    background-color: var(--el-bg-color);
    border-radius: 8px;
    padding: 20px;
  }

  .singer-form {
    max-width: 600px;
    margin: 0 auto;
  }

  .country-select {
    width: 100%;
  }

  .avatar-container {
    position: relative;
    width: 178px;
    height: 178px;
  }

  .avatar-uploader {
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
    cursor: pointer;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);
  }

  .avatar-uploader:hover {
    border-color: var(--el-color-primary);
  }

  .avatar {
    width: 178px;
    height: 178px;
    display: block;
    object-fit: cover;
  }

  .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 178px;
    height: 178px;
    text-align: center;
    line-height: 178px;
  }

  .delete-icon {
    position: absolute;
    top: 8px;
    right: 8px;
    width: 24px;
    height: 24px;
    background-color: rgba(0, 0, 0, 0.6);
    color: #fff;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    border-radius: 50%;
    transition: all 0.3s;
    z-index: 10;
  }

  .delete-icon:hover {
    background-color: rgba(0, 0, 0, 0.8);
    transform: scale(1.1);
  }
</style>