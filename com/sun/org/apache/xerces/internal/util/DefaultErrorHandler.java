package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
import java.io.PrintWriter;

public class DefaultErrorHandler
  implements XMLErrorHandler
{
  protected PrintWriter fOut;
  
  public DefaultErrorHandler()
  {
    this(new PrintWriter(System.err));
  }
  
  public DefaultErrorHandler(PrintWriter paramPrintWriter)
  {
    fOut = paramPrintWriter;
  }
  
  public void warning(String paramString1, String paramString2, XMLParseException paramXMLParseException)
    throws XNIException
  {
    printError("Warning", paramXMLParseException);
  }
  
  public void error(String paramString1, String paramString2, XMLParseException paramXMLParseException)
    throws XNIException
  {
    printError("Error", paramXMLParseException);
  }
  
  public void fatalError(String paramString1, String paramString2, XMLParseException paramXMLParseException)
    throws XNIException
  {
    printError("Fatal Error", paramXMLParseException);
    throw paramXMLParseException;
  }
  
  private void printError(String paramString, XMLParseException paramXMLParseException)
  {
    fOut.print("[");
    fOut.print(paramString);
    fOut.print("] ");
    String str = paramXMLParseException.getExpandedSystemId();
    if (str != null)
    {
      int i = str.lastIndexOf('/');
      if (i != -1) {
        str = str.substring(i + 1);
      }
      fOut.print(str);
    }
    fOut.print(':');
    fOut.print(paramXMLParseException.getLineNumber());
    fOut.print(':');
    fOut.print(paramXMLParseException.getColumnNumber());
    fOut.print(": ");
    fOut.print(paramXMLParseException.getMessage());
    fOut.println();
    fOut.flush();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\util\DefaultErrorHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */