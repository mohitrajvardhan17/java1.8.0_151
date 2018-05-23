package javax.swing.plaf.nimbus;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Ellipse2D.Float;
import java.awt.geom.Path2D;
import java.awt.geom.Path2D.Float;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.RoundRectangle2D.Float;
import javax.swing.JComponent;

final class ArrowButtonPainter
  extends AbstractRegionPainter
{
  static final int BACKGROUND_ENABLED = 1;
  static final int FOREGROUND_DISABLED = 2;
  static final int FOREGROUND_ENABLED = 3;
  private int state;
  private AbstractRegionPainter.PaintContext ctx;
  private Path2D path = new Path2D.Float();
  private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
  private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  private Color color1 = decodeColor("nimbusBase", 0.027408898F, -0.57391655F, 0.1490196F, 0);
  private Color color2 = decodeColor("nimbusBase", -0.57865167F, -0.6357143F, -0.37254906F, 0);
  private Object[] componentColors;
  
  public ArrowButtonPainter(AbstractRegionPainter.PaintContext paramPaintContext, int paramInt)
  {
    state = paramInt;
    ctx = paramPaintContext;
  }
  
  protected void doPaint(Graphics2D paramGraphics2D, JComponent paramJComponent, int paramInt1, int paramInt2, Object[] paramArrayOfObject)
  {
    componentColors = paramArrayOfObject;
    switch (state)
    {
    case 2: 
      paintForegroundDisabled(paramGraphics2D);
      break;
    case 3: 
      paintForegroundEnabled(paramGraphics2D);
    }
  }
  
  protected final AbstractRegionPainter.PaintContext getPaintContext()
  {
    return ctx;
  }
  
  private void paintForegroundDisabled(Graphics2D paramGraphics2D)
  {
    path = decodePath1();
    paramGraphics2D.setPaint(color1);
    paramGraphics2D.fill(path);
  }
  
  private void paintForegroundEnabled(Graphics2D paramGraphics2D)
  {
    path = decodePath1();
    paramGraphics2D.setPaint(color2);
    paramGraphics2D.fill(path);
  }
  
  private Path2D decodePath1()
  {
    path.reset();
    path.moveTo(decodeX(1.8F), decodeY(1.2F));
    path.lineTo(decodeX(1.2F), decodeY(1.5F));
    path.lineTo(decodeX(1.8F), decodeY(1.8F));
    path.lineTo(decodeX(1.8F), decodeY(1.2F));
    path.closePath();
    return path;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\nimbus\ArrowButtonPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */