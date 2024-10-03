package com.example.quiz10.vo;

import java.util.List;

public class StatisticsRes extends BasicRes{
	// 前端呈現資訊可自行新增
	
	private String quizName;
	
	// 一張問卷有多個題目
	private List<StatisticsVo> statisticsList;

	public StatisticsRes() {
		super();
	}

	public StatisticsRes(int code, String message) {
		super(code, message);
	}

	public StatisticsRes(int code, String message, String quizName,  List<StatisticsVo> statisticsList) {
		super(code, message);
		this.quizName = quizName;
		this.statisticsList = statisticsList;
	}

	public List<StatisticsVo> getStatisticsList() {
		return statisticsList;
	}

	public String getQuizName() {
		return quizName;
	}
	
	
}
