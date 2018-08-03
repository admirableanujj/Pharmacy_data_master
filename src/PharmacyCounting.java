/*Author: Anujj Saxena
 * Following code generates a list of all drugs,
 * the total number of UNIQUE individuals who prescribed the medication,
 * and the total drug cost,
 * which gets listed in descending order based on the total drug cost 
 * and if there is a tie, drug name.
 */
package src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class PharmacyCounting {
	// variable declaration for input arguments
	static String inputFileName = null;
	static String outputFileName = null;

	public static void main(String[] args) {

		inputFileName = args[0]; // given input file name
		outputFileName = args[1]; // desired output file name

		ArrayList<InputData> inputDataGiven = new ArrayList<InputData>();
		try {

			inputDataGiven = PharmacyCounting.csvFileRead(inputFileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<OutPut> sortedData = new ArrayList<OutPut>();
		sortedData = PharmacyCounting.sortAndRemoveDuplicate(inputDataGiven);
		Collections.sort(sortedData);
		try {
			boolean success = PharmacyCounting.csvFileWrite(sortedData);
			if (success) {
				System.out.println("Success");
			} else {
				System.out.println("Not Success");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// Reads a CSV input file of given name
	public static ArrayList<InputData> csvFileRead(String fileName) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String line = null;
		ArrayList<InputData> dataGiven = new ArrayList<InputData>();
		boolean firstLine = true;
		int dataCounter = 0;
		int errorData = 0;
		while ((line = br.readLine()) != null) {

			String[] values = line.split(",");
			if (!firstLine) {
				if(values[4]==null||values[4]==""||values[4]==" ") {
					values[4]="0000";
				}
				try {
				InputData inputObject = new InputData(values[0], values[1], values[2], values[3],
						Double.parseDouble(values[4].replaceAll("[^0-9/.]", "")));
				dataGiven.add(inputObject);}
				catch(NumberFormatException e) {System.out.println(e + "Line Number:"+dataCounter);
				errorData++;}
				
			} else {
				firstLine = false;
			}
			dataCounter++;
		}
		br.close();
		System.out.println("Total Data: "+dataCounter+"\n"+"Total Error: "+errorData);
		return dataGiven;

	}

	// Writes a CSV out file with the given name
	public static boolean csvFileWrite(ArrayList<OutPut> outPutData) throws IOException {
		try {
			System.out.println(outputFileName);
			FileWriter writer = new FileWriter(outputFileName, true);
			writer.write("drug_name,num_prescriber,total_cost");
			writer.write("\r\n"); // write new line
			for (OutPut outPut : outPutData) {
				writer.write(outPut.getDrug_name() + "," + outPut.getNum_prescriber() + ","
						+ Math.round(outPut.getTotalCost()));
				writer.write("\r\n"); // write new line
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	// This function sorts and generates required output
	public static ArrayList<OutPut> sortAndRemoveDuplicate(ArrayList<InputData> inputDataGiven) {
		ArrayList<OutPut> sortedData = new ArrayList<OutPut>();
		ArrayList<String> drugName = new ArrayList<String>();
		for (InputData inputData : inputDataGiven) {
			if (drugName.isEmpty()) {
				drugName.add(inputData.drugName);
			} else {
				if (drugName.contains(inputData.drugName)) {
					continue;
				} else {
					drugName.add(inputData.drugName);
				}
			}

		}
		Collections.sort(drugName);

		for (String name : drugName) {
			ArrayList<InputData> tempData = new ArrayList<InputData>();
			for (InputData inputData : inputDataGiven) {

				if (inputData.getDrugName().equalsIgnoreCase(name)) {
					tempData.add(inputData);

				}

			}
			OutPut dataOut = new OutPut();
			dataOut.setDrug_name(name);
			double totalCost = 0;
			int numPrescriber = 0;
			InputData lastTempData = new InputData();
			lastTempData = null;
			boolean firstTimeFlag = true;

			for (InputData inputData2 : tempData) {
				if ((!firstTimeFlag)
						&& inputData2.getPrescriberFirstName().equalsIgnoreCase(lastTempData.getPrescriberFirstName())
						&& (inputData2.getPrescriberLastName())
								.equalsIgnoreCase(lastTempData.getPrescriberLastName())) {
					// continue; do not remove

				} else {
					numPrescriber = numPrescriber + 1;
				}
				totalCost = totalCost + inputData2.getDrugCost();
				lastTempData = inputData2;
				firstTimeFlag = false;
			}
			dataOut.setNum_prescriber(numPrescriber);
			dataOut.setTotalCost(totalCost);
			sortedData.add(dataOut);
		}
		return sortedData;
	}

}

class InputData {

	String id;
	String prescriberLastName;
	String prescriberFirstName;
	String drugName;
	double drugCost;

	public InputData() {
		super();
		// TODO Auto-generated constructor stub
	}

	public InputData(String id, String prescriberLastName, String prescriberFirstName, String drugName,
			double drugCost) {
		super();
		this.id = id;
		this.prescriberLastName = prescriberLastName;
		this.prescriberFirstName = prescriberFirstName;
		this.drugName = drugName;
		this.drugCost = drugCost;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPrescriberLastName() {
		return prescriberLastName;
	}

	public void setPrescriberLastName(String prescriberLastName) {
		this.prescriberLastName = prescriberLastName;
	}

	public String getPrescriberFirstName() {
		return prescriberFirstName;
	}

	public void setPrescriberFirstName(String prescriberFirstName) {
		this.prescriberFirstName = prescriberFirstName;
	}

	public String getDrugName() {
		return drugName;
	}

	public void setDrugName(String drugName) {
		this.drugName = drugName;
	}

	public double getDrugCost() {
		return drugCost;
	}

	public void setDrugCost(double drugCost) {
		this.drugCost = drugCost;
	}

}

class OutPut implements Comparable<OutPut> {

	String drug_name;
	int num_prescriber;
	double totalCost;

	public OutPut() {
		super();
		// TODO Auto-generated constructor stub
	}

	public OutPut(String drug_name, int num_prescriber, double totalCost) {
		super();
		this.drug_name = drug_name;
		this.num_prescriber = num_prescriber;
		this.totalCost = totalCost;
	}

	public String getDrug_name() {
		return drug_name;
	}

	public void setDrug_name(String drug_name) {
		this.drug_name = drug_name;
	}

	public int getNum_prescriber() {
		return num_prescriber;
	}

	public void setNum_prescriber(int num_prescriber) {
		this.num_prescriber = num_prescriber;
	}

	public double getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
	}

	public int compareTo(OutPut opt) {
		if (totalCost == opt.totalCost)
			return 0;
		else if (totalCost < opt.totalCost)
			return 1;
		else
			return -1;
	}
}
