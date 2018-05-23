package com.sun.org.apache.xerces.internal.jaxp.validation;

import java.io.IOException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.xml.sax.SAXException;

abstract interface ValidatorHelper
{
  public abstract void validate(Source paramSource, Result paramResult)
    throws SAXException, IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\validation\ValidatorHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */