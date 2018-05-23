package sun.security.krb5.internal;

import java.io.IOException;
import java.math.BigInteger;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.internal.util.KerberosString;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class ETypeInfo
{
  private int etype;
  private String salt = null;
  private static final byte TAG_TYPE = 0;
  private static final byte TAG_VALUE = 1;
  
  private ETypeInfo() {}
  
  public ETypeInfo(int paramInt, String paramString)
  {
    etype = paramInt;
    salt = paramString;
  }
  
  public Object clone()
  {
    return new ETypeInfo(etype, salt);
  }
  
  public ETypeInfo(DerValue paramDerValue)
    throws Asn1Exception, IOException
  {
    DerValue localDerValue = null;
    if (paramDerValue.getTag() != 48) {
      throw new Asn1Exception(906);
    }
    localDerValue = paramDerValue.getData().getDerValue();
    if ((localDerValue.getTag() & 0x1F) == 0) {
      etype = localDerValue.getData().getBigInteger().intValue();
    } else {
      throw new Asn1Exception(906);
    }
    if (paramDerValue.getData().available() > 0)
    {
      localDerValue = paramDerValue.getData().getDerValue();
      if ((localDerValue.getTag() & 0x1F) == 1)
      {
        byte[] arrayOfByte = localDerValue.getData().getOctetString();
        if (KerberosString.MSNAME) {
          salt = new String(arrayOfByte, "UTF8");
        } else {
          salt = new String(arrayOfByte);
        }
      }
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
    localDerOutputStream2.putInteger(etype);
    localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), localDerOutputStream2);
    if (salt != null)
    {
      localDerOutputStream2 = new DerOutputStream();
      if (KerberosString.MSNAME) {
        localDerOutputStream2.putOctetString(salt.getBytes("UTF8"));
      } else {
        localDerOutputStream2.putOctetString(salt.getBytes());
      }
      localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)1), localDerOutputStream2);
    }
    localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream2.write((byte)48, localDerOutputStream1);
    return localDerOutputStream2.toByteArray();
  }
  
  public int getEType()
  {
    return etype;
  }
  
  public String getSalt()
  {
    return salt;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\ETypeInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */