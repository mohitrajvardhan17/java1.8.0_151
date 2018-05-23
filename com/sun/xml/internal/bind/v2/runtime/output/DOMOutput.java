package com.sun.xml.internal.bind.v2.runtime.output;

import com.sun.xml.internal.bind.marshaller.SAX2DOMEx;
import com.sun.xml.internal.bind.v2.runtime.AssociationMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public final class DOMOutput
  extends SAXOutput
{
  private final AssociationMap assoc;
  
  public DOMOutput(Node paramNode, AssociationMap paramAssociationMap)
  {
    super(new SAX2DOMEx(paramNode));
    assoc = paramAssociationMap;
    assert (paramAssociationMap != null);
  }
  
  private SAX2DOMEx getBuilder()
  {
    return (SAX2DOMEx)out;
  }
  
  public void endStartTag()
    throws SAXException
  {
    super.endStartTag();
    Object localObject1 = nsContext.getCurrent().getOuterPeer();
    if (localObject1 != null) {
      assoc.addOuter(getBuilder().getCurrentElement(), localObject1);
    }
    Object localObject2 = nsContext.getCurrent().getInnerPeer();
    if (localObject2 != null) {
      assoc.addInner(getBuilder().getCurrentElement(), localObject2);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\output\DOMOutput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */