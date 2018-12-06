package en.movemethod;

import java.util.List;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;

public class MoveMethodNode {
	
	public IMethod method;//�ƶ�����
	public ITypeBinding typeBinding;//Ŀ�ĵ�
	public List<IVariableBinding> variableBindings = null;
	public String targetTypeName;//Ŀ�ĵص�����
	public List<String> relationsClasses;
	public MoveMethodNode(){
		setRelationsClasses(relationsClasses);
	}
	public List<String> getRelationsClasses() {
		return relationsClasses;
	}
	public void setRelationsClasses(List<String> relationsClasses) {
		if(variableBindings != null)
			for(IVariableBinding iVariableBinding : variableBindings){
				String className = iVariableBinding.getType().getQualifiedName();
				if(!relationsClasses.contains(className))
					relationsClasses.add(className);
			}
		this.relationsClasses = relationsClasses;
	}
}
