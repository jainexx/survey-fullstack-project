<script>
import MinorHeader from "@/components/MinorHeader.vue";
import { RouterLink, RouterView } from "vue-router";
export default {
    data() {
        return {
            // count: 10,
            canViewFeedback: false,
        };
    },
    components: {
        MinorHeader,
    },
    //動態路由傳的id
    props: ["id"],
    methods: {
        // 用於切換到對應的子路由
        navigateTo(route) {
            // 確保導航時包含父路由的 id
            this.$router.push(`/reply/${this.$route.params.id}/${route}`);
        },
        // 檢查當前子路由是否處於活動狀態
        isActive(route) {
            return this.$route.path.includes(route);
        },
        // 檢查權限決定要不要顯示nav區塊
        checkPermissions() {
            // 檢查導航來源：接收query查詢參數是"authuser"的數據
            const fromAuthUser = this.$route.query.from === "authuser";

            if (fromAuthUser) {
                this.canViewFeedback = true; // 如果來自 authuser，顯示完整內容
            } else {
                this.canViewFeedback = false; // 如果來自 unauth，不顯示 nav 區塊
            }
        },
    },
    mounted() {
        //組件加載時就進行權限檢查
        this.checkPermissions();
    },
};
</script>

<template>
    <div class="reply">
        <div class="header">
            <MinorHeader />
        </div>

        <!-- 動態組件顯示區域 -->
        <div class="content">
            <!-- 根據 canViewFeedback 控制是否顯示導航區塊 -->
            <nav class="reply-nav" v-if="canViewFeedback">
                <button :class="{ active: isActive('feedback') }" @click="navigateTo('feedback')">回饋</button>
                <div class="divider"></div>
                <button :class="{ active: isActive('statistics') }" @click="navigateTo('statistics')">統計</button>
                <!-- <div class="reply-count">{{ count }} 則回覆</div> -->
            </nav>

            <div class="routerView">
                <RouterView />
            </div>
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

.reply {
    max-width: 100dvw;
    max-height: 100dvh;
    display: grid;
    grid-template-columns: repeat(12, 1fr);
    grid-template-rows: repeat(10, 1fr);
    background-color: $bgColor;
    .header {
        grid-area: 1 / 1 / 2 / 13;
    }

    .content {
        grid-area: 2 / 1 / 11 / 13;

        .reply-nav {
            display: flex;
            align-items: center;
            width: 85%;
            padding: 0.5rem 1rem;
            background-color: rgba(14, 14, 14, 0.05);
            margin: 0 auto;

            button {
                margin: 0 auto;
                padding: 0.5rem 1rem;
                border: none;
                cursor: pointer;
                background-color: transparent;
                font-size: 1.2rem;
                font-weight: bold;
                color: rgba(14, 14, 14, 0.3);
                transition: color 0.3s, border-bottom-color 0.3s;

                &.active {
                    color: $black;
                }
            }
        }
    }
}
</style>
