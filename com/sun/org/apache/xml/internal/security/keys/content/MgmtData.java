package com.sun.org.apache.xml.internal.security.keys.content;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class MgmtData
  extends SignatureElementProxy
  implements KeyInfoContent
{
  public MgmtData(Element paramElement, String paramString)
    throws XMLSecurityException
  {
    super(paramElement, paramString);
  }
  
  public MgmtData(Document paramDocument, String paramString)
  {
    super(paramDocument);
    addText(paramString);
  }
  
  public String getMgmtData()
  {
    return getTextFromTextChild();
  }
  
  public String getBaseLocalName()
  {
    return "MgmtData";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\keys\content\MgmtData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */