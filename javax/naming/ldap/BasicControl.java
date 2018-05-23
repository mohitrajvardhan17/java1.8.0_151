package javax.naming.ldap;

public class BasicControl
  implements Control
{
  protected String id;
  protected boolean criticality = false;
  protected byte[] value = null;
  private static final long serialVersionUID = -4233907508771791687L;
  
  public BasicControl(String paramString)
  {
    id = paramString;
  }
  
  public BasicControl(String paramString, boolean paramBoolean, byte[] paramArrayOfByte)
  {
    id = paramString;
    criticality = paramBoolean;
    value = paramArrayOfByte;
  }
  
  public String getID()
  {
    return id;
  }
  
  public boolean isCritical()
  {
    return criticality;
  }
  
  public byte[] getEncodedValue()
  {
    return value;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\ldap\BasicControl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */