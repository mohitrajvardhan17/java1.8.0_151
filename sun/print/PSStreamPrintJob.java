package sun.print;

import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.Vector;
import javax.print.CancelablePrintJob;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocFlavor.BYTE_ARRAY;
import javax.print.DocFlavor.INPUT_STREAM;
import javax.print.DocFlavor.URL;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSetUtilities;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashPrintJobAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintJobAttributeSet;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.DocumentName;
import javax.print.attribute.standard.Fidelity;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.JobOriginatingUserName;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSize.NA;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.RequestingUserName;
import javax.print.event.PrintJobAttributeListener;
import javax.print.event.PrintJobEvent;
import javax.print.event.PrintJobListener;

public class PSStreamPrintJob
  implements CancelablePrintJob
{
  private transient Vector jobListeners;
  private transient Vector attrListeners;
  private transient Vector listenedAttributeSets;
  private PSStreamPrintService service;
  private boolean fidelity;
  private boolean printing = false;
  private boolean printReturned = false;
  private PrintRequestAttributeSet reqAttrSet = null;
  private PrintJobAttributeSet jobAttrSet = null;
  private PrinterJob job;
  private Doc doc;
  private InputStream instream = null;
  private Reader reader = null;
  private String jobName = "Java Printing";
  private int copies = 1;
  private MediaSize mediaSize = MediaSize.NA.LETTER;
  private OrientationRequested orient = OrientationRequested.PORTRAIT;
  
  PSStreamPrintJob(PSStreamPrintService paramPSStreamPrintService)
  {
    service = paramPSStreamPrintService;
  }
  
  public PrintService getPrintService()
  {
    return service;
  }
  
  public PrintJobAttributeSet getAttributes()
  {
    synchronized (this)
    {
      if (jobAttrSet == null)
      {
        HashPrintJobAttributeSet localHashPrintJobAttributeSet = new HashPrintJobAttributeSet();
        return AttributeSetUtilities.unmodifiableView(localHashPrintJobAttributeSet);
      }
      return jobAttrSet;
    }
  }
  
  public void addPrintJobListener(PrintJobListener paramPrintJobListener)
  {
    synchronized (this)
    {
      if (paramPrintJobListener == null) {
        return;
      }
      if (jobListeners == null) {
        jobListeners = new Vector();
      }
      jobListeners.add(paramPrintJobListener);
    }
  }
  
  public void removePrintJobListener(PrintJobListener paramPrintJobListener)
  {
    synchronized (this)
    {
      if ((paramPrintJobListener == null) || (jobListeners == null)) {
        return;
      }
      jobListeners.remove(paramPrintJobListener);
      if (jobListeners.isEmpty()) {
        jobListeners = null;
      }
    }
  }
  
  /* Error */
  private void closeDataStreams()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 422	sun/print/PSStreamPrintJob:doc	Ljavax/print/Doc;
    //   4: ifnonnull +4 -> 8
    //   7: return
    //   8: aconst_null
    //   9: astore_1
    //   10: aload_0
    //   11: getfield 422	sun/print/PSStreamPrintJob:doc	Ljavax/print/Doc;
    //   14: invokeinterface 494 1 0
    //   19: astore_1
    //   20: goto +5 -> 25
    //   23: astore_2
    //   24: return
    //   25: aload_0
    //   26: getfield 416	sun/print/PSStreamPrintJob:instream	Ljava/io/InputStream;
    //   29: ifnull +38 -> 67
    //   32: aload_0
    //   33: getfield 416	sun/print/PSStreamPrintJob:instream	Ljava/io/InputStream;
    //   36: invokevirtual 442	java/io/InputStream:close	()V
    //   39: aload_0
    //   40: aconst_null
    //   41: putfield 416	sun/print/PSStreamPrintJob:instream	Ljava/io/InputStream;
    //   44: goto +20 -> 64
    //   47: astore_2
    //   48: aload_0
    //   49: aconst_null
    //   50: putfield 416	sun/print/PSStreamPrintJob:instream	Ljava/io/InputStream;
    //   53: goto +11 -> 64
    //   56: astore_3
    //   57: aload_0
    //   58: aconst_null
    //   59: putfield 416	sun/print/PSStreamPrintJob:instream	Ljava/io/InputStream;
    //   62: aload_3
    //   63: athrow
    //   64: goto +86 -> 150
    //   67: aload_0
    //   68: getfield 417	sun/print/PSStreamPrintJob:reader	Ljava/io/Reader;
    //   71: ifnull +40 -> 111
    //   74: aload_0
    //   75: getfield 417	sun/print/PSStreamPrintJob:reader	Ljava/io/Reader;
    //   78: invokevirtual 443	java/io/Reader:close	()V
    //   81: aload_0
    //   82: aconst_null
    //   83: putfield 417	sun/print/PSStreamPrintJob:reader	Ljava/io/Reader;
    //   86: goto +22 -> 108
    //   89: astore_2
    //   90: aload_0
    //   91: aconst_null
    //   92: putfield 417	sun/print/PSStreamPrintJob:reader	Ljava/io/Reader;
    //   95: goto +13 -> 108
    //   98: astore 4
    //   100: aload_0
    //   101: aconst_null
    //   102: putfield 417	sun/print/PSStreamPrintJob:reader	Ljava/io/Reader;
    //   105: aload 4
    //   107: athrow
    //   108: goto +42 -> 150
    //   111: aload_1
    //   112: instanceof 207
    //   115: ifeq +17 -> 132
    //   118: aload_1
    //   119: checkcast 207	java/io/InputStream
    //   122: invokevirtual 442	java/io/InputStream:close	()V
    //   125: goto +25 -> 150
    //   128: astore_2
    //   129: goto +21 -> 150
    //   132: aload_1
    //   133: instanceof 208
    //   136: ifeq +14 -> 150
    //   139: aload_1
    //   140: checkcast 208	java/io/Reader
    //   143: invokevirtual 443	java/io/Reader:close	()V
    //   146: goto +4 -> 150
    //   149: astore_2
    //   150: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	151	0	this	PSStreamPrintJob
    //   9	131	1	localObject1	Object
    //   23	1	2	localIOException1	IOException
    //   47	1	2	localIOException2	IOException
    //   89	1	2	localIOException3	IOException
    //   128	1	2	localIOException4	IOException
    //   149	1	2	localIOException5	IOException
    //   56	7	3	localObject2	Object
    //   98	8	4	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   10	20	23	java/io/IOException
    //   32	39	47	java/io/IOException
    //   32	39	56	finally
    //   74	81	89	java/io/IOException
    //   74	81	98	finally
    //   98	100	98	finally
    //   118	125	128	java/io/IOException
    //   139	146	149	java/io/IOException
  }
  
  private void notifyEvent(int paramInt)
  {
    synchronized (this)
    {
      if (jobListeners != null)
      {
        PrintJobEvent localPrintJobEvent = new PrintJobEvent(this, paramInt);
        for (int i = 0; i < jobListeners.size(); i++)
        {
          PrintJobListener localPrintJobListener = (PrintJobListener)jobListeners.elementAt(i);
          switch (paramInt)
          {
          case 101: 
            localPrintJobListener.printJobCanceled(localPrintJobEvent);
            break;
          case 103: 
            localPrintJobListener.printJobFailed(localPrintJobEvent);
            break;
          case 106: 
            localPrintJobListener.printDataTransferCompleted(localPrintJobEvent);
            break;
          case 105: 
            localPrintJobListener.printJobNoMoreEvents(localPrintJobEvent);
            break;
          case 102: 
            localPrintJobListener.printJobCompleted(localPrintJobEvent);
          }
        }
      }
    }
  }
  
  public void addPrintJobAttributeListener(PrintJobAttributeListener paramPrintJobAttributeListener, PrintJobAttributeSet paramPrintJobAttributeSet)
  {
    synchronized (this)
    {
      if (paramPrintJobAttributeListener == null) {
        return;
      }
      if (attrListeners == null)
      {
        attrListeners = new Vector();
        listenedAttributeSets = new Vector();
      }
      attrListeners.add(paramPrintJobAttributeListener);
      if (paramPrintJobAttributeSet == null) {
        paramPrintJobAttributeSet = new HashPrintJobAttributeSet();
      }
      listenedAttributeSets.add(paramPrintJobAttributeSet);
    }
  }
  
  public void removePrintJobAttributeListener(PrintJobAttributeListener paramPrintJobAttributeListener)
  {
    synchronized (this)
    {
      if ((paramPrintJobAttributeListener == null) || (attrListeners == null)) {
        return;
      }
      int i = attrListeners.indexOf(paramPrintJobAttributeListener);
      if (i == -1) {
        return;
      }
      attrListeners.remove(i);
      listenedAttributeSets.remove(i);
      if (attrListeners.isEmpty())
      {
        attrListeners = null;
        listenedAttributeSets = null;
      }
    }
  }
  
  public void print(Doc paramDoc, PrintRequestAttributeSet paramPrintRequestAttributeSet)
    throws PrintException
  {
    synchronized (this)
    {
      if (printing) {
        throw new PrintException("already printing");
      }
      printing = true;
    }
    doc = paramDoc;
    ??? = paramDoc.getDocFlavor();
    Object localObject2;
    try
    {
      localObject2 = paramDoc.getPrintData();
    }
    catch (IOException localIOException1)
    {
      notifyEvent(103);
      throw new PrintException("can't get print data: " + localIOException1.toString());
    }
    if ((??? == null) || (!service.isDocFlavorSupported((DocFlavor)???)))
    {
      notifyEvent(103);
      throw new PrintJobFlavorException("invalid flavor", (DocFlavor)???);
    }
    initializeAttributeSets(paramDoc, paramPrintRequestAttributeSet);
    getAttributeValues((DocFlavor)???);
    String str = ((DocFlavor)???).getRepresentationClassName();
    if ((((DocFlavor)???).equals(DocFlavor.INPUT_STREAM.GIF)) || (((DocFlavor)???).equals(DocFlavor.INPUT_STREAM.JPEG)) || (((DocFlavor)???).equals(DocFlavor.INPUT_STREAM.PNG)) || (((DocFlavor)???).equals(DocFlavor.BYTE_ARRAY.GIF)) || (((DocFlavor)???).equals(DocFlavor.BYTE_ARRAY.JPEG)) || (((DocFlavor)???).equals(DocFlavor.BYTE_ARRAY.PNG))) {
      try
      {
        instream = paramDoc.getStreamForBytes();
        printableJob(new ImagePrinter(instream), reqAttrSet);
        return;
      }
      catch (ClassCastException localClassCastException1)
      {
        notifyEvent(103);
        throw new PrintException(localClassCastException1);
      }
      catch (IOException localIOException2)
      {
        notifyEvent(103);
        throw new PrintException(localIOException2);
      }
    }
    if ((((DocFlavor)???).equals(DocFlavor.URL.GIF)) || (((DocFlavor)???).equals(DocFlavor.URL.JPEG)) || (((DocFlavor)???).equals(DocFlavor.URL.PNG))) {
      try
      {
        printableJob(new ImagePrinter((URL)localObject2), reqAttrSet);
        return;
      }
      catch (ClassCastException localClassCastException2)
      {
        notifyEvent(103);
        throw new PrintException(localClassCastException2);
      }
    }
    if (str.equals("java.awt.print.Pageable")) {
      try
      {
        pageableJob((Pageable)paramDoc.getPrintData(), reqAttrSet);
        return;
      }
      catch (ClassCastException localClassCastException3)
      {
        notifyEvent(103);
        throw new PrintException(localClassCastException3);
      }
      catch (IOException localIOException3)
      {
        notifyEvent(103);
        throw new PrintException(localIOException3);
      }
    }
    if (str.equals("java.awt.print.Printable")) {
      try
      {
        printableJob((Printable)paramDoc.getPrintData(), reqAttrSet);
        return;
      }
      catch (ClassCastException localClassCastException4)
      {
        notifyEvent(103);
        throw new PrintException(localClassCastException4);
      }
      catch (IOException localIOException4)
      {
        notifyEvent(103);
        throw new PrintException(localIOException4);
      }
    }
    notifyEvent(103);
    throw new PrintException("unrecognized class: " + str);
  }
  
  public void printableJob(Printable paramPrintable, PrintRequestAttributeSet paramPrintRequestAttributeSet)
    throws PrintException
  {
    try
    {
      synchronized (this)
      {
        if (job != null) {
          throw new PrintException("already printing");
        }
        job = new PSPrinterJob();
      }
      job.setPrintService(getPrintService());
      ??? = new PageFormat();
      if (mediaSize != null)
      {
        Paper localPaper = new Paper();
        localPaper.setSize(mediaSize.getX(25400) * 72.0D, mediaSize.getY(25400) * 72.0D);
        localPaper.setImageableArea(72.0D, 72.0D, localPaper.getWidth() - 144.0D, localPaper.getHeight() - 144.0D);
        ((PageFormat)???).setPaper(localPaper);
      }
      if (orient == OrientationRequested.REVERSE_LANDSCAPE) {
        ((PageFormat)???).setOrientation(2);
      } else if (orient == OrientationRequested.LANDSCAPE) {
        ((PageFormat)???).setOrientation(0);
      }
      job.setPrintable(paramPrintable, (PageFormat)???);
      job.print(paramPrintRequestAttributeSet);
      notifyEvent(102);
      return;
    }
    catch (PrinterException localPrinterException)
    {
      notifyEvent(103);
      throw new PrintException(localPrinterException);
    }
    finally
    {
      printReturned = true;
    }
  }
  
  public void pageableJob(Pageable paramPageable, PrintRequestAttributeSet paramPrintRequestAttributeSet)
    throws PrintException
  {
    try
    {
      synchronized (this)
      {
        if (job != null) {
          throw new PrintException("already printing");
        }
        job = new PSPrinterJob();
      }
      job.setPrintService(getPrintService());
      job.setPageable(paramPageable);
      job.print(paramPrintRequestAttributeSet);
      notifyEvent(102);
      return;
    }
    catch (PrinterException localPrinterException)
    {
      notifyEvent(103);
      throw new PrintException(localPrinterException);
    }
    finally
    {
      printReturned = true;
    }
  }
  
  private synchronized void initializeAttributeSets(Doc paramDoc, PrintRequestAttributeSet paramPrintRequestAttributeSet)
  {
    reqAttrSet = new HashPrintRequestAttributeSet();
    jobAttrSet = new HashPrintJobAttributeSet();
    Attribute[] arrayOfAttribute;
    if (paramPrintRequestAttributeSet != null)
    {
      reqAttrSet.addAll(paramPrintRequestAttributeSet);
      arrayOfAttribute = paramPrintRequestAttributeSet.toArray();
      for (int i = 0; i < arrayOfAttribute.length; i++) {
        if ((arrayOfAttribute[i] instanceof PrintJobAttribute)) {
          jobAttrSet.add(arrayOfAttribute[i]);
        }
      }
    }
    DocAttributeSet localDocAttributeSet = paramDoc.getAttributes();
    if (localDocAttributeSet != null)
    {
      arrayOfAttribute = localDocAttributeSet.toArray();
      for (int j = 0; j < arrayOfAttribute.length; j++)
      {
        if ((arrayOfAttribute[j] instanceof PrintRequestAttribute)) {
          reqAttrSet.add(arrayOfAttribute[j]);
        }
        if ((arrayOfAttribute[j] instanceof PrintJobAttribute)) {
          jobAttrSet.add(arrayOfAttribute[j]);
        }
      }
    }
    String str = "";
    try
    {
      str = System.getProperty("user.name");
    }
    catch (SecurityException localSecurityException) {}
    Object localObject1;
    if ((str == null) || (str.equals("")))
    {
      localObject1 = (RequestingUserName)paramPrintRequestAttributeSet.get(RequestingUserName.class);
      if (localObject1 != null) {
        jobAttrSet.add(new JobOriginatingUserName(((RequestingUserName)localObject1).getValue(), ((RequestingUserName)localObject1).getLocale()));
      } else {
        jobAttrSet.add(new JobOriginatingUserName("", null));
      }
    }
    else
    {
      jobAttrSet.add(new JobOriginatingUserName(str, null));
    }
    if (jobAttrSet.get(JobName.class) == null)
    {
      Object localObject2;
      if ((localDocAttributeSet != null) && (localDocAttributeSet.get(DocumentName.class) != null))
      {
        localObject2 = (DocumentName)localDocAttributeSet.get(DocumentName.class);
        localObject1 = new JobName(((DocumentName)localObject2).getValue(), ((DocumentName)localObject2).getLocale());
        jobAttrSet.add((Attribute)localObject1);
      }
      else
      {
        localObject2 = "JPS Job:" + paramDoc;
        try
        {
          Object localObject3 = paramDoc.getPrintData();
          if ((localObject3 instanceof URL)) {
            localObject2 = ((URL)paramDoc.getPrintData()).toString();
          }
        }
        catch (IOException localIOException) {}
        localObject1 = new JobName((String)localObject2, null);
        jobAttrSet.add((Attribute)localObject1);
      }
    }
    jobAttrSet = AttributeSetUtilities.unmodifiableView(jobAttrSet);
  }
  
  private void getAttributeValues(DocFlavor paramDocFlavor)
    throws PrintException
  {
    if (reqAttrSet.get(Fidelity.class) == Fidelity.FIDELITY_TRUE) {
      fidelity = true;
    } else {
      fidelity = false;
    }
    Attribute[] arrayOfAttribute = reqAttrSet.toArray();
    for (int i = 0; i < arrayOfAttribute.length; i++)
    {
      Attribute localAttribute = arrayOfAttribute[i];
      Class localClass = localAttribute.getCategory();
      if (fidelity == true)
      {
        if (!service.isAttributeCategorySupported(localClass))
        {
          notifyEvent(103);
          throw new PrintJobAttributeException("unsupported category: " + localClass, localClass, null);
        }
        if (!service.isAttributeValueSupported(localAttribute, paramDocFlavor, null))
        {
          notifyEvent(103);
          throw new PrintJobAttributeException("unsupported attribute: " + localAttribute, null, localAttribute);
        }
      }
      if (localClass == JobName.class) {
        jobName = ((JobName)localAttribute).getValue();
      } else if (localClass == Copies.class) {
        copies = ((Copies)localAttribute).getValue();
      } else if (localClass == Media.class)
      {
        if (((localAttribute instanceof MediaSizeName)) && (service.isAttributeValueSupported(localAttribute, null, null))) {
          mediaSize = MediaSize.getMediaSizeForName((MediaSizeName)localAttribute);
        }
      }
      else if (localClass == OrientationRequested.class) {
        orient = ((OrientationRequested)localAttribute);
      }
    }
  }
  
  public void cancel()
    throws PrintException
  {
    synchronized (this)
    {
      if (!printing) {
        throw new PrintException("Job is not yet submitted.");
      }
      if ((job != null) && (!printReturned))
      {
        job.cancel();
        notifyEvent(101);
        return;
      }
      throw new PrintException("Job could not be cancelled.");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\print\PSStreamPrintJob.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */