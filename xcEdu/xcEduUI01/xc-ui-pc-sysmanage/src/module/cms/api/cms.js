//public是对axios的工具类封装，定义了http请求方法
import http from './../../../base/api/public'
import querystring from 'querystring'
let sysConfig = require('@/../config/sysConfig')
let apiUrl = sysConfig.xcApiUrlPre;
// 页面查询
export const page_list = (page,size,params) => {
  //将json对象转成key/value对
  let query = querystring.stringify(params)
  // 请求服务端的页面查询接口
  return http.requestQuickGet(apiUrl+'/cms/page/list/'+page+'/'+size+'?'+query)
}





