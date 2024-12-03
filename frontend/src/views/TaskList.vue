<template>
  <div class="task-list">
    <div class="header">
      <h2>比较任务</h2>
      <el-button type="primary" @click="showCreateDialog">
        创建任务
      </el-button>
    </div>

    <el-table
      v-loading="taskStore.loading"
      :data="taskStore.tasks"
      style="width: 100%"
    >
      <el-table-column prop="name" label="任务名称" />
      <el-table-column label="源数据源">
        <template #default="{ row }">
          {{ row.source.name }}
        </template>
      </el-table-column>
      <el-table-column label="目标数据源">
        <template #default="{ row }">
          {{ row.target.name }}
        </template>
      </el-table-column>
      <el-table-column label="触发方式">
        <template #default="{ row }">
          <el-tag :type="row.triggerType === 'MANUAL' ? 'info' : 'success'">
            {{ row.triggerType === 'MANUAL' ? '手动' : '定时' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="320">
        <template #default="{ row }">
          <el-button
            type="primary"
            @click="executeTask(row)"
            :loading="executingId === row.id"
          >
            执行
          </el-button>
          <el-button @click="viewHistory(row)">
            历史记录
          </el-button>
          <el-button @click="editTask(row)">
            编辑
          </el-button>
          <el-button type="danger" @click="confirmDelete(row)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 任务表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑任务' : '创建任务'"
      width="800px"
    >
      <TaskForm
        v-if="dialogVisible"
        :initial-data="currentTask"
        @submit="handleSubmit"
        @cancel="dialogVisible = false"
      />
    </el-dialog>

    <!-- 历史记录对话框 -->
    <el-dialog
      v-model="historyDialogVisible"
      title="执行历史"
      width="900px"
    >
      <TaskHistory
        v-if="historyDialogVisible"
        :task-id="currentTaskId"
      />
    </el-dialog>

    <!-- 执行进度对话框 -->
    <el-dialog
      v-model="progressDialogVisible"
      title="执行进度"
      width="500px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="false"
    >
      <TaskProgress
        v-if="progressDialogVisible"
        :task-id="currentTaskId"
        @complete="handleExecutionComplete"
        @view-result="handleViewResult"
      />
    </el-dialog>

    <!-- 结果详情对话框 -->
    <el-dialog
      v-model="resultDialogVisible"
      title="比较结果"
      width="90%"
      top="5vh"
      :fullscreen="isFullscreen"
      @close="resultDialogVisible = false"
    >
      <template #header>
        <div class="result-dialog-header">
          <span>比较结果</span>
          <el-button
            :icon="isFullscreen ? 'Close' : 'FullScreen'"
            @click="isFullscreen = !isFullscreen"
          />
        </div>
      </template>
      <CompareResultDetails
        v-if="resultDialogVisible && currentResult"
        :result="currentResult"
      />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useCompareTaskStore } from '@/stores/compareTask'
import TaskForm from '@/components/TaskForm.vue'
import TaskHistory from '@/components/TaskHistory.vue'
import TaskProgress from '@/components/TaskProgress.vue'
import CompareResultDetails from '@/components/CompareResultDetails.vue'
import { Close, FullScreen } from '@element-plus/icons-vue'
import type { CompareTask, CompareResult } from '@/types'

const taskStore = useCompareTaskStore()
const dialogVisible = ref(false)
const historyDialogVisible = ref(false)
const progressDialogVisible = ref(false)
const isEdit = ref(false)
const currentTask = ref<Partial<CompareTask> | null>(null)
const currentTaskId = ref<number | null>(null)
const executingId = ref<number | null>(null)
const resultDialogVisible = ref(false)
const isFullscreen = ref(false)
const currentResult = ref<CompareResult | null>(null)

function showCreateDialog() {
  isEdit.value = false
  currentTask.value = null
  dialogVisible.value = true
}

function editTask(task: CompareTask) {
  isEdit.value = true
  currentTask.value = { ...task }
  dialogVisible.value = true
}

async function handleSubmit(data: Partial<CompareTask>) {
  try {
    if (isEdit.value && currentTask.value?.id) {
      await taskStore.updateTask(currentTask.value.id, data)
      ElMessage.success('任务更新成功')
    } else {
      await taskStore.createTask(data)
      ElMessage.success('任务创建成功')
    }
    dialogVisible.value = false
  } catch (error) {
    ElMessage.error(taskStore.error || '操作失败')
  }
}

async function confirmDelete(task: CompareTask) {
  try {
    await ElMessageBox.confirm(
      `确定要删除任务"${task.name}"吗？`,
      '确认删除',
      {
        type: 'warning'
      }
    )
    await taskStore.deleteTask(task.id)
    ElMessage.success('任务删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(taskStore.error || '删除失败')
    }
  }
}

async function executeTask(task: CompareTask) {
  executingId.value = task.id
  currentTaskId.value = task.id
  try {
    const result = await taskStore.executeTask(task.id)
    currentResult.value = result
    progressDialogVisible.value = true
  } catch (error) {
    ElMessage.error(taskStore.error || '执行任务失败')
  } finally {
    executingId.value = null
  }
}

function viewHistory(task: CompareTask) {
  currentTaskId.value = task.id
  historyDialogVisible.value = true
}

async function handleViewResult() {
  if (!currentTaskId.value) return
  
  try {
    const result = await taskStore.getTaskStatus(currentTaskId.value)
    currentResult.value = result
    progressDialogVisible.value = false
    resultDialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取结果失败')
  }
}

function handleExecutionComplete() {
  progressDialogVisible.value = false
  resultDialogVisible.value = true
}

onMounted(async () => {
  try {
    await taskStore.fetchTasks()
  } catch (error) {
    ElMessage.error(taskStore.error || '加载任务列表失败')
  }
})
</script>

<style scoped>
.task-list {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header h2 {
  margin: 0;
}

.result-dialog-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
</style> 