package com.sun.org.apache.xml.internal.security.utils.resolver;

import org.w3c.dom.Attr;

public class ResourceResolverContext
{
  public final String uriToResolve;
  public final boolean secureValidation;
  public final String baseUri;
  public final Attr attr;
  
  public ResourceResolverContext(Attr paramAttr, String paramString, boolean paramBoolean)
  {
    attr = paramAttr;
    baseUri = paramString;
    secureValidation = paramBoolean;
    uriToResolve = (paramAttr != null ? paramAttr.getValue() : null);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\utils\resolver\ResourceResolverContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */