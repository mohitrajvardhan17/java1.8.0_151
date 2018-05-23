package java.security;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DigestOutputStream
  extends FilterOutputStream
{
  private boolean on = true;
  protected MessageDigest digest;
  
  public DigestOutputStream(OutputStream paramOutputStream, MessageDigest paramMessageDigest)
  {
    super(paramOutputStream);
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
  
  public void write(int paramInt)
    throws IOException
  {
    out.write(paramInt);
    if (on) {
      digest.update((byte)paramInt);
    }
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    out.write(paramArrayOfByte, paramInt1, paramInt2);
    if (on) {
      digest.update(paramArrayOfByte, paramInt1, paramInt2);
    }
  }
  
  public void on(boolean paramBoolean)
  {
    on = paramBoolean;
  }
  
  public String toString()
  {
    return "[Digest Output Stream] " + digest.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\DigestOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */