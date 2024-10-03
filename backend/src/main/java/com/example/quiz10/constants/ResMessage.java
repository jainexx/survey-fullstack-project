package com.example.quiz10.constants;

public enum ResMessage {
	
	SUCCESS(200, "Success!"), //
	DATE_ERROR(400, "Start date cannot be later than End date!"), //
	OPTIONS_ERR0R(400, "Options cannot be null or empty!"), //
	QUIZ_NOT_FOUND(404, "Quiz not found!"), //
	QUIZ_ID_NOT_MATCH(400, "Quiz Id not match!!"), //
	QUIZ_IN_PROGRESS(400, "Quiz in progess"), //
	QUIZ_ID_OR_EMAIL_INCONSISTENT(400, "Quiz id or email inconsistent!"), //
	EMAIL_DUPLICATE(400, "Email duplicate!"), //
	CANNOT_FILLIN_QUIZ(400, "Cannot fillin Quiz"), //
	FILLIN_INCOMPLETE(400, "Fillin incomplete!"), //
	FILLIN_IS_NECESSAARY(400, "Fillin is necessary!"), //
	QUID_MISMATCH(400, "Qu Id mismatch!"), //
	SINGLE_CHOICE(400, "Single choice question"), //
	OPTION_ANS_MISMATCH(400,"Option answer mismatch!"), //
	USER_NAME_EXISTED(400, "User name existed!!"), //
	USER_NAME_NOTFOUND(400, "User name not found!!"), //
	PASSWORD_INCONSISTENT(400, "Password inconsistent!!"), //
	PLEASE_LOGIN_FIRST(400, "Please login first!!");
	
	private int code;
	
	private String message;

	// private 表示此建構方法只用在自身(如 200, "Success!")
	private ResMessage(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
	
	
}
