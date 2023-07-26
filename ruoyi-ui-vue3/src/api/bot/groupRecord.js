import request from '@/utils/request'

// 查询群提问列表
export function listGroupRecord(query) {
  return request({
    url: '/bot/groupRecord/list',
    method: 'get',
    params: query
  })
}

// 查询群提问详细
export function getGroupRecord(id) {
  return request({
    url: '/bot/groupRecord/' + id,
    method: 'get'
  })
}

// 新增群提问
export function addGroupRecord(data) {
  return request({
    url: '/bot/groupRecord',
    method: 'post',
    data: data
  })
}

// 修改群提问
export function updateGroupRecord(data) {
  return request({
    url: '/bot/groupRecord',
    method: 'put',
    data: data
  })
}

// 删除群提问
export function delGroupRecord(id) {
  return request({
    url: '/bot/groupRecord/' + id,
    method: 'delete'
  })
}
