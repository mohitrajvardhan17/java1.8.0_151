package sun.rmi.transport.proxy;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

class HttpSendOutputStream
  extends FilterOutputStream
{
  HttpSendSocket owner;
  
  public HttpSendOutputStream(OutputStream paramOutputStream, HttpSendSocket paramHttpSendSocket)
    throws IOException
  {
    super(paramOutputStream);
    owner = paramHttpSendSocket;
  }
  
  public void deactivate()
  {
    out = null;
  }
  
  public void write(int paramInt)
    throws IOException
  {
    if (out == null) {
      out = owner.writeNotify();
    }
    out.write(paramInt);
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramInt2 == 0) {
      return;
    }
    if (out == null) {
      out = owner.writeNotify();
    }
    out.write(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public void flush()
    throws IOException
  {
    if (out != null) {
      out.flush();
    }
  }
  
  public void close()
    throws IOException
  {
    flush();
    owner.close();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\rmi\transport\proxy\HttpSendOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */