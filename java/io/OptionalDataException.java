package java.io;

public class OptionalDataException
  extends ObjectStreamException
{
  private static final long serialVersionUID = -8011121865681257820L;
  public int length;
  public boolean eof;
  
  OptionalDataException(int paramInt)
  {
    eof = false;
    length = paramInt;
  }
  
  OptionalDataException(boolean paramBoolean)
  {
    length = 0;
    eof = paramBoolean;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\OptionalDataException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */