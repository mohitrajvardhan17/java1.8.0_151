package com.sun.jndi.ldap;

import java.io.IOException;

public final class PersistentSearchControl
  extends BasicControl
{
  public static final String OID = "2.16.840.1.113730.3.4.3";
  public static final int ADD = 1;
  public static final int DELETE = 2;
  public static final int MODIFY = 4;
  public static final int RENAME = 8;
  public static final int ANY = 15;
  private int changeTypes = 15;
  private boolean changesOnly = false;
  private boolean returnControls = true;
  private static final long serialVersionUID = 6335140491154854116L;
  
  public PersistentSearchControl()
    throws IOException
  {
    super("2.16.840.1.113730.3.4.3");
    value = setEncodedValue();
  }
  
  public PersistentSearchControl(int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
    throws IOException
  {
    super("2.16.840.1.113730.3.4.3", paramBoolean3, null);
    changeTypes = paramInt;
    changesOnly = paramBoolean1;
    returnControls = paramBoolean2;
    value = setEncodedValue();
  }
  
  private byte[] setEncodedValue()
    throws IOException
  {
    BerEncoder localBerEncoder = new BerEncoder(32);
    localBerEncoder.beginSeq(48);
    localBerEncoder.encodeInt(changeTypes);
    localBerEncoder.encodeBoolean(changesOnly);
    localBerEncoder.encodeBoolean(returnControls);
    localBerEncoder.endSeq();
    return localBerEncoder.getTrimmedBuf();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\PersistentSearchControl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */