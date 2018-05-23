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

final class ScrollBarThumbPainter
  extends AbstractRegionPainter
{
  static final int BACKGROUND_DISABLED = 1;
  static final int BACKGROUND_ENABLED = 2;
  static final int BACKGROUND_FOCUSED = 3;
  static final int BACKGROUND_MOUSEOVER = 4;
  static final int BACKGROUND_PRESSED = 5;
  private int state;
  private AbstractRegionPainter.PaintContext ctx;
  private Path2D path = new Path2D.Float();
  private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
  private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  private Color color1 = decodeColor("nimbusBase", 5.1498413E-4F, 0.18061227F, -0.35686278F, 0);
  private Color color2 = decodeColor("nimbusBase", 5.1498413E-4F, -0.21018237F, -0.18039218F, 0);
  private Color color3 = decodeColor("nimbusBase", 7.13408E-4F, -0.53277314F, 0.25098038F, 0);
  private Color color4 = decodeColor("nimbusBase", -0.07865167F, -0.6317617F, 0.44313723F, 0);
  private Color color5 = decodeColor("nimbusBase", 5.1498413E-4F, -0.44340658F, 0.26666665F, 0);
  private Color color6 = decodeColor("nimbusBase", 5.1498413E-4F, -0.4669379F, 0.38039213F, 0);
  private Color color7 = decodeColor("nimbusBase", -0.07865167F, -0.56512606F, 0.45098037F, 0);
  private Color color8 = decodeColor("nimbusBase", -0.0017285943F, -0.362987F, 0.011764705F, 0);
  private Color color9 = decodeColor("nimbusBase", 5.2034855E-5F, -0.41753247F, 0.09803921F, 65314);
  private Color color10 = new Color(255, 200, 0, 255);
  private Color color11 = decodeColor("nimbusBase", -0.0017285943F, -0.362987F, 0.011764705F, 65281);
  private Color color12 = decodeColor("nimbusBase", 0.010237217F, -0.5621849F, 0.25098038F, 0);
  private Color color13 = decodeColor("nimbusBase", 0.08801502F, -0.6317773F, 0.4470588F, 0);
  private Color color14 = decodeColor("nimbusBase", 5.1498413E-4F, -0.45950285F, 0.34117645F, 0);
  private Color color15 = decodeColor("nimbusBase", -0.0017285943F, -0.48277313F, 0.45098037F, 0);
  private Color color16 = decodeColor("nimbusBase", 0.0F, -0.6357143F, 0.45098037F, 0);
  private Color color17 = decodeColor("nimbusBase", -0.57865167F, -0.6357143F, -0.54901963F, 0);
  private Color color18 = decodeColor("nimbusBase", 0.0013483167F, 0.29021162F, -0.33725494F, 0);
  private Color color19 = decodeColor("nimbusBase", 0.002908647F, -0.29012606F, -0.015686274F, 0);
  private Color color20 = decodeColor("nimbusBase", -8.738637E-4F, -0.40612245F, 0.21960783F, 0);
  private Color color21 = decodeColor("nimbusBase", 0.0F, -0.01765871F, 0.015686274F, 0);
  private Color color22 = decodeColor("nimbusBase", 0.0F, -0.12714285F, 0.1372549F, 0);
  private Color color23 = decodeColor("nimbusBase", 0.0018727183F, -0.23116884F, 0.31372547F, 0);
  private Color color24 = decodeColor("nimbusBase", -8.738637E-4F, -0.3579365F, -0.33725494F, 0);
  private Color color25 = decodeColor("nimbusBase", 0.004681647F, -0.3857143F, -0.36078435F, 0);
  private Object[] componentColors;
  
  public ScrollBarThumbPainter(AbstractRegionPainter.PaintContext paramPaintContext, int paramInt)
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
      paintBackgroundEnabled(paramGraphics2D);
      break;
    case 4: 
      paintBackgroundMouseOver(paramGraphics2D);
      break;
    case 5: 
      paintBackgroundPressed(paramGraphics2D);
    }
  }
  
  protected final AbstractRegionPainter.PaintContext getPaintContext()
  {
    return ctx;
  }
  
  private void paintBackgroundEnabled(Graphics2D paramGraphics2D)
  {
    path = decodePath1();
    paramGraphics2D.setPaint(decodeGradient1(path));
    paramGraphics2D.fill(path);
    path = decodePath2();
    paramGraphics2D.setPaint(decodeGradient2(path));
    paramGraphics2D.fill(path);
    path = decodePath3();
    paramGraphics2D.setPaint(decodeGradient3(path));
    paramGraphics2D.fill(path);
    path = decodePath4();
    paramGraphics2D.setPaint(color10);
    paramGraphics2D.fill(path);
    path = decodePath5();
    paramGraphics2D.setPaint(decodeGradient4(path));
    paramGraphics2D.fill(path);
  }
  
  private void paintBackgroundMouseOver(Graphics2D paramGraphics2D)
  {
    path = decodePath1();
    paramGraphics2D.setPaint(decodeGradient1(path));
    paramGraphics2D.fill(path);
    path = decodePath2();
    paramGraphics2D.setPaint(decodeGradient5(path));
    paramGraphics2D.fill(path);
    path = decodePath3();
    paramGraphics2D.setPaint(decodeGradient3(path));
    paramGraphics2D.fill(path);
    path = decodePath4();
    paramGraphics2D.setPaint(color10);
    paramGraphics2D.fill(path);
    path = decodePath5();
    paramGraphics2D.setPaint(decodeGradient4(path));
    paramGraphics2D.fill(path);
  }
  
  private void paintBackgroundPressed(Graphics2D paramGraphics2D)
  {
    path = decodePath1();
    paramGraphics2D.setPaint(decodeGradient6(path));
    paramGraphics2D.fill(path);
    path = decodePath2();
    paramGraphics2D.setPaint(decodeGradient7(path));
    paramGraphics2D.fill(path);
    path = decodePath3();
    paramGraphics2D.setPaint(decodeGradient8(path));
    paramGraphics2D.fill(path);
    path = decodePath4();
    paramGraphics2D.setPaint(color10);
    paramGraphics2D.fill(path);
    path = decodePath6();
    paramGraphics2D.setPaint(decodeGradient9(path));
    paramGraphics2D.fill(path);
  }
  
  private Path2D decodePath1()
  {
    path.reset();
    path.moveTo(decodeX(0.0F), decodeY(1.0F));
    path.lineTo(decodeX(0.0F), decodeY(1.0666667F));
    path.curveTo(decodeAnchorX(0.0F, 0.0F), decodeAnchorY(1.0666667F, 6.0F), decodeAnchorX(1.0F, -10.0F), decodeAnchorY(2.0F, 0.0F), decodeX(1.0F), decodeY(2.0F));
    path.lineTo(decodeX(2.0F), decodeY(2.0F));
    path.curveTo(decodeAnchorX(2.0F, 10.0F), decodeAnchorY(2.0F, 0.0F), decodeAnchorX(3.0F, 0.0F), decodeAnchorY(1.0666667F, 6.0F), decodeX(3.0F), decodeY(1.0666667F));
    path.lineTo(decodeX(3.0F), decodeY(1.0F));
    path.lineTo(decodeX(0.0F), decodeY(1.0F));
    path.closePath();
    return path;
  }
  
  private Path2D decodePath2()
  {
    path.reset();
    path.moveTo(decodeX(0.06666667F), decodeY(1.0F));
    path.lineTo(decodeX(0.06666667F), decodeY(1.0666667F));
    path.curveTo(decodeAnchorX(0.06666667F, -0.045454547F), decodeAnchorY(1.0666667F, 8.454545F), decodeAnchorX(1.0F, -5.8636365F), decodeAnchorY(1.9333334F, 0.0F), decodeX(1.0F), decodeY(1.9333334F));
    path.lineTo(decodeX(2.0F), decodeY(1.9333334F));
    path.curveTo(decodeAnchorX(2.0F, 5.909091F), decodeAnchorY(1.9333334F, -3.5527137E-15F), decodeAnchorX(2.9333334F, -0.045454547F), decodeAnchorY(1.0666667F, 8.363636F), decodeX(2.9333334F), decodeY(1.0666667F));
    path.lineTo(decodeX(2.9333334F), decodeY(1.0F));
    path.lineTo(decodeX(0.06666667F), decodeY(1.0F));
    path.closePath();
    return path;
  }
  
  private Path2D decodePath3()
  {
    path.reset();
    path.moveTo(decodeX(0.4F), decodeY(1.0F));
    path.lineTo(decodeX(0.06666667F), decodeY(1.0F));
    path.lineTo(decodeX(0.16060607F), decodeY(1.5090909F));
    path.curveTo(decodeAnchorX(0.16060607F, 0.0F), decodeAnchorY(1.5090909F, 0.0F), decodeAnchorX(0.2F, -0.95454544F), decodeAnchorY(1.1363636F, 1.5454545F), decodeX(0.2F), decodeY(1.1363636F));
    path.curveTo(decodeAnchorX(0.2F, 0.95454544F), decodeAnchorY(1.1363636F, -1.5454545F), decodeAnchorX(0.4F, 0.0F), decodeAnchorY(1.0F, 0.0F), decodeX(0.4F), decodeY(1.0F));
    path.closePath();
    return path;
  }
  
  private Path2D decodePath4()
  {
    path.reset();
    path.moveTo(decodeX(2.4242425F), decodeY(1.5121212F));
    path.lineTo(decodeX(2.4242425F), decodeY(1.5121212F));
    path.closePath();
    return path;
  }
  
  private Path2D decodePath5()
  {
    path.reset();
    path.moveTo(decodeX(2.9363637F), decodeY(1.0F));
    path.lineTo(decodeX(2.6030304F), decodeY(1.0F));
    path.curveTo(decodeAnchorX(2.6030304F, 0.0F), decodeAnchorY(1.0F, 0.0F), decodeAnchorX(2.778788F, -0.6818182F), decodeAnchorY(1.1333333F, -1.2272727F), decodeX(2.778788F), decodeY(1.1333333F));
    path.curveTo(decodeAnchorX(2.778788F, 0.6818182F), decodeAnchorY(1.1333333F, 1.2272727F), decodeAnchorX(2.8393939F, 0.0F), decodeAnchorY(1.5060606F, 0.0F), decodeX(2.8393939F), decodeY(1.5060606F));
    path.lineTo(decodeX(2.9363637F), decodeY(1.0F));
    path.closePath();
    return path;
  }
  
  private Path2D decodePath6()
  {
    path.reset();
    path.moveTo(decodeX(2.9363637F), decodeY(1.0F));
    path.lineTo(decodeX(2.5563636F), decodeY(1.0F));
    path.curveTo(decodeAnchorX(2.5563636F, 0.0F), decodeAnchorY(1.0F, 0.0F), decodeAnchorX(2.7587879F, -0.6818182F), decodeAnchorY(1.14F, -1.2272727F), decodeX(2.7587879F), decodeY(1.14F));
    path.curveTo(decodeAnchorX(2.7587879F, 0.6818182F), decodeAnchorY(1.14F, 1.2272727F), decodeAnchorX(2.8393939F, 0.0F), decodeAnchorY(1.5060606F, 0.0F), decodeX(2.8393939F), decodeY(1.5060606F));
    path.lineTo(decodeX(2.9363637F), decodeY(1.0F));
    path.closePath();
    return path;
  }
  
  private Paint decodeGradient1(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color1, decodeColor(color1, color2, 0.5F), color2 });
  }
  
  private Paint decodeGradient2(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.038922157F, 0.0508982F, 0.06287425F, 0.19610777F, 0.32934132F, 0.48952097F, 0.6497006F, 0.8248503F, 1.0F }, new Color[] { color3, decodeColor(color3, color4, 0.5F), color4, decodeColor(color4, color5, 0.5F), color5, decodeColor(color5, color6, 0.5F), color6, decodeColor(color6, color7, 0.5F), color7 });
  }
  
  private Paint decodeGradient3(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.06818182F * f3 + f1, -0.005952381F * f4 + f2, 0.3689091F * f3 + f1, 0.23929171F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color8, decodeColor(color8, color9, 0.5F), color9 });
  }
  
  private Paint decodeGradient4(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.9409091F * f3 + f1, 0.035928145F * f4 + f2, 0.5954546F * f3 + f1, 0.26347303F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color8, decodeColor(color8, color11, 0.5F), color11 });
  }
  
  private Paint decodeGradient5(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.038922157F, 0.0508982F, 0.06287425F, 0.19610777F, 0.32934132F, 0.48952097F, 0.6497006F, 0.8248503F, 1.0F }, new Color[] { color12, decodeColor(color12, color13, 0.5F), color13, decodeColor(color13, color14, 0.5F), color14, decodeColor(color14, color15, 0.5F), color15, decodeColor(color15, color16, 0.5F), color16 });
  }
  
  private Paint decodeGradient6(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color17, decodeColor(color17, color18, 0.5F), color18 });
  }
  
  private Paint decodeGradient7(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.038922157F, 0.0508982F, 0.06287425F, 0.19610777F, 0.32934132F, 0.48952097F, 0.6497006F, 0.8248503F, 1.0F }, new Color[] { color19, decodeColor(color19, color20, 0.5F), color20, decodeColor(color20, color21, 0.5F), color21, decodeColor(color21, color22, 0.5F), color22, decodeColor(color22, color23, 0.5F), color23 });
  }
  
  private Paint decodeGradient8(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.06818182F * f3 + f1, -0.005952381F * f4 + f2, 0.3689091F * f3 + f1, 0.23929171F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color24, decodeColor(color24, color9, 0.5F), color9 });
  }
  
  private Paint decodeGradient9(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.9409091F * f3 + f1, 0.035928145F * f4 + f2, 0.37615633F * f3 + f1, 0.34910178F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color25, decodeColor(color25, color11, 0.5F), color11 });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\nimbus\ScrollBarThumbPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */