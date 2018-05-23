package com.sun.jndi.ldap;

import java.io.IOException;

public final class EntryChangeResponseControl
  extends BasicControl
{
  public static final String OID = "2.16.840.1.113730.3.4.7";
  public static final int ADD = 1;
  public static final int DELETE = 2;
  public static final int MODIFY = 4;
  public static final int RENAME = 8;
  private int changeType;
  private String previousDN = null;
  private long changeNumber = -1L;
  private static final long serialVersionUID = -2087354136750180511L;
  
  public EntryChangeResponseControl(String paramString, boolean paramBoolean, byte[] paramArrayOfByte)
    throws IOException
  {
    super(paramString, paramBoolean, paramArrayOfByte);
    if ((paramArrayOfByte != null) && (paramArrayOfByte.length > 0))
    {
      BerDecoder localBerDecoder = new BerDecoder(paramArrayOfByte, 0, paramArrayOfByte.length);
      localBerDecoder.parseSeq(null);
      changeType = localBerDecoder.parseEnumeration();
      if ((localBerDecoder.bytesLeft() > 0) && (localBerDecoder.peekByte() == 4)) {
        previousDN = localBerDecoder.parseString(true);
      }
      if ((localBerDecoder.bytesLeft() > 0) && (localBerDecoder.peekByte() == 2)) {
        changeNumber = localBerDecoder.parseInt();
      }
    }
  }
  
  public int getChangeType()
  {
    return changeType;
  }
  
  public String getPreviousDN()
  {
    return previousDN;
  }
  
  public long getChangeNumber()
  {
    return changeNumber;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\EntryChangeResponseControl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */