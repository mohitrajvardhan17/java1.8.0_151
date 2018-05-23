package java.lang.annotation;

public class IncompleteAnnotationException
  extends RuntimeException
{
  private static final long serialVersionUID = 8445097402741811912L;
  private Class<? extends Annotation> annotationType;
  private String elementName;
  
  public IncompleteAnnotationException(Class<? extends Annotation> paramClass, String paramString)
  {
    super(paramClass.getName() + " missing element " + paramString.toString());
    annotationType = paramClass;
    elementName = paramString;
  }
  
  public Class<? extends Annotation> annotationType()
  {
    return annotationType;
  }
  
  public String elementName()
  {
    return elementName;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\annotation\IncompleteAnnotationException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */