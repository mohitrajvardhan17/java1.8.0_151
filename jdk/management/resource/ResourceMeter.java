package jdk.management.resource;

public abstract interface ResourceMeter
{
  public abstract long getValue();
  
  public abstract long getAllocated();
  
  public abstract ResourceType getType();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\management\resource\ResourceMeter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */