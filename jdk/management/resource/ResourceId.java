package jdk.management.resource;

@FunctionalInterface
public abstract interface ResourceId
{
  public abstract String getName();
  
  public ResourceAccuracy getAccuracy()
  {
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\management\resource\ResourceId.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */