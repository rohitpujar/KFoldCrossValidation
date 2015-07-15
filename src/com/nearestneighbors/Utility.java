package com.nearestneighbors;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Utility {

	public static ArrayList<TrainingExample> reArrange(ArrayList<TrainingExample> trainingData, String[] permutations) {
		ArrayList<TrainingExample> reArrangedTrainingData = new ArrayList<>();
		for (int i = 0; i < permutations.length; i++) {
			reArrangedTrainingData.add(trainingData.get(Integer.parseInt(permutations[i])));
		}
		return reArrangedTrainingData;
	}

	public static ArrayList<TrainingExample> readData() throws FileNotFoundException {
		// Reading the dataSet file and extracting the required information
		Scanner input = new Scanner(new File("data.txt"));

		int no_of_rows = input.nextInt(); // No of rows
		int no_of_columns = input.nextInt(); // No of columns
		int count = 0; // Temp variable to maintain the examples count

		// Creating an arrayList of type trainingExample to store the example
		// info as data file is read

		ArrayList<TrainingExample> a1 = new ArrayList<TrainingExample>();

		// Skipping to the nextLine
		// input.nextLine();

		// String to store a permutation
		String[] s1;
		for (int i = 0; i < no_of_rows + 1; i++) {
			// System.out.println(i);
			s1 = input.nextLine().split(" ");
			for (int j = 0; j < s1.length; j++) {
				// Adding the x1 and x2 values for the positive examples
				if (s1[j].equals("+")) {
					TrainingExample f1 = new TrainingExample();
					f1.x1 = j;
					f1.x2 = i - 1;
					f1.y = '+';
					f1.ex_no = count;
					count++;
					a1.add(f1);
				}

				// Adding the x1 and x2 values for the negative exmaples
				else if (s1[j].equals("-")) {
					TrainingExample f1 = new TrainingExample();
					f1.x1 = j;
					f1.x2 = i - 1;
					f1.y = '-';
					f1.ex_no = count;
					count++;
					a1.add(f1);
				}

			}
		}
		// Returns an arrayList containing all the examples from the dataFile
		return a1;
	}
}
