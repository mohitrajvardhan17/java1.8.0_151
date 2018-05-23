package com.sun.xml.internal.fastinfoset.algorithm;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithmException;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

public class IntEncodingAlgorithm
  extends IntegerEncodingAlgorithm
{
  public IntEncodingAlgorithm() {}
  
  public final int getPrimtiveLengthFromOctetLength(int paramInt)
    throws EncodingAlgorithmException
  {
    if (paramInt % 4 != 0) {
      throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.lengthNotMultipleOfInt", new Object[] { Integer.valueOf(4) }));
    }
    return paramInt / 4;
  }
  
  public int getOctetLengthFromPrimitiveLength(int paramInt)
  {
    return paramInt * 4;
  }
  
  public final Object decodeFromBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws EncodingAlgorithmException
  {
    int[] arrayOfInt = new int[getPrimtiveLengthFromOctetLength(paramInt2)];
    decodeFromBytesToIntArray(arrayOfInt, 0, paramArrayOfByte, paramInt1, paramInt2);
    return arrayOfInt;
  }
  
  public final Object decodeFromInputStream(InputStream paramInputStream)
    throws IOException
  {
    return decodeFromInputStreamToIntArray(paramInputStream);
  }
  
  public void encodeToOutputStream(Object paramObject, OutputStream paramOutputStream)
    throws IOException
  {
    if (!(paramObject instanceof int[])) {
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotIntArray"));
    }
    int[] arrayOfInt = (int[])paramObject;
    encodeToOutputStreamFromIntArray(arrayOfInt, paramOutputStream);
  }
  
  public final Object convertFromCharacters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    final CharBuffer localCharBuffer = CharBuffer.wrap(paramArrayOfChar, paramInt1, paramInt2);
    final ArrayList localArrayList = new ArrayList();
    matchWhiteSpaceDelimnatedWords(localCharBuffer, new BuiltInEncodingAlgorithm.WordListener()
    {
      public void word(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        String str = localCharBuffer.subSequence(paramAnonymousInt1, paramAnonymousInt2).toString();
        localArrayList.add(Integer.valueOf(str));
      }
    });
    return generateArrayFromList(localArrayList);
  }
  
  public final void convertToCharacters(Object paramObject, StringBuffer paramStringBuffer)
  {
    if (!(paramObject instanceof int[])) {
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotIntArray"));
    }
    int[] arrayOfInt = (int[])paramObject;
    convertToCharactersFromIntArray(arrayOfInt, paramStringBuffer);
  }
  
  public final void decodeFromBytesToIntArray(int[] paramArrayOfInt, int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3)
  {
    int i = paramInt3 / 4;
    for (int j = 0; j < i; j++) {
      paramArrayOfInt[(paramInt1++)] = ((paramArrayOfByte[(paramInt2++)] & 0xFF) << 24 | (paramArrayOfByte[(paramInt2++)] & 0xFF) << 16 | (paramArrayOfByte[(paramInt2++)] & 0xFF) << 8 | paramArrayOfByte[(paramInt2++)] & 0xFF);
    }
  }
  
  public final int[] decodeFromInputStreamToIntArray(InputStream paramInputStream)
    throws IOException
  {
    ArrayList localArrayList = new ArrayList();
    byte[] arrayOfByte = new byte[4];
    for (;;)
    {
      int i = paramInputStream.read(arrayOfByte);
      if (i != 4)
      {
        if (i == -1) {
          break;
        }
        while (i != 4)
        {
          j = paramInputStream.read(arrayOfByte, i, 4 - i);
          if (j == -1) {
            throw new EOFException();
          }
          i += j;
        }
      }
      int j = (arrayOfByte[0] & 0xFF) << 24 | (arrayOfByte[1] & 0xFF) << 16 | (arrayOfByte[2] & 0xFF) << 8 | arrayOfByte[3] & 0xFF;
      localArrayList.add(Integer.valueOf(j));
    }
    return generateArrayFromList(localArrayList);
  }
  
  public final void encodeToOutputStreamFromIntArray(int[] paramArrayOfInt, OutputStream paramOutputStream)
    throws IOException
  {
    for (int i = 0; i < paramArrayOfInt.length; i++)
    {
      int j = paramArrayOfInt[i];
      paramOutputStream.write(j >>> 24 & 0xFF);
      paramOutputStream.write(j >>> 16 & 0xFF);
      paramOutputStream.write(j >>> 8 & 0xFF);
      paramOutputStream.write(j & 0xFF);
    }
  }
  
  public final void encodeToBytes(Object paramObject, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
  {
    encodeToBytesFromIntArray((int[])paramObject, paramInt1, paramInt2, paramArrayOfByte, paramInt3);
  }
  
  public final void encodeToBytesFromIntArray(int[] paramArrayOfInt, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
  {
    int i = paramInt1 + paramInt2;
    for (int j = paramInt1; j < i; j++)
    {
      int k = paramArrayOfInt[j];
      paramArrayOfByte[(paramInt3++)] = ((byte)(k >>> 24 & 0xFF));
      paramArrayOfByte[(paramInt3++)] = ((byte)(k >>> 16 & 0xFF));
      paramArrayOfByte[(paramInt3++)] = ((byte)(k >>> 8 & 0xFF));
      paramArrayOfByte[(paramInt3++)] = ((byte)(k & 0xFF));
    }
  }
  
  public final void convertToCharactersFromIntArray(int[] paramArrayOfInt, StringBuffer paramStringBuffer)
  {
    int i = paramArrayOfInt.length - 1;
    for (int j = 0; j <= i; j++)
    {
      paramStringBuffer.append(Integer.toString(paramArrayOfInt[j]));
      if (j != i) {
        paramStringBuffer.append(' ');
      }
    }
  }
  
  public final int[] generateArrayFromList(List paramList)
  {
    int[] arrayOfInt = new int[paramList.size()];
    for (int i = 0; i < arrayOfInt.length; i++) {
      arrayOfInt[i] = ((Integer)paramList.get(i)).intValue();
    }
    return arrayOfInt;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\algorithm\IntEncodingAlgorithm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */