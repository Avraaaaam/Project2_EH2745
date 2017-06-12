package assignment2;

//The main purpose of the class is to implement the whole procedure of the k-means algorithm. It contains all the peripheral methods that will be utilized for the algorithm
//as well as the actual code of the k-means. Each object created is initialized based on the chosen number of clusters and the maximum number of iterations that can be specified from
//the implemented GUI. The steps of the implemented k-means algorithm are given below"
//		1. Normalize the values based on the min-max method descrribed below so that they can lie in the same boundaries and have the same importance
//		2. Create randomnly the initial centers based on the random partition method that assigns randomnly states in each cluster and calculates their position
//		3. Implementation of the k-means algorithm which is: calculate the distance of each state from the centers, assign the state to the closest center based on the minimum Euclidean
//			distance, recalculate the position of the centers after the implementation of the algorithm, repeat the procedure until the centers don't move anymore
//		4. ReNormalize the values again to their physical values
//		5. Label the clusters that occur from the algorithm implementation based on the LabelState class


import java.util.*;

public class Kmeans {
	
	private int cluster_num;		//Specified number of clusters in GUI
	private static double tolerance = Math.pow(10, -9);	//Abstarct so that we get accurate results. It can be greater than this number
	private int max_iterations;		//Maximum number of iterations specified in the GUI
	
	//Initialize a Kmeans object that is characterized by the number of clusters and the necessary iterations specified in the GUI
	public Kmeans(int clusters, int iterations){
		
		this.cluster_num = clusters;
		this.max_iterations = iterations;
	}
	
	
	//Method that takes as input the a list of all the states of the given system in order to find the minimum and maximum value of voltage and angle
	//Returns an array of the requested values and is used for the normalization of the parameters used in the k-means algorithm
	public double[] findMinMaxVoltAngle(ArrayList<GroupByState> states){
		
		double[] voltBound = new double[4];
		double minV = states.get(0).getBuses().get(0).getVolt();
		double maxV = states.get(0).getBuses().get(0).getVolt();
		double minA = states.get(0).getBuses().get(0).getAngle();
		double maxA = states.get(0).getBuses().get(0).getAngle();
		
		for(int i = 0;i<states.size();i++){
			for(int j = 0;j<states.get(i).getBuses().size();j++){
				
				//Minimum value of voltage
				if(states.get(i).getBuses().get(j).getVolt()<minV){
					minV = states.get(i).getBuses().get(j).getVolt();
				}
				
				//Maximum value of voltage
				if(states.get(i).getBuses().get(j).getVolt()>maxV){
					maxV = states.get(i).getBuses().get(j).getVolt();
				}
				
				//Minimum value of angle
				if(states.get(i).getBuses().get(j).getAngle()<minA){
					minA = states.get(i).getBuses().get(j).getAngle();
				}
				
				//Maximum value of angle
				if(states.get(i).getBuses().get(j).getAngle()>maxA){
					maxA = states.get(i).getBuses().get(j).getAngle();
				}
			}
		}
		
		voltBound[0] = maxV;
		voltBound[1] = minV;
		voltBound[2] = maxA;
		voltBound[3] = minA;
		return voltBound;
	}
	
	
	//Takes as input the previously created matrix of the minimum and maximum values of voltage and angles and normalize
	//these parameters based on the min-max normalization function (given below) so that each value will lie between 0-1 in order to give
	//equal importance to all the measured values for the k-means clustering
	//Returns a list of the normalized parameters
	public ArrayList<GroupByState> NormalizeValues(ArrayList<GroupByState> numStates){
		
		ArrayList<GroupByState> normalized = new ArrayList<GroupByState>();
		
		double[] values = new double[4];
		values = findMinMaxVoltAngle(numStates);		//Structure is: 0-VoltMax,1-VoltMin,2-AngMax,3-AngMin
		
		for(int i = 0;i<numStates.size();i++){
			
			ArrayList<Buses> normBuses = new ArrayList<Buses>();
			double normVolt = 0;
			double normAng = 0;
			int numBuses = numStates.get(i).getBuses().size();
			for(int j = 0;j<numBuses;j++){
				
				normVolt = (numStates.get(i).getBuses().get(j).getVolt()- values[1])/(values[0] - values[1]);
				normAng = (numStates.get(i).getBuses().get(j).getAngle() - values[3])/(values[2] - values[3]);
				normBuses.add(new Buses(normVolt,normAng,numStates.get(i).getBuses().get(j).getState(),numStates.get(i).getBuses().get(j).getSubID(),numStates.get(i).getBuses().get(j).getBusNum()));
			}
			
			//Results are added to a new arraylist that contains the normalized parameters of each bus of each specified state
			normalized.add(new GroupByState(numStates.get(i).getState(),normBuses));
		}
		
				// The min - max method used for normalization - values can be either voltage or angle
				//						  (value - min(values))
			 	// normalized_value =	--------------------------
				//						(max(values) - min(values))

		return normalized;
	}
	
	
	//Creates the intial random points for the cluster centers based on the random partition method - Basically, it assigns randomly a cluster to each state and then calculates
	//the initial position of the cluster center to use it to the following iterations
	//For the calculation of the position, we assume that each state can be represented by a mean value of voltage and angle from the contained buses
	//Therefore, the position of the centers is calculated from the mean voltage and angle of all the contained states in each cluster
	public ArrayList<ClusterCenter> createRandomCenter(ArrayList<GroupByState> normStates){
		
		ArrayList<ClusterCenter> random = new ArrayList<ClusterCenter>();
		ArrayList<ArrayList<GroupByState>> randomCluster = new ArrayList<ArrayList<GroupByState>>();
		ArrayList<Integer> stateIndex = new ArrayList<Integer>();
		Random randVal = new Random();
		
		for(int i = 0;i<normStates.size();i++){
			stateIndex.add(i);		//A list that is used for the random generator so that it can give the index of each state in the full list of states
									//Basically, it add values from 0 - 199 which correspond to the index of the states in the normStates list
		}
		
		for(int i = 0;i<cluster_num;i++){
			randomCluster.add(new ArrayList<GroupByState>());		//For each cluster, it initializes a new list
		}
		
		int i = 0;
		while(i<normStates.size()){
			
			int randomState = randVal.nextInt(stateIndex.size());	//Randomly select a number from 0 - 199
			int clusterID = 0;
			for(int j = 0;j<cluster_num;j++){
				if(i<normStates.size()/cluster_num + (normStates.size()/cluster_num)*j){
					
					clusterID = j;		//Assigns the first 50 randomly selected states to the first cluster and so on
					break;
				}
			}
			
			randomCluster.get(clusterID).add(normStates.get(stateIndex.get(randomState)));	//Insert the randomly selected state to the list that correspond to the given cluster
			stateIndex.remove(randomState);		//Then the index of the state that has been selected will be removed from the stateIndex list so that the added state will not be added again
			i++;
		}
		
		//If after this the list is not empty (not even number of states or not a perfect division due to cluster number), 
		//assign the rest of states to the clusters by randomly choosing the cluster ID
		if(stateIndex.size()!=0){
			Random rand = new Random();			
			for(int j = 0;j<stateIndex.size();j++){
				int randCluster = rand.nextInt(cluster_num);
				randomCluster.get(randCluster).add(normStates.get(stateIndex.get(j)));
				stateIndex.remove(j);
			}
		}
		
		
		//Define the mean value of each cluster to specify the initial position of the 4 centers
		for(int k = 0;k<randomCluster.size();k++){
			
			double sumAllV = 0;
			double sumAllA = 0;
			double initX = 0;
			double initY = 0;
			for(int j = 0;j<randomCluster.get(k).size();j++){
				
				double sumStateV = 0;
				double sumStateA = 0;
				double stateX = 0;
				double stateY = 0;

				int busNum = randomCluster.get(k).get(j).getBuses().size();
				for(int m = 0;m<busNum;m++){
					
					sumStateV+=randomCluster.get(k).get(j).getBuses().get(m).getVolt();
					sumStateA+=randomCluster.get(k).get(j).getBuses().get(m).getAngle();
				}
				
				//Mean value of each state - Correspond to the position of the state in space
				stateX = sumStateV/busNum;
				stateY = sumStateA/busNum;
				
				sumAllV+=stateX;
				sumAllA+=stateY;
			}
			
			//Mean value of each center - Correspond to the mean value of the center, based on the states' positions defined before
			initX = sumAllV/randomCluster.get(k).size();
			initY = sumAllA/randomCluster.get(k).size();
			random.add(new ClusterCenter(initX,initY,k));
		}
		
		//Returns the initial position of the cluster centers
		return random;
	}
	
	
	//Main algorithm that implements K-Means Clustering - Utilized info from the class ClusterCenter for the definition of
	//the centroids. Initializes the procedure and performs as many iterations as possible until the distance between the
	//previous and the current position of the cluster center is less than the specified tolerance or the maximum number of iterations is reached.
	//In the labelling section, there have been cases where a cluster is not labelled due to the fact that some labels are given to the same cluster.
	//In this case, the algorithm repeats until each cluster is assigned with a different label
	public ArrayList<ClusterCenter> implementKmeans(ArrayList<GroupByState> states){
		
		ArrayList<ClusterCenter> center = new ArrayList<ClusterCenter>();
		ArrayList<ClusterCenter> InitCenters = new ArrayList<ClusterCenter>();
		ArrayList<ClusterCenter> LastCenters = new ArrayList<ClusterCenter>();
		ArrayList<ClusterCenter> CheckedCenters = new ArrayList<ClusterCenter>();
		ArrayList<ClusterCenter> RandomCenters = new ArrayList<ClusterCenter>();
		ArrayList<GroupByState> normStates = new ArrayList<GroupByState>();
		
		//The repeater value is used when the labelling is not successful so that the algorithm will repeat
		boolean repeater = false;
		while(!repeater){
			
			repeater = true;
			int iteration = 0;
			boolean stopped = false;
			
			//Normalize the voltage and angle of the given states before starting the KMeans algorithm
			normStates = NormalizeValues(states);
			
			//Create random centers based on random partition method
			RandomCenters = createRandomCenter(normStates);
			
			//Starts the iterations for the determination of clusters - Uses normalized values for voltage and angle
			while(!stopped){
				
				if(iteration == 0){
					//Enters only once in order to assign the elements of RandomCenters list to the initial centers list
					InitCenters = RandomCenters;
				}
				else{
					InitCenters = LastCenters;		//Gives as initial centers the ones calculated in the previous iteration
					//Delete all assigned points to cluster to start over - Data are taken from previous iteration
					for(int i = 0;i<InitCenters.size();i++){
						if(InitCenters.get(i).StatesInCluster()!=null){
							InitCenters.get(i).StatesInCluster().clear();		//Deletes all assigned states from each cluster due to previous iteration
						}
					}
				}
				
				
				//-------------------------------START-------------------------------//
				
				
				//Calculate the distance between each point and each cluster center - Assign point to cluster based on the minimum distance values
				//The distance is calculated based on the Euclidean distance
				determineClusters(InitCenters,normStates);
				
				//Checks if a cluster is empty due to the positioning of the centers - If true, a random state from the largest cluster is assigned
				//so that the position of the cluster will change in the next iteration
				CheckedCenters = checkIfEmpty(InitCenters);
				
				//Calculate the new positions of the cluster center based on the mean value of the cluster. They will be given as initial positions in the next iteration
				LastCenters = defineNewCenterPositions(CheckedCenters);			
				
				//Compare the initial position of the centers at the beginning of each iteration with their final position
				//after the assignment of the states into clusters - Try to minimize the total distance of the centers between initial and final position to stop the iterations
				iteration++;
				double distance = 0.0;
				for(int i = 0;i<cluster_num;i++){
					distance += Math.sqrt(Math.pow((LastCenters.get(i).getVcenter() - CheckedCenters.get(i).getVcenter()),2) + Math.pow((LastCenters.get(i).getAcenter() - CheckedCenters.get(i).getAcenter()),2));
				}
				
				//Condition to stop the iterations of the algorithm
				if(distance<tolerance || iteration>=max_iterations){
					stopped = true;										//Variable that marks the end of k-means
					System.out.println("Iterations: " +iteration);
					System.out.println("Centroid Distances: " +distance);
					
					for(int i = 0;i<LastCenters.size();i++){
						LastCenters.get(i).getClusterInfo();
					}
				}
			}
			
			
			//Re-normalize the obtained clusters in order to label them based on the labelState class
			center = ReNormalizeValues(states,LastCenters);
			
			//Label the implemented Clusters based on the stored states. Each of them will be assigned with a name
			//The procedure of labelling is described in the labelState class
			LabelState labels = new LabelState(center);
			labels.labelGenOutage();
			labels.labelLineOutage();
			labels.labelLoadRate();
			
			//Condition that forces the algorithm to repeat if a cluster has not been labelled due to conflict with other cluster
			for(int i = 0;i<center.size();i++){
				if(center.get(i).getClusterLabel().equals("")){
					System.out.println("Cluster with ID "+i+" has no label. Algorithm is repeating");
					repeater = false;
					break;
				}
			}
		}
		
		//Based on the assigned label in each cluster ID, the centers are labelled for the KNN algorithm
		for(int i = 0;i<cluster_num;i++){
			if(center.get(i).getClusterID() == LastCenters.get(i).getClusterID()){
				
				LastCenters.get(i).setLabel(center.get(i).getClusterLabel());
			}
		}
		return LastCenters;
	}
		
	
	//Method that takes as input the original system states and the assigned clusters and renormalizes the values to use them to label the clusters
	public ArrayList<ClusterCenter> ReNormalizeValues(ArrayList<GroupByState> systemStates, ArrayList<ClusterCenter> KmeansCenters){
				
		ArrayList<ClusterCenter> centers = new ArrayList<ClusterCenter>();
		
		//Find again the min and max values of voltage and angle from initial states list
		double[] minmax = new double[4];
		minmax = findMinMaxVoltAngle(systemStates);
		
		//Renormalize the obtained values to their physical ones as given in database - Again the min - max function is utilized
		for(int i = 0;i<KmeansCenters.size();i++){
			
			ArrayList<GroupByState> states = new ArrayList<GroupByState>();
			for(int j = 0;j<KmeansCenters.get(i).StatesInCluster().size();j++){
				
				ArrayList<Buses> buses = new ArrayList<Buses>();
				double volt = 0;
				double angle = 0;
				int busNum = KmeansCenters.get(i).StatesInCluster().get(j).getBuses().size();
				for(int k = 0;k<busNum;k++){
					
					volt = KmeansCenters.get(i).StatesInCluster().get(j).getBuses().get(k).getVolt()*(minmax[0] - minmax[1]) + minmax[1];
					angle = KmeansCenters.get(i).StatesInCluster().get(j).getBuses().get(k).getAngle()*(minmax[2] - minmax[3]) + minmax[3];
					buses.add(new Buses(volt,angle,KmeansCenters.get(i).StatesInCluster().get(j).getBuses().get(k).getState(),KmeansCenters.get(i).StatesInCluster().get(j).getBuses().get(k).getSubID(),KmeansCenters.get(i).StatesInCluster().get(j).getBuses().get(k).getBusNum()));
				}
				
				states.add(new GroupByState(KmeansCenters.get(i).StatesInCluster().get(j).getState(),buses));
			}
			
			//Assigns the physical values of each cluster to a new arrayList that will be utilized in the labelling process
			centers.add(new ClusterCenter(KmeansCenters.get(i).getVcenter(),KmeansCenters.get(i).getAcenter(),KmeansCenters.get(i).getClusterID(),states,KmeansCenters.get(i).getClusterLabel()));
		}
		return centers;
	}
	
	
	//Defines the minimum distance between each state (the state's position is represented by the mean voltage and angle) and each cluster center. 
	//The point that has the minimum distance from a cluster center is stored in the arraylist that contains the points that belong in each cluster.
	//The distance is calculated based on the Euclidean distance
	public void determineClusters(ArrayList<ClusterCenter> center, ArrayList<GroupByState> states){
		
		for(int i = 0;i<states.size();i++){
			
			int assignedTo = 0;
			double distance = 0;
			double dist = 1e9;
			double minDistance = Math.sqrt(dist);
			int busNum = states.get(i).getBuses().size();
			double[] means = new double[2];
			
				for(int k = 0;k<busNum;k++){
					
					means[0]+=states.get(i).getBuses().get(k).getVolt();
					means[1]+=states.get(i).getBuses().get(k).getAngle();
				}
			
				for(int j = 0;j<center.size();j++){
					distance = Math.sqrt(Math.pow((center.get(j).getVcenter()- (means[0]/busNum)),2) + Math.pow((center.get(j).getAcenter()- (means[1]/busNum)),2));
					if(distance<minDistance){
						minDistance = distance;
						assignedTo = j;
					}
				}
			center.get(assignedTo).assignStateToCluster(states.get(i));
		}
	}
	
	
	//Method that defines the new position of the cluster center based on the contained states in each cluster. The position of the center is calculated based on the mean
	//value of voltage and angle from all the contained states in each cluster
	public ArrayList<ClusterCenter> defineNewCenterPositions(ArrayList<ClusterCenter> centers){
		
		ArrayList<ClusterCenter> newCenters = new ArrayList<ClusterCenter>();
		for(int i = 0;i<centers.size();i++){
			double sumV = 0;
			double sumA = 0;
			int statesInCluster = centers.get(i).StatesInCluster().size();
			int busesInState = 0;
			
			for(int j = 0;j<statesInCluster;j++){
				busesInState = centers.get(i).StatesInCluster().get(j).getBuses().size();
				for(int k = 0;k<busesInState;k++){
					
					sumV+=centers.get(i).StatesInCluster().get(j).getBuses().get(k).getVolt();
					sumA+=centers.get(i).StatesInCluster().get(j).getBuses().get(k).getAngle();
				}
			}
			
			//Utilized only when a cluster has non-zero number of states
			if(statesInCluster>0){
				double newPosV = sumV/(statesInCluster*busesInState);
				double newPosA = sumA/(statesInCluster*busesInState);
				newCenters.add(new ClusterCenter(newPosV,newPosA,i,centers.get(i).StatesInCluster(),""));
			}
		}
		return newCenters;
	}
	
	
	//Method that checks if a cluster is empty - If it is, it inserts a random state that is selected from the cluster that contains the maximum number of states
	//We chose that particular cluster as we found out that the results converge better with this procedure
	//Returns the occured centers from this procedure
	public ArrayList<ClusterCenter> checkIfEmpty(ArrayList<ClusterCenter> centers){
		
		Random randState = new Random();
		for(int i= 0;i<centers.size();i++){
			
			if(centers.get(i).StatesInCluster().isEmpty()){
				double max = 0;
				for(int j = 0;j<centers.size();j++){
					
					//Find the Cluster with the maximum number of states
					if(centers.get(j).StatesInCluster().size()>max){
						
						max = centers.get(j).StatesInCluster().size();
					}
				}
				
				for(int k = 0;k<centers.size();k++){
					if(max == centers.get(k).StatesInCluster().size()){
						int randomIndex = randState.nextInt(centers.get(k).StatesInCluster().size());	//Takes a random state index from the cluster's states
						centers.get(i).StatesInCluster().add(centers.get(k).StatesInCluster().get(randomIndex));	//Adds the selected state to the empty cluster
						
						//remove the selected state from the old cluster
						centers.get(k).StatesInCluster().remove(randomIndex);
						break;
					}
				}
			}
		}
		return centers;
	}
}