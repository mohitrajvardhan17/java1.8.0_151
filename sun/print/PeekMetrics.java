package sun.print;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.font.TextLayout;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;

public class PeekMetrics
{
  private boolean mHasNonSolidColors;
  private boolean mHasCompositing;
  private boolean mHasText;
  private boolean mHasImages;
  
  public PeekMetrics() {}
  
  public boolean hasNonSolidColors()
  {
    return mHasNonSolidColors;
  }
  
  public boolean hasCompositing()
  {
    return mHasCompositing;
  }
  
  public boolean hasText()
  {
    return mHasText;
  }
  
  public boolean hasImages()
  {
    return mHasImages;
  }
  
  public void fill(Graphics2D paramGraphics2D)
  {
    checkDrawingMode(paramGraphics2D);
  }
  
  public void draw(Graphics2D paramGraphics2D)
  {
    checkDrawingMode(paramGraphics2D);
  }
  
  public void clear(Graphics2D paramGraphics2D)
  {
    checkPaint(paramGraphics2D.getBackground());
  }
  
  public void drawText(Graphics2D paramGraphics2D)
  {
    mHasText = true;
    checkDrawingMode(paramGraphics2D);
  }
  
  public void drawText(Graphics2D paramGraphics2D, TextLayout paramTextLayout)
  {
    mHasText = true;
    checkDrawingMode(paramGraphics2D);
  }
  
  public void drawImage(Graphics2D paramGraphics2D, Image paramImage)
  {
    mHasImages = true;
  }
  
  public void drawImage(Graphics2D paramGraphics2D, RenderedImage paramRenderedImage)
  {
    mHasImages = true;
  }
  
  public void drawImage(Graphics2D paramGraphics2D, RenderableImage paramRenderableImage)
  {
    mHasImages = true;
  }
  
  private void checkDrawingMode(Graphics2D paramGraphics2D)
  {
    checkPaint(paramGraphics2D.getPaint());
    checkAlpha(paramGraphics2D.getComposite());
  }
  
  private void checkPaint(Paint paramPaint)
  {
    if ((paramPaint instanceof Color))
    {
      if (((Color)paramPaint).getAlpha() < 255) {
        mHasNonSolidColors = true;
      }
    }
    else {
      mHasNonSolidColors = true;
    }
  }
  
  private void checkAlpha(Composite paramComposite)
  {
    if ((paramComposite instanceof AlphaComposite))
    {
      AlphaComposite localAlphaComposite = (AlphaComposite)paramComposite;
      float f = localAlphaComposite.getAlpha();
      int i = localAlphaComposite.getRule();
      if ((f != 1.0D) || ((i != 2) && (i != 3))) {
        mHasCompositing = true;
      }
    }
    else
    {
      mHasCompositing = true;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\print\PeekMetrics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */