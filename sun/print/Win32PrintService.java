package sun.print;

import java.awt.Window;
import java.awt.print.PrinterJob;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import javax.print.DocFlavor;
import javax.print.DocFlavor.BYTE_ARRAY;
import javax.print.DocFlavor.INPUT_STREAM;
import javax.print.DocFlavor.SERVICE_FORMATTED;
import javax.print.DocFlavor.URL;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.ServiceUIFactory;
import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.AttributeSetUtilities;
import javax.print.attribute.HashAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.Chromaticity;
import javax.print.attribute.standard.ColorSupported;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.CopiesSupported;
import javax.print.attribute.standard.Destination;
import javax.print.attribute.standard.Fidelity;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.MediaTray;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PageRanges;
import javax.print.attribute.standard.PrintQuality;
import javax.print.attribute.standard.PrinterIsAcceptingJobs;
import javax.print.attribute.standard.PrinterName;
import javax.print.attribute.standard.PrinterResolution;
import javax.print.attribute.standard.PrinterState;
import javax.print.attribute.standard.PrinterStateReason;
import javax.print.attribute.standard.PrinterStateReasons;
import javax.print.attribute.standard.QueuedJobCount;
import javax.print.attribute.standard.RequestingUserName;
import javax.print.attribute.standard.Severity;
import javax.print.attribute.standard.SheetCollate;
import javax.print.attribute.standard.Sides;
import javax.print.event.PrintServiceAttributeListener;
import sun.awt.windows.WPrinterJob;

public class Win32PrintService
  implements PrintService, AttributeUpdater, SunPrinterJobService
{
  public static MediaSize[] predefMedia = ;
  private static final DocFlavor[] supportedFlavors = { DocFlavor.BYTE_ARRAY.GIF, DocFlavor.INPUT_STREAM.GIF, DocFlavor.URL.GIF, DocFlavor.BYTE_ARRAY.JPEG, DocFlavor.INPUT_STREAM.JPEG, DocFlavor.URL.JPEG, DocFlavor.BYTE_ARRAY.PNG, DocFlavor.INPUT_STREAM.PNG, DocFlavor.URL.PNG, DocFlavor.SERVICE_FORMATTED.PAGEABLE, DocFlavor.SERVICE_FORMATTED.PRINTABLE, DocFlavor.BYTE_ARRAY.AUTOSENSE, DocFlavor.URL.AUTOSENSE, DocFlavor.INPUT_STREAM.AUTOSENSE };
  private static final Class[] serviceAttrCats = { PrinterName.class, PrinterIsAcceptingJobs.class, QueuedJobCount.class, ColorSupported.class };
  private static Class[] otherAttrCats = { JobName.class, RequestingUserName.class, Copies.class, Destination.class, OrientationRequested.class, PageRanges.class, Media.class, MediaPrintableArea.class, Fidelity.class, SheetCollate.class, SunAlternateMedia.class, Chromaticity.class };
  public static final MediaSizeName[] dmPaperToPrintService = { MediaSizeName.NA_LETTER, MediaSizeName.NA_LETTER, MediaSizeName.TABLOID, MediaSizeName.LEDGER, MediaSizeName.NA_LEGAL, MediaSizeName.INVOICE, MediaSizeName.EXECUTIVE, MediaSizeName.ISO_A3, MediaSizeName.ISO_A4, MediaSizeName.ISO_A4, MediaSizeName.ISO_A5, MediaSizeName.JIS_B4, MediaSizeName.JIS_B5, MediaSizeName.FOLIO, MediaSizeName.QUARTO, MediaSizeName.NA_10X14_ENVELOPE, MediaSizeName.B, MediaSizeName.NA_LETTER, MediaSizeName.NA_NUMBER_9_ENVELOPE, MediaSizeName.NA_NUMBER_10_ENVELOPE, MediaSizeName.NA_NUMBER_11_ENVELOPE, MediaSizeName.NA_NUMBER_12_ENVELOPE, MediaSizeName.NA_NUMBER_14_ENVELOPE, MediaSizeName.C, MediaSizeName.D, MediaSizeName.E, MediaSizeName.ISO_DESIGNATED_LONG, MediaSizeName.ISO_C5, MediaSizeName.ISO_C3, MediaSizeName.ISO_C4, MediaSizeName.ISO_C6, MediaSizeName.ITALY_ENVELOPE, MediaSizeName.ISO_B4, MediaSizeName.ISO_B5, MediaSizeName.ISO_B6, MediaSizeName.ITALY_ENVELOPE, MediaSizeName.MONARCH_ENVELOPE, MediaSizeName.PERSONAL_ENVELOPE, MediaSizeName.NA_10X15_ENVELOPE, MediaSizeName.NA_9X12_ENVELOPE, MediaSizeName.FOLIO, MediaSizeName.ISO_B4, MediaSizeName.JAPANESE_POSTCARD, MediaSizeName.NA_9X11_ENVELOPE };
  private static final MediaTray[] dmPaperBinToPrintService = { MediaTray.TOP, MediaTray.BOTTOM, MediaTray.MIDDLE, MediaTray.MANUAL, MediaTray.ENVELOPE, Win32MediaTray.ENVELOPE_MANUAL, Win32MediaTray.AUTO, Win32MediaTray.TRACTOR, Win32MediaTray.SMALL_FORMAT, Win32MediaTray.LARGE_FORMAT, MediaTray.LARGE_CAPACITY, null, null, MediaTray.MAIN, Win32MediaTray.FORMSOURCE };
  private static int DM_PAPERSIZE = 2;
  private static int DM_PRINTQUALITY = 1024;
  private static int DM_YRESOLUTION = 8192;
  private static final int DMRES_MEDIUM = -3;
  private static final int DMRES_HIGH = -4;
  private static final int DMORIENT_LANDSCAPE = 2;
  private static final int DMDUP_VERTICAL = 2;
  private static final int DMDUP_HORIZONTAL = 3;
  private static final int DMCOLLATE_TRUE = 1;
  private static final int DMCOLOR_MONOCHROME = 1;
  private static final int DMCOLOR_COLOR = 2;
  private static final int DMPAPER_A2 = 66;
  private static final int DMPAPER_A6 = 70;
  private static final int DMPAPER_B6_JIS = 88;
  private static final int DEVCAP_COLOR = 1;
  private static final int DEVCAP_DUPLEX = 2;
  private static final int DEVCAP_COLLATE = 4;
  private static final int DEVCAP_QUALITY = 8;
  private static final int DEVCAP_POSTSCRIPT = 16;
  private String printer;
  private PrinterName name;
  private String port;
  private transient PrintServiceAttributeSet lastSet;
  private transient ServiceNotifier notifier = null;
  private MediaSizeName[] mediaSizeNames;
  private MediaPrintableArea[] mediaPrintables;
  private MediaTray[] mediaTrays;
  private PrinterResolution[] printRes;
  private HashMap mpaMap;
  private int nCopies;
  private int prnCaps;
  private int[] defaultSettings;
  private boolean gotTrays;
  private boolean gotCopies;
  private boolean mediaInitialized;
  private boolean mpaListInitialized;
  private ArrayList idList;
  private MediaSize[] mediaSizes;
  private boolean isInvalid;
  private Win32DocumentPropertiesUI docPropertiesUI = null;
  private Win32ServiceUIFactory uiFactory = null;
  
  Win32PrintService(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("null printer name");
    }
    printer = paramString;
    mediaInitialized = false;
    gotTrays = false;
    gotCopies = false;
    isInvalid = false;
    printRes = null;
    prnCaps = 0;
    defaultSettings = null;
    port = null;
  }
  
  public void invalidateService()
  {
    isInvalid = true;
  }
  
  public String getName()
  {
    return printer;
  }
  
  private PrinterName getPrinterName()
  {
    if (name == null) {
      name = new PrinterName(printer, null);
    }
    return name;
  }
  
  public int findPaperID(MediaSizeName paramMediaSizeName)
  {
    if ((paramMediaSizeName instanceof Win32MediaSize))
    {
      Win32MediaSize localWin32MediaSize = (Win32MediaSize)paramMediaSizeName;
      return localWin32MediaSize.getDMPaper();
    }
    for (int i = 0; i < dmPaperToPrintService.length; i++) {
      if (dmPaperToPrintService[i].equals(paramMediaSizeName)) {
        return i + 1;
      }
    }
    if (paramMediaSizeName.equals(MediaSizeName.ISO_A2)) {
      return 66;
    }
    if (paramMediaSizeName.equals(MediaSizeName.ISO_A6)) {
      return 70;
    }
    if (paramMediaSizeName.equals(MediaSizeName.JIS_B6)) {
      return 88;
    }
    initMedia();
    if ((idList != null) && (mediaSizes != null) && (idList.size() == mediaSizes.length)) {
      for (i = 0; i < idList.size(); i++) {
        if (mediaSizes[i].getMediaSizeName() == paramMediaSizeName) {
          return ((Integer)idList.get(i)).intValue();
        }
      }
    }
    return 0;
  }
  
  public int findTrayID(MediaTray paramMediaTray)
  {
    getMediaTrays();
    if ((paramMediaTray instanceof Win32MediaTray))
    {
      Win32MediaTray localWin32MediaTray = (Win32MediaTray)paramMediaTray;
      return localWin32MediaTray.getDMBinID();
    }
    for (int i = 0; i < dmPaperBinToPrintService.length; i++) {
      if (paramMediaTray.equals(dmPaperBinToPrintService[i])) {
        return i + 1;
      }
    }
    return 0;
  }
  
  public MediaTray findMediaTray(int paramInt)
  {
    if ((paramInt >= 1) && (paramInt <= dmPaperBinToPrintService.length)) {
      return dmPaperBinToPrintService[(paramInt - 1)];
    }
    MediaTray[] arrayOfMediaTray = getMediaTrays();
    if (arrayOfMediaTray != null) {
      for (int i = 0; i < arrayOfMediaTray.length; i++) {
        if ((arrayOfMediaTray[i] instanceof Win32MediaTray))
        {
          Win32MediaTray localWin32MediaTray = (Win32MediaTray)arrayOfMediaTray[i];
          if (winID == paramInt) {
            return localWin32MediaTray;
          }
        }
      }
    }
    return Win32MediaTray.AUTO;
  }
  
  public MediaSizeName findWin32Media(int paramInt)
  {
    if ((paramInt >= 1) && (paramInt <= dmPaperToPrintService.length)) {
      return dmPaperToPrintService[(paramInt - 1)];
    }
    switch (paramInt)
    {
    case 66: 
      return MediaSizeName.ISO_A2;
    case 70: 
      return MediaSizeName.ISO_A6;
    case 88: 
      return MediaSizeName.JIS_B6;
    }
    return null;
  }
  
  private boolean addToUniqueList(ArrayList paramArrayList, MediaSizeName paramMediaSizeName)
  {
    for (int i = 0; i < paramArrayList.size(); i++)
    {
      MediaSizeName localMediaSizeName = (MediaSizeName)paramArrayList.get(i);
      if (localMediaSizeName == paramMediaSizeName) {
        return false;
      }
    }
    paramArrayList.add(paramMediaSizeName);
    return true;
  }
  
  private synchronized void initMedia()
  {
    if (mediaInitialized == true) {
      return;
    }
    mediaInitialized = true;
    int[] arrayOfInt = getAllMediaIDs(printer, getPort());
    if (arrayOfInt == null) {
      return;
    }
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    ArrayList localArrayList3 = new ArrayList();
    int i = 0;
    idList = new ArrayList();
    for (int j = 0; j < arrayOfInt.length; j++) {
      idList.add(Integer.valueOf(arrayOfInt[j]));
    }
    ArrayList localArrayList4 = new ArrayList();
    mediaSizes = getMediaSizes(idList, arrayOfInt, localArrayList4);
    boolean bool;
    for (int k = 0; k < idList.size(); k++)
    {
      Object localObject1 = findWin32Media(((Integer)idList.get(k)).intValue());
      Object localObject2;
      if ((localObject1 != null) && (idList.size() == mediaSizes.length))
      {
        MediaSize localMediaSize = MediaSize.getMediaSizeForName((MediaSizeName)localObject1);
        localObject2 = mediaSizes[k];
        int n = 2540;
        if ((Math.abs(localMediaSize.getX(1) - ((MediaSize)localObject2).getX(1)) > n) || (Math.abs(localMediaSize.getY(1) - ((MediaSize)localObject2).getY(1)) > n)) {
          localObject1 = null;
        }
      }
      int m = localObject1 != null ? 1 : 0;
      if ((localObject1 == null) && (idList.size() == mediaSizes.length)) {
        localObject1 = mediaSizes[k].getMediaSizeName();
      }
      bool = false;
      if (localObject1 != null) {
        bool = addToUniqueList(localArrayList1, (MediaSizeName)localObject1);
      }
      if (((m == 0) || (!bool)) && (idList.size() == localArrayList4.size()))
      {
        localObject2 = Win32MediaSize.findMediaName((String)localArrayList4.get(k));
        if ((localObject2 == null) && (idList.size() == mediaSizes.length))
        {
          localObject2 = new Win32MediaSize((String)localArrayList4.get(k), ((Integer)idList.get(k)).intValue());
          mediaSizes[k] = new MediaSize(mediaSizes[k].getX(1000), mediaSizes[k].getY(1000), 1000, (MediaSizeName)localObject2);
        }
        if ((localObject2 != null) && (localObject2 != localObject1)) {
          if (!bool) {
            bool = addToUniqueList(localArrayList1, localObject1 = localObject2);
          } else {
            localArrayList2.add(localObject2);
          }
        }
      }
    }
    Iterator localIterator = localArrayList2.iterator();
    while (localIterator.hasNext())
    {
      Win32MediaSize localWin32MediaSize = (Win32MediaSize)localIterator.next();
      bool = addToUniqueList(localArrayList1, localWin32MediaSize);
    }
    mediaSizeNames = new MediaSizeName[localArrayList1.size()];
    localArrayList1.toArray(mediaSizeNames);
  }
  
  private synchronized MediaPrintableArea[] getMediaPrintables(MediaSizeName paramMediaSizeName)
  {
    Object localObject1;
    if (paramMediaSizeName == null)
    {
      if (mpaListInitialized == true) {
        return mediaPrintables;
      }
    }
    else if ((mpaMap != null) && (mpaMap.get(paramMediaSizeName) != null))
    {
      localObject1 = new MediaPrintableArea[1];
      localObject1[0] = ((MediaPrintableArea)mpaMap.get(paramMediaSizeName));
      return (MediaPrintableArea[])localObject1;
    }
    initMedia();
    if ((mediaSizeNames == null) || (mediaSizeNames.length == 0)) {
      return null;
    }
    if (paramMediaSizeName != null)
    {
      localObject1 = new MediaSizeName[1];
      localObject1[0] = paramMediaSizeName;
    }
    else
    {
      localObject1 = mediaSizeNames;
    }
    if (mpaMap == null) {
      mpaMap = new HashMap();
    }
    for (int i = 0; i < localObject1.length; i++)
    {
      Object localObject2 = localObject1[i];
      if ((mpaMap.get(localObject2) == null) && (localObject2 != null))
      {
        int j = findPaperID((MediaSizeName)localObject2);
        Object localObject3 = j != 0 ? getMediaPrintableArea(printer, j) : null;
        MediaPrintableArea localMediaPrintableArea = null;
        if (localObject3 != null)
        {
          try
          {
            localMediaPrintableArea = new MediaPrintableArea(localObject3[0], localObject3[1], localObject3[2], localObject3[3], 25400);
            mpaMap.put(localObject2, localMediaPrintableArea);
          }
          catch (IllegalArgumentException localIllegalArgumentException1) {}
        }
        else
        {
          MediaSize localMediaSize = MediaSize.getMediaSizeForName((MediaSizeName)localObject2);
          if (localMediaSize != null) {
            try
            {
              localMediaPrintableArea = new MediaPrintableArea(0.0F, 0.0F, localMediaSize.getX(25400), localMediaSize.getY(25400), 25400);
              mpaMap.put(localObject2, localMediaPrintableArea);
            }
            catch (IllegalArgumentException localIllegalArgumentException2) {}
          }
        }
      }
    }
    if (mpaMap.size() == 0) {
      return null;
    }
    if (paramMediaSizeName != null)
    {
      if (mpaMap.get(paramMediaSizeName) == null) {
        return null;
      }
      MediaPrintableArea[] arrayOfMediaPrintableArea = new MediaPrintableArea[1];
      arrayOfMediaPrintableArea[0] = ((MediaPrintableArea)mpaMap.get(paramMediaSizeName));
      return arrayOfMediaPrintableArea;
    }
    mediaPrintables = ((MediaPrintableArea[])mpaMap.values().toArray(new MediaPrintableArea[0]));
    mpaListInitialized = true;
    return mediaPrintables;
  }
  
  private synchronized MediaTray[] getMediaTrays()
  {
    if ((gotTrays == true) && (mediaTrays != null)) {
      return mediaTrays;
    }
    String str = getPort();
    int[] arrayOfInt = getAllMediaTrays(printer, str);
    String[] arrayOfString = getAllMediaTrayNames(printer, str);
    if ((arrayOfInt == null) || (arrayOfString == null)) {
      return null;
    }
    int i = 0;
    for (int j = 0; j < arrayOfInt.length; j++) {
      if (arrayOfInt[j] > 0) {
        i++;
      }
    }
    MediaTray[] arrayOfMediaTray = new MediaTray[i];
    int m = 0;
    int n = 0;
    while (m < Math.min(arrayOfInt.length, arrayOfString.length))
    {
      int k = arrayOfInt[m];
      if (k > 0) {
        if ((k > dmPaperBinToPrintService.length) || (dmPaperBinToPrintService[(k - 1)] == null)) {
          arrayOfMediaTray[(n++)] = new Win32MediaTray(k, arrayOfString[m]);
        } else {
          arrayOfMediaTray[(n++)] = dmPaperBinToPrintService[(k - 1)];
        }
      }
      m++;
    }
    mediaTrays = arrayOfMediaTray;
    gotTrays = true;
    return mediaTrays;
  }
  
  private boolean isSameSize(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    float f1 = paramFloat1 - paramFloat3;
    float f2 = paramFloat2 - paramFloat4;
    float f3 = paramFloat1 - paramFloat4;
    float f4 = paramFloat2 - paramFloat3;
    return ((Math.abs(f1) <= 1.0F) && (Math.abs(f2) <= 1.0F)) || ((Math.abs(f3) <= 1.0F) && (Math.abs(f4) <= 1.0F));
  }
  
  public MediaSizeName findMatchingMediaSizeNameMM(float paramFloat1, float paramFloat2)
  {
    if (predefMedia != null) {
      for (int i = 0; i < predefMedia.length; i++) {
        if ((predefMedia[i] != null) && (isSameSize(predefMedia[i].getX(1000), predefMedia[i].getY(1000), paramFloat1, paramFloat2))) {
          return predefMedia[i].getMediaSizeName();
        }
      }
    }
    return null;
  }
  
  private MediaSize[] getMediaSizes(ArrayList paramArrayList, int[] paramArrayOfInt, ArrayList<String> paramArrayList1)
  {
    if (paramArrayList1 == null) {
      paramArrayList1 = new ArrayList();
    }
    String str = getPort();
    int[] arrayOfInt = getAllMediaSizes(printer, str);
    String[] arrayOfString = getAllMediaNames(printer, str);
    MediaSizeName localMediaSizeName = null;
    MediaSize localMediaSize = null;
    if ((arrayOfInt == null) || (arrayOfString == null)) {
      return null;
    }
    int i = arrayOfInt.length / 2;
    ArrayList localArrayList = new ArrayList();
    int j = 0;
    while (j < i)
    {
      float f1 = arrayOfInt[(j * 2)] / 10.0F;
      float f2 = arrayOfInt[(j * 2 + 1)] / 10.0F;
      Object localObject;
      if ((f1 <= 0.0F) || (f2 <= 0.0F))
      {
        if (i == paramArrayOfInt.length)
        {
          localObject = Integer.valueOf(paramArrayOfInt[j]);
          paramArrayList.remove(paramArrayList.indexOf(localObject));
        }
      }
      else
      {
        localMediaSizeName = findMatchingMediaSizeNameMM(f1, f2);
        if (localMediaSizeName != null) {
          localMediaSize = MediaSize.getMediaSizeForName(localMediaSizeName);
        }
        if (localMediaSize != null)
        {
          localArrayList.add(localMediaSize);
          paramArrayList1.add(arrayOfString[j]);
        }
        else
        {
          localObject = Win32MediaSize.findMediaName(arrayOfString[j]);
          if (localObject == null) {
            localObject = new Win32MediaSize(arrayOfString[j], paramArrayOfInt[j]);
          }
          try
          {
            localMediaSize = new MediaSize(f1, f2, 1000, (MediaSizeName)localObject);
            localArrayList.add(localMediaSize);
            paramArrayList1.add(arrayOfString[j]);
          }
          catch (IllegalArgumentException localIllegalArgumentException)
          {
            if (i == paramArrayOfInt.length)
            {
              Integer localInteger = Integer.valueOf(paramArrayOfInt[j]);
              paramArrayList.remove(paramArrayList.indexOf(localInteger));
            }
          }
        }
      }
      j++;
      localMediaSize = null;
    }
    MediaSize[] arrayOfMediaSize = new MediaSize[localArrayList.size()];
    localArrayList.toArray(arrayOfMediaSize);
    return arrayOfMediaSize;
  }
  
  private PrinterIsAcceptingJobs getPrinterIsAcceptingJobs()
  {
    if (getJobStatus(printer, 2) != 1) {
      return PrinterIsAcceptingJobs.NOT_ACCEPTING_JOBS;
    }
    return PrinterIsAcceptingJobs.ACCEPTING_JOBS;
  }
  
  private PrinterState getPrinterState()
  {
    if (isInvalid) {
      return PrinterState.STOPPED;
    }
    return null;
  }
  
  private PrinterStateReasons getPrinterStateReasons()
  {
    if (isInvalid)
    {
      PrinterStateReasons localPrinterStateReasons = new PrinterStateReasons();
      localPrinterStateReasons.put(PrinterStateReason.SHUTDOWN, Severity.ERROR);
      return localPrinterStateReasons;
    }
    return null;
  }
  
  private QueuedJobCount getQueuedJobCount()
  {
    int i = getJobStatus(printer, 1);
    if (i != -1) {
      return new QueuedJobCount(i);
    }
    return new QueuedJobCount(0);
  }
  
  private boolean isSupportedCopies(Copies paramCopies)
  {
    synchronized (this)
    {
      if (!gotCopies)
      {
        nCopies = getCopiesSupported(printer, getPort());
        gotCopies = true;
      }
    }
    int i = paramCopies.getValue();
    return (i > 0) && (i <= nCopies);
  }
  
  private boolean isSupportedMedia(MediaSizeName paramMediaSizeName)
  {
    initMedia();
    if (mediaSizeNames != null) {
      for (int i = 0; i < mediaSizeNames.length; i++) {
        if (paramMediaSizeName.equals(mediaSizeNames[i])) {
          return true;
        }
      }
    }
    return false;
  }
  
  private boolean isSupportedMediaPrintableArea(MediaPrintableArea paramMediaPrintableArea)
  {
    getMediaPrintables(null);
    if (mediaPrintables != null) {
      for (int i = 0; i < mediaPrintables.length; i++) {
        if (paramMediaPrintableArea.equals(mediaPrintables[i])) {
          return true;
        }
      }
    }
    return false;
  }
  
  private boolean isSupportedMediaTray(MediaTray paramMediaTray)
  {
    MediaTray[] arrayOfMediaTray = getMediaTrays();
    if (arrayOfMediaTray != null) {
      for (int i = 0; i < arrayOfMediaTray.length; i++) {
        if (paramMediaTray.equals(arrayOfMediaTray[i])) {
          return true;
        }
      }
    }
    return false;
  }
  
  private int getPrinterCapabilities()
  {
    if (prnCaps == 0) {
      prnCaps = getCapabilities(printer, getPort());
    }
    return prnCaps;
  }
  
  private String getPort()
  {
    if (port == null) {
      port = getPrinterPort(printer);
    }
    return port;
  }
  
  private int[] getDefaultPrinterSettings()
  {
    if (defaultSettings == null) {
      defaultSettings = getDefaultSettings(printer, getPort());
    }
    return defaultSettings;
  }
  
  private PrinterResolution[] getPrintResolutions()
  {
    if (printRes == null)
    {
      int[] arrayOfInt = getAllResolutions(printer, getPort());
      if (arrayOfInt == null)
      {
        printRes = new PrinterResolution[0];
      }
      else
      {
        int i = arrayOfInt.length / 2;
        ArrayList localArrayList = new ArrayList();
        for (int j = 0; j < i; j++) {
          try
          {
            PrinterResolution localPrinterResolution = new PrinterResolution(arrayOfInt[(j * 2)], arrayOfInt[(j * 2 + 1)], 100);
            localArrayList.add(localPrinterResolution);
          }
          catch (IllegalArgumentException localIllegalArgumentException) {}
        }
        printRes = ((PrinterResolution[])localArrayList.toArray(new PrinterResolution[localArrayList.size()]));
      }
    }
    return printRes;
  }
  
  private boolean isSupportedResolution(PrinterResolution paramPrinterResolution)
  {
    PrinterResolution[] arrayOfPrinterResolution = getPrintResolutions();
    if (arrayOfPrinterResolution != null) {
      for (int i = 0; i < arrayOfPrinterResolution.length; i++) {
        if (paramPrinterResolution.equals(arrayOfPrinterResolution[i])) {
          return true;
        }
      }
    }
    return false;
  }
  
  public DocPrintJob createPrintJob()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPrintJobAccess();
    }
    return new Win32PrintJob(this);
  }
  
  private PrintServiceAttributeSet getDynamicAttributes()
  {
    HashPrintServiceAttributeSet localHashPrintServiceAttributeSet = new HashPrintServiceAttributeSet();
    localHashPrintServiceAttributeSet.add(getPrinterIsAcceptingJobs());
    localHashPrintServiceAttributeSet.add(getQueuedJobCount());
    return localHashPrintServiceAttributeSet;
  }
  
  public PrintServiceAttributeSet getUpdatedAttributes()
  {
    PrintServiceAttributeSet localPrintServiceAttributeSet = getDynamicAttributes();
    if (lastSet == null)
    {
      lastSet = localPrintServiceAttributeSet;
      return AttributeSetUtilities.unmodifiableView(localPrintServiceAttributeSet);
    }
    HashPrintServiceAttributeSet localHashPrintServiceAttributeSet = new HashPrintServiceAttributeSet();
    Attribute[] arrayOfAttribute = localPrintServiceAttributeSet.toArray();
    for (int i = 0; i < arrayOfAttribute.length; i++)
    {
      Attribute localAttribute = arrayOfAttribute[i];
      if (!lastSet.containsValue(localAttribute)) {
        localHashPrintServiceAttributeSet.add(localAttribute);
      }
    }
    lastSet = localPrintServiceAttributeSet;
    return AttributeSetUtilities.unmodifiableView(localHashPrintServiceAttributeSet);
  }
  
  public void wakeNotifier()
  {
    synchronized (this)
    {
      if (notifier != null) {
        notifier.wake();
      }
    }
  }
  
  public void addPrintServiceAttributeListener(PrintServiceAttributeListener paramPrintServiceAttributeListener)
  {
    synchronized (this)
    {
      if (paramPrintServiceAttributeListener == null) {
        return;
      }
      if (notifier == null) {
        notifier = new ServiceNotifier(this);
      }
      notifier.addListener(paramPrintServiceAttributeListener);
    }
  }
  
  public void removePrintServiceAttributeListener(PrintServiceAttributeListener paramPrintServiceAttributeListener)
  {
    synchronized (this)
    {
      if ((paramPrintServiceAttributeListener == null) || (notifier == null)) {
        return;
      }
      notifier.removeListener(paramPrintServiceAttributeListener);
      if (notifier.isEmpty())
      {
        notifier.stopNotifier();
        notifier = null;
      }
    }
  }
  
  public <T extends PrintServiceAttribute> T getAttribute(Class<T> paramClass)
  {
    if (paramClass == null) {
      throw new NullPointerException("category");
    }
    if (!PrintServiceAttribute.class.isAssignableFrom(paramClass)) {
      throw new IllegalArgumentException("Not a PrintServiceAttribute");
    }
    if (paramClass == ColorSupported.class)
    {
      int i = getPrinterCapabilities();
      if ((i & 0x1) != 0) {
        return ColorSupported.SUPPORTED;
      }
      return ColorSupported.NOT_SUPPORTED;
    }
    if (paramClass == PrinterName.class) {
      return getPrinterName();
    }
    if (paramClass == PrinterState.class) {
      return getPrinterState();
    }
    if (paramClass == PrinterStateReasons.class) {
      return getPrinterStateReasons();
    }
    if (paramClass == QueuedJobCount.class) {
      return getQueuedJobCount();
    }
    if (paramClass == PrinterIsAcceptingJobs.class) {
      return getPrinterIsAcceptingJobs();
    }
    return null;
  }
  
  public PrintServiceAttributeSet getAttributes()
  {
    HashPrintServiceAttributeSet localHashPrintServiceAttributeSet = new HashPrintServiceAttributeSet();
    localHashPrintServiceAttributeSet.add(getPrinterName());
    localHashPrintServiceAttributeSet.add(getPrinterIsAcceptingJobs());
    PrinterState localPrinterState = getPrinterState();
    if (localPrinterState != null) {
      localHashPrintServiceAttributeSet.add(localPrinterState);
    }
    PrinterStateReasons localPrinterStateReasons = getPrinterStateReasons();
    if (localPrinterStateReasons != null) {
      localHashPrintServiceAttributeSet.add(localPrinterStateReasons);
    }
    localHashPrintServiceAttributeSet.add(getQueuedJobCount());
    int i = getPrinterCapabilities();
    if ((i & 0x1) != 0) {
      localHashPrintServiceAttributeSet.add(ColorSupported.SUPPORTED);
    } else {
      localHashPrintServiceAttributeSet.add(ColorSupported.NOT_SUPPORTED);
    }
    return AttributeSetUtilities.unmodifiableView(localHashPrintServiceAttributeSet);
  }
  
  public DocFlavor[] getSupportedDocFlavors()
  {
    int i = supportedFlavors.length;
    int j = getPrinterCapabilities();
    DocFlavor[] arrayOfDocFlavor;
    if ((j & 0x10) != 0)
    {
      arrayOfDocFlavor = new DocFlavor[i + 3];
      System.arraycopy(supportedFlavors, 0, arrayOfDocFlavor, 0, i);
      arrayOfDocFlavor[i] = DocFlavor.BYTE_ARRAY.POSTSCRIPT;
      arrayOfDocFlavor[(i + 1)] = DocFlavor.INPUT_STREAM.POSTSCRIPT;
      arrayOfDocFlavor[(i + 2)] = DocFlavor.URL.POSTSCRIPT;
    }
    else
    {
      arrayOfDocFlavor = new DocFlavor[i];
      System.arraycopy(supportedFlavors, 0, arrayOfDocFlavor, 0, i);
    }
    return arrayOfDocFlavor;
  }
  
  public boolean isDocFlavorSupported(DocFlavor paramDocFlavor)
  {
    DocFlavor[] arrayOfDocFlavor;
    if (isPostScriptFlavor(paramDocFlavor)) {
      arrayOfDocFlavor = getSupportedDocFlavors();
    } else {
      arrayOfDocFlavor = supportedFlavors;
    }
    for (int i = 0; i < arrayOfDocFlavor.length; i++) {
      if (paramDocFlavor.equals(arrayOfDocFlavor[i])) {
        return true;
      }
    }
    return false;
  }
  
  public Class<?>[] getSupportedAttributeCategories()
  {
    ArrayList localArrayList = new ArrayList(otherAttrCats.length + 3);
    for (int i = 0; i < otherAttrCats.length; i++) {
      localArrayList.add(otherAttrCats[i]);
    }
    i = getPrinterCapabilities();
    if ((i & 0x2) != 0) {
      localArrayList.add(Sides.class);
    }
    if ((i & 0x8) != 0)
    {
      localObject = getDefaultPrinterSettings();
      if ((localObject[3] >= -4) && (localObject[3] < 0)) {
        localArrayList.add(PrintQuality.class);
      }
    }
    Object localObject = getPrintResolutions();
    if ((localObject != null) && (localObject.length > 0)) {
      localArrayList.add(PrinterResolution.class);
    }
    return (Class[])localArrayList.toArray(new Class[localArrayList.size()]);
  }
  
  public boolean isAttributeCategorySupported(Class<? extends Attribute> paramClass)
  {
    if (paramClass == null) {
      throw new NullPointerException("null category");
    }
    if (!Attribute.class.isAssignableFrom(paramClass)) {
      throw new IllegalArgumentException(paramClass + " is not an Attribute");
    }
    Class[] arrayOfClass = getSupportedAttributeCategories();
    for (int i = 0; i < arrayOfClass.length; i++) {
      if (paramClass.equals(arrayOfClass[i])) {
        return true;
      }
    }
    return false;
  }
  
  public Object getDefaultAttributeValue(Class<? extends Attribute> paramClass)
  {
    if (paramClass == null) {
      throw new NullPointerException("null category");
    }
    if (!Attribute.class.isAssignableFrom(paramClass)) {
      throw new IllegalArgumentException(paramClass + " is not an Attribute");
    }
    if (!isAttributeCategorySupported(paramClass)) {
      return null;
    }
    int[] arrayOfInt = getDefaultPrinterSettings();
    int i = arrayOfInt[0];
    SecurityException localSecurityException1 = arrayOfInt[2];
    URISyntaxException localURISyntaxException1 = arrayOfInt[3];
    int j = arrayOfInt[4];
    int k = arrayOfInt[5];
    int m = arrayOfInt[6];
    int n = arrayOfInt[7];
    int i1 = arrayOfInt[8];
    if (paramClass == Copies.class)
    {
      if (j > 0) {
        return new Copies(j);
      }
      return new Copies(1);
    }
    if (paramClass == Chromaticity.class)
    {
      if (i1 == 2) {
        return Chromaticity.COLOR;
      }
      return Chromaticity.MONOCHROME;
    }
    if (paramClass == JobName.class) {
      return new JobName("Java Printing", null);
    }
    if (paramClass == OrientationRequested.class)
    {
      if (k == 2) {
        return OrientationRequested.LANDSCAPE;
      }
      return OrientationRequested.PORTRAIT;
    }
    if (paramClass == PageRanges.class) {
      return new PageRanges(1, Integer.MAX_VALUE);
    }
    MediaSizeName localMediaSizeName;
    Object localObject;
    if (paramClass == Media.class)
    {
      localMediaSizeName = findWin32Media(i);
      if (localMediaSizeName != null)
      {
        if ((!isSupportedMedia(localMediaSizeName)) && (mediaSizeNames != null))
        {
          localMediaSizeName = mediaSizeNames[0];
          i = findPaperID(localMediaSizeName);
        }
        return localMediaSizeName;
      }
      initMedia();
      if ((mediaSizeNames != null) && (mediaSizeNames.length > 0))
      {
        if ((idList != null) && (mediaSizes != null) && (idList.size() == mediaSizes.length))
        {
          localObject = Integer.valueOf(i);
          int i3 = idList.indexOf(localObject);
          if ((i3 >= 0) && (i3 < mediaSizes.length)) {
            return mediaSizes[i3].getMediaSizeName();
          }
        }
        return mediaSizeNames[0];
      }
    }
    else
    {
      if (paramClass == MediaPrintableArea.class)
      {
        localMediaSizeName = findWin32Media(i);
        if ((localMediaSizeName != null) && (!isSupportedMedia(localMediaSizeName)) && (mediaSizeNames != null)) {
          i = findPaperID(mediaSizeNames[0]);
        }
        localObject = getMediaPrintableArea(printer, i);
        if (localObject != null)
        {
          MediaPrintableArea localMediaPrintableArea = null;
          try
          {
            localMediaPrintableArea = new MediaPrintableArea(localObject[0], localObject[1], localObject[2], localObject[3], 25400);
          }
          catch (IllegalArgumentException localIllegalArgumentException) {}
          return localMediaPrintableArea;
        }
        return null;
      }
      if (paramClass == SunAlternateMedia.class) {
        return null;
      }
      if (paramClass == Destination.class) {
        try
        {
          return new Destination(new File("out.prn").toURI());
        }
        catch (SecurityException localSecurityException2)
        {
          try
          {
            return new Destination(new URI("file:out.prn"));
          }
          catch (URISyntaxException localURISyntaxException2)
          {
            return null;
          }
        }
      }
      if (paramClass == Sides.class)
      {
        switch (m)
        {
        case 2: 
          return Sides.TWO_SIDED_LONG_EDGE;
        case 3: 
          return Sides.TWO_SIDED_SHORT_EDGE;
        }
        return Sides.ONE_SIDED;
      }
      if (paramClass == PrinterResolution.class)
      {
        localSecurityException2 = localSecurityException1;
        localURISyntaxException2 = localURISyntaxException1;
        if ((localURISyntaxException2 < 0) || (localSecurityException2 < 0))
        {
          int i4 = localSecurityException2 > localURISyntaxException2 ? localSecurityException2 : localURISyntaxException2;
          if (i4 > 0) {
            return new PrinterResolution(i4, i4, 100);
          }
        }
        else
        {
          return new PrinterResolution(localURISyntaxException2, localSecurityException2, 100);
        }
      }
      else
      {
        if (paramClass == ColorSupported.class)
        {
          int i2 = getPrinterCapabilities();
          if ((i2 & 0x1) != 0) {
            return ColorSupported.SUPPORTED;
          }
          return ColorSupported.NOT_SUPPORTED;
        }
        if (paramClass == PrintQuality.class)
        {
          if ((localURISyntaxException1 < 0) && (localURISyntaxException1 >= -4))
          {
            switch (localURISyntaxException1)
            {
            case -4: 
              return PrintQuality.HIGH;
            case -3: 
              return PrintQuality.NORMAL;
            }
            return PrintQuality.DRAFT;
          }
        }
        else
        {
          if (paramClass == RequestingUserName.class)
          {
            String str = "";
            try
            {
              str = System.getProperty("user.name", "");
            }
            catch (SecurityException localSecurityException3) {}
            return new RequestingUserName(str, null);
          }
          if (paramClass == SheetCollate.class)
          {
            if (n == 1) {
              return SheetCollate.COLLATED;
            }
            return SheetCollate.UNCOLLATED;
          }
          if (paramClass == Fidelity.class) {
            return Fidelity.FIDELITY_FALSE;
          }
        }
      }
    }
    return null;
  }
  
  private boolean isPostScriptFlavor(DocFlavor paramDocFlavor)
  {
    return (paramDocFlavor.equals(DocFlavor.BYTE_ARRAY.POSTSCRIPT)) || (paramDocFlavor.equals(DocFlavor.INPUT_STREAM.POSTSCRIPT)) || (paramDocFlavor.equals(DocFlavor.URL.POSTSCRIPT));
  }
  
  private boolean isPSDocAttr(Class paramClass)
  {
    return (paramClass == OrientationRequested.class) || (paramClass == Copies.class);
  }
  
  private boolean isAutoSense(DocFlavor paramDocFlavor)
  {
    return (paramDocFlavor.equals(DocFlavor.BYTE_ARRAY.AUTOSENSE)) || (paramDocFlavor.equals(DocFlavor.INPUT_STREAM.AUTOSENSE)) || (paramDocFlavor.equals(DocFlavor.URL.AUTOSENSE));
  }
  
  public Object getSupportedAttributeValues(Class<? extends Attribute> paramClass, DocFlavor paramDocFlavor, AttributeSet paramAttributeSet)
  {
    if (paramClass == null) {
      throw new NullPointerException("null category");
    }
    if (!Attribute.class.isAssignableFrom(paramClass)) {
      throw new IllegalArgumentException(paramClass + " does not implement Attribute");
    }
    if (paramDocFlavor != null)
    {
      if (!isDocFlavorSupported(paramDocFlavor)) {
        throw new IllegalArgumentException(paramDocFlavor + " is an unsupported flavor");
      }
      if ((isAutoSense(paramDocFlavor)) || ((isPostScriptFlavor(paramDocFlavor)) && (isPSDocAttr(paramClass)))) {
        return null;
      }
    }
    if (!isAttributeCategorySupported(paramClass)) {
      return null;
    }
    if (paramClass == JobName.class) {
      return new JobName("Java Printing", null);
    }
    if (paramClass == RequestingUserName.class)
    {
      String str = "";
      try
      {
        str = System.getProperty("user.name", "");
      }
      catch (SecurityException localSecurityException2) {}
      return new RequestingUserName(str, null);
    }
    int i;
    if (paramClass == ColorSupported.class)
    {
      i = getPrinterCapabilities();
      if ((i & 0x1) != 0) {
        return ColorSupported.SUPPORTED;
      }
      return ColorSupported.NOT_SUPPORTED;
    }
    if (paramClass == Chromaticity.class)
    {
      if ((paramDocFlavor == null) || (paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE)) || (paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)) || (paramDocFlavor.equals(DocFlavor.BYTE_ARRAY.GIF)) || (paramDocFlavor.equals(DocFlavor.INPUT_STREAM.GIF)) || (paramDocFlavor.equals(DocFlavor.URL.GIF)) || (paramDocFlavor.equals(DocFlavor.BYTE_ARRAY.JPEG)) || (paramDocFlavor.equals(DocFlavor.INPUT_STREAM.JPEG)) || (paramDocFlavor.equals(DocFlavor.URL.JPEG)) || (paramDocFlavor.equals(DocFlavor.BYTE_ARRAY.PNG)) || (paramDocFlavor.equals(DocFlavor.INPUT_STREAM.PNG)) || (paramDocFlavor.equals(DocFlavor.URL.PNG)))
      {
        i = getPrinterCapabilities();
        if ((i & 0x1) == 0)
        {
          arrayOfChromaticity = new Chromaticity[1];
          arrayOfChromaticity[0] = Chromaticity.MONOCHROME;
          return arrayOfChromaticity;
        }
        Chromaticity[] arrayOfChromaticity = new Chromaticity[2];
        arrayOfChromaticity[0] = Chromaticity.MONOCHROME;
        arrayOfChromaticity[1] = Chromaticity.COLOR;
        return arrayOfChromaticity;
      }
      return null;
    }
    if (paramClass == Destination.class) {
      try
      {
        return new Destination(new File("out.prn").toURI());
      }
      catch (SecurityException localSecurityException1)
      {
        try
        {
          return new Destination(new URI("file:out.prn"));
        }
        catch (URISyntaxException localURISyntaxException)
        {
          return null;
        }
      }
    }
    if (paramClass == OrientationRequested.class)
    {
      if ((paramDocFlavor == null) || (paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE)) || (paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)) || (paramDocFlavor.equals(DocFlavor.INPUT_STREAM.GIF)) || (paramDocFlavor.equals(DocFlavor.INPUT_STREAM.JPEG)) || (paramDocFlavor.equals(DocFlavor.INPUT_STREAM.PNG)) || (paramDocFlavor.equals(DocFlavor.BYTE_ARRAY.GIF)) || (paramDocFlavor.equals(DocFlavor.BYTE_ARRAY.JPEG)) || (paramDocFlavor.equals(DocFlavor.BYTE_ARRAY.PNG)) || (paramDocFlavor.equals(DocFlavor.URL.GIF)) || (paramDocFlavor.equals(DocFlavor.URL.JPEG)) || (paramDocFlavor.equals(DocFlavor.URL.PNG)))
      {
        OrientationRequested[] arrayOfOrientationRequested = new OrientationRequested[3];
        arrayOfOrientationRequested[0] = OrientationRequested.PORTRAIT;
        arrayOfOrientationRequested[1] = OrientationRequested.LANDSCAPE;
        arrayOfOrientationRequested[2] = OrientationRequested.REVERSE_LANDSCAPE;
        return arrayOfOrientationRequested;
      }
      return null;
    }
    if ((paramClass == Copies.class) || (paramClass == CopiesSupported.class))
    {
      synchronized (this)
      {
        if (!gotCopies)
        {
          nCopies = getCopiesSupported(printer, getPort());
          gotCopies = true;
        }
      }
      return new CopiesSupported(1, nCopies);
    }
    Object localObject2;
    Object localObject4;
    if (paramClass == Media.class)
    {
      initMedia();
      int j = mediaSizeNames == null ? 0 : mediaSizeNames.length;
      localObject2 = getMediaTrays();
      j += (localObject2 == null ? 0 : localObject2.length);
      localObject4 = new Media[j];
      if (mediaSizeNames != null) {
        System.arraycopy(mediaSizeNames, 0, localObject4, 0, mediaSizeNames.length);
      }
      if (localObject2 != null) {
        System.arraycopy(localObject2, 0, localObject4, j - localObject2.length, localObject2.length);
      }
      return localObject4;
    }
    Object localObject1;
    if (paramClass == MediaPrintableArea.class)
    {
      localObject1 = null;
      if ((paramAttributeSet != null) && ((localObject1 = (Media)paramAttributeSet.get(Media.class)) != null) && (!(localObject1 instanceof MediaSizeName))) {
        localObject1 = null;
      }
      localObject2 = getMediaPrintables((MediaSizeName)localObject1);
      if (localObject2 != null)
      {
        localObject4 = new MediaPrintableArea[localObject2.length];
        System.arraycopy(localObject2, 0, localObject4, 0, localObject2.length);
        return localObject4;
      }
      return null;
    }
    if (paramClass == SunAlternateMedia.class) {
      return new SunAlternateMedia((Media)getDefaultAttributeValue(Media.class));
    }
    if (paramClass == PageRanges.class)
    {
      if ((paramDocFlavor == null) || (paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE)) || (paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)))
      {
        localObject1 = new PageRanges[1];
        localObject1[0] = new PageRanges(1, Integer.MAX_VALUE);
        return localObject1;
      }
      return null;
    }
    if (paramClass == PrinterResolution.class)
    {
      localObject1 = getPrintResolutions();
      if (localObject1 == null) {
        return null;
      }
      localObject2 = new PrinterResolution[localObject1.length];
      System.arraycopy(localObject1, 0, localObject2, 0, localObject1.length);
      return localObject2;
    }
    if (paramClass == Sides.class)
    {
      if ((paramDocFlavor == null) || (paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE)) || (paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)))
      {
        localObject1 = new Sides[3];
        localObject1[0] = Sides.ONE_SIDED;
        localObject1[1] = Sides.TWO_SIDED_LONG_EDGE;
        localObject1[2] = Sides.TWO_SIDED_SHORT_EDGE;
        return localObject1;
      }
      return null;
    }
    if (paramClass == PrintQuality.class)
    {
      localObject1 = new PrintQuality[3];
      localObject1[0] = PrintQuality.DRAFT;
      localObject1[1] = PrintQuality.HIGH;
      localObject1[2] = PrintQuality.NORMAL;
      return localObject1;
    }
    if (paramClass == SheetCollate.class)
    {
      if ((paramDocFlavor == null) || (paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE)) || (paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)))
      {
        localObject1 = new SheetCollate[2];
        localObject1[0] = SheetCollate.COLLATED;
        localObject1[1] = SheetCollate.UNCOLLATED;
        return localObject1;
      }
      return null;
    }
    if (paramClass == Fidelity.class)
    {
      localObject1 = new Fidelity[2];
      localObject1[0] = Fidelity.FIDELITY_FALSE;
      localObject1[1] = Fidelity.FIDELITY_TRUE;
      return localObject1;
    }
    return null;
  }
  
  public boolean isAttributeValueSupported(Attribute paramAttribute, DocFlavor paramDocFlavor, AttributeSet paramAttributeSet)
  {
    if (paramAttribute == null) {
      throw new NullPointerException("null attribute");
    }
    Class localClass = paramAttribute.getCategory();
    if (paramDocFlavor != null)
    {
      if (!isDocFlavorSupported(paramDocFlavor)) {
        throw new IllegalArgumentException(paramDocFlavor + " is an unsupported flavor");
      }
      if ((isAutoSense(paramDocFlavor)) || ((isPostScriptFlavor(paramDocFlavor)) && (isPSDocAttr(localClass)))) {
        return false;
      }
    }
    if (!isAttributeCategorySupported(localClass)) {
      return false;
    }
    if (localClass == Chromaticity.class)
    {
      if ((paramDocFlavor == null) || (paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE)) || (paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)) || (paramDocFlavor.equals(DocFlavor.BYTE_ARRAY.GIF)) || (paramDocFlavor.equals(DocFlavor.INPUT_STREAM.GIF)) || (paramDocFlavor.equals(DocFlavor.URL.GIF)) || (paramDocFlavor.equals(DocFlavor.BYTE_ARRAY.JPEG)) || (paramDocFlavor.equals(DocFlavor.INPUT_STREAM.JPEG)) || (paramDocFlavor.equals(DocFlavor.URL.JPEG)) || (paramDocFlavor.equals(DocFlavor.BYTE_ARRAY.PNG)) || (paramDocFlavor.equals(DocFlavor.INPUT_STREAM.PNG)) || (paramDocFlavor.equals(DocFlavor.URL.PNG)))
      {
        int i = getPrinterCapabilities();
        if ((i & 0x1) != 0) {
          return true;
        }
        return paramAttribute == Chromaticity.MONOCHROME;
      }
      return false;
    }
    if (localClass == Copies.class) {
      return isSupportedCopies((Copies)paramAttribute);
    }
    Object localObject;
    if (localClass == Destination.class)
    {
      localObject = ((Destination)paramAttribute).getURI();
      return ("file".equals(((URI)localObject).getScheme())) && (!((URI)localObject).getSchemeSpecificPart().equals(""));
    }
    if (localClass == Media.class)
    {
      if ((paramAttribute instanceof MediaSizeName)) {
        return isSupportedMedia((MediaSizeName)paramAttribute);
      }
      if ((paramAttribute instanceof MediaTray)) {
        return isSupportedMediaTray((MediaTray)paramAttribute);
      }
    }
    else
    {
      if (localClass == MediaPrintableArea.class) {
        return isSupportedMediaPrintableArea((MediaPrintableArea)paramAttribute);
      }
      if (localClass == SunAlternateMedia.class)
      {
        localObject = ((SunAlternateMedia)paramAttribute).getMedia();
        return isAttributeValueSupported((Attribute)localObject, paramDocFlavor, paramAttributeSet);
      }
      if ((localClass == PageRanges.class) || (localClass == SheetCollate.class) || (localClass == Sides.class))
      {
        if ((paramDocFlavor != null) && (!paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE)) && (!paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE))) {
          return false;
        }
      }
      else if (localClass == PrinterResolution.class)
      {
        if ((paramAttribute instanceof PrinterResolution)) {
          return isSupportedResolution((PrinterResolution)paramAttribute);
        }
      }
      else if (localClass == OrientationRequested.class)
      {
        if ((paramAttribute == OrientationRequested.REVERSE_PORTRAIT) || ((paramDocFlavor != null) && (!paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE)) && (!paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)) && (!paramDocFlavor.equals(DocFlavor.INPUT_STREAM.GIF)) && (!paramDocFlavor.equals(DocFlavor.INPUT_STREAM.JPEG)) && (!paramDocFlavor.equals(DocFlavor.INPUT_STREAM.PNG)) && (!paramDocFlavor.equals(DocFlavor.BYTE_ARRAY.GIF)) && (!paramDocFlavor.equals(DocFlavor.BYTE_ARRAY.JPEG)) && (!paramDocFlavor.equals(DocFlavor.BYTE_ARRAY.PNG)) && (!paramDocFlavor.equals(DocFlavor.URL.GIF)) && (!paramDocFlavor.equals(DocFlavor.URL.JPEG)) && (!paramDocFlavor.equals(DocFlavor.URL.PNG)))) {
          return false;
        }
      }
      else if (localClass == ColorSupported.class)
      {
        int j = getPrinterCapabilities();
        int k = (j & 0x1) != 0 ? 1 : 0;
        if (((k == 0) && (paramAttribute == ColorSupported.SUPPORTED)) || ((k != 0) && (paramAttribute == ColorSupported.NOT_SUPPORTED))) {
          return false;
        }
      }
    }
    return true;
  }
  
  public AttributeSet getUnsupportedAttributes(DocFlavor paramDocFlavor, AttributeSet paramAttributeSet)
  {
    if ((paramDocFlavor != null) && (!isDocFlavorSupported(paramDocFlavor))) {
      throw new IllegalArgumentException("flavor " + paramDocFlavor + "is not supported");
    }
    if (paramAttributeSet == null) {
      return null;
    }
    HashAttributeSet localHashAttributeSet = new HashAttributeSet();
    Attribute[] arrayOfAttribute = paramAttributeSet.toArray();
    for (int i = 0; i < arrayOfAttribute.length; i++) {
      try
      {
        Attribute localAttribute = arrayOfAttribute[i];
        if (!isAttributeCategorySupported(localAttribute.getCategory())) {
          localHashAttributeSet.add(localAttribute);
        } else if (!isAttributeValueSupported(localAttribute, paramDocFlavor, paramAttributeSet)) {
          localHashAttributeSet.add(localAttribute);
        }
      }
      catch (ClassCastException localClassCastException) {}
    }
    if (localHashAttributeSet.isEmpty()) {
      return null;
    }
    return localHashAttributeSet;
  }
  
  private synchronized DocumentPropertiesUI getDocumentPropertiesUI()
  {
    return new Win32DocumentPropertiesUI(this, null);
  }
  
  public synchronized ServiceUIFactory getServiceUIFactory()
  {
    if (uiFactory == null) {
      uiFactory = new Win32ServiceUIFactory(this);
    }
    return uiFactory;
  }
  
  public String toString()
  {
    return "Win32 Printer : " + getName();
  }
  
  public boolean equals(Object paramObject)
  {
    return (paramObject == this) || (((paramObject instanceof Win32PrintService)) && (((Win32PrintService)paramObject).getName().equals(getName())));
  }
  
  public int hashCode()
  {
    return getClass().hashCode() + getName().hashCode();
  }
  
  public boolean usesClass(Class paramClass)
  {
    return paramClass == WPrinterJob.class;
  }
  
  private native int[] getAllMediaIDs(String paramString1, String paramString2);
  
  private native int[] getAllMediaSizes(String paramString1, String paramString2);
  
  private native int[] getAllMediaTrays(String paramString1, String paramString2);
  
  private native float[] getMediaPrintableArea(String paramString, int paramInt);
  
  private native String[] getAllMediaNames(String paramString1, String paramString2);
  
  private native String[] getAllMediaTrayNames(String paramString1, String paramString2);
  
  private native int getCopiesSupported(String paramString1, String paramString2);
  
  private native int[] getAllResolutions(String paramString1, String paramString2);
  
  private native int getCapabilities(String paramString1, String paramString2);
  
  private native int[] getDefaultSettings(String paramString1, String paramString2);
  
  private native int getJobStatus(String paramString, int paramInt);
  
  private native String getPrinterPort(String paramString);
  
  private static class Win32DocumentPropertiesUI
    extends DocumentPropertiesUI
  {
    Win32PrintService service;
    
    private Win32DocumentPropertiesUI(Win32PrintService paramWin32PrintService)
    {
      service = paramWin32PrintService;
    }
    
    public PrintRequestAttributeSet showDocumentProperties(PrinterJob paramPrinterJob, Window paramWindow, PrintService paramPrintService, PrintRequestAttributeSet paramPrintRequestAttributeSet)
    {
      if (!(paramPrinterJob instanceof WPrinterJob)) {
        return null;
      }
      WPrinterJob localWPrinterJob = (WPrinterJob)paramPrinterJob;
      return localWPrinterJob.showDocumentProperties(paramWindow, paramPrintService, paramPrintRequestAttributeSet);
    }
  }
  
  private static class Win32ServiceUIFactory
    extends ServiceUIFactory
  {
    Win32PrintService service;
    
    Win32ServiceUIFactory(Win32PrintService paramWin32PrintService)
    {
      service = paramWin32PrintService;
    }
    
    public Object getUI(int paramInt, String paramString)
    {
      if (paramInt <= 3) {
        return null;
      }
      if ((paramInt == 199) && (DocumentPropertiesUI.DOCPROPERTIESCLASSNAME.equals(paramString))) {
        return service.getDocumentPropertiesUI();
      }
      throw new IllegalArgumentException("Unsupported role");
    }
    
    public String[] getUIClassNamesForRole(int paramInt)
    {
      if (paramInt <= 3) {
        return null;
      }
      if (paramInt == 199)
      {
        String[] arrayOfString = new String[0];
        arrayOfString[0] = DocumentPropertiesUI.DOCPROPERTIESCLASSNAME;
        return arrayOfString;
      }
      throw new IllegalArgumentException("Unsupported role");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\print\Win32PrintService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */