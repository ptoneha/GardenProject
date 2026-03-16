import request from '@/utils/request'

// 查询地块资源管理列表
export function listLand(query) {
  return request({
    url: '/agriculture/land/list',
    method: 'get',
    params: query
  })
}

// 查询地块资源管理详细
export function getLand(landId) {
  return request({
    url: '/agriculture/land/' + landId,
    method: 'get'
  })
}

// 新增地块资源管理
export function addLand(data) {
  return request({
    url: '/agriculture/land',
    method: 'post',
    data: data
  })
}

// 修改地块资源管理
export function updateLand(data) {
  return request({
    url: '/agriculture/land',
    method: 'put',
    data: data
  })
}

// 删除地块资源管理
export function delLand(landId) {
  return request({
    url: '/agriculture/land/' + landId,
    method: 'delete'
  })
}
