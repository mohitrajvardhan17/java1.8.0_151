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

public class FloatEncodingAlgorithm
  extends IEEE754FloatingPointEncodingAlgorithm
{
  public FloatEncodingAlgorithm() {}
  
  public final int getPrimtiveLengthFromOctetLength(int paramInt)
    throws EncodingAlgorithmException
  {
    if (paramInt % 4 != 0) {
      throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.lengthNotMultipleOfFloat", new Object[] { Integer.valueOf(4) }));
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
    float[] arrayOfFloat = new float[getPrimtiveLengthFromOctetLength(paramInt2)];
    decodeFromBytesToFloatArray(arrayOfFloat, 0, paramArrayOfByte, paramInt1, paramInt2);
    return arrayOfFloat;
  }
  
  public final Object decodeFromInputStream(InputStream paramInputStream)
    throws IOException
  {
    return decodeFromInputStreamToFloatArray(paramInputStream);
  }
  
  public void encodeToOutputStream(Object paramObject, OutputStream paramOutputStream)
    throws IOException
  {
    if (!(paramObject instanceof float[])) {
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotFloat"));
    }
    float[] arrayOfFloat = (float[])paramObject;
    encodeToOutputStreamFromFloatArray(arrayOfFloat, paramOutputStream);
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
        localArrayList.add(Float.valueOf(str));
      }
    });
    return generateArrayFromList(localArrayList);
  }
  
  public final void convertToCharacters(Object paramObject, StringBuffer paramStringBuffer)
  {
    if (!(paramObject instanceof float[])) {
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotFloat"));
    }
    float[] arrayOfFloat = (float[])paramObject;
    convertToCharactersFromFloatArray(arrayOfFloat, paramStringBuffer);
  }
  
  public final void decodeFromBytesToFloatArray(float[] paramArrayOfFloat, int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3)
  {
    int i = paramInt3 / 4;
    for (int j = 0; j < i; j++)
    {
      int k = (paramArrayOfByte[(paramInt2++)] & 0xFF) << 24 | (paramArrayOfByte[(paramInt2++)] & 0xFF) << 16 | (paramArrayOfByte[(paramInt2++)] & 0xFF) << 8 | paramArrayOfByte[(paramInt2++)] & 0xFF;
      paramArrayOfFloat[(paramInt1++)] = Float.intBitsToFloat(k);
    }
  }
  
  public final float[] decodeFromInputStreamToFloatArray(InputStream paramInputStream)
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
      localArrayList.add(Float.valueOf(Float.intBitsToFloat(j)));
    }
    return generateArrayFromList(localArrayList);
  }
  
  public final void encodeToOutputStreamFromFloatArray(float[] paramArrayOfFloat, OutputStream paramOutputStream)
    throws IOException
  {
    for (int i = 0; i < paramArrayOfFloat.length; i++)
    {
      int j = Float.floatToIntBits(paramArrayOfFloat[i]);
      paramOutputStream.write(j >>> 24 & 0xFF);
      paramOutputStream.write(j >>> 16 & 0xFF);
      paramOutputStream.write(j >>> 8 & 0xFF);
      paramOutputStream.write(j & 0xFF);
    }
  }
  
  public final void encodeToBytes(Object paramObject, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
  {
    encodeToBytesFromFloatArray((float[])paramObject, paramInt1, paramInt2, paramArrayOfByte, paramInt3);
  }
  
  public final void encodeToBytesFromFloatArray(float[] paramArrayOfFloat, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
  {
    int i = paramInt1 + paramInt2;
    for (int j = paramInt1; j < i; j++)
    {
      int k = Float.floatToIntBits(paramArrayOfFloat[j]);
      paramArrayOfByte[(paramInt3++)] = ((byte)(k >>> 24 & 0xFF));
      paramArrayOfByte[(paramInt3++)] = ((byte)(k >>> 16 & 0xFF));
      paramArrayOfByte[(paramInt3++)] = ((byte)(k >>> 8 & 0xFF));
      paramArrayOfByte[(paramInt3++)] = ((byte)(k & 0xFF));
    }
  }
  
  public final void convertToCharactersFromFloatArray(float[] paramArrayOfFloat, StringBuffer paramStringBuffer)
  {
    int i = paramArrayOfFloat.length - 1;
    for (int j = 0; j <= i; j++)
    {
      paramStringBuffer.append(Float.toString(paramArrayOfFloat[j]));
      if (j != i) {
        paramStringBuffer.append(' ');
      }
    }
  }
  
  public final float[] generateArrayFromList(List paramList)
  {
    float[] arrayOfFloat = new float[paramList.size()];
    for (int i = 0; i < arrayOfFloat.length; i++) {
      arrayOfFloat[i] = ((Float)paramList.get(i)).floatValue();
    }
    return arrayOfFloat;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\algorithm\FloatEncodingAlgorithm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */