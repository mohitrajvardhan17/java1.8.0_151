package java.sql;

public class DriverPropertyInfo
{
  public String name;
  public String description = null;
  public boolean required = false;
  public String value = null;
  public String[] choices = null;
  
  public DriverPropertyInfo(String paramString1, String paramString2)
  {
    name = paramString1;
    value = paramString2;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\sql\DriverPropertyInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */