package java.text;

public final class StringCharacterIterator
  implements CharacterIterator
{
  private String text;
  private int begin;
  private int end;
  private int pos;
  
  public StringCharacterIterator(String paramString)
  {
    this(paramString, 0);
  }
  
  public StringCharacterIterator(String paramString, int paramInt)
  {
    this(paramString, 0, paramString.length(), paramInt);
  }
  
  public StringCharacterIterator(String paramString, int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    text = paramString;
    if ((paramInt1 < 0) || (paramInt1 > paramInt2) || (paramInt2 > paramString.length())) {
      throw new IllegalArgumentException("Invalid substring range");
    }
    if ((paramInt3 < paramInt1) || (paramInt3 > paramInt2)) {
      throw new IllegalArgumentException("Invalid position");
    }
    begin = paramInt1;
    end = paramInt2;
    pos = paramInt3;
  }
  
  public void setText(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    text = paramString;
    begin = 0;
    end = paramString.length();
    pos = 0;
  }
  
  public char first()
  {
    pos = begin;
    return current();
  }
  
  public char last()
  {
    if (end != begin) {
      pos = (end - 1);
    } else {
      pos = end;
    }
    return current();
  }
  
  public char setIndex(int paramInt)
  {
    if ((paramInt < begin) || (paramInt > end)) {
      throw new IllegalArgumentException("Invalid index");
    }
    pos = paramInt;
    return current();
  }
  
  public char current()
  {
    if ((pos >= begin) && (pos < end)) {
      return text.charAt(pos);
    }
    return 65535;
  }
  
  public char next()
  {
    if (pos < end - 1)
    {
      pos += 1;
      return text.charAt(pos);
    }
    pos = end;
    return 65535;
  }
  
  public char previous()
  {
    if (pos > begin)
    {
      pos -= 1;
      return text.charAt(pos);
    }
    return 65535;
  }
  
  public int getBeginIndex()
  {
    return begin;
  }
  
  public int getEndIndex()
  {
    return end;
  }
  
  public int getIndex()
  {
    return pos;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof StringCharacterIterator)) {
      return false;
    }
    StringCharacterIterator localStringCharacterIterator = (StringCharacterIterator)paramObject;
    if (hashCode() != localStringCharacterIterator.hashCode()) {
      return false;
    }
    if (!text.equals(text)) {
      return false;
    }
    return (pos == pos) && (begin == begin) && (end == end);
  }
  
  public int hashCode()
  {
    return text.hashCode() ^ pos ^ begin ^ end;
  }
  
  public Object clone()
  {
    try
    {
      StringCharacterIterator localStringCharacterIterator = (StringCharacterIterator)super.clone();
      return localStringCharacterIterator;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\text\StringCharacterIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */