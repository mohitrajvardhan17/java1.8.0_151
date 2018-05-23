package sun.applet;

import java.awt.Button;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

final class TextFrame
  extends Frame
{
  private static AppletMessageHandler amh = new AppletMessageHandler("textframe");
  
  TextFrame(int paramInt1, int paramInt2, String paramString1, String paramString2)
  {
    setTitle(paramString1);
    TextArea localTextArea = new TextArea(20, 60);
    localTextArea.setText(paramString2);
    localTextArea.setEditable(false);
    add("Center", localTextArea);
    Panel localPanel = new Panel();
    add("South", localPanel);
    Button localButton = new Button(amh.getMessage("button.dismiss", "Dismiss"));
    localPanel.add(localButton);
    localButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        dispose();
      }
    });
    pack();
    move(paramInt1, paramInt2);
    setVisible(true);
    WindowAdapter local1 = new WindowAdapter()
    {
      public void windowClosing(WindowEvent paramAnonymousWindowEvent)
      {
        dispose();
      }
    };
    addWindowListener(local1);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\applet\TextFrame.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */