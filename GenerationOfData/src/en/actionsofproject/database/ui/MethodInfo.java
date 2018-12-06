package en.actionsofproject.database.ui;

public class MethodInfo {
	int MethodID;
	String MethodName;
	String MethodOfClass;
	int Status;
	int NumOfTimes;
	public MethodInfo(int MethodID, String MethodName, String MethodOfClass, int Status, int NumOfTimes){
		this.MethodID = MethodID;
		this.MethodName = MethodName;
		this.MethodOfClass = MethodOfClass;
		this.Status = Status;
		this.NumOfTimes = NumOfTimes;
	}
	public int getNumOfTimes() {
		return NumOfTimes;
	}

	public void setNumOfTimes(int numOfTimes) {
		NumOfTimes = numOfTimes;
	}
	
	public int getMethodID() {
		return MethodID;
	}
	public void setMethodID(int methodID) {
		MethodID = methodID;
	}
	public String getMethodName() {
		return MethodName;
	}
	public void setMethodName(String methodName) {
		MethodName = methodName;
	}
	public String getMethodOfClass() {
		return MethodOfClass;
	}
	public void setMethodOfClass(String methodOfClass) {
		MethodOfClass = methodOfClass;
	}
	public int getStatus() {
		return Status;
	}
	public void setStatus(int status) {
		Status = status;
	}
	

}
