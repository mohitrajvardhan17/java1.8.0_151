package javax.xml.bind;

import java.io.PrintStream;

public class TypeConstraintException
  extends RuntimeException
{
  private String errorCode;
  private volatile Throwable linkedException;
  static final long serialVersionUID = -3059799699420143848L;
  
  public TypeConstraintException(String paramString)
  {
    this(paramString, null, null);
  }
  
  public TypeConstraintException(String paramString1, String paramString2)
  {
    this(paramString1, paramString2, null);
  }
  
  public TypeConstraintException(Throwable paramThrowable)
  {
    this(null, null, paramThrowable);
  }
  
  public TypeConstraintException(String paramString, Throwable paramThrowable)
  {
    this(paramString, null, paramThrowable);
  }
  
  public TypeConstraintException(String paramString1, String paramString2, Throwable paramThrowable)
  {
    super(paramString1);
    errorCode = paramString2;
    linkedException = paramThrowable;
  }
  
  public String getErrorCode()
  {
    return errorCode;
  }
  
  public Throwable getLinkedException()
  {
    return linkedException;
  }
  
  public void setLinkedException(Throwable paramThrowable)
  {
    linkedException = paramThrowable;
  }
  
  public String toString()
  {
    return super.toString() + "\n - with linked exception:\n[" + linkedException.toString() + "]";
  }
  
  public void printStackTrace(PrintStream paramPrintStream)
  {
    if (linkedException != null)
    {
      linkedException.printStackTrace(paramPrintStream);
      paramPrintStream.println("--------------- linked to ------------------");
    }
    super.printStackTrace(paramPrintStream);
  }
  
  public void printStackTrace()
  {
    printStackTrace(System.err);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\bind\TypeConstraintException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */