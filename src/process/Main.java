package process;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) throws FileNotFoundException {
		Scanner scan = new Scanner(System.in);

		ArrayList<Process> entryRow = Utils.initializeEntry("entry.txt");
		ArrayList<Process> realTimeProcessRow = new ArrayList<Process>();
		ArrayList<Process> userProcessRow = new ArrayList<Process>();
		ArrayList<Process> readyProcessRow = new ArrayList<Process>();
		ArrayList<Process> readySuspendedProcessRow = new ArrayList<Process>();
		ArrayList<Process> blockedProcessRow = new ArrayList<Process>();
		ArrayList<Process> blockedSuspendedProcessRow = new ArrayList<Process>();
		ArrayList<Process> runningProcessRow = new ArrayList<Process>();
		ArrayList<Process> finalizedProcessRow = new ArrayList<Process>();

		OS os = new OS();
		Memory memory = new Memory();

		int totalProcesses = entryRow.size();
		System.out.println("\nEscalonador iniciou com " + totalProcesses + " processos na lista de FE");

		while (finalizedProcessRow.size() != totalProcesses) {

			while (entryRow.size() > 0 && entryRow.get(0).arrivalTime == os.systemTime) {
				if (entryRow.get(0).priority == 0) {
					System.out.println("P" + entryRow.get(0).id + " saiu da FE e entrou em FTR");
					realTimeProcessRow.add(entryRow.remove(0));
				} else {
					System.out.println("P" + entryRow.get(0).id + " saiu da FE e entrou em FU");
					userProcessRow.add(entryRow.remove(0));
				}
			}

			SchedulerLow.checkResources(os.resourceManager, blockedProcessRow, blockedSuspendedProcessRow,
					runningProcessRow, readyProcessRow, readySuspendedProcessRow, os.cpus);

			Scheduler.schedulerLong(os.resourceManager, readyProcessRow, readySuspendedProcessRow, blockedProcessRow,
					blockedSuspendedProcessRow, realTimeProcessRow, userProcessRow, runningProcessRow, memory, os.cpus);

			if (readySuspendedProcessRow.size() > 0 || blockedSuspendedProcessRow.size() > 0) {
				Scheduler.tryToDetachSuspendedProcesses(os.resourceManager, readyProcessRow, readySuspendedProcessRow,
						blockedProcessRow, blockedSuspendedProcessRow, memory);
			}

			SchedulerLow.run(os.systemTime, memory, os.resourceManager, blockedProcessRow, blockedSuspendedProcessRow,
					readyProcessRow, readySuspendedProcessRow, runningProcessRow, finalizedProcessRow, os.cpus, os);

			os.systemTime = os.systemTime;

			System.out.println("\nFinalizou tempo: " + os.systemTime);
			System.out.println("------------------------------------");

			os.printSO();
			memory.printMemory();
			Utils.printRows(readyProcessRow, readySuspendedProcessRow, blockedProcessRow, blockedSuspendedProcessRow,
					finalizedProcessRow, realTimeProcessRow, userProcessRow, entryRow);
			os.passesTime();

			scan.nextLine();
		}

		scan.close();
	}

}