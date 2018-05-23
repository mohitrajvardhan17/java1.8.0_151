package sun.awt.windows;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.color.ColorSpace;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorTable;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import sun.awt.datatransfer.DataTransferer;
import sun.awt.datatransfer.ToolkitThreadBlockedHandler;
import sun.awt.image.ImageRepresentation;
import sun.awt.image.ToolkitImage;

final class WDataTransferer
  extends DataTransferer
{
  private static final String[] predefinedClipboardNames = { "", "TEXT", "BITMAP", "METAFILEPICT", "SYLK", "DIF", "TIFF", "OEM TEXT", "DIB", "PALETTE", "PENDATA", "RIFF", "WAVE", "UNICODE TEXT", "ENHMETAFILE", "HDROP", "LOCALE", "DIBV5" };
  private static final Map<String, Long> predefinedClipboardNameMap;
  public static final int CF_TEXT = 1;
  public static final int CF_METAFILEPICT = 3;
  public static final int CF_DIB = 8;
  public static final int CF_ENHMETAFILE = 14;
  public static final int CF_HDROP = 15;
  public static final int CF_LOCALE = 16;
  public static final long CF_HTML = registerClipboardFormat("HTML Format");
  public static final long CFSTR_INETURL = registerClipboardFormat("UniformResourceLocator");
  public static final long CF_PNG = registerClipboardFormat("PNG");
  public static final long CF_JFIF = registerClipboardFormat("JFIF");
  public static final long CF_FILEGROUPDESCRIPTORW = registerClipboardFormat("FileGroupDescriptorW");
  public static final long CF_FILEGROUPDESCRIPTORA = registerClipboardFormat("FileGroupDescriptor");
  private static final Long L_CF_LOCALE = (Long)predefinedClipboardNameMap.get(predefinedClipboardNames[16]);
  private static final DirectColorModel directColorModel = new DirectColorModel(24, 16711680, 65280, 255);
  private static final int[] bandmasks = { directColorModel.getRedMask(), directColorModel.getGreenMask(), directColorModel.getBlueMask() };
  private static WDataTransferer transferer;
  private final ToolkitThreadBlockedHandler handler = new WToolkitThreadBlockedHandler();
  private static final byte[] UNICODE_NULL_TERMINATOR = { 0, 0 };
  
  private WDataTransferer() {}
  
  static synchronized WDataTransferer getInstanceImpl()
  {
    if (transferer == null) {
      transferer = new WDataTransferer();
    }
    return transferer;
  }
  
  public SortedMap<Long, DataFlavor> getFormatsForFlavors(DataFlavor[] paramArrayOfDataFlavor, FlavorTable paramFlavorTable)
  {
    SortedMap localSortedMap = super.getFormatsForFlavors(paramArrayOfDataFlavor, paramFlavorTable);
    localSortedMap.remove(L_CF_LOCALE);
    return localSortedMap;
  }
  
  public String getDefaultUnicodeEncoding()
  {
    return "utf-16le";
  }
  
  public byte[] translateTransferable(Transferable paramTransferable, DataFlavor paramDataFlavor, long paramLong)
    throws IOException
  {
    byte[] arrayOfByte = null;
    if (paramLong == CF_HTML)
    {
      if (paramTransferable.isDataFlavorSupported(DataFlavor.selectionHtmlFlavor)) {
        arrayOfByte = super.translateTransferable(paramTransferable, DataFlavor.selectionHtmlFlavor, paramLong);
      } else if (paramTransferable.isDataFlavorSupported(DataFlavor.allHtmlFlavor)) {
        arrayOfByte = super.translateTransferable(paramTransferable, DataFlavor.allHtmlFlavor, paramLong);
      } else {
        arrayOfByte = HTMLCodec.convertToHTMLFormat(super.translateTransferable(paramTransferable, paramDataFlavor, paramLong));
      }
    }
    else {
      arrayOfByte = super.translateTransferable(paramTransferable, paramDataFlavor, paramLong);
    }
    return arrayOfByte;
  }
  
  public Object translateStream(InputStream paramInputStream, DataFlavor paramDataFlavor, long paramLong, Transferable paramTransferable)
    throws IOException
  {
    if ((paramLong == CF_HTML) && (paramDataFlavor.isFlavorTextType())) {
      paramInputStream = new HTMLCodec(paramInputStream, EHTMLReadMode.getEHTMLReadMode(paramDataFlavor));
    }
    return super.translateStream(paramInputStream, paramDataFlavor, paramLong, paramTransferable);
  }
  
  public Object translateBytes(byte[] paramArrayOfByte, DataFlavor paramDataFlavor, long paramLong, Transferable paramTransferable)
    throws IOException
  {
    String str;
    if ((paramLong == CF_FILEGROUPDESCRIPTORA) || (paramLong == CF_FILEGROUPDESCRIPTORW))
    {
      if ((paramArrayOfByte == null) || (!DataFlavor.javaFileListFlavor.equals(paramDataFlavor))) {
        throw new IOException("data translation failed");
      }
      str = new String(paramArrayOfByte, 0, paramArrayOfByte.length, "UTF-16LE");
      String[] arrayOfString = str.split("\000");
      if (0 == arrayOfString.length) {
        return null;
      }
      File[] arrayOfFile = new File[arrayOfString.length];
      for (int i = 0; i < arrayOfString.length; i++)
      {
        arrayOfFile[i] = new File(arrayOfString[i]);
        arrayOfFile[i].deleteOnExit();
      }
      return Arrays.asList(arrayOfFile);
    }
    if ((paramLong == CFSTR_INETURL) && (URL.class.equals(paramDataFlavor.getRepresentationClass())))
    {
      str = getDefaultTextCharset();
      if ((paramTransferable != null) && (paramTransferable.isDataFlavorSupported(javaTextEncodingFlavor))) {
        try
        {
          str = new String((byte[])paramTransferable.getTransferData(javaTextEncodingFlavor), "UTF-8");
        }
        catch (UnsupportedFlavorException localUnsupportedFlavorException) {}
      }
      return new URL(new String(paramArrayOfByte, str));
    }
    return super.translateBytes(paramArrayOfByte, paramDataFlavor, paramLong, paramTransferable);
  }
  
  public boolean isLocaleDependentTextFormat(long paramLong)
  {
    return (paramLong == 1L) || (paramLong == CFSTR_INETURL);
  }
  
  public boolean isFileFormat(long paramLong)
  {
    return (paramLong == 15L) || (paramLong == CF_FILEGROUPDESCRIPTORA) || (paramLong == CF_FILEGROUPDESCRIPTORW);
  }
  
  protected Long getFormatForNativeAsLong(String paramString)
  {
    Long localLong = (Long)predefinedClipboardNameMap.get(paramString);
    if (localLong == null) {
      localLong = Long.valueOf(registerClipboardFormat(paramString));
    }
    return localLong;
  }
  
  protected String getNativeForFormat(long paramLong)
  {
    return paramLong < predefinedClipboardNames.length ? predefinedClipboardNames[((int)paramLong)] : getClipboardFormatName(paramLong);
  }
  
  public ToolkitThreadBlockedHandler getToolkitThreadBlockedHandler()
  {
    return handler;
  }
  
  private static native long registerClipboardFormat(String paramString);
  
  private static native String getClipboardFormatName(long paramLong);
  
  public boolean isImageFormat(long paramLong)
  {
    return (paramLong == 8L) || (paramLong == 14L) || (paramLong == 3L) || (paramLong == CF_PNG) || (paramLong == CF_JFIF);
  }
  
  protected byte[] imageToPlatformBytes(Image paramImage, long paramLong)
    throws IOException
  {
    String str = null;
    if (paramLong == CF_PNG) {
      str = "image/png";
    } else if (paramLong == CF_JFIF) {
      str = "image/jpeg";
    }
    if (str != null) {
      return imageToStandardBytes(paramImage, str);
    }
    int i = 0;
    int j = 0;
    if ((paramImage instanceof ToolkitImage))
    {
      ImageRepresentation localImageRepresentation = ((ToolkitImage)paramImage).getImageRep();
      localImageRepresentation.reconstruct(32);
      i = localImageRepresentation.getWidth();
      j = localImageRepresentation.getHeight();
    }
    else
    {
      i = paramImage.getWidth(null);
      j = paramImage.getHeight(null);
    }
    int k = i * 3 % 4;
    int m = k > 0 ? 4 - k : 0;
    ColorSpace localColorSpace = ColorSpace.getInstance(1000);
    int[] arrayOfInt1 = { 8, 8, 8 };
    int[] arrayOfInt2 = { 2, 1, 0 };
    ComponentColorModel localComponentColorModel = new ComponentColorModel(localColorSpace, arrayOfInt1, false, false, 1, 0);
    WritableRaster localWritableRaster = Raster.createInterleavedRaster(0, i, j, i * 3 + m, 3, arrayOfInt2, null);
    BufferedImage localBufferedImage = new BufferedImage(localComponentColorModel, localWritableRaster, false, null);
    AffineTransform localAffineTransform = new AffineTransform(1.0F, 0.0F, 0.0F, -1.0F, 0.0F, j);
    Graphics2D localGraphics2D = localBufferedImage.createGraphics();
    try
    {
      localGraphics2D.drawImage(paramImage, localAffineTransform, null);
    }
    finally
    {
      localGraphics2D.dispose();
    }
    DataBufferByte localDataBufferByte = (DataBufferByte)localWritableRaster.getDataBuffer();
    byte[] arrayOfByte = localDataBufferByte.getData();
    return imageDataToPlatformImageBytes(arrayOfByte, i, j, paramLong);
  }
  
  protected ByteArrayOutputStream convertFileListToBytes(ArrayList<String> paramArrayList)
    throws IOException
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    if (paramArrayList.isEmpty()) {
      localByteArrayOutputStream.write(UNICODE_NULL_TERMINATOR);
    } else {
      for (int i = 0; i < paramArrayList.size(); i++)
      {
        byte[] arrayOfByte = ((String)paramArrayList.get(i)).getBytes(getDefaultUnicodeEncoding());
        localByteArrayOutputStream.write(arrayOfByte, 0, arrayOfByte.length);
        localByteArrayOutputStream.write(UNICODE_NULL_TERMINATOR);
      }
    }
    localByteArrayOutputStream.write(UNICODE_NULL_TERMINATOR);
    return localByteArrayOutputStream;
  }
  
  private native byte[] imageDataToPlatformImageBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2, long paramLong);
  
  protected Image platformImageBytesToImage(byte[] paramArrayOfByte, long paramLong)
    throws IOException
  {
    String str = null;
    if (paramLong == CF_PNG) {
      str = "image/png";
    } else if (paramLong == CF_JFIF) {
      str = "image/jpeg";
    }
    if (str != null) {
      return standardImageBytesToImage(paramArrayOfByte, str);
    }
    int[] arrayOfInt = platformImageBytesToImageData(paramArrayOfByte, paramLong);
    if (arrayOfInt == null) {
      throw new IOException("data translation failed");
    }
    int i = arrayOfInt.length - 2;
    int j = arrayOfInt[i];
    int k = arrayOfInt[(i + 1)];
    DataBufferInt localDataBufferInt = new DataBufferInt(arrayOfInt, i);
    WritableRaster localWritableRaster = Raster.createPackedRaster(localDataBufferInt, j, k, j, bandmasks, null);
    return new BufferedImage(directColorModel, localWritableRaster, false, null);
  }
  
  private native int[] platformImageBytesToImageData(byte[] paramArrayOfByte, long paramLong)
    throws IOException;
  
  protected native String[] dragQueryFile(byte[] paramArrayOfByte);
  
  static
  {
    HashMap localHashMap = new HashMap(predefinedClipboardNames.length, 1.0F);
    for (int i = 1; i < predefinedClipboardNames.length; i++) {
      localHashMap.put(predefinedClipboardNames[i], Long.valueOf(i));
    }
    predefinedClipboardNameMap = Collections.synchronizedMap(localHashMap);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\windows\WDataTransferer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */