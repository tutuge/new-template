import request from '@/utils/request'

// 查询机器人按钮管理列表
export function listButton(query) {
  return request({
    url: '/bot/button/list',
    method: 'get',
    params: query
  })
}

// 查询机器人按钮管理详细
export function getButton(buttonId) {
  return request({
    url: '/bot/button/' + buttonId,
    method: 'get'
  })
}

// 新增机器人按钮管理
export function addButton(data) {
  return request({
    url: '/bot/button',
    method: 'post',
    data: data
  })
}

// 修改机器人按钮管理
export function updateButton(data) {
  return request({
    url: '/bot/button',
    method: 'put',
    data: data
  })
}

// 删除机器人按钮管理
export function delButton(buttonId) {
  return request({
    url: '/bot/button/' + buttonId,
    method: 'delete'
  })
}
