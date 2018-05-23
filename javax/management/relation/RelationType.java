package javax.management.relation;

import java.io.Serializable;
import java.util.List;

public abstract interface RelationType
  extends Serializable
{
  public abstract String getRelationTypeName();
  
  public abstract List<RoleInfo> getRoleInfos();
  
  public abstract RoleInfo getRoleInfo(String paramString)
    throws IllegalArgumentException, RoleInfoNotFoundException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\relation\RelationType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */