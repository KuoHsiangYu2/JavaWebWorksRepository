package __00.intial;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.util.DatabaseData;

public class InitialTable {

	public static void main(String[] args) {
		// 在資料庫建立一個table

		// CREATE TABLE (Transact-SQL) IDENTITY (屬性)
		// https://docs.microsoft.com/zh-tw/sql/t-sql/statements/create-table-transact-sql-identity-property?view=sql-server-ver15

		String connectionURL = "jdbc:sqlserver://localhost:1433;databaseName=SavePictureDB1";
		String username = DatabaseData.getUsername();
		String password = DatabaseData.getPassword();

		Connection connection = null;
		PreparedStatement preparedStatement = null;

		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			connection = DriverManager.getConnection(connectionURL, username, password);
			// 取得當前這個作業系統的換行符號
			// Windows作業系統是 \r \n == CR LF == 13 10
			String newLine = System.getProperty("line.separator");
			StringBuffer sb = new StringBuffer();
			sb.append("USE SavePictureDB1" + newLine);
			sb.append("CREATE TABLE SavePictureDB1..PictureTableTwo" + newLine);
			sb.append("(" + newLine);
			sb.append("	id INT IDENTITY(1, 1) PRIMARY KEY," + newLine);
			sb.append("	title NVARCHAR(200)," + newLine);
			sb.append("	pictureName NVARCHAR(200)," + newLine);
			sb.append("	typeName NVARCHAR(50)," + newLine);
			sb.append(");" + newLine);
			sb.append(newLine);
			sb.append("CREATE TABLE SavePictureDB1..ClassTypeTable" + newLine);
			sb.append("(" + newLine);
			sb.append("	id INT IDENTITY(1, 1) PRIMARY KEY," + newLine);
			sb.append("	typeName NVARCHAR(50)" + newLine);
			sb.append(");" + newLine);
			sb.append(newLine);
			sb.append("INSERT INTO ClassTypeTable(typeName) VALUES('未分類');" + newLine);
			String createTableSQL = sb.toString();
			preparedStatement = connection.prepareStatement(createTableSQL);
			int exeNum = preparedStatement.executeUpdate();
			System.out.println("exeNum = " + exeNum);
			System.out.println("create table successful");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
					preparedStatement = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connection != null) {
				try {
					connection.close();
					connection = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}// end of main method

}
