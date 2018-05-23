package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class DocumentFragmentImpl
  extends ParentNode
  implements DocumentFragment
{
  static final long serialVersionUID = -7596449967279236746L;
  
  public DocumentFragmentImpl(CoreDocumentImpl paramCoreDocumentImpl)
  {
    super(paramCoreDocumentImpl);
  }
  
  public DocumentFragmentImpl() {}
  
  public short getNodeType()
  {
    return 11;
  }
  
  public String getNodeName()
  {
    return "#document-fragment";
  }
  
  public void normalize()
  {
    if (isNormalized()) {
      return;
    }
    if (needsSyncChildren()) {
      synchronizeChildren();
    }
    Object localObject2;
    for (Object localObject1 = firstChild; localObject1 != null; localObject1 = localObject2)
    {
      localObject2 = nextSibling;
      if (((ChildNode)localObject1).getNodeType() == 3) {
        if ((localObject2 != null) && (((ChildNode)localObject2).getNodeType() == 3))
        {
          ((Text)localObject1).appendData(((ChildNode)localObject2).getNodeValue());
          removeChild((Node)localObject2);
          localObject2 = localObject1;
        }
        else if ((((ChildNode)localObject1).getNodeValue() == null) || (((ChildNode)localObject1).getNodeValue().length() == 0))
        {
          removeChild((Node)localObject1);
        }
      }
      ((ChildNode)localObject1).normalize();
    }
    isNormalized(true);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\dom\DocumentFragmentImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */