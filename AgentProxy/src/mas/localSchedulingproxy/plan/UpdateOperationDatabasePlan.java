package mas.localSchedulingproxy.plan;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import jade.core.behaviours.Behaviour;
import mas.localSchedulingproxy.database.OperationDataBase;
import mas.util.ID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import bdi4jade.core.BeliefBase;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class UpdateOperationDatabasePlan extends Behaviour implements PlanBody {

	private static final long serialVersionUID = 1L;
	private BeliefBase bfBase;
	private Logger log;
	private boolean done = false;
	private String path;

	@Override
	public EndState getEndState() {
		return (done ? EndState.SUCCESSFUL : null);
	}

	@Override
	public void init(PlanInstance pInstance) {
		log = LogManager.getLogger();
		bfBase = pInstance.getBeliefBase();
	}

	@Override
	public void action() {
		OperationDataBase db;
		path = "resources/database/" + myAgent.getLocalName() + "_db.data";
		File toRead = new File(path);
		FileInputStream fis;
		try {
			fis = new FileInputStream(toRead);
			ObjectInputStream ois = new ObjectInputStream(fis);
			db = (OperationDataBase)ois.readObject();
			ois.close();
			
			log.info("updating database for the machine ");
			bfBase.updateBelief(ID.LocalScheduler.BeliefBaseConst.operationDatabase, db);
			
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
