package javax.swing.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Path2D.Float;
import java.awt.geom.Rectangle2D.Float;
import java.awt.geom.RoundRectangle2D.Float;
import java.beans.ConstructorProperties;

public class LineBorder
  extends AbstractBorder
{
  private static Border blackLine;
  private static Border grayLine;
  protected int thickness;
  protected Color lineColor;
  protected boolean roundedCorners;
  
  public static Border createBlackLineBorder()
  {
    if (blackLine == null) {
      blackLine = new LineBorder(Color.black, 1);
    }
    return blackLine;
  }
  
  public static Border createGrayLineBorder()
  {
    if (grayLine == null) {
      grayLine = new LineBorder(Color.gray, 1);
    }
    return grayLine;
  }
  
  public LineBorder(Color paramColor)
  {
    this(paramColor, 1, false);
  }
  
  public LineBorder(Color paramColor, int paramInt)
  {
    this(paramColor, paramInt, false);
  }
  
  @ConstructorProperties({"lineColor", "thickness", "roundedCorners"})
  public LineBorder(Color paramColor, int paramInt, boolean paramBoolean)
  {
    lineColor = paramColor;
    thickness = paramInt;
    roundedCorners = paramBoolean;
  }
  
  public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((thickness > 0) && ((paramGraphics instanceof Graphics2D)))
    {
      Graphics2D localGraphics2D = (Graphics2D)paramGraphics;
      Color localColor = localGraphics2D.getColor();
      localGraphics2D.setColor(lineColor);
      int i = thickness;
      int j = i + i;
      Object localObject1;
      Object localObject2;
      if (roundedCorners)
      {
        float f = 0.2F * i;
        localObject1 = new RoundRectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4, i, i);
        localObject2 = new RoundRectangle2D.Float(paramInt1 + i, paramInt2 + i, paramInt3 - j, paramInt4 - j, f, f);
      }
      else
      {
        localObject1 = new Rectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4);
        localObject2 = new Rectangle2D.Float(paramInt1 + i, paramInt2 + i, paramInt3 - j, paramInt4 - j);
      }
      Path2D.Float localFloat = new Path2D.Float(0);
      localFloat.append((Shape)localObject1, false);
      localFloat.append((Shape)localObject2, false);
      localGraphics2D.fill(localFloat);
      localGraphics2D.setColor(localColor);
    }
  }
  
  public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
  {
    paramInsets.set(thickness, thickness, thickness, thickness);
    return paramInsets;
  }
  
  public Color getLineColor()
  {
    return lineColor;
  }
  
  public int getThickness()
  {
    return thickness;
  }
  
  public boolean getRoundedCorners()
  {
    return roundedCorners;
  }
  
  public boolean isBorderOpaque()
  {
    return !roundedCorners;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\border\LineBorder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */