import request from '@/utils/request'

// 查询种植容器管理列表
export function listContainer(query) {
  return request({
    url: '/agriculture/container/list',
    method: 'get',
    params: query
  })
}

// 查询种植容器管理详细
export function getContainer(containerId) {
  return request({
    url: '/agriculture/container/' + containerId,
    method: 'get'
  })
}

// 新增种植容器管理
export function addContainer(data) {
  return request({
    url: '/agriculture/container',
    method: 'post',
    data: data
  })
}

// 修改种植容器管理
export function updateContainer(data) {
  return request({
    url: '/agriculture/container',
    method: 'put',
    data: data
  })
}

// 删除种植容器管理
export function delContainer(containerId) {
  return request({
    url: '/agriculture/container/' + containerId,
    method: 'delete'
  })
}
