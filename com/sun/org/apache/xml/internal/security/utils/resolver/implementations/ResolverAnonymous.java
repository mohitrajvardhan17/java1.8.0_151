package com.sun.org.apache.xml.internal.security.utils.resolver.implementations;

import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverContext;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverSpi;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ResolverAnonymous
  extends ResourceResolverSpi
{
  private InputStream inStream = null;
  
  public boolean engineIsThreadSafe()
  {
    return true;
  }
  
  public ResolverAnonymous(String paramString)
    throws FileNotFoundException, IOException
  {
    inStream = new FileInputStream(paramString);
  }
  
  public ResolverAnonymous(InputStream paramInputStream)
  {
    inStream = paramInputStream;
  }
  
  public XMLSignatureInput engineResolveURI(ResourceResolverContext paramResourceResolverContext)
  {
    return new XMLSignatureInput(inStream);
  }
  
  public boolean engineCanResolveURI(ResourceResolverContext paramResourceResolverContext)
  {
    return uriToResolve == null;
  }
  
  public String[] engineGetPropertyKeys()
  {
    return new String[0];
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\utils\resolver\implementations\ResolverAnonymous.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */