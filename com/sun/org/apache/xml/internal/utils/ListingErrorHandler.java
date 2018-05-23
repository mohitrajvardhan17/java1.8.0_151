package com.sun.org.apache.xml.internal.utils;

import com.sun.org.apache.xml.internal.res.XMLMessages;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ListingErrorHandler
  implements ErrorHandler, ErrorListener
{
  protected PrintWriter m_pw = null;
  protected boolean throwOnWarning = false;
  protected boolean throwOnError = true;
  protected boolean throwOnFatalError = true;
  
  public ListingErrorHandler(PrintWriter paramPrintWriter)
  {
    if (null == paramPrintWriter) {
      throw new NullPointerException(XMLMessages.createXMLMessage("ER_ERRORHANDLER_CREATED_WITH_NULL_PRINTWRITER", null));
    }
    m_pw = paramPrintWriter;
  }
  
  public ListingErrorHandler()
  {
    m_pw = new PrintWriter(System.err, true);
  }
  
  public void warning(SAXParseException paramSAXParseException)
    throws SAXException
  {
    logExceptionLocation(m_pw, paramSAXParseException);
    m_pw.println("warning: " + paramSAXParseException.getMessage());
    m_pw.flush();
    if (getThrowOnWarning()) {
      throw paramSAXParseException;
    }
  }
  
  public void error(SAXParseException paramSAXParseException)
    throws SAXException
  {
    logExceptionLocation(m_pw, paramSAXParseException);
    m_pw.println("error: " + paramSAXParseException.getMessage());
    m_pw.flush();
    if (getThrowOnError()) {
      throw paramSAXParseException;
    }
  }
  
  public void fatalError(SAXParseException paramSAXParseException)
    throws SAXException
  {
    logExceptionLocation(m_pw, paramSAXParseException);
    m_pw.println("fatalError: " + paramSAXParseException.getMessage());
    m_pw.flush();
    if (getThrowOnFatalError()) {
      throw paramSAXParseException;
    }
  }
  
  public void warning(TransformerException paramTransformerException)
    throws TransformerException
  {
    logExceptionLocation(m_pw, paramTransformerException);
    m_pw.println("warning: " + paramTransformerException.getMessage());
    m_pw.flush();
    if (getThrowOnWarning()) {
      throw paramTransformerException;
    }
  }
  
  public void error(TransformerException paramTransformerException)
    throws TransformerException
  {
    logExceptionLocation(m_pw, paramTransformerException);
    m_pw.println("error: " + paramTransformerException.getMessage());
    m_pw.flush();
    if (getThrowOnError()) {
      throw paramTransformerException;
    }
  }
  
  public void fatalError(TransformerException paramTransformerException)
    throws TransformerException
  {
    logExceptionLocation(m_pw, paramTransformerException);
    m_pw.println("error: " + paramTransformerException.getMessage());
    m_pw.flush();
    if (getThrowOnError()) {
      throw paramTransformerException;
    }
  }
  
  public static void logExceptionLocation(PrintWriter paramPrintWriter, Throwable paramThrowable)
  {
    if (null == paramPrintWriter) {
      paramPrintWriter = new PrintWriter(System.err, true);
    }
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
      localObject3 = null != ((SourceLocator)localObject1).getSystemId() ? ((SourceLocator)localObject1).getSystemId() : ((SourceLocator)localObject1).getPublicId() != ((SourceLocator)localObject1).getPublicId() ? ((SourceLocator)localObject1).getPublicId() : "SystemId-Unknown";
      paramPrintWriter.print((String)localObject3 + ":Line=" + ((SourceLocator)localObject1).getLineNumber() + ";Column=" + ((SourceLocator)localObject1).getColumnNumber() + ": ");
      paramPrintWriter.println("exception:" + paramThrowable.getMessage());
      paramPrintWriter.println("root-cause:" + (null != localObject2 ? ((Throwable)localObject2).getMessage() : "null"));
      logSourceLine(paramPrintWriter, (SourceLocator)localObject1);
    }
    else
    {
      paramPrintWriter.print("SystemId-Unknown:locator-unavailable: ");
      paramPrintWriter.println("exception:" + paramThrowable.getMessage());
      paramPrintWriter.println("root-cause:" + (null != localObject2 ? ((Throwable)localObject2).getMessage() : "null"));
    }
  }
  
  public static void logSourceLine(PrintWriter paramPrintWriter, SourceLocator paramSourceLocator)
  {
    if (null == paramSourceLocator) {
      return;
    }
    if (null == paramPrintWriter) {
      paramPrintWriter = new PrintWriter(System.err, true);
    }
    String str = paramSourceLocator.getSystemId();
    if (null == str)
    {
      paramPrintWriter.println("line: (No systemId; cannot read file)");
      paramPrintWriter.println();
      return;
    }
    try
    {
      int i = paramSourceLocator.getLineNumber();
      int j = paramSourceLocator.getColumnNumber();
      paramPrintWriter.println("line: " + getSourceLine(str, i));
      StringBuffer localStringBuffer = new StringBuffer("line: ");
      for (int k = 1; k < j; k++) {
        localStringBuffer.append(' ');
      }
      localStringBuffer.append('^');
      paramPrintWriter.println(localStringBuffer.toString());
    }
    catch (Exception localException)
    {
      paramPrintWriter.println("line: logSourceLine unavailable due to: " + localException.getMessage());
      paramPrintWriter.println();
    }
  }
  
  protected static String getSourceLine(String paramString, int paramInt)
    throws Exception
  {
    URL localURL = null;
    try
    {
      localURL = new URL(paramString);
    }
    catch (MalformedURLException localMalformedURLException)
    {
      int i = paramString.indexOf(':');
      int j = paramString.indexOf('/');
      if ((i != -1) && (j != -1) && (i < j)) {
        throw localMalformedURLException;
      }
      localURL = new URL(SystemIDResolver.getAbsoluteURI(paramString));
    }
    String str = null;
    InputStream localInputStream = null;
    BufferedReader localBufferedReader = null;
    try
    {
      URLConnection localURLConnection = localURL.openConnection();
      localInputStream = localURLConnection.getInputStream();
      localBufferedReader = new BufferedReader(new InputStreamReader(localInputStream));
      for (int k = 1; k <= paramInt; k++) {
        str = localBufferedReader.readLine();
      }
    }
    finally
    {
      localBufferedReader.close();
      localInputStream.close();
    }
    return str;
  }
  
  public void setThrowOnWarning(boolean paramBoolean)
  {
    throwOnWarning = paramBoolean;
  }
  
  public boolean getThrowOnWarning()
  {
    return throwOnWarning;
  }
  
  public void setThrowOnError(boolean paramBoolean)
  {
    throwOnError = paramBoolean;
  }
  
  public boolean getThrowOnError()
  {
    return throwOnError;
  }
  
  public void setThrowOnFatalError(boolean paramBoolean)
  {
    throwOnFatalError = paramBoolean;
  }
  
  public boolean getThrowOnFatalError()
  {
    return throwOnFatalError;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\utils\ListingErrorHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */