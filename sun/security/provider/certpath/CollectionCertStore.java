package sun.security.provider.certpath;

import java.security.InvalidAlgorithmParameterException;
import java.security.cert.CRL;
import java.security.cert.CRLSelector;
import java.security.cert.CertSelector;
import java.security.cert.CertStoreException;
import java.security.cert.CertStoreParameters;
import java.security.cert.CertStoreSpi;
import java.security.cert.Certificate;
import java.security.cert.CollectionCertStoreParameters;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;

public class CollectionCertStore
  extends CertStoreSpi
{
  private Collection<?> coll;
  
  public CollectionCertStore(CertStoreParameters paramCertStoreParameters)
    throws InvalidAlgorithmParameterException
  {
    super(paramCertStoreParameters);
    if (!(paramCertStoreParameters instanceof CollectionCertStoreParameters)) {
      throw new InvalidAlgorithmParameterException("parameters must be CollectionCertStoreParameters");
    }
    coll = ((CollectionCertStoreParameters)paramCertStoreParameters).getCollection();
  }
  
  public Collection<Certificate> engineGetCertificates(CertSelector paramCertSelector)
    throws CertStoreException
  {
    if (coll == null) {
      throw new CertStoreException("Collection is null");
    }
    int i = 0;
    while (i < 10) {
      try
      {
        HashSet localHashSet = new HashSet();
        Iterator localIterator;
        Object localObject;
        if (paramCertSelector != null)
        {
          localIterator = coll.iterator();
          while (localIterator.hasNext())
          {
            localObject = localIterator.next();
            if (((localObject instanceof Certificate)) && (paramCertSelector.match((Certificate)localObject))) {
              localHashSet.add((Certificate)localObject);
            }
          }
        }
        else
        {
          localIterator = coll.iterator();
          while (localIterator.hasNext())
          {
            localObject = localIterator.next();
            if ((localObject instanceof Certificate)) {
              localHashSet.add((Certificate)localObject);
            }
          }
        }
        return localHashSet;
      }
      catch (ConcurrentModificationException localConcurrentModificationException)
      {
        i++;
      }
    }
    throw new ConcurrentModificationException("Too many ConcurrentModificationExceptions");
  }
  
  public Collection<CRL> engineGetCRLs(CRLSelector paramCRLSelector)
    throws CertStoreException
  {
    if (coll == null) {
      throw new CertStoreException("Collection is null");
    }
    int i = 0;
    while (i < 10) {
      try
      {
        HashSet localHashSet = new HashSet();
        Iterator localIterator;
        Object localObject;
        if (paramCRLSelector != null)
        {
          localIterator = coll.iterator();
          while (localIterator.hasNext())
          {
            localObject = localIterator.next();
            if (((localObject instanceof CRL)) && (paramCRLSelector.match((CRL)localObject))) {
              localHashSet.add((CRL)localObject);
            }
          }
        }
        else
        {
          localIterator = coll.iterator();
          while (localIterator.hasNext())
          {
            localObject = localIterator.next();
            if ((localObject instanceof CRL)) {
              localHashSet.add((CRL)localObject);
            }
          }
        }
        return localHashSet;
      }
      catch (ConcurrentModificationException localConcurrentModificationException)
      {
        i++;
      }
    }
    throw new ConcurrentModificationException("Too many ConcurrentModificationExceptions");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\certpath\CollectionCertStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */