package java.security;

public class InvalidParameterException
  extends IllegalArgumentException
{
  private static final long serialVersionUID = -857968536935667808L;
  
  public InvalidParameterException() {}
  
  public InvalidParameterException(String paramString)
  {
    super(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\InvalidParameterException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */