package javax.imageio.metadata;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;

class IIOAttr
  extends IIOMetadataNode
  implements Attr
{
  Element owner;
  String name;
  String value;
  
  public IIOAttr(Element paramElement, String paramString1, String paramString2)
  {
    owner = paramElement;
    name = paramString1;
    value = paramString2;
  }
  
  public String getName()
  {
    return name;
  }
  
  public String getNodeName()
  {
    return name;
  }
  
  public short getNodeType()
  {
    return 2;
  }
  
  public boolean getSpecified()
  {
    return true;
  }
  
  public String getValue()
  {
    return value;
  }
  
  public String getNodeValue()
  {
    return value;
  }
  
  public void setValue(String paramString)
  {
    value = paramString;
  }
  
  public void setNodeValue(String paramString)
  {
    value = paramString;
  }
  
  public Element getOwnerElement()
  {
    return owner;
  }
  
  public void setOwnerElement(Element paramElement)
  {
    owner = paramElement;
  }
  
  public boolean isId()
  {
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\imageio\metadata\IIOAttr.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */