package java.security;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sun.security.util.Debug;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public final class PKCS12Attribute
  implements KeyStore.Entry.Attribute
{
  private static final Pattern COLON_SEPARATED_HEX_PAIRS = Pattern.compile("^[0-9a-fA-F]{2}(:[0-9a-fA-F]{2})+$");
  private String name;
  private String value;
  private byte[] encoded;
  private int hashValue = -1;
  
  public PKCS12Attribute(String paramString1, String paramString2)
  {
    if ((paramString1 == null) || (paramString2 == null)) {
      throw new NullPointerException();
    }
    ObjectIdentifier localObjectIdentifier;
    try
    {
      localObjectIdentifier = new ObjectIdentifier(paramString1);
    }
    catch (IOException localIOException1)
    {
      throw new IllegalArgumentException("Incorrect format: name", localIOException1);
    }
    name = paramString1;
    int i = paramString2.length();
    String[] arrayOfString;
    if ((paramString2.charAt(0) == '[') && (paramString2.charAt(i - 1) == ']')) {
      arrayOfString = paramString2.substring(1, i - 1).split(", ");
    } else {
      arrayOfString = new String[] { paramString2 };
    }
    value = paramString2;
    try
    {
      encoded = encode(localObjectIdentifier, arrayOfString);
    }
    catch (IOException localIOException2)
    {
      throw new IllegalArgumentException("Incorrect format: value", localIOException2);
    }
  }
  
  public PKCS12Attribute(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {
      throw new NullPointerException();
    }
    encoded = ((byte[])paramArrayOfByte.clone());
    try
    {
      parse(paramArrayOfByte);
    }
    catch (IOException localIOException)
    {
      throw new IllegalArgumentException("Incorrect format: encoded", localIOException);
    }
  }
  
  public String getName()
  {
    return name;
  }
  
  public String getValue()
  {
    return value;
  }
  
  public byte[] getEncoded()
  {
    return (byte[])encoded.clone();
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof PKCS12Attribute)) {
      return false;
    }
    return Arrays.equals(encoded, ((PKCS12Attribute)paramObject).getEncoded());
  }
  
  public int hashCode()
  {
    if (hashValue == -1) {
      Arrays.hashCode(encoded);
    }
    return hashValue;
  }
  
  public String toString()
  {
    return name + "=" + value;
  }
  
  private byte[] encode(ObjectIdentifier paramObjectIdentifier, String[] paramArrayOfString)
    throws IOException
  {
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    localDerOutputStream1.putOID(paramObjectIdentifier);
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    for (CharSequence localCharSequence : paramArrayOfString) {
      if (COLON_SEPARATED_HEX_PAIRS.matcher(localCharSequence).matches())
      {
        byte[] arrayOfByte = new BigInteger(localCharSequence.replace(":", ""), 16).toByteArray();
        if (arrayOfByte[0] == 0) {
          arrayOfByte = Arrays.copyOfRange(arrayOfByte, 1, arrayOfByte.length);
        }
        localDerOutputStream2.putOctetString(arrayOfByte);
      }
      else
      {
        localDerOutputStream2.putUTF8String(localCharSequence);
      }
    }
    localDerOutputStream1.write((byte)49, localDerOutputStream2);
    ??? = new DerOutputStream();
    ((DerOutputStream)???).write((byte)48, localDerOutputStream1);
    return ((DerOutputStream)???).toByteArray();
  }
  
  private void parse(byte[] paramArrayOfByte)
    throws IOException
  {
    DerInputStream localDerInputStream1 = new DerInputStream(paramArrayOfByte);
    DerValue[] arrayOfDerValue1 = localDerInputStream1.getSequence(2);
    ObjectIdentifier localObjectIdentifier = arrayOfDerValue1[0].getOID();
    DerInputStream localDerInputStream2 = new DerInputStream(arrayOfDerValue1[1].toByteArray());
    DerValue[] arrayOfDerValue2 = localDerInputStream2.getSet(1);
    String[] arrayOfString = new String[arrayOfDerValue2.length];
    for (int i = 0; i < arrayOfDerValue2.length; i++) {
      if (tag == 4)
      {
        arrayOfString[i] = Debug.toString(arrayOfDerValue2[i].getOctetString());
      }
      else
      {
        String str;
        if ((str = arrayOfDerValue2[i].getAsString()) != null) {
          arrayOfString[i] = str;
        } else if (tag == 6) {
          arrayOfString[i] = arrayOfDerValue2[i].getOID().toString();
        } else if (tag == 24) {
          arrayOfString[i] = arrayOfDerValue2[i].getGeneralizedTime().toString();
        } else if (tag == 23) {
          arrayOfString[i] = arrayOfDerValue2[i].getUTCTime().toString();
        } else if (tag == 2) {
          arrayOfString[i] = arrayOfDerValue2[i].getBigInteger().toString();
        } else if (tag == 1) {
          arrayOfString[i] = String.valueOf(arrayOfDerValue2[i].getBoolean());
        } else {
          arrayOfString[i] = Debug.toString(arrayOfDerValue2[i].getDataBytes());
        }
      }
    }
    name = localObjectIdentifier.toString();
    value = (arrayOfString.length == 1 ? arrayOfString[0] : Arrays.toString(arrayOfString));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\PKCS12Attribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */