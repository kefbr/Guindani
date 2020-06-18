package process;

import java.util.ArrayList;

public class SchedulerLow {

    public static void run(int systemTime, Memory memory, ResourceManager resourceManager,
            ArrayList<Process> blockedProcessRow, ArrayList<Process> blockedSuspendedProcessRow,
            ArrayList<Process> readyProcessRow, ArrayList<Process> readySuspendedProcessRow,
            ArrayList<Process> runningProcessRow, ArrayList<Process> finalizedProcessRow, ArrayList<CPU> cpus, OS os) {
        moveBlockedToReady(blockedProcessRow, readyProcessRow);
        checkResources(resourceManager, blockedProcessRow, runningProcessRow, cpus);
        deallocateProcessInCPU(systemTime, memory, cpus, readyProcessRow, runningProcessRow, finalizedProcessRow);
        if ((readyProcessRow.size() == 0 && readySuspendedProcessRow.size() > 0)
                || (blockedProcessRow.size() == 0 && blockedSuspendedProcessRow.size() > 0)) {
            Scheduler.schedulerMidActive(os.resourceManager, readyProcessRow, readySuspendedProcessRow,
                    blockedProcessRow, blockedSuspendedProcessRow, memory);
            checkResources(resourceManager, blockedProcessRow, runningProcessRow, cpus);
        }
        allocateProcessInCPU(cpus, readyProcessRow, runningProcessRow, systemTime);
    }

    private static void moveBlockedToReady(ArrayList<Process> blockedProcessRow, ArrayList<Process> readyProcessRow) {

        int i = 0;

        while (blockedProcessRow.size() > i) {
            Process process = blockedProcessRow.get(i);
            boolean isItReady = true;

            for (Resource resource : process.resourceList)
                if (resource.getUsageTime() > 0 && resource.processId != 0)
                    isItReady = false;

            if (isItReady) {
                for (Resource resource : process.resourceList)
                    resource.free();
                process.resourceList = new ArrayList<Resource>();
                blockedProcessRow.remove(i).insert(readyProcessRow);
                i--;
            }
            i++;
        }
    }

    private static void allocateProcessInCPU(ArrayList<CPU> cpus, ArrayList<Process> readyProcessRow,
            ArrayList<Process> runningProcessRow, int systemTime) {

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
                runningProcessRow.add(process);

                if (process.initTime == 0)
                    process.initTime = systemTime;
            }
        }
    }

    private static void checkResources(ResourceManager resourceManager, ArrayList<Process> blockedProcessRow,
            ArrayList<Process> runningProcessRow, ArrayList<CPU> cpus) {

        for (CPU cpu : cpus) {
            Process process = cpu.process;
            if (process != null
                    && (process.printerAmount + process.cdAmount + process.scannerAmount + process.modemAmount > 0)) {
                if (cpu.quantum == 0) {
                    runningProcessRow.remove(cpu.positionList).insert(blockedProcessRow);
                    updateCPU(cpus, cpu.positionList);
                    if (process.printerAmount > 0
                            && (!resourceManager.printer1.blockedProcess || !resourceManager.printer2.blockedProcess)) {
                        if (process.printerAmount == 1) {
                            if (resourceManager.printer1.processId != process.id) {
                                process.resourceList.add(resourceManager.printer2);
                                resourceManager.printer2.blockedProcess = true;
                                process.printerAmount--;
                            } else {
                                process.resourceList.add(resourceManager.printer1);
                                resourceManager.printer1.blockedProcess = true;
                                process.printerAmount--;
                            }
                        } else if (process.printerAmount == 2 && !resourceManager.printer1.blockedProcess
                                && !resourceManager.printer2.blockedProcess) {
                            process.resourceList.add(resourceManager.printer1);
                            process.resourceList.add(resourceManager.printer2);
                            resourceManager.printer1.blockedProcess = true;
                            resourceManager.printer2.blockedProcess = true;
                            process.printerAmount -= 2;
                        }
                    }
                    if (process.cdAmount > 0
                            && (!resourceManager.cd1.blockedProcess || !resourceManager.cd2.blockedProcess)) {
                        if (process.cdAmount == 1) {
                            if (resourceManager.cd1.processId != process.id) {
                                process.resourceList.add(resourceManager.cd2);
                                resourceManager.cd2.blockedProcess = true;
                                process.cdAmount--;
                            } else {
                                process.resourceList.add(resourceManager.cd1);
                                resourceManager.cd1.blockedProcess = true;
                                process.cdAmount--;
                            }
                        } else if (process.cdAmount == 2 && !resourceManager.cd1.blockedProcess
                                && !resourceManager.cd2.blockedProcess) {
                            process.resourceList.add(resourceManager.cd1);
                            process.resourceList.add(resourceManager.cd2);
                            resourceManager.cd1.blockedProcess = true;
                            resourceManager.cd2.blockedProcess = true;
                            process.cdAmount -= 2;
                        }
                    }
                    if (process.scannerAmount > 0 && !resourceManager.scanner.blockedProcess) {
                        process.resourceList.add(resourceManager.scanner);
                        resourceManager.scanner.blockedProcess = true;
                        process.scannerAmount--;
                    }
                    if (process.modemAmount > 0 && !resourceManager.modem.blockedProcess) {
                        process.resourceList.add(resourceManager.modem);
                        resourceManager.modem.blockedProcess = true;
                        process.modemAmount--;
                    }
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
                    finalizedProcessRow.add(runningProcessRow.remove(cpu.positionList));
                    updateCPU(cpus, cpu.positionList);
                    memory.available += process.memoryAmount;
                    cpu.process = null;
                    cpu.quantum = 0;
                } else {
                    if (process.row == 3)
                        process.row = 1;
                    else
                        process.row++;
                    runningProcessRow.remove(cpu.positionList).insert(readyProcessRow);
                    updateCPU(cpus, cpu.positionList);
                    cpu.process = null;
                    cpu.quantum = 0;
                }
            }
            if (cpu.quantum == 0 && cpu.process != null && process.row >= 1 && process.timeLeft <= 0) {
                process.exitTime = systemTime;
                finalizedProcessRow.add(runningProcessRow.remove(cpu.positionList));
                updateCPU(cpus, cpu.positionList);
                memory.available += process.memoryAmount;
                cpu.process = null;
                cpu.quantum = 0;
            }
        }
    }

    public static void updateCPU(ArrayList<CPU> cpus, int i) {
        for (int j = 0; j < cpus.size(); j++) {
            CPU cpu = cpus.get(j);
            if (cpu.positionList != 0 && cpu.positionList >= i)
                cpu.positionList--;
        }
    }
}