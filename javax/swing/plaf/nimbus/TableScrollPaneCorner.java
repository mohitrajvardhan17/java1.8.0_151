package javax.swing.plaf.nimbus;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.swing.Painter;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;

class TableScrollPaneCorner
  extends JComponent
  implements UIResource
{
  TableScrollPaneCorner() {}
  
  protected void paintComponent(Graphics paramGraphics)
  {
    Painter localPainter = (Painter)UIManager.get("TableHeader:\"TableHeader.renderer\"[Enabled].backgroundPainter");
    if (localPainter != null) {
      if ((paramGraphics instanceof Graphics2D))
      {
        localPainter.paint((Graphics2D)paramGraphics, this, getWidth() + 1, getHeight());
      }
      else
      {
        BufferedImage localBufferedImage = new BufferedImage(getWidth(), getHeight(), 2);
        Graphics2D localGraphics2D = (Graphics2D)localBufferedImage.getGraphics();
        localPainter.paint(localGraphics2D, this, getWidth() + 1, getHeight());
        localGraphics2D.dispose();
        paramGraphics.drawImage(localBufferedImage, 0, 0, null);
        localBufferedImage = null;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\nimbus\TableScrollPaneCorner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */