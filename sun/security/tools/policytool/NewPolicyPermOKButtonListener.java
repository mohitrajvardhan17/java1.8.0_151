package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.Vector;
import sun.security.provider.PolicyParser.PermissionEntry;

class NewPolicyPermOKButtonListener
  implements ActionListener
{
  private PolicyTool tool;
  private ToolWindow tw;
  private ToolDialog listDialog;
  private ToolDialog infoDialog;
  private boolean edit;
  
  NewPolicyPermOKButtonListener(PolicyTool paramPolicyTool, ToolWindow paramToolWindow, ToolDialog paramToolDialog1, ToolDialog paramToolDialog2, boolean paramBoolean)
  {
    tool = paramPolicyTool;
    tw = paramToolWindow;
    listDialog = paramToolDialog1;
    infoDialog = paramToolDialog2;
    edit = paramBoolean;
  }
  
  public void actionPerformed(ActionEvent paramActionEvent)
  {
    try
    {
      PolicyParser.PermissionEntry localPermissionEntry = infoDialog.getPermFromDialog();
      try
      {
        tool.verifyPermission(permission, name, action);
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        localObject = new MessageFormat(PolicyTool.getMessage("Warning.Class.not.found.class"));
        Object[] arrayOfObject = { permission };
        tool.warnings.addElement(((MessageFormat)localObject).format(arrayOfObject));
        tw.displayStatusDialog(infoDialog, ((MessageFormat)localObject).format(arrayOfObject));
      }
      TaggedList localTaggedList = (TaggedList)listDialog.getComponent(8);
      Object localObject = ToolDialog.PermissionEntryToUserFriendlyString(localPermissionEntry);
      if (edit)
      {
        int i = localTaggedList.getSelectedIndex();
        localTaggedList.replaceTaggedItem((String)localObject, localPermissionEntry, i);
      }
      else
      {
        localTaggedList.addTaggedItem((String)localObject, localPermissionEntry);
      }
      infoDialog.dispose();
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      tw.displayErrorDialog(infoDialog, localInvocationTargetException.getTargetException());
    }
    catch (Exception localException)
    {
      tw.displayErrorDialog(infoDialog, localException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\policytool\NewPolicyPermOKButtonListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */