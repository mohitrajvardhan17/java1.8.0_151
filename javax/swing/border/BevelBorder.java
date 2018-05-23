package javax.swing.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.beans.ConstructorProperties;

public class BevelBorder
  extends AbstractBorder
{
  public static final int RAISED = 0;
  public static final int LOWERED = 1;
  protected int bevelType;
  protected Color highlightOuter;
  protected Color highlightInner;
  protected Color shadowInner;
  protected Color shadowOuter;
  
  public BevelBorder(int paramInt)
  {
    bevelType = paramInt;
  }
  
  public BevelBorder(int paramInt, Color paramColor1, Color paramColor2)
  {
    this(paramInt, paramColor1.brighter(), paramColor1, paramColor2, paramColor2.brighter());
  }
  
  @ConstructorProperties({"bevelType", "highlightOuterColor", "highlightInnerColor", "shadowOuterColor", "shadowInnerColor"})
  public BevelBorder(int paramInt, Color paramColor1, Color paramColor2, Color paramColor3, Color paramColor4)
  {
    this(paramInt);
    highlightOuter = paramColor1;
    highlightInner = paramColor2;
    shadowOuter = paramColor3;
    shadowInner = paramColor4;
  }
  
  public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (bevelType == 0) {
      paintRaisedBevel(paramComponent, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    } else if (bevelType == 1) {
      paintLoweredBevel(paramComponent, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
  }
  
  public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
  {
    paramInsets.set(2, 2, 2, 2);
    return paramInsets;
  }
  
  public Color getHighlightOuterColor(Component paramComponent)
  {
    Color localColor = getHighlightOuterColor();
    return localColor != null ? localColor : paramComponent.getBackground().brighter().brighter();
  }
  
  public Color getHighlightInnerColor(Component paramComponent)
  {
    Color localColor = getHighlightInnerColor();
    return localColor != null ? localColor : paramComponent.getBackground().brighter();
  }
  
  public Color getShadowInnerColor(Component paramComponent)
  {
    Color localColor = getShadowInnerColor();
    return localColor != null ? localColor : paramComponent.getBackground().darker();
  }
  
  public Color getShadowOuterColor(Component paramComponent)
  {
    Color localColor = getShadowOuterColor();
    return localColor != null ? localColor : paramComponent.getBackground().darker().darker();
  }
  
  public Color getHighlightOuterColor()
  {
    return highlightOuter;
  }
  
  public Color getHighlightInnerColor()
  {
    return highlightInner;
  }
  
  public Color getShadowInnerColor()
  {
    return shadowInner;
  }
  
  public Color getShadowOuterColor()
  {
    return shadowOuter;
  }
  
  public int getBevelType()
  {
    return bevelType;
  }
  
  public boolean isBorderOpaque()
  {
    return true;
  }
  
  protected void paintRaisedBevel(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Color localColor = paramGraphics.getColor();
    int i = paramInt4;
    int j = paramInt3;
    paramGraphics.translate(paramInt1, paramInt2);
    paramGraphics.setColor(getHighlightOuterColor(paramComponent));
    paramGraphics.drawLine(0, 0, 0, i - 2);
    paramGraphics.drawLine(1, 0, j - 2, 0);
    paramGraphics.setColor(getHighlightInnerColor(paramComponent));
    paramGraphics.drawLine(1, 1, 1, i - 3);
    paramGraphics.drawLine(2, 1, j - 3, 1);
    paramGraphics.setColor(getShadowOuterColor(paramComponent));
    paramGraphics.drawLine(0, i - 1, j - 1, i - 1);
    paramGraphics.drawLine(j - 1, 0, j - 1, i - 2);
    paramGraphics.setColor(getShadowInnerColor(paramComponent));
    paramGraphics.drawLine(1, i - 2, j - 2, i - 2);
    paramGraphics.drawLine(j - 2, 1, j - 2, i - 3);
    paramGraphics.translate(-paramInt1, -paramInt2);
    paramGraphics.setColor(localColor);
  }
  
  protected void paintLoweredBevel(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Color localColor = paramGraphics.getColor();
    int i = paramInt4;
    int j = paramInt3;
    paramGraphics.translate(paramInt1, paramInt2);
    paramGraphics.setColor(getShadowInnerColor(paramComponent));
    paramGraphics.drawLine(0, 0, 0, i - 1);
    paramGraphics.drawLine(1, 0, j - 1, 0);
    paramGraphics.setColor(getShadowOuterColor(paramComponent));
    paramGraphics.drawLine(1, 1, 1, i - 2);
    paramGraphics.drawLine(2, 1, j - 2, 1);
    paramGraphics.setColor(getHighlightOuterColor(paramComponent));
    paramGraphics.drawLine(1, i - 1, j - 1, i - 1);
    paramGraphics.drawLine(j - 1, 1, j - 1, i - 2);
    paramGraphics.setColor(getHighlightInnerColor(paramComponent));
    paramGraphics.drawLine(2, i - 2, j - 2, i - 2);
    paramGraphics.drawLine(j - 2, 2, j - 2, i - 3);
    paramGraphics.translate(-paramInt1, -paramInt2);
    paramGraphics.setColor(localColor);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\border\BevelBorder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */