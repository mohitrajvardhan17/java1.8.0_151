package com.sun.xml.internal.txw2.output;

import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;

public abstract class ResultFactory
{
  private ResultFactory() {}
  
  public static XmlSerializer createSerializer(Result paramResult)
  {
    if ((paramResult instanceof SAXResult)) {
      return new SaxSerializer((SAXResult)paramResult);
    }
    if ((paramResult instanceof DOMResult)) {
      return new DomSerializer((DOMResult)paramResult);
    }
    if ((paramResult instanceof StreamResult)) {
      return new StreamSerializer((StreamResult)paramResult);
    }
    if ((paramResult instanceof TXWResult)) {
      return new TXWSerializer(((TXWResult)paramResult).getWriter());
    }
    throw new UnsupportedOperationException("Unsupported Result type: " + paramResult.getClass().getName());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\txw2\output\ResultFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */