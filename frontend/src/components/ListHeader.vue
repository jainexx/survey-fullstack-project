<script>
import { RouterLink } from "vue-router";
export default {
    data() {
        return {
            userName: "User Name", // 根據實際登入者信息動態設置
            taiwanTime: "", // 初始時間顯示
            fading: false, // 控制動畫的狀態
        };
    },
    computed: {
        greetingMessage() {
            if (this.$route.path.startsWith("/authuser")) {
                return `Good Morning! ${this.userName}`;
            } else if (this.$route.path.startsWith("/unauth")) {
                return "Good Morning! Stranger :)";
            }
            return "Good Morning!";
        },
        isAuthUserRoute() {
            return this.$route.path.startsWith("/authuser");
        },
    },
    methods: {
        updateTaiwanTime() {
            const date = new Date();
            const options = {
                timeZone: "Asia/Taipei",
                hour: "2-digit",
                minute: "2-digit",
                hour12: false,
            }; // 台灣時間，24小時制
            this.taiwanTime = new Intl.DateTimeFormat("en-US", options).format(date);
            this.fading = true; // 觸發動畫效果
        },
    },
    mounted() {
        this.updateTaiwanTime(); // 初始化台灣時間
        setInterval(this.updateTaiwanTime, 60000); // 每分鐘更新一次時間
    },
};
</script>

<template>
    <div class="cpContainer">
        <div class="logoContainer">
            <img src="../assets/logo.png" alt="logo" @click="this.$router.push('/')" />
        </div>
        <div class="greetContainer">
            <div class="location">
                <!-- <i class="bi bi-geo-alt"></i> -->
                <i class="bi bi-clock"></i>
                <!-- <span> Tainan, Taiwan</span> -->
                <span :class="{ fade: fading }" @animationend="fading = false">&nbsp; {{ taiwanTime }}</span>
            </div>
            <div class="greeting">
                <span>{{ greetingMessage }} </span>
            </div>
        </div>
        <div class="btnsContainer" v-if="isAuthUserRoute">
            <button class="btn"><i class="bi bi-bell"></i></button>
            <button class="btn"><i class="bi bi-person"></i></button>
        </div>
    </div>
</template>

<style scoped lang="scss">
.cpContainer {
    width: 100%;
    height: 100%;
    background-color: $green;
    display: grid;
    grid-template-columns: repeat(12, 1fr);
    grid-template-rows: repeat(2, 1fr);

    .logoContainer {
        grid-area: 1 / 1 / 3 / 2;
        // border: 2px solid black;
        align-self: center;
        text-align: end;
        img {
            width: 90%;
            height: 100%;
            cursor: pointer;
        }
    }

    .greetContainer {
        grid-area: 1 / 2 / 3 / 11;
        padding-left: 2rem;
        // border: 2px solid black;
        .location {
            height: 40%;
            font-size: 1.2rem;
            display: flex;
            align-items: end;

            .fade {
                animation: fadeIn 1s; /* 定義動畫效果和持續時間 */
            }

            @keyframes fadeIn {
                0% {
                    opacity: 0;
                }
                100% {
                    opacity: 1;
                }
            }
        }
        .greeting {
            height: 50%;
            font-size: 1.8rem;
            display: flex;
            align-items: center;
        }
    }

    .btnsContainer {
        grid-area: 1 / 12 / 2 / 12;
        // border: 2px solid black;
        align-self: center;
        .btn {
            width: 35px;
            height: 35px;
            font-size: 1.4rem;
            margin-right: 15px;
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
