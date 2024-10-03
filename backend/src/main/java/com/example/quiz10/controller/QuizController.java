package com.example.quiz10.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.quiz10.constants.ResMessage;
import com.example.quiz10.service.ifs.QuizService;
import com.example.quiz10.vo.BasicRes;
import com.example.quiz10.vo.CreateUpdateReq;
import com.example.quiz10.vo.DeleteReq;
import com.example.quiz10.vo.FeedbackRes;
import com.example.quiz10.vo.FillInReq;
import com.example.quiz10.vo.SearchReq;
import com.example.quiz10.vo.SearchRes;
import com.example.quiz10.vo.StatisticsRes;

@RestController
public class QuizController {
	
	@Autowired
	private QuizService quizService;
	
	// 有 req 就用 POST
	@PostMapping(value = "quiz/create")
	public BasicRes create(@Valid @RequestBody CreateUpdateReq req) {
		return quizService.create(req);
	}
	//=============================
	// 登入成功後才能使用
	@PostMapping(value = "quiz/create_login")
	public BasicRes createLogin(@Valid @RequestBody CreateUpdateReq req, HttpSession session) {
		// 取出session中"user_name"屬性的值(資料型態為Object，要變成String需要強制轉型)
		// 在 UserController 的 login 方法，若有登入成功就會透過 "user_name" 這個 key 把 req 中的 name 加入 session 裡
		// 資料暫存到 Session 的方法是 setAttribute，取出則用 getAttribute
		// key 字串 "user_name" 要完全一樣才能取出對應的 value：取不到 key 對應的 value 則是 null(沒登入或session過期時)
		// ============= 下方寫法，如果取出的 value 是 null 時，再轉型成 String 會報錯 (空指針錯誤 NullPointerException)
		// String userName = (String) session.getAttribute("user_name");
		Object userNameObj = session.getAttribute("user_name");
		if(userNameObj == null) {
			return new BasicRes(ResMessage.PLEASE_LOGIN_FIRST.getCode(), ResMessage.PLEASE_LOGIN_FIRST.getMessage());
		}
		// 若後續有要使用從 Session 取出的值，可以將 userNameObj 強制轉型成字串
		// String userName = (String) userNameObj;
		return quizService.create(req);
	}
	//=============================
	@PostMapping(value = "quiz/update")
	public BasicRes update(@Valid @RequestBody CreateUpdateReq req) {
		return quizService.update(req);
	}
	
	@PostMapping(value = "quiz/delete")
	public BasicRes delete(@Valid @RequestBody DeleteReq req) {
		return quizService.delete(req);
	}
	
	@PostMapping(value = "quiz/search")
	public SearchRes search(@RequestBody SearchReq req) {
		return quizService.search(req);
	}
	
	@PostMapping(value = "quiz/fillin")
	public BasicRes fillin(@Valid @RequestBody FillInReq req) {
		return quizService.fillin(req);
	}
	
	// 外部呼叫此 API 必須要使用 JQuery 的方式： quiz/statistics?quizId=問卷編號(要有問號)
	// quizId 的名字要和方法參數中的變數名稱一樣
	@PostMapping(value = "quiz/statistics")
	public StatisticsRes statistics(@RequestParam int quizId) {
		return quizService.statistics(quizId);
	}
	
	// 外部呼叫此 API 必須要使用 JQuery 的方式： quiz/statistics?quiz_Id=問卷編號(要有問號)
	// 因為 @RequestParam 中的 value 是 quiz_id，所以呼叫路徑問號後面的字串就會是 quiz_id
	// @RequestParam 中的 required 預設是 true
	// defaultValue 是指參數沒給值或是變數 mapping 不到時，會給預設值
	@PostMapping(value = "quiz/statistics1")
	public StatisticsRes statistics1(//
			@RequestParam (value="quiz_id", required = false, defaultValue = "")int quizId) {
		return quizService.statistics(quizId);
	}
	
	//呼叫此 API 的 URL 是 quiz/feedback?quizId=問卷編號
	@PostMapping(value = "quiz/feedback")
	public FeedbackRes feedback(@RequestParam int quizId) {
		return quizService.feedback(quizId);
	}
	
}
