package process;

import java.util.ArrayList;

public class Scheduler {

    public static void schedulerLong(final ResourceManager resourceManager, final ArrayList<Process> readyProcessRow,
            final ArrayList<Process> readySuspendedProcessRow, final ArrayList<Process> blockedProcessRow,
            final ArrayList<Process> blockedSuspendedProcessRow, final ArrayList<Process> realTimeProcessRow,
            final ArrayList<Process> userProcessRow, final ArrayList<Process> runningProcessRow, final Memory memory,
            final ArrayList<CPU> cpus) {
        int i = 0;
        while (realTimeProcessRow.size() > 0) {
            System.out.print("\n");
            final Process process = realTimeProcessRow.get(i);
            if (memory.available >= process.memoryAmount) {
                System.out.println("P" + process.id + " saiu da FTR e entrou na FP");
                process.insert(readyProcessRow);
                System.out.println("Foi alocado " + process.memoryAmount + "MBytes da memória");
                memory.available -= process.memoryAmount;
            } else {
                tryToFreeMemory(process.memoryAmount, readyProcessRow, readySuspendedProcessRow, blockedProcessRow,
                        blockedSuspendedProcessRow, runningProcessRow, process.priority, memory, cpus);
                if (memory.available >= process.memoryAmount) {
                    System.out.println("P" + process.id + " saiu da FTR e entrou na FP");
                    process.insert(readyProcessRow);
                    System.out.println("Foi alocado " + process.memoryAmount + "MBytes da memória para P" + process.id);
                    memory.available -= process.memoryAmount;
                } else {
                    System.out.println("P" + process.id + " saiu da FTR e entrou na FPS");
                    process.insert(readySuspendedProcessRow);
                }
            }
            realTimeProcessRow.remove(i);
        }
        while (i < userProcessRow.size()) {
            System.out.print("\n");
            var printers = resourceManager.availablePrinters();
            var cds = resourceManager.availableCDs();
            var scanners = resourceManager.availableScanners();
            var modems = resourceManager.availableModems();
            final Process process = userProcessRow.get(i);
            if (process.printerAmount <= printers && process.cdAmount <= cds && process.scannerAmount <= scanners
                    && process.modemAmount <= modems) {
                if (memory.available >= process.memoryAmount) {
                    System.out.println("P" + process.id + " saiu da FU e entrou na FP");
                    userProcessRow.remove(i).insert(readyProcessRow);
                    System.out.println("Foi alocado " + process.memoryAmount + "MBytes da memória para P" + process.id);
                    memory.available -= process.memoryAmount;
                    process.resourceList.addAll(resourceManager.allocateResources(process.id, process.printerAmount,
                            process.scannerAmount, process.modemAmount, process.cdAmount));
                    i -= 1;
                } else {
                    tryToFreeMemory(process.memoryAmount, readyProcessRow, readySuspendedProcessRow, blockedProcessRow,
                            blockedSuspendedProcessRow, runningProcessRow, process.priority, memory, cpus);
                    if (memory.available >= process.memoryAmount) {
                        System.out.println("P" + process.id + " saiu da FU e entrou na FP");
                        userProcessRow.remove(i).insert(readyProcessRow);
                        System.out.println(
                                "Foi alocado " + process.memoryAmount + "MBytes da memória para P" + process.id);
                        memory.available -= process.memoryAmount;
                        // System.out.print está dentro do método
                        process.resourceList.addAll(resourceManager.allocateResources(process.id, process.printerAmount,
                                process.scannerAmount, process.modemAmount, process.cdAmount));
                        i -= 1;
                    }
                }
            } else {
                System.out.println("P" + process.id + " não tem recursos disponíveis, continua na FE");
            }
            i += 1;
        }
    }

    public static void tryToFreeMemory(final int memoryAmount, final ArrayList<Process> readyProcessRow,
            final ArrayList<Process> readySuspendedProcessRow, final ArrayList<Process> blockedProcessRow,
            final ArrayList<Process> blockedSuspendedProcessRow, final ArrayList<Process> runningProcessRow,
            final int minPriority, final Memory memory, final ArrayList<CPU> cpus) {
        int priority = 3;

        System.out.println("Tentando liberar " + memoryAmount + "MBytes da memória");

        while (memory.available <= memoryAmount && priority > minPriority) {
            for (int i = blockedProcessRow.size() - 1; i > -1; i--) {
                Process process = blockedProcessRow.get(i);
                if (process.priority > priority) {
                    continue;
                } else if (process.priority < priority) {
                    break;
                } else {
                    System.out.println("P" + process.id + " saiu da FB e entrou na FBS");
                    blockedProcessRow.remove(i).insert(blockedSuspendedProcessRow);
                    System.out.println(
                            "Foi desalocado " + process.memoryAmount + "MBytes da memória pelo P" + process.id);
                    memory.available += process.memoryAmount;
                    if (memory.available >= memoryAmount) {
                        System.out.println("Memória suficiente liberada!");
                        return;
                    }
                }
            }
            for (int i = readyProcessRow.size() - 1; i > -1; i--) {
                Process process = readyProcessRow.get(i);
                if (process.priority > priority) {
                    continue;
                } else if (process.priority < priority) {
                    break;
                } else {
                    System.out.println("P" + process.id + " saiu da FP e entrou na FPS");
                    readyProcessRow.remove(i).insert(readySuspendedProcessRow);
                    System.out.println(
                            "Foi desalocado " + process.memoryAmount + "MBytes da memória pelo P" + process.id);
                    memory.available += process.memoryAmount;
                    if (memory.available >= memoryAmount) {
                        return;
                    }
                }
            }
            if (minPriority == 0 && priority > 0) {
                for (final CPU cpu : cpus) {
                    final Process process = cpu.process;
                    if (process.priority > priority) {
                        continue;
                    } else if (process.priority < priority) {
                        continue;
                    } else {
                        System.out.println("P" + process.id + " parou execução e entrou na FPS");
                        runningProcessRow.remove(cpu.positionList).insert(readySuspendedProcessRow);
                        SchedulerLow.updateCPUs(cpus, cpu.positionList);
                        System.out.println(
                                "Foi desalocado " + process.memoryAmount + "MBytes da memória pelo P" + process.id);
                        memory.available += process.memoryAmount;
                        process.timeLeft += cpu.quantum;
                        cpu.quantum = 0;
                        cpu.process = null;
                        if (memory.available >= memoryAmount) {
                            System.out.println("Memória suficiente liberada!");
                            return;
                        }
                    }
                }
            }
            priority -= 1;
        }
        if (memory.available <= memoryAmount) {
            System.out.println("Memória liberada não foi suficiente!");
        }
    }

    public static void tryToDetachSuspendedProcesses(final ResourceManager resourceManager,
            final ArrayList<Process> readyProcessRow, final ArrayList<Process> readySuspendedProcessRow,
            final ArrayList<Process> blockedProcessRow, final ArrayList<Process> blockedSuspendedProcessRow,
            final Memory memory) {
        int priority = 0;
        while (priority <= 3) {
            for (int i = 0; readySuspendedProcessRow.size() > i; i++) {
                Process process = readySuspendedProcessRow.get(i);
                if (process.priority < priority) {
                    continue;
                } else if (process.priority > priority) {
                    break;
                } else {
                    if (memory.available - process.memoryAmount >= 0) {
                        System.out.println("P" + process.id + " saiu da FPS e entrou na FP");
                        readySuspendedProcessRow.remove(i).insert(readyProcessRow);
                        System.out.println(
                                "Foi alocado " + process.memoryAmount + "MBytes da memória pelo P" + process.id);
                        memory.available -= process.memoryAmount;
                        priority -= 1;
                        if (memory.available == 0)
                            return;
                        break;
                    }
                }
            }

            for (int i = 0; blockedSuspendedProcessRow.size() > i; i++) {
                Process process = blockedSuspendedProcessRow.get(i);
                if (process.priority < priority) {
                    continue;
                } else if (process.priority > priority) {
                    break;
                } else {
                    if (memory.available - process.memoryAmount > 0) {
                        System.out.println("P" + process.id + " saiu da FBS e entrou na FB");
                        blockedSuspendedProcessRow.remove(i).insert(blockedProcessRow);
                        System.out.println(
                                "Foi alocado " + process.memoryAmount + "MBytes da memória pelo P" + process.id);
                        memory.available -= process.memoryAmount;
                        if (memory.available == 0)
                            return;
                        priority -= 1;
                        break;
                    }
                }
            }
            priority += 1;
        }
    }
}