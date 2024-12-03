<template>
  <div class="compare-result-details">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="差异概览" name="overview">
        <div class="overview">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="总差异数">
              {{ result.diffCount }}
            </el-descriptions-item>
            <el-descriptions-item label="执行时间">
              {{ formatDate(result.executionTime) }}
            </el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="getStatusType(result.status)">
                {{ result.status }}
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>

          <CompareResultChart
            v-if="diffStatistics"
            :data="diffStatistics"
            class="diff-chart"
          />
        </div>
      </el-tab-pane>

      <el-tab-pane label="详细差异" name="details">
        <el-table
          :data="diffDetails"
          style="width: 100%"
          :default-sort="{ prop: 'type', order: 'ascending' }"
        >
          <el-table-column prop="type" label="差异类型" sortable>
            <template #default="{ row }">
              <el-tag :type="getDiffTypeTag(row.type)">
                {{ getDiffTypeLabel(row.type) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="key" label="主键" sortable />
          <el-table-column label="源数据">
            <template #default="{ row }">
              <pre>{{ formatJson(row.source) }}</pre>
            </template>
          </el-table-column>
          <el-table-column label="目标数据">
            <template #default="{ row }">
              <pre>{{ formatJson(row.target) }}</pre>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import CompareResultChart from './CompareResultChart.vue'
import type { CompareResult } from '@/types'

const props = defineProps<{
  result: CompareResult
}>()

const activeTab = ref('overview')

const diffDetails = computed(() => {
  try {
    return JSON.parse(props.result.details)
  } catch {
    return []
  }
})

const diffStatistics = computed(() => {
  const details = diffDetails.value
  return {
    missingInSource: details.filter(d => d.type === 'MISSING_IN_SOURCE').length,
    missingInTarget: details.filter(d => d.type === 'MISSING_IN_TARGET').length,
    valueDiff: details.filter(d => !['MISSING_IN_SOURCE', 'MISSING_IN_TARGET'].includes(d.type)).length
  }
})

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

function getDiffTypeTag(type: string) {
  switch (type) {
    case 'MISSING_IN_SOURCE':
      return 'danger'
    case 'MISSING_IN_TARGET':
      return 'warning'
    default:
      return 'info'
  }
}

function getDiffTypeLabel(type: string) {
  switch (type) {
    case 'MISSING_IN_SOURCE':
      return '源数据缺失'
    case 'MISSING_IN_TARGET':
      return '目标数据缺失'
    default:
      return '数据不一致'
  }
}

function formatJson(data: any) {
  return JSON.stringify(data, null, 2)
}
</script>

<style scoped>
.compare-result-details {
  padding: 20px;
}

.overview {
  margin-bottom: 20px;
}

.diff-chart {
  margin-top: 20px;
}

pre {
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
}
</style> 