package com.sun.org.apache.xml.internal.security.utils.resolver;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import org.w3c.dom.Attr;

public class ResourceResolverException
  extends XMLSecurityException
{
  private static final long serialVersionUID = 1L;
  private Attr uri = null;
  private String baseURI = null;
  
  public ResourceResolverException(String paramString1, Attr paramAttr, String paramString2)
  {
    super(paramString1);
    uri = paramAttr;
    baseURI = paramString2;
  }
  
  public ResourceResolverException(String paramString1, Object[] paramArrayOfObject, Attr paramAttr, String paramString2)
  {
    super(paramString1, paramArrayOfObject);
    uri = paramAttr;
    baseURI = paramString2;
  }
  
  public ResourceResolverException(String paramString1, Exception paramException, Attr paramAttr, String paramString2)
  {
    super(paramString1, paramException);
    uri = paramAttr;
    baseURI = paramString2;
  }
  
  public ResourceResolverException(String paramString1, Object[] paramArrayOfObject, Exception paramException, Attr paramAttr, String paramString2)
  {
    super(paramString1, paramArrayOfObject, paramException);
    uri = paramAttr;
    baseURI = paramString2;
  }
  
  public void setURI(Attr paramAttr)
  {
    uri = paramAttr;
  }
  
  public Attr getURI()
  {
    return uri;
  }
  
  public void setbaseURI(String paramString)
  {
    baseURI = paramString;
  }
  
  public String getbaseURI()
  {
    return baseURI;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\utils\resolver\ResourceResolverException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */