<template>
  <div>
    <!--编写页面静态部分，即view部分-->
    <div>
      <!--相当于编写html的内容-->
      <el-button type="primary" size="small" v-on:click="query">查询</el-button>
      <el-table
        :data="list"
        stripe
        style="width: 100%">
        <el-table-column type="index" width="60">
        </el-table-column>
        <el-table-column prop="pageName" label="页面名称" width="120">
        </el-table-column>
        <el-table-column prop="pageAliase" label="别名" width="120">
        </el-table-column>
        <el-table-column prop="pageType" label="页面类型" width="150">
        </el-table-column>
        <el-table-column prop="pageWebPath" label="访问路径" width="250">
        </el-table-column>
        <el-table-column prop="pagePhysicalPath" label="物理路径" width="250">
        </el-table-column>
        <el-table-column prop="pageCreateTime" label="创建时间" width="180">
        </el-table-column>
      </el-table>
      <el-pagination
        layout="prev, pager, next"
        :page-size="this.params.size"
        v-on:current-change="changePage"
        :total="total" :current-page="this.params.page" style="float:right;"><!--v-on:current-change="changePage"也可写成@:current-change="changePage"-->
      </el-pagination>
    </div>
  </div>
</template>
<script>
  /*编写页面静态部分，即model及vm部分。*/
  import * as cmsApi from '../api/cms'
  export default {
    data() {
      return {
        list: [{
          "siteId": "5a751fab6abb5044e0d19ea1",
          "pageId": "5a754adf6abb500ad05688d9",
          "pageName": "index.html",
          "pageAliase": "首页",
          "pageWebPath": "/index.html",
          "pageParameter": null,
          "pagePhysicalPath": "F:\\develop\\xc_portal_static\\",
          "pageType": "0",
          "pageTemplate": null,
          "pageHtml": null,
          "pageStatus": null,
          "pageCreateTime": "2018-02-03T05:37:53.256+0000",
          "templateId": "5a962b52b00ffc514038faf7",
          "pageParams": null,
          "htmlFileId": "5a7c1c54d019f14d90a1fb23",
          "dataUrl": null
        },
          {
            "siteId": "5a751fab6abb5044e0d19ea1",
            "pageId": "5a795ac7dd573c04508f3a56",
            "pageName": "index_banner.html",
            "pageAliase": "轮播图",
            "pageWebPath": "/include/index_banner.html",
            "pageParameter": null,
            "pagePhysicalPath": "F:\\develop\\xc_portal_static\\include\\",
            "pageType": "0",
            "pageTemplate": null,
            "pageHtml": null,
            "pageStatus": null,
            "pageCreateTime": "2018-02-06T07:34:21.255+0000",
            "templateId": "5a962bf8b00ffc514038fafa",
            "pageParams": null,
            "htmlFileId": "5a795bbcdd573c04508f3a59",
            "dataUrl": null
          }],
        total: 50,
        params: {
          page: 2,// 页码
          size: 10// 每页显示个数
        }
      }
    },
    methods: {
      // 分页查询
      changePage: function () {
        // 调用query方法
        this.query();
      },
      // 查询
      query: function () {
        // alert("查询");
        // 调用服务端的接口
        cmsApi.page_list(this.params.page,this.params.size).then((res)=>{
          // 将res结果数据赋值给数据模型对象
          this.list = res.queryResult.list;
          this.total = res.queryResult.total;
        })
      }
    }
  }
</script>
<style>
  /*编写页面样式，不是必须*/
</style>
