package com.sun.xml.internal.ws.fault;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAnyElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

class DetailType
{
  @XmlAnyElement
  private final List<Element> detailEntry = new ArrayList();
  
  @NotNull
  List<Element> getDetails()
  {
    return detailEntry;
  }
  
  @Nullable
  Node getDetail(int paramInt)
  {
    if (paramInt < detailEntry.size()) {
      return (Node)detailEntry.get(paramInt);
    }
    return null;
  }
  
  DetailType(Element paramElement)
  {
    if (paramElement != null) {
      detailEntry.add(paramElement);
    }
  }
  
  DetailType() {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\fault\DetailType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */