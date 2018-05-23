package com.sun.xml.internal.messaging.saaj.packaging.mime.util;

public class ASCIIUtility
{
  private ASCIIUtility() {}
  
  public static int parseInt(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
    throws NumberFormatException
  {
    if (paramArrayOfByte == null) {
      throw new NumberFormatException("null");
    }
    int i = 0;
    int j = 0;
    int k = paramInt1;
    if (paramInt2 > paramInt1)
    {
      int m;
      if (paramArrayOfByte[k] == 45)
      {
        j = 1;
        m = Integer.MIN_VALUE;
        k++;
      }
      else
      {
        m = -2147483647;
      }
      int n = m / paramInt3;
      int i1;
      if (k < paramInt2)
      {
        i1 = Character.digit((char)paramArrayOfByte[(k++)], paramInt3);
        if (i1 < 0) {
          throw new NumberFormatException("illegal number: " + toString(paramArrayOfByte, paramInt1, paramInt2));
        }
        i = -i1;
      }
      while (k < paramInt2)
      {
        i1 = Character.digit((char)paramArrayOfByte[(k++)], paramInt3);
        if (i1 < 0) {
          throw new NumberFormatException("illegal number");
        }
        if (i < n) {
          throw new NumberFormatException("illegal number");
        }
        i *= paramInt3;
        if (i < m + i1) {
          throw new NumberFormatException("illegal number");
        }
        i -= i1;
      }
    }
    throw new NumberFormatException("illegal number");
    if (j != 0)
    {
      if (k > paramInt1 + 1) {
        return i;
      }
      throw new NumberFormatException("illegal number");
    }
    return -i;
  }
  
  public static String toString(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    int i = paramInt2 - paramInt1;
    char[] arrayOfChar = new char[i];
    int j = 0;
    int k = paramInt1;
    while (j < i) {
      arrayOfChar[(j++)] = ((char)(paramArrayOfByte[(k++)] & 0xFF));
    }
    return new String(arrayOfChar);
  }
  
  public static byte[] getBytes(String paramString)
  {
    char[] arrayOfChar = paramString.toCharArray();
    int i = arrayOfChar.length;
    byte[] arrayOfByte = new byte[i];
    int j = 0;
    while (j < i) {
      arrayOfByte[j] = ((byte)arrayOfChar[(j++)]);
    }
    return arrayOfByte;
  }
  
  /* Error */
  /**
   * @deprecated
   */
  public static byte[] getBytes(java.io.InputStream paramInputStream)
    throws java.io.IOException
  {
    // Byte code:
    //   0: new 46	com/sun/xml/internal/messaging/saaj/util/ByteOutputStream
    //   3: dup
    //   4: invokespecial 72	com/sun/xml/internal/messaging/saaj/util/ByteOutputStream:<init>	()V
    //   7: astore_1
    //   8: aload_1
    //   9: aload_0
    //   10: invokevirtual 74	com/sun/xml/internal/messaging/saaj/util/ByteOutputStream:write	(Ljava/io/InputStream;)V
    //   13: aload_0
    //   14: invokevirtual 75	java/io/InputStream:close	()V
    //   17: goto +10 -> 27
    //   20: astore_2
    //   21: aload_0
    //   22: invokevirtual 75	java/io/InputStream:close	()V
    //   25: aload_2
    //   26: athrow
    //   27: aload_1
    //   28: invokevirtual 73	com/sun/xml/internal/messaging/saaj/util/ByteOutputStream:toByteArray	()[B
    //   31: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	32	0	paramInputStream	java.io.InputStream
    //   7	21	1	localByteOutputStream	com.sun.xml.internal.messaging.saaj.util.ByteOutputStream
    //   20	6	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   8	13	20	finally
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\packaging\mime\util\ASCIIUtility.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */