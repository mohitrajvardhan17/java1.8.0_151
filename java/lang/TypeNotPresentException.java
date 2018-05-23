package java.lang;

public class TypeNotPresentException
  extends RuntimeException
{
  private static final long serialVersionUID = -5101214195716534496L;
  private String typeName;
  
  public TypeNotPresentException(String paramString, Throwable paramThrowable)
  {
    super("Type " + paramString + " not present", paramThrowable);
    typeName = paramString;
  }
  
  public String typeName()
  {
    return typeName;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\TypeNotPresentException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */