# 套件

1. element-plus
2. @element-plus/icons-vue
3. axois
4. mock.js 攔截 ajax 製造假數據(新增 api 目錄)
5. echarts

# 路由

是指路徑對應的元件/組件
父 redirect：子路徑
父 children：[ 子路徑 ]

# 語法

:is = "變數名", componet :is="icons"?
:width 屬性都能用 v-bind 變動態
:style
/deep/ css 的深度作用域
newURL()

Promise.reject()

route.name
router.push(item.path)

# Bugfix-1
## 狀況/情境

需要根據不同導航來源 (`authuser` 或 `unauth`) 進入 `reply` 頁面時顯示不同的內容：

- **`authuser` 來源**：顯示完整 `reply` 頁面，包括導航區塊。
- **`unauth` 來源**：隱藏導航區塊，只顯示部分內容。

## 解法

1. **導航時傳遞 query 參數**：
   - 在 `RouterLink` 中添加 query 參數標識來源。
     ```vue
     <RouterLink :to="{ path: '/reply/' + item.id, query: { from: 'authuser' } }">進入 Reply</RouterLink>
     ```

2. **在 `Reply.vue` 檢查 query 決定顯示內容**：
   - 使用 `checkPermissions()` 方法根據 `this.$route.query.from` 判斷是否顯示導航區塊。
     ```javascript
     mounted() {
       this.checkPermissions();
     },
     methods: {
       checkPermissions() {
         this.canViewFeedback = this.$route.query.from === "authuser";
       },
     }
     ```

3. **根據狀態控制導航區塊顯示**：
   - 使用 `v-if="canViewFeedback"` 控制 `<nav class="reply-nav">` 是否顯示。

## 知識點（query）

1. **用途**：傳遞附加信息如狀態或來源，不影響路徑結構。
2. **使用方法**：通過 `RouterLink` 的 `query` 屬性傳遞，在目標組件中用 `this.$route.query` 獲取。
3. **應用**：用於根據來源動態調整頁面顯示邏輯。


