package mas.localSchedulingproxy.database;

import java.io.Serializable;

public class OperationItemId implements Serializable{

	private static final long serialVersionUID = 1L;
	private String operationId;
	private String customerId;

	public OperationItemId(String op, String customer) {
		this.operationId = op;
		this.customerId = customer;
	}

	public OperationItemId() {
		this(null, null);
	}

	public String getOperationId() {
		return operationId;
	}
	public void setOperationId(String operationId) {
		this.operationId = operationId;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((customerId == null) ? 0 : customerId.hashCode());
		result = prime * result
				+ ((operationId == null) ? 0 : operationId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		OperationItemId other = (OperationItemId) obj;
		if (customerId == null) {
			if (other.customerId != null)
				return false;
		} else if (!customerId.equals(other.customerId))
			return false;
		if (operationId == null) {
			if (other.operationId != null)
				return false;
		} else if (!operationId.equals(other.operationId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return new StringBuilder().
				append(" cid : " + customerId).
				append(" oid : " + operationId).
				toString();
	}

}
