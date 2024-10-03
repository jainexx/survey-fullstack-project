<script>
import ListHeader from "@/components/ListHeader.vue";
import req from "@/stores/req";
import { mapState, mapActions } from "pinia";
export default {
    components: {
        ListHeader,
    },
    data() {
        return {
            //列表資料
            // tableData: [],
            //列表顯示頁碼
            tablePagination: {
                currentPage: 1,
                itemsPerPage: 10,
            },
            // 儲存選中的quizId
            selectedQuizzes: [],
            //列表排序
            originalData: [], // 儲存初始未排序的資料
            sortKey: "", // 當前排序的鍵（'startDate','endDate'）
            sortOrder: "", // 當前排序的順序 ('默認無排序','asc','desc')
        };
    },
    computed: {
        totalPages() {
            return Math.ceil(this.tableData.length / this.tablePagination.itemsPerPage);
        },
        //當前列表頁顯示的資料
        paginatedData() {
            let start = (this.tablePagination.currentPage - 1) * this.tablePagination.itemsPerPage;
            let end = start + this.tablePagination.itemsPerPage;
            return this.tableData.slice(start, end);
        },
        //pinia
        ...mapState(req, ["searchReq", "tableData"]),
    },
    methods: {
        //pinia
        ...mapActions(req, ["setTableData"]),
        //列表分頁
        goToPage(page) {
            this.tablePagination.currentPage = page;
        },
        prevPage() {
            if (this.tablePagination.currentPage > 1) {
                this.tablePagination.currentPage--;
            }
        },
        nextPage() {
            if (this.tablePagination.currentPage < this.totalPages) {
                this.tablePagination.currentPage++;
            }
        },

        //抓列表資料 + 判斷狀態欄
        fetchData() {
            this.$axios.post("http://localhost:8080/quiz/search", this.searchReq).then((response) => {
                this.originalData = response.data.quizResList; // 保存初始資料
                this.setTableData([...this.originalData]); // 設定初始資料到 tableData
                console.log(this.tableData);
                // 新增屬性 status
                this.tableData.forEach((item) => {
                    // 檢查是否未發佈
                    if (!item.published) {
                        item.status = "未發佈";
                    } else {
                        // 取得當前 UTC 時間
                        const currentUTCTime = new Date();
                        // 將 startDate 和 endDate 轉換為 UTC 時間
                        const startDateUTC = new Date(item.startDate + "T00:00:00Z");
                        const endDateUTC = new Date(item.endDate + "T23:59:59Z"); // 設置成當天的結束時間

                        if (currentUTCTime < startDateUTC) {
                            item.status = "未開始";
                        } else if (currentUTCTime >= startDateUTC && currentUTCTime <= endDateUTC) {
                            item.status = "進行中";
                        } else {
                            item.status = "已結束";
                        }
                    }
                });
            });
        },

        //刪除問卷
        deleteSelectedQuizzes() {
            const deleteReq = {
                quizIdList: this.selectedQuizzes,
            };
            this.$axios.post("http://localhost:8080/quiz/delete", deleteReq).then((response) => {
                console.log(response.data);
                // 刪除成功後清空選中的問卷
                this.selectedQuizzes = [];
                // 刪除後重新加載資料
                this.fetchData();
            });
        },

        // 選取該頁所有問卷
        selectAll(event) {
            if (event.target.checked) {
                // 狀態為 '未發佈' 或 '未開始' 才能選取以刪除
                this.selectedQuizzes = this.paginatedData.filter((item) => item.status == "未發佈" || item.status == "未開始").map((item) => item.id);
            } else {
                this.selectedQuizzes = [];
            }
        },
        //欄位排序
        sortData(key) {
            //同一個排序鍵：升序 → 降序 → 默認狀態
            // 如果當前的排序鍵相同且當前排序順序為降序，則重置為初始狀態
            if (this.sortKey === key && this.sortOrder === "desc") {
                this.sortKey = "";
                this.sortOrder = "";
                this.setTableData([...this.originalData]); // 重設為初始資料
                return; // 提前返回，不進行後續的排序處理
            }
            // 如果排序鍵相同但不是降序，切換為降序
            if (this.sortKey === key) {
                this.sortOrder = "desc";
            } else {
                // 如果排序鍵不同，設置新的排序鍵並默認為升序
                this.sortKey = key;
                this.sortOrder = "asc";
            }
            // 根據 sortKey 和 sortOrder 進行排序
            const sortedData = [...this.originalData].sort((a, b) => {
                if (this.sortOrder === "asc") {
                    return a[key] > b[key] ? 1 : -1;
                } else {
                    return a[key] < b[key] ? 1 : -1;
                }
            });

            this.setTableData(sortedData); // 更新排序後的資料以渲染畫面
        },
        // 根據當前排序鍵和順序，返回相應的圖標類名
        getSortIcon(key) {
            if (this.sortKey === key) {
                return this.sortOrder === "asc" ? "bi bi-sort-down" : "bi bi-sort-up";
            }
            // 默認情況下顯示為不排序的圖標
            return "bi bi-arrow-down-up";
        },
    },
    // 頁面載入時自動加載全部資料
    mounted() {
        this.fetchData();
    },
    //動態監聽searchReq
    watch: {
        searchReq: {
            deep: true,
            handler() {
                this.fetchData();
            },
        },
    },
};
</script>

<template>
    <div class="viewContainer">
        <div class="headerContainer">
            <ListHeader />
        </div>
        <div class="listContainer">
            <div class="tableBtns">
                <div class="leftBtns">
                    <button class="deleteBtn" @click="deleteSelectedQuizzes">
                        <i class="bi bi-trash3-fill"></i>
                        <span>&nbsp;[ {{ selectedQuizzes.length }} ]</span>
                        <span> 已選取 </span>
                    </button>
                    <button class="addBtn" @click="this.$router.push('/add')"><i class="bi bi-plus-lg"></i> New</button>
                </div>
                <div class="rightBtns">
                    <div class="searchContainer">
                        <i class="bi bi-search"></i>
                        <input type="search" placeholder="輸入問卷名稱" v-model="this.searchReq.quizName" />
                    </div>

                    <div class="dateGroup">
                        <input type="date" v-model="this.searchReq.startDate" />
                        <span> 至 </span>
                        <input type="date" v-model="this.searchReq.endDate" :min="this.searchReq.startDate" />
                    </div>
                </div>
            </div>
            <div class="tableBody">
                <table>
                    <thead>
                        <tr class="tHeader">
                            <th style="width: 5%; border-top-left-radius: 5px">
                                <input type="checkbox" @change="selectAll($event)" />
                            </th>
                            <th style="width: 6%">編號</th>
                            <th style="width: 28%">名稱</th>
                            <th style="width: 6%">狀態</th>
                            <th style="width: 10%; cursor: pointer" @click="sortData('startDate')">
                                開始日
                                <i :class="getSortIcon('startDate')"></i>
                            </th>
                            <th style="width: 10%; cursor: pointer" @click="sortData('endDate')">
                                結束日
                                <i :class="getSortIcon('endDate')"></i>
                            </th>
                            <th style="width: 8%; border-top-right-radius: 5px">結果統計</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="(item, index) in this.paginatedData" :key="item" class="tBody">
                            <!-- 當狀態為 '進行中' 或 '已結束' 時，顯示一個空的 <td> -->
                            <td v-if="item.status !== '進行中' && item.status !== '已結束'">
                                <input type="checkbox" :value="item.id" v-model="selectedQuizzes" />
                            </td>
                            <td v-else></td>

                            <td>{{ item.id }}</td>
                            <!-- 動態路由 -->
                            <td>
                                <RouterLink
                                    :to="'/quizDetail/' + item.id"
                                    class="routeItem"
                                    :class="{ 'disabled-link': item.status === '進行中' || item.status === '已結束' }"
                                    @click="item.status === '進行中' || item.status === '已結束' ? $event.preventDefault() : null"
                                    >{{ item.name }}</RouterLink
                                >
                            </td>
                            <td class="td-status">
                                <span
                                    :class="{
                                        'status-green': item.status === '未開始',
                                        'status-orange': item.status === '進行中',
                                        'status-grey': item.status === '已結束',
                                        'status-unpublished': item.status === '未發佈',
                                    }">
                                    {{ item.status }}
                                </span>
                            </td>
                            <td>{{ item.startDate }}</td>
                            <td>{{ item.endDate }}</td>
                            <td v-if="item.status == '進行中' || item.status == '已結束'">
                                <!-- 添加query查詢參數，傳遞到Reply頁面  -->
                                <RouterLink :to="{ path: '/reply/' + item.id, query: { from: 'authuser' } }" class="routeItem" style="font-size: 1.2rem"
                                    ><i class="bi bi-clipboard-data"></i>
                                </RouterLink>
                            </td>
                        </tr>
                    </tbody>
                </table>

                <div class="pagination">
                    <button @click="prevPage">
                        <i class="bi bi-caret-left-fill"></i>
                    </button>
                    <div
                        v-for="page in this.totalPages"
                        :key="page"
                        @click="goToPage(page)"
                        :class="{
                            hightlightPage: this.tablePagination.currentPage === page,
                        }">
                        {{ page }}
                    </div>
                    <button @click="nextPage">
                        <i class="bi bi-caret-right-fill"></i>
                    </button>
                </div>
            </div>
        </div>
        <!-- <div class="rightSideContainer">
        </div> -->
    </div>
</template>

<style scoped lang="scss">
* {
    font-family: "Roboto", sans-serif;
    font-weight: 400;
    font-style: normal;
    letter-spacing: 0.1rem;
}

.viewContainer {
    display: grid;
    grid-template-columns: repeat(12, 1fr);
    grid-template-rows: repeat(10, 1fr);
    width: 100dvw;
    height: 100dvh;
    background-color: $bgColor;

    .headerContainer {
        grid-area: 1 / 1 / 3 / 13;
    }

    .listContainer {
        grid-area: 3 / 2 / 11 / 12;
        letter-spacing: 0.2dvw;
        .tableBtns {
            // border: 2px solid black;
            width: 95%;
            height: 10%;
            margin: 1rem auto;
            display: flex;
            flex-wrap: wrap;
            align-items: center;
            justify-content: space-between;
            .leftBtns {
                display: flex;
                align-items: center;
                width: 30%;
                .deleteBtn {
                    width: 130px;
                    height: 40px;
                    background-color: transparent;
                    color: $purple;
                    border: 1px solid $purple;
                    border-top-left-radius: 5px;
                    border-bottom-left-radius: 5px;
                    cursor: pointer;
                }
                .addBtn {
                    width: 110px;
                    height: 40px;
                    background-color: $purple;
                    color: $bgColor;
                    border: none;
                    border-top-right-radius: 5px;
                    border-bottom-right-radius: 5px;
                    cursor: pointer;
                }
            }
            .rightBtns {
                width: 70%;
                display: flex;
                justify-content: end;
                .searchContainer {
                    border: 1px solid $black;
                    border-radius: 5px;
                    width: 230px;
                    height: 40px;
                    line-height: 40px;
                    padding-left: 5px;
                    margin-right: 15px;
                    input {
                        background: transparent;
                        border: none;
                        width: 190px;
                        height: 30px;
                        margin-left: 2px;
                    }
                }
                .dateGroup {
                    input {
                        height: 40px;
                        width: 130px;
                        border-radius: 5px;
                        border: 1px solid $black;
                        cursor: pointer;
                        background: transparent;
                    }
                }
            }
        }
        .tableBody {
            // border: 2px solid black;
            width: 95%;
            margin: 5px auto;
            table {
                width: 100%;
                border-collapse: collapse;
                .tHeader {
                    width: 100%;
                    height: 2.5rem;
                    color: $black;
                    background-color: rgba(14, 14, 14, 0.3);
                    th {
                        border: 0.5px solid rgba(14, 14, 14, 0.3);
                    }
                    th:first-child,
                    th:last-child {
                        border: none;
                    }
                }
                .tBody {
                    text-align: center;
                    width: 100%;
                    border: 0.5px solid rgba(14, 14, 14, 0.3);
                    transition: box-shadow 0.5s ease; /* 為 box-shadow 添加過渡效果 */

                    /* 放大整行及添加邊框 */
                    &:hover {
                        box-shadow: 0 0 0 3px rgba(14, 14, 14, 0.3); /* 使用 box-shadow 模擬紫色邊框 */
                    }
                    td {
                        height: 2.5rem;
                        border: 0.5px solid rgba(14, 14, 14, 0.3);
                        transition: background-color 0.2s ease; /* 為表格單元格背景色設定過渡效果 */
                    }
                    .td-status {
                        border: none;
                        padding: 0 0.5rem;
                        display: flex;
                        align-items: center;
                        justify-content: start;
                        //狀態欄
                        span {
                            display: inline-block;
                            margin-top: 3px;
                            padding: 4px 8px;
                            border-radius: 4px;
                            cursor: pointer;
                            margin: 0 auto;
                            &.status-green {
                                background-color: $green;
                            }

                            &.status-orange {
                                background-color: $orange;
                            }

                            &.status-grey {
                                background-color: rgba(14, 14, 14, 0.3);
                            }
                            &.status-unpublished {
                                // background-color: $green;
                                background-color: $purple;
                                color: $bgColor;
                            }
                        }
                    }

                    .routeItem {
                        color: $black;
                        transition: all 0.3s;
                        text-decoration: none;
                        font-weight: 500;
                        &:hover {
                            color: $purple;
                            font-weight: bold;
                        }
                    }
                    .disabled-link {
                        pointer-events: none; /* 禁用鼠標事件 */
                        color: rgba(14, 14, 14, 0.6); /* 改變顏色表示禁用狀態 */
                        // text-decoration: line-through; /* 可選：加刪除線表示禁用 */
                    }
                }
            }

            .pagination {
                width: 100%;
                height: 2.7rem;
                font-size: 1.1rem;
                display: flex;
                justify-content: center;
                align-items: center;
                border-bottom-right-radius: 5px;
                border-bottom-left-radius: 5px;
                background-color: rgba(14, 14, 14, 0.1);
                button,
                div {
                    margin: 0 0.5rem;
                    cursor: pointer;
                    color: $black;
                }
                button {
                    background: transparent;
                    border: none;
                }
                .hightlightPage {
                    width: 2%;
                    text-align: center;
                    background: $purple;
                    color: $bgColor;
                    border-radius: 5px;
                }
            }
        }
    }

    // .rightSideContainer {
    //     grid-area: 3 / 11 / 11 / 13;
    //     border-left: 0.5px solid black;
    // }
}
</style>


