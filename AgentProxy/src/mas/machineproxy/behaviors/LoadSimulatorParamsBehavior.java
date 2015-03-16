package mas.machineproxy.behaviors;

import jade.core.behaviours.OneShotBehaviour;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import mas.machineproxy.Simulator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class LoadSimulatorParamsBehavior extends OneShotBehaviour{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private XSSFWorkbook workBook;
	private XSSFSheet simulatorParameterSheet = null;
	private String filePath;
	private String fileName =  "machine_config.xlsx";
	private Logger log;
	private long minute2Millis = 60000;
	private Simulator machineSimulator;

	@Override
	public void action() {
		log = LogManager.getLogger();
		machineSimulator = (Simulator) getParent().getDataStore().get(Simulator.simulatorStoreName);
		this.filePath = System.getProperty("user.dir");
		try {
			InputStream fStream = new FileInputStream (filePath + 
					"\\" + fileName); 

			workBook = new XSSFWorkbook(fStream); 

			simulatorParameterSheet = workBook.getSheetAt(0);
		} catch(IOException e){
			log.debug("Error in opening excel File!");
			e.printStackTrace();
		}

		Iterator<Row> rows = simulatorParameterSheet.rowIterator();
		XSSFRow row = (XSSFRow) rows.next();
		
		int rowNumber = 0;
		while( rows.hasNext() ) {

			Iterator<Cell> cells = row.cellIterator();
			int cellNumber = 0;

			while(cells.hasNext()) {

				XSSFCell cell = (XSSFCell) cells.next();

				if(cellNumber == 1) {
					switch (rowNumber) {

					case 0:
						machineSimulator.setPercentProcessingTimeVariation(
								(double)cell.getNumericCellValue());

						machineSimulator.setPercentProcessingTimeVariation(
								machineSimulator.getPercentProcessingTimeVariation()/100);
						break;

					case 1:
						machineSimulator.setMeanLoadingTime(
								(double)cell.getNumericCellValue());
						break;
					case 2:
						machineSimulator.setSdLoadingTime(
								(double)cell.getNumericCellValue());
						break;
					case 3:
						machineSimulator.setMeanUnloadingTime(
								(double)cell.getNumericCellValue() );
						break;
					case 4:
						machineSimulator.setSdUnloadingTime(
								(double)cell.getNumericCellValue());
						break;
						
					case 5:
						machineSimulator.setFractionDefective(
								(double)cell.getNumericCellValue() );
						break;
					case 6:
						machineSimulator.setMean_shiftInMean(
								(double)cell.getNumericCellValue());
						break;
					case 7:
						machineSimulator.setSd_shiftInMean(
								(double)cell.getNumericCellValue());
						break;
					case 8:
						machineSimulator.setMean_shiftInSd(
								(double)cell.getNumericCellValue());
						break;
					case 9:
						machineSimulator.setSd_shiftInSd(
								(double)cell.getNumericCellValue());
						break;
					case 10:
						machineSimulator.setRateShift(
								(double)cell.getNumericCellValue() );
						break;
					}
				}
				cellNumber++;
			}
			row = (XSSFRow) rows.next();
			rowNumber++;
		}
		try {
			workBook.close();
//			log.info("input done");
		} catch (IOException e) {
			log.debug("Error in closing excel file");
			e.printStackTrace();
		}
	}
}
