package sun.print;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;

public class ProxyGraphics
  extends Graphics
{
  private Graphics g;
  
  public ProxyGraphics(Graphics paramGraphics)
  {
    g = paramGraphics;
  }
  
  Graphics getGraphics()
  {
    return g;
  }
  
  public Graphics create()
  {
    return new ProxyGraphics(g.create());
  }
  
  public Graphics create(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return new ProxyGraphics(g.create(paramInt1, paramInt2, paramInt3, paramInt4));
  }
  
  public void translate(int paramInt1, int paramInt2)
  {
    g.translate(paramInt1, paramInt2);
  }
  
  public Color getColor()
  {
    return g.getColor();
  }
  
  public void setColor(Color paramColor)
  {
    g.setColor(paramColor);
  }
  
  public void setPaintMode()
  {
    g.setPaintMode();
  }
  
  public void setXORMode(Color paramColor)
  {
    g.setXORMode(paramColor);
  }
  
  public Font getFont()
  {
    return g.getFont();
  }
  
  public void setFont(Font paramFont)
  {
    g.setFont(paramFont);
  }
  
  public FontMetrics getFontMetrics()
  {
    return g.getFontMetrics();
  }
  
  public FontMetrics getFontMetrics(Font paramFont)
  {
    return g.getFontMetrics(paramFont);
  }
  
  public Rectangle getClipBounds()
  {
    return g.getClipBounds();
  }
  
  public void clipRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    g.clipRect(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void setClip(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    g.setClip(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public Shape getClip()
  {
    return g.getClip();
  }
  
  public void setClip(Shape paramShape)
  {
    g.setClip(paramShape);
  }
  
  public void copyArea(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    g.copyArea(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
  }
  
  public void drawLine(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    g.drawLine(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void fillRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    g.fillRect(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void drawRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    g.drawRect(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void clearRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    g.clearRect(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void drawRoundRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    g.drawRoundRect(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
  }
  
  public void fillRoundRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    g.fillRoundRect(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
  }
  
  public void draw3DRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
  {
    g.draw3DRect(paramInt1, paramInt2, paramInt3, paramInt4, paramBoolean);
  }
  
  public void fill3DRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
  {
    g.fill3DRect(paramInt1, paramInt2, paramInt3, paramInt4, paramBoolean);
  }
  
  public void drawOval(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    g.drawOval(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void fillOval(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    g.fillOval(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void drawArc(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    g.drawArc(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
  }
  
  public void fillArc(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    g.fillArc(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
  }
  
  public void drawPolyline(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt)
  {
    g.drawPolyline(paramArrayOfInt1, paramArrayOfInt2, paramInt);
  }
  
  public void drawPolygon(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt)
  {
    g.drawPolygon(paramArrayOfInt1, paramArrayOfInt2, paramInt);
  }
  
  public void drawPolygon(Polygon paramPolygon)
  {
    g.drawPolygon(paramPolygon);
  }
  
  public void fillPolygon(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt)
  {
    g.fillPolygon(paramArrayOfInt1, paramArrayOfInt2, paramInt);
  }
  
  public void fillPolygon(Polygon paramPolygon)
  {
    g.fillPolygon(paramPolygon);
  }
  
  public void drawString(String paramString, int paramInt1, int paramInt2)
  {
    g.drawString(paramString, paramInt1, paramInt2);
  }
  
  public void drawString(AttributedCharacterIterator paramAttributedCharacterIterator, int paramInt1, int paramInt2)
  {
    g.drawString(paramAttributedCharacterIterator, paramInt1, paramInt2);
  }
  
  public void drawChars(char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    g.drawChars(paramArrayOfChar, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void drawBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    g.drawBytes(paramArrayOfByte, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver)
  {
    return g.drawImage(paramImage, paramInt1, paramInt2, paramImageObserver);
  }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, ImageObserver paramImageObserver)
  {
    return g.drawImage(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramImageObserver);
  }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, Color paramColor, ImageObserver paramImageObserver)
  {
    return g.drawImage(paramImage, paramInt1, paramInt2, paramColor, paramImageObserver);
  }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor, ImageObserver paramImageObserver)
  {
    return g.drawImage(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramColor, paramImageObserver);
  }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, ImageObserver paramImageObserver)
  {
    return g.drawImage(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramImageObserver);
  }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, Color paramColor, ImageObserver paramImageObserver)
  {
    return g.drawImage(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramColor, paramImageObserver);
  }
  
  public void dispose()
  {
    g.dispose();
  }
  
  public void finalize() {}
  
  public String toString()
  {
    return getClass().getName() + "[font=" + getFont() + ",color=" + getColor() + "]";
  }
  
  @Deprecated
  public Rectangle getClipRect()
  {
    return g.getClipRect();
  }
  
  public boolean hitClip(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return g.hitClip(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public Rectangle getClipBounds(Rectangle paramRectangle)
  {
    return g.getClipBounds(paramRectangle);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\print\ProxyGraphics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */