package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import org.xml.sax.SAXException;

public abstract interface Patcher
{
  public abstract void run()
    throws SAXException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\unmarshaller\Patcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */