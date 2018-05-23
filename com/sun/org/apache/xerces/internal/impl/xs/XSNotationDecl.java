package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.xs.XSAnnotation;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItem;
import com.sun.org.apache.xerces.internal.xs.XSNotationDeclaration;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;

public class XSNotationDecl
  implements XSNotationDeclaration
{
  public String fName = null;
  public String fTargetNamespace = null;
  public String fPublicId = null;
  public String fSystemId = null;
  public XSObjectList fAnnotations = null;
  private XSNamespaceItem fNamespaceItem = null;
  
  public XSNotationDecl() {}
  
  public short getType()
  {
    return 11;
  }
  
  public String getName()
  {
    return fName;
  }
  
  public String getNamespace()
  {
    return fTargetNamespace;
  }
  
  public String getSystemId()
  {
    return fSystemId;
  }
  
  public String getPublicId()
  {
    return fPublicId;
  }
  
  public XSAnnotation getAnnotation()
  {
    return fAnnotations != null ? (XSAnnotation)fAnnotations.item(0) : null;
  }
  
  public XSObjectList getAnnotations()
  {
    return fAnnotations != null ? fAnnotations : XSObjectListImpl.EMPTY_LIST;
  }
  
  public XSNamespaceItem getNamespaceItem()
  {
    return fNamespaceItem;
  }
  
  void setNamespaceItem(XSNamespaceItem paramXSNamespaceItem)
  {
    fNamespaceItem = paramXSNamespaceItem;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\XSNotationDecl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */