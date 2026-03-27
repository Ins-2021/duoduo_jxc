import { getDict } from '@/api/dict'

type DictItem = { value: number; label: string }

const cache = new Map<string, Map<number, string>>()

export async function loadDict(key: string) {
  if (cache.has(key)) return
  const res = await getDict(key)
  const list = (res.data || []) as DictItem[]
  const map = new Map<number, string>()
  list.forEach(i => map.set(Number(i.value), i.label))
  cache.set(key, map)
}

export function dictLabel(key: string, value: any) {
  const map = cache.get(key)
  if (!map) return value == null ? '' : String(value)
  const v = Number(value)
  return map.get(v) ?? (value == null ? '' : String(value))
}

