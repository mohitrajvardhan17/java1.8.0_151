package javax.swing.text;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.TextHitInfo;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

class GlyphPainter2
  extends GlyphView.GlyphPainter
{
  TextLayout layout;
  
  public GlyphPainter2(TextLayout paramTextLayout)
  {
    layout = paramTextLayout;
  }
  
  public GlyphView.GlyphPainter getPainter(GlyphView paramGlyphView, int paramInt1, int paramInt2)
  {
    return null;
  }
  
  public float getSpan(GlyphView paramGlyphView, int paramInt1, int paramInt2, TabExpander paramTabExpander, float paramFloat)
  {
    if ((paramInt1 == paramGlyphView.getStartOffset()) && (paramInt2 == paramGlyphView.getEndOffset())) {
      return layout.getAdvance();
    }
    int i = paramGlyphView.getStartOffset();
    int j = paramInt1 - i;
    int k = paramInt2 - i;
    TextHitInfo localTextHitInfo1 = TextHitInfo.afterOffset(j);
    TextHitInfo localTextHitInfo2 = TextHitInfo.beforeOffset(k);
    float[] arrayOfFloat = layout.getCaretInfo(localTextHitInfo1);
    float f1 = arrayOfFloat[0];
    arrayOfFloat = layout.getCaretInfo(localTextHitInfo2);
    float f2 = arrayOfFloat[0];
    return f2 > f1 ? f2 - f1 : f1 - f2;
  }
  
  public float getHeight(GlyphView paramGlyphView)
  {
    return layout.getAscent() + layout.getDescent() + layout.getLeading();
  }
  
  public float getAscent(GlyphView paramGlyphView)
  {
    return layout.getAscent();
  }
  
  public float getDescent(GlyphView paramGlyphView)
  {
    return layout.getDescent();
  }
  
  public void paint(GlyphView paramGlyphView, Graphics paramGraphics, Shape paramShape, int paramInt1, int paramInt2)
  {
    if ((paramGraphics instanceof Graphics2D))
    {
      Rectangle2D localRectangle2D = paramShape.getBounds2D();
      Graphics2D localGraphics2D = (Graphics2D)paramGraphics;
      float f1 = (float)localRectangle2D.getY() + layout.getAscent() + layout.getLeading();
      float f2 = (float)localRectangle2D.getX();
      if ((paramInt1 > paramGlyphView.getStartOffset()) || (paramInt2 < paramGlyphView.getEndOffset())) {
        try
        {
          Shape localShape1 = paramGlyphView.modelToView(paramInt1, Position.Bias.Forward, paramInt2, Position.Bias.Backward, paramShape);
          Shape localShape2 = paramGraphics.getClip();
          localGraphics2D.clip(localShape1);
          layout.draw(localGraphics2D, f2, f1);
          paramGraphics.setClip(localShape2);
        }
        catch (BadLocationException localBadLocationException) {}
      } else {
        layout.draw(localGraphics2D, f2, f1);
      }
    }
  }
  
  public Shape modelToView(GlyphView paramGlyphView, int paramInt, Position.Bias paramBias, Shape paramShape)
    throws BadLocationException
  {
    int i = paramInt - paramGlyphView.getStartOffset();
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    TextHitInfo localTextHitInfo = paramBias == Position.Bias.Forward ? TextHitInfo.afterOffset(i) : TextHitInfo.beforeOffset(i);
    float[] arrayOfFloat = layout.getCaretInfo(localTextHitInfo);
    localRectangle2D.setRect(localRectangle2D.getX() + arrayOfFloat[0], localRectangle2D.getY(), 1.0D, localRectangle2D.getHeight());
    return localRectangle2D;
  }
  
  public int viewToModel(GlyphView paramGlyphView, float paramFloat1, float paramFloat2, Shape paramShape, Position.Bias[] paramArrayOfBias)
  {
    Rectangle2D localRectangle2D = (paramShape instanceof Rectangle2D) ? (Rectangle2D)paramShape : paramShape.getBounds2D();
    TextHitInfo localTextHitInfo = layout.hitTestChar(paramFloat1 - (float)localRectangle2D.getX(), 0.0F);
    int i = localTextHitInfo.getInsertionIndex();
    if (i == paramGlyphView.getEndOffset()) {
      i--;
    }
    paramArrayOfBias[0] = (localTextHitInfo.isLeadingEdge() ? Position.Bias.Forward : Position.Bias.Backward);
    return i + paramGlyphView.getStartOffset();
  }
  
  public int getBoundedPosition(GlyphView paramGlyphView, int paramInt, float paramFloat1, float paramFloat2)
  {
    if (paramFloat2 < 0.0F) {
      throw new IllegalArgumentException("Length must be >= 0.");
    }
    TextHitInfo localTextHitInfo;
    if (layout.isLeftToRight()) {
      localTextHitInfo = layout.hitTestChar(paramFloat2, 0.0F);
    } else {
      localTextHitInfo = layout.hitTestChar(layout.getAdvance() - paramFloat2, 0.0F);
    }
    return paramGlyphView.getStartOffset() + localTextHitInfo.getCharIndex();
  }
  
  public int getNextVisualPositionFrom(GlyphView paramGlyphView, int paramInt1, Position.Bias paramBias, Shape paramShape, int paramInt2, Position.Bias[] paramArrayOfBias)
    throws BadLocationException
  {
    Document localDocument = paramGlyphView.getDocument();
    int i = paramGlyphView.getStartOffset();
    int j = paramGlyphView.getEndOffset();
    boolean bool;
    Segment localSegment;
    char c;
    TextHitInfo localTextHitInfo1;
    TextHitInfo localTextHitInfo2;
    switch (paramInt2)
    {
    case 1: 
      break;
    case 5: 
      break;
    case 3: 
      bool = AbstractDocument.isLeftToRight(localDocument, i, j);
      if (i == localDocument.getLength())
      {
        if (paramInt1 == -1)
        {
          paramArrayOfBias[0] = Position.Bias.Forward;
          return i;
        }
        return -1;
      }
      if (paramInt1 == -1)
      {
        if (bool)
        {
          paramArrayOfBias[0] = Position.Bias.Forward;
          return i;
        }
        localSegment = paramGlyphView.getText(j - 1, j);
        c = array[offset];
        SegmentCache.releaseSharedSegment(localSegment);
        if (c == '\n')
        {
          paramArrayOfBias[0] = Position.Bias.Forward;
          return j - 1;
        }
        paramArrayOfBias[0] = Position.Bias.Backward;
        return j;
      }
      if (paramBias == Position.Bias.Forward) {
        localTextHitInfo1 = TextHitInfo.afterOffset(paramInt1 - i);
      } else {
        localTextHitInfo1 = TextHitInfo.beforeOffset(paramInt1 - i);
      }
      localTextHitInfo2 = layout.getNextRightHit(localTextHitInfo1);
      if (localTextHitInfo2 == null) {
        return -1;
      }
      if (bool != layout.isLeftToRight()) {
        localTextHitInfo2 = layout.getVisualOtherHit(localTextHitInfo2);
      }
      paramInt1 = localTextHitInfo2.getInsertionIndex() + i;
      if (paramInt1 == j)
      {
        localSegment = paramGlyphView.getText(j - 1, j);
        c = array[offset];
        SegmentCache.releaseSharedSegment(localSegment);
        if (c == '\n') {
          return -1;
        }
        paramArrayOfBias[0] = Position.Bias.Backward;
      }
      else
      {
        paramArrayOfBias[0] = Position.Bias.Forward;
      }
      return paramInt1;
    case 7: 
      bool = AbstractDocument.isLeftToRight(localDocument, i, j);
      if (i == localDocument.getLength())
      {
        if (paramInt1 == -1)
        {
          paramArrayOfBias[0] = Position.Bias.Forward;
          return i;
        }
        return -1;
      }
      if (paramInt1 == -1)
      {
        if (bool)
        {
          localSegment = paramGlyphView.getText(j - 1, j);
          c = array[offset];
          SegmentCache.releaseSharedSegment(localSegment);
          if ((c == '\n') || (Character.isSpaceChar(c)))
          {
            paramArrayOfBias[0] = Position.Bias.Forward;
            return j - 1;
          }
          paramArrayOfBias[0] = Position.Bias.Backward;
          return j;
        }
        paramArrayOfBias[0] = Position.Bias.Forward;
        return i;
      }
      if (paramBias == Position.Bias.Forward) {
        localTextHitInfo1 = TextHitInfo.afterOffset(paramInt1 - i);
      } else {
        localTextHitInfo1 = TextHitInfo.beforeOffset(paramInt1 - i);
      }
      localTextHitInfo2 = layout.getNextLeftHit(localTextHitInfo1);
      if (localTextHitInfo2 == null) {
        return -1;
      }
      if (bool != layout.isLeftToRight()) {
        localTextHitInfo2 = layout.getVisualOtherHit(localTextHitInfo2);
      }
      paramInt1 = localTextHitInfo2.getInsertionIndex() + i;
      if (paramInt1 == j)
      {
        localSegment = paramGlyphView.getText(j - 1, j);
        c = array[offset];
        SegmentCache.releaseSharedSegment(localSegment);
        if (c == '\n') {
          return -1;
        }
        paramArrayOfBias[0] = Position.Bias.Backward;
      }
      else
      {
        paramArrayOfBias[0] = Position.Bias.Forward;
      }
      return paramInt1;
    case 2: 
    case 4: 
    case 6: 
    default: 
      throw new IllegalArgumentException("Bad direction: " + paramInt2);
    }
    return paramInt1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\GlyphPainter2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */