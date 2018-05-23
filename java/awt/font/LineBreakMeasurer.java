package java.awt.font;

import java.text.AttributedCharacterIterator;
import java.text.BreakIterator;

public final class LineBreakMeasurer
{
  private BreakIterator breakIter;
  private int start;
  private int pos;
  private int limit;
  private TextMeasurer measurer;
  private CharArrayIterator charIter;
  
  public LineBreakMeasurer(AttributedCharacterIterator paramAttributedCharacterIterator, FontRenderContext paramFontRenderContext)
  {
    this(paramAttributedCharacterIterator, BreakIterator.getLineInstance(), paramFontRenderContext);
  }
  
  public LineBreakMeasurer(AttributedCharacterIterator paramAttributedCharacterIterator, BreakIterator paramBreakIterator, FontRenderContext paramFontRenderContext)
  {
    if (paramAttributedCharacterIterator.getEndIndex() - paramAttributedCharacterIterator.getBeginIndex() < 1) {
      throw new IllegalArgumentException("Text must contain at least one character.");
    }
    breakIter = paramBreakIterator;
    measurer = new TextMeasurer(paramAttributedCharacterIterator, paramFontRenderContext);
    limit = paramAttributedCharacterIterator.getEndIndex();
    pos = (start = paramAttributedCharacterIterator.getBeginIndex());
    charIter = new CharArrayIterator(measurer.getChars(), start);
    breakIter.setText(charIter);
  }
  
  public int nextOffset(float paramFloat)
  {
    return nextOffset(paramFloat, limit, false);
  }
  
  public int nextOffset(float paramFloat, int paramInt, boolean paramBoolean)
  {
    int i = pos;
    if (pos < limit)
    {
      if (paramInt <= pos) {
        throw new IllegalArgumentException("offsetLimit must be after current position");
      }
      int j = measurer.getLineBreakIndex(pos, paramFloat);
      if (j == limit)
      {
        i = limit;
      }
      else if (Character.isWhitespace(measurer.getChars()[(j - start)]))
      {
        i = breakIter.following(j);
      }
      else
      {
        int k = j + 1;
        if (k == limit)
        {
          breakIter.last();
          i = breakIter.previous();
        }
        else
        {
          i = breakIter.preceding(k);
        }
        if (i <= pos) {
          if (paramBoolean) {
            i = pos;
          } else {
            i = Math.max(pos + 1, j);
          }
        }
      }
    }
    if (i > paramInt) {
      i = paramInt;
    }
    return i;
  }
  
  public TextLayout nextLayout(float paramFloat)
  {
    return nextLayout(paramFloat, limit, false);
  }
  
  public TextLayout nextLayout(float paramFloat, int paramInt, boolean paramBoolean)
  {
    if (pos < limit)
    {
      int i = nextOffset(paramFloat, paramInt, paramBoolean);
      if (i == pos) {
        return null;
      }
      TextLayout localTextLayout = measurer.getLayout(pos, i);
      pos = i;
      return localTextLayout;
    }
    return null;
  }
  
  public int getPosition()
  {
    return pos;
  }
  
  public void setPosition(int paramInt)
  {
    if ((paramInt < start) || (paramInt > limit)) {
      throw new IllegalArgumentException("position is out of range");
    }
    pos = paramInt;
  }
  
  public void insertChar(AttributedCharacterIterator paramAttributedCharacterIterator, int paramInt)
  {
    measurer.insertChar(paramAttributedCharacterIterator, paramInt);
    limit = paramAttributedCharacterIterator.getEndIndex();
    pos = (start = paramAttributedCharacterIterator.getBeginIndex());
    charIter.reset(measurer.getChars(), paramAttributedCharacterIterator.getBeginIndex());
    breakIter.setText(charIter);
  }
  
  public void deleteChar(AttributedCharacterIterator paramAttributedCharacterIterator, int paramInt)
  {
    measurer.deleteChar(paramAttributedCharacterIterator, paramInt);
    limit = paramAttributedCharacterIterator.getEndIndex();
    pos = (start = paramAttributedCharacterIterator.getBeginIndex());
    charIter.reset(measurer.getChars(), start);
    breakIter.setText(charIter);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\font\LineBreakMeasurer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */