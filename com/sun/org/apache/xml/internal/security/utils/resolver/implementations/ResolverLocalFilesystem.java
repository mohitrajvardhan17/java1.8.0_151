package com.sun.org.apache.xml.internal.security.utils.resolver.implementations;

import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverContext;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverException;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverSpi;
import java.io.FileInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResolverLocalFilesystem
  extends ResourceResolverSpi
{
  private static final int FILE_URI_LENGTH = "file:/".length();
  private static Logger log = Logger.getLogger(ResolverLocalFilesystem.class.getName());
  
  public ResolverLocalFilesystem() {}
  
  public boolean engineIsThreadSafe()
  {
    return true;
  }
  
  public XMLSignatureInput engineResolveURI(ResourceResolverContext paramResourceResolverContext)
    throws ResourceResolverException
  {
    try
    {
      URI localURI = getNewURI(uriToResolve, baseUri);
      String str = translateUriToFilename(localURI.toString());
      FileInputStream localFileInputStream = new FileInputStream(str);
      XMLSignatureInput localXMLSignatureInput = new XMLSignatureInput(localFileInputStream);
      localXMLSignatureInput.setSourceURI(localURI.toString());
      return localXMLSignatureInput;
    }
    catch (Exception localException)
    {
      throw new ResourceResolverException("generic.EmptyMessage", localException, attr, baseUri);
    }
  }
  
  private static String translateUriToFilename(String paramString)
  {
    String str = paramString.substring(FILE_URI_LENGTH);
    if (str.indexOf("%20") > -1)
    {
      int i = 0;
      int j = 0;
      StringBuilder localStringBuilder = new StringBuilder(str.length());
      do
      {
        j = str.indexOf("%20", i);
        if (j == -1)
        {
          localStringBuilder.append(str.substring(i));
        }
        else
        {
          localStringBuilder.append(str.substring(i, j));
          localStringBuilder.append(' ');
          i = j + 3;
        }
      } while (j != -1);
      str = localStringBuilder.toString();
    }
    if (str.charAt(1) == ':') {
      return str;
    }
    return "/" + str;
  }
  
  public boolean engineCanResolveURI(ResourceResolverContext paramResourceResolverContext)
  {
    if (uriToResolve == null) {
      return false;
    }
    if ((uriToResolve.equals("")) || (uriToResolve.charAt(0) == '#') || (uriToResolve.startsWith("http:"))) {
      return false;
    }
    try
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "I was asked whether I can resolve " + uriToResolve);
      }
      if ((uriToResolve.startsWith("file:")) || (baseUri.startsWith("file:")))
      {
        if (log.isLoggable(Level.FINE)) {
          log.log(Level.FINE, "I state that I can resolve " + uriToResolve);
        }
        return true;
      }
    }
    catch (Exception localException)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, localException.getMessage(), localException);
      }
    }
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "But I can't");
    }
    return false;
  }
  
  private static URI getNewURI(String paramString1, String paramString2)
    throws URISyntaxException
  {
    URI localURI1 = null;
    if ((paramString2 == null) || ("".equals(paramString2))) {
      localURI1 = new URI(paramString1);
    } else {
      localURI1 = new URI(paramString2).resolve(paramString1);
    }
    if (localURI1.getFragment() != null)
    {
      URI localURI2 = new URI(localURI1.getScheme(), localURI1.getSchemeSpecificPart(), null);
      return localURI2;
    }
    return localURI1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\utils\resolver\implementations\ResolverLocalFilesystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */