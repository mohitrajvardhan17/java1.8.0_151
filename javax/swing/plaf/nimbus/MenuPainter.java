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

final class MenuPainter
  extends AbstractRegionPainter
{
  static final int BACKGROUND_DISABLED = 1;
  static final int BACKGROUND_ENABLED = 2;
  static final int BACKGROUND_ENABLED_SELECTED = 3;
  static final int ARROWICON_DISABLED = 4;
  static final int ARROWICON_ENABLED = 5;
  static final int ARROWICON_ENABLED_SELECTED = 6;
  private int state;
  private AbstractRegionPainter.PaintContext ctx;
  private Path2D path = new Path2D.Float();
  private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
  private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  private Color color1 = decodeColor("nimbusSelection", 0.0F, 0.0F, 0.0F, 0);
  private Color color2 = decodeColor("nimbusBlueGrey", 0.0F, -0.08983666F, -0.17647058F, 0);
  private Color color3 = decodeColor("nimbusBlueGrey", 0.055555582F, -0.09663743F, -0.4627451F, 0);
  private Color color4 = new Color(255, 255, 255, 255);
  private Object[] componentColors;
  
  public MenuPainter(AbstractRegionPainter.PaintContext paramPaintContext, int paramInt)
  {
    state = paramInt;
    ctx = paramPaintContext;
  }
  
  protected void doPaint(Graphics2D paramGraphics2D, JComponent paramJComponent, int paramInt1, int paramInt2, Object[] paramArrayOfObject)
  {
    componentColors = paramArrayOfObject;
    switch (state)
    {
    case 3: 
      paintBackgroundEnabledAndSelected(paramGraphics2D);
      break;
    case 4: 
      paintarrowIconDisabled(paramGraphics2D);
      break;
    case 5: 
      paintarrowIconEnabled(paramGraphics2D);
      break;
    case 6: 
      paintarrowIconEnabledAndSelected(paramGraphics2D);
    }
  }
  
  protected final AbstractRegionPainter.PaintContext getPaintContext()
  {
    return ctx;
  }
  
  private void paintBackgroundEnabledAndSelected(Graphics2D paramGraphics2D)
  {
    rect = decodeRect1();
    paramGraphics2D.setPaint(color1);
    paramGraphics2D.fill(rect);
  }
  
  private void paintarrowIconDisabled(Graphics2D paramGraphics2D)
  {
    path = decodePath1();
    paramGraphics2D.setPaint(color2);
    paramGraphics2D.fill(path);
  }
  
  private void paintarrowIconEnabled(Graphics2D paramGraphics2D)
  {
    path = decodePath1();
    paramGraphics2D.setPaint(color3);
    paramGraphics2D.fill(path);
  }
  
  private void paintarrowIconEnabledAndSelected(Graphics2D paramGraphics2D)
  {
    path = decodePath2();
    paramGraphics2D.setPaint(color4);
    paramGraphics2D.fill(path);
  }
  
  private Rectangle2D decodeRect1()
  {
    rect.setRect(decodeX(1.0F), decodeY(1.0F), decodeX(2.0F) - decodeX(1.0F), decodeY(2.0F) - decodeY(1.0F));
    return rect;
  }
  
  private Path2D decodePath1()
  {
    path.reset();
    path.moveTo(decodeX(0.0F), decodeY(0.2F));
    path.lineTo(decodeX(2.7512195F), decodeY(2.102439F));
    path.lineTo(decodeX(0.0F), decodeY(3.0F));
    path.lineTo(decodeX(0.0F), decodeY(0.2F));
    path.closePath();
    return path;
  }
  
  private Path2D decodePath2()
  {
    path.reset();
    path.moveTo(decodeX(0.0F), decodeY(1.0F));
    path.lineTo(decodeX(1.9529617F), decodeY(1.5625F));
    path.lineTo(decodeX(0.0F), decodeY(3.0F));
    path.lineTo(decodeX(0.0F), decodeY(1.0F));
    path.closePath();
    return path;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\nimbus\MenuPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */