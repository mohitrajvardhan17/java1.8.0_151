package java.util.jar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.zip.ZipEntry;
import sun.security.util.Debug;
import sun.security.util.ManifestDigester;
import sun.security.util.ManifestEntryVerifier;
import sun.security.util.SignatureFileVerifier;

class JarVerifier
{
  static final Debug debug = Debug.getInstance("jar");
  private Hashtable<String, CodeSigner[]> verifiedSigners;
  private Hashtable<String, CodeSigner[]> sigFileSigners;
  private Hashtable<String, byte[]> sigFileData;
  private ArrayList<SignatureFileVerifier> pendingBlocks;
  private ArrayList<CodeSigner[]> signerCache;
  private boolean parsingBlockOrSF = false;
  private boolean parsingMeta = true;
  private boolean anyToVerify = true;
  private ByteArrayOutputStream baos;
  private volatile ManifestDigester manDig;
  byte[] manifestRawBytes = null;
  boolean eagerValidation;
  private Object csdomain = new Object();
  private List<Object> manifestDigests;
  private Map<URL, Map<CodeSigner[], CodeSource>> urlToCodeSourceMap = new HashMap();
  private Map<CodeSigner[], CodeSource> signerToCodeSource = new HashMap();
  private URL lastURL;
  private Map<CodeSigner[], CodeSource> lastURLMap;
  private CodeSigner[] emptySigner = new CodeSigner[0];
  private Map<String, CodeSigner[]> signerMap;
  private Enumeration<String> emptyEnumeration = new Enumeration()
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
  private List<CodeSigner[]> jarCodeSigners;
  
  public JarVerifier(byte[] paramArrayOfByte)
  {
    manifestRawBytes = paramArrayOfByte;
    sigFileSigners = new Hashtable();
    verifiedSigners = new Hashtable();
    sigFileData = new Hashtable(11);
    pendingBlocks = new ArrayList();
    baos = new ByteArrayOutputStream();
    manifestDigests = new ArrayList();
  }
  
  public void beginEntry(JarEntry paramJarEntry, ManifestEntryVerifier paramManifestEntryVerifier)
    throws IOException
  {
    if (paramJarEntry == null) {
      return;
    }
    if (debug != null) {
      debug.println("beginEntry " + paramJarEntry.getName());
    }
    String str1 = paramJarEntry.getName();
    if (parsingMeta)
    {
      String str2 = str1.toUpperCase(Locale.ENGLISH);
      if ((str2.startsWith("META-INF/")) || (str2.startsWith("/META-INF/")))
      {
        if (paramJarEntry.isDirectory())
        {
          paramManifestEntryVerifier.setEntry(null, paramJarEntry);
          return;
        }
        if ((str2.equals("META-INF/MANIFEST.MF")) || (str2.equals("META-INF/INDEX.LIST"))) {
          return;
        }
        if (SignatureFileVerifier.isBlockOrSF(str2))
        {
          parsingBlockOrSF = true;
          baos.reset();
          paramManifestEntryVerifier.setEntry(null, paramJarEntry);
          return;
        }
      }
    }
    if (parsingMeta) {
      doneWithMeta();
    }
    if (paramJarEntry.isDirectory())
    {
      paramManifestEntryVerifier.setEntry(null, paramJarEntry);
      return;
    }
    if (str1.startsWith("./")) {
      str1 = str1.substring(2);
    }
    if (str1.startsWith("/")) {
      str1 = str1.substring(1);
    }
    if ((!str1.equals("META-INF/MANIFEST.MF")) && ((sigFileSigners.get(str1) != null) || (verifiedSigners.get(str1) != null)))
    {
      paramManifestEntryVerifier.setEntry(str1, paramJarEntry);
      return;
    }
    paramManifestEntryVerifier.setEntry(null, paramJarEntry);
  }
  
  public void update(int paramInt, ManifestEntryVerifier paramManifestEntryVerifier)
    throws IOException
  {
    if (paramInt != -1)
    {
      if (parsingBlockOrSF) {
        baos.write(paramInt);
      } else {
        paramManifestEntryVerifier.update((byte)paramInt);
      }
    }
    else {
      processEntry(paramManifestEntryVerifier);
    }
  }
  
  public void update(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3, ManifestEntryVerifier paramManifestEntryVerifier)
    throws IOException
  {
    if (paramInt1 != -1)
    {
      if (parsingBlockOrSF) {
        baos.write(paramArrayOfByte, paramInt2, paramInt1);
      } else {
        paramManifestEntryVerifier.update(paramArrayOfByte, paramInt2, paramInt1);
      }
    }
    else {
      processEntry(paramManifestEntryVerifier);
    }
  }
  
  private void processEntry(ManifestEntryVerifier paramManifestEntryVerifier)
    throws IOException
  {
    Object localObject1;
    if (!parsingBlockOrSF)
    {
      localObject1 = paramManifestEntryVerifier.getEntry();
      if ((localObject1 != null) && (signers == null))
      {
        signers = paramManifestEntryVerifier.verify(verifiedSigners, sigFileSigners);
        certs = mapSignersToCertArray(signers);
      }
    }
    else
    {
      try
      {
        parsingBlockOrSF = false;
        if (debug != null) {
          debug.println("processEntry: processing block");
        }
        localObject1 = paramManifestEntryVerifier.getEntry().getName().toUpperCase(Locale.ENGLISH);
        Object localObject2;
        if (((String)localObject1).endsWith(".SF"))
        {
          str = ((String)localObject1).substring(0, ((String)localObject1).length() - 3);
          byte[] arrayOfByte = baos.toByteArray();
          sigFileData.put(str, arrayOfByte);
          localObject2 = pendingBlocks.iterator();
          while (((Iterator)localObject2).hasNext())
          {
            SignatureFileVerifier localSignatureFileVerifier = (SignatureFileVerifier)((Iterator)localObject2).next();
            if (localSignatureFileVerifier.needSignatureFile(str))
            {
              if (debug != null) {
                debug.println("processEntry: processing pending block");
              }
              localSignatureFileVerifier.setSignatureFile(arrayOfByte);
              localSignatureFileVerifier.process(sigFileSigners, manifestDigests);
            }
          }
          return;
        }
        String str = ((String)localObject1).substring(0, ((String)localObject1).lastIndexOf("."));
        if (signerCache == null) {
          signerCache = new ArrayList();
        }
        if (manDig == null) {
          synchronized (manifestRawBytes)
          {
            if (manDig == null)
            {
              manDig = new ManifestDigester(manifestRawBytes);
              manifestRawBytes = null;
            }
          }
        }
        ??? = new SignatureFileVerifier(signerCache, manDig, (String)localObject1, baos.toByteArray());
        if (((SignatureFileVerifier)???).needSignatureFileBytes())
        {
          localObject2 = (byte[])sigFileData.get(str);
          if (localObject2 == null)
          {
            if (debug != null) {
              debug.println("adding pending block");
            }
            pendingBlocks.add(???);
            return;
          }
          ((SignatureFileVerifier)???).setSignatureFile((byte[])localObject2);
        }
        ((SignatureFileVerifier)???).process(sigFileSigners, manifestDigests);
      }
      catch (IOException localIOException)
      {
        if (debug != null) {
          debug.println("processEntry caught: " + localIOException);
        }
      }
      catch (SignatureException localSignatureException)
      {
        if (debug != null) {
          debug.println("processEntry caught: " + localSignatureException);
        }
      }
      catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
      {
        if (debug != null) {
          debug.println("processEntry caught: " + localNoSuchAlgorithmException);
        }
      }
      catch (CertificateException localCertificateException)
      {
        if (debug != null) {
          debug.println("processEntry caught: " + localCertificateException);
        }
      }
    }
  }
  
  @Deprecated
  public Certificate[] getCerts(String paramString)
  {
    return mapSignersToCertArray(getCodeSigners(paramString));
  }
  
  public Certificate[] getCerts(JarFile paramJarFile, JarEntry paramJarEntry)
  {
    return mapSignersToCertArray(getCodeSigners(paramJarFile, paramJarEntry));
  }
  
  public CodeSigner[] getCodeSigners(String paramString)
  {
    return (CodeSigner[])verifiedSigners.get(paramString);
  }
  
  public CodeSigner[] getCodeSigners(JarFile paramJarFile, JarEntry paramJarEntry)
  {
    String str = paramJarEntry.getName();
    if ((eagerValidation) && (sigFileSigners.get(str) != null)) {
      try
      {
        InputStream localInputStream = paramJarFile.getInputStream(paramJarEntry);
        byte[] arrayOfByte = new byte['Ѐ'];
        for (int i = arrayOfByte.length; i != -1; i = localInputStream.read(arrayOfByte, 0, arrayOfByte.length)) {}
        localInputStream.close();
      }
      catch (IOException localIOException) {}
    }
    return getCodeSigners(str);
  }
  
  private static Certificate[] mapSignersToCertArray(CodeSigner[] paramArrayOfCodeSigner)
  {
    if (paramArrayOfCodeSigner != null)
    {
      ArrayList localArrayList = new ArrayList();
      for (int i = 0; i < paramArrayOfCodeSigner.length; i++) {
        localArrayList.addAll(paramArrayOfCodeSigner[i].getSignerCertPath().getCertificates());
      }
      return (Certificate[])localArrayList.toArray(new Certificate[localArrayList.size()]);
    }
    return null;
  }
  
  boolean nothingToVerify()
  {
    return !anyToVerify;
  }
  
  void doneWithMeta()
  {
    parsingMeta = false;
    anyToVerify = (!sigFileSigners.isEmpty());
    baos = null;
    sigFileData = null;
    pendingBlocks = null;
    signerCache = null;
    manDig = null;
    if (sigFileSigners.containsKey("META-INF/MANIFEST.MF"))
    {
      CodeSigner[] arrayOfCodeSigner = (CodeSigner[])sigFileSigners.remove("META-INF/MANIFEST.MF");
      verifiedSigners.put("META-INF/MANIFEST.MF", arrayOfCodeSigner);
    }
  }
  
  private synchronized CodeSource mapSignersToCodeSource(URL paramURL, CodeSigner[] paramArrayOfCodeSigner)
  {
    Object localObject1;
    if (paramURL == lastURL)
    {
      localObject1 = lastURLMap;
    }
    else
    {
      localObject1 = (Map)urlToCodeSourceMap.get(paramURL);
      if (localObject1 == null)
      {
        localObject1 = new HashMap();
        urlToCodeSourceMap.put(paramURL, localObject1);
      }
      lastURLMap = ((Map)localObject1);
      lastURL = paramURL;
    }
    Object localObject2 = (CodeSource)((Map)localObject1).get(paramArrayOfCodeSigner);
    if (localObject2 == null)
    {
      localObject2 = new VerifierCodeSource(csdomain, paramURL, paramArrayOfCodeSigner);
      signerToCodeSource.put(paramArrayOfCodeSigner, localObject2);
    }
    return (CodeSource)localObject2;
  }
  
  private CodeSource[] mapSignersToCodeSources(URL paramURL, List<CodeSigner[]> paramList, boolean paramBoolean)
  {
    ArrayList localArrayList = new ArrayList();
    for (int i = 0; i < paramList.size(); i++) {
      localArrayList.add(mapSignersToCodeSource(paramURL, (CodeSigner[])paramList.get(i)));
    }
    if (paramBoolean) {
      localArrayList.add(mapSignersToCodeSource(paramURL, null));
    }
    return (CodeSource[])localArrayList.toArray(new CodeSource[localArrayList.size()]);
  }
  
  private CodeSigner[] findMatchingSigners(CodeSource paramCodeSource)
  {
    if ((paramCodeSource instanceof VerifierCodeSource))
    {
      localObject = (VerifierCodeSource)paramCodeSource;
      if (((VerifierCodeSource)localObject).isSameDomain(csdomain)) {
        return ((VerifierCodeSource)paramCodeSource).getPrivateSigners();
      }
    }
    Object localObject = mapSignersToCodeSources(paramCodeSource.getLocation(), getJarCodeSigners(), true);
    ArrayList localArrayList = new ArrayList();
    for (int i = 0; i < localObject.length; i++) {
      localArrayList.add(localObject[i]);
    }
    i = localArrayList.indexOf(paramCodeSource);
    if (i != -1)
    {
      CodeSigner[] arrayOfCodeSigner = ((VerifierCodeSource)localArrayList.get(i)).getPrivateSigners();
      if (arrayOfCodeSigner == null) {
        arrayOfCodeSigner = emptySigner;
      }
      return arrayOfCodeSigner;
    }
    return null;
  }
  
  private synchronized Map<String, CodeSigner[]> signerMap()
  {
    if (signerMap == null)
    {
      signerMap = new HashMap(verifiedSigners.size() + sigFileSigners.size());
      signerMap.putAll(verifiedSigners);
      signerMap.putAll(sigFileSigners);
    }
    return signerMap;
  }
  
  public synchronized Enumeration<String> entryNames(JarFile paramJarFile, CodeSource[] paramArrayOfCodeSource)
  {
    Map localMap = signerMap();
    final Iterator localIterator = localMap.entrySet().iterator();
    int i = 0;
    ArrayList localArrayList1 = new ArrayList(paramArrayOfCodeSource.length);
    for (int j = 0; j < paramArrayOfCodeSource.length; j++)
    {
      localObject = findMatchingSigners(paramArrayOfCodeSource[j]);
      if (localObject != null)
      {
        if (localObject.length > 0) {
          localArrayList1.add(localObject);
        } else {
          i = 1;
        }
      }
      else {
        i = 1;
      }
    }
    final ArrayList localArrayList2 = localArrayList1;
    final Object localObject = i != 0 ? unsignedEntryNames(paramJarFile) : emptyEnumeration;
    new Enumeration()
    {
      String name;
      
      public boolean hasMoreElements()
      {
        if (name != null) {
          return true;
        }
        while (localIterator.hasNext())
        {
          Map.Entry localEntry = (Map.Entry)localIterator.next();
          if (localArrayList2.contains(localEntry.getValue()))
          {
            name = ((String)localEntry.getKey());
            return true;
          }
        }
        if (localObject.hasMoreElements())
        {
          name = ((String)localObject.nextElement());
          return true;
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
  
  public Enumeration<JarEntry> entries2(final JarFile paramJarFile, Enumeration<? extends ZipEntry> paramEnumeration)
  {
    final HashMap localHashMap = new HashMap();
    localHashMap.putAll(signerMap());
    final Enumeration<? extends ZipEntry> localEnumeration = paramEnumeration;
    new Enumeration()
    {
      Enumeration<String> signers = null;
      JarEntry entry;
      
      public boolean hasMoreElements()
      {
        if (entry != null) {
          return true;
        }
        Object localObject;
        while (localEnumeration.hasMoreElements())
        {
          localObject = (ZipEntry)localEnumeration.nextElement();
          if (!JarVerifier.isSigningRelated(((ZipEntry)localObject).getName()))
          {
            entry = paramJarFile.newEntry((ZipEntry)localObject);
            return true;
          }
        }
        if (signers == null) {
          signers = Collections.enumeration(localHashMap.keySet());
        }
        if (signers.hasMoreElements())
        {
          localObject = (String)signers.nextElement();
          entry = paramJarFile.newEntry(new ZipEntry((String)localObject));
          return true;
        }
        return false;
      }
      
      public JarEntry nextElement()
      {
        if (hasMoreElements())
        {
          JarEntry localJarEntry = entry;
          localHashMap.remove(localJarEntry.getName());
          entry = null;
          return localJarEntry;
        }
        throw new NoSuchElementException();
      }
    };
  }
  
  static boolean isSigningRelated(String paramString)
  {
    return SignatureFileVerifier.isSigningRelated(paramString);
  }
  
  private Enumeration<String> unsignedEntryNames(JarFile paramJarFile)
  {
    final Map localMap = signerMap();
    final Enumeration localEnumeration = paramJarFile.entries();
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
          if ((!localZipEntry.isDirectory()) && (!JarVerifier.isSigningRelated(str))) {
            if (localMap.get(str) == null)
            {
              name = str;
              return true;
            }
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
  
  private synchronized List<CodeSigner[]> getJarCodeSigners()
  {
    if (jarCodeSigners == null)
    {
      HashSet localHashSet = new HashSet();
      localHashSet.addAll(signerMap().values());
      jarCodeSigners = new ArrayList();
      jarCodeSigners.addAll(localHashSet);
    }
    return jarCodeSigners;
  }
  
  public synchronized CodeSource[] getCodeSources(JarFile paramJarFile, URL paramURL)
  {
    boolean bool = unsignedEntryNames(paramJarFile).hasMoreElements();
    return mapSignersToCodeSources(paramURL, getJarCodeSigners(), bool);
  }
  
  public CodeSource getCodeSource(URL paramURL, String paramString)
  {
    CodeSigner[] arrayOfCodeSigner = (CodeSigner[])signerMap().get(paramString);
    return mapSignersToCodeSource(paramURL, arrayOfCodeSigner);
  }
  
  public CodeSource getCodeSource(URL paramURL, JarFile paramJarFile, JarEntry paramJarEntry)
  {
    return mapSignersToCodeSource(paramURL, getCodeSigners(paramJarFile, paramJarEntry));
  }
  
  public void setEagerValidation(boolean paramBoolean)
  {
    eagerValidation = paramBoolean;
  }
  
  public synchronized List<Object> getManifestDigests()
  {
    return Collections.unmodifiableList(manifestDigests);
  }
  
  static CodeSource getUnsignedCS(URL paramURL)
  {
    return new VerifierCodeSource(null, paramURL, (Certificate[])null);
  }
  
  private static class VerifierCodeSource
    extends CodeSource
  {
    private static final long serialVersionUID = -9047366145967768825L;
    URL vlocation;
    CodeSigner[] vsigners;
    Certificate[] vcerts;
    Object csdomain;
    
    VerifierCodeSource(Object paramObject, URL paramURL, CodeSigner[] paramArrayOfCodeSigner)
    {
      super(paramArrayOfCodeSigner);
      csdomain = paramObject;
      vlocation = paramURL;
      vsigners = paramArrayOfCodeSigner;
    }
    
    VerifierCodeSource(Object paramObject, URL paramURL, Certificate[] paramArrayOfCertificate)
    {
      super(paramArrayOfCertificate);
      csdomain = paramObject;
      vlocation = paramURL;
      vcerts = paramArrayOfCertificate;
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == this) {
        return true;
      }
      if ((paramObject instanceof VerifierCodeSource))
      {
        VerifierCodeSource localVerifierCodeSource = (VerifierCodeSource)paramObject;
        if (isSameDomain(csdomain))
        {
          if ((vsigners != vsigners) || (vcerts != vcerts)) {
            return false;
          }
          if (vlocation != null) {
            return vlocation.equals(vlocation);
          }
          if (vlocation != null) {
            return vlocation.equals(vlocation);
          }
          return true;
        }
      }
      return super.equals(paramObject);
    }
    
    boolean isSameDomain(Object paramObject)
    {
      return csdomain == paramObject;
    }
    
    private CodeSigner[] getPrivateSigners()
    {
      return vsigners;
    }
    
    private Certificate[] getPrivateCertificates()
    {
      return vcerts;
    }
  }
  
  static class VerifierStream
    extends InputStream
  {
    private InputStream is;
    private JarVerifier jv;
    private ManifestEntryVerifier mev;
    private long numLeft;
    
    VerifierStream(Manifest paramManifest, JarEntry paramJarEntry, InputStream paramInputStream, JarVerifier paramJarVerifier)
      throws IOException
    {
      is = paramInputStream;
      jv = paramJarVerifier;
      mev = new ManifestEntryVerifier(paramManifest);
      jv.beginEntry(paramJarEntry, mev);
      numLeft = paramJarEntry.getSize();
      if (numLeft == 0L) {
        jv.update(-1, mev);
      }
    }
    
    public int read()
      throws IOException
    {
      if (numLeft > 0L)
      {
        int i = is.read();
        jv.update(i, mev);
        numLeft -= 1L;
        if (numLeft == 0L) {
          jv.update(-1, mev);
        }
        return i;
      }
      return -1;
    }
    
    public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      if ((numLeft > 0L) && (numLeft < paramInt2)) {
        paramInt2 = (int)numLeft;
      }
      if (numLeft > 0L)
      {
        int i = is.read(paramArrayOfByte, paramInt1, paramInt2);
        jv.update(i, paramArrayOfByte, paramInt1, paramInt2, mev);
        numLeft -= i;
        if (numLeft == 0L) {
          jv.update(-1, paramArrayOfByte, paramInt1, paramInt2, mev);
        }
        return i;
      }
      return -1;
    }
    
    public void close()
      throws IOException
    {
      if (is != null) {
        is.close();
      }
      is = null;
      mev = null;
      jv = null;
    }
    
    public int available()
      throws IOException
    {
      return is.available();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\jar\JarVerifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */