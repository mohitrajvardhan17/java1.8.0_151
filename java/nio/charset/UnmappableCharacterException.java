package java.nio.charset;

public class UnmappableCharacterException
  extends CharacterCodingException
{
  private static final long serialVersionUID = -7026962371537706123L;
  private int inputLength;
  
  public UnmappableCharacterException(int paramInt)
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\charset\UnmappableCharacterException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */