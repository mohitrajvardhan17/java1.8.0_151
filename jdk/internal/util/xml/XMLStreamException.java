package jdk.internal.util.xml;

public class XMLStreamException
  extends Exception
{
  private static final long serialVersionUID = 1L;
  protected Throwable nested;
  
  public XMLStreamException() {}
  
  public XMLStreamException(String paramString)
  {
    super(paramString);
  }
  
  public XMLStreamException(Throwable paramThrowable)
  {
    super(paramThrowable);
    nested = paramThrowable;
  }
  
  public XMLStreamException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
    nested = paramThrowable;
  }
  
  public Throwable getNestedException()
  {
    return nested;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\util\xml\XMLStreamException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */