package process;

public class Resource {
    int usageTime = 3;
    int processId = 0;
    boolean blockedProcess = false;

    public Resource() {
    }

    public boolean isAvailable() {
        return this.processId == 0;
    }

    public void decreaseUsageTime() {
        this.usageTime--;
    }

    public void free() {
        this.usageTime = 3;
        this.processId = 0;
        this.blockedProcess = false;
    }

    public int getUsageTime() {
        return this.usageTime;
    }

    public Resource use(int processId) {
        this.processId = processId;
        this.blockedProcess = true;
        this.usageTime = 3;
        return this;
    }

}
