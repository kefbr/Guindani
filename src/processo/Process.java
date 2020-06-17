package processo;

import java.util.ArrayList;

public class Process {
	int id;
	ArrayList<Resource> resources;
	int arrivalTime;
	int priority;
	int processTime;
	int memoryUse;
	int printerAmount;
	int cdAmount;
	int scannerAmount;
	int modemAmount;
	int timeLeft;
	int initTime;
	int exitTime;
	int row;

	public Process(int id, int arrivalTime, int priority, int processTime, int memoryUse, int printerAmount,
			int scannerAmount, int modemAmount, int cdAmount) {
		this.id = id;
		this.arrivalTime = arrivalTime;
		this.priority = priority;
		this.processTime = processTime;
		this.memoryUse = memoryUse;
		this.printerAmount = printerAmount;
		this.cdAmount = cdAmount;
		this.scannerAmount = scannerAmount;
		this.modemAmount = modemAmount;
	}

	public void insert(ArrayList<Process> processList) {
		for (var i = 0; i < processList.size(); i++) {
			var p = processList.get(i);
			if (this.priority < p.priority) {
				processList.add(i, this);
				return;
			} else if (this.priority == p.priority)
				if (this.row < p.row) {
					processList.add(i, this);
					return;
				}
		}
		processList.add(this);
	}

}
