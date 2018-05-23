package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.text.Collator;
import java.text.MessageFormat;
import javax.swing.JTextField;

class FileMenuListener
  implements ActionListener
{
  private PolicyTool tool;
  private ToolWindow tw;
  
  FileMenuListener(PolicyTool paramPolicyTool, ToolWindow paramToolWindow)
  {
    tool = paramPolicyTool;
    tw = paramToolWindow;
  }
  
  public void actionPerformed(ActionEvent paramActionEvent)
  {
    Object localObject1;
    if (PolicyTool.collator.compare(paramActionEvent.getActionCommand(), "Exit") == 0)
    {
      localObject1 = new ToolDialog(PolicyTool.getMessage("Save.Changes"), tool, tw, true);
      ((ToolDialog)localObject1).displayUserSave(1);
    }
    else if (PolicyTool.collator.compare(paramActionEvent.getActionCommand(), "New") == 0)
    {
      localObject1 = new ToolDialog(PolicyTool.getMessage("Save.Changes"), tool, tw, true);
      ((ToolDialog)localObject1).displayUserSave(2);
    }
    else if (PolicyTool.collator.compare(paramActionEvent.getActionCommand(), "Open") == 0)
    {
      localObject1 = new ToolDialog(PolicyTool.getMessage("Save.Changes"), tool, tw, true);
      ((ToolDialog)localObject1).displayUserSave(3);
    }
    else if (PolicyTool.collator.compare(paramActionEvent.getActionCommand(), "Save") == 0)
    {
      localObject1 = ((JTextField)tw.getComponent(1)).getText();
      Object localObject2;
      if ((localObject1 == null) || (((String)localObject1).length() == 0))
      {
        localObject2 = new ToolDialog(PolicyTool.getMessage("Save.As"), tool, tw, true);
        ((ToolDialog)localObject2).displaySaveAsDialog(0);
      }
      else
      {
        try
        {
          tool.savePolicy((String)localObject1);
          localObject2 = new MessageFormat(PolicyTool.getMessage("Policy.successfully.written.to.filename"));
          Object[] arrayOfObject = { localObject1 };
          tw.displayStatusDialog(null, ((MessageFormat)localObject2).format(arrayOfObject));
        }
        catch (FileNotFoundException localFileNotFoundException)
        {
          if ((localObject1 == null) || (((String)localObject1).equals(""))) {
            tw.displayErrorDialog(null, new FileNotFoundException(PolicyTool.getMessage("null.filename")));
          } else {
            tw.displayErrorDialog(null, localFileNotFoundException);
          }
        }
        catch (Exception localException)
        {
          tw.displayErrorDialog(null, localException);
        }
      }
    }
    else if (PolicyTool.collator.compare(paramActionEvent.getActionCommand(), "Save.As") == 0)
    {
      localObject1 = new ToolDialog(PolicyTool.getMessage("Save.As"), tool, tw, true);
      ((ToolDialog)localObject1).displaySaveAsDialog(0);
    }
    else if (PolicyTool.collator.compare(paramActionEvent.getActionCommand(), "View.Warning.Log") == 0)
    {
      tw.displayWarningLog(null);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\policytool\FileMenuListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */