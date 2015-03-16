package mas.util;

public class BlackboardId {
	
	public class Agents{
		public static final String Customer="customer";
		public static final String GlobalScheduling="scheduling-agent";
		public static final String LocalScheduling="machine-simulator-schedule";
		public static final String Maintenance="machine-simulator-maint";
		public static final String Machine="machine-simulator-machine";
		public static final String Blackboard="blackboard";
	}
		class CustomeParameters{
		public static final String JobList="JobList";
		public static final String NegotiationBidList="NegotiationBids";
		public static final String IsJobAcceptedList="IsJobAcceptedList";
	}
	
	class LocalSchedulerParameters{
		public static final String WaitingTime="WaititngTimeData";
		public static final String Bid="BidData";
		public static final String JobForMachine="JobForMachineData";
		public static final String WorkOrder="WorkOrderData";
	}
	class MachineParameters{
		
	}
	
	class BeliefId{
		public static final String Customer="customer";
		public static final String GlobalScheduling="globalscheduling";
		public static final String LocalScheduling="localscheduling";
		public static final String Maintenance="maintenance";
		public static final String Machine="machine";
	}
	
}
