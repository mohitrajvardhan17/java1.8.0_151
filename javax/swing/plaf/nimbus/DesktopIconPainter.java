package javax.swing.plaf.nimbus;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Ellipse2D.Float;
import java.awt.geom.Path2D;
import java.awt.geom.Path2D.Float;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.RoundRectangle2D.Float;
import javax.swing.JComponent;

final class DesktopIconPainter
  extends AbstractRegionPainter
{
  static final int BACKGROUND_ENABLED = 1;
  private int state;
  private AbstractRegionPainter.PaintContext ctx;
  private Path2D path = new Path2D.Float();
  private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
  private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  private Color color1 = decodeColor("nimbusBase", 0.02551502F, -0.47885156F, -0.34901965F, 0);
  private Color color2 = decodeColor("nimbusBlueGrey", -0.027777791F, -0.102261856F, 0.20392156F, 0);
  private Color color3 = decodeColor("nimbusBlueGrey", 0.0F, -0.0682728F, 0.09019607F, 0);
  private Color color4 = decodeColor("nimbusBlueGrey", -0.01111114F, -0.088974595F, 0.16470587F, 0);
  private Color color5 = decodeColor("nimbusBlueGrey", 0.0F, -0.029445238F, -0.019607842F, 0);
  private Object[] componentColors;
  
  public DesktopIconPainter(AbstractRegionPainter.PaintContext paramPaintContext, int paramInt)
  {
    state = paramInt;
    ctx = paramPaintContext;
  }
  
  protected void doPaint(Graphics2D paramGraphics2D, JComponent paramJComponent, int paramInt1, int paramInt2, Object[] paramArrayOfObject)
  {
    componentColors = paramArrayOfObject;
    switch (state)
    {
    case 1: 
      paintBackgroundEnabled(paramGraphics2D);
    }
  }
  
  protected final AbstractRegionPainter.PaintContext getPaintContext()
  {
    return ctx;
  }
  
  private void paintBackgroundEnabled(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(color1);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient1(roundRect));
    paramGraphics2D.fill(roundRect);
    rect = decodeRect1();
    paramGraphics2D.setPaint(decodeGradient2(rect));
    paramGraphics2D.fill(rect);
  }
  
  private RoundRectangle2D decodeRoundRect1()
  {
    roundRect.setRoundRect(decodeX(0.4F), decodeY(0.0F), decodeX(2.8F) - decodeX(0.4F), decodeY(2.6F) - decodeY(0.0F), 4.833333492279053D, 4.833333492279053D);
    return roundRect;
  }
  
  private RoundRectangle2D decodeRoundRect2()
  {
    roundRect.setRoundRect(decodeX(0.6F), decodeY(0.2F), decodeX(2.8F) - decodeX(0.6F), decodeY(2.4F) - decodeY(0.2F), 3.0999999046325684D, 3.0999999046325684D);
    return roundRect;
  }
  
  private Rectangle2D decodeRect1()
  {
    rect.setRect(decodeX(0.8F), decodeY(0.4F), decodeX(2.4F) - decodeX(0.8F), decodeY(2.2F) - decodeY(0.4F));
    return rect;
  }
  
  private Paint decodeGradient1(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color2, decodeColor(color2, color3, 0.5F), color3 });
  }
  
  private Paint decodeGradient2(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.24F, 1.0F }, new Color[] { color4, decodeColor(color4, color5, 0.5F), color5 });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\nimbus\DesktopIconPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */