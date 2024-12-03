<template>
  <div class="task-progress-bar">
    <div class="progress-info">
      <div class="step">
        <el-icon><Loading v-if="isRunning" class="is-loading" /></el-icon>
        {{ progress?.currentStep || '准备中...' }}
      </div>
      <div class="records">
        已处理记录：{{ progress?.processedRecords || 0 }} / {{ progress?.totalRecords || 0 }}
      </div>
      <div class="diff-count" v-if="progress?.diffCount">
        已发现差异：{{ progress.diffCount }}
      </div>
    </div>

    <el-progress
      :percentage="progress?.progressPercentage || 0"
      :status="progressStatus"
      :format="formatProgress"
      :stroke-width="15"
    />

    <div class="actions" v-if="showActions">
      <el-button
        v-if="isCompleted"
        type="primary"
        @click="$emit('view-result')"
      >
        查看结果
      </el-button>
      <el-button
        v-if="isCompleted"
        @click="$emit('close')"
      >
        关闭
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Loading } from '@element-plus/icons-vue'
import type { CompareProgress } from '@/types'

const props = defineProps<{
  progress?: CompareProgress
  showActions?: boolean
}>()

const emit = defineEmits<{
  (e: 'view-result'): void
  (e: 'close'): void
}>()

const isRunning = computed(() => {
  return props.progress?.progressPercentage !== 100 &&
    !props.progress?.currentStep?.includes('失败')
})

const isCompleted = computed(() => {
  return props.progress?.progressPercentage === 100 ||
    props.progress?.currentStep?.includes('完成') ||
    props.progress?.currentStep?.includes('失败')
})

const progressStatus = computed(() => {
  if (!props.progress) return ''
  if (props.progress.currentStep?.includes('失败')) return 'exception'
  if (isCompleted.value) return 'success'
  return ''
})

function formatProgress(percentage: number) {
  return `${percentage.toFixed(1)}%`
}
</script>

<style scoped>
.task-progress-bar {
  padding: 20px;
}

.progress-info {
  margin-bottom: 20px;
}

.step {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
  font-weight: bold;
}

.records, .diff-count {
  margin-bottom: 10px;
  color: #606266;
}

.actions {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.is-loading {
  animation: rotating 2s linear infinite;
}

@keyframes rotating {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style> 