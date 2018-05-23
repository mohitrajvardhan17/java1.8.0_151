package javax.swing.plaf.metal;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.ButtonModel;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.basic.BasicArrowButton;

public class MetalScrollButton
  extends BasicArrowButton
{
  private static Color shadowColor;
  private static Color highlightColor;
  private boolean isFreeStanding = false;
  private int buttonWidth;
  
  public MetalScrollButton(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    super(paramInt1);
    shadowColor = UIManager.getColor("ScrollBar.darkShadow");
    highlightColor = UIManager.getColor("ScrollBar.highlight");
    buttonWidth = paramInt2;
    isFreeStanding = paramBoolean;
  }
  
  public void setFreeStanding(boolean paramBoolean)
  {
    isFreeStanding = paramBoolean;
  }
  
  public void paint(Graphics paramGraphics)
  {
    boolean bool1 = MetalUtils.isLeftToRight(this);
    boolean bool2 = getParent().isEnabled();
    ColorUIResource localColorUIResource = bool2 ? MetalLookAndFeel.getControlInfo() : MetalLookAndFeel.getControlDisabled();
    boolean bool3 = getModel().isPressed();
    int i = getWidth();
    int j = getHeight();
    int k = i;
    int m = j;
    int n = (j + 1) / 4;
    int i1 = (j + 1) / 2;
    if (bool3) {
      paramGraphics.setColor(MetalLookAndFeel.getControlShadow());
    } else {
      paramGraphics.setColor(getBackground());
    }
    paramGraphics.fillRect(0, 0, i, j);
    int i2;
    int i3;
    int i4;
    if (getDirection() == 1)
    {
      if (!isFreeStanding)
      {
        j++;
        paramGraphics.translate(0, -1);
        i += 2;
        if (!bool1) {
          paramGraphics.translate(-1, 0);
        }
      }
      paramGraphics.setColor(localColorUIResource);
      i2 = (m + 1 - n) / 2;
      i3 = k / 2;
      for (i4 = 0; i4 < n; i4++) {
        paramGraphics.drawLine(i3 - i4, i2 + i4, i3 + i4 + 1, i2 + i4);
      }
      if (bool2)
      {
        paramGraphics.setColor(highlightColor);
        if (!bool3)
        {
          paramGraphics.drawLine(1, 1, i - 3, 1);
          paramGraphics.drawLine(1, 1, 1, j - 1);
        }
        paramGraphics.drawLine(i - 1, 1, i - 1, j - 1);
        paramGraphics.setColor(shadowColor);
        paramGraphics.drawLine(0, 0, i - 2, 0);
        paramGraphics.drawLine(0, 0, 0, j - 1);
        paramGraphics.drawLine(i - 2, 2, i - 2, j - 1);
      }
      else
      {
        MetalUtils.drawDisabledBorder(paramGraphics, 0, 0, i, j + 1);
      }
      if (!isFreeStanding)
      {
        j--;
        paramGraphics.translate(0, 1);
        i -= 2;
        if (!bool1) {
          paramGraphics.translate(1, 0);
        }
      }
    }
    else if (getDirection() == 5)
    {
      if (!isFreeStanding)
      {
        j++;
        i += 2;
        if (!bool1) {
          paramGraphics.translate(-1, 0);
        }
      }
      paramGraphics.setColor(localColorUIResource);
      i2 = (m + 1 - n) / 2 + n - 1;
      i3 = k / 2;
      for (i4 = 0; i4 < n; i4++) {
        paramGraphics.drawLine(i3 - i4, i2 - i4, i3 + i4 + 1, i2 - i4);
      }
      if (bool2)
      {
        paramGraphics.setColor(highlightColor);
        if (!bool3)
        {
          paramGraphics.drawLine(1, 0, i - 3, 0);
          paramGraphics.drawLine(1, 0, 1, j - 3);
        }
        paramGraphics.drawLine(1, j - 1, i - 1, j - 1);
        paramGraphics.drawLine(i - 1, 0, i - 1, j - 1);
        paramGraphics.setColor(shadowColor);
        paramGraphics.drawLine(0, 0, 0, j - 2);
        paramGraphics.drawLine(i - 2, 0, i - 2, j - 2);
        paramGraphics.drawLine(2, j - 2, i - 2, j - 2);
      }
      else
      {
        MetalUtils.drawDisabledBorder(paramGraphics, 0, -1, i, j + 1);
      }
      if (!isFreeStanding)
      {
        j--;
        i -= 2;
        if (!bool1) {
          paramGraphics.translate(1, 0);
        }
      }
    }
    else if (getDirection() == 3)
    {
      if (!isFreeStanding)
      {
        j += 2;
        i++;
      }
      paramGraphics.setColor(localColorUIResource);
      i2 = (k + 1 - n) / 2 + n - 1;
      i3 = m / 2;
      for (i4 = 0; i4 < n; i4++) {
        paramGraphics.drawLine(i2 - i4, i3 - i4, i2 - i4, i3 + i4 + 1);
      }
      if (bool2)
      {
        paramGraphics.setColor(highlightColor);
        if (!bool3)
        {
          paramGraphics.drawLine(0, 1, i - 3, 1);
          paramGraphics.drawLine(0, 1, 0, j - 3);
        }
        paramGraphics.drawLine(i - 1, 1, i - 1, j - 1);
        paramGraphics.drawLine(0, j - 1, i - 1, j - 1);
        paramGraphics.setColor(shadowColor);
        paramGraphics.drawLine(0, 0, i - 2, 0);
        paramGraphics.drawLine(i - 2, 2, i - 2, j - 2);
        paramGraphics.drawLine(0, j - 2, i - 2, j - 2);
      }
      else
      {
        MetalUtils.drawDisabledBorder(paramGraphics, -1, 0, i + 1, j);
      }
      if (!isFreeStanding)
      {
        j -= 2;
        i--;
      }
    }
    else if (getDirection() == 7)
    {
      if (!isFreeStanding)
      {
        j += 2;
        i++;
        paramGraphics.translate(-1, 0);
      }
      paramGraphics.setColor(localColorUIResource);
      i2 = (k + 1 - n) / 2;
      i3 = m / 2;
      for (i4 = 0; i4 < n; i4++) {
        paramGraphics.drawLine(i2 + i4, i3 - i4, i2 + i4, i3 + i4 + 1);
      }
      if (bool2)
      {
        paramGraphics.setColor(highlightColor);
        if (!bool3)
        {
          paramGraphics.drawLine(1, 1, i - 1, 1);
          paramGraphics.drawLine(1, 1, 1, j - 3);
        }
        paramGraphics.drawLine(1, j - 1, i - 1, j - 1);
        paramGraphics.setColor(shadowColor);
        paramGraphics.drawLine(0, 0, i - 1, 0);
        paramGraphics.drawLine(0, 0, 0, j - 2);
        paramGraphics.drawLine(2, j - 2, i - 1, j - 2);
      }
      else
      {
        MetalUtils.drawDisabledBorder(paramGraphics, 0, 0, i + 1, j);
      }
      if (!isFreeStanding)
      {
        j -= 2;
        i--;
        paramGraphics.translate(1, 0);
      }
    }
  }
  
  public Dimension getPreferredSize()
  {
    if (getDirection() == 1) {
      return new Dimension(buttonWidth, buttonWidth - 2);
    }
    if (getDirection() == 5) {
      return new Dimension(buttonWidth, buttonWidth - (isFreeStanding ? 1 : 2));
    }
    if (getDirection() == 3) {
      return new Dimension(buttonWidth - (isFreeStanding ? 1 : 2), buttonWidth);
    }
    if (getDirection() == 7) {
      return new Dimension(buttonWidth - 2, buttonWidth);
    }
    return new Dimension(0, 0);
  }
  
  public Dimension getMinimumSize()
  {
    return getPreferredSize();
  }
  
  public Dimension getMaximumSize()
  {
    return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
  }
  
  public int getButtonWidth()
  {
    return buttonWidth;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\metal\MetalScrollButton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */