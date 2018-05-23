package com.sun.org.apache.xml.internal.security.utils;

import com.sun.org.apache.xml.internal.security.algorithms.MessageDigestAlgorithm;
import java.io.ByteArrayOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DigesterOutputStream
  extends ByteArrayOutputStream
{
  private static final Logger log = Logger.getLogger(DigesterOutputStream.class.getName());
  final MessageDigestAlgorithm mda;
  
  public DigesterOutputStream(MessageDigestAlgorithm paramMessageDigestAlgorithm)
  {
    mda = paramMessageDigestAlgorithm;
  }
  
  public void write(byte[] paramArrayOfByte)
  {
    write(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public void write(int paramInt)
  {
    mda.update((byte)paramInt);
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (log.isLoggable(Level.FINE))
    {
      log.log(Level.FINE, "Pre-digested input:");
      StringBuilder localStringBuilder = new StringBuilder(paramInt2);
      for (int i = paramInt1; i < paramInt1 + paramInt2; i++) {
        localStringBuilder.append((char)paramArrayOfByte[i]);
      }
      log.log(Level.FINE, localStringBuilder.toString());
    }
    mda.update(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public byte[] getDigestValue()
  {
    return mda.digest();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\utils\DigesterOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */