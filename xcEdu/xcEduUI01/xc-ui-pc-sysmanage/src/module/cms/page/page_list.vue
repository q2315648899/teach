<template>
  <div>
    <!--编写页面静态部分，即view部分-->
    <div>
      <!--相当于编写html的内容-->
      <!--查询表单-->
      <el-form :model="params">
        <el-select v-model="params.siteId" placeholder="请选择站点">
          <el-option
            v-for="item in siteList"
            :key="item.siteId"
            :label="item.siteName"
            :value="item.siteId">
          </el-option>
        </el-select>
        页面别名：<el-input v-model="params.pageAliase" style="width: 100px"></el-input>
        <el-button type="primary" v-on:click="query" size="small">查询</el-button>
        <router-link class="mui-tab-item" :to="{path:'/cms/page/add/'}">
          <el-button type="primary" size="small">新增页面</el-button>
        </router-link>
      </el-form>
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
        :total="total" :current-page="this.params.page" style="float:right;">
        <!--v-on:current-change="changePage"也可写成@:current-change="changePage"-->
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
        siteList:[],//站点列表
        list: [],
        total: 0,
        params: {
          siteId:'',
          pageAliase:'',
          page: 1,// 页码
          size: 10// 每页显示个数
        }
      }
    },
    methods: {
      // 分页查询
      changePage: function (page) {
        this.params.page = page;
        // 调用query方法
        this.query()
      },
      // 查询
      query: function () {
        // alert("查询");
        // 调用服务端的接口
        cmsApi.page_list(this.params.page, this.params.size, this.params).then((res) => {
          // 将res结果数据赋值给数据模型对象
          this.list = res.queryResult.list;
          this.total = res.queryResult.total;
        })
      }
    },
    // 钩子方法
    mounted() {
      // 默认查询页面
      // DOM元素渲染生成完成后立即调用
      this.query()
      this.siteList = [
        {
          siteId:'5a751fab6abb5044e0d19ea1',
          siteName:'门户主站'
        },
        {
          siteId:'102',
          siteName:'测试站'
        }
      ]
    }
  }
</script>
<style>
  /*编写页面样式，不是必须*/
</style>
