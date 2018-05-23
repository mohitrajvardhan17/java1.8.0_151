package sun.net.www.protocol.netdoc;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.security.AccessController;
import sun.security.action.GetBooleanAction;
import sun.security.action.GetPropertyAction;

public class Handler
  extends URLStreamHandler
{
  static URL base;
  
  public Handler() {}
  
  public synchronized URLConnection openConnection(URL paramURL)
    throws IOException
  {
    URLConnection localURLConnection = null;
    Boolean localBoolean = (Boolean)AccessController.doPrivileged(new GetBooleanAction("newdoc.localonly"));
    boolean bool = localBoolean.booleanValue();
    String str1 = (String)AccessController.doPrivileged(new GetPropertyAction("doc.url"));
    String str2 = paramURL.getFile();
    URL localURL;
    if (!bool)
    {
      try
      {
        if (base == null) {
          base = new URL(str1);
        }
        localURL = new URL(base, str2);
      }
      catch (MalformedURLException localMalformedURLException1)
      {
        localURL = null;
      }
      if (localURL != null) {
        localURLConnection = localURL.openConnection();
      }
    }
    if (localURLConnection == null) {
      try
      {
        localURL = new URL("file", "~", str2);
        localURLConnection = localURL.openConnection();
        InputStream localInputStream = localURLConnection.getInputStream();
      }
      catch (MalformedURLException localMalformedURLException2)
      {
        localURLConnection = null;
      }
      catch (IOException localIOException)
      {
        localURLConnection = null;
      }
    }
    if (localURLConnection == null) {
      throw new IOException("Can't find file for URL: " + paramURL.toExternalForm());
    }
    return localURLConnection;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\www\protocol\netdoc\Handler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */