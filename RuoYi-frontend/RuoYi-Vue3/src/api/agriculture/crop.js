import request from '@/utils/request'

// 查询作物百科管理列表
export function listCrop(query) {
  return request({
    url: '/agriculture/crop/list',
    method: 'get',
    params: query
  })
}

// 查询作物百科管理详细
export function getCrop(cropId) {
  return request({
    url: '/agriculture/crop/' + cropId,
    method: 'get'
  })
}

// 新增作物百科管理
export function addCrop(data) {
  return request({
    url: '/agriculture/crop',
    method: 'post',
    data: data
  })
}

// 修改作物百科管理
export function updateCrop(data) {
  return request({
    url: '/agriculture/crop',
    method: 'put',
    data: data
  })
}

// 删除作物百科管理
export function delCrop(cropId) {
  return request({
    url: '/agriculture/crop/' + cropId,
    method: 'delete'
  })
}
