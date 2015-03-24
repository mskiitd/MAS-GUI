package mas.machineproxy.behaviors;

import jade.core.behaviours.OneShotBehaviour;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import mas.machineproxy.Simulator;
import mas.machineproxy.parametrer.Parameter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class LoadMachineParameterBehavior extends OneShotBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private XSSFWorkbook workBook;
	private XSSFSheet machineParameterSheet = null;
	private String filePath;
	private String fileName =  "machine_config.xlsx";
	private Logger log;
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

			machineParameterSheet = workBook.getSheetAt(1);
		} catch(IOException e){
			log.debug("Error in opening excel File!");
			e.printStackTrace();
		}
		Iterator<Row> rows = machineParameterSheet.rowIterator();
		XSSFRow row = (XSSFRow) rows.next();

		String paramName = "";
		double value = 0;
		boolean isInspection = false;
		int updateFrequency = 0;

		while( rows.hasNext() )  {
			row = (XSSFRow) rows.next();
			Iterator<Cell> cells = row.cellIterator();
			int count = 0;

			while(cells.hasNext()) {
				XSSFCell cell = (XSSFCell) cells.next();

				switch(count)
				{
				case 0:
					paramName = cell.getStringCellValue();
					break;
				case 1:
					value = cell.getNumericCellValue();
					break;
				case 2:
					if(cell.getNumericCellValue() == 1)
						isInspection = true;
					else
						isInspection = false;
					break;
				case 3:
					updateFrequency = (int) cell.getNumericCellValue();
					break;
				}
				count++;
			}
			machineSimulator.addMachineParameter(new Parameter(paramName,
					value, isInspection, updateFrequency));
		}
		try {
//			log.info(Simulator.params.size());
			workBook.close();
		} catch (IOException e) {
			log.debug("Error in closing excel file");
			e.printStackTrace();
		}
	}
}