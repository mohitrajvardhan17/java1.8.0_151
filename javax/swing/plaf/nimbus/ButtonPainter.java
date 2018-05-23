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

final class ButtonPainter
  extends AbstractRegionPainter
{
  static final int BACKGROUND_DEFAULT = 1;
  static final int BACKGROUND_DEFAULT_FOCUSED = 2;
  static final int BACKGROUND_MOUSEOVER_DEFAULT = 3;
  static final int BACKGROUND_MOUSEOVER_DEFAULT_FOCUSED = 4;
  static final int BACKGROUND_PRESSED_DEFAULT = 5;
  static final int BACKGROUND_PRESSED_DEFAULT_FOCUSED = 6;
  static final int BACKGROUND_DISABLED = 7;
  static final int BACKGROUND_ENABLED = 8;
  static final int BACKGROUND_FOCUSED = 9;
  static final int BACKGROUND_MOUSEOVER = 10;
  static final int BACKGROUND_MOUSEOVER_FOCUSED = 11;
  static final int BACKGROUND_PRESSED = 12;
  static final int BACKGROUND_PRESSED_FOCUSED = 13;
  private int state;
  private AbstractRegionPainter.PaintContext ctx;
  private Path2D path = new Path2D.Float();
  private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
  private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  private Color color1 = decodeColor("nimbusBlueGrey", -0.027777791F, -0.06885965F, -0.36862746F, 65346);
  private Color color2 = decodeColor("nimbusBase", 5.1498413E-4F, -0.34585923F, -0.007843137F, 0);
  private Color color3 = decodeColor("nimbusBase", 5.1498413E-4F, -0.095173776F, -0.25882354F, 0);
  private Color color4 = decodeColor("nimbusBase", 0.004681647F, -0.6197143F, 0.43137252F, 0);
  private Color color5 = decodeColor("nimbusBase", 0.004681647F, -0.5766426F, 0.38039213F, 0);
  private Color color6 = decodeColor("nimbusBase", 5.1498413E-4F, -0.43866998F, 0.24705881F, 0);
  private Color color7 = decodeColor("nimbusBase", 5.1498413E-4F, -0.46404046F, 0.36470586F, 0);
  private Color color8 = decodeColor("nimbusBase", 5.1498413E-4F, -0.47761154F, 0.44313723F, 0);
  private Color color9 = decodeColor("nimbusFocus", 0.0F, 0.0F, 0.0F, 0);
  private Color color10 = decodeColor("nimbusBase", 0.0013483167F, -0.1769987F, -0.12156865F, 0);
  private Color color11 = decodeColor("nimbusBase", 0.059279382F, 0.3642857F, -0.43529415F, 0);
  private Color color12 = decodeColor("nimbusBase", 0.004681647F, -0.6198413F, 0.43921566F, 0);
  private Color color13 = decodeColor("nimbusBase", -0.0017285943F, -0.5822163F, 0.40392154F, 0);
  private Color color14 = decodeColor("nimbusBase", 5.1498413E-4F, -0.4555341F, 0.3215686F, 0);
  private Color color15 = decodeColor("nimbusBase", 5.1498413E-4F, -0.47698414F, 0.43921566F, 0);
  private Color color16 = decodeColor("nimbusBase", -0.06415892F, -0.5455182F, 0.45098037F, 0);
  private Color color17 = decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, -95);
  private Color color18 = decodeColor("nimbusBase", -0.57865167F, -0.6357143F, -0.54901963F, 0);
  private Color color19 = decodeColor("nimbusBase", -3.528595E-5F, 0.018606722F, -0.23137257F, 0);
  private Color color20 = decodeColor("nimbusBase", -4.2033195E-4F, -0.38050595F, 0.20392156F, 0);
  private Color color21 = decodeColor("nimbusBase", 0.001903832F, -0.29863563F, 0.1490196F, 0);
  private Color color22 = decodeColor("nimbusBase", 0.0F, 0.0F, 0.0F, 0);
  private Color color23 = decodeColor("nimbusBase", 0.0018727183F, -0.14126986F, 0.15686274F, 0);
  private Color color24 = decodeColor("nimbusBase", 8.9377165E-4F, -0.20852983F, 0.2588235F, 0);
  private Color color25 = decodeColor("nimbusBlueGrey", -0.027777791F, -0.06885965F, -0.36862746F, 65304);
  private Color color26 = decodeColor("nimbusBlueGrey", 0.0F, -0.06766917F, 0.07843137F, 0);
  private Color color27 = decodeColor("nimbusBlueGrey", 0.0F, -0.06484103F, 0.027450979F, 0);
  private Color color28 = decodeColor("nimbusBlueGrey", 0.0F, -0.08477524F, 0.16862744F, 0);
  private Color color29 = decodeColor("nimbusBlueGrey", -0.015872955F, -0.080091536F, 0.15686274F, 0);
  private Color color30 = decodeColor("nimbusBlueGrey", 0.0F, -0.07016757F, 0.12941176F, 0);
  private Color color31 = decodeColor("nimbusBlueGrey", 0.0F, -0.07052632F, 0.1372549F, 0);
  private Color color32 = decodeColor("nimbusBlueGrey", 0.0F, -0.070878744F, 0.14509803F, 0);
  private Color color33 = decodeColor("nimbusBlueGrey", -0.055555522F, -0.05356429F, -0.12549019F, 0);
  private Color color34 = decodeColor("nimbusBlueGrey", 0.0F, -0.0147816315F, -0.3764706F, 0);
  private Color color35 = decodeColor("nimbusBlueGrey", 0.055555582F, -0.10655806F, 0.24313724F, 0);
  private Color color36 = decodeColor("nimbusBlueGrey", 0.0F, -0.09823123F, 0.2117647F, 0);
  private Color color37 = decodeColor("nimbusBlueGrey", 0.0F, -0.0749532F, 0.24705881F, 0);
  private Color color38 = decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, 0);
  private Color color39 = decodeColor("nimbusBlueGrey", 0.0F, -0.020974077F, -0.21960783F, 0);
  private Color color40 = decodeColor("nimbusBlueGrey", 0.0F, 0.11169591F, -0.53333336F, 0);
  private Color color41 = decodeColor("nimbusBlueGrey", 0.055555582F, -0.10658931F, 0.25098038F, 0);
  private Color color42 = decodeColor("nimbusBlueGrey", 0.0F, -0.098526314F, 0.2352941F, 0);
  private Color color43 = decodeColor("nimbusBlueGrey", 0.0F, -0.07333623F, 0.20392156F, 0);
  private Color color44 = new Color(245, 250, 255, 160);
  private Color color45 = decodeColor("nimbusBlueGrey", 0.055555582F, 0.8894737F, -0.7176471F, 0);
  private Color color46 = decodeColor("nimbusBlueGrey", 0.0F, 5.847961E-4F, -0.32156864F, 0);
  private Color color47 = decodeColor("nimbusBlueGrey", -0.00505054F, -0.05960039F, 0.10196078F, 0);
  private Color color48 = decodeColor("nimbusBlueGrey", -0.008547008F, -0.04772438F, 0.06666666F, 0);
  private Color color49 = decodeColor("nimbusBlueGrey", -0.0027777553F, -0.0018306673F, -0.02352941F, 0);
  private Color color50 = decodeColor("nimbusBlueGrey", -0.0027777553F, -0.0212406F, 0.13333333F, 0);
  private Color color51 = decodeColor("nimbusBlueGrey", 0.0055555105F, -0.030845039F, 0.23921567F, 0);
  private Object[] componentColors;
  
  public ButtonPainter(AbstractRegionPainter.PaintContext paramPaintContext, int paramInt)
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
      paintBackgroundDefault(paramGraphics2D);
      break;
    case 2: 
      paintBackgroundDefaultAndFocused(paramGraphics2D);
      break;
    case 3: 
      paintBackgroundMouseOverAndDefault(paramGraphics2D);
      break;
    case 4: 
      paintBackgroundMouseOverAndDefaultAndFocused(paramGraphics2D);
      break;
    case 5: 
      paintBackgroundPressedAndDefault(paramGraphics2D);
      break;
    case 6: 
      paintBackgroundPressedAndDefaultAndFocused(paramGraphics2D);
      break;
    case 7: 
      paintBackgroundDisabled(paramGraphics2D);
      break;
    case 8: 
      paintBackgroundEnabled(paramGraphics2D);
      break;
    case 9: 
      paintBackgroundFocused(paramGraphics2D);
      break;
    case 10: 
      paintBackgroundMouseOver(paramGraphics2D);
      break;
    case 11: 
      paintBackgroundMouseOverAndFocused(paramGraphics2D);
      break;
    case 12: 
      paintBackgroundPressed(paramGraphics2D);
      break;
    case 13: 
      paintBackgroundPressedAndFocused(paramGraphics2D);
    }
  }
  
  protected Object[] getExtendedCacheKeys(JComponent paramJComponent)
  {
    Object[] arrayOfObject = null;
    switch (state)
    {
    case 1: 
      arrayOfObject = new Object[] { getComponentColor(paramJComponent, "background", color4, -0.6197143F, 0.43137252F, 0), getComponentColor(paramJComponent, "background", color5, -0.5766426F, 0.38039213F, 0), getComponentColor(paramJComponent, "background", color6, -0.43866998F, 0.24705881F, 0), getComponentColor(paramJComponent, "background", color7, -0.46404046F, 0.36470586F, 0), getComponentColor(paramJComponent, "background", color8, -0.47761154F, 0.44313723F, 0) };
      break;
    case 2: 
      arrayOfObject = new Object[] { getComponentColor(paramJComponent, "background", color4, -0.6197143F, 0.43137252F, 0), getComponentColor(paramJComponent, "background", color5, -0.5766426F, 0.38039213F, 0), getComponentColor(paramJComponent, "background", color6, -0.43866998F, 0.24705881F, 0), getComponentColor(paramJComponent, "background", color7, -0.46404046F, 0.36470586F, 0), getComponentColor(paramJComponent, "background", color8, -0.47761154F, 0.44313723F, 0) };
      break;
    case 3: 
      arrayOfObject = new Object[] { getComponentColor(paramJComponent, "background", color12, -0.6198413F, 0.43921566F, 0), getComponentColor(paramJComponent, "background", color13, -0.5822163F, 0.40392154F, 0), getComponentColor(paramJComponent, "background", color14, -0.4555341F, 0.3215686F, 0), getComponentColor(paramJComponent, "background", color15, -0.47698414F, 0.43921566F, 0), getComponentColor(paramJComponent, "background", color16, -0.5455182F, 0.45098037F, 0) };
      break;
    case 4: 
      arrayOfObject = new Object[] { getComponentColor(paramJComponent, "background", color12, -0.6198413F, 0.43921566F, 0), getComponentColor(paramJComponent, "background", color13, -0.5822163F, 0.40392154F, 0), getComponentColor(paramJComponent, "background", color14, -0.4555341F, 0.3215686F, 0), getComponentColor(paramJComponent, "background", color15, -0.47698414F, 0.43921566F, 0), getComponentColor(paramJComponent, "background", color16, -0.5455182F, 0.45098037F, 0) };
      break;
    case 5: 
      arrayOfObject = new Object[] { getComponentColor(paramJComponent, "background", color20, -0.38050595F, 0.20392156F, 0), getComponentColor(paramJComponent, "background", color21, -0.29863563F, 0.1490196F, 0), getComponentColor(paramJComponent, "background", color22, 0.0F, 0.0F, 0), getComponentColor(paramJComponent, "background", color23, -0.14126986F, 0.15686274F, 0), getComponentColor(paramJComponent, "background", color24, -0.20852983F, 0.2588235F, 0) };
      break;
    case 6: 
      arrayOfObject = new Object[] { getComponentColor(paramJComponent, "background", color20, -0.38050595F, 0.20392156F, 0), getComponentColor(paramJComponent, "background", color21, -0.29863563F, 0.1490196F, 0), getComponentColor(paramJComponent, "background", color22, 0.0F, 0.0F, 0), getComponentColor(paramJComponent, "background", color23, -0.14126986F, 0.15686274F, 0), getComponentColor(paramJComponent, "background", color24, -0.20852983F, 0.2588235F, 0) };
      break;
    case 8: 
      arrayOfObject = new Object[] { getComponentColor(paramJComponent, "background", color35, -0.10655806F, 0.24313724F, 0), getComponentColor(paramJComponent, "background", color36, -0.09823123F, 0.2117647F, 0), getComponentColor(paramJComponent, "background", color30, -0.07016757F, 0.12941176F, 0), getComponentColor(paramJComponent, "background", color37, -0.0749532F, 0.24705881F, 0), getComponentColor(paramJComponent, "background", color38, -0.110526316F, 0.25490195F, 0) };
      break;
    case 9: 
      arrayOfObject = new Object[] { getComponentColor(paramJComponent, "background", color35, -0.10655806F, 0.24313724F, 0), getComponentColor(paramJComponent, "background", color36, -0.09823123F, 0.2117647F, 0), getComponentColor(paramJComponent, "background", color30, -0.07016757F, 0.12941176F, 0), getComponentColor(paramJComponent, "background", color37, -0.0749532F, 0.24705881F, 0), getComponentColor(paramJComponent, "background", color38, -0.110526316F, 0.25490195F, 0) };
      break;
    case 10: 
      arrayOfObject = new Object[] { getComponentColor(paramJComponent, "background", color41, -0.10658931F, 0.25098038F, 0), getComponentColor(paramJComponent, "background", color42, -0.098526314F, 0.2352941F, 0), getComponentColor(paramJComponent, "background", color43, -0.07333623F, 0.20392156F, 0), getComponentColor(paramJComponent, "background", color38, -0.110526316F, 0.25490195F, 0) };
      break;
    case 11: 
      arrayOfObject = new Object[] { getComponentColor(paramJComponent, "background", color41, -0.10658931F, 0.25098038F, 0), getComponentColor(paramJComponent, "background", color42, -0.098526314F, 0.2352941F, 0), getComponentColor(paramJComponent, "background", color43, -0.07333623F, 0.20392156F, 0), getComponentColor(paramJComponent, "background", color38, -0.110526316F, 0.25490195F, 0) };
      break;
    case 12: 
      arrayOfObject = new Object[] { getComponentColor(paramJComponent, "background", color47, -0.05960039F, 0.10196078F, 0), getComponentColor(paramJComponent, "background", color48, -0.04772438F, 0.06666666F, 0), getComponentColor(paramJComponent, "background", color49, -0.0018306673F, -0.02352941F, 0), getComponentColor(paramJComponent, "background", color50, -0.0212406F, 0.13333333F, 0), getComponentColor(paramJComponent, "background", color51, -0.030845039F, 0.23921567F, 0) };
      break;
    case 13: 
      arrayOfObject = new Object[] { getComponentColor(paramJComponent, "background", color47, -0.05960039F, 0.10196078F, 0), getComponentColor(paramJComponent, "background", color48, -0.04772438F, 0.06666666F, 0), getComponentColor(paramJComponent, "background", color49, -0.0018306673F, -0.02352941F, 0), getComponentColor(paramJComponent, "background", color50, -0.0212406F, 0.13333333F, 0), getComponentColor(paramJComponent, "background", color51, -0.030845039F, 0.23921567F, 0) };
    }
    return arrayOfObject;
  }
  
  protected final AbstractRegionPainter.PaintContext getPaintContext()
  {
    return ctx;
  }
  
  private void paintBackgroundDefault(Graphics2D paramGraphics2D)
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
  }
  
  private void paintBackgroundDefaultAndFocused(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect4();
    paramGraphics2D.setPaint(color9);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient1(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient2(roundRect));
    paramGraphics2D.fill(roundRect);
  }
  
  private void paintBackgroundMouseOverAndDefault(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect5();
    paramGraphics2D.setPaint(color1);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient3(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient2(roundRect));
    paramGraphics2D.fill(roundRect);
  }
  
  private void paintBackgroundMouseOverAndDefaultAndFocused(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect4();
    paramGraphics2D.setPaint(color9);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient3(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient2(roundRect));
    paramGraphics2D.fill(roundRect);
  }
  
  private void paintBackgroundPressedAndDefault(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(color17);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient4(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient2(roundRect));
    paramGraphics2D.fill(roundRect);
  }
  
  private void paintBackgroundPressedAndDefaultAndFocused(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect4();
    paramGraphics2D.setPaint(color9);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient4(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient2(roundRect));
    paramGraphics2D.fill(roundRect);
  }
  
  private void paintBackgroundDisabled(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(color25);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient5(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient6(roundRect));
    paramGraphics2D.fill(roundRect);
  }
  
  private void paintBackgroundEnabled(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(color1);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient7(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient2(roundRect));
    paramGraphics2D.fill(roundRect);
  }
  
  private void paintBackgroundFocused(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect4();
    paramGraphics2D.setPaint(color9);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient7(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient8(roundRect));
    paramGraphics2D.fill(roundRect);
  }
  
  private void paintBackgroundMouseOver(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(color1);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient9(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient10(roundRect));
    paramGraphics2D.fill(roundRect);
  }
  
  private void paintBackgroundMouseOverAndFocused(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect4();
    paramGraphics2D.setPaint(color9);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient9(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient10(roundRect));
    paramGraphics2D.fill(roundRect);
  }
  
  private void paintBackgroundPressed(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(color44);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient11(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient2(roundRect));
    paramGraphics2D.fill(roundRect);
  }
  
  private void paintBackgroundPressedAndFocused(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect4();
    paramGraphics2D.setPaint(color9);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient11(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient2(roundRect));
    paramGraphics2D.fill(roundRect);
  }
  
  private RoundRectangle2D decodeRoundRect1()
  {
    roundRect.setRoundRect(decodeX(0.2857143F), decodeY(0.42857143F), decodeX(2.7142859F) - decodeX(0.2857143F), decodeY(2.857143F) - decodeY(0.42857143F), 12.0D, 12.0D);
    return roundRect;
  }
  
  private RoundRectangle2D decodeRoundRect2()
  {
    roundRect.setRoundRect(decodeX(0.2857143F), decodeY(0.2857143F), decodeX(2.7142859F) - decodeX(0.2857143F), decodeY(2.7142859F) - decodeY(0.2857143F), 9.0D, 9.0D);
    return roundRect;
  }
  
  private RoundRectangle2D decodeRoundRect3()
  {
    roundRect.setRoundRect(decodeX(0.42857143F), decodeY(0.42857143F), decodeX(2.5714285F) - decodeX(0.42857143F), decodeY(2.5714285F) - decodeY(0.42857143F), 7.0D, 7.0D);
    return roundRect;
  }
  
  private RoundRectangle2D decodeRoundRect4()
  {
    roundRect.setRoundRect(decodeX(0.08571429F), decodeY(0.08571429F), decodeX(2.914286F) - decodeX(0.08571429F), decodeY(2.914286F) - decodeY(0.08571429F), 11.0D, 11.0D);
    return roundRect;
  }
  
  private RoundRectangle2D decodeRoundRect5()
  {
    roundRect.setRoundRect(decodeX(0.2857143F), decodeY(0.42857143F), decodeX(2.7142859F) - decodeX(0.2857143F), decodeY(2.857143F) - decodeY(0.42857143F), 9.0D, 9.0D);
    return roundRect;
  }
  
  private Paint decodeGradient1(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.05F, 0.5F, 0.95F }, new Color[] { color2, decodeColor(color2, color3, 0.5F), color3 });
  }
  
  private Paint decodeGradient2(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.024F, 0.06F, 0.276F, 0.6F, 0.65F, 0.7F, 0.856F, 0.96F, 0.98399997F, 1.0F }, new Color[] { (Color)componentColors[0], decodeColor((Color)componentColors[0], (Color)componentColors[1], 0.5F), (Color)componentColors[1], decodeColor((Color)componentColors[1], (Color)componentColors[2], 0.5F), (Color)componentColors[2], decodeColor((Color)componentColors[2], (Color)componentColors[2], 0.5F), (Color)componentColors[2], decodeColor((Color)componentColors[2], (Color)componentColors[3], 0.5F), (Color)componentColors[3], decodeColor((Color)componentColors[3], (Color)componentColors[4], 0.5F), (Color)componentColors[4] });
  }
  
  private Paint decodeGradient3(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.05F, 0.5F, 0.95F }, new Color[] { color10, decodeColor(color10, color11, 0.5F), color11 });
  }
  
  private Paint decodeGradient4(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.05F, 0.5F, 0.95F }, new Color[] { color18, decodeColor(color18, color19, 0.5F), color19 });
  }
  
  private Paint decodeGradient5(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.09F, 0.52F, 0.95F }, new Color[] { color26, decodeColor(color26, color27, 0.5F), color27 });
  }
  
  private Paint decodeGradient6(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.03F, 0.06F, 0.33F, 0.6F, 0.65F, 0.7F, 0.825F, 0.95F, 0.975F, 1.0F }, new Color[] { color28, decodeColor(color28, color29, 0.5F), color29, decodeColor(color29, color30, 0.5F), color30, decodeColor(color30, color30, 0.5F), color30, decodeColor(color30, color31, 0.5F), color31, decodeColor(color31, color32, 0.5F), color32 });
  }
  
  private Paint decodeGradient7(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.09F, 0.52F, 0.95F }, new Color[] { color33, decodeColor(color33, color34, 0.5F), color34 });
  }
  
  private Paint decodeGradient8(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.03F, 0.06F, 0.33F, 0.6F, 0.65F, 0.7F, 0.825F, 0.95F, 0.975F, 1.0F }, new Color[] { (Color)componentColors[0], decodeColor((Color)componentColors[0], (Color)componentColors[1], 0.5F), (Color)componentColors[1], decodeColor((Color)componentColors[1], (Color)componentColors[2], 0.5F), (Color)componentColors[2], decodeColor((Color)componentColors[2], (Color)componentColors[2], 0.5F), (Color)componentColors[2], decodeColor((Color)componentColors[2], (Color)componentColors[3], 0.5F), (Color)componentColors[3], decodeColor((Color)componentColors[3], (Color)componentColors[4], 0.5F), (Color)componentColors[4] });
  }
  
  private Paint decodeGradient9(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.09F, 0.52F, 0.95F }, new Color[] { color39, decodeColor(color39, color40, 0.5F), color40 });
  }
  
  private Paint decodeGradient10(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.024F, 0.06F, 0.276F, 0.6F, 0.65F, 0.7F, 0.856F, 0.96F, 0.98F, 1.0F }, new Color[] { (Color)componentColors[0], decodeColor((Color)componentColors[0], (Color)componentColors[1], 0.5F), (Color)componentColors[1], decodeColor((Color)componentColors[1], (Color)componentColors[2], 0.5F), (Color)componentColors[2], decodeColor((Color)componentColors[2], (Color)componentColors[2], 0.5F), (Color)componentColors[2], decodeColor((Color)componentColors[2], (Color)componentColors[3], 0.5F), (Color)componentColors[3], decodeColor((Color)componentColors[3], (Color)componentColors[3], 0.5F), (Color)componentColors[3] });
  }
  
  private Paint decodeGradient11(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.05F, 0.5F, 0.95F }, new Color[] { color45, decodeColor(color45, color46, 0.5F), color46 });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\nimbus\ButtonPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */