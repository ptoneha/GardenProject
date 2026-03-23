import request from '@/utils/request'

export function listPortalTask(query) {
  return request({
    url: '/portal/agri/task/list',
    method: 'get',
    params: query
  })
}

export function getPortalTask(taskId) {
  return request({
    url: '/portal/agri/task/' + taskId,
    method: 'get'
  })
}

export function addPortalTask(data) {
  return request({
    url: '/portal/agri/task',
    method: 'post',
    data
  })
}

export function updatePortalTask(data) {
  return request({
    url: '/portal/agri/task',
    method: 'put',
    data
  })
}

export function delPortalTask(taskId) {
  return request({
    url: '/portal/agri/task/' + taskId,
    method: 'delete'
  })
}

export function executePortalTask(taskId, config = {}) {
  return request({
    url: '/portal/agri/task/execute/' + taskId,
    method: 'post',
    headers: {
      silentError: true,
      ...(config.headers || {})
    },
    ...config
  })
}

export function listPortalLand(query) {
  return request({
    url: '/portal/agri/land/list',
    method: 'get',
    params: query
  })
}

export function getPortalLand(landId) {
  return request({
    url: '/portal/agri/land/' + landId,
    method: 'get'
  })
}

export function addPortalLand(data) {
  return request({
    url: '/portal/agri/land',
    method: 'post',
    data
  })
}

export function updatePortalLand(data) {
  return request({
    url: '/portal/agri/land',
    method: 'put',
    data
  })
}

export function delPortalLand(landId) {
  return request({
    url: '/portal/agri/land/' + landId,
    method: 'delete'
  })
}

export function listPortalContainer(query) {
  return request({
    url: '/portal/agri/container/list',
    method: 'get',
    params: query
  })
}

export function getPortalContainer(containerId) {
  return request({
    url: '/portal/agri/container/' + containerId,
    method: 'get'
  })
}

export function addPortalContainer(data) {
  return request({
    url: '/portal/agri/container',
    method: 'post',
    data
  })
}

export function updatePortalContainer(data) {
  return request({
    url: '/portal/agri/container',
    method: 'put',
    data
  })
}

export function delPortalContainer(containerId) {
  return request({
    url: '/portal/agri/container/' + containerId,
    method: 'delete'
  })
}

export function listPortalTaskLand(query) {
  return request({
    url: '/portal/agri/taskLand/list',
    method: 'get',
    params: query
  })
}

export function getPortalTaskLand(taskId) {
  return request({
    url: '/portal/agri/taskLand/' + taskId,
    method: 'get'
  })
}

export function addPortalTaskLand(data) {
  return request({
    url: '/portal/agri/taskLand',
    method: 'post',
    data
  })
}

export function delPortalTaskLand(taskId) {
  return request({
    url: '/portal/agri/taskLand/' + taskId,
    method: 'delete'
  })
}

export function listPortalResult(query) {
  return request({
    url: '/portal/agri/result/list',
    method: 'get',
    params: query
  })
}

export function getPortalResult(resultId) {
  return request({
    url: '/portal/agri/result/' + resultId,
    method: 'get'
  })
}

export function listPortalResultByTask(taskId) {
  return listPortalResult({
    pageNum: 1,
    pageSize: 1000,
    taskId
  })
}

export function listPortalCrop(query) {
  return request({
    url: '/portal/agri/crop/list',
    method: 'get',
    params: query
  })
}

export function getPortalCrop(cropId) {
  return request({
    url: '/portal/agri/crop/' + cropId,
    method: 'get'
  })
}
