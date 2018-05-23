package javax.xml.crypto;

import java.io.PrintStream;
import java.io.PrintWriter;

public class MarshalException
  extends Exception
{
  private static final long serialVersionUID = -863185580332643547L;
  private Throwable cause;
  
  public MarshalException() {}
  
  public MarshalException(String paramString)
  {
    super(paramString);
  }
  
  public MarshalException(String paramString, Throwable paramThrowable)
  {
    super(paramString);
    cause = paramThrowable;
  }
  
  public MarshalException(Throwable paramThrowable)
  {
    super(paramThrowable == null ? null : paramThrowable.toString());
    cause = paramThrowable;
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\crypto\MarshalException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */