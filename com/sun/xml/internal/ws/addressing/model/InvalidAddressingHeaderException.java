package com.sun.xml.internal.ws.addressing.model;

import com.sun.xml.internal.ws.resources.AddressingMessages;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

public class InvalidAddressingHeaderException
  extends WebServiceException
{
  private QName problemHeader;
  private QName subsubcode;
  
  public InvalidAddressingHeaderException(QName paramQName1, QName paramQName2)
  {
    super(AddressingMessages.INVALID_ADDRESSING_HEADER_EXCEPTION(paramQName1, paramQName2));
    problemHeader = paramQName1;
    subsubcode = paramQName2;
  }
  
  public QName getProblemHeader()
  {
    return problemHeader;
  }
  
  public QName getSubsubcode()
  {
    return subsubcode;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\addressing\model\InvalidAddressingHeaderException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */