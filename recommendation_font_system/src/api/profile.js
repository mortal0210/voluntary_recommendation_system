import request from '@/utils/request'

// 获取个人中心信息
export function getProfileInfo() {
  return request({
    url: '/user/profile',
    method: 'get'
  })
}

