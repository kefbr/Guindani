package processo;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class Utils {

	public static ArrayList<Process> initializeEntry(String fileName) throws FileNotFoundException {
		int id = 0;

		Scanner entryFile = new Scanner(new FileReader("lista.txt"));

		ArrayList<Process> entryRow = new ArrayList<Process>();

		while (entryFile.hasNextLine()) {
			String[] processArray = entryFile.nextLine().split(",");

			Process newProcess = new Process(id, Integer.parseInt(processArray[0]), Integer.parseInt(processArray[1]),
					Integer.parseInt(processArray[2]), Integer.parseInt(processArray[3]),
					Integer.parseInt(processArray[4]), Integer.parseInt(processArray[5]),
					Integer.parseInt(processArray[6]), Integer.parseInt(processArray[7]));

			entryRow.add(newProcess);
		}
		entryFile.close();

		return entryRow;
	}
}
