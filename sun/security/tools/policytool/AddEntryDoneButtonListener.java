package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.Collator;
import java.text.MessageFormat;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListModel;
import sun.security.provider.PolicyParser.GrantEntry;

class AddEntryDoneButtonListener
  implements ActionListener
{
  private PolicyTool tool;
  private ToolWindow tw;
  private ToolDialog td;
  private boolean edit;
  
  AddEntryDoneButtonListener(PolicyTool paramPolicyTool, ToolWindow paramToolWindow, ToolDialog paramToolDialog, boolean paramBoolean)
  {
    tool = paramPolicyTool;
    tw = paramToolWindow;
    td = paramToolDialog;
    edit = paramBoolean;
  }
  
  public void actionPerformed(ActionEvent paramActionEvent)
  {
    try
    {
      PolicyEntry localPolicyEntry = td.getPolicyEntryFromDialog();
      PolicyParser.GrantEntry localGrantEntry = localPolicyEntry.getGrantEntry();
      int i;
      Object localObject2;
      if (signedBy != null)
      {
        localObject1 = tool.parseSigners(signedBy);
        for (i = 0; i < localObject1.length; i++)
        {
          localObject2 = tool.getPublicKeyAlias(localObject1[i]);
          if (localObject2 == null)
          {
            MessageFormat localMessageFormat = new MessageFormat(PolicyTool.getMessage("Warning.A.public.key.for.alias.signers.i.does.not.exist.Make.sure.a.KeyStore.is.properly.configured."));
            Object[] arrayOfObject = { localObject1[i] };
            tool.warnings.addElement(localMessageFormat.format(arrayOfObject));
            tw.displayStatusDialog(td, localMessageFormat.format(arrayOfObject));
          }
        }
      }
      Object localObject1 = (JList)tw.getComponent(3);
      if (edit)
      {
        i = ((JList)localObject1).getSelectedIndex();
        tool.addEntry(localPolicyEntry, i);
        localObject2 = localPolicyEntry.headerToString();
        if (PolicyTool.collator.compare(localObject2, ((JList)localObject1).getModel().getElementAt(i)) != 0) {
          tool.modified = true;
        }
        ((DefaultListModel)((JList)localObject1).getModel()).set(i, localObject2);
      }
      else
      {
        tool.addEntry(localPolicyEntry, -1);
        ((DefaultListModel)((JList)localObject1).getModel()).addElement(localPolicyEntry.headerToString());
        tool.modified = true;
      }
      td.setVisible(false);
      td.dispose();
    }
    catch (Exception localException)
    {
      tw.displayErrorDialog(td, localException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\policytool\AddEntryDoneButtonListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */