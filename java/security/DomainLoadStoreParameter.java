package java.security;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class DomainLoadStoreParameter
  implements KeyStore.LoadStoreParameter
{
  private final URI configuration;
  private final Map<String, KeyStore.ProtectionParameter> protectionParams;
  
  public DomainLoadStoreParameter(URI paramURI, Map<String, KeyStore.ProtectionParameter> paramMap)
  {
    if ((paramURI == null) || (paramMap == null)) {
      throw new NullPointerException("invalid null input");
    }
    configuration = paramURI;
    protectionParams = Collections.unmodifiableMap(new HashMap(paramMap));
  }
  
  public URI getConfiguration()
  {
    return configuration;
  }
  
  public Map<String, KeyStore.ProtectionParameter> getProtectionParams()
  {
    return protectionParams;
  }
  
  public KeyStore.ProtectionParameter getProtectionParameter()
  {
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\DomainLoadStoreParameter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */