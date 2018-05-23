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

final class TabbedPaneTabPainter
  extends AbstractRegionPainter
{
  static final int BACKGROUND_ENABLED = 1;
  static final int BACKGROUND_ENABLED_MOUSEOVER = 2;
  static final int BACKGROUND_ENABLED_PRESSED = 3;
  static final int BACKGROUND_DISABLED = 4;
  static final int BACKGROUND_SELECTED_DISABLED = 5;
  static final int BACKGROUND_SELECTED = 6;
  static final int BACKGROUND_SELECTED_MOUSEOVER = 7;
  static final int BACKGROUND_SELECTED_PRESSED = 8;
  static final int BACKGROUND_SELECTED_FOCUSED = 9;
  static final int BACKGROUND_SELECTED_MOUSEOVER_FOCUSED = 10;
  static final int BACKGROUND_SELECTED_PRESSED_FOCUSED = 11;
  private int state;
  private AbstractRegionPainter.PaintContext ctx;
  private Path2D path = new Path2D.Float();
  private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
  private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  private Color color1 = decodeColor("nimbusBase", 0.032459438F, -0.55535716F, -0.109803945F, 0);
  private Color color2 = decodeColor("nimbusBase", 0.08801502F, 0.3642857F, -0.4784314F, 0);
  private Color color3 = decodeColor("nimbusBase", 0.08801502F, -0.63174605F, 0.43921566F, 0);
  private Color color4 = decodeColor("nimbusBase", 0.05468172F, -0.6145278F, 0.37647057F, 0);
  private Color color5 = decodeColor("nimbusBase", 0.032459438F, -0.5953556F, 0.32549018F, 0);
  private Color color6 = decodeColor("nimbusBase", 0.032459438F, -0.54616207F, -0.02352941F, 0);
  private Color color7 = decodeColor("nimbusBase", 0.08801502F, -0.6317773F, 0.4470588F, 0);
  private Color color8 = decodeColor("nimbusBase", 0.021348298F, -0.61547136F, 0.41960782F, 0);
  private Color color9 = decodeColor("nimbusBase", 0.032459438F, -0.5985242F, 0.39999998F, 0);
  private Color color10 = decodeColor("nimbusBase", 0.08801502F, 0.3642857F, -0.52156866F, 0);
  private Color color11 = decodeColor("nimbusBase", 0.027408898F, -0.5847884F, 0.2980392F, 0);
  private Color color12 = decodeColor("nimbusBase", 0.035931647F, -0.5553123F, 0.23137254F, 0);
  private Color color13 = decodeColor("nimbusBase", 0.029681683F, -0.5281874F, 0.18039215F, 0);
  private Color color14 = decodeColor("nimbusBase", 0.03801495F, -0.5456242F, 0.3215686F, 0);
  private Color color15 = decodeColor("nimbusBase", 0.032459438F, -0.59181184F, 0.25490195F, 0);
  private Color color16 = decodeColor("nimbusBase", 0.05468172F, -0.58308274F, 0.19607842F, 0);
  private Color color17 = decodeColor("nimbusBase", 0.046348333F, -0.6006266F, 0.34509802F, 0);
  private Color color18 = decodeColor("nimbusBase", 0.046348333F, -0.60015875F, 0.3333333F, 0);
  private Color color19 = decodeColor("nimbusBase", 0.004681647F, -0.6197143F, 0.43137252F, 0);
  private Color color20 = decodeColor("nimbusBase", 7.13408E-4F, -0.543609F, 0.34509802F, 0);
  private Color color21 = decodeColor("nimbusBase", -0.0020751357F, -0.45610264F, 0.2588235F, 0);
  private Color color22 = decodeColor("nimbusBase", 5.1498413E-4F, -0.43866998F, 0.24705881F, 0);
  private Color color23 = decodeColor("nimbusBase", 5.1498413E-4F, -0.44879842F, 0.29019606F, 0);
  private Color color24 = decodeColor("nimbusBase", 5.1498413E-4F, -0.08776909F, -0.2627451F, 0);
  private Color color25 = decodeColor("nimbusBase", 0.06332368F, 0.3642857F, -0.4431373F, 0);
  private Color color26 = decodeColor("nimbusBase", 0.004681647F, -0.6198413F, 0.43921566F, 0);
  private Color color27 = decodeColor("nimbusBase", -0.0022627711F, -0.5335866F, 0.372549F, 0);
  private Color color28 = decodeColor("nimbusBase", -0.0017285943F, -0.4608264F, 0.32549018F, 0);
  private Color color29 = decodeColor("nimbusBase", 5.1498413E-4F, -0.4555341F, 0.3215686F, 0);
  private Color color30 = decodeColor("nimbusBase", 5.1498413E-4F, -0.46404046F, 0.36470586F, 0);
  private Color color31 = decodeColor("nimbusBase", -0.57865167F, -0.6357143F, -0.54901963F, 0);
  private Color color32 = decodeColor("nimbusBase", -4.2033195E-4F, -0.38050595F, 0.20392156F, 0);
  private Color color33 = decodeColor("nimbusBase", 0.0013483167F, -0.16401619F, 0.0745098F, 0);
  private Color color34 = decodeColor("nimbusBase", -0.0010001659F, -0.01599598F, 0.007843137F, 0);
  private Color color35 = decodeColor("nimbusBase", 0.0F, 0.0F, 0.0F, 0);
  private Color color36 = decodeColor("nimbusBase", 0.0018727183F, -0.038398862F, 0.035294116F, 0);
  private Color color37 = decodeColor("nimbusFocus", 0.0F, 0.0F, 0.0F, 0);
  private Object[] componentColors;
  
  public TabbedPaneTabPainter(AbstractRegionPainter.PaintContext paramPaintContext, int paramInt)
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
      paintBackgroundEnabled(paramGraphics2D);
      break;
    case 2: 
      paintBackgroundEnabledAndMouseOver(paramGraphics2D);
      break;
    case 3: 
      paintBackgroundEnabledAndPressed(paramGraphics2D);
      break;
    case 4: 
      paintBackgroundDisabled(paramGraphics2D);
      break;
    case 5: 
      paintBackgroundSelectedAndDisabled(paramGraphics2D);
      break;
    case 6: 
      paintBackgroundSelected(paramGraphics2D);
      break;
    case 7: 
      paintBackgroundSelectedAndMouseOver(paramGraphics2D);
      break;
    case 8: 
      paintBackgroundSelectedAndPressed(paramGraphics2D);
      break;
    case 9: 
      paintBackgroundSelectedAndFocused(paramGraphics2D);
      break;
    case 10: 
      paintBackgroundSelectedAndMouseOverAndFocused(paramGraphics2D);
      break;
    case 11: 
      paintBackgroundSelectedAndPressedAndFocused(paramGraphics2D);
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
  }
  
  private void paintBackgroundEnabledAndMouseOver(Graphics2D paramGraphics2D)
  {
    path = decodePath1();
    paramGraphics2D.setPaint(decodeGradient3(path));
    paramGraphics2D.fill(path);
    path = decodePath2();
    paramGraphics2D.setPaint(decodeGradient4(path));
    paramGraphics2D.fill(path);
  }
  
  private void paintBackgroundEnabledAndPressed(Graphics2D paramGraphics2D)
  {
    path = decodePath3();
    paramGraphics2D.setPaint(decodeGradient5(path));
    paramGraphics2D.fill(path);
    path = decodePath4();
    paramGraphics2D.setPaint(decodeGradient6(path));
    paramGraphics2D.fill(path);
  }
  
  private void paintBackgroundDisabled(Graphics2D paramGraphics2D)
  {
    path = decodePath5();
    paramGraphics2D.setPaint(decodeGradient7(path));
    paramGraphics2D.fill(path);
    path = decodePath6();
    paramGraphics2D.setPaint(decodeGradient8(path));
    paramGraphics2D.fill(path);
  }
  
  private void paintBackgroundSelectedAndDisabled(Graphics2D paramGraphics2D)
  {
    path = decodePath7();
    paramGraphics2D.setPaint(decodeGradient7(path));
    paramGraphics2D.fill(path);
    path = decodePath2();
    paramGraphics2D.setPaint(decodeGradient9(path));
    paramGraphics2D.fill(path);
  }
  
  private void paintBackgroundSelected(Graphics2D paramGraphics2D)
  {
    path = decodePath7();
    paramGraphics2D.setPaint(decodeGradient10(path));
    paramGraphics2D.fill(path);
    path = decodePath2();
    paramGraphics2D.setPaint(decodeGradient9(path));
    paramGraphics2D.fill(path);
  }
  
  private void paintBackgroundSelectedAndMouseOver(Graphics2D paramGraphics2D)
  {
    path = decodePath8();
    paramGraphics2D.setPaint(decodeGradient11(path));
    paramGraphics2D.fill(path);
    path = decodePath9();
    paramGraphics2D.setPaint(decodeGradient12(path));
    paramGraphics2D.fill(path);
  }
  
  private void paintBackgroundSelectedAndPressed(Graphics2D paramGraphics2D)
  {
    path = decodePath8();
    paramGraphics2D.setPaint(decodeGradient13(path));
    paramGraphics2D.fill(path);
    path = decodePath9();
    paramGraphics2D.setPaint(decodeGradient14(path));
    paramGraphics2D.fill(path);
  }
  
  private void paintBackgroundSelectedAndFocused(Graphics2D paramGraphics2D)
  {
    path = decodePath1();
    paramGraphics2D.setPaint(decodeGradient10(path));
    paramGraphics2D.fill(path);
    path = decodePath10();
    paramGraphics2D.setPaint(decodeGradient9(path));
    paramGraphics2D.fill(path);
    path = decodePath11();
    paramGraphics2D.setPaint(color37);
    paramGraphics2D.fill(path);
  }
  
  private void paintBackgroundSelectedAndMouseOverAndFocused(Graphics2D paramGraphics2D)
  {
    path = decodePath12();
    paramGraphics2D.setPaint(decodeGradient11(path));
    paramGraphics2D.fill(path);
    path = decodePath13();
    paramGraphics2D.setPaint(decodeGradient12(path));
    paramGraphics2D.fill(path);
    path = decodePath14();
    paramGraphics2D.setPaint(color37);
    paramGraphics2D.fill(path);
  }
  
  private void paintBackgroundSelectedAndPressedAndFocused(Graphics2D paramGraphics2D)
  {
    path = decodePath12();
    paramGraphics2D.setPaint(decodeGradient13(path));
    paramGraphics2D.fill(path);
    path = decodePath13();
    paramGraphics2D.setPaint(decodeGradient14(path));
    paramGraphics2D.fill(path);
    path = decodePath14();
    paramGraphics2D.setPaint(color37);
    paramGraphics2D.fill(path);
  }
  
  private Path2D decodePath1()
  {
    path.reset();
    path.moveTo(decodeX(0.0F), decodeY(0.71428573F));
    path.curveTo(decodeAnchorX(0.0F, 0.0F), decodeAnchorY(0.71428573F, -3.0F), decodeAnchorX(0.71428573F, -3.0F), decodeAnchorY(0.0F, 0.0F), decodeX(0.71428573F), decodeY(0.0F));
    path.curveTo(decodeAnchorX(0.71428573F, 3.0F), decodeAnchorY(0.0F, 0.0F), decodeAnchorX(2.2857144F, -3.0F), decodeAnchorY(0.0F, 0.0F), decodeX(2.2857144F), decodeY(0.0F));
    path.curveTo(decodeAnchorX(2.2857144F, 3.0F), decodeAnchorY(0.0F, 0.0F), decodeAnchorX(3.0F, 0.0F), decodeAnchorY(0.71428573F, -3.0F), decodeX(3.0F), decodeY(0.71428573F));
    path.curveTo(decodeAnchorX(3.0F, 0.0F), decodeAnchorY(0.71428573F, 3.0F), decodeAnchorX(3.0F, 0.0F), decodeAnchorY(3.0F, 0.0F), decodeX(3.0F), decodeY(3.0F));
    path.lineTo(decodeX(0.0F), decodeY(3.0F));
    path.curveTo(decodeAnchorX(0.0F, 0.0F), decodeAnchorY(3.0F, 0.0F), decodeAnchorX(0.0F, 0.0F), decodeAnchorY(0.71428573F, 3.0F), decodeX(0.0F), decodeY(0.71428573F));
    path.closePath();
    return path;
  }
  
  private Path2D decodePath2()
  {
    path.reset();
    path.moveTo(decodeX(0.14285715F), decodeY(2.0F));
    path.curveTo(decodeAnchorX(0.14285715F, 0.0F), decodeAnchorY(2.0F, 0.0F), decodeAnchorX(0.14285715F, 0.0F), decodeAnchorY(0.85714287F, 3.5555556F), decodeX(0.14285715F), decodeY(0.85714287F));
    path.curveTo(decodeAnchorX(0.14285715F, 0.0F), decodeAnchorY(0.85714287F, -3.5555556F), decodeAnchorX(0.85714287F, -3.4444444F), decodeAnchorY(0.14285715F, 0.0F), decodeX(0.85714287F), decodeY(0.14285715F));
    path.curveTo(decodeAnchorX(0.85714287F, 3.4444444F), decodeAnchorY(0.14285715F, 0.0F), decodeAnchorX(2.142857F, -3.3333333F), decodeAnchorY(0.14285715F, 0.0F), decodeX(2.142857F), decodeY(0.14285715F));
    path.curveTo(decodeAnchorX(2.142857F, 3.3333333F), decodeAnchorY(0.14285715F, 0.0F), decodeAnchorX(2.857143F, 0.0F), decodeAnchorY(0.85714287F, -3.2777777F), decodeX(2.857143F), decodeY(0.85714287F));
    path.curveTo(decodeAnchorX(2.857143F, 0.0F), decodeAnchorY(0.85714287F, 3.2777777F), decodeAnchorX(2.857143F, 0.0F), decodeAnchorY(2.0F, 0.0F), decodeX(2.857143F), decodeY(2.0F));
    path.lineTo(decodeX(0.14285715F), decodeY(2.0F));
    path.closePath();
    return path;
  }
  
  private Path2D decodePath3()
  {
    path.reset();
    path.moveTo(decodeX(0.0F), decodeY(0.71428573F));
    path.curveTo(decodeAnchorX(0.0F, 0.055555556F), decodeAnchorY(0.71428573F, 2.6111112F), decodeAnchorX(0.8333333F, -2.5F), decodeAnchorY(0.0F, 0.0F), decodeX(0.8333333F), decodeY(0.0F));
    path.curveTo(decodeAnchorX(0.8333333F, 2.5F), decodeAnchorY(0.0F, 0.0F), decodeAnchorX(2.2857144F, -2.7222223F), decodeAnchorY(0.0F, 0.0F), decodeX(2.2857144F), decodeY(0.0F));
    path.curveTo(decodeAnchorX(2.2857144F, 2.7222223F), decodeAnchorY(0.0F, 0.0F), decodeAnchorX(3.0F, -0.055555556F), decodeAnchorY(0.71428573F, -2.7222223F), decodeX(3.0F), decodeY(0.71428573F));
    path.curveTo(decodeAnchorX(3.0F, 0.055555556F), decodeAnchorY(0.71428573F, 2.7222223F), decodeAnchorX(3.0F, 0.0F), decodeAnchorY(3.0F, 0.0F), decodeX(3.0F), decodeY(3.0F));
    path.lineTo(decodeX(0.0F), decodeY(3.0F));
    path.curveTo(decodeAnchorX(0.0F, 0.0F), decodeAnchorY(3.0F, 0.0F), decodeAnchorX(0.0F, -0.055555556F), decodeAnchorY(0.71428573F, -2.6111112F), decodeX(0.0F), decodeY(0.71428573F));
    path.closePath();
    return path;
  }
  
  private Path2D decodePath4()
  {
    path.reset();
    path.moveTo(decodeX(0.16666667F), decodeY(2.0F));
    path.curveTo(decodeAnchorX(0.16666667F, 0.0F), decodeAnchorY(2.0F, 0.0F), decodeAnchorX(0.16666667F, 0.0F), decodeAnchorY(0.85714287F, 3.6666667F), decodeX(0.16666667F), decodeY(0.85714287F));
    path.curveTo(decodeAnchorX(0.16666667F, 0.0F), decodeAnchorY(0.85714287F, -3.6666667F), decodeAnchorX(1.0F, -3.5555556F), decodeAnchorY(0.14285715F, 0.0F), decodeX(1.0F), decodeY(0.14285715F));
    path.curveTo(decodeAnchorX(1.0F, 3.5555556F), decodeAnchorY(0.14285715F, 0.0F), decodeAnchorX(2.142857F, -3.5F), decodeAnchorY(0.14285715F, 0.055555556F), decodeX(2.142857F), decodeY(0.14285715F));
    path.curveTo(decodeAnchorX(2.142857F, 3.5F), decodeAnchorY(0.14285715F, -0.055555556F), decodeAnchorX(2.857143F, 0.055555556F), decodeAnchorY(0.85714287F, -3.6666667F), decodeX(2.857143F), decodeY(0.85714287F));
    path.curveTo(decodeAnchorX(2.857143F, -0.055555556F), decodeAnchorY(0.85714287F, 3.6666667F), decodeAnchorX(2.857143F, 0.0F), decodeAnchorY(2.0F, 0.0F), decodeX(2.857143F), decodeY(2.0F));
    path.lineTo(decodeX(0.16666667F), decodeY(2.0F));
    path.closePath();
    return path;
  }
  
  private Path2D decodePath5()
  {
    path.reset();
    path.moveTo(decodeX(0.0F), decodeY(0.8333333F));
    path.curveTo(decodeAnchorX(0.0F, 0.0F), decodeAnchorY(0.8333333F, -3.0F), decodeAnchorX(0.71428573F, -3.0F), decodeAnchorY(0.0F, 0.0F), decodeX(0.71428573F), decodeY(0.0F));
    path.curveTo(decodeAnchorX(0.71428573F, 3.0F), decodeAnchorY(0.0F, 0.0F), decodeAnchorX(2.2857144F, -3.0F), decodeAnchorY(0.0F, 0.0F), decodeX(2.2857144F), decodeY(0.0F));
    path.curveTo(decodeAnchorX(2.2857144F, 3.0F), decodeAnchorY(0.0F, 0.0F), decodeAnchorX(3.0F, 0.0F), decodeAnchorY(0.8333333F, -3.0F), decodeX(3.0F), decodeY(0.8333333F));
    path.curveTo(decodeAnchorX(3.0F, 0.0F), decodeAnchorY(0.8333333F, 3.0F), decodeAnchorX(3.0F, 0.0F), decodeAnchorY(3.0F, 0.0F), decodeX(3.0F), decodeY(3.0F));
    path.lineTo(decodeX(0.0F), decodeY(3.0F));
    path.curveTo(decodeAnchorX(0.0F, 0.0F), decodeAnchorY(3.0F, 0.0F), decodeAnchorX(0.0F, 0.0F), decodeAnchorY(0.8333333F, 3.0F), decodeX(0.0F), decodeY(0.8333333F));
    path.closePath();
    return path;
  }
  
  private Path2D decodePath6()
  {
    path.reset();
    path.moveTo(decodeX(0.14285715F), decodeY(2.0F));
    path.curveTo(decodeAnchorX(0.14285715F, 0.0F), decodeAnchorY(2.0F, 0.0F), decodeAnchorX(0.14285715F, 0.0F), decodeAnchorY(1.0F, 3.5555556F), decodeX(0.14285715F), decodeY(1.0F));
    path.curveTo(decodeAnchorX(0.14285715F, 0.0F), decodeAnchorY(1.0F, -3.5555556F), decodeAnchorX(0.85714287F, -3.4444444F), decodeAnchorY(0.16666667F, 0.0F), decodeX(0.85714287F), decodeY(0.16666667F));
    path.curveTo(decodeAnchorX(0.85714287F, 3.4444444F), decodeAnchorY(0.16666667F, 0.0F), decodeAnchorX(2.142857F, -3.3333333F), decodeAnchorY(0.16666667F, 0.0F), decodeX(2.142857F), decodeY(0.16666667F));
    path.curveTo(decodeAnchorX(2.142857F, 3.3333333F), decodeAnchorY(0.16666667F, 0.0F), decodeAnchorX(2.857143F, 0.0F), decodeAnchorY(1.0F, -3.2777777F), decodeX(2.857143F), decodeY(1.0F));
    path.curveTo(decodeAnchorX(2.857143F, 0.0F), decodeAnchorY(1.0F, 3.2777777F), decodeAnchorX(2.857143F, 0.0F), decodeAnchorY(2.0F, 0.0F), decodeX(2.857143F), decodeY(2.0F));
    path.lineTo(decodeX(0.14285715F), decodeY(2.0F));
    path.closePath();
    return path;
  }
  
  private Path2D decodePath7()
  {
    path.reset();
    path.moveTo(decodeX(0.0F), decodeY(0.71428573F));
    path.curveTo(decodeAnchorX(0.0F, 0.0F), decodeAnchorY(0.71428573F, -3.0F), decodeAnchorX(0.71428573F, -3.0F), decodeAnchorY(0.0F, 0.0F), decodeX(0.71428573F), decodeY(0.0F));
    path.curveTo(decodeAnchorX(0.71428573F, 3.0F), decodeAnchorY(0.0F, 0.0F), decodeAnchorX(2.2857144F, -3.0F), decodeAnchorY(0.0F, 0.0F), decodeX(2.2857144F), decodeY(0.0F));
    path.curveTo(decodeAnchorX(2.2857144F, 3.0F), decodeAnchorY(0.0F, 0.0F), decodeAnchorX(3.0F, 0.0F), decodeAnchorY(0.71428573F, -3.0F), decodeX(3.0F), decodeY(0.71428573F));
    path.curveTo(decodeAnchorX(3.0F, 0.0F), decodeAnchorY(0.71428573F, 3.0F), decodeAnchorX(3.0F, 0.0F), decodeAnchorY(2.0F, 0.0F), decodeX(3.0F), decodeY(2.0F));
    path.lineTo(decodeX(0.0F), decodeY(2.0F));
    path.curveTo(decodeAnchorX(0.0F, 0.0F), decodeAnchorY(2.0F, 0.0F), decodeAnchorX(0.0F, 0.0F), decodeAnchorY(0.71428573F, 3.0F), decodeX(0.0F), decodeY(0.71428573F));
    path.closePath();
    return path;
  }
  
  private Path2D decodePath8()
  {
    path.reset();
    path.moveTo(decodeX(0.0F), decodeY(0.71428573F));
    path.curveTo(decodeAnchorX(0.0F, 0.0F), decodeAnchorY(0.71428573F, -3.0F), decodeAnchorX(0.5555556F, -3.0F), decodeAnchorY(0.0F, 0.0F), decodeX(0.5555556F), decodeY(0.0F));
    path.curveTo(decodeAnchorX(0.5555556F, 3.0F), decodeAnchorY(0.0F, 0.0F), decodeAnchorX(2.4444444F, -3.0F), decodeAnchorY(0.0F, 0.0F), decodeX(2.4444444F), decodeY(0.0F));
    path.curveTo(decodeAnchorX(2.4444444F, 3.0F), decodeAnchorY(0.0F, 0.0F), decodeAnchorX(3.0F, 0.0F), decodeAnchorY(0.71428573F, -3.0F), decodeX(3.0F), decodeY(0.71428573F));
    path.curveTo(decodeAnchorX(3.0F, 0.0F), decodeAnchorY(0.71428573F, 3.0F), decodeAnchorX(3.0F, 0.0F), decodeAnchorY(2.0F, 0.0F), decodeX(3.0F), decodeY(2.0F));
    path.lineTo(decodeX(0.0F), decodeY(2.0F));
    path.curveTo(decodeAnchorX(0.0F, 0.0F), decodeAnchorY(2.0F, 0.0F), decodeAnchorX(0.0F, 0.0F), decodeAnchorY(0.71428573F, 3.0F), decodeX(0.0F), decodeY(0.71428573F));
    path.closePath();
    return path;
  }
  
  private Path2D decodePath9()
  {
    path.reset();
    path.moveTo(decodeX(0.11111111F), decodeY(2.0F));
    path.curveTo(decodeAnchorX(0.11111111F, 0.0F), decodeAnchorY(2.0F, 0.0F), decodeAnchorX(0.11111111F, 0.0F), decodeAnchorY(0.85714287F, 3.5555556F), decodeX(0.11111111F), decodeY(0.85714287F));
    path.curveTo(decodeAnchorX(0.11111111F, 0.0F), decodeAnchorY(0.85714287F, -3.5555556F), decodeAnchorX(0.6666667F, -3.4444444F), decodeAnchorY(0.14285715F, 0.0F), decodeX(0.6666667F), decodeY(0.14285715F));
    path.curveTo(decodeAnchorX(0.6666667F, 3.4444444F), decodeAnchorY(0.14285715F, 0.0F), decodeAnchorX(2.3333333F, -3.3333333F), decodeAnchorY(0.14285715F, 0.0F), decodeX(2.3333333F), decodeY(0.14285715F));
    path.curveTo(decodeAnchorX(2.3333333F, 3.3333333F), decodeAnchorY(0.14285715F, 0.0F), decodeAnchorX(2.8888888F, 0.0F), decodeAnchorY(0.85714287F, -3.2777777F), decodeX(2.8888888F), decodeY(0.85714287F));
    path.curveTo(decodeAnchorX(2.8888888F, 0.0F), decodeAnchorY(0.85714287F, 3.2777777F), decodeAnchorX(2.8888888F, 0.0F), decodeAnchorY(2.0F, 0.0F), decodeX(2.8888888F), decodeY(2.0F));
    path.lineTo(decodeX(0.11111111F), decodeY(2.0F));
    path.closePath();
    return path;
  }
  
  private Path2D decodePath10()
  {
    path.reset();
    path.moveTo(decodeX(0.14285715F), decodeY(3.0F));
    path.curveTo(decodeAnchorX(0.14285715F, 0.0F), decodeAnchorY(3.0F, 0.0F), decodeAnchorX(0.14285715F, 0.0F), decodeAnchorY(0.85714287F, 3.5555556F), decodeX(0.14285715F), decodeY(0.85714287F));
    path.curveTo(decodeAnchorX(0.14285715F, 0.0F), decodeAnchorY(0.85714287F, -3.5555556F), decodeAnchorX(0.85714287F, -3.4444444F), decodeAnchorY(0.14285715F, 0.0F), decodeX(0.85714287F), decodeY(0.14285715F));
    path.curveTo(decodeAnchorX(0.85714287F, 3.4444444F), decodeAnchorY(0.14285715F, 0.0F), decodeAnchorX(2.142857F, -3.3333333F), decodeAnchorY(0.14285715F, 0.0F), decodeX(2.142857F), decodeY(0.14285715F));
    path.curveTo(decodeAnchorX(2.142857F, 3.3333333F), decodeAnchorY(0.14285715F, 0.0F), decodeAnchorX(2.857143F, 0.0F), decodeAnchorY(0.85714287F, -3.2777777F), decodeX(2.857143F), decodeY(0.85714287F));
    path.curveTo(decodeAnchorX(2.857143F, 0.0F), decodeAnchorY(0.85714287F, 3.2777777F), decodeAnchorX(2.857143F, 0.0F), decodeAnchorY(3.0F, 0.0F), decodeX(2.857143F), decodeY(3.0F));
    path.lineTo(decodeX(0.14285715F), decodeY(3.0F));
    path.closePath();
    return path;
  }
  
  private Path2D decodePath11()
  {
    path.reset();
    path.moveTo(decodeX(1.4638889F), decodeY(2.25F));
    path.lineTo(decodeX(1.4652778F), decodeY(2.777778F));
    path.lineTo(decodeX(0.3809524F), decodeY(2.777778F));
    path.lineTo(decodeX(0.375F), decodeY(0.88095236F));
    path.curveTo(decodeAnchorX(0.375F, 0.0F), decodeAnchorY(0.88095236F, -2.25F), decodeAnchorX(0.8452381F, -1.9166666F), decodeAnchorY(0.3809524F, 0.0F), decodeX(0.8452381F), decodeY(0.3809524F));
    path.lineTo(decodeX(2.1011903F), decodeY(0.3809524F));
    path.curveTo(decodeAnchorX(2.1011903F, 2.125F), decodeAnchorY(0.3809524F, 0.0F), decodeAnchorX(2.6309526F, 0.0F), decodeAnchorY(0.8630952F, -2.5833333F), decodeX(2.6309526F), decodeY(0.8630952F));
    path.lineTo(decodeX(2.625F), decodeY(2.7638886F));
    path.lineTo(decodeX(1.4666667F), decodeY(2.777778F));
    path.lineTo(decodeX(1.4638889F), decodeY(2.2361114F));
    path.lineTo(decodeX(2.3869045F), decodeY(2.222222F));
    path.lineTo(decodeX(2.375F), decodeY(0.86904764F));
    path.curveTo(decodeAnchorX(2.375F, -7.1054274E-15F), decodeAnchorY(0.86904764F, -0.9166667F), decodeAnchorX(2.0952382F, 1.0833334F), decodeAnchorY(0.60714287F, -1.7763568E-15F), decodeX(2.0952382F), decodeY(0.60714287F));
    path.lineTo(decodeX(0.8333334F), decodeY(0.6130952F));
    path.curveTo(decodeAnchorX(0.8333334F, -1.0F), decodeAnchorY(0.6130952F, 0.0F), decodeAnchorX(0.625F, 0.041666668F), decodeAnchorY(0.86904764F, -0.9583333F), decodeX(0.625F), decodeY(0.86904764F));
    path.lineTo(decodeX(0.6130952F), decodeY(2.2361114F));
    path.lineTo(decodeX(1.4638889F), decodeY(2.25F));
    path.closePath();
    return path;
  }
  
  private Path2D decodePath12()
  {
    path.reset();
    path.moveTo(decodeX(0.0F), decodeY(0.71428573F));
    path.curveTo(decodeAnchorX(0.0F, 0.0F), decodeAnchorY(0.71428573F, -3.0F), decodeAnchorX(0.5555556F, -3.0F), decodeAnchorY(0.0F, 0.0F), decodeX(0.5555556F), decodeY(0.0F));
    path.curveTo(decodeAnchorX(0.5555556F, 3.0F), decodeAnchorY(0.0F, 0.0F), decodeAnchorX(2.4444444F, -3.0F), decodeAnchorY(0.0F, 0.0F), decodeX(2.4444444F), decodeY(0.0F));
    path.curveTo(decodeAnchorX(2.4444444F, 3.0F), decodeAnchorY(0.0F, 0.0F), decodeAnchorX(3.0F, 0.0F), decodeAnchorY(0.71428573F, -3.0F), decodeX(3.0F), decodeY(0.71428573F));
    path.curveTo(decodeAnchorX(3.0F, 0.0F), decodeAnchorY(0.71428573F, 3.0F), decodeAnchorX(3.0F, 0.0F), decodeAnchorY(3.0F, 0.0F), decodeX(3.0F), decodeY(3.0F));
    path.lineTo(decodeX(0.0F), decodeY(3.0F));
    path.curveTo(decodeAnchorX(0.0F, 0.0F), decodeAnchorY(3.0F, 0.0F), decodeAnchorX(0.0F, 0.0F), decodeAnchorY(0.71428573F, 3.0F), decodeX(0.0F), decodeY(0.71428573F));
    path.closePath();
    return path;
  }
  
  private Path2D decodePath13()
  {
    path.reset();
    path.moveTo(decodeX(0.11111111F), decodeY(3.0F));
    path.curveTo(decodeAnchorX(0.11111111F, 0.0F), decodeAnchorY(3.0F, 0.0F), decodeAnchorX(0.11111111F, 0.0F), decodeAnchorY(0.85714287F, 3.5555556F), decodeX(0.11111111F), decodeY(0.85714287F));
    path.curveTo(decodeAnchorX(0.11111111F, 0.0F), decodeAnchorY(0.85714287F, -3.5555556F), decodeAnchorX(0.6666667F, -3.4444444F), decodeAnchorY(0.14285715F, 0.0F), decodeX(0.6666667F), decodeY(0.14285715F));
    path.curveTo(decodeAnchorX(0.6666667F, 3.4444444F), decodeAnchorY(0.14285715F, 0.0F), decodeAnchorX(2.3333333F, -3.3333333F), decodeAnchorY(0.14285715F, 0.0F), decodeX(2.3333333F), decodeY(0.14285715F));
    path.curveTo(decodeAnchorX(2.3333333F, 3.3333333F), decodeAnchorY(0.14285715F, 0.0F), decodeAnchorX(2.8888888F, 0.0F), decodeAnchorY(0.85714287F, -3.2777777F), decodeX(2.8888888F), decodeY(0.85714287F));
    path.curveTo(decodeAnchorX(2.8888888F, 0.0F), decodeAnchorY(0.85714287F, 3.2777777F), decodeAnchorX(2.8888888F, 0.0F), decodeAnchorY(3.0F, 0.0F), decodeX(2.8888888F), decodeY(3.0F));
    path.lineTo(decodeX(0.11111111F), decodeY(3.0F));
    path.closePath();
    return path;
  }
  
  private Path2D decodePath14()
  {
    path.reset();
    path.moveTo(decodeX(1.4583333F), decodeY(2.25F));
    path.lineTo(decodeX(1.4599359F), decodeY(2.777778F));
    path.lineTo(decodeX(0.2962963F), decodeY(2.777778F));
    path.lineTo(decodeX(0.29166666F), decodeY(0.88095236F));
    path.curveTo(decodeAnchorX(0.29166666F, 0.0F), decodeAnchorY(0.88095236F, -2.25F), decodeAnchorX(0.6574074F, -1.9166666F), decodeAnchorY(0.3809524F, 0.0F), decodeX(0.6574074F), decodeY(0.3809524F));
    path.lineTo(decodeX(2.3009257F), decodeY(0.3809524F));
    path.curveTo(decodeAnchorX(2.3009257F, 2.125F), decodeAnchorY(0.3809524F, 0.0F), decodeAnchorX(2.712963F, 0.0F), decodeAnchorY(0.8630952F, -2.5833333F), decodeX(2.712963F), decodeY(0.8630952F));
    path.lineTo(decodeX(2.7083333F), decodeY(2.7638886F));
    path.lineTo(decodeX(1.4615384F), decodeY(2.777778F));
    path.lineTo(decodeX(1.4583333F), decodeY(2.2361114F));
    path.lineTo(decodeX(2.523148F), decodeY(2.222222F));
    path.lineTo(decodeX(2.5138888F), decodeY(0.86904764F));
    path.curveTo(decodeAnchorX(2.5138888F, -7.1054274E-15F), decodeAnchorY(0.86904764F, -0.9166667F), decodeAnchorX(2.2962964F, 1.0833334F), decodeAnchorY(0.60714287F, -1.7763568E-15F), decodeX(2.2962964F), decodeY(0.60714287F));
    path.lineTo(decodeX(0.6481482F), decodeY(0.6130952F));
    path.curveTo(decodeAnchorX(0.6481482F, -1.0F), decodeAnchorY(0.6130952F, 0.0F), decodeAnchorX(0.4861111F, 0.041666668F), decodeAnchorY(0.86904764F, -0.9583333F), decodeX(0.4861111F), decodeY(0.86904764F));
    path.lineTo(decodeX(0.47685182F), decodeY(2.2361114F));
    path.lineTo(decodeX(1.4583333F), decodeY(2.25F));
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
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.1F, 0.2F, 0.6F, 1.0F }, new Color[] { color3, decodeColor(color3, color4, 0.5F), color4, decodeColor(color4, color5, 0.5F), color5 });
  }
  
  private Paint decodeGradient3(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color6, decodeColor(color6, color2, 0.5F), color2 });
  }
  
  private Paint decodeGradient4(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.1F, 0.2F, 0.6F, 1.0F }, new Color[] { color7, decodeColor(color7, color8, 0.5F), color8, decodeColor(color8, color9, 0.5F), color9 });
  }
  
  private Paint decodeGradient5(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color10, decodeColor(color10, color2, 0.5F), color2 });
  }
  
  private Paint decodeGradient6(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.1F, 0.2F, 0.42096776F, 0.64193547F, 0.82096773F, 1.0F }, new Color[] { color11, decodeColor(color11, color12, 0.5F), color12, decodeColor(color12, color13, 0.5F), color13, decodeColor(color13, color14, 0.5F), color14 });
  }
  
  private Paint decodeGradient7(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color15, decodeColor(color15, color16, 0.5F), color16 });
  }
  
  private Paint decodeGradient8(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.1F, 0.2F, 0.6F, 1.0F }, new Color[] { color17, decodeColor(color17, color18, 0.5F), color18, decodeColor(color18, color5, 0.5F), color5 });
  }
  
  private Paint decodeGradient9(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.12419355F, 0.2483871F, 0.42580646F, 0.6032258F, 0.6854839F, 0.7677419F, 0.88387096F, 1.0F }, new Color[] { color19, decodeColor(color19, color20, 0.5F), color20, decodeColor(color20, color21, 0.5F), color21, decodeColor(color21, color22, 0.5F), color22, decodeColor(color22, color23, 0.5F), color23 });
  }
  
  private Paint decodeGradient10(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color24, decodeColor(color24, color2, 0.5F), color2 });
  }
  
  private Paint decodeGradient11(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color25, decodeColor(color25, color2, 0.5F), color2 });
  }
  
  private Paint decodeGradient12(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.12419355F, 0.2483871F, 0.42580646F, 0.6032258F, 0.6854839F, 0.7677419F, 0.86774194F, 0.9677419F }, new Color[] { color26, decodeColor(color26, color27, 0.5F), color27, decodeColor(color27, color28, 0.5F), color28, decodeColor(color28, color29, 0.5F), color29, decodeColor(color29, color30, 0.5F), color30 });
  }
  
  private Paint decodeGradient13(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color25, decodeColor(color25, color31, 0.5F), color31 });
  }
  
  private Paint decodeGradient14(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.12419355F, 0.2483871F, 0.42580646F, 0.6032258F, 0.6854839F, 0.7677419F, 0.8548387F, 0.9419355F }, new Color[] { color32, decodeColor(color32, color33, 0.5F), color33, decodeColor(color33, color34, 0.5F), color34, decodeColor(color34, color35, 0.5F), color35, decodeColor(color35, color36, 0.5F), color36 });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\nimbus\TabbedPaneTabPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */