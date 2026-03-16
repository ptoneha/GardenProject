import request from '@/utils/request'

// 查询任务资源关联列表
export function listTaskLand(query) {
  return request({
    url: '/agriculture/taskLand/list',
    method: 'get',
    params: query
  })
}

// 查询任务资源关联详细
export function getTaskLand(taskId) {
  return request({
    url: '/agriculture/taskLand/' + taskId,
    method: 'get'
  })
}

// 新增任务资源关联
export function addTaskLand(data) {
  return request({
    url: '/agriculture/taskLand',
    method: 'post',
    data: data
  })
}

// 修改任务资源关联
export function updateTaskLand(data) {
  return request({
    url: '/agriculture/taskLand',
    method: 'put',
    data: data
  })
}

// 删除任务资源关联
export function delTaskLand(taskId) {
  return request({
    url: '/agriculture/taskLand/' + taskId,
    method: 'delete'
  })
}
