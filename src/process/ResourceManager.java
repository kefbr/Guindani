package process;

import java.util.ArrayList;

public class ResourceManager {

    Resource printer1;
    Resource printer2;
    Resource cd1;
    Resource cd2;
    Resource scanner;
    Resource modem;

    public ResourceManager() {
        this.printer1 = new Resource();
        this.printer2 = new Resource();
        this.cd1 = new Resource();
        this.cd2 = new Resource();
        this.scanner = new Resource();
        this.modem = new Resource();
    }

    public int availablePrinters() {
        int amount = 0;

        if (printer1.isAvailable())
            amount++;

        if (printer2.isAvailable())
            amount++;

        return amount;
    }

    public ArrayList<Resource> allocateResources(int processId, int printers, int scanners, int modems, int cds) {
        ArrayList<Resource> resourceList = new ArrayList<Resource>();
        if (printers > 0)
            System.out.println("Foi alocado " + printers + " Impressora para P" + processId);
        resourceList.addAll(this.allocatePrinter(processId, printers));
        if (scanners > 0)
            System.out.println("Foi alocado " + scanners + " Scanner para P" + processId);
        resourceList.addAll(this.allocateScanner(processId, scanners));
        if (modems > 0)
            System.out.println("Foi alocado " + modems + " Modem para P" + processId);
        resourceList.addAll(this.allocateModem(processId, modems));
        if (cds > 0)
            System.out.println("Foi alocado " + cds + " CD para P" + processId);
        resourceList.addAll(this.allocateCD(processId, cds));
        return resourceList;
    }

    public ArrayList<Resource> allocatePrinter(final int processId, final int numberToAllocate) {
        ArrayList<Resource> resourceList = new ArrayList<Resource>();
        if (numberToAllocate == 1) {
            if (this.printer1.isAvailable())
                resourceList.add(this.printer1.use(processId));
            else if (this.printer2.isAvailable())
                resourceList.add(this.printer2.use(processId));
        } else if (numberToAllocate == 2)
            if (this.printer1.isAvailable() && this.printer2.isAvailable()) {
                resourceList.add(this.printer1.use(processId));
                resourceList.add(this.printer2.use(processId));
            }
        return resourceList;
    }

    public int availableCDs() {
        int amount = 0;

        if (cd1.isAvailable())
            amount++;

        if (cd2.isAvailable())
            amount++;

        return amount;
    }

    public ArrayList<Resource> allocateCD(final int processId, final int numberToAllocate) {
        ArrayList<Resource> resourceList = new ArrayList<Resource>();
        if (numberToAllocate == 1) {
            if (this.cd1.isAvailable())
                resourceList.add(this.cd1.use(processId));
            else if (this.cd2.isAvailable())
                resourceList.add(this.cd2.use(processId));
        } else if (numberToAllocate == 2)
            if (this.cd1.isAvailable() && this.cd2.isAvailable()) {
                resourceList.add(this.cd1.use(processId));
                resourceList.add(this.cd2.use(processId));
            }
        return resourceList;
    }

    public int availableScanners() {
        if (this.scanner.isAvailable())
            return 1;
        else
            return 0;
    }

    public ArrayList<Resource> allocateScanner(final int processId, final int numberToAllocate) {
        ArrayList<Resource> resourceList = new ArrayList<Resource>();
        if (numberToAllocate == 1 && this.scanner.isAvailable())
            resourceList.add(this.scanner.use(processId));
        return resourceList;
    }

    public int availableModems() {
        if (this.modem.isAvailable())
            return 1;
        else
            return 0;
    }

    public ArrayList<Resource> allocateModem(final int processId, final int numberToAllocate) {
        ArrayList<Resource> resourceList = new ArrayList<Resource>();
        if (numberToAllocate == 1 && this.modem.isAvailable())
            resourceList.add(this.modem.use(processId));
        return resourceList;
    }

    public void updateUsageTime() {
        if (this.printer1.blockedProcess)
            this.printer1.decreaseUsageTime();
        if (this.printer2.blockedProcess)
            this.printer2.decreaseUsageTime();
        if (this.cd1.blockedProcess)
            this.cd1.decreaseUsageTime();
        if (this.cd2.blockedProcess)
            this.cd2.decreaseUsageTime();
        if (this.scanner.blockedProcess)
            this.scanner.decreaseUsageTime();
        if (this.modem.blockedProcess)
            this.modem.decreaseUsageTime();
    }

}