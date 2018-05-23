package sun.security.krb5.internal;

import java.io.IOException;
import java.util.Vector;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.internal.ccache.CCacheOutputStream;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class AuthorizationData
  implements Cloneable
{
  private AuthorizationDataEntry[] entry = null;
  
  private AuthorizationData() {}
  
  public AuthorizationData(AuthorizationDataEntry[] paramArrayOfAuthorizationDataEntry)
    throws IOException
  {
    if (paramArrayOfAuthorizationDataEntry != null)
    {
      entry = new AuthorizationDataEntry[paramArrayOfAuthorizationDataEntry.length];
      for (int i = 0; i < paramArrayOfAuthorizationDataEntry.length; i++)
      {
        if (paramArrayOfAuthorizationDataEntry[i] == null) {
          throw new IOException("Cannot create an AuthorizationData");
        }
        entry[i] = ((AuthorizationDataEntry)paramArrayOfAuthorizationDataEntry[i].clone());
      }
    }
  }
  
  public AuthorizationData(AuthorizationDataEntry paramAuthorizationDataEntry)
  {
    entry = new AuthorizationDataEntry[1];
    entry[0] = paramAuthorizationDataEntry;
  }
  
  public Object clone()
  {
    AuthorizationData localAuthorizationData = new AuthorizationData();
    if (entry != null)
    {
      entry = new AuthorizationDataEntry[entry.length];
      for (int i = 0; i < entry.length; i++) {
        entry[i] = ((AuthorizationDataEntry)entry[i].clone());
      }
    }
    return localAuthorizationData;
  }
  
  public AuthorizationData(DerValue paramDerValue)
    throws Asn1Exception, IOException
  {
    Vector localVector = new Vector();
    if (paramDerValue.getTag() != 48) {
      throw new Asn1Exception(906);
    }
    while (paramDerValue.getData().available() > 0) {
      localVector.addElement(new AuthorizationDataEntry(paramDerValue.getData().getDerValue()));
    }
    if (localVector.size() > 0)
    {
      entry = new AuthorizationDataEntry[localVector.size()];
      localVector.copyInto(entry);
    }
  }
  
  public byte[] asn1Encode()
    throws Asn1Exception, IOException
  {
    DerOutputStream localDerOutputStream = new DerOutputStream();
    DerValue[] arrayOfDerValue = new DerValue[entry.length];
    for (int i = 0; i < entry.length; i++) {
      arrayOfDerValue[i] = new DerValue(entry[i].asn1Encode());
    }
    localDerOutputStream.putSequence(arrayOfDerValue);
    return localDerOutputStream.toByteArray();
  }
  
  public static AuthorizationData parse(DerInputStream paramDerInputStream, byte paramByte, boolean paramBoolean)
    throws Asn1Exception, IOException
  {
    if ((paramBoolean) && (((byte)paramDerInputStream.peekByte() & 0x1F) != paramByte)) {
      return null;
    }
    DerValue localDerValue1 = paramDerInputStream.getDerValue();
    if (paramByte != (localDerValue1.getTag() & 0x1F)) {
      throw new Asn1Exception(906);
    }
    DerValue localDerValue2 = localDerValue1.getData().getDerValue();
    return new AuthorizationData(localDerValue2);
  }
  
  public void writeAuth(CCacheOutputStream paramCCacheOutputStream)
    throws IOException
  {
    for (int i = 0; i < entry.length; i++) {
      entry[i].writeEntry(paramCCacheOutputStream);
    }
  }
  
  public String toString()
  {
    String str = "AuthorizationData:\n";
    for (int i = 0; i < entry.length; i++) {
      str = str + entry[i].toString();
    }
    return str;
  }
  
  public int count()
  {
    return entry.length;
  }
  
  public AuthorizationDataEntry item(int paramInt)
  {
    return (AuthorizationDataEntry)entry[paramInt].clone();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\AuthorizationData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */