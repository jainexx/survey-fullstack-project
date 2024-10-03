<script>
import req from "@/stores/req";
import { mapState } from "pinia";
export default {
    computed: {
        ...mapState(req, ["quiz"]),
    },
    methods: {
        submitForm() {
            this.$router.push("/add/quesSetting");
        },
        cancel() {
            this.$router.push("/authuser"); // 返回上一頁
        },
    },
};
</script>

<template>
    <!-- {{ this.quiz.quizName }}, {{ this.quiz.description }} ,{{ this.quiz.startDate }} ,{{ this.quiz.endDate }} -->
    <div class="quizContainer">
        <form @submit.prevent="submitForm">
            <div class="formGroup">
                <label for="quizName">問卷標題</label>
                <input type="text" id="quizName" v-model="this.quiz.quizName" placeholder="標 題" required />
            </div>

            <div class="formGroup" style="align-items: start">
                <label for="description">問卷說明</label>
                <textarea id="description" v-model="this.quiz.description" placeholder="說 明" required></textarea>
            </div>

            <div class="formGroup dateGroup">
                <label for="publishedDate">開放期間</label>
                <input type="date" id="startDate" v-model="this.quiz.startDate" :min="new Date().toISOString().split('T')[0]" required />
                <span>至</span>
                <input type="date" id="endDate" v-model="this.quiz.endDate" :min="this.quiz.startDate" required />
            </div>

            <div class="divider"></div>

            <div class="buttonGroup">
                <button type="button" @click="cancel" class="cancelBtn"><i class="bi bi-arrow-left"></i>取消</button>
                <button type="submit" class="nextBtn">下一步<i class="bi bi-arrow-right"></i></button>
            </div>
        </form>
    </div>
</template>

<style scoped lang="scss">
* {
    font-family: "Roboto", sans-serif;
    font-weight: 400;
    font-style: normal;
    letter-spacing: 0.5rem;
}

.quizContainer {
    // border: 2px solid black;
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: start;
    // border-top: 2px solid $black;
    position: relative;

    form {
        width: 85%;
        height: 90%;
        // border: 2px solid black;
        display: flex;
        flex-wrap: wrap;
        justify-content: center;
        position: relative;

        .formGroup,
        .dateGroup {
            width: 100%;
            margin: 1rem auto;
            display: flex;
            align-items: center;
            // border: 2px solid black;
        }

        label {
            font-size: 1.2rem;
            font-weight: bold;
        }

        input,
        textarea {
            width: 75%;
            background-color: rgba($color: $black, $alpha: 0.05);
            border: none;
            border-radius: 5px;
            margin-left: 2rem;
            letter-spacing: 0.2dvw;
            padding: 0.8rem 2rem;
        }

        span {
            margin-left: 2rem;
            font-size: 1.2rem;
            font-weight: bold;
        }

        input {
            height: 2.5rem;
        }

        textarea {
            resize: none;
            height: 100%;
        }

        input[type="date"] {
            width: auto;
            margin-right: 0.5rem;
        }

        .divider {
            background-color: rgba($color: $black, $alpha: 0.3);
            position: absolute;
            height: 2px;
            width: 90vw;
            top: calc(100% - 4rem);
            margin-left: calc(-7.5vw);
        }

        .buttonGroup {
            display: flex;
            justify-content: space-between;
            margin-left: auto;
            margin-top: auto;
            width: 350px;

            .cancelBtn,
            .nextBtn {
                width: 150px;
                padding: 0.5rem 1rem;
                border: none;
                cursor: pointer;
                text-align: center;
            }

            .cancelBtn {
                background-color: $bgColor;
                color: $black;
                border: 1px solid $black;
            }
            .nextBtn {
                background-color: $black;
                color: $bgColor;
            }
        }
    }
}
</style>
