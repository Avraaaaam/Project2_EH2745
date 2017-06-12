package assignment2;

//Basically, the main method implements the GUI of the assignment as well as handles the cases of when a button is pressed etc
//Allows users to add their credentials, login to database, specify database name and tables, add parameters important for the k-means and KNN algorithms
//and implement the algorithms correspondingly

import java.util.*;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import javax.swing.table.*;



public class MainMethod extends JFrame{
	 
	    private JTextField userTextField;
	    private JTextField usertxt;
	    private JTextField passTextField;
	    private JPasswordField passtxt;
	    private JTextField dbtxt;
	    private JTextField dbnametxt;
	    private JTextField subtablestxt;
	    private JTextField learntablestxt;
	    private JTextField testtablestxt;
	    private JTextField kTextField;
	    private JTextField numk;
	    private JTextField tolfield;
	    private JTextField knntxt;
	    private JTextField knnNum;
	    
	    private JLabel DBlabel;
	    private JLabel learnlabel;
	    private JLabel testlabel;
	    private JLabel sublabel;
	    private JLabel klabel;
	    private JLabel tollabel;
	    private JLabel knnlabel;
	    
	    private JButton kmeans;
	    private JButton knn;
	    
	    
	    private static String USER = "root";
	    private static String PASS = "abi10539";
	    private static int cluster_num = 4;
	    private static String learnSet = "measurements";
	    private static String testSet = "analog_values";
	    private static String subs = "substations";
	    private static String database = "power_system";
	    private static int neighbors = 5;
	    private static int iterations = 100;
	    private String[] labels = {"High Load Rate","Generator Outage","Low Load Rate","Line Outage"};
	    
	    
	    ReadFromDatabase temp;
	    ArrayList<GroupByState> SystemStates = new ArrayList<GroupByState>();
		ArrayList<ArrayList<String>> substations = new ArrayList<ArrayList<String>>();
		ArrayList<GroupByState> TestStates = new ArrayList<GroupByState>();
		ArrayList<ClusterCenter> centers = new ArrayList<ClusterCenter>();
		ArrayList<GroupByState> normTestStates = new ArrayList<GroupByState>();
		
		boolean kEnabled = false;
		boolean iterEnabled = false;
		boolean neighborsEnab = false;
	    
	
	public MainMethod(){
		
		setLayout(new FlowLayout());
		
		//Create the main frame for the credentials, k and so on
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = (int)(screenSize.width*0.5);
		int height = (int)(screenSize.height*0.5);
	    setBounds(0,0,width,height);
	    
	  //---------------------------------------------------------------------//
	    
        //Textfield for username for the SQL login
        userTextField = new JTextField("Username", 150);
        userTextField.setEditable(false);
        userTextField.setFont(new Font("Calibri",Font.BOLD, 16));
        userTextField.setForeground(Color.WHITE);
        userTextField.setBackground(Color.BLACK);
        userTextField.setHorizontalAlignment(JTextField.CENTER);
 		add(userTextField);
 		
 		usertxt = new JTextField("root", 15);
		usertxt.setFont(new Font("Calibri",Font.PLAIN, 16));
		usertxt.setEnabled(true);
		add(usertxt);
		
		
		//Enter USERNAME action handler
		usertxt.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				
				if(e.getSource() == usertxt){
					
					USER = e.getActionCommand();
					JOptionPane.showMessageDialog(null, "Username inserted successfully");
				}}});

 		
		//Textfield for password for the SQL login
		passTextField = new JTextField("Password", 150);
        passTextField.setEditable(false);
        passTextField.setFont(new Font("Calibri",Font.BOLD, 16));
        passTextField.setForeground(Color.WHITE);
        passTextField.setBackground(Color.BLACK);
        passTextField.setHorizontalAlignment(JTextField.CENTER);
 		add(passTextField);
 		
 		passtxt = new JPasswordField("abi10539", 15);
		passtxt.setFont(new Font("Calibri",Font.PLAIN, 16));
		passtxt.setEnabled(true);
		add(passtxt);
		
		
		//Enter PASSWORD action handler
		passtxt.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				
				if(e.getSource() == passtxt){
					
					PASS = e.getActionCommand();
					JOptionPane.showMessageDialog(null, "Password inserted successfully");
				}}});
		
		
		
		//-----------------------------Database Login------------------------------------//
		
		//Textfield for database settings e.g database name and tables
		dbtxt = new JTextField("Database Settings - Database Name and Tables", 300);
        dbtxt.setEditable(false);
        dbtxt.setFont(new Font("Calibri",Font.BOLD, 16));
        dbtxt.setForeground(Color.WHITE);
        dbtxt.setBackground(Color.BLACK);
        dbtxt.setHorizontalAlignment(JTextField.CENTER);
 		add(dbtxt);
 		
 		
 		//Database Name
 		DBlabel = new JLabel("Database Name: ");
 		DBlabel.setFont(new Font("Calibri",Font.PLAIN, 16));
 		dbnametxt = new JTextField("power_system", 15);
 		dbnametxt.setFont(new Font("Calibri",Font.PLAIN, 16));
 		dbnametxt.setEnabled(true);
		
 		JPanel panel = new JPanel(new BorderLayout());
 		panel.add(DBlabel,BorderLayout.WEST);
 		panel.add(dbnametxt,BorderLayout.CENTER);
 		add(panel);
 		
 		
 		//Login to database based on entered credentials and given database name
 		dbnametxt.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				
				if(e.getSource() ==dbnametxt){

					database = e.getActionCommand();
					JOptionPane.showMessageDialog(null, "Database name inserted");
					
					//Login to database based on the given name and credentials
					temp = new ReadFromDatabase(database,USER,PASS);
					
		}}});
		
 		
 		//Substations Table
 		sublabel = new JLabel("Substations:         ");
		sublabel.setFont(new Font("Calibri",Font.PLAIN, 16));
 		subtablestxt = new JTextField("substations", 15);
 		subtablestxt.setFont(new Font("Calibri",Font.PLAIN, 16));
 		subtablestxt.setEnabled(true);
		
		JPanel panel4 = new JPanel(new BorderLayout());
 		panel4.add(sublabel,BorderLayout.WEST);
 		panel4.add(subtablestxt,BorderLayout.CENTER);
 		add(panel4);
 		
 		
 		//Read data from substations table - ReadFromDatabase checks if the table exists in the database
 		subtablestxt.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				
				if(e.getSource() == subtablestxt){

					subs = e.getActionCommand();
					
					//Read the data from the substations table in SQL - If the inserted name is false, checkTable function from
					//class ReadFromDatabase will detect it
					substations = temp.readFromTable(subs);
					
		}}});
 		
 		
		//Learning Set of values 
		learnlabel = new JLabel("Learning Set:       ");
		learnlabel.setFont(new Font("Calibri",Font.PLAIN, 16));
 		learntablestxt = new JTextField("measurements", 15);
 		learntablestxt.setFont(new Font("Calibri",Font.PLAIN, 16));
 		learntablestxt.setEnabled(true);
		
		JPanel panel2 = new JPanel(new BorderLayout());
 		panel2.add(learnlabel,BorderLayout.WEST);
 		panel2.add(learntablestxt,BorderLayout.CENTER);
 		add(panel2);
 		
 		
 		//Read data from the learning set of values - The data will be used for the K-means algorithm afterwards
 		learntablestxt.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				
				if(e.getSource() == learntablestxt){

					learnSet = e.getActionCommand();
					
					//Read the data from the measurements table in SQL - If the inserted name is false, checkTable function from
					//class ReadFromDatabase will detect it
					SystemStates = temp.createStates(learnSet,substations.size());
					
		}}});
 		
 		
 		//Test set of values
 		testlabel = new JLabel("Test Set:              ");
		testlabel.setFont(new Font("Calibri",Font.PLAIN, 16));
 		testtablestxt = new JTextField("analog_values", 15);
 		testtablestxt.setFont(new Font("Calibri",Font.PLAIN, 16));
 		testtablestxt.setEnabled(true);
		
		JPanel panel3 = new JPanel(new BorderLayout());
 		panel3.add(testlabel,BorderLayout.WEST);
 		panel3.add(testtablestxt,BorderLayout.CENTER);
 		add(panel3);
 		
 		
 		//Read data from the learning set of values - The data will be used for the KNN algorithm afterwards in combination with the values from the learnSet
 		testtablestxt.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				
				if(e.getSource() == testtablestxt){

					testSet = e.getActionCommand();
					
					//Read the data from the analog_values table in SQL - If the inserted name is false, checkTable function from
					//class ReadFromDatabase will detect it
					TestStates = temp.createStates(testSet,substations.size());
					numk.setEnabled(true);
					tolfield.setEnabled(true);
					
		}}});
 		
 		
 		
 		//----------------------------K-means Section-------------------------------------//
 		
 		//Specify number of clusters and iterations
 		kTextField = new JTextField("K-Means Algorithm - Specifications", 150);
 		kTextField.setEditable(false);
 		kTextField.setFont(new Font("Calibri",Font.BOLD, 16));
 		kTextField.setForeground(Color.WHITE);
 		kTextField.setBackground(Color.BLACK);
 		kTextField.setHorizontalAlignment(JTextField.CENTER);
 		add(kTextField);
 		
 		//Cluster Number, Max iterations	
 		klabel = new JLabel("Clusters:");
		klabel.setFont(new Font("Calibri",Font.PLAIN, 16));
 		numk = new JTextField("4", 5);
 		numk.setFont(new Font("Calibri",Font.PLAIN, 16));
 		numk.setEnabled(false);
		
		JPanel kpanel = new JPanel(new BorderLayout());
 		kpanel.add(klabel,BorderLayout.WEST);
 		kpanel.add(numk,BorderLayout.CENTER);
 		add(kpanel);
 		
 		
 		//Define the number of clusters to perform the K-means algorithm
 		numk.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				
				if(e.getSource() == numk){
					try{
						cluster_num = Integer.parseInt(e.getActionCommand());
						JOptionPane.showMessageDialog(null, "Let's see what you can do with " +cluster_num+" Clusters");
						
					}catch(NumberFormatException e1){
						JOptionPane.showMessageDialog(null, "Gotta place a number here, mate!");
					}
		}}});
 		
 		
 		tollabel = new JLabel("Iterations:");
		tollabel.setFont(new Font("Calibri",Font.PLAIN, 16));
 		tolfield = new JTextField("50", 5);
 		tolfield.setFont(new Font("Calibri",Font.PLAIN, 16));
 		tolfield.setEnabled(false);
		
 		JPanel tolpanel = new JPanel(new BorderLayout());
 		tolpanel.add(tollabel,BorderLayout.WEST);
 		tolpanel.add(tolfield,BorderLayout.EAST);
 		add(tolpanel);
 		
 		
 		//Define the maximum number of iterations that the algorithm will perform
 		tolfield.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				
				if(e.getSource() == tolfield){
					try{
						iterations = Integer.parseInt(e.getActionCommand());
						JOptionPane.showMessageDialog(null, "Maximum number of iterations: " +iterations);
						kmeans.setEnabled(true);
						
					}catch(NumberFormatException e1){
						JOptionPane.showMessageDialog(null, "Gotta place a number here, mate!");
					}
		}}});
 		
 		
 		//Button for executing the algorithm
 		kmeans = new JButton("Launch K-Means Algorithm");
 		kmeans.setFont(new Font("Calibri",Font.BOLD,15));
 		kmeans.setBackground(Color.BLACK);
 		kmeans.setForeground(Color.WHITE);
 		kmeans.setEnabled(false);
 		add(kmeans);
 		
 		
 		//Upon definition of the number of clusters and the max number of iterations, the k-means algorithm will be performed. As output there will be
 		//a new frame with the sorted states based on the already specified cluster label
 		kmeans.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				
				//Create a K-means object to implement the algorithm. The object'constructor will take as input the number of clusters k
				//and the maximum number of iterations that can be performed
				Kmeans newClustering = new Kmeans(cluster_num,iterations);
				centers = newClustering.implementKmeans(SystemStates);
				
				JFrame frame1  = new JFrame();
				frame1.setSize (950,300);
				frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame1.setTitle("K-Means Algorithm Cluster Assignment");
				frame1.setVisible(true);
				
				
				setLayout(new BorderLayout());
				
				JPanel outputPanel = new JPanel();
				outputPanel.setPreferredSize(new Dimension((int)(width*0.91),(int)(height*0.81)));
				
				//Presentation of the assigned states in the clusters
				for(int i = 0;i<centers.size();i++){
					
					String[][] states = new String[centers.get(i).StatesInCluster().size()][1];
					String[] label = new String[1];
					label[0] = "State ID";
					for(int j = 0;j<states.length;j++){
						states[j][0] = Integer.toString(centers.get(i).StatesInCluster().get(j).getState());
					}
					
					JTable table = new JTable(states,label);
					table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
					table.setFont(new Font("Calibri",Font.PLAIN, 14));
					
					TableColumn column = null;
					column = table.getColumnModel().getColumn(0);
				    column.setPreferredWidth(70);
					
					table.setRowHeight(25);
					
					JScrollPane scrollPane = new JScrollPane(table);
					scrollPane.setFont(new Font("Calibri",Font.PLAIN, 50));
					scrollPane.setPreferredSize( new Dimension((int)(0.15*width),(int)(0.5*height)));
					
					//Add the textfield that describes what is every group of states based on their cluster label
					JTextField tabletxt = new JTextField(""+centers.get(i).getClusterLabel());
					tabletxt.setEditable(false);
			        tabletxt.setFont(new Font("Calibri",Font.BOLD, 16));
			        tabletxt.setForeground(Color.WHITE);
			        tabletxt.setBackground(Color.BLACK);
			        tabletxt.setHorizontalAlignment(JTextField.CENTER);
			 		outputPanel.add(tabletxt);
					
					outputPanel.add(scrollPane);
					frame1.add(outputPanel);
				}
				
				knnNum.setEnabled(true);
					
		}});
 		
 		
 		
 		//----------------------------K-NN Section-------------------------------------//
 		//Specify number of clusters, iterations and tolerance
 		knntxt = new JTextField("KNN Algorithm - Specifications", 150);
 		knntxt.setEditable(false);
 		knntxt.setFont(new Font("Calibri",Font.BOLD, 16));
 		knntxt.setForeground(Color.WHITE);
 		knntxt.setBackground(Color.BLACK);
 		knntxt.setHorizontalAlignment(JTextField.CENTER);
 		add(knntxt);
 		
 		//Specify the number of neighbors utilized in the algorithm
 		knnlabel = new JLabel("Number of Neighbors:");
 		knnlabel.setFont(new Font("Calibri",Font.PLAIN, 16));
 		knnNum = new JTextField("5", 5);
 		knnNum.setFont(new Font("Calibri",Font.PLAIN, 16));
 		knnNum.setEnabled(false);
		
		JPanel knnpanel = new JPanel(new BorderLayout());
 		knnpanel.add(knnlabel,BorderLayout.WEST);
 		knnpanel.add(knnNum,BorderLayout.CENTER);
 		add(knnpanel);
 		
 		
 		//Define the maximum number of neighbors that the KNN algorithm will perform
 		knnNum.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				
				if(e.getSource() == knnNum){
					try{
						neighbors = Integer.parseInt(e.getActionCommand());
						JOptionPane.showMessageDialog(null, "Number of Neighbors Chosen: " +neighbors);
						knn.setEnabled(true);
						
					}catch(NumberFormatException e1){
						JOptionPane.showMessageDialog(null, "Gotta place a number here, mate!");
					}
		}}});
 		
 		
 		//Button for executing the algorithm
 		knn = new JButton("Launch KNN Algorithm");
 		knn.setFont(new Font("Calibri",Font.BOLD,15));
 		knn.setBackground(Color.BLACK);
 		knn.setForeground(Color.WHITE);
 		knn.setEnabled(false);
 		add(knn);
 		
 		
 		
 		//After the implementation of the K-Means algorithm, the test set has been inserted before and therefore the implementation of the KNN algorithm
 		//will take place. The output in the new frame will give which states will be assigned to which cluster based on the cluster label
 		knn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				
				//Create a K-means object to implement the algorithm. The object'constructor will take as input the number of clusters k
				//and the maximum number of iterations that can be performed
				Kmeans normalizer = new Kmeans(cluster_num,iterations);
				normTestStates = normalizer.NormalizeValues(TestStates);
				
				
				KNN knn = new KNN(centers,normTestStates,neighbors,labels);
				
				
				JFrame frame2  = new JFrame();
				frame2.setSize (850,500);
				frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame2.setTitle("KNN Cluster Assignment");
				frame2.setVisible(true);
				
				
				setLayout(new BorderLayout());
				
				JPanel outputPanel2 = new JPanel();
				outputPanel2.setPreferredSize(new Dimension((int)(width*0.91),(int)(height*0.81)));
				
				//Presentation of the assigned states in the clusters
				String[] label = new String[1];
				label[0] = "State ID";
				for(int j = 0;j<labels.length;j++){
					
					int count = 0;
					
					for(int i = 0;i<knn.assignedStates.size();i++){
						if(labels[j].equals(knn.assignedStates.get(i).label)){
							
							count++;
						}
					}
					
					int k = 0;
					int m = 0;
					String[][] knnstates = new String[count][1];
					while(k<knn.assignedStates.size()){
						if(labels[j].equals(knn.assignedStates.get(k).label)){
						
							knnstates[m][0] = Integer.toString(knn.assignedStates.get(k).state);
							m++;
							if(m == knnstates.length){
								break;
							}
						}
						k++;
					}
						
					JTable table2 = new JTable(knnstates,label);
					table2.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
					table2.setFont(new Font("Calibri",Font.PLAIN, 14));
					
					TableColumn column1 = null;
					column1 = table2.getColumnModel().getColumn(0);
				    column1.setPreferredWidth(70);
					
					table2.setRowHeight(25);
					
					JScrollPane scrollPane2 = new JScrollPane(table2);
					scrollPane2.setFont(new Font("Calibri",Font.PLAIN, 50));
					// set window size and limits
					scrollPane2.setPreferredSize( new Dimension((int)(0.15*width),(int)(0.5*height)));
					
					//Add the textfield that describes what is every group of states based on their cluster label
					JTextField knntabletxt = new JTextField("Assigned to "+labels[j]+" Cluster");
					knntabletxt.setEditable(false);
					knntabletxt.setFont(new Font("Calibri",Font.BOLD, 16));
					knntabletxt.setForeground(Color.WHITE);
					knntabletxt.setBackground(Color.BLACK);
					knntabletxt.setHorizontalAlignment(JTextField.CENTER);
			 		outputPanel2.add(knntabletxt);
					
					outputPanel2.add(scrollPane2);
					frame2.add(outputPanel2);
				}
		}});
 		
	}
 		
 		//-----------------------------------------------------------------------------------//
 		
 		
 		

	public static void main(String[] args) {
		
		Test t = new Test();
		t.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		t.setSize(500, 500);
		t.setResizable(false);
		t.setVisible(true);
	}
}
