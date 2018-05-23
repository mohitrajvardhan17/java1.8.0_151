package com.sun.xml.internal.bind;

import java.util.concurrent.Callable;
import javax.xml.bind.ValidationEventHandler;
import org.xml.sax.SAXException;

public abstract class IDResolver
{
  public IDResolver() {}
  
  public void startDocument(ValidationEventHandler paramValidationEventHandler)
    throws SAXException
  {}
  
  public void endDocument()
    throws SAXException
  {}
  
  public abstract void bind(String paramString, Object paramObject)
    throws SAXException;
  
  public abstract Callable<?> resolve(String paramString, Class paramClass)
    throws SAXException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\IDResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */