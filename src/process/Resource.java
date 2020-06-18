package process;

public class Resource {
    boolean inUse = false;
    int usageTime = 3;
    int processId = 0;
    boolean blockedProcess = false;

    public Resource() {
    }

    public boolean isAvailable() {
        return !this.inUse;
    }

    public void decreaseUsageTime() {
        this.usageTime--;
    }

    public void free() {
        this.inUse = false;
        this.usageTime = 3;
        this.processId = 0;
        this.blockedProcess = false;
    }

    public int getUsageTime() {
        return this.usageTime;
    }

    public void use(int processId) {
        this.inUse = true;
        this.processId = processId;
        this.blockedProcess = true;
    }

}
