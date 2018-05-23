package com.sun.xml.internal.messaging.saaj;

import java.io.PrintStream;
import java.io.PrintWriter;
import javax.xml.soap.SOAPException;

public class SOAPExceptionImpl
  extends SOAPException
{
  private Throwable cause;
  
  public SOAPExceptionImpl()
  {
    cause = null;
  }
  
  public SOAPExceptionImpl(String paramString)
  {
    super(paramString);
    cause = null;
  }
  
  public SOAPExceptionImpl(String paramString, Throwable paramThrowable)
  {
    super(paramString);
    initCause(paramThrowable);
  }
  
  public SOAPExceptionImpl(Throwable paramThrowable)
  {
    super(paramThrowable.toString());
    initCause(paramThrowable);
  }
  
  public String getMessage()
  {
    String str = super.getMessage();
    if ((str == null) && (cause != null)) {
      return cause.getMessage();
    }
    return str;
  }
  
  public Throwable getCause()
  {
    return cause;
  }
  
  public synchronized Throwable initCause(Throwable paramThrowable)
  {
    if (cause != null) {
      throw new IllegalStateException("Can't override cause");
    }
    if (paramThrowable == this) {
      throw new IllegalArgumentException("Self-causation not permitted");
    }
    cause = paramThrowable;
    return this;
  }
  
  public void printStackTrace()
  {
    super.printStackTrace();
    if (cause != null)
    {
      System.err.println("\nCAUSE:\n");
      cause.printStackTrace();
    }
  }
  
  public void printStackTrace(PrintStream paramPrintStream)
  {
    super.printStackTrace(paramPrintStream);
    if (cause != null)
    {
      paramPrintStream.println("\nCAUSE:\n");
      cause.printStackTrace(paramPrintStream);
    }
  }
  
  public void printStackTrace(PrintWriter paramPrintWriter)
  {
    super.printStackTrace(paramPrintWriter);
    if (cause != null)
    {
      paramPrintWriter.println("\nCAUSE:\n");
      cause.printStackTrace(paramPrintWriter);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\SOAPExceptionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */