package jdk.management.resource;

@FunctionalInterface
public abstract interface ResourceApprover
{
  public abstract long request(ResourceMeter paramResourceMeter, long paramLong1, long paramLong2, ResourceId paramResourceId);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\management\resource\ResourceApprover.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */