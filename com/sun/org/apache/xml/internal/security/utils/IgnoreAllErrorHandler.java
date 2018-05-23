package com.sun.org.apache.xml.internal.security.utils;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class IgnoreAllErrorHandler
  implements ErrorHandler
{
  private static Logger log = Logger.getLogger(IgnoreAllErrorHandler.class.getName());
  private static final boolean warnOnExceptions = System.getProperty("com.sun.org.apache.xml.internal.security.test.warn.on.exceptions", "false").equals("true");
  private static final boolean throwExceptions = System.getProperty("com.sun.org.apache.xml.internal.security.test.throw.exceptions", "false").equals("true");
  
  public IgnoreAllErrorHandler() {}
  
  public void warning(SAXParseException paramSAXParseException)
    throws SAXException
  {
    if (warnOnExceptions) {
      log.log(Level.WARNING, "", paramSAXParseException);
    }
    if (throwExceptions) {
      throw paramSAXParseException;
    }
  }
  
  public void error(SAXParseException paramSAXParseException)
    throws SAXException
  {
    if (warnOnExceptions) {
      log.log(Level.SEVERE, "", paramSAXParseException);
    }
    if (throwExceptions) {
      throw paramSAXParseException;
    }
  }
  
  public void fatalError(SAXParseException paramSAXParseException)
    throws SAXException
  {
    if (warnOnExceptions) {
      log.log(Level.WARNING, "", paramSAXParseException);
    }
    if (throwExceptions) {
      throw paramSAXParseException;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\utils\IgnoreAllErrorHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */