package javax.swing.colorchooser;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.UIManager;

class RecentSwatchPanel
  extends SwatchPanel
{
  RecentSwatchPanel() {}
  
  protected void initValues()
  {
    swatchSize = UIManager.getDimension("ColorChooser.swatchesRecentSwatchSize", getLocale());
    numSwatches = new Dimension(5, 7);
    gap = new Dimension(1, 1);
  }
  
  protected void initColors()
  {
    Color localColor = UIManager.getColor("ColorChooser.swatchesDefaultRecentColor", getLocale());
    int i = numSwatches.width * numSwatches.height;
    colors = new Color[i];
    for (int j = 0; j < i; j++) {
      colors[j] = localColor;
    }
  }
  
  public void setMostRecentColor(Color paramColor)
  {
    System.arraycopy(colors, 0, colors, 1, colors.length - 1);
    colors[0] = paramColor;
    repaint();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\colorchooser\RecentSwatchPanel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */