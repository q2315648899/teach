import http from './../../../base/api/public'
import querystring from 'querystring'
let sysConfig = require('@/../config/sysConfig')
let apiUrl = sysConfig.xcApiUrlPre;
// 页面查询
export const page_list = (page,size,params) => {
  // 请求服务端的页面查询接口
  return http.requestQuickGet('http://localhost:31001/cms/page/list/'+page+'/'+size)
}





