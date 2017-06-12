package assignment2;

//This class is utilized in combination with the GroupByState class and the buses class so that we can group all the states in the assigned clusters after the implementation of the
//k-means algorithm. Basically, each object of the class contains two values that correspond to the position of the cluster center in the space, an ID which a serial number and occurs based on
//the utilized number of k clusters, a list of assigned points which are the states that have been assigned to each cluster from the k-means algorithm (initially it is empty) and
//a label that is one of the 4 required labels for the clusters. Basically, each clusterCenter objects groups everything that is required for the k-means algorithm for better
//inspection of the code.

import java.util.*;

public class ClusterCenter {

	private double Vcenter;
	private double Acenter;
	private int id;
	private ArrayList<GroupByState> containedPoints;
	private String label;
	
	
	//This constructor is used for the random centers creation - Initialized the list of states
	public ClusterCenter(double x, double y, int id){
		this.Vcenter = x;
		this.Acenter = y;
		this.id = id;
		this.containedPoints = new ArrayList<GroupByState>();
	}
	
	
	//This constructor is used when all the states have been assigned to a cluster so that we can process the labelling function
	//and the KNN algorithm in the next step
	public ClusterCenter(double x, double y, int id, ArrayList<GroupByState> points,String label){
		this.Vcenter = x;
		this.Acenter = y;
		this.id = id;
		this.containedPoints = points;
		this.label = label;
	}
	
	
	//Returns an array of all the assigned points in a cluster
	public ArrayList<GroupByState> StatesInCluster(){
		return this.containedPoints;
	}
	
	
	public double getVcenter(){ return this.Vcenter;}
	
	public double getAcenter(){ return this.Acenter;}
	
	public int getClusterID(){return this.id;}
	
	public String getClusterLabel(){ return this.label;}
	
	public void setLabel(String label){
		this.label = label;
	}
	
	public void setNewVcenter(double volt){
		this.Vcenter = volt;
	}
	
	public void setNewAcenter(double angle){
		this.Acenter = angle;
	}
	
	
	//Inserts a new point, which a state, to a specified cluster by its ID. This occurs when the minimum distance has been defined
	public void assignStateToCluster(GroupByState point){
		containedPoints.add(point);
	}
	
	
	//Returns the Cluster's main Information: Position in the space, ID and the number of assigned states in each cluster
	public void getClusterInfo(){
		
		System.out.println("Cluster ID: " +id);
		System.out.println("Cluster Position in Space: "+Vcenter+","+Acenter);
		System.out.println("Number of States in cluster: "+containedPoints.size());
	}
}
