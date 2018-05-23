package javax.management.relation;

public abstract interface RelationSupportMBean
  extends Relation
{
  public abstract Boolean isInRelationService();
  
  public abstract void setRelationServiceManagementFlag(Boolean paramBoolean)
    throws IllegalArgumentException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\relation\RelationSupportMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */