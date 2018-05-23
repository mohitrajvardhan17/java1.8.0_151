package java.security;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DigestInputStream
  extends FilterInputStream
{
  private boolean on = true;
  protected MessageDigest digest;
  
  public DigestInputStream(InputStream paramInputStream, MessageDigest paramMessageDigest)
  {
    super(paramInputStream);
    setMessageDigest(paramMessageDigest);
  }
  
  public MessageDigest getMessageDigest()
  {
    return digest;
  }
  
  public void setMessageDigest(MessageDigest paramMessageDigest)
  {
    digest = paramMessageDigest;
  }
  
  public int read()
    throws IOException
  {
    int i = in.read();
    if ((on) && (i != -1)) {
      digest.update((byte)i);
    }
    return i;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    int i = in.read(paramArrayOfByte, paramInt1, paramInt2);
    if ((on) && (i != -1)) {
      digest.update(paramArrayOfByte, paramInt1, i);
    }
    return i;
  }
  
  public void on(boolean paramBoolean)
  {
    on = paramBoolean;
  }
  
  public String toString()
  {
    return "[Digest Input Stream] " + digest.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\DigestInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */