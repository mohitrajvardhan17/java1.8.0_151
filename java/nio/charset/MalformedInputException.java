package java.nio.charset;

public class MalformedInputException
  extends CharacterCodingException
{
  private static final long serialVersionUID = -3438823399834806194L;
  private int inputLength;
  
  public MalformedInputException(int paramInt)
  {
    inputLength = paramInt;
  }
  
  public int getInputLength()
  {
    return inputLength;
  }
  
  public String getMessage()
  {
    return "Input length = " + inputLength;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\charset\MalformedInputException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */