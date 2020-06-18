package process;

import java.util.ArrayList;

public class OS {
    ArrayList<CPU> cpus = new ArrayList<CPU>();
    int systemTime = 0;
    ResourceManager resourceManager = new ResourceManager();
    ArrayList<Process> endedProcessRow = new ArrayList<Process>();

    public OS() {
        for (int i = 0; i < 4; i++) {
            this.cpus.add(new CPU());
        }
    }

    public void passesTime() {
        for (int i = 0; i < this.cpus.size(); i++) {
            CPU cpu = this.cpus.get(i);
            if (cpu.process != null)
                cpu.quantum--;
        }

        this.resourceManager.updateUsageTime();
        this.systemTime++;
    }

    public void printSO() {
        System.out.println("\n\nTEMPO DE EXECUCAO: " + this.systemTime);
        System.out.println("\nCPUs:\n");
        System.out.println("+-------+-------+-------+-------+");
        System.out.println("| CPU 0 | CPU 1 | CPU 2 | CPU 3 |");
        System.out.println("+-------+-------+-------+-------+");
        System.out.print("|");
        for (int i = 0; i < this.cpus.size(); i++) {
            if (this.cpus.get(i).process != null) {
                System.out.print("  P" + this.cpus.get(i).process.id + "   ");
            } else {
                System.out.print("  --   ");
            }
            System.out.print("|");
        }
        System.out.println("\n+-------+-------+-------+-------+");
        System.out.println("\n\nDISPOSITIVOS\t|\tPROCESSO \t|\tTempo Restante\n");
        if (this.resourceManager.printer1.processId == 0)
            System.out.println("Impressora 1\t|\t   --   \t|\t\t" + this.resourceManager.printer1.getUsageTime());
        else
            System.out.println("Impressora 1\t|\t   P" + this.resourceManager.printer1.processId + "   \t|\t\t"
                    + this.resourceManager.printer1.getUsageTime());
        if (this.resourceManager.printer2.processId == 0)
            System.out.println("Impressora 2\t|\t   --   \t|\t\t" + this.resourceManager.printer2.getUsageTime());
        else
            System.out.println("Impressora 2\t|\t   P" + this.resourceManager.printer2.processId + "   \t|\t\t"
                    + this.resourceManager.printer2.getUsageTime());
        if (this.resourceManager.cd1.processId == 0)
            System.out.println("CD 1        \t|\t   --   \t|\t\t" + this.resourceManager.cd1.getUsageTime());
        else
            System.out.println("CD 1        \t|\t   P" + this.resourceManager.cd1.processId + "   \t|\t\t"
                    + this.resourceManager.cd1.getUsageTime());
        if (this.resourceManager.cd2.processId == 0)
            System.out.println("CD 2        \t|\t   --   \t|\t\t" + this.resourceManager.cd2.getUsageTime());
        else
            System.out.println("CD 2        \t|\t   P" + this.resourceManager.cd2.processId + "   \t|\t\t"
                    + this.resourceManager.cd2.getUsageTime());
        if (this.resourceManager.scanner.processId == 0)
            System.out.println("Scanner     \t|\t   --   \t|\t\t" + this.resourceManager.scanner.getUsageTime());
        else
            System.out.println("Scanner     \t|\t   P" + this.resourceManager.scanner.processId + "   \t|\t\t"
                    + this.resourceManager.scanner.getUsageTime());
        if (this.resourceManager.modem.processId == 0)
            System.out.println("Modem       \t|\t   --   \t|\t\t" + this.resourceManager.modem.getUsageTime());
        else
            System.out.println("Modem       \t|\t   P" + this.resourceManager.modem.processId + "   \t|\t\t"
                    + this.resourceManager.modem.getUsageTime());
    }
}