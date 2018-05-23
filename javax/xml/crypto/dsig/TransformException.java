package javax.xml.crypto.dsig;

import java.io.PrintStream;
import java.io.PrintWriter;

public class TransformException
  extends Exception
{
  private static final long serialVersionUID = 5082634801360427800L;
  private Throwable cause;
  
  public TransformException() {}
  
  public TransformException(String paramString)
  {
    super(paramString);
  }
  
  public TransformException(String paramString, Throwable paramThrowable)
  {
    super(paramString);
    cause = paramThrowable;
  }
  
  public TransformException(Throwable paramThrowable)
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
    if (cause != null) {
      cause.printStackTrace();
    }
  }
  
  public void printStackTrace(PrintStream paramPrintStream)
  {
    super.printStackTrace(paramPrintStream);
    if (cause != null) {
      cause.printStackTrace(paramPrintStream);
    }
  }
  
  public void printStackTrace(PrintWriter paramPrintWriter)
  {
    super.printStackTrace(paramPrintWriter);
    if (cause != null) {
      cause.printStackTrace(paramPrintWriter);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\crypto\dsig\TransformException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */