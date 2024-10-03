package com.example.quiz10.vo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import com.example.quiz10.entity.Feedback;

public class FillInReq{
	
	@Valid
	private List<Feedback> feedbackList;

	public List<Feedback> getFeedbackList() {
		return feedbackList;
	}

	public void setFeedbackList(List<Feedback> feedbackList) {
		this.feedbackList = feedbackList;
	}
	
	
	
}
