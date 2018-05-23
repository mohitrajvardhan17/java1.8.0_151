package java.sql;

class DriverInfo
{
  final Driver driver;
  DriverAction da;
  
  DriverInfo(Driver paramDriver, DriverAction paramDriverAction)
  {
    driver = paramDriver;
    da = paramDriverAction;
  }
  
  public boolean equals(Object paramObject)
  {
    return ((paramObject instanceof DriverInfo)) && (driver == driver);
  }
  
  public int hashCode()
  {
    return driver.hashCode();
  }
  
  public String toString()
  {
    return "driver[className=" + driver + "]";
  }
  
  DriverAction action()
  {
    return da;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\sql\DriverInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */