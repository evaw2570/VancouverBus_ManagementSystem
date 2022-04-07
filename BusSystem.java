import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class BusSystem {
	public static class StopTimes {
		public String tripID;
		public String arrivalTime;
		public String stopID;
		public String stopSequence;
		public String stopHeadSign;
		public String pickupType;
		public String dropOffType;
		public String shapeDistTraveled;
		
		public StopTimes(String a1, String a2, String a3, String a4, String a5, String a6, String a7, String a8)
		{
			tripID = a1;
			arrivalTime = a2;
			stopID = a3;
			stopSequence = a4;
			stopHeadSign = a5;
			pickupType = a6;
			dropOffType = a7;
			shapeDistTraveled = a8;
		}

		@Override
		public String toString()
		{
			return String.format("%s, %s, %s, %s, %s, %s, %s, %s", 
				tripID, arrivalTime, stopID, stopSequence, stopHeadSign, pickupType, dropOffType, shapeDistTraveled);
		}
	}
	public static class StopTimeComparator implements Comparator<StopTimes> {
		@Override
		public int compare(StopTimes o1, StopTimes o2) {
			return o1.tripID.compareTo(o2.tripID);
		}
	}

	public static Scanner scanner = new Scanner(System.in);
	public static StopGraph<String> graph = new StopGraph<String>();
	public static TST tst = new TST();
	public static List<StopTimes> stopTimes = new ArrayList<StopTimes>();

	public static void function1() throws FileNotFoundException, IOException {
		String []splits;
		String file;
		int nIndex = 0;
		String line;
		String from, to, type, min_transfer;
		double cost;
		String tripID, beforeTrip, beforeStop;
		
		if(graph.getEdgeCount() == 0)
		{
			file = "./transfers.txt";
			try (BufferedReader br = new BufferedReader(new FileReader(file))) {
				nIndex = 0;
				while ((line = br.readLine()) != null) {
					if (nIndex == 0 || line.equals("")) {
						nIndex++;
						continue;
					}
					nIndex++;
					splits = line.split(",", -1);
					from = splits[0];
					to = splits[1];
					type = splits[2];
					if(type.equals("2"))
					{
						min_transfer = splits[3];
						cost = Double.parseDouble(min_transfer)/100.0;
					}
					else
					{
						cost = 2;
					}
					graph.add(from, to, cost);
				}
			}

			beforeTrip = "";
			beforeStop = "";
			cost = 1.0f;

			file = "./stop_times.txt";
			try (BufferedReader br = new BufferedReader(new FileReader(file))) {
				nIndex = 0;
				
				while ((line = br.readLine()) != null) {
					if (nIndex == 0 || line.equals("")) {
						nIndex++;
						continue;
					}
					nIndex++;
					splits = line.split(",", -1);
					tripID = splits[0];
					if(beforeTrip.equals(tripID))
					{
						from = beforeStop;
						to = splits[3];
						graph.add(from, to, cost);
						
						beforeStop = to;
					}
					else
					{
						beforeTrip = tripID;
						beforeStop = splits[3];
					}
				}
			}
		}

		String source, target;
		System.out.print("Source Stop ID: ");
		source = scanner.nextLine();
		System.out.print("Target Stop ID: ");
		target = scanner.nextLine();

		List<String> path = graph.getPath(source, target);
		if(path != null)
		{
			for (String each : path) 
				System.out.println(each);
		}
	}

	public static void function2() throws IOException {
		String 	file;
		int 	nIndex;
		String	line;
		String	[]splits;
		String	[]splitsName;
		String	stopName;
		int		i;

		if(tst.size() == 0)
		{
			file = "./stops.txt";
			try (BufferedReader br = new BufferedReader(new FileReader(file))) {
				nIndex = 0;
				
				while ((line = br.readLine()) != null) {
					if (nIndex == 0 || line.equals("")) {
						nIndex++;
						continue;
					}
					nIndex++;
					splits = line.split(",", -1);
					stopName = splits[2];
					splitsName = stopName.split(" ", -1);
					if(splitsName[0].equals("WB") || splitsName[0].equals("NB") || 
						splitsName[0].equals("SB") || splitsName[0].equals("EB"))
					{
						StringBuffer sb = new StringBuffer();
						for(i = 1; i < splitsName.length; i++) {
							sb.append(splitsName[i]);
							sb.append(" ");
						}
						sb.append(splitsName[0]);
						stopName = sb.toString();
					}
					tst.put(stopName, nIndex);
				}
			}
		}

		String searchString;
		System.out.print("String to search: ");
		searchString = scanner.nextLine();
		// System.out.println(tst.longestPrefixOf(searchString));
		Iterable<String> it = tst.keysWithPrefix(searchString);
        for (String s1 : it)
            System.out.println(s1);
        System.out.println();
	}

	public static void function3() throws IOException, ParseException {
		String		file;
		int			nIndex;
		String		line;
		String		[]splits;
		String		[]splitsTime;
		String		arrivalTime;

		if(stopTimes.size() == 0)
		{
			file = "./stop_times.txt";
			try (BufferedReader br = new BufferedReader(new FileReader(file))) {
				nIndex = 0;
				
				while ((line = br.readLine()) != null) {
					if (nIndex == 0 || line.equals("")) {
						nIndex++;
						continue;
					}
					nIndex++;
					splits = line.split(",", -1);
					arrivalTime = splits[1];
					arrivalTime = arrivalTime.trim();
					splitsTime = arrivalTime.split(":", -1);
					if(Integer.parseInt(splitsTime[0]) >= 24)
					{
						continue;
					}
					else
					{
					}
					LocalTime localTime = LocalTime.parse(arrivalTime, DateTimeFormatter.ofPattern("H:mm:ss"));
					int hour = localTime.get(ChronoField.CLOCK_HOUR_OF_DAY);
					int minute = localTime.get(ChronoField.MINUTE_OF_HOUR);
					int second = localTime.get(ChronoField.SECOND_OF_MINUTE);
					arrivalTime = String.format("%02d:%02d:%02d", hour, minute, second);
					StopTimes stopTime = new StopTimes(splits[0], arrivalTime, splits[3], splits[4], 
						splits[5], splits[6], splits[7], splits[8]);
					stopTimes.add(stopTime);					
				}
			}
		}
		stopTimes.sort(new StopTimeComparator());

		String searchTime;
		System.out.print("Time(HH:mm:ss): ");
		searchTime = scanner.nextLine();
		for(StopTimes e : stopTimes) {
			if(e.arrivalTime.equals(searchTime))
			{
				System.out.println(e.toString());
			}
		}
	}

	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
		while (true) {
			System.out.println("");
			System.out.println("1. Finding shortest paths between 2 bus stops");
			System.out.println("2. Searching for a bus stop");
			System.out.println("3. Searching for all trips");
			System.out.println("4. Quit");
			System.out.print(":");
			String menu = scanner.nextLine();
			if (menu.equals("4"))
				break;
			if (menu.equals("1")) {
				function1();
			} else if (menu.equals("2")) {
				function2();
			} else if (menu.equals("3")) {
				function3();
			} else {
				System.out.println("Invalid menu");
			}
		}

		System.out.println("\nGood Bye");
	}
}
