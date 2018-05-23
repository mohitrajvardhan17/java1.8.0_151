package sun.net.httpserver;

import java.io.IOException;
import java.io.OutputStream;

class PlaceholderOutputStream
  extends OutputStream
{
  OutputStream wrapped;
  
  PlaceholderOutputStream(OutputStream paramOutputStream)
  {
    wrapped = paramOutputStream;
  }
  
  void setWrappedStream(OutputStream paramOutputStream)
  {
    wrapped = paramOutputStream;
  }
  
  boolean isWrapped()
  {
    return wrapped != null;
  }
  
  private void checkWrap()
    throws IOException
  {
    if (wrapped == null) {
      throw new IOException("response headers not sent yet");
    }
  }
  
  public void write(int paramInt)
    throws IOException
  {
    checkWrap();
    wrapped.write(paramInt);
  }
  
  public void write(byte[] paramArrayOfByte)
    throws IOException
  {
    checkWrap();
    wrapped.write(paramArrayOfByte);
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    checkWrap();
    wrapped.write(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public void flush()
    throws IOException
  {
    checkWrap();
    wrapped.flush();
  }
  
  public void close()
    throws IOException
  {
    checkWrap();
    wrapped.close();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\httpserver\PlaceholderOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */