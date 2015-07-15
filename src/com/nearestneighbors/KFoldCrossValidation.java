package com.nearestneighbors;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class KFoldCrossValidation extends CrossValidation {
//	public ArrayList<trainingExample> trainingData;

	public static void main(String[] args) throws FileNotFoundException {
		KFoldCrossValidation k1 = new KFoldCrossValidation(); 

		k1.readInput(); 
	}

	public ArrayList<TrainingExample> readInput() throws FileNotFoundException {
		for (int k = 1; k < 6; k++) {
			Scanner input = new Scanner(new File("file1.txt"));

			int fold = input.nextInt(); 
			int no_of_ex = input.nextInt(); 
			int perm_no = input.nextInt(); 

			String[] permutations;

			ArrayList<TrainingExample> trainingData = new ArrayList<>();
			trainingData = Utility.readData();

			ArrayList<TrainingExample> reArrangedTrainingData;

			// Used to find the errors for a given permutation, this array
			// contains errors of all the permutations according to index
			double errorEstimates[] = new double[perm_no];

			// used to skip to next line to read the permutations from the next
			// line
			input.nextLine();
			double num_perm = perm_no;

			// Taking the permutation from the dataSet
			while (input.hasNextLine()) {
				permutations = input.nextLine().split(" ");

				// Rearranging the permutation in the serial order- so that
				// working becomes easy
				reArrangedTrainingData = Utility.reArrange(trainingData, permutations);

				// Calling the KFold Cross Validation on the permutation for a
				// given fold, and finding the error estimate
				errorEstimates[perm_no - 1] = kfoldCV(reArrangedTrainingData, fold, k);
				perm_no--;
			}

			// Computing the error estimate
			double errorEstimate = 0;
			double temp = 0;

			// Summation of the errors across various permutation
			for (int i = 0; i < num_perm; i++) {
				temp = temp + errorEstimates[i];
			}

			// This gives the 'accurate error' estimate
			errorEstimate = temp / num_perm;

			temp = 0;

			// Calculates the summation of the values for computing variance
			for (int i = 0; i < num_perm; i++) {
				temp = temp + Math.pow((errorEstimate - errorEstimates[i]), 2);
			}

			// This gives the variance of all the permutations for a given 'k'
			// value
			double variance = 0;
			variance = temp / (num_perm - 1);

			// This is the standard deviation for a given 'k' value
			double standardDeviation = Math.sqrt(variance);

			// Prints the error and standard deviation
			System.out.println("Error:" + errorEstimate + "Standard Deviation:" + standardDeviation);

			// Calls gridLabelling.. wherein the function prints the label grid
			labelGrids(k);
		}
		return trainingData;
	}

	public void labelGrids(int k) throws FileNotFoundException {
		// Initializing the global arraylist
		trainingData = new ArrayList<>();

		int count = 0;
		Scanner input = new Scanner(new File("data.txt"));
		int noOfRows = input.nextInt();
		int noOfColumns = input.nextInt();
		input.nextLine();
		String[] row;

		for (int i = 0; i < noOfRows; i++) {
			row = input.nextLine().split(" ");
			for (int j = 0; j < row.length; j++) {
				if (row[j].equals("+")) {
					TrainingExample newExample = new TrainingExample();
					newExample.x2 = i;
					newExample.x1 = j;
					newExample.y = '+';
					newExample.ex_no = count;
					count++;
					trainingData.add(newExample);
				} else if (row[j].equals("-")) {
					TrainingExample newExample = new TrainingExample();
					newExample.x2 = i;
					newExample.x1 = j;
					newExample.y = '-';
					newExample.ex_no = count;
					count++;
					trainingData.add(newExample);
				} else {
					TrainingExample newExample = new TrainingExample();
					newExample.x2 = i;
					newExample.x1 = j;
					newExample.y = '.';
					newExample.ex_no = count;
					count++;
					trainingData.add(newExample);
				}
			}
		}

		for (int i = 0; i < trainingData.size(); i++) {
			kNN(trainingData, i, 1, k, false, true);
		}

		labelPrint(trainingData, noOfRows, noOfColumns);

	}

	public void labelPrint(ArrayList<TrainingExample> trainingData, int no_of_rows, int no_of_columns) {
		System.out.println();
		for (int i = 0; i < trainingData.size(); i = i + no_of_columns) {
			for (int j = i; j < i + no_of_columns; j++) {
				System.out.print(trainingData.get(j).y + " ");
			}
			System.out.println();
		}

	}

	

	// Prints the given cross validation data in the file 1 -(Primarily used for
	// debugging purposes)
	public void printCVData(int fold, int no_of_ex, int perm_no, String[] permutations) {
		int count = 0;

		System.out.println(fold);
		System.out.println(no_of_ex);
		System.out.println(perm_no);

		for (int i = 0; i < count; i++)
			System.out.println(permutations[i]);
	}

	// Finding error estimate for a given permutation based on fold
	public double kfoldCV(ArrayList<TrainingExample> a1, int fold, int k) {
		boolean label = false;

		int no_of_ex = a1.size(); // No of examples
		int fold_size = no_of_ex / fold; // Fold size- i.e. number of examples
											// in each fold

		// Variables for total errors and errorsInGivenIteration
		double total_error_count = 0;
		double errorsInGivenIteration = 0;
		boolean lastFold = false;

		// Iterating through the examples in the testing fold
		for (int i = 0; i + fold_size < no_of_ex; i = i + fold_size) {
			if (fold == 1) {
				lastFold = true;
			}

			// Calling the function to find the errors for those examples in
			// that fold by finding nearest neighbors algorithms
			errorsInGivenIteration = kNN(a1, i, fold_size, k, lastFold, label);
			total_error_count = total_error_count + errorsInGivenIteration;
			// System.out.println(errorsInGivenIteration);
			fold--;
		}
		return total_error_count / (double) no_of_ex;
	}

	// Function to computer nearest neighbors for a given testing fold
	/*public double kNN(ArrayList<TrainingExample> a1, int fold_ex, int fold_size, int k, boolean lastFold, boolean label) {
		double[] distances = new double[a1.size()];

		// Keeps track of no. of mis-classifications
		int misClassified_no = 0;

		// Here i is the testExample, iterating through all of them until
		// testing fold examples are over
		for (int i = fold_ex; i < fold_ex + fold_size || lastFold; i++) {
			// We will exit out, if it is the lastFold and include the remaining
			// examples in this fold itself
			if (lastFold) {
				if (i >= a1.size())
					break;
			}

			// for every example in testing fold, we will calculate the
			// distances between itself and the training fold example
			for (int j = 0; j < a1.size(); j++) {
				// If its last fold, distance is set to MAXVALUE
				if (lastFold) {
					if (j >= fold_ex && j < a1.size()) {
						distances[j] = Double.MAX_VALUE;
						continue;
					}
				}

				// If two examples are within the same fold, then do not have to
				// calculate the distances
				else {
					if (j >= fold_ex && j < fold_ex + fold_size) {
						distances[j] = Double.MAX_VALUE;
						continue;
					}
				}

				// In this case we calculate the distance between a testing fold
				// example and training fold example by making a function call
				distances[j] = distance(a1.get(i), a1.get(j));
			}

			// The below code aims at finding the minimum distance between the
			// testing fold across all training fold examples
			double minDistance;
			int element = -1;
			int posEx = 0; // No. of positive examples
			int negEx = 0; // No.of negative examples

			// Finds kth minimum distance training example, and finding the
			// majority 'y' classification among them
			for (int p = 0; p < k; p++) {
				minDistance = Double.MAX_VALUE;
				for (int m = 0; m < distances.length; m++) {
					// Compares distance and swaps if it finds a minimum element
					if (minDistance > distances[m]) {
						minDistance = distances[m];
						element = m;
					}
				}
				distances[element] = Double.MAX_VALUE;

				// If an encountered example is positive, increment the number
				// of positive examples
				if (a1.get(element).y == '+') {
					posEx = posEx + 1;
				}

				// If an encountered example is negative, increment the number
				// of negative examples
				else {
					negEx = negEx + 1;
				}
			}

			// The following code increments the misclassified count if the
			// original classification is not equal to the majority 'y' that is
			// computed
			if (posEx > negEx) {
				// If the point is not an example, classify it as a positive on
				// based on majority
				if (label && a1.get(i).y == '.') {
					a1.get(i).y = '+';
				}
				// Since, positive is the majority, and if we find the
				// classification that is not positive, then increment the
				// counter for misclassifications
				else if (a1.get(i).y != '+') {
					misClassified_no = misClassified_no + 1;
				}
			}

			else {
				// If the point is not an example, classify it as a positive on
				// based on majority
				if (label && a1.get(i).y == '.') {

					a1.get(i).y = '-';
				}
				// Since, negative is the majority, and if we find the
				// classification that is not negative, then increment the
				// counter for misclassifications
				else if (a1.get(i).y != '-') {
					misClassified_no = misClassified_no + 1;
				}
			}
		}
		return misClassified_no; // Returns the total number of
									// misclassifications for that 'k' value
	}

	// Calculates distance between two given examples and returns the values
	public double distance(TrainingExample f1, TrainingExample f2) {
		return Math.pow((f1.x1 - f2.x1), 2) + Math.pow((f1.x2 - f2.x2), 2);
	}

*/

	// Prints to check the data used by the algorithm (Used for Debuggin
	// purposes)

	public void printData(ArrayList<TrainingExample> a1) {
		for (int i = 0; i < a1.size(); i++)
			System.out.println(a1.get(i).ex_no + " " + a1.get(i).x1 + " " + a1.get(i).x2 + " " + a1.get(i).y);
		System.out.println();
	}

}