package com.sun.org.apache.xml.internal.security.utils.resolver.implementations;

import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverContext;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverException;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverSpi;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ResolverXPointer
  extends ResourceResolverSpi
{
  private static Logger log = Logger.getLogger(ResolverXPointer.class.getName());
  private static final String XP = "#xpointer(id(";
  private static final int XP_LENGTH = "#xpointer(id(".length();
  
  public ResolverXPointer() {}
  
  public boolean engineIsThreadSafe()
  {
    return true;
  }
  
  public XMLSignatureInput engineResolveURI(ResourceResolverContext paramResourceResolverContext)
    throws ResourceResolverException
  {
    Object localObject1 = null;
    Document localDocument = attr.getOwnerElement().getOwnerDocument();
    if (isXPointerSlash(uriToResolve))
    {
      localObject1 = localDocument;
    }
    else if (isXPointerId(uriToResolve))
    {
      localObject2 = getXPointerId(uriToResolve);
      localObject1 = localDocument.getElementById((String)localObject2);
      Object localObject3;
      if (secureValidation)
      {
        localObject3 = attr.getOwnerDocument().getDocumentElement();
        if (!XMLUtils.protectAgainstWrappingAttack((Node)localObject3, (String)localObject2))
        {
          Object[] arrayOfObject = { localObject2 };
          throw new ResourceResolverException("signature.Verification.MultipleIDs", arrayOfObject, attr, baseUri);
        }
      }
      if (localObject1 == null)
      {
        localObject3 = new Object[] { localObject2 };
        throw new ResourceResolverException("signature.Verification.MissingID", (Object[])localObject3, attr, baseUri);
      }
    }
    Object localObject2 = new XMLSignatureInput((Node)localObject1);
    ((XMLSignatureInput)localObject2).setMIMEType("text/xml");
    if ((baseUri != null) && (baseUri.length() > 0)) {
      ((XMLSignatureInput)localObject2).setSourceURI(baseUri.concat(uriToResolve));
    } else {
      ((XMLSignatureInput)localObject2).setSourceURI(uriToResolve);
    }
    return (XMLSignatureInput)localObject2;
  }
  
  public boolean engineCanResolveURI(ResourceResolverContext paramResourceResolverContext)
  {
    if (uriToResolve == null) {
      return false;
    }
    return (isXPointerSlash(uriToResolve)) || (isXPointerId(uriToResolve));
  }
  
  private static boolean isXPointerSlash(String paramString)
  {
    return paramString.equals("#xpointer(/)");
  }
  
  private static boolean isXPointerId(String paramString)
  {
    if ((paramString.startsWith("#xpointer(id(")) && (paramString.endsWith("))")))
    {
      String str = paramString.substring(XP_LENGTH, paramString.length() - 2);
      int i = str.length() - 1;
      if (((str.charAt(0) == '"') && (str.charAt(i) == '"')) || ((str.charAt(0) == '\'') && (str.charAt(i) == '\'')))
      {
        if (log.isLoggable(Level.FINE)) {
          log.log(Level.FINE, "Id = " + str.substring(1, i));
        }
        return true;
      }
    }
    return false;
  }
  
  private static String getXPointerId(String paramString)
  {
    if ((paramString.startsWith("#xpointer(id(")) && (paramString.endsWith("))")))
    {
      String str = paramString.substring(XP_LENGTH, paramString.length() - 2);
      int i = str.length() - 1;
      if (((str.charAt(0) == '"') && (str.charAt(i) == '"')) || ((str.charAt(0) == '\'') && (str.charAt(i) == '\''))) {
        return str.substring(1, i);
      }
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\utils\resolver\implementations\ResolverXPointer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */