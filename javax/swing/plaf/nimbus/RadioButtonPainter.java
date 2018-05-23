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

final class RadioButtonPainter
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
  private Color color2 = decodeColor("nimbusBlueGrey", 0.0F, -0.06413457F, 0.015686274F, 0);
  private Color color3 = decodeColor("nimbusBlueGrey", 0.0F, -0.08466425F, 0.16470587F, 0);
  private Color color4 = decodeColor("nimbusBlueGrey", 0.0F, -0.07016757F, 0.12941176F, 0);
  private Color color5 = decodeColor("nimbusBlueGrey", 0.0F, -0.070703305F, 0.14117646F, 0);
  private Color color6 = decodeColor("nimbusBlueGrey", 0.0F, -0.07052632F, 0.1372549F, 0);
  private Color color7 = decodeColor("nimbusBlueGrey", 0.0F, 0.0F, 0.0F, -112);
  private Color color8 = decodeColor("nimbusBlueGrey", 0.0F, -0.053201474F, -0.12941176F, 0);
  private Color color9 = decodeColor("nimbusBlueGrey", 0.0F, 0.006356798F, -0.44313726F, 0);
  private Color color10 = decodeColor("nimbusBlueGrey", 0.055555582F, -0.10654225F, 0.23921567F, 0);
  private Color color11 = decodeColor("nimbusBlueGrey", 0.0F, -0.07206477F, 0.17254901F, 0);
  private Color color12 = decodeColor("nimbusFocus", 0.0F, 0.0F, 0.0F, 0);
  private Color color13 = decodeColor("nimbusBlueGrey", -0.00505054F, -0.027819552F, -0.2235294F, 0);
  private Color color14 = decodeColor("nimbusBlueGrey", 0.0F, 0.24241486F, -0.6117647F, 0);
  private Color color15 = decodeColor("nimbusBlueGrey", -0.111111104F, -0.10655806F, 0.24313724F, 0);
  private Color color16 = decodeColor("nimbusBlueGrey", 0.0F, -0.07333623F, 0.20392156F, 0);
  private Color color17 = decodeColor("nimbusBlueGrey", 0.08585858F, -0.067389056F, 0.25490195F, 0);
  private Color color18 = decodeColor("nimbusBlueGrey", -0.111111104F, -0.10628903F, 0.18039215F, 0);
  private Color color19 = decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, 0);
  private Color color20 = decodeColor("nimbusBlueGrey", 0.055555582F, 0.23947367F, -0.6666667F, 0);
  private Color color21 = decodeColor("nimbusBlueGrey", -0.0777778F, -0.06815343F, -0.28235295F, 0);
  private Color color22 = decodeColor("nimbusBlueGrey", 0.0F, -0.06866585F, 0.09803921F, 0);
  private Color color23 = decodeColor("nimbusBlueGrey", -0.0027777553F, -0.0018306673F, -0.02352941F, 0);
  private Color color24 = decodeColor("nimbusBlueGrey", 0.002924025F, -0.02047892F, 0.082352936F, 0);
  private Color color25 = decodeColor("nimbusBase", 2.9569864E-4F, -0.36035198F, -0.007843137F, 0);
  private Color color26 = decodeColor("nimbusBase", 2.9569864E-4F, 0.019458115F, -0.32156867F, 0);
  private Color color27 = decodeColor("nimbusBase", 0.004681647F, -0.6195853F, 0.4235294F, 0);
  private Color color28 = decodeColor("nimbusBase", 0.004681647F, -0.56704473F, 0.36470586F, 0);
  private Color color29 = decodeColor("nimbusBase", 5.1498413E-4F, -0.43866998F, 0.24705881F, 0);
  private Color color30 = decodeColor("nimbusBase", 5.1498413E-4F, -0.44879842F, 0.29019606F, 0);
  private Color color31 = decodeColor("nimbusBlueGrey", -0.027777791F, -0.07243107F, -0.33333334F, 0);
  private Color color32 = decodeColor("nimbusBlueGrey", -0.6111111F, -0.110526316F, -0.74509805F, 0);
  private Color color33 = decodeColor("nimbusBlueGrey", -0.027777791F, 0.07129187F, -0.6156863F, 0);
  private Color color34 = decodeColor("nimbusBase", -0.57865167F, -0.6357143F, -0.49803925F, 0);
  private Color color35 = decodeColor("nimbusBase", 0.0030477047F, -0.1257143F, -0.15686277F, 0);
  private Color color36 = decodeColor("nimbusBase", -0.0017285943F, -0.4367347F, 0.21960783F, 0);
  private Color color37 = decodeColor("nimbusBase", -0.0010654926F, -0.31349206F, 0.15686274F, 0);
  private Color color38 = decodeColor("nimbusBase", 0.0F, 0.0F, 0.0F, 0);
  private Color color39 = decodeColor("nimbusBase", 8.05676E-4F, -0.12380952F, 0.109803915F, 0);
  private Color color40 = decodeColor("nimbusBlueGrey", -0.027777791F, -0.080223285F, -0.4862745F, 0);
  private Color color41 = decodeColor("nimbusBase", -6.374717E-4F, -0.20452163F, -0.12156865F, 0);
  private Color color42 = decodeColor("nimbusBase", -0.57865167F, -0.6357143F, -0.5058824F, 0);
  private Color color43 = decodeColor("nimbusBase", -0.011985004F, -0.6157143F, 0.43137252F, 0);
  private Color color44 = decodeColor("nimbusBase", 0.004681647F, -0.56932425F, 0.3960784F, 0);
  private Color color45 = decodeColor("nimbusBase", 5.1498413E-4F, -0.4555341F, 0.3215686F, 0);
  private Color color46 = decodeColor("nimbusBase", 5.1498413E-4F, -0.46550155F, 0.372549F, 0);
  private Color color47 = decodeColor("nimbusBase", 0.0024294257F, -0.47271872F, 0.34117645F, 0);
  private Color color48 = decodeColor("nimbusBase", 0.010237217F, -0.56289876F, 0.2588235F, 0);
  private Color color49 = decodeColor("nimbusBase", 0.016586483F, -0.5620301F, 0.19607842F, 0);
  private Color color50 = decodeColor("nimbusBase", 0.027408898F, -0.5878882F, 0.35294116F, 0);
  private Color color51 = decodeColor("nimbusBase", 0.021348298F, -0.56722116F, 0.3098039F, 0);
  private Color color52 = decodeColor("nimbusBase", 0.021348298F, -0.567841F, 0.31764704F, 0);
  private Color color53 = decodeColor("nimbusBlueGrey", -0.01111114F, -0.058170296F, 0.0039215684F, 0);
  private Color color54 = decodeColor("nimbusBlueGrey", -0.013888836F, -0.04195489F, -0.058823526F, 0);
  private Color color55 = decodeColor("nimbusBlueGrey", 0.009259284F, -0.0147816315F, -0.007843137F, 0);
  private Object[] componentColors;
  
  public RadioButtonPainter(AbstractRegionPainter.PaintContext paramPaintContext, int paramInt)
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
    ellipse = decodeEllipse1();
    paramGraphics2D.setPaint(decodeGradient1(ellipse));
    paramGraphics2D.fill(ellipse);
    ellipse = decodeEllipse2();
    paramGraphics2D.setPaint(decodeGradient2(ellipse));
    paramGraphics2D.fill(ellipse);
  }
  
  private void painticonEnabled(Graphics2D paramGraphics2D)
  {
    ellipse = decodeEllipse3();
    paramGraphics2D.setPaint(color7);
    paramGraphics2D.fill(ellipse);
    ellipse = decodeEllipse1();
    paramGraphics2D.setPaint(decodeGradient3(ellipse));
    paramGraphics2D.fill(ellipse);
    ellipse = decodeEllipse2();
    paramGraphics2D.setPaint(decodeGradient4(ellipse));
    paramGraphics2D.fill(ellipse);
  }
  
  private void painticonFocused(Graphics2D paramGraphics2D)
  {
    ellipse = decodeEllipse4();
    paramGraphics2D.setPaint(color12);
    paramGraphics2D.fill(ellipse);
    ellipse = decodeEllipse1();
    paramGraphics2D.setPaint(decodeGradient3(ellipse));
    paramGraphics2D.fill(ellipse);
    ellipse = decodeEllipse2();
    paramGraphics2D.setPaint(decodeGradient4(ellipse));
    paramGraphics2D.fill(ellipse);
  }
  
  private void painticonMouseOver(Graphics2D paramGraphics2D)
  {
    ellipse = decodeEllipse3();
    paramGraphics2D.setPaint(color7);
    paramGraphics2D.fill(ellipse);
    ellipse = decodeEllipse1();
    paramGraphics2D.setPaint(decodeGradient5(ellipse));
    paramGraphics2D.fill(ellipse);
    ellipse = decodeEllipse2();
    paramGraphics2D.setPaint(decodeGradient6(ellipse));
    paramGraphics2D.fill(ellipse);
  }
  
  private void painticonMouseOverAndFocused(Graphics2D paramGraphics2D)
  {
    ellipse = decodeEllipse4();
    paramGraphics2D.setPaint(color12);
    paramGraphics2D.fill(ellipse);
    ellipse = decodeEllipse1();
    paramGraphics2D.setPaint(decodeGradient5(ellipse));
    paramGraphics2D.fill(ellipse);
    ellipse = decodeEllipse2();
    paramGraphics2D.setPaint(decodeGradient6(ellipse));
    paramGraphics2D.fill(ellipse);
  }
  
  private void painticonPressed(Graphics2D paramGraphics2D)
  {
    ellipse = decodeEllipse3();
    paramGraphics2D.setPaint(color19);
    paramGraphics2D.fill(ellipse);
    ellipse = decodeEllipse1();
    paramGraphics2D.setPaint(decodeGradient7(ellipse));
    paramGraphics2D.fill(ellipse);
    ellipse = decodeEllipse2();
    paramGraphics2D.setPaint(decodeGradient8(ellipse));
    paramGraphics2D.fill(ellipse);
  }
  
  private void painticonPressedAndFocused(Graphics2D paramGraphics2D)
  {
    ellipse = decodeEllipse4();
    paramGraphics2D.setPaint(color12);
    paramGraphics2D.fill(ellipse);
    ellipse = decodeEllipse1();
    paramGraphics2D.setPaint(decodeGradient7(ellipse));
    paramGraphics2D.fill(ellipse);
    ellipse = decodeEllipse2();
    paramGraphics2D.setPaint(decodeGradient8(ellipse));
    paramGraphics2D.fill(ellipse);
  }
  
  private void painticonSelected(Graphics2D paramGraphics2D)
  {
    ellipse = decodeEllipse3();
    paramGraphics2D.setPaint(color7);
    paramGraphics2D.fill(ellipse);
    ellipse = decodeEllipse1();
    paramGraphics2D.setPaint(decodeGradient9(ellipse));
    paramGraphics2D.fill(ellipse);
    ellipse = decodeEllipse2();
    paramGraphics2D.setPaint(decodeGradient10(ellipse));
    paramGraphics2D.fill(ellipse);
    ellipse = decodeEllipse5();
    paramGraphics2D.setPaint(decodeGradient11(ellipse));
    paramGraphics2D.fill(ellipse);
  }
  
  private void painticonSelectedAndFocused(Graphics2D paramGraphics2D)
  {
    ellipse = decodeEllipse4();
    paramGraphics2D.setPaint(color12);
    paramGraphics2D.fill(ellipse);
    ellipse = decodeEllipse1();
    paramGraphics2D.setPaint(decodeGradient9(ellipse));
    paramGraphics2D.fill(ellipse);
    ellipse = decodeEllipse2();
    paramGraphics2D.setPaint(decodeGradient10(ellipse));
    paramGraphics2D.fill(ellipse);
    ellipse = decodeEllipse5();
    paramGraphics2D.setPaint(decodeGradient11(ellipse));
    paramGraphics2D.fill(ellipse);
  }
  
  private void painticonPressedAndSelected(Graphics2D paramGraphics2D)
  {
    ellipse = decodeEllipse3();
    paramGraphics2D.setPaint(color19);
    paramGraphics2D.fill(ellipse);
    ellipse = decodeEllipse1();
    paramGraphics2D.setPaint(decodeGradient12(ellipse));
    paramGraphics2D.fill(ellipse);
    ellipse = decodeEllipse2();
    paramGraphics2D.setPaint(decodeGradient13(ellipse));
    paramGraphics2D.fill(ellipse);
    ellipse = decodeEllipse5();
    paramGraphics2D.setPaint(decodeGradient14(ellipse));
    paramGraphics2D.fill(ellipse);
  }
  
  private void painticonPressedAndSelectedAndFocused(Graphics2D paramGraphics2D)
  {
    ellipse = decodeEllipse4();
    paramGraphics2D.setPaint(color12);
    paramGraphics2D.fill(ellipse);
    ellipse = decodeEllipse1();
    paramGraphics2D.setPaint(decodeGradient12(ellipse));
    paramGraphics2D.fill(ellipse);
    ellipse = decodeEllipse2();
    paramGraphics2D.setPaint(decodeGradient13(ellipse));
    paramGraphics2D.fill(ellipse);
    ellipse = decodeEllipse5();
    paramGraphics2D.setPaint(decodeGradient14(ellipse));
    paramGraphics2D.fill(ellipse);
  }
  
  private void painticonMouseOverAndSelected(Graphics2D paramGraphics2D)
  {
    ellipse = decodeEllipse3();
    paramGraphics2D.setPaint(color7);
    paramGraphics2D.fill(ellipse);
    ellipse = decodeEllipse1();
    paramGraphics2D.setPaint(decodeGradient15(ellipse));
    paramGraphics2D.fill(ellipse);
    ellipse = decodeEllipse2();
    paramGraphics2D.setPaint(decodeGradient16(ellipse));
    paramGraphics2D.fill(ellipse);
    ellipse = decodeEllipse5();
    paramGraphics2D.setPaint(decodeGradient11(ellipse));
    paramGraphics2D.fill(ellipse);
  }
  
  private void painticonMouseOverAndSelectedAndFocused(Graphics2D paramGraphics2D)
  {
    ellipse = decodeEllipse4();
    paramGraphics2D.setPaint(color12);
    paramGraphics2D.fill(ellipse);
    ellipse = decodeEllipse1();
    paramGraphics2D.setPaint(decodeGradient15(ellipse));
    paramGraphics2D.fill(ellipse);
    ellipse = decodeEllipse2();
    paramGraphics2D.setPaint(decodeGradient16(ellipse));
    paramGraphics2D.fill(ellipse);
    ellipse = decodeEllipse5();
    paramGraphics2D.setPaint(decodeGradient11(ellipse));
    paramGraphics2D.fill(ellipse);
  }
  
  private void painticonDisabledAndSelected(Graphics2D paramGraphics2D)
  {
    ellipse = decodeEllipse1();
    paramGraphics2D.setPaint(decodeGradient17(ellipse));
    paramGraphics2D.fill(ellipse);
    ellipse = decodeEllipse2();
    paramGraphics2D.setPaint(decodeGradient18(ellipse));
    paramGraphics2D.fill(ellipse);
    ellipse = decodeEllipse5();
    paramGraphics2D.setPaint(decodeGradient19(ellipse));
    paramGraphics2D.fill(ellipse);
  }
  
  private Ellipse2D decodeEllipse1()
  {
    ellipse.setFrame(decodeX(0.4F), decodeY(0.4F), decodeX(2.6F) - decodeX(0.4F), decodeY(2.6F) - decodeY(0.4F));
    return ellipse;
  }
  
  private Ellipse2D decodeEllipse2()
  {
    ellipse.setFrame(decodeX(0.6F), decodeY(0.6F), decodeX(2.4F) - decodeX(0.6F), decodeY(2.4F) - decodeY(0.6F));
    return ellipse;
  }
  
  private Ellipse2D decodeEllipse3()
  {
    ellipse.setFrame(decodeX(0.4F), decodeY(0.6F), decodeX(2.6F) - decodeX(0.4F), decodeY(2.8F) - decodeY(0.6F));
    return ellipse;
  }
  
  private Ellipse2D decodeEllipse4()
  {
    ellipse.setFrame(decodeX(0.120000005F), decodeY(0.120000005F), decodeX(2.8799999F) - decodeX(0.120000005F), decodeY(2.8799999F) - decodeY(0.120000005F));
    return ellipse;
  }
  
  private Ellipse2D decodeEllipse5()
  {
    ellipse.setFrame(decodeX(1.125F), decodeY(1.125F), decodeX(1.875F) - decodeX(1.125F), decodeY(1.875F) - decodeY(1.125F));
    return ellipse;
  }
  
  private Paint decodeGradient1(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.49789914F * f3 + f1, -0.004201681F * f4 + f2, 0.5F * f3 + f1, 0.9978992F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color1, decodeColor(color1, color2, 0.5F), color2 });
  }
  
  private Paint decodeGradient2(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.49754903F * f3 + f1, 0.004901961F * f4 + f2, 0.50735295F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.06344411F, 0.21601209F, 0.36858007F, 0.54833835F, 0.72809666F, 0.77492446F, 0.82175225F, 0.91087615F, 1.0F }, new Color[] { color3, decodeColor(color3, color4, 0.5F), color4, decodeColor(color4, color4, 0.5F), color4, decodeColor(color4, color5, 0.5F), color5, decodeColor(color5, color6, 0.5F), color6 });
  }
  
  private Paint decodeGradient3(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.49789914F * f3 + f1, -0.004201681F * f4 + f2, 0.5F * f3 + f1, 0.9978992F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color8, decodeColor(color8, color9, 0.5F), color9 });
  }
  
  private Paint decodeGradient4(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.49754903F * f3 + f1, 0.004901961F * f4 + f2, 0.50735295F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.06344411F, 0.25009555F, 0.43674698F, 0.48042166F, 0.52409637F, 0.70481926F, 0.88554215F }, new Color[] { color10, decodeColor(color10, color4, 0.5F), color4, decodeColor(color4, color4, 0.5F), color4, decodeColor(color4, color11, 0.5F), color11 });
  }
  
  private Paint decodeGradient5(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.49789914F * f3 + f1, -0.004201681F * f4 + f2, 0.5F * f3 + f1, 0.9978992F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color13, decodeColor(color13, color14, 0.5F), color14 });
  }
  
  private Paint decodeGradient6(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.49754903F * f3 + f1, 0.004901961F * f4 + f2, 0.50735295F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.06344411F, 0.21601209F, 0.36858007F, 0.54833835F, 0.72809666F, 0.77492446F, 0.82175225F, 0.91087615F, 1.0F }, new Color[] { color15, decodeColor(color15, color16, 0.5F), color16, decodeColor(color16, color16, 0.5F), color16, decodeColor(color16, color17, 0.5F), color17, decodeColor(color17, color18, 0.5F), color18 });
  }
  
  private Paint decodeGradient7(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.49789914F * f3 + f1, -0.004201681F * f4 + f2, 0.5F * f3 + f1, 0.9978992F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color20, decodeColor(color20, color21, 0.5F), color21 });
  }
  
  private Paint decodeGradient8(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.49754903F * f3 + f1, 0.004901961F * f4 + f2, 0.50735295F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.06344411F, 0.20792687F, 0.35240963F, 0.45030123F, 0.5481928F, 0.748494F, 0.9487952F }, new Color[] { color22, decodeColor(color22, color23, 0.5F), color23, decodeColor(color23, color23, 0.5F), color23, decodeColor(color23, color24, 0.5F), color24 });
  }
  
  private Paint decodeGradient9(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.49789914F * f3 + f1, -0.004201681F * f4 + f2, 0.5F * f3 + f1, 0.9978992F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color25, decodeColor(color25, color26, 0.5F), color26 });
  }
  
  private Paint decodeGradient10(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.49754903F * f3 + f1, 0.004901961F * f4 + f2, 0.50735295F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0813253F, 0.100903615F, 0.12048193F, 0.28915662F, 0.45783132F, 0.6159638F, 0.77409637F, 0.82981926F, 0.88554215F }, new Color[] { color27, decodeColor(color27, color28, 0.5F), color28, decodeColor(color28, color29, 0.5F), color29, decodeColor(color29, color29, 0.5F), color29, decodeColor(color29, color30, 0.5F), color30 });
  }
  
  private Paint decodeGradient11(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.50490195F * f3 + f1, 0.0F * f4 + f2, 0.49509802F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.23192771F, 0.46385542F, 0.73192775F, 1.0F }, new Color[] { color31, decodeColor(color31, color32, 0.5F), color32, decodeColor(color32, color33, 0.5F), color33 });
  }
  
  private Paint decodeGradient12(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.49789914F * f3 + f1, -0.004201681F * f4 + f2, 0.5F * f3 + f1, 0.9978992F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color34, decodeColor(color34, color26, 0.5F), color26 });
  }
  
  private Paint decodeGradient13(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.49754903F * f3 + f1, 0.004901961F * f4 + f2, 0.50735295F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.039156627F, 0.07831325F, 0.11746988F, 0.2876506F, 0.45783132F, 0.56174695F, 0.66566265F, 0.7756024F, 0.88554215F }, new Color[] { color36, decodeColor(color36, color37, 0.5F), color37, decodeColor(color37, color38, 0.5F), color38, decodeColor(color38, color38, 0.5F), color38, decodeColor(color38, color39, 0.5F), color39 });
  }
  
  private Paint decodeGradient14(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.50490195F * f3 + f1, 0.0F * f4 + f2, 0.49509802F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.23192771F, 0.46385542F, 0.73192775F, 1.0F }, new Color[] { color40, decodeColor(color40, color32, 0.5F), color32, decodeColor(color32, color33, 0.5F), color33 });
  }
  
  private Paint decodeGradient15(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.49789914F * f3 + f1, -0.004201681F * f4 + f2, 0.5F * f3 + f1, 0.9978992F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color41, decodeColor(color41, color42, 0.5F), color42 });
  }
  
  private Paint decodeGradient16(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.49754903F * f3 + f1, 0.004901961F * f4 + f2, 0.50735295F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0813253F, 0.100903615F, 0.12048193F, 0.20180723F, 0.28313252F, 0.49246985F, 0.7018072F, 0.7560241F, 0.810241F, 0.84789157F, 0.88554215F }, new Color[] { color43, decodeColor(color43, color44, 0.5F), color44, decodeColor(color44, color45, 0.5F), color45, decodeColor(color45, color45, 0.5F), color45, decodeColor(color45, color46, 0.5F), color46, decodeColor(color46, color47, 0.5F), color47 });
  }
  
  private Paint decodeGradient17(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.49789914F * f3 + f1, -0.004201681F * f4 + f2, 0.5F * f3 + f1, 0.9978992F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color48, decodeColor(color48, color49, 0.5F), color49 });
  }
  
  private Paint decodeGradient18(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.49754903F * f3 + f1, 0.004901961F * f4 + f2, 0.50735295F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0813253F, 0.2695783F, 0.45783132F, 0.67168677F, 0.88554215F }, new Color[] { color50, decodeColor(color50, color51, 0.5F), color51, decodeColor(color51, color52, 0.5F), color52 });
  }
  
  private Paint decodeGradient19(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.50490195F * f3 + f1, 0.0F * f4 + f2, 0.49509802F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.23192771F, 0.46385542F, 0.73192775F, 1.0F }, new Color[] { color53, decodeColor(color53, color54, 0.5F), color54, decodeColor(color54, color55, 0.5F), color55 });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\nimbus\RadioButtonPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */