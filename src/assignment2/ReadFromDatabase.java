package assignment2;

//The class is responsible for reading the data from the specified SQL database for the three tables that exist in the database. Among the functions are the ones that check the existence
//of the given database name as it may vary as well as the existence of the given table in GUI. The most important for the given assignment is the create states method that groups all the data
//of the given tables based on their state ID. Basically, each state contains its ID as well as a list of all the voltage and angles of the buses as well as their serial number and ID.


import java.sql.*;
import java.util.*;

public class ReadFromDatabase {
	
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/";
	
	private String USER;
	private String PASS;
	
	private String database;
	private Connection DatabaseConnect = null;
	private Statement stmt = null;
	private PreparedStatement pst = null;
	
	
	
	//Initialize the created object - Checks if the specified database exists - Connects to database
	public ReadFromDatabase(String dataName, String USER, String PASS){
		
		this.USER = USER;
		this.PASS = PASS;
		this.database = dataName;
		
		try{
			Class.forName(JDBC_DRIVER);
			System.out.println("Connecting to database...");
			DatabaseConnect = DriverManager.getConnection(DB_URL, USER, PASS);
			
			//Checks if the specified database exists in MySQL
			if(checkDBIfexists(database)){
			
				//Connects to the database specified by the string given as attribute
				DatabaseConnect = DriverManager.getConnection(DB_URL + database, USER, PASS);
				stmt = DatabaseConnect.createStatement();
			}
			
			System.out.println("Connection was successful!");
			
		}catch(SQLException se){
			//Handle errors for JDBC
			System.out.println("Error while logging to database. Please check your credentials and try again");
			//se.printStackTrace();
			System.exit(0);
		}catch(Exception e){
			//Handle errors for Class.forName
			e.printStackTrace();
		}
	}
	
	
	//Method that reads data from a specified table in an already created database. The method reads all the columns of the table regardless of whether they are utilized or not
	public ArrayList<ArrayList<String>> readFromTable(String tableName){
		
		ArrayList<ArrayList<String>> SQLobjects = new ArrayList<ArrayList<String>>();
		if(checkTableExist(tableName)){
		
			try{
				String sqlSelect = "SELECT * FROM " +tableName;
				
				//ResultSet is used in order to print the elements of the data table
				ResultSet rset = stmt.executeQuery(sqlSelect);
				ResultSetMetaData rsmd = rset.getMetaData();
				int columnsNumber = rsmd.getColumnCount();
				
				while(rset.next()){
					
					ArrayList<String> elemList = new ArrayList<String>();
					for(int i = 1;i<columnsNumber+1;i++){
							elemList.add(rset.getString(i));
							
					}
					SQLobjects.add(elemList);
				}
				rset.close();
				
				System.out.println(tableName+" table was read successfully!");
				
				
			}catch(SQLException sqle){
				//JDBC errors handler
				System.out.println(" SQL Error occured in the system");
				sqle.printStackTrace();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		else{
			System.out.println("Table with name " +tableName+" doesn't exist in database. Program terminates.");
			System.exit(0);
		}	
		return SQLobjects;
	}
	
	
	//Method that compares ID of substation and the state of each case and couples voltage with angle for each substation in the corresponding state as well as the ID of the substation
	//Then the buses class is utilized for the grouping of the data as well as the GroupByState class to group in each state all the contained buses with their values
	public ArrayList<GroupByState> createStates(String tableName, int numSub){
		
		ArrayList<GroupByState> SystemStates = new ArrayList<GroupByState>();
		ArrayList<Buses> newBuses = new ArrayList<Buses>();
		ArrayList<ArrayList<String>> elements = new ArrayList<ArrayList<String>>();
		elements = readFromTable(tableName);
		int busEnum = 0;
		
		//Incorporate in the same bus the necessary values of volt,angle,subID,state and the bus number according to topology
		for(int i = 0;i<elements.size();i++){
			busEnum++;
			for(int j = i+1;j<elements.size();j++){
				if((elements.get(i).get(2)).equals(elements.get(j).get(2)) && (elements.get(i).get(4)).equals(elements.get(j).get(4))){
					
					double volt = Double.parseDouble(elements.get(i).get(3));
					double angle = Double.parseDouble(elements.get(j).get(3));
					int state = Integer.parseInt(elements.get(i).get(2));
					newBuses.add(new Buses(volt,angle,state,elements.get(i).get(4),busEnum));
					elements.remove(j);
					if(busEnum == numSub){
						busEnum = 0;
					}
					break;
				}
			}
		}
		
		
		//From the newly created arraylist of buses, we group the buses based on their state number. We create a new object of the GroupByState
		//class that takes as input the state number and an arraylist of the contained buses
		int state = newBuses.get(0).getState();
		int totalStates = newBuses.size()/numSub;
		int index = 0;
		for(int i = 0;i<totalStates;i++){
			ArrayList<Buses> stateBuses = new ArrayList<Buses>();
			for(int j = 0;j<newBuses.size();j++){
				
				if(state == newBuses.get(j).getState()){
					
					stateBuses.add(newBuses.get(j));
					if(stateBuses.size() == numSub){
						index = j;
					}
				}
			}
			
			SystemStates.add(new GroupByState(state,stateBuses));
			if(index<newBuses.size()-1){
				state = newBuses.get(index+1).getState();
			}
		}
		
		System.out.println("Total number of states: "+SystemStates.size());
		return SystemStates;
	}
	
	
	//Method that prints all the states of a given table. Counts the total states for all buses based on certain input - Print is used mainly for debug
	public void printStates(ArrayList<GroupByState> states){
		
		for(int i = 0;i<states.size();i++){			
			
			System.out.println("State: "+states.get(i).getState());
			for(int j = 0;j<states.get(i).getBuses().size();j++){
				
				System.out.print(states.get(i).getBuses().get(j).getVolt()+"\t");	//Voltage
				System.out.print(states.get(i).getBuses().get(j).getAngle()+"\t");	//Angle
				System.out.println(states.get(i).getBuses().get(j).getSubID());		//Substation ID
			
			}
		}
	}
	
	
	//Simple method that checks if a specified table exists in database - Returns true if successful
	public boolean checkTableExist(String tableName){
		
		boolean tableExists = false;
		
		try{
			ResultSet resultSet = DatabaseConnect.getMetaData().getTables(null, null, tableName, null);
			if(resultSet.next()){
				tableExists = true;
				return tableExists;
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		return tableExists;
	}
	
	
	//Simple method that checks if a database exists - Returns a true value if successful
	public boolean checkDBIfexists(String DBname){
		
		try{
			ResultSet resultSet = DatabaseConnect.getMetaData().getCatalogs();
	        while (resultSet.next()){
	          String databaseName = resultSet.getString(1);
	            if(databaseName.equals(DBname)){
	                return true;
	            }
	        }
	        resultSet.close();
	    }
	    catch(Exception e){
	        e.printStackTrace();
	    }
	    return false;
	}
}