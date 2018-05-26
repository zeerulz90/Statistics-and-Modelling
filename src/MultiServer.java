//Sysc 5001 Course Project 2015
//Ehsanul Hakim Khan
//Student No 7481664
//Mahjabeen Alam
//Student No 100934098

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class MultiServer {

	public static int nq = 5; // number of queue(s)
	public static int ns = 3; // number of server(s)
	public static int timeslots = 50000; // number of time slots for simulation
	public static int[][][] connected = new int[timeslots][ns][nq];
	public static int[][] arrived = new int[timeslots][nq];
	public static int[][] queueSize = new int[timeslots][nq]; // number of
																// packets in
																// queue
	public static int[][][] served = new int[timeslots][ns][nq];

	public static double p; // connection probability
	public static double lamda; // arrival probability

	public static int timer = 0;
	public static int queueIndex = 0;

	public static void generateConnectedVariables(int index) {
		Random r = new Random();
		for (int s = 0; s < ns; s++) {
			for (int i = 0; i < nq; i++) {
				double randomValue = r.nextInt(100);
				double number = randomValue / 100;
				if (number <= p) {
					connected[index][s][i] = 1;
				} else {
					connected[index][s][i] = 0;
				}
			}
		}
	}

	public static void generateArrivedVariables(int index) {
		Random r = new Random();
		double randomValue = r.nextInt(100);
		double number = randomValue / 100;
		double number2;
		for (int i = 0; i < nq; i++) {
			number2 = -0.1 + (Math.random() * (0.1 + 0.1));
			number += number2;
			if (number <= (lamda * (i + 1))) {
				arrived[index][i] = 1;
			} else {
				arrived[index][i] = 0;
			}
		}

	}

	public static void updateServedRandomizedPolicy(int index) {
		Random r = new Random();
		List<Integer> serverList = new ArrayList<>();
		serverList.add(0);
		serverList.add(1);
		serverList.add(2);

		while (!serverList.isEmpty()) {
			int k = r.nextInt(serverList.size());
			int serverIndex = serverList.get(k);

			List<Integer> selected = new ArrayList<>();
			for (int i = 0; i < nq; i++) {
				int servedSum = served[index][0][i] + served[index][1][i]
						+ served[index][2][i];

				if (connected[index][serverIndex][i] == 1
						&& ((queueSize[index - 1][i] + arrived[index][i]) > servedSum)) {
					// connected and non-empty
					selected.add(i);
				}
			}
			if (!selected.isEmpty()) {
				int j = r.nextInt(selected.size());
				int queueIndex = selected.get(j);
				served[index][serverIndex][queueIndex] = 1;
			}

			serverList.remove(k);

		}
	}

	public static void updateASLCQPolicy(int index) {

		Random r = new Random();
		List<Integer> serverList = new ArrayList<>();
		serverList.add(0);
		serverList.add(1);
		serverList.add(2);

		while (!serverList.isEmpty()) {
			int k = r.nextInt(serverList.size());
			int serverIndex = serverList.get(k);

			List<Integer> selected = new ArrayList<>();
			for (int i = 0; i < nq; i++) {
				int servedSum = served[index][0][i] + served[index][1][i]
						+ served[index][2][i];
				if (connected[index][serverIndex][i] == 1
						&& ((queueSize[index - 1][i] + arrived[index][i]) > servedSum)) {
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

				int l = r.nextInt(shortlist.size());
				pickIndex = selected.get(l);

				served[index][serverIndex][pickIndex] = 1;
			}

			serverList.remove(k);
		}
	}

	public static void updateLCSFLCQPolicy(int index) {
		Random r = new Random();
		int[] sortedServerList = new int[ns];

		int[] connectionSum = new int[ns];

		connectionSum[0] = connected[index][0][0] + connected[index][0][1]
				+ connected[index][0][2] + connected[index][0][3]
				+ connected[index][0][4];

		connectionSum[1] = connected[index][1][0] + connected[index][1][1]
				+ connected[index][1][2] + connected[index][1][3]
				+ connected[index][1][4];

		connectionSum[2] = connected[index][2][0] + connected[index][2][1]
				+ connected[index][2][2] + connected[index][2][3]
				+ connected[index][2][4];

		HashMap<Integer, Integer> hm = new HashMap<Integer, Integer>(); // Hashmap
																		// (serverIndex,number
																		// of
																		// queues
		// connected)

		hm.put(0, connectionSum[0]);
		hm.put(1, connectionSum[1]);
		hm.put(2, connectionSum[2]);

		Map<Integer, Integer> sorted = sortByValues(hm);
		int b = 0;
		Set set = sorted.entrySet();
		Iterator iterator = set.iterator();
		while (iterator.hasNext()) {
			Map.Entry me = (Map.Entry) iterator.next();
			sortedServerList[b] = (int) me.getKey();
			b++;
		}

		for (int s = 0; s < sortedServerList.length; s++) {

			List<Integer> selected = new ArrayList<>();
			for (int i = 0; i < nq; i++) {
				int servedSum = served[index][0][i] + served[index][1][i]
						+ served[index][2][i];
				if (connected[index][sortedServerList[s]][i] == 1
						&& ((queueSize[index - 1][i] + arrived[index][i]) > servedSum)) {
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

				int l = r.nextInt(shortlist.size());
				pickIndex = selected.get(l);

				served[index][sortedServerList[s]][pickIndex] = 1;
			}

		}

	}

	private static HashMap sortByValues(HashMap map) {
		List list = new LinkedList(map.entrySet());
		// Defined Custom Comparator here
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o1)).getValue())
						.compareTo(((Map.Entry) (o2)).getValue());
			}
		});

		// Here I am copying the sorted list in HashMap
		// using LinkedHashMap to preserve the insertion order
		HashMap sortedHashMap = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedHashMap.put(entry.getKey(), entry.getValue());
		}
		return sortedHashMap;
	}

	public static void updateQueueSize(int index) {
		for (int i = 0; i < nq; i++) {
			queueSize[index][i] = queueSize[index - 1][i] + arrived[index][i]
					- served[index][0][i] - served[index][1][i]
					- served[index][2][i];
		}
	}

	public static void initialize() {
		generateArrivedVariables(0);
		generateConnectedVariables(0);
		for (int i = 0; i < nq; i++) {
			queueSize[0][i] = arrived[0][i];
		}

		for (int i = 0; i < timeslots; i++) {
			for (int s = 0; s < ns; s++) {
				for (int j = 0; j < nq; j++) {
					served[i][s][j] = 0;
				}
			}
		}
	}

	public static double runSimulationMulti(double prob, double langda,
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
				updateASLCQPolicy(i);
				break;
			case 3:
				updateLCSFLCQPolicy(i);
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

		return average;
	}
}
