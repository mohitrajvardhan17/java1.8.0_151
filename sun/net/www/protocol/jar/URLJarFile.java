package sun.net.www.protocol.jar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.security.AccessController;
import java.security.CodeSigner;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.cert.Certificate;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import sun.net.www.ParseUtil;

public class URLJarFile
  extends JarFile
{
  private static URLJarFileCallBack callback = null;
  private URLJarFileCloseController closeController = null;
  private static int BUF_SIZE = 2048;
  private Manifest superMan;
  private Attributes superAttr;
  private Map<String, Attributes> superEntries;
  
  static JarFile getJarFile(URL paramURL)
    throws IOException
  {
    return getJarFile(paramURL, null);
  }
  
  static JarFile getJarFile(URL paramURL, URLJarFileCloseController paramURLJarFileCloseController)
    throws IOException
  {
    if (isFileURL(paramURL)) {
      return new URLJarFile(paramURL, paramURLJarFileCloseController);
    }
    return retrieve(paramURL, paramURLJarFileCloseController);
  }
  
  public URLJarFile(File paramFile)
    throws IOException
  {
    this(paramFile, null);
  }
  
  public URLJarFile(File paramFile, URLJarFileCloseController paramURLJarFileCloseController)
    throws IOException
  {
    super(paramFile, true, 5);
    closeController = paramURLJarFileCloseController;
  }
  
  private URLJarFile(URL paramURL, URLJarFileCloseController paramURLJarFileCloseController)
    throws IOException
  {
    super(ParseUtil.decode(paramURL.getFile()));
    closeController = paramURLJarFileCloseController;
  }
  
  private static boolean isFileURL(URL paramURL)
  {
    if (paramURL.getProtocol().equalsIgnoreCase("file"))
    {
      String str = paramURL.getHost();
      if ((str == null) || (str.equals("")) || (str.equals("~")) || (str.equalsIgnoreCase("localhost"))) {
        return true;
      }
    }
    return false;
  }
  
  protected void finalize()
    throws IOException
  {
    close();
  }
  
  public ZipEntry getEntry(String paramString)
  {
    ZipEntry localZipEntry = super.getEntry(paramString);
    if (localZipEntry != null)
    {
      if ((localZipEntry instanceof JarEntry)) {
        return new URLJarFileEntry((JarEntry)localZipEntry);
      }
      throw new InternalError(super.getClass() + " returned unexpected entry type " + localZipEntry.getClass());
    }
    return null;
  }
  
  public Manifest getManifest()
    throws IOException
  {
    if (!isSuperMan()) {
      return null;
    }
    Manifest localManifest = new Manifest();
    Attributes localAttributes1 = localManifest.getMainAttributes();
    localAttributes1.putAll((Map)superAttr.clone());
    if (superEntries != null)
    {
      Map localMap = localManifest.getEntries();
      Iterator localIterator = superEntries.keySet().iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        Attributes localAttributes2 = (Attributes)superEntries.get(str);
        localMap.put(str, (Attributes)localAttributes2.clone());
      }
    }
    return localManifest;
  }
  
  public void close()
    throws IOException
  {
    if (closeController != null) {
      closeController.close(this);
    }
    super.close();
  }
  
  private synchronized boolean isSuperMan()
    throws IOException
  {
    if (superMan == null) {
      superMan = super.getManifest();
    }
    if (superMan != null)
    {
      superAttr = superMan.getMainAttributes();
      superEntries = superMan.getEntries();
      return true;
    }
    return false;
  }
  
  private static JarFile retrieve(URL paramURL)
    throws IOException
  {
    return retrieve(paramURL, null);
  }
  
  private static JarFile retrieve(URL paramURL, final URLJarFileCloseController paramURLJarFileCloseController)
    throws IOException
  {
    if (callback != null) {
      return callback.retrieve(paramURL);
    }
    JarFile localJarFile = null;
    try
    {
      InputStream localInputStream = paramURL.openConnection().getInputStream();
      Object localObject1 = null;
      try
      {
        localJarFile = (JarFile)AccessController.doPrivileged(new PrivilegedExceptionAction()
        {
          public JarFile run()
            throws IOException
          {
            Path localPath = Files.createTempFile("jar_cache", null, new FileAttribute[0]);
            try
            {
              Files.copy(val$in, localPath, new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });
              URLJarFile localURLJarFile = new URLJarFile(localPath.toFile(), paramURLJarFileCloseController);
              localPath.toFile().deleteOnExit();
              return localURLJarFile;
            }
            catch (Throwable localThrowable)
            {
              try
              {
                Files.delete(localPath);
              }
              catch (IOException localIOException)
              {
                localThrowable.addSuppressed(localIOException);
              }
              throw localThrowable;
            }
          }
        });
      }
      catch (Throwable localThrowable2)
      {
        localObject1 = localThrowable2;
        throw localThrowable2;
      }
      finally
      {
        if (localInputStream != null) {
          if (localObject1 != null) {
            try
            {
              localInputStream.close();
            }
            catch (Throwable localThrowable3)
            {
              ((Throwable)localObject1).addSuppressed(localThrowable3);
            }
          } else {
            localInputStream.close();
          }
        }
      }
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw ((IOException)localPrivilegedActionException.getException());
    }
    return localJarFile;
  }
  
  public static void setCallBack(URLJarFileCallBack paramURLJarFileCallBack)
  {
    callback = paramURLJarFileCallBack;
  }
  
  public static abstract interface URLJarFileCloseController
  {
    public abstract void close(JarFile paramJarFile);
  }
  
  private class URLJarFileEntry
    extends JarEntry
  {
    private JarEntry je;
    
    URLJarFileEntry(JarEntry paramJarEntry)
    {
      super();
      je = paramJarEntry;
    }
    
    public Attributes getAttributes()
      throws IOException
    {
      if (URLJarFile.this.isSuperMan())
      {
        Map localMap = superEntries;
        if (localMap != null)
        {
          Attributes localAttributes = (Attributes)localMap.get(getName());
          if (localAttributes != null) {
            return (Attributes)localAttributes.clone();
          }
        }
      }
      return null;
    }
    
    public Certificate[] getCertificates()
    {
      Certificate[] arrayOfCertificate = je.getCertificates();
      return arrayOfCertificate == null ? null : (Certificate[])arrayOfCertificate.clone();
    }
    
    public CodeSigner[] getCodeSigners()
    {
      CodeSigner[] arrayOfCodeSigner = je.getCodeSigners();
      return arrayOfCodeSigner == null ? null : (CodeSigner[])arrayOfCodeSigner.clone();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\www\protocol\jar\URLJarFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */