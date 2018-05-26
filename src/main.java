//Sysc 5001 Course Project 2015
//Ehsanul Hakim Khan
//Student No 7481664
//Mahjabeen Alam
//Student No 100934098

public class main {

	public static double p = 0.5;
	public static double lamda = 0.02;
	public static double average = 0;

	public static void main(String[] args) {

		System.out.println("lamda\tOccupancy");

		for (int i = 1; i < 11; i++) {
			average = 0;
			for (int j = 0; j < 20; j++) {
				MultiServer simulate = new MultiServer();
				average += simulate.runSimulationMulti(p, lamda * i, 2);
			}

			System.out.println(lamda * i + "\t" + average / 20);
		}
		/*
		 * for (int i = 1; i < 11; i++) { average = 0; for (int j = 0; j < 20;
		 * j++) { SingleServer simulate = new SingleServer(); average +=
		 * simulate.runSimulationSingle(p, lamda * i, 1); }
		 * 
		 * System.out.println(lamda * i + "\t" + average / 20); }
		 */

	}
}
