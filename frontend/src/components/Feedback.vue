<script>
import req from "@/stores/req";
import { mapState } from "pinia";

export default {
    data() {
        return {
            quizId: this.$route.params.id,
            quiz: [
                {
                    id: "",
                    name: "",
                    quesList: [],
                },
            ],
            feedbackList: null,
            organizedFeedback: [],
            showDrawer: false,
            currentDetail: {},

            currentPage: 1,
            itemsPerPage: 10,
            selectedItem: null, // 用於追踪選中的按鈕
        };
    },
    computed: {
        ...mapState(req, ["tableData"]),

        //分頁
        paginatedItems() {
            const start = (this.currentPage - 1) * this.itemsPerPage;
            const end = start + this.itemsPerPage;
            return this.organizedFeedback.slice(start, end);
        },
        totalPages() {
            return Math.ceil(this.organizedFeedback.length / this.itemsPerPage);
        },
    },
    methods: {
        // 請求所需資料
        fetchData() {
            // quiz 問卷資料(id. name, quesList)
            this.quiz = this.tableData.filter((item) => item.id === Number(this.quizId)).map(({ id, name, quesList }) => ({ id, name, quesList }));
            console.log(this.quiz);

            // 獲取並整理填寫者資料
            this.$axios.post(`http://localhost:8080/quiz/feedback?quizId=${this.quizId}`).then((res) => {
                this.feedbackList = res.data.feedbacklist;

                const feedbackMap = this.feedbackList.reduce((acc, item) => {
                    // 如果此 email 已經存在於 acc 中，則更新它的 ansList
                    if (acc[item.email]) {
                        acc[item.email].ansList.push({
                            quId: item.quId,
                            ans: item.ans,
                        });
                    } else {
                        // 如果此 email 尚不存在，則新增一個新元素
                        acc[item.email] = {
                            name: item.name,
                            email: item.email,
                            phone: item.phone,
                            age: item.age,
                            fillinDateTime: item.fillinDateTime,
                            ansList: [
                                {
                                    quId: item.quId,
                                    ans: item.ans,
                                },
                            ],
                        };
                    }
                    return acc;
                }, {});

                // 將資料轉換為陣列，並依據填寫日期排序
                this.organizedFeedback = Object.values(feedbackMap).sort((a, b) => new Date(b.fillinDateTime) - new Date(a.fillinDateTime));

                console.log(this.organizedFeedback);
            });
        },
        //詳細資料
        showDetail(item) {
            this.selectedItem = item.email; // 設定選中的按鈕
            this.currentDetail = {
                ...item,
                ansList: item.ansList.map((answer) => {
                    // 合併答案和題目敘述
                    const question = this.quiz[0].quesList.find((q) => q.id === answer.quId);
                    return {
                        ...answer,
                        questionText: question ? question.qu : "題目不存在", // 加入題目描述
                    };
                }),
            };
            this.showDrawer = true;
        },
        closeDrawer() {
            this.showDrawer = false;
            this.selectedItem = null; // 關閉時重置選中的按鈕
        },
        changePage(page) {
            this.currentPage = page;
        },
    },
    mounted() {
        this.fetchData();
    },
};
</script>

<template>
    <div :class="['feedback-container', { 'drawer-open': showDrawer }]">
        <div class="table-pages">
            <table>
                <thead>
                    <tr>
                        <th>編號</th>
                        <th>姓名</th>
                        <th>填寫日期</th>
                        <th>詳細內容</th>
                    </tr>
                </thead>
                <tbody>
                    <!-- 全部的資料 organizedFeedback 替換成當前頁面資料 paginatedItems -->
                    <tr v-for="(item, index) in paginatedItems" :key="index">
                        <td>{{ index + 1 }}</td>
                        <td>{{ item.name }}</td>
                        <!-- 填寫日期，只顯示日期部分 -->
                        <td>{{ item.fillinDateTime.split("T")[0] }}</td>
                        <td><button @click="showDetail(item)" :class="{ active: selectedItem === item.email }">查看</button></td>
                    </tr>
                </tbody>
            </table>

            <!-- 分頁 -->
            <div class="pagination">
                <button v-for="page in totalPages" :key="page" @click="changePage(page)" :class="{ active: currentPage === page }">
                    {{ page }}
                </button>
            </div>
        </div>

        <!-- Drawer 內容 -->
        <div :class="['drawer', { 'drawer-open': showDrawer }]" @click.self="closeDrawer">
            <div v-if="showDrawer" class="drawer-content">
                <button class="close-btn" @click="closeDrawer"><i class="bi bi-x-square-fill"></i></button>
                <h3>{{ quiz[0].name }}</h3>
                <div class="info-section">
                    <div class="info-item">
                        <label>姓名</label>
                        <span>{{ currentDetail.name }}</span>
                    </div>
                    <div class="info-item">
                        <label>Email</label>
                        <span>{{ currentDetail.email }}</span>
                    </div>
                    <div class="info-item">
                        <label>電話</label>
                        <span>{{ currentDetail.phone }}</span>
                    </div>
                    <div class="info-item">
                        <label>年齡</label>
                        <span>{{ currentDetail.age }}</span>
                    </div>
                </div>
                <ul>
                    <li v-for="answer in currentDetail.ansList" :key="answer.quId">
                        <div class="question-header">
                            <span class="question-number">{{ answer.quId }}</span>
                            <span class="question-text">{{ answer.questionText }}</span>
                        </div>
                        <div class="answer">
                            {{ answer.ans.split(';').join(' ｜ ') }}
                        </div>
                    </li>
                </ul>
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
.feedback-container {
    width: 85%;
    height: 100%;
    margin: 0 auto;
    text-align: center;
    border-top: 1px solid $black;
    display: flex;
    justify-content: center; // 當沒有 Drawer 時讓 table 置中
    transition: all 0.3s ease;

    &.drawer-open {
        justify-content: space-between; // 當 Drawer 打開時調整布局
    }

    .table-pages {
        width: 60%;
        table {
            width: 100%;
            border-collapse: collapse;
            transition: width 0.3s ease;
            margin: auto; // 使表格置中
            margin-top: 2rem;

            thead {
                font-weight: bolder;
            }

            th,
            td {
                border: 1px solid rgba(14, 14, 14, 0.1);
                padding: 8px;
                text-align: center;

                button {
                    border: none;
                    cursor: pointer;
                    position: relative;
                    color: $black; // 默認顏色
                    font-weight: normal; // 默認字體重量

                    &::after {
                        position: absolute;
                        bottom: 5px;
                        right: -10px;
                        content: "";
                        width: 10px;
                        height: 10px;
                        background-color: $orange;
                    }

                    &.active {
                        font-weight: bold; // 點擊後字體變粗
                        color: $orange; // 點擊後字體顏色變化
                        &::after {
                            display: none; // 點擊後隱藏 ::after
                        }
                    }
                }
            }
        }

        .pagination {
            margin: 20px 0;
            button {
                margin: 0 5px;
                padding: 5px 10px;
                cursor: pointer;
                &.active {
                    font-weight: bold;
                    color: $bgColor;
                    background-color: $purple;
                    border: none;
                    border-radius: 5px;
                }
            }
        }
    }
}

.drawer {
    position: relative;
    top: 0;
    right: 0;
    width: 0; // 初始寬度為 0
    height: 100%;
    background-color: transparent;
    transition: width 0.3s ease; // 添加平滑過渡效果
    overflow: hidden; // 隱藏超出部分
    z-index: 1000;

    &.drawer-open {
        width: 66.66%; // Drawer 打開時寬度設為 2/3
    }

    .drawer-content {
        padding: 5px 10px;
        max-height: 78dvh;
        overflow-y: auto;
        display: flex;
        flex-direction: column;
        border-left: 1px solid lighten($purple, 20%);
        padding: 5px;
        margin-left: 5px;
        box-shadow: -2px 0 5px rgba(0, 0, 0, 0.1);

        .close-btn {
            display: block;
            margin-left: auto;
            padding: 5px 10px;
            cursor: pointer;
            border: none;
            background-color: transparent;

            i {
                font-size: 1.4rem;
                color: $orange;
            }
        }

        h3 {
            color: $purple;
            font-weight: 600;
            font-size: 24px;
            text-align: center;
            margin-bottom: 20px;
        }

        .info-section {
            display: grid;
            grid-template-columns: 1fr 1fr; // 兩欄佈局
            gap: 10px;
            margin-bottom: 20px;

            .info-item {
                display: flex;
                align-items: center;
                justify-content: center;
                width: 85%;

                label {
                    font-size: 12px;
                    margin-bottom: 5px;
                }
                span {
                    width: 70%;
                    font-size: 14px;
                    color: $black;
                    font-weight: 600;
                    border-bottom: 3px solid rgba($black, 0.1);
                    margin-left: 10px;
                }
            }
        }

        ul {
            list-style: none;
            padding: 0;
            margin: 0 auto;

            li {
                margin-bottom: 20px;
                padding: 10px;
                // background: lighten($purple, 40%);
                border-radius: 8px;

                .question-header {
                    display: flex;
                    align-items: center;
                    margin-bottom: 5px;

                    .question-number {
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        background: $purple;
                        color: $bgColor;
                        padding: 5px 8px;
                        border-radius: 5px;
                        font-weight: bold;
                        margin-right: 10px;
                    }

                    .question-text {
                        font-size: 16px;
                        color: $black;
                    }
                }

                .answer {
                    background: $bgColor;
                    padding: 10px;
                    border-radius: 5px;
                    border: 1px solid lighten($purple, 20%);
                    font-size: 14px;
                    color: $black;
                }
            }
        }
    }
}
</style>
