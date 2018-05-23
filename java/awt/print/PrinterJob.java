package java.awt.print;

import java.awt.AWTError;
import java.awt.HeadlessException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.print.DocFlavor.SERVICE_FORMATTED;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.StreamPrintServiceFactory;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;

public abstract class PrinterJob
{
  public static PrinterJob getPrinterJob()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPrintJobAccess();
    }
    (PrinterJob)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        String str = System.getProperty("java.awt.printerjob", null);
        try
        {
          return (PrinterJob)Class.forName(str).newInstance();
        }
        catch (ClassNotFoundException localClassNotFoundException)
        {
          throw new AWTError("PrinterJob not found: " + str);
        }
        catch (InstantiationException localInstantiationException)
        {
          throw new AWTError("Could not instantiate PrinterJob: " + str);
        }
        catch (IllegalAccessException localIllegalAccessException)
        {
          throw new AWTError("Could not access PrinterJob: " + str);
        }
      }
    });
  }
  
  public static PrintService[] lookupPrintServices()
  {
    return PrintServiceLookup.lookupPrintServices(DocFlavor.SERVICE_FORMATTED.PAGEABLE, null);
  }
  
  public static StreamPrintServiceFactory[] lookupStreamPrintServices(String paramString)
  {
    return StreamPrintServiceFactory.lookupStreamPrintServiceFactories(DocFlavor.SERVICE_FORMATTED.PAGEABLE, paramString);
  }
  
  public PrinterJob() {}
  
  public PrintService getPrintService()
  {
    return null;
  }
  
  public void setPrintService(PrintService paramPrintService)
    throws PrinterException
  {
    throw new PrinterException("Setting a service is not supported on this class");
  }
  
  public abstract void setPrintable(Printable paramPrintable);
  
  public abstract void setPrintable(Printable paramPrintable, PageFormat paramPageFormat);
  
  public abstract void setPageable(Pageable paramPageable)
    throws NullPointerException;
  
  public abstract boolean printDialog()
    throws HeadlessException;
  
  public boolean printDialog(PrintRequestAttributeSet paramPrintRequestAttributeSet)
    throws HeadlessException
  {
    if (paramPrintRequestAttributeSet == null) {
      throw new NullPointerException("attributes");
    }
    return printDialog();
  }
  
  public abstract PageFormat pageDialog(PageFormat paramPageFormat)
    throws HeadlessException;
  
  public PageFormat pageDialog(PrintRequestAttributeSet paramPrintRequestAttributeSet)
    throws HeadlessException
  {
    if (paramPrintRequestAttributeSet == null) {
      throw new NullPointerException("attributes");
    }
    return pageDialog(defaultPage());
  }
  
  public abstract PageFormat defaultPage(PageFormat paramPageFormat);
  
  public PageFormat defaultPage()
  {
    return defaultPage(new PageFormat());
  }
  
  public PageFormat getPageFormat(PrintRequestAttributeSet paramPrintRequestAttributeSet)
  {
    PrintService localPrintService = getPrintService();
    PageFormat localPageFormat = defaultPage();
    if ((localPrintService == null) || (paramPrintRequestAttributeSet == null)) {
      return localPageFormat;
    }
    Media localMedia = (Media)paramPrintRequestAttributeSet.get(Media.class);
    MediaPrintableArea localMediaPrintableArea = (MediaPrintableArea)paramPrintRequestAttributeSet.get(MediaPrintableArea.class);
    OrientationRequested localOrientationRequested = (OrientationRequested)paramPrintRequestAttributeSet.get(OrientationRequested.class);
    if ((localMedia == null) && (localMediaPrintableArea == null) && (localOrientationRequested == null)) {
      return localPageFormat;
    }
    Paper localPaper = localPageFormat.getPaper();
    Object localObject;
    if ((localMediaPrintableArea == null) && (localMedia != null) && (localPrintService.isAttributeCategorySupported(MediaPrintableArea.class)))
    {
      localObject = localPrintService.getSupportedAttributeValues(MediaPrintableArea.class, null, paramPrintRequestAttributeSet);
      if (((localObject instanceof MediaPrintableArea[])) && (((MediaPrintableArea[])localObject).length > 0)) {
        localMediaPrintableArea = ((MediaPrintableArea[])(MediaPrintableArea[])localObject)[0];
      }
    }
    if ((localMedia != null) && (localPrintService.isAttributeValueSupported(localMedia, null, paramPrintRequestAttributeSet)) && ((localMedia instanceof MediaSizeName)))
    {
      localObject = (MediaSizeName)localMedia;
      MediaSize localMediaSize = MediaSize.getMediaSizeForName((MediaSizeName)localObject);
      if (localMediaSize != null)
      {
        double d1 = 72.0D;
        double d2 = localMediaSize.getX(25400) * d1;
        double d3 = localMediaSize.getY(25400) * d1;
        localPaper.setSize(d2, d3);
        if (localMediaPrintableArea == null) {
          localPaper.setImageableArea(d1, d1, d2 - 2.0D * d1, d3 - 2.0D * d1);
        }
      }
    }
    if ((localMediaPrintableArea != null) && (localPrintService.isAttributeValueSupported(localMediaPrintableArea, null, paramPrintRequestAttributeSet)))
    {
      localObject = localMediaPrintableArea.getPrintableArea(25400);
      for (int j = 0; j < localObject.length; j++) {
        localObject[j] *= 72.0F;
      }
      localPaper.setImageableArea(localObject[0], localObject[1], localObject[2], localObject[3]);
    }
    if ((localOrientationRequested != null) && (localPrintService.isAttributeValueSupported(localOrientationRequested, null, paramPrintRequestAttributeSet)))
    {
      int i;
      if (localOrientationRequested.equals(OrientationRequested.REVERSE_LANDSCAPE)) {
        i = 2;
      } else if (localOrientationRequested.equals(OrientationRequested.LANDSCAPE)) {
        i = 0;
      } else {
        i = 1;
      }
      localPageFormat.setOrientation(i);
    }
    localPageFormat.setPaper(localPaper);
    localPageFormat = validatePage(localPageFormat);
    return localPageFormat;
  }
  
  public abstract PageFormat validatePage(PageFormat paramPageFormat);
  
  public abstract void print()
    throws PrinterException;
  
  public void print(PrintRequestAttributeSet paramPrintRequestAttributeSet)
    throws PrinterException
  {
    print();
  }
  
  public abstract void setCopies(int paramInt);
  
  public abstract int getCopies();
  
  public abstract String getUserName();
  
  public abstract void setJobName(String paramString);
  
  public abstract String getJobName();
  
  public abstract void cancel();
  
  public abstract boolean isCancelled();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\print\PrinterJob.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */