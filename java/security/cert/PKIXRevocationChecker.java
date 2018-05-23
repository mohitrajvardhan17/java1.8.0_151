package java.security.cert;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public abstract class PKIXRevocationChecker
  extends PKIXCertPathChecker
{
  private URI ocspResponder;
  private X509Certificate ocspResponderCert;
  private List<Extension> ocspExtensions = Collections.emptyList();
  private Map<X509Certificate, byte[]> ocspResponses = Collections.emptyMap();
  private Set<Option> options = Collections.emptySet();
  
  protected PKIXRevocationChecker() {}
  
  public void setOcspResponder(URI paramURI)
  {
    ocspResponder = paramURI;
  }
  
  public URI getOcspResponder()
  {
    return ocspResponder;
  }
  
  public void setOcspResponderCert(X509Certificate paramX509Certificate)
  {
    ocspResponderCert = paramX509Certificate;
  }
  
  public X509Certificate getOcspResponderCert()
  {
    return ocspResponderCert;
  }
  
  public void setOcspExtensions(List<Extension> paramList)
  {
    ocspExtensions = (paramList == null ? Collections.emptyList() : new ArrayList(paramList));
  }
  
  public List<Extension> getOcspExtensions()
  {
    return Collections.unmodifiableList(ocspExtensions);
  }
  
  public void setOcspResponses(Map<X509Certificate, byte[]> paramMap)
  {
    if (paramMap == null)
    {
      ocspResponses = Collections.emptyMap();
    }
    else
    {
      HashMap localHashMap = new HashMap(paramMap.size());
      Iterator localIterator = paramMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        localHashMap.put(localEntry.getKey(), ((byte[])localEntry.getValue()).clone());
      }
      ocspResponses = localHashMap;
    }
  }
  
  public Map<X509Certificate, byte[]> getOcspResponses()
  {
    HashMap localHashMap = new HashMap(ocspResponses.size());
    Iterator localIterator = ocspResponses.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      localHashMap.put(localEntry.getKey(), ((byte[])localEntry.getValue()).clone());
    }
    return localHashMap;
  }
  
  public void setOptions(Set<Option> paramSet)
  {
    options = (paramSet == null ? Collections.emptySet() : new HashSet(paramSet));
  }
  
  public Set<Option> getOptions()
  {
    return Collections.unmodifiableSet(options);
  }
  
  public abstract List<CertPathValidatorException> getSoftFailExceptions();
  
  public PKIXRevocationChecker clone()
  {
    PKIXRevocationChecker localPKIXRevocationChecker = (PKIXRevocationChecker)super.clone();
    ocspExtensions = new ArrayList(ocspExtensions);
    ocspResponses = new HashMap(ocspResponses);
    Iterator localIterator = ocspResponses.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      byte[] arrayOfByte = (byte[])localEntry.getValue();
      localEntry.setValue(arrayOfByte.clone());
    }
    options = new HashSet(options);
    return localPKIXRevocationChecker;
  }
  
  public static enum Option
  {
    ONLY_END_ENTITY,  PREFER_CRLS,  NO_FALLBACK,  SOFT_FAIL;
    
    private Option() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\cert\PKIXRevocationChecker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */