package com.sun.org.apache.xml.internal.security.keys.storage.implementations;

import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolverSpi;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class SingleCertificateResolver
  extends StorageResolverSpi
{
  private X509Certificate certificate = null;
  
  public SingleCertificateResolver(X509Certificate paramX509Certificate)
  {
    certificate = paramX509Certificate;
  }
  
  public Iterator<Certificate> getIterator()
  {
    return new InternalIterator(certificate);
  }
  
  static class InternalIterator
    implements Iterator<Certificate>
  {
    boolean alreadyReturned = false;
    X509Certificate certificate = null;
    
    public InternalIterator(X509Certificate paramX509Certificate)
    {
      certificate = paramX509Certificate;
    }
    
    public boolean hasNext()
    {
      return !alreadyReturned;
    }
    
    public Certificate next()
    {
      if (alreadyReturned) {
        throw new NoSuchElementException();
      }
      alreadyReturned = true;
      return certificate;
    }
    
    public void remove()
    {
      throw new UnsupportedOperationException("Can't remove keys from KeyStore");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\keys\storage\implementations\SingleCertificateResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */