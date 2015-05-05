package mas.blackboard.namedWorkspace;

/**
 * Used in creation of named workspace. Creates workspace with name "DEFAULT_WORKSPACE"
 * 
 * recommondaed not to use unless you are changing code in library
 * @author NikhilChilwant
 *
 */
public final class DefaultWorkspace implements WorkspaceName
{
   public String name() {
      return "DEFAULT_WORKSPACE";
   }

   public boolean equals(Object obj) {
      return (obj != null && (obj instanceof DefaultWorkspace));
   }
   

   public int hashCode() {
      return DefaultWorkspace.class.hashCode();
   }

   public String toString() {
      return "the default Workspace";
   }
}
