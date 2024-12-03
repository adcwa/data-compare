<template>
  <div class="task-progress">
    <div class="progress-info">
      <div class="step">
        当前步骤：{{ progress?.currentStep || '准备中...' }}
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
      :status="getProgressStatus()"
      :format="formatProgress"
    />

    <div class="actions">
      <el-button
        v-if="isCompleted"
        type="primary"
        @click="handleComplete"
      >
        完成
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useCompareTaskStore } from '@/stores/compareTask'
import { wsService } from '@/services/websocket'
import type { CompareProgress } from '@/types'

const props = defineProps<{
  taskId: number
}>()

const emit = defineEmits<{
  (e: 'complete'): void
}>()

const taskStore = useCompareTaskStore()
const progress = computed(() => taskStore.taskProgress.get(props.taskId))
const isCompleted = computed(() => 
  progress.value?.progressPercentage === 100 || 
  progress.value?.currentStep?.includes('完成') ||
  progress.value?.currentStep?.includes('失败')
)

function getProgressStatus() {
  if (!progress.value) return ''
  if (progress.value.currentStep?.includes('失败')) return 'exception'
  if (isCompleted.value) return 'success'
  return ''
}

function formatProgress(percentage: number) {
  return `${percentage.toFixed(1)}%`
}

function handleComplete() {
  emit('complete')
}

function setupWebSocket() {
  // 订阅任务进度更新
  wsService.send({
    type: 'SUBSCRIBE_PROGRESS',
    taskId: props.taskId
  })
}

onMounted(() => {
  setupWebSocket()
})

onUnmounted(() => {
  // 取消订阅任务进度更新
  wsService.send({
    type: 'UNSUBSCRIBE_PROGRESS',
    taskId: props.taskId
  })
})
</script>

<style scoped>
.task-progress {
  padding: 20px;
}

.progress-info {
  margin-bottom: 20px;
}

.step, .records, .diff-count {
  margin-bottom: 10px;
}

.actions {
  margin-top: 20px;
  text-align: right;
}
</style> 