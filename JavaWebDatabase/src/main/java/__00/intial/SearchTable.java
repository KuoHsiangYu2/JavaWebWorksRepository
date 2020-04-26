package __00.intial;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

// 測試 JNDI DataSource 能否正常運作的類別。
public class SearchTable {

	public static void testJNDIProgram() {
		DataSource dataSource = null;
		try {
			Context context = new InitialContext();
			dataSource = (DataSource) context.lookup("java:comp/env/jdbc/SavePictureDB1");
		} catch (NamingException e) {
			e.printStackTrace();
		}

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = dataSource.getConnection();
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT id, title FROM SavePictureDB1..PictureTable");
			String createTableSQL = sb.toString();
			preparedStatement = connection.prepareStatement(createTableSQL);
			resultSet = preparedStatement.executeQuery();
			String output = "";
			while (resultSet.next()) {
				output = resultSet.getInt("id") + ",\t" + resultSet.getString("title");
				System.out.println(output);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
					resultSet = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
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
		}// end of try-catch-finally
	}// end of testProgram() method

}
