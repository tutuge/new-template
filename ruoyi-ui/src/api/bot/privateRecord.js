import request from '@/utils/request'

// 查询私人提问列表
export function listPrivateRecord(query) {
  return request({
    url: '/bot/privateRecord/list',
    method: 'get',
    params: query
  })
}

// 查询私人提问详细
export function getPrivateRecord(id) {
  return request({
    url: '/bot/privateRecord/' + id,
    method: 'get'
  })
}

// 新增私人提问
export function addPrivateRecord(data) {
  return request({
    url: '/bot/privateRecord',
    method: 'post',
    data: data
  })
}

// 修改私人提问
export function updatePrivateRecord(data) {
  return request({
    url: '/bot/privateRecord',
    method: 'put',
    data: data
  })
}

// 删除私人提问
export function delPrivateRecord(id) {
  return request({
    url: '/bot/privateRecord/' + id,
    method: 'delete'
  })
}
