<script>
import { mapState } from "pinia";
import req from "@/stores/req";
import AlertDialog from "@/components/AlertDialog.vue";
export default {
    data() {
        return {
            matchingObject: null,
            //儲存每個問題的答案
            answers: {},
            userData: {
                name: "",
                phone: "",
                email: "",
                age: null,
            },
            // AlertDialog使用屬性
            isAlertVisible: false,
            isSubmitSuccessful: false, // 用於標識提交是否成功
            alertMessage: "",
        };
    },
    components: {
        AlertDialog,
    },
    computed: {
        ...mapState(req, ["searchReq"]),
    },
    props: ["id"],
    methods: {
        initializeAnswers() {
            if (this.matchingObject) {
                this.matchingObject.quesList.forEach((question) => {
                    if (question.type === "multi") {
                        // 直接初始化多選題的答案為空數組
                        this.answers[question.id] = [];
                    } else {
                        // 對於非多選題，可以初始化為空字符串或其他適當值
                        this.answers[question.id] = "";
                    }
                });
            }
        },
        //抓匹配問卷資料
        findMatchingObject() {
            this.$axios.post("http://localhost:8080/quiz/search", this.searchReq).then((response) => {
                this.allData = response.data.quizResList;
                // 使用 find 方法在 allData 中找到與 props 傳遞過來的 id 匹配的物件
                const foundObject = this.allData.find(
                    //item.id是數字，this.id是字串，===會比較內容和資料型態
                    (item) => item.id === Number(this.id)
                );
                if (foundObject) {
                    // 將匹配的物件賦值給 data 中的 matchingObject
                    this.matchingObject = foundObject;
                    console.log(this.matchingObject);

                    this.matchingObject.quesList.forEach((item) => {
                        //將options的值從字串轉成陣列
                        item.options = item.options.split(";");
                        // 初始化expanded 屬性
                        item.expanded = true;
                    });
                } else {
                    console.log("未找到匹配的物件");
                }
            });
        },
        //題目展開收合切換
        toggleExpand(index) {
            this.matchingObject.quesList[index].expanded = !this.matchingObject.quesList[index].expanded;
        },
        //返回unauth
        backToList() {
            this.$router.push("/unauth");
        },
        //填寫問卷
        handleFillin() {
            // request: feedbackList
            const feedbackList = this.matchingObject.quesList.map((question) => ({
                quizId: question.quizId,
                quId: question.id,
                name: this.userData.name,
                phone: this.userData.phone,
                email: this.userData.email,
                age: this.userData.age,
                ans: Array.isArray(this.answers[question.id]) ? this.answers[question.id].join(";") : this.answers[question.id] || "",
                fillinDateTime: new Date().toISOString(),
            }));

            // 發送 POST 請求
            this.$axios
                .post("http://localhost:8080/quiz/fillin", { feedbackList })
                .then(() => {
                    this.isSubmitSuccessful = true;
                    this.alertMessage = "提交成功！";
                    this.isAlertVisible = true;
                })
                .catch((error) => {
                    this.isSubmitSuccessful = false;
                    this.alertMessage = "提交失敗，請重試！";
                    this.isAlertVisible = true;
                    console.error(error);
                });
        },
        handleDialogConfirm() {
            if (this.isSubmitSuccessful) {
                this.$router.push("/unauth"); // 如果提交成功，跳轉到 /unauth
            } else {
                // 還要修改！！使用從路由傳入的問卷ID進行跳轉
                this.$router.push("/unauth");
            }
        },
    },
    mounted() {
        this.findMatchingObject();
        this.$watch("matchingObject", (newVal) => {
            if (newVal) {
                this.initializeAnswers(); // 初始化答案
            }
        });
    },
};
</script>

<template>
    <div class="fillin-container">
        <div v-if="matchingObject" class="form-preview">
            <h1 class="quiz-title">{{ matchingObject.name }}</h1>
            <p class="quiz-description">{{ matchingObject.description }}</p>
            <p class="quiz-date">開放期間: {{ matchingObject.startDate }} ~ {{ matchingObject.endDate }}</p>

            <!-- 個人訊息 -->
            <form class="personal-info">
                <div class="field-group">
                    <label for="name">姓 名</label>
                    <input type="text" id="name" placeholder="輸入姓名" v-model="userData.name" required />
                </div>
                <div class="field-group">
                    <label for="email">Email</label>
                    <input type="email" id="email" placeholder="輸入Email" v-model="userData.email" required />
                </div>
                <div class="field-group">
                    <label for="phone">手 機</label>
                    <input type="text" id="phone" placeholder="輸入手機" v-model="userData.phone" required />
                </div>
                <div class="field-group">
                    <label for="age">年 齡</label>
                    <input type="text" id="age" placeholder="輸入年齡" v-model="userData.age" required />
                </div>
            </form>

            <!-- 題目 -->
            <div class="questionnaire">
                <div class="header"><span class="required-indicator"></span> 為必填題目</div>
                <div v-for="(question, index) in matchingObject.quesList" :key="question.id" class="question-block">
                    <div class="question-header" @click="toggleExpand(index)">
                        <span class="question-number">{{ index + 1 }}</span>
                        <span class="question-text">{{ question.qu }}</span>
                        <span v-if="question.necessary" class="question-required"></span>
                        <span class="question-type">
                            <span v-if="question.type === 'single'">單選</span>
                            <span v-else-if="question.type === 'multi'">複選</span>
                            <span v-else-if="question.type === 'text'">簡答</span></span
                        >
                        <span class="expand-icon">
                            <i v-if="question.expanded" class="bi bi-chevron-up"></i>
                            <i v-else class="bi bi-chevron-down"></i>
                        </span>
                    </div>

                    <!-- 單選、多選和文本框選項 -->
                    <div v-if="question.expanded" class="options">
                        <!-- 單選題 -->
                        <div
                            v-if="question.type === 'single'"
                            v-for="(option, optIndex) in question.options"
                            :key="optIndex"
                            class="option-item"
                            :class="{
                                'selected-option': answers[question.id] === option,
                                'unselected-option': answers[question.id] !== option && answers[question.id],
                            }">
                            <!-- 確保同一組的單選題選項使用相同的 name -->
                            <input type="radio" :name="'question_' + question.id" v-model="answers[question.id]" :value="option" />
                            <label>{{ option }}</label>
                        </div>

                        <!-- 多選題 -->
                        <div
                            v-if="question.type === 'multi'"
                            v-for="(option, optIndex) in question.options"
                            :key="optIndex"
                            class="option-item"
                            :class="{
                                'selected-option': answers[question.id]?.includes(option),
                                'unselected-option': !answers[question.id]?.includes(option) && answers[question.id]?.length > 0,
                            }">
                            <input type="checkbox" :name="'question_' + question.id" v-model="answers[question.id]" :value="option" />
                            <label>{{ option }}</label>
                        </div>

                        <!-- 簡答題 -->
                        <textarea
                            v-if="question.type === 'text'"
                            class="text-answer"
                            placeholder="請輸入答案"
                            v-model="answers[question.id]"
                            :class="{ 'highlighted-input': answers[question.id] }"></textarea>
                    </div>
                </div>
            </div>

            <!-- 底部按鈕 -->
            <div class="form-buttons">
                <button class="cancel-btn" @click="backToList">取消</button>
                <button class="publish-btn" @click="handleFillin">送出</button>
            </div>

            <!-- 自定義警示框-->
            <AlertDialog :visible="isAlertVisible" @confirm="handleDialogConfirm">
                <p>{{ alertMessage }}</p>
            </AlertDialog>
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
.fillin-container {
    width: 100%;
    height: 100vh;
    background-color: rgba(0, 0, 0, 0.8); /* 半透明黑色背景，透出 header */
    position: relative;

    .form-preview {
        position: relative;
        top: 2.5vh;
        margin: 0 auto;
        // z-index: 3;
        width: 70%;
        height: 95vh;
        background-color: $bgColor;
        padding: 1.5rem 2rem;
        box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        border-left: 9px solid $purple;

        .quiz-title {
            font-size: 2rem;
            color: $black;
            text-align: center;
            font-weight: bold;
        }

        .quiz-description {
            font-size: 1rem;
            color: $black;
            text-align: center;
            margin: 0.5rem 0;
            padding: 0.4rem 3rem;
            border-top: 2px solid $black;
        }

        .quiz-date {
            font-size: 0.9rem;
            color: $black;
            text-align: center;
            margin-bottom: 0.5rem;
        }

        .personal-info {
            display: flex;
            justify-content: space-between;
            flex-wrap: wrap;
            max-width: 650px;
            margin: 0 auto;

            .field-group {
                display: flex;
                // flex-direction: column;
                align-items: center;
                margin-bottom: 0.8rem;
                width: 45%;

                label {
                    font-size: 14px;
                    margin-right: 8px;
                    color: $black;
                    font-weight: 600;
                }

                input {
                    width: 75%;
                    padding: 8px;
                    border: none;
                    border-radius: 5px;
                    background-color: rgba(0, 0, 0, 0.1);
                    font-size: 12px;
                }
            }
        }

        .questionnaire {
            max-height: 445px;
            overflow-y: auto;
            padding: 10px;
            // border: 1px solid #ccc;
            border-radius: 4px;
            // background-color: #f9f9f9;

            .header {
                font-size: 14px;
                margin-bottom: 8px;
                display: flex;
                align-items: center;
                justify-content: end;

                .required-indicator {
                    width: 12px;
                    height: 12px;
                    background-color: $orange;
                    border-radius: 50%;
                    margin-right: 8px;
                }
            }

            .question-block {
                padding: 10px;
                border-bottom: 1px solid rgba(0, 0, 0, 0.2);
                margin-bottom: 10px;

                .question-header {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    cursor: pointer;

                    .question-number {
                        font-weight: bold;
                        background-color: $purple;
                        color: $bgColor;
                        padding: 5px 8px;
                        border-radius: 5px;
                        margin-right: 10px;
                    }

                    .question-text {
                        flex: 1;
                    }

                    .question-required {
                        width: 12px;
                        height: 12px;
                        background-color: $orange;
                        border-radius: 50%;
                        margin-right: 10px;
                    }

                    .expand-icon {
                        font-size: 14px;
                        margin-left: 10px;
                    }
                }

                .options {
                    margin-top: 10px;

                    .option-item {
                        display: flex;
                        align-items: center;
                        margin-bottom: 5px;
                        margin-left: 30px;
                        color: $black;

                        label {
                            margin-left: 10px;
                        }
                    }

                    .selected-option {
                        color: darken($purple, 20%);;
                        font-weight: bold;
                        padding: 5px;
                        border: 1px solid $purple; /* 邊框顏色 */
                        box-shadow: 0 0 3px rgba($purple, 0.5); /* 陰影效果 */
                        border-radius: 5px;
                        transition: background-color 0.3s, border-color 0.3s;
                    }

                    .unselected-option {
                        color: rgba(0, 0, 0, 0.5); /* 未選中選項的顏色變淡 */
                    }

                    .text-answer {
                        margin-left: 30px;
                        width: 90%;
                        height: 80px;
                        padding: 8px;
                        border-radius: 5px;
                        font-size: 14px;
                        resize: none;
                        border: 2px solid $bgColor;
                        border-radius: 5px;
                        background-color: rgba(0, 0, 0, 0.05);
                    }

                    .text-answer:focus {
                        outline: none; /* 移除默認的藍色外框 */
                        border-color: $purple; /* 自定義聚焦時的邊框顏色 */
                    }

                    .highlighted-input {
                        border: 2px solid rgba($purple, 1); /* 簡答題輸入內容時的邊框顏色 */
                        border-radius: 5px;
                        transition: border-color 0.3s;
                    }
                }
            }
        }

        .form-buttons {
            display: flex;
            justify-content: center;
            margin-top: 1rem;

            button {
                padding: 0.5rem 2rem;
                border: none;
                margin: 0 1rem;
                cursor: pointer;

                &.cancel-btn {
                    background-color: $black;
                    color: $bgColor;
                }

                &.publish-btn {
                    background-color: $purple;
                    color: $bgColor;
                }
            }
        }
    }
}
</style>
