<template>
  <div>
    one页面
  </div>
</template>
<script>
  export default {
    //指定页面布局，若不指定，则默认为/layouts/default.vue页面
    layout: 'test'
  }
</script>
<style>
</style>

<!--
  nuxt基础路由，根据路径自动生成
  该页面访问路径为: localhost:10000/user
  自动访问index.vue页面
-->
