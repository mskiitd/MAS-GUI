package mas.globalSchedulingproxy.plan;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import mas.globalSchedulingproxy.database.BatchDataBase;
import mas.globalSchedulingproxy.database.CustomerBatches;
import mas.globalSchedulingproxy.database.UnitBatchInfo;
import mas.jobproxy.jobOperation;
import mas.util.ID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import bdi4jade.core.BeliefBase;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;
import jade.core.behaviours.Behaviour;

public class LoadBatchOperationDetailsPlan extends Behaviour implements PlanBody{

	private static final long serialVersionUID = 1L;
	private BeliefBase bfBase;
	private Logger log;
	private boolean done = false;
	private String path;
	private BatchDataBase db;

	@Override
	public EndState getEndState() {
		return (done ? EndState.SUCCESSFUL : null);
	}

	@Override
	public void init(PlanInstance pInstance) {
		log = LogManager.getLogger();
		bfBase = pInstance.getBeliefBase();
		db = new BatchDataBase();
		path = "resources/GSA/database/";
	}

	@Override
	public void action() {

		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		XSSFWorkbook wb;

		for (File file : listOfFiles) {
			if (file.isFile()) {
				try {
					FileInputStream fileIs = new FileInputStream(file);	
					wb = new XSSFWorkbook(fileIs);

					int NumJobs = wb.getNumberOfSheets();
					String fName = file.getName().split("\\.")[0];

					XSSFSheet localSheet;
					for(int i = 0 ; i < NumJobs ; i++) {
						localSheet = wb.getSheetAt(i);
						db.put(fName, readSheet(localSheet));
						log.info("Database loaded in gsa for : " + fName);
					}
					log.info("Database fully loaded !!");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		bfBase.updateBelief(ID.GlobalScheduler.BeliefBaseConst.batchDatabase, db);
		done = true;
	}

	private CustomerBatches readSheet(XSSFSheet currSheet) {

		CustomerBatches allBatches = new CustomerBatches();

		Iterator<Row> rows = currSheet.rowIterator();

		while(rows.hasNext()) {
			XSSFRow row = (XSSFRow) rows.next();
			Iterator<Cell> cells = row.cellIterator();

			int count = 0; 
			String jobId = null;
			ArrayList<jobOperation> jobOpsList = new ArrayList<jobOperation>();

			while(cells.hasNext()) {
				XSSFCell cell = (XSSFCell) cells.next();

				if(count== 0) {
					jobId = cell.getStringCellValue();
				} else {
					jobOperation currOp = new jobOperation();
					String op = cell.getStringCellValue();
					currOp.setJobOperationType(op);
					jobOpsList.add(currOp);
				}
				count ++;
			}
			UnitBatchInfo batch = new UnitBatchInfo();
			batch.setOperations(jobOpsList);
			allBatches.put(jobId, batch);
		}

		return allBatches;
	}

	@Override
	public boolean done() {
		return done;
	}
}
