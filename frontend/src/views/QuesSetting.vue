<script>
import req from "@/stores/req";
import { mapState } from "pinia";
export default {
    computed: {
        ...mapState(req, ["questions"]),
    },
    methods: {
        addQuestion() {
            const newId = this.questions.length + 1; // 新的題目編號
            this.questions.push({
                id: newId,
                name: "",
                type: "single",
                options: [{ option: "" }],
                necessary: false,
                // textAns: "",
                collapsed: false,
            });

            this.$nextTick(() => {
                this.$refs[`question-input-${newId}`][0].focus();
            });
        },
        removeQuestion(index) {
            this.questions.splice(index, 1);
        },
        addOption(index) {
            this.questions[index].options.push({ option: "" });
        },
        removeOption(qIndex, optIndex) {
            this.questions[qIndex].options.splice(optIndex, 1);
        },
        toggleCollapse(index) {
            this.questions[index].collapsed = !this.questions[index].collapsed;
        },
        cancel() {
            this.$router.push("/add/quizSetting"); // 返回上一頁
        },
        submitForm() {
            this.$router.push("/add/preview");
        },
    },
};
</script>

<template>
    <div class="formContainer">
        <div class="quesWrapper">
            <div v-for="(question, index) in this.questions" :key="question.id" class="quesBlock">
                <!-- 折疊區塊 -->
                <div class="quesSummary" @click="toggleCollapse(index)">
                    <button>
                        <i :class="question.collapsed ? 'bi bi-caret-down-fill' : 'bi bi-caret-up-fill'"></i>
                    </button>
                    <span>{{ index + 1 }}. </span>
                    <span>{{ question.name || "未命名題目" }}</span>
                    <span v-if="question.necessary" class="necessaryIndicator"></span>
                </div>

                <!-- 展開區塊 -->
                <div v-if="!question.collapsed" class="quesDetail">
                    <div class="question-header">
                        <!-- 題目標題input -->
                        <input
                            :id="question.quizId + question.id"
                            type="text"
                            v-model="question.name"
                            :placeholder="'題目' + question.id"
                            class="title-input"
                            :ref="'question-input-' + question.id"
                            required />

                        <!-- 題型下拉選單 -->
                        <select v-model="question.type" class="question-type">
                            <option value="single">單選題</option>
                            <option value="multi">多選題</option>
                            <option value="text">簡答題</option>
                        </select>

                        <!-- 必填checkbox -->
                        <label class="required-checkbox">
                            <input type="checkbox" v-model="question.necessary" class="custom-checkbox" />
                            必填
                        </label>
                    </div>

                    <!-- 單選或多選 顯示選項 -->
                    <div v-if="question.type === 'single' || question.type === 'multi'" class="options-list">
                        <div v-for="(option, optIndex) in question.options" :key="optIndex" class="option-item">
                            <!-- @keydown.enter.prevent="addOption(index)" -->
                            <input type="text" v-model="option.option" :placeholder="'選項'" class="option-input" required />
                            <button @click="removeOption(index, optIndex)" class="remove-option">
                                <i class="bi bi-x-square"></i>
                            </button>
                        </div>

                        <button @click="addOption(index)" class="add-option">+ 新增選項</button>
                    </div>

                    <!-- 簡答 顯示textarea -->
                    <div v-if="question.type === 'text'" class="text-answer">
                        <textarea placeholder="簡答文字"></textarea>
                        <!-- v-model="question.textAns" -->
                    </div>

                    <!-- 刪除題目區塊的button -->
                    <button @click="removeQuestion(index)" class="delete-question">
                        <i class="bi bi-trash"></i>
                    </button>
                </div>
            </div>
        </div>
        <!-- 添加題目區塊的按鈕 -->
        <button @click="addQuestion" class="add-button">
            <i class="bi bi-plus-square"></i>
        </button>
    </div>

    <div class="buttonGroup">
        <button type="button" @click="cancel" class="cancelBtn"><i class="bi bi-arrow-left"></i>上一步</button>
        <button type="submit" class="previewBtn" @click="submitForm">預覽</button>
    </div>
</template>

<style scoped lang="scss">
* {
    font-family: "Roboto", sans-serif;
    font-weight: 400;
    font-style: normal;
    letter-spacing: 0.2rem;
}

.formContainer {
    // border: 1px solid black;
    max-width: 100%;
    height: 85%;
    margin: 0 auto;
    padding: 1rem;
    // border-top: 2px solid $black;
    border-bottom: 2px solid rgba($color: $black, $alpha: 0.3);
}

.quesWrapper {
    max-width: 100%;
    max-height: 90%;
    overflow-y: auto;
    margin: 0 auto;
    margin-bottom: 1rem;
}

.quesBlock {
    width: 80%;
    margin: 0 auto;
    border: 1px solid rgba($color: $black, $alpha: 0.3);
    border-left: 5px solid $purple;
    padding: 1rem;
    margin-bottom: 1rem;
    position: relative; /* 讓刪除按鈕定位到右側 */
}

.quesSummary {
    display: flex;
    align-items: center;
    cursor: pointer;
    font-size: 1.1rem;
    font-weight: bold;

    button {
        background: none;
        border: none;
        cursor: pointer;
        margin-right: 0.5rem;

        i {
            font-size: 1.2rem;
            color: $purple;
        }
    }

    span {
        margin-right: 0.3rem;
    }

    .necessaryIndicator {
        display: inline-block;
        width: 10px;
        height: 10px;
        background-color: $orange;
        border-radius: 50%;
        margin-left: 0.3rem;
    }
}

.quesDetail {
    display: flex;
    flex-direction: column;

    .question-header {
        display: flex;
        align-items: center;
        margin-bottom: 1rem;
        margin-top: 0.5rem;

        .title-input {
            width: 70%;
            height: 2rem;
            margin-right: 1rem;
            padding: 0.5rem;
            background-color: rgba($color: $black, $alpha: 0.05);
            border: none;
            border-radius: 5px;
        }

        .question-type {
            width: 20%;
            height: 2rem;
            margin-right: 1rem;
            padding: 0.5rem;
            background-color: rgba($color: $black, $alpha: 0.05);
            border: none;
            border-radius: 5px;
        }

        .required-checkbox {
            width: 10%;
            height: 2rem;
            margin-right: auto; /* 自動將後續元素推到右側 */
            .custom-checkbox {
                appearance: none; // appearance: none 屬性隱藏 checkbox
                width: 18px;
                height: 18px;
                border: 2px solid $black;
                border-radius: 5px;
                outline: none;
                cursor: pointer;
            }

            .custom-checkbox:checked {
                background-color: $orange;
            }
        }
    }

    .options-list {
        display: flex;
        flex-direction: column;

        .option-item {
            display: flex;
            align-items: center;
            margin-bottom: 0.5rem;
        }

        .option-input {
            width: 80%;
            height: 1.8rem;
            margin-right: 1rem;
            padding: 0.5rem;
            background-color: rgba($color: $black, $alpha: 0.05);
            border: none;
            border-radius: 5px;
        }

        .remove-option {
            background: none;
            border: none;
            color: $black;
            font-size: 1.2rem;
            cursor: pointer;
        }
    }

    .add-option {
        background: none;
        border: none;
        color: $purple;
        cursor: pointer;
        align-self: start;
    }

    .text-answer {
        // margin-top: 0.5rem;
        width: 80%;

        textarea {
            width: 100%;
            height: 5rem;
            padding: 0.5rem;
            resize: none;
            background-color: rgba($color: $black, $alpha: 0.05);
            border: none;
            border-radius: 5px;
        }
    }

    .delete-question {
        position: absolute;
        bottom: 1.5rem;
        right: 1.5rem;
        background: none;
        border: none;
        color: $purple;
        cursor: pointer;
        font-size: 1.4rem;
        font-weight: bold;
    }
}

.add-button {
    display: block;
    margin: 0 auto;
    background: none;
    border: none;
    cursor: pointer;
    i {
        font-size: 2rem;
        color: $purple;
        font-weight: bold;
    }
}

.buttonGroup {
    display: flex;
    justify-content: space-between;
    margin-left: auto;
    margin-top: 0.5rem;
    width: 300px;

    .cancelBtn,
    .previewBtn {
        width: 125px;
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
    .previewBtn {
        background-color: $black;
        color: $bgColor;
    }
}
</style>
