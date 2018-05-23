package sun.net.www.protocol.jar;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.Permission;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarURLConnection
  extends java.net.JarURLConnection
{
  private static final boolean debug = false;
  private static final JarFileFactory factory = ;
  private URL jarFileURL = getJarFileURL();
  private Permission permission;
  private URLConnection jarFileURLConnection = jarFileURL.openConnection();
  private String entryName = getEntryName();
  private JarEntry jarEntry;
  private JarFile jarFile;
  private String contentType;
  
  public JarURLConnection(URL paramURL, Handler paramHandler)
    throws MalformedURLException, IOException
  {
    super(paramURL);
  }
  
  public JarFile getJarFile()
    throws IOException
  {
    connect();
    return jarFile;
  }
  
  public JarEntry getJarEntry()
    throws IOException
  {
    connect();
    return jarEntry;
  }
  
  public Permission getPermission()
    throws IOException
  {
    return jarFileURLConnection.getPermission();
  }
  
  public void connect()
    throws IOException
  {
    if (!connected)
    {
      jarFile = factory.get(getJarFileURL(), getUseCaches());
      if (getUseCaches()) {
        jarFileURLConnection = factory.getConnection(jarFile);
      }
      if (entryName != null)
      {
        jarEntry = ((JarEntry)jarFile.getEntry(entryName));
        if (jarEntry == null)
        {
          try
          {
            if (!getUseCaches()) {
              jarFile.close();
            }
          }
          catch (Exception localException) {}
          throw new FileNotFoundException("JAR entry " + entryName + " not found in " + jarFile.getName());
        }
      }
      connected = true;
    }
  }
  
  public InputStream getInputStream()
    throws IOException
  {
    connect();
    JarURLInputStream localJarURLInputStream = null;
    if (entryName == null) {
      throw new IOException("no entry name specified");
    }
    if (jarEntry == null) {
      throw new FileNotFoundException("JAR entry " + entryName + " not found in " + jarFile.getName());
    }
    localJarURLInputStream = new JarURLInputStream(jarFile.getInputStream(jarEntry));
    return localJarURLInputStream;
  }
  
  public int getContentLength()
  {
    long l = getContentLengthLong();
    if (l > 2147483647L) {
      return -1;
    }
    return (int)l;
  }
  
  public long getContentLengthLong()
  {
    long l = -1L;
    try
    {
      connect();
      if (jarEntry == null) {
        l = jarFileURLConnection.getContentLengthLong();
      } else {
        l = getJarEntry().getSize();
      }
    }
    catch (IOException localIOException) {}
    return l;
  }
  
  public Object getContent()
    throws IOException
  {
    Object localObject = null;
    connect();
    if (entryName == null) {
      localObject = jarFile;
    } else {
      localObject = super.getContent();
    }
    return localObject;
  }
  
  public String getContentType()
  {
    if (contentType == null)
    {
      if (entryName == null) {
        contentType = "x-java/jar";
      } else {
        try
        {
          connect();
          InputStream localInputStream = jarFile.getInputStream(jarEntry);
          contentType = guessContentTypeFromStream(new BufferedInputStream(localInputStream));
          localInputStream.close();
        }
        catch (IOException localIOException) {}
      }
      if (contentType == null) {
        contentType = guessContentTypeFromName(entryName);
      }
      if (contentType == null) {
        contentType = "content/unknown";
      }
    }
    return contentType;
  }
  
  public String getHeaderField(String paramString)
  {
    return jarFileURLConnection.getHeaderField(paramString);
  }
  
  public void setRequestProperty(String paramString1, String paramString2)
  {
    jarFileURLConnection.setRequestProperty(paramString1, paramString2);
  }
  
  public String getRequestProperty(String paramString)
  {
    return jarFileURLConnection.getRequestProperty(paramString);
  }
  
  public void addRequestProperty(String paramString1, String paramString2)
  {
    jarFileURLConnection.addRequestProperty(paramString1, paramString2);
  }
  
  public Map<String, List<String>> getRequestProperties()
  {
    return jarFileURLConnection.getRequestProperties();
  }
  
  public void setAllowUserInteraction(boolean paramBoolean)
  {
    jarFileURLConnection.setAllowUserInteraction(paramBoolean);
  }
  
  public boolean getAllowUserInteraction()
  {
    return jarFileURLConnection.getAllowUserInteraction();
  }
  
  public void setUseCaches(boolean paramBoolean)
  {
    jarFileURLConnection.setUseCaches(paramBoolean);
  }
  
  public boolean getUseCaches()
  {
    return jarFileURLConnection.getUseCaches();
  }
  
  public void setIfModifiedSince(long paramLong)
  {
    jarFileURLConnection.setIfModifiedSince(paramLong);
  }
  
  public void setDefaultUseCaches(boolean paramBoolean)
  {
    jarFileURLConnection.setDefaultUseCaches(paramBoolean);
  }
  
  public boolean getDefaultUseCaches()
  {
    return jarFileURLConnection.getDefaultUseCaches();
  }
  
  class JarURLInputStream
    extends FilterInputStream
  {
    JarURLInputStream(InputStream paramInputStream)
    {
      super();
    }
    
    /* Error */
    public void close()
      throws IOException
    {
      // Byte code:
      //   0: aload_0
      //   1: invokespecial 34	java/io/FilterInputStream:close	()V
      //   4: aload_0
      //   5: getfield 33	sun/net/www/protocol/jar/JarURLConnection$JarURLInputStream:this$0	Lsun/net/www/protocol/jar/JarURLConnection;
      //   8: invokevirtual 37	sun/net/www/protocol/jar/JarURLConnection:getUseCaches	()Z
      //   11: ifne +39 -> 50
      //   14: aload_0
      //   15: getfield 33	sun/net/www/protocol/jar/JarURLConnection$JarURLInputStream:this$0	Lsun/net/www/protocol/jar/JarURLConnection;
      //   18: invokestatic 38	sun/net/www/protocol/jar/JarURLConnection:access$000	(Lsun/net/www/protocol/jar/JarURLConnection;)Ljava/util/jar/JarFile;
      //   21: invokevirtual 36	java/util/jar/JarFile:close	()V
      //   24: goto +26 -> 50
      //   27: astore_1
      //   28: aload_0
      //   29: getfield 33	sun/net/www/protocol/jar/JarURLConnection$JarURLInputStream:this$0	Lsun/net/www/protocol/jar/JarURLConnection;
      //   32: invokevirtual 37	sun/net/www/protocol/jar/JarURLConnection:getUseCaches	()Z
      //   35: ifne +13 -> 48
      //   38: aload_0
      //   39: getfield 33	sun/net/www/protocol/jar/JarURLConnection$JarURLInputStream:this$0	Lsun/net/www/protocol/jar/JarURLConnection;
      //   42: invokestatic 38	sun/net/www/protocol/jar/JarURLConnection:access$000	(Lsun/net/www/protocol/jar/JarURLConnection;)Ljava/util/jar/JarFile;
      //   45: invokevirtual 36	java/util/jar/JarFile:close	()V
      //   48: aload_1
      //   49: athrow
      //   50: return
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	51	0	this	JarURLInputStream
      //   27	22	1	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   0	4	27	finally
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\www\protocol\jar\JarURLConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */