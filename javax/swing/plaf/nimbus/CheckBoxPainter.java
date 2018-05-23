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

final class CheckBoxPainter
  extends AbstractRegionPainter
{
  static final int BACKGROUND_DISABLED = 1;
  static final int BACKGROUND_ENABLED = 2;
  static final int ICON_DISABLED = 3;
  static final int ICON_ENABLED = 4;
  static final int ICON_FOCUSED = 5;
  static final int ICON_MOUSEOVER = 6;
  static final int ICON_MOUSEOVER_FOCUSED = 7;
  static final int ICON_PRESSED = 8;
  static final int ICON_PRESSED_FOCUSED = 9;
  static final int ICON_SELECTED = 10;
  static final int ICON_SELECTED_FOCUSED = 11;
  static final int ICON_PRESSED_SELECTED = 12;
  static final int ICON_PRESSED_SELECTED_FOCUSED = 13;
  static final int ICON_MOUSEOVER_SELECTED = 14;
  static final int ICON_MOUSEOVER_SELECTED_FOCUSED = 15;
  static final int ICON_DISABLED_SELECTED = 16;
  private int state;
  private AbstractRegionPainter.PaintContext ctx;
  private Path2D path = new Path2D.Float();
  private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
  private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  private Color color1 = decodeColor("nimbusBlueGrey", 0.0F, -0.06766917F, 0.07843137F, 0);
  private Color color2 = decodeColor("nimbusBlueGrey", 0.0F, -0.06484103F, 0.027450979F, 0);
  private Color color3 = decodeColor("nimbusBase", 0.032459438F, -0.60996324F, 0.36470586F, 0);
  private Color color4 = decodeColor("nimbusBase", 0.02551502F, -0.5996783F, 0.3215686F, 0);
  private Color color5 = decodeColor("nimbusBase", 0.032459438F, -0.59624064F, 0.34509802F, 0);
  private Color color6 = decodeColor("nimbusBlueGrey", 0.0F, 0.0F, 0.0F, -89);
  private Color color7 = decodeColor("nimbusBlueGrey", 0.0F, -0.05356429F, -0.12549019F, 0);
  private Color color8 = decodeColor("nimbusBlueGrey", 0.0F, -0.015789472F, -0.37254903F, 0);
  private Color color9 = decodeColor("nimbusBase", 0.08801502F, -0.63174605F, 0.43921566F, 0);
  private Color color10 = decodeColor("nimbusBase", 0.032459438F, -0.5953556F, 0.32549018F, 0);
  private Color color11 = decodeColor("nimbusBase", 0.032459438F, -0.59942394F, 0.4235294F, 0);
  private Color color12 = decodeColor("nimbusFocus", 0.0F, 0.0F, 0.0F, 0);
  private Color color13 = decodeColor("nimbusBlueGrey", 0.0F, -0.020974077F, -0.21960783F, 0);
  private Color color14 = decodeColor("nimbusBlueGrey", 0.01010108F, 0.08947369F, -0.5294118F, 0);
  private Color color15 = decodeColor("nimbusBase", 0.08801502F, -0.6317773F, 0.4470588F, 0);
  private Color color16 = decodeColor("nimbusBase", 0.032459438F, -0.5985242F, 0.39999998F, 0);
  private Color color17 = decodeColor("nimbusBase", 0.0F, -0.6357143F, 0.45098037F, 0);
  private Color color18 = decodeColor("nimbusBlueGrey", 0.055555582F, 0.8894737F, -0.7176471F, 0);
  private Color color19 = decodeColor("nimbusBlueGrey", 0.0F, 0.0016232133F, -0.3254902F, 0);
  private Color color20 = decodeColor("nimbusBase", 0.027408898F, -0.5847884F, 0.2980392F, 0);
  private Color color21 = decodeColor("nimbusBase", 0.029681683F, -0.52701867F, 0.17254901F, 0);
  private Color color22 = decodeColor("nimbusBase", 0.029681683F, -0.5376751F, 0.25098038F, 0);
  private Color color23 = decodeColor("nimbusBase", 5.1498413E-4F, -0.34585923F, -0.007843137F, 0);
  private Color color24 = decodeColor("nimbusBase", 5.1498413E-4F, -0.10238093F, -0.25490198F, 0);
  private Color color25 = decodeColor("nimbusBase", 0.004681647F, -0.6197143F, 0.43137252F, 0);
  private Color color26 = decodeColor("nimbusBase", 5.1498413E-4F, -0.44153953F, 0.2588235F, 0);
  private Color color27 = decodeColor("nimbusBase", 5.1498413E-4F, -0.4602757F, 0.34509802F, 0);
  private Color color28 = decodeColor("nimbusBase", -0.57865167F, -0.6357143F, -0.54901963F, 0);
  private Color color29 = decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, 0);
  private Color color30 = decodeColor("nimbusBase", -3.528595E-5F, 0.026785731F, -0.23529413F, 0);
  private Color color31 = decodeColor("nimbusBase", -4.2033195E-4F, -0.38050595F, 0.20392156F, 0);
  private Color color32 = decodeColor("nimbusBase", -0.0021489263F, -0.2891234F, 0.14117646F, 0);
  private Color color33 = decodeColor("nimbusBase", -0.006362498F, -0.016311288F, -0.02352941F, 0);
  private Color color34 = decodeColor("nimbusBase", 0.0F, -0.17930403F, 0.21568626F, 0);
  private Color color35 = decodeColor("nimbusBase", 0.0013483167F, -0.1769987F, -0.12156865F, 0);
  private Color color36 = decodeColor("nimbusBase", 0.05468172F, 0.3642857F, -0.43137258F, 0);
  private Color color37 = decodeColor("nimbusBase", 0.004681647F, -0.6198413F, 0.43921566F, 0);
  private Color color38 = decodeColor("nimbusBase", 5.1498413E-4F, -0.4555341F, 0.3215686F, 0);
  private Color color39 = decodeColor("nimbusBase", 5.1498413E-4F, -0.47377098F, 0.41960782F, 0);
  private Color color40 = decodeColor("nimbusBlueGrey", -0.01111114F, -0.03771078F, 0.062745094F, 0);
  private Color color41 = decodeColor("nimbusBlueGrey", -0.02222222F, -0.032806106F, 0.011764705F, 0);
  private Color color42 = decodeColor("nimbusBase", 0.021348298F, -0.59223604F, 0.35294116F, 0);
  private Color color43 = decodeColor("nimbusBase", 0.021348298F, -0.56722116F, 0.3098039F, 0);
  private Color color44 = decodeColor("nimbusBase", 0.021348298F, -0.56875F, 0.32941175F, 0);
  private Color color45 = decodeColor("nimbusBase", 0.027408898F, -0.5735674F, 0.14509803F, 0);
  private Object[] componentColors;
  
  public CheckBoxPainter(AbstractRegionPainter.PaintContext paramPaintContext, int paramInt)
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
      painticonDisabled(paramGraphics2D);
      break;
    case 4: 
      painticonEnabled(paramGraphics2D);
      break;
    case 5: 
      painticonFocused(paramGraphics2D);
      break;
    case 6: 
      painticonMouseOver(paramGraphics2D);
      break;
    case 7: 
      painticonMouseOverAndFocused(paramGraphics2D);
      break;
    case 8: 
      painticonPressed(paramGraphics2D);
      break;
    case 9: 
      painticonPressedAndFocused(paramGraphics2D);
      break;
    case 10: 
      painticonSelected(paramGraphics2D);
      break;
    case 11: 
      painticonSelectedAndFocused(paramGraphics2D);
      break;
    case 12: 
      painticonPressedAndSelected(paramGraphics2D);
      break;
    case 13: 
      painticonPressedAndSelectedAndFocused(paramGraphics2D);
      break;
    case 14: 
      painticonMouseOverAndSelected(paramGraphics2D);
      break;
    case 15: 
      painticonMouseOverAndSelectedAndFocused(paramGraphics2D);
      break;
    case 16: 
      painticonDisabledAndSelected(paramGraphics2D);
    }
  }
  
  protected final AbstractRegionPainter.PaintContext getPaintContext()
  {
    return ctx;
  }
  
  private void painticonDisabled(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(decodeGradient1(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient2(roundRect));
    paramGraphics2D.fill(roundRect);
  }
  
  private void painticonEnabled(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(color6);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(decodeGradient3(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient4(roundRect));
    paramGraphics2D.fill(roundRect);
  }
  
  private void painticonFocused(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect4();
    paramGraphics2D.setPaint(color12);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(decodeGradient3(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient4(roundRect));
    paramGraphics2D.fill(roundRect);
  }
  
  private void painticonMouseOver(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(color6);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(decodeGradient5(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient6(roundRect));
    paramGraphics2D.fill(roundRect);
  }
  
  private void painticonMouseOverAndFocused(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect4();
    paramGraphics2D.setPaint(color12);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(decodeGradient5(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient6(roundRect));
    paramGraphics2D.fill(roundRect);
  }
  
  private void painticonPressed(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(color6);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(decodeGradient7(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient8(roundRect));
    paramGraphics2D.fill(roundRect);
  }
  
  private void painticonPressedAndFocused(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect4();
    paramGraphics2D.setPaint(color12);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(decodeGradient7(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient8(roundRect));
    paramGraphics2D.fill(roundRect);
  }
  
  private void painticonSelected(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(color6);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(decodeGradient9(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient10(roundRect));
    paramGraphics2D.fill(roundRect);
    path = decodePath1();
    paramGraphics2D.setPaint(color28);
    paramGraphics2D.fill(path);
  }
  
  private void painticonSelectedAndFocused(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect4();
    paramGraphics2D.setPaint(color12);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(decodeGradient9(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient10(roundRect));
    paramGraphics2D.fill(roundRect);
    path = decodePath1();
    paramGraphics2D.setPaint(color28);
    paramGraphics2D.fill(path);
  }
  
  private void painticonPressedAndSelected(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(color29);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(decodeGradient11(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient12(roundRect));
    paramGraphics2D.fill(roundRect);
    path = decodePath1();
    paramGraphics2D.setPaint(color28);
    paramGraphics2D.fill(path);
  }
  
  private void painticonPressedAndSelectedAndFocused(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect4();
    paramGraphics2D.setPaint(color12);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(decodeGradient11(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient12(roundRect));
    paramGraphics2D.fill(roundRect);
    path = decodePath1();
    paramGraphics2D.setPaint(color28);
    paramGraphics2D.fill(path);
  }
  
  private void painticonMouseOverAndSelected(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(color6);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(decodeGradient13(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient14(roundRect));
    paramGraphics2D.fill(roundRect);
    path = decodePath1();
    paramGraphics2D.setPaint(color28);
    paramGraphics2D.fill(path);
  }
  
  private void painticonMouseOverAndSelectedAndFocused(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect4();
    paramGraphics2D.setPaint(color12);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(decodeGradient13(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient14(roundRect));
    paramGraphics2D.fill(roundRect);
    path = decodePath1();
    paramGraphics2D.setPaint(color28);
    paramGraphics2D.fill(path);
  }
  
  private void painticonDisabledAndSelected(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(decodeGradient15(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient16(roundRect));
    paramGraphics2D.fill(roundRect);
    path = decodePath1();
    paramGraphics2D.setPaint(color45);
    paramGraphics2D.fill(path);
  }
  
  private RoundRectangle2D decodeRoundRect1()
  {
    roundRect.setRoundRect(decodeX(0.4F), decodeY(0.4F), decodeX(2.6F) - decodeX(0.4F), decodeY(2.6F) - decodeY(0.4F), 3.7058823108673096D, 3.7058823108673096D);
    return roundRect;
  }
  
  private RoundRectangle2D decodeRoundRect2()
  {
    roundRect.setRoundRect(decodeX(0.6F), decodeY(0.6F), decodeX(2.4F) - decodeX(0.6F), decodeY(2.4F) - decodeY(0.6F), 3.7647058963775635D, 3.7647058963775635D);
    return roundRect;
  }
  
  private RoundRectangle2D decodeRoundRect3()
  {
    roundRect.setRoundRect(decodeX(0.4F), decodeY(1.75F), decodeX(2.6F) - decodeX(0.4F), decodeY(2.8F) - decodeY(1.75F), 5.176470756530762D, 5.176470756530762D);
    return roundRect;
  }
  
  private RoundRectangle2D decodeRoundRect4()
  {
    roundRect.setRoundRect(decodeX(0.120000005F), decodeY(0.120000005F), decodeX(2.8799999F) - decodeX(0.120000005F), decodeY(2.8799999F) - decodeY(0.120000005F), 8.0D, 8.0D);
    return roundRect;
  }
  
  private Path2D decodePath1()
  {
    path.reset();
    path.moveTo(decodeX(1.0036764F), decodeY(1.382353F));
    path.lineTo(decodeX(1.2536764F), decodeY(1.382353F));
    path.lineTo(decodeX(1.430147F), decodeY(1.757353F));
    path.lineTo(decodeX(1.8235294F), decodeY(0.62352943F));
    path.lineTo(decodeX(2.2F), decodeY(0.61764705F));
    path.lineTo(decodeX(1.492647F), decodeY(2.0058823F));
    path.lineTo(decodeX(1.382353F), decodeY(2.0058823F));
    path.lineTo(decodeX(1.0036764F), decodeY(1.382353F));
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
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25210086F * f3 + f1, 0.9957983F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color1, decodeColor(color1, color2, 0.5F), color2 });
  }
  
  private Paint decodeGradient2(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25F * f3 + f1, 0.997549F * f4 + f2, new float[] { 0.0F, 0.32228917F, 0.64457834F, 0.82228917F, 1.0F }, new Color[] { color3, decodeColor(color3, color4, 0.5F), color4, decodeColor(color4, color5, 0.5F), color5 });
  }
  
  private Paint decodeGradient3(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25210086F * f3 + f1, 0.9957983F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color7, decodeColor(color7, color8, 0.5F), color8 });
  }
  
  private Paint decodeGradient4(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25F * f3 + f1, 0.997549F * f4 + f2, new float[] { 0.0F, 0.32228917F, 0.64457834F, 0.82228917F, 1.0F }, new Color[] { color9, decodeColor(color9, color10, 0.5F), color10, decodeColor(color10, color11, 0.5F), color11 });
  }
  
  private Paint decodeGradient5(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25210086F * f3 + f1, 0.9957983F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color13, decodeColor(color13, color14, 0.5F), color14 });
  }
  
  private Paint decodeGradient6(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25F * f3 + f1, 0.997549F * f4 + f2, new float[] { 0.0F, 0.32228917F, 0.64457834F, 0.82228917F, 1.0F }, new Color[] { color15, decodeColor(color15, color16, 0.5F), color16, decodeColor(color16, color17, 0.5F), color17 });
  }
  
  private Paint decodeGradient7(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25210086F * f3 + f1, 0.9957983F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color18, decodeColor(color18, color19, 0.5F), color19 });
  }
  
  private Paint decodeGradient8(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25F * f3 + f1, 0.997549F * f4 + f2, new float[] { 0.0F, 0.32228917F, 0.64457834F, 0.82228917F, 1.0F }, new Color[] { color20, decodeColor(color20, color21, 0.5F), color21, decodeColor(color21, color22, 0.5F), color22 });
  }
  
  private Paint decodeGradient9(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25210086F * f3 + f1, 0.9957983F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color23, decodeColor(color23, color24, 0.5F), color24 });
  }
  
  private Paint decodeGradient10(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25F * f3 + f1, 0.997549F * f4 + f2, new float[] { 0.0F, 0.32228917F, 0.64457834F, 0.82228917F, 1.0F }, new Color[] { color25, decodeColor(color25, color26, 0.5F), color26, decodeColor(color26, color27, 0.5F), color27 });
  }
  
  private Paint decodeGradient11(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25210086F * f3 + f1, 0.9957983F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color28, decodeColor(color28, color30, 0.5F), color30 });
  }
  
  private Paint decodeGradient12(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25F * f3 + f1, 0.997549F * f4 + f2, new float[] { 0.0F, 0.05775076F, 0.11550152F, 0.38003993F, 0.64457834F, 0.82228917F, 1.0F }, new Color[] { color31, decodeColor(color31, color32, 0.5F), color32, decodeColor(color32, color33, 0.5F), color33, decodeColor(color33, color34, 0.5F), color34 });
  }
  
  private Paint decodeGradient13(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25210086F * f3 + f1, 0.9957983F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color35, decodeColor(color35, color36, 0.5F), color36 });
  }
  
  private Paint decodeGradient14(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25F * f3 + f1, 0.997549F * f4 + f2, new float[] { 0.0F, 0.32228917F, 0.64457834F, 0.82228917F, 1.0F }, new Color[] { color37, decodeColor(color37, color38, 0.5F), color38, decodeColor(color38, color39, 0.5F), color39 });
  }
  
  private Paint decodeGradient15(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25210086F * f3 + f1, 0.9957983F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color40, decodeColor(color40, color41, 0.5F), color41 });
  }
  
  private Paint decodeGradient16(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25F * f3 + f1, 0.997549F * f4 + f2, new float[] { 0.0F, 0.32228917F, 0.64457834F, 0.82228917F, 1.0F }, new Color[] { color42, decodeColor(color42, color43, 0.5F), color43, decodeColor(color43, color44, 0.5F), color44 });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\nimbus\CheckBoxPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */