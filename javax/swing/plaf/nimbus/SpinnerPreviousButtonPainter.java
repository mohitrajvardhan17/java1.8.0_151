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

final class SpinnerPreviousButtonPainter
  extends AbstractRegionPainter
{
  static final int BACKGROUND_DISABLED = 1;
  static final int BACKGROUND_ENABLED = 2;
  static final int BACKGROUND_FOCUSED = 3;
  static final int BACKGROUND_MOUSEOVER_FOCUSED = 4;
  static final int BACKGROUND_PRESSED_FOCUSED = 5;
  static final int BACKGROUND_MOUSEOVER = 6;
  static final int BACKGROUND_PRESSED = 7;
  static final int FOREGROUND_DISABLED = 8;
  static final int FOREGROUND_ENABLED = 9;
  static final int FOREGROUND_FOCUSED = 10;
  static final int FOREGROUND_MOUSEOVER_FOCUSED = 11;
  static final int FOREGROUND_PRESSED_FOCUSED = 12;
  static final int FOREGROUND_MOUSEOVER = 13;
  static final int FOREGROUND_PRESSED = 14;
  private int state;
  private AbstractRegionPainter.PaintContext ctx;
  private Path2D path = new Path2D.Float();
  private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
  private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  private Color color1 = decodeColor("nimbusBase", 0.015098333F, -0.5557143F, 0.2352941F, 0);
  private Color color2 = decodeColor("nimbusBase", 0.010237217F, -0.55799407F, 0.20784312F, 0);
  private Color color3 = decodeColor("nimbusBase", 0.018570602F, -0.5821429F, 0.32941175F, 0);
  private Color color4 = decodeColor("nimbusBase", 0.021348298F, -0.56722116F, 0.3098039F, 0);
  private Color color5 = decodeColor("nimbusBase", 0.021348298F, -0.567841F, 0.31764704F, 0);
  private Color color6 = decodeColor("nimbusBlueGrey", 0.0F, -0.0033834577F, -0.30588236F, 65388);
  private Color color7 = decodeColor("nimbusBase", 5.1498413E-4F, -0.2583558F, -0.13333336F, 0);
  private Color color8 = decodeColor("nimbusBase", 5.1498413E-4F, -0.095173776F, -0.25882354F, 0);
  private Color color9 = decodeColor("nimbusBase", 0.004681647F, -0.5383692F, 0.33725488F, 0);
  private Color color10 = decodeColor("nimbusBase", -0.0017285943F, -0.44453782F, 0.25098038F, 0);
  private Color color11 = decodeColor("nimbusBase", 5.1498413E-4F, -0.43866998F, 0.24705881F, 0);
  private Color color12 = decodeColor("nimbusBase", 5.1498413E-4F, -0.4625541F, 0.35686272F, 0);
  private Color color13 = decodeColor("nimbusFocus", 0.0F, 0.0F, 0.0F, 0);
  private Color color14 = decodeColor("nimbusBase", 0.0013483167F, 0.088923395F, -0.2784314F, 0);
  private Color color15 = decodeColor("nimbusBase", 0.059279382F, 0.3642857F, -0.43529415F, 0);
  private Color color16 = decodeColor("nimbusBase", 0.0010585189F, -0.541452F, 0.4078431F, 0);
  private Color color17 = decodeColor("nimbusBase", 0.00254488F, -0.4608264F, 0.32549018F, 0);
  private Color color18 = decodeColor("nimbusBase", 5.1498413E-4F, -0.4555341F, 0.3215686F, 0);
  private Color color19 = decodeColor("nimbusBase", 5.1498413E-4F, -0.4757143F, 0.43137252F, 0);
  private Color color20 = decodeColor("nimbusBase", 0.061133325F, 0.3642857F, -0.427451F, 0);
  private Color color21 = decodeColor("nimbusBase", -3.528595E-5F, 0.018606722F, -0.23137257F, 0);
  private Color color22 = decodeColor("nimbusBase", 8.354783E-4F, -0.2578073F, 0.12549019F, 0);
  private Color color23 = decodeColor("nimbusBase", 8.9377165E-4F, -0.01599598F, 0.007843137F, 0);
  private Color color24 = decodeColor("nimbusBase", 0.0F, -0.00895375F, 0.007843137F, 0);
  private Color color25 = decodeColor("nimbusBase", 8.9377165E-4F, -0.13853917F, 0.14509803F, 0);
  private Color color26 = decodeColor("nimbusBlueGrey", -0.6111111F, -0.110526316F, -0.63529414F, 65357);
  private Color color27 = decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, 65350);
  private Color color28 = decodeColor("nimbusBase", 0.018570602F, -0.56714284F, 0.1372549F, 0);
  private Color color29 = decodeColor("nimbusBase", -0.57865167F, -0.6357143F, -0.54901963F, 0);
  private Color color30 = decodeColor("nimbusBase", 0.0F, -0.6357143F, 0.45098037F, 0);
  private Object[] componentColors;
  
  public SpinnerPreviousButtonPainter(AbstractRegionPainter.PaintContext paramPaintContext, int paramInt)
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
      paintBackgroundDisabled(paramGraphics2D);
      break;
    case 2: 
      paintBackgroundEnabled(paramGraphics2D);
      break;
    case 3: 
      paintBackgroundFocused(paramGraphics2D);
      break;
    case 4: 
      paintBackgroundMouseOverAndFocused(paramGraphics2D);
      break;
    case 5: 
      paintBackgroundPressedAndFocused(paramGraphics2D);
      break;
    case 6: 
      paintBackgroundMouseOver(paramGraphics2D);
      break;
    case 7: 
      paintBackgroundPressed(paramGraphics2D);
      break;
    case 8: 
      paintForegroundDisabled(paramGraphics2D);
      break;
    case 9: 
      paintForegroundEnabled(paramGraphics2D);
      break;
    case 10: 
      paintForegroundFocused(paramGraphics2D);
      break;
    case 11: 
      paintForegroundMouseOverAndFocused(paramGraphics2D);
      break;
    case 12: 
      paintForegroundPressedAndFocused(paramGraphics2D);
      break;
    case 13: 
      paintForegroundMouseOver(paramGraphics2D);
      break;
    case 14: 
      paintForegroundPressed(paramGraphics2D);
    }
  }
  
  protected final AbstractRegionPainter.PaintContext getPaintContext()
  {
    return ctx;
  }
  
  private void paintBackgroundDisabled(Graphics2D paramGraphics2D)
  {
    path = decodePath1();
    paramGraphics2D.setPaint(decodeGradient1(path));
    paramGraphics2D.fill(path);
    path = decodePath2();
    paramGraphics2D.setPaint(decodeGradient2(path));
    paramGraphics2D.fill(path);
  }
  
  private void paintBackgroundEnabled(Graphics2D paramGraphics2D)
  {
    path = decodePath3();
    paramGraphics2D.setPaint(color6);
    paramGraphics2D.fill(path);
    path = decodePath1();
    paramGraphics2D.setPaint(decodeGradient3(path));
    paramGraphics2D.fill(path);
    path = decodePath2();
    paramGraphics2D.setPaint(decodeGradient4(path));
    paramGraphics2D.fill(path);
  }
  
  private void paintBackgroundFocused(Graphics2D paramGraphics2D)
  {
    path = decodePath4();
    paramGraphics2D.setPaint(color13);
    paramGraphics2D.fill(path);
    path = decodePath1();
    paramGraphics2D.setPaint(decodeGradient3(path));
    paramGraphics2D.fill(path);
    path = decodePath2();
    paramGraphics2D.setPaint(decodeGradient4(path));
    paramGraphics2D.fill(path);
  }
  
  private void paintBackgroundMouseOverAndFocused(Graphics2D paramGraphics2D)
  {
    path = decodePath5();
    paramGraphics2D.setPaint(color13);
    paramGraphics2D.fill(path);
    path = decodePath6();
    paramGraphics2D.setPaint(decodeGradient5(path));
    paramGraphics2D.fill(path);
    path = decodePath7();
    paramGraphics2D.setPaint(decodeGradient6(path));
    paramGraphics2D.fill(path);
  }
  
  private void paintBackgroundPressedAndFocused(Graphics2D paramGraphics2D)
  {
    path = decodePath4();
    paramGraphics2D.setPaint(color13);
    paramGraphics2D.fill(path);
    path = decodePath1();
    paramGraphics2D.setPaint(decodeGradient7(path));
    paramGraphics2D.fill(path);
    path = decodePath2();
    paramGraphics2D.setPaint(decodeGradient8(path));
    paramGraphics2D.fill(path);
  }
  
  private void paintBackgroundMouseOver(Graphics2D paramGraphics2D)
  {
    path = decodePath3();
    paramGraphics2D.setPaint(color26);
    paramGraphics2D.fill(path);
    path = decodePath1();
    paramGraphics2D.setPaint(decodeGradient5(path));
    paramGraphics2D.fill(path);
    path = decodePath2();
    paramGraphics2D.setPaint(decodeGradient6(path));
    paramGraphics2D.fill(path);
  }
  
  private void paintBackgroundPressed(Graphics2D paramGraphics2D)
  {
    path = decodePath8();
    paramGraphics2D.setPaint(color27);
    paramGraphics2D.fill(path);
    path = decodePath1();
    paramGraphics2D.setPaint(decodeGradient7(path));
    paramGraphics2D.fill(path);
    path = decodePath2();
    paramGraphics2D.setPaint(decodeGradient8(path));
    paramGraphics2D.fill(path);
  }
  
  private void paintForegroundDisabled(Graphics2D paramGraphics2D)
  {
    path = decodePath9();
    paramGraphics2D.setPaint(color28);
    paramGraphics2D.fill(path);
  }
  
  private void paintForegroundEnabled(Graphics2D paramGraphics2D)
  {
    path = decodePath9();
    paramGraphics2D.setPaint(color29);
    paramGraphics2D.fill(path);
  }
  
  private void paintForegroundFocused(Graphics2D paramGraphics2D)
  {
    path = decodePath9();
    paramGraphics2D.setPaint(color29);
    paramGraphics2D.fill(path);
  }
  
  private void paintForegroundMouseOverAndFocused(Graphics2D paramGraphics2D)
  {
    path = decodePath9();
    paramGraphics2D.setPaint(color29);
    paramGraphics2D.fill(path);
  }
  
  private void paintForegroundPressedAndFocused(Graphics2D paramGraphics2D)
  {
    path = decodePath9();
    paramGraphics2D.setPaint(color30);
    paramGraphics2D.fill(path);
  }
  
  private void paintForegroundMouseOver(Graphics2D paramGraphics2D)
  {
    path = decodePath9();
    paramGraphics2D.setPaint(color29);
    paramGraphics2D.fill(path);
  }
  
  private void paintForegroundPressed(Graphics2D paramGraphics2D)
  {
    path = decodePath9();
    paramGraphics2D.setPaint(color30);
    paramGraphics2D.fill(path);
  }
  
  private Path2D decodePath1()
  {
    path.reset();
    path.moveTo(decodeX(0.0F), decodeY(1.0F));
    path.lineTo(decodeX(0.0F), decodeY(2.6666667F));
    path.lineTo(decodeX(2.142857F), decodeY(2.6666667F));
    path.curveTo(decodeAnchorX(2.142857F, 3.0F), decodeAnchorY(2.6666667F, 0.0F), decodeAnchorX(2.7142859F, 0.0F), decodeAnchorY(2.0F, 2.0F), decodeX(2.7142859F), decodeY(2.0F));
    path.lineTo(decodeX(2.7142859F), decodeY(1.0F));
    path.lineTo(decodeX(0.0F), decodeY(1.0F));
    path.closePath();
    return path;
  }
  
  private Path2D decodePath2()
  {
    path.reset();
    path.moveTo(decodeX(1.0F), decodeY(1.0F));
    path.lineTo(decodeX(1.0F), decodeY(2.5F));
    path.lineTo(decodeX(2.142857F), decodeY(2.5F));
    path.curveTo(decodeAnchorX(2.142857F, 2.0F), decodeAnchorY(2.5F, 0.0F), decodeAnchorX(2.5714285F, 0.0F), decodeAnchorY(2.0F, 1.0F), decodeX(2.5714285F), decodeY(2.0F));
    path.lineTo(decodeX(2.5714285F), decodeY(1.0F));
    path.lineTo(decodeX(1.0F), decodeY(1.0F));
    path.closePath();
    return path;
  }
  
  private Path2D decodePath3()
  {
    path.reset();
    path.moveTo(decodeX(0.0F), decodeY(2.6666667F));
    path.lineTo(decodeX(0.0F), decodeY(2.8333333F));
    path.lineTo(decodeX(2.0324676F), decodeY(2.8333333F));
    path.curveTo(decodeAnchorX(2.0324676F, 2.1136363F), decodeAnchorY(2.8333333F, 0.0F), decodeAnchorX(2.7142859F, 0.0F), decodeAnchorY(2.0F, 3.0F), decodeX(2.7142859F), decodeY(2.0F));
    path.lineTo(decodeX(0.0F), decodeY(2.6666667F));
    path.closePath();
    return path;
  }
  
  private Path2D decodePath4()
  {
    path.reset();
    path.moveTo(decodeX(0.0F), decodeY(1.0F));
    path.lineTo(decodeX(0.0F), decodeY(2.8999999F));
    path.lineTo(decodeX(2.2F), decodeY(2.8999999F));
    path.curveTo(decodeAnchorX(2.2F, 3.0F), decodeAnchorY(2.8999999F, 0.0F), decodeAnchorX(2.9142857F, 0.0F), decodeAnchorY(2.2333333F, 3.0F), decodeX(2.9142857F), decodeY(2.2333333F));
    path.lineTo(decodeX(2.9142857F), decodeY(1.0F));
    path.lineTo(decodeX(0.0F), decodeY(1.0F));
    path.closePath();
    return path;
  }
  
  private Path2D decodePath5()
  {
    path.reset();
    path.moveTo(decodeX(0.0F), decodeY(0.0F));
    path.lineTo(decodeX(0.0F), decodeY(2.8999999F));
    path.lineTo(decodeX(2.2F), decodeY(2.8999999F));
    path.curveTo(decodeAnchorX(2.2F, 3.0F), decodeAnchorY(2.8999999F, 0.0F), decodeAnchorX(2.9142857F, 0.0F), decodeAnchorY(2.2333333F, 3.0F), decodeX(2.9142857F), decodeY(2.2333333F));
    path.lineTo(decodeX(2.9142857F), decodeY(0.0F));
    path.lineTo(decodeX(0.0F), decodeY(0.0F));
    path.closePath();
    return path;
  }
  
  private Path2D decodePath6()
  {
    path.reset();
    path.moveTo(decodeX(0.0F), decodeY(0.0F));
    path.lineTo(decodeX(0.0F), decodeY(2.6666667F));
    path.lineTo(decodeX(2.142857F), decodeY(2.6666667F));
    path.curveTo(decodeAnchorX(2.142857F, 3.0F), decodeAnchorY(2.6666667F, 0.0F), decodeAnchorX(2.7142859F, 0.0F), decodeAnchorY(2.0F, 2.0F), decodeX(2.7142859F), decodeY(2.0F));
    path.lineTo(decodeX(2.7142859F), decodeY(0.0F));
    path.lineTo(decodeX(0.0F), decodeY(0.0F));
    path.closePath();
    return path;
  }
  
  private Path2D decodePath7()
  {
    path.reset();
    path.moveTo(decodeX(1.0F), decodeY(0.0F));
    path.lineTo(decodeX(1.0F), decodeY(2.5F));
    path.lineTo(decodeX(2.142857F), decodeY(2.5F));
    path.curveTo(decodeAnchorX(2.142857F, 2.0F), decodeAnchorY(2.5F, 0.0F), decodeAnchorX(2.5714285F, 0.0F), decodeAnchorY(2.0F, 1.0F), decodeX(2.5714285F), decodeY(2.0F));
    path.lineTo(decodeX(2.5714285F), decodeY(0.0F));
    path.lineTo(decodeX(1.0F), decodeY(0.0F));
    path.closePath();
    return path;
  }
  
  private Path2D decodePath8()
  {
    path.reset();
    path.moveTo(decodeX(0.0F), decodeY(2.6666667F));
    path.lineTo(decodeX(0.0F), decodeY(2.8333333F));
    path.curveTo(decodeAnchorX(0.0F, 0.0F), decodeAnchorY(2.8333333F, 0.0F), decodeAnchorX(2.0324676F, -2.1136363F), decodeAnchorY(2.8333333F, 0.0F), decodeX(2.0324676F), decodeY(2.8333333F));
    path.curveTo(decodeAnchorX(2.0324676F, 2.1136363F), decodeAnchorY(2.8333333F, 0.0F), decodeAnchorX(2.7142859F, 0.0F), decodeAnchorY(2.0F, 3.0F), decodeX(2.7142859F), decodeY(2.0F));
    path.lineTo(decodeX(0.0F), decodeY(2.6666667F));
    path.closePath();
    return path;
  }
  
  private Path2D decodePath9()
  {
    path.reset();
    path.moveTo(decodeX(1.0F), decodeY(1.0F));
    path.lineTo(decodeX(1.5045455F), decodeY(1.9943181F));
    path.lineTo(decodeX(2.0F), decodeY(1.0F));
    path.lineTo(decodeX(1.0F), decodeY(1.0F));
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
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.05748663F, 0.11497326F, 0.55748665F, 1.0F }, new Color[] { color3, decodeColor(color3, color4, 0.5F), color4, decodeColor(color4, color5, 0.5F), color5 });
  }
  
  private Paint decodeGradient3(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color7, decodeColor(color7, color8, 0.5F), color8 });
  }
  
  private Paint decodeGradient4(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.05748663F, 0.11497326F, 0.2419786F, 0.36898395F, 0.684492F, 1.0F }, new Color[] { color9, decodeColor(color9, color10, 0.5F), color10, decodeColor(color10, color11, 0.5F), color11, decodeColor(color11, color12, 0.5F), color12 });
  }
  
  private Paint decodeGradient5(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color14, decodeColor(color14, color15, 0.5F), color15 });
  }
  
  private Paint decodeGradient6(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.05748663F, 0.11497326F, 0.2419786F, 0.36898395F, 0.684492F, 1.0F }, new Color[] { color16, decodeColor(color16, color17, 0.5F), color17, decodeColor(color17, color18, 0.5F), color18, decodeColor(color18, color19, 0.5F), color19 });
  }
  
  private Paint decodeGradient7(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color20, decodeColor(color20, color21, 0.5F), color21 });
  }
  
  private Paint decodeGradient8(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.05748663F, 0.11497326F, 0.2419786F, 0.36898395F, 0.684492F, 1.0F }, new Color[] { color22, decodeColor(color22, color23, 0.5F), color23, decodeColor(color23, color24, 0.5F), color24, decodeColor(color24, color25, 0.5F), color25 });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\nimbus\SpinnerPreviousButtonPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */