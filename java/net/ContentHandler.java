package java.net;

import java.io.IOException;

public abstract class ContentHandler
{
  public ContentHandler() {}
  
  public abstract Object getContent(URLConnection paramURLConnection)
    throws IOException;
  
  public Object getContent(URLConnection paramURLConnection, Class[] paramArrayOfClass)
    throws IOException
  {
    Object localObject = getContent(paramURLConnection);
    for (int i = 0; i < paramArrayOfClass.length; i++) {
      if (paramArrayOfClass[i].isInstance(localObject)) {
        return localObject;
      }
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\ContentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */