package com.sun.xml.internal.messaging.saaj.soap;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

public class SOAPIOException
  extends IOException
{
  SOAPExceptionImpl soapException;
  
  public SOAPIOException()
  {
    soapException = new SOAPExceptionImpl();
    soapException.fillInStackTrace();
  }
  
  public SOAPIOException(String paramString)
  {
    soapException = new SOAPExceptionImpl(paramString);
    soapException.fillInStackTrace();
  }
  
  public SOAPIOException(String paramString, Throwable paramThrowable)
  {
    soapException = new SOAPExceptionImpl(paramString, paramThrowable);
    soapException.fillInStackTrace();
  }
  
  public SOAPIOException(Throwable paramThrowable)
  {
    super(paramThrowable.toString());
    soapException = new SOAPExceptionImpl(paramThrowable);
    soapException.fillInStackTrace();
  }
  
  public Throwable fillInStackTrace()
  {
    if (soapException != null) {
      soapException.fillInStackTrace();
    }
    return this;
  }
  
  public String getLocalizedMessage()
  {
    return soapException.getLocalizedMessage();
  }
  
  public String getMessage()
  {
    return soapException.getMessage();
  }
  
  public void printStackTrace()
  {
    soapException.printStackTrace();
  }
  
  public void printStackTrace(PrintStream paramPrintStream)
  {
    soapException.printStackTrace(paramPrintStream);
  }
  
  public void printStackTrace(PrintWriter paramPrintWriter)
  {
    soapException.printStackTrace(paramPrintWriter);
  }
  
  public String toString()
  {
    return soapException.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\SOAPIOException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */