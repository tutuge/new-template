import request from '@/utils/request'

// 查询电报私人列表
export function listPrivate(query) {
  return request({
    url: '/bot/private/list',
    method: 'get',
    params: query
  })
}

// 查询电报私人详细
export function getPrivate(privateId) {
  return request({
    url: '/bot/private/' + privateId,
    method: 'get'
  })
}

// 新增电报私人
export function addPrivate(data) {
  return request({
    url: '/bot/private',
    method: 'post',
    data: data
  })
}

// 修改电报私人
export function updatePrivate(data) {
  return request({
    url: '/bot/private',
    method: 'put',
    data: data
  })
}

// 删除电报私人
export function delPrivate(privateId) {
  return request({
    url: '/bot/private/' + privateId,
    method: 'delete'
  })
}
