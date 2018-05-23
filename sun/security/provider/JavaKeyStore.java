package sun.security.provider;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.DigestInputStream;
import java.security.DigestOutputStream;
import java.security.Key;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import sun.misc.IOUtils;
import sun.security.pkcs.EncryptedPrivateKeyInfo;
import sun.security.pkcs12.PKCS12KeyStore;

abstract class JavaKeyStore
  extends KeyStoreSpi
{
  private static final int MAGIC = -17957139;
  private static final int VERSION_1 = 1;
  private static final int VERSION_2 = 2;
  private final Hashtable<String, Object> entries = new Hashtable();
  
  JavaKeyStore() {}
  
  abstract String convertAlias(String paramString);
  
  public Key engineGetKey(String paramString, char[] paramArrayOfChar)
    throws NoSuchAlgorithmException, UnrecoverableKeyException
  {
    Object localObject = entries.get(convertAlias(paramString));
    if ((localObject == null) || (!(localObject instanceof KeyEntry))) {
      return null;
    }
    if (paramArrayOfChar == null) {
      throw new UnrecoverableKeyException("Password must not be null");
    }
    KeyProtector localKeyProtector = new KeyProtector(paramArrayOfChar);
    byte[] arrayOfByte = protectedPrivKey;
    EncryptedPrivateKeyInfo localEncryptedPrivateKeyInfo;
    try
    {
      localEncryptedPrivateKeyInfo = new EncryptedPrivateKeyInfo(arrayOfByte);
    }
    catch (IOException localIOException)
    {
      throw new UnrecoverableKeyException("Private key not stored as PKCS #8 EncryptedPrivateKeyInfo");
    }
    return localKeyProtector.recover(localEncryptedPrivateKeyInfo);
  }
  
  public Certificate[] engineGetCertificateChain(String paramString)
  {
    Object localObject = entries.get(convertAlias(paramString));
    if ((localObject != null) && ((localObject instanceof KeyEntry)))
    {
      if (chain == null) {
        return null;
      }
      return (Certificate[])chain.clone();
    }
    return null;
  }
  
  public Certificate engineGetCertificate(String paramString)
  {
    Object localObject = entries.get(convertAlias(paramString));
    if (localObject != null)
    {
      if ((localObject instanceof TrustedCertEntry)) {
        return cert;
      }
      if (chain == null) {
        return null;
      }
      return chain[0];
    }
    return null;
  }
  
  public Date engineGetCreationDate(String paramString)
  {
    Object localObject = entries.get(convertAlias(paramString));
    if (localObject != null)
    {
      if ((localObject instanceof TrustedCertEntry)) {
        return new Date(date.getTime());
      }
      return new Date(date.getTime());
    }
    return null;
  }
  
  public void engineSetKeyEntry(String paramString, Key paramKey, char[] paramArrayOfChar, Certificate[] paramArrayOfCertificate)
    throws KeyStoreException
  {
    KeyProtector localKeyProtector = null;
    if (!(paramKey instanceof PrivateKey)) {
      throw new KeyStoreException("Cannot store non-PrivateKeys");
    }
    try
    {
      synchronized (entries)
      {
        KeyEntry localKeyEntry = new KeyEntry(null);
        date = new Date();
        localKeyProtector = new KeyProtector(paramArrayOfChar);
        protectedPrivKey = localKeyProtector.protect(paramKey);
        if ((paramArrayOfCertificate != null) && (paramArrayOfCertificate.length != 0)) {
          chain = ((Certificate[])paramArrayOfCertificate.clone());
        } else {
          chain = null;
        }
        entries.put(convertAlias(paramString), localKeyEntry);
      }
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      throw new KeyStoreException("Key protection algorithm not found");
    }
    finally
    {
      localKeyProtector = null;
    }
  }
  
  public void engineSetKeyEntry(String paramString, byte[] paramArrayOfByte, Certificate[] paramArrayOfCertificate)
    throws KeyStoreException
  {
    synchronized (entries)
    {
      try
      {
        new EncryptedPrivateKeyInfo(paramArrayOfByte);
      }
      catch (IOException localIOException)
      {
        throw new KeyStoreException("key is not encoded as EncryptedPrivateKeyInfo");
      }
      KeyEntry localKeyEntry = new KeyEntry(null);
      date = new Date();
      protectedPrivKey = ((byte[])paramArrayOfByte.clone());
      if ((paramArrayOfCertificate != null) && (paramArrayOfCertificate.length != 0)) {
        chain = ((Certificate[])paramArrayOfCertificate.clone());
      } else {
        chain = null;
      }
      entries.put(convertAlias(paramString), localKeyEntry);
    }
  }
  
  public void engineSetCertificateEntry(String paramString, Certificate paramCertificate)
    throws KeyStoreException
  {
    synchronized (entries)
    {
      Object localObject1 = entries.get(convertAlias(paramString));
      if ((localObject1 != null) && ((localObject1 instanceof KeyEntry))) {
        throw new KeyStoreException("Cannot overwrite own certificate");
      }
      TrustedCertEntry localTrustedCertEntry = new TrustedCertEntry(null);
      cert = paramCertificate;
      date = new Date();
      entries.put(convertAlias(paramString), localTrustedCertEntry);
    }
  }
  
  public void engineDeleteEntry(String paramString)
    throws KeyStoreException
  {
    synchronized (entries)
    {
      entries.remove(convertAlias(paramString));
    }
  }
  
  public Enumeration<String> engineAliases()
  {
    return entries.keys();
  }
  
  public boolean engineContainsAlias(String paramString)
  {
    return entries.containsKey(convertAlias(paramString));
  }
  
  public int engineSize()
  {
    return entries.size();
  }
  
  public boolean engineIsKeyEntry(String paramString)
  {
    Object localObject = entries.get(convertAlias(paramString));
    return (localObject != null) && ((localObject instanceof KeyEntry));
  }
  
  public boolean engineIsCertificateEntry(String paramString)
  {
    Object localObject = entries.get(convertAlias(paramString));
    return (localObject != null) && ((localObject instanceof TrustedCertEntry));
  }
  
  public String engineGetCertificateAlias(Certificate paramCertificate)
  {
    Enumeration localEnumeration = entries.keys();
    while (localEnumeration.hasMoreElements())
    {
      String str = (String)localEnumeration.nextElement();
      Object localObject = entries.get(str);
      Certificate localCertificate;
      if ((localObject instanceof TrustedCertEntry))
      {
        localCertificate = cert;
      }
      else
      {
        if (chain == null) {
          continue;
        }
        localCertificate = chain[0];
      }
      if (localCertificate.equals(paramCertificate)) {
        return str;
      }
    }
    return null;
  }
  
  public void engineStore(OutputStream paramOutputStream, char[] paramArrayOfChar)
    throws IOException, NoSuchAlgorithmException, CertificateException
  {
    synchronized (entries)
    {
      if (paramArrayOfChar == null) {
        throw new IllegalArgumentException("password can't be null");
      }
      MessageDigest localMessageDigest = getPreKeyedHash(paramArrayOfChar);
      DataOutputStream localDataOutputStream = new DataOutputStream(new DigestOutputStream(paramOutputStream, localMessageDigest));
      localDataOutputStream.writeInt(-17957139);
      localDataOutputStream.writeInt(2);
      localDataOutputStream.writeInt(entries.size());
      Object localObject1 = entries.keys();
      while (((Enumeration)localObject1).hasMoreElements())
      {
        String str = (String)((Enumeration)localObject1).nextElement();
        Object localObject2 = entries.get(str);
        byte[] arrayOfByte;
        if ((localObject2 instanceof KeyEntry))
        {
          localDataOutputStream.writeInt(1);
          localDataOutputStream.writeUTF(str);
          localDataOutputStream.writeLong(date.getTime());
          localDataOutputStream.writeInt(protectedPrivKey.length);
          localDataOutputStream.write(protectedPrivKey);
          int i;
          if (chain == null) {
            i = 0;
          } else {
            i = chain.length;
          }
          localDataOutputStream.writeInt(i);
          for (int j = 0; j < i; j++)
          {
            arrayOfByte = chain[j].getEncoded();
            localDataOutputStream.writeUTF(chain[j].getType());
            localDataOutputStream.writeInt(arrayOfByte.length);
            localDataOutputStream.write(arrayOfByte);
          }
        }
        else
        {
          localDataOutputStream.writeInt(2);
          localDataOutputStream.writeUTF(str);
          localDataOutputStream.writeLong(date.getTime());
          arrayOfByte = cert.getEncoded();
          localDataOutputStream.writeUTF(cert.getType());
          localDataOutputStream.writeInt(arrayOfByte.length);
          localDataOutputStream.write(arrayOfByte);
        }
      }
      localObject1 = localMessageDigest.digest();
      localDataOutputStream.write((byte[])localObject1);
      localDataOutputStream.flush();
    }
  }
  
  public void engineLoad(InputStream paramInputStream, char[] paramArrayOfChar)
    throws IOException, NoSuchAlgorithmException, CertificateException
  {
    synchronized (entries)
    {
      MessageDigest localMessageDigest = null;
      CertificateFactory localCertificateFactory = null;
      Hashtable localHashtable = null;
      ByteArrayInputStream localByteArrayInputStream = null;
      byte[] arrayOfByte1 = null;
      if (paramInputStream == null) {
        return;
      }
      DataInputStream localDataInputStream;
      if (paramArrayOfChar != null)
      {
        localMessageDigest = getPreKeyedHash(paramArrayOfChar);
        localDataInputStream = new DataInputStream(new DigestInputStream(paramInputStream, localMessageDigest));
      }
      else
      {
        localDataInputStream = new DataInputStream(paramInputStream);
      }
      int i = localDataInputStream.readInt();
      int j = localDataInputStream.readInt();
      if ((i != -17957139) || ((j != 1) && (j != 2))) {
        throw new IOException("Invalid keystore format");
      }
      if (j == 1) {
        localCertificateFactory = CertificateFactory.getInstance("X509");
      } else {
        localHashtable = new Hashtable(3);
      }
      entries.clear();
      int k = localDataInputStream.readInt();
      Object localObject1;
      for (int m = 0; m < k; m++)
      {
        int n = localDataInputStream.readInt();
        String str1;
        if (n == 1)
        {
          localObject1 = new KeyEntry(null);
          str1 = localDataInputStream.readUTF();
          date = new Date(localDataInputStream.readLong());
          protectedPrivKey = IOUtils.readFully(localDataInputStream, localDataInputStream.readInt(), true);
          int i2 = localDataInputStream.readInt();
          if (i2 > 0)
          {
            ArrayList localArrayList = new ArrayList(i2 > 10 ? 10 : i2);
            for (int i3 = 0; i3 < i2; i3++)
            {
              if (j == 2)
              {
                String str3 = localDataInputStream.readUTF();
                if (localHashtable.containsKey(str3))
                {
                  localCertificateFactory = (CertificateFactory)localHashtable.get(str3);
                }
                else
                {
                  localCertificateFactory = CertificateFactory.getInstance(str3);
                  localHashtable.put(str3, localCertificateFactory);
                }
              }
              arrayOfByte1 = IOUtils.readFully(localDataInputStream, localDataInputStream.readInt(), true);
              localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte1);
              localArrayList.add(localCertificateFactory.generateCertificate(localByteArrayInputStream));
              localByteArrayInputStream.close();
            }
            chain = ((Certificate[])localArrayList.toArray(new Certificate[i2]));
          }
          entries.put(str1, localObject1);
        }
        else if (n == 2)
        {
          localObject1 = new TrustedCertEntry(null);
          str1 = localDataInputStream.readUTF();
          date = new Date(localDataInputStream.readLong());
          if (j == 2)
          {
            String str2 = localDataInputStream.readUTF();
            if (localHashtable.containsKey(str2))
            {
              localCertificateFactory = (CertificateFactory)localHashtable.get(str2);
            }
            else
            {
              localCertificateFactory = CertificateFactory.getInstance(str2);
              localHashtable.put(str2, localCertificateFactory);
            }
          }
          arrayOfByte1 = IOUtils.readFully(localDataInputStream, localDataInputStream.readInt(), true);
          localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte1);
          cert = localCertificateFactory.generateCertificate(localByteArrayInputStream);
          localByteArrayInputStream.close();
          entries.put(str1, localObject1);
        }
        else
        {
          throw new IOException("Unrecognized keystore entry");
        }
      }
      if (paramArrayOfChar != null)
      {
        byte[] arrayOfByte2 = localMessageDigest.digest();
        byte[] arrayOfByte3 = new byte[arrayOfByte2.length];
        localDataInputStream.readFully(arrayOfByte3);
        for (int i1 = 0; i1 < arrayOfByte2.length; i1++) {
          if (arrayOfByte2[i1] != arrayOfByte3[i1])
          {
            localObject1 = new UnrecoverableKeyException("Password verification failed");
            throw ((IOException)new IOException("Keystore was tampered with, or password was incorrect").initCause((Throwable)localObject1));
          }
        }
      }
    }
  }
  
  private MessageDigest getPreKeyedHash(char[] paramArrayOfChar)
    throws NoSuchAlgorithmException, UnsupportedEncodingException
  {
    MessageDigest localMessageDigest = MessageDigest.getInstance("SHA");
    byte[] arrayOfByte = new byte[paramArrayOfChar.length * 2];
    int i = 0;
    int j = 0;
    while (i < paramArrayOfChar.length)
    {
      arrayOfByte[(j++)] = ((byte)(paramArrayOfChar[i] >> '\b'));
      arrayOfByte[(j++)] = ((byte)paramArrayOfChar[i]);
      i++;
    }
    localMessageDigest.update(arrayOfByte);
    for (i = 0; i < arrayOfByte.length; i++) {
      arrayOfByte[i] = 0;
    }
    localMessageDigest.update("Mighty Aphrodite".getBytes("UTF8"));
    return localMessageDigest;
  }
  
  public static final class CaseExactJKS
    extends JavaKeyStore
  {
    public CaseExactJKS() {}
    
    String convertAlias(String paramString)
    {
      return paramString;
    }
  }
  
  public static final class DualFormatJKS
    extends KeyStoreDelegator
  {
    public DualFormatJKS()
    {
      super(JavaKeyStore.JKS.class, "PKCS12", PKCS12KeyStore.class);
    }
  }
  
  public static final class JKS
    extends JavaKeyStore
  {
    public JKS() {}
    
    String convertAlias(String paramString)
    {
      return paramString.toLowerCase(Locale.ENGLISH);
    }
  }
  
  private static class KeyEntry
  {
    Date date;
    byte[] protectedPrivKey;
    Certificate[] chain;
    
    private KeyEntry() {}
  }
  
  private static class TrustedCertEntry
  {
    Date date;
    Certificate cert;
    
    private TrustedCertEntry() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\JavaKeyStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */