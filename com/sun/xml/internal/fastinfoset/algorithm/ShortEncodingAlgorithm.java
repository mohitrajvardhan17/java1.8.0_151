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

public class ShortEncodingAlgorithm
  extends IntegerEncodingAlgorithm
{
  public ShortEncodingAlgorithm() {}
  
  public final int getPrimtiveLengthFromOctetLength(int paramInt)
    throws EncodingAlgorithmException
  {
    if (paramInt % 2 != 0) {
      throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.lengthNotMultipleOfShort", new Object[] { Integer.valueOf(2) }));
    }
    return paramInt / 2;
  }
  
  public int getOctetLengthFromPrimitiveLength(int paramInt)
  {
    return paramInt * 2;
  }
  
  public final Object decodeFromBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws EncodingAlgorithmException
  {
    short[] arrayOfShort = new short[getPrimtiveLengthFromOctetLength(paramInt2)];
    decodeFromBytesToShortArray(arrayOfShort, 0, paramArrayOfByte, paramInt1, paramInt2);
    return arrayOfShort;
  }
  
  public final Object decodeFromInputStream(InputStream paramInputStream)
    throws IOException
  {
    return decodeFromInputStreamToShortArray(paramInputStream);
  }
  
  public void encodeToOutputStream(Object paramObject, OutputStream paramOutputStream)
    throws IOException
  {
    if (!(paramObject instanceof short[])) {
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotShortArray"));
    }
    short[] arrayOfShort = (short[])paramObject;
    encodeToOutputStreamFromShortArray(arrayOfShort, paramOutputStream);
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
        localArrayList.add(Short.valueOf(str));
      }
    });
    return generateArrayFromList(localArrayList);
  }
  
  public final void convertToCharacters(Object paramObject, StringBuffer paramStringBuffer)
  {
    if (!(paramObject instanceof short[])) {
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotShortArray"));
    }
    short[] arrayOfShort = (short[])paramObject;
    convertToCharactersFromShortArray(arrayOfShort, paramStringBuffer);
  }
  
  public final void decodeFromBytesToShortArray(short[] paramArrayOfShort, int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3)
  {
    int i = paramInt3 / 2;
    for (int j = 0; j < i; j++) {
      paramArrayOfShort[(paramInt1++)] = ((short)((paramArrayOfByte[(paramInt2++)] & 0xFF) << 8 | paramArrayOfByte[(paramInt2++)] & 0xFF));
    }
  }
  
  public final short[] decodeFromInputStreamToShortArray(InputStream paramInputStream)
    throws IOException
  {
    ArrayList localArrayList = new ArrayList();
    byte[] arrayOfByte = new byte[2];
    for (;;)
    {
      int i = paramInputStream.read(arrayOfByte);
      if (i != 2)
      {
        if (i == -1) {
          break;
        }
        while (i != 2)
        {
          j = paramInputStream.read(arrayOfByte, i, 2 - i);
          if (j == -1) {
            throw new EOFException();
          }
          i += j;
        }
      }
      int j = (arrayOfByte[0] & 0xFF) << 8 | arrayOfByte[1] & 0xFF;
      localArrayList.add(Short.valueOf((short)j));
    }
    return generateArrayFromList(localArrayList);
  }
  
  public final void encodeToOutputStreamFromShortArray(short[] paramArrayOfShort, OutputStream paramOutputStream)
    throws IOException
  {
    for (int i = 0; i < paramArrayOfShort.length; i++)
    {
      int j = paramArrayOfShort[i];
      paramOutputStream.write(j >>> 8 & 0xFF);
      paramOutputStream.write(j & 0xFF);
    }
  }
  
  public final void encodeToBytes(Object paramObject, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
  {
    encodeToBytesFromShortArray((short[])paramObject, paramInt1, paramInt2, paramArrayOfByte, paramInt3);
  }
  
  public final void encodeToBytesFromShortArray(short[] paramArrayOfShort, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
  {
    int i = paramInt1 + paramInt2;
    for (int j = paramInt1; j < i; j++)
    {
      int k = paramArrayOfShort[j];
      paramArrayOfByte[(paramInt3++)] = ((byte)(k >>> 8 & 0xFF));
      paramArrayOfByte[(paramInt3++)] = ((byte)(k & 0xFF));
    }
  }
  
  public final void convertToCharactersFromShortArray(short[] paramArrayOfShort, StringBuffer paramStringBuffer)
  {
    int i = paramArrayOfShort.length - 1;
    for (int j = 0; j <= i; j++)
    {
      paramStringBuffer.append(Short.toString(paramArrayOfShort[j]));
      if (j != i) {
        paramStringBuffer.append(' ');
      }
    }
  }
  
  public final short[] generateArrayFromList(List paramList)
  {
    short[] arrayOfShort = new short[paramList.size()];
    for (int i = 0; i < arrayOfShort.length; i++) {
      arrayOfShort[i] = ((Short)paramList.get(i)).shortValue();
    }
    return arrayOfShort;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\algorithm\ShortEncodingAlgorithm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */