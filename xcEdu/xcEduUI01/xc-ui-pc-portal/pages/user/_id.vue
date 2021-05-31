<template>
  <div>
    修改用户信息{{id}}，名称:{{name}}
  </div>
</template>
<script>
  export default {
    layout: "test",
    async asyncData() {
      console.log("请求服务端接口...")
      // alert(0) 无法在服务端运行
      // 请求服务端接口...

      // 先调用a方法
      var a = await new Promise(function (resolve, reject) {
        setTimeout(function () {
          console.log("1")
          resolve(1)
        },2000)
      });
      // 再调用b方法
      var b = await new Promise(function (resolve, reject) {
        setTimeout(function () {
          console.log("2")
          resolve(2)
        },1000)
      });

      return {
        name: '黑马程序员'
      };
    },
    data() {
      return {
        id: '',
        name: ''
      }
    },
    methods: {
      getUser: function () {
        // ajax请求服务的接口
        this.name = "传智博客"
      },
      a() {
        return new Promise(function (resolve, reject) {
          setTimeout(function () {
            resolve(1)
          }, 1000)
        })
      },
      b() {
        return new Promise(function (resolve, reject) {
          setTimeout(function () {
            resolve(2)
          }, 2000)
        })
      }

    },
    mounted() {
      //从请求中获取参数
      this.id = this.$route.params.id;
      console.log(this.id)
      // this.getUser()

      //先执行b方法，后执行a方法，执行结果为a方法先执行(并没有按照方法执行的顺序输出,使用promise实现了异步调用)
      // this.b().then(res => {
      //   alert(res)
      // });
      // this.a().then(res => {
      //   alert(res)
      // });
    }
  }

</script>
<style>
</style>

<!--
  请求中携带参数，并获取
  命名为 _id.vue,请求页面需携带id参数
-->

<!--
  从服务端获取数据在客户端赋值，页面源代码没有数据，不利于SEO
  所以需要在服务端赋值完成，返回到客户端时即为完整的页面，才有利于SEO
-->

<!--
  asyncData()方法在组件加载之前调用，相当于在服务端完成
  在该方法内加载数据，到客户端组件加载时，得到的即为完整的页面数据，
  查看页面源代码，即可看到显示数据
-->

<!--
  promise方法是异步调用
  可以在asyncData()方法中使用async和await两个关键字实现同步调用
-->
