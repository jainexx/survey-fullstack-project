package com.example.quiz10.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.quiz10.constants.ResMessage;
import com.example.quiz10.constants.SelectType;
import com.example.quiz10.entity.Feedback;
import com.example.quiz10.entity.Ques;
import com.example.quiz10.entity.Quiz;
import com.example.quiz10.repository.FeedbackDao;
import com.example.quiz10.repository.QuesDao;
import com.example.quiz10.repository.QuizDao;
import com.example.quiz10.service.ifs.QuizService;
import com.example.quiz10.vo.BasicRes;
import com.example.quiz10.vo.CreateUpdateReq;
import com.example.quiz10.vo.DeleteReq;
import com.example.quiz10.vo.FeedbackRes;
import com.example.quiz10.vo.FillInReq;
import com.example.quiz10.vo.QuizRes;
import com.example.quiz10.vo.SearchReq;
import com.example.quiz10.vo.SearchRes;
import com.example.quiz10.vo.StatisticsRes;
import com.example.quiz10.vo.StatisticsVo;

@Service
public class QuizServiceImpl implements QuizService {

	@Autowired
	private QuizDao quizDao;

	@Autowired
	private QuesDao quesDao;

	@Autowired
	private FeedbackDao feedbackDao;

	// @Transactional import 的 library， javax 和 springframework 都可以使用
	// 兩者差異可參照 PPT spring boot_02 @Transactional 部分
	@Transactional
	@Override
	public BasicRes create(CreateUpdateReq req) {
		// 基本的屬性判斷已交由 @Valid
		// 判斷：開始日不能比結束日晚
		if (req.getStartDate().isAfter(req.getEndDate())) {
			return new BasicRes(ResMessage.DATE_ERROR.getCode(), ResMessage.DATE_ERROR.getMessage());
		}

		// 判斷題目非簡答題時，選項要有值
		List<Ques> quesList = req.getQuesList();
		for (Ques item : quesList) {
			// 檢查題目中的 quizId 和問卷中的 Id 是否相同 (防止非合理操作，如quiz_id不是從quiz送來的狀況)
			if (item.getQuizId() != req.getId()) {
				return new BasicRes(ResMessage.QUIZ_ID_NOT_MATCH.getCode(), ResMessage.QUIZ_ID_NOT_MATCH.getMessage());
			}

			// 判斷item.getType()不是簡答題(Text)的狀況
			// if(!item.getType().equalsIgnoreCase(SelectType.TEXT.getType())) { }

			// 不使用上面的寫法是因為從 req 過來的參數值可能會有第4種的值
			// 下面判斷式的寫法就必須是 SINGLE 或 MULTI 之一
			if (item.getType().equalsIgnoreCase(SelectType.SINGLE.getType())
					|| item.getType().equalsIgnoreCase(SelectType.MULTI.getType())) {
				// 選項沒有值就回傳錯誤訊息
				if (!StringUtils.hasText(item.getOptions())) {
					return new BasicRes(ResMessage.OPTIONS_ERR0R.getCode(), ResMessage.OPTIONS_ERR0R.getMessage());
				}
			}
		}
		// req存入資料庫
		// 1- 存問卷
		/*
		 * 將返回的 res 中的 id(quiz_id) 塞到 Ques 中的 quizId 屬性上 因 Quiz 中的 id 是 AI 自動生成的流水號，要讓
		 * quizDao 執行 save 後可以把該 id 的值回傳 必須在 Quiz Entity 中將資料型態為 int 的屬性 id
		 * 加上 @GeneratedValue...
		 */
		Quiz res = quizDao.save(
				new Quiz(req.getName(), req.getDescription(), req.getStartDate(), req.getEndDate(), req.isPublished()));
		quesList.forEach(item -> {
			item.setQuizId(res.getId());
		});
		// 2- 存題目
		quesDao.saveAll(quesList);
		// 3- 回傳成功訊息
		return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
	}

	@Transactional
	@Override
	public BasicRes update(CreateUpdateReq req) {
		// req參數檢查同create()方法
		if (req.getStartDate().isAfter(req.getEndDate())) {
			return new BasicRes(ResMessage.DATE_ERROR.getCode(), ResMessage.DATE_ERROR.getMessage());
		}
		List<Ques> quesList = req.getQuesList();
		for (Ques item : quesList) {
			if (item.getQuizId() != req.getId()) {
				return new BasicRes(ResMessage.QUIZ_ID_NOT_MATCH.getCode(), ResMessage.QUIZ_ID_NOT_MATCH.getMessage());
			}

			if (item.getType().equalsIgnoreCase(SelectType.SINGLE.getType())
					|| item.getType().equalsIgnoreCase(SelectType.MULTI.getType())) {
				if (!StringUtils.hasText(item.getOptions())) {
					return new BasicRes(ResMessage.OPTIONS_ERR0R.getCode(), ResMessage.OPTIONS_ERR0R.getMessage());
				}
			}
		}

		// 檢查欲修改的問卷是否已存在
		// 1-不存在：回傳錯誤訊息
		// req.getId() <= 0 成立就不用動到quizDao (此判斷可以不寫）
		if (req.getId() <= 0 || !quizDao.existsById(req.getId())) {
			return new BasicRes(ResMessage.QUIZ_NOT_FOUND.getCode(), ResMessage.QUIZ_NOT_FOUND.getMessage());
		}

		// 2-存在：修改的req存入資料庫
		quizDao.save(new Quiz(req.getId(), req.getName(), req.getDescription(), req.getStartDate(), req.getEndDate(),
				req.isPublished()));
		// 防止編號錯亂: 刪除全部 -> 新增
		// 刪除同張問卷下的所有題目(使用自訂方法)
		quesDao.deleteByQuizId(req.getId());
		// 新增更新後的題目
		quesDao.saveAll(quesList);
		return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
	}

	@Override
	public BasicRes delete(DeleteReq req) {
		// "進行中"的問卷不能刪除：找出要刪除的 idList 中是否有包含進行中的問卷
		// 進行中的問卷條件：1.已發布 且 2.當前日期>=開始日期 且 3.當前日期<=結束日期
		boolean res = quizDao.existsByIdInAndPublishedTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual( //
				req.getQuizIdList(), LocalDate.now(), LocalDate.now());
		if (res) { // res == true，表示要刪除的問卷 ID 中有正在進行中的
			return new BasicRes(ResMessage.QUIZ_IN_PROGRESS.getCode(), ResMessage.QUIZ_IN_PROGRESS.getMessage());
		}

		// 刪除選取的問卷
		quizDao.deleteAllById(req.getQuizIdList());
		return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());

	}

	@Override
	public SearchRes search(SearchReq req) {
		// 用到的參數
		String quizName = req.getQuizName();
		LocalDate startDate = req.getStartDate();
		LocalDate endDate = req.getEndDate();

		// 三個條件不設定時，各自設定成預設值
		if (!StringUtils.hasText(quizName)) { // quizName 沒值 = 不用關鍵字搜尋代表非搜尋條件之一，所以其值可能是空字串,null,全空白
			quizName = ""; // 把 quizName 變成空字串是因為 Containing 的值帶空字串時，會撈出 quizName 全部的欄位
		}
		if (startDate == null) {
			// startDate = LocalDate.MIN; //為'-999999999-01-01'的字串，超出了資料庫所能處理的日期範圍
			startDate = LocalDate.of(1970, 1, 1); // 很久以前的日期
		}
		if (endDate == null) {
			endDate = LocalDate.of(2999, 12, 31); // 很久之後的日期
		}

		// 問卷列表
		List<Quiz> res = quizDao.findByNameContainingAndStartDateGreaterThanEqualAndEndDateLessThanEqual(quizName,
				startDate, endDate);
		// quizResList 放搜尋結果(所有問卷+問卷下的題目)
		List<QuizRes> quizResList = new ArrayList<>();

		// 方法1: 使用 foreach 根據每個 quizId 去撈對應的 Ques
		// 缺點： 依據 Quiz 的多寡決定連接 DB 撈取資料的次數，問卷越多，連接 DB 次數就越多（Dao使用多）
		for (Quiz item : res) {
			// 根據 quizId 撈取每張問卷裡的 quesList (所有題目)
			int quizId = item.getId();
			List<Ques> quesList = quesDao.findByQuizId(quizId);
			// 建立不同問卷的 QuizRes 用來放 Quiz 和對應的 List<Ques>
			// 若參數太多使用建構方法容易混亂，建議用以下的方式
			QuizRes quizRes = new QuizRes();
			quizRes.setId(quizId);
			quizRes.setName(item.getName());
			quizRes.setDescription(item.getDescription());
			quizRes.setStartDate(item.getStartDate());
			quizRes.setEndDate(item.getEndDate());
			quizRes.setPublished(item.isPublished());
			quizRes.setQuesList(quesList);
			// 把每張不同的問卷+問題放進 List<QuizRes>
			quizResList.add(quizRes);
		}

		// 方法2: 搜集所有符合的 quizId 組成 List，再一次撈取符合條件的所有 Ques（不管問卷多寡，固定就是連接 DB 兩次）
		List<Integer> quizIdList = new ArrayList<>();
		for (Quiz item : res) {
			quizIdList.add(item.getId());
		}
		// 用 quizIdList 撈出所有題目
		List<Ques> quesList = quesDao.findByQuizIdIn(quizIdList);
		// quizResList2 放搜尋結果(所有問卷+問卷下的題目)
		List<QuizRes> quizResList2 = new ArrayList<>();
		// 將符合搜尋條件的 res(所有的問卷) 和 quesList(所有的問題) 配對
		for (Quiz item : res) {
			int quizId = item.getId();
			List<Ques> returnQuesList = new ArrayList<>();
			for (Ques quesItem : quesList) {
				// 判斷 Quiz 和 Ques 中的 quizId 是否相同
				if (quizId == quesItem.getQuizId()) {
					// quizId 相同，就加進要返回的 QuesList 中
					returnQuesList.add(quesItem);
				}
			}
			QuizRes quizRes = new QuizRes();
			quizRes.setId(quizId);
			quizRes.setName(item.getName());
			quizRes.setDescription(item.getDescription());
			quizRes.setStartDate(item.getStartDate());
			quizRes.setEndDate(item.getEndDate());
			quizRes.setPublished(item.isPublished());
			quizRes.setQuesList(returnQuesList);
			// 把每張不同的問卷+問題放進 List<QuizRes>
			quizResList2.add(quizRes);
		}

		// return new SearchRes(ResMessage.SUCCESS.getCode(),
		// ResMessage.SUCCESS.getMessage(), quizResList);
		return new SearchRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(), quizResList2);
	}

	@Override
	public BasicRes fillin(FillInReq req) {
		// 1. 檢查 quizId 和 email
		// 1.1 檢查 req 中的 List<Feedback>，所有 Feedback 的 quizId 和 email 都是一樣的，就是 quizId 和
		// email 都只會有一個
		List<Feedback> feedbackList = req.getFeedbackList();
		// Set 不允許重複， quizId 和 email 都只會有一個，代表兩個 Set 長度正常都應該是 1
		Set<Integer> quizIdSet = new HashSet<>();
		Set<String> emailSet = new HashSet<>();
		for (Feedback item : feedbackList) {
			quizIdSet.add(item.getQuizId());
			emailSet.add(item.getEmail());
		}
		if (quizIdSet.size() != 1 || emailSet.size() != 1) { // set 大小不是 1 就代表 quizId 或 email 超過 1 個 (quizId 不一致 或 email
																// 不一致)
			return new BasicRes(ResMessage.QUIZ_ID_OR_EMAIL_INCONSISTENT.getCode(),
					ResMessage.QUIZ_ID_OR_EMAIL_INCONSISTENT.getMessage());
		}

		// 1.2 檢查同一個 email + quizId 是否已存在 (同一個 email 已經填寫過同一張問卷)
		int quizId = feedbackList.get(0).getQuizId();
		if (feedbackDao.existsByQuizIdAndEmail(quizId, req.getFeedbackList().get(0).getEmail())) {
			return new BasicRes(ResMessage.EMAIL_DUPLICATE.getCode(), ResMessage.EMAIL_DUPLICATE.getMessage());
		}

		// 2. 檢查問卷狀態是否能填寫：1.已發布 2.當前日大於等於開始日 3.當前日小於等於結束日，才能填寫
		// 排除不能填寫的問卷狀態
		if (!quizDao.existsByIdInAndPublishedTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(//
				List.of(quizId), LocalDate.now(), LocalDate.now())) {
			return new BasicRes(ResMessage.CANNOT_FILLIN_QUIZ.getCode(), ResMessage.CANNOT_FILLIN_QUIZ.getMessage());
		}

		// 3. 檢查答案
		List<Ques> quesList = quesDao.findByQuizId(quizId);
		// 檢查答案和問題的筆數是否相同(防止非正常狀況遺失feedbackList部分內容)
		if (feedbackList.size() != quesList.size()) {
			return new BasicRes(ResMessage.FILLIN_INCOMPLETE.getCode(), ResMessage.FILLIN_INCOMPLETE.getMessage());
		}

		// 先蒐集必填的 quId 和單選的 quId 以及題目的編號
		Set<Integer> necessaryQuIds = new HashSet<>();
		Set<Integer> singleQuIds = new HashSet<>();
		Set<Integer> quIds = new HashSet<>();
		for (Ques quesItem : quesList) {
			quIds.add(quesItem.getId());

			if (quesItem.isNecessary()) {
				necessaryQuIds.add(quesItem.getId());
			}

			if (quesItem.getType().equalsIgnoreCase(SelectType.SINGLE.getType())) {
				singleQuIds.add(quesItem.getId());
			}
		}

		// quIdAnsMap給下一個迴圈用
		Map<Integer, List<String>> quIdAnsMap = new HashMap<>();
		for (Feedback item : feedbackList) {
			int quId = item.getQuId();
			// 檢查題目的題號是否與答案的題號相符
			if (!quIds.contains(quId)) { // 題目題號不包含答案的題號時，回傳錯誤訊息
				return new BasicRes(ResMessage.QUID_MISMATCH.getCode(), ResMessage.QUID_MISMATCH.getMessage());
			}

			// 3.1 檢查必填要有答案
			// 必填但沒答案
			if (necessaryQuIds.contains(quId) && !(StringUtils.hasText(item.getAns()))) {
				return new BasicRes(ResMessage.FILLIN_IS_NECESSAARY.getCode(),
						ResMessage.FILLIN_IS_NECESSAARY.getMessage());
			}

			// 3.2 檢查單選時，不能有多個答案
			// 答案是字串用分號接 -> 用分號切成陣列 -> 算陣列 size 大於 1 代表超過 1 個答案就回傳錯誤訊息
			List<String> ansList = List.of(item.getAns().split(";"));
			if (singleQuIds.contains(quId) && ansList.size() > 1) {
				return new BasicRes(ResMessage.SINGLE_CHOICE.getCode(), ResMessage.SINGLE_CHOICE.getMessage());
			}
			// 將題號與對應的答案搜集後，後續比對答案和選項使用
			// 1對1 或 1對多 -> Map
			quIdAnsMap.put(quId, ansList);

		}
		// 3.3 檢查非簡答時，答案要跟選項一致
		for (Ques item : quesList) {
			String type = item.getType();
			if (!type.equalsIgnoreCase(SelectType.TEXT.getType())) {
				// quIdAnsMap.get(quId) 是根據 key 取得 map 中對應的 value
				List<String> ansList = quIdAnsMap.get(item.getId());
				List<String> optionList = List.of(item.getOptions().split(";"));
				// 判斷相同的題目下，答案是否與選項一致
				if (!optionList.containsAll(ansList) && !(ansList.get(0).isEmpty())) {
					return new BasicRes(ResMessage.OPTION_ANS_MISMATCH.getCode(),
							ResMessage.OPTION_ANS_MISMATCH.getMessage());
				}
			}
		}
		feedbackDao.saveAll(feedbackList);
		return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
	}

	@Override
	public StatisticsRes statistics(int quizId) {
		// 先撈取 quiz 的相關資訊
		Optional<Quiz> op = quizDao.findById(quizId);
		if (op.isEmpty()) {
			return new StatisticsRes(ResMessage.QUIZ_NOT_FOUND.getCode(), ResMessage.QUIZ_NOT_FOUND.getMessage());
		}
		Quiz quiz = op.get();
		String quizName = quiz.getName();
		// 先從 ques 撈取非簡答題的題目(簡答題不列入統計)
		List<Ques> quesList = quesDao.findByQuizIdAndTypeNot(quizId, SelectType.TEXT.getType());
		// 搜集單選或多選題的 Id，後續用來撈取 feedback 資料用
		List<Integer> quIdList = new ArrayList<>();
		// 先搜集問題編號和選項，之後可確定feedback中的答案一定會與題目選項一致，避免feedback中的答案非原來的選項
		// 題目編號 選項 次數 -> 將每個選項次數預設為0
		Map<Integer, Map<String, Integer>> quIdOptionCountMap = new HashMap<>();
		List<StatisticsVo> statisticsList = new ArrayList<>();
		for (Ques item : quesList) {
			quIdList.add(item.getId());
			Map<String, Integer> optionCountMap = new HashMap<>();
			List<String> optionList = List.of(item.getOptions().split(";"));
			for (String option : optionList) {
				optionCountMap.put(option, 0);
			}
			quIdOptionCountMap.put(item.getId(), optionCountMap);
			StatisticsVo vo = new StatisticsVo();
			vo.setQuId(item.getId());
			vo.setQu(item.getQu());
			statisticsList.add(vo);
		}
		// =====================================
		// 上面的 for 迴圈執行後類似以下概念
		// 題目編號 選項 次數
		// Map<Integer, Map<String, Integer>> quIdOptionCountMap = new HashMap<>();
		// 1 蛋餅 0
		// 1 三明治 0
		// 1 飯糰 0
		// 1 漢堡 0
		// 1 奶茶 0
		// 以上相同 key 的資料，只會有一筆就是題目編號1，但對應到的value 中的 map 會有 4 個資料
		// 可寫 test 資料得知資料格式
		// 2 奶茶 0
		// 以此類推
		// ======================================
		List<Feedback> feedbackList = feedbackDao.findByQuizIdAndQuIdIn(quizId, quIdList);
		// ====================================
		for (Feedback item : feedbackList) {
			// 根據題目編號 把選項次數的 map 從 quIdOptionCountMap 中取出
			Map<String, Integer> optionCountMap = quIdOptionCountMap.get(item.getQuId());
			if (optionCountMap != null) {
				// 將 feedback 中的 ans 字串轉成 List
				List<String> ansList = List.of(item.getAns().split(";"));
				for (String ans : ansList) {
					if (optionCountMap.containsKey(ans)) {
						// 根據選項取出對應的次數
						int count = optionCountMap.get(ans);
						// 將原本的次數+1
						count++;
						// 把相同的 選項:次數 放回到 map
						optionCountMap.put(ans, count);
					}
				}
				// 更新 quIdOptionCountMap 中的資料
				quIdOptionCountMap.put(item.getQuId(), optionCountMap);
			}
		}
		// ======================================
		/*
		 * 方法2 for(Feedback item : feedbackList) { StatisticsVo vo = new StatisticsVo();
		 * Map<String, Integer> optionCountMap = new HashMap<>(); List<String> ansList =
		 * List.of(item.getAns().split(";")); for(String ans : ansList) { // ans 出現大於1次
		 * // 判斷 map 中是否已有選項(key) if(optionCountMap.containsKey(ans)) { // 選項已存在 map
		 * 中，將其對應的次數取出(用 key 取出 value ) int count = optionCountMap.get(ans); // 將原本的次數+1
		 * count++; // 把相同的 選項:次數 放回到 map optionCountMap.put(ans, count); // 跳過當次迴圈
		 * continue; } // ans 出現1次 optionCountMap.put(ans, 1); } }
		 */
		for (StatisticsVo item : statisticsList) {
			int quId = item.getQuId();
			Map<String, Integer> optionCountMap = quIdOptionCountMap.get(quId);
			item.setOptionCountMap(optionCountMap);
			// 上面3行程式碼可用下面一行表示
			// item.setOptionCountMap(quIdOptionCountMap.get(item.getQuId()));
		}

		return new StatisticsRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(), //
				quizName, statisticsList);
	}

	@Override
	public FeedbackRes feedback(int quizId) {
		// 寫法1
		// List<Feedback> feedback = new ArrayList<>(); 用匿名寫在new FeedbackRes最後一個參數
		if (quizId <= 0) {
			return new FeedbackRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(), //
					new ArrayList<>());
		}
		List<Feedback> res = feedbackDao.findByQuizId(quizId);
		return new FeedbackRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(), //
				res);

		/*
		 * 寫法2: if else if(quizId <= 0) { return new
		 * FeedbackRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(), //
		 * new ArrayList<>()); }else { return new
		 * FeedbackRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(), //
		 * feedbackDao.findByQuizId(quizId)); }
		 */

		/*
		 * 寫法3: 三元式 return quizId <= 0 ? new FeedbackRes(ResMessage.SUCCESS.getCode(),
		 * ResMessage.SUCCESS.getMessage(), // new ArrayList<>()):new
		 * FeedbackRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(), //
		 * feedbackDao.findByQuizId(quizId));
		 */
	}

}
