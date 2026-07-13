<template>
  <div class="overview">
    <div class="statistics-grid">
      <!-- 歌曲统计 -->
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon>
            <Mic />
          </el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ statistics.totalSongs }}</div>
          <div class="stat-label">歌曲总数</div>
        </div>
      </div>

      <!-- 歌手统计 -->
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon>
            <User />
          </el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ statistics.totalSingers }}</div>
          <div class="stat-label">歌手总数</div>
        </div>
      </div>

      <!-- 专辑统计 -->
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon>
            <List />
          </el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ statistics.totalAlbums }}</div>
          <div class="stat-label">专辑总数</div>
        </div>
      </div>

      <!-- 用户统计 -->
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon>
            <UserFilled />
          </el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ statistics.totalUsers }}</div>
          <div class="stat-label">用户总数</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { ref, onMounted } from 'vue';
  import { ElMessage } from 'element-plus';
  import { getStatistics } from '@/api/auth.ts';
  import { Mic, User, List, UserFilled } from '@element-plus/icons-vue';
  import type { StatisticsVO } from '@/types/api';

  const statistics = ref < StatisticsVO > ({
    totalSongs: 0,
    totalSingers: 0,
    totalAlbums: 0,
    totalUsers: 0
  });

  const fetchStatistics = async () => {
    try {
      const res = await getStatistics();
      if (res.state) {
        statistics.value = res.data;
      } else {
        ElMessage.error(res.message || '获取统计数据失败');
      }
    } catch (error) {
      ElMessage.error('获取统计数据失败');
    }
  };

  onMounted(() => {
    fetchStatistics();
  });
</script>

<style scoped>
  .overview {
    padding: 20px;
  }

  .statistics-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 20px;
    margin-bottom: 30px;
  }

  .stat-card {
    background-color: #2d3035;
    border-radius: 12px;
    padding: 20px;
    display: flex;
    align-items: center;
    gap: 20px;
    transition: all 0.3s ease;
  }

  .stat-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  }

  .stat-icon {
    background: linear-gradient(45deg, #409EFF, #36D1DC);
    width: 60px;
    height: 60px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 24px;
    color: white;
  }

  .stat-info {
    flex: 1;
  }

  .stat-value {
    font-size: 28px;
    font-weight: 600;
    color: #ffffff;
    margin-bottom: 5px;
  }

  .stat-label {
    font-size: 14px;
    color: #8a8f98;
  }
</style>