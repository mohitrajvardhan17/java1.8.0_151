package java.util.jar;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.security.AccessController;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import sun.misc.IOUtils;
import sun.misc.SharedSecrets;
import sun.security.action.GetPropertyAction;
import sun.security.util.Debug;
import sun.security.util.ManifestEntryVerifier;
import sun.security.util.SignatureFileVerifier;

public class JarFile
  extends ZipFile
{
  private SoftReference<Manifest> manRef;
  private JarEntry manEntry;
  private JarVerifier jv;
  private boolean jvInitialized;
  private boolean verify;
  private boolean hasClassPathAttribute;
  private volatile boolean hasCheckedSpecialAttributes;
  public static final String MANIFEST_NAME = "META-INF/MANIFEST.MF";
  private static final char[] CLASSPATH_CHARS;
  private static final int[] CLASSPATH_LASTOCC;
  private static final int[] CLASSPATH_OPTOSFT;
  private static String javaHome;
  private static volatile String[] jarNames;
  
  public JarFile(String paramString)
    throws IOException
  {
    this(new File(paramString), true, 1);
  }
  
  public JarFile(String paramString, boolean paramBoolean)
    throws IOException
  {
    this(new File(paramString), paramBoolean, 1);
  }
  
  public JarFile(File paramFile)
    throws IOException
  {
    this(paramFile, true, 1);
  }
  
  public JarFile(File paramFile, boolean paramBoolean)
    throws IOException
  {
    this(paramFile, paramBoolean, 1);
  }
  
  public JarFile(File paramFile, boolean paramBoolean, int paramInt)
    throws IOException
  {
    super(paramFile, paramInt);
    verify = paramBoolean;
  }
  
  public Manifest getManifest()
    throws IOException
  {
    return getManifestFromReference();
  }
  
  private Manifest getManifestFromReference()
    throws IOException
  {
    Manifest localManifest = manRef != null ? (Manifest)manRef.get() : null;
    if (localManifest == null)
    {
      JarEntry localJarEntry = getManEntry();
      if (localJarEntry != null)
      {
        if (verify)
        {
          byte[] arrayOfByte = getBytes(localJarEntry);
          localManifest = new Manifest(new ByteArrayInputStream(arrayOfByte));
          if (!jvInitialized) {
            jv = new JarVerifier(arrayOfByte);
          }
        }
        else
        {
          localManifest = new Manifest(super.getInputStream(localJarEntry));
        }
        manRef = new SoftReference(localManifest);
      }
    }
    return localManifest;
  }
  
  private native String[] getMetaInfEntryNames();
  
  public JarEntry getJarEntry(String paramString)
  {
    return (JarEntry)getEntry(paramString);
  }
  
  public ZipEntry getEntry(String paramString)
  {
    ZipEntry localZipEntry = super.getEntry(paramString);
    if (localZipEntry != null) {
      return new JarFileEntry(localZipEntry);
    }
    return null;
  }
  
  public Enumeration<JarEntry> entries()
  {
    return new JarEntryIterator(null);
  }
  
  public Stream<JarEntry> stream()
  {
    return StreamSupport.stream(Spliterators.spliterator(new JarEntryIterator(null), size(), 1297), false);
  }
  
  private void maybeInstantiateVerifier()
    throws IOException
  {
    if (jv != null) {
      return;
    }
    if (verify)
    {
      String[] arrayOfString = getMetaInfEntryNames();
      if (arrayOfString != null) {
        for (int i = 0; i < arrayOfString.length; i++)
        {
          String str = arrayOfString[i].toUpperCase(Locale.ENGLISH);
          if ((str.endsWith(".DSA")) || (str.endsWith(".RSA")) || (str.endsWith(".EC")) || (str.endsWith(".SF")))
          {
            getManifest();
            return;
          }
        }
      }
      verify = false;
    }
  }
  
  private void initializeVerifier()
  {
    ManifestEntryVerifier localManifestEntryVerifier = null;
    try
    {
      String[] arrayOfString = getMetaInfEntryNames();
      if (arrayOfString != null) {
        for (int i = 0; i < arrayOfString.length; i++)
        {
          String str = arrayOfString[i].toUpperCase(Locale.ENGLISH);
          if (("META-INF/MANIFEST.MF".equals(str)) || (SignatureFileVerifier.isBlockOrSF(str)))
          {
            JarEntry localJarEntry = getJarEntry(arrayOfString[i]);
            if (localJarEntry == null) {
              throw new JarException("corrupted jar file");
            }
            if (localManifestEntryVerifier == null) {
              localManifestEntryVerifier = new ManifestEntryVerifier(getManifestFromReference());
            }
            byte[] arrayOfByte = getBytes(localJarEntry);
            if ((arrayOfByte != null) && (arrayOfByte.length > 0))
            {
              jv.beginEntry(localJarEntry, localManifestEntryVerifier);
              jv.update(arrayOfByte.length, arrayOfByte, 0, arrayOfByte.length, localManifestEntryVerifier);
              jv.update(-1, null, 0, 0, localManifestEntryVerifier);
            }
          }
        }
      }
    }
    catch (IOException localIOException)
    {
      jv = null;
      verify = false;
      if (JarVerifier.debug != null)
      {
        JarVerifier.debug.println("jarfile parsing error!");
        localIOException.printStackTrace();
      }
    }
    if (jv != null)
    {
      jv.doneWithMeta();
      if (JarVerifier.debug != null) {
        JarVerifier.debug.println("done with meta!");
      }
      if (jv.nothingToVerify())
      {
        if (JarVerifier.debug != null) {
          JarVerifier.debug.println("nothing to verify!");
        }
        jv = null;
        verify = false;
      }
    }
  }
  
  private byte[] getBytes(ZipEntry paramZipEntry)
    throws IOException
  {
    InputStream localInputStream = super.getInputStream(paramZipEntry);
    Object localObject1 = null;
    try
    {
      byte[] arrayOfByte = IOUtils.readFully(localInputStream, (int)paramZipEntry.getSize(), true);
      return arrayOfByte;
    }
    catch (Throwable localThrowable1)
    {
      localObject1 = localThrowable1;
      throw localThrowable1;
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
  
  public synchronized InputStream getInputStream(ZipEntry paramZipEntry)
    throws IOException
  {
    maybeInstantiateVerifier();
    if (jv == null) {
      return super.getInputStream(paramZipEntry);
    }
    if (!jvInitialized)
    {
      initializeVerifier();
      jvInitialized = true;
      if (jv == null) {
        return super.getInputStream(paramZipEntry);
      }
    }
    return new JarVerifier.VerifierStream(getManifestFromReference(), (paramZipEntry instanceof JarFileEntry) ? (JarEntry)paramZipEntry : getJarEntry(paramZipEntry.getName()), super.getInputStream(paramZipEntry), jv);
  }
  
  private JarEntry getManEntry()
  {
    if (manEntry == null)
    {
      manEntry = getJarEntry("META-INF/MANIFEST.MF");
      if (manEntry == null)
      {
        String[] arrayOfString = getMetaInfEntryNames();
        if (arrayOfString != null) {
          for (int i = 0; i < arrayOfString.length; i++) {
            if ("META-INF/MANIFEST.MF".equals(arrayOfString[i].toUpperCase(Locale.ENGLISH)))
            {
              manEntry = getJarEntry(arrayOfString[i]);
              break;
            }
          }
        }
      }
    }
    return manEntry;
  }
  
  boolean hasClassPathAttribute()
    throws IOException
  {
    checkForSpecialAttributes();
    return hasClassPathAttribute;
  }
  
  private boolean match(char[] paramArrayOfChar, byte[] paramArrayOfByte, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    int i = paramArrayOfChar.length;
    int j = paramArrayOfByte.length - i;
    int k = 0;
    if (k <= j)
    {
      for (int m = i - 1;; m--)
      {
        if (m < 0) {
          break label112;
        }
        int n = (char)paramArrayOfByte[(k + m)];
        n = (n - 65 | 90 - n) >= 0 ? (char)(n + 32) : n;
        if (n != paramArrayOfChar[m])
        {
          k += Math.max(m + 1 - paramArrayOfInt1[(n & 0x7F)], paramArrayOfInt2[m]);
          break;
        }
      }
      label112:
      return true;
    }
    return false;
  }
  
  private void checkForSpecialAttributes()
    throws IOException
  {
    if (hasCheckedSpecialAttributes) {
      return;
    }
    if (!isKnownNotToHaveSpecialAttributes())
    {
      JarEntry localJarEntry = getManEntry();
      if (localJarEntry != null)
      {
        byte[] arrayOfByte = getBytes(localJarEntry);
        if (match(CLASSPATH_CHARS, arrayOfByte, CLASSPATH_LASTOCC, CLASSPATH_OPTOSFT)) {
          hasClassPathAttribute = true;
        }
      }
    }
    hasCheckedSpecialAttributes = true;
  }
  
  private boolean isKnownNotToHaveSpecialAttributes()
  {
    if (javaHome == null) {
      javaHome = (String)AccessController.doPrivileged(new GetPropertyAction("java.home"));
    }
    if (jarNames == null)
    {
      localObject = new String[11];
      str = File.separator;
      int i = 0;
      localObject[(i++)] = (str + "rt.jar");
      localObject[(i++)] = (str + "jsse.jar");
      localObject[(i++)] = (str + "jce.jar");
      localObject[(i++)] = (str + "charsets.jar");
      localObject[(i++)] = (str + "dnsns.jar");
      localObject[(i++)] = (str + "zipfs.jar");
      localObject[(i++)] = (str + "localedata.jar");
      localObject[(i++)] = (str = "cldrdata.jar");
      localObject[(i++)] = (str + "sunjce_provider.jar");
      localObject[(i++)] = (str + "sunpkcs11.jar");
      localObject[(i++)] = (str + "sunec.jar");
      jarNames = (String[])localObject;
    }
    Object localObject = getName();
    String str = javaHome;
    if (((String)localObject).startsWith(str))
    {
      String[] arrayOfString = jarNames;
      for (int j = 0; j < arrayOfString.length; j++) {
        if (((String)localObject).endsWith(arrayOfString[j])) {
          return true;
        }
      }
    }
    return false;
  }
  
  private synchronized void ensureInitialization()
  {
    try
    {
      maybeInstantiateVerifier();
    }
    catch (IOException localIOException)
    {
      throw new RuntimeException(localIOException);
    }
    if ((jv != null) && (!jvInitialized))
    {
      initializeVerifier();
      jvInitialized = true;
    }
  }
  
  JarEntry newEntry(ZipEntry paramZipEntry)
  {
    return new JarFileEntry(paramZipEntry);
  }
  
  Enumeration<String> entryNames(CodeSource[] paramArrayOfCodeSource)
  {
    ensureInitialization();
    if (jv != null) {
      return jv.entryNames(this, paramArrayOfCodeSource);
    }
    int i = 0;
    for (int j = 0; j < paramArrayOfCodeSource.length; j++) {
      if (paramArrayOfCodeSource[j].getCodeSigners() == null)
      {
        i = 1;
        break;
      }
    }
    if (i != 0) {
      return unsignedEntryNames();
    }
    new Enumeration()
    {
      public boolean hasMoreElements()
      {
        return false;
      }
      
      public String nextElement()
      {
        throw new NoSuchElementException();
      }
    };
  }
  
  Enumeration<JarEntry> entries2()
  {
    ensureInitialization();
    if (jv != null) {
      return jv.entries2(this, super.entries());
    }
    final Enumeration localEnumeration = super.entries();
    new Enumeration()
    {
      ZipEntry entry;
      
      public boolean hasMoreElements()
      {
        if (entry != null) {
          return true;
        }
        while (localEnumeration.hasMoreElements())
        {
          ZipEntry localZipEntry = (ZipEntry)localEnumeration.nextElement();
          if (!JarVerifier.isSigningRelated(localZipEntry.getName()))
          {
            entry = localZipEntry;
            return true;
          }
        }
        return false;
      }
      
      public JarFile.JarFileEntry nextElement()
      {
        if (hasMoreElements())
        {
          ZipEntry localZipEntry = entry;
          entry = null;
          return new JarFile.JarFileEntry(JarFile.this, localZipEntry);
        }
        throw new NoSuchElementException();
      }
    };
  }
  
  CodeSource[] getCodeSources(URL paramURL)
  {
    ensureInitialization();
    if (jv != null) {
      return jv.getCodeSources(this, paramURL);
    }
    Enumeration localEnumeration = unsignedEntryNames();
    if (localEnumeration.hasMoreElements()) {
      return new CodeSource[] { JarVerifier.getUnsignedCS(paramURL) };
    }
    return null;
  }
  
  private Enumeration<String> unsignedEntryNames()
  {
    final Enumeration localEnumeration = entries();
    new Enumeration()
    {
      String name;
      
      public boolean hasMoreElements()
      {
        if (name != null) {
          return true;
        }
        while (localEnumeration.hasMoreElements())
        {
          ZipEntry localZipEntry = (ZipEntry)localEnumeration.nextElement();
          String str = localZipEntry.getName();
          if ((!localZipEntry.isDirectory()) && (!JarVerifier.isSigningRelated(str)))
          {
            name = str;
            return true;
          }
        }
        return false;
      }
      
      public String nextElement()
      {
        if (hasMoreElements())
        {
          String str = name;
          name = null;
          return str;
        }
        throw new NoSuchElementException();
      }
    };
  }
  
  CodeSource getCodeSource(URL paramURL, String paramString)
  {
    ensureInitialization();
    if (jv != null)
    {
      if (jv.eagerValidation)
      {
        CodeSource localCodeSource = null;
        JarEntry localJarEntry = getJarEntry(paramString);
        if (localJarEntry != null) {
          localCodeSource = jv.getCodeSource(paramURL, this, localJarEntry);
        } else {
          localCodeSource = jv.getCodeSource(paramURL, paramString);
        }
        return localCodeSource;
      }
      return jv.getCodeSource(paramURL, paramString);
    }
    return JarVerifier.getUnsignedCS(paramURL);
  }
  
  void setEagerValidation(boolean paramBoolean)
  {
    try
    {
      maybeInstantiateVerifier();
    }
    catch (IOException localIOException)
    {
      throw new RuntimeException(localIOException);
    }
    if (jv != null) {
      jv.setEagerValidation(paramBoolean);
    }
  }
  
  List<Object> getManifestDigests()
  {
    ensureInitialization();
    if (jv != null) {
      return jv.getManifestDigests();
    }
    return new ArrayList();
  }
  
  static
  {
    SharedSecrets.setJavaUtilJarAccess(new JavaUtilJarAccessImpl());
    CLASSPATH_CHARS = new char[] { 'c', 'l', 'a', 's', 's', '-', 'p', 'a', 't', 'h' };
    CLASSPATH_LASTOCC = new int[''];
    CLASSPATH_OPTOSFT = new int[10];
    CLASSPATH_LASTOCC[99] = 1;
    CLASSPATH_LASTOCC[108] = 2;
    CLASSPATH_LASTOCC[115] = 5;
    CLASSPATH_LASTOCC[45] = 6;
    CLASSPATH_LASTOCC[112] = 7;
    CLASSPATH_LASTOCC[97] = 8;
    CLASSPATH_LASTOCC[116] = 9;
    CLASSPATH_LASTOCC[104] = 10;
    for (int i = 0; i < 9; i++) {
      CLASSPATH_OPTOSFT[i] = 10;
    }
    CLASSPATH_OPTOSFT[9] = 1;
  }
  
  private class JarEntryIterator
    implements Enumeration<JarEntry>, Iterator<JarEntry>
  {
    final Enumeration<? extends ZipEntry> e = JarFile.this.entries();
    
    private JarEntryIterator() {}
    
    public boolean hasNext()
    {
      return e.hasMoreElements();
    }
    
    public JarEntry next()
    {
      ZipEntry localZipEntry = (ZipEntry)e.nextElement();
      return new JarFile.JarFileEntry(JarFile.this, localZipEntry);
    }
    
    public boolean hasMoreElements()
    {
      return hasNext();
    }
    
    public JarEntry nextElement()
    {
      return next();
    }
  }
  
  private class JarFileEntry
    extends JarEntry
  {
    JarFileEntry(ZipEntry paramZipEntry)
    {
      super();
    }
    
    public Attributes getAttributes()
      throws IOException
    {
      Manifest localManifest = getManifest();
      if (localManifest != null) {
        return localManifest.getAttributes(getName());
      }
      return null;
    }
    
    public Certificate[] getCertificates()
    {
      try
      {
        JarFile.this.maybeInstantiateVerifier();
      }
      catch (IOException localIOException)
      {
        throw new RuntimeException(localIOException);
      }
      if ((certs == null) && (jv != null)) {
        certs = jv.getCerts(JarFile.this, this);
      }
      return certs == null ? null : (Certificate[])certs.clone();
    }
    
    public CodeSigner[] getCodeSigners()
    {
      try
      {
        JarFile.this.maybeInstantiateVerifier();
      }
      catch (IOException localIOException)
      {
        throw new RuntimeException(localIOException);
      }
      if ((signers == null) && (jv != null)) {
        signers = jv.getCodeSigners(JarFile.this, this);
      }
      return signers == null ? null : (CodeSigner[])signers.clone();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\jar\JarFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */