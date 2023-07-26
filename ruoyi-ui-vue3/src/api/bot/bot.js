import request from '@/utils/request'

// 查询机器人管理列表
export function listBot(query) {
  return request({
    url: '/bot/bot/list',
    method: 'get',
    params: query
  })
}

// 查询机器人管理详细
export function getBot(botId) {
  return request({
    url: '/bot/bot/' + botId,
    method: 'get'
  })
}

// 新增机器人管理
export function addBot(data) {
  return request({
    url: '/bot/bot',
    method: 'post',
    data: data
  })
}

// 修改机器人管理
export function updateBot(data) {
  return request({
    url: '/bot/bot',
    method: 'put',
    data: data
  })
}

// 删除机器人管理
export function delBot(botId) {
  return request({
    url: '/bot/bot/' + botId,
    method: 'delete'
  })
}
