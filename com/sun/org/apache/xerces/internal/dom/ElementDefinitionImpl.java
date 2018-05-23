package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class ElementDefinitionImpl
  extends ParentNode
{
  static final long serialVersionUID = -8373890672670022714L;
  protected String name;
  protected NamedNodeMapImpl attributes;
  
  public ElementDefinitionImpl(CoreDocumentImpl paramCoreDocumentImpl, String paramString)
  {
    super(paramCoreDocumentImpl);
    name = paramString;
    attributes = new NamedNodeMapImpl(paramCoreDocumentImpl);
  }
  
  public short getNodeType()
  {
    return 21;
  }
  
  public String getNodeName()
  {
    if (needsSyncData()) {
      synchronizeData();
    }
    return name;
  }
  
  public Node cloneNode(boolean paramBoolean)
  {
    ElementDefinitionImpl localElementDefinitionImpl = (ElementDefinitionImpl)super.cloneNode(paramBoolean);
    attributes = attributes.cloneMap(localElementDefinitionImpl);
    return localElementDefinitionImpl;
  }
  
  public NamedNodeMap getAttributes()
  {
    if (needsSyncChildren()) {
      synchronizeChildren();
    }
    return attributes;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\dom\ElementDefinitionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */