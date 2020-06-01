# JavaWebWorksRepository  

## demo Java Web 作品  

開發環境  
電腦：Windows 10 家用版  
IDE：Eclipse IDE for Enterprise Java Developers 2020-03 (4.15.0)  
Java jdk version：1.8.0_162  
資料庫：Microsoft SQL Server 2019  

本作品應用到的技術有：Java、JSP、Servlet、JDBC、AJAX

```no-highlight
預前準備  

1.  
打開 Microsoft SQL Server Management Studio 18，  
接著創建 SavePictureDB1 這個 database，  
切換到 SavePictureDB1 database。  

2.  
打開 Eclipse project ，  
打開 /JavaWebDatabaseTwo/src/main/java/__00/intial/InitialTable.java檔  
滑鼠右鍵「Run As -> 2 Java Application」，執行Java程式建立 PictureTableTwo、ClassTypeTable 2張table  

3.  
在C槽建立imageData資料夾  
路徑為 C:\imageData\  

4.  
打開 Tomcat Server 裡面的 server.xml，  
找到 <Host>標籤 ，在 <Host>標籤 裡面加上 <Context docBase="C:/imageData/" path="/imageData" reloadable="true"/>  
去設定 "C:\imageData\"硬碟實體路徑 對應的 URL  
```