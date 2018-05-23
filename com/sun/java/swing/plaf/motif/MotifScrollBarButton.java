package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.ButtonModel;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicArrowButton;

public class MotifScrollBarButton
  extends BasicArrowButton
{
  private Color darkShadow = UIManager.getColor("controlShadow");
  private Color lightShadow = UIManager.getColor("controlLtHighlight");
  
  public MotifScrollBarButton(int paramInt)
  {
    super(paramInt);
    switch (paramInt)
    {
    case 1: 
    case 3: 
    case 5: 
    case 7: 
      direction = paramInt;
      break;
    case 2: 
    case 4: 
    case 6: 
    default: 
      throw new IllegalArgumentException("invalid direction");
    }
    setRequestFocusEnabled(false);
    setOpaque(true);
    setBackground(UIManager.getColor("ScrollBar.background"));
    setForeground(UIManager.getColor("ScrollBar.foreground"));
  }
  
  public Dimension getPreferredSize()
  {
    switch (direction)
    {
    case 1: 
    case 5: 
      return new Dimension(11, 12);
    }
    return new Dimension(12, 11);
  }
  
  public Dimension getMinimumSize()
  {
    return getPreferredSize();
  }
  
  public Dimension getMaximumSize()
  {
    return getPreferredSize();
  }
  
  public boolean isFocusTraversable()
  {
    return false;
  }
  
  public void paint(Graphics paramGraphics)
  {
    int i = getWidth();
    int j = getHeight();
    if (isOpaque())
    {
      paramGraphics.setColor(getBackground());
      paramGraphics.fillRect(0, 0, i, j);
    }
    boolean bool = getModel().isPressed();
    Color localColor1 = bool ? darkShadow : lightShadow;
    Color localColor2 = bool ? lightShadow : darkShadow;
    Color localColor3 = getBackground();
    int k = i / 2;
    int m = j / 2;
    int n = Math.min(i, j);
    int i1;
    int i2;
    int i3;
    switch (direction)
    {
    case 1: 
      paramGraphics.setColor(localColor1);
      paramGraphics.drawLine(k, 0, k, 0);
      i1 = k - 1;
      i2 = 1;
      i3 = 1;
      while (i2 <= n - 2)
      {
        paramGraphics.setColor(localColor1);
        paramGraphics.drawLine(i1, i2, i1, i2);
        if (i2 >= n - 2) {
          paramGraphics.drawLine(i1, i2 + 1, i1, i2 + 1);
        }
        paramGraphics.setColor(localColor3);
        paramGraphics.drawLine(i1 + 1, i2, i1 + i3, i2);
        if (i2 < n - 2) {
          paramGraphics.drawLine(i1, i2 + 1, i1 + i3 + 1, i2 + 1);
        }
        paramGraphics.setColor(localColor2);
        paramGraphics.drawLine(i1 + i3 + 1, i2, i1 + i3 + 1, i2);
        if (i2 >= n - 2) {
          paramGraphics.drawLine(i1 + 1, i2 + 1, i1 + i3 + 1, i2 + 1);
        }
        i3 += 2;
        i1--;
        i2 += 2;
      }
      break;
    case 5: 
      paramGraphics.setColor(localColor2);
      paramGraphics.drawLine(k, n, k, n);
      i1 = k - 1;
      i2 = n - 1;
      i3 = 1;
      while (i2 >= 1)
      {
        paramGraphics.setColor(localColor1);
        paramGraphics.drawLine(i1, i2, i1, i2);
        if (i2 <= 2) {
          paramGraphics.drawLine(i1, i2 - 1, i1 + i3 + 1, i2 - 1);
        }
        paramGraphics.setColor(localColor3);
        paramGraphics.drawLine(i1 + 1, i2, i1 + i3, i2);
        if (i2 > 2) {
          paramGraphics.drawLine(i1, i2 - 1, i1 + i3 + 1, i2 - 1);
        }
        paramGraphics.setColor(localColor2);
        paramGraphics.drawLine(i1 + i3 + 1, i2, i1 + i3 + 1, i2);
        i3 += 2;
        i1--;
        i2 -= 2;
      }
      break;
    case 3: 
      paramGraphics.setColor(localColor1);
      paramGraphics.drawLine(n, m, n, m);
      i1 = m - 1;
      i2 = n - 1;
      i3 = 1;
      while (i2 >= 1)
      {
        paramGraphics.setColor(localColor1);
        paramGraphics.drawLine(i2, i1, i2, i1);
        if (i2 <= 2) {
          paramGraphics.drawLine(i2 - 1, i1, i2 - 1, i1 + i3 + 1);
        }
        paramGraphics.setColor(localColor3);
        paramGraphics.drawLine(i2, i1 + 1, i2, i1 + i3);
        if (i2 > 2) {
          paramGraphics.drawLine(i2 - 1, i1, i2 - 1, i1 + i3 + 1);
        }
        paramGraphics.setColor(localColor2);
        paramGraphics.drawLine(i2, i1 + i3 + 1, i2, i1 + i3 + 1);
        i3 += 2;
        i1--;
        i2 -= 2;
      }
      break;
    case 7: 
      paramGraphics.setColor(localColor2);
      paramGraphics.drawLine(0, m, 0, m);
      i1 = m - 1;
      i2 = 1;
      i3 = 1;
      while (i2 <= n - 2)
      {
        paramGraphics.setColor(localColor1);
        paramGraphics.drawLine(i2, i1, i2, i1);
        if (i2 >= n - 2) {
          paramGraphics.drawLine(i2 + 1, i1, i2 + 1, i1);
        }
        paramGraphics.setColor(localColor3);
        paramGraphics.drawLine(i2, i1 + 1, i2, i1 + i3);
        if (i2 < n - 2) {
          paramGraphics.drawLine(i2 + 1, i1, i2 + 1, i1 + i3 + 1);
        }
        paramGraphics.setColor(localColor2);
        paramGraphics.drawLine(i2, i1 + i3 + 1, i2, i1 + i3 + 1);
        if (i2 >= n - 2) {
          paramGraphics.drawLine(i2 + 1, i1 + 1, i2 + 1, i1 + i3 + 1);
        }
        i3 += 2;
        i1--;
        i2 += 2;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\motif\MotifScrollBarButton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */