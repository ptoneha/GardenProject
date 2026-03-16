import request from '@/utils/request'

// 查询种植优化任务列表
export function listTask(query) {
  return request({
    url: '/agriculture/task/list',
    method: 'get',
    params: query
  })
}

// 查询种植优化任务详细
export function getTask(taskId) {
  return request({
    url: '/agriculture/task/' + taskId,
    method: 'get'
  })
}

// 新增种植优化任务
export function addTask(data) {
  return request({
    url: '/agriculture/task',
    method: 'post',
    data: data
  })
}

// 修改种植优化任务
export function updateTask(data) {
  return request({
    url: '/agriculture/task',
    method: 'put',
    data: data
  })
}

// 删除种植优化任务
export function delTask(taskId) {
  return request({
    url: '/agriculture/task/' + taskId,
    method: 'delete'
  })
}
