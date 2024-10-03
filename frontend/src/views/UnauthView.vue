<script>
import ListHeader from "@/components/ListHeader.vue";
import req from "@/stores/req";
import { mapState } from "pinia";
export default {
    components: {
        ListHeader,
    },
    data() {
        return {
            //列表資料
            tableData: [],
            //列表顯示頁碼
            tablePagination: {
                currentPage: 1,
                itemsPerPage: 10,
            },
            // 儲存選中的quizId
            selectedQuizzes: [],
            //列表排序
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
        ...mapState(req, ["searchReq"]),
    },
    methods: {
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
                this.tableData = response.data.quizResList;
                console.log(this.tableData);
                // 新增屬性 status
                this.tableData.forEach((item) => {
                    // 檢查是否未發佈
                    if (!item.published) {
                        item.status = "未開始";
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
        //欄位排序
        sortData(key) {
            //同一個排序鍵：升序 → 降序 → 默認狀態
            // 如果當前的排序鍵相同且當前排序順序為降序，則重置為默認狀態
            if (this.sortKey === key && this.sortOrder === "desc") {
                this.sortKey = "";
                this.sortOrder = "";
                this.tableData = [...this.tableData]; // 重置為原始順序
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

            //只有當排序鍵存在時才進行排序
            if (this.sortKey) {
                // 排序數據
                this.paginatedData.sort((a, b) => {
                    let dateA = new Date(a[key]);
                    let dateB = new Date(b[key]);

                    if (this.sortOrder === "asc") {
                        return dateA - dateB; // 升序
                    } else {
                        return dateB - dateA; // 降序
                    }
                });
            }
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
                            <td>{{ item.id }}</td>
                            <td>
                                <!-- 動態路由 -->
                                <RouterLink
                                    :to="'/fillin/' + item.id"
                                    class="routeItem"
                                    :class="{ 'disabled-link': item.status !== '進行中' }"
                                    @click="item.status !== '進行中' ? $event.preventDefault() : null"
                                    >{{ item.name }}</RouterLink
                                >
                            </td>
                            <td class="td-status">
                                <span
                                    :class="{
                                        'status-green': item.status === '未開始',
                                        'status-orange': item.status === '進行中',
                                        'status-grey': item.status === '已結束',
                                    }">
                                    {{ item.status }}
                                </span>
                            </td>
                            <td>{{ item.startDate }}</td>
                            <td>{{ item.endDate }}</td>
                            <td v-if="item.status !== '未開始' && item.published">
                                <!-- 添加query查詢參數，傳遞到Reply頁面  -->
                                <RouterLink :to="{ path: '/reply/' + item.id, query: { from: 'unauth' } }" class="routeItem" style="font-size: 1.2rem"
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
            // .leftBtns {
            //     display: flex;
            //     align-items: center;
            //     width: 30%;
            //     .deleteBtn {
            //         width: 130px;
            //         height: 40px;
            //         background-color: transparent;
            //         color: $purple;
            //         border: 1px solid $purple;
            //         border-top-left-radius: 5px;
            //         border-bottom-left-radius: 5px;
            //         cursor: pointer;
            //     }
            //     .addBtn {
            //         width: 110px;
            //         height: 40px;
            //         background-color: $purple;
            //         color: $bgColor;
            //         border: none;
            //         border-top-right-radius: 5px;
            //         border-bottom-right-radius: 5px;
            //         cursor: pointer;
            //     }
            // }
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
}
</style>
