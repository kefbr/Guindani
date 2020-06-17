package processo;

public class Resource {
    boolean inUse;
    int usageTime;
    int processId;
    boolean blockedProcess = false;

    public Resource() {
        this.inUse = false;
        this.usageTime = 3;
        this.processId = 0;
        this.blockedProcess = false;
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
    }

    public int getUsageTime() {
        return this.usageTime;
    }

    public void use(int processId) {
        this.inUse = true;
        this.processId = processId;
    }

    public boolean blockedProcess(){
return this.blockedProcess;
    }

}
