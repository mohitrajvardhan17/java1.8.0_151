package java.lang;

public class ClassCircularityError
  extends LinkageError
{
  private static final long serialVersionUID = 1054362542914539689L;
  
  public ClassCircularityError() {}
  
  public ClassCircularityError(String paramString)
  {
    super(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\ClassCircularityError.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */