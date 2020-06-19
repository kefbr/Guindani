package process;

import java.util.ArrayList;

public class SchedulerLow {

    public static void run(int systemTime, Memory memory, ResourceManager resourceManager,
            ArrayList<Process> blockedProcessRow, ArrayList<Process> blockedSuspendedProcessRow,
            ArrayList<Process> readyProcessRow, ArrayList<Process> readySuspendedProcessRow,
            ArrayList<Process> runningProcessRow, ArrayList<Process> finalizedProcessRow, ArrayList<CPU> cpus, OS os) {
        deallocateProcessInCPU(systemTime, memory, cpus, readyProcessRow, runningProcessRow, finalizedProcessRow);
        if (readySuspendedProcessRow.size() > 0 || blockedSuspendedProcessRow.size() > 0) {
            Scheduler.tryToDetachSuspendedProcesses(os.resourceManager, readyProcessRow, readySuspendedProcessRow,
                    blockedProcessRow, blockedSuspendedProcessRow, memory);
        }
        moveBlockedToReady(blockedProcessRow, readyProcessRow, resourceManager);
        allocateProcessInCPU(cpus, readyProcessRow, runningProcessRow, systemTime);
    }

    private static void moveBlockedToReady(ArrayList<Process> blockedProcessRow, ArrayList<Process> readyProcessRow,
            ResourceManager resourceManager) {

        int i = 0;

        while (blockedProcessRow.size() > i) {
            Process process = blockedProcessRow.get(i);

            var printers = resourceManager.availablePrinters();
            var cds = resourceManager.availableCDs();
            var scanners = resourceManager.availableScanners();
            var modems = resourceManager.availableModems();

            if (process.printerAmount <= printers && process.cdAmount <= cds && process.scannerAmount <= scanners
                    && process.modemAmount <= modems) {
                System.out.println("P" + process.id + " saiu da FB e entrou na FP");
                blockedProcessRow.remove(i).insert(readyProcessRow);
                process.resourceList.addAll(resourceManager.allocateResources(process.id, process.printerAmount,
                        process.scannerAmount, process.modemAmount, process.cdAmount));
                i--;
            }
            i++;
        }
    }

    private static void allocateProcessInCPU(ArrayList<CPU> cpus, ArrayList<Process> readyProcessRow,
            ArrayList<Process> runningProcessRow, int systemTime) {

        boolean breakLine = false;

        for (CPU cpu : cpus) {
            if (cpu.quantum == 0 && readyProcessRow.size() > 0) {
                Process process = readyProcessRow.remove(0);

                cpu.process = process;
                if (process.priority == 0 || process.timeLeft - Utils.DEFAULT_QUANTUM <= 0) {
                    cpu.quantum = process.timeLeft;
                    process.timeLeft = 0;
                } else {
                    cpu.quantum = Utils.DEFAULT_QUANTUM;
                    process.timeLeft -= Utils.DEFAULT_QUANTUM;
                }

                cpu.positionList = runningProcessRow.size();
                if (!breakLine) {
                    System.out.println("");
                    breakLine = true;
                }
                System.out.println("P" + process.id + " saiu da FP e iniciou execução");
                runningProcessRow.add(process);

                if (process.initTime == 0)
                    process.initTime = systemTime;
            }
        }
    }

    public static void checkResources(ResourceManager resourceManager, ArrayList<Process> blockedProcessRow,
            ArrayList<Process> blockedSuspendedProcessRow, ArrayList<Process> runningProcessRow,
            ArrayList<Process> readyProcessRow, ArrayList<Process> readySuspendedProcessRow, ArrayList<CPU> cpus) {

        for (int i = 0; i < readyProcessRow.size(); i++) {
            Process process = readyProcessRow.get(i);
            if (process.printerAmount + process.cdAmount + process.scannerAmount + process.modemAmount > 0) {
                boolean hasEndedResourceTime = false;
                for (Resource resource : process.resourceList) {
                    if (resource.usageTime == 0)
                        hasEndedResourceTime = true;
                }

                if (!hasEndedResourceTime) {
                    System.out.println("P" + process.id + " saiu da FP e entrou na FB");
                    readyProcessRow.remove(i).insert(blockedProcessRow);
                    System.out.println("P" + process.id + " desalocou os recursos utilizados\n");
                    for (Resource resource : process.resourceList) {
                        resource.free();
                    }
                    process.resourceList = new ArrayList<Resource>();
                }
            }
        }

        for (int i = 0; i < readySuspendedProcessRow.size(); i++) {
            Process process = readySuspendedProcessRow.get(i);
            if (process.printerAmount + process.cdAmount + process.scannerAmount + process.modemAmount > 0) {
                boolean hasEndedResourceTime = false;
                for (Resource resource : process.resourceList) {
                    if (resource.usageTime == 0)
                        hasEndedResourceTime = true;
                }

                if (!hasEndedResourceTime) {
                    System.out.println("P" + process.id + " saiu da FPS e entrou na FBS");
                    readySuspendedProcessRow.remove(i).insert(blockedSuspendedProcessRow);
                    System.out.println("P" + process.id + " desalocou os recursos utilizados\n");
                    for (Resource resource : process.resourceList) {
                        resource.free();
                    }
                    process.resourceList = new ArrayList<Resource>();
                }
            }
        }

        for (CPU cpu : cpus) {
            Process process = cpu.process;
            if (process != null
                    && process.printerAmount + process.cdAmount + process.scannerAmount + process.modemAmount > 0) {
                boolean hasEndedResourceTime = false;
                for (Resource resource : process.resourceList) {
                    if (resource.usageTime == 0)
                        hasEndedResourceTime = true;
                }

                if (hasEndedResourceTime) {
                    runningProcessRow.remove(cpu.positionList).insert(blockedProcessRow);
                    System.out.println("P" + process.id + " parou execução e entrou na FB");
                    process.timeLeft += cpu.quantum;
                    updateCPUs(cpus, cpu.positionList);
                    for (Resource resource : process.resourceList) {
                        resource.free();
                    }
                    process.resourceList = new ArrayList<Resource>();
                    System.out.println("P" + process.id + " desalocou os recursos utilizados\n");
                    cpu.process = null;
                    cpu.quantum = 0;
                }
            }
        }

    }

    private static void deallocateProcessInCPU(int systemTime, Memory memory, ArrayList<CPU> cpus,
            ArrayList<Process> readyProcessRow, ArrayList<Process> runningProcessRow,
            ArrayList<Process> finalizedProcessRow) {

        for (CPU cpu : cpus) {
            Process process = cpu.process;
            if (cpu.quantum == 0 && process != null) {
                if (process.timeLeft == 0) {
                    process.exitTime = systemTime;
                    System.out.println("P" + process.id + " finalizou execução e entrou na FF");
                    finalizedProcessRow.add(runningProcessRow.remove(cpu.positionList));
                    updateCPUs(cpus, cpu.positionList);
                    System.out.println(
                            "Foi desalocado " + process.memoryAmount + "MBytes da memória pelo P" + process.id);
                    memory.available += process.memoryAmount;
                    System.out.println("P" + process.id + " desalocou os recursos utilizados\n");
                    for (Resource resource : process.resourceList) {
                        resource.free();
                    }
                    cpu.process = null;
                    cpu.quantum = 0;
                } else {
                    if (process.row == 3)
                        process.row = 1;
                    else
                        process.row++;
                    System.out.println("P" + process.id + " parou execução e entrou na FP");
                    runningProcessRow.remove(cpu.positionList).insert(readyProcessRow);
                    updateCPUs(cpus, cpu.positionList);
                    cpu.process = null;
                    cpu.quantum = 0;
                }
            }
        }
    }

    public static void updateCPUs(ArrayList<CPU> cpus, int i) {
        for (int j = 0; j < cpus.size(); j++) {
            CPU cpu = cpus.get(j);
            if (cpu.positionList != 0 && cpu.positionList >= i)
                cpu.positionList--;
        }
    }
}