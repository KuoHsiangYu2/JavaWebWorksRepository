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
import javax.sql.rowset.serial.SerialException;

import com.model.PictureTableSearchType;
import com.model.PictureTableTwo;

public class PictureTableMSSQLDao {

	private DataSource dataSource = null;
	private int pageNo = 1; // 存放目前顯示頁面的編號
	private int recordsPerPage = 5; // 預設值：每頁5筆
	private int totalPages = 1;// 總頁數

	public PictureTableMSSQLDao() {
		super();
		try {
			Context context = new InitialContext();
			dataSource = (DataSource) context.lookup("java:comp/env/jdbc/SavePictureDB1");
		} catch (NamingException e) {
			System.out.println("DataSource 連線池 建立失敗。");
			e.printStackTrace();
		}
	}

	public int getPageNo() {
		// 取得現在第幾頁
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		// 設定第幾頁
		this.pageNo = pageNo;
	}

	public int getTotalPages() {
		// 取得全部資料總頁數。

		// getCount() 取得資料總共有幾筆
		// totalPages 計算總共有幾頁要顯示
		// Math.ceil [4.1 變成 5]、[4.9 變成 5]、[4.0 變成 4]
		// celi(num) 無條件進位 (大於 num 的最小整數)
		totalPages = (int) (Math.ceil((double) getCount() / (double) recordsPerPage));
		return totalPages;
	}

	public int savePicture(PictureTableTwo pictureTable) {
		// 新增一筆資料
		int exeNum = 0;

		if (pictureTable == null) {
			System.out.println("JavaBean 為 null，結束程式執行。");
			return exeNum;
		}

		Connection connection = null;
		PreparedStatement preparedStatement = null;

		try {
			connection = dataSource.getConnection();

			// 關閉 auto commit，開啟交易模式
			connection.setAutoCommit(false);

			String insertStatementSQL = "INSERT INTO SavePictureDB1..PictureTableTwo(title, pictureName, typeName) VALUES(?, ?, ?)";
			preparedStatement = connection.prepareStatement(insertStatementSQL);
			preparedStatement.setString(1, pictureTable.getTitle());
			preparedStatement.setString(2, pictureTable.getPictureName());
			preparedStatement.setString(3, pictureTable.getTypeName());
			exeNum = preparedStatement.executeUpdate();
			connection.commit();
			// System.out.println("exeNum = " + exeNum);
			// System.out.println("insert picture data successful");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Transaction is being rolled back");
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

	public List<PictureTableTwo> getPagePicture(int pageNo) {
		// 藉由輸入頁碼編號來指定回傳一部份的資料。

		if (pageNo < 1) {
			// 不合法的頁數編號就停止程式。
			return new ArrayList<PictureTableTwo>();
		}
		if (pageNo > getTotalPages()) {
			// 不合法的頁數編號就停止程式。
			return new ArrayList<PictureTableTwo>();
		}

		// 重新更新頁數編號。
		setPageNo(pageNo);

		List<PictureTableTwo> pictureTableList = new ArrayList<PictureTableTwo>();

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
			sb.append("SELECT id, title, pictureName, typeName" + newLine);
			sb.append("FROM SavePictureDB1..PictureTableTwo" + newLine);// 指定要撈資料的表格名稱
			sb.append("ORDER BY id ASC" + newLine);// 用 id 來排序
			sb.append("OFFSET ? ROW" + newLine);// 省略幾?筆資料
			sb.append("FETCH NEXT ? ROWS ONLY" + newLine);// 往後多加?筆資料
			String selectStatementSQL = sb.toString();
			preparedStatement = connection.prepareStatement(selectStatementSQL);

			// startRecordNo 起始略過的資料筆數
			// pageNo 目前的頁數
			// recordsPerPage 每頁有幾筆資料
			int startRecordNo = (pageNo - 1) * recordsPerPage;

			preparedStatement.setInt(1, startRecordNo);
			preparedStatement.setInt(2, recordsPerPage);

			resultSet = preparedStatement.executeQuery();
			connection.commit();

			PictureTableTwo pictureTable = null;
			// 把圖片的清單依依加進去，包含 Primary Key主鍵[id]，以及圖片標題[title]
			while (true == resultSet.next()) {
				pictureTable = new PictureTableTwo();
				pictureTable.setId(resultSet.getInt("id"));
				pictureTable.setTitle(resultSet.getString("title"));
				pictureTable.setPictureName(resultSet.getString("pictureName"));
				pictureTable.setTypeName(resultSet.getString("typeName"));
				pictureTableList.add(pictureTable);
				pictureTable = null;
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

		return pictureTableList;
	}

	public List<PictureTableTwo> getAllPicture() {
		// 取得所有的資料。
		List<PictureTableTwo> pictureTableList = new ArrayList<PictureTableTwo>();

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = dataSource.getConnection();
			connection.setAutoCommit(false);
			String selectStatementSQL = "SELECT id, title, pictureName, typeName FROM SavePictureDB1..PictureTableTwo";
			preparedStatement = connection.prepareStatement(selectStatementSQL);
			resultSet = preparedStatement.executeQuery();
			connection.commit();

			PictureTableTwo pictureTable = null;
			// 把圖片的清單依依加進去，包含 Primary Key主鍵[id]，以及圖片標題[title]
			while (true == resultSet.next()) {
				pictureTable = new PictureTableTwo();
				pictureTable.setId(resultSet.getInt("id"));
				pictureTable.setTitle(resultSet.getString("title"));
				pictureTable.setPictureName(resultSet.getString("pictureName"));
				pictureTable.setTypeName(resultSet.getString("typeName"));
				pictureTableList.add(pictureTable);
				pictureTable = null;
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

		return pictureTableList;
	}

	public int getCount() {
		// 取得資料庫裡面圖片資料的筆數
		int result = 0;

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = dataSource.getConnection();
			connection.setAutoCommit(false);
			String getCountSQL = "SELECT COUNT(id) AS 'number' FROM SavePictureDB1..PictureTableTwo";
			preparedStatement = connection.prepareStatement(getCountSQL);
			resultSet = preparedStatement.executeQuery();
			connection.commit();
			while (true == resultSet.next()) {
				result = resultSet.getInt("number");
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

		return result;
	}

	public PictureTableTwo getFullPictureDataById(int index) {
		// 取得單一筆資料的所有欄位。
		PictureTableTwo pictureTable = null;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = dataSource.getConnection();
			connection.setAutoCommit(false);
			String selectStatementSQL = "SELECT id, title, pictureName, typeName FROM SavePictureDB1..PictureTableTwo WHERE id = ?";
			preparedStatement = connection.prepareStatement(selectStatementSQL);
			preparedStatement.setInt(1, index);
			resultSet = preparedStatement.executeQuery();
			connection.commit();
			while (true == resultSet.next()) {
				pictureTable = new PictureTableTwo();
				pictureTable.setId(resultSet.getInt("id"));
				pictureTable.setTitle(resultSet.getString("title"));
				pictureTable.setPictureName(resultSet.getString("pictureName"));
				pictureTable.setTypeName(resultSet.getString("typeName"));
			} // end of while-loop
		} catch (SerialException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new RuntimeException(e);
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

		return pictureTable;
	}

	public int saveAndUpdatePictureById(int index, PictureTableTwo newObj) {
		// 修改所有欄位值。
		int exeNum = 0;

		// System.out.println("修改所有欄位值。");

		if (newObj.getId() == null) {
			return exeNum;
		}
		if (newObj.getId().intValue() != index) {
			// 要重新修改的物件id跟傳入的id參數不同，
			// 回傳0終止程式。
			return exeNum;
		}

		PictureTableTwo oldObj = getFullPictureDataById(index);

		// 如果使用者修改時，沒有加的資料，就由舊資料補充上去。
		if (newObj.getTitle() == null || newObj.getTitle().trim().length() == 0) {
			newObj.setTitle(oldObj.getTitle());
		}
		if (newObj.getPictureName() == null || newObj.getPictureName().trim().length() == 0) {
			newObj.setPictureName(oldObj.getPictureName());
		}
		if (newObj.getTypeName() == null || newObj.getTypeName().trim().length() == 0) {
			newObj.setTypeName(oldObj.getTypeName());
		}

		Connection connection = null;
		PreparedStatement preparedStatement = null;

		String newLine = System.getProperty("line.separator");
		StringBuffer sb = new StringBuffer();
		sb.append("UPDATE SavePictureDB1..PictureTableTwo" + newLine);
		sb.append("SET title = ?, pictureName = ?, typeName = ?" + newLine);
		sb.append("WHERE id = ?" + newLine);
		String updateStatementSQL = sb.toString();

		try {
			connection = dataSource.getConnection();
			connection.setAutoCommit(false);
			preparedStatement = connection.prepareStatement(updateStatementSQL);
			preparedStatement.setString(1, newObj.getTitle());
			preparedStatement.setString(2, newObj.getPictureName());
			preparedStatement.setString(3, newObj.getTypeName());
			preparedStatement.setInt(4, newObj.getId());
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

	public int deletePictureById(int index) {
		int exeNum = 0;

		if (index < 1) {
			// 不合法的id編號，直接結束程式。
			return exeNum;
		}

		Connection connection = null;
		PreparedStatement preparedStatement = null;

		String newLine = System.getProperty("line.separator");
		StringBuffer sb = new StringBuffer();
		sb.append("DELETE FROM SavePictureDB1..PictureTableTwo" + newLine);
		sb.append("WHERE id = ?" + newLine);
		String deleteStatementSQL = sb.toString();

		try {
			connection = dataSource.getConnection();
			connection.setAutoCommit(false);
			preparedStatement = connection.prepareStatement(deleteStatementSQL);
			preparedStatement.setInt(1, index);
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

	public int resetTypeNameList(List<PictureTableTwo> updateTypeNameList) {
		int exeNum = 0;
		if (updateTypeNameList == null) {
			return exeNum;
		}
		if (updateTypeNameList.size() == 0) {
			// 如果修改清單為空，則不須耗費資源對資料庫資料進行存取。
			return exeNum;
		}

		Connection connection = null;
		PreparedStatement preparedStatement = null;

		try {
			connection = dataSource.getConnection();
			connection.setAutoCommit(false);

			String newLine = System.getProperty("line.separator");
			StringBuffer sb = new StringBuffer();
			sb.append("UPDATE SavePictureDB1..PictureTableTwo" + newLine);
			sb.append("SET typeName = '未分類'" + newLine);
			sb.append("WHERE id = ?" + newLine);
			String updateStatementSQL = sb.toString();

			preparedStatement = connection.prepareStatement(updateStatementSQL);
			int length = updateTypeNameList.size();
			for (int i = 0; i < length; i++) {
				exeNum = exeNum + 1;
				preparedStatement.setInt(1, updateTypeNameList.get(i).getId());
				preparedStatement.addBatch();// 使用批次處理一口氣寫入大量資料
			}
			preparedStatement.executeBatch();// 最後再執行指令存取資料庫的資料
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

	public List<PictureTableTwo> getPagePicture(int pageNo, String targetString, PictureTableSearchType type) {
		if (type == PictureTableSearchType.searchString) {
			// 依據使用者輸入的字來搜尋圖片
			return getPagePictureSearch(pageNo, targetString);
		} else if (type == PictureTableSearchType.typeName) {
			// 依據圖片分類來回傳值
			return getPagePictureType(pageNo, targetString);
		} else {
			// 程式正常執行時，不應該執行到這段else
			return null;
		}
	}

	private List<PictureTableTwo> getPagePictureType(int pageNo, String typeString) {
		// 依據圖片分類來回傳值
		// 依據使用者選擇的圖片分類來撈取圖片資料

		if (pageNo < 1) {
			// 不合法的頁數編號就停止程式。
			return new ArrayList<PictureTableTwo>();
		}
		if (pageNo > getTotalPagesType(typeString)) {
			// 不合法的頁數編號就停止程式。
			return new ArrayList<PictureTableTwo>();
		}

		if (typeString == null || typeString.trim().length() == 0) {
			// 沒有圖片分類字串，直接回傳空集合。
			return new ArrayList<PictureTableTwo>();
		}

		// 重新更新頁數編號。
		setPageNo(pageNo);

		List<PictureTableTwo> pictureTableList = new ArrayList<PictureTableTwo>();

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			connection = dataSource.getConnection();
			connection.setAutoCommit(false);

			String newLine = System.getProperty("line.separator");

			StringBuffer sb = new StringBuffer();
			sb.append("SELECT id, title, pictureName, typeName" + newLine);
			sb.append("FROM SavePictureDB1..PictureTableTwo" + newLine);// 指定要撈資料的表格名稱
			sb.append("WHERE typeName = ?" + newLine);
			sb.append("ORDER BY id ASC" + newLine);// 用 id 來排序，ASC：小排到大
			sb.append("OFFSET ? ROW" + newLine);// 省略幾?筆資料
			sb.append("FETCH NEXT ? ROWS ONLY" + newLine);// 往後多加?筆資料
			String selectStatementSQL = sb.toString();
			preparedStatement = connection.prepareStatement(selectStatementSQL);

			// startRecordNo 起始略過的資料筆數
			// pageNo 目前的頁數
			// recordsPerPage 每頁有幾筆資料
			int startRecordNo = (pageNo - 1) * recordsPerPage;

			preparedStatement.setString(1, typeString);
			preparedStatement.setInt(2, startRecordNo);
			preparedStatement.setInt(3, recordsPerPage);

			resultSet = preparedStatement.executeQuery();
			connection.commit();

			PictureTableTwo pictureTable = null;
			// 把圖片的清單依依加進去，包含 Primary Key主鍵[id]，以及圖片標題[title]
			while (true == resultSet.next()) {
				pictureTable = new PictureTableTwo();
				pictureTable.setId(resultSet.getInt("id"));
				pictureTable.setTitle(resultSet.getString("title"));
				pictureTable.setPictureName(resultSet.getString("pictureName"));
				pictureTable.setTypeName(resultSet.getString("typeName"));
				pictureTableList.add(pictureTable);
				pictureTable = null;
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

		return pictureTableList;
	}

	private List<PictureTableTwo> getPagePictureSearch(int pageNo, String searchString) {
		// 藉由輸入頁碼編號來指定回傳一部份的資料。
		// 依據使用者輸入的字來搜尋圖片

		if (pageNo < 1) {
			// 不合法的頁數編號就停止程式。
			return new ArrayList<PictureTableTwo>();
		}
		if (pageNo > getTotalPagesSearch(searchString)) {
			// 不合法的頁數編號就停止程式。
			return new ArrayList<PictureTableTwo>();
		}

		if (searchString == null || searchString.trim().length() == 0) {
			// 沒有輸入搜索字串，直接回傳空集合。
			return new ArrayList<PictureTableTwo>();
		}

		// 重新更新頁數編號。
		setPageNo(pageNo);

		List<PictureTableTwo> pictureTableList = new ArrayList<PictureTableTwo>();

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			connection = dataSource.getConnection();
			connection.setAutoCommit(false);

			String newLine = System.getProperty("line.separator");

			StringBuffer sb = new StringBuffer();
			sb.append("SELECT id, title, pictureName, typeName" + newLine);
			sb.append("FROM SavePictureDB1..PictureTableTwo" + newLine);// 指定要撈資料的表格名稱
			sb.append("WHERE title LIKE ?" + newLine);
			sb.append("ORDER BY id ASC" + newLine);// 用 id 來排序，ASC：小排到大
			sb.append("OFFSET ? ROW" + newLine);// 省略幾?筆資料
			sb.append("FETCH NEXT ? ROWS ONLY" + newLine);// 往後多加?筆資料
			String selectStatementSQL = sb.toString();
			preparedStatement = connection.prepareStatement(selectStatementSQL);

			// startRecordNo 起始略過的資料筆數
			// pageNo 目前的頁數
			// recordsPerPage 每頁有幾筆資料
			int startRecordNo = (pageNo - 1) * recordsPerPage;

			preparedStatement.setString(1, "%" + searchString + "%");
			preparedStatement.setInt(2, startRecordNo);
			preparedStatement.setInt(3, recordsPerPage);

			resultSet = preparedStatement.executeQuery();
			connection.commit();

			PictureTableTwo pictureTable = null;
			// 把圖片的清單依依加進去，包含 Primary Key主鍵[id]，以及圖片標題[title]
			while (true == resultSet.next()) {
				pictureTable = new PictureTableTwo();
				pictureTable.setId(resultSet.getInt("id"));
				pictureTable.setTitle(resultSet.getString("title"));
				pictureTable.setPictureName(resultSet.getString("pictureName"));
				pictureTable.setTypeName(resultSet.getString("typeName"));
				pictureTableList.add(pictureTable);
				pictureTable = null;
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

		return pictureTableList;
	}

	public int getTotalPages(String targetString, PictureTableSearchType type) {
		if (type == PictureTableSearchType.searchString) {
			// 依據使用者輸入的字來搜尋圖片
			// 回傳搜尋圖片總頁數
			return getTotalPagesSearch(targetString);
		} else if (type == PictureTableSearchType.typeName) {
			// 依據圖片分類來回傳值
			// 回傳分類圖片總頁數
			return getTotalPagesType(targetString);
		} else {
			// 程式正常執行，絕對不應該執行到這段。
			return -1;
		}
	}

	private int getTotalPagesType(String typeString) {
		// 依據圖片分類來回傳值
		// 回傳分類圖片總頁數

		int totalCount = 0;

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = dataSource.getConnection();
			connection.setAutoCommit(false);

			String newLine = System.getProperty("line.separator");
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT COUNT(id) AS 'number'" + newLine);
			sb.append("FROM SavePictureDB1..PictureTableTwo" + newLine);
			sb.append("WHERE typeName = ?" + newLine);
			String getCountSQL = sb.toString();

			preparedStatement = connection.prepareStatement(getCountSQL);
			preparedStatement.setString(1, typeString);
			resultSet = preparedStatement.executeQuery();
			connection.commit();
			while (true == resultSet.next()) {
				totalCount = resultSet.getInt("number");
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

		int totalPages = (int) (Math.ceil((double) totalCount / (double) recordsPerPage));
		return totalPages;
	}

	private int getTotalPagesSearch(String searchString) {
		// 依據使用者輸入的字來搜尋圖片
		// 回傳搜尋圖片總頁數

		int totalCount = 0;

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = dataSource.getConnection();
			connection.setAutoCommit(false);

			String newLine = System.getProperty("line.separator");
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT COUNT(id) AS 'number'" + newLine);
			sb.append("FROM SavePictureDB1..PictureTableTwo" + newLine);
			sb.append("WHERE title LIKE ?" + newLine);
			String getCountSQL = sb.toString();

			preparedStatement = connection.prepareStatement(getCountSQL);
			preparedStatement.setString(1, "%" + searchString + "%");
			resultSet = preparedStatement.executeQuery();
			connection.commit();
			while (true == resultSet.next()) {
				totalCount = resultSet.getInt("number");
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

		int totalPages = (int) (Math.ceil((double) totalCount / (double) recordsPerPage));
		return totalPages;
	}

}
