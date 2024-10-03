import { defineStore } from "pinia";

export default defineStore("req", {
    state() {
        return {
            searchReq: {
                quizName: "",
                startDate: "",
                endDate: "",
            },
            quiz: {
                quizId: "", //問卷編號
                quizName: "", //問卷標題
                description: "",
                startDate: "",
                endDate: "",
                published: false, //放在預覽的發佈button
            },
            questions: [
                {
                    quizId: "", //問卷編號
                    id: 1, // 題目編號
                    name: "",
                    type: "single", //"Single","Multi","Text"
                    options: [{ option: "" }], // 單選/多選的選項
                    necessary: false, // 是否必填
                    //以下屬性不存在資料表
                    textAns: "", // 簡答題的答案
                    collapsed: false, // 是否折疊
                },
            ],
            tableData: [],
        };
    },
    getters: {},
    actions: {
        setTableData(data) {
            this.tableData = data;
        },
    },

    persist: {
        enabled: true, // 啟用持久化
        strategies: [
            {
                key: 'reqStore', // 存儲的 key
                storage: localStorage, // 使用 localStorage 或 sessionStorage
            },
        ],
    },
});
