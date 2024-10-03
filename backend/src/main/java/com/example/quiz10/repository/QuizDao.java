package com.example.quiz10.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.quiz10.entity.Quiz;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface QuizDao extends JpaRepository<Quiz, Integer>{

	// 找出要刪除的 idList 中是否有包含進行中的問卷
	// 判定“進行中”的條件： 1.已發布 2.當前日>=開始日 3. 當前日<=結束日
	// PublishedTrue: 直接在 published 後面給定 true，所以方法的參數中不用再給 Published 這個欄位對應的值
	public boolean existsByIdInAndPublishedTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
			   List<Integer> idList, LocalDate now1, LocalDate now2);
	
	//搜尋問卷的自訂方法
	//1. findByNameContaining 關鍵字搜尋(問卷名稱用模糊比對)
	//2. StartDateGreaterThanEqual 搜尋大於等於StartDate的日期
	//3. EndDateLessThanEqual 搜尋小於等於EndDate的日期
	public List<Quiz> findByNameContainingAndStartDateGreaterThanEqualAndEndDateLessThanEqual( //
			String quizName, LocalDate startDate, LocalDate endDate);
}
