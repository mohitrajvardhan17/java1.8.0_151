package java.net;

import java.io.IOException;
import java.security.cert.Certificate;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import sun.net.www.ParseUtil;

public abstract class JarURLConnection
  extends URLConnection
{
  private URL jarFileURL;
  private String entryName;
  protected URLConnection jarFileURLConnection;
  
  protected JarURLConnection(URL paramURL)
    throws MalformedURLException
  {
    super(paramURL);
    parseSpecs(paramURL);
  }
  
  private void parseSpecs(URL paramURL)
    throws MalformedURLException
  {
    String str = paramURL.getFile();
    int i = str.indexOf("!/");
    if (i == -1) {
      throw new MalformedURLException("no !/ found in url spec:" + str);
    }
    jarFileURL = new URL(str.substring(0, i++));
    entryName = null;
    i++;
    if (i != str.length())
    {
      entryName = str.substring(i, str.length());
      entryName = ParseUtil.decode(entryName);
    }
  }
  
  public URL getJarFileURL()
  {
    return jarFileURL;
  }
  
  public String getEntryName()
  {
    return entryName;
  }
  
  public abstract JarFile getJarFile()
    throws IOException;
  
  public Manifest getManifest()
    throws IOException
  {
    return getJarFile().getManifest();
  }
  
  public JarEntry getJarEntry()
    throws IOException
  {
    return getJarFile().getJarEntry(entryName);
  }
  
  public Attributes getAttributes()
    throws IOException
  {
    JarEntry localJarEntry = getJarEntry();
    return localJarEntry != null ? localJarEntry.getAttributes() : null;
  }
  
  public Attributes getMainAttributes()
    throws IOException
  {
    Manifest localManifest = getManifest();
    return localManifest != null ? localManifest.getMainAttributes() : null;
  }
  
  public Certificate[] getCertificates()
    throws IOException
  {
    JarEntry localJarEntry = getJarEntry();
    return localJarEntry != null ? localJarEntry.getCertificates() : null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\JarURLConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */