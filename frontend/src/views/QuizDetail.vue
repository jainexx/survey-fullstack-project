<script>
import req from "@/stores/req";
import { mapState } from "pinia";
import MinorHeader from "@/components/MinorHeader.vue";
import AlertDialog from "@/components/AlertDialog.vue";
export default {
    components: {
        MinorHeader,
        AlertDialog,
    },
    data() {
        return {
            //所有問卷+題目
            allData: [],
            // 初始值設為 null，當找到匹配的物件後再更新
            matchingObject: null,
            // AlertDialog使用屬性
            isAlertVisible: false,
            alertMessage: "",
        };
    },
    computed: {
        ...mapState(req, ["searchReq"]),
    },
    //動態路由傳的id
    props: ["id"],
    methods: {
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
                        // 初始化collapsed 屬性
                        item.collapsed = false;
                    });
                } else {
                    console.log("未找到匹配的物件");
                }
            });
        },
        //題目折疊
        toggleCollapse(question) {
            question.collapsed = !question.collapsed;
        },
        //新增選項
        addOption(question, optIndex = null) {
            // 如果 optIndex 存在且為最後一個選項，或沒有提供 optIndex，則新增選項
            if (optIndex === null || optIndex === question.options.length - 1) {
                question.options.push(""); // 新增一個空的選項
            }
        },
        //移除選項
        removeOption(question, optIndex) {
            question.options.splice(optIndex, 1);
        },
        //新增題目
        addQuestion() {
            // 新增一個新題目
            this.matchingObject.quesList.push({
                id: this.matchingObject.quesList.length + 1, // 這裡的 id 根據當前長度設置
                qu: "",
                type: "single",
                necessary: false,
                options: [""],
                collapsed: false,
            });

            // 更新題目編號
            this.matchingObject.quesList.forEach((question, i) => {
                question.id = i + 1; // 題目編號從 1 開始
            });
        },
        //移除題目
        removeQuestion(index) {
            // 刪除指定的題目
            this.matchingObject.quesList.splice(index - 1, 1);

            // 更新題目編號
            this.matchingObject.quesList.forEach((question, i) => {
                question.id = i + 1; // 題目編號從 1 開始
            });
        },
        //更新問卷
        updateQuiz(isPublished) {
            this.matchingObject.published = isPublished;

            const updateReq = {
                id: this.matchingObject.id,
                name: this.matchingObject.name,
                description: this.matchingObject.description,
                startDate: this.matchingObject.startDate,
                endDate: this.matchingObject.endDate,
                published: this.matchingObject.published,
                quesList: this.matchingObject.quesList.map((question) => ({
                    quizId: this.matchingObject.id,
                    id: question.id,
                    qu: question.qu,
                    type: question.type,
                    necessary: question.necessary,
                    options: question.options.map((option) => option.trim()).join(";"),
                })),
            };

            this.$axios.post("http://localhost:8080/quiz/update", updateReq).then((response) => {
                console.log(response.data);
            });
        },
        //AlertDialog使用方法
        handleSave() {
            this.updateQuiz(false);
            this.alertMessage = "儲存成功!!";
            this.isAlertVisible = true;
        },
        handlePublish() {
            this.updateQuiz(true);
            this.alertMessage = "發佈成功！!";
            this.isAlertVisible = true;
        },
        navigateToAuthUser() {
            this.isAlertVisible = false;
            this.$router.push("/authuser");
        },
    },
    mounted() {
        this.findMatchingObject();
    },
};
</script>

<template>
    <div class="quizDetail">
        <div class="header">
            <MinorHeader />
        </div>

        <div class="quizContainer" v-if="matchingObject">
            <!-- 新增滾動容器 -->
            <div class="scrollContainer">
                <!-- 問卷內容 -->
                <form @submit.prevent="submitForm" class="formSection">
                    <div class="formBody">
                        <div class="formGroup">
                            <input type="text" v-model="matchingObject.name" required />
                        </div>
                        <div class="formGroup">
                            <textarea v-model="matchingObject.description" required></textarea>
                        </div>
                        <div class="formGroup dateGroup">
                            <label for="publishedDate">開放期間</label>
                            <input type="date" v-model="matchingObject.startDate" :min="new Date().toISOString().split('T')[0]" required />
                            <span>至</span>
                            <input type="date" v-model="matchingObject.endDate" :min="matchingObject.startDate" required />
                        </div>
                    </div>
                </form>

                <!-- 題目內容 -->
                <div class="questionSection">
                    <div v-for="question in matchingObject.quesList" :key="question.id" class="questionBlock">
                        <div class="quesSummary" @click="toggleCollapse(question)">
                            <button>
                                <i :class="question.collapsed ? 'bi bi-caret-down-fill' : 'bi bi-caret-up-fill'"></i>
                            </button>
                            <span>{{ question.id }}. </span>
                            <span>{{ question.qu || "未命名題目" }}</span>
                            <span v-if="question.necessary" class="necessaryIndicator"></span>
                        </div>

                        <div v-if="!question.collapsed" class="quesDetail">
                            <div class="question-header">
                                <input type="text" v-model="question.qu" :placeholder="'題目' + question.id" class="title-input" :ref="'question-input-' + question.id" required />
                                <select v-model="question.type" class="question-type">
                                    <option value="single">單選題</option>
                                    <option value="multi">多選題</option>
                                    <option value="text">簡答題</option>
                                </select>
                                <label class="required-checkbox">
                                    <input type="checkbox" v-model="question.necessary" class="custom-checkbox" />
                                    必填
                                </label>
                            </div>
                            <div v-if="question.type === 'single' || question.type === 'multi'" class="options-list">
                                <div v-for="(opt, optIndex) in question.options" :key="optIndex" class="option-item">
                                    <input
                                        type="text"
                                        v-model="question.options[optIndex]"
                                        :placeholder="'選項'"
                                        class="option-input"
                                        @keydown.enter.prevent="addOption(question, optIndex)"
                                        required />
                                    <button @click="removeOption(question, optIndex)" class="remove-option">
                                        <i class="bi bi-x-square"></i>
                                    </button>
                                </div>
                                <button @click="addOption(question)" class="add-option">+ 新增選項</button>
                            </div>
                            <div v-if="question.type === 'text'" class="text-answer">
                                <textarea placeholder="簡答文字"></textarea>
                            </div>
                            <button @click="removeQuestion(index)" class="delete-question">
                                <i class="bi bi-trash"></i>
                            </button>
                        </div>
                    </div>
                    <button type="button" @click="addQuestion" class="addButton"><i class="bi bi-plus-square"></i></button>
                </div>
            </div>

            <!-- 固定的按鈕區塊 -->
            <div class="buttonGroup">
                <button type="button" class="saveBtn" @click="handleSave">儲存草稿<i class="bi bi-save"></i></button>
                <button type="button" class="publishBtn" @click="handlePublish">發佈問卷<i class="bi bi-send"></i></button>
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

.quizDetail {
    max-width: 100dvw;
    max-height: 100dvh;
    display: grid;
    grid-template-columns: repeat(12, 1fr);
    grid-template-rows: repeat(10, 1fr);
    background-color: $bgColor;

    .header {
        grid-area: 1 / 1 / 2 / 13;
    }

    .quizContainer {
        grid-area: 2 / 1 / 11 / 13;
        margin: 0 auto;
        width: 80dvw;
        height: 80dvh;
        padding: 1rem;
        box-shadow: 0 2px 4px rgba($black, 0.1);
        display: flex;
        flex-direction: column;
        justify-content: space-between;

        .scrollContainer {
            border-bottom: 2px solid rgba($color: $black, $alpha: 0.3);
            overflow-y: auto; // 啟用滾動條
            max-height: calc(100% - 5rem); // 限制最大高度為容器的總高度減去底部按鈕區域的高度
            padding-right: 1rem; // 右側內邊距，避免滾動條遮擋內容
        }

        .formSection,
        .questionSection {
            margin-bottom: 1rem;
        }

        .formSection {
            max-width: 100%;
            background-color: rgba($purple, 0.7);
            padding: 0.8rem;
            color: $black;
            border-top: 2px solid $black;

            .formBody {
                display: flex;
                flex-direction: column;
                gap: 1rem;

                .formGroup {
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    label {
                        font-weight: 500;
                        margin-bottom: 0.5rem;
                    }
                    textarea {
                        width: 80%;
                        height: 10dvh;
                        resize: none;
                        border: none;
                        border-radius: 5px;
                        padding: 0.5rem;
                        font-size: 1rem;
                        color: $black;
                        background-color: rgba(245, 242, 240, 0.1);
                    }
                    input[type="text"] {
                        width: 80%;
                        text-align: center;
                        background-color: transparent;
                        padding: 0.3rem;
                        border: none;
                        border-radius: 0px;
                        border-bottom: 3px solid rgba(245, 242, 240, 0.3);
                        font-size: 1.2rem;
                        font-weight: bold;
                        color: $black;
                    }
                }

                .dateGroup {
                    display: flex;
                    align-items: center;

                    label {
                        margin-right: 1rem;
                    }

                    input[type="date"] {
                        width: 18%;
                        border: none;
                        border-radius: 5px;
                        padding: 0.5rem;
                        font-size: 1rem;
                        color: $black;
                        background-color: rgba(245, 242, 240, 0.1);
                        margin-left: 0.8rem;
                    }

                    span {
                        margin-left: 0.8rem;
                    }
                }
            }
        }

        .questionSection {
            margin-top: 0.5rem;

            .questionBlock {
                width: 80%;
                margin: 0 auto;
                border: 1px solid rgba($color: $black, $alpha: 0.3);
                border-left: 5px solid $purple;
                padding: 1rem;
                margin-bottom: 1rem;
                position: relative; /* 讓刪除按鈕定位到右側 */

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
            }
        }

        .addButton {
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
            margin: 0 auto;
            margin-top: 0.5rem;
            width: 300px;

            .saveBtn,
            .publishBtn {
                width: 125px;
                padding: 0.5rem 1rem;
                border: none;
                cursor: pointer;
                text-align: center;
            }

            .saveBtn {
                background-color: $bgColor;
                color: $black;
                border: 1px solid $black;
            }
            .publishBtn {
                background-color: $black;
                color: $bgColor;
            }
        }
    }
}
</style>
