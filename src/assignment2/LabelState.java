package assignment2;

//The purpose of this class is to handle the procedure of labeling the clusters that occur from the k-means algorithm implementation. The procedure corresponds to the
//results given in the attached .pdf file, however here we will also give a small insight on how we decided to label the clusters. The class implements three functions 
//for the generator outage, line outage and load rate labels.
//For the generator outage label, we thought that when a generator is out, there will be no flow of power through the line that connects the generator bus with its adjacent.
//Therefore, the voltage drop in that line will be almost equal to zero and so the labelGenOutage calculates the voltage drop in the three lines thta connects the generator buses
//with their adjacent. The cluster that has the minimum value of voltage drop is assigned as Generator Outage
//For the line outage label, we took into account that when a line that connects to buses is disconnected, then the voltage drop between these two connected buses will be very large
//as the current must find an alternative path that involves more lines and so the voltage drop will be greater compared to a healthy position. So basically, we calculates each voltage
//drop through the lines, we summed them and the cluster that has the maximum value will be assigned as line outage
//Finally, the load rate is based on the voltage on each bus. When the load is high, more current will flow through the lines and so the losses will increase. This means that the voltage
//drop will be higher and therefore the voltage will be below the nominal value. The opposite happens when the load is low and the voltage will be higher. So basically, we found the mean
//voltage of each bus based on the states of the cluster, we summed them and the maximum value will be assigned as high load whereas the minimum as low load rate

import java.util.*;

public class LabelState {
	
	private static int cluster_num = 4;
	private String[] generators = {"_2BCC3D5923464FED9E08EB12EC388BD7","_4273A87151C0409780237BFD866C23DA","_4F715892155341C8A76534537F095B49"};	//SubID of generator buses
	private String[] subID = {"_2BCC3D5923464FED9E08EB12EC388BD7","_4273A87151C0409780237BFD866C23DA","_4F715892155341C8A76534537F095B49",
							  "_58F637D8B03A4B12A67DF2E5797F9B6A","_7324D6723635494784A4D8A9578FCE8A","_7AC1BC6CDFAF4F26A97AD322E9F5AD31",
							  "_7DD325DCEFC248989B72AAD58D3DD4E9","_95D3CD0256FB4C9DB2860CFEFA45CD57","_9D8BB8E8B5DB40F6ABF515042B7DFF97"};	//Given as: 1,2,3,4,5,6,7,8,9
	
	private String[] labels = {"High Load Rate","Generator Outage","Low Load Rate","Line Outage",""};
	private String[] connections = {"1-4","2-8","3-6","4-5","5-6","6-7","7-8","8-9","9-4"};		//Interconnected lines
	
	private ArrayList<ClusterCenter> centers;
	
	public LabelState(ArrayList<ClusterCenter> states){
		this.centers = states;
	}
	
	public void labelCluster(ClusterCenter cluster, int i){
		cluster.setLabel(labels[i]);
	}
	
	
	//Method used to label the cluster where a generator outage is observed. Basically, calculates the voltage drop between the Gen bus and the adjacent one
	//When there is generation outage, no power will flow through the line and the voltage drop between the two buses will be almost zero
	//The requested voltage drops are calculated as mean values based on the total number of states in each cluster
	public void labelGenOutage(){
		
		ArrayList<double[]> voltDif = new ArrayList<double[]>();
		for(int i= 0;i<centers.size();i++){
			double[] voltDrop = new double[generators.length];
			
			for(int j = 0;j<centers.get(i).StatesInCluster().size();j++){
				for(int k = 0;k<centers.get(i).StatesInCluster().get(j).getBuses().size();k++){
					for(int m = k+1;m<centers.get(i).StatesInCluster().get(j).getBuses().size();m++){
						
						//Buses 1 and 4
						if(centers.get(i).StatesInCluster().get(j).getBuses().get(k).getSubID().equals(subID[0]) && centers.get(i).StatesInCluster().get(j).getBuses().get(m).getSubID().equals(subID[3])){
							voltDrop[0] += voltageDifference(i,j,k,m);
							break;
						}
						
						//Buses 2 and 8
						else if(centers.get(i).StatesInCluster().get(j).getBuses().get(k).getSubID().equals(subID[1]) && centers.get(i).StatesInCluster().get(j).getBuses().get(m).getSubID().equals(subID[7])){
							voltDrop[1] += voltageDifference(i,j,k,m);
							break;
						}
						
						//Buses 3 and 6
						else if(centers.get(i).StatesInCluster().get(j).getBuses().get(k).getSubID().equals(subID[2]) && centers.get(i).StatesInCluster().get(j).getBuses().get(m).getSubID().equals(subID[5])){
							voltDrop[2] += voltageDifference(i,j,k,m);
							break;
							}
						}
					}
				}
			voltDif.add(voltDrop);
		}
		
		//Find the mean value of the voltage drop between buses 1-4, 2-8, 3-6 for all the states in each cluster
		for(int i = 0;i<voltDif.size();i++){
			double[] array = new double[generators.length];
			array = voltDif.get(i);
			for(int j = 0;j<array.length;j++){
				array[j] = array[j]/centers.get(i).StatesInCluster().size();
			}
		}
		
		//Assign the minimum voltage drops from each cluster to a new array
		double[] minVoltDrop = new double[cluster_num];
		for(int i = 0;i<voltDif.size();i++){
			double[] array = new double[generators.length];
			array = voltDif.get(i);
			double min = array[0];
			for(int j=0;j<array.length;j++){
				if(array[j]<min){
					min = array[j];
				}
			}			
			minVoltDrop[i] = min;
			System.out.println(minVoltDrop[i]);
		}
		
		//Based on the minimum obtained from the previous array, decide the label of the cluster
		double min = minVoltDrop[0];
		int index = 0;
		for(int i = 0;i<minVoltDrop.length;i++){
			if(minVoltDrop[i]<min){
				min = minVoltDrop[i];
				index = i;
			}
		}

		System.out.println("Cluster with id "+index+" is assigned as " +labels[1]);
		labelCluster(centers.get(index),1);
	}
	
	
	//Method used to label the cluster where a line outage is detected - Basically, it calculates the voltage drop between the interconnected lines given in the array of the class
	//The maximum voltage drop will indicate line outage as the current will have to find a different path that involves more lines to reach the selected bus. The voltage drop
	//is calculated as a mean value based on the number os states in each cluster
	public void labelLineOutage(){
		
		ArrayList<double[]> voltDif = new ArrayList<double[]>();
		for(int i= 0;i<centers.size();i++){
			double[] voltDrop = new double[connections.length];
			
			//Checks the connection between the lines - If connected, calculate the voltage drop
			for(int j = 0;j<centers.get(i).StatesInCluster().size();j++){
				for(int k = 0;k<centers.get(i).StatesInCluster().get(j).getBuses().size();k++){
					String IDb1 = Integer.toString(centers.get(i).StatesInCluster().get(j).getBuses().get(k).getBusNum());
					
					for(int m = k+1;m<centers.get(i).StatesInCluster().get(j).getBuses().size();m++){
						String IDb2 = Integer.toString(centers.get(i).StatesInCluster().get(j).getBuses().get(m).getBusNum());	
						
						for(int n = 0;n<connections.length;n++){

							//Line connection n
							if(connections[n].contains(IDb1) && connections[n].contains(IDb2)){
								voltDrop[n] += voltageDifference(i,j,k,m);
								break;
							}
						}
					}
				}
			}
			voltDif.add(voltDrop);
		}
		
		//Find the mean value of the voltage drop between connected buses
		for(int i = 0;i<voltDif.size();i++){
			double[] array = new double[connections.length];
			array = voltDif.get(i);
			for(int j = 0;j<array.length;j++){
				array[j] = array[j]/centers.get(i).StatesInCluster().size();
			}
		}
		
		//Assign the maximum voltage drops from each cluster to a new array
		double[] maxVoltDrop = new double[cluster_num];
		for(int i = 0;i<voltDif.size();i++){
			double[] array = new double[connections.length];
			array = voltDif.get(i);
			double max = array[0];
			for(int j=0;j<array.length;j++){
				if(array[j]>max){
					max = array[j];
				}
			}			
			maxVoltDrop[i] = max;
			System.out.println(maxVoltDrop[i]);
		}
		
		//Based on the maximum obtained from the previous array, decide the label of the cluster
		double max = maxVoltDrop[0];
		int index = 0;
		for(int i = 0;i<maxVoltDrop.length;i++){
			if(maxVoltDrop[i]>max){
				max = maxVoltDrop[i];
				index = i;
			}
		}

		System.out.println("Cluster with id "+index+" is assigned as " +labels[3]);
		labelCluster(centers.get(index),3);
	}
	
	
	//Method that labels the remaining clusters. Based on voltage on each bus, decide the label for the high and low load rate. The voltage on each bus is specified as a mean
	//value from the states contained in each cluster. When high load rate, the mean voltage on each bus will be be very low as there will be increased losses in the lines. On 
	//the other hand, in low load rate the some substations will have increased voltage. So the maximum mean voltage in each bus will indicate low load rate and the minimum
	//will indicate the high load rate situation
	public void labelLoadRate(){
		
		ArrayList<double[]> voltages = new ArrayList<double[]>();
		for(int i= 0;i<centers.size();i++){
			double[] BusVoltage = new double[subID.length];
			
			//Define mean voltage of each bus
			for(int j = 0;j<centers.get(i).StatesInCluster().size();j++){
				for(int k = 0;k<centers.get(i).StatesInCluster().get(j).getBuses().size();k++){
					
					BusVoltage[k] += centers.get(i).StatesInCluster().get(j).getBuses().get(k).getVolt();
				}
			}
			
			for(int k = 0;k<BusVoltage.length;k++){
				 BusVoltage[k] = BusVoltage[k]/centers.get(i).StatesInCluster().size();
			}
			voltages.add(BusVoltage);
		}
		
		//From the mean voltage of each bus, define the mean voltage of the whole system
		double[] meanVolt = new double[cluster_num];
		for(int i = 0;i<voltages.size();i++){
			double[] sumsV = voltages.get(i);
			for(int j = 0;j<voltages.get(i).length;j++){
				
				meanVolt[i]+=sumsV[j];
			}
			meanVolt[i] = meanVolt[i]/subID.length;
			System.out.println(meanVolt[i]);
		}
		
		//Find the max and min value of the system's mean voltage - Max = Low load and Min = High Load
		int indexMax = 0;
		int indexMin = 0;
		double min = meanVolt[0];
		double max = meanVolt[0];
		for(int i = 0;i<meanVolt.length;i++){
			if(meanVolt[i]>max){
				max = meanVolt[i];
				indexMax = i;
			}
			
			if(meanVolt[i]<min){
				min = meanVolt[i];
				indexMin = i;
			}
		}
		
		System.out.println("Cluster with id "+indexMax+" is assigned as " +labels[2]);
		System.out.println("Cluster with id "+indexMin+" is assigned as " +labels[0]);
		labelCluster(centers.get(indexMax),2);
		labelCluster(centers.get(indexMin),0);
	}
	
	
	//Method that calculates the voltage difference between two buses - i corresponds to the cluster ID, j to the state in the cluster
	//and k and m are the interconnected buses based on the given scheme
	//Returns the voltage difference
	public double voltageDifference(int i, int j, int k, int m){
		
		double voltDif = 0;
		
		double Gvolt = (centers.get(i).StatesInCluster().get(j).getBuses().get(k).getVolt())*Math.cos(centers.get(i).StatesInCluster().get(j).getBuses().get(k).getAngle()*2*Math.PI/360);
		double Gang = (centers.get(i).StatesInCluster().get(j).getBuses().get(k).getVolt())*Math.sin(centers.get(i).StatesInCluster().get(j).getBuses().get(k).getAngle()*2*Math.PI/360);
		double Bvolt = (centers.get(i).StatesInCluster().get(j).getBuses().get(m).getVolt())*Math.cos(centers.get(i).StatesInCluster().get(j).getBuses().get(m).getAngle()*2*Math.PI/360);
		double Bang = (centers.get(i).StatesInCluster().get(j).getBuses().get(m).getVolt())*Math.sin(centers.get(i).StatesInCluster().get(j).getBuses().get(m).getAngle()*2*Math.PI/360);
		voltDif = Math.sqrt(Math.pow((Gvolt - Bvolt),2) + Math.pow((Gang - Bang),2));;
		
		return voltDif;
	}
}