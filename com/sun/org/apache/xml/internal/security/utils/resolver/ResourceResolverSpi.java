package com.sun.org.apache.xml.internal.security.utils.resolver;

import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Attr;

public abstract class ResourceResolverSpi
{
  private static Logger log = Logger.getLogger(ResourceResolverSpi.class.getName());
  protected Map<String, String> properties = null;
  @Deprecated
  protected final boolean secureValidation = true;
  
  public ResourceResolverSpi() {}
  
  @Deprecated
  public XMLSignatureInput engineResolve(Attr paramAttr, String paramString)
    throws ResourceResolverException
  {
    throw new UnsupportedOperationException();
  }
  
  public XMLSignatureInput engineResolveURI(ResourceResolverContext paramResourceResolverContext)
    throws ResourceResolverException
  {
    return engineResolve(attr, baseUri);
  }
  
  public void engineSetProperty(String paramString1, String paramString2)
  {
    if (properties == null) {
      properties = new HashMap();
    }
    properties.put(paramString1, paramString2);
  }
  
  public String engineGetProperty(String paramString)
  {
    if (properties == null) {
      return null;
    }
    return (String)properties.get(paramString);
  }
  
  public void engineAddProperies(Map<String, String> paramMap)
  {
    if ((paramMap != null) && (!paramMap.isEmpty()))
    {
      if (properties == null) {
        properties = new HashMap();
      }
      properties.putAll(paramMap);
    }
  }
  
  public boolean engineIsThreadSafe()
  {
    return false;
  }
  
  @Deprecated
  public boolean engineCanResolve(Attr paramAttr, String paramString)
  {
    throw new UnsupportedOperationException();
  }
  
  public boolean engineCanResolveURI(ResourceResolverContext paramResourceResolverContext)
  {
    return engineCanResolve(attr, baseUri);
  }
  
  public String[] engineGetPropertyKeys()
  {
    return new String[0];
  }
  
  public boolean understandsProperty(String paramString)
  {
    String[] arrayOfString = engineGetPropertyKeys();
    if (arrayOfString != null) {
      for (int i = 0; i < arrayOfString.length; i++) {
        if (arrayOfString[i].equals(paramString)) {
          return true;
        }
      }
    }
    return false;
  }
  
  public static String fixURI(String paramString)
  {
    paramString = paramString.replace(File.separatorChar, '/');
    int i;
    int j;
    if (paramString.length() >= 4)
    {
      i = Character.toUpperCase(paramString.charAt(0));
      j = paramString.charAt(1);
      int k = paramString.charAt(2);
      int m = paramString.charAt(3);
      int n = (65 <= i) && (i <= 90) && (j == 58) && (k == 47) && (m != 47) ? 1 : 0;
      if ((n != 0) && (log.isLoggable(Level.FINE))) {
        log.log(Level.FINE, "Found DOS filename: " + paramString);
      }
    }
    if (paramString.length() >= 2)
    {
      i = paramString.charAt(1);
      if (i == 58)
      {
        j = Character.toUpperCase(paramString.charAt(0));
        if ((65 <= j) && (j <= 90)) {
          paramString = "/" + paramString;
        }
      }
    }
    return paramString;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\utils\resolver\ResourceResolverSpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */