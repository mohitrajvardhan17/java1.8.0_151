package com.sun.org.apache.xml.internal.security.keys.keyresolver;

import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.DEREncodedKeyValueResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.DSAKeyValueResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.KeyInfoReferenceResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.RSAKeyValueResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.RetrievalMethodResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.X509CertificateResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.X509DigestResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.X509IssuerSerialResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.X509SKIResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.X509SubjectNameResolver;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import com.sun.org.apache.xml.internal.security.utils.JavaUtils;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import org.w3c.dom.Element;

public class KeyResolver
{
  private static Logger log = Logger.getLogger(KeyResolver.class.getName());
  private static List<KeyResolver> resolverVector = new CopyOnWriteArrayList();
  private final KeyResolverSpi resolverSpi;
  
  private KeyResolver(KeyResolverSpi paramKeyResolverSpi)
  {
    resolverSpi = paramKeyResolverSpi;
  }
  
  public static int length()
  {
    return resolverVector.size();
  }
  
  public static final X509Certificate getX509Certificate(Element paramElement, String paramString, StorageResolver paramStorageResolver)
    throws KeyResolverException
  {
    Object localObject1 = resolverVector.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      KeyResolver localKeyResolver = (KeyResolver)((Iterator)localObject1).next();
      if (localKeyResolver == null)
      {
        localObject2 = new Object[] { (paramElement != null) && (paramElement.getNodeType() == 1) ? paramElement.getTagName() : "null" };
        throw new KeyResolverException("utils.resolver.noClass", (Object[])localObject2);
      }
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "check resolvability by class " + localKeyResolver.getClass());
      }
      Object localObject2 = localKeyResolver.resolveX509Certificate(paramElement, paramString, paramStorageResolver);
      if (localObject2 != null) {
        return (X509Certificate)localObject2;
      }
    }
    localObject1 = new Object[] { (paramElement != null) && (paramElement.getNodeType() == 1) ? paramElement.getTagName() : "null" };
    throw new KeyResolverException("utils.resolver.noClass", (Object[])localObject1);
  }
  
  public static final PublicKey getPublicKey(Element paramElement, String paramString, StorageResolver paramStorageResolver)
    throws KeyResolverException
  {
    Object localObject1 = resolverVector.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      KeyResolver localKeyResolver = (KeyResolver)((Iterator)localObject1).next();
      if (localKeyResolver == null)
      {
        localObject2 = new Object[] { (paramElement != null) && (paramElement.getNodeType() == 1) ? paramElement.getTagName() : "null" };
        throw new KeyResolverException("utils.resolver.noClass", (Object[])localObject2);
      }
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "check resolvability by class " + localKeyResolver.getClass());
      }
      Object localObject2 = localKeyResolver.resolvePublicKey(paramElement, paramString, paramStorageResolver);
      if (localObject2 != null) {
        return (PublicKey)localObject2;
      }
    }
    localObject1 = new Object[] { (paramElement != null) && (paramElement.getNodeType() == 1) ? paramElement.getTagName() : "null" };
    throw new KeyResolverException("utils.resolver.noClass", (Object[])localObject1);
  }
  
  public static void register(String paramString, boolean paramBoolean)
    throws ClassNotFoundException, IllegalAccessException, InstantiationException
  {
    JavaUtils.checkRegisterPermission();
    KeyResolverSpi localKeyResolverSpi = (KeyResolverSpi)Class.forName(paramString).newInstance();
    localKeyResolverSpi.setGlobalResolver(paramBoolean);
    register(localKeyResolverSpi, false);
  }
  
  public static void registerAtStart(String paramString, boolean paramBoolean)
  {
    JavaUtils.checkRegisterPermission();
    KeyResolverSpi localKeyResolverSpi = null;
    Object localObject = null;
    try
    {
      localKeyResolverSpi = (KeyResolverSpi)Class.forName(paramString).newInstance();
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      localObject = localClassNotFoundException;
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      localObject = localIllegalAccessException;
    }
    catch (InstantiationException localInstantiationException)
    {
      localObject = localInstantiationException;
    }
    if (localObject != null) {
      throw ((IllegalArgumentException)new IllegalArgumentException("Invalid KeyResolver class name").initCause((Throwable)localObject));
    }
    localKeyResolverSpi.setGlobalResolver(paramBoolean);
    register(localKeyResolverSpi, true);
  }
  
  public static void register(KeyResolverSpi paramKeyResolverSpi, boolean paramBoolean)
  {
    JavaUtils.checkRegisterPermission();
    KeyResolver localKeyResolver = new KeyResolver(paramKeyResolverSpi);
    if (paramBoolean) {
      resolverVector.add(0, localKeyResolver);
    } else {
      resolverVector.add(localKeyResolver);
    }
  }
  
  public static void registerClassNames(List<String> paramList)
    throws ClassNotFoundException, IllegalAccessException, InstantiationException
  {
    JavaUtils.checkRegisterPermission();
    ArrayList localArrayList = new ArrayList(paramList.size());
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      KeyResolverSpi localKeyResolverSpi = (KeyResolverSpi)Class.forName(str).newInstance();
      localKeyResolverSpi.setGlobalResolver(false);
      localArrayList.add(new KeyResolver(localKeyResolverSpi));
    }
    resolverVector.addAll(localArrayList);
  }
  
  public static void registerDefaultResolvers()
  {
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(new KeyResolver(new RSAKeyValueResolver()));
    localArrayList.add(new KeyResolver(new DSAKeyValueResolver()));
    localArrayList.add(new KeyResolver(new X509CertificateResolver()));
    localArrayList.add(new KeyResolver(new X509SKIResolver()));
    localArrayList.add(new KeyResolver(new RetrievalMethodResolver()));
    localArrayList.add(new KeyResolver(new X509SubjectNameResolver()));
    localArrayList.add(new KeyResolver(new X509IssuerSerialResolver()));
    localArrayList.add(new KeyResolver(new DEREncodedKeyValueResolver()));
    localArrayList.add(new KeyResolver(new KeyInfoReferenceResolver()));
    localArrayList.add(new KeyResolver(new X509DigestResolver()));
    resolverVector.addAll(localArrayList);
  }
  
  public PublicKey resolvePublicKey(Element paramElement, String paramString, StorageResolver paramStorageResolver)
    throws KeyResolverException
  {
    return resolverSpi.engineLookupAndResolvePublicKey(paramElement, paramString, paramStorageResolver);
  }
  
  public X509Certificate resolveX509Certificate(Element paramElement, String paramString, StorageResolver paramStorageResolver)
    throws KeyResolverException
  {
    return resolverSpi.engineLookupResolveX509Certificate(paramElement, paramString, paramStorageResolver);
  }
  
  public SecretKey resolveSecretKey(Element paramElement, String paramString, StorageResolver paramStorageResolver)
    throws KeyResolverException
  {
    return resolverSpi.engineLookupAndResolveSecretKey(paramElement, paramString, paramStorageResolver);
  }
  
  public void setProperty(String paramString1, String paramString2)
  {
    resolverSpi.engineSetProperty(paramString1, paramString2);
  }
  
  public String getProperty(String paramString)
  {
    return resolverSpi.engineGetProperty(paramString);
  }
  
  public boolean understandsProperty(String paramString)
  {
    return resolverSpi.understandsProperty(paramString);
  }
  
  public String resolverClassName()
  {
    return resolverSpi.getClass().getName();
  }
  
  public static Iterator<KeyResolverSpi> iterator()
  {
    return new ResolverIterator(resolverVector);
  }
  
  static class ResolverIterator
    implements Iterator<KeyResolverSpi>
  {
    List<KeyResolver> res;
    Iterator<KeyResolver> it;
    
    public ResolverIterator(List<KeyResolver> paramList)
    {
      res = paramList;
      it = res.iterator();
    }
    
    public boolean hasNext()
    {
      return it.hasNext();
    }
    
    public KeyResolverSpi next()
    {
      KeyResolver localKeyResolver = (KeyResolver)it.next();
      if (localKeyResolver == null) {
        throw new RuntimeException("utils.resolver.noClass");
      }
      return resolverSpi;
    }
    
    public void remove()
    {
      throw new UnsupportedOperationException("Can't remove resolvers using the iterator");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\keys\keyresolver\KeyResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */