package assignment2;

//The purpose of this class is to group the necessary values (voltage, angle, state that the bus belongs, serial number and substation ID) 
//required for each bus given in the SQL table. Basically, this will be incorporated in the GroupByState class
//and the main utilization of this class is to not lost track of data


public class Buses {
	
	private double voltage;
	private double angle;
	private String sub_ID;
	private int state;
	private int busNum;
	
	public Buses(double volt,double ang,int state,String rdfID,int busNum){
		
		this.voltage = volt;
		this.angle = ang;
		this.state = state;
		this.sub_ID = rdfID;
		this.busNum = busNum;
	}
	
	
	public void setVolt(double volt){
		this.voltage = volt;
	}
	
	public void setAngle(double ang){
		this.angle = ang;
	}
	
	public double getVolt(){ return this.voltage; }
	
	
	public double getAngle(){ return this.angle; }
	
	
	public int getState(){ return this.state; }
	
	
	public String getSubID(){ return this.sub_ID; }
	
	
	public int getBusNum(){ return this.busNum; }
	
	public String toString(){
		return "("+this.voltage+","+this.angle+")";
	}
	
	

}
