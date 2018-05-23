package sun.security.krb5.internal;

import java.io.IOException;
import java.math.BigInteger;
import sun.security.krb5.Asn1Exception;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class MethodData
{
  private int methodType;
  private byte[] methodData = null;
  
  public MethodData(int paramInt, byte[] paramArrayOfByte)
  {
    methodType = paramInt;
    if (paramArrayOfByte != null) {
      methodData = ((byte[])paramArrayOfByte.clone());
    }
  }
  
  public MethodData(DerValue paramDerValue)
    throws Asn1Exception, IOException
  {
    if (paramDerValue.getTag() != 48) {
      throw new Asn1Exception(906);
    }
    DerValue localDerValue = paramDerValue.getData().getDerValue();
    if ((localDerValue.getTag() & 0x1F) == 0)
    {
      BigInteger localBigInteger = localDerValue.getData().getBigInteger();
      methodType = localBigInteger.intValue();
    }
    else
    {
      throw new Asn1Exception(906);
    }
    if (paramDerValue.getData().available() > 0)
    {
      localDerValue = paramDerValue.getData().getDerValue();
      if ((localDerValue.getTag() & 0x1F) == 1) {
        methodData = localDerValue.getData().getOctetString();
      } else {
        throw new Asn1Exception(906);
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
    localDerOutputStream2.putInteger(BigInteger.valueOf(methodType));
    localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), localDerOutputStream2);
    if (methodData != null)
    {
      localDerOutputStream2 = new DerOutputStream();
      localDerOutputStream2.putOctetString(methodData);
      localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)1), localDerOutputStream2);
    }
    localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream2.write((byte)48, localDerOutputStream1);
    return localDerOutputStream2.toByteArray();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\MethodData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */