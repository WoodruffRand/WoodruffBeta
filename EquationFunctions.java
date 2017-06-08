import java.math.*;
import java.lang.*;
import java.lang.String;
public class EquationFunctions implements MathConstants{
	

	
	public double evalFunctionPoint(String equation, /*String var,*/ double point) throws EquationException{
		//clearing white sapce
		equation= dellInst(equation , " ");
		//resolve possible allias
		equation = replaceOppSyns(equation);
		//checking that only single var is used 
		String var =findVar(equation);
		if( !parensBalanced(equation)) throw new EquationException("Unbalanced Parentheses");
				
		//if( binary ops balanced){
		
		//make immplied multipcation explicit 
		equation = makeOppsExplicit(equation, var);

		// time to do some work and actauly solve the equation
		return solveEquation( equation, var , point );
		
	}
	
	private String findVar(String eq) throws EquationException{
		String tempEQ= eq;
		eq.toUpperCase();
		if(eq.toUpperCase().contains("BEES")){
			throw new EquationException
			("BEES?! NO BEES!");
		}
		
		tempEQ = delleteNumbs(tempEQ);
		tempEQ = delleteOpps(tempEQ);
		tempEQ = delleteBrackets(tempEQ);
		tempEQ = delleteConsts(tempEQ);
		tempEQ = deDuplicateVar(eq, tempEQ);
		//need to delete decmials!!!
		if( eq.contains(tempEQ)){
			if( tempEQ.equals("")) return "x";
			else return tempEQ;
		} else {
			throw new EquationException("Improper Variable");
		}
		
		
	}
	private String replaceOppSyns(String eq){
		String tempStr= eq;
		tempStr= standarizeInverTrigOpps(tempStr);
		
		
		return tempStr;
	}
	
	private String standarizeInverTrigOpps(String eq){
		String tempEq= eq;
		for(int i=0 ; i< inverseTrigSyns.length ; i++){
			for(int j=1; j< inverseTrigSyns[i].length; j++){//start at 1 as first index is stadard name
				tempEq=tempEq.replaceAll(inverseTrigSyns[i][j],inverseTrigSyns[i][0]);
			}
		}
		return tempEq;
	}
	/*
	private String replaceAllInstWith(String mainStr, String targetStr, String replaceWith){
		int targetIndex=0;
		
		while(targetIndex>-1){
			targetIndex = mainStr.indexOf(targetStr);
			mainStr.replaceAll(regex, replacement)
		}
		
		return mainStr;
	}
	*/
	
	private String deDuplicateVar(String eq, String cleanedEQ) throws EquationException {
		String tempStr="";
		
		for(int i=0; i<cleanedEQ.length(); i++){
			if( eq.contains( tempStr+cleanedEQ.charAt(i) ) ) {
				tempStr+=cleanedEQ.charAt(i);
			}else break;
			
		}
		
		
		String shouldbeEmpty= dellInstCaseSense(cleanedEQ,tempStr);
		
		if ( !shouldbeEmpty.equals("") ) {
			throw new EquationException
			("multiple possible variables \""+shouldbeEmpty +"\" and " +tempStr+"\" ");
		}
		
		return tempStr;
		
	}
	
	private String delleteConsts( String str){
		str = dellInst(str, "E");
		str = dellInst(str, "PI");
	
		return str; 
	}
	
	private String delleteBrackets( String str){
		str = dellInst(str, "(");
		str = dellInst(str, ")");
		str = dellInst(str, "|");
		return str; 
	}
	
	private String delleteNumbs(String str){
		for(int i=0; i<10; i++){
			String tempStr = ""+i;
			str= dellInst(str, tempStr);
		}
		str= dellInst(str, ".");//need to fix those decmials!!
		return str;
	}
	
	
	private String delleteOpps(String str){
		for (int i=0 ; i<oppTeirs.length ;i++){
			for (int j=0 ; j<oppTeirs[i].length ;j++){
				str = dellInst(str, oppTeirs[i][j] );
			}
		}
		return str;
	}
	
	private String dellInstCaseSense( String str, String target){
		int nextIndex=0;
		nextIndex= str.indexOf(target, nextIndex);
		
		while( nextIndex!=-1 && !str.equals("")){
			str = deleteSubString(str,nextIndex, nextIndex+target.length()-1);
			nextIndex= str.indexOf(target, nextIndex);
		}
		return str;
	}
	
	
	
	
	private String dellInst( String str, String target){
		int nextIndex=0;
		nextIndex= str.toUpperCase().indexOf(target, nextIndex);
		
		while( nextIndex!=-1 && !str.equals("")){
			str = deleteSubString(str,nextIndex, nextIndex+target.length()-1);
			nextIndex= str.indexOf(target, nextIndex);
		}
		return str;
	}
	/*
	private boolean validVar(String var){
		if ( strContainsNumb(var) ) return false;
		else if(strContasinsOpp (var) ) return false;
		else if(var.contains("(") || var.contains("(") ) return false;
		else if(var.contains("|") ) return false;
		else if(var.toUpperCase().contains("e") || var.toUpperCase().contains("pi") ) return false;
		return true;
	}
	*/
	private boolean strContasinsOpp(String str){
		for (int i=0 ; i<oppTeirs.length ;i++){
			for (int j=0 ; j<oppTeirs[i].length ;j++){
				if(str.toUpperCase().contains(oppTeirs[i][j])) return true;
			}
		}
		return false;
	}
	
	private boolean strContainsNumb(String str){
		for(int i=0;i<10; i++){
			String tempSt= ""+i;
			if(str.contains(tempSt)) return true;
		} 
		return false;
	}
	
	
	private boolean parensBalanced(String equation){
		int lBraceIndex=-1;
		int rBraceIndex=-1;
		int i=0;//use for cross index across both loops 
		
		//HANDEL EMPY SUB STRINGS, may want to revise, require prior verfication of equation balance
		if(equation.equals("")) return true;
		
		for (i=equation.length()-1;i>-1;i--){//scan from right for last (
			if(equation.charAt(i)=='('){
				lBraceIndex=i;
				break;
			}
		}
		
		i++;//correcting indexing 
		
		for (int j=i; j<equation.length();j++ ){//scan left for next closest )
			if(equation.charAt(j)==')')	{
				rBraceIndex=j;
				break;
			}
		}	
		
		if( (lBraceIndex==-1) && (rBraceIndex==-1)){//if no braces found, return true 
			return true;
		} else if ( ( (lBraceIndex==-1) && (rBraceIndex>-1) ) ||//if no matching bracket found, return false  
				( (lBraceIndex>-1) && (rBraceIndex==-1) ) ){
			return false;
		}else { //else recurse without internal segment 
			String tempEq=deleteSubString(equation,lBraceIndex, rBraceIndex) ;
			
			return parensBalanced(tempEq);
		} 
	}
	
	private String makeOppsExplicit(String equation, String var){
		String tempEq= equation;
		
		tempEq = makeFwdStrExplicit(tempEq, "(", false); //not exhustalivly tested 
		tempEq = makeBackwardStrExplict(tempEq, ")");
				
		tempEq = makeFwdStrExplicit(tempEq, "E", true); //not exhustalivly tested 
		tempEq = makeBackwardStrExplict(tempEq, "E");
		
		tempEq = makeFwdStrExplicit(tempEq, "PI", true); //not exhustalivly tested 
		tempEq = makeBackwardStrExplict(tempEq, "PI");
				
		tempEq = makeFwdStrExplicit(tempEq, var, false); // chec user var
		tempEq = makeBackwardStrExplict(tempEq, var);
		
		for (int i=0;i<oppTeirs[transendtalOppsIndex].length ;i++){
			tempEq = 
				makeFwdStrExplicit(tempEq, oppTeirs[transendtalOppsIndex][i], true);//chech multi char transentals 
		}
		
		return tempEq;
		
	}
	
	private String makeFwdStrExplicit(String str, String var, boolean ignoreCase){
		String tempStr= str.toUpperCase();
		int strLength=str.length();
		for(int i=0 ; i<strLength-var.length() ; i++){//minus as we do no consider last char
			int nextIndex=-1;
			if(ignoreCase){
				nextIndex=tempStr.indexOf(var, i);
				
			}else {
				nextIndex=str.indexOf(var, i);
			}
			i=nextIndex;
			
			if(nextIndex==-1)return str;
			if( (nextIndex!=0) && !oppsPreseentAtIndex(str, nextIndex-1) && !(str.charAt(nextIndex-1)=='(') && !(str.charAt(nextIndex-1)=='|')){
				str= str.substring(0, nextIndex) + "*" + str.substring(nextIndex,str.length() );
				tempStr= tempStr.substring(0, nextIndex) + "*" + tempStr.substring(nextIndex,tempStr.length() );
			}			
		}
		return str;
	}
	

	private String makeBackwardStrExplict(String equation, String target){
		
		for (int i=0;i<equation.length()-target.length();i++){
			i= equation.indexOf(target, i);
			if(i==-1 || i>equation.length()-target.length()-1) return equation;
			if( !(oppsPreseentAtIndex(equation,i+target.length())) && !( equation.charAt( i+target.length() ) == ')' ) && !( equation.charAt( i+target.length() ) == '|') ){
				equation= equation.substring(0, i+target.length()) + "*" + equation.substring(i+target.length(),equation.length() );
				i++; //just added character so need to advance index 
			}
		}
		return equation;
	}
	
	private boolean oppsPreseentAtIndex(String str, int index){
		str=str.toUpperCase();
		for(int i =0; i<oppTeirs.length ;i++){//scanning over opp tiers 
			for( int j=0; j<oppTeirs[i].length ;j++){//scanning over opps within tier 
				int currOppLength = oppTeirs[i][j].length();
				for(int k=0; k<currOppLength;k++){//scanning over chars in opp
					if(index<currOppLength-1) break;//if string forward of index is smaller than opp
					char curOppChar =oppTeirs[i][j].charAt(k);// grabbing current opp char for comparison
					char curStrChar =str.charAt(index-currOppLength+1+k);//grabbing current str char for comparison
					if( curOppChar ==curStrChar ){
						if( k==currOppLength-1) return true;//return true of all opp char match
					}else break;//stop checking if mismatch found
				}
			}
		}			
		return false; //fall through if no opps match
	}
	
	private String deleteSubString(String str, int lIndex, int rIndex){//helper method, deletes substring within given bounds 
		int startIndex=0; 
		int endIndex=0; 
		
		if(lIndex==0) {
			startIndex=rIndex+1;
			return str.substring(startIndex, str.length());
		}else{ 
			startIndex=lIndex;
			endIndex=rIndex;
			return str.substring(0, startIndex) + str.substring(endIndex+1, str.length());
		}
	}
	
	
	private double solveEquation(String equation, String var, double atValue){
		if(equation.equals("")) return 0;
		OppIndexes nextOppInd= new OppIndexes();
		nextUnboundedOpp(equation,nextOppInd);//scan for next unbounded opperator
		
		if(nextOppInd.getL()==-1){//if no unbounded opps presents but ( or | present 
			return solveEquation(equation.substring(1,equation.length()-1),var,atValue);
		}else if(nextOppInd.getL()==-2){
			return Math.abs(solveEquation(equation.substring(1,equation.length()-1),var,atValue));
		}else if(nextOppInd.getL()==-3){//if no opps present and no brackets present 
			if(equation.equals(var)) return atValue;
			else if(equation.toUpperCase().equals("PI")) return Math.PI;
			else if(equation.equals("e")) return Math.E;
			else if(equation.equals("")) return 0;
			else return Double.parseDouble(equation);
		}
		
		String nextOpp= 
			equation.substring(nextOppInd.getL(),nextOppInd.getR());	//recurse on opperator index 
		nextOpp=nextOpp.toUpperCase();//set opp to upper to match conventions
		

		String leftSubStr=equation.substring(0,nextOppInd.getL());
		String rightSubStr=equation.substring(nextOppInd.getR());
		
		if(nextOpp.equals("+")) return solveEquation(leftSubStr, var,atValue)+solveEquation(rightSubStr, var,atValue);
		else 
		if (nextOpp.equals("-")) return solveEquation(leftSubStr, var,atValue)-solveEquation(rightSubStr, var,atValue);
		else 
		if (nextOpp.equals("*")) return solveEquation(leftSubStr, var,atValue)*solveEquation(rightSubStr, var,atValue);
		else 
		if (nextOpp.equals("/")) return solveEquation(leftSubStr, var,atValue)/solveEquation(rightSubStr, var,atValue);
		else 
		if (nextOpp.equals("%")) return solveEquation(leftSubStr, var,atValue)%solveEquation(rightSubStr, var,atValue);
		else 
		if (nextOpp.equals("^")) return Math.pow(solveEquation(leftSubStr, var,atValue), solveEquation(rightSubStr, var,atValue));
		else 
		if (nextOpp.equals("SQRT")) return Math.pow(solveEquation(rightSubStr, var,atValue), (0.5) );
		else 
		if (nextOpp.equals("LOG")||nextOpp.equals("LN")) return Math.log(solveEquation(rightSubStr, var,atValue));
		else 
		if (nextOpp.equals("SIN")) return Math.sin(solveEquation(rightSubStr, var,atValue));
		else 
		if (nextOpp.equals("COS")) return Math.cos(solveEquation(rightSubStr, var,atValue));
		else 
		if (nextOpp.equals("TAN")) return Math.tan(solveEquation(rightSubStr, var,atValue));
		else
		if (nextOpp.equals(ARCSIN)) return Math.asin(solveEquation(rightSubStr, var,atValue));
		else
		if (nextOpp.equals(ARCCOS)) return Math.acos(solveEquation(rightSubStr, var,atValue));
		else
		if (nextOpp.equals(ARCTAN)) return Math.atan(solveEquation(rightSubStr, var,atValue));
		else
		
		return 0; 
		
		
	}
	
	private boolean isTransOpp(String opp){
		for (int i=0; i<oppTeirs[transendtalOppsIndex].length;i++){
			if(opp.equals(oppTeirs[transendtalOppsIndex][i])) return true; 
		}
		return false;
	}
	
	private OppIndexes nextUnboundedOpp(String equation, OppIndexes indexes){
		String tempStr=equation.toUpperCase();//aid in opp matching for muilt char opps 		
		boolean openIndexes[]= markUnboundedIndex(tempStr);
		
		
		for (int i =0; i<oppTeirs.length; i++){//iterate over op tiers 
			for(int j=0; j<oppTeirs[i].length; j++){//iterate over within a a tier 
				int nextIndex=0;//intlizing search index 
				while(nextIndex>-1){	
					nextIndex= tempStr.indexOf(oppTeirs[i][j],nextIndex);
					if(nextIndex==-1) {//when current opp is not found 
						//nextIndex=0;//reset search index 
					}else if( openIndexes[nextIndex]){//if current index is outside brackets 
						indexes.setL(nextIndex);
						indexes.setR(nextIndex+oppTeirs[i][j].length());
						return indexes;
					}else nextIndex++;//reset search index;
				}
			}
		}
		//fall through returns 
		if(tempStr.charAt(0)=='(' ){
			indexes.setL(-1);
			return indexes;// return when -1 brackets still present 
		}else if(tempStr.charAt(0)=='|' ) {
			indexes.setL(-2);
			return indexes; //return -2 when no | present
		}else {
			indexes.setL(-3);
			return indexes; //return -3 when no opps or brakets present
		}
		  
	}
	
	private boolean[] markUnboundedIndex(String equation){
		boolean markings[] = new boolean[equation.length()];
		int bracketDepth =0;
		boolean absBound= false;
		for( int i=0 ; i<equation.length(); i++){
			//moves in if ( encountered 
			if( equation.charAt(i)=='(' ){//used to indicate when in unbounded region 
				bracketDepth--;
			}else if( equation.charAt(i)==')'){
				bracketDepth++;
			} else if(equation.charAt(i)=='|'){
				if(!absBound) absBound=true;
				else absBound=false;
			}
			
			if( bracketDepth>-1 && !absBound){
				markings[i]=true;
			}else markings[i]=false;
			//mouse out if ) encountered 
			
		}
		return markings;
	}
	
	//using crazy chars as they cannot be entered my user, leading to weird results
	//private final char ARCTAN_CHAR= 0; 
	private final String ARCTAN = "☢";
	
	//private final char ARCCOS_CHAR= 1; 
	private final String ARCCOS = "♞";
	
	//private final char ARCSIN_CHAR= 2; 
	private final String ARCSIN = "☂";
	
	private final String[][] oppTeirs= new String[][]{
		{"+","-"},//tier one
		{"*","/","%"},//tier two
		{"^"},// tier three(transendential functions )
		{"SQRT","LOG", "LN","SIN","COS","TAN", ARCCOS, ARCSIN, ARCTAN}//transidentals with implied multipaction
		
	};


	
	private final String[][] inverseTrigSyns= new String[][]{//synonims of invers trgi functions
		//first member of each aray is what algebra engine recognizes, following are sydonims
		{ARCCOS,"ARCCOS","ARCOS","COS^-1"},	//arccos
		{ARCSIN,"ARCSIN","SIN^-1"},	//arcsin
		{ARCTAN,"ARCTAN","TAN^-1"}	//arctan
	};
	
	private final int transendtalOppsIndex = 3;
	
}
