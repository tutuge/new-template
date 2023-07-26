import request from '@/utils/request'

// 查询机器人通知列表
export function listNotice(query) {
    return request({
        url: '/bot/notice/list',
        method: 'get',
        params: query
    })
}

// 查询机器人通知详细
export function getNotice(noticeId) {
    return request({
        url: '/bot/notice/' + noticeId,
        method: 'get'
    })
}

// 新增机器人通知
export function addNotice(data) {
    return request({
        url: '/bot/notice',
        method: 'post',
        data: data
    })
}

// 修改机器人通知
export function updateNotice(data) {
    return request({
        url: '/bot/notice',
        method: 'put',
        data: data
    })
}

// 删除机器人通知
export function delNotice(noticeId) {
    return request({
        url: '/bot/notice/' + noticeId,
        method: 'delete'
    })
}
