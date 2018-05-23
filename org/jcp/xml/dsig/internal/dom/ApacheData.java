package org.jcp.xml.dsig.internal.dom;

import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import javax.xml.crypto.Data;

public abstract interface ApacheData
  extends Data
{
  public abstract XMLSignatureInput getXMLSignatureInput();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\jcp\xml\dsig\internal\dom\ApacheData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */