package mas.maintenanceproxy.classes;

import java.io.Serializable;
import java.util.Date;

public class PMaintenance implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String maintId;
	private Date expectedStartTime;
	private Date expectedFinishTime;
	private Date actualStartTime;
	private Date actualFinishTime;
	
	private MaintStatus maintStatus;
	
	public PMaintenance(String id) {
		this.maintId = id;
	}
	
	public Date getExpectedStartTime() {
		return expectedStartTime;
	}
	public void setExpectedStartTime(Date expectedStartTime) {
		this.expectedStartTime = expectedStartTime;
	}
	public Date getExpectedFinishTime() {
		return expectedFinishTime;
	}
	public void setExpectedFinishTime(Date expectedFinishTime) {
		this.expectedFinishTime = expectedFinishTime;
	}
	public Date getActualStartTime() {
		return actualStartTime;
	}
	public void setActualStartTime(Date actualStartTime) {
		this.actualStartTime = actualStartTime;
	}
	public Date getActualFinishTime() {
		return actualFinishTime;
	}
	public void setActualFinishTime(Date actualFinishTime) {
		this.actualFinishTime = actualFinishTime;
	}
	
	public String getMaintId() {
		return maintId;
	}

	public MaintStatus getMaintStatus() {
		return maintStatus;
	}

	public void setMaintStatus(MaintStatus maintStatus) {
		this.maintStatus = maintStatus;
	}

}
