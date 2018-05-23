package com.sun.org.apache.xml.internal.security.keys.storage;

import com.sun.org.apache.xml.internal.security.keys.storage.implementations.KeyStoreResolver;
import com.sun.org.apache.xml.internal.security.keys.storage.implementations.SingleCertificateResolver;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StorageResolver
{
  private static Logger log = Logger.getLogger(StorageResolver.class.getName());
  private List<StorageResolverSpi> storageResolvers = null;
  
  public StorageResolver() {}
  
  public StorageResolver(StorageResolverSpi paramStorageResolverSpi)
  {
    add(paramStorageResolverSpi);
  }
  
  public void add(StorageResolverSpi paramStorageResolverSpi)
  {
    if (storageResolvers == null) {
      storageResolvers = new ArrayList();
    }
    storageResolvers.add(paramStorageResolverSpi);
  }
  
  public StorageResolver(KeyStore paramKeyStore)
  {
    add(paramKeyStore);
  }
  
  public void add(KeyStore paramKeyStore)
  {
    try
    {
      add(new KeyStoreResolver(paramKeyStore));
    }
    catch (StorageResolverException localStorageResolverException)
    {
      log.log(Level.SEVERE, "Could not add KeyStore because of: ", localStorageResolverException);
    }
  }
  
  public StorageResolver(X509Certificate paramX509Certificate)
  {
    add(paramX509Certificate);
  }
  
  public void add(X509Certificate paramX509Certificate)
  {
    add(new SingleCertificateResolver(paramX509Certificate));
  }
  
  public Iterator<Certificate> getIterator()
  {
    return new StorageResolverIterator(storageResolvers.iterator());
  }
  
  static class StorageResolverIterator
    implements Iterator<Certificate>
  {
    Iterator<StorageResolverSpi> resolvers = null;
    Iterator<Certificate> currentResolver = null;
    
    public StorageResolverIterator(Iterator<StorageResolverSpi> paramIterator)
    {
      resolvers = paramIterator;
      currentResolver = findNextResolver();
    }
    
    public boolean hasNext()
    {
      if (currentResolver == null) {
        return false;
      }
      if (currentResolver.hasNext()) {
        return true;
      }
      currentResolver = findNextResolver();
      return currentResolver != null;
    }
    
    public Certificate next()
    {
      if (hasNext()) {
        return (Certificate)currentResolver.next();
      }
      throw new NoSuchElementException();
    }
    
    public void remove()
    {
      throw new UnsupportedOperationException("Can't remove keys from KeyStore");
    }
    
    private Iterator<Certificate> findNextResolver()
    {
      while (resolvers.hasNext())
      {
        StorageResolverSpi localStorageResolverSpi = (StorageResolverSpi)resolvers.next();
        Iterator localIterator = localStorageResolverSpi.getIterator();
        if (localIterator.hasNext()) {
          return localIterator;
        }
      }
      return null;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\keys\storage\StorageResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */