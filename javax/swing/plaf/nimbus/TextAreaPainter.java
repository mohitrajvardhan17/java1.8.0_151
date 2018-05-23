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

final class TextAreaPainter
  extends AbstractRegionPainter
{
  static final int BACKGROUND_DISABLED = 1;
  static final int BACKGROUND_ENABLED = 2;
  static final int BACKGROUND_DISABLED_NOTINSCROLLPANE = 3;
  static final int BACKGROUND_ENABLED_NOTINSCROLLPANE = 4;
  static final int BACKGROUND_SELECTED = 5;
  static final int BORDER_DISABLED_NOTINSCROLLPANE = 6;
  static final int BORDER_FOCUSED_NOTINSCROLLPANE = 7;
  static final int BORDER_ENABLED_NOTINSCROLLPANE = 8;
  private int state;
  private AbstractRegionPainter.PaintContext ctx;
  private Path2D path = new Path2D.Float();
  private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
  private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  private Color color1 = decodeColor("nimbusBlueGrey", -0.015872955F, -0.07995863F, 0.15294117F, 0);
  private Color color2 = decodeColor("nimbusLightBackground", 0.0F, 0.0F, 0.0F, 0);
  private Color color3 = decodeColor("nimbusBlueGrey", -0.006944418F, -0.07187897F, 0.06666666F, 0);
  private Color color4 = decodeColor("nimbusBlueGrey", 0.007936537F, -0.07826825F, 0.10588235F, 0);
  private Color color5 = decodeColor("nimbusBlueGrey", 0.007936537F, -0.07856284F, 0.11372548F, 0);
  private Color color6 = decodeColor("nimbusBlueGrey", 0.007936537F, -0.07796818F, 0.09803921F, 0);
  private Color color7 = decodeColor("nimbusBlueGrey", -0.027777791F, -0.0965403F, -0.18431371F, 0);
  private Color color8 = decodeColor("nimbusBlueGrey", 0.055555582F, -0.1048766F, -0.05098039F, 0);
  private Color color9 = decodeColor("nimbusLightBackground", 0.6666667F, 0.004901961F, -0.19999999F, 0);
  private Color color10 = decodeColor("nimbusBlueGrey", 0.055555582F, -0.10512091F, -0.019607842F, 0);
  private Color color11 = decodeColor("nimbusBlueGrey", 0.055555582F, -0.105344966F, 0.011764705F, 0);
  private Color color12 = decodeColor("nimbusFocus", 0.0F, 0.0F, 0.0F, 0);
  private Object[] componentColors;
  
  public TextAreaPainter(AbstractRegionPainter.PaintContext paramPaintContext, int paramInt)
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
      paintBackgroundDisabledAndNotInScrollPane(paramGraphics2D);
      break;
    case 4: 
      paintBackgroundEnabledAndNotInScrollPane(paramGraphics2D);
      break;
    case 5: 
      paintBackgroundSelected(paramGraphics2D);
      break;
    case 6: 
      paintBorderDisabledAndNotInScrollPane(paramGraphics2D);
      break;
    case 7: 
      paintBorderFocusedAndNotInScrollPane(paramGraphics2D);
      break;
    case 8: 
      paintBorderEnabledAndNotInScrollPane(paramGraphics2D);
    }
  }
  
  protected Object[] getExtendedCacheKeys(JComponent paramJComponent)
  {
    Object[] arrayOfObject = null;
    switch (state)
    {
    case 2: 
      arrayOfObject = new Object[] { getComponentColor(paramJComponent, "background", color2, 0.0F, 0.0F, 0) };
      break;
    case 4: 
      arrayOfObject = new Object[] { getComponentColor(paramJComponent, "background", color2, 0.0F, 0.0F, 0) };
      break;
    case 7: 
      arrayOfObject = new Object[] { getComponentColor(paramJComponent, "background", color9, 0.004901961F, -0.19999999F, 0), getComponentColor(paramJComponent, "background", color2, 0.0F, 0.0F, 0) };
      break;
    case 8: 
      arrayOfObject = new Object[] { getComponentColor(paramJComponent, "background", color9, 0.004901961F, -0.19999999F, 0), getComponentColor(paramJComponent, "background", color2, 0.0F, 0.0F, 0) };
    }
    return arrayOfObject;
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
  }
  
  private void paintBackgroundEnabled(Graphics2D paramGraphics2D)
  {
    rect = decodeRect1();
    paramGraphics2D.setPaint((Color)componentColors[0]);
    paramGraphics2D.fill(rect);
  }
  
  private void paintBackgroundDisabledAndNotInScrollPane(Graphics2D paramGraphics2D)
  {
    rect = decodeRect2();
    paramGraphics2D.setPaint(color1);
    paramGraphics2D.fill(rect);
  }
  
  private void paintBackgroundEnabledAndNotInScrollPane(Graphics2D paramGraphics2D)
  {
    rect = decodeRect2();
    paramGraphics2D.setPaint((Color)componentColors[0]);
    paramGraphics2D.fill(rect);
  }
  
  private void paintBackgroundSelected(Graphics2D paramGraphics2D)
  {
    rect = decodeRect2();
    paramGraphics2D.setPaint(color2);
    paramGraphics2D.fill(rect);
  }
  
  private void paintBorderDisabledAndNotInScrollPane(Graphics2D paramGraphics2D)
  {
    rect = decodeRect3();
    paramGraphics2D.setPaint(decodeGradient1(rect));
    paramGraphics2D.fill(rect);
    rect = decodeRect4();
    paramGraphics2D.setPaint(decodeGradient2(rect));
    paramGraphics2D.fill(rect);
    rect = decodeRect5();
    paramGraphics2D.setPaint(color6);
    paramGraphics2D.fill(rect);
    rect = decodeRect6();
    paramGraphics2D.setPaint(color4);
    paramGraphics2D.fill(rect);
    rect = decodeRect7();
    paramGraphics2D.setPaint(color4);
    paramGraphics2D.fill(rect);
  }
  
  private void paintBorderFocusedAndNotInScrollPane(Graphics2D paramGraphics2D)
  {
    rect = decodeRect8();
    paramGraphics2D.setPaint(decodeGradient3(rect));
    paramGraphics2D.fill(rect);
    rect = decodeRect9();
    paramGraphics2D.setPaint(decodeGradient4(rect));
    paramGraphics2D.fill(rect);
    rect = decodeRect10();
    paramGraphics2D.setPaint(color10);
    paramGraphics2D.fill(rect);
    rect = decodeRect11();
    paramGraphics2D.setPaint(color10);
    paramGraphics2D.fill(rect);
    rect = decodeRect12();
    paramGraphics2D.setPaint(color11);
    paramGraphics2D.fill(rect);
    path = decodePath1();
    paramGraphics2D.setPaint(color12);
    paramGraphics2D.fill(path);
  }
  
  private void paintBorderEnabledAndNotInScrollPane(Graphics2D paramGraphics2D)
  {
    rect = decodeRect8();
    paramGraphics2D.setPaint(decodeGradient5(rect));
    paramGraphics2D.fill(rect);
    rect = decodeRect9();
    paramGraphics2D.setPaint(decodeGradient4(rect));
    paramGraphics2D.fill(rect);
    rect = decodeRect10();
    paramGraphics2D.setPaint(color10);
    paramGraphics2D.fill(rect);
    rect = decodeRect11();
    paramGraphics2D.setPaint(color10);
    paramGraphics2D.fill(rect);
    rect = decodeRect12();
    paramGraphics2D.setPaint(color11);
    paramGraphics2D.fill(rect);
  }
  
  private Rectangle2D decodeRect1()
  {
    rect.setRect(decodeX(0.0F), decodeY(0.0F), decodeX(3.0F) - decodeX(0.0F), decodeY(3.0F) - decodeY(0.0F));
    return rect;
  }
  
  private Rectangle2D decodeRect2()
  {
    rect.setRect(decodeX(0.4F), decodeY(0.4F), decodeX(2.6F) - decodeX(0.4F), decodeY(2.6F) - decodeY(0.4F));
    return rect;
  }
  
  private Rectangle2D decodeRect3()
  {
    rect.setRect(decodeX(0.6666667F), decodeY(0.4F), decodeX(2.3333333F) - decodeX(0.6666667F), decodeY(1.0F) - decodeY(0.4F));
    return rect;
  }
  
  private Rectangle2D decodeRect4()
  {
    rect.setRect(decodeX(1.0F), decodeY(0.6F), decodeX(2.0F) - decodeX(1.0F), decodeY(1.0F) - decodeY(0.6F));
    return rect;
  }
  
  private Rectangle2D decodeRect5()
  {
    rect.setRect(decodeX(0.6666667F), decodeY(1.0F), decodeX(1.0F) - decodeX(0.6666667F), decodeY(2.0F) - decodeY(1.0F));
    return rect;
  }
  
  private Rectangle2D decodeRect6()
  {
    rect.setRect(decodeX(0.6666667F), decodeY(2.3333333F), decodeX(2.3333333F) - decodeX(0.6666667F), decodeY(2.0F) - decodeY(2.3333333F));
    return rect;
  }
  
  private Rectangle2D decodeRect7()
  {
    rect.setRect(decodeX(2.0F), decodeY(1.0F), decodeX(2.3333333F) - decodeX(2.0F), decodeY(2.0F) - decodeY(1.0F));
    return rect;
  }
  
  private Rectangle2D decodeRect8()
  {
    rect.setRect(decodeX(0.4F), decodeY(0.4F), decodeX(2.6F) - decodeX(0.4F), decodeY(1.0F) - decodeY(0.4F));
    return rect;
  }
  
  private Rectangle2D decodeRect9()
  {
    rect.setRect(decodeX(0.6F), decodeY(0.6F), decodeX(2.4F) - decodeX(0.6F), decodeY(1.0F) - decodeY(0.6F));
    return rect;
  }
  
  private Rectangle2D decodeRect10()
  {
    rect.setRect(decodeX(0.4F), decodeY(1.0F), decodeX(0.6F) - decodeX(0.4F), decodeY(2.6F) - decodeY(1.0F));
    return rect;
  }
  
  private Rectangle2D decodeRect11()
  {
    rect.setRect(decodeX(2.4F), decodeY(1.0F), decodeX(2.6F) - decodeX(2.4F), decodeY(2.6F) - decodeY(1.0F));
    return rect;
  }
  
  private Rectangle2D decodeRect12()
  {
    rect.setRect(decodeX(0.6F), decodeY(2.4F), decodeX(2.4F) - decodeX(0.6F), decodeY(2.6F) - decodeY(2.4F));
    return rect;
  }
  
  private Path2D decodePath1()
  {
    path.reset();
    path.moveTo(decodeX(0.4F), decodeY(0.4F));
    path.lineTo(decodeX(0.4F), decodeY(2.6F));
    path.lineTo(decodeX(2.6F), decodeY(2.6F));
    path.lineTo(decodeX(2.6F), decodeY(0.4F));
    path.curveTo(decodeAnchorX(2.6F, 0.0F), decodeAnchorY(0.4F, 0.0F), decodeAnchorX(2.8800004F, 0.1F), decodeAnchorY(0.4F, 0.0F), decodeX(2.8800004F), decodeY(0.4F));
    path.curveTo(decodeAnchorX(2.8800004F, 0.1F), decodeAnchorY(0.4F, 0.0F), decodeAnchorX(2.8800004F, 0.0F), decodeAnchorY(2.8799999F, 0.0F), decodeX(2.8800004F), decodeY(2.8799999F));
    path.lineTo(decodeX(0.120000005F), decodeY(2.8799999F));
    path.lineTo(decodeX(0.120000005F), decodeY(0.120000005F));
    path.lineTo(decodeX(2.8800004F), decodeY(0.120000005F));
    path.lineTo(decodeX(2.8800004F), decodeY(0.4F));
    path.lineTo(decodeX(0.4F), decodeY(0.4F));
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
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color3, decodeColor(color3, color4, 0.5F), color4 });
  }
  
  private Paint decodeGradient2(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color5, decodeColor(color5, color1, 0.5F), color1 });
  }
  
  private Paint decodeGradient3(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25F * f3 + f1, 0.1625F * f4 + f2, new float[] { 0.1F, 0.49999997F, 0.9F }, new Color[] { color7, decodeColor(color7, color8, 0.5F), color8 });
  }
  
  private Paint decodeGradient4(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.1F, 0.49999997F, 0.9F }, new Color[] { (Color)componentColors[0], decodeColor((Color)componentColors[0], (Color)componentColors[1], 0.5F), (Color)componentColors[1] });
  }
  
  private Paint decodeGradient5(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.1F, 0.49999997F, 0.9F }, new Color[] { color7, decodeColor(color7, color8, 0.5F), color8 });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\nimbus\TextAreaPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */