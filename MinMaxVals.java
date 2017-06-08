
public class MinMaxVals {
	
	
	MinMaxVals(){
		min = 0;
		max = 0;
	}
	
	MinMaxVals( double minV, double maxV){
		min = minV;
		max = maxV;
	}

	public void setMin(double minV){
		min = minV;
	}
	
	public void setMax(double maxV){
		max = maxV;
	}
	
	public double getMax(){
		return max;
	}
	
	public double getMin(){
		return min;
	}
	
	
	private double min;
	private double max;
}
