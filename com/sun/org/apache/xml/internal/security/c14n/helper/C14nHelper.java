package com.sun.org.apache.xml.internal.security.c14n.helper;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

public class C14nHelper
{
  private C14nHelper() {}
  
  public static boolean namespaceIsRelative(Attr paramAttr)
  {
    return !namespaceIsAbsolute(paramAttr);
  }
  
  public static boolean namespaceIsRelative(String paramString)
  {
    return !namespaceIsAbsolute(paramString);
  }
  
  public static boolean namespaceIsAbsolute(Attr paramAttr)
  {
    return namespaceIsAbsolute(paramAttr.getValue());
  }
  
  public static boolean namespaceIsAbsolute(String paramString)
  {
    if (paramString.length() == 0) {
      return true;
    }
    return paramString.indexOf(':') > 0;
  }
  
  public static void assertNotRelativeNS(Attr paramAttr)
    throws CanonicalizationException
  {
    if (paramAttr == null) {
      return;
    }
    String str1 = paramAttr.getNodeName();
    boolean bool1 = str1.equals("xmlns");
    boolean bool2 = str1.startsWith("xmlns:");
    if (((bool1) || (bool2)) && (namespaceIsRelative(paramAttr)))
    {
      String str2 = paramAttr.getOwnerElement().getTagName();
      String str3 = paramAttr.getValue();
      Object[] arrayOfObject = { str2, str1, str3 };
      throw new CanonicalizationException("c14n.Canonicalizer.RelativeNamespace", arrayOfObject);
    }
  }
  
  public static void checkTraversability(Document paramDocument)
    throws CanonicalizationException
  {
    if (!paramDocument.isSupported("Traversal", "2.0"))
    {
      Object[] arrayOfObject = { paramDocument.getImplementation().getClass().getName() };
      throw new CanonicalizationException("c14n.Canonicalizer.TraversalNotSupported", arrayOfObject);
    }
  }
  
  public static void checkForRelativeNamespace(Element paramElement)
    throws CanonicalizationException
  {
    if (paramElement != null)
    {
      NamedNodeMap localNamedNodeMap = paramElement.getAttributes();
      for (int i = 0; i < localNamedNodeMap.getLength(); i++) {
        assertNotRelativeNS((Attr)localNamedNodeMap.item(i));
      }
    }
    else
    {
      throw new CanonicalizationException("Called checkForRelativeNamespace() on null");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\c14n\helper\C14nHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */