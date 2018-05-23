package com.sun.xml.internal.messaging.saaj.soap.ver1_2;

import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.BodyElementImpl;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

public class BodyElement1_2Impl
  extends BodyElementImpl
{
  public BodyElement1_2Impl(SOAPDocumentImpl paramSOAPDocumentImpl, Name paramName)
  {
    super(paramSOAPDocumentImpl, paramName);
  }
  
  public BodyElement1_2Impl(SOAPDocumentImpl paramSOAPDocumentImpl, QName paramQName)
  {
    super(paramSOAPDocumentImpl, paramQName);
  }
  
  public SOAPElement setElementQName(QName paramQName)
    throws SOAPException
  {
    BodyElement1_2Impl localBodyElement1_2Impl = new BodyElement1_2Impl((SOAPDocumentImpl)getOwnerDocument(), paramQName);
    return replaceElementWithSOAPElement(this, localBodyElement1_2Impl);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\ver1_2\BodyElement1_2Impl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */