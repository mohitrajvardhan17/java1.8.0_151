package javax.xml.crypto;

import java.io.PrintStream;
import java.io.PrintWriter;

public class URIReferenceException
  extends Exception
{
  private static final long serialVersionUID = 7173469703932561419L;
  private Throwable cause;
  private URIReference uriReference;
  
  public URIReferenceException() {}
  
  public URIReferenceException(String paramString)
  {
    super(paramString);
  }
  
  public URIReferenceException(String paramString, Throwable paramThrowable)
  {
    super(paramString);
    cause = paramThrowable;
  }
  
  public URIReferenceException(String paramString, Throwable paramThrowable, URIReference paramURIReference)
  {
    this(paramString, paramThrowable);
    if (paramURIReference == null) {
      throw new NullPointerException("uriReference cannot be null");
    }
    uriReference = paramURIReference;
  }
  
  public URIReferenceException(Throwable paramThrowable)
  {
    super(paramThrowable == null ? null : paramThrowable.toString());
    cause = paramThrowable;
  }
  
  public URIReference getURIReference()
  {
    return uriReference;
  }
  
  public Throwable getCause()
  {
    return cause;
  }
  
  public void printStackTrace()
  {
    super.printStackTrace();
  }
  
  public void printStackTrace(PrintStream paramPrintStream)
  {
    super.printStackTrace(paramPrintStream);
  }
  
  public void printStackTrace(PrintWriter paramPrintWriter)
  {
    super.printStackTrace(paramPrintWriter);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\crypto\URIReferenceException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */