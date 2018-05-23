package sun.security.krb5.internal;

import java.io.IOException;
import java.math.BigInteger;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.internal.ccache.CCacheOutputStream;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class AuthorizationDataEntry
  implements Cloneable
{
  public int adType;
  public byte[] adData;
  
  private AuthorizationDataEntry() {}
  
  public AuthorizationDataEntry(int paramInt, byte[] paramArrayOfByte)
  {
    adType = paramInt;
    adData = paramArrayOfByte;
  }
  
  public Object clone()
  {
    AuthorizationDataEntry localAuthorizationDataEntry = new AuthorizationDataEntry();
    adType = adType;
    if (adData != null)
    {
      adData = new byte[adData.length];
      System.arraycopy(adData, 0, adData, 0, adData.length);
    }
    return localAuthorizationDataEntry;
  }
  
  public AuthorizationDataEntry(DerValue paramDerValue)
    throws Asn1Exception, IOException
  {
    if (paramDerValue.getTag() != 48) {
      throw new Asn1Exception(906);
    }
    DerValue localDerValue = paramDerValue.getData().getDerValue();
    if ((localDerValue.getTag() & 0x1F) == 0) {
      adType = localDerValue.getData().getBigInteger().intValue();
    } else {
      throw new Asn1Exception(906);
    }
    localDerValue = paramDerValue.getData().getDerValue();
    if ((localDerValue.getTag() & 0x1F) == 1) {
      adData = localDerValue.getData().getOctetString();
    } else {
      throw new Asn1Exception(906);
    }
    if (paramDerValue.getData().available() > 0) {
      throw new Asn1Exception(906);
    }
  }
  
  public byte[] asn1Encode()
    throws Asn1Exception, IOException
  {
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream2.putInteger(adType);
    localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), localDerOutputStream2);
    localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream2.putOctetString(adData);
    localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)1), localDerOutputStream2);
    localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream2.write((byte)48, localDerOutputStream1);
    return localDerOutputStream2.toByteArray();
  }
  
  public void writeEntry(CCacheOutputStream paramCCacheOutputStream)
    throws IOException
  {
    paramCCacheOutputStream.write16(adType);
    paramCCacheOutputStream.write32(adData.length);
    paramCCacheOutputStream.write(adData, 0, adData.length);
  }
  
  public String toString()
  {
    return "adType=" + adType + " adData.length=" + adData.length;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\AuthorizationDataEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */