package com.nearestneighbors;

import java.util.ArrayList;

public class CrossValidation {
	public ArrayList<TrainingExample> trainingData;

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

	public double kNN(ArrayList<TrainingExample> a1, int fold_ex, int fold_size, int k, boolean lastFold, boolean label) {
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

}
