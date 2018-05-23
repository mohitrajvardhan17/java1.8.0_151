package com.sun.org.apache.xml.internal.utils;

import com.sun.org.apache.xml.internal.res.XMLMessages;
import java.io.PrintStream;
import java.io.PrintWriter;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class DefaultErrorHandler
  implements ErrorHandler, ErrorListener
{
  PrintWriter m_pw;
  boolean m_throwExceptionOnError = true;
  
  public DefaultErrorHandler(PrintWriter paramPrintWriter)
  {
    m_pw = paramPrintWriter;
  }
  
  public DefaultErrorHandler(PrintStream paramPrintStream)
  {
    m_pw = new PrintWriter(paramPrintStream, true);
  }
  
  public DefaultErrorHandler()
  {
    this(true);
  }
  
  public DefaultErrorHandler(boolean paramBoolean)
  {
    m_pw = new PrintWriter(System.err, true);
    m_throwExceptionOnError = paramBoolean;
  }
  
  public void warning(SAXParseException paramSAXParseException)
    throws SAXException
  {
    printLocation(m_pw, paramSAXParseException);
    m_pw.println("Parser warning: " + paramSAXParseException.getMessage());
  }
  
  public void error(SAXParseException paramSAXParseException)
    throws SAXException
  {
    throw paramSAXParseException;
  }
  
  public void fatalError(SAXParseException paramSAXParseException)
    throws SAXException
  {
    throw paramSAXParseException;
  }
  
  public void warning(TransformerException paramTransformerException)
    throws TransformerException
  {
    printLocation(m_pw, paramTransformerException);
    m_pw.println(paramTransformerException.getMessage());
  }
  
  public void error(TransformerException paramTransformerException)
    throws TransformerException
  {
    if (m_throwExceptionOnError) {
      throw paramTransformerException;
    }
    printLocation(m_pw, paramTransformerException);
    m_pw.println(paramTransformerException.getMessage());
  }
  
  public void fatalError(TransformerException paramTransformerException)
    throws TransformerException
  {
    if (m_throwExceptionOnError) {
      throw paramTransformerException;
    }
    printLocation(m_pw, paramTransformerException);
    m_pw.println(paramTransformerException.getMessage());
  }
  
  public static void ensureLocationSet(TransformerException paramTransformerException)
  {
    Object localObject1 = null;
    Object localObject2 = paramTransformerException;
    do
    {
      if ((localObject2 instanceof SAXParseException))
      {
        localObject1 = new SAXSourceLocator((SAXParseException)localObject2);
      }
      else if ((localObject2 instanceof TransformerException))
      {
        SourceLocator localSourceLocator = ((TransformerException)localObject2).getLocator();
        if (null != localSourceLocator) {
          localObject1 = localSourceLocator;
        }
      }
      if ((localObject2 instanceof TransformerException)) {
        localObject2 = ((TransformerException)localObject2).getCause();
      } else if ((localObject2 instanceof SAXException)) {
        localObject2 = ((SAXException)localObject2).getException();
      } else {
        localObject2 = null;
      }
    } while (null != localObject2);
    paramTransformerException.setLocator((SourceLocator)localObject1);
  }
  
  public static void printLocation(PrintStream paramPrintStream, TransformerException paramTransformerException)
  {
    printLocation(new PrintWriter(paramPrintStream), paramTransformerException);
  }
  
  public static void printLocation(PrintStream paramPrintStream, SAXParseException paramSAXParseException)
  {
    printLocation(new PrintWriter(paramPrintStream), paramSAXParseException);
  }
  
  public static void printLocation(PrintWriter paramPrintWriter, Throwable paramThrowable)
  {
    Object localObject1 = null;
    Object localObject2 = paramThrowable;
    Object localObject3;
    do
    {
      if ((localObject2 instanceof SAXParseException))
      {
        localObject1 = new SAXSourceLocator((SAXParseException)localObject2);
      }
      else if ((localObject2 instanceof TransformerException))
      {
        localObject3 = ((TransformerException)localObject2).getLocator();
        if (null != localObject3) {
          localObject1 = localObject3;
        }
      }
      if ((localObject2 instanceof TransformerException)) {
        localObject2 = ((TransformerException)localObject2).getCause();
      } else if ((localObject2 instanceof WrappedRuntimeException)) {
        localObject2 = ((WrappedRuntimeException)localObject2).getException();
      } else if ((localObject2 instanceof SAXException)) {
        localObject2 = ((SAXException)localObject2).getException();
      } else {
        localObject2 = null;
      }
    } while (null != localObject2);
    if (null != localObject1)
    {
      localObject3 = null != ((SourceLocator)localObject1).getSystemId() ? ((SourceLocator)localObject1).getSystemId() : null != ((SourceLocator)localObject1).getPublicId() ? ((SourceLocator)localObject1).getPublicId() : XMLMessages.createXMLMessage("ER_SYSTEMID_UNKNOWN", null);
      paramPrintWriter.print((String)localObject3 + "; " + XMLMessages.createXMLMessage("line", null) + ((SourceLocator)localObject1).getLineNumber() + "; " + XMLMessages.createXMLMessage("column", null) + ((SourceLocator)localObject1).getColumnNumber() + "; ");
    }
    else
    {
      paramPrintWriter.print("(" + XMLMessages.createXMLMessage("ER_LOCATION_UNKNOWN", null) + ")");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\utils\DefaultErrorHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */