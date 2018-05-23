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

final class InternalFrameTitlePaneIconifyButtonPainter
  extends AbstractRegionPainter
{
  static final int BACKGROUND_ENABLED = 1;
  static final int BACKGROUND_DISABLED = 2;
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
  private Color color1 = decodeColor("nimbusBlueGrey", 0.0055555105F, -0.0029994324F, -0.38039216F, 65351);
  private Color color2 = decodeColor("nimbusOrange", -0.08377897F, 0.02094239F, -0.40392157F, 0);
  private Color color3 = decodeColor("nimbusOrange", 0.0F, 0.0F, 0.0F, 0);
  private Color color4 = decodeColor("nimbusOrange", -4.4563413E-4F, -0.48364475F, 0.10588235F, 0);
  private Color color5 = decodeColor("nimbusOrange", 0.0F, -0.0050992966F, 0.0039215684F, 0);
  private Color color6 = decodeColor("nimbusOrange", 0.0F, -0.12125945F, 0.10588235F, 0);
  private Color color7 = decodeColor("nimbusOrange", -0.08377897F, 0.02094239F, -0.40392157F, -106);
  private Color color8 = decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, 0);
  private Color color9 = decodeColor("nimbusOrange", 0.5203877F, -0.9376068F, 0.007843137F, 0);
  private Color color10 = decodeColor("nimbusOrange", 0.5273321F, -0.8903002F, -0.086274505F, 0);
  private Color color11 = decodeColor("nimbusOrange", 0.5273321F, -0.93313926F, 0.019607842F, 0);
  private Color color12 = decodeColor("nimbusOrange", 0.53526866F, -0.8995122F, -0.058823526F, 0);
  private Color color13 = decodeColor("nimbusOrange", 0.5233639F, -0.8971863F, -0.07843137F, 0);
  private Color color14 = decodeColor("nimbusBlueGrey", -0.0808081F, 0.015910469F, -0.40392157F, 65320);
  private Color color15 = decodeColor("nimbusBlueGrey", -0.003968239F, -0.03760965F, 0.007843137F, 0);
  private Color color16 = new Color(255, 200, 0, 255);
  private Color color17 = decodeColor("nimbusOrange", -0.08377897F, 0.02094239F, -0.31764707F, 0);
  private Color color18 = decodeColor("nimbusOrange", -0.02758849F, 0.02094239F, -0.062745094F, 0);
  private Color color19 = decodeColor("nimbusOrange", -4.4563413E-4F, -0.5074419F, 0.1490196F, 0);
  private Color color20 = decodeColor("nimbusOrange", 9.745359E-6F, -0.11175901F, 0.07843137F, 0);
  private Color color21 = decodeColor("nimbusOrange", 0.0F, -0.09280169F, 0.07843137F, 0);
  private Color color22 = decodeColor("nimbusOrange", 0.0F, -0.19002807F, 0.18039215F, 0);
  private Color color23 = decodeColor("nimbusOrange", -0.025772434F, 0.02094239F, 0.05098039F, 0);
  private Color color24 = decodeColor("nimbusOrange", -0.08377897F, 0.02094239F, -0.4F, 0);
  private Color color25 = decodeColor("nimbusOrange", -0.053104125F, 0.02094239F, -0.109803915F, 0);
  private Color color26 = decodeColor("nimbusOrange", -0.017887495F, -0.33726656F, 0.039215684F, 0);
  private Color color27 = decodeColor("nimbusOrange", -0.018038228F, 0.02094239F, -0.043137252F, 0);
  private Color color28 = decodeColor("nimbusOrange", -0.015844189F, 0.02094239F, -0.027450979F, 0);
  private Color color29 = decodeColor("nimbusOrange", -0.010274701F, 0.02094239F, 0.015686274F, 0);
  private Color color30 = decodeColor("nimbusOrange", -0.08377897F, 0.02094239F, -0.14509803F, -91);
  private Color color31 = decodeColor("nimbusOrange", 0.5273321F, -0.87971985F, -0.15686274F, 0);
  private Color color32 = decodeColor("nimbusOrange", 0.5273321F, -0.842694F, -0.31764707F, 0);
  private Color color33 = decodeColor("nimbusOrange", 0.516221F, -0.9567362F, 0.12941176F, 0);
  private Color color34 = decodeColor("nimbusOrange", 0.5222816F, -0.9229352F, 0.019607842F, 0);
  private Color color35 = decodeColor("nimbusOrange", 0.5273321F, -0.91751915F, 0.015686274F, 0);
  private Color color36 = decodeColor("nimbusOrange", 0.5273321F, -0.9193561F, 0.039215684F, 0);
  private Color color37 = decodeColor("nimbusBlueGrey", -0.01111114F, -0.017933726F, -0.32156864F, 0);
  private Object[] componentColors;
  
  public InternalFrameTitlePaneIconifyButtonPainter(AbstractRegionPainter.PaintContext paramPaintContext, int paramInt)
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
      paintBackgroundDisabled(paramGraphics2D);
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
  
  private void paintBackgroundEnabled(Graphics2D paramGraphics2D)
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
    rect = decodeRect1();
    paramGraphics2D.setPaint(color7);
    paramGraphics2D.fill(rect);
    rect = decodeRect2();
    paramGraphics2D.setPaint(color8);
    paramGraphics2D.fill(rect);
  }
  
  private void paintBackgroundDisabled(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(color1);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient3(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient4(roundRect));
    paramGraphics2D.fill(roundRect);
    rect = decodeRect1();
    paramGraphics2D.setPaint(color14);
    paramGraphics2D.fill(rect);
    rect = decodeRect2();
    paramGraphics2D.setPaint(color15);
    paramGraphics2D.fill(rect);
    rect = decodeRect3();
    paramGraphics2D.setPaint(color16);
    paramGraphics2D.fill(rect);
    rect = decodeRect3();
    paramGraphics2D.setPaint(color16);
    paramGraphics2D.fill(rect);
    rect = decodeRect3();
    paramGraphics2D.setPaint(color16);
    paramGraphics2D.fill(rect);
    rect = decodeRect3();
    paramGraphics2D.setPaint(color16);
    paramGraphics2D.fill(rect);
    rect = decodeRect3();
    paramGraphics2D.setPaint(color16);
    paramGraphics2D.fill(rect);
    rect = decodeRect3();
    paramGraphics2D.setPaint(color16);
    paramGraphics2D.fill(rect);
    rect = decodeRect3();
    paramGraphics2D.setPaint(color16);
    paramGraphics2D.fill(rect);
  }
  
  private void paintBackgroundMouseOver(Graphics2D paramGraphics2D)
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
    rect = decodeRect1();
    paramGraphics2D.setPaint(color23);
    paramGraphics2D.fill(rect);
    rect = decodeRect2();
    paramGraphics2D.setPaint(color8);
    paramGraphics2D.fill(rect);
    rect = decodeRect3();
    paramGraphics2D.setPaint(color16);
    paramGraphics2D.fill(rect);
    rect = decodeRect3();
    paramGraphics2D.setPaint(color16);
    paramGraphics2D.fill(rect);
    rect = decodeRect3();
    paramGraphics2D.setPaint(color16);
    paramGraphics2D.fill(rect);
    rect = decodeRect3();
    paramGraphics2D.setPaint(color16);
    paramGraphics2D.fill(rect);
    rect = decodeRect3();
    paramGraphics2D.setPaint(color16);
    paramGraphics2D.fill(rect);
    rect = decodeRect3();
    paramGraphics2D.setPaint(color16);
    paramGraphics2D.fill(rect);
    rect = decodeRect3();
    paramGraphics2D.setPaint(color16);
    paramGraphics2D.fill(rect);
  }
  
  private void paintBackgroundPressed(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(color1);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient7(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient8(roundRect));
    paramGraphics2D.fill(roundRect);
    rect = decodeRect4();
    paramGraphics2D.setPaint(color30);
    paramGraphics2D.fill(rect);
    rect = decodeRect2();
    paramGraphics2D.setPaint(color8);
    paramGraphics2D.fill(rect);
    rect = decodeRect3();
    paramGraphics2D.setPaint(color16);
    paramGraphics2D.fill(rect);
    rect = decodeRect3();
    paramGraphics2D.setPaint(color16);
    paramGraphics2D.fill(rect);
    rect = decodeRect3();
    paramGraphics2D.setPaint(color16);
    paramGraphics2D.fill(rect);
    rect = decodeRect3();
    paramGraphics2D.setPaint(color16);
    paramGraphics2D.fill(rect);
    rect = decodeRect3();
    paramGraphics2D.setPaint(color16);
    paramGraphics2D.fill(rect);
    rect = decodeRect3();
    paramGraphics2D.setPaint(color16);
    paramGraphics2D.fill(rect);
    rect = decodeRect3();
    paramGraphics2D.setPaint(color16);
    paramGraphics2D.fill(rect);
  }
  
  private void paintBackgroundEnabledAndWindowNotFocused(Graphics2D paramGraphics2D)
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
    rect = decodeRect1();
    paramGraphics2D.setPaint(color14);
    paramGraphics2D.fill(rect);
    rect = decodeRect2();
    paramGraphics2D.setPaint(color37);
    paramGraphics2D.fill(rect);
    rect = decodeRect3();
    paramGraphics2D.setPaint(color16);
    paramGraphics2D.fill(rect);
    rect = decodeRect3();
    paramGraphics2D.setPaint(color16);
    paramGraphics2D.fill(rect);
    rect = decodeRect3();
    paramGraphics2D.setPaint(color16);
    paramGraphics2D.fill(rect);
    rect = decodeRect3();
    paramGraphics2D.setPaint(color16);
    paramGraphics2D.fill(rect);
    rect = decodeRect3();
    paramGraphics2D.setPaint(color16);
    paramGraphics2D.fill(rect);
    rect = decodeRect3();
    paramGraphics2D.setPaint(color16);
    paramGraphics2D.fill(rect);
    rect = decodeRect3();
    paramGraphics2D.setPaint(color16);
    paramGraphics2D.fill(rect);
  }
  
  private void paintBackgroundMouseOverAndWindowNotFocused(Graphics2D paramGraphics2D)
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
    rect = decodeRect1();
    paramGraphics2D.setPaint(color23);
    paramGraphics2D.fill(rect);
    rect = decodeRect2();
    paramGraphics2D.setPaint(color8);
    paramGraphics2D.fill(rect);
    rect = decodeRect3();
    paramGraphics2D.setPaint(color16);
    paramGraphics2D.fill(rect);
    rect = decodeRect3();
    paramGraphics2D.setPaint(color16);
    paramGraphics2D.fill(rect);
    rect = decodeRect3();
    paramGraphics2D.setPaint(color16);
    paramGraphics2D.fill(rect);
    rect = decodeRect3();
    paramGraphics2D.setPaint(color16);
    paramGraphics2D.fill(rect);
    rect = decodeRect3();
    paramGraphics2D.setPaint(color16);
    paramGraphics2D.fill(rect);
    rect = decodeRect3();
    paramGraphics2D.setPaint(color16);
    paramGraphics2D.fill(rect);
    rect = decodeRect3();
    paramGraphics2D.setPaint(color16);
    paramGraphics2D.fill(rect);
  }
  
  private void paintBackgroundPressedAndWindowNotFocused(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(color1);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient7(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient8(roundRect));
    paramGraphics2D.fill(roundRect);
    rect = decodeRect4();
    paramGraphics2D.setPaint(color30);
    paramGraphics2D.fill(rect);
    rect = decodeRect2();
    paramGraphics2D.setPaint(color8);
    paramGraphics2D.fill(rect);
    rect = decodeRect3();
    paramGraphics2D.setPaint(color16);
    paramGraphics2D.fill(rect);
    rect = decodeRect3();
    paramGraphics2D.setPaint(color16);
    paramGraphics2D.fill(rect);
    rect = decodeRect3();
    paramGraphics2D.setPaint(color16);
    paramGraphics2D.fill(rect);
    rect = decodeRect3();
    paramGraphics2D.setPaint(color16);
    paramGraphics2D.fill(rect);
    rect = decodeRect3();
    paramGraphics2D.setPaint(color16);
    paramGraphics2D.fill(rect);
    rect = decodeRect3();
    paramGraphics2D.setPaint(color16);
    paramGraphics2D.fill(rect);
    rect = decodeRect3();
    paramGraphics2D.setPaint(color16);
    paramGraphics2D.fill(rect);
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
  
  private Rectangle2D decodeRect1()
  {
    rect.setRect(decodeX(1.25F), decodeY(1.6628788F), decodeX(1.75F) - decodeX(1.25F), decodeY(1.7487373F) - decodeY(1.6628788F));
    return rect;
  }
  
  private Rectangle2D decodeRect2()
  {
    rect.setRect(decodeX(1.2870814F), decodeY(1.6123737F), decodeX(1.7165072F) - decodeX(1.2870814F), decodeY(1.7222222F) - decodeY(1.6123737F));
    return rect;
  }
  
  private Rectangle2D decodeRect3()
  {
    rect.setRect(decodeX(1.0F), decodeY(1.0F), decodeX(1.0F) - decodeX(1.0F), decodeY(1.0F) - decodeY(1.0F));
    return rect;
  }
  
  private Rectangle2D decodeRect4()
  {
    rect.setRect(decodeX(1.25F), decodeY(1.6527778F), decodeX(1.7511961F) - decodeX(1.25F), decodeY(1.7828283F) - decodeY(1.6527778F));
    return rect;
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
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25441176F * f3 + f1, 1.0016667F * f4 + f2, new float[] { 0.0F, 0.26988637F, 0.53977275F, 0.5951705F, 0.6505682F, 0.8252841F, 1.0F }, new Color[] { color4, decodeColor(color4, color3, 0.5F), color3, decodeColor(color3, color5, 0.5F), color5, decodeColor(color5, color6, 0.5F), color6 });
  }
  
  private Paint decodeGradient3(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.24868421F * f3 + f1, 0.0014705883F * f4 + f2, 0.24868421F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color9, decodeColor(color9, color10, 0.5F), color10 });
  }
  
  private Paint decodeGradient4(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25441176F * f3 + f1, 1.0016667F * f4 + f2, new float[] { 0.0F, 0.26988637F, 0.53977275F, 0.5951705F, 0.6505682F, 0.8252841F, 1.0F }, new Color[] { color11, decodeColor(color11, color12, 0.5F), color12, decodeColor(color12, color13, 0.5F), color13, decodeColor(color13, color10, 0.5F), color10 });
  }
  
  private Paint decodeGradient5(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.24868421F * f3 + f1, 0.0014705883F * f4 + f2, 0.24868421F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color17, decodeColor(color17, color18, 0.5F), color18 });
  }
  
  private Paint decodeGradient6(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25441176F * f3 + f1, 1.0016667F * f4 + f2, new float[] { 0.0F, 0.26988637F, 0.53977275F, 0.5951705F, 0.6505682F, 0.8252841F, 1.0F }, new Color[] { color19, decodeColor(color19, color20, 0.5F), color20, decodeColor(color20, color21, 0.5F), color21, decodeColor(color21, color22, 0.5F), color22 });
  }
  
  private Paint decodeGradient7(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.24868421F * f3 + f1, 0.0014705883F * f4 + f2, 0.24868421F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color24, decodeColor(color24, color25, 0.5F), color25 });
  }
  
  private Paint decodeGradient8(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25441176F * f3 + f1, 1.0016667F * f4 + f2, new float[] { 0.0F, 0.26988637F, 0.53977275F, 0.5951705F, 0.6505682F, 0.8252841F, 1.0F }, new Color[] { color26, decodeColor(color26, color27, 0.5F), color27, decodeColor(color27, color28, 0.5F), color28, decodeColor(color28, color29, 0.5F), color29 });
  }
  
  private Paint decodeGradient9(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.24868421F * f3 + f1, 0.0014705883F * f4 + f2, 0.24868421F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color31, decodeColor(color31, color32, 0.5F), color32 });
  }
  
  private Paint decodeGradient10(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25441176F * f3 + f1, 1.0016667F * f4 + f2, new float[] { 0.0F, 0.26988637F, 0.53977275F, 0.5951705F, 0.6505682F, 0.78336793F, 0.9161677F }, new Color[] { color33, decodeColor(color33, color34, 0.5F), color34, decodeColor(color34, color35, 0.5F), color35, decodeColor(color35, color36, 0.5F), color36 });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\nimbus\InternalFrameTitlePaneIconifyButtonPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */