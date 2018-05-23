package sun.reflect.annotation;

import java.lang.annotation.AnnotationTypeMismatchException;
import java.lang.reflect.Method;

class AnnotationTypeMismatchExceptionProxy
  extends ExceptionProxy
{
  private static final long serialVersionUID = 7844069490309503934L;
  private Method member;
  private String foundType;
  
  AnnotationTypeMismatchExceptionProxy(String paramString)
  {
    foundType = paramString;
  }
  
  AnnotationTypeMismatchExceptionProxy setMember(Method paramMethod)
  {
    member = paramMethod;
    return this;
  }
  
  protected RuntimeException generateException()
  {
    return new AnnotationTypeMismatchException(member, foundType);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\annotation\AnnotationTypeMismatchExceptionProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */