import request from '@/utils/request'

export function checkUserBindGoogle() {
  return request({
    url: '/system/user/checkUserBindGoogle',
    method: 'get'
  })
}

export function bind(key,code) {
  const query = {
    "key":key,
    "code":code
  }
  return request({
    url: '/system/user/bindGoogle',
    method: 'get',
    params: query
  })
}

