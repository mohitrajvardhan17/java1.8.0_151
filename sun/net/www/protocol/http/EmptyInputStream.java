package sun.net.www.protocol.http;

import java.io.InputStream;

class EmptyInputStream
  extends InputStream
{
  EmptyInputStream() {}
  
  public int available()
  {
    return 0;
  }
  
  public int read()
  {
    return -1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\www\protocol\http\EmptyInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */