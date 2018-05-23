package java.lang;

public class EnumConstantNotPresentException
  extends RuntimeException
{
  private static final long serialVersionUID = -6046998521960521108L;
  private Class<? extends Enum> enumType;
  private String constantName;
  
  public EnumConstantNotPresentException(Class<? extends Enum> paramClass, String paramString)
  {
    super(paramClass.getName() + "." + paramString);
    enumType = paramClass;
    constantName = paramString;
  }
  
  public Class<? extends Enum> enumType()
  {
    return enumType;
  }
  
  public String constantName()
  {
    return constantName;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\EnumConstantNotPresentException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */