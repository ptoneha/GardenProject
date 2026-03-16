import request from '@/utils/request'

// 查询作物指标配置列表
export function listConfig(query) {
  return request({
    url: '/agriculture/config/list',
    method: 'get',
    params: query
  })
}

// 查询作物指标配置详细
export function getConfig(configId) {
  return request({
    url: '/agriculture/config/' + configId,
    method: 'get'
  })
}

// 新增作物指标配置
export function addConfig(data) {
  return request({
    url: '/agriculture/config',
    method: 'post',
    data: data
  })
}

// 修改作物指标配置
export function updateConfig(data) {
  return request({
    url: '/agriculture/config',
    method: 'put',
    data: data
  })
}

// 删除作物指标配置
export function delConfig(configId) {
  return request({
    url: '/agriculture/config/' + configId,
    method: 'delete'
  })
}
