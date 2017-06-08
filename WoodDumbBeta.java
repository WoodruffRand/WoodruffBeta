import acm.program.ConsoleProgram;


public class WoodDumbBeta extends ConsoleProgram{

	//class instances 
	private GraphCanvas graph; 
	private EquationFunctions eqSolver;
	
	
	
	public static void main(String[] args) {
		new WoodDumbBeta().start(args);
		}
	
	public void init() {
		//addActionListeners(this);
		
		graph = new GraphCanvas();
		eqSolver = new EquationFunctions();
		AutoScale=true;
		add(graph);
		// re size text window, expand graphics window and change realative size 
		
		welcomeSplash();
	}
	
	public void run(){
		try {
			graph.update();
		} catch (EquationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
			
		mainMenu();
	
	}
		
	private void welcomeSplash(){
		println("Welcome to WooddruffBeta");
		println("The main funcationality of this program is as a riemann sum calculator.");
		println("");
		//println("I'm a Randall, \n I made this!");
		println("");
		println("");
	}
	//ivars 
	 
	private void mainMenu(){
		int selection=0;
			while (true){
			println("\n\n ◊ Main Menu ◊");
			println("────────────────");
			println("");
			println("\t(1)Options Menu.");
			println("\t(2)Compute Sum.");
			println("\t(3)Graph Function.");
			while (true){
				 selection =readInt("\nPlease enter selection: ");
				if(selection<1 || selection>3) println("Invalid selction, please try again. ");
				else break;
			}
			
			if(selection ==1) optionsMenu();
			else 
			if(selection ==2) computeFunction();
			else 
			if(selection ==3) graphMenu();
		}
	}
	
	private void graphMenu(){
		
	}
	
	private void optionsMenu(){
		int selection=0;
		boolean cont= true;
		
		while(cont){
		
			println("\n\n   Options Menu");
			println("────────────────");
			println("");
			println("\t(1) Auto Scale Axis: Currently "+AutoScale);
			println("\t(2) Change Riemann Method. Curr: "+graph.getREMethod());
			println("\t(3) Return to main. ");
			
			while (true){
				 selection =readInt("\nPlease enter selection: ");
				if(selection<1 || selection>3) println("Invalid selction, please try again. ");
				else break;
			}
			
			if(selection ==1) {
				if (AutoScale==true) AutoScale=false;
				else AutoScale=true;
			}
			else if(selection ==2){
				setRMethod();
			}
			else if(selection ==3) cont=false;
			else println("Invalid selection.");
		}
		
	}
	
	private void setRMethod(){
		while(true){
			println("Currently: "+graph.getREMethod() );
			println("\t1)Right Hand");
			println("\t2)Left Hand");
			println("\t3)Mid Point");
			println("\t4)Trapezoidal");
			
			int choice =readInt("Please chose one: ");
			
			if(choice>4 ||choice<1) println("Invalid choice");
			else{
				String strChoice="";
				if(choice ==1 ) {
					strChoice= "RH";
					//break;
				}	else if(choice ==2 ) {
					strChoice= "LH";
					//break;
				}
				else if(choice ==3 ) {
					strChoice= "MP";
					//break;
				} else if(choice ==4 ) {
					strChoice= "TRAP";
					//break;
				}
				
				if(!graph.setREMethod(strChoice)) {
					println("error is seeting RM method");
				} else {
					break;
				}
					
			}
		}
		
	}
	
	private double getBound(String promt){
		boolean cont= true;
		double rtrnVal=0;
		while(cont){ 
			String UserInput= readLine(promt);
			cont =false;
			try{
				rtrnVal=eqSolver.evalFunctionPoint(UserInput, 0);
			} catch (EquationException e) {
				println("Could not be evaluated to a numerical value.");
				cont = true;
			}
		}
		return rtrnVal;
	}
	
	
	private void computeFunction(){
		while (true){
			String equation=readLine("Please enter an Equation: f(x)= ");
			double lowerLim=getBound("Please specify lower bound: ");
			double upperLim=getBound("Please specify upper bound: ");
			int n=readInt("Specify number of rectangles to aproximate area: ");
			graph.setEquation(equation, lowerLim,upperLim,n, AutoScale);
			try {
				println ("Graphing.");
				graph.update();
				break;
			} catch (EquationException e) {
				println("Cannot compute: "+e.getMessage());
			//	e.printStackTrace();
			}
		}
	}
	
	//Ivars
	
	private boolean AutoScale;
}
