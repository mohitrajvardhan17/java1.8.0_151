package javax.xml.crypto.dom;

import javax.xml.crypto.XMLStructure;
import org.w3c.dom.Node;

public class DOMStructure
  implements XMLStructure
{
  private final Node node;
  
  public DOMStructure(Node paramNode)
  {
    if (paramNode == null) {
      throw new NullPointerException("node cannot be null");
    }
    node = paramNode;
  }
  
  public Node getNode()
  {
    return node;
  }
  
  public boolean isFeatureSupported(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\crypto\dom\DOMStructure.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */