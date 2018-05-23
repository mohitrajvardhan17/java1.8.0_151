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

final class SliderThumbPainter
  extends AbstractRegionPainter
{
  static final int BACKGROUND_DISABLED = 1;
  static final int BACKGROUND_ENABLED = 2;
  static final int BACKGROUND_FOCUSED = 3;
  static final int BACKGROUND_FOCUSED_MOUSEOVER = 4;
  static final int BACKGROUND_FOCUSED_PRESSED = 5;
  static final int BACKGROUND_MOUSEOVER = 6;
  static final int BACKGROUND_PRESSED = 7;
  static final int BACKGROUND_ENABLED_ARROWSHAPE = 8;
  static final int BACKGROUND_DISABLED_ARROWSHAPE = 9;
  static final int BACKGROUND_MOUSEOVER_ARROWSHAPE = 10;
  static final int BACKGROUND_PRESSED_ARROWSHAPE = 11;
  static final int BACKGROUND_FOCUSED_ARROWSHAPE = 12;
  static final int BACKGROUND_FOCUSED_MOUSEOVER_ARROWSHAPE = 13;
  static final int BACKGROUND_FOCUSED_PRESSED_ARROWSHAPE = 14;
  private int state;
  private AbstractRegionPainter.PaintContext ctx;
  private Path2D path = new Path2D.Float();
  private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
  private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  private Color color1 = decodeColor("nimbusBase", 0.021348298F, -0.5625436F, 0.25490195F, 0);
  private Color color2 = decodeColor("nimbusBase", 0.015098333F, -0.55105823F, 0.19215685F, 0);
  private Color color3 = decodeColor("nimbusBase", 0.021348298F, -0.5924243F, 0.35686272F, 0);
  private Color color4 = decodeColor("nimbusBase", 0.021348298F, -0.56722116F, 0.3098039F, 0);
  private Color color5 = decodeColor("nimbusBase", 0.021348298F, -0.56844974F, 0.32549018F, 0);
  private Color color6 = decodeColor("nimbusBlueGrey", -0.003968239F, 0.0014736876F, -0.25490198F, 65380);
  private Color color7 = decodeColor("nimbusBase", 5.1498413E-4F, -0.34585923F, -0.007843137F, 0);
  private Color color8 = decodeColor("nimbusBase", -0.0017285943F, -0.11571431F, -0.25490198F, 0);
  private Color color9 = decodeColor("nimbusBase", -0.023096085F, -0.6238095F, 0.43921566F, 0);
  private Color color10 = decodeColor("nimbusBase", 5.1498413E-4F, -0.43866998F, 0.24705881F, 0);
  private Color color11 = decodeColor("nimbusBase", 5.1498413E-4F, -0.45714286F, 0.32941175F, 0);
  private Color color12 = decodeColor("nimbusFocus", 0.0F, 0.0F, 0.0F, 0);
  private Color color13 = decodeColor("nimbusBase", -0.0038217902F, -0.15532213F, -0.14901963F, 0);
  private Color color14 = decodeColor("nimbusBase", -0.57865167F, -0.6357143F, -0.54509807F, 0);
  private Color color15 = decodeColor("nimbusBase", 0.004681647F, -0.62780917F, 0.44313723F, 0);
  private Color color16 = decodeColor("nimbusBase", 2.9569864E-4F, -0.4653107F, 0.32549018F, 0);
  private Color color17 = decodeColor("nimbusBase", 5.1498413E-4F, -0.4563421F, 0.32549018F, 0);
  private Color color18 = decodeColor("nimbusBase", -0.0017285943F, -0.4732143F, 0.39215684F, 0);
  private Color color19 = decodeColor("nimbusBase", 0.0015952587F, -0.04875779F, -0.18823531F, 0);
  private Color color20 = decodeColor("nimbusBase", 2.9569864E-4F, -0.44943976F, 0.25098038F, 0);
  private Color color21 = decodeColor("nimbusBase", 0.0F, 0.0F, 0.0F, 0);
  private Color color22 = decodeColor("nimbusBase", 8.9377165E-4F, -0.121094406F, 0.12156862F, 0);
  private Color color23 = decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, -121);
  private Color color24 = new Color(150, 156, 168, 146);
  private Color color25 = decodeColor("nimbusBase", -0.0033828616F, -0.40608466F, -0.019607842F, 0);
  private Color color26 = decodeColor("nimbusBase", 5.1498413E-4F, -0.17594418F, -0.20784315F, 0);
  private Color color27 = decodeColor("nimbusBase", 0.0023007393F, -0.11332625F, -0.28627452F, 0);
  private Color color28 = decodeColor("nimbusBase", -0.023096085F, -0.62376213F, 0.4352941F, 0);
  private Color color29 = decodeColor("nimbusBase", 0.004681647F, -0.594392F, 0.39999998F, 0);
  private Color color30 = decodeColor("nimbusBase", -0.0017285943F, -0.4454704F, 0.25490195F, 0);
  private Color color31 = decodeColor("nimbusBase", 5.1498413E-4F, -0.4625541F, 0.35686272F, 0);
  private Color color32 = decodeColor("nimbusBase", 5.1498413E-4F, -0.47442397F, 0.4235294F, 0);
  private Object[] componentColors;
  
  public SliderThumbPainter(AbstractRegionPainter.PaintContext paramPaintContext, int paramInt)
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
      paintBackgroundFocusedAndMouseOver(paramGraphics2D);
      break;
    case 5: 
      paintBackgroundFocusedAndPressed(paramGraphics2D);
      break;
    case 6: 
      paintBackgroundMouseOver(paramGraphics2D);
      break;
    case 7: 
      paintBackgroundPressed(paramGraphics2D);
      break;
    case 8: 
      paintBackgroundEnabledAndArrowShape(paramGraphics2D);
      break;
    case 9: 
      paintBackgroundDisabledAndArrowShape(paramGraphics2D);
      break;
    case 10: 
      paintBackgroundMouseOverAndArrowShape(paramGraphics2D);
      break;
    case 11: 
      paintBackgroundPressedAndArrowShape(paramGraphics2D);
      break;
    case 12: 
      paintBackgroundFocusedAndArrowShape(paramGraphics2D);
      break;
    case 13: 
      paintBackgroundFocusedAndMouseOverAndArrowShape(paramGraphics2D);
      break;
    case 14: 
      paintBackgroundFocusedAndPressedAndArrowShape(paramGraphics2D);
    }
  }
  
  protected final AbstractRegionPainter.PaintContext getPaintContext()
  {
    return ctx;
  }
  
  private void paintBackgroundDisabled(Graphics2D paramGraphics2D)
  {
    ellipse = decodeEllipse1();
    paramGraphics2D.setPaint(decodeGradient1(ellipse));
    paramGraphics2D.fill(ellipse);
    ellipse = decodeEllipse2();
    paramGraphics2D.setPaint(decodeGradient2(ellipse));
    paramGraphics2D.fill(ellipse);
  }
  
  private void paintBackgroundEnabled(Graphics2D paramGraphics2D)
  {
    ellipse = decodeEllipse3();
    paramGraphics2D.setPaint(color6);
    paramGraphics2D.fill(ellipse);
    ellipse = decodeEllipse1();
    paramGraphics2D.setPaint(decodeGradient3(ellipse));
    paramGraphics2D.fill(ellipse);
    ellipse = decodeEllipse2();
    paramGraphics2D.setPaint(decodeGradient4(ellipse));
    paramGraphics2D.fill(ellipse);
  }
  
  private void paintBackgroundFocused(Graphics2D paramGraphics2D)
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
  
  private void paintBackgroundFocusedAndMouseOver(Graphics2D paramGraphics2D)
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
  
  private void paintBackgroundFocusedAndPressed(Graphics2D paramGraphics2D)
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
  
  private void paintBackgroundMouseOver(Graphics2D paramGraphics2D)
  {
    ellipse = decodeEllipse3();
    paramGraphics2D.setPaint(color6);
    paramGraphics2D.fill(ellipse);
    ellipse = decodeEllipse1();
    paramGraphics2D.setPaint(decodeGradient5(ellipse));
    paramGraphics2D.fill(ellipse);
    ellipse = decodeEllipse2();
    paramGraphics2D.setPaint(decodeGradient6(ellipse));
    paramGraphics2D.fill(ellipse);
  }
  
  private void paintBackgroundPressed(Graphics2D paramGraphics2D)
  {
    ellipse = decodeEllipse3();
    paramGraphics2D.setPaint(color23);
    paramGraphics2D.fill(ellipse);
    ellipse = decodeEllipse1();
    paramGraphics2D.setPaint(decodeGradient7(ellipse));
    paramGraphics2D.fill(ellipse);
    ellipse = decodeEllipse2();
    paramGraphics2D.setPaint(decodeGradient8(ellipse));
    paramGraphics2D.fill(ellipse);
  }
  
  private void paintBackgroundEnabledAndArrowShape(Graphics2D paramGraphics2D)
  {
    path = decodePath1();
    paramGraphics2D.setPaint(color24);
    paramGraphics2D.fill(path);
    path = decodePath2();
    paramGraphics2D.setPaint(decodeGradient9(path));
    paramGraphics2D.fill(path);
    path = decodePath3();
    paramGraphics2D.setPaint(decodeGradient10(path));
    paramGraphics2D.fill(path);
  }
  
  private void paintBackgroundDisabledAndArrowShape(Graphics2D paramGraphics2D)
  {
    path = decodePath2();
    paramGraphics2D.setPaint(decodeGradient11(path));
    paramGraphics2D.fill(path);
    path = decodePath3();
    paramGraphics2D.setPaint(decodeGradient12(path));
    paramGraphics2D.fill(path);
  }
  
  private void paintBackgroundMouseOverAndArrowShape(Graphics2D paramGraphics2D)
  {
    path = decodePath1();
    paramGraphics2D.setPaint(color24);
    paramGraphics2D.fill(path);
    path = decodePath2();
    paramGraphics2D.setPaint(decodeGradient13(path));
    paramGraphics2D.fill(path);
    path = decodePath3();
    paramGraphics2D.setPaint(decodeGradient14(path));
    paramGraphics2D.fill(path);
  }
  
  private void paintBackgroundPressedAndArrowShape(Graphics2D paramGraphics2D)
  {
    path = decodePath1();
    paramGraphics2D.setPaint(color24);
    paramGraphics2D.fill(path);
    path = decodePath2();
    paramGraphics2D.setPaint(decodeGradient15(path));
    paramGraphics2D.fill(path);
    path = decodePath3();
    paramGraphics2D.setPaint(decodeGradient16(path));
    paramGraphics2D.fill(path);
  }
  
  private void paintBackgroundFocusedAndArrowShape(Graphics2D paramGraphics2D)
  {
    path = decodePath4();
    paramGraphics2D.setPaint(color12);
    paramGraphics2D.fill(path);
    path = decodePath2();
    paramGraphics2D.setPaint(decodeGradient9(path));
    paramGraphics2D.fill(path);
    path = decodePath3();
    paramGraphics2D.setPaint(decodeGradient17(path));
    paramGraphics2D.fill(path);
  }
  
  private void paintBackgroundFocusedAndMouseOverAndArrowShape(Graphics2D paramGraphics2D)
  {
    path = decodePath4();
    paramGraphics2D.setPaint(color12);
    paramGraphics2D.fill(path);
    path = decodePath2();
    paramGraphics2D.setPaint(decodeGradient13(path));
    paramGraphics2D.fill(path);
    path = decodePath3();
    paramGraphics2D.setPaint(decodeGradient14(path));
    paramGraphics2D.fill(path);
  }
  
  private void paintBackgroundFocusedAndPressedAndArrowShape(Graphics2D paramGraphics2D)
  {
    path = decodePath4();
    paramGraphics2D.setPaint(color12);
    paramGraphics2D.fill(path);
    path = decodePath2();
    paramGraphics2D.setPaint(decodeGradient15(path));
    paramGraphics2D.fill(path);
    path = decodePath3();
    paramGraphics2D.setPaint(decodeGradient16(path));
    paramGraphics2D.fill(path);
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
  
  private Path2D decodePath1()
  {
    path.reset();
    path.moveTo(decodeX(0.8166667F), decodeY(0.5007576F));
    path.curveTo(decodeAnchorX(0.8166667F, 1.5643269F), decodeAnchorY(0.5007576F, -0.3097513F), decodeAnchorX(2.7925456F, 0.058173586F), decodeAnchorY(1.6116884F, -0.4647635F), decodeX(2.7925456F), decodeY(1.6116884F));
    path.curveTo(decodeAnchorX(2.7925456F, -0.34086856F), decodeAnchorY(1.6116884F, 2.7232852F), decodeAnchorX(0.7006364F, 4.568128F), decodeAnchorY(2.7693636F, -0.006014915F), decodeX(0.7006364F), decodeY(2.7693636F));
    path.curveTo(decodeAnchorX(0.7006364F, -3.5233955F), decodeAnchorY(2.7693636F, 0.004639302F), decodeAnchorX(0.8166667F, -1.8635255F), decodeAnchorY(0.5007576F, 0.36899543F), decodeX(0.8166667F), decodeY(0.5007576F));
    path.closePath();
    return path;
  }
  
  private Path2D decodePath2()
  {
    path.reset();
    path.moveTo(decodeX(0.6155303F), decodeY(2.5954547F));
    path.curveTo(decodeAnchorX(0.6155303F, 0.90980893F), decodeAnchorY(2.5954547F, 1.3154242F), decodeAnchorX(2.6151516F, 0.014588808F), decodeAnchorY(1.6112013F, 0.9295521F), decodeX(2.6151516F), decodeY(1.6112013F));
    path.curveTo(decodeAnchorX(2.6151516F, -0.01365518F), decodeAnchorY(1.6112013F, -0.8700643F), decodeAnchorX(0.60923916F, 0.9729935F), decodeAnchorY(0.40716404F, -1.4248644F), decodeX(0.60923916F), decodeY(0.40716404F));
    path.curveTo(decodeAnchorX(0.60923916F, -0.7485209F), decodeAnchorY(0.40716404F, 1.0961438F), decodeAnchorX(0.6155303F, -0.74998796F), decodeAnchorY(2.5954547F, -1.0843511F), decodeX(0.6155303F), decodeY(2.5954547F));
    path.closePath();
    return path;
  }
  
  private Path2D decodePath3()
  {
    path.reset();
    path.moveTo(decodeX(0.8055606F), decodeY(0.6009697F));
    path.curveTo(decodeAnchorX(0.8055606F, 0.50820893F), decodeAnchorY(0.6009697F, -0.8490881F), decodeAnchorX(2.3692727F, 0.0031846066F), decodeAnchorY(1.613117F, -0.60668826F), decodeX(2.3692727F), decodeY(1.613117F));
    path.curveTo(decodeAnchorX(2.3692727F, -0.003890196F), decodeAnchorY(1.613117F, 0.74110764F), decodeAnchorX(0.7945455F, 0.3870974F), decodeAnchorY(2.3932729F, 1.240782F), decodeX(0.7945455F), decodeY(2.3932729F));
    path.curveTo(decodeAnchorX(0.7945455F, -0.38636583F), decodeAnchorY(2.3932729F, -1.2384372F), decodeAnchorX(0.8055606F, -0.995154F), decodeAnchorY(0.6009697F, 1.6626496F), decodeX(0.8055606F), decodeY(0.6009697F));
    path.closePath();
    return path;
  }
  
  private Path2D decodePath4()
  {
    path.reset();
    path.moveTo(decodeX(0.60059524F), decodeY(0.11727543F));
    path.curveTo(decodeAnchorX(0.60059524F, 1.5643269F), decodeAnchorY(0.11727543F, -0.3097513F), decodeAnchorX(2.7925456F, 0.004405844F), decodeAnchorY(1.6116884F, -1.1881162F), decodeX(2.7925456F), decodeY(1.6116884F));
    path.curveTo(decodeAnchorX(2.7925456F, -0.007364541F), decodeAnchorY(1.6116884F, 1.9859827F), decodeAnchorX(0.7006364F, 2.7716863F), decodeAnchorY(2.8693638F, -0.008974582F), decodeX(0.7006364F), decodeY(2.8693638F));
    path.curveTo(decodeAnchorX(0.7006364F, -3.754899F), decodeAnchorY(2.8693638F, 0.012158176F), decodeAnchorX(0.60059524F, -1.8635255F), decodeAnchorY(0.11727543F, 0.36899543F), decodeX(0.60059524F), decodeY(0.11727543F));
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
    return decodeGradient(0.5106101F * f3 + f1, -4.553649E-18F * f4 + f2, 0.49933687F * f3 + f1, 1.0039787F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color1, decodeColor(color1, color2, 0.5F), color2 });
  }
  
  private Paint decodeGradient2(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5023511F * f3 + f1, 0.0015673981F * f4 + f2, 0.5023511F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.21256684F, 0.42513368F, 0.71256685F, 1.0F }, new Color[] { color3, decodeColor(color3, color4, 0.5F), color4, decodeColor(color4, color5, 0.5F), color5 });
  }
  
  private Paint decodeGradient3(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.51F * f3 + f1, -4.553649E-18F * f4 + f2, 0.51F * f3 + f1, 1.0039787F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color7, decodeColor(color7, color8, 0.5F), color8 });
  }
  
  private Paint decodeGradient4(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0015673981F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.21256684F, 0.42513368F, 0.56149733F, 0.69786096F, 0.8489305F, 1.0F }, new Color[] { color9, decodeColor(color9, color10, 0.5F), color10, decodeColor(color10, color10, 0.5F), color10, decodeColor(color10, color11, 0.5F), color11 });
  }
  
  private Paint decodeGradient5(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5106101F * f3 + f1, -4.553649E-18F * f4 + f2, 0.49933687F * f3 + f1, 1.0039787F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color13, decodeColor(color13, color14, 0.5F), color14 });
  }
  
  private Paint decodeGradient6(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5023511F * f3 + f1, 0.0015673981F * f4 + f2, 0.5023511F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.21256684F, 0.42513368F, 0.56149733F, 0.69786096F, 0.8489305F, 1.0F }, new Color[] { color15, decodeColor(color15, color16, 0.5F), color16, decodeColor(color16, color17, 0.5F), color17, decodeColor(color17, color18, 0.5F), color18 });
  }
  
  private Paint decodeGradient7(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5106101F * f3 + f1, -4.553649E-18F * f4 + f2, 0.49933687F * f3 + f1, 1.0039787F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color14, decodeColor(color14, color19, 0.5F), color19 });
  }
  
  private Paint decodeGradient8(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5023511F * f3 + f1, 0.0015673981F * f4 + f2, 0.5023511F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.23796791F, 0.47593582F, 0.5360962F, 0.5962567F, 0.79812837F, 1.0F }, new Color[] { color20, decodeColor(color20, color21, 0.5F), color21, decodeColor(color21, color21, 0.5F), color21, decodeColor(color21, color22, 0.5F), color22 });
  }
  
  private Paint decodeGradient9(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.24032257F, 0.48064515F, 0.7403226F, 1.0F }, new Color[] { color25, decodeColor(color25, color26, 0.5F), color26, decodeColor(color26, color27, 0.5F), color27 });
  }
  
  private Paint decodeGradient10(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.061290324F, 0.1016129F, 0.14193548F, 0.3016129F, 0.46129033F, 0.5983871F, 0.7354839F, 0.7935484F, 0.8516129F }, new Color[] { color28, decodeColor(color28, color29, 0.5F), color29, decodeColor(color29, color30, 0.5F), color30, decodeColor(color30, color31, 0.5F), color31, decodeColor(color31, color32, 0.5F), color32 });
  }
  
  private Paint decodeGradient11(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color1, decodeColor(color1, color2, 0.5F), color2 });
  }
  
  private Paint decodeGradient12(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.21256684F, 0.42513368F, 0.71256685F, 1.0F }, new Color[] { color3, decodeColor(color3, color4, 0.5F), color4, decodeColor(color4, color5, 0.5F), color5 });
  }
  
  private Paint decodeGradient13(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color13, decodeColor(color13, color14, 0.5F), color14 });
  }
  
  private Paint decodeGradient14(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.21256684F, 0.42513368F, 0.56149733F, 0.69786096F, 0.8489305F, 1.0F }, new Color[] { color15, decodeColor(color15, color16, 0.5F), color16, decodeColor(color16, color17, 0.5F), color17, decodeColor(color17, color18, 0.5F), color18 });
  }
  
  private Paint decodeGradient15(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color14, decodeColor(color14, color19, 0.5F), color19 });
  }
  
  private Paint decodeGradient16(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.23796791F, 0.47593582F, 0.5360962F, 0.5962567F, 0.79812837F, 1.0F }, new Color[] { color20, decodeColor(color20, color21, 0.5F), color21, decodeColor(color21, color21, 0.5F), color21, decodeColor(color21, color22, 0.5F), color22 });
  }
  
  private Paint decodeGradient17(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.4925773F * f3 + f1, 0.082019866F * f4 + f2, 0.4925773F * f3 + f1, 0.91798013F * f4 + f2, new float[] { 0.061290324F, 0.1016129F, 0.14193548F, 0.3016129F, 0.46129033F, 0.5983871F, 0.7354839F, 0.7935484F, 0.8516129F }, new Color[] { color28, decodeColor(color28, color29, 0.5F), color29, decodeColor(color29, color30, 0.5F), color30, decodeColor(color30, color31, 0.5F), color31, decodeColor(color31, color32, 0.5F), color32 });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\nimbus\SliderThumbPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */