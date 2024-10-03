//改在 vite.config.js 全域引入
//import './assets/main.scss'

import { createApp } from "vue";
import { createPinia } from "pinia";
import piniaPersistedstate from "pinia-plugin-persistedstate";

import App from "./App.vue";
import router from "./router";
import "bootstrap-icons/font/bootstrap-icons.css";
// import * as ElementPlusIconsVue from "@element-plus/icons-vue";
import axios from "axios";

// 創建 Pinia 實例並配置持久化插件
const pinia = createPinia();
pinia.use(piniaPersistedstate);

// 創建 Vue 應用並掛載 Pinia
const app = createApp(App);

//全局註冊 Element Plus 圖標組件
// for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
//     app.component(key, component);
// }

// 將 Axios 附加到全域變數
app.config.globalProperties.$axios = axios;

// 正確掛載 Pinia 和其他插件
app.use(pinia);
app.use(router);

app.mount("#app");
