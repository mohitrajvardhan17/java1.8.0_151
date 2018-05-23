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

final class ScrollBarButtonPainter
  extends AbstractRegionPainter
{
  static final int FOREGROUND_ENABLED = 1;
  static final int FOREGROUND_DISABLED = 2;
  static final int FOREGROUND_MOUSEOVER = 3;
  static final int FOREGROUND_PRESSED = 4;
  private int state;
  private AbstractRegionPainter.PaintContext ctx;
  private Path2D path = new Path2D.Float();
  private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
  private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  private Color color1 = new Color(255, 200, 0, 255);
  private Color color2 = decodeColor("nimbusBlueGrey", -0.01111114F, -0.07763158F, -0.1490196F, 0);
  private Color color3 = decodeColor("nimbusBlueGrey", -0.111111104F, -0.10580933F, 0.086274505F, 0);
  private Color color4 = decodeColor("nimbusBlueGrey", -0.027777791F, -0.102261856F, 0.20392156F, 0);
  private Color color5 = decodeColor("nimbusBlueGrey", -0.039682567F, -0.079276316F, 0.13333333F, 0);
  private Color color6 = decodeColor("nimbusBlueGrey", -0.027777791F, -0.07382907F, 0.109803915F, 0);
  private Color color7 = decodeColor("nimbusBlueGrey", -0.039682567F, -0.08241387F, 0.23137254F, 0);
  private Color color8 = decodeColor("nimbusBlueGrey", -0.055555522F, -0.08443936F, -0.29411766F, 65400);
  private Color color9 = decodeColor("nimbusBlueGrey", -0.055555522F, -0.09876161F, 0.25490195F, 65358);
  private Color color10 = decodeColor("nimbusBlueGrey", 0.055555582F, -0.08878718F, -0.5647059F, 0);
  private Color color11 = decodeColor("nimbusBlueGrey", -0.027777791F, -0.080223285F, -0.4862745F, 0);
  private Color color12 = decodeColor("nimbusBlueGrey", -0.111111104F, -0.09525914F, -0.23137254F, 0);
  private Color color13 = decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, 65371);
  private Color color14 = decodeColor("nimbusBlueGrey", -0.04444444F, -0.080223285F, -0.09803921F, 0);
  private Color color15 = decodeColor("nimbusBlueGrey", -0.6111111F, -0.110526316F, 0.10588235F, 0);
  private Color color16 = decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, 0);
  private Color color17 = decodeColor("nimbusBlueGrey", -0.039682567F, -0.081719734F, 0.20784312F, 0);
  private Color color18 = decodeColor("nimbusBlueGrey", -0.027777791F, -0.07677104F, 0.18431371F, 0);
  private Color color19 = decodeColor("nimbusBlueGrey", -0.04444444F, -0.080223285F, -0.09803921F, -69);
  private Color color20 = decodeColor("nimbusBlueGrey", -0.055555522F, -0.09876161F, 0.25490195F, -39);
  private Color color21 = decodeColor("nimbusBlueGrey", 0.055555582F, -0.0951417F, -0.49019608F, 0);
  private Color color22 = decodeColor("nimbusBlueGrey", -0.027777791F, -0.086996906F, -0.4117647F, 0);
  private Color color23 = decodeColor("nimbusBlueGrey", -0.111111104F, -0.09719298F, -0.15686274F, 0);
  private Color color24 = decodeColor("nimbusBlueGrey", -0.037037015F, -0.043859646F, -0.21568626F, 0);
  private Color color25 = decodeColor("nimbusBlueGrey", -0.06349206F, -0.07309316F, -0.011764705F, 0);
  private Color color26 = decodeColor("nimbusBlueGrey", -0.048611104F, -0.07296763F, 0.09019607F, 0);
  private Color color27 = decodeColor("nimbusBlueGrey", -0.03535354F, -0.05497076F, 0.031372547F, 0);
  private Color color28 = decodeColor("nimbusBlueGrey", -0.034188032F, -0.043168806F, 0.011764705F, 0);
  private Color color29 = decodeColor("nimbusBlueGrey", -0.03535354F, -0.0600676F, 0.109803915F, 0);
  private Color color30 = decodeColor("nimbusBlueGrey", -0.037037015F, -0.043859646F, -0.21568626F, -44);
  private Color color31 = decodeColor("nimbusBlueGrey", -0.6111111F, -0.110526316F, -0.74509805F, 0);
  private Object[] componentColors;
  
  public ScrollBarButtonPainter(AbstractRegionPainter.PaintContext paramPaintContext, int paramInt)
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
      paintForegroundEnabled(paramGraphics2D);
      break;
    case 2: 
      paintForegroundDisabled(paramGraphics2D);
      break;
    case 3: 
      paintForegroundMouseOver(paramGraphics2D);
      break;
    case 4: 
      paintForegroundPressed(paramGraphics2D);
    }
  }
  
  protected final AbstractRegionPainter.PaintContext getPaintContext()
  {
    return ctx;
  }
  
  private void paintForegroundEnabled(Graphics2D paramGraphics2D)
  {
    path = decodePath1();
    paramGraphics2D.setPaint(color1);
    paramGraphics2D.fill(path);
    path = decodePath2();
    paramGraphics2D.setPaint(decodeGradient1(path));
    paramGraphics2D.fill(path);
    path = decodePath3();
    paramGraphics2D.setPaint(decodeGradient2(path));
    paramGraphics2D.fill(path);
    path = decodePath4();
    paramGraphics2D.setPaint(decodeGradient3(path));
    paramGraphics2D.fill(path);
    path = decodePath5();
    paramGraphics2D.setPaint(color13);
    paramGraphics2D.fill(path);
  }
  
  private void paintForegroundDisabled(Graphics2D paramGraphics2D)
  {
    path = decodePath1();
    paramGraphics2D.setPaint(color1);
    paramGraphics2D.fill(path);
  }
  
  private void paintForegroundMouseOver(Graphics2D paramGraphics2D)
  {
    path = decodePath1();
    paramGraphics2D.setPaint(color1);
    paramGraphics2D.fill(path);
    path = decodePath2();
    paramGraphics2D.setPaint(decodeGradient4(path));
    paramGraphics2D.fill(path);
    path = decodePath3();
    paramGraphics2D.setPaint(decodeGradient5(path));
    paramGraphics2D.fill(path);
    path = decodePath4();
    paramGraphics2D.setPaint(decodeGradient6(path));
    paramGraphics2D.fill(path);
    path = decodePath5();
    paramGraphics2D.setPaint(color13);
    paramGraphics2D.fill(path);
  }
  
  private void paintForegroundPressed(Graphics2D paramGraphics2D)
  {
    path = decodePath1();
    paramGraphics2D.setPaint(color1);
    paramGraphics2D.fill(path);
    path = decodePath2();
    paramGraphics2D.setPaint(decodeGradient7(path));
    paramGraphics2D.fill(path);
    path = decodePath3();
    paramGraphics2D.setPaint(decodeGradient8(path));
    paramGraphics2D.fill(path);
    path = decodePath4();
    paramGraphics2D.setPaint(color31);
    paramGraphics2D.fill(path);
    path = decodePath5();
    paramGraphics2D.setPaint(color13);
    paramGraphics2D.fill(path);
  }
  
  private Path2D decodePath1()
  {
    path.reset();
    path.moveTo(decodeX(3.0F), decodeY(3.0F));
    path.lineTo(decodeX(3.0F), decodeY(3.0F));
    path.closePath();
    return path;
  }
  
  private Path2D decodePath2()
  {
    path.reset();
    path.moveTo(decodeX(0.0F), decodeY(0.0F));
    path.lineTo(decodeX(1.6956522F), decodeY(0.0F));
    path.curveTo(decodeAnchorX(1.6956522F, 0.0F), decodeAnchorY(0.0F, 0.0F), decodeAnchorX(1.6956522F, -0.7058824F), decodeAnchorY(1.3076923F, -3.0294118F), decodeX(1.6956522F), decodeY(1.3076923F));
    path.curveTo(decodeAnchorX(1.6956522F, 0.7058824F), decodeAnchorY(1.3076923F, 3.0294118F), decodeAnchorX(1.826087F, -2.0F), decodeAnchorY(1.7692308F, -1.9411764F), decodeX(1.826087F), decodeY(1.7692308F));
    path.curveTo(decodeAnchorX(1.826087F, 2.0F), decodeAnchorY(1.7692308F, 1.9411764F), decodeAnchorX(3.0F, 0.0F), decodeAnchorY(2.0F, 0.0F), decodeX(3.0F), decodeY(2.0F));
    path.lineTo(decodeX(3.0F), decodeY(3.0F));
    path.lineTo(decodeX(0.0F), decodeY(3.0F));
    path.lineTo(decodeX(0.0F), decodeY(0.0F));
    path.closePath();
    return path;
  }
  
  private Path2D decodePath3()
  {
    path.reset();
    path.moveTo(decodeX(0.0F), decodeY(1.0022625F));
    path.lineTo(decodeX(0.9705882F), decodeY(1.0384616F));
    path.lineTo(decodeX(1.0409207F), decodeY(1.0791855F));
    path.lineTo(decodeX(1.0409207F), decodeY(3.0F));
    path.lineTo(decodeX(0.0F), decodeY(3.0F));
    path.lineTo(decodeX(0.0F), decodeY(1.0022625F));
    path.closePath();
    return path;
  }
  
  private Path2D decodePath4()
  {
    path.reset();
    path.moveTo(decodeX(1.4782609F), decodeY(1.2307693F));
    path.lineTo(decodeX(1.4782609F), decodeY(1.7692308F));
    path.lineTo(decodeX(1.1713555F), decodeY(1.5F));
    path.lineTo(decodeX(1.4782609F), decodeY(1.2307693F));
    path.closePath();
    return path;
  }
  
  private Path2D decodePath5()
  {
    path.reset();
    path.moveTo(decodeX(1.6713555F), decodeY(1.0769231F));
    path.curveTo(decodeAnchorX(1.6713555F, 0.7352941F), decodeAnchorY(1.0769231F, 0.0F), decodeAnchorX(1.7186701F, -0.9117647F), decodeAnchorY(1.4095023F, -2.2058823F), decodeX(1.7186701F), decodeY(1.4095023F));
    path.curveTo(decodeAnchorX(1.7186701F, 0.9117647F), decodeAnchorY(1.4095023F, 2.2058823F), decodeAnchorX(1.8439897F, -2.3529413F), decodeAnchorY(1.7941177F, -1.8529412F), decodeX(1.8439897F), decodeY(1.7941177F));
    path.curveTo(decodeAnchorX(1.8439897F, 2.3529413F), decodeAnchorY(1.7941177F, 1.8529412F), decodeAnchorX(2.5F, 0.0F), decodeAnchorY(2.2352943F, 0.0F), decodeX(2.5F), decodeY(2.2352943F));
    path.lineTo(decodeX(2.3529415F), decodeY(2.8235292F));
    path.curveTo(decodeAnchorX(2.3529415F, 0.0F), decodeAnchorY(2.8235292F, 0.0F), decodeAnchorX(1.8184143F, 1.5588236F), decodeAnchorY(1.8438914F, 1.382353F), decodeX(1.8184143F), decodeY(1.8438914F));
    path.curveTo(decodeAnchorX(1.8184143F, -1.5588236F), decodeAnchorY(1.8438914F, -1.382353F), decodeAnchorX(1.6943734F, 0.7941176F), decodeAnchorY(1.4841628F, 2.0F), decodeX(1.6943734F), decodeY(1.4841628F));
    path.curveTo(decodeAnchorX(1.6943734F, -0.7941176F), decodeAnchorY(1.4841628F, -2.0F), decodeAnchorX(1.6713555F, -0.7352941F), decodeAnchorY(1.0769231F, 0.0F), decodeX(1.6713555F), decodeY(1.0769231F));
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
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.032934133F, 0.065868266F, 0.089820355F, 0.11377245F, 0.23053892F, 0.3473054F, 0.494012F, 0.6407186F, 0.78443116F, 0.92814374F }, new Color[] { color2, decodeColor(color2, color3, 0.5F), color3, decodeColor(color3, color4, 0.5F), color4, decodeColor(color4, color5, 0.5F), color5, decodeColor(color5, color6, 0.5F), color6, decodeColor(color6, color7, 0.5F), color7 });
  }
  
  private Paint decodeGradient2(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.0F * f3 + f1, 0.5F * f4 + f2, 0.5735294F * f3 + f1, 0.5F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color8, decodeColor(color8, color9, 0.5F), color9 });
  }
  
  private Paint decodeGradient3(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.925F * f3 + f1, 0.9285714F * f4 + f2, 0.925F * f3 + f1, 0.004201681F * f4 + f2, new float[] { 0.0F, 0.2964072F, 0.5928144F, 0.79341316F, 0.994012F }, new Color[] { color10, decodeColor(color10, color11, 0.5F), color11, decodeColor(color11, color12, 0.5F), color12 });
  }
  
  private Paint decodeGradient4(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.032934133F, 0.065868266F, 0.089820355F, 0.11377245F, 0.23053892F, 0.3473054F, 0.494012F, 0.6407186F, 0.78443116F, 0.92814374F }, new Color[] { color14, decodeColor(color14, color15, 0.5F), color15, decodeColor(color15, color16, 0.5F), color16, decodeColor(color16, color17, 0.5F), color17, decodeColor(color17, color18, 0.5F), color18, decodeColor(color18, color16, 0.5F), color16 });
  }
  
  private Paint decodeGradient5(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.0F * f3 + f1, 0.5F * f4 + f2, 0.5735294F * f3 + f1, 0.5F * f4 + f2, new float[] { 0.19518717F, 0.5975936F, 1.0F }, new Color[] { color19, decodeColor(color19, color20, 0.5F), color20 });
  }
  
  private Paint decodeGradient6(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.925F * f3 + f1, 0.9285714F * f4 + f2, 0.925F * f3 + f1, 0.004201681F * f4 + f2, new float[] { 0.0F, 0.2964072F, 0.5928144F, 0.79341316F, 0.994012F }, new Color[] { color21, decodeColor(color21, color22, 0.5F), color22, decodeColor(color22, color23, 0.5F), color23 });
  }
  
  private Paint decodeGradient7(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.032934133F, 0.065868266F, 0.089820355F, 0.11377245F, 0.23053892F, 0.3473054F, 0.494012F, 0.6407186F, 0.78443116F, 0.92814374F }, new Color[] { color24, decodeColor(color24, color25, 0.5F), color25, decodeColor(color25, color26, 0.5F), color26, decodeColor(color26, color27, 0.5F), color27, decodeColor(color27, color28, 0.5F), color28, decodeColor(color28, color29, 0.5F), color29 });
  }
  
  private Paint decodeGradient8(Shape paramShape)
  {
    Rectangle2D localRectangle2D = paramShape.getBounds2D();
    float f1 = (float)localRectangle2D.getX();
    float f2 = (float)localRectangle2D.getY();
    float f3 = (float)localRectangle2D.getWidth();
    float f4 = (float)localRectangle2D.getHeight();
    return decodeGradient(0.0F * f3 + f1, 0.5F * f4 + f2, 0.5735294F * f3 + f1, 0.5F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { color30, decodeColor(color30, color9, 0.5F), color9 });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\nimbus\ScrollBarButtonPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */