package com.sun.xml.internal.bind.v2.runtime.reflect;

import javax.xml.bind.JAXBException;
import org.xml.sax.SAXException;

public abstract interface ListIterator<E>
{
  public abstract boolean hasNext();
  
  public abstract E next()
    throws SAXException, JAXBException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\ListIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */