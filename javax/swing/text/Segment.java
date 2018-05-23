package javax.swing.text;

import java.text.CharacterIterator;

public class Segment
  implements Cloneable, CharacterIterator, CharSequence
{
  public char[] array;
  public int offset;
  public int count;
  private boolean partialReturn;
  private int pos;
  
  public Segment()
  {
    this(null, 0, 0);
  }
  
  public Segment(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    array = paramArrayOfChar;
    offset = paramInt1;
    count = paramInt2;
    partialReturn = false;
  }
  
  public void setPartialReturn(boolean paramBoolean)
  {
    partialReturn = paramBoolean;
  }
  
  public boolean isPartialReturn()
  {
    return partialReturn;
  }
  
  public String toString()
  {
    if (array != null) {
      return new String(array, offset, count);
    }
    return "";
  }
  
  public char first()
  {
    pos = offset;
    if (count != 0) {
      return array[pos];
    }
    return 65535;
  }
  
  public char last()
  {
    pos = (offset + count);
    if (count != 0)
    {
      pos -= 1;
      return array[pos];
    }
    return 65535;
  }
  
  public char current()
  {
    if ((count != 0) && (pos < offset + count)) {
      return array[pos];
    }
    return 65535;
  }
  
  public char next()
  {
    pos += 1;
    int i = offset + count;
    if (pos >= i)
    {
      pos = i;
      return 65535;
    }
    return current();
  }
  
  public char previous()
  {
    if (pos == offset) {
      return 65535;
    }
    pos -= 1;
    return current();
  }
  
  public char setIndex(int paramInt)
  {
    int i = offset + count;
    if ((paramInt < offset) || (paramInt > i)) {
      throw new IllegalArgumentException("bad position: " + paramInt);
    }
    pos = paramInt;
    if ((pos != i) && (count != 0)) {
      return array[pos];
    }
    return 65535;
  }
  
  public int getBeginIndex()
  {
    return offset;
  }
  
  public int getEndIndex()
  {
    return offset + count;
  }
  
  public int getIndex()
  {
    return pos;
  }
  
  public char charAt(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= count)) {
      throw new StringIndexOutOfBoundsException(paramInt);
    }
    return array[(offset + paramInt)];
  }
  
  public int length()
  {
    return count;
  }
  
  public CharSequence subSequence(int paramInt1, int paramInt2)
  {
    if (paramInt1 < 0) {
      throw new StringIndexOutOfBoundsException(paramInt1);
    }
    if (paramInt2 > count) {
      throw new StringIndexOutOfBoundsException(paramInt2);
    }
    if (paramInt1 > paramInt2) {
      throw new StringIndexOutOfBoundsException(paramInt2 - paramInt1);
    }
    Segment localSegment = new Segment();
    array = array;
    offset += paramInt1;
    count = (paramInt2 - paramInt1);
    return localSegment;
  }
  
  public Object clone()
  {
    Object localObject;
    try
    {
      localObject = super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      localObject = null;
    }
    return localObject;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\Segment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */