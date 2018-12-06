package en.inlinerefactoring.database.ui;

public class LongMethodInformations {
	int keynum;
	String methodName;
	String methodOfClass;
	String methodParameters;
	String methodBody;
	String projectName;
	int NOAV;
	int MLOC;
	double DR;
	int ODD;
	double WMA;
	int isLongMethodOrNot;
	public int getIsLongMethodOrNot() {
		return isLongMethodOrNot;
	}

	public void setIsLongMethodOrNot(int isLongMethodOrNot) {
		this.isLongMethodOrNot = isLongMethodOrNot;
	}

	public LongMethodInformations(int keynum, String methodName, String methodOfClass, String methodParameters, 
			String methodBody, int NOAV, int MLOC, double DR, int ODD, double WMA,int isLongMethodOrNot, String projectName){
		this.keynum = keynum;
		this.methodName = methodName;
		this.methodOfClass = methodOfClass;
		this.methodParameters = methodParameters;
		this.methodBody = methodBody;
		this.NOAV = NOAV;
		this.MLOC = MLOC;
		this.DR = DR;
		this.ODD = ODD;
		this.WMA = WMA;
		this.isLongMethodOrNot = isLongMethodOrNot;
		this.projectName=projectName;
	}
	
	public int getKeynum() {
		return keynum;
	}
	public void setKeynum(int keynum) {
		this.keynum = keynum;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public String getMethodOfClass() {
		return methodOfClass;
	}
	public void setMethodOfClass(String methodOfClass) {
		this.methodOfClass = methodOfClass;
	}
	public String getMethodParameters() {
		return methodParameters;
	}
	public void setMethodParameters(String methodParameters) {
		this.methodParameters = methodParameters;
	}
	public String getMethodBody() {
		return methodBody;
	}
	public void setMethodBody(String methodBody) {
		this.methodBody = methodBody;
	}
	public int getNOAV() {
		return NOAV;
	}
	public void setNOAV(int nOAV) {
		NOAV = nOAV;
	}
	public int getMLOC() {
		return MLOC;
	}
	public void setMLOC(int mLOC) {
		MLOC = mLOC;
	}
	public double getDR() {
		return DR;
	}
	public void setDR(double dR) {
		DR = dR;
	}
	public int getODD() {
		return ODD;
	}
	public void setODD(int oDD) {
		ODD = oDD;
	}
	public double getWMA() {
		return WMA;
	}
	public void setWMA(double wMA) {
		WMA = wMA;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	

}
