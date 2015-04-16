package mas.maintenanceproxy.plan;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import mas.maintenanceproxy.goal.PeriodicPreventiveMaintenanceGoal;
import mas.util.ID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import bdi4jade.core.BDIAgent;
import bdi4jade.core.BeliefBase;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;
import jade.core.behaviours.OneShotBehaviour;
import jdk.nashorn.internal.runtime.regexp.joni.Warnings;

public class LoadConfigMaintPlan extends OneShotBehaviour implements PlanBody{

	private static final long serialVersionUID = 1L;
	private BeliefBase bfBase;
	private long maintPeriod;
	private long maintWarningPeriod;
	private String configFilePath;
	private long timeConversion = 60*1000;
	private String localName = "config";
	private XSSFWorkbook wb;
	private Logger log;

	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	@Override
	public void init(PlanInstance planInstance) {
		bfBase = planInstance.getBeliefBase();
		configFilePath = "resources/LMA/";
		log = LogManager.getLogger();
	}

	@Override
	public void action() {
		FileInputStream file;
		try {
			file = new FileInputStream(configFilePath +
					"\\" + localName + ".xlsx");
			wb = new XSSFWorkbook(file);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		XSSFSheet sheet = wb.getSheetAt(0);
		Iterator<Row> rows = sheet.rowIterator();
		XSSFRow row;
		int rowCount = 0;

		// the value of parameters is in the second column
		// hence skip the first column
		while(rows.hasNext()) {
			row = (XSSFRow) rows.next();
			Iterator<Cell> cells = row.cellIterator();
			XSSFCell cell = (XSSFCell) cells.next();
			cell = (XSSFCell) cells.next();

			switch(rowCount) {
			case 0:
				maintPeriod = (long)cell.getNumericCellValue()*timeConversion;
				bfBase.updateBelief(ID.Maintenance.BeliefBaseConst.maintenancePeriod, maintPeriod);
				break;
			case 1:
				maintWarningPeriod = (long)cell.getNumericCellValue()*timeConversion;
				bfBase.updateBelief(ID.Maintenance.BeliefBaseConst.maintWarningPeriod, maintWarningPeriod);
				break;
			}
			rowCount++;
		}
		log.info("configuration file loaded, maint : " + maintPeriod +
				", warning : " + maintWarningPeriod);
		((BDIAgent)myAgent).addGoal(new PeriodicPreventiveMaintenanceGoal());
	}
}


