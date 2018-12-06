package en.actionsofproject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import en.actionsofproject.database.ActionsAboutDB;
import en.actionsofproject.database.ui.ClassInfo;
import en.entitys.Entity;
import en.inlinerefactoring.ExtractMethodInformations;
import en.inlinerefactoring.InlineMethodActions;



public class ProjectEvolution {
	
	protected IJavaProject iJavaproject = null;
	protected IProject iProject = null;
	public List<ICompilationUnit> compilationUnits = new ArrayList<ICompilationUnit>();
	public List<IType> types = new ArrayList<IType>();
	public List<IMethod> methods = new ArrayList<IMethod>();
	Map<IMethod, Integer> methodStatus = new HashMap();//whether method has moved  yes:1 no: 0
	public Map<IMethod, IType> MethodAndItsClass = new HashMap();
//	public Map<IMethod,MethodDeclaration> methodAndItsMehthodDeclaration = new HashMap();
	
	public ProjectEvolution(IJavaProject selectedProject, IProject selectedIProject){
		this.iJavaproject = selectedProject;
		this.iProject = selectedIProject;
		compilationUnits = getAllCompilationUnits();
		}
	//iProject.build(IncrementalProjectBuilder.CLEAN_BUILD, null);
	//iProject.refreshLocal(IResource.DEPTH_INFINITE, null);
	public void run() {
		MethodAndItsClass.clear();
		getAllITypesAndAllIMethods();
		//print();
		System.out.println("Class size-------"+types.size());
		System.out.println("method size-------"+methods.size());
/*
 * randomly select 200 methods to inline
 */
//		List<IMethod> methods = new ArrayList<>(); 
//		try {
//			methods.addAll(readFile());
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		int numOfMethods = methods.size();
//		System.out.println("numOfMethods-----"+numOfMethods);
//		Set<Integer> methodNumbers = new HashSet<>();
//		while(methodNumbers.size()<44){
//			Random ran=new Random();
//			int r = ran.nextInt(numOfMethods);
//			methodNumbers.add(r);
//			//System.out.println("methodNumbers size-----"+methodNumbers.size());
//		}
//		InlineMethodActions inlineMethodActions = new InlineMethodActions();
//		List<String> srcPackages = getSrcPackages();
//		for (int i : methodNumbers){
//			System.out.println("i--------------------"+i+"----methodName------------"+methods.get(i).getElementName());
//			ExtractMethodInformations extractMethodInformations = new ExtractMethodInformations();
//			extractMethodInformations.WriteIntoFiles(methods.get(i), 1);	
//		}
//		for (int i : methodNumbers){
//			System.out.println("----start inline --"+i);
//			//inlineMethodActions.SelectMethodToInline(methods.get(i),srcPackages);
//		}
		/*
		 * Extracting method informations to compared with other tools
		 */
//		ExtractMethodInformations extractMethodInformations = new ExtractMethodInformations();
//		for(int i=329; i<types.size(); i++){
//
//			IType type = types.get(i);
//			System.out.println("class number-------------------------------------------------------------------"+i);
//			try {
//				IMethod[] methods = type.getMethods();
//				for(IMethod method : methods){	
//					extractMethodInformations.getInformationsFromDB(method);
//				}
//			} catch (JavaModelException e) {
//				// TODO Auto-generated catch block
//				//e.printStackTrace();
//				System.out.println("Exception!!!!");
//				System.out.println(type==null);
//			}
//		}
		/*
		 * Inline method to create long methods		about 1% in all methods
		 */
		InlineMethodActions inlineMethodActions = new InlineMethodActions();
		List<String> srcPackages = getSrcPackages();
		int num = 0;
		for(int i = 0; i<types.size(); i++){
			IType type = types.get(i);
			System.out.println("class number-------------------------------------------------------------------"+i);
			//if(i==20)
				//continue;
			ExtractMethodInformations extractMethodInformations = new ExtractMethodInformations();
			IMethod[] methods;
			try {
				methods = type.getMethods();
				num+=methods.length;
				for(IMethod method : methods){
					inlineMethodActions.SelectMethodToInline(method, srcPackages);
				}
			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("======================================="+num);
//			try {
//				IMethod[] methods = type.getMethods();
//				for(IMethod method : methods){
//					Random ran=new Random();
//					int r = ran.nextInt(10);
//					if(r==1 && num<=this.methods.size()/100){
//						int flag = inlineMethodActions.SelectMethodToInline(method,srcPackages);
//						if(flag ==1){
//							extractMethodInformations.WriteIntoFiles(method, 1);
//							num++;
//						}
//					}
//					else{
//						inlineMethodActions.metrics(method, 0);
//						extractMethodInformations.WriteIntoFiles(method, 0);
//						continue;
//					}
//				
//					System.out.println("num----------------------------------------------------------------"+num);
//					//if(num == 44)
//						//break;
//				}
//			} catch (JavaModelException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			//if(num == 44)
//				//break;
//		}		
		System.out.println("Class size-------"+types.size());
		System.out.println("method size-------"+methods.size());
		System.out.println("about inline number------"+methods.size()/100);

		
//		writeNameList();
//		GetAllIMethodAndAllMethodDeclaration();
//		RelatedClass relatedClass = new RelatedClass(iProject, methods,types);
//		for(int i = 0; i<types.size(); i++){
//			IType type= types.get(i);
//			try {
//				if(type.isClass()){
//					String className = type.getFullyQualifiedName();
//					ActionsAboutDB actionsAboutDB = new ActionsAboutDB();
//					try {
//						int maxTableRow = actionsAboutDB.getTableMaxRow(3);
//						ClassInfo classinfo = new ClassInfo(maxTableRow+1, className,1);
//						actionsAboutDB.insertClassInfo(classinfo);
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			} catch (JavaModelException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		System.out.println("all method's sum ------" + methods.size());
//		for(int i = 0; i < types.size(); i++){
//			System.out.println("the " + i + " class------------------------------");
//			IType type= types.get(i);
//			try {
//				if(type.isClass()){
//					relatedClass.getMoveMethodCandidates(types.get(i));
//				}
//			} catch (JavaModelException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		ActionsAboutDB actionsAboutDB = new ActionsAboutDB();
//		try {
//			int i = actionsAboutDB.getTableMaxRow(1);
//			
//			System.out.println("getTableMaxRow----"+i);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	public Set<IMethod> readFile() throws Exception{
		File file = new File("D:\\Word2Vec\\Longmethod\\compare\\Ours\\"+iProject.getName()+"--1.txt");
		StringBuilder result = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(file));
        String s = null;
        Set<IMethod> methods = new HashSet<>(); 
        int line = 0;
        while((s = reader.readLine())!=null){//使用readLine方法，一次读一行
        	System.out.println("line---------"+line);
        	line++;
        	String[] splitString = s.split("\t");
        	String className= splitString[0];
        	String methodName = splitString[1];
        	//System.out.println("1 over-----------------------------");
        	System.out.println("className------"+className);
        	System.out.println("methodName-----"+methodName);
        	IMethod method = getIMethod(className,methodName);
        	methods.add(method);
        	}
        return methods;
	}
	public IMethod getIMethod(String className,String methodName){
		IMethod method = null;
		for (IType type:types){
			Entity classEntity = new Entity(type);
			TypeDeclaration typeDeclaration = classEntity.getTypeDeclaration();
			String className1 = typeDeclaration.resolveBinding().getQualifiedName();
			if(!className1.equals(className))
				continue;
			try {
				for(IMethod method1:type.getMethods()){
					Entity entity = new Entity(method1);
					//TypeDeclaration typeDeclaration = entity.getTypeDeclaration();
					MethodDeclaration methodDeclaration = (MethodDeclaration) entity.getAssociatedNode();
					String methodName1 = methodDeclaration.resolveBinding().toString();
					if(className.equals(className1)&&methodName.equals(methodName1)){
						System.out.println("className1-----"+className1);
						System.out.println("methodName1------"+methodName1);
						return method1;
					}		
				}
			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return method;
	}
	public List<String> getSrcPackages(){
		List<String> srcPackages = new ArrayList<String>();
		IPackageFragment[] fragments;
		try {
			fragments = iJavaproject.getPackageFragments();
			for(IPackageFragment fragment : fragments){
		        if(fragment.getKind() != IPackageFragmentRoot.K_SOURCE)
		        	continue;
		        
		        if(fragment.containsJavaResources()){
		        	srcPackages.add(fragment.getElementName());
		        }
		    }
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return srcPackages;
	}
	public void getAllITypesAndAllIMethods(){
		for(ICompilationUnit compilationUnit : compilationUnits){
			if(!compilationUnit.exists())
				continue;
			IType[] classes = null;
			try {
				classes = compilationUnit.getTypes();
			} catch (JavaModelException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			for(IType type : classes){
				try {
					if(type.isClass()){
						types.add(type);
						IMethod[] approches = null;
						approches = type.getMethods();
						for(IMethod method : approches){
							methods.add(method);
							methodStatus.put(method, 0);
							MethodAndItsClass.put(method, type);
						}	
					}
				} catch (JavaModelException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		}	
	}
//	public void GetAllIMethodAndAllMethodDeclaration(){
//		for(IMethod method : methods){
//			Entity entity = new Entity(method);
//			MethodDeclaration methodDeclaration = (MethodDeclaration) entity.getAssociatedNode();
//			methodAndItsMehthodDeclaration.put(method, methodDeclaration);
//		}
//		
//	}
	
	public void writeNameList(){
		String filePath = "D:\\PrintNameList.xlsx";
		try {
			FileOutputStream output = new FileOutputStream(filePath);
			XSSFWorkbook workbook = new XSSFWorkbook();
			for(int sheetIndex = 0; sheetIndex < 1; sheetIndex++){
				XSSFSheet sheet = workbook.createSheet("sheet" + sheetIndex);				
				for(int rowIndex = 0; rowIndex < methods.size(); rowIndex++){				
					XSSFRow row = sheet.createRow(rowIndex);					
					if(rowIndex == 0){
						row.createCell(0).setCellValue("NUM");
						row.createCell(1).setCellValue("ClassName");
						row.createCell(2).setCellValue("ClassName");
					}
					else{
						for (int j = 0; j < 3; j ++){
							if(j == 0){
								row.createCell(j).setCellValue(rowIndex);
							}
							else
								if(j == 1){
									IMethod method=methods.get(rowIndex);
									row.createCell(j).setCellValue(method.getElementName());
								}
								else
									if(j == 2 ){
										IType type = MethodAndItsClass.get(methods.get(rowIndex));
										row.createCell(j).setCellValue(type.getElementName());
									}	 
			    		 }	
					}					
					output.flush();
				}
			}
			workbook.write(output);
			output.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		 System.out.println("excel file generation");
	}
	
//	for (int j = 0; j<methods.size(); j++){
//		 IMethod method=methods.get(j);
//		 row.createCell(j + 1).setCellValue(method.getElementName());
//	 }	
	
	public void print(){
		for(IType type : types){
			System.out.println("type of name ---"+type.getElementName());
		}
		for(IMethod method : methods){
			System.out.println("method of name ---"+method.getElementName());
		}
	}
	
	protected List<ICompilationUnit> getAllCompilationUnits() {
		List<ICompilationUnit> allCompilationUnits = new ArrayList<ICompilationUnit>();
		if (iJavaproject == null) {
			return allCompilationUnits;
		}

		IPackageFragment[] packageFragments = getPackageFragments(iJavaproject);
		if (packageFragments == null) {
			return allCompilationUnits;
		}

		for (IPackageFragment packageFragment : packageFragments) {
			ICompilationUnit[] compilationUnits = getCompilationUnits(packageFragment);
			if (compilationUnits == null) {
				continue;
			}

			for (ICompilationUnit compilationUnit : compilationUnits) {
				if(!allCompilationUnits.contains(compilationUnit)){
					allCompilationUnits.add(compilationUnit);
				}
			}
		}
		return allCompilationUnits;
	}

	private IPackageFragment[] getPackageFragments(IJavaProject javaProject) {
		IPackageFragment[] packageFragments = null;

		try {
			packageFragments = javaProject.getPackageFragments();
		} catch (JavaModelException e) {
			return null;
		}

		return packageFragments;
	}

	private ICompilationUnit[] getCompilationUnits(
			IPackageFragment packageFragment) {
		try {
			if (packageFragment.getKind() != IPackageFragmentRoot.K_SOURCE) {
				return null;
			}
		} catch (JavaModelException e) {
			return null;
		}

		ICompilationUnit[] compilationUnits = null;

		try {
			compilationUnits = packageFragment.getCompilationUnits();
		} catch (JavaModelException e) {
			return null;
		}

		return compilationUnits;
	}
	
	protected CompilationUnit createCompilationUnit(ICompilationUnit compilationUnit) {
		ASTParser parser = ASTParser.newParser(AST.JLS4);
	//	parser.setProject(iJavaproject);
		parser.setSource(compilationUnit);
		parser.setProject(compilationUnit.getJavaProject());
		//Config.projectName=compilationUnit.getJavaProject().getElementName();
	//	System.out.println("项目名称："+compilationUnit.getJavaProject().getElementName());
		IPath path=compilationUnit.getPath();
		parser.setUnitName(path.toString());
		parser.setResolveBindings(true);
		parser.setStatementsRecovery(true);
		CompilationUnit unit=null;
		try
		{
			unit= (CompilationUnit) parser.createAST(null);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return unit;
	}
	
}
