package javax.swing.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.beans.ConstructorProperties;

public class EtchedBorder
  extends AbstractBorder
{
  public static final int RAISED = 0;
  public static final int LOWERED = 1;
  protected int etchType;
  protected Color highlight;
  protected Color shadow;
  
  public EtchedBorder()
  {
    this(1);
  }
  
  public EtchedBorder(int paramInt)
  {
    this(paramInt, null, null);
  }
  
  public EtchedBorder(Color paramColor1, Color paramColor2)
  {
    this(1, paramColor1, paramColor2);
  }
  
  @ConstructorProperties({"etchType", "highlightColor", "shadowColor"})
  public EtchedBorder(int paramInt, Color paramColor1, Color paramColor2)
  {
    etchType = paramInt;
    highlight = paramColor1;
    shadow = paramColor2;
  }
  
  public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = paramInt3;
    int j = paramInt4;
    paramGraphics.translate(paramInt1, paramInt2);
    paramGraphics.setColor(etchType == 1 ? getShadowColor(paramComponent) : getHighlightColor(paramComponent));
    paramGraphics.drawRect(0, 0, i - 2, j - 2);
    paramGraphics.setColor(etchType == 1 ? getHighlightColor(paramComponent) : getShadowColor(paramComponent));
    paramGraphics.drawLine(1, j - 3, 1, 1);
    paramGraphics.drawLine(1, 1, i - 3, 1);
    paramGraphics.drawLine(0, j - 1, i - 1, j - 1);
    paramGraphics.drawLine(i - 1, j - 1, i - 1, 0);
    paramGraphics.translate(-paramInt1, -paramInt2);
  }
  
  public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
  {
    paramInsets.set(2, 2, 2, 2);
    return paramInsets;
  }
  
  public boolean isBorderOpaque()
  {
    return true;
  }
  
  public int getEtchType()
  {
    return etchType;
  }
  
  public Color getHighlightColor(Component paramComponent)
  {
    return highlight != null ? highlight : paramComponent.getBackground().brighter();
  }
  
  public Color getHighlightColor()
  {
    return highlight;
  }
  
  public Color getShadowColor(Component paramComponent)
  {
    return shadow != null ? shadow : paramComponent.getBackground().darker();
  }
  
  public Color getShadowColor()
  {
    return shadow;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\border\EtchedBorder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */