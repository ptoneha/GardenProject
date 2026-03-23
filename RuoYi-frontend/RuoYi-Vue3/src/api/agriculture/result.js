import request from '@/utils/request'

export function listResult(query) {
  return request({
    url: '/agriculture/result/list',
    method: 'get',
    params: query
  })
}

export function getResult(resultId) {
  return request({
    url: '/agriculture/result/' + resultId,
    method: 'get'
  })
}

export function addResult(data) {
  return request({
    url: '/agriculture/result',
    method: 'post',
    data
  })
}

export function updateResult(data) {
  return request({
    url: '/agriculture/result',
    method: 'put',
    data
  })
}

export function delResult(resultId) {
  return request({
    url: '/agriculture/result/' + resultId,
    method: 'delete'
  })
}

export function listResultByTask(taskId) {
  return listResult({
    pageNum: 1,
    pageSize: 1000,
    taskId
  })
}
