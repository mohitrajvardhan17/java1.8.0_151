package javax.activation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class URLDataSource
  implements DataSource
{
  private URL url = null;
  private URLConnection url_conn = null;
  
  public URLDataSource(URL paramURL)
  {
    url = paramURL;
  }
  
  public String getContentType()
  {
    String str = null;
    try
    {
      if (url_conn == null) {
        url_conn = url.openConnection();
      }
    }
    catch (IOException localIOException) {}
    if (url_conn != null) {
      str = url_conn.getContentType();
    }
    if (str == null) {
      str = "application/octet-stream";
    }
    return str;
  }
  
  public String getName()
  {
    return url.getFile();
  }
  
  public InputStream getInputStream()
    throws IOException
  {
    return url.openStream();
  }
  
  public OutputStream getOutputStream()
    throws IOException
  {
    url_conn = url.openConnection();
    if (url_conn != null)
    {
      url_conn.setDoOutput(true);
      return url_conn.getOutputStream();
    }
    return null;
  }
  
  public URL getURL()
  {
    return url;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\activation\URLDataSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */