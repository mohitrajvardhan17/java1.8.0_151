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

final class InternalFrameTitlePaneCloseButtonPainter
  extends AbstractRegionPainter
{
  static final int BACKGROUND_DISABLED = 1;
  static final int BACKGROUND_ENABLED = 2;
  static final int BACKGROUND_MOUSEOVER = 3;
  static final int BACKGROUND_PRESSED = 4;
  static final int BACKGROUND_ENABLED_WINDOWNOTFOCUSED = 5;
  static final int BACKGROUND_MOUSEOVER_WINDOWNOTFOCUSED = 6;
  static final int BACKGROUND_PRESSED_WINDOWNOTFOCUSED = 7;
  private int state;
  private AbstractRegionPainter.PaintContext ctx;
  private Path2D path = new Path2D.Float();
  private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
  private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  private Color color1 = decodeColor("nimbusRed", 0.5893519F, -0.75736576F, 0.09411764F, 0);
  private Color color2 = decodeColor("nimbusRed", 0.5962963F, -0.71005917F, 0.0F, 0);
  private Color color3 = decodeColor("nimbusRed", 0.6005698F, -0.7200287F, -0.015686274F, -122);
  private Color color4 = decodeColor("nimbusBlueGrey", 0.0055555105F, -0.062449392F, 0.07058823F, 0);
  private Color color5 = decodeColor("nimbusBlueGrey", 0.0055555105F, -0.0029994324F, -0.38039216F, 65351);
  private Color color6 = decodeColor("nimbusRed", -0.014814814F, 0.20118344F, -0.4431373F, 0);
  private Color color7 = decodeColor("nimbusRed", -2.7342606E-4F, 0.13829035F, -0.039215684F, 0);
  private Color color8 = decodeColor("nimbusRed", 6.890595E-4F, -0.36665577F, 0.11764705F, 0);
  private Color color9 = decodeColor("nimbusRed", -0.001021713F, 0.101804554F, -0.031372547F, 0);
  private Color color10 = decodeColor("nimbusRed", -2.7342606E-4F, 0.13243341F, -0.035294116F, 0);
  private Color color11 = decodeColor("nimbusRed", -2.7342606E-4F, 0.002258718F, 0.06666666F, 0);
  private Color color12 = decodeColor("nimbusRed", 0.0056530247F, 0.0040003657F, -0.38431373F, -122);
  private Color color13 = decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, 0);
  private Color color14 = decodeColor("nimbusRed", -0.014814814F, 0.20118344F, -0.3882353F, 0);
  private Color color15 = decodeColor("nimbusRed", -0.014814814F, 0.20118344F, -0.13333333F, 0);
  private Color color16 = decodeColor("nimbusRed", 6.890595E-4F, -0.38929275F, 0.1607843F, 0);
  private Color color17 = decodeColor("nimbusRed", 2.537202E-5F, 0.012294531F, 0.043137252F, 0);
  private Color color18 = decodeColor("nimbusRed", -2.7342606E-4F, 0.033585668F, 0.039215684F, 0);
  private Color color19 = decodeColor("nimbusRed", -2.7342606E-4F, -0.07198727F, 0.14117646F, 0);
  private Color color20 = decodeColor("nimbusRed", -0.014814814F, 0.20118344F, 0.0039215684F, -122);
  private Color color21 = decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, 65396);
  private Color color22 = decodeColor("nimbusRed", -0.014814814F, 0.20118344F, -0.49411768F, 0);
  private Color color23 = decodeColor("nimbusRed", -0.014814814F, 0.20118344F, -0.20392159F, 0);
  private Color color24 = decodeColor("nimbusRed", -0.014814814F, -0.21260965F, 0.019607842F, 0);
  private Color color25 = decodeColor("nimbusRed", -0.014814814F, 0.17340565F, -0.09803921F, 0);
  private Color color26 = decodeColor("nimbusRed", -0.014814814F, 0.20118344F, -0.10588235F, 0);
  private Color color27 = decodeColor("nimbusRed", -0.014814814F, 0.20118344F, -0.04705882F, 0);
  private Color color28 = decodeColor("nimbusRed", -0.014814814F, 0.20118344F, -0.31764707F, -122);
  private Color color29 = decodeColor("nimbusRed", 0.5962963F, -0.6994788F, -0.07058823F, 0);
  private Color color30 = decodeColor("nimbusRed", 0.5962963F, -0.66245294F, -0.23137257F, 0);
  private Color color31 = decodeColor("nimbusRed", 0.58518517F, -0.77649516F, 0.21568626F, 0);
  private Color color32 = decodeColor("nimbusRed", 0.5962963F, -0.7372781F, 0.10196078F, 0);
  private Color color33 = decodeColor("nimbusRed", 0.5962963F, -0.73911506F, 0.12549019F, 0);
  private Color color34 = decodeColor("nimbusBlueGrey", 0.0F, -0.027957506F, -0.31764707F, 0);
  private Object[] componentColors;
  
  public InternalFrameTitlePaneCloseButtonPainter(AbstractRegionPainter.PaintContext paramPaintContext, int paramInt)
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
      paintBackgroundMouseOver(paramGraphics2D);
      break;
    case 4: 
      paintBackgroundPressed(paramGraphics2D);
      break;
    case 5: 
      paintBackgroundEnabledAndWindowNotFocused(paramGraphics2D);
      break;
    case 6: 
      paintBackgroundMouseOverAndWindowNotFocused(paramGraphics2D);
      break;
    case 7: 
      paintBackgroundPressedAndWindowNotFocused(paramGraphics2D);
    }
  }
  
  protected final AbstractRegionPainter.PaintContext getPaintContext()
  {
    return ctx;
  }
  
  private void paintBackgroundDisabled(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(decodeGradient1(roundRect));
    paramGraphics2D.fill(roundRect);
    path = decodePath1();
    paramGraphics2D.setPaint(color3);
    paramGraphics2D.fill(path);
    path = decodePath2();
    paramGraphics2D.setPaint(color4);
    paramGraphics2D.fill(path);
  }
  
  private void paintBackgroundEnabled(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(color5);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(decodeGradient2(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient3(roundRect));
    paramGraphics2D.fill(roundRect);
    path = decodePath1();
    paramGraphics2D.setPaint(color12);
    paramGraphics2D.fill(path);
    path = decodePath2();
    paramGraphics2D.setPaint(color13);
    paramGraphics2D.fill(path);
  }
  
  private void paintBackgroundMouseOver(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(color5);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(decodeGradient4(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect4();
    paramGraphics2D.setPaint(decodeGradient5(roundRect));
    paramGraphics2D.fill(roundRect);
    path = decodePath1();
    paramGraphics2D.setPaint(color20);
    paramGraphics2D.fill(path);
    path = decodePath2();
    paramGraphics2D.setPaint(color13);
    paramGraphics2D.fill(path);
  }
  
  private void paintBackgroundPressed(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(color21);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(decodeGradient6(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient7(roundRect));
    paramGraphics2D.fill(roundRect);
    path = decodePath1();
    paramGraphics2D.setPaint(color28);
    paramGraphics2D.fill(path);
    path = decodePath2();
    paramGraphics2D.setPaint(color13);
    paramGraphics2D.fill(path);
  }
  
  private void paintBackgroundEnabledAndWindowNotFocused(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(decodeGradient8(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient9(roundRect));
    paramGraphics2D.fill(roundRect);
    path = decodePath2();
    paramGraphics2D.setPaint(color34);
    paramGraphics2D.fill(path);
  }
  
  private void paintBackgroundMouseOverAndWindowNotFocused(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(color5);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(decodeGradient4(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect4();
    paramGraphics2D.setPaint(decodeGradient5(roundRect));
    paramGraphics2D.fill(roundRect);
    path = decodePath1();
    paramGraphics2D.setPaint(color20);
    paramGraphics2D.fill(path);
    path = decodePath2();
    paramGraphics2D.setPaint(color13);
    paramGraphics2D.fill(path);
  }
  
  private void paintBackgroundPressedAndWindowNotFocused(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(color21);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(decodeGradient6(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient7(roundRect));
    paramGraphics2D.fill(roundRect);
    path = decodePath1();
    paramGraphics2D.setPaint(color28);
    paramGraphics2D.fill(path);
    path = decodePath2();
    paramGraphics2D.setPaint(color13);
    paramGraphics2D.fill(path);
  }
  
  private RoundRectangle2D decodeRoundRect1()
  {
    roundRect.setRoundRect(decodeX(1.0F), decodeY(1.0F), decodeX(2.0F) - decodeX(1.0F), decodeY(1.9444444F) - decodeY(1.0F), 8.600000381469727D, 8.600000381469727D);
    return roundRect;
  }
  
  private Path2D decodePath1()
  {
    path.reset();
    path.moveTo(decodeX(1.25F), decodeY(1.7373737F));
    path.lineTo(decodeX(1.3002392F), decodeY(1.794192F));
    path.lineTo(decodeX(1.5047847F), decodeY(1.5909091F));
    path.lineTo(decodeX(1.6842105F), decodeY(1.7954545F));
    path.lineTo(decodeX(1.7595694F), decodeY(1.719697F));
    path.lineTo(decodeX(1.5956938F), decodeY(1.5239899F));
    path.lineTo(decodeX(1.7535884F), decodeY(1.3409091F));
    path.lineTo(decodeX(1.6830144F), decodeY(1.2537879F));
    path.lineTo(decodeX(1.5083733F), decodeY(1.4406565F));
    path.lineTo(decodeX(1.3301436F), decodeY(1.2563131F));
    path.lineTo(decodeX(1.257177F), decodeY(1.3320707F));
    path.lineTo(decodeX(1.4270334F), decodeY(1.5252526F));
    path.lineTo(decodeX(1.25F), decodeY(1.7373737F));
    path.closePath();
    return path;
  }
  
  private Path2D decodePath2()
  {
    path.reset();
    path.moveTo(decodeX(1.257177F), decodeY(1.2828283F));
    path.lineTo(decodeX(1.3217703F), decodeY(1.2133838F));
    path.lineTo(decodeX(1.5F), decodeY(1.4040405F));
    path.lineTo(decodeX(1.673445F), decodeY(1.2108586F));
    path.lineTo(decodeX(1.7440192F), decodeY(1.2853535F));
    path.lineTo(decodeX(1.5669856F), decodeY(1.4709597F));
    path.lineTo(decodeX(1.7488039F), decodeY(1.6527778F));
    path.lineTo(decodeX(1.673445F), decodeY(1.7398989F));
    path.lineTo(decodeX(1.4988039F), decodeY(1.5416667F));
    path.lineTo(decodeX(1.3313397F), decodeY(1.7424242F));
    path.lineTo(decodeX(1.2523923F), decodeY(1.6565657F));
    path.lineTo(decodeX(1.4366028F), decodeY(1.4722222F));
    path.lineTo(decodeX(1.257177F), decodeY(1.2828283F));
    path.closePath();
    return path;
  }
  
  private RoundRectangle2D decodeRoundRect2()
  {
    roundRect.setRoundRect(decodeX(1.0F), decodeY(1.6111112F), decodeX(2.0F) - decodeX(1.0F), decodeY(2.0F) - decodeY(1.6111112F), 6.0D, 6.0D);
    return roundRect;
  }
  
  private RoundRectangle2D decodeRoundRect3()
  {
    roundRect.setRoundRect(decodeX(1.0526316F), decodeY(1.0530303F), decodeX(1.9473684F) - decodeX(1.0526316F), decodeY(1.8863636F) - decodeY(1.0530303F), 6.75D, 6.75D);
    return roundRect;
  }
  
  private RoundRectangle2D decodeRoundRect4()
  {
    roundRect.setRoundRect(decodeX(1.0526316F), decodeY(1.0517677F), decodeX(1.9473684F) - decodeX(1.0526316F), decodeY(1.8851011F) - decodeY(1.0517677F), 6.75D, 6.75D);
    return roundRect;
  }
  
  private Paint decodeGradient1(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.24868421F * f3 + f1, 0.0014705883F * f4 + f2, 0.24868421F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color1, decodeColor(color1, color2, 0.5F), color2 });
  }
  
  private Paint decodeGradient2(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.24868421F * f3 + f1, 0.0014705883F * f4 + f2, 0.24868421F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color6, decodeColor(color6, color7, 0.5F), color7 });
  }
  
  private Paint decodeGradient3(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25441176F * f3 + f1, 1.0016667F * f4 + f2, new float[] { 0.0F, 0.26988637F, 0.53977275F, 0.5951705F, 0.6505682F, 0.8252841F, 1.0F }, new Color[] { color8, decodeColor(color8, color9, 0.5F), color9, decodeColor(color9, color10, 0.5F), color10, decodeColor(color10, color11, 0.5F), color11 });
  }
  
  private Paint decodeGradient4(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.24868421F * f3 + f1, 0.0014705883F * f4 + f2, 0.24868421F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color14, decodeColor(color14, color15, 0.5F), color15 });
  }
  
  private Paint decodeGradient5(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25441176F * f3 + f1, 1.0016667F * f4 + f2, new float[] { 0.0F, 0.26988637F, 0.53977275F, 0.5951705F, 0.6505682F, 0.81480503F, 0.97904193F }, new Color[] { color16, decodeColor(color16, color17, 0.5F), color17, decodeColor(color17, color18, 0.5F), color18, decodeColor(color18, color19, 0.5F), color19 });
  }
  
  private Paint decodeGradient6(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.24868421F * f3 + f1, 0.0014705883F * f4 + f2, 0.24868421F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color22, decodeColor(color22, color23, 0.5F), color23 });
  }
  
  private Paint decodeGradient7(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25441176F * f3 + f1, 1.0016667F * f4 + f2, new float[] { 0.0F, 0.26988637F, 0.53977275F, 0.5951705F, 0.6505682F, 0.81630206F, 0.98203593F }, new Color[] { color24, decodeColor(color24, color25, 0.5F), color25, decodeColor(color25, color26, 0.5F), color26, decodeColor(color26, color27, 0.5F), color27 });
  }
  
  private Paint decodeGradient8(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.24868421F * f3 + f1, 0.0014705883F * f4 + f2, 0.24868421F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color29, decodeColor(color29, color30, 0.5F), color30 });
  }
  
  private Paint decodeGradient9(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25441176F * f3 + f1, 1.0016667F * f4 + f2, new float[] { 0.0F, 0.24101797F, 0.48203593F, 0.5838324F, 0.6856288F, 0.8428144F, 1.0F }, new Color[] { color31, decodeColor(color31, color32, 0.5F), color32, decodeColor(color32, color32, 0.5F), color32, decodeColor(color32, color33, 0.5F), color33 });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\nimbus\InternalFrameTitlePaneCloseButtonPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */