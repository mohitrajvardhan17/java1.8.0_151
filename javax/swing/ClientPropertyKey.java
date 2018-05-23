package javax.swing;

import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.ClientPropertyKeyAccessor;

 enum ClientPropertyKey
{
  JComponent_INPUT_VERIFIER(true),  JComponent_TRANSFER_HANDLER(true),  JComponent_ANCESTOR_NOTIFIER(true),  PopupFactory_FORCE_HEAVYWEIGHT_POPUP(true);
  
  private final boolean reportValueNotSerializable;
  
  private ClientPropertyKey()
  {
    this(false);
  }
  
  private ClientPropertyKey(boolean paramBoolean)
  {
    reportValueNotSerializable = paramBoolean;
  }
  
  public boolean getReportValueNotSerializable()
  {
    return reportValueNotSerializable;
  }
  
  static
  {
    AWTAccessor.setClientPropertyKeyAccessor(new AWTAccessor.ClientPropertyKeyAccessor()
    {
      public Object getJComponent_TRANSFER_HANDLER()
      {
        return ClientPropertyKey.JComponent_TRANSFER_HANDLER;
      }
    });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\ClientPropertyKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */