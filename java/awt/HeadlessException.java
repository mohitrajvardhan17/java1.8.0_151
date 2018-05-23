package java.awt;

public class HeadlessException
  extends UnsupportedOperationException
{
  private static final long serialVersionUID = 167183644944358563L;
  
  public HeadlessException() {}
  
  public HeadlessException(String paramString)
  {
    super(paramString);
  }
  
  public String getMessage()
  {
    String str1 = super.getMessage();
    String str2 = GraphicsEnvironment.getHeadlessMessage();
    if (str1 == null) {
      return str2;
    }
    if (str2 == null) {
      return str1;
    }
    return str1 + str2;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\HeadlessException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */