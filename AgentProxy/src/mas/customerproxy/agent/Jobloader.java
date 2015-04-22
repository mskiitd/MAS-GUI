package mas.customerproxy.agent;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import mas.jobproxy.Batch;
import mas.jobproxy.job;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


/** 
 * A class to read and load batch data from excel file.
 * It picks up the excel files from resources/Customer/ folder.
 * This is what is displayed in the GUI of customer
 *
 */
public class Jobloader {

	private int NumJobs;
	private ArrayList<XSSFSheet> sheets;

	private String jobFilePath;
	private ArrayList<String> jobIdList;
	private ArrayList<Double> jobCPNs;
	private ArrayList<Integer> jobQuantity;
	private ArrayList<Double> jobPenaltyRate;
	int countJob = 1;
	private String localName;

	private String[] tableHeaders = {"Batch ID", "CPN" , "Penalty Rate", "Batch Count"};

	public Jobloader(String str) {
		this.localName = str;
		this.jobIdList = new ArrayList<String>();
		this.jobQuantity = new ArrayList<Integer>();
		this.jobCPNs = new ArrayList<Double>();
		this.jobPenaltyRate = new ArrayList<Double>();
		this.sheets = new ArrayList<XSSFSheet>();
		this.jobFilePath = "resources/customer/";
	}

	/**
	 * Creates a vector of batch which are read from the excel file of customer
	 * @return vector of batch read from the excel file
	 */
	public Vector<Batch> getBatchVector() {
		Vector<Batch> jobs = new Vector<Batch>();

		for(int index = 0 ; index < jobIdList.size() ; index ++) {

			job j = new job.Builder(jobIdList.get(index))
			.build() ;

			Batch batch = new Batch(jobIdList.get(index));
			ArrayList<job> jobsList = new ArrayList<job>();
			
			for(int bSize = 0; bSize < this.jobQuantity.get(index); bSize++) {
				jobsList.add(j);
			}

			batch.setBatchNumber(countJob++);
			batch.setJobsInBatch(jobsList);
			batch.setCPN(this.jobCPNs.get(index));
			batch.setPenaltyRate(this.jobPenaltyRate.get(index));
			jobs.add(batch);
		}
		return jobs;
	}

	/**
	 * @return headers of the table which displays all the batches within the excel file
	 */
	public Vector<String> getJobHeaders(){
		Vector<String> headers = new Vector<String>();

		for(int index = 0 ; index < tableHeaders.length ; index ++){
			headers.add(tableHeaders[index]);
		}

		return headers;
	}

	/**
	 * 
	 * @return headers of the table which displays all the accepted batches
	 */
	public Vector<String> getAcceptedJobTableHeader() {
		Vector<String> headers = new Vector<String>();

		for(int index = 0 ; index < tableHeaders.length ; index ++){
			headers.add(tableHeaders[index]);
		}

		headers.add("Due Date");
		return headers;
	}

	/**
	 * 
	 * @return headers of the table which displays all the completed batches
	 */
	public Vector<String> getCompleteJobTableHeader() {
		Vector<String> headers = new Vector<String>();

		for(int index = 0 ; index < tableHeaders.length ; index ++){
			headers.add(tableHeaders[index]);
		}

		headers.add("Completion Time");
		return headers;
	}

	/**
	 * Reads the excel file of batches
	 **/
	public void readFile() {
		XSSFWorkbook wb;
		try{
			FileInputStream file = new FileInputStream(this.jobFilePath +
					"\\" + localName + ".xlsx");	
			wb = new XSSFWorkbook(file);

			this.NumJobs = wb.getNumberOfSheets();

			XSSFSheet localSheet;
			for(int i = 0 ; i < NumJobs ; i++) {
				localSheet = wb.getSheetAt(i);
				sheets.add(localSheet);
				readSheet(localSheet);
			}

		}catch(IOException e){
			e.printStackTrace();
		}
	}

	private void readSheet(XSSFSheet currSheet) {

		Iterator<Row> rows = currSheet.rowIterator();
		XSSFRow row = (XSSFRow) rows.next();

		// first read the second row of job file
		// skip the first header line
		row = (XSSFRow) rows.next();

		Iterator<Cell> cells = row.cellIterator();

		int count = 0; 
		while(cells.hasNext()) {
			XSSFCell cell = (XSSFCell) cells.next();

			switch(count) {
			case 0:
				jobIdList.add(cell.getStringCellValue());
				break;
			case 1:
				jobQuantity.add((int) cell.getNumericCellValue());
				break;
			case 2:
				jobCPNs.add(cell.getNumericCellValue());
				break;
			case 3:
				jobPenaltyRate.add(cell.getNumericCellValue());
				break;
			}
			count ++;
		}
	}
}
