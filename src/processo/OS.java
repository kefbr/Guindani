package processo;

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
        System.out.println("x");
    }
}