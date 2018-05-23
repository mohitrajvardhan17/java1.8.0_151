package org.jcp.xml.dsig.internal.dom;

import com.sun.org.apache.xml.internal.security.utils.Base64;
import java.math.BigInteger;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dom.DOMCryptoContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public final class DOMCryptoBinary
  extends DOMStructure
{
  private final BigInteger bigNum;
  private final String value;
  
  public DOMCryptoBinary(BigInteger paramBigInteger)
  {
    if (paramBigInteger == null) {
      throw new NullPointerException("bigNum is null");
    }
    bigNum = paramBigInteger;
    value = Base64.encode(paramBigInteger);
  }
  
  public DOMCryptoBinary(Node paramNode)
    throws MarshalException
  {
    value = paramNode.getNodeValue();
    try
    {
      bigNum = Base64.decodeBigIntegerFromText((Text)paramNode);
    }
    catch (Exception localException)
    {
      throw new MarshalException(localException);
    }
  }
  
  public BigInteger getBigNum()
  {
    return bigNum;
  }
  
  public void marshal(Node paramNode, String paramString, DOMCryptoContext paramDOMCryptoContext)
    throws MarshalException
  {
    paramNode.appendChild(DOMUtils.getOwnerDocument(paramNode).createTextNode(value));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\jcp\xml\dsig\internal\dom\DOMCryptoBinary.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */