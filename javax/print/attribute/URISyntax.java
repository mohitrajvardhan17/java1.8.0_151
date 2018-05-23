package javax.print.attribute;

import java.io.Serializable;
import java.net.URI;

public abstract class URISyntax
  implements Serializable, Cloneable
{
  private static final long serialVersionUID = -7842661210486401678L;
  private URI uri;
  
  protected URISyntax(URI paramURI)
  {
    uri = verify(paramURI);
  }
  
  private static URI verify(URI paramURI)
  {
    if (paramURI == null) {
      throw new NullPointerException(" uri is null");
    }
    return paramURI;
  }
  
  public URI getURI()
  {
    return uri;
  }
  
  public int hashCode()
  {
    return uri.hashCode();
  }
  
  public boolean equals(Object paramObject)
  {
    return (paramObject != null) && ((paramObject instanceof URISyntax)) && (uri.equals(uri));
  }
  
  public String toString()
  {
    return uri.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\attribute\URISyntax.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */