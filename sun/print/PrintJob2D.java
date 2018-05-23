package sun.print;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.JobAttributes;
import java.awt.JobAttributes.DefaultSelectionType;
import java.awt.JobAttributes.DestinationType;
import java.awt.JobAttributes.DialogType;
import java.awt.JobAttributes.MultipleDocumentHandlingType;
import java.awt.JobAttributes.SidesType;
import java.awt.PageAttributes;
import java.awt.PageAttributes.ColorType;
import java.awt.PageAttributes.MediaType;
import java.awt.PageAttributes.OrientationRequestedType;
import java.awt.PageAttributes.OriginType;
import java.awt.PageAttributes.PrintQualityType;
import java.awt.PrintJob;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Properties;
import javax.print.PrintService;
import javax.print.attribute.Attribute;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Chromaticity;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.Destination;
import javax.print.attribute.standard.DialogTypeSelection;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PageRanges;
import javax.print.attribute.standard.PrintQuality;
import javax.print.attribute.standard.SheetCollate;
import javax.print.attribute.standard.Sides;

public class PrintJob2D
  extends PrintJob
  implements Printable, Runnable
{
  private static final PageAttributes.MediaType[] SIZES = { PageAttributes.MediaType.ISO_4A0, PageAttributes.MediaType.ISO_2A0, PageAttributes.MediaType.ISO_A0, PageAttributes.MediaType.ISO_A1, PageAttributes.MediaType.ISO_A2, PageAttributes.MediaType.ISO_A3, PageAttributes.MediaType.ISO_A4, PageAttributes.MediaType.ISO_A5, PageAttributes.MediaType.ISO_A6, PageAttributes.MediaType.ISO_A7, PageAttributes.MediaType.ISO_A8, PageAttributes.MediaType.ISO_A9, PageAttributes.MediaType.ISO_A10, PageAttributes.MediaType.ISO_B0, PageAttributes.MediaType.ISO_B1, PageAttributes.MediaType.ISO_B2, PageAttributes.MediaType.ISO_B3, PageAttributes.MediaType.ISO_B4, PageAttributes.MediaType.ISO_B5, PageAttributes.MediaType.ISO_B6, PageAttributes.MediaType.ISO_B7, PageAttributes.MediaType.ISO_B8, PageAttributes.MediaType.ISO_B9, PageAttributes.MediaType.ISO_B10, PageAttributes.MediaType.JIS_B0, PageAttributes.MediaType.JIS_B1, PageAttributes.MediaType.JIS_B2, PageAttributes.MediaType.JIS_B3, PageAttributes.MediaType.JIS_B4, PageAttributes.MediaType.JIS_B5, PageAttributes.MediaType.JIS_B6, PageAttributes.MediaType.JIS_B7, PageAttributes.MediaType.JIS_B8, PageAttributes.MediaType.JIS_B9, PageAttributes.MediaType.JIS_B10, PageAttributes.MediaType.ISO_C0, PageAttributes.MediaType.ISO_C1, PageAttributes.MediaType.ISO_C2, PageAttributes.MediaType.ISO_C3, PageAttributes.MediaType.ISO_C4, PageAttributes.MediaType.ISO_C5, PageAttributes.MediaType.ISO_C6, PageAttributes.MediaType.ISO_C7, PageAttributes.MediaType.ISO_C8, PageAttributes.MediaType.ISO_C9, PageAttributes.MediaType.ISO_C10, PageAttributes.MediaType.ISO_DESIGNATED_LONG, PageAttributes.MediaType.EXECUTIVE, PageAttributes.MediaType.FOLIO, PageAttributes.MediaType.INVOICE, PageAttributes.MediaType.LEDGER, PageAttributes.MediaType.NA_LETTER, PageAttributes.MediaType.NA_LEGAL, PageAttributes.MediaType.QUARTO, PageAttributes.MediaType.A, PageAttributes.MediaType.B, PageAttributes.MediaType.C, PageAttributes.MediaType.D, PageAttributes.MediaType.E, PageAttributes.MediaType.NA_10X15_ENVELOPE, PageAttributes.MediaType.NA_10X14_ENVELOPE, PageAttributes.MediaType.NA_10X13_ENVELOPE, PageAttributes.MediaType.NA_9X12_ENVELOPE, PageAttributes.MediaType.NA_9X11_ENVELOPE, PageAttributes.MediaType.NA_7X9_ENVELOPE, PageAttributes.MediaType.NA_6X9_ENVELOPE, PageAttributes.MediaType.NA_NUMBER_9_ENVELOPE, PageAttributes.MediaType.NA_NUMBER_10_ENVELOPE, PageAttributes.MediaType.NA_NUMBER_11_ENVELOPE, PageAttributes.MediaType.NA_NUMBER_12_ENVELOPE, PageAttributes.MediaType.NA_NUMBER_14_ENVELOPE, PageAttributes.MediaType.INVITE_ENVELOPE, PageAttributes.MediaType.ITALY_ENVELOPE, PageAttributes.MediaType.MONARCH_ENVELOPE, PageAttributes.MediaType.PERSONAL_ENVELOPE };
  private static final MediaSizeName[] JAVAXSIZES = { null, null, MediaSizeName.ISO_A0, MediaSizeName.ISO_A1, MediaSizeName.ISO_A2, MediaSizeName.ISO_A3, MediaSizeName.ISO_A4, MediaSizeName.ISO_A5, MediaSizeName.ISO_A6, MediaSizeName.ISO_A7, MediaSizeName.ISO_A8, MediaSizeName.ISO_A9, MediaSizeName.ISO_A10, MediaSizeName.ISO_B0, MediaSizeName.ISO_B1, MediaSizeName.ISO_B2, MediaSizeName.ISO_B3, MediaSizeName.ISO_B4, MediaSizeName.ISO_B5, MediaSizeName.ISO_B6, MediaSizeName.ISO_B7, MediaSizeName.ISO_B8, MediaSizeName.ISO_B9, MediaSizeName.ISO_B10, MediaSizeName.JIS_B0, MediaSizeName.JIS_B1, MediaSizeName.JIS_B2, MediaSizeName.JIS_B3, MediaSizeName.JIS_B4, MediaSizeName.JIS_B5, MediaSizeName.JIS_B6, MediaSizeName.JIS_B7, MediaSizeName.JIS_B8, MediaSizeName.JIS_B9, MediaSizeName.JIS_B10, MediaSizeName.ISO_C0, MediaSizeName.ISO_C1, MediaSizeName.ISO_C2, MediaSizeName.ISO_C3, MediaSizeName.ISO_C4, MediaSizeName.ISO_C5, MediaSizeName.ISO_C6, null, null, null, null, MediaSizeName.ISO_DESIGNATED_LONG, MediaSizeName.EXECUTIVE, MediaSizeName.FOLIO, MediaSizeName.INVOICE, MediaSizeName.LEDGER, MediaSizeName.NA_LETTER, MediaSizeName.NA_LEGAL, MediaSizeName.QUARTO, MediaSizeName.A, MediaSizeName.B, MediaSizeName.C, MediaSizeName.D, MediaSizeName.E, MediaSizeName.NA_10X15_ENVELOPE, MediaSizeName.NA_10X14_ENVELOPE, MediaSizeName.NA_10X13_ENVELOPE, MediaSizeName.NA_9X12_ENVELOPE, MediaSizeName.NA_9X11_ENVELOPE, MediaSizeName.NA_7X9_ENVELOPE, MediaSizeName.NA_6X9_ENVELOPE, MediaSizeName.NA_NUMBER_9_ENVELOPE, MediaSizeName.NA_NUMBER_10_ENVELOPE, MediaSizeName.NA_NUMBER_11_ENVELOPE, MediaSizeName.NA_NUMBER_12_ENVELOPE, MediaSizeName.NA_NUMBER_14_ENVELOPE, null, MediaSizeName.ITALY_ENVELOPE, MediaSizeName.MONARCH_ENVELOPE, MediaSizeName.PERSONAL_ENVELOPE };
  private static final int[] WIDTHS = { 4768, 3370, 2384, 1684, 1191, 842, 595, 420, 298, 210, 147, 105, 74, 2835, 2004, 1417, 1001, 709, 499, 354, 249, 176, 125, 88, 2920, 2064, 1460, 1032, 729, 516, 363, 258, 181, 128, 91, 2599, 1837, 1298, 918, 649, 459, 323, 230, 162, 113, 79, 312, 522, 612, 396, 792, 612, 612, 609, 612, 792, 1224, 1584, 2448, 720, 720, 720, 648, 648, 504, 432, 279, 297, 324, 342, 360, 624, 312, 279, 261 };
  private static final int[] LENGTHS = { 6741, 4768, 3370, 2384, 1684, 1191, 842, 595, 420, 298, 210, 147, 105, 4008, 2835, 2004, 1417, 1001, 729, 499, 354, 249, 176, 125, 4127, 2920, 2064, 1460, 1032, 729, 516, 363, 258, 181, 128, 3677, 2599, 1837, 1298, 918, 649, 459, 323, 230, 162, 113, 624, 756, 936, 612, 1224, 792, 1008, 780, 792, 1224, 1584, 2448, 3168, 1080, 1008, 936, 864, 792, 648, 648, 639, 684, 747, 792, 828, 624, 652, 540, 468 };
  private Frame frame;
  private String docTitle = "";
  private JobAttributes jobAttributes;
  private PageAttributes pageAttributes;
  private PrintRequestAttributeSet attributes;
  private PrinterJob printerJob;
  private PageFormat pageFormat;
  private MessageQ graphicsToBeDrawn = new MessageQ("tobedrawn");
  private MessageQ graphicsDrawn = new MessageQ("drawn");
  private Graphics2D currentGraphics;
  private int pageIndex = -1;
  private static final String DEST_PROP = "awt.print.destination";
  private static final String PRINTER = "printer";
  private static final String FILE = "file";
  private static final String PRINTER_PROP = "awt.print.printer";
  private static final String FILENAME_PROP = "awt.print.fileName";
  private static final String NUMCOPIES_PROP = "awt.print.numCopies";
  private static final String OPTIONS_PROP = "awt.print.options";
  private static final String ORIENT_PROP = "awt.print.orientation";
  private static final String PORTRAIT = "portrait";
  private static final String LANDSCAPE = "landscape";
  private static final String PAPERSIZE_PROP = "awt.print.paperSize";
  private static final String LETTER = "letter";
  private static final String LEGAL = "legal";
  private static final String EXECUTIVE = "executive";
  private static final String A4 = "a4";
  private Properties props;
  private String options = "";
  private Thread printerJobThread;
  
  public PrintJob2D(Frame paramFrame, String paramString, Properties paramProperties)
  {
    props = paramProperties;
    jobAttributes = new JobAttributes();
    pageAttributes = new PageAttributes();
    translateInputProps();
    initPrintJob2D(paramFrame, paramString, jobAttributes, pageAttributes);
  }
  
  public PrintJob2D(Frame paramFrame, String paramString, JobAttributes paramJobAttributes, PageAttributes paramPageAttributes)
  {
    initPrintJob2D(paramFrame, paramString, paramJobAttributes, paramPageAttributes);
  }
  
  private void initPrintJob2D(Frame paramFrame, String paramString, JobAttributes paramJobAttributes, PageAttributes paramPageAttributes)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPrintJobAccess();
    }
    if ((paramFrame == null) && ((paramJobAttributes == null) || (paramJobAttributes.getDialog() == JobAttributes.DialogType.NATIVE))) {
      throw new NullPointerException("Frame must not be null");
    }
    frame = paramFrame;
    docTitle = (paramString == null ? "" : paramString);
    jobAttributes = (paramJobAttributes != null ? paramJobAttributes : new JobAttributes());
    pageAttributes = (paramPageAttributes != null ? paramPageAttributes : new PageAttributes());
    int[][] arrayOfInt = jobAttributes.getPageRanges();
    int i = arrayOfInt[0][0];
    int j = arrayOfInt[(arrayOfInt.length - 1)][1];
    jobAttributes.setPageRanges(new int[][] { { i, j } });
    jobAttributes.setToPage(j);
    jobAttributes.setFromPage(i);
    int[] arrayOfInt1 = pageAttributes.getPrinterResolution();
    if (arrayOfInt1[0] != arrayOfInt1[1]) {
      throw new IllegalArgumentException("Differing cross feed and feed resolutions not supported.");
    }
    JobAttributes.DestinationType localDestinationType = jobAttributes.getDestination();
    if (localDestinationType == JobAttributes.DestinationType.FILE)
    {
      throwPrintToFile();
      String str = paramJobAttributes.getFileName();
      if ((str != null) && (paramJobAttributes.getDialog() == JobAttributes.DialogType.NONE))
      {
        File localFile1 = new File(str);
        try
        {
          if (localFile1.createNewFile()) {
            localFile1.delete();
          }
        }
        catch (IOException localIOException)
        {
          throw new IllegalArgumentException("Cannot write to file:" + str);
        }
        catch (SecurityException localSecurityException) {}
        File localFile2 = localFile1.getParentFile();
        if (((localFile1.exists()) && ((!localFile1.isFile()) || (!localFile1.canWrite()))) || ((localFile2 != null) && ((!localFile2.exists()) || ((localFile2.exists()) && (!localFile2.canWrite()))))) {
          throw new IllegalArgumentException("Cannot write to file:" + str);
        }
      }
    }
  }
  
  public boolean printDialog()
  {
    boolean bool = false;
    printerJob = PrinterJob.getPrinterJob();
    if (printerJob == null) {
      return false;
    }
    JobAttributes.DialogType localDialogType = jobAttributes.getDialog();
    PrintService localPrintService = printerJob.getPrintService();
    if ((localPrintService == null) && (localDialogType == JobAttributes.DialogType.NONE)) {
      return false;
    }
    copyAttributes(localPrintService);
    JobAttributes.DefaultSelectionType localDefaultSelectionType = jobAttributes.getDefaultSelection();
    if (localDefaultSelectionType == JobAttributes.DefaultSelectionType.RANGE) {
      attributes.add(SunPageSelection.RANGE);
    } else if (localDefaultSelectionType == JobAttributes.DefaultSelectionType.SELECTION) {
      attributes.add(SunPageSelection.SELECTION);
    } else {
      attributes.add(SunPageSelection.ALL);
    }
    if (frame != null) {
      attributes.add(new DialogOwner(frame));
    }
    if (localDialogType == JobAttributes.DialogType.NONE)
    {
      bool = true;
    }
    else
    {
      if (localDialogType == JobAttributes.DialogType.NATIVE) {
        attributes.add(DialogTypeSelection.NATIVE);
      } else {
        attributes.add(DialogTypeSelection.COMMON);
      }
      if ((bool = printerJob.printDialog(attributes)))
      {
        if (localPrintService == null)
        {
          localPrintService = printerJob.getPrintService();
          if (localPrintService == null) {
            return false;
          }
        }
        updateAttributes();
        translateOutputProps();
      }
    }
    if (bool)
    {
      JobName localJobName = (JobName)attributes.get(JobName.class);
      if (localJobName != null) {
        printerJob.setJobName(localJobName.toString());
      }
      pageFormat = new PageFormat();
      Media localMedia = (Media)attributes.get(Media.class);
      MediaSize localMediaSize = null;
      if ((localMedia != null) && ((localMedia instanceof MediaSizeName))) {
        localMediaSize = MediaSize.getMediaSizeForName((MediaSizeName)localMedia);
      }
      Paper localPaper = pageFormat.getPaper();
      if (localMediaSize != null) {
        localPaper.setSize(localMediaSize.getX(25400) * 72.0D, localMediaSize.getY(25400) * 72.0D);
      }
      if (pageAttributes.getOrigin() == PageAttributes.OriginType.PRINTABLE) {
        localPaper.setImageableArea(18.0D, 18.0D, localPaper.getWidth() - 36.0D, localPaper.getHeight() - 36.0D);
      } else {
        localPaper.setImageableArea(0.0D, 0.0D, localPaper.getWidth(), localPaper.getHeight());
      }
      pageFormat.setPaper(localPaper);
      OrientationRequested localOrientationRequested = (OrientationRequested)attributes.get(OrientationRequested.class);
      if ((localOrientationRequested != null) && (localOrientationRequested == OrientationRequested.REVERSE_LANDSCAPE)) {
        pageFormat.setOrientation(2);
      } else if (localOrientationRequested == OrientationRequested.LANDSCAPE) {
        pageFormat.setOrientation(0);
      } else {
        pageFormat.setOrientation(1);
      }
      printerJob.setPrintable(this, pageFormat);
    }
    return bool;
  }
  
  private void updateAttributes()
  {
    Copies localCopies = (Copies)attributes.get(Copies.class);
    jobAttributes.setCopies(localCopies.getValue());
    SunPageSelection localSunPageSelection = (SunPageSelection)attributes.get(SunPageSelection.class);
    if (localSunPageSelection == SunPageSelection.RANGE) {
      jobAttributes.setDefaultSelection(JobAttributes.DefaultSelectionType.RANGE);
    } else if (localSunPageSelection == SunPageSelection.SELECTION) {
      jobAttributes.setDefaultSelection(JobAttributes.DefaultSelectionType.SELECTION);
    } else {
      jobAttributes.setDefaultSelection(JobAttributes.DefaultSelectionType.ALL);
    }
    Destination localDestination = (Destination)attributes.get(Destination.class);
    if (localDestination != null)
    {
      jobAttributes.setDestination(JobAttributes.DestinationType.FILE);
      jobAttributes.setFileName(localDestination.getURI().getPath());
    }
    else
    {
      jobAttributes.setDestination(JobAttributes.DestinationType.PRINTER);
    }
    PrintService localPrintService = printerJob.getPrintService();
    if (localPrintService != null) {
      jobAttributes.setPrinter(localPrintService.getName());
    }
    PageRanges localPageRanges = (PageRanges)attributes.get(PageRanges.class);
    int[][] arrayOfInt = localPageRanges.getMembers();
    jobAttributes.setPageRanges(arrayOfInt);
    SheetCollate localSheetCollate = (SheetCollate)attributes.get(SheetCollate.class);
    if (localSheetCollate == SheetCollate.COLLATED) {
      jobAttributes.setMultipleDocumentHandling(JobAttributes.MultipleDocumentHandlingType.SEPARATE_DOCUMENTS_COLLATED_COPIES);
    } else {
      jobAttributes.setMultipleDocumentHandling(JobAttributes.MultipleDocumentHandlingType.SEPARATE_DOCUMENTS_UNCOLLATED_COPIES);
    }
    Sides localSides = (Sides)attributes.get(Sides.class);
    if (localSides == Sides.TWO_SIDED_LONG_EDGE) {
      jobAttributes.setSides(JobAttributes.SidesType.TWO_SIDED_LONG_EDGE);
    } else if (localSides == Sides.TWO_SIDED_SHORT_EDGE) {
      jobAttributes.setSides(JobAttributes.SidesType.TWO_SIDED_SHORT_EDGE);
    } else {
      jobAttributes.setSides(JobAttributes.SidesType.ONE_SIDED);
    }
    Chromaticity localChromaticity = (Chromaticity)attributes.get(Chromaticity.class);
    if (localChromaticity == Chromaticity.COLOR) {
      pageAttributes.setColor(PageAttributes.ColorType.COLOR);
    } else {
      pageAttributes.setColor(PageAttributes.ColorType.MONOCHROME);
    }
    OrientationRequested localOrientationRequested = (OrientationRequested)attributes.get(OrientationRequested.class);
    if (localOrientationRequested == OrientationRequested.LANDSCAPE) {
      pageAttributes.setOrientationRequested(PageAttributes.OrientationRequestedType.LANDSCAPE);
    } else {
      pageAttributes.setOrientationRequested(PageAttributes.OrientationRequestedType.PORTRAIT);
    }
    PrintQuality localPrintQuality = (PrintQuality)attributes.get(PrintQuality.class);
    if (localPrintQuality == PrintQuality.DRAFT) {
      pageAttributes.setPrintQuality(PageAttributes.PrintQualityType.DRAFT);
    } else if (localPrintQuality == PrintQuality.HIGH) {
      pageAttributes.setPrintQuality(PageAttributes.PrintQualityType.HIGH);
    } else {
      pageAttributes.setPrintQuality(PageAttributes.PrintQualityType.NORMAL);
    }
    Media localMedia = (Media)attributes.get(Media.class);
    if ((localMedia != null) && ((localMedia instanceof MediaSizeName)))
    {
      PageAttributes.MediaType localMediaType = unMapMedia((MediaSizeName)localMedia);
      if (localMediaType != null) {
        pageAttributes.setMedia(localMediaType);
      }
    }
    debugPrintAttributes(false, false);
  }
  
  private void debugPrintAttributes(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean1) {
      System.out.println("new Attributes\ncopies = " + jobAttributes.getCopies() + "\nselection = " + jobAttributes.getDefaultSelection() + "\ndest " + jobAttributes.getDestination() + "\nfile " + jobAttributes.getFileName() + "\nfromPage " + jobAttributes.getFromPage() + "\ntoPage " + jobAttributes.getToPage() + "\ncollation " + jobAttributes.getMultipleDocumentHandling() + "\nPrinter " + jobAttributes.getPrinter() + "\nSides2 " + jobAttributes.getSides());
    }
    if (paramBoolean2) {
      System.out.println("new Attributes\ncolor = " + pageAttributes.getColor() + "\norientation = " + pageAttributes.getOrientationRequested() + "\nquality " + pageAttributes.getPrintQuality() + "\nMedia2 " + pageAttributes.getMedia());
    }
  }
  
  private void copyAttributes(PrintService paramPrintService)
  {
    attributes = new HashPrintRequestAttributeSet();
    attributes.add(new JobName(docTitle, null));
    PrintService localPrintService = paramPrintService;
    String str = jobAttributes.getPrinter();
    if ((str != null) && (str != "") && (!str.equals(localPrintService.getName())))
    {
      localObject1 = PrinterJob.lookupPrintServices();
      try
      {
        for (int i = 0; i < localObject1.length; i++) {
          if (str.equals(localObject1[i].getName()))
          {
            printerJob.setPrintService(localObject1[i]);
            localPrintService = localObject1[i];
            break;
          }
        }
      }
      catch (PrinterException localPrinterException) {}
    }
    Object localObject1 = jobAttributes.getDestination();
    if ((localObject1 == JobAttributes.DestinationType.FILE) && (localPrintService.isAttributeCategorySupported(Destination.class)))
    {
      localObject2 = jobAttributes.getFileName();
      if ((localObject2 == null) && ((localObject3 = (Destination)localPrintService.getDefaultAttributeValue(Destination.class)) != null))
      {
        attributes.add((Attribute)localObject3);
      }
      else
      {
        localObject4 = null;
        try
        {
          if (localObject2 != null)
          {
            if (((String)localObject2).equals("")) {
              localObject2 = ".";
            }
          }
          else {
            localObject2 = "out.prn";
          }
          localObject4 = new File((String)localObject2).toURI();
        }
        catch (SecurityException localSecurityException)
        {
          try
          {
            localObject2 = ((String)localObject2).replace('\\', '/');
            localObject4 = new URI("file:" + (String)localObject2);
          }
          catch (URISyntaxException localURISyntaxException) {}
        }
        if (localObject4 != null) {
          attributes.add(new Destination((URI)localObject4));
        }
      }
    }
    attributes.add(new SunMinMaxPage(jobAttributes.getMinPage(), jobAttributes.getMaxPage()));
    Object localObject2 = jobAttributes.getSides();
    if (localObject2 == JobAttributes.SidesType.TWO_SIDED_LONG_EDGE) {
      attributes.add(Sides.TWO_SIDED_LONG_EDGE);
    } else if (localObject2 == JobAttributes.SidesType.TWO_SIDED_SHORT_EDGE) {
      attributes.add(Sides.TWO_SIDED_SHORT_EDGE);
    } else if (localObject2 == JobAttributes.SidesType.ONE_SIDED) {
      attributes.add(Sides.ONE_SIDED);
    }
    Object localObject3 = jobAttributes.getMultipleDocumentHandling();
    if (localObject3 == JobAttributes.MultipleDocumentHandlingType.SEPARATE_DOCUMENTS_COLLATED_COPIES) {
      attributes.add(SheetCollate.COLLATED);
    } else {
      attributes.add(SheetCollate.UNCOLLATED);
    }
    attributes.add(new Copies(jobAttributes.getCopies()));
    attributes.add(new PageRanges(jobAttributes.getFromPage(), jobAttributes.getToPage()));
    if (pageAttributes.getColor() == PageAttributes.ColorType.COLOR) {
      attributes.add(Chromaticity.COLOR);
    } else {
      attributes.add(Chromaticity.MONOCHROME);
    }
    pageFormat = printerJob.defaultPage();
    if (pageAttributes.getOrientationRequested() == PageAttributes.OrientationRequestedType.LANDSCAPE)
    {
      pageFormat.setOrientation(0);
      attributes.add(OrientationRequested.LANDSCAPE);
    }
    else
    {
      pageFormat.setOrientation(1);
      attributes.add(OrientationRequested.PORTRAIT);
    }
    Object localObject4 = pageAttributes.getMedia();
    MediaSizeName localMediaSizeName = mapMedia((PageAttributes.MediaType)localObject4);
    if (localMediaSizeName != null) {
      attributes.add(localMediaSizeName);
    }
    PageAttributes.PrintQualityType localPrintQualityType = pageAttributes.getPrintQuality();
    if (localPrintQualityType == PageAttributes.PrintQualityType.DRAFT) {
      attributes.add(PrintQuality.DRAFT);
    } else if (localPrintQualityType == PageAttributes.PrintQualityType.NORMAL) {
      attributes.add(PrintQuality.NORMAL);
    } else if (localPrintQualityType == PageAttributes.PrintQualityType.HIGH) {
      attributes.add(PrintQuality.HIGH);
    }
  }
  
  public Graphics getGraphics()
  {
    ProxyPrintGraphics localProxyPrintGraphics = null;
    synchronized (this)
    {
      pageIndex += 1;
      if ((pageIndex == 0) && (!graphicsToBeDrawn.isClosed())) {
        startPrinterJobThread();
      }
      notify();
    }
    if (currentGraphics != null)
    {
      graphicsDrawn.append(currentGraphics);
      currentGraphics = null;
    }
    currentGraphics = graphicsToBeDrawn.pop();
    if ((currentGraphics instanceof PeekGraphics))
    {
      ((PeekGraphics)currentGraphics).setAWTDrawingOnly();
      graphicsDrawn.append(currentGraphics);
      currentGraphics = graphicsToBeDrawn.pop();
    }
    if (currentGraphics != null)
    {
      currentGraphics.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
      double d = 72.0D / getPageResolutionInternal();
      currentGraphics.scale(d, d);
      localProxyPrintGraphics = new ProxyPrintGraphics(currentGraphics.create(), this);
    }
    return localProxyPrintGraphics;
  }
  
  public Dimension getPageDimension()
  {
    double d1;
    double d2;
    if ((pageAttributes != null) && (pageAttributes.getOrigin() == PageAttributes.OriginType.PRINTABLE))
    {
      d1 = pageFormat.getImageableWidth();
      d2 = pageFormat.getImageableHeight();
    }
    else
    {
      d1 = pageFormat.getWidth();
      d2 = pageFormat.getHeight();
    }
    double d3 = getPageResolutionInternal() / 72.0D;
    return new Dimension((int)(d1 * d3), (int)(d2 * d3));
  }
  
  private double getPageResolutionInternal()
  {
    if (pageAttributes != null)
    {
      int[] arrayOfInt = pageAttributes.getPrinterResolution();
      if (arrayOfInt[2] == 3) {
        return arrayOfInt[0];
      }
      return arrayOfInt[0] * 2.54D;
    }
    return 72.0D;
  }
  
  public int getPageResolution()
  {
    return (int)getPageResolutionInternal();
  }
  
  public boolean lastPageFirst()
  {
    return false;
  }
  
  public synchronized void end()
  {
    graphicsToBeDrawn.close();
    if (currentGraphics != null) {
      graphicsDrawn.append(currentGraphics);
    }
    graphicsDrawn.closeWhenEmpty();
    if ((printerJobThread != null) && (printerJobThread.isAlive())) {
      try
      {
        printerJobThread.join();
      }
      catch (InterruptedException localInterruptedException) {}
    }
  }
  
  public void finalize()
  {
    end();
  }
  
  public int print(Graphics paramGraphics, PageFormat paramPageFormat, int paramInt)
    throws PrinterException
  {
    graphicsToBeDrawn.append((Graphics2D)paramGraphics);
    int i;
    if (graphicsDrawn.pop() != null) {
      i = 0;
    } else {
      i = 1;
    }
    return i;
  }
  
  private void startPrinterJobThread()
  {
    printerJobThread = new Thread(this, "printerJobThread");
    printerJobThread.start();
  }
  
  public void run()
  {
    try
    {
      printerJob.print(attributes);
    }
    catch (PrinterException localPrinterException) {}
    graphicsToBeDrawn.closeWhenEmpty();
    graphicsDrawn.close();
  }
  
  private static int[] getSize(PageAttributes.MediaType paramMediaType)
  {
    int[] arrayOfInt = new int[2];
    arrayOfInt[0] = 612;
    arrayOfInt[1] = 792;
    for (int i = 0; i < SIZES.length; i++) {
      if (SIZES[i] == paramMediaType)
      {
        arrayOfInt[0] = WIDTHS[i];
        arrayOfInt[1] = LENGTHS[i];
        break;
      }
    }
    return arrayOfInt;
  }
  
  public static MediaSizeName mapMedia(PageAttributes.MediaType paramMediaType)
  {
    Object localObject = null;
    int i = Math.min(SIZES.length, JAVAXSIZES.length);
    for (int j = 0; j < i; j++) {
      if (SIZES[j] == paramMediaType)
      {
        if ((JAVAXSIZES[j] != null) && (MediaSize.getMediaSizeForName(JAVAXSIZES[j]) != null))
        {
          localObject = JAVAXSIZES[j];
          break;
        }
        localObject = new CustomMediaSizeName(SIZES[j].toString());
        float f1 = (float)Math.rint(WIDTHS[j] / 72.0D);
        float f2 = (float)Math.rint(LENGTHS[j] / 72.0D);
        if ((f1 <= 0.0D) || (f2 <= 0.0D)) {
          break;
        }
        new MediaSize(f1, f2, 25400, (MediaSizeName)localObject);
        break;
      }
    }
    return (MediaSizeName)localObject;
  }
  
  public static PageAttributes.MediaType unMapMedia(MediaSizeName paramMediaSizeName)
  {
    PageAttributes.MediaType localMediaType = null;
    int i = Math.min(SIZES.length, JAVAXSIZES.length);
    for (int j = 0; j < i; j++) {
      if ((JAVAXSIZES[j] == paramMediaSizeName) && (SIZES[j] != null))
      {
        localMediaType = SIZES[j];
        break;
      }
    }
    return localMediaType;
  }
  
  private void translateInputProps()
  {
    if (props == null) {
      return;
    }
    String str = props.getProperty("awt.print.destination");
    if (str != null) {
      if (str.equals("printer")) {
        jobAttributes.setDestination(JobAttributes.DestinationType.PRINTER);
      } else if (str.equals("file")) {
        jobAttributes.setDestination(JobAttributes.DestinationType.FILE);
      }
    }
    str = props.getProperty("awt.print.printer");
    if (str != null) {
      jobAttributes.setPrinter(str);
    }
    str = props.getProperty("awt.print.fileName");
    if (str != null) {
      jobAttributes.setFileName(str);
    }
    str = props.getProperty("awt.print.numCopies");
    if (str != null) {
      jobAttributes.setCopies(Integer.parseInt(str));
    }
    options = props.getProperty("awt.print.options", "");
    str = props.getProperty("awt.print.orientation");
    if (str != null) {
      if (str.equals("portrait")) {
        pageAttributes.setOrientationRequested(PageAttributes.OrientationRequestedType.PORTRAIT);
      } else if (str.equals("landscape")) {
        pageAttributes.setOrientationRequested(PageAttributes.OrientationRequestedType.LANDSCAPE);
      }
    }
    str = props.getProperty("awt.print.paperSize");
    if (str != null) {
      if (str.equals("letter")) {
        pageAttributes.setMedia(SIZES[PageAttributes.MediaType.LETTER.hashCode()]);
      } else if (str.equals("legal")) {
        pageAttributes.setMedia(SIZES[PageAttributes.MediaType.LEGAL.hashCode()]);
      } else if (str.equals("executive")) {
        pageAttributes.setMedia(SIZES[PageAttributes.MediaType.EXECUTIVE.hashCode()]);
      } else if (str.equals("a4")) {
        pageAttributes.setMedia(SIZES[PageAttributes.MediaType.A4.hashCode()]);
      }
    }
  }
  
  private void translateOutputProps()
  {
    if (props == null) {
      return;
    }
    props.setProperty("awt.print.destination", jobAttributes.getDestination() == JobAttributes.DestinationType.PRINTER ? "printer" : "file");
    String str = jobAttributes.getPrinter();
    if ((str != null) && (!str.equals(""))) {
      props.setProperty("awt.print.printer", str);
    }
    str = jobAttributes.getFileName();
    if ((str != null) && (!str.equals(""))) {
      props.setProperty("awt.print.fileName", str);
    }
    int i = jobAttributes.getCopies();
    if (i > 0) {
      props.setProperty("awt.print.numCopies", "" + i);
    }
    str = options;
    if ((str != null) && (!str.equals(""))) {
      props.setProperty("awt.print.options", str);
    }
    props.setProperty("awt.print.orientation", pageAttributes.getOrientationRequested() == PageAttributes.OrientationRequestedType.PORTRAIT ? "portrait" : "landscape");
    PageAttributes.MediaType localMediaType = SIZES[pageAttributes.getMedia().hashCode()];
    if (localMediaType == PageAttributes.MediaType.LETTER) {
      str = "letter";
    } else if (localMediaType == PageAttributes.MediaType.LEGAL) {
      str = "legal";
    } else if (localMediaType == PageAttributes.MediaType.EXECUTIVE) {
      str = "executive";
    } else if (localMediaType == PageAttributes.MediaType.A4) {
      str = "a4";
    } else {
      str = localMediaType.toString();
    }
    props.setProperty("awt.print.paperSize", str);
  }
  
  private void throwPrintToFile()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    FilePermission localFilePermission = null;
    if (localSecurityManager != null)
    {
      if (localFilePermission == null) {
        localFilePermission = new FilePermission("<<ALL FILES>>", "read,write");
      }
      localSecurityManager.checkPermission(localFilePermission);
    }
  }
  
  private class MessageQ
  {
    private String qid = "noname";
    private ArrayList queue = new ArrayList();
    
    MessageQ(String paramString)
    {
      qid = paramString;
    }
    
    synchronized void closeWhenEmpty()
    {
      while ((queue != null) && (queue.size() > 0)) {
        try
        {
          wait(1000L);
        }
        catch (InterruptedException localInterruptedException) {}
      }
      queue = null;
      notifyAll();
    }
    
    synchronized void close()
    {
      queue = null;
      notifyAll();
    }
    
    synchronized boolean append(Graphics2D paramGraphics2D)
    {
      boolean bool = false;
      if (queue != null)
      {
        queue.add(paramGraphics2D);
        bool = true;
        notify();
      }
      return bool;
    }
    
    synchronized Graphics2D pop()
    {
      Graphics2D localGraphics2D = null;
      while ((localGraphics2D == null) && (queue != null)) {
        if (queue.size() > 0)
        {
          localGraphics2D = (Graphics2D)queue.remove(0);
          notify();
        }
        else
        {
          try
          {
            wait(2000L);
          }
          catch (InterruptedException localInterruptedException) {}
        }
      }
      return localGraphics2D;
    }
    
    synchronized boolean isClosed()
    {
      return queue == null;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\print\PrintJob2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */