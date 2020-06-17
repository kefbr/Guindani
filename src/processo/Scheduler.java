package processo;

import java.util.ArrayList;

public class Scheduler {

    public static void schedulerLong(ResourceManager resourceManager, ArrayList<Process> readyProcessRow,
            ArrayList<Process> readySuspendedProcessRow, ArrayList<Process> blockedProcessRow,
            ArrayList<Process> blockedSuspendedProcessRow, ArrayList<Process> realTimeProcessRow,
            ArrayList<Process> userProcessRow, Memory memory) {
        int iterator = 0;
        while (realTimeProcessRow.size() > 0) {
            Process process = realTimeProcessRow.get(iterator);
            if (memory.available >= process.memoryUse) {
                process.insert(readyProcessRow);
                memory.available -= process.memoryUse;
            } else {

            }
        }

    }

    public void schedulerMidSuspend(int memoryAmount, ArrayList<Process> readyProcessRow,
            ArrayList<Process> readySuspendedProcessRow, ArrayList<Process> blockedProcessRow,
            ArrayList<Process> blockedSuspendedProcessRow, int minPriority, Memory memory) {
        int priority = 3;
        while (memory.available <= memoryAmount && priority > minPriority) {
            for (int i = blockedProcessRow.size(); i > 0; i--) {
                if (blockedProcessRow.get(i).priority > priority) {
                    continue;
                } else if (blockedProcessRow.get(i).priority < priority) {
                    break;
                } else {
                    blockedProcessRow.get(i).insert(blockedSuspendedProcessRow);
                    memory.available += blockedProcessRow.get(i).memoryUse;
                    blockedProcessRow.remove(i);
                    if (memory.available > memoryAmount) {
                        return;
                    }
                }
            }
            for (int i = readyProcessRow.size(); i > 0; i--) {
                if (readyProcessRow.get(i).priority > priority) {
                    continue;
                } else if (readyProcessRow.get(i).priority < priority) {
                    break;
                } else {
                    readyProcessRow.get(i).insert(readySuspendedProcessRow);
                    memory.available += readyProcessRow.get(i).memoryUse;
                    readyProcessRow.remove(i);
                    if (memory.available > memoryAmount) {
                        return;
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
                    if (memory.available - readySuspendedProcessRow.get(i).modemAmount >= 0
                            || readySuspendedProcessRow.get(i).printerAmount <= printers
                            || readySuspendedProcessRow.get(i).cdAmount <= cds
                            || (readySuspendedProcessRow.get(i).scannerAmount == 0 || scanner)
                            || (readySuspendedProcessRow.get(i).modemAmount == 0 || modem)) {
                        readySuspendedProcessRow.get(i).insert(readyProcessRow);
                        memory.available -= readySuspendedProcessRow.get(i).memoryUse;
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
                    if (memory.available - blockedSuspendedProcessRow.get(i).memoryUse > 0) {
                        blockedSuspendedProcessRow.get(i).insert(blockedProcessRow);
                        memory.available -= blockedSuspendedProcessRow.get(i).memoryUse;
                        blockedSuspendedProcessRow.remove(i);
                        if (memory.available == 0) {
                            return;
                        }
                        priority -= 1;
                        break;
                    }
                }
                priority += 1;
            }
        }
    }
}