import { createRouter, createWebHistory } from "vue-router";
import HomeView from "../views/HomeView.vue";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: "/",
            name: "home",
            component: HomeView,
        },
        {
            path: "/login",
            name: "login",
            component: () => import("../views/LoginView.vue"),
        },
        {
            path: "/authuser",
            name: "authUser",
            component: () => import("../views/AuthUserView.vue"),
        },
        {
            path: "/add",
            name: "add",
            component: () => import("../layouts/AddLayout.vue"),
            redirect: "/add/quizSetting",
            children: [
                {
                    path: "quizSetting",
                    name: "quizSetting",
                    component: () => import("../views/QuizSetting.vue"),
                },
                {
                    path: "quesSetting",
                    name: "quesSetting",
                    component: () => import("../views/QuesSetting.vue"),
                },
                {
                    path: "preview",
                    name: "preview",
                    component: () => import("../views/Preview.vue"),
                },
            ],
        },
        {
            path: "/quizDetail/:id",
            name: "QuizDetail",
            component: () => import("../views/QuizDetail.vue"),
            props: true, // 將路由參數作為props傳遞給組件
        },
        {
            path: "/reply/:id",
            name: "reply",
            component: () => import("../views/Reply.vue"),
            props: true, // 將路由參數作為props傳遞給組件
            redirect: (to) => {
                // 使用函數動態構建重定向的目標路徑
                return `/reply/${to.params.id}/statistics`;
            },
            children: [
                {
                    path: "feedback",
                    name: "feedback",
                    component: () => import("../components/Feedback.vue"),
                },
                {
                    path: "statistics",
                    name: "Statistics",
                    component: () => import("../components/Statistics.vue"),
                },
            ],
        },
        {
            path: "/unauth",
            name: "unauth",
            component: () => import("../views/UnauthView.vue"),
        },
        {
            path: "/fillin/:id",
            name: "fillin",
            component: () => import("../views/Fillin.vue"),
            props: true, // 將路由參數作為props傳遞給組件
        },
    ],
});

export default router;
