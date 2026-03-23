export const CARD_STATUS_UI = {
  active: {
    key: 'active',
    label: '使用中'
  },
  idle: {
    key: 'idle',
    label: '空闲'
  },
  unbound: {
    key: 'unbound',
    label: '未绑定空间'
  },
  disabled: {
    key: 'disabled',
    label: '停用'
  }
}

export const DEFAULT_MANAGEMENT_ACTIONS = Object.freeze([
  { key: 'edit', type: 'primary', label: '编辑' },
  { key: 'delete', type: 'danger', label: '删除' }
])

export function getStatusUI(status) {
  return CARD_STATUS_UI[status] || CARD_STATUS_UI.idle
}
