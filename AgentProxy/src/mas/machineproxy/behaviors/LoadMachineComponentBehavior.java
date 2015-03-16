package mas.machineproxy.behaviors;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import mas.machineproxy.Simulator;
import mas.machineproxy.component.Component;

import org.apache.commons.lang3.Conversion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jade.core.behaviours.OneShotBehaviour;

public class LoadMachineComponentBehavior extends OneShotBehaviour{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private XSSFWorkbook workBook;
	private XSSFSheet compononentSheet = null;
	private String filePath;
	private String fileName =  "machine_config.xlsx";
	private Logger log;
	private Simulator machineSimulator;
	/**
	 *  data being read is in hours/minutes/seconds
	 *  convert that into milliseconds
	 */
	private int unitConversion = 1; //keep this one as it helps me in debugging ~Nikhil

	public LoadMachineComponentBehavior() {
	}

	@Override
	public void action() {
		log = LogManager.getLogger();
		machineSimulator = (Simulator) getParent().
				getDataStore().
				get(Simulator.simulatorStoreName);
		this.filePath = System.getProperty("user.dir");
		try {
			InputStream fStream = new FileInputStream (filePath + 
					"\\" + fileName); 

			workBook = new XSSFWorkbook(fStream); 

			compononentSheet = workBook.getSheetAt(2);
		} catch(IOException e){
			log.debug("Error in opening excel File!");
			e.printStackTrace();
		}

		Iterator<Row> rows = compononentSheet.rowIterator();
		XSSFRow row = (XSSFRow) rows.next();

		double beta = 0 ;
		double eta = 0 ;
		double restorationFactor = 1;
		int rfType = 1;
		double TTR_sd = 1 ;
		double MTTR  = 0;
		double replacementCost = 0;
		double failureCost = 0;
		double preventiveMaintenanceCost = 0 ;
		double meanDelay = 0;
		double sdDelay = 1;
		Component tempComponent;

		while( rows.hasNext() )  {
			row = (XSSFRow) rows.next();
			Iterator<Cell> cells = row.cellIterator();

			int cellCount = 0;
			while(cells.hasNext()){

				XSSFCell cell = (XSSFCell) cells.next();
				switch(cellCount)
				{
				case 0:
					eta = cell.getNumericCellValue()*unitConversion;
					break;
				case 1:
					beta = cell.getNumericCellValue();
					break;
				case 2:
					restorationFactor = cell.getNumericCellValue();
					break;
				case 3:
					rfType = (int) cell.getNumericCellValue();
					break;
				case 4:
					MTTR = cell.getNumericCellValue()*unitConversion;
					break;
				case 5:
					TTR_sd= cell.getNumericCellValue()*unitConversion;
					break;
				case 6:
					replacementCost = cell.getNumericCellValue();
					break;
				case 7:
					failureCost = cell.getNumericCellValue();
					break;
				case 8:
					preventiveMaintenanceCost = cell.getNumericCellValue();
					break;
				case 9:
					meanDelay = cell.getNumericCellValue()*unitConversion;
					break;
				case 10:
					sdDelay = cell.getNumericCellValue()*unitConversion;
					break;
				}
				cellCount++;
			}
			tempComponent = new Component.Builder(eta, beta).
					restorationFactor(restorationFactor).
					restorationFactorType(rfType).
					MTTR(MTTR).
					TTR_sd(TTR_sd).
					replacementCost(replacementCost).
					failureCost(failureCost).
					prevMaintCost(preventiveMaintenanceCost).
					meanDelay(meanDelay).
					sdDelay(sdDelay).
					build(machineSimulator);

			machineSimulator.addComponent(tempComponent);
		}
		try {
			workBook.close();
		} catch (IOException e) {
			log.debug("Error in closing excel file");
			e.printStackTrace();
		}
	}
}
