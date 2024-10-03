package com.example.quiz10.vo;

import java.util.List;

public class SearchRes extends BasicRes{
	// code + message 繼承
	
	// List 放問卷們
	private List<QuizRes> quizResList;

	public SearchRes() {
		super();
	}

	//失敗時回傳
	public SearchRes(int code, String message) {
		super(code, message);
	}

	// 成功時回傳
	public SearchRes(int code, String message, List<QuizRes> quizResList) {
		super(code, message);
		this.quizResList = quizResList;
	}

	public List<QuizRes> getQuizResList() {
		return quizResList;
	}
	
	
	
}
