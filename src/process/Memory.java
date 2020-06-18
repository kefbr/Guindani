package process;

public class Memory {
    int total;
    int available;

    public Memory() {
        this.total = 1024;
        this.available = 1024;
    }

    public void printMemory() {
        System.out.println("\nMEMORIA:\n\n" + "USADO: " + (this.total - this.available) + "/" + this.total);
    }
}