package process;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Utils {

	static int DEFAULT_QUANTUM = 2;

	public static ArrayList<Process> initializeEntry(String fileName) throws FileNotFoundException {
		int id = 1;

		Scanner entryFile = new Scanner(new FileReader(fileName));

		ArrayList<Process> entryRow = new ArrayList<Process>();

		while (entryFile.hasNextLine()) {
			String[] processArray = entryFile.nextLine().split(", ");

			Process newProcess = new Process(id, Integer.parseInt(processArray[0]), Integer.parseInt(processArray[1]),
					Integer.parseInt(processArray[2]), Integer.parseInt(processArray[3]),
					Integer.parseInt(processArray[4]), Integer.parseInt(processArray[5]),
					Integer.parseInt(processArray[6]), Integer.parseInt(processArray[7]));

			entryRow.add(newProcess);

			id++;
		}
		entryFile.close();

		Collections.sort(entryRow);

		return entryRow;
	}

	public static void printRows(ArrayList<Process> readyProcessRow, ArrayList<Process> readySuspendedProcessRow,
			ArrayList<Process> blockedProcessRow, ArrayList<Process> blockedSuspendedProcessRow,
			ArrayList<Process> finalizedProcessRow, ArrayList<Process> realtimeProcessRow,
			ArrayList<Process> userProcessRow, ArrayList<Process> entryRow) {
		System.out.println("\n FILAS:");
		System.out.print("\t FTR: ");
		for (Process process : realtimeProcessRow) {
			if (process != null) {
				System.out.print("P" + process.id + ", ");
			}
		}
		System.out.print("\n\t FU: ");
		for (Process process : userProcessRow) {
			if (process != null) {
				System.out.print("P" + process.id + ", ");
			}
		}

		System.out.println("\n\n FE:");
		for (Process process : entryRow) {
			if (process != null) {
				System.out.print("P" + process.id + ", ");
			}
		}

		System.out.print("\n\n FP:");
		for (Process process : readyProcessRow) {
			if (process != null) {
				System.out.print("P" + process.id + ", ");
			}
		}
		System.out.println("\n FPS:");
		for (Process process : readySuspendedProcessRow) {
			if (process != null) {
				System.out.print("P" + process.id + ", ");
			}
		}
		System.out.print("\n FB:");
		for (Process process : blockedProcessRow) {
			if (process != null) {
				System.out.print("P" + process.id + ", ");
			}
		}
		System.out.println("\n FBS:");
		for (Process process : blockedSuspendedProcessRow) {
			if (process != null) {
				System.out.print("P" + process.id + ", ");
			}
		}
		System.out.println("\n FF:");
		for (Process process : finalizedProcessRow) {
			if (process != null) {
				System.out.println("P" + process.id + " entrou na cpu em " + process.arrivalTime + "s e finalizou em "
						+ process.exitTime + "s");
			}
		}
	}
}
