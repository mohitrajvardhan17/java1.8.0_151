package java.security.cert;

import java.util.Collection;
import java.util.Collections;

public class CollectionCertStoreParameters
  implements CertStoreParameters
{
  private Collection<?> coll;
  
  public CollectionCertStoreParameters(Collection<?> paramCollection)
  {
    if (paramCollection == null) {
      throw new NullPointerException();
    }
    coll = paramCollection;
  }
  
  public CollectionCertStoreParameters()
  {
    coll = Collections.EMPTY_SET;
  }
  
  public Collection<?> getCollection()
  {
    return coll;
  }
  
  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException.toString(), localCloneNotSupportedException);
    }
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("CollectionCertStoreParameters: [\n");
    localStringBuffer.append("  collection: " + coll + "\n");
    localStringBuffer.append("]");
    return localStringBuffer.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\cert\CollectionCertStoreParameters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */