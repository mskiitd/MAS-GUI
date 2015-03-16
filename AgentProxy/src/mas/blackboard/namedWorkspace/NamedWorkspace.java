package mas.blackboard.namedWorkspace;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class NamedWorkspace implements WorkspaceName
{
   private String name = null;

   public NamedWorkspace(String name) {
//      Validate.notNull(name);
      this.name = name;
   }

   public String name() {
      return this.name;
   }

   public boolean equals(Object obj) {
      if (obj == null || !(obj instanceof NamedWorkspace)) return false;

      return this.name().equals(((NamedWorkspace)obj).name());
   }

   public int hashCode() {
      return new HashCodeBuilder().append(this.name).append(NamedWorkspace.class).toHashCode();
   }

   public String toString() {
      return "a NamedWorkspace \"" + this.name() + "\"";
   }
}
