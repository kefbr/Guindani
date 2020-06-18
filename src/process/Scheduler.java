package process;

import java.util.ArrayList;

public class Scheduler {

    public static void schedulerLong(ResourceManager resourceManager, ArrayList<Process> readyProcessRow,
            ArrayList<Process> readySuspendedProcessRow, ArrayList<Process> blockedProcessRow,
            ArrayList<Process> blockedSuspendedProcessRow, ArrayList<Process> realTimeProcessRow,
            ArrayList<Process> userProcessRow, ArrayList<Process> runningProcessRow, Memory memory,
            ArrayList<CPU> cpus) {
        int iterator = 0;
        while (realTimeProcessRow.size() > 0) {
            Process process = realTimeProcessRow.get(iterator);
            if (memory.available >= process.memoryAmount) {
                process.insert(readyProcessRow);
                memory.available -= process.memoryAmount;
            } else {
                schedulerMidSuspend(process.memoryAmount, readyProcessRow, readySuspendedProcessRow, blockedProcessRow,
                        blockedSuspendedProcessRow, runningProcessRow, process.priority, memory, cpus);
                if (memory.available >= process.memoryAmount) {
                    process.insert(readyProcessRow);
                    memory.available -= process.memoryAmount;
                } else {
                    process.insert(readySuspendedProcessRow);
                }
            }
            realTimeProcessRow.remove(iterator);
        }
        while (iterator < userProcessRow.size()) {
            var printers = resourceManager.availablePrinters();
            var cds = resourceManager.availableCDs();
            var scanner = resourceManager.isScannerAvailable();
            var modem = resourceManager.isScannerAvailable();
            for (Process process : readyProcessRow) {
                printers -= process.printerAmount;
                cds -= process.cdAmount;
                if (process.scannerAmount == 1) {
                    scanner = scanner && false;
                } else {
                    scanner = scanner && true;
                }
                if (process.modemAmount == 1) {
                    modem = modem && false;
                } else {
                    modem = modem && true;
                }
            }
            if (userProcessRow.get(iterator).printerAmount <= printers && userProcessRow.get(iterator).cdAmount <= cds
                    && (userProcessRow.get(iterator).scannerAmount == 0 || scanner)
                    && (userProcessRow.get(iterator).modemAmount == 0 || modem)) {
                if (memory.available >= userProcessRow.get(iterator).memoryAmount) {
                    userProcessRow.get(iterator).insert(readyProcessRow);
                    memory.available -= userProcessRow.get(iterator).memoryAmount;
                    resourceManager.allocateCD(userProcessRow.get(iterator).id, userProcessRow.get(iterator).cdAmount);
                    resourceManager.allocatePrinter(userProcessRow.get(iterator).id,
                            userProcessRow.get(iterator).printerAmount);
                    resourceManager.allocateScanner(userProcessRow.get(iterator).id,
                            userProcessRow.get(iterator).scannerAmount);
                    resourceManager.allocateModem(userProcessRow.get(iterator).id,
                            userProcessRow.get(iterator).modemAmount);
                    userProcessRow.remove(iterator);
                    iterator -= 1;
                } else {
                    schedulerMidSuspend(userProcessRow.get(iterator).memoryAmount, readyProcessRow,
                            readySuspendedProcessRow, blockedProcessRow, blockedSuspendedProcessRow, runningProcessRow,
                            userProcessRow.get(iterator).priority, memory, cpus);
                    if (memory.available >= userProcessRow.get(iterator).memoryAmount) {
                        userProcessRow.get(iterator).insert(readyProcessRow);
                        memory.available -= userProcessRow.get(iterator).memoryAmount;
                        resourceManager.allocateCD(userProcessRow.get(iterator).id,
                                userProcessRow.get(iterator).cdAmount);
                        resourceManager.allocatePrinter(userProcessRow.get(iterator).id,
                                userProcessRow.get(iterator).printerAmount);
                        resourceManager.allocateScanner(userProcessRow.get(iterator).id,
                                userProcessRow.get(iterator).scannerAmount);
                        resourceManager.allocateModem(userProcessRow.get(iterator).id,
                                userProcessRow.get(iterator).modemAmount);
                        userProcessRow.remove(iterator);
                        iterator -= 1;
                    }
                }
            }
            iterator += 1;
        }
    }

    public static void schedulerMidSuspend(int memoryAmount, ArrayList<Process> readyProcessRow,
            ArrayList<Process> readySuspendedProcessRow, ArrayList<Process> blockedProcessRow,
            ArrayList<Process> blockedSuspendedProcessRow, ArrayList<Process> runningProcessRow, int minPriority,
            Memory memory, ArrayList<CPU> cpus) {
        int priority = 3;
        while (memory.available <= memoryAmount && priority > minPriority) {
            for (int i = blockedProcessRow.size() - 1; i > -1; i--) {
                if (blockedProcessRow.get(i).priority > priority) {
                    continue;
                } else if (blockedProcessRow.get(i).priority < priority) {
                    break;
                } else {
                    blockedProcessRow.get(i).insert(blockedSuspendedProcessRow);
                    memory.available += blockedProcessRow.get(i).memoryAmount;
                    blockedProcessRow.remove(i);
                    if (memory.available > memoryAmount) {
                        return;
                    }
                }
            }
            for (int i = readyProcessRow.size() - 1; i > -1; i--) {
                if (readyProcessRow.get(i).priority > priority) {
                    continue;
                } else if (readyProcessRow.get(i).priority < priority) {
                    break;
                } else {
                    readyProcessRow.get(i).insert(readySuspendedProcessRow);
                    memory.available += readyProcessRow.get(i).memoryAmount;
                    readyProcessRow.remove(i);
                    if (memory.available > memoryAmount) {
                        return;
                    }
                }
            }
            if (minPriority == 0 && priority > 0) {
                for (CPU cpu : cpus) {
                    Process process = cpu.process;
                    if (process.priority > priority) {
                        continue;
                    } else if (process.priority < priority) {
                        continue;
                    } else {
                        runningProcessRow.remove(cpu.positionList).insert(readySuspendedProcessRow);
                        SchedulerLow.updateCPU(cpus, cpu.positionList);
                        memory.available += process.memoryAmount;
                        process.timeLeft += cpu.quantum;
                        cpu.quantum = 0;
                        cpu.process = null;
                        if (memory.available >= memoryAmount) {
                            return;
                        }
                    }
                }
            }
            priority -= 1;
        }
    }

    public static void schedulerMidActive(ResourceManager resourceManager, ArrayList<Process> readyProcessRow,
            ArrayList<Process> readySuspendedProcessRow, ArrayList<Process> blockedProcessRow,
            ArrayList<Process> blockedSuspendedProcessRow, Memory memory) {
        int priority = 0;
        while (priority <= 3) {
            for (int i = 0; readySuspendedProcessRow.size() > i; i++) {
                if (readySuspendedProcessRow.get(i).priority < priority) {
                    continue;
                } else if (readySuspendedProcessRow.get(i).priority > priority) {
                    break;
                } else {
                    var printers = resourceManager.availablePrinters();
                    var cds = resourceManager.availableCDs();
                    var scanner = resourceManager.isScannerAvailable();
                    var modem = resourceManager.isScannerAvailable();
                    for (Process process : readyProcessRow) {
                        printers -= process.printerAmount;
                        cds -= process.cdAmount;
                        if (process.scannerAmount == 1) {
                            scanner = scanner && false;
                        } else {
                            scanner = scanner && true;
                        }
                        if (process.modemAmount == 1) {
                            modem = modem && false;
                        } else {
                            modem = modem && true;
                        }
                    }
                    if (memory.available - readySuspendedProcessRow.get(i).memoryAmount >= 0
                            && readySuspendedProcessRow.get(i).printerAmount <= printers
                            && readySuspendedProcessRow.get(i).cdAmount <= cds
                            && (readySuspendedProcessRow.get(i).scannerAmount == 0 || scanner)
                            && (readySuspendedProcessRow.get(i).modemAmount == 0 || modem)) {
                        readySuspendedProcessRow.get(i).insert(readyProcessRow);
                        memory.available -= readySuspendedProcessRow.get(i).memoryAmount;
                        readySuspendedProcessRow.remove(i);
                        priority -= 1;
                        if (memory.available == 0) {
                            return;
                        }
                        break;
                    }
                }
            }

            for (int i = 0; blockedSuspendedProcessRow.size() > i; i++) {
                if (blockedSuspendedProcessRow.get(i).priority < priority) {
                    continue;
                } else if (blockedSuspendedProcessRow.get(i).priority > priority) {
                    break;
                } else {
                    if (memory.available - blockedSuspendedProcessRow.get(i).memoryAmount > 0) {
                        memory.available -= blockedSuspendedProcessRow.get(i).memoryAmount;
                        blockedSuspendedProcessRow.remove(i).insert(blockedProcessRow);
                        if (memory.available == 0) {
                            return;
                        }
                        priority -= 1;
                        break;
                    }
                }
            }
            priority += 1;
        }
    }
}