package process;

import java.util.ArrayList;

public class Process implements java.lang.Comparable<Process> {
	int id;
	ArrayList<Resource> resourceList;
	int arrivalTime;
	int priority;
	int processTime;
	int memoryAmount;
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
		this.timeLeft = processTime;
		this.resourceList = new ArrayList<Resource>();
		this.priority = priority;
		this.processTime = processTime;
		this.memoryAmount = memoryUse;
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

	@Override
	public int compareTo(Process o) {
		if (this.arrivalTime != o.arrivalTime) {
			return this.arrivalTime - o.arrivalTime;
		} else
			return this.priority - o.priority;
	}

}
