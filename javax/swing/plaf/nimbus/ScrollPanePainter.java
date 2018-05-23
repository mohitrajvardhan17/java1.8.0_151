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

final class ScrollPanePainter
  extends AbstractRegionPainter
{
  static final int BACKGROUND_ENABLED = 1;
  static final int BORDER_ENABLED_FOCUSED = 2;
  static final int BORDER_ENABLED = 3;
  private int state;
  private AbstractRegionPainter.PaintContext ctx;
  private Path2D path = new Path2D.Float();
  private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
  private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  private Color color1 = decodeColor("nimbusBorder", 0.0F, 0.0F, 0.0F, 0);
  private Color color2 = decodeColor("nimbusFocus", 0.0F, 0.0F, 0.0F, 0);
  private Object[] componentColors;
  
  public ScrollPanePainter(AbstractRegionPainter.PaintContext paramPaintContext, int paramInt)
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
      paintBorderEnabledAndFocused(paramGraphics2D);
      break;
    case 3: 
      paintBorderEnabled(paramGraphics2D);
    }
  }
  
  protected final AbstractRegionPainter.PaintContext getPaintContext()
  {
    return ctx;
  }
  
  private void paintBorderEnabledAndFocused(Graphics2D paramGraphics2D)
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
    path = decodePath1();
    paramGraphics2D.setPaint(color2);
    paramGraphics2D.fill(path);
  }
  
  private void paintBorderEnabled(Graphics2D paramGraphics2D)
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
  }
  
  private Rectangle2D decodeRect1()
  {
    rect.setRect(decodeX(0.6F), decodeY(0.4F), decodeX(2.4F) - decodeX(0.6F), decodeY(0.6F) - decodeY(0.4F));
    return rect;
  }
  
  private Rectangle2D decodeRect2()
  {
    rect.setRect(decodeX(0.4F), decodeY(0.4F), decodeX(0.6F) - decodeX(0.4F), decodeY(2.6F) - decodeY(0.4F));
    return rect;
  }
  
  private Rectangle2D decodeRect3()
  {
    rect.setRect(decodeX(2.4F), decodeY(0.4F), decodeX(2.6F) - decodeX(2.4F), decodeY(2.6F) - decodeY(0.4F));
    return rect;
  }
  
  private Rectangle2D decodeRect4()
  {
    rect.setRect(decodeX(0.6F), decodeY(2.4F), decodeX(2.4F) - decodeX(0.6F), decodeY(2.6F) - decodeY(2.4F));
    return rect;
  }
  
  private Path2D decodePath1()
  {
    path.reset();
    path.moveTo(decodeX(0.4F), decodeY(0.4F));
    path.lineTo(decodeX(0.4F), decodeY(2.6F));
    path.lineTo(decodeX(2.6F), decodeY(2.6F));
    path.lineTo(decodeX(2.6F), decodeY(0.4F));
    path.curveTo(decodeAnchorX(2.6F, 0.0F), decodeAnchorY(0.4F, 0.0F), decodeAnchorX(2.8800004F, 0.1F), decodeAnchorY(0.4F, 0.0F), decodeX(2.8800004F), decodeY(0.4F));
    path.curveTo(decodeAnchorX(2.8800004F, 0.1F), decodeAnchorY(0.4F, 0.0F), decodeAnchorX(2.8800004F, 0.0F), decodeAnchorY(2.8799999F, 0.0F), decodeX(2.8800004F), decodeY(2.8799999F));
    path.lineTo(decodeX(0.120000005F), decodeY(2.8799999F));
    path.lineTo(decodeX(0.120000005F), decodeY(0.120000005F));
    path.lineTo(decodeX(2.8800004F), decodeY(0.120000005F));
    path.lineTo(decodeX(2.8800004F), decodeY(0.4F));
    path.lineTo(decodeX(0.4F), decodeY(0.4F));
    path.closePath();
    return path;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\nimbus\ScrollPanePainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */