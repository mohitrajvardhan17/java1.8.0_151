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

final class InternalFramePainter
  extends AbstractRegionPainter
{
  static final int BACKGROUND_ENABLED = 1;
  static final int BACKGROUND_ENABLED_WINDOWFOCUSED = 2;
  private int state;
  private AbstractRegionPainter.PaintContext ctx;
  private Path2D path = new Path2D.Float();
  private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
  private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  private Color color1 = decodeColor("nimbusBase", 0.032459438F, -0.53637654F, 0.043137252F, 0);
  private Color color2 = decodeColor("nimbusBlueGrey", 0.004273474F, -0.039488062F, -0.027450979F, 0);
  private Color color3 = decodeColor("nimbusBlueGrey", -0.00505054F, -0.056339122F, 0.05098039F, 0);
  private Color color4 = decodeColor("nimbusBlueGrey", -0.01111114F, -0.06357796F, 0.09019607F, 0);
  private Color color5 = decodeColor("nimbusBlueGrey", 0.0F, -0.023821115F, -0.06666666F, 0);
  private Color color6 = decodeColor("control", 0.0F, 0.0F, 0.0F, 0);
  private Color color7 = decodeColor("nimbusBlueGrey", -0.006944418F, -0.07399663F, 0.11372548F, 0);
  private Color color8 = decodeColor("nimbusBase", 0.02551502F, -0.47885156F, -0.34901965F, 0);
  private Color color9 = new Color(255, 200, 0, 255);
  private Color color10 = decodeColor("nimbusBase", 0.004681647F, -0.6274498F, 0.39999998F, 0);
  private Color color11 = decodeColor("nimbusBase", 0.032459438F, -0.5934608F, 0.2862745F, 0);
  private Color color12 = new Color(204, 207, 213, 255);
  private Color color13 = decodeColor("nimbusBase", 0.032459438F, -0.55506915F, 0.18039215F, 0);
  private Color color14 = decodeColor("nimbusBase", 0.004681647F, -0.52792984F, 0.10588235F, 0);
  private Color color15 = decodeColor("nimbusBase", 0.03801495F, -0.4794643F, -0.04705882F, 0);
  private Color color16 = decodeColor("nimbusBase", 0.021348298F, -0.61416256F, 0.3607843F, 0);
  private Color color17 = decodeColor("nimbusBase", 0.032459438F, -0.5546332F, 0.17647058F, 0);
  private Color color18 = new Color(235, 236, 238, 255);
  private Object[] componentColors;
  
  public InternalFramePainter(AbstractRegionPainter.PaintContext paramPaintContext, int paramInt)
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
      paintBackgroundEnabledAndWindowFocused(paramGraphics2D);
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
    path = decodePath1();
    paramGraphics2D.setPaint(decodeGradient1(path));
    paramGraphics2D.fill(path);
    path = decodePath2();
    paramGraphics2D.setPaint(color3);
    paramGraphics2D.fill(path);
    path = decodePath3();
    paramGraphics2D.setPaint(color4);
    paramGraphics2D.fill(path);
    path = decodePath4();
    paramGraphics2D.setPaint(color5);
    paramGraphics2D.fill(path);
    rect = decodeRect1();
    paramGraphics2D.setPaint(color6);
    paramGraphics2D.fill(rect);
    rect = decodeRect2();
    paramGraphics2D.setPaint(color7);
    paramGraphics2D.fill(rect);
  }
  
  private void paintBackgroundEnabledAndWindowFocused(Graphics2D paramGraphics2D)
  {
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(color8);
    paramGraphics2D.fill(roundRect);
    path = decodePath5();
    paramGraphics2D.setPaint(color9);
    paramGraphics2D.fill(path);
    path = decodePath1();
    paramGraphics2D.setPaint(decodeGradient2(path));
    paramGraphics2D.fill(path);
    path = decodePath6();
    paramGraphics2D.setPaint(color12);
    paramGraphics2D.fill(path);
    path = decodePath7();
    paramGraphics2D.setPaint(color13);
    paramGraphics2D.fill(path);
    path = decodePath8();
    paramGraphics2D.setPaint(color14);
    paramGraphics2D.fill(path);
    path = decodePath9();
    paramGraphics2D.setPaint(color15);
    paramGraphics2D.fill(path);
    rect = decodeRect1();
    paramGraphics2D.setPaint(color6);
    paramGraphics2D.fill(rect);
    rect = decodeRect3();
    paramGraphics2D.setPaint(color9);
    paramGraphics2D.fill(rect);
    rect = decodeRect3();
    paramGraphics2D.setPaint(color9);
    paramGraphics2D.fill(rect);
    rect = decodeRect3();
    paramGraphics2D.setPaint(color9);
    paramGraphics2D.fill(rect);
    rect = decodeRect4();
    paramGraphics2D.setPaint(decodeGradient3(rect));
    paramGraphics2D.fill(rect);
    rect = decodeRect2();
    paramGraphics2D.setPaint(color18);
    paramGraphics2D.fill(rect);
  }
  
  private RoundRectangle2D decodeRoundRect1()
  {
    roundRect.setRoundRect(decodeX(0.0F), decodeY(0.0F), decodeX(3.0F) - decodeX(0.0F), decodeY(3.0F) - decodeY(0.0F), 4.666666507720947D, 4.666666507720947D);
    return roundRect;
  }
  
  private Path2D decodePath1()
  {
    path.reset();
    path.moveTo(decodeX(0.16666667F), decodeY(0.12F));
    path.curveTo(decodeAnchorX(0.16666667F, 0.0F), decodeAnchorY(0.12F, -1.0F), decodeAnchorX(0.5F, -1.0F), decodeAnchorY(0.04F, 0.0F), decodeX(0.5F), decodeY(0.04F));
    path.curveTo(decodeAnchorX(0.5F, 1.0F), decodeAnchorY(0.04F, 0.0F), decodeAnchorX(2.5F, -1.0F), decodeAnchorY(0.04F, 0.0F), decodeX(2.5F), decodeY(0.04F));
    path.curveTo(decodeAnchorX(2.5F, 1.0F), decodeAnchorY(0.04F, 0.0F), decodeAnchorX(2.8333333F, 0.0F), decodeAnchorY(0.12F, -1.0F), decodeX(2.8333333F), decodeY(0.12F));
    path.curveTo(decodeAnchorX(2.8333333F, 0.0F), decodeAnchorY(0.12F, 1.0F), decodeAnchorX(2.8333333F, 0.0F), decodeAnchorY(0.96F, 0.0F), decodeX(2.8333333F), decodeY(0.96F));
    path.lineTo(decodeX(0.16666667F), decodeY(0.96F));
    path.curveTo(decodeAnchorX(0.16666667F, 0.0F), decodeAnchorY(0.96F, 0.0F), decodeAnchorX(0.16666667F, 0.0F), decodeAnchorY(0.12F, 1.0F), decodeX(0.16666667F), decodeY(0.12F));
    path.closePath();
    return path;
  }
  
  private Path2D decodePath2()
  {
    path.reset();
    path.moveTo(decodeX(0.6666667F), decodeY(0.96F));
    path.lineTo(decodeX(0.16666667F), decodeY(0.96F));
    path.curveTo(decodeAnchorX(0.16666667F, 0.0F), decodeAnchorY(0.96F, 0.0F), decodeAnchorX(0.16666667F, 0.0F), decodeAnchorY(2.5F, -1.0F), decodeX(0.16666667F), decodeY(2.5F));
    path.curveTo(decodeAnchorX(0.16666667F, 0.0F), decodeAnchorY(2.5F, 1.0F), decodeAnchorX(0.5F, -1.0F), decodeAnchorY(2.8333333F, 0.0F), decodeX(0.5F), decodeY(2.8333333F));
    path.curveTo(decodeAnchorX(0.5F, 1.0F), decodeAnchorY(2.8333333F, 0.0F), decodeAnchorX(2.5F, -1.0F), decodeAnchorY(2.8333333F, 0.0F), decodeX(2.5F), decodeY(2.8333333F));
    path.curveTo(decodeAnchorX(2.5F, 1.0F), decodeAnchorY(2.8333333F, 0.0F), decodeAnchorX(2.8333333F, 0.0F), decodeAnchorY(2.5F, 1.0F), decodeX(2.8333333F), decodeY(2.5F));
    path.curveTo(decodeAnchorX(2.8333333F, 0.0F), decodeAnchorY(2.5F, -1.0F), decodeAnchorX(2.8333333F, 0.0F), decodeAnchorY(0.96F, 0.0F), decodeX(2.8333333F), decodeY(0.96F));
    path.lineTo(decodeX(2.3333333F), decodeY(0.96F));
    path.lineTo(decodeX(2.3333333F), decodeY(2.3333333F));
    path.lineTo(decodeX(0.6666667F), decodeY(2.3333333F));
    path.lineTo(decodeX(0.6666667F), decodeY(0.96F));
    path.closePath();
    return path;
  }
  
  private Path2D decodePath3()
  {
    path.reset();
    path.moveTo(decodeX(0.8333333F), decodeY(0.96F));
    path.lineTo(decodeX(0.6666667F), decodeY(0.96F));
    path.lineTo(decodeX(0.6666667F), decodeY(2.3333333F));
    path.lineTo(decodeX(2.3333333F), decodeY(2.3333333F));
    path.lineTo(decodeX(2.3333333F), decodeY(0.96F));
    path.lineTo(decodeX(2.1666667F), decodeY(0.96F));
    path.lineTo(decodeX(2.1666667F), decodeY(2.1666667F));
    path.lineTo(decodeX(0.8333333F), decodeY(2.1666667F));
    path.lineTo(decodeX(0.8333333F), decodeY(0.96F));
    path.closePath();
    return path;
  }
  
  private Path2D decodePath4()
  {
    path.reset();
    path.moveTo(decodeX(2.1666667F), decodeY(1.0F));
    path.lineTo(decodeX(1.0F), decodeY(1.0F));
    path.lineTo(decodeX(1.0F), decodeY(2.0F));
    path.lineTo(decodeX(2.0F), decodeY(2.0F));
    path.lineTo(decodeX(2.0F), decodeY(1.0F));
    path.lineTo(decodeX(2.1666667F), decodeY(1.0F));
    path.lineTo(decodeX(2.1666667F), decodeY(2.1666667F));
    path.lineTo(decodeX(0.8333333F), decodeY(2.1666667F));
    path.lineTo(decodeX(0.8333333F), decodeY(0.96F));
    path.lineTo(decodeX(2.1666667F), decodeY(0.96F));
    path.lineTo(decodeX(2.1666667F), decodeY(1.0F));
    path.closePath();
    return path;
  }
  
  private Rectangle2D decodeRect1()
  {
    rect.setRect(decodeX(1.0F), decodeY(1.0F), decodeX(2.0F) - decodeX(1.0F), decodeY(2.0F) - decodeY(1.0F));
    return rect;
  }
  
  private Rectangle2D decodeRect2()
  {
    rect.setRect(decodeX(0.33333334F), decodeY(2.6666667F), decodeX(2.6666667F) - decodeX(0.33333334F), decodeY(2.8333333F) - decodeY(2.6666667F));
    return rect;
  }
  
  private RoundRectangle2D decodeRoundRect2()
  {
    roundRect.setRoundRect(decodeX(0.0F), decodeY(0.0F), decodeX(3.0F) - decodeX(0.0F), decodeY(3.0F) - decodeY(0.0F), 4.833333492279053D, 4.833333492279053D);
    return roundRect;
  }
  
  private Path2D decodePath5()
  {
    path.reset();
    path.moveTo(decodeX(0.16666667F), decodeY(0.08F));
    path.curveTo(decodeAnchorX(0.16666667F, 0.0F), decodeAnchorY(0.08F, 1.0F), decodeAnchorX(0.16666667F, 0.0F), decodeAnchorY(0.08F, -1.0F), decodeX(0.16666667F), decodeY(0.08F));
    path.closePath();
    return path;
  }
  
  private Path2D decodePath6()
  {
    path.reset();
    path.moveTo(decodeX(0.5F), decodeY(0.96F));
    path.lineTo(decodeX(0.16666667F), decodeY(0.96F));
    path.curveTo(decodeAnchorX(0.16666667F, 0.0F), decodeAnchorY(0.96F, 0.0F), decodeAnchorX(0.16666667F, 0.0F), decodeAnchorY(2.5F, -1.0F), decodeX(0.16666667F), decodeY(2.5F));
    path.curveTo(decodeAnchorX(0.16666667F, 0.0F), decodeAnchorY(2.5F, 1.0F), decodeAnchorX(0.5F, -1.0F), decodeAnchorY(2.8333333F, 0.0F), decodeX(0.5F), decodeY(2.8333333F));
    path.curveTo(decodeAnchorX(0.5F, 1.0F), decodeAnchorY(2.8333333F, 0.0F), decodeAnchorX(2.5F, -1.0F), decodeAnchorY(2.8333333F, 0.0F), decodeX(2.5F), decodeY(2.8333333F));
    path.curveTo(decodeAnchorX(2.5F, 1.0F), decodeAnchorY(2.8333333F, 0.0F), decodeAnchorX(2.8333333F, 0.0F), decodeAnchorY(2.5F, 1.0F), decodeX(2.8333333F), decodeY(2.5F));
    path.curveTo(decodeAnchorX(2.8333333F, 0.0F), decodeAnchorY(2.5F, -1.0F), decodeAnchorX(2.8333333F, 0.0F), decodeAnchorY(0.96F, 0.0F), decodeX(2.8333333F), decodeY(0.96F));
    path.lineTo(decodeX(2.5F), decodeY(0.96F));
    path.lineTo(decodeX(2.5F), decodeY(2.5F));
    path.lineTo(decodeX(0.5F), decodeY(2.5F));
    path.lineTo(decodeX(0.5F), decodeY(0.96F));
    path.closePath();
    return path;
  }
  
  private Path2D decodePath7()
  {
    path.reset();
    path.moveTo(decodeX(0.6666667F), decodeY(0.96F));
    path.lineTo(decodeX(0.33333334F), decodeY(0.96F));
    path.curveTo(decodeAnchorX(0.33333334F, 0.0F), decodeAnchorY(0.96F, 0.0F), decodeAnchorX(0.33333334F, 0.0F), decodeAnchorY(2.3333333F, -1.0F), decodeX(0.33333334F), decodeY(2.3333333F));
    path.curveTo(decodeAnchorX(0.33333334F, 0.0F), decodeAnchorY(2.3333333F, 1.0F), decodeAnchorX(0.6666667F, -1.0F), decodeAnchorY(2.6666667F, 0.0F), decodeX(0.6666667F), decodeY(2.6666667F));
    path.curveTo(decodeAnchorX(0.6666667F, 1.0F), decodeAnchorY(2.6666667F, 0.0F), decodeAnchorX(2.3333333F, -1.0F), decodeAnchorY(2.6666667F, 0.0F), decodeX(2.3333333F), decodeY(2.6666667F));
    path.curveTo(decodeAnchorX(2.3333333F, 1.0F), decodeAnchorY(2.6666667F, 0.0F), decodeAnchorX(2.6666667F, 0.0F), decodeAnchorY(2.3333333F, 1.0F), decodeX(2.6666667F), decodeY(2.3333333F));
    path.curveTo(decodeAnchorX(2.6666667F, 0.0F), decodeAnchorY(2.3333333F, -1.0F), decodeAnchorX(2.6666667F, 0.0F), decodeAnchorY(0.96F, 0.0F), decodeX(2.6666667F), decodeY(0.96F));
    path.lineTo(decodeX(2.3333333F), decodeY(0.96F));
    path.lineTo(decodeX(2.3333333F), decodeY(2.3333333F));
    path.lineTo(decodeX(0.6666667F), decodeY(2.3333333F));
    path.lineTo(decodeX(0.6666667F), decodeY(0.96F));
    path.closePath();
    return path;
  }
  
  private Path2D decodePath8()
  {
    path.reset();
    path.moveTo(decodeX(2.3333333F), decodeY(0.96F));
    path.lineTo(decodeX(2.1666667F), decodeY(0.96F));
    path.lineTo(decodeX(2.1666667F), decodeY(2.1666667F));
    path.lineTo(decodeX(0.8333333F), decodeY(2.1666667F));
    path.lineTo(decodeX(0.8333333F), decodeY(0.96F));
    path.lineTo(decodeX(0.6666667F), decodeY(0.96F));
    path.lineTo(decodeX(0.6666667F), decodeY(2.3333333F));
    path.lineTo(decodeX(2.3333333F), decodeY(2.3333333F));
    path.lineTo(decodeX(2.3333333F), decodeY(0.96F));
    path.closePath();
    return path;
  }
  
  private Path2D decodePath9()
  {
    path.reset();
    path.moveTo(decodeX(0.8333333F), decodeY(1.0F));
    path.lineTo(decodeX(0.8333333F), decodeY(2.1666667F));
    path.lineTo(decodeX(2.1666667F), decodeY(2.1666667F));
    path.lineTo(decodeX(2.1666667F), decodeY(0.96F));
    path.lineTo(decodeX(0.8333333F), decodeY(0.96F));
    path.lineTo(decodeX(0.8333333F), decodeY(1.0F));
    path.lineTo(decodeX(2.0F), decodeY(1.0F));
    path.lineTo(decodeX(2.0F), decodeY(2.0F));
    path.lineTo(decodeX(1.0F), decodeY(2.0F));
    path.lineTo(decodeX(1.0F), decodeY(1.0F));
    path.lineTo(decodeX(0.8333333F), decodeY(1.0F));
    path.closePath();
    return path;
  }
  
  private Rectangle2D decodeRect3()
  {
    rect.setRect(decodeX(0.0F), decodeY(0.0F), decodeX(0.0F) - decodeX(0.0F), decodeY(0.0F) - decodeY(0.0F));
    return rect;
  }
  
  private Rectangle2D decodeRect4()
  {
    rect.setRect(decodeX(0.33333334F), decodeY(0.08F), decodeX(2.6666667F) - decodeX(0.33333334F), decodeY(0.96F) - decodeY(0.08F));
    return rect;
  }
  
  private Paint decodeGradient1(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.3203593F, 1.0F }, new Color[] { color2, decodeColor(color2, color3, 0.5F), color3 });
  }
  
  private Paint decodeGradient2(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color10, decodeColor(color10, color11, 0.5F), color11 });
  }
  
  private Paint decodeGradient3(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.24251497F, 1.0F }, new Color[] { color16, decodeColor(color16, color17, 0.5F), color17 });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\nimbus\InternalFramePainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */