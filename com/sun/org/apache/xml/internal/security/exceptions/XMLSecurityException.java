package com.sun.org.apache.xml.internal.security.exceptions;

import com.sun.org.apache.xml.internal.security.utils.I18n;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.MessageFormat;

public class XMLSecurityException
  extends Exception
{
  private static final long serialVersionUID = 1L;
  protected String msgID;
  
  public XMLSecurityException()
  {
    super("Missing message string");
    msgID = null;
  }
  
  public XMLSecurityException(String paramString)
  {
    super(I18n.getExceptionMessage(paramString));
    msgID = paramString;
  }
  
  public XMLSecurityException(String paramString, Object[] paramArrayOfObject)
  {
    super(MessageFormat.format(I18n.getExceptionMessage(paramString), paramArrayOfObject));
    msgID = paramString;
  }
  
  public XMLSecurityException(Exception paramException)
  {
    super("Missing message ID to locate message string in resource bundle \"com/sun/org/apache/xml/internal/security/resource/xmlsecurity\". Original Exception was a " + paramException.getClass().getName() + " and message " + paramException.getMessage(), paramException);
  }
  
  public XMLSecurityException(String paramString, Exception paramException)
  {
    super(I18n.getExceptionMessage(paramString, paramException), paramException);
    msgID = paramString;
  }
  
  public XMLSecurityException(String paramString, Object[] paramArrayOfObject, Exception paramException)
  {
    super(MessageFormat.format(I18n.getExceptionMessage(paramString), paramArrayOfObject), paramException);
    msgID = paramString;
  }
  
  public String getMsgID()
  {
    if (msgID == null) {
      return "Missing message ID";
    }
    return msgID;
  }
  
  public String toString()
  {
    String str1 = getClass().getName();
    String str2 = super.getLocalizedMessage();
    if (str2 != null) {
      str2 = str1 + ": " + str2;
    } else {
      str2 = str1;
    }
    if (super.getCause() != null) {
      str2 = str2 + "\nOriginal Exception was " + super.getCause().toString();
    }
    return str2;
  }
  
  public void printStackTrace()
  {
    synchronized (System.err)
    {
      super.printStackTrace(System.err);
    }
  }
  
  public void printStackTrace(PrintWriter paramPrintWriter)
  {
    super.printStackTrace(paramPrintWriter);
  }
  
  public void printStackTrace(PrintStream paramPrintStream)
  {
    super.printStackTrace(paramPrintStream);
  }
  
  public Exception getOriginalException()
  {
    if ((getCause() instanceof Exception)) {
      return (Exception)getCause();
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\exceptions\XMLSecurityException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */