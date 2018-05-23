package com.sun.org.apache.xerces.internal.jaxp;

import com.sun.org.apache.xerces.internal.util.SAXMessageFormatter;
import java.io.PrintStream;
import java.util.Locale;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

class DefaultValidationErrorHandler
  extends DefaultHandler
{
  private static int ERROR_COUNT_LIMIT = 10;
  private int errorCount = 0;
  private Locale locale = Locale.getDefault();
  
  public DefaultValidationErrorHandler(Locale paramLocale)
  {
    locale = paramLocale;
  }
  
  public void error(SAXParseException paramSAXParseException)
    throws SAXException
  {
    if (errorCount >= ERROR_COUNT_LIMIT) {
      return;
    }
    if (errorCount == 0) {
      System.err.println(SAXMessageFormatter.formatMessage(locale, "errorHandlerNotSet", new Object[] { Integer.valueOf(errorCount) }));
    }
    String str1 = paramSAXParseException.getSystemId();
    if (str1 == null) {
      str1 = "null";
    }
    String str2 = "Error: URI=" + str1 + " Line=" + paramSAXParseException.getLineNumber() + ": " + paramSAXParseException.getMessage();
    System.err.println(str2);
    errorCount += 1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\DefaultValidationErrorHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */