import java.awt.Color;
import java.awt.Font;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import acm.graphics.*;


public class GraphCanvas extends GCanvas implements ComponentListener{

	
	GraphCanvas(){
		eqSolver = new EquationFunctions();
		currentEquation="";

		
		xMin=-100;
		xMax=100;
		
		yMin=-100;
		yMax=100;
		
		
		reMethod=method.LH;
		N=0;
			
		//drawBoarder();	
		//drawAxis(xMin, xMax ,yMin, yMax);
	}
	

	public void update()throws EquationException{
		removeAll();
		drawBoarder();	
		MinMaxVals xMinyMax= new MinMaxVals(xMin,xMax);
		MinMaxVals yMinyMax= new MinMaxVals();
		if(currentEquation!=""){
			GPoint graphPoints[] =calculateGraphPoints(xMinyMax,yMinyMax);//poits to draw secan line aproximations of function
			drawFunction(graphPoints,yMinyMax);
			
			
			switch (reMethod){
				case LH:{
					drawLhRemain(xMinyMax,yMinyMax); 
					break;					
				}
				case RH:{
					drawRhRemain(xMinyMax,yMinyMax); 
					break;
				}
				case MP:{
					drawMpRemain(xMinyMax,yMinyMax);
					break;
				}
				case TRAP:{
					drawTrapRemain(xMinyMax,yMinyMax);
					break;
				}
				default:{
					EquationException temp= new EquationException("Well, this is embarrassing but I have no idea what Remain method to use");
					throw temp;
				}
			}
		}
		drawAxis(xMin, xMax ,yMinyMax.getMin(), yMinyMax.getMax());
	}
	
	
	
	public boolean setREMethod(String methType){
		methType=methType.toUpperCase();
		if(methType.equals("LH")){
			reMethod=method.LH;
			return true;
		}else if(methType.equals("RH")){
			reMethod=method.RH;
			return true;
		}else if(methType.equals("MP")){
			reMethod=method.MP;
			return true;
		}else if(methType.equals("TRAP")){
			reMethod=method.TRAP;
			return true;
		}else return false;
	}
	
	public String getREMethod(){
		switch(reMethod){
			case LH: return "LH";
			case RH: return "RH";
			case MP: return "MP";
			case TRAP:return "TRAP";
			default: return "ERROR EVALUTING REMETHOD";
		}
	}

	
	
	public void setEquation(String eq,double lBound,double uBound, int nPoints, boolean autoScaleY){
		xMin=lBound;
		xMax=uBound;
		
		N=nPoints;
		if(autoScaleY){
			yMin=0;
			yMax=0;
		}else {
			yMin=xMin;
			yMax=xMax;
		}
		
		currentEquation=eq;

	}
	
	private void drawLhRemain(MinMaxVals xMinyMax,MinMaxVals yMinyMax)throws EquationException{
		GPoint[] remainPoints= calculateRectReimanPoints(N, xMinyMax,0); 
		graphRects(remainPoints,xMinyMax,yMinyMax);

	}
	
	private void drawRhRemain(MinMaxVals xMinyMax,MinMaxVals yMinyMax) throws EquationException{
		double deltaX= (xMinyMax.getMax()-xMinyMax.getMin())/N;
		GPoint[] remainPoints= calculateRectReimanPoints(N, xMinyMax, deltaX); 
		graphRects(remainPoints,xMinyMax,yMinyMax);
	}
	
	private void drawMpRemain(MinMaxVals xMinyMax,MinMaxVals yMinyMax) throws EquationException{
		double deltaX= (xMinyMax.getMax()-xMinyMax.getMin())/N;
		GPoint[] remainPoints= calculateRectReimanPoints(N, xMinyMax, (deltaX/2) ); 
		graphRects(remainPoints,xMinyMax,yMinyMax);
	}
	
	private void drawTrapRemain(MinMaxVals xMinyMax,MinMaxVals yMinyMax) throws EquationException{
		//double deltaX= (xMinyMax.getMax()-xMinyMax.getMin())/N;
		GPoint[] remainPoints= calculateTrapReimanPoints(N, xMinyMax); 
		graphTraps(remainPoints,xMinyMax,yMinyMax);
	}
	
	
	private GPoint[] calculateGraphPoints(MinMaxVals xMinMax, MinMaxVals yMinMax) throws EquationException{
		int nPoints = getWidth()-2;
		GPoint[] graphPoints= new GPoint[nPoints];
		double yMin = eqSolver.evalFunctionPoint(currentEquation, xMinMax.getMin());//int min and max with valid data
		double yMax = yMin;
		
		double deltaX = (xMinMax.getMax()-xMinMax.getMin())/nPoints;
		
		double currXVal = xMinMax.getMin(); //seeding start value for x
		for(int i=0; i<nPoints; i++){
			
			Double currYVal=eqSolver.evalFunctionPoint(currentEquation,currXVal);
			
			//updating y min/max vals 
			if(currYVal>yMax || ( !isReal(yMax) ) ) yMax=currYVal;
			
			if(currYVal<yMin || ( !isReal(yMin) ))	yMin=currYVal;
									
			graphPoints[i] = new GPoint(i+1,currYVal);//cannot scale y as we do not know range yet!!!
			currXVal+=deltaX;
		}
		yMinMax.setMin(yMin);
		yMinMax.setMax(yMax);
		return graphPoints;
	}
	
	private GPoint[] calculateRectReimanPoints(int nPoints, MinMaxVals xMinxMax, double StartVal) throws EquationException{
		
		GPoint[] points;
		
		if( nPoints>getWidth() ) {// caps max number of rectangles used for graphing as java gets angry at 10million rectangle objects :-(
			points=oneRecPerPixle(xMinxMax,StartVal);
		}else points = new GPoint[nPoints];
		
		
		double deltaX = (xMinxMax.getMax()-xMinxMax.getMin())/nPoints;
		double sum = 0;
		double currVal=xMinxMax.getMin()+StartVal; 
		for(int i=0; i<nPoints; i++){
			double yTemp= eqSolver.evalFunctionPoint(currentEquation, currVal);
			if (isReal(yTemp)){
				sum+=yTemp;
				
				if(nPoints<getWidth()) points[i]= new GPoint(i, yTemp);
			}else {
				if(nPoints<getWidth()) points[i]= new GPoint(i, 0);
			}
			currVal+=deltaX;
		}
		sum*=deltaX;
		displaySum(sum);
		
		return points;
	}
	
	private GPoint[] calculateTrapReimanPoints(int nPoints, MinMaxVals xMinxMax) throws EquationException{
		
		GPoint[] points;
		
		if( nPoints>getWidth() ) {// caps max number of rectangles used for graphing as java gets angry at 10million rectangle objects :-(
			points=oneRecPerPixle(xMinxMax,0);
		}else points = new GPoint[nPoints+1];//+ 1 as Trap start index at 1 through N
		
		
		double deltaX = (xMinxMax.getMax()-xMinxMax.getMin())/nPoints;
		double sum = 0;
		double currVal=xMinxMax.getMin(); 
		
		//zero index
		double yZero= eqSolver.evalFunctionPoint(currentEquation, currVal);
			if (isReal(yZero)){
				sum+=yZero/2;
				if(nPoints<getWidth()) points[0]= new GPoint(0, yZero);//zero index is divide by two
			}else {
				if(nPoints<getWidth()) points[0]= new GPoint(0, 0);
			}
		
		currVal+=deltaX;
		for(int i=1; i<nPoints; i++){
			double yTemp= eqSolver.evalFunctionPoint(currentEquation, currVal);
			if (isReal(yTemp)){
				sum+=yTemp;
				
				if(nPoints<getWidth()) points[i]= new GPoint(i, yTemp);
			}else {
				if(nPoints<getWidth()) points[i]= new GPoint(i, 0);
			}
			currVal+=deltaX;
		}
		
		//last Index
		double yN= eqSolver.evalFunctionPoint(currentEquation, currVal);
			if (isReal(yZero)){
				sum+=yN/2;
				if(nPoints<getWidth()) points[nPoints]= new GPoint(nPoints, yN);//zero index is divide by two
			}else {
				if(nPoints<getWidth()) points[nPoints]= new GPoint(nPoints-1, 0);
			}
		
		sum*=deltaX;
		displaySum(sum);
		
		return points;
	}
	
	
	
	
	private GPoint[] oneRecPerPixle(MinMaxVals xMinxMax, double StartVal) throws EquationException{
		GPoint[] points = new GPoint[getWidth()];
		double smallDeltaX= (xMinxMax.getMax()-xMinxMax.getMin())/points.length;
		for(int i =0; i<points.length;i++){
			double yTemp= eqSolver.evalFunctionPoint(currentEquation, xMinxMax.getMin()+StartVal+smallDeltaX*i);
			if (isReal(yTemp)){
				points[i]= new GPoint(i, yTemp);
			}else {
				points[i]= new GPoint(i, 0);
			}
		}
		return points;
		
	}
	
	private void graphRects(GPoint[] points, MinMaxVals xMinMax ,MinMaxVals yMinMax ){
		
		//double range= (xMinMax.getMax()-xMinMax.getMin())/points.length;
		double deltaY= yMinMax.getMax()-yMinMax.getMin();
		
		double xIntervals=points.length;
		double scalledDeltaX= (getWidth())/xIntervals;

		
		double yZeroPoint=getHeight()*(yMinMax.getMax())/deltaY;
		
		for(int i=0; i<points.length; i++){
			double currScalY= points[i].getY()/deltaY*getHeight();
			
			GRect tempRect= new GRect(scalledDeltaX,Math.abs(currScalY));
			tempRect.setFilled(true);
			
			Color redTrans=new Color(1f,0f,0f,.5f );			
			tempRect.setFillColor(redTrans);
			
			if(currScalY<0){
				tempRect.setLocation(1+i*scalledDeltaX,yZeroPoint);
			} else{
				tempRect.setLocation(1+i*scalledDeltaX,yZeroPoint-tempRect.getHeight());
			}
			add(tempRect);
			tempRect.sendToBack();
			int totalPauseDurr= 1500;//currently 1.5 seconds
			
			pause(totalPauseDurr/points.length);
		}
		
	}
	
	private void graphTraps(GPoint[] points, MinMaxVals xMinMax ,MinMaxVals yMinMax ){
		
		//double range= (xMinMax.getMax()-xMinMax.getMin())/points.length;
		double deltaY= yMinMax.getMax()-yMinMax.getMin();
		double yZeroPoint=getHeight()*(yMinMax.getMax())/deltaY;
		double nTraps=points.length;
		double scalledDeltaX= getWidth()/(nTraps-1);//minus 1 as there are n-1 traps to lines

		for(int i=1; i<points.length; i++){
			double currScalYZeo= points[i-1].getY()/deltaY*getHeight();
			double currScalYOne= points[i].getY()/deltaY*getHeight();
			Color redTrans=new Color(1f,0f,0f,.5f );
			GPolygon tempTrap= tappazoid(currScalYZeo,currScalYOne, scalledDeltaX, redTrans);
			
			
						
			//tempRect.setFillColor(redTrans);
			tempTrap.setLocation(1+(i-1)*scalledDeltaX,yZeroPoint);

			add(tempTrap);
			tempTrap.sendToBack();
			int totalPauseDurr= 1500;//currently 1.5 seconds
			
			pause(totalPauseDurr/points.length);
		}
		
	}
	
	
	private void displaySum(double sum){
		String str= "\u03A3"+"\u2248"+sum;
		
		Font font = new Font("Garamond", Font.BOLD, 16);
	
		GLabel sigma= new GLabel(str);
		sigma.setFont(font);
		
		
		GRect whiteBack= new GRect(sigma.getWidth()+2 , sigma.getHeight() );
		whiteBack.setFillColor(Color.WHITE);
		whiteBack.setFilled(true);
		whiteBack.setLocation(9,10+2);
		
		sigma.setLocation(10,10+sigma.getHeight());
		
		add(whiteBack);
		add(sigma);
		sigma.sendToFront();
		
	}
	
	private void drawFunction(GPoint[] points, MinMaxVals yMinMax){
		double yRange = yMinMax.getMax()-yMinMax.getMin();
		double deltaY =(yRange)/getHeight();

		double yZeroPoint = (yMinMax.getMax())/deltaY;
		
		GPoint temp0=new GPoint(points[0].getX(), yZeroPoint-points[0].getY()/deltaY);
		
		for(int i=1;i<points.length-1;i++){
			
			double currY= points[i].getY();
			double currScalledY= yZeroPoint-currY/deltaY;
			GPoint temp1=new GPoint(points[i].getX(), currScalledY);
			
			if (currScalledY>1 && currScalledY<getHeight()-1 || temp0.getY()>1 && temp0.getY()<getHeight()-1
				&& isReal(currScalledY) && isReal(temp0.getY() ) 	){//+- and -1 to correct for boarder
				drawLine(temp0, temp1,3,Color.BLUE);
			}
			temp0=temp1;
		}
	}
	
	boolean isReal(double x){
		return (x<0) || (x>=0);
		
	}
	
	
	private void drawBoarder(){
		GRect boarder = new GRect( 1 , 1 , getWidth()-3 , getHeight()-3 );
		add(boarder);	
	}
		
	private void drawAxis(double xMin, double xMax, double yMin, double yMax){
		if(xMin<=0 ){
			double deltaX =(xMax-xMin)/getWidth();
			double zeroPoint = (0-xMin)/deltaX;
			
			GLine yAxis = new GLine( zeroPoint ,1 ,zeroPoint ,getHeight()-1 );//y axis +1 and -1 to correct for boarder
			add(yAxis);
			drawYLabels(yMin, yMax, zeroPoint);
		}
		
		if (yMin<=0){
			double deltaY =(yMax-yMin)/getHeight();
			double zeroPoint = (yMax)/deltaY;
			
			GLine xAxis = new GLine( 1, zeroPoint ,getWidth()-3 ,zeroPoint); //x axis, +1 and -1 to correct for boarder
			add(xAxis);	
			drawXLabels(xMin, xMax, zeroPoint);
		}
		
	}
	
	private void drawPoint(double x, double y, double weight){
		GLine temp =new GLine(x,y+weight/2,x,y-+weight/2);
		temp.setColor(Color.RED);
		add(temp); 
		
	}
	
	private void drawLine(GPoint zero, GPoint one, int weight, Color color){
		for(int i=0;i<weight-1; i++){
			GLine tempLine= new GLine(zero.getX()+i, zero.getY(),one.getX()+i, one.getY()); 
			tempLine.setColor(color);
			add(tempLine);
		}
		
	}
	
	private GPolygon tappazoid(double lengthOne, double lengthTwo, double width, Color color){
		GPolygon temp = new GPolygon ();
		
		temp.addVertex(0,0);
		temp.addVertex(0,-lengthOne);
		temp.addVertex(width,-lengthTwo);
		temp.addVertex(width,0);
		temp.addVertex(0,0);
		temp.setFilled(true);
		temp.setFillColor(color);
		return(temp);
	}
	
	
	private void drawYLabels(double yMin, double yMax, double yZeroPoint){
		String yMinStr= ""+yMin;
		String yMaxStr= ""+yMax;
		GLabel yMinLab= new GLabel(yMinStr);
		GLabel yMaxLab= new GLabel(yMaxStr);
		
		Font font = new Font("Verdana", Font.BOLD, 10);
		yMinLab.setFont(font);
		yMaxLab.setFont(font);
		
		yMinLab.setLocation(yZeroPoint+2, getHeight()-yMinLab.getHeight()-1);
		yMaxLab.setLocation(yZeroPoint+2, yMaxLab.getHeight()+1);
		
		add(yMinLab); 
		add(yMaxLab);
	}
	
	private void drawXLabels(double xMin, double xMax, double xZeroPoint){
		
		String xMinStr= ""+xMin;
		String xMaxStr= ""+xMax;
		GLabel xMinLab= new GLabel(xMinStr);
		GLabel xMaxLab= new GLabel(xMaxStr);
		
		Font font = new Font("Verdana", Font.BOLD, 10);
		xMinLab.setFont(font);
		xMaxLab.setFont(font);
		
		xMinLab.setLocation(1,xZeroPoint-xMinLab.getHeight()-2);
		xMaxLab.setLocation(getWidth()-3-xMaxLab.getWidth(), xZeroPoint-xMinLab.getHeight()-2);
		
		add(xMinLab); 
		add(xMaxLab);
	}
	
	private void pause(int pauseDurr){
		double targetTime= System.currentTimeMillis()+pauseDurr;
		
		while(targetTime>System.currentTimeMillis());
	}
	
	
	private EquationFunctions eqSolver;
	private String currentEquation;

	
	private method reMethod; 
	
	private enum method {
		LH, RH, MP, TRAP
	};
	
	
	private int N;
	
	private double xMin;
	private double xMax;
	
	private double yMin;
	private double yMax;
	
	/* Implementation of the ComponentListener interface */
	public void componentHidden(ComponentEvent e) { }
	public void componentMoved(ComponentEvent e) { }
	public void componentResized(ComponentEvent e) { 
		removeAll();
		try {

			update();
		} catch (EquationException e1) {

		} 
	}
	public void componentShown(ComponentEvent e) { }
	
	

}



