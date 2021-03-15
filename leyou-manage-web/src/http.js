import Vue from 'vue'
import axios from 'axios'
import config from './config'

//config中定义的基础路径是：http://api.leyou.com/api
axios.defaults.baseURL = config.api; // 设置axios的基础请求路径，任何使用axios请求的路径都会自动添加这个路径
axios.defaults.timeout = 2000; // 设置axios的请求时间

// axios.interceptors.request.use(function (config) {
//   // console.log(config);
//   return config;
// })

axios.loadData = async function (url) {
  const resp = await axios.get(url);
  return resp.data;
};

Vue.prototype.$http = axios;// 将axios赋值给Vue原型的$http属性，这样一切vue实例都可以使用该对象

