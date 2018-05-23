package sun.print;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterIOException;
import java.awt.print.PrinterJob;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderMalfunctionError;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import javax.print.PrintService;
import javax.print.StreamPrintService;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.Destination;
import javax.print.attribute.standard.DialogTypeSelection;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.PrinterName;
import javax.print.attribute.standard.Sides;
import sun.awt.CharsetString;
import sun.awt.FontConfiguration;
import sun.awt.FontDescriptor;
import sun.awt.PlatformFont;
import sun.awt.SunToolkit;
import sun.font.Font2D;
import sun.font.FontUtilities;

public class PSPrinterJob
  extends RasterPrinterJob
{
  protected static final int FILL_EVEN_ODD = 1;
  protected static final int FILL_WINDING = 2;
  private static final int MAX_PSSTR = 65535;
  private static final int RED_MASK = 16711680;
  private static final int GREEN_MASK = 65280;
  private static final int BLUE_MASK = 255;
  private static final int RED_SHIFT = 16;
  private static final int GREEN_SHIFT = 8;
  private static final int BLUE_SHIFT = 0;
  private static final int LOWNIBBLE_MASK = 15;
  private static final int HINIBBLE_MASK = 240;
  private static final int HINIBBLE_SHIFT = 4;
  private static final byte[] hexDigits = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70 };
  private static final int PS_XRES = 300;
  private static final int PS_YRES = 300;
  private static final String ADOBE_PS_STR = "%!PS-Adobe-3.0";
  private static final String EOF_COMMENT = "%%EOF";
  private static final String PAGE_COMMENT = "%%Page: ";
  private static final String READIMAGEPROC = "/imStr 0 def /imageSrc {currentfile /ASCII85Decode filter /RunLengthDecode filter  imStr readstring pop } def";
  private static final String COPIES = "/#copies exch def";
  private static final String PAGE_SAVE = "/pgSave save def";
  private static final String PAGE_RESTORE = "pgSave restore";
  private static final String SHOWPAGE = "showpage";
  private static final String IMAGE_SAVE = "/imSave save def";
  private static final String IMAGE_STR = " string /imStr exch def";
  private static final String IMAGE_RESTORE = "imSave restore";
  private static final String COORD_PREP = " 0 exch translate 1 -1 scale[72 300 div 0 0 72 300 div 0 0]concat";
  private static final String SetFontName = "F";
  private static final String DrawStringName = "S";
  private static final String EVEN_ODD_FILL_STR = "EF";
  private static final String WINDING_FILL_STR = "WF";
  private static final String EVEN_ODD_CLIP_STR = "EC";
  private static final String WINDING_CLIP_STR = "WC";
  private static final String MOVETO_STR = " M";
  private static final String LINETO_STR = " L";
  private static final String CURVETO_STR = " C";
  private static final String GRESTORE_STR = "R";
  private static final String GSAVE_STR = "G";
  private static final String NEWPATH_STR = "N";
  private static final String CLOSEPATH_STR = "P";
  private static final String SETRGBCOLOR_STR = " SC";
  private static final String SETGRAY_STR = " SG";
  private int mDestType;
  private String mDestination = "lp";
  private boolean mNoJobSheet = false;
  private String mOptions;
  private Font mLastFont;
  private Color mLastColor;
  private Shape mLastClip;
  private AffineTransform mLastTransform;
  private EPSPrinter epsPrinter = null;
  FontMetrics mCurMetrics;
  PrintStream mPSStream;
  File spoolFile;
  private String mFillOpStr = "WF";
  private String mClipOpStr = "WC";
  ArrayList mGStateStack = new ArrayList();
  private float mPenX;
  private float mPenY;
  private float mStartPathX;
  private float mStartPathY;
  private static Properties mFontProps = null;
  private static boolean isMac;
  
  private static Properties initProps()
  {
    String str1 = System.getProperty("java.home");
    if (str1 != null)
    {
      String str2 = SunToolkit.getStartupLocale().getLanguage();
      try
      {
        File localFile = new File(str1 + File.separator + "lib" + File.separator + "psfontj2d.properties." + str2);
        if (!localFile.canRead())
        {
          localFile = new File(str1 + File.separator + "lib" + File.separator + "psfont.properties." + str2);
          if (!localFile.canRead())
          {
            localFile = new File(str1 + File.separator + "lib" + File.separator + "psfontj2d.properties");
            if (!localFile.canRead())
            {
              localFile = new File(str1 + File.separator + "lib" + File.separator + "psfont.properties");
              if (!localFile.canRead()) {
                return (Properties)null;
              }
            }
          }
        }
        BufferedInputStream localBufferedInputStream = new BufferedInputStream(new FileInputStream(localFile.getPath()));
        Properties localProperties = new Properties();
        localProperties.load(localBufferedInputStream);
        localBufferedInputStream.close();
        return localProperties;
      }
      catch (Exception localException)
      {
        return (Properties)null;
      }
    }
    return (Properties)null;
  }
  
  public PSPrinterJob() {}
  
  public boolean printDialog()
    throws HeadlessException
  {
    if (GraphicsEnvironment.isHeadless()) {
      throw new HeadlessException();
    }
    if (attributes == null) {
      attributes = new HashPrintRequestAttributeSet();
    }
    attributes.add(new Copies(getCopies()));
    attributes.add(new JobName(getJobName(), null));
    boolean bool = false;
    DialogTypeSelection localDialogTypeSelection = (DialogTypeSelection)attributes.get(DialogTypeSelection.class);
    if (localDialogTypeSelection == DialogTypeSelection.NATIVE)
    {
      attributes.remove(DialogTypeSelection.class);
      bool = printDialog(attributes);
      attributes.add(DialogTypeSelection.NATIVE);
    }
    else
    {
      bool = printDialog(attributes);
    }
    if (bool)
    {
      JobName localJobName = (JobName)attributes.get(JobName.class);
      if (localJobName != null) {
        setJobName(localJobName.getValue());
      }
      Copies localCopies = (Copies)attributes.get(Copies.class);
      if (localCopies != null) {
        setCopies(localCopies.getValue());
      }
      Destination localDestination = (Destination)attributes.get(Destination.class);
      if (localDestination != null)
      {
        try
        {
          mDestType = 1;
          mDestination = new File(localDestination.getURI()).getPath();
        }
        catch (Exception localException)
        {
          mDestination = "out.ps";
        }
      }
      else
      {
        mDestType = 0;
        PrintService localPrintService = getPrintService();
        if (localPrintService != null)
        {
          mDestination = localPrintService.getName();
          if (isMac)
          {
            PrintServiceAttributeSet localPrintServiceAttributeSet = localPrintService.getAttributes();
            if (localPrintServiceAttributeSet != null) {
              mDestination = localPrintServiceAttributeSet.get(PrinterName.class).toString();
            }
          }
        }
      }
    }
    return bool;
  }
  
  protected void startDoc()
    throws PrinterException
  {
    if (epsPrinter == null)
    {
      Object localObject;
      if ((getPrintService() instanceof PSStreamPrintService))
      {
        StreamPrintService localStreamPrintService = (StreamPrintService)getPrintService();
        mDestType = 2;
        if (localStreamPrintService.isDisposed()) {
          throw new PrinterException("service is disposed");
        }
        localObject = localStreamPrintService.getOutputStream();
        if (localObject == null) {
          throw new PrinterException("Null output stream");
        }
      }
      else
      {
        mNoJobSheet = noJobSheet;
        if (destinationAttr != null)
        {
          mDestType = 1;
          mDestination = destinationAttr;
        }
        if (mDestType == 1)
        {
          try
          {
            spoolFile = new File(mDestination);
            localObject = new FileOutputStream(spoolFile);
          }
          catch (IOException localIOException)
          {
            throw new PrinterIOException(localIOException);
          }
        }
        else
        {
          PrinterOpener localPrinterOpener = new PrinterOpener(null);
          AccessController.doPrivileged(localPrinterOpener);
          if (pex != null) {
            throw pex;
          }
          localObject = result;
        }
      }
      mPSStream = new PrintStream(new BufferedOutputStream((OutputStream)localObject));
      mPSStream.println("%!PS-Adobe-3.0");
    }
    mPSStream.println("%%BeginProlog");
    mPSStream.println("/imStr 0 def /imageSrc {currentfile /ASCII85Decode filter /RunLengthDecode filter  imStr readstring pop } def");
    mPSStream.println("/BD {bind def} bind def");
    mPSStream.println("/D {def} BD");
    mPSStream.println("/C {curveto} BD");
    mPSStream.println("/L {lineto} BD");
    mPSStream.println("/M {moveto} BD");
    mPSStream.println("/R {grestore} BD");
    mPSStream.println("/G {gsave} BD");
    mPSStream.println("/N {newpath} BD");
    mPSStream.println("/P {closepath} BD");
    mPSStream.println("/EC {eoclip} BD");
    mPSStream.println("/WC {clip} BD");
    mPSStream.println("/EF {eofill} BD");
    mPSStream.println("/WF {fill} BD");
    mPSStream.println("/SG {setgray} BD");
    mPSStream.println("/SC {setrgbcolor} BD");
    mPSStream.println("/ISOF {");
    mPSStream.println("     dup findfont dup length 1 add dict begin {");
    mPSStream.println("             1 index /FID eq {pop pop} {D} ifelse");
    mPSStream.println("     } forall /Encoding ISOLatin1Encoding D");
    mPSStream.println("     currentdict end definefont");
    mPSStream.println("} BD");
    mPSStream.println("/NZ {dup 1 lt {pop 1} if} BD");
    mPSStream.println("/S {");
    mPSStream.println("     moveto 1 index stringwidth pop NZ sub");
    mPSStream.println("     1 index length 1 sub NZ div 0");
    mPSStream.println("     3 2 roll ashow newpath} BD");
    mPSStream.println("/FL [");
    if (mFontProps == null)
    {
      mPSStream.println(" /Helvetica ISOF");
      mPSStream.println(" /Helvetica-Bold ISOF");
      mPSStream.println(" /Helvetica-Oblique ISOF");
      mPSStream.println(" /Helvetica-BoldOblique ISOF");
      mPSStream.println(" /Times-Roman ISOF");
      mPSStream.println(" /Times-Bold ISOF");
      mPSStream.println(" /Times-Italic ISOF");
      mPSStream.println(" /Times-BoldItalic ISOF");
      mPSStream.println(" /Courier ISOF");
      mPSStream.println(" /Courier-Bold ISOF");
      mPSStream.println(" /Courier-Oblique ISOF");
      mPSStream.println(" /Courier-BoldOblique ISOF");
    }
    else
    {
      int i = Integer.parseInt(mFontProps.getProperty("font.num", "9"));
      for (int j = 0; j < i; j++) {
        mPSStream.println("    /" + mFontProps.getProperty(new StringBuilder().append("font.").append(String.valueOf(j)).toString(), "Courier ISOF"));
      }
    }
    mPSStream.println("] D");
    mPSStream.println("/F {");
    mPSStream.println("     FL exch get exch scalefont");
    mPSStream.println("     [1 0 0 -1 0 0] makefont setfont} BD");
    mPSStream.println("%%EndProlog");
    mPSStream.println("%%BeginSetup");
    if (epsPrinter == null)
    {
      PageFormat localPageFormat = getPageable().getPageFormat(0);
      double d1 = localPageFormat.getPaper().getHeight();
      double d2 = localPageFormat.getPaper().getWidth();
      mPSStream.print("<< /PageSize [" + d2 + " " + d1 + "]");
      final PrintService localPrintService = getPrintService();
      Boolean localBoolean = (Boolean)AccessController.doPrivileged(new PrivilegedAction()
      {
        public Object run()
        {
          try
          {
            Class localClass = Class.forName("sun.print.IPPPrintService");
            if (localClass.isInstance(localPrintService))
            {
              Method localMethod = localClass.getMethod("isPostscript", (Class[])null);
              return (Boolean)localMethod.invoke(localPrintService, (Object[])null);
            }
          }
          catch (Throwable localThrowable) {}
          return Boolean.TRUE;
        }
      });
      if (localBoolean.booleanValue()) {
        mPSStream.print(" /DeferredMediaSelection true");
      }
      mPSStream.print(" /ImagingBBox null /ManualFeed false");
      mPSStream.print(isCollated() ? " /Collate true" : "");
      mPSStream.print(" /NumCopies " + getCopiesInt());
      if (sidesAttr != Sides.ONE_SIDED) {
        if (sidesAttr == Sides.TWO_SIDED_LONG_EDGE) {
          mPSStream.print(" /Duplex true ");
        } else if (sidesAttr == Sides.TWO_SIDED_SHORT_EDGE) {
          mPSStream.print(" /Duplex true /Tumble true ");
        }
      }
      mPSStream.println(" >> setpagedevice ");
    }
    mPSStream.println("%%EndSetup");
  }
  
  protected void abortDoc()
  {
    if ((mPSStream != null) && (mDestType != 2)) {
      mPSStream.close();
    }
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        if ((spoolFile != null) && (spoolFile.exists())) {
          spoolFile.delete();
        }
        return null;
      }
    });
  }
  
  protected void endDoc()
    throws PrinterException
  {
    if (mPSStream != null)
    {
      mPSStream.println("%%EOF");
      mPSStream.flush();
      if (mDestType != 2) {
        mPSStream.close();
      }
    }
    if (mDestType == 0)
    {
      PrintService localPrintService = getPrintService();
      if (localPrintService != null)
      {
        mDestination = localPrintService.getName();
        if (isMac)
        {
          localObject = localPrintService.getAttributes();
          if (localObject != null) {
            mDestination = ((PrintServiceAttributeSet)localObject).get(PrinterName.class).toString();
          }
        }
      }
      Object localObject = new PrinterSpooler(null);
      AccessController.doPrivileged((PrivilegedAction)localObject);
      if (pex != null) {
        throw pex;
      }
    }
  }
  
  protected void startPage(PageFormat paramPageFormat, Printable paramPrintable, int paramInt, boolean paramBoolean)
    throws PrinterException
  {
    double d1 = paramPageFormat.getPaper().getHeight();
    double d2 = paramPageFormat.getPaper().getWidth();
    int i = paramInt + 1;
    mGStateStack = new ArrayList();
    mGStateStack.add(new GState());
    mPSStream.println("%%Page: " + i + " " + i);
    if ((paramInt > 0) && (paramBoolean))
    {
      mPSStream.print("<< /PageSize [" + d2 + " " + d1 + "]");
      final PrintService localPrintService = getPrintService();
      Boolean localBoolean = (Boolean)AccessController.doPrivileged(new PrivilegedAction()
      {
        public Object run()
        {
          try
          {
            Class localClass = Class.forName("sun.print.IPPPrintService");
            if (localClass.isInstance(localPrintService))
            {
              Method localMethod = localClass.getMethod("isPostscript", (Class[])null);
              return (Boolean)localMethod.invoke(localPrintService, (Object[])null);
            }
          }
          catch (Throwable localThrowable) {}
          return Boolean.TRUE;
        }
      });
      if (localBoolean.booleanValue()) {
        mPSStream.print(" /DeferredMediaSelection true");
      }
      mPSStream.println(" >> setpagedevice");
    }
    mPSStream.println("/pgSave save def");
    mPSStream.println(d1 + " 0 exch translate 1 -1 scale[72 300 div 0 0 72 300 div 0 0]concat");
  }
  
  protected void endPage(PageFormat paramPageFormat, Printable paramPrintable, int paramInt)
    throws PrinterException
  {
    mPSStream.println("pgSave restore");
    mPSStream.println("showpage");
  }
  
  protected void drawImageBGR(byte[] paramArrayOfByte, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7, float paramFloat8, int paramInt1, int paramInt2)
  {
    setTransform(new AffineTransform());
    prepDrawing();
    int i = (int)paramFloat7;
    int j = (int)paramFloat8;
    mPSStream.println("/imSave save def");
    int k = 3 * i;
    while (k > 65535) {
      k /= 2;
    }
    mPSStream.println(k + " string /imStr exch def");
    mPSStream.println("[" + paramFloat3 + " 0 0 " + paramFloat4 + " " + paramFloat1 + " " + paramFloat2 + "]concat");
    mPSStream.println(i + " " + j + " " + 8 + "[" + i + " 0 0 " + j + " 0 " + 0 + "]/imageSrc load false 3 colorimage");
    int m = 0;
    byte[] arrayOfByte1 = new byte[i * 3];
    try
    {
      m = (int)paramFloat6 * paramInt1;
      for (int n = 0; n < j; n++)
      {
        m += (int)paramFloat5;
        m = swapBGRtoRGB(paramArrayOfByte, m, arrayOfByte1);
        byte[] arrayOfByte2 = rlEncode(arrayOfByte1);
        byte[] arrayOfByte3 = ascii85Encode(arrayOfByte2);
        mPSStream.write(arrayOfByte3);
        mPSStream.println("");
      }
    }
    catch (IOException localIOException) {}
    mPSStream.println("imSave restore");
  }
  
  protected void printBand(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws PrinterException
  {
    mPSStream.println("/imSave save def");
    int i = 3 * paramInt3;
    while (i > 65535) {
      i /= 2;
    }
    mPSStream.println(i + " string /imStr exch def");
    mPSStream.println("[" + paramInt3 + " 0 0 " + paramInt4 + " " + paramInt1 + " " + paramInt2 + "]concat");
    mPSStream.println(paramInt3 + " " + paramInt4 + " " + 8 + "[" + paramInt3 + " 0 0 " + -paramInt4 + " 0 " + paramInt4 + "]/imageSrc load false 3 colorimage");
    int j = 0;
    byte[] arrayOfByte1 = new byte[paramInt3 * 3];
    try
    {
      for (int k = 0; k < paramInt4; k++)
      {
        j = swapBGRtoRGB(paramArrayOfByte, j, arrayOfByte1);
        byte[] arrayOfByte2 = rlEncode(arrayOfByte1);
        byte[] arrayOfByte3 = ascii85Encode(arrayOfByte2);
        mPSStream.write(arrayOfByte3);
        mPSStream.println("");
      }
    }
    catch (IOException localIOException)
    {
      throw new PrinterIOException(localIOException);
    }
    mPSStream.println("imSave restore");
  }
  
  protected Graphics2D createPathGraphics(PeekGraphics paramPeekGraphics, PrinterJob paramPrinterJob, Printable paramPrintable, PageFormat paramPageFormat, int paramInt)
  {
    PeekMetrics localPeekMetrics = paramPeekGraphics.getMetrics();
    PSPathGraphics localPSPathGraphics;
    if ((!forcePDL) && ((forceRaster == true) || (localPeekMetrics.hasNonSolidColors()) || (localPeekMetrics.hasCompositing())))
    {
      localPSPathGraphics = null;
    }
    else
    {
      BufferedImage localBufferedImage = new BufferedImage(8, 8, 1);
      Graphics2D localGraphics2D = localBufferedImage.createGraphics();
      boolean bool = !paramPeekGraphics.getAWTDrawingOnly();
      localPSPathGraphics = new PSPathGraphics(localGraphics2D, paramPrinterJob, paramPrintable, paramPageFormat, paramInt, bool);
    }
    return localPSPathGraphics;
  }
  
  protected void selectClipPath()
  {
    mPSStream.println(mClipOpStr);
  }
  
  protected void setClip(Shape paramShape)
  {
    mLastClip = paramShape;
  }
  
  protected void setTransform(AffineTransform paramAffineTransform)
  {
    mLastTransform = paramAffineTransform;
  }
  
  protected boolean setFont(Font paramFont)
  {
    mLastFont = paramFont;
    return true;
  }
  
  private int[] getPSFontIndexArray(Font paramFont, CharsetString[] paramArrayOfCharsetString)
  {
    int[] arrayOfInt = null;
    if (mFontProps != null) {
      arrayOfInt = new int[paramArrayOfCharsetString.length];
    }
    for (int i = 0; (i < paramArrayOfCharsetString.length) && (arrayOfInt != null); i++)
    {
      CharsetString localCharsetString = paramArrayOfCharsetString[i];
      CharsetEncoder localCharsetEncoder = fontDescriptor.encoder;
      String str1 = fontDescriptor.getFontCharsetName();
      if ("Symbol".equals(str1)) {
        str1 = "symbol";
      } else if (("WingDings".equals(str1)) || ("X11Dingbats".equals(str1))) {
        str1 = "dingbats";
      } else {
        str1 = makeCharsetName(str1, charsetChars);
      }
      int j = paramFont.getStyle() | FontUtilities.getFont2D(paramFont).getStyle();
      String str2 = FontConfiguration.getStyleString(j);
      String str3 = paramFont.getFamily().toLowerCase(Locale.ENGLISH);
      str3 = str3.replace(' ', '_');
      String str4 = mFontProps.getProperty(str3, "");
      String str5 = mFontProps.getProperty(str4 + "." + str1 + "." + str2, null);
      if (str5 != null) {
        try
        {
          arrayOfInt[i] = Integer.parseInt(mFontProps.getProperty(str5));
        }
        catch (NumberFormatException localNumberFormatException)
        {
          arrayOfInt = null;
        }
      } else {
        arrayOfInt = null;
      }
    }
    return arrayOfInt;
  }
  
  private static String escapeParens(String paramString)
  {
    if ((paramString.indexOf('(') == -1) && (paramString.indexOf(')') == -1)) {
      return paramString;
    }
    int i = 0;
    for (int j = 0; (j = paramString.indexOf('(', j)) != -1; j++) {
      i++;
    }
    for (j = 0; (j = paramString.indexOf(')', j)) != -1; j++) {
      i++;
    }
    char[] arrayOfChar1 = paramString.toCharArray();
    char[] arrayOfChar2 = new char[arrayOfChar1.length + i];
    j = 0;
    for (int k = 0; k < arrayOfChar1.length; k++)
    {
      if ((arrayOfChar1[k] == '(') || (arrayOfChar1[k] == ')')) {
        arrayOfChar2[(j++)] = '\\';
      }
      arrayOfChar2[(j++)] = arrayOfChar1[k];
    }
    return new String(arrayOfChar2);
  }
  
  protected int platformFontCount(Font paramFont, String paramString)
  {
    if (mFontProps == null) {
      return 0;
    }
    CharsetString[] arrayOfCharsetString = ((PlatformFont)paramFont.getPeer()).makeMultiCharsetString(paramString, false);
    if (arrayOfCharsetString == null) {
      return 0;
    }
    int[] arrayOfInt = getPSFontIndexArray(paramFont, arrayOfCharsetString);
    return arrayOfInt == null ? 0 : arrayOfInt.length;
  }
  
  protected boolean textOut(Graphics paramGraphics, String paramString, float paramFloat1, float paramFloat2, Font paramFont, FontRenderContext paramFontRenderContext, float paramFloat3)
  {
    boolean bool = true;
    if (mFontProps == null) {
      return false;
    }
    prepDrawing();
    paramString = removeControlChars(paramString);
    if (paramString.length() == 0) {
      return true;
    }
    CharsetString[] arrayOfCharsetString = ((PlatformFont)paramFont.getPeer()).makeMultiCharsetString(paramString, false);
    if (arrayOfCharsetString == null) {
      return false;
    }
    int[] arrayOfInt = getPSFontIndexArray(paramFont, arrayOfCharsetString);
    if (arrayOfInt != null) {
      for (int i = 0; i < arrayOfCharsetString.length; i++)
      {
        CharsetString localCharsetString = arrayOfCharsetString[i];
        CharsetEncoder localCharsetEncoder = fontDescriptor.encoder;
        StringBuffer localStringBuffer = new StringBuffer();
        byte[] arrayOfByte = new byte[length * 2];
        int j = 0;
        try
        {
          ByteBuffer localByteBuffer = ByteBuffer.wrap(arrayOfByte);
          localCharsetEncoder.encode(CharBuffer.wrap(charsetChars, offset, length), localByteBuffer, true);
          localByteBuffer.flip();
          j = localByteBuffer.limit();
        }
        catch (IllegalStateException localIllegalStateException)
        {
          continue;
        }
        catch (CoderMalfunctionError localCoderMalfunctionError)
        {
          continue;
        }
        float f;
        if ((arrayOfCharsetString.length == 1) && (paramFloat3 != 0.0F))
        {
          f = paramFloat3;
        }
        else
        {
          Rectangle2D localRectangle2D = paramFont.getStringBounds(charsetChars, offset, offset + length, paramFontRenderContext);
          f = (float)localRectangle2D.getWidth();
        }
        if (f == 0.0F) {
          return bool;
        }
        localStringBuffer.append('<');
        for (int k = 0; k < j; k++)
        {
          int m = arrayOfByte[k];
          String str = Integer.toHexString(m);
          int n = str.length();
          if (n > 2) {
            str = str.substring(n - 2, n);
          } else if (n == 1) {
            str = "0" + str;
          } else if (n == 0) {
            str = "00";
          }
          localStringBuffer.append(str);
        }
        localStringBuffer.append('>');
        getGState().emitPSFont(arrayOfInt[i], paramFont.getSize2D());
        mPSStream.println(localStringBuffer.toString() + " " + f + " " + paramFloat1 + " " + paramFloat2 + " " + "S");
        paramFloat1 += f;
      }
    } else {
      bool = false;
    }
    return bool;
  }
  
  protected void setFillMode(int paramInt)
  {
    switch (paramInt)
    {
    case 1: 
      mFillOpStr = "EF";
      mClipOpStr = "EC";
      break;
    case 2: 
      mFillOpStr = "WF";
      mClipOpStr = "WC";
      break;
    default: 
      throw new IllegalArgumentException();
    }
  }
  
  protected void setColor(Color paramColor)
  {
    mLastColor = paramColor;
  }
  
  protected void fillPath()
  {
    mPSStream.println(mFillOpStr);
  }
  
  protected void beginPath()
  {
    prepDrawing();
    mPSStream.println("N");
    mPenX = 0.0F;
    mPenY = 0.0F;
  }
  
  protected void closeSubpath()
  {
    mPSStream.println("P");
    mPenX = mStartPathX;
    mPenY = mStartPathY;
  }
  
  protected void moveTo(float paramFloat1, float paramFloat2)
  {
    mPSStream.println(trunc(paramFloat1) + " " + trunc(paramFloat2) + " M");
    mStartPathX = paramFloat1;
    mStartPathY = paramFloat2;
    mPenX = paramFloat1;
    mPenY = paramFloat2;
  }
  
  protected void lineTo(float paramFloat1, float paramFloat2)
  {
    mPSStream.println(trunc(paramFloat1) + " " + trunc(paramFloat2) + " L");
    mPenX = paramFloat1;
    mPenY = paramFloat2;
  }
  
  protected void bezierTo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
  {
    mPSStream.println(trunc(paramFloat1) + " " + trunc(paramFloat2) + " " + trunc(paramFloat3) + " " + trunc(paramFloat4) + " " + trunc(paramFloat5) + " " + trunc(paramFloat6) + " C");
    mPenX = paramFloat5;
    mPenY = paramFloat6;
  }
  
  String trunc(float paramFloat)
  {
    float f = Math.abs(paramFloat);
    if ((f >= 1.0F) && (f <= 1000.0F)) {
      paramFloat = Math.round(paramFloat * 1000.0F) / 1000.0F;
    }
    return Float.toString(paramFloat);
  }
  
  protected float getPenX()
  {
    return mPenX;
  }
  
  protected float getPenY()
  {
    return mPenY;
  }
  
  protected double getXRes()
  {
    return 300.0D;
  }
  
  protected double getYRes()
  {
    return 300.0D;
  }
  
  protected double getPhysicalPrintableX(Paper paramPaper)
  {
    return 0.0D;
  }
  
  protected double getPhysicalPrintableY(Paper paramPaper)
  {
    return 0.0D;
  }
  
  protected double getPhysicalPrintableWidth(Paper paramPaper)
  {
    return paramPaper.getImageableWidth();
  }
  
  protected double getPhysicalPrintableHeight(Paper paramPaper)
  {
    return paramPaper.getImageableHeight();
  }
  
  protected double getPhysicalPageWidth(Paper paramPaper)
  {
    return paramPaper.getWidth();
  }
  
  protected double getPhysicalPageHeight(Paper paramPaper)
  {
    return paramPaper.getHeight();
  }
  
  protected int getNoncollatedCopies()
  {
    return 1;
  }
  
  protected int getCollatedCopies()
  {
    return 1;
  }
  
  private String[] printExecCmd(String paramString1, String paramString2, boolean paramBoolean, String paramString3, int paramInt, String paramString4)
  {
    int i = 1;
    int j = 2;
    int k = 4;
    int m = 8;
    int n = 16;
    int i1 = 0;
    int i2 = 2;
    int i3 = 0;
    if ((paramString1 != null) && (!paramString1.equals("")) && (!paramString1.equals("lp")))
    {
      i1 |= i;
      i2++;
    }
    if ((paramString2 != null) && (!paramString2.equals("")))
    {
      i1 |= j;
      i2++;
    }
    if ((paramString3 != null) && (!paramString3.equals("")))
    {
      i1 |= k;
      i2++;
    }
    if (paramInt > 1)
    {
      i1 |= m;
      i2++;
    }
    if (paramBoolean)
    {
      i1 |= n;
      i2++;
    }
    String str = System.getProperty("os.name");
    String[] arrayOfString;
    if ((str.equals("Linux")) || (str.contains("OS X")))
    {
      arrayOfString = new String[i2];
      arrayOfString[(i3++)] = "/usr/bin/lpr";
      if ((i1 & i) != 0) {
        arrayOfString[(i3++)] = ("-P" + paramString1);
      }
      if ((i1 & k) != 0) {
        arrayOfString[(i3++)] = ("-J" + paramString3);
      }
      if ((i1 & m) != 0) {
        arrayOfString[(i3++)] = ("-#" + paramInt);
      }
      if ((i1 & n) != 0) {
        arrayOfString[(i3++)] = "-h";
      }
      if ((i1 & j) != 0) {
        arrayOfString[(i3++)] = new String(paramString2);
      }
    }
    else
    {
      i2++;
      arrayOfString = new String[i2];
      arrayOfString[(i3++)] = "/usr/bin/lp";
      arrayOfString[(i3++)] = "-c";
      if ((i1 & i) != 0) {
        arrayOfString[(i3++)] = ("-d" + paramString1);
      }
      if ((i1 & k) != 0) {
        arrayOfString[(i3++)] = ("-t" + paramString3);
      }
      if ((i1 & m) != 0) {
        arrayOfString[(i3++)] = ("-n" + paramInt);
      }
      if ((i1 & n) != 0) {
        arrayOfString[(i3++)] = "-o nobanner";
      }
      if ((i1 & j) != 0) {
        arrayOfString[(i3++)] = ("-o" + paramString2);
      }
    }
    arrayOfString[(i3++)] = paramString4;
    return arrayOfString;
  }
  
  private static int swapBGRtoRGB(byte[] paramArrayOfByte1, int paramInt, byte[] paramArrayOfByte2)
  {
    int i = 0;
    while ((paramInt < paramArrayOfByte1.length - 2) && (i < paramArrayOfByte2.length - 2))
    {
      paramArrayOfByte2[(i++)] = paramArrayOfByte1[(paramInt + 2)];
      paramArrayOfByte2[(i++)] = paramArrayOfByte1[(paramInt + 1)];
      paramArrayOfByte2[(i++)] = paramArrayOfByte1[(paramInt + 0)];
      paramInt += 3;
    }
    return paramInt;
  }
  
  private String makeCharsetName(String paramString, char[] paramArrayOfChar)
  {
    if ((paramString.equals("Cp1252")) || (paramString.equals("ISO8859_1"))) {
      return "latin1";
    }
    int i;
    if (paramString.equals("UTF8"))
    {
      for (i = 0; i < paramArrayOfChar.length; i++) {
        if (paramArrayOfChar[i] > 'ÿ') {
          return paramString.toLowerCase();
        }
      }
      return "latin1";
    }
    if (paramString.startsWith("ISO8859"))
    {
      for (i = 0; i < paramArrayOfChar.length; i++) {
        if (paramArrayOfChar[i] > '') {
          return paramString.toLowerCase();
        }
      }
      return "latin1";
    }
    return paramString.toLowerCase();
  }
  
  private void prepDrawing()
  {
    while ((!isOuterGState()) && ((!getGState().canSetClip(mLastClip)) || (!getGStatemTransform.equals(mLastTransform)))) {
      grestore();
    }
    getGState().emitPSColor(mLastColor);
    if (isOuterGState())
    {
      gsave();
      getGState().emitTransform(mLastTransform);
      getGState().emitPSClip(mLastClip);
    }
  }
  
  private GState getGState()
  {
    int i = mGStateStack.size();
    return (GState)mGStateStack.get(i - 1);
  }
  
  private void gsave()
  {
    GState localGState = getGState();
    mGStateStack.add(new GState(localGState));
    mPSStream.println("G");
  }
  
  private void grestore()
  {
    int i = mGStateStack.size();
    mGStateStack.remove(i - 1);
    mPSStream.println("R");
  }
  
  private boolean isOuterGState()
  {
    return mGStateStack.size() == 1;
  }
  
  void convertToPSPath(PathIterator paramPathIterator)
  {
    float[] arrayOfFloat = new float[6];
    int j;
    if (paramPathIterator.getWindingRule() == 0) {
      j = 1;
    } else {
      j = 2;
    }
    beginPath();
    setFillMode(j);
    while (!paramPathIterator.isDone())
    {
      int i = paramPathIterator.currentSegment(arrayOfFloat);
      switch (i)
      {
      case 0: 
        moveTo(arrayOfFloat[0], arrayOfFloat[1]);
        break;
      case 1: 
        lineTo(arrayOfFloat[0], arrayOfFloat[1]);
        break;
      case 2: 
        float f1 = getPenX();
        float f2 = getPenY();
        float f3 = f1 + (arrayOfFloat[0] - f1) * 2.0F / 3.0F;
        float f4 = f2 + (arrayOfFloat[1] - f2) * 2.0F / 3.0F;
        float f5 = arrayOfFloat[2] - (arrayOfFloat[2] - arrayOfFloat[0]) * 2.0F / 3.0F;
        float f6 = arrayOfFloat[3] - (arrayOfFloat[3] - arrayOfFloat[1]) * 2.0F / 3.0F;
        bezierTo(f3, f4, f5, f6, arrayOfFloat[2], arrayOfFloat[3]);
        break;
      case 3: 
        bezierTo(arrayOfFloat[0], arrayOfFloat[1], arrayOfFloat[2], arrayOfFloat[3], arrayOfFloat[4], arrayOfFloat[5]);
        break;
      case 4: 
        closeSubpath();
      }
      paramPathIterator.next();
    }
  }
  
  protected void deviceFill(PathIterator paramPathIterator, Color paramColor, AffineTransform paramAffineTransform, Shape paramShape)
  {
    setTransform(paramAffineTransform);
    setClip(paramShape);
    setColor(paramColor);
    convertToPSPath(paramPathIterator);
    mPSStream.println("G");
    selectClipPath();
    fillPath();
    mPSStream.println("R N");
  }
  
  private byte[] rlEncode(byte[] paramArrayOfByte)
  {
    int i = 0;
    int j = 0;
    int k = 0;
    int m = 0;
    byte[] arrayOfByte1 = new byte[paramArrayOfByte.length * 2 + 2];
    while (i < paramArrayOfByte.length)
    {
      if (m == 0)
      {
        k = i++;
        m = 1;
      }
      while ((m < 128) && (i < paramArrayOfByte.length) && (paramArrayOfByte[i] == paramArrayOfByte[k]))
      {
        m++;
        i++;
      }
      if (m > 1)
      {
        arrayOfByte1[(j++)] = ((byte)(257 - m));
        arrayOfByte1[(j++)] = paramArrayOfByte[k];
        m = 0;
      }
      else
      {
        while ((m < 128) && (i < paramArrayOfByte.length) && (paramArrayOfByte[i] != paramArrayOfByte[(i - 1)]))
        {
          m++;
          i++;
        }
        arrayOfByte1[(j++)] = ((byte)(m - 1));
        for (int n = k; n < k + m; n++) {
          arrayOfByte1[(j++)] = paramArrayOfByte[n];
        }
        m = 0;
      }
    }
    arrayOfByte1[(j++)] = Byte.MIN_VALUE;
    byte[] arrayOfByte2 = new byte[j];
    System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, j);
    return arrayOfByte2;
  }
  
  private byte[] ascii85Encode(byte[] paramArrayOfByte)
  {
    byte[] arrayOfByte1 = new byte[(paramArrayOfByte.length + 4) * 5 / 4 + 2];
    long l1 = 85L;
    long l2 = l1 * l1;
    long l3 = l1 * l2;
    long l4 = l1 * l3;
    int i = 33;
    int j = 0;
    int k = 0;
    long l5;
    long l6;
    while (j + 3 < paramArrayOfByte.length)
    {
      l5 = ((paramArrayOfByte[(j++)] & 0xFF) << 24) + ((paramArrayOfByte[(j++)] & 0xFF) << 16) + ((paramArrayOfByte[(j++)] & 0xFF) << 8) + (paramArrayOfByte[(j++)] & 0xFF);
      if (l5 == 0L)
      {
        arrayOfByte1[(k++)] = 122;
      }
      else
      {
        l6 = l5;
        arrayOfByte1[(k++)] = ((byte)(int)(l6 / l4 + i));
        l6 %= l4;
        arrayOfByte1[(k++)] = ((byte)(int)(l6 / l3 + i));
        l6 %= l3;
        arrayOfByte1[(k++)] = ((byte)(int)(l6 / l2 + i));
        l6 %= l2;
        arrayOfByte1[(k++)] = ((byte)(int)(l6 / l1 + i));
        l6 %= l1;
        arrayOfByte1[(k++)] = ((byte)(int)(l6 + i));
      }
    }
    if (j < paramArrayOfByte.length)
    {
      int m = paramArrayOfByte.length - j;
      for (l5 = 0L; j < paramArrayOfByte.length; l5 = (l5 << 8) + (paramArrayOfByte[(j++)] & 0xFF)) {}
      int n = 4 - m;
      while (n-- > 0) {
        l5 <<= 8;
      }
      byte[] arrayOfByte3 = new byte[5];
      l6 = l5;
      arrayOfByte3[0] = ((byte)(int)(l6 / l4 + i));
      l6 %= l4;
      arrayOfByte3[1] = ((byte)(int)(l6 / l3 + i));
      l6 %= l3;
      arrayOfByte3[2] = ((byte)(int)(l6 / l2 + i));
      l6 %= l2;
      arrayOfByte3[3] = ((byte)(int)(l6 / l1 + i));
      l6 %= l1;
      arrayOfByte3[4] = ((byte)(int)(l6 + i));
      for (int i1 = 0; i1 < m + 1; i1++) {
        arrayOfByte1[(k++)] = arrayOfByte3[i1];
      }
    }
    arrayOfByte1[(k++)] = 126;
    arrayOfByte1[(k++)] = 62;
    byte[] arrayOfByte2 = new byte[k];
    System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, k);
    return arrayOfByte2;
  }
  
  static
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        PSPrinterJob.access$002(PSPrinterJob.access$100());
        String str = System.getProperty("os.name");
        PSPrinterJob.access$202(str.startsWith("Mac"));
        return null;
      }
    });
  }
  
  public static class EPSPrinter
    implements Pageable
  {
    private PageFormat pf;
    private PSPrinterJob job;
    private int llx;
    private int lly;
    private int urx;
    private int ury;
    private Printable printable;
    private PrintStream stream;
    private String epsTitle;
    
    public EPSPrinter(Printable paramPrintable, String paramString, PrintStream paramPrintStream, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      printable = paramPrintable;
      epsTitle = paramString;
      stream = paramPrintStream;
      llx = paramInt1;
      lly = paramInt2;
      urx = (llx + paramInt3);
      ury = (lly + paramInt4);
      Paper localPaper = new Paper();
      localPaper.setSize(paramInt3, paramInt4);
      localPaper.setImageableArea(0.0D, 0.0D, paramInt3, paramInt4);
      pf = new PageFormat();
      pf.setPaper(localPaper);
    }
    
    public void print()
      throws PrinterException
    {
      stream.println("%!PS-Adobe-3.0 EPSF-3.0");
      stream.println("%%BoundingBox: " + llx + " " + lly + " " + urx + " " + ury);
      stream.println("%%Title: " + epsTitle);
      stream.println("%%Creator: Java Printing");
      stream.println("%%CreationDate: " + new Date());
      stream.println("%%EndComments");
      stream.println("/pluginSave save def");
      stream.println("mark");
      job = new PSPrinterJob();
      job.epsPrinter = this;
      job.mPSStream = stream;
      job.mDestType = 2;
      job.startDoc();
      try
      {
        job.printPage(this, 0);
      }
      catch (Throwable localThrowable)
      {
        if ((localThrowable instanceof PrinterException)) {
          throw ((PrinterException)localThrowable);
        }
        throw new PrinterException(localThrowable.toString());
      }
      finally
      {
        stream.println("cleartomark");
        stream.println("pluginSave restore");
        job.endDoc();
      }
      stream.flush();
    }
    
    public int getNumberOfPages()
    {
      return 1;
    }
    
    public PageFormat getPageFormat(int paramInt)
    {
      if (paramInt > 0) {
        throw new IndexOutOfBoundsException("pgIndex");
      }
      return pf;
    }
    
    public Printable getPrintable(int paramInt)
    {
      if (paramInt > 0) {
        throw new IndexOutOfBoundsException("pgIndex");
      }
      return printable;
    }
  }
  
  private class GState
  {
    Color mColor;
    Shape mClip;
    Font mFont;
    AffineTransform mTransform;
    
    GState()
    {
      mColor = Color.black;
      mClip = null;
      mFont = null;
      mTransform = new AffineTransform();
    }
    
    GState(GState paramGState)
    {
      mColor = mColor;
      mClip = mClip;
      mFont = mFont;
      mTransform = mTransform;
    }
    
    boolean canSetClip(Shape paramShape)
    {
      return (mClip == null) || (mClip.equals(paramShape));
    }
    
    void emitPSClip(Shape paramShape)
    {
      if ((paramShape != null) && ((mClip == null) || (!mClip.equals(paramShape))))
      {
        String str1 = mFillOpStr;
        String str2 = mClipOpStr;
        convertToPSPath(paramShape.getPathIterator(new AffineTransform()));
        selectClipPath();
        mClip = paramShape;
        mClipOpStr = str1;
        mFillOpStr = str1;
      }
    }
    
    void emitTransform(AffineTransform paramAffineTransform)
    {
      if ((paramAffineTransform != null) && (!paramAffineTransform.equals(mTransform)))
      {
        double[] arrayOfDouble = new double[6];
        paramAffineTransform.getMatrix(arrayOfDouble);
        mPSStream.println("[" + (float)arrayOfDouble[0] + " " + (float)arrayOfDouble[1] + " " + (float)arrayOfDouble[2] + " " + (float)arrayOfDouble[3] + " " + (float)arrayOfDouble[4] + " " + (float)arrayOfDouble[5] + "] concat");
        mTransform = paramAffineTransform;
      }
    }
    
    void emitPSColor(Color paramColor)
    {
      if ((paramColor != null) && (!paramColor.equals(mColor)))
      {
        float[] arrayOfFloat = paramColor.getRGBColorComponents(null);
        if ((arrayOfFloat[0] == arrayOfFloat[1]) && (arrayOfFloat[1] == arrayOfFloat[2])) {
          mPSStream.println(arrayOfFloat[0] + " SG");
        } else {
          mPSStream.println(arrayOfFloat[0] + " " + arrayOfFloat[1] + " " + arrayOfFloat[2] + " " + " SC");
        }
        mColor = paramColor;
      }
    }
    
    void emitPSFont(int paramInt, float paramFloat)
    {
      mPSStream.println(paramFloat + " " + paramInt + " " + "F");
    }
  }
  
  public static class PluginPrinter
    implements Printable
  {
    private PSPrinterJob.EPSPrinter epsPrinter;
    private Component applet;
    private PrintStream stream;
    private String epsTitle;
    private int bx;
    private int by;
    private int bw;
    private int bh;
    private int width;
    private int height;
    
    public PluginPrinter(Component paramComponent, PrintStream paramPrintStream, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      applet = paramComponent;
      epsTitle = "Java Plugin Applet";
      stream = paramPrintStream;
      bx = paramInt1;
      by = paramInt2;
      bw = paramInt3;
      bh = paramInt4;
      width = sizewidth;
      height = sizeheight;
      epsPrinter = new PSPrinterJob.EPSPrinter(this, epsTitle, paramPrintStream, 0, 0, width, height);
    }
    
    public void printPluginPSHeader()
    {
      stream.println("%%BeginDocument: JavaPluginApplet");
    }
    
    public void printPluginApplet()
    {
      try
      {
        epsPrinter.print();
      }
      catch (PrinterException localPrinterException) {}
    }
    
    public void printPluginPSTrailer()
    {
      stream.println("%%EndDocument: JavaPluginApplet");
      stream.flush();
    }
    
    public void printAll()
    {
      printPluginPSHeader();
      printPluginApplet();
      printPluginPSTrailer();
    }
    
    public int print(Graphics paramGraphics, PageFormat paramPageFormat, int paramInt)
    {
      if (paramInt > 0) {
        return 1;
      }
      applet.printAll(paramGraphics);
      return 0;
    }
  }
  
  private class PrinterOpener
    implements PrivilegedAction
  {
    PrinterException pex;
    OutputStream result;
    
    private PrinterOpener() {}
    
    public Object run()
    {
      try
      {
        spoolFile = Files.createTempFile("javaprint", ".ps", new FileAttribute[0]).toFile();
        spoolFile.deleteOnExit();
        result = new FileOutputStream(spoolFile);
        return result;
      }
      catch (IOException localIOException)
      {
        pex = new PrinterIOException(localIOException);
      }
      return null;
    }
  }
  
  private class PrinterSpooler
    implements PrivilegedAction
  {
    PrinterException pex;
    
    private PrinterSpooler() {}
    
    private void handleProcessFailure(Process paramProcess, String[] paramArrayOfString, int paramInt)
      throws IOException
    {
      StringWriter localStringWriter = new StringWriter();
      Object localObject1 = null;
      try
      {
        PrintWriter localPrintWriter = new PrintWriter(localStringWriter);
        Object localObject2 = null;
        try
        {
          localPrintWriter.append("error=").append(Integer.toString(paramInt));
          localPrintWriter.append(" running:");
          Object localObject5;
          for (localObject5 : paramArrayOfString) {
            localPrintWriter.append(" '").append((CharSequence)localObject5).append("'");
          }
          try
          {
            ??? = paramProcess.getErrorStream();
            Object localObject4 = null;
            try
            {
              InputStreamReader localInputStreamReader = new InputStreamReader((InputStream)???);
              localObject5 = null;
              try
              {
                BufferedReader localBufferedReader = new BufferedReader(localInputStreamReader);
                Object localObject6 = null;
                try
                {
                  while (localBufferedReader.ready())
                  {
                    localPrintWriter.println();
                    localPrintWriter.append("\t\t").append(localBufferedReader.readLine());
                  }
                }
                catch (Throwable localThrowable8)
                {
                  localObject6 = localThrowable8;
                  throw localThrowable8;
                }
                finally {}
              }
              catch (Throwable localThrowable6)
              {
                localObject5 = localThrowable6;
                throw localThrowable6;
              }
              finally
              {
                if (localInputStreamReader != null) {
                  if (localObject5 != null) {
                    try {}catch (Throwable localThrowable10)
                    {
                      ((Throwable)localObject5).addSuppressed(localThrowable10);
                    }
                  }
                }
              }
            }
            catch (Throwable localThrowable4)
            {
              localObject4 = localThrowable4;
              throw localThrowable4;
            }
            finally
            {
              if (??? != null) {
                if (localObject4 != null) {
                  try
                  {
                    ((InputStream)???).close();
                  }
                  catch (Throwable localThrowable11)
                  {
                    ((Throwable)localObject4).addSuppressed(localThrowable11);
                  }
                } else {
                  ((InputStream)???).close();
                }
              }
            }
            throw new IOException(localStringWriter.toString());
          }
          finally
          {
            localPrintWriter.flush();
          }
        }
        catch (Throwable localThrowable2)
        {
          localThrowable2 = localThrowable2;
          localObject2 = localThrowable2;
          throw localThrowable2;
        }
        finally
        {
          localObject11 = finally;
          if (localPrintWriter != null) {
            if (localObject2 != null) {
              try
              {
                localPrintWriter.close();
              }
              catch (Throwable localThrowable12)
              {
                ((Throwable)localObject2).addSuppressed(localThrowable12);
              }
            } else {
              localPrintWriter.close();
            }
          }
          throw ((Throwable)localObject11);
        }
      }
      catch (Throwable localThrowable1)
      {
        localThrowable1 = localThrowable1;
        localObject1 = localThrowable1;
        throw localThrowable1;
      }
      finally
      {
        localObject12 = finally;
        if (localStringWriter != null) {
          if (localObject1 != null) {
            try
            {
              localStringWriter.close();
            }
            catch (Throwable localThrowable13)
            {
              ((Throwable)localObject1).addSuppressed(localThrowable13);
            }
          } else {
            localStringWriter.close();
          }
        }
        throw ((Throwable)localObject12);
      }
    }
    
    public Object run()
    {
      if ((spoolFile == null) || (!spoolFile.exists()))
      {
        pex = new PrinterException("No spool file");
        return null;
      }
      try
      {
        String str = spoolFile.getAbsolutePath();
        String[] arrayOfString = PSPrinterJob.this.printExecCmd(mDestination, mOptions, mNoJobSheet, getJobNameInt(), 1, str);
        Process localProcess = Runtime.getRuntime().exec(arrayOfString);
        localProcess.waitFor();
        int i = localProcess.exitValue();
        if (0 != i) {
          handleProcessFailure(localProcess, arrayOfString, i);
        }
      }
      catch (IOException localIOException)
      {
        pex = new PrinterIOException(localIOException);
      }
      catch (InterruptedException localInterruptedException)
      {
        pex = new PrinterException(localInterruptedException.toString());
      }
      finally
      {
        spoolFile.delete();
      }
      return null;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\print\PSPrinterJob.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */