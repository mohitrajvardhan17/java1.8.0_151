package com.sun.org.apache.xml.internal.security.keys.storage.implementations;

import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolverException;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolverSpi;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class KeyStoreResolver
  extends StorageResolverSpi
{
  private KeyStore keyStore = null;
  
  public KeyStoreResolver(KeyStore paramKeyStore)
    throws StorageResolverException
  {
    keyStore = paramKeyStore;
    try
    {
      paramKeyStore.aliases();
    }
    catch (KeyStoreException localKeyStoreException)
    {
      throw new StorageResolverException("generic.EmptyMessage", localKeyStoreException);
    }
  }
  
  public Iterator<Certificate> getIterator()
  {
    return new KeyStoreIterator(keyStore);
  }
  
  static class KeyStoreIterator
    implements Iterator<Certificate>
  {
    KeyStore keyStore = null;
    Enumeration<String> aliases = null;
    Certificate nextCert = null;
    
    public KeyStoreIterator(KeyStore paramKeyStore)
    {
      try
      {
        keyStore = paramKeyStore;
        aliases = keyStore.aliases();
      }
      catch (KeyStoreException localKeyStoreException)
      {
        aliases = new Enumeration()
        {
          public boolean hasMoreElements()
          {
            return false;
          }
          
          public String nextElement()
          {
            return null;
          }
        };
      }
    }
    
    public boolean hasNext()
    {
      if (nextCert == null) {
        nextCert = findNextCert();
      }
      return nextCert != null;
    }
    
    public Certificate next()
    {
      if (nextCert == null)
      {
        nextCert = findNextCert();
        if (nextCert == null) {
          throw new NoSuchElementException();
        }
      }
      Certificate localCertificate = nextCert;
      nextCert = null;
      return localCertificate;
    }
    
    public void remove()
    {
      throw new UnsupportedOperationException("Can't remove keys from KeyStore");
    }
    
    private Certificate findNextCert()
    {
      while (aliases.hasMoreElements())
      {
        String str = (String)aliases.nextElement();
        try
        {
          Certificate localCertificate = keyStore.getCertificate(str);
          if (localCertificate != null) {
            return localCertificate;
          }
        }
        catch (KeyStoreException localKeyStoreException)
        {
          return null;
        }
      }
      return null;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\keys\storage\implementations\KeyStoreResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */