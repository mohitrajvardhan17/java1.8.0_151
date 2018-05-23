package sun.net.www.http;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class HttpCaptureInputStream
  extends FilterInputStream
{
  private HttpCapture capture = null;
  
  public HttpCaptureInputStream(InputStream paramInputStream, HttpCapture paramHttpCapture)
  {
    super(paramInputStream);
    capture = paramHttpCapture;
  }
  
  public int read()
    throws IOException
  {
    int i = super.read();
    capture.received(i);
    return i;
  }
  
  public void close()
    throws IOException
  {
    try
    {
      capture.flush();
    }
    catch (IOException localIOException) {}
    super.close();
  }
  
  public int read(byte[] paramArrayOfByte)
    throws IOException
  {
    int i = super.read(paramArrayOfByte);
    for (int j = 0; j < i; j++) {
      capture.received(paramArrayOfByte[j]);
    }
    return i;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    int i = super.read(paramArrayOfByte, paramInt1, paramInt2);
    for (int j = 0; j < i; j++) {
      capture.received(paramArrayOfByte[(paramInt1 + j)]);
    }
    return i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\www\http\HttpCaptureInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */