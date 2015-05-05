package mas.blackboard.workspace;

import java.io.Serializable;

import mas.blackboard.namezonespace.NamedZoneSpace;
import mas.blackboard.zonespace.ZoneSpace;

/**
 * Identifies a Workspace on a Blackboard
 */

public interface WorkspaceIFace extends Serializable {
	/**
	 * 
	 * @param title instance of Named Zone Space
	 * @return if found, returns Zone Space 
	 */
	public ZoneSpace findZoneSpace(NamedZoneSpace title);


}
