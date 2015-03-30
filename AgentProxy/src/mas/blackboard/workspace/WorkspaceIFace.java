package mas.blackboard.workspace;

import java.io.Serializable;
import mas.blackboard.namezonespace.NamedZoneSpace;
import mas.blackboard.zonespace.ZoneSpace;

/**
 * Identifies a Workspace on a Blackboard
 */

public interface WorkspaceIFace extends Serializable {
	
	public void dropZoneSpace(NamedZoneSpace title);
	public ZoneSpace findZoneSpace(NamedZoneSpace title);
	public void removeItem(Object obj);
}
