import { defineStore } from 'pinia'
import { compareTaskApi } from '@/services/api'
import type { CompareTask, CompareResult, CompareProgress } from '@/types'

export const useCompareTaskStore = defineStore('compareTask', {
  state: () => ({
    tasks: [] as CompareTask[],
    currentTask: null as CompareTask | null,
    taskProgress: new Map<number, CompareProgress>(),
    loading: false,
    error: null as string | null
  }),

  actions: {
    async fetchTasks() {
      this.loading = true
      try {
        const response = await compareTaskApi.getAll()
        this.tasks = response.data
      } catch (error) {
        this.error = '获取任务列表失败'
        throw error
      } finally {
        this.loading = false
      }
    },

    async executeTask(id: number) {
      try {
        const response = await compareTaskApi.execute(id)
        return response.data
      } catch (error) {
        this.error = '执行任务失败'
        throw error
      }
    },

    async getTaskStatus(id: number) {
      try {
        const response = await compareTaskApi.getStatus(id)
        return response.data
      } catch (error) {
        this.error = '获取任务状态失败'
        throw error
      }
    },

    updateTaskProgress(taskId: number, progress: CompareProgress) {
      this.taskProgress.set(taskId, progress)
    }
  }
}) 