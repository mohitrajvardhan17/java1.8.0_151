package org.jcp.xml.dsig.internal.dom;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import java.io.IOException;
import javax.xml.crypto.OctetStreamData;

public class ApacheOctetStreamData
  extends OctetStreamData
  implements ApacheData
{
  private XMLSignatureInput xi;
  
  public ApacheOctetStreamData(XMLSignatureInput paramXMLSignatureInput)
    throws CanonicalizationException, IOException
  {
    super(paramXMLSignatureInput.getOctetStream(), paramXMLSignatureInput.getSourceURI(), paramXMLSignatureInput.getMIMEType());
    xi = paramXMLSignatureInput;
  }
  
  public XMLSignatureInput getXMLSignatureInput()
  {
    return xi;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\jcp\xml\dsig\internal\dom\ApacheOctetStreamData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */