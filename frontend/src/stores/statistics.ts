import { defineStore } from 'pinia'

export const useStatisticsStore = defineStore('statistics', {
  state: () => ({
    statistics: {
      dataSources: 0,
      tasks: 0,
      executions: 0
    },
    recentTasks: [],
    loading: false,
    error: null as string | null
  }),

  actions: {
    async fetchStatistics() {
      this.loading = true
      try {
        // TODO: 实现统计数据获取API
        // const response = await api.getStatistics()
        // this.statistics = response.data
      } catch (error) {
        this.error = '获取统计数据失败'
        throw error
      } finally {
        this.loading = false
      }
    },

    async fetchRecentTasks() {
      try {
        // TODO: 实现最近任务获取API
        // const response = await api.getRecentTasks()
        // this.recentTasks = response.data
      } catch (error) {
        this.error = '获取最近任务失败'
        throw error
      }
    }
  }
}) 