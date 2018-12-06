package en.actionsofproject.database;

import java.sql.*;
import java.util.List;

import en.actionsofproject.database.ui.ClassInfo;
import en.actionsofproject.database.ui.EPValue;
import en.actionsofproject.database.ui.MethodInfo;
import en.actionsofproject.database.ui.Relations;

public class ActionsAboutDB {
	
	Connection conn;
	public ActionsAboutDB(){
		try {
			this.conn = getConn();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public Connection getConn() throws Exception{
		Class.forName("com.mysql.jdbc.Driver");
		
        Connection conn = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/mydata",
                "root","123456");
        
        //Statement stmt =  conn.createStatement();
        return conn;
	}
	public int getTableMaxRow(int i) throws Exception{
		String sql = null;
		Connection conn = getConn();
		if(i == 1){
			sql = "select max(KeyNum) from relations;";
		}else 
			if(i == 2){
				sql = "select max(MethodID) from methodinfo;";
			}else
				if(i == 3){
					sql = "select max(ClassID) from classinfo;";
				}
				else
					sql = "select max(ClassID) from EPValue;";
				
			
		PreparedStatement pstmt = (PreparedStatement)conn.prepareStatement(sql);
	    ResultSet rs = pstmt.executeQuery();
	    //System.out.println("getTableMaxRow"+rs.getInt(0));
	    int maxRow = 0;
	    if(rs.next()){
	    	if(i == 1)
	    		maxRow = rs.getInt("max(KeyNum)");
	    	else
	    		if(i == 2)
	    			maxRow = rs.getInt("max(MethodID)");
	    		else{
	    			maxRow = rs.getInt("max(ClassID)");
	    		}
	    			
	    }
	    pstmt.close();
		conn.close();
	    return maxRow;
	}
	public int getMaxTimes() throws Exception{
		String sql =  "select max(NumOfTimes) from classinfo;";
		Connection conn = getConn();
		PreparedStatement pstmt = (PreparedStatement)conn.prepareStatement(sql);
	    ResultSet rs = pstmt.executeQuery();
	    int maxTimes = 0;
	    if(rs.next()){
	    	maxTimes = rs.getInt("max(NumOfTimes)");
	    }
	    pstmt.close();
		conn.close();
	    return maxTimes;
		
	}
	public void delete(int i) throws Exception{
		String sql = null;
		if(i == 1){
			sql = "delete from relations;";
		}else 
			if(i == 2){
				sql = "delete from methodinfo;";
			}else
				sql = "delete from classinfo;";
		PreparedStatement pstmt = (PreparedStatement)conn.prepareStatement(sql);
	    ResultSet rs = pstmt.executeQuery();
	    pstmt.close();
		conn.close();
	}
	public int getRelationsClassID(String className,int num) throws Exception {
		int classId = 0;
		Connection conn = getConn();
		String sql = null;
		sql = "select ClassID from ClassInfo where ClassQualifiedName = ? and NumOfTimes = ?;";
		PreparedStatement pstmt;
		pstmt = (PreparedStatement) conn.prepareStatement(sql);
		pstmt.setString(1, className);
		pstmt.setInt(2, num);
	    ResultSet rs = pstmt.executeQuery();
	    while(rs.next()){
	    	classId = rs.getInt("ClassID");
	    }
		pstmt.close();
		conn.close();
		return classId;
	}
	public int getRelationsMethodID(String methodName, String className, int num) throws Exception {
		int methodId = 0;
		String sql = null;
		Connection conn = getConn();
		sql = "select methodID from MethodInfo where methodName = ? and methodOfClass = ? and NumOfTimes = ? ;";
		PreparedStatement pstmt;
		pstmt = (PreparedStatement) conn.prepareStatement(sql);
		pstmt.setString(1, methodName);
		pstmt.setString(2, className);
		pstmt.setInt(3, num);
	    ResultSet rs = pstmt.executeQuery();
	    while(rs.next()){
	    	methodId = rs.getInt("MethodID");
	    }
		pstmt.close();
		conn.close();
		return methodId;
	}
	
	public int insertClassInfo(ClassInfo classInfo) throws Exception{
		int i = 0;
		String sql = "insert into ClassInfo (ClassID,ClassQualifiedName,ClassName,NumOfTimes) values(?,?,?,?);";
		PreparedStatement pstmt;
		pstmt = (PreparedStatement) conn.prepareStatement(sql);
		pstmt.setInt(1, classInfo.getClassID());
		pstmt.setString(2, classInfo.getClassQualifiedName());
		pstmt.setString(3, classInfo.getClassName());
		pstmt.setInt(4, classInfo.getNumOfTimes());
		i = pstmt.executeUpdate();
		pstmt.close();
		conn.close();
	
		return i;
		 
	}
	public int insertEPValue(EPValue epvalue) throws Exception{
		int i = 0;
		String sql = "insert into EPValue (ClassID,ClassName,EntityPlacement) values(?,?,?);";
		PreparedStatement pstmt;
		pstmt = (PreparedStatement) conn.prepareStatement(sql);
		pstmt.setInt(1, epvalue.getClassID());
		pstmt.setString(2, epvalue.getClassName());
		pstmt.setDouble(3, epvalue.getEntityPlacement());
		i = pstmt.executeUpdate();
		pstmt.close();
		conn.close();
	
		return i;
		 
	}
	public int insertMethodInfo(MethodInfo methodInfo) throws Exception{
		int i = 0;
		String sql = "insert into methodinfo (MethodID, ClassName, MethodOfClass, MethodStatus,NumOfTimes) values(?,?,?,?,?);";
		PreparedStatement pstmt;
		pstmt = (PreparedStatement) conn.prepareStatement(sql);
		pstmt.setInt(1, methodInfo.getMethodID());
		pstmt.setString(2, methodInfo.getMethodName());
		pstmt.setString(3, methodInfo.getMethodOfClass());
		pstmt.setInt(4, methodInfo.getStatus());
		pstmt.setInt(5, methodInfo.getNumOfTimes());
		i = pstmt.executeUpdate();
		pstmt.close();
		conn.close();
//		System.out.println("methodinfo the num of insert----" + i);
		return i;
		
	}

	public int insertRelations(Relations relations) throws Exception{
		int i = 0;
		int result = selectResults(relations);
//		System.out.println("result---------------"+result);
		if(result == 0){
			Connection conn = getConn();
			String sql = "insert into relations (KeyNum, ClassID, MethodID,MethodInThisClassOrNot,NumOfTimes) values(?,?,?,?,?);";
			PreparedStatement pstmt;
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			pstmt.setInt(1, relations.getKey());
			pstmt.setInt(2, relations.getClassID());
			pstmt.setInt(3, relations.getMethodID());
			pstmt.setInt(4, relations.getMethodInThisClassOrNot());
			pstmt.setInt(5, relations.getNumOfTimes());
			i = pstmt.executeUpdate();
			pstmt.close();
			conn.close();
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
	public int selectResults(Relations relations) throws Exception{
		int i = 0;
		Connection conn = getConn();
		String sql ="select * from relations where MethodID = ? and ClassID = ? and NumOfTimes = ?;";
		PreparedStatement pstmt;
		pstmt = (PreparedStatement) conn.prepareStatement(sql);
		pstmt.setInt(1, relations.getMethodID());
		pstmt.setInt(2, relations.getClassID());
		pstmt.setInt(3, relations.getNumOfTimes());
		ResultSet rs = pstmt.executeQuery();
		if(rs.next())
			i = 1;
		pstmt.close();
		conn.close();
		return i;
	}
	

}
