package java.util.jar;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import sun.security.util.ManifestEntryVerifier;

public class JarInputStream
  extends ZipInputStream
{
  private Manifest man;
  private JarEntry first;
  private JarVerifier jv;
  private ManifestEntryVerifier mev;
  private final boolean doVerify;
  private boolean tryManifest;
  
  public JarInputStream(InputStream paramInputStream)
    throws IOException
  {
    this(paramInputStream, true);
  }
  
  public JarInputStream(InputStream paramInputStream, boolean paramBoolean)
    throws IOException
  {
    super(paramInputStream);
    doVerify = paramBoolean;
    JarEntry localJarEntry = (JarEntry)super.getNextEntry();
    if ((localJarEntry != null) && (localJarEntry.getName().equalsIgnoreCase("META-INF/"))) {
      localJarEntry = (JarEntry)super.getNextEntry();
    }
    first = checkManifest(localJarEntry);
  }
  
  private JarEntry checkManifest(JarEntry paramJarEntry)
    throws IOException
  {
    if ((paramJarEntry != null) && ("META-INF/MANIFEST.MF".equalsIgnoreCase(paramJarEntry.getName())))
    {
      man = new Manifest();
      byte[] arrayOfByte = getBytes(new BufferedInputStream(this));
      man.read(new ByteArrayInputStream(arrayOfByte));
      closeEntry();
      if (doVerify)
      {
        jv = new JarVerifier(arrayOfByte);
        mev = new ManifestEntryVerifier(man);
      }
      return (JarEntry)super.getNextEntry();
    }
    return paramJarEntry;
  }
  
  private byte[] getBytes(InputStream paramInputStream)
    throws IOException
  {
    byte[] arrayOfByte = new byte[' '];
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(2048);
    int i;
    while ((i = paramInputStream.read(arrayOfByte, 0, arrayOfByte.length)) != -1) {
      localByteArrayOutputStream.write(arrayOfByte, 0, i);
    }
    return localByteArrayOutputStream.toByteArray();
  }
  
  public Manifest getManifest()
  {
    return man;
  }
  
  public ZipEntry getNextEntry()
    throws IOException
  {
    JarEntry localJarEntry;
    if (first == null)
    {
      localJarEntry = (JarEntry)super.getNextEntry();
      if (tryManifest)
      {
        localJarEntry = checkManifest(localJarEntry);
        tryManifest = false;
      }
    }
    else
    {
      localJarEntry = first;
      if (first.getName().equalsIgnoreCase("META-INF/INDEX.LIST")) {
        tryManifest = true;
      }
      first = null;
    }
    if ((jv != null) && (localJarEntry != null)) {
      if (jv.nothingToVerify() == true)
      {
        jv = null;
        mev = null;
      }
      else
      {
        jv.beginEntry(localJarEntry, mev);
      }
    }
    return localJarEntry;
  }
  
  public JarEntry getNextJarEntry()
    throws IOException
  {
    return (JarEntry)getNextEntry();
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    int i;
    if (first == null) {
      i = super.read(paramArrayOfByte, paramInt1, paramInt2);
    } else {
      i = -1;
    }
    if (jv != null) {
      jv.update(i, paramArrayOfByte, paramInt1, paramInt2, mev);
    }
    return i;
  }
  
  protected ZipEntry createZipEntry(String paramString)
  {
    JarEntry localJarEntry = new JarEntry(paramString);
    if (man != null) {
      attr = man.getAttributes(paramString);
    }
    return localJarEntry;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\jar\JarInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */