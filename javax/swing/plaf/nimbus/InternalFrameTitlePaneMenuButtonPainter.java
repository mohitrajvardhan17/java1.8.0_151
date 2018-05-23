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

final class InternalFrameTitlePaneMenuButtonPainter
  extends AbstractRegionPainter
{
  static final int ICON_ENABLED = 1;
  static final int ICON_DISABLED = 2;
  static final int ICON_MOUSEOVER = 3;
  static final int ICON_PRESSED = 4;
  static final int ICON_ENABLED_WINDOWNOTFOCUSED = 5;
  static final int ICON_MOUSEOVER_WINDOWNOTFOCUSED = 6;
  static final int ICON_PRESSED_WINDOWNOTFOCUSED = 7;
  private int state;
  private AbstractRegionPainter.PaintContext ctx;
  private Path2D path = new Path2D.Float();
  private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
  private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  private Color color1 = decodeColor("nimbusBlueGrey", 0.0055555105F, -0.0029994324F, -0.38039216F, 65351);
  private Color color2 = decodeColor("nimbusBase", 0.08801502F, 0.3642857F, -0.5019608F, 0);
  private Color color3 = decodeColor("nimbusBase", 0.030543745F, -0.3835404F, -0.09803924F, 0);
  private Color color4 = decodeColor("nimbusBase", 0.029191494F, -0.53801316F, 0.13333333F, 0);
  private Color color5 = decodeColor("nimbusBase", 0.030543745F, -0.3857143F, -0.09411767F, 0);
  private Color color6 = decodeColor("nimbusBase", 0.030543745F, -0.43148893F, 0.007843137F, 0);
  private Color color7 = decodeColor("nimbusBase", 0.029191494F, -0.24935067F, -0.20392159F, 65404);
  private Color color8 = decodeColor("nimbusBase", 0.029191494F, -0.24935067F, -0.20392159F, 0);
  private Color color9 = decodeColor("nimbusBase", 0.029191494F, -0.24935067F, -0.20392159F, -123);
  private Color color10 = decodeColor("nimbusBase", 0.0F, -0.6357143F, 0.45098037F, 0);
  private Color color11 = decodeColor("nimbusBlueGrey", 0.0055555105F, -0.0029994324F, -0.38039216F, 65328);
  private Color color12 = decodeColor("nimbusBase", 0.02551502F, -0.5942635F, 0.20784312F, 0);
  private Color color13 = decodeColor("nimbusBase", 0.032459438F, -0.5490091F, 0.12941176F, 0);
  private Color color14 = decodeColor("nimbusBase", 0.032459438F, -0.5469569F, 0.11372548F, 0);
  private Color color15 = decodeColor("nimbusBase", 0.032459438F, -0.5760128F, 0.23921567F, 0);
  private Color color16 = decodeColor("nimbusBase", 0.08801502F, 0.3642857F, -0.4901961F, 0);
  private Color color17 = decodeColor("nimbusBase", 0.032459438F, -0.1857143F, -0.23529413F, 0);
  private Color color18 = decodeColor("nimbusBase", 0.029191494F, -0.5438224F, 0.17647058F, 0);
  private Color color19 = decodeColor("nimbusBase", 0.030543745F, -0.41929638F, -0.02352941F, 0);
  private Color color20 = decodeColor("nimbusBase", 0.030543745F, -0.45559007F, 0.082352936F, 0);
  private Color color21 = decodeColor("nimbusBase", 0.03409344F, -0.329408F, -0.11372551F, 65404);
  private Color color22 = decodeColor("nimbusBase", 0.03409344F, -0.329408F, -0.11372551F, 0);
  private Color color23 = decodeColor("nimbusBase", 0.03409344F, -0.329408F, -0.11372551F, -123);
  private Color color24 = decodeColor("nimbusBase", -0.57865167F, -0.6357143F, -0.54901963F, 0);
  private Color color25 = decodeColor("nimbusBase", 0.031104386F, 0.12354499F, -0.33725494F, 0);
  private Color color26 = decodeColor("nimbusBase", 0.032459438F, -0.4592437F, -0.015686274F, 0);
  private Color color27 = decodeColor("nimbusBase", 0.029191494F, -0.2579365F, -0.19607845F, 0);
  private Color color28 = decodeColor("nimbusBase", 0.03409344F, -0.3149596F, -0.13333336F, 0);
  private Color color29 = decodeColor("nimbusBase", 0.029681683F, 0.07857144F, -0.3294118F, 65404);
  private Color color30 = decodeColor("nimbusBase", 0.029681683F, 0.07857144F, -0.3294118F, 0);
  private Color color31 = decodeColor("nimbusBase", 0.029681683F, 0.07857144F, -0.3294118F, -123);
  private Color color32 = decodeColor("nimbusBase", 0.032459438F, -0.53637654F, 0.043137252F, 0);
  private Color color33 = decodeColor("nimbusBase", 0.032459438F, -0.49935067F, -0.11764708F, 0);
  private Color color34 = decodeColor("nimbusBase", 0.021348298F, -0.6133929F, 0.32941175F, 0);
  private Color color35 = decodeColor("nimbusBase", 0.042560518F, -0.5804379F, 0.23137254F, 0);
  private Color color36 = decodeColor("nimbusBase", 0.032459438F, -0.57417583F, 0.21568626F, 0);
  private Color color37 = decodeColor("nimbusBase", 0.027408898F, -0.5784226F, 0.20392156F, 65404);
  private Color color38 = decodeColor("nimbusBase", 0.042560518F, -0.5665319F, 0.0745098F, 0);
  private Color color39 = decodeColor("nimbusBase", 0.036732912F, -0.5642857F, 0.16470587F, -123);
  private Color color40 = decodeColor("nimbusBase", 0.021348298F, -0.54480517F, -0.11764708F, 0);
  private Object[] componentColors;
  
  public InternalFrameTitlePaneMenuButtonPainter(AbstractRegionPainter.PaintContext paramPaintContext, int paramInt)
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
      painticonEnabled(paramGraphics2D);
      break;
    case 2: 
      painticonDisabled(paramGraphics2D);
      break;
    case 3: 
      painticonMouseOver(paramGraphics2D);
      break;
    case 4: 
      painticonPressed(paramGraphics2D);
      break;
    case 5: 
      painticonEnabledAndWindowNotFocused(paramGraphics2D);
      break;
    case 6: 
      painticonMouseOverAndWindowNotFocused(paramGraphics2D);
      break;
    case 7: 
      painticonPressedAndWindowNotFocused(paramGraphics2D);
    }
  }
  
  protected final AbstractRegionPainter.PaintContext getPaintContext()
  {
    return ctx;
  }
  
  private void painticonEnabled(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(color1);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient1(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient2(roundRect));
    paramGraphics2D.fill(roundRect);
    path = decodePath1();
    paramGraphics2D.setPaint(decodeGradient3(path));
    paramGraphics2D.fill(path);
    path = decodePath2();
    paramGraphics2D.setPaint(color10);
    paramGraphics2D.fill(path);
  }
  
  private void painticonDisabled(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(color11);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient4(roundRect));
    paramGraphics2D.fill(roundRect);
    path = decodePath2();
    paramGraphics2D.setPaint(color15);
    paramGraphics2D.fill(path);
  }
  
  private void painticonMouseOver(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(color1);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient5(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient6(roundRect));
    paramGraphics2D.fill(roundRect);
    path = decodePath1();
    paramGraphics2D.setPaint(decodeGradient7(path));
    paramGraphics2D.fill(path);
    path = decodePath2();
    paramGraphics2D.setPaint(color10);
    paramGraphics2D.fill(path);
  }
  
  private void painticonPressed(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(color1);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient8(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient9(roundRect));
    paramGraphics2D.fill(roundRect);
    path = decodePath1();
    paramGraphics2D.setPaint(decodeGradient10(path));
    paramGraphics2D.fill(path);
    path = decodePath2();
    paramGraphics2D.setPaint(color10);
    paramGraphics2D.fill(path);
  }
  
  private void painticonEnabledAndWindowNotFocused(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient11(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient12(roundRect));
    paramGraphics2D.fill(roundRect);
    path = decodePath3();
    paramGraphics2D.setPaint(decodeGradient13(path));
    paramGraphics2D.fill(path);
    path = decodePath2();
    paramGraphics2D.setPaint(color40);
    paramGraphics2D.fill(path);
  }
  
  private void painticonMouseOverAndWindowNotFocused(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(color1);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient5(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient6(roundRect));
    paramGraphics2D.fill(roundRect);
    path = decodePath1();
    paramGraphics2D.setPaint(decodeGradient7(path));
    paramGraphics2D.fill(path);
    path = decodePath2();
    paramGraphics2D.setPaint(color10);
    paramGraphics2D.fill(path);
  }
  
  private void painticonPressedAndWindowNotFocused(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(color1);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient8(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient9(roundRect));
    paramGraphics2D.fill(roundRect);
    path = decodePath1();
    paramGraphics2D.setPaint(decodeGradient10(path));
    paramGraphics2D.fill(path);
    path = decodePath2();
    paramGraphics2D.setPaint(color10);
    paramGraphics2D.fill(path);
  }
  
  private RoundRectangle2D decodeRoundRect1()
  {
    roundRect.setRoundRect(decodeX(1.0F), decodeY(1.6111112F), decodeX(2.0F) - decodeX(1.0F), decodeY(2.0F) - decodeY(1.6111112F), 6.0D, 6.0D);
    return roundRect;
  }
  
  private RoundRectangle2D decodeRoundRect2()
  {
    roundRect.setRoundRect(decodeX(1.0F), decodeY(1.0F), decodeX(2.0F) - decodeX(1.0F), decodeY(1.9444444F) - decodeY(1.0F), 8.600000381469727D, 8.600000381469727D);
    return roundRect;
  }
  
  private RoundRectangle2D decodeRoundRect3()
  {
    roundRect.setRoundRect(decodeX(1.0526316F), decodeY(1.0555556F), decodeX(1.9473684F) - decodeX(1.0526316F), decodeY(1.8888888F) - decodeY(1.0555556F), 6.75D, 6.75D);
    return roundRect;
  }
  
  private Path2D decodePath1()
  {
    path.reset();
    path.moveTo(decodeX(1.3157895F), decodeY(1.4444444F));
    path.lineTo(decodeX(1.6842105F), decodeY(1.4444444F));
    path.lineTo(decodeX(1.5013158F), decodeY(1.7208333F));
    path.lineTo(decodeX(1.3157895F), decodeY(1.4444444F));
    path.closePath();
    return path;
  }
  
  private Path2D decodePath2()
  {
    path.reset();
    path.moveTo(decodeX(1.3157895F), decodeY(1.3333334F));
    path.lineTo(decodeX(1.6842105F), decodeY(1.3333334F));
    path.lineTo(decodeX(1.5F), decodeY(1.6083333F));
    path.lineTo(decodeX(1.3157895F), decodeY(1.3333334F));
    path.closePath();
    return path;
  }
  
  private Path2D decodePath3()
  {
    path.reset();
    path.moveTo(decodeX(1.3157895F), decodeY(1.3888888F));
    path.lineTo(decodeX(1.6842105F), decodeY(1.3888888F));
    path.lineTo(decodeX(1.4952153F), decodeY(1.655303F));
    path.lineTo(decodeX(1.3157895F), decodeY(1.3888888F));
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
    return decodeGradient(0.24868421F * f3 + f1, 0.0014705883F * f4 + f2, 0.24868421F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color2, decodeColor(color2, color3, 0.5F), color3 });
  }
  
  private Paint decodeGradient2(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25441176F * f3 + f1, 1.0016667F * f4 + f2, new float[] { 0.0F, 0.26988637F, 0.53977275F, 0.5951705F, 0.6505682F, 0.8252841F, 1.0F }, new Color[] { color4, decodeColor(color4, color5, 0.5F), color5, decodeColor(color5, color3, 0.5F), color3, decodeColor(color3, color6, 0.5F), color6 });
  }
  
  private Paint decodeGradient3(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.50714284F * f3 + f1, 0.095F * f4 + f2, 0.49285713F * f3 + f1, 0.91F * f4 + f2, new float[] { 0.0F, 0.24289773F, 0.48579547F, 0.74289775F, 1.0F }, new Color[] { color7, decodeColor(color7, color8, 0.5F), color8, decodeColor(color8, color9, 0.5F), color9 });
  }
  
  private Paint decodeGradient4(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.24868421F * f3 + f1, 0.0014705883F * f4 + f2, 0.24868421F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.31107953F, 0.62215906F, 0.8110795F, 1.0F }, new Color[] { color12, decodeColor(color12, color13, 0.5F), color13, decodeColor(color13, color14, 0.5F), color14 });
  }
  
  private Paint decodeGradient5(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.24868421F * f3 + f1, 0.0014705883F * f4 + f2, 0.24868421F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color16, decodeColor(color16, color17, 0.5F), color17 });
  }
  
  private Paint decodeGradient6(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25441176F * f3 + f1, 1.0016667F * f4 + f2, new float[] { 0.0F, 0.26988637F, 0.53977275F, 0.5951705F, 0.6505682F, 0.8252841F, 1.0F }, new Color[] { color18, decodeColor(color18, color19, 0.5F), color19, decodeColor(color19, color19, 0.5F), color19, decodeColor(color19, color20, 0.5F), color20 });
  }
  
  private Paint decodeGradient7(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.50714284F * f3 + f1, 0.095F * f4 + f2, 0.49285713F * f3 + f1, 0.91F * f4 + f2, new float[] { 0.0F, 0.24289773F, 0.48579547F, 0.74289775F, 1.0F }, new Color[] { color21, decodeColor(color21, color22, 0.5F), color22, decodeColor(color22, color23, 0.5F), color23 });
  }
  
  private Paint decodeGradient8(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.24868421F * f3 + f1, 0.0014705883F * f4 + f2, 0.24868421F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color24, decodeColor(color24, color25, 0.5F), color25 });
  }
  
  private Paint decodeGradient9(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25441176F * f3 + f1, 1.0016667F * f4 + f2, new float[] { 0.0F, 0.26988637F, 0.53977275F, 0.5951705F, 0.6505682F, 0.8252841F, 1.0F }, new Color[] { color26, decodeColor(color26, color27, 0.5F), color27, decodeColor(color27, color27, 0.5F), color27, decodeColor(color27, color28, 0.5F), color28 });
  }
  
  private Paint decodeGradient10(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.50714284F * f3 + f1, 0.095F * f4 + f2, 0.49285713F * f3 + f1, 0.91F * f4 + f2, new float[] { 0.0F, 0.24289773F, 0.48579547F, 0.74289775F, 1.0F }, new Color[] { color29, decodeColor(color29, color30, 0.5F), color30, decodeColor(color30, color31, 0.5F), color31 });
  }
  
  private Paint decodeGradient11(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.24868421F * f3 + f1, 0.0014705883F * f4 + f2, 0.24868421F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color32, decodeColor(color32, color33, 0.5F), color33 });
  }
  
  private Paint decodeGradient12(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25441176F * f3 + f1, 1.0016667F * f4 + f2, new float[] { 0.0F, 0.26988637F, 0.53977275F, 0.5951705F, 0.6505682F, 0.8252841F, 1.0F }, new Color[] { color34, decodeColor(color34, color35, 0.5F), color35, decodeColor(color35, color36, 0.5F), color36, decodeColor(color36, color15, 0.5F), color15 });
  }
  
  private Paint decodeGradient13(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.50714284F * f3 + f1, 0.095F * f4 + f2, 0.49285713F * f3 + f1, 0.91F * f4 + f2, new float[] { 0.0F, 0.24289773F, 0.48579547F, 0.74289775F, 1.0F }, new Color[] { color37, decodeColor(color37, color38, 0.5F), color38, decodeColor(color38, color39, 0.5F), color39 });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\nimbus\InternalFrameTitlePaneMenuButtonPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */