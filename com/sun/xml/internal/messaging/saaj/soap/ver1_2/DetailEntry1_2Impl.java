package com.sun.xml.internal.messaging.saaj.soap.ver1_2;

import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.DetailEntryImpl;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

public class DetailEntry1_2Impl
  extends DetailEntryImpl
{
  public DetailEntry1_2Impl(SOAPDocumentImpl paramSOAPDocumentImpl, Name paramName)
  {
    super(paramSOAPDocumentImpl, paramName);
  }
  
  public DetailEntry1_2Impl(SOAPDocumentImpl paramSOAPDocumentImpl, QName paramQName)
  {
    super(paramSOAPDocumentImpl, paramQName);
  }
  
  public SOAPElement setElementQName(QName paramQName)
    throws SOAPException
  {
    DetailEntry1_2Impl localDetailEntry1_2Impl = new DetailEntry1_2Impl((SOAPDocumentImpl)getOwnerDocument(), paramQName);
    return replaceElementWithSOAPElement(this, localDetailEntry1_2Impl);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\ver1_2\DetailEntry1_2Impl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */