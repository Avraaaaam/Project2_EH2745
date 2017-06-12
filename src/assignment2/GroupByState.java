package assignment2;

//This class is utilized so that the data can be grouped by the state number. So basically, when an object is created, it contains an ID which is the state number
//and initializes an ArrayList of buses which contains all the buses that correspond to the specific state. The buses objects contain all the required information
//(See also the Buses class for more information on the buses objects)

import java.util.*;

public class GroupByState {

	private int state;
	private ArrayList<Buses> stateBuses;
	
	public GroupByState(int stateID,ArrayList<Buses> buses){
		
		this.state = stateID;
		this.stateBuses = buses;
	}
	
	public int getState(){ return this.state; }
	
	
	public ArrayList<Buses> getBuses(){ return this.stateBuses; }
}
