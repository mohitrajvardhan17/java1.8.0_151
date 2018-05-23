package com.sun.xml.internal.ws.fault;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElements;

class ReasonType
{
  @XmlElements({@javax.xml.bind.annotation.XmlElement(name="Text", namespace="http://www.w3.org/2003/05/soap-envelope", type=TextType.class)})
  private final List<TextType> text = new ArrayList();
  
  ReasonType() {}
  
  ReasonType(String paramString)
  {
    text.add(new TextType(paramString));
  }
  
  List<TextType> texts()
  {
    return text;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\fault\ReasonType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */