package java.lang.annotation;

import java.lang.reflect.Method;

public class AnnotationTypeMismatchException
  extends RuntimeException
{
  private static final long serialVersionUID = 8125925355765570191L;
  private final Method element;
  private final String foundType;
  
  public AnnotationTypeMismatchException(Method paramMethod, String paramString)
  {
    super("Incorrectly typed data found for annotation element " + paramMethod + " (Found data of type " + paramString + ")");
    element = paramMethod;
    foundType = paramString;
  }
  
  public Method element()
  {
    return element;
  }
  
  public String foundType()
  {
    return foundType;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\annotation\AnnotationTypeMismatchException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */