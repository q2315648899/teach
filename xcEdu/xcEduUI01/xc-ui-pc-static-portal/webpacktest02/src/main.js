
// 导入model01.js
var {add} = require("./model01.js");//".js"后缀也可省略
var Vue = require("./vue.min.js");

var VM = new Vue({
    el:"#app",//表示当前vue对象接管app的div区域
    data:{
        name:'传智播客',// 相当于是MVVM中的Model这个角色
        num1:0,
        num2:0,
        result:0,
        url:'http://www.baidu.com',
        size:20
    },
    methods:{
        change:function () {
            this.result = add(Number.parseInt(this.num1),Number.parseInt(this.num2));
            // this.result = Number.parseInt(this.num1)+Number.parseInt(this.num2);
            // alert("计算结果："+this.result);
        }
    }
});