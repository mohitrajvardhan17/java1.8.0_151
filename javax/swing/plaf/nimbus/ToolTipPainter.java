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

final class ToolTipPainter
  extends AbstractRegionPainter
{
  static final int BACKGROUND_ENABLED = 1;
  private int state;
  private AbstractRegionPainter.PaintContext ctx;
  private Path2D path = new Path2D.Float();
  private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
  private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  private Color color1 = decodeColor("nimbusBorder", 0.0F, 0.0F, 0.0F, 0);
  private Color color2 = decodeColor("info", 0.0F, 0.0F, 0.0F, 0);
  private Object[] componentColors;
  
  public ToolTipPainter(AbstractRegionPainter.PaintContext paramPaintContext, int paramInt)
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
    rect = decodeRect1();
    paramGraphics2D.setPaint(color1);
    paramGraphics2D.fill(rect);
    rect = decodeRect2();
    paramGraphics2D.setPaint(color1);
    paramGraphics2D.fill(rect);
    rect = decodeRect3();
    paramGraphics2D.setPaint(color1);
    paramGraphics2D.fill(rect);
    rect = decodeRect4();
    paramGraphics2D.setPaint(color1);
    paramGraphics2D.fill(rect);
    rect = decodeRect5();
    paramGraphics2D.setPaint(color2);
    paramGraphics2D.fill(rect);
  }
  
  private Rectangle2D decodeRect1()
  {
    rect.setRect(decodeX(2.0F), decodeY(1.0F), decodeX(3.0F) - decodeX(2.0F), decodeY(2.0F) - decodeY(1.0F));
    return rect;
  }
  
  private Rectangle2D decodeRect2()
  {
    rect.setRect(decodeX(0.0F), decodeY(1.0F), decodeX(1.0F) - decodeX(0.0F), decodeY(2.0F) - decodeY(1.0F));
    return rect;
  }
  
  private Rectangle2D decodeRect3()
  {
    rect.setRect(decodeX(0.0F), decodeY(2.0F), decodeX(3.0F) - decodeX(0.0F), decodeY(3.0F) - decodeY(2.0F));
    return rect;
  }
  
  private Rectangle2D decodeRect4()
  {
    rect.setRect(decodeX(0.0F), decodeY(0.0F), decodeX(3.0F) - decodeX(0.0F), decodeY(1.0F) - decodeY(0.0F));
    return rect;
  }
  
  private Rectangle2D decodeRect5()
  {
    rect.setRect(decodeX(1.0F), decodeY(1.0F), decodeX(2.0F) - decodeX(1.0F), decodeY(2.0F) - decodeY(1.0F));
    return rect;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\nimbus\ToolTipPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */