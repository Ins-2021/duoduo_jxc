import http from './request'

class OfflineManager {
  private queueKey = 'offline_queue'
  private syncing = false

  private loadQueue(): any[] {
    try { return JSON.parse(uni.getStorageSync(this.queueKey) || '[]') }
    catch { return [] }
  }

  private saveQueue(queue: any[]) {
    uni.setStorageSync(this.queueKey, JSON.stringify(queue))
  }

  async addQueue(type: string, data: any): Promise<string> {
    const queue = this.loadQueue()
    const id = 'offline_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9)
    queue.push({ id, type, data, createdAt: Date.now(), status: 'pending', retryCount: 0 })
    this.saveQueue(queue)
    return id
  }

  async sync(): Promise<void> {
    if (this.syncing) return
    this.syncing = true
    const queue = this.loadQueue()
    const apiMap: Record<string, string> = {
      piecework: '/worker/piecework',
      quality: '/inspector/check',
      inbound: '/warehouse/in',
      outbound: '/warehouse/out'
    }
    for (const record of queue.filter((r: any) => r.status === 'pending')) {
      try {
        const url = apiMap[record.type]
        if (!url) { record.status = 'failed'; continue }
        await http.post(url, record.data)
        record.status = 'synced'
        record.syncedAt = Date.now()
      } catch (e: any) {
        record.retryCount++
        if (record.retryCount >= 3) {
          record.status = 'failed'
          record.error = e.message
        }
      }
    }
    this.saveQueue(queue)
    this.syncing = false
  }

  getPendingCount(): number {
    return this.loadQueue().filter((r: any) => r.status === 'pending').length
  }
}

export const offlineManager = new OfflineManager()
