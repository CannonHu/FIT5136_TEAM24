import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import axios from 'axios'
import qs from 'qs'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
Vue.use(ElementUI)

Vue.config.productionTip = false
Vue.prototype.$axios= axios

axios.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded;charset=UTF-8';

axios.interceptors.request.use(config => {
  config.withCredentials = true
  config.timeout = 6000

  let token = sessionStorage.getItem('access_token')
  if (config.method === 'post') {
    config.data = qs.stringify(config.data);
  }
  if (token) {
    config.headers.access_token = token
  }
  return config;
}, error => {
  console.log('error param')
  return Promise.reject(error)
})

axios.interceptors.response.use((res) =>{
    if(!res.data.success){
        return Promise.resolve(res);
    }
    return res;
}, (error) => {
    console.log('network error')
    return Promise.reject(error);
});

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
