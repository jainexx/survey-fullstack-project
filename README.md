# 問卷管理系統

![專案封面](./public/動問%20cover.png)

## 專案簡介
**問卷管理系統**是一個基於前後端分離架構的完整網頁應用程式，旨在提升問卷的製作、管理與回饋分析流程。系統後端採用 **Java Spring Boot** 架構，前端則使用 **Vue.js** 開發，並結合 Vue Router、Pinia、Axios 與 ECharts 等技術，提供直觀且功能豐富的問卷管理體驗。

- **後端技術**：
  - Java Spring Boot：處理問卷 CRUD 操作（增、刪、查、改）及回饋數據統計分析。
  - RESTful API：提供穩定的 API 端點，供前端進行資料存取與互動。

- **前端技術**：
  - Vue.js：建立前端互動介面，並搭配 Vue Router 進行頁面管理與路由設計。
  - Pinia：作為狀態管理工具，有效管理應用內狀態。
  - Axios：進行 HTTP 請求以與後端 API 進行通訊。
  - ECharts：將數據視覺化呈現，輕鬆分析問卷回饋。

## 安裝依賴
1. clone 專案到本地：
   ```bash
   git clone https://github.com/jainexx/survey-fullstack-project.git
   ```

2.	切換到 frontend 資料夾，安裝所有 npm 依賴：
  ```bash
    cd frontend
    npm install
  ```

## 專案結構
此專案採用 Monorepo 結構，前後端程式碼都包含於同一個 GitHub 儲存庫中，方便管理與維護。
- frontend 資料夾放置 vue.js 程式碼
- backend 資料夾放置 java 程式碼

## 功能模組
### 後端管理功能
- **問卷管理**：支援問卷的增刪查改操作，提供靈活的問卷製作與管理介面。
- **回饋統計**：統計問卷回覆數據，以圖表形式視覺化呈現，方便管理者進行分析與決策。

### 前端使用者功能
- **問卷填寫**：用戶可以透過前端介面瀏覽、選擇並填寫問卷，提交後系統會即時顯示統計結果。
- **數據視覺化**：使用 ECharts 呈現問卷各題統計數據，協助用戶了解其他受訪者的回應趨勢。

## 技術棧
- **後端**：
  - Java Spring Boot
  - RESTful API
  - MySQL (資料庫)

- **前端**：
  - Vue.js
  - Vue Router
  - Pinia
  - Axios
  - ECharts

## 頁面展示
以下為系統主要頁面與功能展示：
- **問卷管理頁面**：可查看、編輯與刪除現有問卷，並能快速建立新問卷，支援多種題型的設置（單選、多選、文字填答等）。
- **統計分析頁面**：提供各題目詳細的統計圖表，幫助管理者理解用戶回應。
- **問卷填寫頁面**：使用者可以選擇要填寫的問卷並提交結果。

## 未來改進方向
- 增加角色分級與權限控管，提升系統安全性。
- 增加問卷結果匯出功能（CSV 或 PDF 格式）。

## 聯絡方式
若對專案有任何問題或建議，歡迎聯絡我：
- Email: jainesbox@email.com
- GitHub: [jainexx](https://github.com/jainexx)

---

© 2024 問卷管理系統. All Rights Reserved.