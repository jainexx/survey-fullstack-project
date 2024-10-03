package com.example.quiz10.service.ifs;

import com.example.quiz10.vo.BasicRes;
import com.example.quiz10.vo.UserReq;

public interface UserService {
	//方法：會員註冊(帳號＋密碼)
	public BasicRes register(UserReq req);

	//方法：會員登入
	public BasicRes login(UserReq req);

}
