package sun.awt.windows;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.PrintJob;
import java.awt.Toolkit;
import java.awt.peer.ComponentPeer;
import java.awt.print.PrinterJob;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.ComponentAccessor;

class WPrintDialog
  extends Dialog
{
  protected PrintJob job;
  protected PrinterJob pjob;
  private boolean retval = false;
  
  WPrintDialog(Frame paramFrame, PrinterJob paramPrinterJob)
  {
    super(paramFrame, true);
    pjob = paramPrinterJob;
    setLayout(null);
  }
  
  WPrintDialog(Dialog paramDialog, PrinterJob paramPrinterJob)
  {
    super(paramDialog, "", true);
    pjob = paramPrinterJob;
    setLayout(null);
  }
  
  final void setPeer(ComponentPeer paramComponentPeer)
  {
    AWTAccessor.getComponentAccessor().setPeer(this, paramComponentPeer);
  }
  
  public void addNotify()
  {
    synchronized (getTreeLock())
    {
      Container localContainer = getParent();
      if ((localContainer != null) && (localContainer.getPeer() == null)) {
        localContainer.addNotify();
      }
      if (getPeer() == null)
      {
        WPrintDialogPeer localWPrintDialogPeer = ((WToolkit)Toolkit.getDefaultToolkit()).createWPrintDialog(this);
        setPeer(localWPrintDialogPeer);
      }
      super.addNotify();
    }
  }
  
  final void setRetVal(boolean paramBoolean)
  {
    retval = paramBoolean;
  }
  
  final boolean getRetVal()
  {
    return retval;
  }
  
  private static native void initIDs();
  
  static {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\windows\WPrintDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */