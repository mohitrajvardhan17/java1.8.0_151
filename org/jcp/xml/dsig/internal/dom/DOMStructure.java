package org.jcp.xml.dsig.internal.dom;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMCryptoContext;
import org.w3c.dom.Node;

public abstract class DOMStructure
  implements XMLStructure
{
  public DOMStructure() {}
  
  public final boolean isFeatureSupported(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    return false;
  }
  
  public abstract void marshal(Node paramNode, String paramString, DOMCryptoContext paramDOMCryptoContext)
    throws MarshalException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\jcp\xml\dsig\internal\dom\DOMStructure.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */