package sun.font;

public final class TextRecord
{
  public char[] text;
  public int start;
  public int limit;
  public int min;
  public int max;
  
  public TextRecord() {}
  
  public void init(char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    text = paramArrayOfChar;
    start = paramInt1;
    limit = paramInt2;
    min = paramInt3;
    max = paramInt4;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\font\TextRecord.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */