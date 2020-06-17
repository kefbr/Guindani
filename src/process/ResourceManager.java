package process;

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

    public void allocatePrinter(final int processId, final int numberToAllocate) {
        if (numberToAllocate == 1) {
            if (this.printer1.isAvailable())
                this.printer1.use((processId));
            else if (this.printer2.isAvailable())
                this.printer2.use((processId));
        } else if (numberToAllocate == 2)
            if (this.printer1.isAvailable() && this.printer2.isAvailable()) {
                this.printer1.use(processId);
                this.printer2.use(processId);
            }
    }

    public int availableCDs() {
        int amount = 0;

        if (cd1.isAvailable())
            amount++;

        if (cd2.isAvailable())
            amount++;

        return amount;
    }

    public void allocateCD(final int processId, final int numberToAllocate) {
        if (numberToAllocate == 1) {
            if (this.cd1.isAvailable())
                this.cd1.use((processId));
            else if (this.cd2.isAvailable())
                this.cd2.use((processId));
        } else if (numberToAllocate == 2)
            if (this.cd1.isAvailable() && this.cd2.isAvailable()) {
                this.cd1.use(processId);
                this.cd2.use(processId);
            }
    }

    public boolean isScannerAvailable() {
        return this.scanner.isAvailable();
    }

    public void allocateScanner(final int processId, final int numberToAllocate) {
        if (numberToAllocate == 1 && this.scanner.isAvailable())
            this.scanner.use(processId);
    }

    public boolean isModemAvailable() {
        return this.modem.isAvailable();
    }

    public void allocateModem(final int processId, final int numberToAllocate) {
        if (numberToAllocate == 1 && this.modem.isAvailable())
            this.modem.use(processId);
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