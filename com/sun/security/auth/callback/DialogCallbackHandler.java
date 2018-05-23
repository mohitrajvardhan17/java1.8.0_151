package com.sun.security.auth.callback;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.ConfirmationCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextOutputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import jdk.Exported;

@Exported(false)
@Deprecated
public class DialogCallbackHandler
  implements CallbackHandler
{
  private Component parentComponent;
  private static final int JPasswordFieldLen = 8;
  private static final int JTextFieldLen = 8;
  
  public DialogCallbackHandler() {}
  
  public DialogCallbackHandler(Component paramComponent)
  {
    parentComponent = paramComponent;
  }
  
  public void handle(Callback[] paramArrayOfCallback)
    throws UnsupportedCallbackException
  {
    ArrayList localArrayList1 = new ArrayList(3);
    ArrayList localArrayList2 = new ArrayList(2);
    ConfirmationInfo localConfirmationInfo = new ConfirmationInfo(null);
    final Object localObject1;
    for (int i = 0; i < paramArrayOfCallback.length; i++) {
      if ((paramArrayOfCallback[i] instanceof TextOutputCallback))
      {
        localObject1 = (TextOutputCallback)paramArrayOfCallback[i];
        switch (((TextOutputCallback)localObject1).getMessageType())
        {
        case 0: 
          messageType = 1;
          break;
        case 1: 
          messageType = 2;
          break;
        case 2: 
          messageType = 0;
          break;
        default: 
          throw new UnsupportedCallbackException(paramArrayOfCallback[i], "Unrecognized message type");
        }
        localArrayList1.add(((TextOutputCallback)localObject1).getMessage());
      }
      else
      {
        JLabel localJLabel;
        final Object localObject2;
        Object localObject3;
        if ((paramArrayOfCallback[i] instanceof NameCallback))
        {
          localObject1 = (NameCallback)paramArrayOfCallback[i];
          localJLabel = new JLabel(((NameCallback)localObject1).getPrompt());
          localObject2 = new JTextField(8);
          localObject3 = ((NameCallback)localObject1).getDefaultName();
          if (localObject3 != null) {
            ((JTextField)localObject2).setText((String)localObject3);
          }
          Box localBox = Box.createHorizontalBox();
          localBox.add(localJLabel);
          localBox.add((Component)localObject2);
          localArrayList1.add(localBox);
          localArrayList2.add(new Action()
          {
            public void perform()
            {
              localObject1.setName(localObject2.getText());
            }
          });
        }
        else if ((paramArrayOfCallback[i] instanceof PasswordCallback))
        {
          localObject1 = (PasswordCallback)paramArrayOfCallback[i];
          localJLabel = new JLabel(((PasswordCallback)localObject1).getPrompt());
          localObject2 = new JPasswordField(8);
          if (!((PasswordCallback)localObject1).isEchoOn()) {
            ((JPasswordField)localObject2).setEchoChar('*');
          }
          localObject3 = Box.createHorizontalBox();
          ((Box)localObject3).add(localJLabel);
          ((Box)localObject3).add((Component)localObject2);
          localArrayList1.add(localObject3);
          localArrayList2.add(new Action()
          {
            public void perform()
            {
              localObject1.setPassword(localObject2.getPassword());
            }
          });
        }
        else if ((paramArrayOfCallback[i] instanceof ConfirmationCallback))
        {
          localObject1 = (ConfirmationCallback)paramArrayOfCallback[i];
          localConfirmationInfo.setCallback((ConfirmationCallback)localObject1);
          if (((ConfirmationCallback)localObject1).getPrompt() != null) {
            localArrayList1.add(((ConfirmationCallback)localObject1).getPrompt());
          }
        }
        else
        {
          throw new UnsupportedCallbackException(paramArrayOfCallback[i], "Unrecognized Callback");
        }
      }
    }
    i = JOptionPane.showOptionDialog(parentComponent, localArrayList1.toArray(), "Confirmation", optionType, messageType, null, options, initialValue);
    if ((i == 0) || (i == 0))
    {
      localObject1 = localArrayList2.iterator();
      while (((Iterator)localObject1).hasNext()) {
        ((Action)((Iterator)localObject1).next()).perform();
      }
    }
    localConfirmationInfo.handleResult(i);
  }
  
  private static abstract interface Action
  {
    public abstract void perform();
  }
  
  private static class ConfirmationInfo
  {
    private int[] translations;
    int optionType = 2;
    Object[] options = null;
    Object initialValue = null;
    int messageType = 3;
    private ConfirmationCallback callback;
    
    private ConfirmationInfo() {}
    
    void setCallback(ConfirmationCallback paramConfirmationCallback)
      throws UnsupportedCallbackException
    {
      callback = paramConfirmationCallback;
      int i = paramConfirmationCallback.getOptionType();
      switch (i)
      {
      case 0: 
        optionType = 0;
        translations = new int[] { 0, 0, 1, 1, -1, 1 };
        break;
      case 1: 
        optionType = 1;
        translations = new int[] { 0, 0, 1, 1, 2, 2, -1, 2 };
        break;
      case 2: 
        optionType = 2;
        translations = new int[] { 0, 3, 2, 2, -1, 2 };
        break;
      case -1: 
        options = paramConfirmationCallback.getOptions();
        translations = new int[] { -1, paramConfirmationCallback.getDefaultOption() };
        break;
      default: 
        throw new UnsupportedCallbackException(paramConfirmationCallback, "Unrecognized option type: " + i);
      }
      int j = paramConfirmationCallback.getMessageType();
      switch (j)
      {
      case 1: 
        messageType = 2;
        break;
      case 2: 
        messageType = 0;
        break;
      case 0: 
        messageType = 1;
        break;
      default: 
        throw new UnsupportedCallbackException(paramConfirmationCallback, "Unrecognized message type: " + j);
      }
    }
    
    void handleResult(int paramInt)
    {
      if (callback == null) {
        return;
      }
      for (int i = 0; i < translations.length; i += 2) {
        if (translations[i] == paramInt)
        {
          paramInt = translations[(i + 1)];
          break;
        }
      }
      callback.setSelectedIndex(paramInt);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\security\auth\callback\DialogCallbackHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */