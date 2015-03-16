package mas.blackboard.namedWorkspace;

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
