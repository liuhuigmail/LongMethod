package en.actionsofproject.database.ui;

public class Relations {
	int key;
	int MethodID;
	int ClassID;
	int MethodInThisClassOrNot;
	int NumOfTimes;
	
	public Relations(int key, int MethodID, int ClassID, int MethodInThisClassOrNot,int NumOfTimes){
		this.key = key;
		this.MethodID = MethodID;
		this.ClassID = ClassID;
		this.MethodInThisClassOrNot = MethodInThisClassOrNot;
		this.NumOfTimes = NumOfTimes;
	}
	
	public int getNumOfTimes() {
		return NumOfTimes;
	}

	public void setNumOfTimes(int numOfTimes) {
		NumOfTimes = numOfTimes;
	}

	public int getMethodInThisClassOrNot() {
		return MethodInThisClassOrNot;
	}
	public void setMethodInThisClassOrNot(int methodInThisClassOrNot) {
		MethodInThisClassOrNot = methodInThisClassOrNot;
	}
	public int getKey() {
		return key;
	}
	public void setKey(int key) {
		this.key = key;
	}
	public int getMethodID() {
		return MethodID;
	}
	public void setMethodID(int methodID) {
		MethodID = methodID;
	}
	public int getClassID() {
		return ClassID;
	}
	public void setClassID(int classID) {
		ClassID = classID;
	}
	
}
