import request from '@/utils/request'

// 查询电报群组列表
export function listGroup(query) {
  return request({
    url: '/bot/group/list',
    method: 'get',
    params: query
  })
}

// 查询电报群组详细
export function getGroup(groupId) {
  return request({
    url: '/bot/group/' + groupId,
    method: 'get'
  })
}

// 新增电报群组
export function addGroup(data) {
  return request({
    url: '/bot/group',
    method: 'post',
    data: data
  })
}

// 修改电报群组
export function updateGroup(data) {
  return request({
    url: '/bot/group',
    method: 'put',
    data: data
  })
}

// 删除电报群组
export function delGroup(groupId) {
  return request({
    url: '/bot/group/' + groupId,
    method: 'delete'
  })
}
