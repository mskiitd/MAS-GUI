package mas.blackboard.workspace;
import java.io.Serializable;

import mas.blackboard.nameZoneData.NamedZoneData;
import mas.blackboard.namezonespace.NamedZoneSpace;
import mas.blackboard.zonespace.ZoneSpace;

/**
 * Identifies a Workspace on a Blackboard
 *
 */

public interface WorkspaceIFace extends Serializable
{
//   public static final WorkspaceIFace DEFAULT = new DefaultWorkspace();
//	public void createZoneSpace(NamedZoneSpace title);
	public void dropZoneSpace(NamedZoneSpace title);
	public ZoneSpace findZoneSpace(NamedZoneSpace title);
	public boolean insertItem(NamedZoneSpace zSpaceName,NamedZoneData var, Object obj);
	public void removeItem(Object obj);
	
}
