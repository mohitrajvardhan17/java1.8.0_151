package com.sun.xml.internal.org.jvnet.fastinfoset;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract interface EncodingAlgorithm
{
  public abstract Object decodeFromBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws EncodingAlgorithmException;
  
  public abstract Object decodeFromInputStream(InputStream paramInputStream)
    throws EncodingAlgorithmException, IOException;
  
  public abstract void encodeToOutputStream(Object paramObject, OutputStream paramOutputStream)
    throws EncodingAlgorithmException, IOException;
  
  public abstract Object convertFromCharacters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws EncodingAlgorithmException;
  
  public abstract void convertToCharacters(Object paramObject, StringBuffer paramStringBuffer)
    throws EncodingAlgorithmException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\org\jvnet\fastinfoset\EncodingAlgorithm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */