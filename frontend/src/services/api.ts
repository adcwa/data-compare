import request from '@/utils/request'
import type { DataSource, CompareTask, CompareResult, CompareProgress } from '@/types'

export const dataSourceApi = {
  create: (data: Partial<DataSource>) => 
    request.post<DataSource>('/datasources', data),
  
  getAll: () => 
    request.get<DataSource[]>('/datasources'),
  
  getById: (id: number) => 
    request.get<DataSource>(`/datasources/${id}`),
  
  update: (id: number, data: Partial<DataSource>) => 
    request.put<DataSource>(`/datasources/${id}`, data),
  
  delete: (id: number) => 
    request.delete(`/datasources/${id}`),
  
  testConnection: (id: number) => 
    request.post(`/datasources/${id}/test`)
}

export const compareTaskApi = {
  create: (data: Partial<CompareTask>) =>
    request.post<CompareTask>('/tasks', data),

  getAll: (params?: { page?: number; size?: number }) =>
    request.get<{ items: CompareTask[]; total: number }>('/tasks', { params }),

  getById: (id: number) =>
    request.get<CompareTask>(`/tasks/${id}`),

  update: (id: number, data: Partial<CompareTask>) =>
    request.put<CompareTask>(`/tasks/${id}`, data),

  delete: (id: number) =>
    request.delete(`/tasks/${id}`),

  execute: (id: number) =>
    request.post<CompareResult>(`/tasks/${id}/execute`),

  getStatus: (id: number) =>
    request.get<CompareResult>(`/tasks/${id}/status`),

  getHistory: (id: number, page: number = 0, size: number = 10) =>
    request.get<{ items: CompareResult[]; total: number }>(`/tasks/${id}/history`, {
      params: { page, size }
    }),

  getProgress: (id: number) =>
    request.get<CompareProgress>(`/tasks/${id}/progress`)
} 