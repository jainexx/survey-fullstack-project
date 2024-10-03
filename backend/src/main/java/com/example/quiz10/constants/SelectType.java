package com.example.quiz10.constants;

public enum SelectType {
	// 單選：single, 多選：multi, 簡答：text
	SINGLE("Single"), //
	MULTI("Multi"), //
	TEXT("Text");

	private String type;

	private SelectType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	// 確認從前端帶進來的字串是"Single","Multi","Text"三者之一

}
