package com.sun.xml.internal.messaging.saaj.soap.ver1_1;

import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.FaultElementImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

public class FaultElement1_1Impl
  extends FaultElementImpl
{
  public FaultElement1_1Impl(SOAPDocumentImpl paramSOAPDocumentImpl, NameImpl paramNameImpl)
  {
    super(paramSOAPDocumentImpl, paramNameImpl);
  }
  
  public FaultElement1_1Impl(SOAPDocumentImpl paramSOAPDocumentImpl, QName paramQName)
  {
    super(paramSOAPDocumentImpl, paramQName);
  }
  
  public FaultElement1_1Impl(SOAPDocumentImpl paramSOAPDocumentImpl, String paramString)
  {
    super(paramSOAPDocumentImpl, NameImpl.createFaultElement1_1Name(paramString));
  }
  
  public FaultElement1_1Impl(SOAPDocumentImpl paramSOAPDocumentImpl, String paramString1, String paramString2)
  {
    super(paramSOAPDocumentImpl, NameImpl.createFaultElement1_1Name(paramString1, paramString2));
  }
  
  protected boolean isStandardFaultElement()
  {
    String str = elementQName.getLocalPart();
    return (str.equalsIgnoreCase("faultcode")) || (str.equalsIgnoreCase("faultstring")) || (str.equalsIgnoreCase("faultactor"));
  }
  
  public SOAPElement setElementQName(QName paramQName)
    throws SOAPException
  {
    if (!isStandardFaultElement())
    {
      FaultElement1_1Impl localFaultElement1_1Impl = new FaultElement1_1Impl((SOAPDocumentImpl)getOwnerDocument(), paramQName);
      return replaceElementWithSOAPElement(this, localFaultElement1_1Impl);
    }
    return super.setElementQName(paramQName);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\ver1_1\FaultElement1_1Impl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */