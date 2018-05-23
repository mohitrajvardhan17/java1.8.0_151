package sun.font;

public final class FontRunIterator
{
  CompositeFont font;
  char[] text;
  int start;
  int limit;
  CompositeGlyphMapper mapper;
  int slot = -1;
  int pos;
  static final int SURROGATE_START = 65536;
  static final int LEAD_START = 55296;
  static final int LEAD_LIMIT = 56320;
  static final int TAIL_START = 56320;
  static final int TAIL_LIMIT = 57344;
  static final int LEAD_SURROGATE_SHIFT = 10;
  static final int SURROGATE_OFFSET = -56613888;
  static final int DONE = -1;
  
  public FontRunIterator() {}
  
  public void init(CompositeFont paramCompositeFont, char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    if ((paramCompositeFont == null) || (paramArrayOfChar == null) || (paramInt1 < 0) || (paramInt2 < paramInt1) || (paramInt2 > paramArrayOfChar.length)) {
      throw new IllegalArgumentException();
    }
    font = paramCompositeFont;
    text = paramArrayOfChar;
    start = paramInt1;
    limit = paramInt2;
    mapper = ((CompositeGlyphMapper)paramCompositeFont.getMapper());
    slot = -1;
    pos = paramInt1;
  }
  
  public PhysicalFont getFont()
  {
    return slot == -1 ? null : font.getSlotFont(slot);
  }
  
  public int getGlyphMask()
  {
    return slot << 24;
  }
  
  public int getPos()
  {
    return pos;
  }
  
  public boolean next(int paramInt1, int paramInt2)
  {
    if (pos == paramInt2) {
      return false;
    }
    int i = nextCodePoint(paramInt2);
    int j = mapper.charToGlyph(i) & 0xFF000000;
    slot = (j >>> 24);
    while (((i = nextCodePoint(paramInt2)) != -1) && ((mapper.charToGlyph(i) & 0xFF000000) == j)) {}
    pushback(i);
    return true;
  }
  
  public boolean next()
  {
    return next(0, limit);
  }
  
  final int nextCodePoint()
  {
    return nextCodePoint(limit);
  }
  
  final int nextCodePoint(int paramInt)
  {
    if (pos >= paramInt) {
      return -1;
    }
    int i = text[(pos++)];
    if ((i >= 55296) && (i < 56320) && (pos < paramInt))
    {
      int j = text[pos];
      if ((j >= 56320) && (j < 57344))
      {
        pos += 1;
        i = (i << 10) + j + -56613888;
      }
    }
    return i;
  }
  
  final void pushback(int paramInt)
  {
    if (paramInt >= 0) {
      if (paramInt >= 65536) {
        pos -= 2;
      } else {
        pos -= 1;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\font\FontRunIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */