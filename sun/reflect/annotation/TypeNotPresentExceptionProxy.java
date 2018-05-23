package sun.reflect.annotation;

public class TypeNotPresentExceptionProxy
  extends ExceptionProxy
{
  private static final long serialVersionUID = 5565925172427947573L;
  String typeName;
  Throwable cause;
  
  public TypeNotPresentExceptionProxy(String paramString, Throwable paramThrowable)
  {
    typeName = paramString;
    cause = paramThrowable;
  }
  
  protected RuntimeException generateException()
  {
    return new TypeNotPresentException(typeName, cause);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\annotation\TypeNotPresentExceptionProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */