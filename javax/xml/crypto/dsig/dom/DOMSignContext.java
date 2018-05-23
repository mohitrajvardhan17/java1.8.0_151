package javax.xml.crypto.dsig.dom;

import java.security.Key;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.XMLSignContext;
import org.w3c.dom.Node;

public class DOMSignContext
  extends DOMCryptoContext
  implements XMLSignContext
{
  private Node parent;
  private Node nextSibling;
  
  public DOMSignContext(Key paramKey, Node paramNode)
  {
    if (paramKey == null) {
      throw new NullPointerException("signingKey cannot be null");
    }
    if (paramNode == null) {
      throw new NullPointerException("parent cannot be null");
    }
    setKeySelector(KeySelector.singletonKeySelector(paramKey));
    parent = paramNode;
  }
  
  public DOMSignContext(Key paramKey, Node paramNode1, Node paramNode2)
  {
    if (paramKey == null) {
      throw new NullPointerException("signingKey cannot be null");
    }
    if (paramNode1 == null) {
      throw new NullPointerException("parent cannot be null");
    }
    if (paramNode2 == null) {
      throw new NullPointerException("nextSibling cannot be null");
    }
    setKeySelector(KeySelector.singletonKeySelector(paramKey));
    parent = paramNode1;
    nextSibling = paramNode2;
  }
  
  public DOMSignContext(KeySelector paramKeySelector, Node paramNode)
  {
    if (paramKeySelector == null) {
      throw new NullPointerException("key selector cannot be null");
    }
    if (paramNode == null) {
      throw new NullPointerException("parent cannot be null");
    }
    setKeySelector(paramKeySelector);
    parent = paramNode;
  }
  
  public DOMSignContext(KeySelector paramKeySelector, Node paramNode1, Node paramNode2)
  {
    if (paramKeySelector == null) {
      throw new NullPointerException("key selector cannot be null");
    }
    if (paramNode1 == null) {
      throw new NullPointerException("parent cannot be null");
    }
    if (paramNode2 == null) {
      throw new NullPointerException("nextSibling cannot be null");
    }
    setKeySelector(paramKeySelector);
    parent = paramNode1;
    nextSibling = paramNode2;
  }
  
  public void setParent(Node paramNode)
  {
    if (paramNode == null) {
      throw new NullPointerException("parent is null");
    }
    parent = paramNode;
  }
  
  public void setNextSibling(Node paramNode)
  {
    nextSibling = paramNode;
  }
  
  public Node getParent()
  {
    return parent;
  }
  
  public Node getNextSibling()
  {
    return nextSibling;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\crypto\dsig\dom\DOMSignContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */