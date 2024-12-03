import { defineStore } from 'pinia'
import { dataSourceApi } from '@/services/api'
import type { DataSource } from '@/types'

export const useDataSourceStore = defineStore('dataSource', {
  state: () => ({
    dataSources: [] as DataSource[],
    loading: false,
    error: null as string | null
  }),

  actions: {
    async fetchDataSources() {
      this.loading = true
      try {
        const response = await dataSourceApi.getAll()
        this.dataSources = response.data
      } catch (error) {
        this.error = '获取数据源列表失败'
        throw error
      } finally {
        this.loading = false
      }
    },

    async createDataSource(data: Partial<DataSource>) {
      try {
        const response = await dataSourceApi.create(data)
        this.dataSources.push(response.data)
        return response.data
      } catch (error) {
        this.error = '创建数据源失败'
        throw error
      }
    },

    async updateDataSource(id: number, data: Partial<DataSource>) {
      try {
        const response = await dataSourceApi.update(id, data)
        const index = this.dataSources.findIndex(ds => ds.id === id)
        if (index !== -1) {
          this.dataSources[index] = response.data
        }
        return response.data
      } catch (error) {
        this.error = '更新数据源失败'
        throw error
      }
    },

    async deleteDataSource(id: number) {
      try {
        await dataSourceApi.delete(id)
        const index = this.dataSources.findIndex(ds => ds.id === id)
        if (index !== -1) {
          this.dataSources.splice(index, 1)
        }
      } catch (error) {
        this.error = '删除数据源失败'
        throw error
      }
    },

    async testConnection(id: number) {
      try {
        await dataSourceApi.testConnection(id)
        return true
      } catch (error) {
        this.error = '连接测试失败'
        throw error
      }
    }
  }
}) 