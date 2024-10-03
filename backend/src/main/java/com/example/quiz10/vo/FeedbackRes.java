package com.example.quiz10.vo;

import java.util.List;

import com.example.quiz10.entity.Feedback;

public class FeedbackRes extends BasicRes{
	
	private List<Feedback> feedbacklist;

	public FeedbackRes() {
		super();
	}

	public FeedbackRes(int code, String message) {
		super(code, message);
	}

	public FeedbackRes(int code, String message, List<Feedback> feedbacklist) {
		super(code, message);

		this.feedbacklist = feedbacklist;
	}

	public List<Feedback> getFeedbacklist() {
		return feedbacklist;
	}
	
	

}
