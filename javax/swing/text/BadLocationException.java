package javax.swing.text;

public class BadLocationException
  extends Exception
{
  private int offs;
  
  public BadLocationException(String paramString, int paramInt)
  {
    super(paramString);
    offs = paramInt;
  }
  
  public int offsetRequested()
  {
    return offs;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\BadLocationException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */