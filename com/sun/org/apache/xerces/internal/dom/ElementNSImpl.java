package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.impl.dv.xs.XSSimpleTypeDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSComplexTypeDecl;
import com.sun.org.apache.xerces.internal.util.URI;
import com.sun.org.apache.xerces.internal.util.URI.MalformedURIException;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;

public class ElementNSImpl
  extends ElementImpl
{
  static final long serialVersionUID = -9142310625494392642L;
  static final String xmlURI = "http://www.w3.org/XML/1998/namespace";
  protected String namespaceURI;
  protected String localName;
  transient XSTypeDefinition type;
  
  protected ElementNSImpl() {}
  
  protected ElementNSImpl(CoreDocumentImpl paramCoreDocumentImpl, String paramString1, String paramString2)
    throws DOMException
  {
    super(paramCoreDocumentImpl, paramString2);
    setName(paramString1, paramString2);
  }
  
  private void setName(String paramString1, String paramString2)
  {
    namespaceURI = paramString1;
    if (paramString1 != null) {
      namespaceURI = (paramString1.length() == 0 ? null : paramString1);
    }
    String str2;
    if (paramString2 == null)
    {
      str2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
      throw new DOMException((short)14, str2);
    }
    int i = paramString2.indexOf(':');
    int j = paramString2.lastIndexOf(':');
    ownerDocument.checkNamespaceWF(paramString2, i, j);
    if (i < 0)
    {
      localName = paramString2;
      if (ownerDocument.errorChecking)
      {
        ownerDocument.checkQName(null, localName);
        if (((paramString2.equals("xmlns")) && ((paramString1 == null) || (!paramString1.equals(NamespaceContext.XMLNS_URI)))) || ((paramString1 != null) && (paramString1.equals(NamespaceContext.XMLNS_URI)) && (!paramString2.equals("xmlns"))))
        {
          str2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
          throw new DOMException((short)14, str2);
        }
      }
    }
    else
    {
      String str1 = paramString2.substring(0, i);
      localName = paramString2.substring(j + 1);
      if (ownerDocument.errorChecking)
      {
        if ((paramString1 == null) || ((str1.equals("xml")) && (!paramString1.equals(NamespaceContext.XML_URI))))
        {
          str2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
          throw new DOMException((short)14, str2);
        }
        ownerDocument.checkQName(str1, localName);
        ownerDocument.checkDOMNSErr(str1, paramString1);
      }
    }
  }
  
  protected ElementNSImpl(CoreDocumentImpl paramCoreDocumentImpl, String paramString1, String paramString2, String paramString3)
    throws DOMException
  {
    super(paramCoreDocumentImpl, paramString2);
    localName = paramString3;
    namespaceURI = paramString1;
  }
  
  protected ElementNSImpl(CoreDocumentImpl paramCoreDocumentImpl, String paramString)
  {
    super(paramCoreDocumentImpl, paramString);
  }
  
  void rename(String paramString1, String paramString2)
  {
    if (needsSyncData()) {
      synchronizeData();
    }
    name = paramString2;
    setName(paramString1, paramString2);
    reconcileDefaultAttributes();
  }
  
  protected void setValues(CoreDocumentImpl paramCoreDocumentImpl, String paramString1, String paramString2, String paramString3)
  {
    firstChild = null;
    previousSibling = null;
    nextSibling = null;
    fNodeListCache = null;
    attributes = null;
    flags = 0;
    setOwnerDocument(paramCoreDocumentImpl);
    needsSyncData(true);
    name = paramString2;
    localName = paramString3;
    namespaceURI = paramString1;
  }
  
  public String getNamespaceURI()
  {
    if (needsSyncData()) {
      synchronizeData();
    }
    return namespaceURI;
  }
  
  public String getPrefix()
  {
    if (needsSyncData()) {
      synchronizeData();
    }
    int i = name.indexOf(':');
    return i < 0 ? null : name.substring(0, i);
  }
  
  public void setPrefix(String paramString)
    throws DOMException
  {
    if (needsSyncData()) {
      synchronizeData();
    }
    if (ownerDocument.errorChecking)
    {
      String str;
      if (isReadOnly())
      {
        str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
        throw new DOMException((short)7, str);
      }
      if ((paramString != null) && (paramString.length() != 0))
      {
        if (!CoreDocumentImpl.isXMLName(paramString, ownerDocument.isXML11Version()))
        {
          str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
          throw new DOMException((short)5, str);
        }
        if ((namespaceURI == null) || (paramString.indexOf(':') >= 0))
        {
          str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
          throw new DOMException((short)14, str);
        }
        if ((paramString.equals("xml")) && (!namespaceURI.equals("http://www.w3.org/XML/1998/namespace")))
        {
          str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
          throw new DOMException((short)14, str);
        }
      }
    }
    if ((paramString != null) && (paramString.length() != 0)) {
      name = (paramString + ":" + localName);
    } else {
      name = localName;
    }
  }
  
  public String getLocalName()
  {
    if (needsSyncData()) {
      synchronizeData();
    }
    return localName;
  }
  
  public String getBaseURI()
  {
    if (needsSyncData()) {
      synchronizeData();
    }
    if (attributes != null)
    {
      localAttr = (Attr)attributes.getNamedItemNS("http://www.w3.org/XML/1998/namespace", "base");
      if (localAttr != null)
      {
        String str1 = localAttr.getNodeValue();
        if (str1.length() != 0)
        {
          try
          {
            str1 = new URI(str1).toString();
          }
          catch (URI.MalformedURIException localMalformedURIException2)
          {
            NodeImpl localNodeImpl = parentNode() != null ? parentNode() : ownerNode;
            String str3 = localNodeImpl != null ? localNodeImpl.getBaseURI() : null;
            if (str3 != null)
            {
              try
              {
                str1 = new URI(new URI(str3), str1).toString();
              }
              catch (URI.MalformedURIException localMalformedURIException4)
              {
                return null;
              }
              return str1;
            }
            return null;
          }
          return str1;
        }
      }
    }
    Attr localAttr = parentNode() != null ? parentNode().getBaseURI() : null;
    if (localAttr != null) {
      try
      {
        return new URI(localAttr).toString();
      }
      catch (URI.MalformedURIException localMalformedURIException1)
      {
        return null;
      }
    }
    String str2 = ownerNode != null ? ownerNode.getBaseURI() : null;
    if (str2 != null) {
      try
      {
        return new URI(str2).toString();
      }
      catch (URI.MalformedURIException localMalformedURIException3)
      {
        return null;
      }
    }
    return null;
  }
  
  public String getTypeName()
  {
    if (type != null)
    {
      if ((type instanceof XSSimpleTypeDecl)) {
        return ((XSSimpleTypeDecl)type).getTypeName();
      }
      if ((type instanceof XSComplexTypeDecl)) {
        return ((XSComplexTypeDecl)type).getTypeName();
      }
    }
    return null;
  }
  
  public String getTypeNamespace()
  {
    if (type != null) {
      return type.getNamespace();
    }
    return null;
  }
  
  public boolean isDerivedFrom(String paramString1, String paramString2, int paramInt)
  {
    if (needsSyncData()) {
      synchronizeData();
    }
    if (type != null)
    {
      if ((type instanceof XSSimpleTypeDecl)) {
        return ((XSSimpleTypeDecl)type).isDOMDerivedFrom(paramString1, paramString2, paramInt);
      }
      if ((type instanceof XSComplexTypeDecl)) {
        return ((XSComplexTypeDecl)type).isDOMDerivedFrom(paramString1, paramString2, paramInt);
      }
    }
    return false;
  }
  
  public void setType(XSTypeDefinition paramXSTypeDefinition)
  {
    type = paramXSTypeDefinition;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\dom\ElementNSImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */