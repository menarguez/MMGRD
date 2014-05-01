package gpAlgorithm;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

public class Dataset {
	private String[] header;
	private List<double[]> data;
	
	public Dataset(String filePath){
		readDatasetCSV(filePath);
	}
	
	private void readDatasetCSV(String filePath){
		try{
		CSVReader reader = new CSVReader(new FileReader(new File(filePath)),
				',');
		// Read header (Variable names)
		String[] line = reader.readNext();
		header = new String[line.length];
		for (int i =0 ; i< line.length; i ++){
			header[i] = line[i];
		}
		String[] nextLine;
		data = new ArrayList<double[]>();
		while ((nextLine = reader.readNext()) != null) {
	        if (nextLine.length!= header.length){
	        	System.err.println("Number of variables is different. Aborting...");
	        	System.exit(-1);
	        }else{
	        	double[] currentDataLine = new double[header.length];
	        	for (int i =0 ; i < header.length; i++){
	        		currentDataLine[i] = new Double(nextLine[i]).doubleValue();
	        	}
	        	data.add(currentDataLine);
	        }
	    }
		reader.close();
		}catch(Exception e){
			System.err.println("Error: "+ e.getMessage()+" Aborting...");
        	System.exit(-2);
		}
	}
	
	public int getDimension(){
		return (header.length-1);
	}
	
	public double[] getDatasetValueSet(int lineNumber){
		if (lineNumber >=0 && lineNumber< data.size()){
			return data.get(lineNumber);
		}else {
			System.err.println("Error: Requested index out of bounds. Aborting...");
        	System.exit(-2);
        	return null;
		}
	}
	
	public String[] getVariableNames(){
		return header;
	}
	
	public int getNumberOfRows(){
		return data.size();
	}
	
	public String getVariableName(int columnNumber){
		if (columnNumber >=0 && columnNumber< header.length){
			return header[columnNumber];
		}else {
			System.err.println("Error: Requested column index out of bounds. Aborting...");
        	System.exit(-2);
        	return null;
		}
	}
}
