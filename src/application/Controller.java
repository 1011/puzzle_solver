package application;


import java.util.Calendar;
import java.util.Random;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Controller {
	
	@FXML TextArea Puzzle;
	@FXML TextField Value_Display;
	@FXML TextField Compute_Display;
	@FXML MenuButton Size_Selector;
	@FXML Button Show_Puzzle;
	@FXML Button Gen_New_Puzzle;
	@FXML Button Show_Solution;
	@FXML Button Hill_Climbing;
	@FXML Button Sim_Annealing;
	@FXML Button File_Selector;
	@FXML Button Own_Approach;
	@FXML TextField Hill_Climbs;
	@FXML TextField Hill_Climb_Iterations;
	@FXML TextField Probability;
	@FXML TextField Sim_Annealing_Iterations;
	@FXML TextField Temperature;
	@FXML TextField Decay_Rate;
	@FXML TextField Own_Approach_Iterations;
	
	private int PUZZLE_SIZE;
	private int[][] main_puzzle = null;
	private int[][] visited = null;
	
	public void setPuzzle(int[][] target) {
		main_puzzle = target;
	}
	
	public int[][] getPuzzle() {
		return main_puzzle;
	}
	
	public void set_visited(int[][] target) {
		visited = target;
	}
	
	/*
	 * Returns private global variable visited, which is a matrix of the visited spots during a 
	 * BFS of the puzzle
	 * */
	public int[][] get_visited() {
		return visited;
	}
	/*
	 * Method changes the size of the puzzle board based on the size selector on the GUI. 
	 * Sets the global variables and the text to the GUI 
	 * */
	public void change_size(int size) {
		PUZZLE_SIZE = size;
		Size_Selector.setText(Integer.toString(size));
	}
	
	/*
	 * Event handlers for the menu items to change the Puzzle size in the GUI
	 * */
	public void setSize5(ActionEvent event) {
		change_size(5);
	}
	public void setSize7(ActionEvent event) {
		change_size(7);
	}
	public void setSize9(ActionEvent event) {
		change_size(9);
	}
	public void setSize11(ActionEvent event) {
		change_size(11);
	}
	
	/*
	 * Returns the size of the current puzzle instance, so that a new puzzle can be created.
	 * Do not use it for evaluation!! Use puzzle.length instead
	 * */
	public int puzzle_size() {
		return PUZZLE_SIZE;
	}
	
	/*
	 * Generates a puzzle by checking for errors and sets the compute time and displays the value.
	 * Calls make_puzzle() to create the puzzle of given size
	 * */
	public void gen_puzzle(ActionEvent event) {
		if (puzzle_size() == 0){
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setContentText("No size selected! Please select a size from the dropdown");
			alert.showAndWait();
			return;
		}
		
		long start = Calendar.getInstance().getTimeInMillis();
		main_puzzle = make_puzzle(puzzle_size());
		display_puzzle(main_puzzle);
		compute_time(start);
		
	}
	
	/*
	 * Creates a puzzle given an input size and sets it to main_puzzle which is used to display it
	 * */
	public int[][] make_puzzle(int size){
		int[][] puzzle = new int[size][size];
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				if ((x == size - 1) && (y == size - 1)) {
					puzzle[x][y] = 0;
				} else {
					puzzle[x][y] = check_legal_spot(size, x, y);
				}
			}
		}
		return puzzle;
	}

	/*
	 * Displays the puzzle in the TextArea by traversing through the 2 dimensional matrix
	 * */
	private void display_puzzle(int[][] puzzle) {
		if(puzzle == null) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setContentText("Puzzle does not exist! Please generate a puzzle first");
			alert.showAndWait();
			return;
		}
		
		Puzzle.setText("");
		for(int i = 0; i < puzzle.length; i++) {
			for(int j = 0; j < puzzle.length; j++) {
				if(i == puzzle.length - 1 && j == i) {
					Puzzle.appendText(Integer.toString(0));
				}
				else {
					Puzzle.appendText(Integer.toString(puzzle[i][j]) + "\t");
				}
			}
			Puzzle.appendText("\n");
		}
	}
	
	public void show_puzzle(ActionEvent event) {
		if (getPuzzle() == null) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Puzzle does not exist.");
			alert.setContentText("Please generate a puzzle before showing it.");
			alert.showAndWait();
			return;
		}
		
		display_puzzle(getPuzzle());
	}
	
	/* This resets the solution matrix from previous usage 
	 * 
	 * Not sure if the size to reset is correct, need to check about solution resizing
	 * 
	 */
	public int[][] initmatrix(int[][] solution){
		int n = solution.length;
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++) {
				solution[i][j] = -1;
			}
		}
		return solution;
	}
	
	/*
	 * Takes a puzzle matrix as a parameter and builds a solution matrix, by traversing the nodes of the first matrix
	 */
	
	public int[][] findsolution(int[][] puzzle) throws Exception {
		int[][] visited = new int[puzzle_size()][puzzle_size()];
		initmatrix(visited);	//reset values in solution matrix
		int n = puzzle.length;
		Node child = null;
		
		Node start = new Node(0,0,puzzle[0][0]);
		visited[0][0] = 0;
		Queue monitor = new Queue(start);
		
		while(monitor.peek() != null) {
			//
			Node node = monitor.dequeue();
			int index = node.index;
			
			if (node.y >= index) {	//check North of node
				if(visited[node.x][node.y - index] == -1 || visited[node.x][node.y - index] > node.depth+1) {
					child = new Node(node.x, node.y - index, puzzle[node.x][node.y-index],node);
					visited[child.x][child.y]= child.depth;
					monitor.enqueue(child);
				}
			}
			if(node.y + index < n) {	//check South of node
				if(visited[node.x][node.y + index] == -1 || visited[node.x][node.y + index] > node.depth+1) {
					child = new Node(node.x, node.y + index, puzzle[node.x][node.y+index],node);
					visited[child.x][child.y]= child.depth;
					monitor.enqueue(child);
				}
			}
			if(node.x >= index) {	//check West of node
				if(visited[node.x - index][node.y] == -1 || visited[node.x - index][node.y] > node.depth+1) {
					child = new Node(node.x - index, node.y , puzzle[node.x - index][node.y],node);
					visited[child.x][child.y]= child.depth;
					monitor.enqueue(child);
				}
			}
			if(node.x +index < n) {	//check East of node
				if(visited[node.x + index][node.y] == -1 || visited[node.x + index][node.y] > node.depth+1) {
					child = new Node(node.x + index, node.y , puzzle[node.x + index][node.y],node);
					visited[child.x][child.y]= child.depth;
					monitor.enqueue(child);
				}
			}
				
		}
		set_visited(visited);
		return visited;
	}
	
	/*
	 * Prints out the Solution Matrix on the GUI, replaces -1 in visited matrix with "X"
	 */
	public void show_solution(ActionEvent event) throws Exception {

		if(main_puzzle == null) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setContentText("Puzzle does not exist! Please generate a puzzle first");
			alert.showAndWait();
			return;
		}
		try {
			findsolution(main_puzzle);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		visited = get_visited();
		Puzzle.setText("");
		for(int i = 0; i < visited.length; i++) {
			for(int j = 0; j < visited.length; j++) {
				if(j == 0 && j == i) {
					Puzzle.appendText(Integer.toString(0) + "\t");
				}
				else if(visited[i][j] == -1) {
					Puzzle.appendText("X\t");
				}
				else {
					Puzzle.appendText(Integer.toString(visited[i][j]) + "\t");
				}
			}
			Puzzle.appendText("\n");
		}

		get_value(main_puzzle);
	}
	
	
	
	/*
	 * Hill climb controller checks the inputs to see what kind of hill climbing is required. There are 3 possibilities:
	 * 1. Basic hill climbing - where only the number of iterations is specified
	 * 2. Random restarts - where the number of iterations and number of climbs is required as an input
	 * 3. Random walks - where the probability is used as an input along with iterations
	 * After checking what inputs are provided and what variation of hill climbing needs to be performed it calls the 
	 * corresponding methods.
	 * */
	public void hill_climb_controller(ActionEvent event) throws Exception {
		int[][] temp = main_puzzle;
		if(puzzle_size() == 0) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setContentText("Puzzle does not exist! Please generate a puzzle first");
			alert.showAndWait();
			return;
		}
		long start = Calendar.getInstance().getTimeInMillis();
		int type = 1;
		int[][] solution = null;
		int num_hill_climbs = 0;
		int num_iterations = 0;
		double probability = 0;
		
		try {
			num_iterations = Integer.parseInt(Hill_Climb_Iterations.getText());
		}
		catch(NumberFormatException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Invalid input");
			alert.setContentText("Number of iterations must be an integer. Please try again");
			alert.showAndWait();
			return;
		}
		
		if(!Hill_Climbs.getText().equals("")) {
			try {
				num_hill_climbs = Integer.parseInt(Hill_Climbs.getText());
			}
			catch(NumberFormatException e) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Invalid input");
				alert.setContentText("Hill climb iterations must be an integer. Please try again");
				alert.showAndWait();
				return;
			}
			type = 2;
			solution = rand_restarts(num_hill_climbs, num_iterations, temp);
			compute_time(start);
			get_value(solution);
			display_puzzle(solution);
			return;
		}
		
		if(!Probability.getText().equals("")) {
			try {
				probability = Double.parseDouble(Probability.getText());
			}
			catch(NumberFormatException e){
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Invalid input");
				alert.setContentText("Probability must be a number between 0 and 1. Please try again");
				alert.showAndWait();
				return;
			}
			
			if(probability < 0 || probability > 1) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Invalid input");
				alert.setContentText("Probability must be a number between 0 and 1. Please try again");
				alert.showAndWait();
				return;
			}
			type = 3;
			solution = hill_climb(num_iterations, probability, type, temp);
			compute_time(start);
			get_value(solution);
			display_puzzle(solution);
			return;
		}
		
		solution = hill_climb(num_iterations, probability, type, temp);
		compute_time(start);
		get_value(solution);
		display_puzzle(solution);
		
	}
	
	/*
	 * Performs pure hill climbing and hill climbing with random walk. 
	 * @param	num_terations	number of iterations
	 * @param	probability		for random walk
	 * @param	temp				matrix that holds an instance of the puzzle 
	 * */
	private int[][] hill_climb(int num_iterations, double probability,
			int type, int[][] temp) throws Exception{
		
		Random rand = new Random();
		int size = main_puzzle.length;
		
		for(int i  = 0; i < num_iterations; i++) {
			int spot[] = gen_spot(size);
			int x = spot[0];
			int y = spot[1];
			
			int init_spot = temp[x][y];
			int init_value = valueFunction(temp);
			
			int curr_spot = check_legal_spot(size, x, y);
			temp[x][y] = curr_spot;
			int curr_value = valueFunction(temp);
			
			if(curr_value > init_value) {
				//hill climb successfully made puzzle harder, keep trying
				continue;
			}
			else {
				switch(type) {
				case 1:
					temp[x][y] = init_spot;
					break;
				case 3:
					double result = rand.nextDouble();
					
					if (result <= probability) {
						continue;
					} 
					else {
						temp[x][y] = init_spot;
					}
				}	
			}
		}
		return temp;
	}
	
	
	/*
	 * Called by hill_climb_controller
	 * @param	num_hill_climbs		Number of hill climbs
	 * @param	num_iterations		Number of iterations per hill climb
	 * @param	puzzle				Matrix holding an instance of the main puzzle
	 * */
	public int[][] rand_restarts(int num_hill_climbs, int num_iterations,
			int[][] init_puzzle) throws Exception {
		int[][] best_puzzle = init_puzzle;
		int best_value = valueFunction(best_puzzle);
		int curr_value = 0;
		//int size = puzzle_size();
		
		for(int i = 0; i < num_hill_climbs; i++) {
				int[][] puzzle = make_puzzle(puzzle_size());
						puzzle = hill_climb(num_iterations,0,1,puzzle);
				curr_value = valueFunction(puzzle);
				/*int init_value = valueFunction(puzzle);
				int best_value = valueFunction(best_puzzle);
				*/
				
				if (curr_value >= best_value) {
					best_puzzle = puzzle;
					best_value = curr_value;
				}
			//}
			//puzzle = make_puzzle(size);
		}
		setPuzzle(best_puzzle);
		return best_puzzle;
		
	}
	
	/*
	 * Controller for simulated annealing 
	 * */
	public void sim_annealing_controller(ActionEvent event) throws Exception {
		if(puzzle_size() == 0) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setContentText("Puzzle does not exist! Please generate a puzzle first");
			alert.showAndWait();
			return;
		}
		long start = Calendar.getInstance().getTimeInMillis();

		int num_iterations = 0;
		double init_temperature = 0;
		double decay = 0;
		
		try {
			num_iterations = Integer.parseInt(Sim_Annealing_Iterations.getText());
		}
		catch(NumberFormatException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Invalid input");
			alert.setContentText("Number of iterations for sim annealing must be an integer. Please try again");
			alert.showAndWait();
			return;
		}
		
		if(!Temperature.getText().equals("")) {
			try {
				init_temperature = Double.parseDouble(Temperature.getText());
			}
			catch(NumberFormatException e) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Invalid input");
				alert.setContentText("Temperature must be a number. Please try again");
				alert.showAndWait();
				return;
			}
		}
		
		if(!Decay_Rate.getText().equals("")) {
			try {
				decay = Double.parseDouble(Decay_Rate.getText());
			}
			catch(NumberFormatException e){
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Invalid input");
				alert.setContentText("Decay rate must be a number. Please try again");
				alert.showAndWait();
				return;
			}
			
			if(decay < 0 || decay > 1) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Invalid input");
				alert.setContentText("Decay rate must be between 0 and 1. Please try again");
				alert.showAndWait();
				return;
			}
		}
		
		int [][] solution = sim_annealing(num_iterations, init_temperature, decay);
		compute_time(start);
		get_value(solution);
		display_puzzle(solution);
	}
	
	/*
	 * Called by sim_annealing controller. Performs simulated annealing and returns the puzzle
	 * @param	num_iterations		Number of iterations for simulated annealing
	 * @param	init_temperature		The current temperature of the environment
	 * @param	decay				The decay rate that factors the temperature
	 * */
	private int[][] sim_annealing(int num_iterations, double init_temperature, double decay) throws Exception{
		int[][] temp = main_puzzle;
		int size = temp.length;
		
		for(int i = 0; i < num_iterations; i++) {
			int spot[] = gen_spot(size);
			int x = spot[0];
			int y = spot[1];
			
			int init_spot = temp[x][y];
			int init_value = valueFunction(temp);
			
			int curr_spot = check_legal_spot(size, x, y);
			temp[x][y] = curr_spot;
			int curr_value = valueFunction(temp);
			
			/* invert this block to eliminate the continue statements
			 * so that the cooling step isnt skipped
			 * if(curr_value >= init_value) {
				continue;
			}
			else {
				Random rand = new Random();
				double num = init_value - curr_value;
				double exp = num/init_temperature;
				double sol = Math.pow(Math.E, exp);
				
				if(rand.nextDouble() <= sol) {
					continue;
				}
				else {
					temp[x][y] = init_spot;
				}
			}*/
			if (curr_value < init_value) {
				Random rand = new Random();
				double num = curr_value - init_value;
				double exp = num/init_temperature;
				double sol = Math.pow(Math.E, exp);
				
				if(rand.nextDouble() > sol) {
					temp[x][y] = init_spot;
				}
			}
			init_temperature *= 1-decay; 
		}
		return temp;
	}
	
	/*
	 * Generates a random spot in the matrix given the size of the matrix. 
	 * Returns the x and y coordinates of the spot in an array
	 * */
	private int[] gen_spot(int size) {

		Random rand = new Random();
		int max = size - 1, min = 0;
		int x = rand.nextInt(max - min + 1) + min;
		int y = rand.nextInt(max - min + 1) + min;
		
		while (x == max && y == max) {
			x = rand.nextInt(max - min + 1) + min;
			y = rand.nextInt(max - min + 1) + min;
		}
		
		int[] spot = new int[2];
		spot[0] = x;
		spot[1] = y;
		
		return spot;
	}
	
	/*
	 * Checks if for a given size, the x,y coordinate in the matrix is a legal spot 
	 * for the value and returns that spot. 
	 * */
	private int check_legal_spot(int size, int x, int y) {
		
		int horizontal_max = Math.max(y, size - y - 1);
		int vertical_max = Math.max(x, size - x - 1);
		int spot = Math.max(horizontal_max, vertical_max);
		
		Random rand = new Random();
		int result = rand.nextInt(spot) + 1;
		
		return result;
	}
	
	/*
	 * Calculates the value function given a particular solution. This is the least number 
	 * of moves required to reach the goal. Uses the Breadth first search process in 
	 * the findSolution method
	 * */
	public int valueFunction(int[][] puzzle) throws Exception {
		int size = puzzle.length;
		int[][] result = findsolution(puzzle);
		int sol = result[size-1][size-1];
		
		if (sol == -1) {
			int numFails = 0;
			
			for (int x = 0; x < size; x++) {
				for (int y = 0; y < size; y++) {
					if (result[x][y] == -1) {
						numFails++;
					}
				}
			}
			
			return (-1 * numFails);
		}
		
		return sol;
	}
	
	/*
	 * Performs own-solution implementation of natural selection by position. 
	 * @param	num_terations	number of iterations
	 * 
	 * */
	private int[][] own_solution(int num_iterations,  int[][] temp) throws Exception{
		
		Random rand = new Random();
		int size = temp.length;
		//int curr_spot = 0;
		
		for(int i = 0; i < num_iterations; i++) {
			int spot[] = gen_spot(size);
			int x = spot[0];
			int y = spot[1];
			int curr_spot = 0;
			
			int init_spot = temp[x][y];
			int init_value = valueFunction(temp);
			
			/*if (init_spot == -1) {
				continue;
			}*/
			if (x + y >= size) {				
				//try to increase spot index
				
				/*int horizontal_max = Math.max(y, size - y - 1);
				int vertical_max = Math.max(x, size - x - 1);
				int max = Math.max(horizontal_max, vertical_max);
				curr_spot = rand.nextInt(max) + 1;*/
				
				curr_spot = check_legal_spot(size,x,y);
			} else {
				if(x == size - 1 && y == size - 1) {
					//catch the goal position which must always be 0
					continue;
				}
				//decrease spot index, always possible
				curr_spot = rand.nextInt(init_spot) + 1;
			}
			temp[x][y] = curr_spot;			
			int curr_value = valueFunction(temp);
			
			if(curr_value > init_value) {
				continue;
			}
			else {
				temp[x][y] = init_spot;
				}
		}
		setPuzzle(temp);
		return temp;
	}
	
	public void own_solution_controller(ActionEvent event) throws Exception {
		if(puzzle_size() == 0) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setContentText("Puzzle does not exist! Please generate a puzzle first");
			alert.showAndWait();
			return;
		}
		int num_iterations = 0;
		
		try {
			num_iterations = Integer.parseInt(Own_Approach_Iterations.getText());
		}
		catch(NumberFormatException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Invalid input");
			alert.setContentText("Number of iterations must be an integer. Please try again");
			alert.showAndWait();
			return;
		}
		
		long start = Calendar.getInstance().getTimeInMillis();
		int[][] solution = own_solution(num_iterations, getPuzzle());
		compute_time(start);
		get_value(solution);
		display_puzzle(solution);
	}
	
	/*
	 * Gets the start time as an argument, checks the current time and subtracts the difference.
	 * Does not return the time, only sets it on the Compute_display TextField
	 * */
	public void compute_time(long start_time) {
		long current_time = Calendar.getInstance().getTimeInMillis();
		Compute_Display.setText(Long.toString(current_time - start_time));
	}
	
	/*
	 * OnAction method to displaay the value funciton onto the textfield. Textfield is disabled
	 * */
	public void get_value(int[][] puzzle) throws Exception {
		Value_Display.setText(Integer.toString(valueFunction(puzzle)));
	}
	
	/*
	 * Opens a FileChooser dialog to let the user browse for a file and then reads the file
	 * that contains the size of the puzzle and the puzzle itself. Throws an exception if the 
	 * file type or file format is invalid 
	 * */
	public void file_selector(ActionEvent event) throws Exception {
		Stage stage = Main.getPrimaryStage();
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		File file = fileChooser.showOpenDialog(stage);
		try {
			long start = Calendar.getInstance().getTimeInMillis();
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			int size = Integer.parseInt(bufferedReader.readLine());
			
			int[][] puzzle = new int[size][size];
			change_size(size);
			for (int i = 0; i < size; i++) {
				String next_line = bufferedReader.readLine();
				String[] num = next_line.split("\\s+");
				
				for (int j = 0; j < size; j++) {
					int spot = Integer.parseInt(num[j]);
					puzzle[i][j] = spot;
				}
			}
			display_puzzle(puzzle);
			main_puzzle = puzzle;
			compute_time(start);
			
		}
		catch(IOException e){
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Invalid File Input");
			alert.setContentText("File needs to be a txt with correct format. Please choose another file");
			alert.showAndWait();
			return;
		}
	}
	
}
