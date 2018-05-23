package java.net;

public class URISyntaxException
  extends Exception
{
  private static final long serialVersionUID = 2137979680897488891L;
  private String input;
  private int index;
  
  public URISyntaxException(String paramString1, String paramString2, int paramInt)
  {
    super(paramString2);
    if ((paramString1 == null) || (paramString2 == null)) {
      throw new NullPointerException();
    }
    if (paramInt < -1) {
      throw new IllegalArgumentException();
    }
    input = paramString1;
    index = paramInt;
  }
  
  public URISyntaxException(String paramString1, String paramString2)
  {
    this(paramString1, paramString2, -1);
  }
  
  public String getInput()
  {
    return input;
  }
  
  public String getReason()
  {
    return super.getMessage();
  }
  
  public int getIndex()
  {
    return index;
  }
  
  public String getMessage()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(getReason());
    if (index > -1)
    {
      localStringBuffer.append(" at index ");
      localStringBuffer.append(index);
    }
    localStringBuffer.append(": ");
    localStringBuffer.append(input);
    return localStringBuffer.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\URISyntaxException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */