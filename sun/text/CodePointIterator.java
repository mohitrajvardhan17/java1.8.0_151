package sun.text;

import java.text.CharacterIterator;

public abstract class CodePointIterator
{
  public static final int DONE = -1;
  
  public CodePointIterator() {}
  
  public abstract void setToStart();
  
  public abstract void setToLimit();
  
  public abstract int next();
  
  public abstract int prev();
  
  public abstract int charIndex();
  
  public static CodePointIterator create(char[] paramArrayOfChar)
  {
    return new CharArrayCodePointIterator(paramArrayOfChar);
  }
  
  public static CodePointIterator create(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    return new CharArrayCodePointIterator(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public static CodePointIterator create(CharSequence paramCharSequence)
  {
    return new CharSequenceCodePointIterator(paramCharSequence);
  }
  
  public static CodePointIterator create(CharacterIterator paramCharacterIterator)
  {
    return new CharacterIteratorCodePointIterator(paramCharacterIterator);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\text\CodePointIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */