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

final class ToolBarButtonPainter
  extends AbstractRegionPainter
{
  static final int BACKGROUND_ENABLED = 1;
  static final int BACKGROUND_FOCUSED = 2;
  static final int BACKGROUND_MOUSEOVER = 3;
  static final int BACKGROUND_MOUSEOVER_FOCUSED = 4;
  static final int BACKGROUND_PRESSED = 5;
  static final int BACKGROUND_PRESSED_FOCUSED = 6;
  private int state;
  private AbstractRegionPainter.PaintContext ctx;
  private Path2D path = new Path2D.Float();
  private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
  private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  private Color color1 = decodeColor("nimbusFocus", 0.0F, 0.0F, 0.0F, 0);
  private Color color2 = decodeColor("nimbusBlueGrey", -0.027777791F, -0.06885965F, -0.36862746F, 65383);
  private Color color3 = decodeColor("nimbusBlueGrey", 0.0F, -0.020974077F, -0.21960783F, 0);
  private Color color4 = decodeColor("nimbusBlueGrey", 0.0F, 0.11169591F, -0.53333336F, 0);
  private Color color5 = decodeColor("nimbusBlueGrey", 0.055555582F, -0.10658931F, 0.25098038F, 0);
  private Color color6 = decodeColor("nimbusBlueGrey", 0.0F, -0.098526314F, 0.2352941F, 0);
  private Color color7 = decodeColor("nimbusBlueGrey", 0.0F, -0.07333623F, 0.20392156F, 0);
  private Color color8 = decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, 0);
  private Color color9 = decodeColor("nimbusBlueGrey", -0.00505054F, -0.05960039F, 0.10196078F, 0);
  private Color color10 = decodeColor("nimbusBlueGrey", -0.008547008F, -0.04772438F, 0.06666666F, 0);
  private Color color11 = decodeColor("nimbusBlueGrey", -0.0027777553F, -0.0018306673F, -0.02352941F, 0);
  private Color color12 = decodeColor("nimbusBlueGrey", -0.0027777553F, -0.0212406F, 0.13333333F, 0);
  private Color color13 = decodeColor("nimbusBlueGrey", 0.0055555105F, -0.030845039F, 0.23921567F, 0);
  private Object[] componentColors;
  
  public ToolBarButtonPainter(AbstractRegionPainter.PaintContext paramPaintContext, int paramInt)
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
      paintBackgroundFocused(paramGraphics2D);
      break;
    case 3: 
      paintBackgroundMouseOver(paramGraphics2D);
      break;
    case 4: 
      paintBackgroundMouseOverAndFocused(paramGraphics2D);
      break;
    case 5: 
      paintBackgroundPressed(paramGraphics2D);
      break;
    case 6: 
      paintBackgroundPressedAndFocused(paramGraphics2D);
    }
  }
  
  protected final AbstractRegionPainter.PaintContext getPaintContext()
  {
    return ctx;
  }
  
  private void paintBackgroundFocused(Graphics2D paramGraphics2D)
  {
    path = decodePath1();
    paramGraphics2D.setPaint(color1);
    paramGraphics2D.fill(path);
  }
  
  private void paintBackgroundMouseOver(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(color2);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient1(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient2(roundRect));
    paramGraphics2D.fill(roundRect);
  }
  
  private void paintBackgroundMouseOverAndFocused(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect4();
    paramGraphics2D.setPaint(color1);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient1(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient2(roundRect));
    paramGraphics2D.fill(roundRect);
  }
  
  private void paintBackgroundPressed(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(color2);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient1(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient3(roundRect));
    paramGraphics2D.fill(roundRect);
  }
  
  private void paintBackgroundPressedAndFocused(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect4();
    paramGraphics2D.setPaint(color1);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient1(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient3(roundRect));
    paramGraphics2D.fill(roundRect);
  }
  
  private Path2D decodePath1()
  {
    path.reset();
    path.moveTo(decodeX(1.4133738F), decodeY(0.120000005F));
    path.lineTo(decodeX(1.9893618F), decodeY(0.120000005F));
    path.curveTo(decodeAnchorX(1.9893618F, 3.0F), decodeAnchorY(0.120000005F, 0.0F), decodeAnchorX(2.8857148F, 0.0F), decodeAnchorY(1.0434783F, -3.0F), decodeX(2.8857148F), decodeY(1.0434783F));
    path.lineTo(decodeX(2.9F), decodeY(1.9565217F));
    path.curveTo(decodeAnchorX(2.9F, 0.0F), decodeAnchorY(1.9565217F, 3.0F), decodeAnchorX(1.9893618F, 3.0F), decodeAnchorY(2.8714287F, 0.0F), decodeX(1.9893618F), decodeY(2.8714287F));
    path.lineTo(decodeX(1.0106384F), decodeY(2.8714287F));
    path.curveTo(decodeAnchorX(1.0106384F, -3.0F), decodeAnchorY(2.8714287F, 0.0F), decodeAnchorX(0.120000005F, 0.0F), decodeAnchorY(1.9565217F, 3.0F), decodeX(0.120000005F), decodeY(1.9565217F));
    path.lineTo(decodeX(0.120000005F), decodeY(1.0465839F));
    path.curveTo(decodeAnchorX(0.120000005F, 0.0F), decodeAnchorY(1.0465839F, -3.0F), decodeAnchorX(1.0106384F, -3.0F), decodeAnchorY(0.120000005F, 0.0F), decodeX(1.0106384F), decodeY(0.120000005F));
    path.lineTo(decodeX(1.4148936F), decodeY(0.120000005F));
    path.lineTo(decodeX(1.4148936F), decodeY(0.4857143F));
    path.lineTo(decodeX(1.0106384F), decodeY(0.4857143F));
    path.curveTo(decodeAnchorX(1.0106384F, -1.9285715F), decodeAnchorY(0.4857143F, 0.0F), decodeAnchorX(0.47142857F, -0.044279482F), decodeAnchorY(1.0403726F, -2.429218F), decodeX(0.47142857F), decodeY(1.0403726F));
    path.lineTo(decodeX(0.47142857F), decodeY(1.9565217F));
    path.curveTo(decodeAnchorX(0.47142857F, 0.0F), decodeAnchorY(1.9565217F, 2.2142856F), decodeAnchorX(1.0106384F, -1.7857143F), decodeAnchorY(2.5142856F, 0.0F), decodeX(1.0106384F), decodeY(2.5142856F));
    path.lineTo(decodeX(1.9893618F), decodeY(2.5142856F));
    path.curveTo(decodeAnchorX(1.9893618F, 2.0714285F), decodeAnchorY(2.5142856F, 0.0F), decodeAnchorX(2.5F, 0.0F), decodeAnchorY(1.9565217F, 2.2142856F), decodeX(2.5F), decodeY(1.9565217F));
    path.lineTo(decodeX(2.5142853F), decodeY(1.0434783F));
    path.curveTo(decodeAnchorX(2.5142853F, 0.0F), decodeAnchorY(1.0434783F, -2.142857F), decodeAnchorX(1.9901216F, 2.142857F), decodeAnchorY(0.47142857F, 0.0F), decodeX(1.9901216F), decodeY(0.47142857F));
    path.lineTo(decodeX(1.4148936F), decodeY(0.4857143F));
    path.lineTo(decodeX(1.4133738F), decodeY(0.120000005F));
    path.closePath();
    return path;
  }
  
  private RoundRectangle2D decodeRoundRect1()
  {
    roundRect.setRoundRect(decodeX(0.4F), decodeY(0.6F), decodeX(2.6F) - decodeX(0.4F), decodeY(2.8F) - decodeY(0.6F), 12.0D, 12.0D);
    return roundRect;
  }
  
  private RoundRectangle2D decodeRoundRect2()
  {
    roundRect.setRoundRect(decodeX(0.4F), decodeY(0.4F), decodeX(2.6F) - decodeX(0.4F), decodeY(2.6F) - decodeY(0.4F), 12.0D, 12.0D);
    return roundRect;
  }
  
  private RoundRectangle2D decodeRoundRect3()
  {
    roundRect.setRoundRect(decodeX(0.6F), decodeY(0.6F), decodeX(2.4F) - decodeX(0.6F), decodeY(2.4F) - decodeY(0.6F), 9.0D, 9.0D);
    return roundRect;
  }
  
  private RoundRectangle2D decodeRoundRect4()
  {
    roundRect.setRoundRect(decodeX(0.120000005F), decodeY(0.120000005F), decodeX(2.8800004F) - decodeX(0.120000005F), decodeY(2.8800004F) - decodeY(0.120000005F), 13.0D, 13.0D);
    return roundRect;
  }
  
  private Paint decodeGradient1(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.09F, 0.52F, 0.95F }, new Color[] { color3, decodeColor(color3, color4, 0.5F), color4 });
  }
  
  private Paint decodeGradient2(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.03F, 0.06F, 0.33F, 0.6F, 0.65F, 0.7F, 0.825F, 0.95F, 0.975F, 1.0F }, new Color[] { color5, decodeColor(color5, color6, 0.5F), color6, decodeColor(color6, color7, 0.5F), color7, decodeColor(color7, color7, 0.5F), color7, decodeColor(color7, color8, 0.5F), color8, decodeColor(color8, color8, 0.5F), color8 });
  }
  
  private Paint decodeGradient3(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.03F, 0.06F, 0.33F, 0.6F, 0.65F, 0.7F, 0.825F, 0.95F, 0.975F, 1.0F }, new Color[] { color9, decodeColor(color9, color10, 0.5F), color10, decodeColor(color10, color11, 0.5F), color11, decodeColor(color11, color11, 0.5F), color11, decodeColor(color11, color12, 0.5F), color12, decodeColor(color12, color13, 0.5F), color13 });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\nimbus\ToolBarButtonPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */