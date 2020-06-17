package processo;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) throws FileNotFoundException {
		Scanner scan = new Scanner(System.in);

		ArrayList<Process> entryRow = Utils.initializeEntry("entrada.txt");

		ArrayList<Process> realTimeProcessRow = new ArrayList<Process>();
		ArrayList<Process> userProcessRow = new ArrayList<Process>();
		ArrayList<Process> readyProcessRow = new ArrayList<Process>();
		ArrayList<Process> readySuspendedProcessRow = new ArrayList<Process>();
		ArrayList<Process> blockedProcessRow = new ArrayList<Process>();
		ArrayList<Process> blockedSuspendedProcessRow = new ArrayList<Process>();
		ArrayList<Process> runningProcessList = new ArrayList<Process>();
		ArrayList<Process> finalizedProcessList = new ArrayList<Process>();

		OS os = new OS();
		Memory memory = new Memory();

		int totalProcesses = entryRow.size();

		while (finalizedProcessList.size() != totalProcesses) {

			while (entryRow.size() > 0 && entryRow.get(0).arrivalTime == os.systemTime) {
				if (entryRow.get(0).priority == 0) {
					realTimeProcessRow.add(entryRow.remove(0));
				} else {
					userProcessRow.add(entryRow.remove(0));
				}
			}

			Scheduler.schedulerLong(os.resourceManager, readyProcessRow, readySuspendedProcessRow, blockedProcessRow,
					blockedSuspendedProcessRow, realTimeProcessRow, userProcessRow, memory);

			if ((readyProcessRow.size() == 0 && readySuspendedProcessRow.size() > 0)
					|| (blockedProcessRow.size() == 0 && blockedSuspendedProcessRow.size() > 0)) {
				Scheduler.schedulerMidActive(os.resourceManager, readyProcessRow, readySuspendedProcessRow,
						blockedProcessRow, blockedSuspendedProcessRow, memory);
			}

			// rodadaDeEscalonadorCurto(so.tempoSistema, memoria, so.gerenciadorIO,
			// processosBloqueados, processosProntos, processosExecutando,
			// processosFinalizados, so.cpus)

			scan.nextLine();

			os.printSO();
			memory.printMemory();
			// imprimeFilas(processosProntos, processosProntosSuspenso, processosBloqueados,
			// processosBloqueadosSuspenso, processosFinalizados)
			os.passesTime();
		}
	}

}