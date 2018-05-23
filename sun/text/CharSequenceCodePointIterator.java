package sun.text;

final class CharSequenceCodePointIterator
  extends CodePointIterator
{
  private CharSequence text;
  private int index;
  
  public CharSequenceCodePointIterator(CharSequence paramCharSequence)
  {
    text = paramCharSequence;
  }
  
  public void setToStart()
  {
    index = 0;
  }
  
  public void setToLimit()
  {
    index = text.length();
  }
  
  public int next()
  {
    if (index < text.length())
    {
      char c1 = text.charAt(index++);
      if ((Character.isHighSurrogate(c1)) && (index < text.length()))
      {
        char c2 = text.charAt(index + 1);
        if (Character.isLowSurrogate(c2))
        {
          index += 1;
          return Character.toCodePoint(c1, c2);
        }
      }
      return c1;
    }
    return -1;
  }
  
  public int prev()
  {
    if (index > 0)
    {
      char c1 = text.charAt(--index);
      if ((Character.isLowSurrogate(c1)) && (index > 0))
      {
        char c2 = text.charAt(index - 1);
        if (Character.isHighSurrogate(c2))
        {
          index -= 1;
          return Character.toCodePoint(c2, c1);
        }
      }
      return c1;
    }
    return -1;
  }
  
  public int charIndex()
  {
    return index;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\text\CharSequenceCodePointIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */