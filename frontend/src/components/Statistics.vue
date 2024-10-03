<script>
import req from "@/stores/req";
import { mapState } from "pinia";
import * as echarts from "echarts";

export default {
    data() {
        return {
            quizId: this.$route.params.id,
            statisRes: {
                quizName: "",
                statisticsList: [],
            },
        };
    },
    computed: {
        ...mapState(req, ["tableData"]),
    },
    methods: {
        // 抓統計資料，同時將題目類型放進統計資料
        async findMatchingData() {
            try {
                const mathingObject = this.tableData.find((item) => item.id === Number(this.quizId));
                const response = await this.$axios.post(`http://localhost:8080/quiz/statistics?quizId=${this.quizId}`);
                this.statisRes = response.data;
                mathingObject.quesList.forEach((quesItem) => {
                    const matchingStat = this.statisRes.statisticsList.find((statItem) => statItem.quId === quesItem.id);
                    if (matchingStat) {
                        matchingStat.type = quesItem.type; // 新增 type 屬性
                    }
                });

                this.$nextTick(() => {
                    // 確保 DOM 完成後進行圖表更新
                    this.updateChart();
                });
            } catch (error) {
                console.error(error);
            }
        },
        // 更新 ECharts 圖表
        updateChart() {
            this.statisRes.statisticsList.forEach((stat) => {
                const data = Object.entries(stat.optionCountMap).map(([name, value]) => ({ name, value }));
                this.drawChart(stat.quId, data);
            });
        },
        // 初始化並繪製圖表
        drawChart(quId, data) {
            var chartDom = document.getElementById(`chart-${quId}`);
            if (chartDom) {
                var myChart = echarts.init(chartDom);
                var option = {
                    tooltip: {
                        trigger: "item",
                    },
                    legend: {
                        orient: "vertical", // 設置為垂直排列
                        left: "left", // 將 legend 移到左側
                        top: "center", // 垂直居中
                        formatter: (name) => {
                            const item = data.find((d) => d.name === name);
                            return `${name}(${item.value})`; // 顯示名稱和統計次數
                        },
                    },
                    series: [
                        {
                            name: "Answers",
                            type: "pie",
                            radius: ["40%", "70%"], // 調整餅圖大小
                            center: ["60%", "70%"], // 將餅圖中心移到右側
                            startAngle: 180,
                            endAngle: 360,
                            data: data, // 使用從 statisticsList 中生成的數據
                            emphasis: {
                                itemStyle: {
                                    shadowBlur: 10,
                                    shadowOffsetX: 0,
                                    shadowColor: "rgba(0, 0, 0, 0.5)",
                                },
                            },
                        },
                    ],
                };
                myChart.setOption(option);
            }
        },
    },
    mounted() {
        this.findMatchingData();
    },
};
</script>

<template>
    <div class="statistics-container">
        <h1>{{ statisRes.quizName }}</h1>
        <div v-for="stat in statisRes.statisticsList" :key="stat.quId" class="question-block">
            <!-- 標題列 -->
            <div class="question-header">
                <span class="question-id">{{ stat.quId }}</span>
                <span class="question-text">{{ stat.qu }}</span>
                <span class="question-type">{{ stat.type === "single" ? "單選" : "多選" }}</span>
            </div>
            <!-- 答案區塊 -->
            <div :id="'chart-' + stat.quId" class="my-chart"></div>
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
.statistics-container {
    border-top: 1px solid $black;
    width: 85%;
    max-height: 78dvh;
    margin: 0 auto;
    padding: 20px;
    background-color: $bgColor; /* 使用米白色背景 */
    overflow-y: auto; /* 當內容超過高度時顯示滾動條 */

    h1 {
        text-align: center;
        font-weight: 500;
        font-size: 1.8rem;
        margin-bottom: 20px;
    }

    .question-block {
        width: 80%;
        border-bottom: 1px solid rgba(14, 14, 14, 0.2);
        margin: 0 auto;
        
        .question-header {
            display: flex;
            align-items: center;
            padding: 10px 0;
            color: $black;

            .question-id {
                margin-right: 10px;
                font-weight: bold;
                color: $bgColor;
                background-color: $purple;
                border-radius: 5px;
                font-size: 1.2rem;
                text-align: center;
                width: 30px;
            }

            .question-text {
                flex: 1; /* 讓題目敘述佔滿可用空間 */
                margin-right: 10px;
                color: $black;
                font-size: 1.1rem;
            }

            .question-type {
                color: $black;

            }
        }

        .my-chart {
            padding: 0 4rem;
            width: 100%;
            height: 300px;
        }
    }
}
</style>
