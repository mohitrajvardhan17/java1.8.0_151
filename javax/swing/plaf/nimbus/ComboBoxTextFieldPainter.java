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

final class ComboBoxTextFieldPainter
  extends AbstractRegionPainter
{
  static final int BACKGROUND_DISABLED = 1;
  static final int BACKGROUND_ENABLED = 2;
  static final int BACKGROUND_SELECTED = 3;
  private int state;
  private AbstractRegionPainter.PaintContext ctx;
  private Path2D path = new Path2D.Float();
  private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
  private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  private Color color1 = decodeColor("nimbusBlueGrey", -0.6111111F, -0.110526316F, -0.74509805F, 65299);
  private Color color2 = decodeColor("nimbusBlueGrey", -0.006944418F, -0.07187897F, 0.06666666F, 0);
  private Color color3 = decodeColor("nimbusBlueGrey", 0.007936537F, -0.07703349F, 0.0745098F, 0);
  private Color color4 = decodeColor("nimbusBlueGrey", 0.007936537F, -0.07968931F, 0.14509803F, 0);
  private Color color5 = decodeColor("nimbusBlueGrey", 0.007936537F, -0.07856284F, 0.11372548F, 0);
  private Color color6 = decodeColor("nimbusBase", 0.040395975F, -0.60315615F, 0.29411763F, 0);
  private Color color7 = decodeColor("nimbusBase", 0.016586483F, -0.6051466F, 0.3490196F, 0);
  private Color color8 = decodeColor("nimbusBlueGrey", -0.027777791F, -0.0965403F, -0.18431371F, 0);
  private Color color9 = decodeColor("nimbusBlueGrey", 0.055555582F, -0.1048766F, -0.05098039F, 0);
  private Color color10 = decodeColor("nimbusLightBackground", 0.6666667F, 0.004901961F, -0.19999999F, 0);
  private Color color11 = decodeColor("nimbusLightBackground", 0.0F, 0.0F, 0.0F, 0);
  private Color color12 = decodeColor("nimbusBlueGrey", 0.055555582F, -0.105344966F, 0.011764705F, 0);
  private Object[] componentColors;
  
  public ComboBoxTextFieldPainter(AbstractRegionPainter.PaintContext paramPaintContext, int paramInt)
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
      paintBackgroundSelected(paramGraphics2D);
    }
  }
  
  protected final AbstractRegionPainter.PaintContext getPaintContext()
  {
    return ctx;
  }
  
  private void paintBackgroundDisabled(Graphics2D paramGraphics2D)
  {
    rect = decodeRect1();
    paramGraphics2D.setPaint(color1);
    paramGraphics2D.fill(rect);
    rect = decodeRect2();
    paramGraphics2D.setPaint(decodeGradient1(rect));
    paramGraphics2D.fill(rect);
    rect = decodeRect3();
    paramGraphics2D.setPaint(decodeGradient2(rect));
    paramGraphics2D.fill(rect);
    rect = decodeRect4();
    paramGraphics2D.setPaint(color6);
    paramGraphics2D.fill(rect);
    rect = decodeRect5();
    paramGraphics2D.setPaint(color7);
    paramGraphics2D.fill(rect);
  }
  
  private void paintBackgroundEnabled(Graphics2D paramGraphics2D)
  {
    rect = decodeRect1();
    paramGraphics2D.setPaint(color1);
    paramGraphics2D.fill(rect);
    rect = decodeRect2();
    paramGraphics2D.setPaint(decodeGradient3(rect));
    paramGraphics2D.fill(rect);
    rect = decodeRect3();
    paramGraphics2D.setPaint(decodeGradient4(rect));
    paramGraphics2D.fill(rect);
    rect = decodeRect4();
    paramGraphics2D.setPaint(color12);
    paramGraphics2D.fill(rect);
    rect = decodeRect5();
    paramGraphics2D.setPaint(color11);
    paramGraphics2D.fill(rect);
  }
  
  private void paintBackgroundSelected(Graphics2D paramGraphics2D)
  {
    rect = decodeRect1();
    paramGraphics2D.setPaint(color1);
    paramGraphics2D.fill(rect);
    rect = decodeRect2();
    paramGraphics2D.setPaint(decodeGradient3(rect));
    paramGraphics2D.fill(rect);
    rect = decodeRect3();
    paramGraphics2D.setPaint(decodeGradient4(rect));
    paramGraphics2D.fill(rect);
    rect = decodeRect4();
    paramGraphics2D.setPaint(color12);
    paramGraphics2D.fill(rect);
    rect = decodeRect5();
    paramGraphics2D.setPaint(color11);
    paramGraphics2D.fill(rect);
  }
  
  private Rectangle2D decodeRect1()
  {
    rect.setRect(decodeX(0.6666667F), decodeY(2.3333333F), decodeX(3.0F) - decodeX(0.6666667F), decodeY(2.6666667F) - decodeY(2.3333333F));
    return rect;
  }
  
  private Rectangle2D decodeRect2()
  {
    rect.setRect(decodeX(0.6666667F), decodeY(0.4F), decodeX(3.0F) - decodeX(0.6666667F), decodeY(1.0F) - decodeY(0.4F));
    return rect;
  }
  
  private Rectangle2D decodeRect3()
  {
    rect.setRect(decodeX(1.0F), decodeY(0.6F), decodeX(3.0F) - decodeX(1.0F), decodeY(1.0F) - decodeY(0.6F));
    return rect;
  }
  
  private Rectangle2D decodeRect4()
  {
    rect.setRect(decodeX(0.6666667F), decodeY(1.0F), decodeX(3.0F) - decodeX(0.6666667F), decodeY(2.3333333F) - decodeY(1.0F));
    return rect;
  }
  
  private Rectangle2D decodeRect5()
  {
    rect.setRect(decodeX(1.0F), decodeY(1.0F), decodeX(3.0F) - decodeX(1.0F), decodeY(2.0F) - decodeY(1.0F));
    return rect;
  }
  
  private Paint decodeGradient1(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color2, decodeColor(color2, color3, 0.5F), color3 });
  }
  
  private Paint decodeGradient2(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 1.0F * f4 + f2, 0.5F * f3 + f1, 0.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color4, decodeColor(color4, color5, 0.5F), color5 });
  }
  
  private Paint decodeGradient3(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.49573863F, 0.99147725F }, new Color[] { color8, decodeColor(color8, color9, 0.5F), color9 });
  }
  
  private Paint decodeGradient4(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.1F, 0.49999997F, 0.9F }, new Color[] { color10, decodeColor(color10, color11, 0.5F), color11 });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\nimbus\ComboBoxTextFieldPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */