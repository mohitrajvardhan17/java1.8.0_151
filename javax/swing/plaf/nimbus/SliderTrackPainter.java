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

final class SliderTrackPainter
  extends AbstractRegionPainter
{
  static final int BACKGROUND_DISABLED = 1;
  static final int BACKGROUND_ENABLED = 2;
  private int state;
  private AbstractRegionPainter.PaintContext ctx;
  private Path2D path = new Path2D.Float();
  private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
  private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  private Color color1 = decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, 65291);
  private Color color2 = decodeColor("nimbusBlueGrey", 0.0055555105F, -0.061265234F, 0.05098039F, 0);
  private Color color3 = decodeColor("nimbusBlueGrey", 0.01010108F, -0.059835073F, 0.10588235F, 0);
  private Color color4 = decodeColor("nimbusBlueGrey", -0.01111114F, -0.061982628F, 0.062745094F, 0);
  private Color color5 = decodeColor("nimbusBlueGrey", -0.00505054F, -0.058639523F, 0.086274505F, 0);
  private Color color6 = decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, -111);
  private Color color7 = decodeColor("nimbusBlueGrey", 0.0F, -0.034093194F, -0.12941176F, 0);
  private Color color8 = decodeColor("nimbusBlueGrey", 0.01111114F, -0.023821115F, -0.06666666F, 0);
  private Color color9 = decodeColor("nimbusBlueGrey", -0.008547008F, -0.03314536F, -0.086274505F, 0);
  private Color color10 = decodeColor("nimbusBlueGrey", 0.004273474F, -0.040256046F, -0.019607842F, 0);
  private Color color11 = decodeColor("nimbusBlueGrey", 0.0F, -0.03626889F, 0.04705882F, 0);
  private Object[] componentColors;
  
  public SliderTrackPainter(AbstractRegionPainter.PaintContext paramPaintContext, int paramInt)
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
    }
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
    roundRect = decodeRoundRect4();
    paramGraphics2D.setPaint(color6);
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient3(roundRect));
    paramGraphics2D.fill(roundRect);
    roundRect = decodeRoundRect5();
    paramGraphics2D.setPaint(decodeGradient4(roundRect));
    paramGraphics2D.fill(roundRect);
  }
  
  private RoundRectangle2D decodeRoundRect1()
  {
    roundRect.setRoundRect(decodeX(0.2F), decodeY(1.6F), decodeX(2.8F) - decodeX(0.2F), decodeY(2.8333333F) - decodeY(1.6F), 8.70588207244873D, 8.70588207244873D);
    return roundRect;
  }
  
  private RoundRectangle2D decodeRoundRect2()
  {
    roundRect.setRoundRect(decodeX(0.0F), decodeY(1.0F), decodeX(3.0F) - decodeX(0.0F), decodeY(2.0F) - decodeY(1.0F), 4.941176414489746D, 4.941176414489746D);
    return roundRect;
  }
  
  private RoundRectangle2D decodeRoundRect3()
  {
    roundRect.setRoundRect(decodeX(0.29411763F), decodeY(1.2F), decodeX(2.7058823F) - decodeX(0.29411763F), decodeY(2.0F) - decodeY(1.2F), 4.0D, 4.0D);
    return roundRect;
  }
  
  private RoundRectangle2D decodeRoundRect4()
  {
    roundRect.setRoundRect(decodeX(0.2F), decodeY(1.6F), decodeX(2.8F) - decodeX(0.2F), decodeY(2.1666667F) - decodeY(1.6F), 8.70588207244873D, 8.70588207244873D);
    return roundRect;
  }
  
  private RoundRectangle2D decodeRoundRect5()
  {
    roundRect.setRoundRect(decodeX(0.28823528F), decodeY(1.2F), decodeX(2.7F) - decodeX(0.28823528F), decodeY(2.0F) - decodeY(1.2F), 4.0D, 4.0D);
    return roundRect;
  }
  
  private Paint decodeGradient1(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.07647059F * f4 + f2, 0.25F * f3 + f1, 0.9117647F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color2, decodeColor(color2, color3, 0.5F), color3 });
  }
  
  private Paint decodeGradient2(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.13770053F, 0.27540106F, 0.63770056F, 1.0F }, new Color[] { color4, decodeColor(color4, color5, 0.5F), color5, decodeColor(color5, color3, 0.5F), color3 });
  }
  
  private Paint decodeGradient3(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.07647059F * f4 + f2, 0.25F * f3 + f1, 0.9117647F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color7, decodeColor(color7, color8, 0.5F), color8 });
  }
  
  private Paint decodeGradient4(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.13770053F, 0.27540106F, 0.4906417F, 0.7058824F }, new Color[] { color9, decodeColor(color9, color10, 0.5F), color10, decodeColor(color10, color11, 0.5F), color11 });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\nimbus\SliderTrackPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */