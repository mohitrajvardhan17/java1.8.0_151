package jdk.management.resource;

public enum ResourceAccuracy
{
  LOW,  MEDIUM,  HIGH,  HIGHEST;
  
  private ResourceAccuracy() {}
  
  public ResourceAccuracy improve()
  {
    if (equals(LOW)) {
      return MEDIUM;
    }
    if (equals(MEDIUM)) {
      return HIGH;
    }
    return HIGHEST;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\management\resource\ResourceAccuracy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */