import request from '@/utils/request'

export function listTask(query) {
  return request({
    url: '/agriculture/task/list',
    method: 'get',
    params: query
  })
}

export function getTask(taskId) {
  return request({
    url: '/agriculture/task/' + taskId,
    method: 'get'
  })
}

export function addTask(data) {
  return request({
    url: '/agriculture/task',
    method: 'post',
    data
  })
}

export function updateTask(data) {
  return request({
    url: '/agriculture/task',
    method: 'put',
    data
  })
}

export function delTask(taskId) {
  return request({
    url: '/agriculture/task/' + taskId,
    method: 'delete'
  })
}

export function executeTask(taskId, config = {}) {
  return request({
    url: '/agriculture/task/execute/' + taskId,
    method: 'post',
    headers: {
      silentError: true,
      ...(config.headers || {})
    },
    ...config
  })
}
