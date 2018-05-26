//Sysc 5001 Course Project 2015
//Ehsanul Hakim Khan
//Student No 7481664
//Mahjabeen Alam
//Student No 100934098

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SingleServer {

	public static int nq = 5; // number of queue(s)
	public static int ns = 1; // number of server(s)
	public static int timeslots = 50000; // number of time slots for simulation
	public static int[][] connected = new int[timeslots][nq];
	public static int[][] arrived = new int[timeslots][nq];
	public static int[][] queueSize = new int[timeslots][nq]; // number of
																// packets in
																// queue
	public static int[][] served = new int[timeslots][nq];

	public static double[] p = new double[5]; // connection probability
	public static double lamda; // arrival probability

	public static int timer = 0;
	public static int queueIndex = 0;

	public static void generateConnectedVariables(int index) {
		Random r = new Random();
		for (int i = 0; i < nq; i++) {
			double randomValue = r.nextInt(100);
			double number = randomValue / 100;
			if (number <= p[i]) {
				connected[index][i] = 1;
			} else {
				connected[index][i] = 0;
			}
		}

	}

	public static void generateArrivedVariables(int index) {
		Random r = new Random();
		double randomValue = r.nextInt(100);
		double number = randomValue / 100;
		double number2;
		for (int i = 0; i < nq; i++) {
			if (i > 0) {
				number2 = -0.1 + (Math.random() * (0.1 + 0.1));
				number += number2;
			}
			if (number <= (lamda * (i + 1))) {
				arrived[index][i] = 1;
			} else {
				arrived[index][i] = 0;
			}
		}

	}

	public static void updateServedRandomizedPolicy(int index) {
		List<Integer> selected = new ArrayList<>();
		Random r = new Random();
		for (int i = 0; i < nq; i++) {
			if (connected[index][i] == 1 && queueSize[index - 1][i] > 0) {
				// connected and non-empty
				selected.add(i);
			}
		}
		if (!selected.isEmpty()) {
			int j = r.nextInt(selected.size());
			int queueIndex = selected.get(j);
			served[index][queueIndex] = 1;
		}
	}

	public static void updateServedRoundRobinPolicy(int index) {

		if (connected[index][queueIndex] == 1
				&& queueSize[index - 1][queueIndex] > 0) {
			// connected and non-empty
			served[index][queueIndex] = 1;
		}

		timer++;
		if (timer > 4) {
			timer = 0;
			queueIndex++;
			if (queueIndex > 4) {
				queueIndex = 0;
			}
		}
	}

	public static void updateServedLongestQueuePolicy(int index) {
		List<Integer> selected = new ArrayList<>();
		Random r = new Random();
		for (int i = 0; i < nq; i++) {
			if (connected[index][i] == 1 && queueSize[index - 1][i] > 0) {
				// connected and non-empty
				selected.add(i);
			}
		}

		if (!selected.isEmpty()) {
			int pickIndex = -1;
			Integer max = Collections.max(selected, null);

			List<Integer> shortlist = new ArrayList<>();
			for (int j = 0; j < selected.size(); j++) {
				if (selected.get(j).equals(max)) {
					shortlist.add(j);
				}
			}

			int k = r.nextInt(shortlist.size());
			pickIndex = selected.get(k);

			served[index][pickIndex] = 1;
		}
	}

	public static void updateQueueSize(int index) {
		for (int i = 0; i < nq; i++) {
			queueSize[index][i] = queueSize[index - 1][i] + arrived[index][i]
					- served[index][i];
		}
	}

	public static void initialize() {

		resetRoundRobin();
		generateArrivedVariables(0);
		generateConnectedVariables(0);
		for (int i = 0; i < nq; i++) {
			queueSize[0][i] = arrived[0][i];
		}

		for (int i = 0; i < timeslots; i++) {
			for (int j = 0; j < nq; j++) {
				served[i][j] = 0;
			}
		}
	}

	public static void resetRoundRobin() {
		timer = 0;
		queueIndex = 0;
	}

	public static double runSimulationSingle(double[] prob, double langda,
			int serviceType) {
		double sum = 0;
		double average = 0;

		p = prob;
		lamda = langda;

		initialize();

		for (int i = 1; i < timeslots; i++) {
			generateConnectedVariables(i);
			generateArrivedVariables(i);

			switch (serviceType) {
			case 1:
				updateServedRandomizedPolicy(i);
				break;
			case 2:
				updateServedRoundRobinPolicy(i);
				break;
			case 3:
				updateServedLongestQueuePolicy(i);
				break;
			default:
				updateServedRandomizedPolicy(i);

			}

			updateQueueSize(i);

			for (int j = 0; j < nq; j++) {
				sum += queueSize[i][j];
			}
		}

		average = sum / timeslots;

		// System.out.println(lamda + "\t" + average);

		return average;
	}
}
