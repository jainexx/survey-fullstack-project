package com.example.quiz10.service.ifs;

import com.example.quiz10.vo.BasicRes;
import com.example.quiz10.vo.CreateUpdateReq;
import com.example.quiz10.vo.DeleteReq;
import com.example.quiz10.vo.FeedbackRes;
import com.example.quiz10.vo.FillInReq;
import com.example.quiz10.vo.SearchReq;
import com.example.quiz10.vo.SearchRes;
import com.example.quiz10.vo.StatisticsRes;
import com.example.quiz10.vo.UserReq;

public interface QuizService {
	
	//方法：新增問卷
	public BasicRes create(CreateUpdateReq req);
	
	//方法：更新問卷
	public BasicRes update(CreateUpdateReq req);
	
	//方法：刪除問卷
	public BasicRes delete(DeleteReq req);
	
	//方法：搜尋問卷
	public SearchRes search(SearchReq req);
	
	//方法：填寫問卷
	public BasicRes fillin(FillInReq req);
	
	//方法：統計
	public StatisticsRes statistics(int quizId);
	
	//方法：回饋
	public FeedbackRes feedback(int quizId);
}
