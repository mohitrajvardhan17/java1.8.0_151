package javax.swing.text;

import java.text.BreakIterator;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Arrays;

class WhitespaceBasedBreakIterator
  extends BreakIterator
{
  private char[] text = new char[0];
  private int[] breaks = { 0 };
  private int pos = 0;
  
  WhitespaceBasedBreakIterator() {}
  
  public void setText(CharacterIterator paramCharacterIterator)
  {
    int i = paramCharacterIterator.getBeginIndex();
    text = new char[paramCharacterIterator.getEndIndex() - i];
    int[] arrayOfInt = new int[text.length + 1];
    int j = 0;
    arrayOfInt[(j++)] = i;
    int k = 0;
    int m = 0;
    int i1;
    for (int n = paramCharacterIterator.first(); n != 65535; i1 = paramCharacterIterator.next())
    {
      text[k] = n;
      boolean bool = Character.isWhitespace(n);
      if ((m != 0) && (!bool)) {
        arrayOfInt[(j++)] = (k + i);
      }
      m = bool;
      k++;
    }
    if (text.length > 0) {
      arrayOfInt[(j++)] = (text.length + i);
    }
    System.arraycopy(arrayOfInt, 0, breaks = new int[j], 0, j);
  }
  
  public CharacterIterator getText()
  {
    return new StringCharacterIterator(new String(text));
  }
  
  public int first()
  {
    return breaks[(pos = 0)];
  }
  
  public int last()
  {
    return breaks[(pos = breaks.length - 1)];
  }
  
  public int current()
  {
    return breaks[pos];
  }
  
  public int next()
  {
    return pos == breaks.length - 1 ? -1 : breaks[(++pos)];
  }
  
  public int previous()
  {
    return pos == 0 ? -1 : breaks[(--pos)];
  }
  
  public int next(int paramInt)
  {
    return checkhit(pos + paramInt);
  }
  
  public int following(int paramInt)
  {
    return adjacent(paramInt, 1);
  }
  
  public int preceding(int paramInt)
  {
    return adjacent(paramInt, -1);
  }
  
  private int checkhit(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= breaks.length)) {
      return -1;
    }
    return breaks[(pos = paramInt)];
  }
  
  private int adjacent(int paramInt1, int paramInt2)
  {
    int i = Arrays.binarySearch(breaks, paramInt1);
    int j = i < 0 ? -2 : paramInt2 < 0 ? -1 : 0;
    return checkhit(Math.abs(i) + paramInt2 + j);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\WhitespaceBasedBreakIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */