package mas.blackboard.namezonespace;

import jade.core.AID;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class NamedZoneSpace implements ZoneSpaceName
{
   private AID name = null;

   public NamedZoneSpace(AID name) {

      this.name = name;
   }

   public String getLocalName() {
      return this.name.getLocalName();
   }

   public boolean equals(Object obj) {
      if (obj == null || !(obj instanceof NamedZoneSpace)) return false;

      return this.getLocalName().equals(((NamedZoneSpace)obj).getLocalName());
   }

   public int hashCode() {
      return new HashCodeBuilder().append(this.name).append(NamedZoneSpace.class).toHashCode();
   }

   public String toString() {
      return "a NamedZone \"" + this.getLocalName() + "\"";
   }
}
