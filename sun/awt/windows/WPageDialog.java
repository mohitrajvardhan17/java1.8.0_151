package sun.awt.windows;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;

final class WPageDialog
  extends WPrintDialog
{
  PageFormat page;
  Printable painter;
  
  WPageDialog(Frame paramFrame, PrinterJob paramPrinterJob, PageFormat paramPageFormat, Printable paramPrintable)
  {
    super(paramFrame, paramPrinterJob);
    page = paramPageFormat;
    painter = paramPrintable;
  }
  
  WPageDialog(Dialog paramDialog, PrinterJob paramPrinterJob, PageFormat paramPageFormat, Printable paramPrintable)
  {
    super(paramDialog, paramPrinterJob);
    page = paramPageFormat;
    painter = paramPrintable;
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
        WPageDialogPeer localWPageDialogPeer = ((WToolkit)Toolkit.getDefaultToolkit()).createWPageDialog(this);
        setPeer(localWPageDialogPeer);
      }
      super.addNotify();
    }
  }
  
  private static native void initIDs();
  
  static {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\windows\WPageDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */