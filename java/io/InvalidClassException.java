package java.io;

public class InvalidClassException
  extends ObjectStreamException
{
  private static final long serialVersionUID = -4333316296251054416L;
  public String classname;
  
  public InvalidClassException(String paramString)
  {
    super(paramString);
  }
  
  public InvalidClassException(String paramString1, String paramString2)
  {
    super(paramString2);
    classname = paramString1;
  }
  
  public String getMessage()
  {
    if (classname == null) {
      return super.getMessage();
    }
    return classname + "; " + super.getMessage();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\InvalidClassException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */