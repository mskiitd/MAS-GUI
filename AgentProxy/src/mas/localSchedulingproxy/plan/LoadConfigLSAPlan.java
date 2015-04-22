package mas.localSchedulingproxy.plan;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Properties;

import jade.core.behaviours.Behaviour;
import mas.localSchedulingproxy.database.OperationDataBase;
import mas.util.ID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bdi4jade.core.BeliefBase;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class LoadConfigLSAPlan extends Behaviour implements PlanBody {

	private static final long serialVersionUID = 1L;
	private BeliefBase bfBase;
	private Logger log;
	private boolean done = false;
	private String path;
	private Properties prop;
	private double schedulingPeriod;
	private double timeUnitConversion = 1000;

	@Override
	public EndState getEndState() {
		return (done ? EndState.SUCCESSFUL : null);
	}

	@Override
	public void init(PlanInstance pInstance) {
		log = LogManager.getLogger();
		bfBase = pInstance.getBeliefBase();
		prop = new Properties();
	}

	@Override
	public void action() {
		// read properties file to initialize scheduling period
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream("resources/mas.properties");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		if (inputStream != null) {
			try {
				prop.load(inputStream);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			log.error("mas.properties file is not found");
		}
		// update scheduling interval into belief base
		String period = prop.getProperty("SchedulingInterval");
		if(period != null && period.matches("-?\\d+(\\.\\d+)?") ) {
			schedulingPeriod = Double.parseDouble(period);
			schedulingPeriod = schedulingPeriod * timeUnitConversion ;
			log.info("period : " + schedulingPeriod);
			bfBase.updateBelief(ID.LocalScheduler.BeliefBaseConst.schedulingInterval, schedulingPeriod);
		} else {
			log.info("Wrong format for period : " + schedulingPeriod);
		}
		
		// update regret threshold into belief base
		String regThresh = prop.getProperty("regretThershold");
		if(regThresh != null && regThresh.matches("-?\\d+(\\.\\d+)?") ) {
			log.info("Regret threshold : " + regThresh);
			Double numThresh = Double.parseDouble(regThresh);
			bfBase.updateBelief(ID.LocalScheduler.BeliefBaseConst.regretThreshold, numThresh);
		} else {
			log.info("Wrong format for regret threshold : " + regThresh);
		}

		OperationDataBase db;
		path = "resources/LSA/database/" + myAgent.getLocalName() + "_db.data";
		File toRead = new File(path);
		try {
			if(toRead.exists() && toRead.length() != 0) {
				FileInputStream fis;

				fis = new FileInputStream(toRead);

				ObjectInputStream ois = new ObjectInputStream(fis);
				db = (OperationDataBase)ois.readObject();
				ois.close();

				log.info("updating database for the machine ");
				bfBase.updateBelief(ID.LocalScheduler.BeliefBaseConst.operationDatabase, db);
			} else {
				toRead.createNewFile();
			}

			done = true;

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean done() {
		return done;
	}

}
