package org.jcp.xml.dsig.internal;

import com.sun.org.apache.xml.internal.security.utils.UnsyncByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DigesterOutputStream
  extends OutputStream
{
  private static Logger log = Logger.getLogger("org.jcp.xml.dsig.internal");
  private final boolean buffer;
  private UnsyncByteArrayOutputStream bos;
  private final MessageDigest md;
  
  public DigesterOutputStream(MessageDigest paramMessageDigest)
  {
    this(paramMessageDigest, false);
  }
  
  public DigesterOutputStream(MessageDigest paramMessageDigest, boolean paramBoolean)
  {
    md = paramMessageDigest;
    buffer = paramBoolean;
    if (paramBoolean) {
      bos = new UnsyncByteArrayOutputStream();
    }
  }
  
  public void write(int paramInt)
  {
    if (buffer) {
      bos.write(paramInt);
    }
    md.update((byte)paramInt);
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (buffer) {
      bos.write(paramArrayOfByte, paramInt1, paramInt2);
    }
    if (log.isLoggable(Level.FINE))
    {
      log.log(Level.FINE, "Pre-digested input:");
      StringBuilder localStringBuilder = new StringBuilder(paramInt2);
      for (int i = paramInt1; i < paramInt1 + paramInt2; i++) {
        localStringBuilder.append((char)paramArrayOfByte[i]);
      }
      log.log(Level.FINE, localStringBuilder.toString());
    }
    md.update(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public byte[] getDigestValue()
  {
    return md.digest();
  }
  
  public InputStream getInputStream()
  {
    if (buffer) {
      return new ByteArrayInputStream(bos.toByteArray());
    }
    return null;
  }
  
  public void close()
    throws IOException
  {
    if (buffer) {
      bos.close();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\jcp\xml\dsig\internal\DigesterOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */