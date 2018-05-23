package com.sun.jndi.ldap;

import javax.naming.ldap.Control;

public class BasicControl
  implements Control
{
  protected String id;
  protected boolean criticality = false;
  protected byte[] value = null;
  private static final long serialVersionUID = -5914033725246428413L;
  
  public BasicControl(String paramString)
  {
    id = paramString;
  }
  
  public BasicControl(String paramString, boolean paramBoolean, byte[] paramArrayOfByte)
  {
    id = paramString;
    criticality = paramBoolean;
    if (paramArrayOfByte != null) {
      value = ((byte[])paramArrayOfByte.clone());
    }
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
    return value == null ? null : (byte[])value.clone();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\BasicControl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */