package javax.swing.plaf.nimbus;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.swing.border.Border;

class LoweredBorder
  extends AbstractRegionPainter
  implements Border
{
  private static final int IMG_SIZE = 30;
  private static final int RADIUS = 13;
  private static final Insets INSETS = new Insets(10, 10, 10, 10);
  private static final AbstractRegionPainter.PaintContext PAINT_CONTEXT = new AbstractRegionPainter.PaintContext(INSETS, new Dimension(30, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.147483647E9D, 2.147483647E9D);
  
  LoweredBorder() {}
  
  protected Object[] getExtendedCacheKeys(JComponent paramJComponent)
  {
    return paramJComponent != null ? new Object[] { paramJComponent.getBackground() } : null;
  }
  
  protected void doPaint(Graphics2D paramGraphics2D, JComponent paramJComponent, int paramInt1, int paramInt2, Object[] paramArrayOfObject)
  {
    Color localColor = paramJComponent == null ? Color.BLACK : paramJComponent.getBackground();
    BufferedImage localBufferedImage1 = new BufferedImage(30, 30, 2);
    BufferedImage localBufferedImage2 = new BufferedImage(30, 30, 2);
    Graphics2D localGraphics2D = (Graphics2D)localBufferedImage1.getGraphics();
    localGraphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    localGraphics2D.setColor(localColor);
    localGraphics2D.fillRoundRect(2, 0, 26, 26, 13, 13);
    localGraphics2D.dispose();
    InnerShadowEffect localInnerShadowEffect = new InnerShadowEffect();
    localInnerShadowEffect.setDistance(1);
    localInnerShadowEffect.setSize(3);
    localInnerShadowEffect.setColor(getLighter(localColor, 2.1F));
    localInnerShadowEffect.setAngle(90);
    localInnerShadowEffect.applyEffect(localBufferedImage1, localBufferedImage2, 30, 30);
    localGraphics2D = (Graphics2D)localBufferedImage2.getGraphics();
    localGraphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    localGraphics2D.setClip(0, 28, 30, 1);
    localGraphics2D.setColor(getLighter(localColor, 0.9F));
    localGraphics2D.drawRoundRect(2, 1, 25, 25, 13, 13);
    localGraphics2D.dispose();
    if ((paramInt1 != 30) || (paramInt2 != 30)) {
      ImageScalingHelper.paint(paramGraphics2D, 0, 0, paramInt1, paramInt2, localBufferedImage2, INSETS, INSETS, ImageScalingHelper.PaintType.PAINT9_STRETCH, 512);
    } else {
      paramGraphics2D.drawImage(localBufferedImage2, 0, 0, paramJComponent);
    }
    localBufferedImage1 = null;
    localBufferedImage2 = null;
  }
  
  protected AbstractRegionPainter.PaintContext getPaintContext()
  {
    return PAINT_CONTEXT;
  }
  
  public Insets getBorderInsets(Component paramComponent)
  {
    return (Insets)INSETS.clone();
  }
  
  public boolean isBorderOpaque()
  {
    return false;
  }
  
  public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    JComponent localJComponent = (paramComponent instanceof JComponent) ? (JComponent)paramComponent : null;
    Object localObject;
    if ((paramGraphics instanceof Graphics2D))
    {
      localObject = (Graphics2D)paramGraphics;
      ((Graphics2D)localObject).translate(paramInt1, paramInt2);
      paint((Graphics2D)localObject, localJComponent, paramInt3, paramInt4);
      ((Graphics2D)localObject).translate(-paramInt1, -paramInt2);
    }
    else
    {
      localObject = new BufferedImage(30, 30, 2);
      Graphics2D localGraphics2D = (Graphics2D)((BufferedImage)localObject).getGraphics();
      paint(localGraphics2D, localJComponent, paramInt3, paramInt4);
      localGraphics2D.dispose();
      ImageScalingHelper.paint(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, (Image)localObject, INSETS, INSETS, ImageScalingHelper.PaintType.PAINT9_STRETCH, 512);
    }
  }
  
  private Color getLighter(Color paramColor, float paramFloat)
  {
    return new Color(Math.min((int)(paramColor.getRed() / paramFloat), 255), Math.min((int)(paramColor.getGreen() / paramFloat), 255), Math.min((int)(paramColor.getBlue() / paramFloat), 255));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\nimbus\LoweredBorder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */