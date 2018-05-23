package javax.swing.colorchooser;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

class SwatchPanel
  extends JPanel
{
  protected Color[] colors;
  protected Dimension swatchSize;
  protected Dimension numSwatches;
  protected Dimension gap;
  private int selRow;
  private int selCol;
  
  public SwatchPanel()
  {
    initValues();
    initColors();
    setToolTipText("");
    setOpaque(true);
    setBackground(Color.white);
    setFocusable(true);
    setInheritsPopupMenu(true);
    addFocusListener(new FocusAdapter()
    {
      public void focusGained(FocusEvent paramAnonymousFocusEvent)
      {
        repaint();
      }
      
      public void focusLost(FocusEvent paramAnonymousFocusEvent)
      {
        repaint();
      }
    });
    addKeyListener(new KeyAdapter()
    {
      public void keyPressed(KeyEvent paramAnonymousKeyEvent)
      {
        int i = paramAnonymousKeyEvent.getKeyCode();
        switch (i)
        {
        case 38: 
          if (selRow > 0)
          {
            SwatchPanel.access$010(SwatchPanel.this);
            repaint();
          }
          break;
        case 40: 
          if (selRow < numSwatches.height - 1)
          {
            SwatchPanel.access$008(SwatchPanel.this);
            repaint();
          }
          break;
        case 37: 
          if ((selCol > 0) && (getComponentOrientation().isLeftToRight()))
          {
            SwatchPanel.access$110(SwatchPanel.this);
            repaint();
          }
          else if ((selCol < numSwatches.width - 1) && (!getComponentOrientation().isLeftToRight()))
          {
            SwatchPanel.access$108(SwatchPanel.this);
            repaint();
          }
          break;
        case 39: 
          if ((selCol < numSwatches.width - 1) && (getComponentOrientation().isLeftToRight()))
          {
            SwatchPanel.access$108(SwatchPanel.this);
            repaint();
          }
          else if ((selCol > 0) && (!getComponentOrientation().isLeftToRight()))
          {
            SwatchPanel.access$110(SwatchPanel.this);
            repaint();
          }
          break;
        case 36: 
          selCol = 0;
          selRow = 0;
          repaint();
          break;
        case 35: 
          selCol = (numSwatches.width - 1);
          selRow = (numSwatches.height - 1);
          repaint();
        }
      }
    });
  }
  
  public Color getSelectedColor()
  {
    return getColorForCell(selCol, selRow);
  }
  
  protected void initValues() {}
  
  public void paintComponent(Graphics paramGraphics)
  {
    paramGraphics.setColor(getBackground());
    paramGraphics.fillRect(0, 0, getWidth(), getHeight());
    for (int i = 0; i < numSwatches.height; i++)
    {
      int j = i * (swatchSize.height + gap.height);
      for (int k = 0; k < numSwatches.width; k++)
      {
        Color localColor1 = getColorForCell(k, i);
        paramGraphics.setColor(localColor1);
        int m;
        if (!getComponentOrientation().isLeftToRight()) {
          m = (numSwatches.width - k - 1) * (swatchSize.width + gap.width);
        } else {
          m = k * (swatchSize.width + gap.width);
        }
        paramGraphics.fillRect(m, j, swatchSize.width, swatchSize.height);
        paramGraphics.setColor(Color.black);
        paramGraphics.drawLine(m + swatchSize.width - 1, j, m + swatchSize.width - 1, j + swatchSize.height - 1);
        paramGraphics.drawLine(m, j + swatchSize.height - 1, m + swatchSize.width - 1, j + swatchSize.height - 1);
        if ((selRow == i) && (selCol == k) && (isFocusOwner()))
        {
          Color localColor2 = new Color(localColor1.getRed() < 125 ? 255 : 0, localColor1.getGreen() < 125 ? 255 : 0, localColor1.getBlue() < 125 ? 255 : 0);
          paramGraphics.setColor(localColor2);
          paramGraphics.drawLine(m, j, m + swatchSize.width - 1, j);
          paramGraphics.drawLine(m, j, m, j + swatchSize.height - 1);
          paramGraphics.drawLine(m + swatchSize.width - 1, j, m + swatchSize.width - 1, j + swatchSize.height - 1);
          paramGraphics.drawLine(m, j + swatchSize.height - 1, m + swatchSize.width - 1, j + swatchSize.height - 1);
          paramGraphics.drawLine(m, j, m + swatchSize.width - 1, j + swatchSize.height - 1);
          paramGraphics.drawLine(m, j + swatchSize.height - 1, m + swatchSize.width - 1, j);
        }
      }
    }
  }
  
  public Dimension getPreferredSize()
  {
    int i = numSwatches.width * (swatchSize.width + gap.width) - 1;
    int j = numSwatches.height * (swatchSize.height + gap.height) - 1;
    return new Dimension(i, j);
  }
  
  protected void initColors() {}
  
  public String getToolTipText(MouseEvent paramMouseEvent)
  {
    Color localColor = getColorForLocation(paramMouseEvent.getX(), paramMouseEvent.getY());
    return localColor.getRed() + ", " + localColor.getGreen() + ", " + localColor.getBlue();
  }
  
  public void setSelectedColorFromLocation(int paramInt1, int paramInt2)
  {
    if (!getComponentOrientation().isLeftToRight()) {
      selCol = (numSwatches.width - paramInt1 / (swatchSize.width + gap.width) - 1);
    } else {
      selCol = (paramInt1 / (swatchSize.width + gap.width));
    }
    selRow = (paramInt2 / (swatchSize.height + gap.height));
    repaint();
  }
  
  public Color getColorForLocation(int paramInt1, int paramInt2)
  {
    int i;
    if (!getComponentOrientation().isLeftToRight()) {
      i = numSwatches.width - paramInt1 / (swatchSize.width + gap.width) - 1;
    } else {
      i = paramInt1 / (swatchSize.width + gap.width);
    }
    int j = paramInt2 / (swatchSize.height + gap.height);
    return getColorForCell(i, j);
  }
  
  private Color getColorForCell(int paramInt1, int paramInt2)
  {
    return colors[(paramInt2 * numSwatches.width + paramInt1)];
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\colorchooser\SwatchPanel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */