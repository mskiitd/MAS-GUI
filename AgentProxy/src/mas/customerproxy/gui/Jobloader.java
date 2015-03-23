package mas.customerproxy.gui;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import mas.job.OperationType;
import mas.job.job;
import mas.job.jobAttribute;
import mas.job.jobDimension;
import mas.job.jobOperation;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Jobloader {

	// processing time is input as seconds. Convert it into milliseconds
	private int timeUnitConversion = 1; //keep this 1 as it helps in debugging ~Nikhil

	private int NumJobs;
	private ArrayList<XSSFSheet> sheets;

	private String jobFilePath;
	private ArrayList<String> jobIdList;
	private ArrayList<Double> jobCPNs;
	private ArrayList<Long> jobDueDates;
	private ArrayList<Integer> jobQuantity;
	private ArrayList<Double> jobPenaltyRate;
	private ArrayList<ArrayList<jobOperation> > jobOperations;
	int countJob = 0;
	private String[] tableHeaders = {"Job ID" , "Operations",
			"CPN" , "Penalty Rate", "Due Date"};

	public Jobloader() {
		this.jobIdList = new ArrayList<String>();
		this.jobQuantity = new ArrayList<Integer>();
		this.jobCPNs = new ArrayList<Double>();
		this.jobDueDates = new ArrayList<Long>();
		this.jobPenaltyRate = new ArrayList<Double>();
		this.sheets = new ArrayList<XSSFSheet>();
		this.jobOperations = new ArrayList<ArrayList<jobOperation> >();
		this.jobFilePath = System.getProperty("user.dir");
	}

	public Vector<job> getjobVector() {
		Vector<job> jobs = new Vector<job>();

		for(int index = 0 ; index < jobIdList.size() ; index ++){

			job j = new job.Builder(jobIdList.get(index))
			.jobCPN(jobCPNs.get(index))
			.jobOperation(this.jobOperations.get(index))
			.jobPenalty(this.jobPenaltyRate.get(index))
			.build() ;

			j.setJobNo(countJob++);

			jobs.add(j);
		}
		return jobs;
	}

	public Vector<String> getJobHeaders(){
		Vector<String> headers = new Vector<String>();

		for(int index = 0 ; index < tableHeaders.length ; index ++){
			headers.add(tableHeaders[index]);
		}

		return headers;
	}

	public void readFile() {
		XSSFWorkbook wb;
		try{
			FileInputStream file=new FileInputStream(this.jobFilePath +
					"\\jobdata.xlsx");	
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
				jobIdList.add(cell.getNumericCellValue() + "");
				break;
			case 1:
				jobQuantity.add((int) cell.getNumericCellValue());
				break;
			case 2:
				jobCPNs.add(cell.getNumericCellValue());
				break;
			case 3:
				jobDueDates.add((long) (cell.getNumericCellValue()*timeUnitConversion));
				//				log.info((long) (cell.getNumericCellValue()*timeUnitConversion));
				break;
			case 4:
				jobPenaltyRate.add(cell.getNumericCellValue());
				break;
			}
			count ++;
		}

		ArrayList<jobOperation> opList = new ArrayList<jobOperation>();
		// Now read operations for the job
		// Skip the header row for operations
		row = (XSSFRow) rows.next();

		while( rows.hasNext() ) {

			row = (XSSFRow) rows.next();
			cells = row.cellIterator();

			jobOperation currOperation = new jobOperation();
			count = 0; 
			while(cells.hasNext()) {
				XSSFCell cell = (XSSFCell) cells.next();

				switch(count) {
				case 0:
					// Operation type for the job
					currOperation.setJobOperationType(OperationType.Operation_1);
					break;

				case 1:
					// Processing time for this operation
					currOperation.
					setProcessingTime((long) cell.getNumericCellValue()*timeUnitConversion);
					break;

				case 2:
					// Dimensions for this operation
					//					log.info(cell.getCellType());
					cell.setCellType(1);
					String s = cell.getStringCellValue();
					String temp[] = s.split(",");
					//			            		  System.out.println("length="+temp.length);
					ArrayList<jobDimension> tempDimList = new ArrayList<jobDimension>();
					jobDimension tempDim = new jobDimension();
					for(int i=0; i < temp.length; i++){
						tempDim.setTargetDimension(Double.parseDouble(temp[i]));
						tempDimList.add(tempDim );
					}
					currOperation.setjDims(tempDimList);
					break;

				case 3:
					// Attributes for this operation
					String Attr=cell.getStringCellValue();
					String tempAttr[]=Attr.split(",");

					ArrayList<jobAttribute> tempAttrList = new ArrayList<jobAttribute>();

					for(int i=0; i < tempAttr.length; i++){
						jobAttribute tempAttribute = new jobAttribute(tempAttr[i]);
						tempAttrList.add(tempAttribute );
					}
					currOperation.setjAttributes(tempAttrList);

					break;
				}
				count++;
			}
			opList.add(currOperation);
			this.jobOperations.add(opList);
		}
	}
}
