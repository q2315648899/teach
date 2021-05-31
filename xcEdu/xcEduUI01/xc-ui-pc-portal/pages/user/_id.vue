<template>
  <div>
    修改用户信息{{id}}
  </div>
</template>
<script>
  export default{
    layout:"test",
    data(){
      return {
        id:''
      }
    },
    mounted(){
      this.id = this.$route.params.id;
      console.log(this.id)
    }
  }

</script>
<style>
</style>

<!--
  nuxt基础路由，根据路径自动生成
  该页面访问路径为: localhost:10000/user
  自动访问index.vue页面
-->
