package sun.security.provider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.DomainLoadStoreParameter;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStore.Builder;
import java.security.KeyStore.LoadStoreParameter;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.ProtectionParameter;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import sun.security.util.PolicyUtil;

abstract class DomainKeyStore
  extends KeyStoreSpi
{
  private static final String ENTRY_NAME_SEPARATOR = "entrynameseparator";
  private static final String KEYSTORE_PROVIDER_NAME = "keystoreprovidername";
  private static final String KEYSTORE_TYPE = "keystoretype";
  private static final String KEYSTORE_URI = "keystoreuri";
  private static final String KEYSTORE_PASSWORD_ENV = "keystorepasswordenv";
  private static final String REGEX_META = ".$|()[{^?*+\\";
  private static final String DEFAULT_STREAM_PREFIX = "iostream";
  private int streamCounter = 1;
  private String entryNameSeparator = " ";
  private String entryNameSeparatorRegEx = " ";
  private static final String DEFAULT_KEYSTORE_TYPE = ;
  private final Map<String, KeyStore> keystores = new HashMap();
  
  DomainKeyStore() {}
  
  abstract String convertAlias(String paramString);
  
  public Key engineGetKey(String paramString, char[] paramArrayOfChar)
    throws NoSuchAlgorithmException, UnrecoverableKeyException
  {
    AbstractMap.SimpleEntry localSimpleEntry = getKeystoresForReading(paramString);
    Key localKey = null;
    try
    {
      String str = (String)localSimpleEntry.getKey();
      Iterator localIterator = ((Collection)localSimpleEntry.getValue()).iterator();
      while (localIterator.hasNext())
      {
        KeyStore localKeyStore = (KeyStore)localIterator.next();
        localKey = localKeyStore.getKey(str, paramArrayOfChar);
        if (localKey != null) {
          break;
        }
      }
    }
    catch (KeyStoreException localKeyStoreException)
    {
      throw new IllegalStateException(localKeyStoreException);
    }
    return localKey;
  }
  
  public Certificate[] engineGetCertificateChain(String paramString)
  {
    AbstractMap.SimpleEntry localSimpleEntry = getKeystoresForReading(paramString);
    Certificate[] arrayOfCertificate = null;
    try
    {
      String str = (String)localSimpleEntry.getKey();
      Iterator localIterator = ((Collection)localSimpleEntry.getValue()).iterator();
      while (localIterator.hasNext())
      {
        KeyStore localKeyStore = (KeyStore)localIterator.next();
        arrayOfCertificate = localKeyStore.getCertificateChain(str);
        if (arrayOfCertificate != null) {
          break;
        }
      }
    }
    catch (KeyStoreException localKeyStoreException)
    {
      throw new IllegalStateException(localKeyStoreException);
    }
    return arrayOfCertificate;
  }
  
  public Certificate engineGetCertificate(String paramString)
  {
    AbstractMap.SimpleEntry localSimpleEntry = getKeystoresForReading(paramString);
    Certificate localCertificate = null;
    try
    {
      String str = (String)localSimpleEntry.getKey();
      Iterator localIterator = ((Collection)localSimpleEntry.getValue()).iterator();
      while (localIterator.hasNext())
      {
        KeyStore localKeyStore = (KeyStore)localIterator.next();
        localCertificate = localKeyStore.getCertificate(str);
        if (localCertificate != null) {
          break;
        }
      }
    }
    catch (KeyStoreException localKeyStoreException)
    {
      throw new IllegalStateException(localKeyStoreException);
    }
    return localCertificate;
  }
  
  public Date engineGetCreationDate(String paramString)
  {
    AbstractMap.SimpleEntry localSimpleEntry = getKeystoresForReading(paramString);
    Date localDate = null;
    try
    {
      String str = (String)localSimpleEntry.getKey();
      Iterator localIterator = ((Collection)localSimpleEntry.getValue()).iterator();
      while (localIterator.hasNext())
      {
        KeyStore localKeyStore = (KeyStore)localIterator.next();
        localDate = localKeyStore.getCreationDate(str);
        if (localDate != null) {
          break;
        }
      }
    }
    catch (KeyStoreException localKeyStoreException)
    {
      throw new IllegalStateException(localKeyStoreException);
    }
    return localDate;
  }
  
  public void engineSetKeyEntry(String paramString, Key paramKey, char[] paramArrayOfChar, Certificate[] paramArrayOfCertificate)
    throws KeyStoreException
  {
    AbstractMap.SimpleEntry localSimpleEntry = getKeystoreForWriting(paramString);
    if (localSimpleEntry == null) {
      throw new KeyStoreException("Error setting key entry for '" + paramString + "'");
    }
    String str = (String)localSimpleEntry.getKey();
    Map.Entry localEntry = (Map.Entry)localSimpleEntry.getValue();
    ((KeyStore)localEntry.getValue()).setKeyEntry(str, paramKey, paramArrayOfChar, paramArrayOfCertificate);
  }
  
  public void engineSetKeyEntry(String paramString, byte[] paramArrayOfByte, Certificate[] paramArrayOfCertificate)
    throws KeyStoreException
  {
    AbstractMap.SimpleEntry localSimpleEntry = getKeystoreForWriting(paramString);
    if (localSimpleEntry == null) {
      throw new KeyStoreException("Error setting protected key entry for '" + paramString + "'");
    }
    String str = (String)localSimpleEntry.getKey();
    Map.Entry localEntry = (Map.Entry)localSimpleEntry.getValue();
    ((KeyStore)localEntry.getValue()).setKeyEntry(str, paramArrayOfByte, paramArrayOfCertificate);
  }
  
  public void engineSetCertificateEntry(String paramString, Certificate paramCertificate)
    throws KeyStoreException
  {
    AbstractMap.SimpleEntry localSimpleEntry = getKeystoreForWriting(paramString);
    if (localSimpleEntry == null) {
      throw new KeyStoreException("Error setting certificate entry for '" + paramString + "'");
    }
    String str = (String)localSimpleEntry.getKey();
    Map.Entry localEntry = (Map.Entry)localSimpleEntry.getValue();
    ((KeyStore)localEntry.getValue()).setCertificateEntry(str, paramCertificate);
  }
  
  public void engineDeleteEntry(String paramString)
    throws KeyStoreException
  {
    AbstractMap.SimpleEntry localSimpleEntry = getKeystoreForWriting(paramString);
    if (localSimpleEntry == null) {
      throw new KeyStoreException("Error deleting entry for '" + paramString + "'");
    }
    String str = (String)localSimpleEntry.getKey();
    Map.Entry localEntry = (Map.Entry)localSimpleEntry.getValue();
    ((KeyStore)localEntry.getValue()).deleteEntry(str);
  }
  
  public Enumeration<String> engineAliases()
  {
    final Iterator localIterator = keystores.entrySet().iterator();
    new Enumeration()
    {
      private int index = 0;
      private Map.Entry<String, KeyStore> keystoresEntry = null;
      private String prefix = null;
      private Enumeration<String> aliases = null;
      
      public boolean hasMoreElements()
      {
        try
        {
          if (aliases == null) {
            if (localIterator.hasNext())
            {
              keystoresEntry = ((Map.Entry)localIterator.next());
              prefix = ((String)keystoresEntry.getKey() + entryNameSeparator);
              aliases = ((KeyStore)keystoresEntry.getValue()).aliases();
            }
            else
            {
              return false;
            }
          }
          if (aliases.hasMoreElements()) {
            return true;
          }
          if (localIterator.hasNext())
          {
            keystoresEntry = ((Map.Entry)localIterator.next());
            prefix = ((String)keystoresEntry.getKey() + entryNameSeparator);
            aliases = ((KeyStore)keystoresEntry.getValue()).aliases();
          }
          else
          {
            return false;
          }
        }
        catch (KeyStoreException localKeyStoreException)
        {
          return false;
        }
        return aliases.hasMoreElements();
      }
      
      public String nextElement()
      {
        if (hasMoreElements()) {
          return prefix + (String)aliases.nextElement();
        }
        throw new NoSuchElementException();
      }
    };
  }
  
  public boolean engineContainsAlias(String paramString)
  {
    AbstractMap.SimpleEntry localSimpleEntry = getKeystoresForReading(paramString);
    try
    {
      String str = (String)localSimpleEntry.getKey();
      Iterator localIterator = ((Collection)localSimpleEntry.getValue()).iterator();
      while (localIterator.hasNext())
      {
        KeyStore localKeyStore = (KeyStore)localIterator.next();
        if (localKeyStore.containsAlias(str)) {
          return true;
        }
      }
    }
    catch (KeyStoreException localKeyStoreException)
    {
      throw new IllegalStateException(localKeyStoreException);
    }
    return false;
  }
  
  public int engineSize()
  {
    int i = 0;
    try
    {
      Iterator localIterator = keystores.values().iterator();
      while (localIterator.hasNext())
      {
        KeyStore localKeyStore = (KeyStore)localIterator.next();
        i += localKeyStore.size();
      }
    }
    catch (KeyStoreException localKeyStoreException)
    {
      throw new IllegalStateException(localKeyStoreException);
    }
    return i;
  }
  
  public boolean engineIsKeyEntry(String paramString)
  {
    AbstractMap.SimpleEntry localSimpleEntry = getKeystoresForReading(paramString);
    try
    {
      String str = (String)localSimpleEntry.getKey();
      Iterator localIterator = ((Collection)localSimpleEntry.getValue()).iterator();
      while (localIterator.hasNext())
      {
        KeyStore localKeyStore = (KeyStore)localIterator.next();
        if (localKeyStore.isKeyEntry(str)) {
          return true;
        }
      }
    }
    catch (KeyStoreException localKeyStoreException)
    {
      throw new IllegalStateException(localKeyStoreException);
    }
    return false;
  }
  
  public boolean engineIsCertificateEntry(String paramString)
  {
    AbstractMap.SimpleEntry localSimpleEntry = getKeystoresForReading(paramString);
    try
    {
      String str = (String)localSimpleEntry.getKey();
      Iterator localIterator = ((Collection)localSimpleEntry.getValue()).iterator();
      while (localIterator.hasNext())
      {
        KeyStore localKeyStore = (KeyStore)localIterator.next();
        if (localKeyStore.isCertificateEntry(str)) {
          return true;
        }
      }
    }
    catch (KeyStoreException localKeyStoreException)
    {
      throw new IllegalStateException(localKeyStoreException);
    }
    return false;
  }
  
  private AbstractMap.SimpleEntry<String, Collection<KeyStore>> getKeystoresForReading(String paramString)
  {
    String[] arrayOfString = paramString.split(entryNameSeparatorRegEx, 2);
    if (arrayOfString.length == 2)
    {
      KeyStore localKeyStore = (KeyStore)keystores.get(arrayOfString[0]);
      if (localKeyStore != null) {
        return new AbstractMap.SimpleEntry(arrayOfString[1], Collections.singleton(localKeyStore));
      }
    }
    else if (arrayOfString.length == 1)
    {
      return new AbstractMap.SimpleEntry(paramString, keystores.values());
    }
    return new AbstractMap.SimpleEntry("", Collections.emptyList());
  }
  
  private AbstractMap.SimpleEntry<String, AbstractMap.SimpleEntry<String, KeyStore>> getKeystoreForWriting(String paramString)
  {
    String[] arrayOfString = paramString.split(entryNameSeparator, 2);
    if (arrayOfString.length == 2)
    {
      KeyStore localKeyStore = (KeyStore)keystores.get(arrayOfString[0]);
      if (localKeyStore != null) {
        return new AbstractMap.SimpleEntry(arrayOfString[1], new AbstractMap.SimpleEntry(arrayOfString[0], localKeyStore));
      }
    }
    return null;
  }
  
  public String engineGetCertificateAlias(Certificate paramCertificate)
  {
    try
    {
      String str = null;
      Iterator localIterator = keystores.values().iterator();
      while (localIterator.hasNext())
      {
        KeyStore localKeyStore = (KeyStore)localIterator.next();
        if ((str = localKeyStore.getCertificateAlias(paramCertificate)) != null) {
          break;
        }
      }
      return str;
    }
    catch (KeyStoreException localKeyStoreException)
    {
      throw new IllegalStateException(localKeyStoreException);
    }
  }
  
  public void engineStore(OutputStream paramOutputStream, char[] paramArrayOfChar)
    throws IOException, NoSuchAlgorithmException, CertificateException
  {
    try
    {
      if (keystores.size() == 1)
      {
        ((KeyStore)keystores.values().iterator().next()).store(paramOutputStream, paramArrayOfChar);
        return;
      }
    }
    catch (KeyStoreException localKeyStoreException)
    {
      throw new IllegalStateException(localKeyStoreException);
    }
    throw new UnsupportedOperationException("This keystore must be stored using a DomainLoadStoreParameter");
  }
  
  public void engineStore(KeyStore.LoadStoreParameter paramLoadStoreParameter)
    throws IOException, NoSuchAlgorithmException, CertificateException
  {
    if ((paramLoadStoreParameter instanceof DomainLoadStoreParameter))
    {
      DomainLoadStoreParameter localDomainLoadStoreParameter = (DomainLoadStoreParameter)paramLoadStoreParameter;
      List localList = getBuilders(localDomainLoadStoreParameter.getConfiguration(), localDomainLoadStoreParameter.getProtectionParams());
      Iterator localIterator = localList.iterator();
      while (localIterator.hasNext())
      {
        KeyStoreBuilderComponents localKeyStoreBuilderComponents = (KeyStoreBuilderComponents)localIterator.next();
        try
        {
          KeyStore.ProtectionParameter localProtectionParameter = protection;
          if (!(localProtectionParameter instanceof KeyStore.PasswordProtection)) {
            throw new KeyStoreException(new IllegalArgumentException("ProtectionParameter must be a KeyStore.PasswordProtection"));
          }
          char[] arrayOfChar = ((KeyStore.PasswordProtection)protection).getPassword();
          KeyStore localKeyStore = (KeyStore)keystores.get(name);
          FileOutputStream localFileOutputStream = new FileOutputStream(file);
          Object localObject1 = null;
          try
          {
            localKeyStore.store(localFileOutputStream, arrayOfChar);
          }
          catch (Throwable localThrowable2)
          {
            localObject1 = localThrowable2;
            throw localThrowable2;
          }
          finally
          {
            if (localFileOutputStream != null) {
              if (localObject1 != null) {
                try
                {
                  localFileOutputStream.close();
                }
                catch (Throwable localThrowable3)
                {
                  ((Throwable)localObject1).addSuppressed(localThrowable3);
                }
              } else {
                localFileOutputStream.close();
              }
            }
          }
        }
        catch (KeyStoreException localKeyStoreException)
        {
          throw new IOException(localKeyStoreException);
        }
      }
    }
    else
    {
      throw new UnsupportedOperationException("This keystore must be stored using a DomainLoadStoreParameter");
    }
  }
  
  public void engineLoad(InputStream paramInputStream, char[] paramArrayOfChar)
    throws IOException, NoSuchAlgorithmException, CertificateException
  {
    try
    {
      KeyStore localKeyStore = null;
      try
      {
        localKeyStore = KeyStore.getInstance("JKS");
        localKeyStore.load(paramInputStream, paramArrayOfChar);
      }
      catch (Exception localException2)
      {
        if (!"JKS".equalsIgnoreCase(DEFAULT_KEYSTORE_TYPE))
        {
          localKeyStore = KeyStore.getInstance(DEFAULT_KEYSTORE_TYPE);
          localKeyStore.load(paramInputStream, paramArrayOfChar);
        }
        else
        {
          throw localException2;
        }
      }
      String str = "iostream" + streamCounter++;
      keystores.put(str, localKeyStore);
    }
    catch (Exception localException1)
    {
      throw new UnsupportedOperationException("This keystore must be loaded using a DomainLoadStoreParameter");
    }
  }
  
  public void engineLoad(KeyStore.LoadStoreParameter paramLoadStoreParameter)
    throws IOException, NoSuchAlgorithmException, CertificateException
  {
    if ((paramLoadStoreParameter instanceof DomainLoadStoreParameter))
    {
      DomainLoadStoreParameter localDomainLoadStoreParameter = (DomainLoadStoreParameter)paramLoadStoreParameter;
      List localList = getBuilders(localDomainLoadStoreParameter.getConfiguration(), localDomainLoadStoreParameter.getProtectionParams());
      Iterator localIterator = localList.iterator();
      while (localIterator.hasNext())
      {
        KeyStoreBuilderComponents localKeyStoreBuilderComponents = (KeyStoreBuilderComponents)localIterator.next();
        try
        {
          if (file != null) {
            keystores.put(name, KeyStore.Builder.newInstance(type, provider, file, protection).getKeyStore());
          } else {
            keystores.put(name, KeyStore.Builder.newInstance(type, provider, protection).getKeyStore());
          }
        }
        catch (KeyStoreException localKeyStoreException)
        {
          throw new IOException(localKeyStoreException);
        }
      }
    }
    else
    {
      throw new UnsupportedOperationException("This keystore must be loaded using a DomainLoadStoreParameter");
    }
  }
  
  private List<KeyStoreBuilderComponents> getBuilders(URI paramURI, Map<String, KeyStore.ProtectionParameter> paramMap)
    throws IOException
  {
    PolicyParser localPolicyParser = new PolicyParser(true);
    Collection localCollection1 = null;
    ArrayList localArrayList = new ArrayList();
    String str1 = paramURI.getFragment();
    Object localObject1;
    try
    {
      InputStreamReader localInputStreamReader = new InputStreamReader(PolicyUtil.getInputStream(paramURI.toURL()), "UTF-8");
      localObject1 = null;
      try
      {
        localPolicyParser.read(localInputStreamReader);
        localCollection1 = localPolicyParser.getDomainEntries();
      }
      catch (Throwable localThrowable2)
      {
        localObject1 = localThrowable2;
        throw localThrowable2;
      }
      finally
      {
        if (localInputStreamReader != null) {
          if (localObject1 != null) {
            try
            {
              localInputStreamReader.close();
            }
            catch (Throwable localThrowable3)
            {
              ((Throwable)localObject1).addSuppressed(localThrowable3);
            }
          } else {
            localInputStreamReader.close();
          }
        }
      }
    }
    catch (MalformedURLException localMalformedURLException)
    {
      throw new IOException(localMalformedURLException);
    }
    catch (PolicyParser.ParsingException localParsingException)
    {
      throw new IOException(localParsingException);
    }
    Iterator localIterator = localCollection1.iterator();
    while (localIterator.hasNext())
    {
      localObject1 = (PolicyParser.DomainEntry)localIterator.next();
      Map localMap = ((PolicyParser.DomainEntry)localObject1).getProperties();
      if ((str1 == null) || (str1.equalsIgnoreCase(((PolicyParser.DomainEntry)localObject1).getName())))
      {
        if (localMap.containsKey("entrynameseparator"))
        {
          entryNameSeparator = ((String)localMap.get("entrynameseparator"));
          char c = '\000';
          localObject3 = new StringBuilder();
          for (int i = 0; i < entryNameSeparator.length(); i++)
          {
            c = entryNameSeparator.charAt(i);
            if (".$|()[{^?*+\\".indexOf(c) != -1) {
              ((StringBuilder)localObject3).append('\\');
            }
            ((StringBuilder)localObject3).append(c);
          }
          entryNameSeparatorRegEx = ((StringBuilder)localObject3).toString();
        }
        Collection localCollection2 = ((PolicyParser.DomainEntry)localObject1).getEntries();
        Object localObject3 = localCollection2.iterator();
        while (((Iterator)localObject3).hasNext())
        {
          PolicyParser.KeyStoreEntry localKeyStoreEntry = (PolicyParser.KeyStoreEntry)((Iterator)localObject3).next();
          String str2 = localKeyStoreEntry.getName();
          HashMap localHashMap = new HashMap(localMap);
          localHashMap.putAll(localKeyStoreEntry.getProperties());
          String str3 = DEFAULT_KEYSTORE_TYPE;
          if (localHashMap.containsKey("keystoretype")) {
            str3 = (String)localHashMap.get("keystoretype");
          }
          Provider localProvider = null;
          if (localHashMap.containsKey("keystoreprovidername"))
          {
            localObject4 = (String)localHashMap.get("keystoreprovidername");
            localProvider = Security.getProvider((String)localObject4);
            if (localProvider == null) {
              throw new IOException("Error locating JCE provider: " + (String)localObject4);
            }
          }
          Object localObject4 = null;
          if (localHashMap.containsKey("keystoreuri"))
          {
            localObject5 = (String)localHashMap.get("keystoreuri");
            try
            {
              if (((String)localObject5).startsWith("file://")) {
                localObject4 = new File(new URI((String)localObject5));
              } else {
                localObject4 = new File((String)localObject5);
              }
            }
            catch (URISyntaxException|IllegalArgumentException localURISyntaxException)
            {
              throw new IOException("Error processing keystore property: keystoreURI=\"" + (String)localObject5 + "\"", localURISyntaxException);
            }
          }
          Object localObject5 = null;
          if (paramMap.containsKey(str2))
          {
            localObject5 = (KeyStore.ProtectionParameter)paramMap.get(str2);
          }
          else if (localHashMap.containsKey("keystorepasswordenv"))
          {
            String str4 = (String)localHashMap.get("keystorepasswordenv");
            String str5 = System.getenv(str4);
            if (str5 != null) {
              localObject5 = new KeyStore.PasswordProtection(str5.toCharArray());
            } else {
              throw new IOException("Error processing keystore property: keystorePasswordEnv=\"" + str4 + "\"");
            }
          }
          else
          {
            localObject5 = new KeyStore.PasswordProtection(null);
          }
          localArrayList.add(new KeyStoreBuilderComponents(str2, str3, localProvider, (File)localObject4, (KeyStore.ProtectionParameter)localObject5));
        }
      }
    }
    if (localArrayList.isEmpty()) {
      throw new IOException("Error locating domain configuration data for: " + paramURI);
    }
    return localArrayList;
  }
  
  public static final class DKS
    extends DomainKeyStore
  {
    public DKS() {}
    
    String convertAlias(String paramString)
    {
      return paramString.toLowerCase(Locale.ENGLISH);
    }
  }
  
  class KeyStoreBuilderComponents
  {
    String name;
    String type;
    Provider provider;
    File file;
    KeyStore.ProtectionParameter protection;
    
    KeyStoreBuilderComponents(String paramString1, String paramString2, Provider paramProvider, File paramFile, KeyStore.ProtectionParameter paramProtectionParameter)
    {
      name = paramString1;
      type = paramString2;
      provider = paramProvider;
      file = paramFile;
      protection = paramProtectionParameter;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\DomainKeyStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */