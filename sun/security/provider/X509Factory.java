package sun.security.provider;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactorySpi;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import sun.security.pkcs.PKCS7;
import sun.security.pkcs.ParsingException;
import sun.security.provider.certpath.X509CertPath;
import sun.security.provider.certpath.X509CertificatePair;
import sun.security.util.Cache;
import sun.security.util.Cache.EqualByteArray;
import sun.security.util.Pem;
import sun.security.x509.X509CRLImpl;
import sun.security.x509.X509CertImpl;

public class X509Factory
  extends CertificateFactorySpi
{
  public static final String BEGIN_CERT = "-----BEGIN CERTIFICATE-----";
  public static final String END_CERT = "-----END CERTIFICATE-----";
  private static final int ENC_MAX_LENGTH = 4194304;
  private static final Cache<Object, X509CertImpl> certCache = Cache.newSoftMemoryCache(750);
  private static final Cache<Object, X509CRLImpl> crlCache = Cache.newSoftMemoryCache(750);
  
  public X509Factory() {}
  
  public Certificate engineGenerateCertificate(InputStream paramInputStream)
    throws CertificateException
  {
    if (paramInputStream == null)
    {
      certCache.clear();
      X509CertificatePair.clearCache();
      throw new CertificateException("Missing input stream");
    }
    try
    {
      byte[] arrayOfByte = readOneBlock(paramInputStream);
      if (arrayOfByte != null)
      {
        X509CertImpl localX509CertImpl = (X509CertImpl)getFromCache(certCache, arrayOfByte);
        if (localX509CertImpl != null) {
          return localX509CertImpl;
        }
        localX509CertImpl = new X509CertImpl(arrayOfByte);
        addToCache(certCache, localX509CertImpl.getEncodedInternal(), localX509CertImpl);
        return localX509CertImpl;
      }
      throw new IOException("Empty input");
    }
    catch (IOException localIOException)
    {
      throw new CertificateException("Could not parse certificate: " + localIOException.toString(), localIOException);
    }
  }
  
  private static int readFully(InputStream paramInputStream, ByteArrayOutputStream paramByteArrayOutputStream, int paramInt)
    throws IOException
  {
    int i = 0;
    byte[] arrayOfByte = new byte['ࠀ'];
    while (paramInt > 0)
    {
      int j = paramInputStream.read(arrayOfByte, 0, paramInt < 2048 ? paramInt : 2048);
      if (j <= 0) {
        break;
      }
      paramByteArrayOutputStream.write(arrayOfByte, 0, j);
      i += j;
      paramInt -= j;
    }
    return i;
  }
  
  public static synchronized X509CertImpl intern(X509Certificate paramX509Certificate)
    throws CertificateException
  {
    if (paramX509Certificate == null) {
      return null;
    }
    boolean bool = paramX509Certificate instanceof X509CertImpl;
    byte[] arrayOfByte;
    if (bool) {
      arrayOfByte = ((X509CertImpl)paramX509Certificate).getEncodedInternal();
    } else {
      arrayOfByte = paramX509Certificate.getEncoded();
    }
    X509CertImpl localX509CertImpl = (X509CertImpl)getFromCache(certCache, arrayOfByte);
    if (localX509CertImpl != null) {
      return localX509CertImpl;
    }
    if (bool)
    {
      localX509CertImpl = (X509CertImpl)paramX509Certificate;
    }
    else
    {
      localX509CertImpl = new X509CertImpl(arrayOfByte);
      arrayOfByte = localX509CertImpl.getEncodedInternal();
    }
    addToCache(certCache, arrayOfByte, localX509CertImpl);
    return localX509CertImpl;
  }
  
  public static synchronized X509CRLImpl intern(X509CRL paramX509CRL)
    throws CRLException
  {
    if (paramX509CRL == null) {
      return null;
    }
    boolean bool = paramX509CRL instanceof X509CRLImpl;
    byte[] arrayOfByte;
    if (bool) {
      arrayOfByte = ((X509CRLImpl)paramX509CRL).getEncodedInternal();
    } else {
      arrayOfByte = paramX509CRL.getEncoded();
    }
    X509CRLImpl localX509CRLImpl = (X509CRLImpl)getFromCache(crlCache, arrayOfByte);
    if (localX509CRLImpl != null) {
      return localX509CRLImpl;
    }
    if (bool)
    {
      localX509CRLImpl = (X509CRLImpl)paramX509CRL;
    }
    else
    {
      localX509CRLImpl = new X509CRLImpl(arrayOfByte);
      arrayOfByte = localX509CRLImpl.getEncodedInternal();
    }
    addToCache(crlCache, arrayOfByte, localX509CRLImpl);
    return localX509CRLImpl;
  }
  
  private static synchronized <K, V> V getFromCache(Cache<K, V> paramCache, byte[] paramArrayOfByte)
  {
    Cache.EqualByteArray localEqualByteArray = new Cache.EqualByteArray(paramArrayOfByte);
    return (V)paramCache.get(localEqualByteArray);
  }
  
  private static synchronized <V> void addToCache(Cache<Object, V> paramCache, byte[] paramArrayOfByte, V paramV)
  {
    if (paramArrayOfByte.length > 4194304) {
      return;
    }
    Cache.EqualByteArray localEqualByteArray = new Cache.EqualByteArray(paramArrayOfByte);
    paramCache.put(localEqualByteArray, paramV);
  }
  
  public CertPath engineGenerateCertPath(InputStream paramInputStream)
    throws CertificateException
  {
    if (paramInputStream == null) {
      throw new CertificateException("Missing input stream");
    }
    try
    {
      byte[] arrayOfByte = readOneBlock(paramInputStream);
      if (arrayOfByte != null) {
        return new X509CertPath(new ByteArrayInputStream(arrayOfByte));
      }
      throw new IOException("Empty input");
    }
    catch (IOException localIOException)
    {
      throw new CertificateException(localIOException.getMessage());
    }
  }
  
  public CertPath engineGenerateCertPath(InputStream paramInputStream, String paramString)
    throws CertificateException
  {
    if (paramInputStream == null) {
      throw new CertificateException("Missing input stream");
    }
    try
    {
      byte[] arrayOfByte = readOneBlock(paramInputStream);
      if (arrayOfByte != null) {
        return new X509CertPath(new ByteArrayInputStream(arrayOfByte), paramString);
      }
      throw new IOException("Empty input");
    }
    catch (IOException localIOException)
    {
      throw new CertificateException(localIOException.getMessage());
    }
  }
  
  public CertPath engineGenerateCertPath(List<? extends Certificate> paramList)
    throws CertificateException
  {
    return new X509CertPath(paramList);
  }
  
  public Iterator<String> engineGetCertPathEncodings()
  {
    return X509CertPath.getEncodingsStatic();
  }
  
  public Collection<? extends Certificate> engineGenerateCertificates(InputStream paramInputStream)
    throws CertificateException
  {
    if (paramInputStream == null) {
      throw new CertificateException("Missing input stream");
    }
    try
    {
      return parseX509orPKCS7Cert(paramInputStream);
    }
    catch (IOException localIOException)
    {
      throw new CertificateException(localIOException);
    }
  }
  
  public CRL engineGenerateCRL(InputStream paramInputStream)
    throws CRLException
  {
    if (paramInputStream == null)
    {
      crlCache.clear();
      throw new CRLException("Missing input stream");
    }
    try
    {
      byte[] arrayOfByte = readOneBlock(paramInputStream);
      if (arrayOfByte != null)
      {
        X509CRLImpl localX509CRLImpl = (X509CRLImpl)getFromCache(crlCache, arrayOfByte);
        if (localX509CRLImpl != null) {
          return localX509CRLImpl;
        }
        localX509CRLImpl = new X509CRLImpl(arrayOfByte);
        addToCache(crlCache, localX509CRLImpl.getEncodedInternal(), localX509CRLImpl);
        return localX509CRLImpl;
      }
      throw new IOException("Empty input");
    }
    catch (IOException localIOException)
    {
      throw new CRLException(localIOException.getMessage());
    }
  }
  
  public Collection<? extends CRL> engineGenerateCRLs(InputStream paramInputStream)
    throws CRLException
  {
    if (paramInputStream == null) {
      throw new CRLException("Missing input stream");
    }
    try
    {
      return parseX509orPKCS7CRL(paramInputStream);
    }
    catch (IOException localIOException)
    {
      throw new CRLException(localIOException.getMessage());
    }
  }
  
  private Collection<? extends Certificate> parseX509orPKCS7Cert(InputStream paramInputStream)
    throws CertificateException, IOException
  {
    PushbackInputStream localPushbackInputStream = new PushbackInputStream(paramInputStream);
    ArrayList localArrayList = new ArrayList();
    int i = localPushbackInputStream.read();
    if (i == -1) {
      return new ArrayList(0);
    }
    localPushbackInputStream.unread(i);
    byte[] arrayOfByte = readOneBlock(localPushbackInputStream);
    if (arrayOfByte == null) {
      throw new CertificateException("No certificate data found");
    }
    try
    {
      PKCS7 localPKCS7 = new PKCS7(arrayOfByte);
      X509Certificate[] arrayOfX509Certificate = localPKCS7.getCertificates();
      if (arrayOfX509Certificate != null) {
        return Arrays.asList(arrayOfX509Certificate);
      }
      return new ArrayList(0);
    }
    catch (ParsingException localParsingException)
    {
      while (arrayOfByte != null)
      {
        localArrayList.add(new X509CertImpl(arrayOfByte));
        arrayOfByte = readOneBlock(localPushbackInputStream);
      }
    }
    return localArrayList;
  }
  
  private Collection<? extends CRL> parseX509orPKCS7CRL(InputStream paramInputStream)
    throws CRLException, IOException
  {
    PushbackInputStream localPushbackInputStream = new PushbackInputStream(paramInputStream);
    ArrayList localArrayList = new ArrayList();
    int i = localPushbackInputStream.read();
    if (i == -1) {
      return new ArrayList(0);
    }
    localPushbackInputStream.unread(i);
    byte[] arrayOfByte = readOneBlock(localPushbackInputStream);
    if (arrayOfByte == null) {
      throw new CRLException("No CRL data found");
    }
    try
    {
      PKCS7 localPKCS7 = new PKCS7(arrayOfByte);
      X509CRL[] arrayOfX509CRL = localPKCS7.getCRLs();
      if (arrayOfX509CRL != null) {
        return Arrays.asList(arrayOfX509CRL);
      }
      return new ArrayList(0);
    }
    catch (ParsingException localParsingException)
    {
      while (arrayOfByte != null)
      {
        localArrayList.add(new X509CRLImpl(arrayOfByte));
        arrayOfByte = readOneBlock(localPushbackInputStream);
      }
    }
    return localArrayList;
  }
  
  private static byte[] readOneBlock(InputStream paramInputStream)
    throws IOException
  {
    int i = paramInputStream.read();
    if (i == -1) {
      return null;
    }
    if (i == 48)
    {
      localObject = new ByteArrayOutputStream(2048);
      ((ByteArrayOutputStream)localObject).write(i);
      readBERInternal(paramInputStream, (ByteArrayOutputStream)localObject, i);
      return ((ByteArrayOutputStream)localObject).toByteArray();
    }
    Object localObject = new char['ࠀ'];
    int j = 0;
    int k = i == 45 ? 1 : 0;
    int m = i == 45 ? -1 : i;
    int n;
    for (;;)
    {
      n = paramInputStream.read();
      if (n == -1) {
        return null;
      }
      if (n == 45)
      {
        k++;
      }
      else
      {
        k = 0;
        m = n;
      }
      if ((k == 5) && ((m == -1) || (m == 13) || (m == 10))) {
        break;
      }
    }
    StringBuilder localStringBuilder1 = new StringBuilder("-----");
    int i1;
    for (;;)
    {
      i1 = paramInputStream.read();
      if (i1 == -1) {
        throw new IOException("Incomplete data");
      }
      if (i1 == 10)
      {
        n = 10;
        break;
      }
      if (i1 == 13)
      {
        i1 = paramInputStream.read();
        if (i1 == -1) {
          throw new IOException("Incomplete data");
        }
        if (i1 == 10)
        {
          n = 10;
          break;
        }
        n = 13;
        localObject[(j++)] = ((char)i1);
        break;
      }
      localStringBuilder1.append((char)i1);
    }
    for (;;)
    {
      i1 = paramInputStream.read();
      if (i1 == -1) {
        throw new IOException("Incomplete data");
      }
      if (i1 == 45) {
        break;
      }
      localObject[(j++)] = ((char)i1);
      if (j >= localObject.length) {
        localObject = Arrays.copyOf((char[])localObject, localObject.length + 1024);
      }
    }
    StringBuilder localStringBuilder2 = new StringBuilder("-");
    for (;;)
    {
      int i2 = paramInputStream.read();
      if ((i2 == -1) || (i2 == n) || (i2 == 10)) {
        break;
      }
      if (i2 != 13) {
        localStringBuilder2.append((char)i2);
      }
    }
    checkHeaderFooter(localStringBuilder1.toString(), localStringBuilder2.toString());
    return Pem.decode(new String((char[])localObject, 0, j));
  }
  
  private static void checkHeaderFooter(String paramString1, String paramString2)
    throws IOException
  {
    if ((paramString1.length() < 16) || (!paramString1.startsWith("-----BEGIN ")) || (!paramString1.endsWith("-----"))) {
      throw new IOException("Illegal header: " + paramString1);
    }
    if ((paramString2.length() < 14) || (!paramString2.startsWith("-----END ")) || (!paramString2.endsWith("-----"))) {
      throw new IOException("Illegal footer: " + paramString2);
    }
    String str1 = paramString1.substring(11, paramString1.length() - 5);
    String str2 = paramString2.substring(9, paramString2.length() - 5);
    if (!str1.equals(str2)) {
      throw new IOException("Header and footer do not match: " + paramString1 + " " + paramString2);
    }
  }
  
  private static int readBERInternal(InputStream paramInputStream, ByteArrayOutputStream paramByteArrayOutputStream, int paramInt)
    throws IOException
  {
    if (paramInt == -1)
    {
      paramInt = paramInputStream.read();
      if (paramInt == -1) {
        throw new IOException("BER/DER tag info absent");
      }
      if ((paramInt & 0x1F) == 31) {
        throw new IOException("Multi octets tag not supported");
      }
      paramByteArrayOutputStream.write(paramInt);
    }
    int i = paramInputStream.read();
    if (i == -1) {
      throw new IOException("BER/DER length info absent");
    }
    paramByteArrayOutputStream.write(i);
    int k;
    if (i == 128)
    {
      if ((paramInt & 0x20) != 32) {
        throw new IOException("Non constructed encoding must have definite length");
      }
      for (;;)
      {
        k = readBERInternal(paramInputStream, paramByteArrayOutputStream, -1);
        if (k == 0) {
          break;
        }
      }
    }
    int j;
    if (i < 128)
    {
      j = i;
    }
    else if (i == 129)
    {
      j = paramInputStream.read();
      if (j == -1) {
        throw new IOException("Incomplete BER/DER length info");
      }
      paramByteArrayOutputStream.write(j);
    }
    else
    {
      int m;
      if (i == 130)
      {
        k = paramInputStream.read();
        m = paramInputStream.read();
        if (m == -1) {
          throw new IOException("Incomplete BER/DER length info");
        }
        paramByteArrayOutputStream.write(k);
        paramByteArrayOutputStream.write(m);
        j = k << 8 | m;
      }
      else
      {
        int n;
        if (i == 131)
        {
          k = paramInputStream.read();
          m = paramInputStream.read();
          n = paramInputStream.read();
          if (n == -1) {
            throw new IOException("Incomplete BER/DER length info");
          }
          paramByteArrayOutputStream.write(k);
          paramByteArrayOutputStream.write(m);
          paramByteArrayOutputStream.write(n);
          j = k << 16 | m << 8 | n;
        }
        else if (i == 132)
        {
          k = paramInputStream.read();
          m = paramInputStream.read();
          n = paramInputStream.read();
          int i1 = paramInputStream.read();
          if (i1 == -1) {
            throw new IOException("Incomplete BER/DER length info");
          }
          if (k > 127) {
            throw new IOException("Invalid BER/DER data (a little huge?)");
          }
          paramByteArrayOutputStream.write(k);
          paramByteArrayOutputStream.write(m);
          paramByteArrayOutputStream.write(n);
          paramByteArrayOutputStream.write(i1);
          j = k << 24 | m << 16 | n << 8 | i1;
        }
        else
        {
          throw new IOException("Invalid BER/DER data (too huge?)");
        }
      }
    }
    if (readFully(paramInputStream, paramByteArrayOutputStream, j) != j) {
      throw new IOException("Incomplete BER/DER data");
    }
    return paramInt;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\X509Factory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */