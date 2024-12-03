import { useCompareTaskStore } from '@/stores/compareTask'
import type { CompareProgress } from '@/types'

export class WebSocketService {
  private ws: WebSocket | null = null
  private reconnectAttempts = 0
  private maxReconnectAttempts = 5
  private reconnectTimeout = 3000
  private subscriptions = new Set<number>()

  constructor(private url: string) {}

  connect() {
    try {
      this.ws = new WebSocket(this.url)
      this.setupEventHandlers()
    } catch (error) {
      console.error('WebSocket连接失败:', error)
      this.handleReconnect()
    }
  }

  private setupEventHandlers() {
    if (!this.ws) return

    this.ws.onopen = () => {
      console.log('WebSocket连接成功')
      this.reconnectAttempts = 0
      this.resubscribe()
    }

    this.ws.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data)
        this.handleMessage(data)
      } catch (error) {
        console.error('处理WebSocket消息失败:', error)
      }
    }

    this.ws.onclose = () => {
      console.log('WebSocket连接关闭')
      this.handleReconnect()
    }

    this.ws.onerror = (error) => {
      console.error('WebSocket错误:', error)
    }
  }

  private handleMessage(data: any) {
    const taskStore = useCompareTaskStore()

    switch (data.type) {
      case 'PROGRESS_UPDATE':
        if (this.subscriptions.has(data.taskId)) {
          taskStore.updateTaskProgress(data.taskId, data.progress as CompareProgress)
        }
        break
      // 处理其他类型的消息
    }
  }

  private handleReconnect() {
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      this.reconnectAttempts++
      console.log(`尝试重新连接 (${this.reconnectAttempts}/${this.maxReconnectAttempts})...`)
      setTimeout(() => this.connect(), this.reconnectTimeout)
    } else {
      console.error('WebSocket重连失败，已达到最大重试次数')
    }
  }

  private resubscribe() {
    // 重新连接后重新订阅所有任务的进度更新
    this.subscriptions.forEach(taskId => {
      this.send({
        type: 'SUBSCRIBE_PROGRESS',
        taskId
      })
    })
  }

  disconnect() {
    if (this.ws) {
      this.ws.close()
      this.ws = null
    }
  }

  send(message: any) {
    if (message.type === 'SUBSCRIBE_PROGRESS') {
      this.subscriptions.add(message.taskId)
    } else if (message.type === 'UNSUBSCRIBE_PROGRESS') {
      this.subscriptions.delete(message.taskId)
    }

    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      this.ws.send(JSON.stringify(message))
    } else {
      console.error('WebSocket未连接')
    }
  }
}

// 创建WebSocket服务实例
export const wsService = new WebSocketService('ws://localhost:8080/ws') 