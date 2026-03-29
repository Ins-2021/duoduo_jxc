import { defineStore } from 'pinia'
import { ref } from 'vue'

export interface OfflineRecord {
  id: string
  type: string
  data: any
  createdAt: number
  status: 'pending' | 'synced' | 'failed'
  retryCount: number
  syncedAt?: number
  error?: string
}

export const useOfflineStore = defineStore('offline', () => {
  const isOffline = ref(false)
  const queue = ref<OfflineRecord[]>(loadQueue())
  const syncing = ref(false)

  function loadQueue(): OfflineRecord[] {
    try {
      return JSON.parse(uni.getStorageSync('offline_queue') || '[]')
    } catch {
      return []
    }
  }

  function saveQueue() {
    uni.setStorageSync('offline_queue', JSON.stringify(queue.value))
  }

  function generateId(): string {
    return 'offline_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9)
  }

  function addQueue(type: string, data: any): string {
    const record: OfflineRecord = {
      id: generateId(),
      type,
      data,
      createdAt: Date.now(),
      status: 'pending',
      retryCount: 0
    }
    queue.value.push(record)
    saveQueue()
    return record.id
  }

  function getPendingCount(): number {
    return queue.value.filter(r => r.status === 'pending').length
  }

  function clearSynced() {
    queue.value = queue.value.filter(r => r.status !== 'synced')
    saveQueue()
  }

  return { isOffline, queue, syncing, addQueue, getPendingCount, clearSynced, saveQueue }
})
