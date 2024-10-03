<script>
import { RouterLink } from "vue-router";
export default {
    components: {
        RouterLink,
    },
    data() {
        return {
            steps: [{ name: "問卷設定", route: "/add/quizSetting" }, { name: "題目設定", route: "/add/quesSetting" }, { name: "預覽問卷", route: "/add/preview" }, { name: "發布問卷" }],
        };
    },
    methods: {
        handleLogoClick() {
            const currentPath = this.$route.path; // 取得目前路由的 path

            // 根據父層路由決定導向
            if (currentPath.includes("/add") || currentPath.includes("/quizDetail") || currentPath.includes("/reply")) {
                this.$router.push("/authuser");
            } else {
                this.$router.push("/");
            }
        },
    },
};
</script>

<template>
    <div class="cpContainer">
        <div class="logoContainer">
            <img src="../assets/logo.png" alt="logo" @click="handleLogoClick" />
        </div>

        <!-- 根據路由顯示 -->
        <div v-if="$route.path.startsWith('/add')" class="stepNavigation">
            <RouterLink v-for="step in steps" :key="step.name" :to="step.route" class="step" :class="{ active: $route.path === step.route }"> / {{ step.name }} </RouterLink>
        </div>

        <div v-else class="routerContainter">
            <RouterLink :to="`/quizDetail/${$route.params.id}`" class="routeItem" :class="{ active: $route.path.startsWith('/quizDetail') }">問 題</RouterLink>
            <RouterLink :to="`/reply/${$route.params.id}`" class="routeItem" :class="{ active: $route.path.startsWith('/reply') }">回 覆</RouterLink>
        </div>

        <div class="btnsContainer">
            <button class="btn"><i class="bi bi-person"></i></button>
        </div>
    </div>
</template>

<style scoped lang="scss">
* {
    font-family: "Roboto", sans-serif;
    font-weight: 400;
    font-style: normal;
    letter-spacing: 0.2rem;
}

.cpContainer {
    width: 100%;
    height: 100%;
    background-color: $green;
    display: grid;
    grid-template-columns: repeat(12, 1fr);
    grid-template-rows: 1fr;

    .logoContainer {
        grid-area: 1 / 1 / 2 / 2;
        // border: 2px solid black;
        img {
            width: 90%;
            height: 100%;
            cursor: pointer;
        }
    }

    .stepNavigation {
        grid-area: 1 / 3 / 2 / 11;
        display: flex;
        justify-content: center;
        align-items: flex-end;
        font-size: 1rem;
        .step {
            display: flex;
            align-items: center;
            color: rgba(14, 14, 14, 0.4);
            text-decoration: none;
            font-weight: bold;
            margin-bottom: 0.5rem;
        }
        .active {
            color: $black;
        }
    }

    .routerContainter {
        grid-area: 1 / 3 / 2 / 11;
        // border: 2px solid black;
        display: flex;
        justify-content: center;
        align-items: flex-end;
        .routeItem {
            font-weight: bold;
            font-size: 1.3rem;
            margin: 0 1rem;
            text-decoration: none;
            color: rgba(14, 14, 14, 0.5);
            width: 7.5rem;
            text-align: center;
            // border: 2px solid black;
        }
        .active {
            color: $black;
            text-align: center;
            border-bottom: 4px solid $black;
        }
    }

    .btnsContainer {
        grid-area: 1 / 12 / 2 / 13;
        // border: 2px solid black;
        padding: 20px;

        .btn {
            width: 35px;
            height: 35px;
            font-size: 1.4rem;
            background-color: $bgColor;
            color: $black;
            border: 0.5px solid $black;
            border-radius: 5px;
            cursor: pointer;
            :hover {
                color: $orange;
            }
        }
    }
}
</style>
