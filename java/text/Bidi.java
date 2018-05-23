package java.text;

import sun.text.bidi.BidiBase;

public final class Bidi
{
  public static final int DIRECTION_LEFT_TO_RIGHT = 0;
  public static final int DIRECTION_RIGHT_TO_LEFT = 1;
  public static final int DIRECTION_DEFAULT_LEFT_TO_RIGHT = -2;
  public static final int DIRECTION_DEFAULT_RIGHT_TO_LEFT = -1;
  private BidiBase bidiBase;
  
  public Bidi(String paramString, int paramInt)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("paragraph is null");
    }
    bidiBase = new BidiBase(paramString.toCharArray(), 0, null, 0, paramString.length(), paramInt);
  }
  
  public Bidi(AttributedCharacterIterator paramAttributedCharacterIterator)
  {
    if (paramAttributedCharacterIterator == null) {
      throw new IllegalArgumentException("paragraph is null");
    }
    bidiBase = new BidiBase(0, 0);
    bidiBase.setPara(paramAttributedCharacterIterator);
  }
  
  public Bidi(char[] paramArrayOfChar, int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3, int paramInt4)
  {
    if (paramArrayOfChar == null) {
      throw new IllegalArgumentException("text is null");
    }
    if (paramInt3 < 0) {
      throw new IllegalArgumentException("bad length: " + paramInt3);
    }
    if ((paramInt1 < 0) || (paramInt3 > paramArrayOfChar.length - paramInt1)) {
      throw new IllegalArgumentException("bad range: " + paramInt1 + " length: " + paramInt3 + " for text of length: " + paramArrayOfChar.length);
    }
    if ((paramArrayOfByte != null) && ((paramInt2 < 0) || (paramInt3 > paramArrayOfByte.length - paramInt2))) {
      throw new IllegalArgumentException("bad range: " + paramInt2 + " length: " + paramInt3 + " for embeddings of length: " + paramArrayOfChar.length);
    }
    bidiBase = new BidiBase(paramArrayOfChar, paramInt1, paramArrayOfByte, paramInt2, paramInt3, paramInt4);
  }
  
  public Bidi createLineBidi(int paramInt1, int paramInt2)
  {
    AttributedString localAttributedString = new AttributedString("");
    Bidi localBidi = new Bidi(localAttributedString.getIterator());
    return bidiBase.setLine(this, bidiBase, localBidi, bidiBase, paramInt1, paramInt2);
  }
  
  public boolean isMixed()
  {
    return bidiBase.isMixed();
  }
  
  public boolean isLeftToRight()
  {
    return bidiBase.isLeftToRight();
  }
  
  public boolean isRightToLeft()
  {
    return bidiBase.isRightToLeft();
  }
  
  public int getLength()
  {
    return bidiBase.getLength();
  }
  
  public boolean baseIsLeftToRight()
  {
    return bidiBase.baseIsLeftToRight();
  }
  
  public int getBaseLevel()
  {
    return bidiBase.getParaLevel();
  }
  
  public int getLevelAt(int paramInt)
  {
    return bidiBase.getLevelAt(paramInt);
  }
  
  public int getRunCount()
  {
    return bidiBase.countRuns();
  }
  
  public int getRunLevel(int paramInt)
  {
    return bidiBase.getRunLevel(paramInt);
  }
  
  public int getRunStart(int paramInt)
  {
    return bidiBase.getRunStart(paramInt);
  }
  
  public int getRunLimit(int paramInt)
  {
    return bidiBase.getRunLimit(paramInt);
  }
  
  public static boolean requiresBidi(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    return BidiBase.requiresBidi(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public static void reorderVisually(byte[] paramArrayOfByte, int paramInt1, Object[] paramArrayOfObject, int paramInt2, int paramInt3)
  {
    BidiBase.reorderVisually(paramArrayOfByte, paramInt1, paramArrayOfObject, paramInt2, paramInt3);
  }
  
  public String toString()
  {
    return bidiBase.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\text\Bidi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */