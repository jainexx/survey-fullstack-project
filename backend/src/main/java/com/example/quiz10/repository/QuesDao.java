package com.example.quiz10.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.quiz10.entity.Ques;
import com.example.quiz10.entity.QuesId;

@Repository
public interface QuesDao extends JpaRepository<Ques, QuesId>{

	// 自訂方法: 刪除同張問卷下的所有題目
	public void deleteByQuizId(int quizId);
	
	// 自訂方法: 用 quizId 找出問卷下的所有題目
	public List<Ques> findByQuizId(int quizId);
	
	// 自訂方法: 用 quizIdList 找出問卷下的所有題目
	public List<Ques> findByQuizIdIn(List<Integer> quizIdList);
	
	// 自訂方法：查找指定 quizId 下，不屬於特定題目類型的所有題目，並將它們組成列表返回
	public List<Ques> findByQuizIdAndTypeNot(int quizId, String selectType);
}
