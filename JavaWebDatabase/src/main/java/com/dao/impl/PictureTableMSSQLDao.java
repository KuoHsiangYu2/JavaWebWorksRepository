package com.dao.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import com.dao.IPictureTableDao;
import com.model.PictureTable;

public class PictureTableMSSQLDao implements IPictureTableDao {
	// DAO類別，所有與資料庫連線相關的程式。

	private DataSource dataSource = null;
	private int pageNo = 1; // 存放目前顯示頁面的編號
	private int recordsPerPage = 5; // 預設值：每頁5筆
	private int totalPages = 0;// 總頁數

	@Override
	public int getPageNo() {
		// 取得現在第幾頁
		return pageNo;
	}

	@Override
	public void setPageNo(int pageNo) {
		// 設定第幾頁
		this.pageNo = pageNo;
	}

	@Override
	public int getTotalPages() {
		// 取得總頁數。

		// getCount() 取得資料總共有幾筆
		// totalPages 計算總共有幾頁要顯示
		// Math.ceil [4.1 變成 5]、[4.9 變成 5]、[4.0 變成 4]
		// celi(num) 無條件進位 (大於 num 的最小整數)
		totalPages = (int) (Math.ceil((double) getCount() / (double) recordsPerPage));
		return totalPages;
	}

	public PictureTableMSSQLDao() {
		super();
		try {
			Context context = new InitialContext();
			dataSource = (DataSource) context.lookup("java:comp/env/jdbc/SavePictureDB1");
		} catch (NamingException e) {
			System.out.println("DataSource 連線池 建立失敗。");
			e.printStackTrace();
		}
	}// end of PictureTableMSSQLDao() constructor

	@Override
	public int savePicture(PictureTable pictureTable) {
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

			String insertStatementSQL = "INSERT INTO SavePictureDB1..PictureTable(title, pictureName, file2) VALUES(?, ?, ?)";
			preparedStatement = connection.prepareStatement(insertStatementSQL);
			preparedStatement.setString(1, pictureTable.getTitle());
			preparedStatement.setString(2, pictureTable.getPictureName());
			preparedStatement.setBinaryStream(3, pictureTable.getFile2().getBinaryStream());
			exeNum = preparedStatement.executeUpdate();
			connection.commit();
			// System.out.println("exeNum = " + exeNum);
			// System.out.println("insert picture data successful");
		} catch (SQLException e) {
			e.printStackTrace();
			// System.out.println("Transaction is being rolled back");
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
	}// end of savePicture() method

	@Override
	public List<PictureTable> getPagePictureNoBlob(int pageNo) {
		// 藉由輸入頁碼編號來指定回傳一部份的資料。

		if (pageNo < 1) {
			// 不合法的頁數編號就停止程式。
			return new ArrayList<PictureTable>();
		}
		if (pageNo > getTotalPages()) {
			// 不合法的頁數編號就停止程式。
			return new ArrayList<PictureTable>();
		}

		// 重新更新頁數編號。
		setPageNo(pageNo);

		List<PictureTable> pictureTableList = new ArrayList<PictureTable>();

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
			sb.append("SELECT id, title" + newLine);
			sb.append("FROM SavePictureDB1..PictureTable" + newLine);// 指定要撈資料的表格名稱
			sb.append("ORDER BY id" + newLine);// 用 id 來排序
			sb.append("OFFSET ? ROW" + newLine);// 省略幾?筆資料
			sb.append("FETCH NEXT ? ROWS ONLY" + newLine);// 往後多加?筆資料
			String selectStatementSQL = sb.toString();
			preparedStatement = connection.prepareStatement(selectStatementSQL);

			// startRecordNo 起始略過的資料筆數
			// pageNo 目前的頁數
			// recordsPerPage 每頁有幾筆資料
			int startRecordNo = (pageNo - 1) * recordsPerPage;

			// String result1 = "int startRecordNo = (pageNo - 1) * recordsPerPage;";
			// System.out.println(result1);

			// String result2 = String.format("startRecordNo = %d, pageNo = %d,
			// recordsPerPage = %d", startRecordNo, pageNo, recordsPerPage);

			// System.out.println(result2);
			// System.out.println("startRecordNo 起始略過的資料筆數");
			// System.out.println("pageNo 目前的頁數");
			// System.out.println("recordsPerPage 每頁有幾筆資料");

			preparedStatement.setInt(1, startRecordNo);
			preparedStatement.setInt(2, recordsPerPage);

			resultSet = preparedStatement.executeQuery();
			connection.commit();

			PictureTable pictureTable = null;
			// 把圖片的清單依依加進去，包含 Primary Key主鍵[id]，以及圖片標題[title]
			while (true == resultSet.next()) {
				pictureTable = new PictureTable();
				pictureTable.setId(resultSet.getInt("id"));
				pictureTable.setTitle(resultSet.getString("title"));
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

	@Override
	public List<PictureTable> getAllPictureNoBlob() {
		// 取得所有的資料，不包含圖片二進位資料。
		List<PictureTable> pictureTableList = new ArrayList<PictureTable>();

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = dataSource.getConnection();
			connection.setAutoCommit(false);
			String selectStatementSQL = "SELECT id, title FROM SavePictureDB1..PictureTable";
			preparedStatement = connection.prepareStatement(selectStatementSQL);
			resultSet = preparedStatement.executeQuery();
			connection.commit();

			PictureTable pictureTable = null;
			// 把圖片的清單依依加進去，包含 Primary Key主鍵[id]，以及圖片標題[title]
			while (true == resultSet.next()) {
				pictureTable = new PictureTable();
				pictureTable.setId(resultSet.getInt("id"));
				pictureTable.setTitle(resultSet.getString("title"));
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
	}// end of getAllPictureNoBlob() method

	@Override
	public PictureTable getPictureWithBlobById(int index) {
		// 取得圖片名稱與圖片二進二檔案資料
		PictureTable pictureTable = null;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		InputStream inputStream = null;
		ByteArrayOutputStream byteArrayOutputStream = null;
		try {
			connection = dataSource.getConnection();
			connection.setAutoCommit(false);
			String selectStatementSQL = "SELECT pictureName, file2 FROM SavePictureDB1..PictureTable WHERE id = ?";
			preparedStatement = connection.prepareStatement(selectStatementSQL);
			preparedStatement.setInt(1, index);
			resultSet = preparedStatement.executeQuery();
			connection.commit();
			while (true == resultSet.next()) {
				pictureTable = new PictureTable();
				pictureTable.setPictureName(resultSet.getString("pictureName"));

				inputStream = resultSet.getBinaryStream("file2");
				byteArrayOutputStream = new ByteArrayOutputStream();
				byte[] byteBuffer = new byte[8192];
				int length = 0;
				while ((length = inputStream.read(byteBuffer)) != -1) {
					byteArrayOutputStream.write(byteBuffer, 0, length);
				}
				SerialBlob serialBlob = new SerialBlob(byteArrayOutputStream.toByteArray());
				pictureTable.setFile2(serialBlob);
			} // end of while-loop
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new RuntimeException(e);
		} catch (IOException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new RuntimeException(e);
		} finally {
			if (byteArrayOutputStream != null) {
				try {
					byteArrayOutputStream.close();
					byteArrayOutputStream = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (inputStream != null) {
				try {
					inputStream.close();
					inputStream = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
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
	}// end of getPictureWithBlobById() method

	@Override
	public int getCount() {
		// 取得資料庫裡面圖片資料的筆數
		int result = 0;

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = dataSource.getConnection();
			connection.setAutoCommit(false);
			String getCountSQL = "SELECT COUNT(id) AS 'number' FROM SavePictureDB1..PictureTable";
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
	}// end of getCount() method

	@Override
	public PictureTable getFullPictureDataById(int index) {
		// 取得單一筆資料的所有欄位。
		PictureTable pictureTable = null;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		InputStream inputStream = null;
		ByteArrayOutputStream byteArrayOutputStream = null;

		try {
			connection = dataSource.getConnection();
			connection.setAutoCommit(false);
			String selectStatementSQL = "SELECT id, title, pictureName, file2 FROM SavePictureDB1..PictureTable WHERE id = ?";
			preparedStatement = connection.prepareStatement(selectStatementSQL);
			preparedStatement.setInt(1, index);
			resultSet = preparedStatement.executeQuery();
			connection.commit();
			while (true == resultSet.next()) {
				pictureTable = new PictureTable();
				pictureTable.setId(Integer.valueOf(resultSet.getInt("id")));
				pictureTable.setTitle(resultSet.getString("title"));
				pictureTable.setPictureName(resultSet.getString("pictureName"));

				inputStream = resultSet.getBinaryStream("file2");
				byteArrayOutputStream = new ByteArrayOutputStream();
				byte[] byteBuffer = new byte[8192];
				int length = 0;
				while ((length = inputStream.read(byteBuffer)) != -1) {
					byteArrayOutputStream.write(byteBuffer, 0, length);
				}
				SerialBlob serialBlob = new SerialBlob(byteArrayOutputStream.toByteArray());
				pictureTable.setFile2(serialBlob);
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
		} catch (IOException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new RuntimeException(e);
		} finally {
			if (byteArrayOutputStream != null) {
				try {
					byteArrayOutputStream.close();
					byteArrayOutputStream = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (inputStream != null) {
				try {
					inputStream.close();
					inputStream = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
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
	}// end of getFullPictureDataById() method

	@Override
	public int saveAndUpdatePictureById(int index, PictureTable newObj, boolean needSaveFile) {
		// 修改所有欄位值。
		int exeNum = 0;

		if (false == needSaveFile) {
			exeNum = saveAndUpdatePictureWithoutBlobFieldById(index, newObj);
			return exeNum;
		}

		// System.out.println("修改所有欄位值。");

		if (newObj.getId() == null) {
			return exeNum;
		}
		if (newObj.getId().intValue() != index) {
			// 要重新修改的物件id跟傳入的id參數不同，
			// 回傳0終止程式。
			return exeNum;
		}

		PictureTable oldObj = getFullPictureDataById(index);

		// 如果使用者修改時，沒有加的資料，就由舊資料補充上去。
		if (newObj.getTitle() == null || newObj.getTitle().trim().length() == 0) {
			newObj.setTitle(oldObj.getTitle());
		}
		if (newObj.getPictureName() == null || newObj.getPictureName().trim().length() == 0) {
			newObj.setPictureName(oldObj.getPictureName());
		}
		try {
			if (newObj.getFile2() == null || newObj.getFile2().length() == 0) {
				newObj.setFile2(oldObj.getFile2());
			}
		} catch (SQLException e) {
			e.printStackTrace();
			newObj.setFile2(oldObj.getFile2());
		}

		Connection connection = null;
		PreparedStatement preparedStatement = null;

		String newLine = System.getProperty("line.separator");
		StringBuffer sb = new StringBuffer();
		sb.append("UPDATE SavePictureDB1..PictureTable" + newLine);
		sb.append("SET title = ?, pictureName = ?, file2 = ?" + newLine);
		sb.append("WHERE id = ?" + newLine);
		String updateStatementSQL = sb.toString();

		try {
			connection = dataSource.getConnection();
			connection.setAutoCommit(false);
			preparedStatement = connection.prepareStatement(updateStatementSQL);
			preparedStatement.setString(1, newObj.getTitle());
			preparedStatement.setString(2, newObj.getPictureName());
			preparedStatement.setBinaryStream(3, newObj.getFile2().getBinaryStream());
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
	}// end of saveAndUpdatePictureById() method

	private int saveAndUpdatePictureWithoutBlobFieldById(int index, PictureTable newObj) {
		// 只修改文字欄位資料，圖片欄位不予更動。
		// System.out.println("只修改文字欄位資料，圖片欄位不予更動。");
		int exeNum = 0;

		if (newObj.getId() == null) {
			return exeNum;
		}
		if (newObj.getId().intValue() != index) {
			// 要重新修改的物件id跟傳入的id參數不同，
			// 回傳0終止程式。
			return exeNum;
		}

		PictureTable oldObj = getFullPictureDataById(index);

		// 如果使用者修改時，沒有加的資料，就由舊資料補充上去。
		if (newObj.getTitle() == null || newObj.getTitle().trim().length() == 0) {
			newObj.setTitle(oldObj.getTitle());
		}
		if (newObj.getPictureName() == null || newObj.getPictureName().trim().length() == 0) {
			newObj.setPictureName(oldObj.getPictureName());
		}

		Connection connection = null;
		PreparedStatement preparedStatement = null;

		String newLine = System.getProperty("line.separator");
		StringBuffer sb = new StringBuffer();
		sb.append("UPDATE SavePictureDB1..PictureTable" + newLine);
		sb.append("SET title = ?, pictureName = ?" + newLine);
		sb.append("WHERE id = ?" + newLine);
		String updateStatementSQL = sb.toString();

		try {
			connection = dataSource.getConnection();
			connection.setAutoCommit(false);
			preparedStatement = connection.prepareStatement(updateStatementSQL);
			preparedStatement.setString(1, newObj.getTitle());
			preparedStatement.setString(2, newObj.getPictureName());
			preparedStatement.setInt(3, newObj.getId());
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
	}// end of saveAndUpdatePictureWithoutBlobFieldById() method

	@Override
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
		sb.append("DELETE FROM SavePictureDB1..PictureTable" + newLine);
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
	}// end of deletePictureById() method
}// end of PictureTableMSSQLDao class
