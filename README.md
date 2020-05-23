# JavaWebWorksRepository  

## demo Java Web 作品  

必須在C槽建立imageData資料夾  
路徑為 C:\imageData\  

打開 Tomcat Server 裡面的 server.xml，  
找到 <Host>標籤 ，在 <Host>標籤 裡面加上 <Context docBase="C:/imageData/" path="/imageData" reloadable="true"/>  
去設定 "C:\imageData\"實體路徑 對應的 URL  
