package javax.xml.crypto.dsig.dom;

import java.security.Key;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.XMLValidateContext;
import org.w3c.dom.Node;

public class DOMValidateContext
  extends DOMCryptoContext
  implements XMLValidateContext
{
  private Node node;
  
  public DOMValidateContext(KeySelector paramKeySelector, Node paramNode)
  {
    if (paramKeySelector == null) {
      throw new NullPointerException("key selector is null");
    }
    init(paramNode, paramKeySelector);
  }
  
  public DOMValidateContext(Key paramKey, Node paramNode)
  {
    if (paramKey == null) {
      throw new NullPointerException("validatingKey is null");
    }
    init(paramNode, KeySelector.singletonKeySelector(paramKey));
  }
  
  private void init(Node paramNode, KeySelector paramKeySelector)
  {
    if (paramNode == null) {
      throw new NullPointerException("node is null");
    }
    node = paramNode;
    super.setKeySelector(paramKeySelector);
    if (System.getSecurityManager() != null) {
      super.setProperty("org.jcp.xml.dsig.secureValidation", Boolean.TRUE);
    }
  }
  
  public void setNode(Node paramNode)
  {
    if (paramNode == null) {
      throw new NullPointerException();
    }
    node = paramNode;
  }
  
  public Node getNode()
  {
    return node;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\crypto\dsig\dom\DOMValidateContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */