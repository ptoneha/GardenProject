import request from '@/utils/request'

// 查询种植决策方案列表
export function listResult(query) {
  return request({
    url: '/agriculture/result/list',
    method: 'get',
    params: query
  })
}

// 查询种植决策方案详细
export function getResult(resultId) {
  return request({
    url: '/agriculture/result/' + resultId,
    method: 'get'
  })
}

// 新增种植决策方案
export function addResult(data) {
  return request({
    url: '/agriculture/result',
    method: 'post',
    data: data
  })
}

// 修改种植决策方案
export function updateResult(data) {
  return request({
    url: '/agriculture/result',
    method: 'put',
    data: data
  })
}

// 删除种植决策方案
export function delResult(resultId) {
  return request({
    url: '/agriculture/result/' + resultId,
    method: 'delete'
  })
}
