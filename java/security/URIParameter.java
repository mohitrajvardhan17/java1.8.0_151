package java.security;

import java.net.URI;
import javax.security.auth.login.Configuration.Parameters;

public class URIParameter
  implements Policy.Parameters, Configuration.Parameters
{
  private URI uri;
  
  public URIParameter(URI paramURI)
  {
    if (paramURI == null) {
      throw new NullPointerException("invalid null URI");
    }
    uri = paramURI;
  }
  
  public URI getURI()
  {
    return uri;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\URIParameter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */