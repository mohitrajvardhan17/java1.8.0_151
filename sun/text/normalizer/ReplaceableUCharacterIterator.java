package sun.text.normalizer;

public class ReplaceableUCharacterIterator
  extends UCharacterIterator
{
  private Replaceable replaceable;
  private int currentIndex;
  
  public ReplaceableUCharacterIterator(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException();
    }
    replaceable = new ReplaceableString(paramString);
    currentIndex = 0;
  }
  
  public ReplaceableUCharacterIterator(StringBuffer paramStringBuffer)
  {
    if (paramStringBuffer == null) {
      throw new IllegalArgumentException();
    }
    replaceable = new ReplaceableString(paramStringBuffer);
    currentIndex = 0;
  }
  
  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException) {}
    return null;
  }
  
  public int current()
  {
    if (currentIndex < replaceable.length()) {
      return replaceable.charAt(currentIndex);
    }
    return -1;
  }
  
  public int getLength()
  {
    return replaceable.length();
  }
  
  public int getIndex()
  {
    return currentIndex;
  }
  
  public int next()
  {
    if (currentIndex < replaceable.length()) {
      return replaceable.charAt(currentIndex++);
    }
    return -1;
  }
  
  public int previous()
  {
    if (currentIndex > 0) {
      return replaceable.charAt(--currentIndex);
    }
    return -1;
  }
  
  public void setIndex(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > replaceable.length())) {
      throw new IllegalArgumentException();
    }
    currentIndex = paramInt;
  }
  
  public int getText(char[] paramArrayOfChar, int paramInt)
  {
    int i = replaceable.length();
    if ((paramInt < 0) || (paramInt + i > paramArrayOfChar.length)) {
      throw new IndexOutOfBoundsException(Integer.toString(i));
    }
    replaceable.getChars(0, i, paramArrayOfChar, paramInt);
    return i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\text\normalizer\ReplaceableUCharacterIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */