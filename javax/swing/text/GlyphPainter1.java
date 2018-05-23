package javax.swing.text;

import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;

class GlyphPainter1
  extends GlyphView.GlyphPainter
{
  FontMetrics metrics;
  
  GlyphPainter1() {}
  
  public float getSpan(GlyphView paramGlyphView, int paramInt1, int paramInt2, TabExpander paramTabExpander, float paramFloat)
  {
    sync(paramGlyphView);
    Segment localSegment = paramGlyphView.getText(paramInt1, paramInt2);
    int[] arrayOfInt = getJustificationData(paramGlyphView);
    int i = Utilities.getTabbedTextWidth(paramGlyphView, localSegment, metrics, (int)paramFloat, paramTabExpander, paramInt1, arrayOfInt);
    SegmentCache.releaseSharedSegment(localSegment);
    return i;
  }
  
  public float getHeight(GlyphView paramGlyphView)
  {
    sync(paramGlyphView);
    return metrics.getHeight();
  }
  
  public float getAscent(GlyphView paramGlyphView)
  {
    sync(paramGlyphView);
    return metrics.getAscent();
  }
  
  public float getDescent(GlyphView paramGlyphView)
  {
    sync(paramGlyphView);
    return metrics.getDescent();
  }
  
  public void paint(GlyphView paramGlyphView, Graphics paramGraphics, Shape paramShape, int paramInt1, int paramInt2)
  {
    sync(paramGlyphView);
    TabExpander localTabExpander = paramGlyphView.getTabExpander();
    Rectangle localRectangle = (paramShape instanceof Rectangle) ? (Rectangle)paramShape : paramShape.getBounds();
    int i = x;
    int j = paramGlyphView.getStartOffset();
    int[] arrayOfInt = getJustificationData(paramGlyphView);
    if (j != paramInt1)
    {
      localSegment = paramGlyphView.getText(j, paramInt1);
      k = Utilities.getTabbedTextWidth(paramGlyphView, localSegment, metrics, i, localTabExpander, j, arrayOfInt);
      i += k;
      SegmentCache.releaseSharedSegment(localSegment);
    }
    int k = y + metrics.getHeight() - metrics.getDescent();
    Segment localSegment = paramGlyphView.getText(paramInt1, paramInt2);
    paramGraphics.setFont(metrics.getFont());
    Utilities.drawTabbedText(paramGlyphView, localSegment, i, k, paramGraphics, localTabExpander, paramInt1, arrayOfInt);
    SegmentCache.releaseSharedSegment(localSegment);
  }
  
  public Shape modelToView(GlyphView paramGlyphView, int paramInt, Position.Bias paramBias, Shape paramShape)
    throws BadLocationException
  {
    sync(paramGlyphView);
    Rectangle localRectangle = (paramShape instanceof Rectangle) ? (Rectangle)paramShape : paramShape.getBounds();
    int i = paramGlyphView.getStartOffset();
    int j = paramGlyphView.getEndOffset();
    TabExpander localTabExpander = paramGlyphView.getTabExpander();
    if (paramInt == j) {
      return new Rectangle(x + width, y, 0, metrics.getHeight());
    }
    if ((paramInt >= i) && (paramInt <= j))
    {
      Segment localSegment = paramGlyphView.getText(i, paramInt);
      int[] arrayOfInt = getJustificationData(paramGlyphView);
      int k = Utilities.getTabbedTextWidth(paramGlyphView, localSegment, metrics, x, localTabExpander, i, arrayOfInt);
      SegmentCache.releaseSharedSegment(localSegment);
      return new Rectangle(x + k, y, 0, metrics.getHeight());
    }
    throw new BadLocationException("modelToView - can't convert", j);
  }
  
  public int viewToModel(GlyphView paramGlyphView, float paramFloat1, float paramFloat2, Shape paramShape, Position.Bias[] paramArrayOfBias)
  {
    sync(paramGlyphView);
    Rectangle localRectangle = (paramShape instanceof Rectangle) ? (Rectangle)paramShape : paramShape.getBounds();
    int i = paramGlyphView.getStartOffset();
    int j = paramGlyphView.getEndOffset();
    TabExpander localTabExpander = paramGlyphView.getTabExpander();
    Segment localSegment = paramGlyphView.getText(i, j);
    int[] arrayOfInt = getJustificationData(paramGlyphView);
    int k = Utilities.getTabbedTextOffset(paramGlyphView, localSegment, metrics, x, (int)paramFloat1, localTabExpander, i, arrayOfInt);
    SegmentCache.releaseSharedSegment(localSegment);
    int m = i + k;
    if (m == j) {
      m--;
    }
    paramArrayOfBias[0] = Position.Bias.Forward;
    return m;
  }
  
  public int getBoundedPosition(GlyphView paramGlyphView, int paramInt, float paramFloat1, float paramFloat2)
  {
    sync(paramGlyphView);
    TabExpander localTabExpander = paramGlyphView.getTabExpander();
    Segment localSegment = paramGlyphView.getText(paramInt, paramGlyphView.getEndOffset());
    int[] arrayOfInt = getJustificationData(paramGlyphView);
    int i = Utilities.getTabbedTextOffset(paramGlyphView, localSegment, metrics, (int)paramFloat1, (int)(paramFloat1 + paramFloat2), localTabExpander, paramInt, false, arrayOfInt);
    SegmentCache.releaseSharedSegment(localSegment);
    int j = paramInt + i;
    return j;
  }
  
  void sync(GlyphView paramGlyphView)
  {
    Font localFont = paramGlyphView.getFont();
    if ((metrics == null) || (!localFont.equals(metrics.getFont())))
    {
      Container localContainer = paramGlyphView.getContainer();
      metrics = (localContainer != null ? localContainer.getFontMetrics(localFont) : Toolkit.getDefaultToolkit().getFontMetrics(localFont));
    }
  }
  
  private int[] getJustificationData(GlyphView paramGlyphView)
  {
    View localView = paramGlyphView.getParent();
    int[] arrayOfInt = null;
    if ((localView instanceof ParagraphView.Row))
    {
      ParagraphView.Row localRow = (ParagraphView.Row)localView;
      arrayOfInt = justificationData;
    }
    return arrayOfInt;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\GlyphPainter1.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */