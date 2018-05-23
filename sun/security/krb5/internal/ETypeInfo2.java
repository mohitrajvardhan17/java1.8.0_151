package sun.security.krb5.internal;

import java.io.IOException;
import java.math.BigInteger;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.internal.util.KerberosString;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class ETypeInfo2
{
  private int etype;
  private String saltStr = null;
  private byte[] s2kparams = null;
  private static final byte TAG_TYPE = 0;
  private static final byte TAG_VALUE1 = 1;
  private static final byte TAG_VALUE2 = 2;
  
  private ETypeInfo2() {}
  
  public ETypeInfo2(int paramInt, String paramString, byte[] paramArrayOfByte)
  {
    etype = paramInt;
    saltStr = paramString;
    if (paramArrayOfByte != null) {
      s2kparams = ((byte[])paramArrayOfByte.clone());
    }
  }
  
  public Object clone()
  {
    ETypeInfo2 localETypeInfo2 = new ETypeInfo2();
    etype = etype;
    saltStr = saltStr;
    if (s2kparams != null)
    {
      s2kparams = new byte[s2kparams.length];
      System.arraycopy(s2kparams, 0, s2kparams, 0, s2kparams.length);
    }
    return localETypeInfo2;
  }
  
  public ETypeInfo2(DerValue paramDerValue)
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
    if ((paramDerValue.getData().available() > 0) && ((paramDerValue.getData().peekByte() & 0x1F) == 1))
    {
      localDerValue = paramDerValue.getData().getDerValue();
      saltStr = new KerberosString(localDerValue.getData().getDerValue()).toString();
    }
    if ((paramDerValue.getData().available() > 0) && ((paramDerValue.getData().peekByte() & 0x1F) == 2))
    {
      localDerValue = paramDerValue.getData().getDerValue();
      s2kparams = localDerValue.getData().getOctetString();
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
    if (saltStr != null)
    {
      localDerOutputStream2 = new DerOutputStream();
      localDerOutputStream2.putDerValue(new KerberosString(saltStr).toDerValue());
      localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)1), localDerOutputStream2);
    }
    if (s2kparams != null)
    {
      localDerOutputStream2 = new DerOutputStream();
      localDerOutputStream2.putOctetString(s2kparams);
      localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)2), localDerOutputStream2);
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
    return saltStr;
  }
  
  public byte[] getParams()
  {
    return s2kparams == null ? null : (byte[])s2kparams.clone();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\ETypeInfo2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */