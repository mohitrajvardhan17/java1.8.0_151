package javax.swing.colorchooser;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

final class DiagramComponent
  extends JComponent
  implements MouseListener, MouseMotionListener
{
  private final ColorPanel panel;
  private final boolean diagram;
  private final Insets insets = new Insets(0, 0, 0, 0);
  private int width;
  private int height;
  private int[] array;
  private BufferedImage image;
  
  DiagramComponent(ColorPanel paramColorPanel, boolean paramBoolean)
  {
    panel = paramColorPanel;
    diagram = paramBoolean;
    addMouseListener(this);
    addMouseMotionListener(this);
  }
  
  protected void paintComponent(Graphics paramGraphics)
  {
    getInsets(insets);
    width = (getWidth() - insets.left - insets.right);
    height = (getHeight() - insets.top - insets.bottom);
    int i = (image == null) || (width != image.getWidth()) || (height != image.getHeight()) ? 1 : 0;
    if (i != 0)
    {
      int j = width * height;
      if ((array == null) || (array.length < j)) {
        array = new int[j];
      }
      image = new BufferedImage(width, height, 1);
    }
    float f1 = 1.0F / (width - 1);
    float f2 = 1.0F / (height - 1);
    int n = 0;
    float f3 = 0.0F;
    int i1 = 0;
    while (i1 < height)
    {
      int i3;
      if (diagram)
      {
        float f4 = 0.0F;
        i3 = 0;
        while (i3 < width)
        {
          array[n] = panel.getColor(f4, f3);
          i3++;
          f4 += f1;
          n++;
        }
      }
      else
      {
        int i2 = panel.getColor(f3);
        i3 = 0;
        while (i3 < width)
        {
          array[n] = i2;
          i3++;
          n++;
        }
      }
      i1++;
      f3 += f2;
    }
    image.setRGB(0, 0, width, height, array, 0, width);
    paramGraphics.drawImage(image, insets.left, insets.top, width, height, this);
    if (isEnabled())
    {
      width -= 1;
      height -= 1;
      paramGraphics.setXORMode(Color.WHITE);
      paramGraphics.setColor(Color.BLACK);
      int k;
      if (diagram)
      {
        k = getValue(panel.getValueX(), insets.left, width);
        int m = getValue(panel.getValueY(), insets.top, height);
        paramGraphics.drawLine(k - 8, m, k + 8, m);
        paramGraphics.drawLine(k, m - 8, k, m + 8);
      }
      else
      {
        k = getValue(panel.getValueZ(), insets.top, height);
        paramGraphics.drawLine(insets.left, k, insets.left + width, k);
      }
      paramGraphics.setPaintMode();
    }
  }
  
  public void mousePressed(MouseEvent paramMouseEvent)
  {
    mouseDragged(paramMouseEvent);
  }
  
  public void mouseReleased(MouseEvent paramMouseEvent) {}
  
  public void mouseClicked(MouseEvent paramMouseEvent) {}
  
  public void mouseEntered(MouseEvent paramMouseEvent) {}
  
  public void mouseExited(MouseEvent paramMouseEvent) {}
  
  public void mouseMoved(MouseEvent paramMouseEvent) {}
  
  public void mouseDragged(MouseEvent paramMouseEvent)
  {
    if (isEnabled())
    {
      float f1 = getValue(paramMouseEvent.getY(), insets.top, height);
      if (diagram)
      {
        float f2 = getValue(paramMouseEvent.getX(), insets.left, width);
        panel.setValue(f2, f1);
      }
      else
      {
        panel.setValue(f1);
      }
    }
  }
  
  private static int getValue(float paramFloat, int paramInt1, int paramInt2)
  {
    return paramInt1 + (int)(paramFloat * paramInt2);
  }
  
  private static float getValue(int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt2 < paramInt1)
    {
      paramInt1 -= paramInt2;
      return paramInt1 < paramInt3 ? paramInt1 / paramInt3 : 1.0F;
    }
    return 0.0F;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\colorchooser\DiagramComponent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */