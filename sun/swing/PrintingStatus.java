package sun.swing;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class PrintingStatus
{
  private final PrinterJob job;
  private final Component parent;
  private JDialog abortDialog;
  private JButton abortButton;
  private JLabel statusLabel;
  private MessageFormat statusFormat;
  private final AtomicBoolean isAborted = new AtomicBoolean(false);
  private final Action abortAction = new AbstractAction()
  {
    public void actionPerformed(ActionEvent paramAnonymousActionEvent)
    {
      if (!isAborted.get())
      {
        isAborted.set(true);
        abortButton.setEnabled(false);
        abortDialog.setTitle(UIManager.getString("PrintingDialog.titleAbortingText"));
        statusLabel.setText(UIManager.getString("PrintingDialog.contentAbortingText"));
        job.cancel();
      }
    }
  };
  private final WindowAdapter closeListener = new WindowAdapter()
  {
    public void windowClosing(WindowEvent paramAnonymousWindowEvent)
    {
      abortAction.actionPerformed(null);
    }
  };
  
  public static PrintingStatus createPrintingStatus(Component paramComponent, PrinterJob paramPrinterJob)
  {
    return new PrintingStatus(paramComponent, paramPrinterJob);
  }
  
  protected PrintingStatus(Component paramComponent, PrinterJob paramPrinterJob)
  {
    job = paramPrinterJob;
    parent = paramComponent;
  }
  
  private void init()
  {
    String str1 = UIManager.getString("PrintingDialog.titleProgressText");
    String str2 = UIManager.getString("PrintingDialog.contentInitialText");
    statusFormat = new MessageFormat(UIManager.getString("PrintingDialog.contentProgressText"));
    String str3 = UIManager.getString("PrintingDialog.abortButtonText");
    String str4 = UIManager.getString("PrintingDialog.abortButtonToolTipText");
    int i = getInt("PrintingDialog.abortButtonMnemonic", -1);
    int j = getInt("PrintingDialog.abortButtonDisplayedMnemonicIndex", -1);
    abortButton = new JButton(str3);
    abortButton.addActionListener(abortAction);
    abortButton.setToolTipText(str4);
    if (i != -1) {
      abortButton.setMnemonic(i);
    }
    if (j != -1) {
      abortButton.setDisplayedMnemonicIndex(j);
    }
    statusLabel = new JLabel(str2);
    JOptionPane localJOptionPane = new JOptionPane(statusLabel, 1, -1, null, new Object[] { abortButton }, abortButton);
    localJOptionPane.getActionMap().put("close", abortAction);
    if ((parent != null) && ((parent.getParent() instanceof JViewport))) {
      abortDialog = localJOptionPane.createDialog(parent.getParent(), str1);
    } else {
      abortDialog = localJOptionPane.createDialog(parent, str1);
    }
    abortDialog.setDefaultCloseOperation(0);
    abortDialog.addWindowListener(closeListener);
  }
  
  public void showModal(final boolean paramBoolean)
  {
    if (SwingUtilities.isEventDispatchThread()) {
      showModalOnEDT(paramBoolean);
    } else {
      try
      {
        SwingUtilities.invokeAndWait(new Runnable()
        {
          public void run()
          {
            PrintingStatus.this.showModalOnEDT(paramBoolean);
          }
        });
      }
      catch (InterruptedException localInterruptedException)
      {
        throw new RuntimeException(localInterruptedException);
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        Throwable localThrowable = localInvocationTargetException.getCause();
        if ((localThrowable instanceof RuntimeException)) {
          throw ((RuntimeException)localThrowable);
        }
        if ((localThrowable instanceof Error)) {
          throw ((Error)localThrowable);
        }
        throw new RuntimeException(localThrowable);
      }
    }
  }
  
  private void showModalOnEDT(boolean paramBoolean)
  {
    assert (SwingUtilities.isEventDispatchThread());
    init();
    abortDialog.setModal(paramBoolean);
    abortDialog.setVisible(true);
  }
  
  public void dispose()
  {
    if (SwingUtilities.isEventDispatchThread()) {
      disposeOnEDT();
    } else {
      SwingUtilities.invokeLater(new Runnable()
      {
        public void run()
        {
          PrintingStatus.this.disposeOnEDT();
        }
      });
    }
  }
  
  private void disposeOnEDT()
  {
    assert (SwingUtilities.isEventDispatchThread());
    if (abortDialog != null)
    {
      abortDialog.removeWindowListener(closeListener);
      abortDialog.dispose();
      abortDialog = null;
    }
  }
  
  public boolean isAborted()
  {
    return isAborted.get();
  }
  
  public Printable createNotificationPrintable(Printable paramPrintable)
  {
    return new NotificationPrintable(paramPrintable);
  }
  
  static int getInt(Object paramObject, int paramInt)
  {
    Object localObject = UIManager.get(paramObject);
    if ((localObject instanceof Integer)) {
      return ((Integer)localObject).intValue();
    }
    if ((localObject instanceof String)) {
      try
      {
        return Integer.parseInt((String)localObject);
      }
      catch (NumberFormatException localNumberFormatException) {}
    }
    return paramInt;
  }
  
  private class NotificationPrintable
    implements Printable
  {
    private final Printable printDelegatee;
    
    public NotificationPrintable(Printable paramPrintable)
    {
      if (paramPrintable == null) {
        throw new NullPointerException("Printable is null");
      }
      printDelegatee = paramPrintable;
    }
    
    public int print(Graphics paramGraphics, PageFormat paramPageFormat, final int paramInt)
      throws PrinterException
    {
      int i = printDelegatee.print(paramGraphics, paramPageFormat, paramInt);
      if ((i != 1) && (!isAborted())) {
        if (SwingUtilities.isEventDispatchThread()) {
          updateStatusOnEDT(paramInt);
        } else {
          SwingUtilities.invokeLater(new Runnable()
          {
            public void run()
            {
              PrintingStatus.NotificationPrintable.this.updateStatusOnEDT(paramInt);
            }
          });
        }
      }
      return i;
    }
    
    private void updateStatusOnEDT(int paramInt)
    {
      assert (SwingUtilities.isEventDispatchThread());
      Object[] arrayOfObject = { new Integer(paramInt + 1) };
      statusLabel.setText(statusFormat.format(arrayOfObject));
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\swing\PrintingStatus.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */