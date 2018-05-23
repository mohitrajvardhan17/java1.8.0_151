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

final class ToggleButtonPainter
  extends AbstractRegionPainter
{
  static final int BACKGROUND_DISABLED = 1;
  static final int BACKGROUND_ENABLED = 2;
  static final int BACKGROUND_FOCUSED = 3;
  static final int BACKGROUND_MOUSEOVER = 4;
  static final int BACKGROUND_MOUSEOVER_FOCUSED = 5;
  static final int BACKGROUND_PRESSED = 6;
  static final int BACKGROUND_PRESSED_FOCUSED = 7;
  static final int BACKGROUND_SELECTED = 8;
  static final int BACKGROUND_SELECTED_FOCUSED = 9;
  static final int BACKGROUND_PRESSED_SELECTED = 10;
  static final int BACKGROUND_PRESSED_SELECTED_FOCUSED = 11;
  static final int BACKGROUND_MOUSEOVER_SELECTED = 12;
  static final int BACKGROUND_MOUSEOVER_SELECTED_FOCUSED = 13;
  static final int BACKGROUND_DISABLED_SELECTED = 14;
  private int state;
  private AbstractRegionPainter.PaintContext ctx;
  private Path2D path = new Path2D.Float();
  private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
  private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  private Color color1 = decodeColor("nimbusBlueGrey", -0.027777791F, -0.06885965F, -0.36862746F, 65304);
  private Color color2 = decodeColor("nimbusBlueGrey", 0.0F, -0.06766917F, 0.07843137F, 0);
  private Color color3 = decodeColor("nimbusBlueGrey", 0.0F, -0.06484103F, 0.027450979F, 0);
  private Color color4 = decodeColor("nimbusBlueGrey", 0.0F, -0.08477524F, 0.16862744F, 0);
  private Color color5 = decodeColor("nimbusBlueGrey", -0.015872955F, -0.080091536F, 0.15686274F, 0);
  private Color color6 = decodeColor("nimbusBlueGrey", 0.0F, -0.07016757F, 0.12941176F, 0);
  private Color color7 = decodeColor("nimbusBlueGrey", 0.0F, -0.07052632F, 0.1372549F, 0);
  private Color color8 = decodeColor("nimbusBlueGrey", 0.0F, -0.070878744F, 0.14509803F, 0);
  private Color color9 = decodeColor("nimbusBlueGrey", -0.027777791F, -0.06885965F, -0.36862746F, 65346);
  private Color color10 = decodeColor("nimbusBlueGrey", -0.055555522F, -0.05356429F, -0.12549019F, 0);
  private Color color11 = decodeColor("nimbusBlueGrey", 0.0F, -0.0147816315F, -0.3764706F, 0);
  private Color color12 = decodeColor("nimbusBlueGrey", 0.055555582F, -0.10655806F, 0.24313724F, 0);
  private Color color13 = decodeColor("nimbusBlueGrey", 0.0F, -0.09823123F, 0.2117647F, 0);
  private Color color14 = decodeColor("nimbusBlueGrey", 0.0F, -0.0749532F, 0.24705881F, 0);
  private Color color15 = decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, 0);
  private Color color16 = decodeColor("nimbusFocus", 0.0F, 0.0F, 0.0F, 0);
  private Color color17 = decodeColor("nimbusBlueGrey", 0.0F, -0.020974077F, -0.21960783F, 0);
  private Color color18 = decodeColor("nimbusBlueGrey", 0.0F, 0.11169591F, -0.53333336F, 0);
  private Color color19 = decodeColor("nimbusBlueGrey", 0.055555582F, -0.10658931F, 0.25098038F, 0);
  private Color color20 = decodeColor("nimbusBlueGrey", 0.0F, -0.098526314F, 0.2352941F, 0);
  private Color color21 = decodeColor("nimbusBlueGrey", 0.0F, -0.07333623F, 0.20392156F, 0);
  private Color color22 = new Color(245, 250, 255, 160);
  private Color color23 = decodeColor("nimbusBlueGrey", 0.055555582F, 0.8894737F, -0.7176471F, 0);
  private Color color24 = decodeColor("nimbusBlueGrey", 0.0F, 5.847961E-4F, -0.32156864F, 0);
  private Color color25 = decodeColor("nimbusBlueGrey", -0.00505054F, -0.05960039F, 0.10196078F, 0);
  private Color color26 = decodeColor("nimbusBlueGrey", -0.008547008F, -0.04772438F, 0.06666666F, 0);
  private Color color27 = decodeColor("nimbusBlueGrey", -0.0027777553F, -0.0018306673F, -0.02352941F, 0);
  private Color color28 = decodeColor("nimbusBlueGrey", -0.0027777553F, -0.0212406F, 0.13333333F, 0);
  private Color color29 = decodeColor("nimbusBlueGrey", 0.0055555105F, -0.030845039F, 0.23921567F, 0);
  private Color color30 = decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, -86);
  private Color color31 = decodeColor("nimbusBlueGrey", 0.0F, -0.06472479F, -0.23137254F, 0);
  private Color color32 = decodeColor("nimbusBlueGrey", 0.007936537F, -0.06959064F, -0.0745098F, 0);
  private Color color33 = decodeColor("nimbusBlueGrey", 0.0138888955F, -0.06401469F, -0.07058823F, 0);
  private Color color34 = decodeColor("nimbusBlueGrey", 0.0F, -0.06530018F, 0.035294116F, 0);
  private Color color35 = decodeColor("nimbusBlueGrey", 0.0F, -0.06507177F, 0.031372547F, 0);
  private Color color36 = decodeColor("nimbusBlueGrey", -0.027777791F, -0.05338346F, -0.47058824F, 0);
  private Color color37 = decodeColor("nimbusBlueGrey", 0.0F, -0.049301825F, -0.36078432F, 0);
  private Color color38 = decodeColor("nimbusBlueGrey", -0.018518567F, -0.03909774F, -0.2509804F, 0);
  private Color color39 = decodeColor("nimbusBlueGrey", -0.00505054F, -0.040013492F, -0.13333333F, 0);
  private Color color40 = decodeColor("nimbusBlueGrey", 0.01010108F, -0.039558575F, -0.1372549F, 0);
  private Color color41 = decodeColor("nimbusBlueGrey", -0.01111114F, -0.060526315F, -0.3529412F, 0);
  private Color color42 = decodeColor("nimbusBlueGrey", 0.0F, -0.064372465F, -0.2352941F, 0);
  private Color color43 = decodeColor("nimbusBlueGrey", -0.006944418F, -0.0595709F, -0.12941176F, 0);
  private Color color44 = decodeColor("nimbusBlueGrey", 0.0F, -0.061075766F, -0.031372547F, 0);
  private Color color45 = decodeColor("nimbusBlueGrey", 0.0F, -0.06080256F, -0.035294116F, 0);
  private Color color46 = decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, 65316);
  private Color color47 = decodeColor("nimbusBlueGrey", 0.0F, -0.066408664F, 0.054901958F, 0);
  private Color color48 = decodeColor("nimbusBlueGrey", 0.0F, -0.06807348F, 0.086274505F, 0);
  private Color color49 = decodeColor("nimbusBlueGrey", 0.0F, -0.06924191F, 0.109803915F, 0);
  private Object[] componentColors;
  
  public ToggleButtonPainter(AbstractRegionPainter.PaintContext paramPaintContext, int paramInt)
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
      paintBackgroundMouseOver(paramGraphics2D);
      break;
    case 5: 
      paintBackgroundMouseOverAndFocused(paramGraphics2D);
      break;
    case 6: 
      paintBackgroundPressed(paramGraphics2D);
      break;
    case 7: 
      paintBackgroundPressedAndFocused(paramGraphics2D);
      break;
    case 8: 
      paintBackgroundSelected(paramGraphics2D);
      break;
    case 9: 
      paintBackgroundSelectedAndFocused(paramGraphics2D);
      break;
    case 10: 
      paintBackgroundPressedAndSelected(paramGraphics2D);
      break;
    case 11: 
      paintBackgroundPressedAndSelectedAndFocused(paramGraphics2D);
      break;
    case 12: 
      paintBackgroundMouseOverAndSelected(paramGraphics2D);
      break;
    case 13: 
      paintBackgroundMouseOverAndSelectedAndFocused(paramGraphics2D);
      break;
    case 14: 
      paintBackgroundDisabledAndSelected(paramGraphics2D);
    }
  }
  
  protected Object[] getExtendedCacheKeys(JComponent paramJComponent)
  {
    Object[] arrayOfObject = null;
    switch (state)
    {
    case 2: 
      arrayOfObject = new Object[] { getComponentColor(paramJComponent, "background", color12, -0.10655806F, 0.24313724F, 0), getComponentColor(paramJComponent, "background", color13, -0.09823123F, 0.2117647F, 0), getComponentColor(paramJComponent, "background", color6, -0.07016757F, 0.12941176F, 0), getComponentColor(paramJComponent, "background", color14, -0.0749532F, 0.24705881F, 0), getComponentColor(paramJComponent, "background", color15, -0.110526316F, 0.25490195F, 0) };
      break;
    case 3: 
      arrayOfObject = new Object[] { getComponentColor(paramJComponent, "background", color12, -0.10655806F, 0.24313724F, 0), getComponentColor(paramJComponent, "background", color13, -0.09823123F, 0.2117647F, 0), getComponentColor(paramJComponent, "background", color6, -0.07016757F, 0.12941176F, 0), getComponentColor(paramJComponent, "background", color14, -0.0749532F, 0.24705881F, 0), getComponentColor(paramJComponent, "background", color15, -0.110526316F, 0.25490195F, 0) };
      break;
    case 4: 
      arrayOfObject = new Object[] { getComponentColor(paramJComponent, "background", color19, -0.10658931F, 0.25098038F, 0), getComponentColor(paramJComponent, "background", color20, -0.098526314F, 0.2352941F, 0), getComponentColor(paramJComponent, "background", color21, -0.07333623F, 0.20392156F, 0), getComponentColor(paramJComponent, "background", color15, -0.110526316F, 0.25490195F, 0) };
      break;
    case 5: 
      arrayOfObject = new Object[] { getComponentColor(paramJComponent, "background", color19, -0.10658931F, 0.25098038F, 0), getComponentColor(paramJComponent, "background", color20, -0.098526314F, 0.2352941F, 0), getComponentColor(paramJComponent, "background", color21, -0.07333623F, 0.20392156F, 0), getComponentColor(paramJComponent, "background", color15, -0.110526316F, 0.25490195F, 0) };
      break;
    case 6: 
      arrayOfObject = new Object[] { getComponentColor(paramJComponent, "background", color25, -0.05960039F, 0.10196078F, 0), getComponentColor(paramJComponent, "background", color26, -0.04772438F, 0.06666666F, 0), getComponentColor(paramJComponent, "background", color27, -0.0018306673F, -0.02352941F, 0), getComponentColor(paramJComponent, "background", color28, -0.0212406F, 0.13333333F, 0), getComponentColor(paramJComponent, "background", color29, -0.030845039F, 0.23921567F, 0) };
      break;
    case 7: 
      arrayOfObject = new Object[] { getComponentColor(paramJComponent, "background", color25, -0.05960039F, 0.10196078F, 0), getComponentColor(paramJComponent, "background", color26, -0.04772438F, 0.06666666F, 0), getComponentColor(paramJComponent, "background", color27, -0.0018306673F, -0.02352941F, 0), getComponentColor(paramJComponent, "background", color28, -0.0212406F, 0.13333333F, 0), getComponentColor(paramJComponent, "background", color29, -0.030845039F, 0.23921567F, 0) };
      break;
    case 8: 
      arrayOfObject = new Object[] { getComponentColor(paramJComponent, "background", color33, -0.06401469F, -0.07058823F, 0), getComponentColor(paramJComponent, "background", color34, -0.06530018F, 0.035294116F, 0), getComponentColor(paramJComponent, "background", color35, -0.06507177F, 0.031372547F, 0) };
      break;
    case 9: 
      arrayOfObject = new Object[] { getComponentColor(paramJComponent, "background", color33, -0.06401469F, -0.07058823F, 0), getComponentColor(paramJComponent, "background", color34, -0.06530018F, 0.035294116F, 0), getComponentColor(paramJComponent, "background", color35, -0.06507177F, 0.031372547F, 0) };
      break;
    case 10: 
      arrayOfObject = new Object[] { getComponentColor(paramJComponent, "background", color38, -0.03909774F, -0.2509804F, 0), getComponentColor(paramJComponent, "background", color39, -0.040013492F, -0.13333333F, 0), getComponentColor(paramJComponent, "background", color40, -0.039558575F, -0.1372549F, 0) };
      break;
    case 11: 
      arrayOfObject = new Object[] { getComponentColor(paramJComponent, "background", color38, -0.03909774F, -0.2509804F, 0), getComponentColor(paramJComponent, "background", color39, -0.040013492F, -0.13333333F, 0), getComponentColor(paramJComponent, "background", color40, -0.039558575F, -0.1372549F, 0) };
      break;
    case 12: 
      arrayOfObject = new Object[] { getComponentColor(paramJComponent, "background", color43, -0.0595709F, -0.12941176F, 0), getComponentColor(paramJComponent, "background", color44, -0.061075766F, -0.031372547F, 0), getComponentColor(paramJComponent, "background", color45, -0.06080256F, -0.035294116F, 0) };
      break;
    case 13: 
      arrayOfObject = new Object[] { getComponentColor(paramJComponent, "background", color43, -0.0595709F, -0.12941176F, 0), getComponentColor(paramJComponent, "background", color44, -0.061075766F, -0.031372547F, 0), getComponentColor(paramJComponent, "background", color45, -0.06080256F, -0.035294116F, 0) };
    }
    return arrayOfObject;
  }
  
  protected final AbstractRegionPainter.PaintContext getPaintContext()
  {
    return ctx;
  }
  
  private void paintBackgroundDisabled(Graphics2D paramGraphics2D)
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
  
  private void paintBackgroundEnabled(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(color9);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient3(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient4(roundRect));
    paramGraphics2D.fill(roundRect);
  }
  
  private void paintBackgroundFocused(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect4();
    paramGraphics2D.setPaint(color16);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient3(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient5(roundRect));
    paramGraphics2D.fill(roundRect);
  }
  
  private void paintBackgroundMouseOver(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(color9);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient6(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient7(roundRect));
    paramGraphics2D.fill(roundRect);
  }
  
  private void paintBackgroundMouseOverAndFocused(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect4();
    paramGraphics2D.setPaint(color16);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient6(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient7(roundRect));
    paramGraphics2D.fill(roundRect);
  }
  
  private void paintBackgroundPressed(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(color22);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient8(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient4(roundRect));
    paramGraphics2D.fill(roundRect);
  }
  
  private void paintBackgroundPressedAndFocused(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect4();
    paramGraphics2D.setPaint(color16);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient8(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient4(roundRect));
    paramGraphics2D.fill(roundRect);
  }
  
  private void paintBackgroundSelected(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect5();
    paramGraphics2D.setPaint(color30);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient9(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient10(roundRect));
    paramGraphics2D.fill(roundRect);
  }
  
  private void paintBackgroundSelectedAndFocused(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect6();
    paramGraphics2D.setPaint(color16);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient9(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient10(roundRect));
    paramGraphics2D.fill(roundRect);
  }
  
  private void paintBackgroundPressedAndSelected(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect5();
    paramGraphics2D.setPaint(color30);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient11(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient10(roundRect));
    paramGraphics2D.fill(roundRect);
  }
  
  private void paintBackgroundPressedAndSelectedAndFocused(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect6();
    paramGraphics2D.setPaint(color16);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient11(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient10(roundRect));
    paramGraphics2D.fill(roundRect);
  }
  
  private void paintBackgroundMouseOverAndSelected(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect5();
    paramGraphics2D.setPaint(color30);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient12(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient10(roundRect));
    paramGraphics2D.fill(roundRect);
  }
  
  private void paintBackgroundMouseOverAndSelectedAndFocused(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect6();
    paramGraphics2D.setPaint(color16);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient12(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient10(roundRect));
    paramGraphics2D.fill(roundRect);
  }
  
  private void paintBackgroundDisabledAndSelected(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect5();
    paramGraphics2D.setPaint(color46);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient13(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient14(roundRect));
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
  
  private RoundRectangle2D decodeRoundRect6()
  {
    roundRect.setRoundRect(decodeX(0.08571429F), decodeY(0.08571429F), decodeX(2.914286F) - decodeX(0.08571429F), decodeY(2.9142857F) - decodeY(0.08571429F), 11.0D, 11.0D);
    return roundRect;
  }
  
  private Paint decodeGradient1(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.09F, 0.52F, 0.95F }, new Color[] { color2, decodeColor(color2, color3, 0.5F), color3 });
  }
  
  private Paint decodeGradient2(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.03F, 0.06F, 0.33F, 0.6F, 0.65F, 0.7F, 0.825F, 0.95F, 0.975F, 1.0F }, new Color[] { color4, decodeColor(color4, color5, 0.5F), color5, decodeColor(color5, color6, 0.5F), color6, decodeColor(color6, color6, 0.5F), color6, decodeColor(color6, color7, 0.5F), color7, decodeColor(color7, color8, 0.5F), color8 });
  }
  
  private Paint decodeGradient3(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.09F, 0.52F, 0.95F }, new Color[] { color10, decodeColor(color10, color11, 0.5F), color11 });
  }
  
  private Paint decodeGradient4(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.024F, 0.06F, 0.276F, 0.6F, 0.65F, 0.7F, 0.856F, 0.96F, 0.98399997F, 1.0F }, new Color[] { (Color)componentColors[0], decodeColor((Color)componentColors[0], (Color)componentColors[1], 0.5F), (Color)componentColors[1], decodeColor((Color)componentColors[1], (Color)componentColors[2], 0.5F), (Color)componentColors[2], decodeColor((Color)componentColors[2], (Color)componentColors[2], 0.5F), (Color)componentColors[2], decodeColor((Color)componentColors[2], (Color)componentColors[3], 0.5F), (Color)componentColors[3], decodeColor((Color)componentColors[3], (Color)componentColors[4], 0.5F), (Color)componentColors[4] });
  }
  
  private Paint decodeGradient5(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.03F, 0.06F, 0.33F, 0.6F, 0.65F, 0.7F, 0.825F, 0.95F, 0.975F, 1.0F }, new Color[] { (Color)componentColors[0], decodeColor((Color)componentColors[0], (Color)componentColors[1], 0.5F), (Color)componentColors[1], decodeColor((Color)componentColors[1], (Color)componentColors[2], 0.5F), (Color)componentColors[2], decodeColor((Color)componentColors[2], (Color)componentColors[2], 0.5F), (Color)componentColors[2], decodeColor((Color)componentColors[2], (Color)componentColors[3], 0.5F), (Color)componentColors[3], decodeColor((Color)componentColors[3], (Color)componentColors[4], 0.5F), (Color)componentColors[4] });
  }
  
  private Paint decodeGradient6(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.09F, 0.52F, 0.95F }, new Color[] { color17, decodeColor(color17, color18, 0.5F), color18 });
  }
  
  private Paint decodeGradient7(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.024F, 0.06F, 0.276F, 0.6F, 0.65F, 0.7F, 0.856F, 0.96F, 0.98F, 1.0F }, new Color[] { (Color)componentColors[0], decodeColor((Color)componentColors[0], (Color)componentColors[1], 0.5F), (Color)componentColors[1], decodeColor((Color)componentColors[1], (Color)componentColors[2], 0.5F), (Color)componentColors[2], decodeColor((Color)componentColors[2], (Color)componentColors[2], 0.5F), (Color)componentColors[2], decodeColor((Color)componentColors[2], (Color)componentColors[3], 0.5F), (Color)componentColors[3], decodeColor((Color)componentColors[3], (Color)componentColors[3], 0.5F), (Color)componentColors[3] });
  }
  
  private Paint decodeGradient8(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.05F, 0.5F, 0.95F }, new Color[] { color23, decodeColor(color23, color24, 0.5F), color24 });
  }
  
  private Paint decodeGradient9(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color31, decodeColor(color31, color32, 0.5F), color32 });
  }
  
  private Paint decodeGradient10(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.06684492F, 0.13368984F, 0.56684494F, 1.0F }, new Color[] { (Color)componentColors[0], decodeColor((Color)componentColors[0], (Color)componentColors[1], 0.5F), (Color)componentColors[1], decodeColor((Color)componentColors[1], (Color)componentColors[2], 0.5F), (Color)componentColors[2] });
  }
  
  private Paint decodeGradient11(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color36, decodeColor(color36, color37, 0.5F), color37 });
  }
  
  private Paint decodeGradient12(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color41, decodeColor(color41, color42, 0.5F), color42 });
  }
  
  private Paint decodeGradient13(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color47, decodeColor(color47, color48, 0.5F), color48 });
  }
  
  private Paint decodeGradient14(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.06684492F, 0.13368984F, 0.56684494F, 1.0F }, new Color[] { color48, decodeColor(color48, color49, 0.5F), color49, decodeColor(color49, color49, 0.5F), color49 });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\nimbus\ToggleButtonPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */