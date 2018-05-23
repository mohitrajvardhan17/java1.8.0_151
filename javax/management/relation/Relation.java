package javax.management.relation;

import java.util.List;
import java.util.Map;
import javax.management.ObjectName;

public abstract interface Relation
{
  public abstract List<ObjectName> getRole(String paramString)
    throws IllegalArgumentException, RoleNotFoundException, RelationServiceNotRegisteredException;
  
  public abstract RoleResult getRoles(String[] paramArrayOfString)
    throws IllegalArgumentException, RelationServiceNotRegisteredException;
  
  public abstract Integer getRoleCardinality(String paramString)
    throws IllegalArgumentException, RoleNotFoundException;
  
  public abstract RoleResult getAllRoles()
    throws RelationServiceNotRegisteredException;
  
  public abstract RoleList retrieveAllRoles();
  
  public abstract void setRole(Role paramRole)
    throws IllegalArgumentException, RoleNotFoundException, RelationTypeNotFoundException, InvalidRoleValueException, RelationServiceNotRegisteredException, RelationNotFoundException;
  
  public abstract RoleResult setRoles(RoleList paramRoleList)
    throws IllegalArgumentException, RelationServiceNotRegisteredException, RelationTypeNotFoundException, RelationNotFoundException;
  
  public abstract void handleMBeanUnregistration(ObjectName paramObjectName, String paramString)
    throws IllegalArgumentException, RoleNotFoundException, InvalidRoleValueException, RelationServiceNotRegisteredException, RelationTypeNotFoundException, RelationNotFoundException;
  
  public abstract Map<ObjectName, List<String>> getReferencedMBeans();
  
  public abstract String getRelationTypeName();
  
  public abstract ObjectName getRelationServiceName();
  
  public abstract String getRelationId();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\relation\Relation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */