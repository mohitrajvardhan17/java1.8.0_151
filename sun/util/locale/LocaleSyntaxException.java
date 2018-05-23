package sun.util.locale;

public class LocaleSyntaxException
  extends Exception
{
  private static final long serialVersionUID = 1L;
  private int index = -1;
  
  public LocaleSyntaxException(String paramString)
  {
    this(paramString, 0);
  }
  
  public LocaleSyntaxException(String paramString, int paramInt)
  {
    super(paramString);
    index = paramInt;
  }
  
  public int getErrorIndex()
  {
    return index;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\util\locale\LocaleSyntaxException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */