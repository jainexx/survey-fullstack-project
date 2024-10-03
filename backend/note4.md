# Session
1. Cookie: 瀏覽器暫存資料資料的地方
2. Session: 存在伺服器端(Server)的資料
機器：cache快取, DB, 記憶體

[ 運作機制 ]
client         發送Req->         Server
           <- Session + 唯一id
1. Session 在不使用的情況下保留30分鐘

[ 用法 ]
1. Controller(UserController) 的 req 加上 HttpSession session
2. 

