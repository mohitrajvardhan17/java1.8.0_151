package sun.applet;

import java.awt.Button;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Rectangle;

class AppletPropsErrorDialog
  extends Dialog
{
  public AppletPropsErrorDialog(Frame paramFrame, String paramString1, String paramString2, String paramString3)
  {
    super(paramFrame, paramString1, true);
    Panel localPanel = new Panel();
    add("Center", new Label(paramString2));
    localPanel.add(new Button(paramString3));
    add("South", localPanel);
    pack();
    Dimension localDimension = size();
    Rectangle localRectangle = paramFrame.bounds();
    move(x + (width - width) / 2, y + (height - height) / 2);
  }
  
  public boolean action(Event paramEvent, Object paramObject)
  {
    hide();
    dispose();
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\applet\AppletPropsErrorDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */