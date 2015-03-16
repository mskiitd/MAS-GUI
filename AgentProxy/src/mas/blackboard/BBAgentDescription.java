package mas.blackboard;

import java.util.Date;

import jade.content.Concept;
import jade.core.AID;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.util.leap.ArrayList;
import jade.util.leap.Iterator;
import jade.util.leap.List;

public class BBAgentDescription implements Concept {

	private AID name;
	private List services = new ArrayList();
	
	public BBAgentDescription(){
			
	}
	
	public void setName(AID n) {
		name = n;
	}
	
	public AID getName() {
		return name;
	}
	
	public void addServices(ServiceDescription a) {
		services.add(a);
	}
	
	public boolean removeServices(ServiceDescription a) {
		return services.remove(a);
	}
	
	public void clearAllServices(){
		services.clear();
	}
	
	public Iterator getAllServices(){
		return services.iterator();
	}


}
