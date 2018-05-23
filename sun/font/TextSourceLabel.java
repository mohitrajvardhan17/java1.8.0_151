package sun.font;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;

public class TextSourceLabel
  extends TextLabel
{
  TextSource source;
  Rectangle2D lb;
  Rectangle2D ab;
  Rectangle2D vb;
  Rectangle2D ib;
  GlyphVector gv;
  
  public TextSourceLabel(TextSource paramTextSource)
  {
    this(paramTextSource, null, null, null);
  }
  
  public TextSourceLabel(TextSource paramTextSource, Rectangle2D paramRectangle2D1, Rectangle2D paramRectangle2D2, GlyphVector paramGlyphVector)
  {
    source = paramTextSource;
    lb = paramRectangle2D1;
    ab = paramRectangle2D2;
    gv = paramGlyphVector;
  }
  
  public TextSource getSource()
  {
    return source;
  }
  
  public final Rectangle2D getLogicalBounds(float paramFloat1, float paramFloat2)
  {
    if (lb == null) {
      lb = createLogicalBounds();
    }
    return new Rectangle2D.Float((float)(lb.getX() + paramFloat1), (float)(lb.getY() + paramFloat2), (float)lb.getWidth(), (float)lb.getHeight());
  }
  
  public final Rectangle2D getVisualBounds(float paramFloat1, float paramFloat2)
  {
    if (vb == null) {
      vb = createVisualBounds();
    }
    return new Rectangle2D.Float((float)(vb.getX() + paramFloat1), (float)(vb.getY() + paramFloat2), (float)vb.getWidth(), (float)vb.getHeight());
  }
  
  public final Rectangle2D getAlignBounds(float paramFloat1, float paramFloat2)
  {
    if (ab == null) {
      ab = createAlignBounds();
    }
    return new Rectangle2D.Float((float)(ab.getX() + paramFloat1), (float)(ab.getY() + paramFloat2), (float)ab.getWidth(), (float)ab.getHeight());
  }
  
  public Rectangle2D getItalicBounds(float paramFloat1, float paramFloat2)
  {
    if (ib == null) {
      ib = createItalicBounds();
    }
    return new Rectangle2D.Float((float)(ib.getX() + paramFloat1), (float)(ib.getY() + paramFloat2), (float)ib.getWidth(), (float)ib.getHeight());
  }
  
  public Rectangle getPixelBounds(FontRenderContext paramFontRenderContext, float paramFloat1, float paramFloat2)
  {
    return getGV().getPixelBounds(paramFontRenderContext, paramFloat1, paramFloat2);
  }
  
  public AffineTransform getBaselineTransform()
  {
    Font localFont = source.getFont();
    if (localFont.hasLayoutAttributes()) {
      return AttributeValues.getBaselineTransform(localFont.getAttributes());
    }
    return null;
  }
  
  public Shape getOutline(float paramFloat1, float paramFloat2)
  {
    return getGV().getOutline(paramFloat1, paramFloat2);
  }
  
  public void draw(Graphics2D paramGraphics2D, float paramFloat1, float paramFloat2)
  {
    paramGraphics2D.drawGlyphVector(getGV(), paramFloat1, paramFloat2);
  }
  
  protected Rectangle2D createLogicalBounds()
  {
    return getGV().getLogicalBounds();
  }
  
  protected Rectangle2D createVisualBounds()
  {
    return getGV().getVisualBounds();
  }
  
  protected Rectangle2D createItalicBounds()
  {
    return getGV().getLogicalBounds();
  }
  
  protected Rectangle2D createAlignBounds()
  {
    return createLogicalBounds();
  }
  
  private final GlyphVector getGV()
  {
    if (gv == null) {
      gv = createGV();
    }
    return gv;
  }
  
  protected GlyphVector createGV()
  {
    Font localFont = source.getFont();
    FontRenderContext localFontRenderContext = source.getFRC();
    int i = source.getLayoutFlags();
    char[] arrayOfChar = source.getChars();
    int j = source.getStart();
    int k = source.getLength();
    GlyphLayout localGlyphLayout = GlyphLayout.get(null);
    StandardGlyphVector localStandardGlyphVector = localGlyphLayout.layout(localFont, localFontRenderContext, arrayOfChar, j, k, i, null);
    GlyphLayout.done(localGlyphLayout);
    return localStandardGlyphVector;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\font\TextSourceLabel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */