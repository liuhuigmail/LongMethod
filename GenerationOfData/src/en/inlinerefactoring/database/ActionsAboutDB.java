package en.inlinerefactoring.database;

import java.sql.*;
import java.util.List;

import en.actionsofproject.database.ui.ClassInfo;
import en.actionsofproject.database.ui.EPValue;
import en.actionsofproject.database.ui.MethodInfo;
import en.actionsofproject.database.ui.Relations;
import en.inlinerefactoring.database.ui.LongMethodInformations;

public class ActionsAboutDB {
	
	Connection conn;
	public ActionsAboutDB(){
//		try {
//			this.conn = getConn();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	public Connection getConn() throws Exception{
		Class.forName("com.mysql.jdbc.Driver");
		
        Connection conn = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/mydata",
                "root","123456");
        
        //Statement stmt =  conn.createStatement();
        return conn;
	}
	public int getTableMaxRow() throws Exception{
		String sql = null;
		Connection conn = getConn();	
		sql = "select max(keynum) from LongMethodInformations;";
		PreparedStatement pstmt = (PreparedStatement)conn.prepareStatement(sql);
	    ResultSet rs = pstmt.executeQuery();
	    //System.out.println("getTableMaxRow"+rs.getInt(0));
	    int maxRow = 0;
	    if(rs.next()){
	    	maxRow = rs.getInt("max(KeyNum)");	
	    }
	    pstmt.close();
		conn.close();
	    return maxRow;
	}
	public int insertLongMethodInformations(LongMethodInformations longMethodInformations) throws Exception{
		int i = 0;
		if(selectResults(longMethodInformations) == 0){
//			String sql = "insert into LongMethodInformations (keynum, MethodName, MethodOfClass, MethodParameters,MethodBody,NOAV,MLOC,DR,ODD,WMA,IsLongMethodOrNot,projectName) values(?,?,?,?,?,?,?,?,?,?,?,?);";
//			PreparedStatement pstmt;
//			pstmt = (PreparedStatement) conn.prepareStatement(sql);
//			pstmt.setInt(1, longMethodInformations.getKeynum());
//			pstmt.setString(2, longMethodInformations.getMethodName());
//			pstmt.setString(3, longMethodInformations.getMethodOfClass());
//			pstmt.setString(4, longMethodInformations.getMethodParameters());
//			pstmt.setString(5, longMethodInformations.getMethodBody());
//			pstmt.setInt(6, longMethodInformations.getNOAV());
//			pstmt.setInt(7, longMethodInformations.getMLOC());
//			pstmt.setDouble(8, longMethodInformations.getDR());
//			pstmt.setInt(9, longMethodInformations.getODD());
//			pstmt.setDouble(10, longMethodInformations.getWMA());
//			pstmt.setInt(11, longMethodInformations.getIsLongMethodOrNot());
//			pstmt.setString(12, longMethodInformations.getProjectName());
			
			Class.forName("com.mysql.jdbc.Driver");
			
	        Connection conn = DriverManager.getConnection(
	                "jdbc:mysql://127.0.0.1:3306/mydata",
	                "root","123456");
	        
			String methodname=longMethodInformations.getMethodName();
			String classname=longMethodInformations.getMethodOfClass().replace('.', '\\');
			
			PreparedStatement pstmt;
			//pstmt = (PreparedStatement) conn.prepareStatement("update case_study set DR=?,NOAV=?,WMA=? where methodName  and methodOfClass=?;");
			pstmt = (PreparedStatement) conn.prepareStatement("select * from case_study");
			ResultSet rs=pstmt.executeQuery();
			while(rs.next()) {
				String tm=rs.getString("methodName");
				String tc=rs.getString("methodOfClass");
				if(tm.contains(methodname)&&tc.contains(classname)) {
					PreparedStatement pstm = (PreparedStatement) conn.prepareStatement("update case_study set DR=?,NOAV=?,WMA=? where keynum=?;");
					pstm.setDouble(1, longMethodInformations.getDR());
					pstm.setDouble(2, longMethodInformations.getNOAV());
					pstm.setDouble(3, longMethodInformations.getWMA());
					pstm.setInt(4, rs.getInt("keynum"));
					i = pstm.executeUpdate();
					pstm.close();
					break;
				}
			}

			pstmt.close();
			conn.close();

//		}
//		System.out.println("methodinfo the num of insert----" + i);
		}
		return i;
		
	}

	public void commitMySQL(){
		try {
			conn.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public int selectResults(LongMethodInformations longMethodInformations) throws Exception{
		int i = 0;
		Connection conn = getConn();
		String sql ="select * from longMethodInformations where MethodName = ? and MethodOfClass = ? and MethodParameters = ?;";
		PreparedStatement pstmt;
		pstmt = (PreparedStatement) conn.prepareStatement(sql);
		pstmt.setString(1, longMethodInformations.getMethodName());
		pstmt.setString(2, longMethodInformations.getMethodOfClass());
		pstmt.setString(3, longMethodInformations.getMethodParameters());
		ResultSet rs = pstmt.executeQuery();
		if(rs.next())
			i = 1;
		pstmt.close();
		conn.close();
		return i;
	}
	public int selectIsLongMethodOrNot(String methodName,String methodOfClass, String methodParameters) throws Exception{
		int i = 0;
		Connection conn = getConn();
		String sql ="select IsLongMethodOrNot from longMethodInformations where MethodName = ? and MethodOfClass = ? and MethodParameters = ?;";
		PreparedStatement pstmt;
		pstmt = (PreparedStatement) conn.prepareStatement(sql);
		pstmt.setString(1, methodName);
		pstmt.setString(2, methodOfClass);
		pstmt.setString(3, methodParameters);
		ResultSet rs = pstmt.executeQuery();
		int IsLongMethodOrNot = 0;
		if(rs.next()){
			IsLongMethodOrNot = rs.getInt("IsLongMethodOrNot");
		}	
		pstmt.close();
		conn.close();
		return IsLongMethodOrNot;
	}
	

}
