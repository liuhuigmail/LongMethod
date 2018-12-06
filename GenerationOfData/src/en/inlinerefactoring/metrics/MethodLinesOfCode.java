package en.inlinerefactoring.metrics;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.compiler.IScanner;
import org.eclipse.jdt.core.compiler.ITerminalSymbols;
import org.eclipse.jdt.core.compiler.InvalidInputException;


/**
 * Calculates MLOC: Method Lines of Code
 * 
 * @author Guillaume Boissier
 * @since 1.3.6
 */
public class MethodLinesOfCode {

	/**
	 * filter out everything outside of the method body
	 */
	
	protected String filterSourceToProcess(String a_source) {
		IMethod method;
		String l_return;
		int l_indexOfMethodBodyStart = a_source.indexOf('{');
		int l_indexOfMethodBodyEnd = a_source.lastIndexOf('}');
		if (l_indexOfMethodBodyStart != -1 && l_indexOfMethodBodyEnd != -1) {
			l_return = a_source.substring(l_indexOfMethodBodyStart + 1, l_indexOfMethodBodyEnd).trim();
		} else {
			l_return = "";
		}

		return l_return;
	}
	public int calculateNumberOfLines(String a_source) throws InvalidInputException{
		//String l_srcToCount = filterSourceToProcess(a_source).trim();
		
		//Set<Integer> l_lineSet = new HashSet<Integer>();
		//System.out.println("���� calculatenumberofline");
		IScanner l_scanner = ToolFactory.createScanner(false, false, true, true);
		l_scanner.setSource(a_source.toCharArray());
		
		int token = l_scanner.getNextToken();
		
		int l_startpos = l_scanner.getCurrentTokenStartPosition();
		int l_lineNb = l_scanner.getLineNumber(l_startpos);
			
	
		while (true) {
			if (token == ITerminalSymbols.TokenNameEOF) {
				break;
			}
			token = l_scanner.getNextToken();
		}
	
		int l_endpos = l_scanner.getCurrentTokenEndPosition();
			
		int l_endLine = l_scanner.getLineNumber(l_endpos);

		//System.out.println("endLine:"+l_endLine);
		//System.out.println("startLine"+l_lineNb);
		return l_endLine-l_lineNb+1;
	}

}
