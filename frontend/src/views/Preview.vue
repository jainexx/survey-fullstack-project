<script>
import req from "@/stores/req";
import { mapState } from "pinia";
import AlertDialog from "@/components/AlertDialog.vue";
export default {
    data() {
        return {
            // AlertDialog使用屬性
            isAlertVisible: false,
            alertMessage: "",
        };
    },
    components: {
        AlertDialog,
    },
    computed: {
        ...mapState(req, ["quiz", "questions"]),
    },
    methods: {
        //新增問卷
        createQuiz(isPublished) {
            this.quiz.published = isPublished;

            const createReq = {
                name: this.quiz.quizName,
                description: this.quiz.description,
                startDate: this.quiz.startDate,
                endDate: this.quiz.endDate,
                published: this.quiz.published,
                quesList: this.questions.map((question) => ({
                    id: question.id,
                    qu: question.name,
                    type: question.type,
                    necessary: question.necessary,
                    options: question.options.map((option) => option.option).join(";"),
                })),
            };

            this.$axios.post("http://localhost:8080/quiz/create", createReq).then((response) => {
                console.log(response.data);
            });
        },
        //返回題目設定頁
        backToQues() {
            this.$router.push("/add/quesSetting");
        },
        //題目展開收合切換
        toggleExpand(index) {
            this.questions[index].expanded = !this.questions[index].expanded;
        },
        //AlertDialog使用方法
        handleSave() {
            this.createQuiz(false);
            this.alertMessage = "儲存成功!!";
            this.isAlertVisible = true;
        },
        handlePublish() {
            this.createQuiz(true);
            this.alertMessage = "發佈成功！!";
            this.isAlertVisible = true;
        },
        navigateToAuthUser() {
            this.isAlertVisible = false;
            this.$router.push("/authuser");
        },
    },
};
</script>

<template>
    <div class="preview-container">
        <!-- 背景遮罩 -->ˇ
        <div class="overlay" @click="backToQues"></div>

        <!-- 預覽表單區塊 -->
        <div class="form-preview" @click.stop>
            <h1 class="quiz-title">{{ quiz.quizName }}</h1>
            <p class="quiz-description">{{ quiz.description }}</p>
            <p class="quiz-date">開放期間: {{ quiz.startDate }} ~ {{ quiz.endDate }}</p>

            <!-- 個人訊息 -->
            <div class="personal-info">
                <div class="field-group">
                    <label for="name">姓 名</label>
                    <input type="text" id="name" placeholder="輸入姓名" disabled />
                </div>
                <div class="field-group">
                    <label for="email">Email</label>
                    <input type="email" id="email" placeholder="輸入Email" disabled />
                </div>
                <div class="field-group">
                    <label for="phone">手 機</label>
                    <input type="text" id="phone" placeholder="輸入手機" disabled />
                </div>
                <div class="field-group">
                    <label for="age">年 齡</label>
                    <input type="text" id="age" placeholder="輸入年齡" disabled />
                </div>
            </div>

            <!-- 題目 -->
            <div class="questionnaire">
                <div class="header"><span class="required-indicator"></span> 為必填題目</div>
                <div v-for="(question, index) in questions" :key="question.id" class="question-block">
                    <div class="question-header" @click="toggleExpand(index)">
                        <span class="question-number">{{ index + 1 }}</span>
                        <span class="question-text">{{ question.name }}</span>
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
                        <div v-if="question.type === 'single' || question.type === 'multi'" v-for="(option, optIndex) in question.options" :key="optIndex" class="option-item">
                            <input :type="question.type === 'single' ? 'radio' : 'checkbox'" :name="'question-' + question.id" disabled />
                            <label>{{ option.option }}</label>
                        </div>

                        <textarea v-if="question.type === 'text'" class="text-answer" placeholder="請輸入答案" disabled></textarea>
                    </div>
                </div>
            </div>

            <!-- 底部按鈕 -->
            <div class="form-buttons">
                <button class="save-btn" @click="handleSave">儲存草稿</button>
                <button class="publish-btn" @click="handlePublish">發佈問卷</button>
            </div>

            <!-- 自定義警示框 -->
            <AlertDialog :visible="isAlertVisible" @confirm="navigateToAuthUser">
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
.preview-container {
    position: fixed; /* 或者使用 position: absolute */
    top: 0;
    left: 0;
    width: 100%;
    height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
    background-color: rgba(0, 0, 0, 0.8); /* 半透明黑色背景，透出 header */
    z-index: 1; /* 調整 z-index 以確保位於 header 之上 */

    .overlay {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-color: rgba(0, 0, 0, 0.5); /* 半透明遮罩，透出一些背景 */
        opacity: 0.5;
        z-index: 2;
    }

    .form-preview {
        position: relative;
        z-index: 3;
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

                        label {
                            margin-left: 10px;
                        }
                    }

                    .text-answer {
                        margin-left: 30px;
                        width: 90%;
                        height: 80px;
                        padding: 8px;
                        border-radius: 5px;
                        font-size: 14px;
                        resize: none;
                        border: none;
                        border-radius: 5px;
                        background-color: rgba(0, 0, 0, 0.05);
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

                &.save-btn {
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
