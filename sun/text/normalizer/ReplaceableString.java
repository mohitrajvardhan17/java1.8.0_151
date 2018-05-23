package sun.text.normalizer;

public class ReplaceableString
  implements Replaceable
{
  private StringBuffer buf;
  
  public ReplaceableString(String paramString)
  {
    buf = new StringBuffer(paramString);
  }
  
  public ReplaceableString(StringBuffer paramStringBuffer)
  {
    buf = paramStringBuffer;
  }
  
  public int length()
  {
    return buf.length();
  }
  
  public char charAt(int paramInt)
  {
    return buf.charAt(paramInt);
  }
  
  public void getChars(int paramInt1, int paramInt2, char[] paramArrayOfChar, int paramInt3)
  {
    Utility.getChars(buf, paramInt1, paramInt2, paramArrayOfChar, paramInt3);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\text\normalizer\ReplaceableString.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */