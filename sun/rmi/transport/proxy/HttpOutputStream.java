package sun.rmi.transport.proxy;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

class HttpOutputStream
  extends ByteArrayOutputStream
{
  protected OutputStream out;
  boolean responseSent = false;
  private static byte[] emptyData = { 0 };
  
  public HttpOutputStream(OutputStream paramOutputStream)
  {
    out = paramOutputStream;
  }
  
  public synchronized void close()
    throws IOException
  {
    if (!responseSent)
    {
      if (size() == 0) {
        write(emptyData);
      }
      DataOutputStream localDataOutputStream = new DataOutputStream(out);
      localDataOutputStream.writeBytes("Content-type: application/octet-stream\r\n");
      localDataOutputStream.writeBytes("Content-length: " + size() + "\r\n");
      localDataOutputStream.writeBytes("\r\n");
      writeTo(localDataOutputStream);
      localDataOutputStream.flush();
      reset();
      responseSent = true;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\rmi\transport\proxy\HttpOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */