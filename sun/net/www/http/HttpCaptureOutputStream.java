package sun.net.www.http;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class HttpCaptureOutputStream
  extends FilterOutputStream
{
  private HttpCapture capture = null;
  
  public HttpCaptureOutputStream(OutputStream paramOutputStream, HttpCapture paramHttpCapture)
  {
    super(paramOutputStream);
    capture = paramHttpCapture;
  }
  
  public void write(int paramInt)
    throws IOException
  {
    capture.sent(paramInt);
    out.write(paramInt);
  }
  
  public void write(byte[] paramArrayOfByte)
    throws IOException
  {
    for (int k : paramArrayOfByte) {
      capture.sent(k);
    }
    out.write(paramArrayOfByte);
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    for (int i = paramInt1; i < paramInt2; i++) {
      capture.sent(paramArrayOfByte[i]);
    }
    out.write(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public void flush()
    throws IOException
  {
    try
    {
      capture.flush();
    }
    catch (IOException localIOException) {}
    super.flush();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\www\http\HttpCaptureOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */