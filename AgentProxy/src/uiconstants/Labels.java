package uiconstants;

/**
 * Contains constants for labels used within the GUI
 */
public class Labels {
	public static final int defaultJTextSize = 12;
	public static final String createJobButton = "Send Job";
	public static final String sendJobButton = "Submit";

	/**
	 * UI labels for customer UI
 	 */
	public class CustomerLabels {
		public static final String jobGenerateHeading = "Parameters of job ";
		public static final String jobPriority = "CPN :";
		public static final String jobPenalty = "Penalty Rate (/time) :";
		public static final String jobDueDate = "Due date :";
		public static final String BatchID = "Batch ID :";
		public static final String jobDimension = "Dimensions :";
		public static final String jobOperationHeading = "Job-Operations :";
		public static final String jobOpeationsDoneButton = "Done";
		public static final String batchNo = "Batch No : ";
		public static final String batchSize = "Batch Size : ";
	}

	/**
	 * UI Labels for machine ui
	 */
	public class MachineLabels {
	}

	/**
	 * UI labels for GSA UI
	 */
	public class GSLabels {
		public static final String queryForJobLabel = "Query Job";
	}

	/**
	 * UI labels for maintenance agent UI
	 */
	public class MaintenanceLabels {
		public static final String repairTimeLabel = "Repair Time (in minutes)";
	}
}
