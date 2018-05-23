package sun.print;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.awt.image.BufferedImage;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterAbortException;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Locale;
import javax.print.DocFlavor.SERVICE_FORMATTED;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.ServiceUI;
import javax.print.StreamPrintService;
import javax.print.StreamPrintServiceFactory;
import javax.print.attribute.Attribute;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.Destination;
import javax.print.attribute.standard.DialogTypeSelection;
import javax.print.attribute.standard.Fidelity;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.JobSheets;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSize.NA;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PageRanges;
import javax.print.attribute.standard.PrinterIsAcceptingJobs;
import javax.print.attribute.standard.PrinterState;
import javax.print.attribute.standard.PrinterStateReason;
import javax.print.attribute.standard.PrinterStateReasons;
import javax.print.attribute.standard.RequestingUserName;
import javax.print.attribute.standard.SheetCollate;
import javax.print.attribute.standard.Sides;
import sun.awt.image.ByteInterleavedRaster;
import sun.security.action.GetPropertyAction;

public abstract class RasterPrinterJob
  extends PrinterJob
{
  protected static final int PRINTER = 0;
  protected static final int FILE = 1;
  protected static final int STREAM = 2;
  protected static final int MAX_UNKNOWN_PAGES = 9999;
  protected static final int PD_ALLPAGES = 0;
  protected static final int PD_SELECTION = 1;
  protected static final int PD_PAGENUMS = 2;
  protected static final int PD_NOSELECTION = 4;
  private static final int MAX_BAND_SIZE = 4194304;
  private static final float DPI = 72.0F;
  private static final String FORCE_PIPE_PROP = "sun.java2d.print.pipeline";
  private static final String FORCE_RASTER = "raster";
  private static final String FORCE_PDL = "pdl";
  private static final String SHAPE_TEXT_PROP = "sun.java2d.print.shapetext";
  public static boolean forcePDL = false;
  public static boolean forceRaster = false;
  public static boolean shapeTextProp = false;
  private int cachedBandWidth = 0;
  private int cachedBandHeight = 0;
  private BufferedImage cachedBand = null;
  private int mNumCopies = 1;
  private boolean mCollate = false;
  private int mFirstPage = -1;
  private int mLastPage = -1;
  private Paper previousPaper;
  protected Pageable mDocument = new Book();
  private String mDocName = "Java Printing";
  protected boolean performingPrinting = false;
  protected boolean userCancelled = false;
  private FilePermission printToFilePermission;
  private ArrayList redrawList = new ArrayList();
  private int copiesAttr;
  private String jobNameAttr;
  private String userNameAttr;
  private PageRanges pageRangesAttr;
  protected Sides sidesAttr;
  protected String destinationAttr;
  protected boolean noJobSheet = false;
  protected int mDestType = 1;
  protected String mDestination = "";
  protected boolean collateAttReq = false;
  protected boolean landscapeRotates270 = false;
  protected PrintRequestAttributeSet attributes = null;
  protected PrintService myService;
  public static boolean debugPrint = false;
  private int deviceWidth;
  private int deviceHeight;
  private AffineTransform defaultDeviceTransform;
  private PrinterGraphicsConfig pgConfig;
  
  public RasterPrinterJob() {}
  
  protected abstract double getXRes();
  
  protected abstract double getYRes();
  
  protected abstract double getPhysicalPrintableX(Paper paramPaper);
  
  protected abstract double getPhysicalPrintableY(Paper paramPaper);
  
  protected abstract double getPhysicalPrintableWidth(Paper paramPaper);
  
  protected abstract double getPhysicalPrintableHeight(Paper paramPaper);
  
  protected abstract double getPhysicalPageWidth(Paper paramPaper);
  
  protected abstract double getPhysicalPageHeight(Paper paramPaper);
  
  protected abstract void startPage(PageFormat paramPageFormat, Printable paramPrintable, int paramInt, boolean paramBoolean)
    throws PrinterException;
  
  protected abstract void endPage(PageFormat paramPageFormat, Printable paramPrintable, int paramInt)
    throws PrinterException;
  
  protected abstract void printBand(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws PrinterException;
  
  public void saveState(AffineTransform paramAffineTransform, Shape paramShape, Rectangle2D paramRectangle2D, double paramDouble1, double paramDouble2)
  {
    GraphicsState localGraphicsState = new GraphicsState(null);
    theTransform = paramAffineTransform;
    theClip = paramShape;
    region = paramRectangle2D;
    sx = paramDouble1;
    sy = paramDouble2;
    redrawList.add(localGraphicsState);
  }
  
  protected static PrintService lookupDefaultPrintService()
  {
    PrintService localPrintService = PrintServiceLookup.lookupDefaultPrintService();
    if ((localPrintService != null) && (localPrintService.isDocFlavorSupported(DocFlavor.SERVICE_FORMATTED.PAGEABLE)) && (localPrintService.isDocFlavorSupported(DocFlavor.SERVICE_FORMATTED.PRINTABLE))) {
      return localPrintService;
    }
    PrintService[] arrayOfPrintService = PrintServiceLookup.lookupPrintServices(DocFlavor.SERVICE_FORMATTED.PAGEABLE, null);
    if (arrayOfPrintService.length > 0) {
      return arrayOfPrintService[0];
    }
    return null;
  }
  
  public PrintService getPrintService()
  {
    if (myService == null)
    {
      PrintService localPrintService = PrintServiceLookup.lookupDefaultPrintService();
      if ((localPrintService != null) && (localPrintService.isDocFlavorSupported(DocFlavor.SERVICE_FORMATTED.PAGEABLE))) {
        try
        {
          setPrintService(localPrintService);
          myService = localPrintService;
        }
        catch (PrinterException localPrinterException1) {}
      }
      if (myService == null)
      {
        PrintService[] arrayOfPrintService = PrintServiceLookup.lookupPrintServices(DocFlavor.SERVICE_FORMATTED.PAGEABLE, null);
        if (arrayOfPrintService.length > 0) {
          try
          {
            setPrintService(arrayOfPrintService[0]);
            myService = arrayOfPrintService[0];
          }
          catch (PrinterException localPrinterException2) {}
        }
      }
    }
    return myService;
  }
  
  public void setPrintService(PrintService paramPrintService)
    throws PrinterException
  {
    if (paramPrintService == null) {
      throw new PrinterException("Service cannot be null");
    }
    if ((!(paramPrintService instanceof StreamPrintService)) && (paramPrintService.getName() == null)) {
      throw new PrinterException("Null PrintService name.");
    }
    PrinterState localPrinterState = (PrinterState)paramPrintService.getAttribute(PrinterState.class);
    if (localPrinterState == PrinterState.STOPPED)
    {
      PrinterStateReasons localPrinterStateReasons = (PrinterStateReasons)paramPrintService.getAttribute(PrinterStateReasons.class);
      if ((localPrinterStateReasons != null) && (localPrinterStateReasons.containsKey(PrinterStateReason.SHUTDOWN))) {
        throw new PrinterException("PrintService is no longer available.");
      }
    }
    if ((paramPrintService.isDocFlavorSupported(DocFlavor.SERVICE_FORMATTED.PAGEABLE)) && (paramPrintService.isDocFlavorSupported(DocFlavor.SERVICE_FORMATTED.PRINTABLE))) {
      myService = paramPrintService;
    } else {
      throw new PrinterException("Not a 2D print service: " + paramPrintService);
    }
  }
  
  private PageFormat attributeToPageFormat(PrintService paramPrintService, PrintRequestAttributeSet paramPrintRequestAttributeSet)
  {
    PageFormat localPageFormat = defaultPage();
    if (paramPrintService == null) {
      return localPageFormat;
    }
    OrientationRequested localOrientationRequested = (OrientationRequested)paramPrintRequestAttributeSet.get(OrientationRequested.class);
    if (localOrientationRequested == null) {
      localOrientationRequested = (OrientationRequested)paramPrintService.getDefaultAttributeValue(OrientationRequested.class);
    }
    if (localOrientationRequested == OrientationRequested.REVERSE_LANDSCAPE) {
      localPageFormat.setOrientation(2);
    } else if (localOrientationRequested == OrientationRequested.LANDSCAPE) {
      localPageFormat.setOrientation(0);
    } else {
      localPageFormat.setOrientation(1);
    }
    Media localMedia = (Media)paramPrintRequestAttributeSet.get(Media.class);
    MediaSize localMediaSize = getMediaSize(localMedia, paramPrintService, localPageFormat);
    Paper localPaper = new Paper();
    float[] arrayOfFloat = localMediaSize.getSize(1);
    double d1 = Math.rint(arrayOfFloat[0] * 72.0D / 25400.0D);
    double d2 = Math.rint(arrayOfFloat[1] * 72.0D / 25400.0D);
    localPaper.setSize(d1, d2);
    MediaPrintableArea localMediaPrintableArea = (MediaPrintableArea)paramPrintRequestAttributeSet.get(MediaPrintableArea.class);
    if (localMediaPrintableArea == null) {
      localMediaPrintableArea = getDefaultPrintableArea(localPageFormat, d1, d2);
    }
    double d3 = Math.rint(localMediaPrintableArea.getX(25400) * 72.0F);
    double d5 = Math.rint(localMediaPrintableArea.getY(25400) * 72.0F);
    double d4 = Math.rint(localMediaPrintableArea.getWidth(25400) * 72.0F);
    double d6 = Math.rint(localMediaPrintableArea.getHeight(25400) * 72.0F);
    localPaper.setImageableArea(d3, d5, d4, d6);
    localPageFormat.setPaper(localPaper);
    return localPageFormat;
  }
  
  protected MediaSize getMediaSize(Media paramMedia, PrintService paramPrintService, PageFormat paramPageFormat)
  {
    if (paramMedia == null) {
      paramMedia = (Media)paramPrintService.getDefaultAttributeValue(Media.class);
    }
    if (!(paramMedia instanceof MediaSizeName)) {
      paramMedia = MediaSizeName.NA_LETTER;
    }
    MediaSize localMediaSize = MediaSize.getMediaSizeForName((MediaSizeName)paramMedia);
    return localMediaSize != null ? localMediaSize : MediaSize.NA.LETTER;
  }
  
  protected MediaPrintableArea getDefaultPrintableArea(PageFormat paramPageFormat, double paramDouble1, double paramDouble2)
  {
    double d1;
    double d2;
    if (paramDouble1 >= 432.0D)
    {
      d1 = 72.0D;
      d2 = paramDouble1 - 144.0D;
    }
    else
    {
      d1 = paramDouble1 / 6.0D;
      d2 = paramDouble1 * 0.75D;
    }
    double d3;
    double d4;
    if (paramDouble2 >= 432.0D)
    {
      d3 = 72.0D;
      d4 = paramDouble2 - 144.0D;
    }
    else
    {
      d3 = paramDouble2 / 6.0D;
      d4 = paramDouble2 * 0.75D;
    }
    return new MediaPrintableArea((float)(d1 / 72.0D), (float)(d3 / 72.0D), (float)(d2 / 72.0D), (float)(d4 / 72.0D), 25400);
  }
  
  protected void updatePageAttributes(PrintService paramPrintService, PageFormat paramPageFormat)
  {
    if (attributes == null) {
      attributes = new HashPrintRequestAttributeSet();
    }
    updateAttributesWithPageFormat(paramPrintService, paramPageFormat, attributes);
  }
  
  protected void updateAttributesWithPageFormat(PrintService paramPrintService, PageFormat paramPageFormat, PrintRequestAttributeSet paramPrintRequestAttributeSet)
  {
    if ((paramPrintService == null) || (paramPageFormat == null) || (paramPrintRequestAttributeSet == null)) {
      return;
    }
    float f1 = (float)Math.rint(paramPageFormat.getPaper().getWidth() * 25400.0D / 72.0D) / 25400.0F;
    float f2 = (float)Math.rint(paramPageFormat.getPaper().getHeight() * 25400.0D / 72.0D) / 25400.0F;
    Media[] arrayOfMedia = (Media[])paramPrintService.getSupportedAttributeValues(Media.class, null, null);
    Object localObject = null;
    try
    {
      localObject = CustomMediaSizeName.findMedia(arrayOfMedia, f1, f2, 25400);
    }
    catch (IllegalArgumentException localIllegalArgumentException1) {}
    if ((localObject == null) || (!paramPrintService.isAttributeValueSupported((Attribute)localObject, null, null))) {
      localObject = (Media)paramPrintService.getDefaultAttributeValue(Media.class);
    }
    OrientationRequested localOrientationRequested;
    switch (paramPageFormat.getOrientation())
    {
    case 0: 
      localOrientationRequested = OrientationRequested.LANDSCAPE;
      break;
    case 2: 
      localOrientationRequested = OrientationRequested.REVERSE_LANDSCAPE;
      break;
    default: 
      localOrientationRequested = OrientationRequested.PORTRAIT;
    }
    if (localObject != null) {
      paramPrintRequestAttributeSet.add((Attribute)localObject);
    }
    paramPrintRequestAttributeSet.add(localOrientationRequested);
    float f3 = (float)(paramPageFormat.getPaper().getImageableX() / 72.0D);
    float f4 = (float)(paramPageFormat.getPaper().getImageableWidth() / 72.0D);
    float f5 = (float)(paramPageFormat.getPaper().getImageableY() / 72.0D);
    float f6 = (float)(paramPageFormat.getPaper().getImageableHeight() / 72.0D);
    if (f3 < 0.0F) {
      f3 = 0.0F;
    }
    if (f5 < 0.0F) {
      f5 = 0.0F;
    }
    try
    {
      paramPrintRequestAttributeSet.add(new MediaPrintableArea(f3, f5, f4, f6, 25400));
    }
    catch (IllegalArgumentException localIllegalArgumentException2) {}
  }
  
  public PageFormat pageDialog(PageFormat paramPageFormat)
    throws HeadlessException
  {
    if (GraphicsEnvironment.isHeadless()) {
      throw new HeadlessException();
    }
    final GraphicsConfiguration localGraphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    PrintService localPrintService = (PrintService)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        PrintService localPrintService = getPrintService();
        if (localPrintService == null)
        {
          ServiceDialog.showNoPrintService(localGraphicsConfiguration);
          return null;
        }
        return localPrintService;
      }
    });
    if (localPrintService == null) {
      return paramPageFormat;
    }
    updatePageAttributes(localPrintService, paramPageFormat);
    PageFormat localPageFormat = pageDialog(attributes);
    if (localPageFormat == null) {
      return paramPageFormat;
    }
    return localPageFormat;
  }
  
  public PageFormat pageDialog(PrintRequestAttributeSet paramPrintRequestAttributeSet)
    throws HeadlessException
  {
    if (GraphicsEnvironment.isHeadless()) {
      throw new HeadlessException();
    }
    DialogTypeSelection localDialogTypeSelection = (DialogTypeSelection)paramPrintRequestAttributeSet.get(DialogTypeSelection.class);
    if (localDialogTypeSelection == DialogTypeSelection.NATIVE)
    {
      localObject1 = getPrintService();
      localObject2 = pageDialog(attributeToPageFormat((PrintService)localObject1, paramPrintRequestAttributeSet));
      updateAttributesWithPageFormat((PrintService)localObject1, (PageFormat)localObject2, paramPrintRequestAttributeSet);
      return (PageFormat)localObject2;
    }
    final Object localObject1 = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    Object localObject2 = ((GraphicsConfiguration)localObject1).getBounds();
    int i = x + width / 3;
    int j = y + height / 3;
    PrintService localPrintService = (PrintService)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        PrintService localPrintService = getPrintService();
        if (localPrintService == null)
        {
          ServiceDialog.showNoPrintService(localObject1);
          return null;
        }
        return localPrintService;
      }
    });
    if (localPrintService == null) {
      return null;
    }
    ServiceDialog localServiceDialog = new ServiceDialog((GraphicsConfiguration)localObject1, i, j, localPrintService, DocFlavor.SERVICE_FORMATTED.PAGEABLE, paramPrintRequestAttributeSet, (Frame)null);
    localServiceDialog.show();
    if (localServiceDialog.getStatus() == 1)
    {
      PrintRequestAttributeSet localPrintRequestAttributeSet = localServiceDialog.getAttributes();
      Class localClass = SunAlternateMedia.class;
      if ((paramPrintRequestAttributeSet.containsKey(localClass)) && (!localPrintRequestAttributeSet.containsKey(localClass))) {
        paramPrintRequestAttributeSet.remove(localClass);
      }
      paramPrintRequestAttributeSet.addAll(localPrintRequestAttributeSet);
      return attributeToPageFormat(localPrintService, paramPrintRequestAttributeSet);
    }
    return null;
  }
  
  protected PageFormat getPageFormatFromAttributes()
  {
    if ((attributes == null) || (attributes.isEmpty())) {
      return null;
    }
    return attributeToPageFormat(getPrintService(), attributes);
  }
  
  public boolean printDialog(PrintRequestAttributeSet paramPrintRequestAttributeSet)
    throws HeadlessException
  {
    if (GraphicsEnvironment.isHeadless()) {
      throw new HeadlessException();
    }
    DialogTypeSelection localDialogTypeSelection = (DialogTypeSelection)paramPrintRequestAttributeSet.get(DialogTypeSelection.class);
    if (localDialogTypeSelection == DialogTypeSelection.NATIVE)
    {
      attributes = paramPrintRequestAttributeSet;
      try
      {
        debug_println("calling setAttributes in printDialog");
        setAttributes(paramPrintRequestAttributeSet);
      }
      catch (PrinterException localPrinterException1) {}
      boolean bool = printDialog();
      attributes = paramPrintRequestAttributeSet;
      return bool;
    }
    final GraphicsConfiguration localGraphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    PrintService localPrintService1 = (PrintService)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        PrintService localPrintService = getPrintService();
        if (localPrintService == null)
        {
          ServiceDialog.showNoPrintService(localGraphicsConfiguration);
          return null;
        }
        return localPrintService;
      }
    });
    if (localPrintService1 == null) {
      return false;
    }
    StreamPrintServiceFactory[] arrayOfStreamPrintServiceFactory = null;
    Object localObject;
    if ((localPrintService1 instanceof StreamPrintService))
    {
      arrayOfStreamPrintServiceFactory = lookupStreamPrintServices(null);
      localObject = new StreamPrintService[arrayOfStreamPrintServiceFactory.length];
      for (int i = 0; i < arrayOfStreamPrintServiceFactory.length; i++) {
        localObject[i] = arrayOfStreamPrintServiceFactory[i].getPrintService(null);
      }
    }
    else
    {
      localObject = (PrintService[])AccessController.doPrivileged(new PrivilegedAction()
      {
        public Object run()
        {
          PrintService[] arrayOfPrintService = PrinterJob.lookupPrintServices();
          return arrayOfPrintService;
        }
      });
      if ((localObject == null) || (localObject.length == 0))
      {
        localObject = new PrintService[1];
        localObject[0] = localPrintService1;
      }
    }
    Rectangle localRectangle = localGraphicsConfiguration.getBounds();
    int j = x + width / 3;
    int k = y + height / 3;
    PrinterJobWrapper localPrinterJobWrapper = new PrinterJobWrapper(this);
    paramPrintRequestAttributeSet.add(localPrinterJobWrapper);
    PrintService localPrintService2;
    try
    {
      localPrintService2 = ServiceUI.printDialog(localGraphicsConfiguration, j, k, (PrintService[])localObject, localPrintService1, DocFlavor.SERVICE_FORMATTED.PAGEABLE, paramPrintRequestAttributeSet);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      localPrintService2 = ServiceUI.printDialog(localGraphicsConfiguration, j, k, (PrintService[])localObject, localObject[0], DocFlavor.SERVICE_FORMATTED.PAGEABLE, paramPrintRequestAttributeSet);
    }
    paramPrintRequestAttributeSet.remove(PrinterJobWrapper.class);
    if (localPrintService2 == null) {
      return false;
    }
    if (!localPrintService1.equals(localPrintService2)) {
      try
      {
        setPrintService(localPrintService2);
      }
      catch (PrinterException localPrinterException2)
      {
        myService = localPrintService2;
      }
    }
    return true;
  }
  
  public boolean printDialog()
    throws HeadlessException
  {
    if (GraphicsEnvironment.isHeadless()) {
      throw new HeadlessException();
    }
    HashPrintRequestAttributeSet localHashPrintRequestAttributeSet = new HashPrintRequestAttributeSet();
    localHashPrintRequestAttributeSet.add(new Copies(getCopies()));
    localHashPrintRequestAttributeSet.add(new JobName(getJobName(), null));
    boolean bool = printDialog(localHashPrintRequestAttributeSet);
    if (bool)
    {
      JobName localJobName = (JobName)localHashPrintRequestAttributeSet.get(JobName.class);
      if (localJobName != null) {
        setJobName(localJobName.getValue());
      }
      Copies localCopies = (Copies)localHashPrintRequestAttributeSet.get(Copies.class);
      if (localCopies != null) {
        setCopies(localCopies.getValue());
      }
      Destination localDestination1 = (Destination)localHashPrintRequestAttributeSet.get(Destination.class);
      if (localDestination1 != null)
      {
        try
        {
          mDestType = 1;
          mDestination = new File(localDestination1.getURI()).getPath();
        }
        catch (Exception localException)
        {
          mDestination = "out.prn";
          PrintService localPrintService2 = getPrintService();
          if (localPrintService2 != null)
          {
            Destination localDestination2 = (Destination)localPrintService2.getDefaultAttributeValue(Destination.class);
            if (localDestination2 != null) {
              mDestination = new File(localDestination2.getURI()).getPath();
            }
          }
        }
      }
      else
      {
        mDestType = 0;
        PrintService localPrintService1 = getPrintService();
        if (localPrintService1 != null) {
          mDestination = localPrintService1.getName();
        }
      }
    }
    return bool;
  }
  
  public void setPrintable(Printable paramPrintable)
  {
    setPageable(new OpenBook(defaultPage(new PageFormat()), paramPrintable));
  }
  
  public void setPrintable(Printable paramPrintable, PageFormat paramPageFormat)
  {
    setPageable(new OpenBook(paramPageFormat, paramPrintable));
    updatePageAttributes(getPrintService(), paramPageFormat);
  }
  
  public void setPageable(Pageable paramPageable)
    throws NullPointerException
  {
    if (paramPageable != null) {
      mDocument = paramPageable;
    } else {
      throw new NullPointerException();
    }
  }
  
  protected void initPrinter() {}
  
  protected boolean isSupportedValue(Attribute paramAttribute, PrintRequestAttributeSet paramPrintRequestAttributeSet)
  {
    PrintService localPrintService = getPrintService();
    return (paramAttribute != null) && (localPrintService != null) && (localPrintService.isAttributeValueSupported(paramAttribute, DocFlavor.SERVICE_FORMATTED.PAGEABLE, paramPrintRequestAttributeSet));
  }
  
  protected void setAttributes(PrintRequestAttributeSet paramPrintRequestAttributeSet)
    throws PrinterException
  {
    setCollated(false);
    sidesAttr = null;
    pageRangesAttr = null;
    copiesAttr = 0;
    jobNameAttr = null;
    userNameAttr = null;
    destinationAttr = null;
    collateAttReq = false;
    PrintService localPrintService = getPrintService();
    if ((paramPrintRequestAttributeSet == null) || (localPrintService == null)) {
      return;
    }
    int i = 0;
    Fidelity localFidelity = (Fidelity)paramPrintRequestAttributeSet.get(Fidelity.class);
    if ((localFidelity != null) && (localFidelity == Fidelity.FIDELITY_TRUE)) {
      i = 1;
    }
    if (i == 1)
    {
      localObject1 = localPrintService.getUnsupportedAttributes(DocFlavor.SERVICE_FORMATTED.PAGEABLE, paramPrintRequestAttributeSet);
      if (localObject1 != null) {
        throw new PrinterException("Fidelity cannot be satisfied");
      }
    }
    Object localObject1 = (SheetCollate)paramPrintRequestAttributeSet.get(SheetCollate.class);
    if (isSupportedValue((Attribute)localObject1, paramPrintRequestAttributeSet)) {
      setCollated(localObject1 == SheetCollate.COLLATED);
    }
    sidesAttr = ((Sides)paramPrintRequestAttributeSet.get(Sides.class));
    if (!isSupportedValue(sidesAttr, paramPrintRequestAttributeSet)) {
      sidesAttr = Sides.ONE_SIDED;
    }
    pageRangesAttr = ((PageRanges)paramPrintRequestAttributeSet.get(PageRanges.class));
    if (!isSupportedValue(pageRangesAttr, paramPrintRequestAttributeSet))
    {
      pageRangesAttr = null;
    }
    else if ((SunPageSelection)paramPrintRequestAttributeSet.get(SunPageSelection.class) == SunPageSelection.RANGE)
    {
      localObject2 = pageRangesAttr.getMembers();
      setPageRange(localObject2[0][0] - 1, localObject2[0][1] - 1);
    }
    else
    {
      setPageRange(-1, -1);
    }
    Object localObject2 = (Copies)paramPrintRequestAttributeSet.get(Copies.class);
    if ((isSupportedValue((Attribute)localObject2, paramPrintRequestAttributeSet)) || ((i == 0) && (localObject2 != null)))
    {
      copiesAttr = ((Copies)localObject2).getValue();
      setCopies(copiesAttr);
    }
    else
    {
      copiesAttr = getCopies();
    }
    Destination localDestination = (Destination)paramPrintRequestAttributeSet.get(Destination.class);
    if (isSupportedValue(localDestination, paramPrintRequestAttributeSet)) {
      try
      {
        destinationAttr = ("" + new File(localDestination.getURI().getSchemeSpecificPart()));
      }
      catch (Exception localException)
      {
        localObject3 = (Destination)localPrintService.getDefaultAttributeValue(Destination.class);
        if (localObject3 != null) {
          destinationAttr = ("" + new File(((Destination)localObject3).getURI().getSchemeSpecificPart()));
        }
      }
    }
    JobSheets localJobSheets = (JobSheets)paramPrintRequestAttributeSet.get(JobSheets.class);
    if (localJobSheets != null) {
      noJobSheet = (localJobSheets == JobSheets.NONE);
    }
    Object localObject3 = (JobName)paramPrintRequestAttributeSet.get(JobName.class);
    if ((isSupportedValue((Attribute)localObject3, paramPrintRequestAttributeSet)) || ((i == 0) && (localObject3 != null)))
    {
      jobNameAttr = ((JobName)localObject3).getValue();
      setJobName(jobNameAttr);
    }
    else
    {
      jobNameAttr = getJobName();
    }
    RequestingUserName localRequestingUserName = (RequestingUserName)paramPrintRequestAttributeSet.get(RequestingUserName.class);
    if ((isSupportedValue(localRequestingUserName, paramPrintRequestAttributeSet)) || ((i == 0) && (localRequestingUserName != null))) {
      userNameAttr = localRequestingUserName.getValue();
    } else {
      try
      {
        userNameAttr = getUserName();
      }
      catch (SecurityException localSecurityException)
      {
        userNameAttr = "";
      }
    }
    Media localMedia = (Media)paramPrintRequestAttributeSet.get(Media.class);
    OrientationRequested localOrientationRequested = (OrientationRequested)paramPrintRequestAttributeSet.get(OrientationRequested.class);
    MediaPrintableArea localMediaPrintableArea = (MediaPrintableArea)paramPrintRequestAttributeSet.get(MediaPrintableArea.class);
    if (((localOrientationRequested != null) || (localMedia != null) || (localMediaPrintableArea != null)) && ((getPageable() instanceof OpenBook)))
    {
      Pageable localPageable = getPageable();
      Printable localPrintable = localPageable.getPrintable(0);
      PageFormat localPageFormat = (PageFormat)localPageable.getPageFormat(0).clone();
      Paper localPaper = localPageFormat.getPaper();
      if ((localMediaPrintableArea == null) && (localMedia != null) && (localPrintService.isAttributeCategorySupported(MediaPrintableArea.class)))
      {
        Object localObject4 = localPrintService.getSupportedAttributeValues(MediaPrintableArea.class, null, paramPrintRequestAttributeSet);
        if (((localObject4 instanceof MediaPrintableArea[])) && (((MediaPrintableArea[])localObject4).length > 0)) {
          localMediaPrintableArea = ((MediaPrintableArea[])(MediaPrintableArea[])localObject4)[0];
        }
      }
      if ((isSupportedValue(localOrientationRequested, paramPrintRequestAttributeSet)) || ((i == 0) && (localOrientationRequested != null)))
      {
        int j;
        if (localOrientationRequested.equals(OrientationRequested.REVERSE_LANDSCAPE)) {
          j = 2;
        } else if (localOrientationRequested.equals(OrientationRequested.LANDSCAPE)) {
          j = 0;
        } else {
          j = 1;
        }
        localPageFormat.setOrientation(j);
      }
      Object localObject5;
      if (((isSupportedValue(localMedia, paramPrintRequestAttributeSet)) || ((i == 0) && (localMedia != null))) && ((localMedia instanceof MediaSizeName)))
      {
        localObject5 = (MediaSizeName)localMedia;
        MediaSize localMediaSize = MediaSize.getMediaSizeForName((MediaSizeName)localObject5);
        if (localMediaSize != null)
        {
          float f1 = localMediaSize.getX(25400) * 72.0F;
          float f2 = localMediaSize.getY(25400) * 72.0F;
          localPaper.setSize(f1, f2);
          if (localMediaPrintableArea == null) {
            localPaper.setImageableArea(72.0D, 72.0D, f1 - 144.0D, f2 - 144.0D);
          }
        }
      }
      if ((isSupportedValue(localMediaPrintableArea, paramPrintRequestAttributeSet)) || ((i == 0) && (localMediaPrintableArea != null)))
      {
        localObject5 = localMediaPrintableArea.getPrintableArea(25400);
        for (int k = 0; k < localObject5.length; k++) {
          localObject5[k] *= 72.0F;
        }
        localPaper.setImageableArea(localObject5[0], localObject5[1], localObject5[2], localObject5[3]);
      }
      localPageFormat.setPaper(localPaper);
      localPageFormat = validatePage(localPageFormat);
      setPrintable(localPrintable, localPageFormat);
    }
    else
    {
      attributes = paramPrintRequestAttributeSet;
    }
  }
  
  protected void spoolToService(PrintService paramPrintService, PrintRequestAttributeSet paramPrintRequestAttributeSet)
    throws PrinterException
  {
    if (paramPrintService == null) {
      throw new PrinterException("No print service found.");
    }
    DocPrintJob localDocPrintJob = paramPrintService.createPrintJob();
    PageableDoc localPageableDoc = new PageableDoc(getPageable());
    if (paramPrintRequestAttributeSet == null) {
      paramPrintRequestAttributeSet = new HashPrintRequestAttributeSet();
    }
    try
    {
      localDocPrintJob.print(localPageableDoc, paramPrintRequestAttributeSet);
    }
    catch (PrintException localPrintException)
    {
      throw new PrinterException(localPrintException.toString());
    }
  }
  
  public void print()
    throws PrinterException
  {
    print(attributes);
  }
  
  protected void debug_println(String paramString)
  {
    if (debugPrint) {
      System.out.println("RasterPrinterJob " + paramString + " " + this);
    }
  }
  
  public void print(PrintRequestAttributeSet paramPrintRequestAttributeSet)
    throws PrinterException
  {
    PrintService localPrintService = getPrintService();
    debug_println("psvc = " + localPrintService);
    if (localPrintService == null) {
      throw new PrinterException("No print service found.");
    }
    PrinterState localPrinterState = (PrinterState)localPrintService.getAttribute(PrinterState.class);
    if (localPrinterState == PrinterState.STOPPED)
    {
      PrinterStateReasons localPrinterStateReasons = (PrinterStateReasons)localPrintService.getAttribute(PrinterStateReasons.class);
      if ((localPrinterStateReasons != null) && (localPrinterStateReasons.containsKey(PrinterStateReason.SHUTDOWN))) {
        throw new PrinterException("PrintService is no longer available.");
      }
    }
    if ((PrinterIsAcceptingJobs)localPrintService.getAttribute(PrinterIsAcceptingJobs.class) == PrinterIsAcceptingJobs.NOT_ACCEPTING_JOBS) {
      throw new PrinterException("Printer is not accepting job.");
    }
    if (((localPrintService instanceof SunPrinterJobService)) && (((SunPrinterJobService)localPrintService).usesClass(getClass())))
    {
      setAttributes(paramPrintRequestAttributeSet);
      if (destinationAttr != null) {
        validateDestination(destinationAttr);
      }
    }
    else
    {
      spoolToService(localPrintService, paramPrintRequestAttributeSet);
      return;
    }
    initPrinter();
    int i = getCollatedCopies();
    int j = getNoncollatedCopies();
    debug_println("getCollatedCopies()  " + i + " getNoncollatedCopies() " + j);
    int k = mDocument.getNumberOfPages();
    if (k == 0) {
      return;
    }
    int m = getFirstPage();
    int n = getLastPage();
    if (n == -1)
    {
      int i1 = mDocument.getNumberOfPages();
      if (i1 != -1) {
        n = mDocument.getNumberOfPages() - 1;
      }
    }
    try
    {
      synchronized (this)
      {
        performingPrinting = true;
        userCancelled = false;
      }
      startDoc();
      if (isCancelled()) {
        cancelDoc();
      }
      boolean bool = true;
      if (paramPrintRequestAttributeSet != null)
      {
        SunPageSelection localSunPageSelection = (SunPageSelection)paramPrintRequestAttributeSet.get(SunPageSelection.class);
        if ((localSunPageSelection != null) && (localSunPageSelection != SunPageSelection.RANGE)) {
          bool = false;
        }
      }
      debug_println("after startDoc rangeSelected? " + bool + " numNonCollatedCopies " + j);
      for (int i2 = 0; i2 < i; i2++)
      {
        int i3 = m;
        int i4 = 0;
        while (((i3 <= n) || (n == -1)) && (i4 == 0))
        {
          int i5;
          if ((pageRangesAttr != null) && (bool))
          {
            i5 = pageRangesAttr.next(i3);
            if (i5 == -1) {
              break;
            }
            if (i5 != i3 + 1) {}
          }
          else
          {
            for (i5 = 0; (i5 < j) && (i4 == 0); i5++)
            {
              if (isCancelled()) {
                cancelDoc();
              }
              debug_println("printPage " + i3);
              i4 = printPage(mDocument, i3);
            }
          }
          i3++;
        }
      }
      if (isCancelled()) {
        cancelDoc();
      }
    }
    finally
    {
      previousPaper = null;
      synchronized (this)
      {
        if (performingPrinting) {
          endDoc();
        }
        performingPrinting = false;
        notify();
      }
    }
  }
  
  protected void validateDestination(String paramString)
    throws PrinterException
  {
    if (paramString == null) {
      return;
    }
    File localFile1 = new File(paramString);
    try
    {
      if (localFile1.createNewFile()) {
        localFile1.delete();
      }
    }
    catch (IOException localIOException)
    {
      throw new PrinterException("Cannot write to file:" + paramString);
    }
    catch (SecurityException localSecurityException) {}
    File localFile2 = localFile1.getParentFile();
    if (((localFile1.exists()) && ((!localFile1.isFile()) || (!localFile1.canWrite()))) || ((localFile2 != null) && ((!localFile2.exists()) || ((localFile2.exists()) && (!localFile2.canWrite()))))) {
      throw new PrinterException("Cannot write to file:" + paramString);
    }
  }
  
  protected void validatePaper(Paper paramPaper1, Paper paramPaper2)
  {
    if ((paramPaper1 == null) || (paramPaper2 == null)) {
      return;
    }
    double d1 = paramPaper1.getWidth();
    double d2 = paramPaper1.getHeight();
    double d3 = paramPaper1.getImageableX();
    double d4 = paramPaper1.getImageableY();
    double d5 = paramPaper1.getImageableWidth();
    double d6 = paramPaper1.getImageableHeight();
    Paper localPaper = new Paper();
    d1 = d1 > 0.0D ? d1 : localPaper.getWidth();
    d2 = d2 > 0.0D ? d2 : localPaper.getHeight();
    d3 = d3 > 0.0D ? d3 : localPaper.getImageableX();
    d4 = d4 > 0.0D ? d4 : localPaper.getImageableY();
    d5 = d5 > 0.0D ? d5 : localPaper.getImageableWidth();
    d6 = d6 > 0.0D ? d6 : localPaper.getImageableHeight();
    if (d5 > d1) {
      d5 = d1;
    }
    if (d6 > d2) {
      d6 = d2;
    }
    if (d3 + d5 > d1) {
      d3 = d1 - d5;
    }
    if (d4 + d6 > d2) {
      d4 = d2 - d6;
    }
    paramPaper2.setSize(d1, d2);
    paramPaper2.setImageableArea(d3, d4, d5, d6);
  }
  
  public PageFormat defaultPage(PageFormat paramPageFormat)
  {
    PageFormat localPageFormat = (PageFormat)paramPageFormat.clone();
    localPageFormat.setOrientation(1);
    Paper localPaper = new Paper();
    double d1 = 72.0D;
    Media localMedia = null;
    PrintService localPrintService = getPrintService();
    double d2;
    double d3;
    if (localPrintService != null)
    {
      localMedia = (Media)localPrintService.getDefaultAttributeValue(Media.class);
      if (((localMedia instanceof MediaSizeName)) && ((localObject = MediaSize.getMediaSizeForName((MediaSizeName)localMedia)) != null))
      {
        d2 = ((MediaSize)localObject).getX(25400) * d1;
        d3 = ((MediaSize)localObject).getY(25400) * d1;
        localPaper.setSize(d2, d3);
        localPaper.setImageableArea(d1, d1, d2 - 2.0D * d1, d3 - 2.0D * d1);
        localPageFormat.setPaper(localPaper);
        return localPageFormat;
      }
    }
    Object localObject = Locale.getDefault().getCountry();
    if ((!Locale.getDefault().equals(Locale.ENGLISH)) && (localObject != null) && (!((String)localObject).equals(Locale.US.getCountry())) && (!((String)localObject).equals(Locale.CANADA.getCountry())))
    {
      double d4 = 25.4D;
      d2 = Math.rint(210.0D * d1 / d4);
      d3 = Math.rint(297.0D * d1 / d4);
      localPaper.setSize(d2, d3);
      localPaper.setImageableArea(d1, d1, d2 - 2.0D * d1, d3 - 2.0D * d1);
    }
    localPageFormat.setPaper(localPaper);
    return localPageFormat;
  }
  
  public PageFormat validatePage(PageFormat paramPageFormat)
  {
    PageFormat localPageFormat = (PageFormat)paramPageFormat.clone();
    Paper localPaper = new Paper();
    validatePaper(localPageFormat.getPaper(), localPaper);
    localPageFormat.setPaper(localPaper);
    return localPageFormat;
  }
  
  public void setCopies(int paramInt)
  {
    mNumCopies = paramInt;
  }
  
  public int getCopies()
  {
    return mNumCopies;
  }
  
  protected int getCopiesInt()
  {
    return copiesAttr > 0 ? copiesAttr : getCopies();
  }
  
  public String getUserName()
  {
    return System.getProperty("user.name");
  }
  
  protected String getUserNameInt()
  {
    if (userNameAttr != null) {
      return userNameAttr;
    }
    try
    {
      return getUserName();
    }
    catch (SecurityException localSecurityException) {}
    return "";
  }
  
  public void setJobName(String paramString)
  {
    if (paramString != null) {
      mDocName = paramString;
    } else {
      throw new NullPointerException();
    }
  }
  
  public String getJobName()
  {
    return mDocName;
  }
  
  protected String getJobNameInt()
  {
    return jobNameAttr != null ? jobNameAttr : getJobName();
  }
  
  protected void setPageRange(int paramInt1, int paramInt2)
  {
    if ((paramInt1 >= 0) && (paramInt2 >= 0))
    {
      mFirstPage = paramInt1;
      mLastPage = paramInt2;
      if (mLastPage < mFirstPage) {
        mLastPage = mFirstPage;
      }
    }
    else
    {
      mFirstPage = -1;
      mLastPage = -1;
    }
  }
  
  protected int getFirstPage()
  {
    return mFirstPage == -1 ? 0 : mFirstPage;
  }
  
  protected int getLastPage()
  {
    return mLastPage;
  }
  
  protected void setCollated(boolean paramBoolean)
  {
    mCollate = paramBoolean;
    collateAttReq = true;
  }
  
  protected boolean isCollated()
  {
    return mCollate;
  }
  
  protected final int getSelectAttrib()
  {
    if (attributes != null)
    {
      SunPageSelection localSunPageSelection = (SunPageSelection)attributes.get(SunPageSelection.class);
      if (localSunPageSelection == SunPageSelection.RANGE) {
        return 2;
      }
      if (localSunPageSelection == SunPageSelection.SELECTION) {
        return 1;
      }
      if (localSunPageSelection == SunPageSelection.ALL) {
        return 0;
      }
    }
    return 4;
  }
  
  protected final int getFromPageAttrib()
  {
    if (attributes != null)
    {
      PageRanges localPageRanges = (PageRanges)attributes.get(PageRanges.class);
      if (localPageRanges != null)
      {
        int[][] arrayOfInt = localPageRanges.getMembers();
        return arrayOfInt[0][0];
      }
    }
    return getMinPageAttrib();
  }
  
  protected final int getToPageAttrib()
  {
    if (attributes != null)
    {
      PageRanges localPageRanges = (PageRanges)attributes.get(PageRanges.class);
      if (localPageRanges != null)
      {
        int[][] arrayOfInt = localPageRanges.getMembers();
        return arrayOfInt[(arrayOfInt.length - 1)][1];
      }
    }
    return getMaxPageAttrib();
  }
  
  protected final int getMinPageAttrib()
  {
    if (attributes != null)
    {
      SunMinMaxPage localSunMinMaxPage = (SunMinMaxPage)attributes.get(SunMinMaxPage.class);
      if (localSunMinMaxPage != null) {
        return localSunMinMaxPage.getMin();
      }
    }
    return 1;
  }
  
  protected final int getMaxPageAttrib()
  {
    if (attributes != null)
    {
      localObject = (SunMinMaxPage)attributes.get(SunMinMaxPage.class);
      if (localObject != null) {
        return ((SunMinMaxPage)localObject).getMax();
      }
    }
    Object localObject = getPageable();
    if (localObject != null)
    {
      int i = ((Pageable)localObject).getNumberOfPages();
      if (i <= -1) {
        i = 9999;
      }
      return i == 0 ? 1 : i;
    }
    return Integer.MAX_VALUE;
  }
  
  protected abstract void startDoc()
    throws PrinterException;
  
  protected abstract void endDoc()
    throws PrinterException;
  
  protected abstract void abortDoc();
  
  protected void cancelDoc()
    throws PrinterAbortException
  {
    abortDoc();
    synchronized (this)
    {
      userCancelled = false;
      performingPrinting = false;
      notify();
    }
    throw new PrinterAbortException();
  }
  
  protected int getCollatedCopies()
  {
    return isCollated() ? getCopiesInt() : 1;
  }
  
  protected int getNoncollatedCopies()
  {
    return isCollated() ? 1 : getCopiesInt();
  }
  
  synchronized void setGraphicsConfigInfo(AffineTransform paramAffineTransform, double paramDouble1, double paramDouble2)
  {
    Point2D.Double localDouble = new Point2D.Double(paramDouble1, paramDouble2);
    paramAffineTransform.transform(localDouble, localDouble);
    if ((pgConfig == null) || (defaultDeviceTransform == null) || (!paramAffineTransform.equals(defaultDeviceTransform)) || (deviceWidth != (int)localDouble.getX()) || (deviceHeight != (int)localDouble.getY()))
    {
      deviceWidth = ((int)localDouble.getX());
      deviceHeight = ((int)localDouble.getY());
      defaultDeviceTransform = paramAffineTransform;
      pgConfig = null;
    }
  }
  
  synchronized PrinterGraphicsConfig getPrinterGraphicsConfig()
  {
    if (pgConfig != null) {
      return pgConfig;
    }
    String str = "Printer Device";
    PrintService localPrintService = getPrintService();
    if (localPrintService != null) {
      str = localPrintService.toString();
    }
    pgConfig = new PrinterGraphicsConfig(str, defaultDeviceTransform, deviceWidth, deviceHeight);
    return pgConfig;
  }
  
  protected int printPage(Pageable paramPageable, int paramInt)
    throws PrinterException
  {
    PageFormat localPageFormat2;
    PageFormat localPageFormat1;
    Printable localPrintable;
    try
    {
      localPageFormat2 = paramPageable.getPageFormat(paramInt);
      localPageFormat1 = (PageFormat)localPageFormat2.clone();
      localPrintable = paramPageable.getPrintable(paramInt);
    }
    catch (Exception localException)
    {
      PrinterException localPrinterException = new PrinterException("Error getting page or printable.[ " + localException + " ]");
      localPrinterException.initCause(localException);
      throw localPrinterException;
    }
    Paper localPaper1 = localPageFormat1.getPaper();
    if ((localPageFormat1.getOrientation() != 1) && (landscapeRotates270))
    {
      d1 = localPaper1.getImageableX();
      d2 = localPaper1.getImageableY();
      double d3 = localPaper1.getImageableWidth();
      double d4 = localPaper1.getImageableHeight();
      localPaper1.setImageableArea(localPaper1.getWidth() - d1 - d3, localPaper1.getHeight() - d2 - d4, d3, d4);
      localPageFormat1.setPaper(localPaper1);
      if (localPageFormat1.getOrientation() == 0) {
        localPageFormat1.setOrientation(2);
      } else {
        localPageFormat1.setOrientation(0);
      }
    }
    double d1 = getXRes() / 72.0D;
    double d2 = getYRes() / 72.0D;
    Rectangle2D.Double localDouble1 = new Rectangle2D.Double(localPaper1.getImageableX() * d1, localPaper1.getImageableY() * d2, localPaper1.getImageableWidth() * d1, localPaper1.getImageableHeight() * d2);
    AffineTransform localAffineTransform1 = new AffineTransform();
    AffineTransform localAffineTransform2 = new AffineTransform();
    localAffineTransform2.scale(d1, d2);
    int i = (int)localDouble1.getWidth();
    if (i % 4 != 0) {
      i += 4 - i % 4;
    }
    if (i <= 0) {
      throw new PrinterException("Paper's imageable width is too small.");
    }
    int j = (int)localDouble1.getHeight();
    if (j <= 0) {
      throw new PrinterException("Paper's imageable height is too small.");
    }
    int k = 4194304 / i / 3;
    int m = (int)Math.rint(localPaper1.getImageableX() * d1);
    int n = (int)Math.rint(localPaper1.getImageableY() * d2);
    AffineTransform localAffineTransform3 = new AffineTransform();
    localAffineTransform3.translate(-m, n);
    localAffineTransform3.translate(0.0D, k);
    localAffineTransform3.scale(1.0D, -1.0D);
    BufferedImage localBufferedImage = new BufferedImage(1, 1, 5);
    PeekGraphics localPeekGraphics = createPeekGraphics(localBufferedImage.createGraphics(), this);
    Rectangle2D.Double localDouble2 = new Rectangle2D.Double(localPageFormat1.getImageableX(), localPageFormat1.getImageableY(), localPageFormat1.getImageableWidth(), localPageFormat1.getImageableHeight());
    localPeekGraphics.transform(localAffineTransform2);
    localPeekGraphics.translate(-getPhysicalPrintableX(localPaper1) / d1, -getPhysicalPrintableY(localPaper1) / d2);
    localPeekGraphics.transform(new AffineTransform(localPageFormat1.getMatrix()));
    initPrinterGraphics(localPeekGraphics, localDouble2);
    AffineTransform localAffineTransform4 = localPeekGraphics.getTransform();
    setGraphicsConfigInfo(localAffineTransform2, localPaper1.getWidth(), localPaper1.getHeight());
    int i1 = localPrintable.print(localPeekGraphics, localPageFormat2, paramInt);
    debug_println("pageResult " + i1);
    if (i1 == 0)
    {
      debug_println("startPage " + paramInt);
      Paper localPaper2 = localPageFormat1.getPaper();
      boolean bool = (previousPaper == null) || (localPaper2.getWidth() != previousPaper.getWidth()) || (localPaper2.getHeight() != previousPaper.getHeight());
      previousPaper = localPaper2;
      startPage(localPageFormat1, localPrintable, paramInt, bool);
      Graphics2D localGraphics2D1 = createPathGraphics(localPeekGraphics, this, localPrintable, localPageFormat1, paramInt);
      Object localObject1;
      Object localObject2;
      if (localGraphics2D1 != null)
      {
        localGraphics2D1.transform(localAffineTransform2);
        localGraphics2D1.translate(-getPhysicalPrintableX(localPaper1) / d1, -getPhysicalPrintableY(localPaper1) / d2);
        localGraphics2D1.transform(new AffineTransform(localPageFormat1.getMatrix()));
        initPrinterGraphics(localGraphics2D1, localDouble2);
        redrawList.clear();
        localObject1 = localGraphics2D1.getTransform();
        localPrintable.print(localGraphics2D1, localPageFormat2, paramInt);
        for (int i2 = 0; i2 < redrawList.size(); i2++)
        {
          localObject2 = (GraphicsState)redrawList.get(i2);
          localGraphics2D1.setTransform((AffineTransform)localObject1);
          ((PathGraphics)localGraphics2D1).redrawRegion(region, sx, sy, theClip, theTransform);
        }
      }
      else
      {
        localObject1 = cachedBand;
        if ((cachedBand == null) || (i != cachedBandWidth) || (k != cachedBandHeight))
        {
          localObject1 = new BufferedImage(i, k, 5);
          cachedBand = ((BufferedImage)localObject1);
          cachedBandWidth = i;
          cachedBandHeight = k;
        }
        Graphics2D localGraphics2D2 = ((BufferedImage)localObject1).createGraphics();
        localObject2 = new Rectangle2D.Double(0.0D, 0.0D, i, k);
        initPrinterGraphics(localGraphics2D2, (Rectangle2D)localObject2);
        ProxyGraphics2D localProxyGraphics2D = new ProxyGraphics2D(localGraphics2D2, this);
        Graphics2D localGraphics2D3 = ((BufferedImage)localObject1).createGraphics();
        localGraphics2D3.setColor(Color.white);
        ByteInterleavedRaster localByteInterleavedRaster = (ByteInterleavedRaster)((BufferedImage)localObject1).getRaster();
        byte[] arrayOfByte = localByteInterleavedRaster.getDataStorage();
        int i3 = n + j;
        int i4 = (int)getPhysicalPrintableX(localPaper1);
        int i5 = (int)getPhysicalPrintableY(localPaper1);
        int i6 = 0;
        while (i6 <= j)
        {
          localGraphics2D3.fillRect(0, 0, i, k);
          localGraphics2D2.setTransform(localAffineTransform1);
          localGraphics2D2.transform(localAffineTransform3);
          localAffineTransform3.translate(0.0D, -k);
          localGraphics2D2.transform(localAffineTransform2);
          localGraphics2D2.transform(new AffineTransform(localPageFormat1.getMatrix()));
          Rectangle localRectangle = localGraphics2D2.getClipBounds();
          localRectangle = localAffineTransform4.createTransformedShape(localRectangle).getBounds();
          if ((localRectangle == null) || ((localPeekGraphics.hitsDrawingArea(localRectangle)) && (i > 0) && (k > 0)))
          {
            int i7 = m - i4;
            if (i7 < 0)
            {
              localGraphics2D2.translate(i7 / d1, 0.0D);
              i7 = 0;
            }
            int i8 = n + i6 - i5;
            if (i8 < 0)
            {
              localGraphics2D2.translate(0.0D, i8 / d2);
              i8 = 0;
            }
            localProxyGraphics2D.setDelegate((Graphics2D)localGraphics2D2.create());
            localPrintable.print(localProxyGraphics2D, localPageFormat2, paramInt);
            localProxyGraphics2D.dispose();
            printBand(arrayOfByte, i7, i8, i, k);
          }
          i6 += k;
        }
        localGraphics2D3.dispose();
        localGraphics2D2.dispose();
      }
      debug_println("calling endPage " + paramInt);
      endPage(localPageFormat1, localPrintable, paramInt);
    }
    return i1;
  }
  
  public void cancel()
  {
    synchronized (this)
    {
      if (performingPrinting) {
        userCancelled = true;
      }
      notify();
    }
  }
  
  public boolean isCancelled()
  {
    boolean bool = false;
    synchronized (this)
    {
      bool = (performingPrinting) && (userCancelled);
      notify();
    }
    return bool;
  }
  
  protected Pageable getPageable()
  {
    return mDocument;
  }
  
  protected Graphics2D createPathGraphics(PeekGraphics paramPeekGraphics, PrinterJob paramPrinterJob, Printable paramPrintable, PageFormat paramPageFormat, int paramInt)
  {
    return null;
  }
  
  protected PeekGraphics createPeekGraphics(Graphics2D paramGraphics2D, PrinterJob paramPrinterJob)
  {
    return new PeekGraphics(paramGraphics2D, paramPrinterJob);
  }
  
  protected void initPrinterGraphics(Graphics2D paramGraphics2D, Rectangle2D paramRectangle2D)
  {
    paramGraphics2D.setClip(paramRectangle2D);
    paramGraphics2D.setPaint(Color.black);
  }
  
  public boolean checkAllowedToPrintToFile()
  {
    try
    {
      throwPrintToFile();
      return true;
    }
    catch (SecurityException localSecurityException) {}
    return false;
  }
  
  private void throwPrintToFile()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      if (printToFilePermission == null) {
        printToFilePermission = new FilePermission("<<ALL FILES>>", "read,write");
      }
      localSecurityManager.checkPermission(printToFilePermission);
    }
  }
  
  protected String removeControlChars(String paramString)
  {
    char[] arrayOfChar1 = paramString.toCharArray();
    int i = arrayOfChar1.length;
    char[] arrayOfChar2 = new char[i];
    int j = 0;
    for (int k = 0; k < i; k++)
    {
      int m = arrayOfChar1[k];
      if ((m > 13) || (m < 9) || (m == 11) || (m == 12)) {
        arrayOfChar2[(j++)] = m;
      }
    }
    if (j == i) {
      return paramString;
    }
    return new String(arrayOfChar2, 0, j);
  }
  
  static
  {
    String str1 = (String)AccessController.doPrivileged(new GetPropertyAction("sun.java2d.print.pipeline"));
    if (str1 != null) {
      if (str1.equalsIgnoreCase("pdl")) {
        forcePDL = true;
      } else if (str1.equalsIgnoreCase("raster")) {
        forceRaster = true;
      }
    }
    String str2 = (String)AccessController.doPrivileged(new GetPropertyAction("sun.java2d.print.shapetext"));
    if (str2 != null) {
      shapeTextProp = true;
    }
  }
  
  private class GraphicsState
  {
    Rectangle2D region;
    Shape theClip;
    AffineTransform theTransform;
    double sx;
    double sy;
    
    private GraphicsState() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\print\RasterPrinterJob.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */