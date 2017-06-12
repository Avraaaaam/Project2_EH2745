package assignment2;

//The KNN class implements the procedure for assigning the test Set of values to each one of the labelled clusters based on the chosen number of neighbors and the minimum
//distance of the selected state of the test set with the states from the defined clusters in the k-means algorith. Basically, this class creates an object that take as
//input the number of neighbors from the GUI, the test set of values and the learn set that contains the clusters from the k-means algorithm. Basically, the algorithm works
//as follows: for each state of the test Set, we calculate the Euclidean distance of this state and the rest of the states given in the learn set, we take the minimum values
//based on the number of neighbors and we check the given label that each state belongs. The majority of same labels will define the cluster that the examined state will be
//assigned. Repeat this for all the states in the test Set. For us, the optimal number of neighbors was 5.

import java.util.*;

public class KNN {

	private int neighbors;
	private String[] labels;
	public ArrayList<Result> assignedStates;
	
	
	//Initialize a simple KNN object that implements the KNN algorithm. Takes as input the number of neighbors, the labels and the Test Set and
	//learn set of the given database. As output, it gives the cluster that each state has been assigned
	public KNN(ArrayList<ClusterCenter> learnStates,ArrayList<GroupByState> testStates, int neighbors, String[] labels){
		
		this.neighbors = neighbors;
		this.labels = labels;
		
		ArrayList<Result> minNeighbors = new ArrayList<Result>();
		
		//Find the nearest neighbors for each state
		for(int i = 0;i<testStates.size();i++){
			
			ArrayList<Result> ResultList = new ArrayList<Result>();
			//locate nearest neighbors to each state of the test set
			ResultList = locateNearestNeighbor(learnStates,testStates.get(i));
			
			//the obtained results are added to the total list of minNeighbors for each state
			minNeighbors.add(new Result(testStates.get(i).getState(),ResultList));
		}
		
		//Based on the nearest neighbors of the list, assign each test state to one of the clusters
		assignedStates = new ArrayList<Result>();
		assignedStates = assignTestStatetoCluster(minNeighbors);
	}
	
	
	//Method that take as input each state from the test system and calculates the distance of this state from the already assigned states of all the clusters
	//Return a list of all the nearest neighbors according to the number of k given in the GUI.
	public ArrayList<Result> locateNearestNeighbor(ArrayList<ClusterCenter> learnStates, GroupByState testState){
		
		ArrayList<Result> Neighbors = new ArrayList<Result>();
		ArrayList<Result> nearestNeighbors = new ArrayList<Result>();
		double positionX = 0;
		double positionY = 0;
		int buses = testState.getBuses().size();
		
		//Find the position of the test state in space
		for(int i = 0;i<buses;i++){
			
			positionX += testState.getBuses().get(i).getVolt();
			positionY += testState.getBuses().get(i).getAngle();
		}
		
		double posV = positionX/buses;
		double posA = positionY/buses;
		
		for(int i = 0;i<learnStates.size();i++){			
			for(int j = 0;j<learnStates.get(i).StatesInCluster().size();j++){
				
				double stateV = 0;
				double stateA = 0;
				double distance = 0;
				int busNum = learnStates.get(i).StatesInCluster().get(j).getBuses().size();
				for(int k = 0;k<busNum;k++){
					
					stateV += learnStates.get(i).StatesInCluster().get(j).getBuses().get(k).getVolt();
					stateA += learnStates.get(i).StatesInCluster().get(j).getBuses().get(k).getAngle();
				}
				
				//Calculates the distance of the test and learn states
				double stV = stateV/busNum;
				double stA = stateA/busNum;
				distance = Math.sqrt(Math.pow((stV - posV), 2) + Math.pow((stA - posA), 2));
				
				//Adds the distance to the list of all the distances for each state
				Neighbors.add(new Result(distance,learnStates.get(i).getClusterLabel()));
				
			}
		}
		
		//Sort the obtained arraylist of each cluster from minimum to maximum distances
		Collections.sort(Neighbors,new DistanceComparator());
		
		for(int m = 0;m<neighbors;m++){
			
			//Add the first k minimum distances to the list that will be utilized for the label of the state
			nearestNeighbors.add(Neighbors.get(m));
		}
		
		return nearestNeighbors;
	}
	
	
	//Method that assigns the test state to one of the clusters based on the KNN algorithm. Counts the appearances of each label in the defined minimum distances
	//The label that appears the most wil be the label of the test set state. 
	//Also counts the frequency of this max value - If two clusters have the same max value, we urge the user to change the number of neighbors.
	public ArrayList<Result> assignTestStatetoCluster(ArrayList<Result> nearestNeighbors){
		
		ArrayList<Result> assignedStateList = new ArrayList<Result>();
		
		for(int i = 0;i<nearestNeighbors.size();i++){
			
			int[] counter = new int[labels.length];
			int max = 0;
			int index = 0;		//Corresponds with the index of the labels array
			int freq = 0;
			
			//Count the appearances of each cluster in the nearest neighbors of each state
			for(int j = 0;j<nearestNeighbors.get(i).results.size();j++){
				for(int k = 0;k<labels.length;k++){
					if(nearestNeighbors.get(i).results.get(j).label.equals(labels[k])){
						
						counter[k]++;
					}
				}
			}
				
			//Get the index of the label that appears the most as well as the number of appearances
			for(int k = 0;k<labels.length;k++){
				if(counter[k]>max){
					max = counter[k];
					index = k;
				}
			}
			
			
			//Count the times the max label appears
			for(int k = 0;k<labels.length;k++){
				if(max == counter[k]){
					freq++;
				}
			}
			
			//If appears only once, labels the state correspondingly
			if(freq == 1){
				assignedStateList.add(new Result(labels[index],nearestNeighbors.get(i).state));
			}
			else{
				System.out.println("Choose a different number of k to extract more accurate results");
				System.exit(0);
			}
		}
		
		return assignedStateList;
	}
	
	
	//A simple class that groups the values required from 
	public class Result{
		 
		String label;
		double distance;
		int state;
		ArrayList<Result> results;
		
		//Constructor that contains the distance of the two states and the label of the state in the learn Set
		public Result(double dist, String label){
			this.distance = dist;
			this.label = label;
		}
		
		//Constructor that contains the state ID of the test set and the minimum distances based on the number of k - List contains distance and label
		public Result(int state, ArrayList<Result> list){
			this.state = state;
			this.results = list;
		}
		
		//Constructor that labels each state of the test Set
		public Result(String label,int state){
			this.label = label;
			this.state = state;
		}
	}
	
	
	//Compares the collected distances and sorts the from minimum to maximum
	public class DistanceComparator implements Comparator<Result>{
		@Override
		public int compare(Result a, Result b){
			return a.distance< b.distance ? -1 : a.distance == b.distance ? 0 : 1;
		}
	}
}
