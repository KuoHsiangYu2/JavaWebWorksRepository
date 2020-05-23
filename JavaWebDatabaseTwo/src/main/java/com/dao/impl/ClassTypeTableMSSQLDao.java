package com.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.model.ClassTypeTable;

public class ClassTypeTableMSSQLDao {
	private DataSource dataSource = null;

	public ClassTypeTableMSSQLDao() {
		super();
		try {
			Context context = new InitialContext();
			dataSource = (DataSource) context.lookup("java:comp/env/jdbc/SavePictureDB1");
		} catch (NamingException e) {
			System.out.println("DataSource 連線池 建立失敗。");
			e.printStackTrace();
		}
	}

	public List<ClassTypeTable> getClassTypeList() {
		List<ClassTypeTable> classTypeList = new ArrayList<ClassTypeTable>();

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			connection = dataSource.getConnection();
			connection.setAutoCommit(false);
			// 取得當前這個作業系統的換行符號
			// Windows作業系統是 \r \n == CR LF == 13 10
			String newLine = System.getProperty("line.separator");

			StringBuffer sb = new StringBuffer();
			sb.append("SELECT id, typeName" + newLine);
			sb.append("FROM SavePictureDB1..ClassTypeTable" + newLine);// 指定要撈資料的表格名稱
			sb.append("ORDER BY id ASC" + newLine);// 由小排列到大
			String selectStatementSQL = sb.toString();
			preparedStatement = connection.prepareStatement(selectStatementSQL);
			resultSet = preparedStatement.executeQuery();
			connection.commit();

			ClassTypeTable classTypeTable = null;
			while (true == resultSet.next()) {
				classTypeTable = new ClassTypeTable();
				classTypeTable.setId(resultSet.getInt("id"));
				classTypeTable.setTypeName(resultSet.getString("typeName"));
				classTypeList.add(classTypeTable);
				classTypeTable = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new RuntimeException(e);
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
					connection.setAutoCommit(true);
					connection.close();
					connection = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} // end of try-catch-finally

		return classTypeList;
	}

	public List<String> getClassTypeStringList() {
		List<String> classTypeStringList = new ArrayList<String>();

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			connection = dataSource.getConnection();
			connection.setAutoCommit(false);
			// 取得當前這個作業系統的換行符號
			// Windows作業系統是 \r \n == CR LF == 13 10
			String newLine = System.getProperty("line.separator");

			StringBuffer sb = new StringBuffer();
			sb.append("SELECT typeName" + newLine);
			sb.append("FROM SavePictureDB1..ClassTypeTable" + newLine);// 指定要撈資料的表格名稱
			sb.append("ORDER BY id ASC" + newLine);// 由小排列到大
			String selectStatementSQL = sb.toString();
			preparedStatement = connection.prepareStatement(selectStatementSQL);
			resultSet = preparedStatement.executeQuery();
			connection.commit();

			while (true == resultSet.next()) {
				classTypeStringList.add(resultSet.getString("typeName"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new RuntimeException(e);
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
					connection.setAutoCommit(true);
					connection.close();
					connection = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} // end of try-catch-finally

		return classTypeStringList;
	}

	public int deleteAll() {
		int exeNum = 0;

		Connection connection = null;
		PreparedStatement preparedStatement = null;

		String newLine = System.getProperty("line.separator");
		StringBuffer sb = new StringBuffer();
		sb.append("DELETE FROM SavePictureDB1..ClassTypeTable" + newLine);

		String deleteStatementSQL = sb.toString();

		try {
			connection = dataSource.getConnection();
			connection.setAutoCommit(false);
			preparedStatement = connection.prepareStatement(deleteStatementSQL);
			exeNum = preparedStatement.executeUpdate();
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new RuntimeException(e);
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
					connection.setAutoCommit(true);
					connection.close();
					connection = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} // end of try-catch-finally

		return exeNum;
	}

	public int insertAll(List<String> classTypeList) {
		int exeNum = 0;
		if (classTypeList == null) {
			return exeNum;
		}
		if (classTypeList.size() == 0) {
			return exeNum;
		}

		// 使用批次處理一口氣寫入大量資料
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		try {
			connection = dataSource.getConnection();
			connection.setAutoCommit(false);
			String insertStatement = "INSERT INTO ClassTypeTable(typeName) VALUES(?)";
			preparedStatement = connection.prepareStatement(insertStatement);
			int length = classTypeList.size();
			for (int i = 0; i < length; i++) {
				exeNum++;
				preparedStatement.setString(1, classTypeList.get(i));
				preparedStatement.addBatch();
			}
			preparedStatement.executeBatch();
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new RuntimeException(e);
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
					connection.setAutoCommit(true);
					connection.close();
					connection = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return exeNum;
	}
}
