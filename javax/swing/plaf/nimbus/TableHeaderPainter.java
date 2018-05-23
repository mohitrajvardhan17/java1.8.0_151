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

final class TableHeaderPainter
  extends AbstractRegionPainter
{
  static final int ASCENDINGSORTICON_ENABLED = 1;
  static final int DESCENDINGSORTICON_ENABLED = 2;
  private int state;
  private AbstractRegionPainter.PaintContext ctx;
  private Path2D path = new Path2D.Float();
  private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
  private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  private Color color1 = decodeColor("nimbusBase", 0.0057927966F, -0.21904764F, 0.15686274F, 0);
  private Color color2 = decodeColor("nimbusBase", 0.0038565993F, 0.02012986F, 0.054901958F, 0);
  private Object[] componentColors;
  
  public TableHeaderPainter(AbstractRegionPainter.PaintContext paramPaintContext, int paramInt)
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
      paintascendingSortIconEnabled(paramGraphics2D);
      break;
    case 2: 
      paintdescendingSortIconEnabled(paramGraphics2D);
    }
  }
  
  protected final AbstractRegionPainter.PaintContext getPaintContext()
  {
    return ctx;
  }
  
  private void paintascendingSortIconEnabled(Graphics2D paramGraphics2D)
  {
    path = decodePath1();
    paramGraphics2D.setPaint(decodeGradient1(path));
    paramGraphics2D.fill(path);
  }
  
  private void paintdescendingSortIconEnabled(Graphics2D paramGraphics2D)
  {
    path = decodePath2();
    paramGraphics2D.setPaint(decodeGradient1(path));
    paramGraphics2D.fill(path);
  }
  
  private Path2D decodePath1()
  {
    path.reset();
    path.moveTo(decodeX(1.0F), decodeY(2.0F));
    path.lineTo(decodeX(1.7070175F), decodeY(0.0F));
    path.lineTo(decodeX(3.0F), decodeY(2.0F));
    path.lineTo(decodeX(1.0F), decodeY(2.0F));
    path.closePath();
    return path;
  }
  
  private Path2D decodePath2()
  {
    path.reset();
    path.moveTo(decodeX(1.0F), decodeY(1.0F));
    path.lineTo(decodeX(2.0F), decodeY(1.0F));
    path.lineTo(decodeX(1.5025063F), decodeY(2.0F));
    path.lineTo(decodeX(1.0F), decodeY(1.0F));
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
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\nimbus\TableHeaderPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */