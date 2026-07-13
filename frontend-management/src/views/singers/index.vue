<template>
  <div class="singers-container">
    <h1 class="page-title">歌手管理</h1>
    <div class="content-box">
      <!-- 搜索区域 -->
      <div class="search-area">
        <el-input v-model="searchForm.singerName" placeholder="请输入歌手名称" class="search-input" :prefix-icon="Search"
          @keyup.enter="handleSearch" />
        <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        <el-button type="primary" :icon="Plus" @click="handleAdd" style="margin-left: auto">新增歌手</el-button>
      </div>

      <!-- 表格 -->
      <el-table v-loading="loading" :data="tableData" style="width: 100%" class="singer-table">
        <el-table-column label="头像" width="100">
          <template #default="{ row }">
            <el-avatar :size="60" :src="row.singerAvatar" @error="handleAvatarError">
              <el-icon>
                <User />
              </el-icon>
            </el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="singerName" label="姓名" />
        <el-table-column prop="singerNat" label="国籍" />
        <el-table-column prop="singerSex" label="性别">
          <template #default="{ row }">
            {{ row.singerSex === 1 ? '男' : '女' }}
          </template>
        </el-table-column>
        <el-table-column prop="singerBirth" label="生日" />
        <el-table-column prop="singerDebutDate" label="出道日期" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination v-model:current-page="searchForm.pageNum" v-model:page-size="searchForm.pageSize" :total="total"
          :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next" @size-change="handleSizeChange"
          @current-change="handleCurrentChange" />
      </div>
    </div>
  </div>
</template>

<script setup>
  import { ref, onMounted } from 'vue';
  import { ElMessage } from 'element-plus';
  import { Search, User, Refresh, Plus } from '@element-plus/icons-vue';
  import { getSingerList } from '@/api/singer';
  import { useRouter } from 'vue-router';

  const router = useRouter();

  const tableData = ref([]);
  const total = ref(0);
  const loading = ref(false);

  const searchForm = ref({
    singerName: '',
    pageNum: 1,
    pageSize: 10
  });

  const fetchData = async () => {
    loading.value = true;
    try {
      const res = await getSingerList(searchForm.value);
      if (res.state) {
        tableData.value = res.data.records;
        total.value = res.data.total;
      } else {
        ElMessage.error(res.message || '获取歌手列表失败');
      }
    } catch (error) {
      ElMessage.error('获取歌手列表失败');
    } finally {
      loading.value = false;
    }
  };

  const handleReset = () => {
    searchForm.value.singerName = '';
    searchForm.value.pageNum = 1;
    fetchData();
  };

  const handleSearch = () => {
    searchForm.value.pageNum = 1;
    fetchData();
  };

  const handleSizeChange = (val) => {
    searchForm.value.pageSize = val;
    fetchData();
  };

  const handleCurrentChange = (val) => {
    searchForm.value.pageNum = val;
    fetchData();
  };

  const handleAvatarError = () => true;

  const handleEdit = (row) => {
    router.push({
      name: 'EditSinger',
      params: { id: row.singerId },
      state: { singer: row }
    });
  };

  const handleDelete = (row) => {
    console.log('删除', row);
  };

  const handleAdd = () => {
    router.push('/home/singers/add');
  };

  onMounted(() => {
    fetchData();
  });
</script>

<style scoped>
  .singers-container {
    padding: 0px;
  }

  .page-title {
    margin-bottom: 20px;
    font-size: 24px;
    color: white;
    ;
  }

  .content-box {
    background-color: var(--el-bg-color);
    border-radius: 8px;
    padding: 20px;
  }

  .search-area {
    display: flex;
    gap: 10px;
    margin-bottom: 20px;
  }

  .search-input {
    width: 300px;
  }

  .singer-table {
    margin-bottom: 20px;
  }

  .pagination-container {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
  }
</style>