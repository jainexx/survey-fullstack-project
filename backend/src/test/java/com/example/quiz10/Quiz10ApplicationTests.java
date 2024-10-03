package com.example.quiz10;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.quiz10.entity.Quiz;
import com.example.quiz10.repository.QuizDao;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
class Quiz10ApplicationTests {

	@Autowired
	private QuizDao quizDao;
	
	@Test
	public void test() {
		List<String> options = List.of("A", "B", "C");
		List<String> ans = List.of("B");
		List<String> ans1 = List.of("A", "C");
		List<String> ans2 = List.of("A", "B", "C");
		List<String> ans3 = List.of("A", "B", "D");
		System.out.println(options.containsAll(ans));
		System.out.println(options.containsAll(ans1));
		System.out.println(options.containsAll(ans2));
		System.out.println(options.containsAll(ans3));
		
	}
	
	
	@Test
	void contextLoads() {
		// 測試資料型態 int 加不加 @GeneratedValue 的差異
		Quiz quiz = new Quiz("QAZWSX", "XXXX", LocalDate.now(), LocalDate.now(), false);
		Quiz res = quizDao.save(quiz);
		System.out.println(res.getId());
	}
	
	@Test
	public void objectMapperTest() {
		// ObjectMapper 轉成字串後再轉回物件，可以還原物件的資料型態
		// map.toString() 轉成字串後，無法轉回物件的原本資料型態
		ObjectMapper mapper = new ObjectMapper();
		Map<Integer, String> map = new HashMap<>();
		map.put(1, "AAA");
		map.put(2, "BBB");
		map.put(3, "CCC");
		try {
			String str = mapper.writeValueAsString(map);
			String str1 = map.toString();
			System.out.println(str);
			System.out.println(str1);
			Map<Integer, String> newMap = mapper.readValue(str, Map.class);
			System.out.println(newMap);
			Map<Integer, String> newMap1 = mapper.readValue(str1, Map.class);
			System.out.println(newMap1);
		} catch (JsonProcessingException e){
			e.printStackTrace();
		}
	}
	
	@Test
	public void objectMapperTest2() {
		ObjectMapper mapper = new ObjectMapper();
		List<String> list = List.of("AAA", "BBB", "CCC");
		try {
			String str = mapper.writeValueAsString(list);
			String str1 = list.toString();
			System.out.println(str);
			System.out.println(str1);
			List<String> newList = mapper.readValue(str, new TypeReference<>() {
			});
			System.out.println(newList);
			List<String> newList1 = mapper.readValue(str1, new TypeReference<>() {
			});
			System.out.println(newList);
		} catch (JsonProcessingException e){
			e.printStackTrace();
		}
	}

}
