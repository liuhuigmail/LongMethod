package en.actionsofproject.database.ui;

public class ClassInfo {
	int ClassID;
	String ClassQualifiedName;
	String ClassName;
	int NumOfTimes;
	
	public ClassInfo(int ClassID, String ClassQualifiedName, String ClassName, int NumOfTimes){
		this.ClassID = ClassID;
		this.ClassQualifiedName = ClassQualifiedName;
		this.NumOfTimes = NumOfTimes;
		this.ClassName = ClassName;
	}
	public int getNumOfTimes() {
		return NumOfTimes;
	}

	public void setNumOfTimes(int numOfTimes) {
		NumOfTimes = numOfTimes;
	}
	public int getClassID() {
		return ClassID;
	}
	
	public String getClassName() {
		return ClassName;
	}
	public void setClassName(String className) {
		ClassName = className;
	}
	public void setClassID(int classID) {
		ClassID = classID;
	}
	public String getClassQualifiedName() {
		return ClassQualifiedName;
	}

	public void setClassQualifiedName(String classQualifiedName) {
		ClassQualifiedName = classQualifiedName;
	}

}
