package sun.reflect.annotation;

public class EnumConstantNotPresentExceptionProxy
  extends ExceptionProxy
{
  private static final long serialVersionUID = -604662101303187330L;
  Class<? extends Enum<?>> enumType;
  String constName;
  
  public EnumConstantNotPresentExceptionProxy(Class<? extends Enum<?>> paramClass, String paramString)
  {
    enumType = paramClass;
    constName = paramString;
  }
  
  protected RuntimeException generateException()
  {
    return new EnumConstantNotPresentException(enumType, constName);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\annotation\EnumConstantNotPresentExceptionProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */