package java.net;

import java.io.IOException;

class UnknownContentHandler
  extends ContentHandler
{
  static final ContentHandler INSTANCE = new UnknownContentHandler();
  
  UnknownContentHandler() {}
  
  public Object getContent(URLConnection paramURLConnection)
    throws IOException
  {
    return paramURLConnection.getInputStream();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\UnknownContentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */