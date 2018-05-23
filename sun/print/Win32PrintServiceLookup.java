package sun.print;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import javax.print.DocFlavor;
import javax.print.MultiDocPrintService;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.PrinterName;
import sun.security.action.GetPropertyAction;

public class Win32PrintServiceLookup
  extends PrintServiceLookup
{
  private String defaultPrinter;
  private PrintService defaultPrintService;
  private String[] printers;
  private PrintService[] printServices;
  private static Win32PrintServiceLookup win32PrintLUS;
  
  public static Win32PrintServiceLookup getWin32PrintLUS()
  {
    if (win32PrintLUS == null) {
      PrintServiceLookup.lookupDefaultPrintService();
    }
    return win32PrintLUS;
  }
  
  public Win32PrintServiceLookup()
  {
    if (win32PrintLUS == null)
    {
      win32PrintLUS = this;
      String str = (String)AccessController.doPrivileged(new GetPropertyAction("os.name"));
      if ((str != null) && (str.startsWith("Windows 98"))) {
        return;
      }
      PrinterChangeListener localPrinterChangeListener = new PrinterChangeListener();
      localPrinterChangeListener.setDaemon(true);
      localPrinterChangeListener.start();
    }
  }
  
  public synchronized PrintService[] getPrintServices()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPrintJobAccess();
    }
    if (printServices == null) {
      refreshServices();
    }
    return printServices;
  }
  
  private synchronized void refreshServices()
  {
    printers = getAllPrinterNames();
    if (printers == null)
    {
      printServices = new PrintService[0];
      return;
    }
    PrintService[] arrayOfPrintService = new PrintService[printers.length];
    PrintService localPrintService = getDefaultPrintService();
    for (int i = 0; i < printers.length; i++) {
      if ((localPrintService != null) && (printers[i].equals(localPrintService.getName())))
      {
        arrayOfPrintService[i] = localPrintService;
      }
      else if (printServices == null)
      {
        arrayOfPrintService[i] = new Win32PrintService(printers[i]);
      }
      else
      {
        for (int j = 0; j < printServices.length; j++) {
          if ((printServices[j] != null) && (printers[i].equals(printServices[j].getName())))
          {
            arrayOfPrintService[i] = printServices[j];
            printServices[j] = null;
            break;
          }
        }
        if (j == printServices.length) {
          arrayOfPrintService[i] = new Win32PrintService(printers[i]);
        }
      }
    }
    if (printServices != null) {
      for (i = 0; i < printServices.length; i++) {
        if (((printServices[i] instanceof Win32PrintService)) && (!printServices[i].equals(defaultPrintService))) {
          ((Win32PrintService)printServices[i]).invalidateService();
        }
      }
    }
    printServices = arrayOfPrintService;
  }
  
  public synchronized PrintService getPrintServiceByName(String paramString)
  {
    if ((paramString == null) || (paramString.equals(""))) {
      return null;
    }
    PrintService[] arrayOfPrintService = getPrintServices();
    for (int i = 0; i < arrayOfPrintService.length; i++) {
      if (arrayOfPrintService[i].getName().equals(paramString)) {
        return arrayOfPrintService[i];
      }
    }
    return null;
  }
  
  boolean matchingService(PrintService paramPrintService, PrintServiceAttributeSet paramPrintServiceAttributeSet)
  {
    if (paramPrintServiceAttributeSet != null)
    {
      Attribute[] arrayOfAttribute = paramPrintServiceAttributeSet.toArray();
      for (int i = 0; i < arrayOfAttribute.length; i++)
      {
        PrintServiceAttribute localPrintServiceAttribute = paramPrintService.getAttribute(arrayOfAttribute[i].getCategory());
        if ((localPrintServiceAttribute == null) || (!localPrintServiceAttribute.equals(arrayOfAttribute[i]))) {
          return false;
        }
      }
    }
    return true;
  }
  
  public PrintService[] getPrintServices(DocFlavor paramDocFlavor, AttributeSet paramAttributeSet)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPrintJobAccess();
    }
    HashPrintRequestAttributeSet localHashPrintRequestAttributeSet = null;
    HashPrintServiceAttributeSet localHashPrintServiceAttributeSet = null;
    if ((paramAttributeSet != null) && (!paramAttributeSet.isEmpty()))
    {
      localHashPrintRequestAttributeSet = new HashPrintRequestAttributeSet();
      localHashPrintServiceAttributeSet = new HashPrintServiceAttributeSet();
      localObject1 = paramAttributeSet.toArray();
      for (int i = 0; i < localObject1.length; i++) {
        if ((localObject1[i] instanceof PrintRequestAttribute)) {
          localHashPrintRequestAttributeSet.add(localObject1[i]);
        } else if ((localObject1[i] instanceof PrintServiceAttribute)) {
          localHashPrintServiceAttributeSet.add(localObject1[i]);
        }
      }
    }
    Object localObject1 = null;
    if ((localHashPrintServiceAttributeSet != null) && (localHashPrintServiceAttributeSet.get(PrinterName.class) != null))
    {
      localObject2 = (PrinterName)localHashPrintServiceAttributeSet.get(PrinterName.class);
      PrintService localPrintService = getPrintServiceByName(((PrinterName)localObject2).getValue());
      if ((localPrintService == null) || (!matchingService(localPrintService, localHashPrintServiceAttributeSet)))
      {
        localObject1 = new PrintService[0];
      }
      else
      {
        localObject1 = new PrintService[1];
        localObject1[0] = localPrintService;
      }
    }
    else
    {
      localObject1 = getPrintServices();
    }
    if (localObject1.length == 0) {
      return (PrintService[])localObject1;
    }
    Object localObject2 = new ArrayList();
    for (int j = 0; j < localObject1.length; j++) {
      try
      {
        if (localObject1[j].getUnsupportedAttributes(paramDocFlavor, localHashPrintRequestAttributeSet) == null) {
          ((ArrayList)localObject2).add(localObject1[j]);
        }
      }
      catch (IllegalArgumentException localIllegalArgumentException) {}
    }
    localObject1 = new PrintService[((ArrayList)localObject2).size()];
    return (PrintService[])((ArrayList)localObject2).toArray((Object[])localObject1);
  }
  
  public MultiDocPrintService[] getMultiDocPrintServices(DocFlavor[] paramArrayOfDocFlavor, AttributeSet paramAttributeSet)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPrintJobAccess();
    }
    return new MultiDocPrintService[0];
  }
  
  public synchronized PrintService getDefaultPrintService()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPrintJobAccess();
    }
    defaultPrinter = getDefaultPrinterName();
    if (defaultPrinter == null) {
      return null;
    }
    if ((defaultPrintService != null) && (defaultPrintService.getName().equals(defaultPrinter))) {
      return defaultPrintService;
    }
    defaultPrintService = null;
    if (printServices != null) {
      for (int i = 0; i < printServices.length; i++) {
        if (defaultPrinter.equals(printServices[i].getName()))
        {
          defaultPrintService = printServices[i];
          break;
        }
      }
    }
    if (defaultPrintService == null) {
      defaultPrintService = new Win32PrintService(defaultPrinter);
    }
    return defaultPrintService;
  }
  
  private native String getDefaultPrinterName();
  
  private native String[] getAllPrinterNames();
  
  private native long notifyFirstPrinterChange(String paramString);
  
  private native void notifyClosePrinterChange(long paramLong);
  
  private native int notifyPrinterChange(long paramLong);
  
  static
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        System.loadLibrary("awt");
        return null;
      }
    });
  }
  
  class PrinterChangeListener
    extends Thread
  {
    long chgObj = Win32PrintServiceLookup.this.notifyFirstPrinterChange(null);
    
    PrinterChangeListener() {}
    
    public void run()
    {
      if (chgObj != -1L)
      {
        for (;;)
        {
          if (Win32PrintServiceLookup.this.notifyPrinterChange(chgObj) != 0) {
            try
            {
              Win32PrintServiceLookup.this.refreshServices();
            }
            catch (SecurityException localSecurityException)
            {
              return;
            }
          }
        }
        Win32PrintServiceLookup.this.notifyClosePrinterChange(chgObj);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\print\Win32PrintServiceLookup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */