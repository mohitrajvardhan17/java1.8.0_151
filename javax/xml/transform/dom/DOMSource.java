package javax.xml.transform.dom;

import javax.xml.transform.Source;
import org.w3c.dom.Node;

public class DOMSource
  implements Source
{
  private Node node;
  private String systemID;
  public static final String FEATURE = "http://javax.xml.transform.dom.DOMSource/feature";
  
  public DOMSource() {}
  
  public DOMSource(Node paramNode)
  {
    setNode(paramNode);
  }
  
  public DOMSource(Node paramNode, String paramString)
  {
    setNode(paramNode);
    setSystemId(paramString);
  }
  
  public void setNode(Node paramNode)
  {
    node = paramNode;
  }
  
  public Node getNode()
  {
    return node;
  }
  
  public void setSystemId(String paramString)
  {
    systemID = paramString;
  }
  
  public String getSystemId()
  {
    return systemID;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\transform\dom\DOMSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */