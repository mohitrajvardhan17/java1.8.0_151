package sun.security.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;

public final class ObjectIdentifier
  implements Serializable
{
  private byte[] encoding = null;
  private volatile transient String stringForm;
  private static final long serialVersionUID = 8697030238860181294L;
  private Object components = null;
  private int componentLen = -1;
  private transient boolean componentsCalculated = false;
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    if (encoding == null)
    {
      int[] arrayOfInt = (int[])components;
      if (componentLen > arrayOfInt.length) {
        componentLen = arrayOfInt.length;
      }
      init(arrayOfInt, componentLen);
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    if (!componentsCalculated)
    {
      int[] arrayOfInt = toIntArray();
      if (arrayOfInt != null)
      {
        components = arrayOfInt;
        componentLen = arrayOfInt.length;
      }
      else
      {
        components = HugeOidNotSupportedByOldJDK.theOne;
      }
      componentsCalculated = true;
    }
    paramObjectOutputStream.defaultWriteObject();
  }
  
  public ObjectIdentifier(String paramString)
    throws IOException
  {
    int i = 46;
    int j = 0;
    int k = 0;
    int m = 0;
    byte[] arrayOfByte = new byte[paramString.length()];
    int n = 0;
    int i1 = 0;
    try
    {
      String str = null;
      do
      {
        int i2 = 0;
        k = paramString.indexOf(i, j);
        if (k == -1)
        {
          str = paramString.substring(j);
          i2 = paramString.length() - j;
        }
        else
        {
          str = paramString.substring(j, k);
          i2 = k - j;
        }
        if (i2 > 9)
        {
          BigInteger localBigInteger = new BigInteger(str);
          if (i1 == 0)
          {
            checkFirstComponent(localBigInteger);
            n = localBigInteger.intValue();
          }
          else
          {
            if (i1 == 1)
            {
              checkSecondComponent(n, localBigInteger);
              localBigInteger = localBigInteger.add(BigInteger.valueOf(40 * n));
            }
            else
            {
              checkOtherComponent(i1, localBigInteger);
            }
            m += pack7Oid(localBigInteger, arrayOfByte, m);
          }
        }
        else
        {
          int i3 = Integer.parseInt(str);
          if (i1 == 0)
          {
            checkFirstComponent(i3);
            n = i3;
          }
          else
          {
            if (i1 == 1)
            {
              checkSecondComponent(n, i3);
              i3 += 40 * n;
            }
            else
            {
              checkOtherComponent(i1, i3);
            }
            m += pack7Oid(i3, arrayOfByte, m);
          }
        }
        j = k + 1;
        i1++;
      } while (k != -1);
      checkCount(i1);
      encoding = new byte[m];
      System.arraycopy(arrayOfByte, 0, encoding, 0, m);
      stringForm = paramString;
    }
    catch (IOException localIOException)
    {
      throw localIOException;
    }
    catch (Exception localException)
    {
      throw new IOException("ObjectIdentifier() -- Invalid format: " + localException.toString(), localException);
    }
  }
  
  public ObjectIdentifier(int[] paramArrayOfInt)
    throws IOException
  {
    checkCount(paramArrayOfInt.length);
    checkFirstComponent(paramArrayOfInt[0]);
    checkSecondComponent(paramArrayOfInt[0], paramArrayOfInt[1]);
    for (int i = 2; i < paramArrayOfInt.length; i++) {
      checkOtherComponent(i, paramArrayOfInt[i]);
    }
    init(paramArrayOfInt, paramArrayOfInt.length);
  }
  
  public ObjectIdentifier(DerInputStream paramDerInputStream)
    throws IOException
  {
    int i = (byte)paramDerInputStream.getByte();
    if (i != 6) {
      throw new IOException("ObjectIdentifier() -- data isn't an object ID (tag = " + i + ")");
    }
    int j = paramDerInputStream.getLength();
    if (j > paramDerInputStream.available()) {
      throw new IOException("ObjectIdentifier() -- length exceedsdata available.  Length: " + j + ", Available: " + paramDerInputStream.available());
    }
    encoding = new byte[j];
    paramDerInputStream.getBytes(encoding);
    check(encoding);
  }
  
  ObjectIdentifier(DerInputBuffer paramDerInputBuffer)
    throws IOException
  {
    DerInputStream localDerInputStream = new DerInputStream(paramDerInputBuffer);
    encoding = new byte[localDerInputStream.available()];
    localDerInputStream.getBytes(encoding);
    check(encoding);
  }
  
  private void init(int[] paramArrayOfInt, int paramInt)
  {
    int i = 0;
    byte[] arrayOfByte = new byte[paramInt * 5 + 1];
    if (paramArrayOfInt[1] < Integer.MAX_VALUE - paramArrayOfInt[0] * 40)
    {
      i += pack7Oid(paramArrayOfInt[0] * 40 + paramArrayOfInt[1], arrayOfByte, i);
    }
    else
    {
      BigInteger localBigInteger = BigInteger.valueOf(paramArrayOfInt[1]);
      localBigInteger = localBigInteger.add(BigInteger.valueOf(paramArrayOfInt[0] * 40));
      i += pack7Oid(localBigInteger, arrayOfByte, i);
    }
    for (int j = 2; j < paramInt; j++) {
      i += pack7Oid(paramArrayOfInt[j], arrayOfByte, i);
    }
    encoding = new byte[i];
    System.arraycopy(arrayOfByte, 0, encoding, 0, i);
  }
  
  public static ObjectIdentifier newInternal(int[] paramArrayOfInt)
  {
    try
    {
      return new ObjectIdentifier(paramArrayOfInt);
    }
    catch (IOException localIOException)
    {
      throw new RuntimeException(localIOException);
    }
  }
  
  void encode(DerOutputStream paramDerOutputStream)
    throws IOException
  {
    paramDerOutputStream.write((byte)6, encoding);
  }
  
  @Deprecated
  public boolean equals(ObjectIdentifier paramObjectIdentifier)
  {
    return equals(paramObjectIdentifier);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof ObjectIdentifier)) {
      return false;
    }
    ObjectIdentifier localObjectIdentifier = (ObjectIdentifier)paramObject;
    return Arrays.equals(encoding, encoding);
  }
  
  public int hashCode()
  {
    return Arrays.hashCode(encoding);
  }
  
  private int[] toIntArray()
  {
    int i = encoding.length;
    int[] arrayOfInt = new int[20];
    int j = 0;
    BigInteger localBigInteger1 = 0;
    for (BigInteger localBigInteger2 = 0; localBigInteger2 < i; localBigInteger2++)
    {
      if ((encoding[localBigInteger2] & 0x80) == 0)
      {
        BigInteger localBigInteger4;
        if (localBigInteger2 - localBigInteger1 + 1 > 4)
        {
          BigInteger localBigInteger3 = new BigInteger(pack(encoding, localBigInteger1, localBigInteger2 - localBigInteger1 + 1, 7, 8));
          if (localBigInteger1 == 0)
          {
            arrayOfInt[(j++)] = 2;
            localBigInteger4 = localBigInteger3.subtract(BigInteger.valueOf(80L));
            if (localBigInteger4.compareTo(BigInteger.valueOf(2147483647L)) == 1) {
              return null;
            }
            arrayOfInt[(j++)] = localBigInteger4.intValue();
          }
          else
          {
            if (localBigInteger3.compareTo(BigInteger.valueOf(2147483647L)) == 1) {
              return null;
            }
            arrayOfInt[(j++)] = localBigInteger3.intValue();
          }
        }
        else
        {
          int k = 0;
          for (localBigInteger4 = localBigInteger1; localBigInteger4 <= localBigInteger2; localBigInteger4++)
          {
            k <<= 7;
            int m = encoding[localBigInteger4];
            k |= m & 0x7F;
          }
          if (localBigInteger1 == 0)
          {
            if (k < 80)
            {
              arrayOfInt[(j++)] = (k / 40);
              arrayOfInt[(j++)] = (k % 40);
            }
            else
            {
              arrayOfInt[(j++)] = 2;
              arrayOfInt[(j++)] = (k - 80);
            }
          }
          else {
            arrayOfInt[(j++)] = k;
          }
        }
        localBigInteger1 = localBigInteger2 + 1;
      }
      if (j >= arrayOfInt.length) {
        arrayOfInt = Arrays.copyOf(arrayOfInt, j + 10);
      }
    }
    return Arrays.copyOf(arrayOfInt, j);
  }
  
  public String toString()
  {
    String str = stringForm;
    if (str == null)
    {
      int i = encoding.length;
      StringBuffer localStringBuffer = new StringBuffer(i * 4);
      int j = 0;
      for (int k = 0; k < i; k++) {
        if ((encoding[k] & 0x80) == 0)
        {
          if (j != 0) {
            localStringBuffer.append('.');
          }
          if (k - j + 1 > 4)
          {
            BigInteger localBigInteger = new BigInteger(pack(encoding, j, k - j + 1, 7, 8));
            if (j == 0)
            {
              localStringBuffer.append("2.");
              localStringBuffer.append(localBigInteger.subtract(BigInteger.valueOf(80L)));
            }
            else
            {
              localStringBuffer.append(localBigInteger);
            }
          }
          else
          {
            int m = 0;
            for (int n = j; n <= k; n++)
            {
              m <<= 7;
              int i1 = encoding[n];
              m |= i1 & 0x7F;
            }
            if (j == 0)
            {
              if (m < 80)
              {
                localStringBuffer.append(m / 40);
                localStringBuffer.append('.');
                localStringBuffer.append(m % 40);
              }
              else
              {
                localStringBuffer.append("2.");
                localStringBuffer.append(m - 80);
              }
            }
            else {
              localStringBuffer.append(m);
            }
          }
          j = k + 1;
        }
      }
      str = localStringBuffer.toString();
      stringForm = str;
    }
    return str;
  }
  
  private static byte[] pack(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    assert ((paramInt3 > 0) && (paramInt3 <= 8)) : "input NUB must be between 1 and 8";
    assert ((paramInt4 > 0) && (paramInt4 <= 8)) : "output NUB must be between 1 and 8";
    if (paramInt3 == paramInt4) {
      return (byte[])paramArrayOfByte.clone();
    }
    int i = paramInt2 * paramInt3;
    byte[] arrayOfByte = new byte[(i + paramInt4 - 1) / paramInt4];
    int j = 0;
    int k = (i + paramInt4 - 1) / paramInt4 * paramInt4 - i;
    while (j < i)
    {
      int m = paramInt3 - j % paramInt3;
      if (m > paramInt4 - k % paramInt4) {
        m = paramInt4 - k % paramInt4;
      }
      int tmp153_152 = (k / paramInt4);
      byte[] tmp153_146 = arrayOfByte;
      tmp153_146[tmp153_152] = ((byte)(tmp153_146[tmp153_152] | (paramArrayOfByte[(paramInt1 + j / paramInt3)] + 256 >> paramInt3 - j % paramInt3 - m & (1 << m) - 1) << paramInt4 - k % paramInt4 - m));
      j += m;
      k += m;
    }
    return arrayOfByte;
  }
  
  private static int pack7Oid(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3)
  {
    byte[] arrayOfByte = pack(paramArrayOfByte1, paramInt1, paramInt2, 8, 7);
    int i = arrayOfByte.length - 1;
    for (int j = arrayOfByte.length - 2; j >= 0; j--)
    {
      if (arrayOfByte[j] != 0) {
        i = j;
      }
      int tmp47_45 = j;
      byte[] tmp47_43 = arrayOfByte;
      tmp47_43[tmp47_45] = ((byte)(tmp47_43[tmp47_45] | 0x80));
    }
    System.arraycopy(arrayOfByte, i, paramArrayOfByte2, paramInt3, arrayOfByte.length - i);
    return arrayOfByte.length - i;
  }
  
  private static int pack8(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3)
  {
    byte[] arrayOfByte = pack(paramArrayOfByte1, paramInt1, paramInt2, 7, 8);
    int i = arrayOfByte.length - 1;
    for (int j = arrayOfByte.length - 2; j >= 0; j--) {
      if (arrayOfByte[j] != 0) {
        i = j;
      }
    }
    System.arraycopy(arrayOfByte, i, paramArrayOfByte2, paramInt3, arrayOfByte.length - i);
    return arrayOfByte.length - i;
  }
  
  private static int pack7Oid(int paramInt1, byte[] paramArrayOfByte, int paramInt2)
  {
    byte[] arrayOfByte = new byte[4];
    arrayOfByte[0] = ((byte)(paramInt1 >> 24));
    arrayOfByte[1] = ((byte)(paramInt1 >> 16));
    arrayOfByte[2] = ((byte)(paramInt1 >> 8));
    arrayOfByte[3] = ((byte)paramInt1);
    return pack7Oid(arrayOfByte, 0, 4, paramArrayOfByte, paramInt2);
  }
  
  private static int pack7Oid(BigInteger paramBigInteger, byte[] paramArrayOfByte, int paramInt)
  {
    byte[] arrayOfByte = paramBigInteger.toByteArray();
    return pack7Oid(arrayOfByte, 0, arrayOfByte.length, paramArrayOfByte, paramInt);
  }
  
  private static void check(byte[] paramArrayOfByte)
    throws IOException
  {
    int i = paramArrayOfByte.length;
    if ((i < 1) || ((paramArrayOfByte[(i - 1)] & 0x80) != 0)) {
      throw new IOException("ObjectIdentifier() -- Invalid DER encoding, not ended");
    }
    for (int j = 0; j < i; j++) {
      if ((paramArrayOfByte[j] == Byte.MIN_VALUE) && ((j == 0) || ((paramArrayOfByte[(j - 1)] & 0x80) == 0))) {
        throw new IOException("ObjectIdentifier() -- Invalid DER encoding, useless extra octet detected");
      }
    }
  }
  
  private static void checkCount(int paramInt)
    throws IOException
  {
    if (paramInt < 2) {
      throw new IOException("ObjectIdentifier() -- Must be at least two oid components ");
    }
  }
  
  private static void checkFirstComponent(int paramInt)
    throws IOException
  {
    if ((paramInt < 0) || (paramInt > 2)) {
      throw new IOException("ObjectIdentifier() -- First oid component is invalid ");
    }
  }
  
  private static void checkFirstComponent(BigInteger paramBigInteger)
    throws IOException
  {
    if ((paramBigInteger.signum() == -1) || (paramBigInteger.compareTo(BigInteger.valueOf(2L)) == 1)) {
      throw new IOException("ObjectIdentifier() -- First oid component is invalid ");
    }
  }
  
  private static void checkSecondComponent(int paramInt1, int paramInt2)
    throws IOException
  {
    if ((paramInt2 < 0) || ((paramInt1 != 2) && (paramInt2 > 39))) {
      throw new IOException("ObjectIdentifier() -- Second oid component is invalid ");
    }
  }
  
  private static void checkSecondComponent(int paramInt, BigInteger paramBigInteger)
    throws IOException
  {
    if ((paramBigInteger.signum() == -1) || ((paramInt != 2) && (paramBigInteger.compareTo(BigInteger.valueOf(39L)) == 1))) {
      throw new IOException("ObjectIdentifier() -- Second oid component is invalid ");
    }
  }
  
  private static void checkOtherComponent(int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramInt2 < 0) {
      throw new IOException("ObjectIdentifier() -- oid component #" + (paramInt1 + 1) + " must be non-negative ");
    }
  }
  
  private static void checkOtherComponent(int paramInt, BigInteger paramBigInteger)
    throws IOException
  {
    if (paramBigInteger.signum() == -1) {
      throw new IOException("ObjectIdentifier() -- oid component #" + (paramInt + 1) + " must be non-negative ");
    }
  }
  
  static class HugeOidNotSupportedByOldJDK
    implements Serializable
  {
    private static final long serialVersionUID = 1L;
    static HugeOidNotSupportedByOldJDK theOne = new HugeOidNotSupportedByOldJDK();
    
    HugeOidNotSupportedByOldJDK() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\util\ObjectIdentifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */