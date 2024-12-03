<template>
  <div class="home">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>数据源统计</span>
            </div>
          </template>
          <div class="statistics">
            <div class="number">{{ statistics.dataSources }}</div>
            <div class="label">总数据源</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>比较任务统计</span>
            </div>
          </template>
          <div class="statistics">
            <div class="number">{{ statistics.tasks }}</div>
            <div class="label">总任务数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>执行统计</span>
            </div>
          </template>
          <div class="statistics">
            <div class="number">{{ statistics.executions }}</div>
            <div class="label">总执行次数</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-4">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>最近执行的任务</span>
            </div>
          </template>
          <el-table :data="recentTasks" style="width: 100%">
            <el-table-column prop="name" label="任务名称" />
            <el-table-column prop="executionTime" label="执行时间" />
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">
                  {{ row.status }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>数据差异统计</span>
            </div>
          </template>
          <!-- TODO: 添加图表展示 -->
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

const statistics = ref({
  dataSources: 0,
  tasks: 0,
  executions: 0
})

const recentTasks = ref([])

function getStatusType(status: string) {
  switch (status) {
    case 'SUCCESS':
      return 'success'
    case 'FAILED':
      return 'danger'
    case 'IN_PROGRESS':
      return 'warning'
    default:
      return 'info'
  }
}

onMounted(async () => {
  // TODO: 从后端获取统计数据
})
</script>

<style scoped>
.home {
  padding: 20px;
}

.mt-4 {
  margin-top: 20px;
}

.statistics {
  text-align: center;
}

.statistics .number {
  font-size: 36px;
  font-weight: bold;
  color: #409EFF;
}

.statistics .label {
  margin-top: 10px;
  color: #909399;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style> 