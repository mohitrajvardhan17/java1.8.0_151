package sun.print;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D.Float;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import java.awt.geom.RoundRectangle2D.Float;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.awt.print.PrinterGraphics;
import java.awt.print.PrinterJob;
import java.text.AttributedCharacterIterator;
import java.util.Map;
import sun.java2d.Spans;

public class PeekGraphics
  extends Graphics2D
  implements PrinterGraphics, ImageObserver, Cloneable
{
  Graphics2D mGraphics;
  PrinterJob mPrinterJob;
  private Spans mDrawingArea = new Spans();
  private PeekMetrics mPrintMetrics = new PeekMetrics();
  private boolean mAWTDrawingOnly = false;
  
  public PeekGraphics(Graphics2D paramGraphics2D, PrinterJob paramPrinterJob)
  {
    mGraphics = paramGraphics2D;
    mPrinterJob = paramPrinterJob;
  }
  
  public Graphics2D getDelegate()
  {
    return mGraphics;
  }
  
  public void setDelegate(Graphics2D paramGraphics2D)
  {
    mGraphics = paramGraphics2D;
  }
  
  public PrinterJob getPrinterJob()
  {
    return mPrinterJob;
  }
  
  public void setAWTDrawingOnly()
  {
    mAWTDrawingOnly = true;
  }
  
  public boolean getAWTDrawingOnly()
  {
    return mAWTDrawingOnly;
  }
  
  public Spans getDrawingArea()
  {
    return mDrawingArea;
  }
  
  public GraphicsConfiguration getDeviceConfiguration()
  {
    return ((RasterPrinterJob)mPrinterJob).getPrinterGraphicsConfig();
  }
  
  public Graphics create()
  {
    PeekGraphics localPeekGraphics = null;
    try
    {
      localPeekGraphics = (PeekGraphics)clone();
      mGraphics = ((Graphics2D)mGraphics.create());
    }
    catch (CloneNotSupportedException localCloneNotSupportedException) {}
    return localPeekGraphics;
  }
  
  public void translate(int paramInt1, int paramInt2)
  {
    mGraphics.translate(paramInt1, paramInt2);
  }
  
  public void translate(double paramDouble1, double paramDouble2)
  {
    mGraphics.translate(paramDouble1, paramDouble2);
  }
  
  public void rotate(double paramDouble)
  {
    mGraphics.rotate(paramDouble);
  }
  
  public void rotate(double paramDouble1, double paramDouble2, double paramDouble3)
  {
    mGraphics.rotate(paramDouble1, paramDouble2, paramDouble3);
  }
  
  public void scale(double paramDouble1, double paramDouble2)
  {
    mGraphics.scale(paramDouble1, paramDouble2);
  }
  
  public void shear(double paramDouble1, double paramDouble2)
  {
    mGraphics.shear(paramDouble1, paramDouble2);
  }
  
  public Color getColor()
  {
    return mGraphics.getColor();
  }
  
  public void setColor(Color paramColor)
  {
    mGraphics.setColor(paramColor);
  }
  
  public void setPaintMode()
  {
    mGraphics.setPaintMode();
  }
  
  public void setXORMode(Color paramColor)
  {
    mGraphics.setXORMode(paramColor);
  }
  
  public Font getFont()
  {
    return mGraphics.getFont();
  }
  
  public void setFont(Font paramFont)
  {
    mGraphics.setFont(paramFont);
  }
  
  public FontMetrics getFontMetrics(Font paramFont)
  {
    return mGraphics.getFontMetrics(paramFont);
  }
  
  public FontRenderContext getFontRenderContext()
  {
    return mGraphics.getFontRenderContext();
  }
  
  public Rectangle getClipBounds()
  {
    return mGraphics.getClipBounds();
  }
  
  public void clipRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    mGraphics.clipRect(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void setClip(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    mGraphics.setClip(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public Shape getClip()
  {
    return mGraphics.getClip();
  }
  
  public void setClip(Shape paramShape)
  {
    mGraphics.setClip(paramShape);
  }
  
  public void copyArea(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {}
  
  public void drawLine(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    addStrokeShape(new Line2D.Float(paramInt1, paramInt2, paramInt3, paramInt4));
    mPrintMetrics.draw(this);
  }
  
  public void fillRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    addDrawingRect(new Rectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4));
    mPrintMetrics.fill(this);
  }
  
  public void clearRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Rectangle2D.Float localFloat = new Rectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4);
    addDrawingRect(localFloat);
    mPrintMetrics.clear(this);
  }
  
  public void drawRoundRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    addStrokeShape(new RoundRectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6));
    mPrintMetrics.draw(this);
  }
  
  public void fillRoundRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    Rectangle2D.Float localFloat = new Rectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4);
    addDrawingRect(localFloat);
    mPrintMetrics.fill(this);
  }
  
  public void drawOval(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    addStrokeShape(new Rectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4));
    mPrintMetrics.draw(this);
  }
  
  public void fillOval(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Rectangle2D.Float localFloat = new Rectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4);
    addDrawingRect(localFloat);
    mPrintMetrics.fill(this);
  }
  
  public void drawArc(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    addStrokeShape(new Rectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4));
    mPrintMetrics.draw(this);
  }
  
  public void fillArc(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    Rectangle2D.Float localFloat = new Rectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4);
    addDrawingRect(localFloat);
    mPrintMetrics.fill(this);
  }
  
  public void drawPolyline(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt)
  {
    if (paramInt > 0)
    {
      int i = paramArrayOfInt1[0];
      int j = paramArrayOfInt2[0];
      for (int k = 1; k < paramInt; k++)
      {
        drawLine(i, j, paramArrayOfInt1[k], paramArrayOfInt2[k]);
        i = paramArrayOfInt1[k];
        j = paramArrayOfInt2[k];
      }
    }
  }
  
  public void drawPolygon(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt)
  {
    if (paramInt > 0)
    {
      drawPolyline(paramArrayOfInt1, paramArrayOfInt2, paramInt);
      drawLine(paramArrayOfInt1[(paramInt - 1)], paramArrayOfInt2[(paramInt - 1)], paramArrayOfInt1[0], paramArrayOfInt2[0]);
    }
  }
  
  public void fillPolygon(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt)
  {
    if (paramInt > 0)
    {
      int i = paramArrayOfInt1[0];
      int j = paramArrayOfInt2[0];
      int k = paramArrayOfInt1[0];
      int m = paramArrayOfInt2[0];
      for (int n = 1; n < paramInt; n++)
      {
        if (paramArrayOfInt1[n] < i) {
          i = paramArrayOfInt1[n];
        } else if (paramArrayOfInt1[n] > k) {
          k = paramArrayOfInt1[n];
        }
        if (paramArrayOfInt2[n] < j) {
          j = paramArrayOfInt2[n];
        } else if (paramArrayOfInt2[n] > m) {
          m = paramArrayOfInt2[n];
        }
      }
      addDrawingRect(i, j, k - i, m - j);
    }
    mPrintMetrics.fill(this);
  }
  
  public void drawString(String paramString, int paramInt1, int paramInt2)
  {
    drawString(paramString, paramInt1, paramInt2);
  }
  
  public void drawString(AttributedCharacterIterator paramAttributedCharacterIterator, int paramInt1, int paramInt2)
  {
    drawString(paramAttributedCharacterIterator, paramInt1, paramInt2);
  }
  
  public void drawString(AttributedCharacterIterator paramAttributedCharacterIterator, float paramFloat1, float paramFloat2)
  {
    if (paramAttributedCharacterIterator == null) {
      throw new NullPointerException("AttributedCharacterIterator is null");
    }
    TextLayout localTextLayout = new TextLayout(paramAttributedCharacterIterator, getFontRenderContext());
    localTextLayout.draw(this, paramFloat1, paramFloat2);
  }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver)
  {
    if (paramImage == null) {
      return true;
    }
    ImageWaiter localImageWaiter = new ImageWaiter(paramImage);
    addDrawingRect(paramInt1, paramInt2, localImageWaiter.getWidth(), localImageWaiter.getHeight());
    mPrintMetrics.drawImage(this, paramImage);
    return mGraphics.drawImage(paramImage, paramInt1, paramInt2, paramImageObserver);
  }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, ImageObserver paramImageObserver)
  {
    if (paramImage == null) {
      return true;
    }
    addDrawingRect(paramInt1, paramInt2, paramInt3, paramInt4);
    mPrintMetrics.drawImage(this, paramImage);
    return mGraphics.drawImage(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramImageObserver);
  }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, Color paramColor, ImageObserver paramImageObserver)
  {
    if (paramImage == null) {
      return true;
    }
    ImageWaiter localImageWaiter = new ImageWaiter(paramImage);
    addDrawingRect(paramInt1, paramInt2, localImageWaiter.getWidth(), localImageWaiter.getHeight());
    mPrintMetrics.drawImage(this, paramImage);
    return mGraphics.drawImage(paramImage, paramInt1, paramInt2, paramColor, paramImageObserver);
  }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor, ImageObserver paramImageObserver)
  {
    if (paramImage == null) {
      return true;
    }
    addDrawingRect(paramInt1, paramInt2, paramInt3, paramInt4);
    mPrintMetrics.drawImage(this, paramImage);
    return mGraphics.drawImage(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramColor, paramImageObserver);
  }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, ImageObserver paramImageObserver)
  {
    if (paramImage == null) {
      return true;
    }
    int i = paramInt3 - paramInt1;
    int j = paramInt4 - paramInt2;
    addDrawingRect(paramInt1, paramInt2, i, j);
    mPrintMetrics.drawImage(this, paramImage);
    return mGraphics.drawImage(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramImageObserver);
  }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, Color paramColor, ImageObserver paramImageObserver)
  {
    if (paramImage == null) {
      return true;
    }
    int i = paramInt3 - paramInt1;
    int j = paramInt4 - paramInt2;
    addDrawingRect(paramInt1, paramInt2, i, j);
    mPrintMetrics.drawImage(this, paramImage);
    return mGraphics.drawImage(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramColor, paramImageObserver);
  }
  
  public void drawRenderedImage(RenderedImage paramRenderedImage, AffineTransform paramAffineTransform)
  {
    if (paramRenderedImage == null) {
      return;
    }
    mPrintMetrics.drawImage(this, paramRenderedImage);
    mDrawingArea.addInfinite();
  }
  
  public void drawRenderableImage(RenderableImage paramRenderableImage, AffineTransform paramAffineTransform)
  {
    if (paramRenderableImage == null) {
      return;
    }
    mPrintMetrics.drawImage(this, paramRenderableImage);
    mDrawingArea.addInfinite();
  }
  
  public void dispose()
  {
    mGraphics.dispose();
  }
  
  public void finalize() {}
  
  public void draw(Shape paramShape)
  {
    addStrokeShape(paramShape);
    mPrintMetrics.draw(this);
  }
  
  public boolean drawImage(Image paramImage, AffineTransform paramAffineTransform, ImageObserver paramImageObserver)
  {
    if (paramImage == null) {
      return true;
    }
    mDrawingArea.addInfinite();
    mPrintMetrics.drawImage(this, paramImage);
    return mGraphics.drawImage(paramImage, paramAffineTransform, paramImageObserver);
  }
  
  public void drawImage(BufferedImage paramBufferedImage, BufferedImageOp paramBufferedImageOp, int paramInt1, int paramInt2)
  {
    if (paramBufferedImage == null) {
      return;
    }
    mPrintMetrics.drawImage(this, paramBufferedImage);
    mDrawingArea.addInfinite();
  }
  
  public void drawString(String paramString, float paramFloat1, float paramFloat2)
  {
    if (paramString.length() == 0) {
      return;
    }
    FontRenderContext localFontRenderContext = getFontRenderContext();
    Rectangle2D localRectangle2D = getFont().getStringBounds(paramString, localFontRenderContext);
    addDrawingRect(localRectangle2D, paramFloat1, paramFloat2);
    mPrintMetrics.drawText(this);
  }
  
  public void drawGlyphVector(GlyphVector paramGlyphVector, float paramFloat1, float paramFloat2)
  {
    Rectangle2D localRectangle2D = paramGlyphVector.getLogicalBounds();
    addDrawingRect(localRectangle2D, paramFloat1, paramFloat2);
    mPrintMetrics.drawText(this);
  }
  
  public void fill(Shape paramShape)
  {
    addDrawingRect(paramShape.getBounds());
    mPrintMetrics.fill(this);
  }
  
  public boolean hit(Rectangle paramRectangle, Shape paramShape, boolean paramBoolean)
  {
    return mGraphics.hit(paramRectangle, paramShape, paramBoolean);
  }
  
  public void setComposite(Composite paramComposite)
  {
    mGraphics.setComposite(paramComposite);
  }
  
  public void setPaint(Paint paramPaint)
  {
    mGraphics.setPaint(paramPaint);
  }
  
  public void setStroke(Stroke paramStroke)
  {
    mGraphics.setStroke(paramStroke);
  }
  
  public void setRenderingHint(RenderingHints.Key paramKey, Object paramObject)
  {
    mGraphics.setRenderingHint(paramKey, paramObject);
  }
  
  public Object getRenderingHint(RenderingHints.Key paramKey)
  {
    return mGraphics.getRenderingHint(paramKey);
  }
  
  public void setRenderingHints(Map<?, ?> paramMap)
  {
    mGraphics.setRenderingHints(paramMap);
  }
  
  public void addRenderingHints(Map<?, ?> paramMap)
  {
    mGraphics.addRenderingHints(paramMap);
  }
  
  public RenderingHints getRenderingHints()
  {
    return mGraphics.getRenderingHints();
  }
  
  public void transform(AffineTransform paramAffineTransform)
  {
    mGraphics.transform(paramAffineTransform);
  }
  
  public void setTransform(AffineTransform paramAffineTransform)
  {
    mGraphics.setTransform(paramAffineTransform);
  }
  
  public AffineTransform getTransform()
  {
    return mGraphics.getTransform();
  }
  
  public Paint getPaint()
  {
    return mGraphics.getPaint();
  }
  
  public Composite getComposite()
  {
    return mGraphics.getComposite();
  }
  
  public void setBackground(Color paramColor)
  {
    mGraphics.setBackground(paramColor);
  }
  
  public Color getBackground()
  {
    return mGraphics.getBackground();
  }
  
  public Stroke getStroke()
  {
    return mGraphics.getStroke();
  }
  
  public void clip(Shape paramShape)
  {
    mGraphics.clip(paramShape);
  }
  
  public boolean hitsDrawingArea(Rectangle paramRectangle)
  {
    return mDrawingArea.intersects((float)paramRectangle.getMinY(), (float)paramRectangle.getMaxY());
  }
  
  public PeekMetrics getMetrics()
  {
    return mPrintMetrics;
  }
  
  private void addDrawingRect(Rectangle2D paramRectangle2D, float paramFloat1, float paramFloat2)
  {
    addDrawingRect((float)(paramRectangle2D.getX() + paramFloat1), (float)(paramRectangle2D.getY() + paramFloat2), (float)paramRectangle2D.getWidth(), (float)paramRectangle2D.getHeight());
  }
  
  private void addDrawingRect(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    Rectangle2D.Float localFloat = new Rectangle2D.Float(paramFloat1, paramFloat2, paramFloat3, paramFloat4);
    addDrawingRect(localFloat);
  }
  
  private void addDrawingRect(Rectangle2D paramRectangle2D)
  {
    AffineTransform localAffineTransform = getTransform();
    Shape localShape = localAffineTransform.createTransformedShape(paramRectangle2D);
    Rectangle2D localRectangle2D = localShape.getBounds2D();
    mDrawingArea.add((float)localRectangle2D.getMinY(), (float)localRectangle2D.getMaxY());
  }
  
  private void addStrokeShape(Shape paramShape)
  {
    Shape localShape = getStroke().createStrokedShape(paramShape);
    addDrawingRect(localShape.getBounds2D());
  }
  
  public synchronized boolean imageUpdate(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    boolean bool = false;
    if ((paramInt1 & 0x3) != 0)
    {
      bool = true;
      notify();
    }
    return bool;
  }
  
  private synchronized int getImageWidth(Image paramImage)
  {
    while (paramImage.getWidth(this) == -1) {
      try
      {
        wait();
      }
      catch (InterruptedException localInterruptedException) {}
    }
    return paramImage.getWidth(this);
  }
  
  private synchronized int getImageHeight(Image paramImage)
  {
    while (paramImage.getHeight(this) == -1) {
      try
      {
        wait();
      }
      catch (InterruptedException localInterruptedException) {}
    }
    return paramImage.getHeight(this);
  }
  
  protected class ImageWaiter
    implements ImageObserver
  {
    private int mWidth;
    private int mHeight;
    private boolean badImage = false;
    
    ImageWaiter(Image paramImage)
    {
      waitForDimensions(paramImage);
    }
    
    public int getWidth()
    {
      return mWidth;
    }
    
    public int getHeight()
    {
      return mHeight;
    }
    
    private synchronized void waitForDimensions(Image paramImage)
    {
      mHeight = paramImage.getHeight(this);
      for (mWidth = paramImage.getWidth(this); (!badImage) && ((mWidth < 0) || (mHeight < 0)); mWidth = paramImage.getWidth(this))
      {
        try
        {
          Thread.sleep(50L);
        }
        catch (InterruptedException localInterruptedException) {}
        mHeight = paramImage.getHeight(this);
      }
      if (badImage)
      {
        mHeight = 0;
        mWidth = 0;
      }
    }
    
    public synchronized boolean imageUpdate(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      boolean bool = (paramInt1 & 0xC2) != 0;
      badImage = ((paramInt1 & 0xC0) != 0);
      return bool;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\print\PeekGraphics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */