<template>
  <div class="task-history">
    <el-table
      v-loading="loading"
      :data="history.items"
      style="width: 100%"
    >
      <el-table-column prop="executionTime" label="执行时间">
        <template #default="{ row }">
          {{ formatDate(row.executionTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="diffCount" label="差异数量" />
      <el-table-column label="操作">
        <template #default="{ row }">
          <el-button @click="viewDetails(row)">
            查看详情
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :total="history.total"
      :page-sizes="[10, 20, 50, 100]"
      layout="total, sizes, prev, pager, next"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />

    <!-- 详情对话框 -->
    <el-dialog
      v-model="detailsVisible"
      title="比较结果详情"
      width="900px"
    >
      <CompareResultDetails
        v-if="detailsVisible"
        :result="currentResult"
      />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { useCompareTaskStore } from '@/stores/compareTask'
import CompareResultDetails from './CompareResultDetails.vue'
import type { CompareResult } from '@/types'

const props = defineProps<{
  taskId: number
}>()

const taskStore = useCompareTaskStore()
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const history = ref({
  items: [] as CompareResult[],
  total: 0
})
const detailsVisible = ref(false)
const currentResult = ref<CompareResult | null>(null)

function formatDate(date: string) {
  return new Date(date).toLocaleString()
}

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

function viewDetails(result: CompareResult) {
  currentResult.value = result
  detailsVisible.value = true
}

async function loadHistory() {
  loading.value = true
  try {
    const response = await taskStore.getTaskHistory(
      props.taskId,
      currentPage.value - 1,
      pageSize.value
    )
    history.value = response
  } catch (error) {
    ElMessage.error(taskStore.error || '加载历史记录失败')
  } finally {
    loading.value = false
  }
}

function handleSizeChange(size: number) {
  pageSize.value = size
  loadHistory()
}

function handleCurrentChange(page: number) {
  currentPage.value = page
  loadHistory()
}

watch(() => props.taskId, () => {
  currentPage.value = 1
  loadHistory()
})

onMounted(() => {
  loadHistory()
})
</script>

<style scoped>
.task-history {
  padding: 20px;
}

.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style> 