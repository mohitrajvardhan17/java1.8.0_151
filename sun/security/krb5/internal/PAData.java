package sun.security.krb5.internal;

import java.io.IOException;
import java.math.BigInteger;
import sun.misc.HexDumpEncoder;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.internal.util.KerberosString;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class PAData
{
  private int pADataType;
  private byte[] pADataValue = null;
  private static final byte TAG_PATYPE = 1;
  private static final byte TAG_PAVALUE = 2;
  
  private PAData() {}
  
  public PAData(int paramInt, byte[] paramArrayOfByte)
  {
    pADataType = paramInt;
    if (paramArrayOfByte != null) {
      pADataValue = ((byte[])paramArrayOfByte.clone());
    }
  }
  
  public Object clone()
  {
    PAData localPAData = new PAData();
    pADataType = pADataType;
    if (pADataValue != null)
    {
      pADataValue = new byte[pADataValue.length];
      System.arraycopy(pADataValue, 0, pADataValue, 0, pADataValue.length);
    }
    return localPAData;
  }
  
  public PAData(DerValue paramDerValue)
    throws Asn1Exception, IOException
  {
    DerValue localDerValue = null;
    if (paramDerValue.getTag() != 48) {
      throw new Asn1Exception(906);
    }
    localDerValue = paramDerValue.getData().getDerValue();
    if ((localDerValue.getTag() & 0x1F) == 1) {
      pADataType = localDerValue.getData().getBigInteger().intValue();
    } else {
      throw new Asn1Exception(906);
    }
    localDerValue = paramDerValue.getData().getDerValue();
    if ((localDerValue.getTag() & 0x1F) == 2) {
      pADataValue = localDerValue.getData().getOctetString();
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
    localDerOutputStream2.putInteger(pADataType);
    localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)1), localDerOutputStream2);
    localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream2.putOctetString(pADataValue);
    localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)2), localDerOutputStream2);
    localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream2.write((byte)48, localDerOutputStream1);
    return localDerOutputStream2.toByteArray();
  }
  
  public int getType()
  {
    return pADataType;
  }
  
  public byte[] getValue()
  {
    return pADataValue == null ? null : (byte[])pADataValue.clone();
  }
  
  public static int getPreferredEType(PAData[] paramArrayOfPAData, int paramInt)
    throws IOException, Asn1Exception
  {
    if (paramArrayOfPAData == null) {
      return paramInt;
    }
    DerValue localDerValue1 = null;
    DerValue localDerValue2 = null;
    for (Object localObject3 : paramArrayOfPAData) {
      if (((PAData)localObject3).getValue() != null) {
        switch (((PAData)localObject3).getType())
        {
        case 11: 
          localDerValue1 = new DerValue(((PAData)localObject3).getValue());
          break;
        case 19: 
          localDerValue2 = new DerValue(((PAData)localObject3).getValue());
        }
      }
    }
    Object localObject2;
    if (localDerValue2 != null) {
      while (data.available() > 0)
      {
        ??? = data.getDerValue();
        localObject2 = new ETypeInfo2((DerValue)???);
        if (((ETypeInfo2)localObject2).getParams() == null) {
          return ((ETypeInfo2)localObject2).getEType();
        }
      }
    }
    if ((localDerValue1 != null) && (data.available() > 0))
    {
      ??? = data.getDerValue();
      localObject2 = new ETypeInfo((DerValue)???);
      return ((ETypeInfo)localObject2).getEType();
    }
    return paramInt;
  }
  
  public static SaltAndParams getSaltAndParams(int paramInt, PAData[] paramArrayOfPAData)
    throws Asn1Exception, IOException
  {
    if (paramArrayOfPAData == null) {
      return null;
    }
    DerValue localDerValue1 = null;
    DerValue localDerValue2 = null;
    String str = null;
    for (Object localObject3 : paramArrayOfPAData) {
      if (((PAData)localObject3).getValue() != null) {
        switch (((PAData)localObject3).getType())
        {
        case 3: 
          str = new String(((PAData)localObject3).getValue(), KerberosString.MSNAME ? "UTF8" : "8859_1");
          break;
        case 11: 
          localDerValue1 = new DerValue(((PAData)localObject3).getValue());
          break;
        case 19: 
          localDerValue2 = new DerValue(((PAData)localObject3).getValue());
        }
      }
    }
    Object localObject2;
    if (localDerValue2 != null) {
      while (data.available() > 0)
      {
        ??? = data.getDerValue();
        localObject2 = new ETypeInfo2((DerValue)???);
        if ((((ETypeInfo2)localObject2).getParams() == null) && (((ETypeInfo2)localObject2).getEType() == paramInt)) {
          return new SaltAndParams(((ETypeInfo2)localObject2).getSalt(), ((ETypeInfo2)localObject2).getParams());
        }
      }
    }
    if (localDerValue1 != null) {
      while (data.available() > 0)
      {
        ??? = data.getDerValue();
        localObject2 = new ETypeInfo((DerValue)???);
        if (((ETypeInfo)localObject2).getEType() == paramInt) {
          return new SaltAndParams(((ETypeInfo)localObject2).getSalt(), null);
        }
      }
    }
    if (str != null) {
      return new SaltAndParams(str, null);
    }
    return null;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(">>>Pre-Authentication Data:\n\t PA-DATA type = ").append(pADataType).append('\n');
    DerValue localDerValue3;
    Object localObject;
    switch (pADataType)
    {
    case 2: 
      localStringBuilder.append("\t PA-ENC-TIMESTAMP");
      break;
    case 11: 
      if (pADataValue != null) {
        try
        {
          DerValue localDerValue1 = new DerValue(pADataValue);
          while (data.available() > 0)
          {
            localDerValue3 = data.getDerValue();
            localObject = new ETypeInfo(localDerValue3);
            localStringBuilder.append("\t PA-ETYPE-INFO etype = ").append(((ETypeInfo)localObject).getEType()).append(", salt = ").append(((ETypeInfo)localObject).getSalt()).append('\n');
          }
        }
        catch (IOException|Asn1Exception localIOException1)
        {
          localStringBuilder.append("\t <Unparseable PA-ETYPE-INFO>\n");
        }
      }
      break;
    case 19: 
      if (pADataValue != null) {
        try
        {
          DerValue localDerValue2 = new DerValue(pADataValue);
          while (data.available() > 0)
          {
            localDerValue3 = data.getDerValue();
            localObject = new ETypeInfo2(localDerValue3);
            localStringBuilder.append("\t PA-ETYPE-INFO2 etype = ").append(((ETypeInfo2)localObject).getEType()).append(", salt = ").append(((ETypeInfo2)localObject).getSalt()).append(", s2kparams = ");
            byte[] arrayOfByte = ((ETypeInfo2)localObject).getParams();
            if (arrayOfByte == null) {
              localStringBuilder.append("null\n");
            } else if (arrayOfByte.length == 0) {
              localStringBuilder.append("empty\n");
            } else {
              localStringBuilder.append(new HexDumpEncoder().encodeBuffer(arrayOfByte));
            }
          }
        }
        catch (IOException|Asn1Exception localIOException2)
        {
          localStringBuilder.append("\t <Unparseable PA-ETYPE-INFO>\n");
        }
      }
      break;
    case 129: 
      localStringBuilder.append("\t PA-FOR-USER\n");
      break;
    }
    return localStringBuilder.toString();
  }
  
  public static class SaltAndParams
  {
    public final String salt;
    public final byte[] params;
    
    public SaltAndParams(String paramString, byte[] paramArrayOfByte)
    {
      if ((paramString != null) && (paramString.isEmpty())) {
        paramString = null;
      }
      salt = paramString;
      params = paramArrayOfByte;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\PAData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */