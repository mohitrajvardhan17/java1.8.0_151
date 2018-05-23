package com.sun.imageio.plugins.jpeg;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.color.CMMException;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.PrintStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.imageio.IIOException;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.jpeg.JPEGHuffmanTable;
import javax.imageio.plugins.jpeg.JPEGImageReadParam;
import javax.imageio.plugins.jpeg.JPEGQTable;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;

public class JPEGImageReader
  extends ImageReader
{
  private boolean debug = false;
  private long structPointer = 0L;
  private ImageInputStream iis = null;
  private List imagePositions = null;
  private int numImages = 0;
  protected static final int WARNING_NO_EOI = 0;
  protected static final int WARNING_NO_JFIF_IN_THUMB = 1;
  protected static final int WARNING_IGNORE_INVALID_ICC = 2;
  private static final int MAX_WARNING = 2;
  private int currentImage = -1;
  private int width;
  private int height;
  private int colorSpaceCode;
  private int outColorSpaceCode;
  private int numComponents;
  private ColorSpace iccCS = null;
  private ColorConvertOp convert = null;
  private BufferedImage image = null;
  private WritableRaster raster = null;
  private WritableRaster target = null;
  private DataBufferByte buffer = null;
  private Rectangle destROI = null;
  private int[] destinationBands = null;
  private JPEGMetadata streamMetadata = null;
  private JPEGMetadata imageMetadata = null;
  private int imageMetadataIndex = -1;
  private boolean haveSeeked = false;
  private JPEGQTable[] abbrevQTables = null;
  private JPEGHuffmanTable[] abbrevDCHuffmanTables = null;
  private JPEGHuffmanTable[] abbrevACHuffmanTables = null;
  private int minProgressivePass = 0;
  private int maxProgressivePass = Integer.MAX_VALUE;
  private static final int UNKNOWN = -1;
  private static final int MIN_ESTIMATED_PASSES = 10;
  private int knownPassCount = -1;
  private int pass = 0;
  private float percentToDate = 0.0F;
  private float previousPassPercentage = 0.0F;
  private int progInterval = 0;
  private boolean tablesOnlyChecked = false;
  private Object disposerReferent = new Object();
  private DisposerRecord disposerRecord = new JPEGReaderDisposerRecord(structPointer);
  private Thread theThread = null;
  private int theLockCount = 0;
  private CallBackLock cbLock = new CallBackLock();
  
  private static native void initReaderIDs(Class paramClass1, Class paramClass2, Class paramClass3);
  
  public JPEGImageReader(ImageReaderSpi paramImageReaderSpi)
  {
    super(paramImageReaderSpi);
    Disposer.addRecord(disposerReferent, disposerRecord);
  }
  
  private native long initJPEGImageReader();
  
  /* Error */
  protected void warningOccurred(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 755	com/sun/imageio/plugins/jpeg/JPEGImageReader:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;
    //   4: invokestatic 842	com/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock:access$000	(Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;)V
    //   7: iload_1
    //   8: iflt +8 -> 16
    //   11: iload_1
    //   12: iconst_2
    //   13: if_icmple +13 -> 26
    //   16: new 437	java/lang/InternalError
    //   19: dup
    //   20: ldc 17
    //   22: invokespecial 881	java/lang/InternalError:<init>	(Ljava/lang/String;)V
    //   25: athrow
    //   26: aload_0
    //   27: ldc 20
    //   29: iload_1
    //   30: invokestatic 880	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   33: invokevirtual 832	com/sun/imageio/plugins/jpeg/JPEGImageReader:processWarningOccurred	(Ljava/lang/String;Ljava/lang/String;)V
    //   36: aload_0
    //   37: getfield 755	com/sun/imageio/plugins/jpeg/JPEGImageReader:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;
    //   40: invokestatic 843	com/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock:access$100	(Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;)V
    //   43: goto +13 -> 56
    //   46: astore_2
    //   47: aload_0
    //   48: getfield 755	com/sun/imageio/plugins/jpeg/JPEGImageReader:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;
    //   51: invokestatic 843	com/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock:access$100	(Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;)V
    //   54: aload_2
    //   55: athrow
    //   56: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	57	0	this	JPEGImageReader
    //   0	57	1	paramInt	int
    //   46	9	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   7	36	46	finally
  }
  
  /* Error */
  protected void warningWithMessage(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 755	com/sun/imageio/plugins/jpeg/JPEGImageReader:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;
    //   4: invokestatic 842	com/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock:access$000	(Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;)V
    //   7: aload_0
    //   8: aload_1
    //   9: invokevirtual 826	com/sun/imageio/plugins/jpeg/JPEGImageReader:processWarningOccurred	(Ljava/lang/String;)V
    //   12: aload_0
    //   13: getfield 755	com/sun/imageio/plugins/jpeg/JPEGImageReader:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;
    //   16: invokestatic 843	com/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock:access$100	(Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;)V
    //   19: goto +13 -> 32
    //   22: astore_2
    //   23: aload_0
    //   24: getfield 755	com/sun/imageio/plugins/jpeg/JPEGImageReader:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;
    //   27: invokestatic 843	com/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock:access$100	(Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;)V
    //   30: aload_2
    //   31: athrow
    //   32: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	33	0	this	JPEGImageReader
    //   0	33	1	paramString	String
    //   22	9	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   7	12	22	finally
  }
  
  public void setInput(Object paramObject, boolean paramBoolean1, boolean paramBoolean2)
  {
    setThreadLock();
    try
    {
      cbLock.check();
      super.setInput(paramObject, paramBoolean1, paramBoolean2);
      ignoreMetadata = paramBoolean2;
      resetInternalState();
      iis = ((ImageInputStream)paramObject);
      setSource(structPointer);
    }
    finally
    {
      clearThreadLock();
    }
  }
  
  private int readInputData(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    cbLock.lock();
    try
    {
      int i = iis.read(paramArrayOfByte, paramInt1, paramInt2);
      return i;
    }
    finally
    {
      cbLock.unlock();
    }
  }
  
  private long skipInputBytes(long paramLong)
    throws IOException
  {
    cbLock.lock();
    try
    {
      long l = iis.skipBytes(paramLong);
      return l;
    }
    finally
    {
      cbLock.unlock();
    }
  }
  
  private native void setSource(long paramLong);
  
  private void checkTablesOnly()
    throws IOException
  {
    if (debug) {
      System.out.println("Checking for tables-only image");
    }
    long l1 = iis.getStreamPosition();
    if (debug)
    {
      System.out.println("saved pos is " + l1);
      System.out.println("length is " + iis.length());
    }
    boolean bool = readNativeHeader(true);
    if (bool)
    {
      long l2;
      if (debug)
      {
        System.out.println("tables-only image found");
        l2 = iis.getStreamPosition();
        System.out.println("pos after return from native is " + l2);
      }
      if (!ignoreMetadata)
      {
        iis.seek(l1);
        haveSeeked = true;
        streamMetadata = new JPEGMetadata(true, false, iis, this);
        l2 = iis.getStreamPosition();
        if (debug) {
          System.out.println("pos after constructing stream metadata is " + l2);
        }
      }
      if (hasNextImage()) {
        imagePositions.add(new Long(iis.getStreamPosition()));
      }
    }
    else
    {
      imagePositions.add(new Long(l1));
      currentImage = 0;
    }
    if (seekForwardOnly)
    {
      Long localLong = (Long)imagePositions.get(imagePositions.size() - 1);
      iis.flushBefore(localLong.longValue());
    }
    tablesOnlyChecked = true;
  }
  
  public int getNumImages(boolean paramBoolean)
    throws IOException
  {
    setThreadLock();
    try
    {
      cbLock.check();
      int i = getNumImagesOnThread(paramBoolean);
      return i;
    }
    finally
    {
      clearThreadLock();
    }
  }
  
  /* Error */
  private void skipPastImage(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 755	com/sun/imageio/plugins/jpeg/JPEGImageReader:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;
    //   4: invokestatic 842	com/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock:access$000	(Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;)V
    //   7: aload_0
    //   8: iload_1
    //   9: invokespecial 808	com/sun/imageio/plugins/jpeg/JPEGImageReader:gotoImage	(I)V
    //   12: aload_0
    //   13: invokespecial 803	com/sun/imageio/plugins/jpeg/JPEGImageReader:skipImage	()V
    //   16: aload_0
    //   17: getfield 755	com/sun/imageio/plugins/jpeg/JPEGImageReader:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;
    //   20: invokestatic 843	com/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock:access$100	(Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;)V
    //   23: goto +24 -> 47
    //   26: astore_2
    //   27: aload_0
    //   28: getfield 755	com/sun/imageio/plugins/jpeg/JPEGImageReader:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;
    //   31: invokestatic 843	com/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock:access$100	(Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;)V
    //   34: goto +13 -> 47
    //   37: astore_3
    //   38: aload_0
    //   39: getfield 755	com/sun/imageio/plugins/jpeg/JPEGImageReader:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;
    //   42: invokestatic 843	com/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock:access$100	(Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;)V
    //   45: aload_3
    //   46: athrow
    //   47: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	48	0	this	JPEGImageReader
    //   0	48	1	paramInt	int
    //   26	1	2	localIOException	IOException
    //   37	9	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   7	16	26	java/io/IOException
    //   7	16	26	java/lang/IndexOutOfBoundsException
    //   7	16	37	finally
  }
  
  private int getNumImagesOnThread(boolean paramBoolean)
    throws IOException
  {
    if (numImages != 0) {
      return numImages;
    }
    if (iis == null) {
      throw new IllegalStateException("Input not set");
    }
    if (paramBoolean == true)
    {
      if (seekForwardOnly) {
        throw new IllegalStateException("seekForwardOnly and allowSearch can't both be true!");
      }
      if (!tablesOnlyChecked) {
        checkTablesOnly();
      }
      iis.mark();
      gotoImage(0);
      JPEGBuffer localJPEGBuffer = new JPEGBuffer(iis);
      localJPEGBuffer.loadBuf(0);
      boolean bool = false;
      while (!bool)
      {
        bool = localJPEGBuffer.scanForFF(this);
        switch (buf[bufPtr] & 0xFF)
        {
        case 216: 
          numImages += 1;
        case 0: 
        case 208: 
        case 209: 
        case 210: 
        case 211: 
        case 212: 
        case 213: 
        case 214: 
        case 215: 
        case 217: 
          bufAvail -= 1;
          bufPtr += 1;
          break;
        default: 
          bufAvail -= 1;
          bufPtr += 1;
          localJPEGBuffer.loadBuf(2);
          int i = (buf[(bufPtr++)] & 0xFF) << 8 | buf[(bufPtr++)] & 0xFF;
          bufAvail -= 2;
          i -= 2;
          localJPEGBuffer.skipData(i);
        }
      }
      iis.reset();
      return numImages;
    }
    return -1;
  }
  
  private void gotoImage(int paramInt)
    throws IOException
  {
    if (iis == null) {
      throw new IllegalStateException("Input not set");
    }
    if (paramInt < minIndex) {
      throw new IndexOutOfBoundsException();
    }
    if (!tablesOnlyChecked) {
      checkTablesOnly();
    }
    if (paramInt < imagePositions.size())
    {
      iis.seek(((Long)imagePositions.get(paramInt)).longValue());
    }
    else
    {
      Long localLong = (Long)imagePositions.get(imagePositions.size() - 1);
      iis.seek(localLong.longValue());
      skipImage();
      for (int i = imagePositions.size(); i <= paramInt; i++)
      {
        if (!hasNextImage()) {
          throw new IndexOutOfBoundsException();
        }
        localLong = new Long(iis.getStreamPosition());
        imagePositions.add(localLong);
        if (seekForwardOnly) {
          iis.flushBefore(localLong.longValue());
        }
        if (i < paramInt) {
          skipImage();
        }
      }
    }
    if (seekForwardOnly) {
      minIndex = paramInt;
    }
    haveSeeked = true;
  }
  
  private void skipImage()
    throws IOException
  {
    if (debug) {
      System.out.println("skipImage called");
    }
    int i = 0;
    for (int j = iis.read(); j != -1; j = iis.read())
    {
      if ((i == 1) && (j == 217)) {
        return;
      }
      i = j == 255 ? 1 : 0;
    }
    throw new IndexOutOfBoundsException();
  }
  
  private boolean hasNextImage()
    throws IOException
  {
    if (debug) {
      System.out.print("hasNextImage called; returning ");
    }
    iis.mark();
    int i = 0;
    for (int j = iis.read(); j != -1; j = iis.read())
    {
      if ((i == 1) && (j == 216))
      {
        iis.reset();
        if (debug) {
          System.out.println("true");
        }
        return true;
      }
      i = j == 255 ? 1 : 0;
    }
    iis.reset();
    if (debug) {
      System.out.println("false");
    }
    return false;
  }
  
  /* Error */
  private void pushBack(int paramInt)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 749	com/sun/imageio/plugins/jpeg/JPEGImageReader:debug	Z
    //   4: ifeq +33 -> 37
    //   7: getstatic 782	java/lang/System:out	Ljava/io/PrintStream;
    //   10: new 442	java/lang/StringBuilder
    //   13: dup
    //   14: invokespecial 886	java/lang/StringBuilder:<init>	()V
    //   17: ldc 34
    //   19: invokevirtual 893	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   22: iload_1
    //   23: invokevirtual 889	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   26: ldc 4
    //   28: invokevirtual 893	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   31: invokevirtual 887	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   34: invokevirtual 876	java/io/PrintStream:println	(Ljava/lang/String;)V
    //   37: aload_0
    //   38: getfield 755	com/sun/imageio/plugins/jpeg/JPEGImageReader:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;
    //   41: invokestatic 842	com/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock:access$000	(Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;)V
    //   44: aload_0
    //   45: getfield 773	com/sun/imageio/plugins/jpeg/JPEGImageReader:iis	Ljavax/imageio/stream/ImageInputStream;
    //   48: aload_0
    //   49: getfield 773	com/sun/imageio/plugins/jpeg/JPEGImageReader:iis	Ljavax/imageio/stream/ImageInputStream;
    //   52: invokeinterface 928 1 0
    //   57: iload_1
    //   58: i2l
    //   59: lsub
    //   60: invokeinterface 934 3 0
    //   65: aload_0
    //   66: getfield 755	com/sun/imageio/plugins/jpeg/JPEGImageReader:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;
    //   69: invokestatic 843	com/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock:access$100	(Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;)V
    //   72: goto +13 -> 85
    //   75: astore_2
    //   76: aload_0
    //   77: getfield 755	com/sun/imageio/plugins/jpeg/JPEGImageReader:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;
    //   80: invokestatic 843	com/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock:access$100	(Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;)V
    //   83: aload_2
    //   84: athrow
    //   85: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	86	0	this	JPEGImageReader
    //   0	86	1	paramInt	int
    //   75	9	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   44	65	75	finally
  }
  
  private void readHeader(int paramInt, boolean paramBoolean)
    throws IOException
  {
    gotoImage(paramInt);
    readNativeHeader(paramBoolean);
    currentImage = paramInt;
  }
  
  private boolean readNativeHeader(boolean paramBoolean)
    throws IOException
  {
    boolean bool = false;
    bool = readImageHeader(structPointer, haveSeeked, paramBoolean);
    haveSeeked = false;
    return bool;
  }
  
  private native boolean readImageHeader(long paramLong, boolean paramBoolean1, boolean paramBoolean2)
    throws IOException;
  
  private void setImageData(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, byte[] paramArrayOfByte)
  {
    width = paramInt1;
    height = paramInt2;
    colorSpaceCode = paramInt3;
    outColorSpaceCode = paramInt4;
    numComponents = paramInt5;
    if (paramArrayOfByte == null)
    {
      iccCS = null;
      return;
    }
    ICC_Profile localICC_Profile1 = null;
    try
    {
      localICC_Profile1 = ICC_Profile.getInstance(paramArrayOfByte);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      iccCS = null;
      warningOccurred(2);
      return;
    }
    byte[] arrayOfByte1 = localICC_Profile1.getData();
    ICC_Profile localICC_Profile2 = null;
    if ((iccCS instanceof ICC_ColorSpace)) {
      localICC_Profile2 = ((ICC_ColorSpace)iccCS).getProfile();
    }
    byte[] arrayOfByte2 = null;
    if (localICC_Profile2 != null) {
      arrayOfByte2 = localICC_Profile2.getData();
    }
    if ((arrayOfByte2 == null) || (!Arrays.equals(arrayOfByte2, arrayOfByte1)))
    {
      iccCS = new ICC_ColorSpace(localICC_Profile1);
      try
      {
        float[] arrayOfFloat = iccCS.fromRGB(new float[] { 1.0F, 0.0F, 0.0F });
      }
      catch (CMMException localCMMException)
      {
        iccCS = null;
        cbLock.lock();
        try
        {
          warningOccurred(2);
        }
        finally
        {
          cbLock.unlock();
        }
      }
    }
  }
  
  public int getWidth(int paramInt)
    throws IOException
  {
    setThreadLock();
    try
    {
      if (currentImage != paramInt)
      {
        cbLock.check();
        readHeader(paramInt, true);
      }
      int i = width;
      return i;
    }
    finally
    {
      clearThreadLock();
    }
  }
  
  public int getHeight(int paramInt)
    throws IOException
  {
    setThreadLock();
    try
    {
      if (currentImage != paramInt)
      {
        cbLock.check();
        readHeader(paramInt, true);
      }
      int i = height;
      return i;
    }
    finally
    {
      clearThreadLock();
    }
  }
  
  private ImageTypeProducer getImageType(int paramInt)
  {
    ImageTypeProducer localImageTypeProducer = null;
    if ((paramInt > 0) && (paramInt < 12)) {
      localImageTypeProducer = ImageTypeProducer.getTypeProducer(paramInt);
    }
    return localImageTypeProducer;
  }
  
  public ImageTypeSpecifier getRawImageType(int paramInt)
    throws IOException
  {
    setThreadLock();
    try
    {
      if (currentImage != paramInt)
      {
        cbLock.check();
        readHeader(paramInt, true);
      }
      ImageTypeSpecifier localImageTypeSpecifier = getImageType(colorSpaceCode).getType();
      return localImageTypeSpecifier;
    }
    finally
    {
      clearThreadLock();
    }
  }
  
  public Iterator getImageTypes(int paramInt)
    throws IOException
  {
    setThreadLock();
    try
    {
      Iterator localIterator = getImageTypesOnThread(paramInt);
      return localIterator;
    }
    finally
    {
      clearThreadLock();
    }
  }
  
  private Iterator getImageTypesOnThread(int paramInt)
    throws IOException
  {
    if (currentImage != paramInt)
    {
      cbLock.check();
      readHeader(paramInt, true);
    }
    ImageTypeProducer localImageTypeProducer = getImageType(colorSpaceCode);
    ArrayList localArrayList = new ArrayList(1);
    switch (colorSpaceCode)
    {
    case 1: 
      localArrayList.add(localImageTypeProducer);
      localArrayList.add(getImageType(2));
      break;
    case 2: 
      localArrayList.add(localImageTypeProducer);
      localArrayList.add(getImageType(1));
      localArrayList.add(getImageType(5));
      break;
    case 6: 
      localArrayList.add(localImageTypeProducer);
      break;
    case 5: 
      if (localImageTypeProducer != null)
      {
        localArrayList.add(localImageTypeProducer);
        localArrayList.add(getImageType(2));
      }
      break;
    case 10: 
      if (localImageTypeProducer != null) {
        localArrayList.add(localImageTypeProducer);
      }
      break;
    case 3: 
      localArrayList.add(getImageType(2));
      if (iccCS != null) {
        localArrayList.add(new ImageTypeProducer()
        {
          protected ImageTypeSpecifier produce()
          {
            return ImageTypeSpecifier.createInterleaved(iccCS, JPEG.bOffsRGB, 0, false, false);
          }
        });
      }
      localArrayList.add(getImageType(1));
      localArrayList.add(getImageType(5));
      break;
    case 7: 
      localArrayList.add(getImageType(6));
    }
    return new ImageTypeIterator(localArrayList.iterator());
  }
  
  private void checkColorConversion(BufferedImage paramBufferedImage, ImageReadParam paramImageReadParam)
    throws IIOException
  {
    if ((paramImageReadParam != null) && ((paramImageReadParam.getSourceBands() != null) || (paramImageReadParam.getDestinationBands() != null))) {
      return;
    }
    ColorModel localColorModel = paramBufferedImage.getColorModel();
    if ((localColorModel instanceof IndexColorModel)) {
      throw new IIOException("IndexColorModel not supported");
    }
    ColorSpace localColorSpace1 = localColorModel.getColorSpace();
    int i = localColorSpace1.getType();
    convert = null;
    ColorSpace localColorSpace2;
    switch (outColorSpaceCode)
    {
    case 1: 
      if (i == 5)
      {
        setOutColorSpace(structPointer, 2);
        outColorSpaceCode = 2;
        numComponents = 3;
      }
      else if (i != 6)
      {
        throw new IIOException("Incompatible color conversion");
      }
      break;
    case 2: 
      if (i == 6)
      {
        if (colorSpaceCode == 3)
        {
          setOutColorSpace(structPointer, 1);
          outColorSpaceCode = 1;
          numComponents = 1;
        }
      }
      else if ((iccCS != null) && (localColorModel.getNumComponents() == numComponents) && (localColorSpace1 != iccCS)) {
        convert = new ColorConvertOp(iccCS, localColorSpace1, null);
      } else if ((iccCS == null) && (!localColorSpace1.isCS_sRGB()) && (localColorModel.getNumComponents() == numComponents)) {
        convert = new ColorConvertOp(JPEG.JCS.sRGB, localColorSpace1, null);
      } else if (i != 5) {
        throw new IIOException("Incompatible color conversion");
      }
      break;
    case 6: 
      if ((i != 5) || (localColorModel.getNumComponents() != numComponents)) {
        throw new IIOException("Incompatible color conversion");
      }
      break;
    case 5: 
      localColorSpace2 = JPEG.JCS.getYCC();
      if (localColorSpace2 == null) {
        throw new IIOException("Incompatible color conversion");
      }
      if ((localColorSpace1 != localColorSpace2) && (localColorModel.getNumComponents() == numComponents)) {
        convert = new ColorConvertOp(localColorSpace2, localColorSpace1, null);
      }
      break;
    case 10: 
      localColorSpace2 = JPEG.JCS.getYCC();
      if ((localColorSpace2 == null) || (localColorSpace1 != localColorSpace2) || (localColorModel.getNumComponents() != numComponents)) {
        throw new IIOException("Incompatible color conversion");
      }
      break;
    case 3: 
    case 4: 
    case 7: 
    case 8: 
    case 9: 
    default: 
      throw new IIOException("Incompatible color conversion");
    }
  }
  
  private native void setOutColorSpace(long paramLong, int paramInt);
  
  public ImageReadParam getDefaultReadParam()
  {
    return new JPEGImageReadParam();
  }
  
  public IIOMetadata getStreamMetadata()
    throws IOException
  {
    setThreadLock();
    try
    {
      if (!tablesOnlyChecked)
      {
        cbLock.check();
        checkTablesOnly();
      }
      JPEGMetadata localJPEGMetadata = streamMetadata;
      return localJPEGMetadata;
    }
    finally
    {
      clearThreadLock();
    }
  }
  
  public IIOMetadata getImageMetadata(int paramInt)
    throws IOException
  {
    setThreadLock();
    try
    {
      if ((imageMetadataIndex == paramInt) && (imageMetadata != null))
      {
        localJPEGMetadata = imageMetadata;
        return localJPEGMetadata;
      }
      cbLock.check();
      gotoImage(paramInt);
      imageMetadata = new JPEGMetadata(false, false, iis, this);
      imageMetadataIndex = paramInt;
      JPEGMetadata localJPEGMetadata = imageMetadata;
      return localJPEGMetadata;
    }
    finally
    {
      clearThreadLock();
    }
  }
  
  public BufferedImage read(int paramInt, ImageReadParam paramImageReadParam)
    throws IOException
  {
    setThreadLock();
    try
    {
      cbLock.check();
      try
      {
        readInternal(paramInt, paramImageReadParam, false);
      }
      catch (RuntimeException localRuntimeException)
      {
        resetLibraryState(structPointer);
        throw localRuntimeException;
      }
      catch (IOException localIOException)
      {
        resetLibraryState(structPointer);
        throw localIOException;
      }
      BufferedImage localBufferedImage1 = image;
      image = null;
      BufferedImage localBufferedImage2 = localBufferedImage1;
      return localBufferedImage2;
    }
    finally
    {
      clearThreadLock();
    }
  }
  
  private Raster readInternal(int paramInt, ImageReadParam paramImageReadParam, boolean paramBoolean)
    throws IOException
  {
    readHeader(paramInt, false);
    WritableRaster localWritableRaster = null;
    int i = 0;
    if (!paramBoolean)
    {
      localObject1 = getImageTypes(paramInt);
      if (!((Iterator)localObject1).hasNext()) {
        throw new IIOException("Unsupported Image Type");
      }
      image = getDestination(paramImageReadParam, (Iterator)localObject1, width, height);
      localWritableRaster = image.getRaster();
      i = image.getSampleModel().getNumBands();
      checkColorConversion(image, paramImageReadParam);
      checkReadParamBandSettings(paramImageReadParam, numComponents, i);
    }
    else
    {
      setOutColorSpace(structPointer, colorSpaceCode);
      image = null;
    }
    Object localObject1 = JPEG.bandOffsets[(numComponents - 1)];
    int j = paramBoolean ? numComponents : i;
    destinationBands = null;
    Rectangle localRectangle = new Rectangle(0, 0, 0, 0);
    destROI = new Rectangle(0, 0, 0, 0);
    computeRegions(paramImageReadParam, width, height, image, localRectangle, destROI);
    int k = 1;
    int m = 1;
    minProgressivePass = 0;
    maxProgressivePass = Integer.MAX_VALUE;
    if (paramImageReadParam != null)
    {
      k = paramImageReadParam.getSourceXSubsampling();
      m = paramImageReadParam.getSourceYSubsampling();
      int[] arrayOfInt1 = paramImageReadParam.getSourceBands();
      if (arrayOfInt1 != null)
      {
        localObject1 = arrayOfInt1;
        j = localObject1.length;
      }
      if (!paramBoolean) {
        destinationBands = paramImageReadParam.getDestinationBands();
      }
      minProgressivePass = paramImageReadParam.getSourceMinProgressivePass();
      maxProgressivePass = paramImageReadParam.getSourceMaxProgressivePass();
      if ((paramImageReadParam instanceof JPEGImageReadParam))
      {
        localObject2 = (JPEGImageReadParam)paramImageReadParam;
        if (((JPEGImageReadParam)localObject2).areTablesSet())
        {
          abbrevQTables = ((JPEGImageReadParam)localObject2).getQTables();
          abbrevDCHuffmanTables = ((JPEGImageReadParam)localObject2).getDCHuffmanTables();
          abbrevACHuffmanTables = ((JPEGImageReadParam)localObject2).getACHuffmanTables();
        }
      }
    }
    int n = destROI.width * j;
    buffer = new DataBufferByte(n);
    Object localObject2 = JPEG.bandOffsets[(j - 1)];
    raster = Raster.createInterleavedRaster(buffer, destROI.width, 1, n, j, (int[])localObject2, null);
    if (paramBoolean) {
      target = Raster.createInterleavedRaster(0, destROI.width, destROI.height, n, j, (int[])localObject2, null);
    } else {
      target = localWritableRaster;
    }
    int[] arrayOfInt2 = target.getSampleModel().getSampleSize();
    for (int i1 = 0; i1 < arrayOfInt2.length; i1++) {
      if ((arrayOfInt2[i1] <= 0) || (arrayOfInt2[i1] > 8)) {
        throw new IIOException("Illegal band size: should be 0 < size <= 8");
      }
    }
    i1 = (updateListeners != null) || (progressListeners != null) ? 1 : 0;
    initProgressData();
    if (paramInt == imageMetadataIndex)
    {
      knownPassCount = 0;
      Iterator localIterator = imageMetadata.markerSequence.iterator();
      while (localIterator.hasNext()) {
        if ((localIterator.next() instanceof SOSMarkerSegment)) {
          knownPassCount += 1;
        }
      }
    }
    progInterval = Math.max((target.getHeight() - 1) / 20, 1);
    if (knownPassCount > 0) {
      progInterval *= knownPassCount;
    } else if (maxProgressivePass != Integer.MAX_VALUE) {
      progInterval *= (maxProgressivePass - minProgressivePass + 1);
    }
    if (debug)
    {
      System.out.println("**** Read Data *****");
      System.out.println("numRasterBands is " + j);
      System.out.print("srcBands:");
      for (i2 = 0; i2 < localObject1.length; i2++) {
        System.out.print(" " + localObject1[i2]);
      }
      System.out.println();
      System.out.println("destination bands is " + destinationBands);
      if (destinationBands != null)
      {
        for (i2 = 0; i2 < destinationBands.length; i2++) {
          System.out.print(" " + destinationBands[i2]);
        }
        System.out.println();
      }
      System.out.println("sourceROI is " + localRectangle);
      System.out.println("destROI is " + destROI);
      System.out.println("periodX is " + k);
      System.out.println("periodY is " + m);
      System.out.println("minProgressivePass is " + minProgressivePass);
      System.out.println("maxProgressivePass is " + maxProgressivePass);
      System.out.println("callbackUpdates is " + i1);
    }
    processImageStarted(currentImage);
    int i2 = 0;
    boolean bool = readImage(paramInt, structPointer, buffer.getData(), j, (int[])localObject1, arrayOfInt2, x, y, width, height, k, m, abbrevQTables, abbrevDCHuffmanTables, abbrevACHuffmanTables, minProgressivePass, maxProgressivePass, i1);
    if (bool) {
      processReadAborted();
    } else {
      processImageComplete();
    }
    return target;
  }
  
  private void acceptPixels(int paramInt, boolean paramBoolean)
  {
    if (convert != null) {
      convert.filter(raster, raster);
    }
    target.setRect(destROI.x, destROI.y + paramInt, raster);
    cbLock.lock();
    try
    {
      processImageUpdate(image, destROI.x, destROI.y + paramInt, raster.getWidth(), 1, 1, 1, destinationBands);
      if ((paramInt > 0) && (paramInt % progInterval == 0))
      {
        int i = target.getHeight() - 1;
        float f = paramInt / i;
        if (paramBoolean)
        {
          if (knownPassCount != -1)
          {
            processImageProgress((pass + f) * 100.0F / knownPassCount);
          }
          else if (maxProgressivePass != Integer.MAX_VALUE)
          {
            processImageProgress((pass + f) * 100.0F / (maxProgressivePass - minProgressivePass + 1));
          }
          else
          {
            int j = Math.max(2, 10 - pass);
            int k = pass + j - 1;
            progInterval = Math.max(i / 20 * k, k);
            if (paramInt % progInterval == 0)
            {
              percentToDate = (previousPassPercentage + (1.0F - previousPassPercentage) * f / j);
              if (debug)
              {
                System.out.print("pass= " + pass);
                System.out.print(", y= " + paramInt);
                System.out.print(", progInt= " + progInterval);
                System.out.print(", % of pass: " + f);
                System.out.print(", rem. passes: " + j);
                System.out.print(", prev%: " + previousPassPercentage);
                System.out.print(", %ToDate: " + percentToDate);
                System.out.print(" ");
              }
              processImageProgress(percentToDate * 100.0F);
            }
          }
        }
        else {
          processImageProgress(f * 100.0F);
        }
      }
    }
    finally
    {
      cbLock.unlock();
    }
  }
  
  private void initProgressData()
  {
    knownPassCount = -1;
    pass = 0;
    percentToDate = 0.0F;
    previousPassPercentage = 0.0F;
    progInterval = 0;
  }
  
  /* Error */
  private void passStarted(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 755	com/sun/imageio/plugins/jpeg/JPEGImageReader:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;
    //   4: invokestatic 842	com/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock:access$000	(Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;)V
    //   7: aload_0
    //   8: iload_1
    //   9: putfield 744	com/sun/imageio/plugins/jpeg/JPEGImageReader:pass	I
    //   12: aload_0
    //   13: aload_0
    //   14: getfield 731	com/sun/imageio/plugins/jpeg/JPEGImageReader:percentToDate	F
    //   17: putfield 732	com/sun/imageio/plugins/jpeg/JPEGImageReader:previousPassPercentage	F
    //   20: aload_0
    //   21: aload_0
    //   22: getfield 760	com/sun/imageio/plugins/jpeg/JPEGImageReader:image	Ljava/awt/image/BufferedImage;
    //   25: iload_1
    //   26: aload_0
    //   27: getfield 740	com/sun/imageio/plugins/jpeg/JPEGImageReader:minProgressivePass	I
    //   30: aload_0
    //   31: getfield 738	com/sun/imageio/plugins/jpeg/JPEGImageReader:maxProgressivePass	I
    //   34: iconst_0
    //   35: iconst_0
    //   36: iconst_1
    //   37: iconst_1
    //   38: aload_0
    //   39: getfield 754	com/sun/imageio/plugins/jpeg/JPEGImageReader:destinationBands	[I
    //   42: invokevirtual 824	com/sun/imageio/plugins/jpeg/JPEGImageReader:processPassStarted	(Ljava/awt/image/BufferedImage;IIIIIII[I)V
    //   45: aload_0
    //   46: getfield 755	com/sun/imageio/plugins/jpeg/JPEGImageReader:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;
    //   49: invokestatic 843	com/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock:access$100	(Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;)V
    //   52: goto +13 -> 65
    //   55: astore_2
    //   56: aload_0
    //   57: getfield 755	com/sun/imageio/plugins/jpeg/JPEGImageReader:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;
    //   60: invokestatic 843	com/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock:access$100	(Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;)V
    //   63: aload_2
    //   64: athrow
    //   65: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	66	0	this	JPEGImageReader
    //   0	66	1	paramInt	int
    //   55	9	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   7	45	55	finally
  }
  
  /* Error */
  private void passComplete()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 755	com/sun/imageio/plugins/jpeg/JPEGImageReader:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;
    //   4: invokestatic 842	com/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock:access$000	(Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;)V
    //   7: aload_0
    //   8: aload_0
    //   9: getfield 760	com/sun/imageio/plugins/jpeg/JPEGImageReader:image	Ljava/awt/image/BufferedImage;
    //   12: invokevirtual 823	com/sun/imageio/plugins/jpeg/JPEGImageReader:processPassComplete	(Ljava/awt/image/BufferedImage;)V
    //   15: aload_0
    //   16: getfield 755	com/sun/imageio/plugins/jpeg/JPEGImageReader:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;
    //   19: invokestatic 843	com/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock:access$100	(Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;)V
    //   22: goto +13 -> 35
    //   25: astore_1
    //   26: aload_0
    //   27: getfield 755	com/sun/imageio/plugins/jpeg/JPEGImageReader:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;
    //   30: invokestatic 843	com/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock:access$100	(Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;)V
    //   33: aload_1
    //   34: athrow
    //   35: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	36	0	this	JPEGImageReader
    //   25	9	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   7	15	25	finally
  }
  
  /* Error */
  void thumbnailStarted(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 755	com/sun/imageio/plugins/jpeg/JPEGImageReader:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;
    //   4: invokestatic 842	com/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock:access$000	(Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;)V
    //   7: aload_0
    //   8: aload_0
    //   9: getfield 734	com/sun/imageio/plugins/jpeg/JPEGImageReader:currentImage	I
    //   12: iload_1
    //   13: invokevirtual 811	com/sun/imageio/plugins/jpeg/JPEGImageReader:processThumbnailStarted	(II)V
    //   16: aload_0
    //   17: getfield 755	com/sun/imageio/plugins/jpeg/JPEGImageReader:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;
    //   20: invokestatic 843	com/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock:access$100	(Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;)V
    //   23: goto +13 -> 36
    //   26: astore_2
    //   27: aload_0
    //   28: getfield 755	com/sun/imageio/plugins/jpeg/JPEGImageReader:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;
    //   31: invokestatic 843	com/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock:access$100	(Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;)V
    //   34: aload_2
    //   35: athrow
    //   36: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	37	0	this	JPEGImageReader
    //   0	37	1	paramInt	int
    //   26	9	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   7	16	26	finally
  }
  
  /* Error */
  void thumbnailProgress(float paramFloat)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 755	com/sun/imageio/plugins/jpeg/JPEGImageReader:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;
    //   4: invokestatic 842	com/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock:access$000	(Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;)V
    //   7: aload_0
    //   8: fload_1
    //   9: invokevirtual 806	com/sun/imageio/plugins/jpeg/JPEGImageReader:processThumbnailProgress	(F)V
    //   12: aload_0
    //   13: getfield 755	com/sun/imageio/plugins/jpeg/JPEGImageReader:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;
    //   16: invokestatic 843	com/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock:access$100	(Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;)V
    //   19: goto +13 -> 32
    //   22: astore_2
    //   23: aload_0
    //   24: getfield 755	com/sun/imageio/plugins/jpeg/JPEGImageReader:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;
    //   27: invokestatic 843	com/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock:access$100	(Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;)V
    //   30: aload_2
    //   31: athrow
    //   32: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	33	0	this	JPEGImageReader
    //   0	33	1	paramFloat	float
    //   22	9	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   7	12	22	finally
  }
  
  /* Error */
  void thumbnailComplete()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 755	com/sun/imageio/plugins/jpeg/JPEGImageReader:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;
    //   4: invokestatic 842	com/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock:access$000	(Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;)V
    //   7: aload_0
    //   8: invokevirtual 800	com/sun/imageio/plugins/jpeg/JPEGImageReader:processThumbnailComplete	()V
    //   11: aload_0
    //   12: getfield 755	com/sun/imageio/plugins/jpeg/JPEGImageReader:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;
    //   15: invokestatic 843	com/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock:access$100	(Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;)V
    //   18: goto +13 -> 31
    //   21: astore_1
    //   22: aload_0
    //   23: getfield 755	com/sun/imageio/plugins/jpeg/JPEGImageReader:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;
    //   26: invokestatic 843	com/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock:access$100	(Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;)V
    //   29: aload_1
    //   30: athrow
    //   31: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	32	0	this	JPEGImageReader
    //   21	9	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   7	11	21	finally
  }
  
  private native boolean readImage(int paramInt1, long paramLong, byte[] paramArrayOfByte, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, JPEGQTable[] paramArrayOfJPEGQTable, JPEGHuffmanTable[] paramArrayOfJPEGHuffmanTable1, JPEGHuffmanTable[] paramArrayOfJPEGHuffmanTable2, int paramInt9, int paramInt10, boolean paramBoolean);
  
  /* Error */
  public void abort()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 802	com/sun/imageio/plugins/jpeg/JPEGImageReader:setThreadLock	()V
    //   4: aload_0
    //   5: invokespecial 911	javax/imageio/ImageReader:abort	()V
    //   8: aload_0
    //   9: aload_0
    //   10: getfield 748	com/sun/imageio/plugins/jpeg/JPEGImageReader:structPointer	J
    //   13: invokespecial 813	com/sun/imageio/plugins/jpeg/JPEGImageReader:abortRead	(J)V
    //   16: aload_0
    //   17: invokespecial 796	com/sun/imageio/plugins/jpeg/JPEGImageReader:clearThreadLock	()V
    //   20: goto +10 -> 30
    //   23: astore_1
    //   24: aload_0
    //   25: invokespecial 796	com/sun/imageio/plugins/jpeg/JPEGImageReader:clearThreadLock	()V
    //   28: aload_1
    //   29: athrow
    //   30: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	31	0	this	JPEGImageReader
    //   23	6	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   4	16	23	finally
  }
  
  private native void abortRead(long paramLong);
  
  private native void resetLibraryState(long paramLong);
  
  public boolean canReadRaster()
  {
    return true;
  }
  
  public Raster readRaster(int paramInt, ImageReadParam paramImageReadParam)
    throws IOException
  {
    setThreadLock();
    Raster localRaster = null;
    try
    {
      cbLock.check();
      Point localPoint = null;
      if (paramImageReadParam != null)
      {
        localPoint = paramImageReadParam.getDestinationOffset();
        paramImageReadParam.setDestinationOffset(new Point(0, 0));
      }
      localRaster = readInternal(paramInt, paramImageReadParam, true);
      if (localPoint != null) {
        target = target.createWritableTranslatedChild(x, y);
      }
    }
    catch (RuntimeException localRuntimeException)
    {
      resetLibraryState(structPointer);
      throw localRuntimeException;
    }
    catch (IOException localIOException)
    {
      resetLibraryState(structPointer);
      throw localIOException;
    }
    finally
    {
      clearThreadLock();
    }
    return localRaster;
  }
  
  public boolean readerSupportsThumbnails()
  {
    return true;
  }
  
  public int getNumThumbnails(int paramInt)
    throws IOException
  {
    setThreadLock();
    try
    {
      cbLock.check();
      getImageMetadata(paramInt);
      JFIFMarkerSegment localJFIFMarkerSegment = (JFIFMarkerSegment)imageMetadata.findMarkerSegment(JFIFMarkerSegment.class, true);
      int i = 0;
      if (localJFIFMarkerSegment != null)
      {
        i = thumb == null ? 0 : 1;
        i += extSegments.size();
      }
      int j = i;
      return j;
    }
    finally
    {
      clearThreadLock();
    }
  }
  
  public int getThumbnailWidth(int paramInt1, int paramInt2)
    throws IOException
  {
    setThreadLock();
    try
    {
      cbLock.check();
      if ((paramInt2 < 0) || (paramInt2 >= getNumThumbnails(paramInt1))) {
        throw new IndexOutOfBoundsException("No such thumbnail");
      }
      JFIFMarkerSegment localJFIFMarkerSegment = (JFIFMarkerSegment)imageMetadata.findMarkerSegment(JFIFMarkerSegment.class, true);
      int i = localJFIFMarkerSegment.getThumbnailWidth(paramInt2);
      return i;
    }
    finally
    {
      clearThreadLock();
    }
  }
  
  public int getThumbnailHeight(int paramInt1, int paramInt2)
    throws IOException
  {
    setThreadLock();
    try
    {
      cbLock.check();
      if ((paramInt2 < 0) || (paramInt2 >= getNumThumbnails(paramInt1))) {
        throw new IndexOutOfBoundsException("No such thumbnail");
      }
      JFIFMarkerSegment localJFIFMarkerSegment = (JFIFMarkerSegment)imageMetadata.findMarkerSegment(JFIFMarkerSegment.class, true);
      int i = localJFIFMarkerSegment.getThumbnailHeight(paramInt2);
      return i;
    }
    finally
    {
      clearThreadLock();
    }
  }
  
  public BufferedImage readThumbnail(int paramInt1, int paramInt2)
    throws IOException
  {
    setThreadLock();
    try
    {
      cbLock.check();
      if ((paramInt2 < 0) || (paramInt2 >= getNumThumbnails(paramInt1))) {
        throw new IndexOutOfBoundsException("No such thumbnail");
      }
      JFIFMarkerSegment localJFIFMarkerSegment = (JFIFMarkerSegment)imageMetadata.findMarkerSegment(JFIFMarkerSegment.class, true);
      BufferedImage localBufferedImage = localJFIFMarkerSegment.getThumbnail(iis, paramInt2, this);
      return localBufferedImage;
    }
    finally
    {
      clearThreadLock();
    }
  }
  
  private void resetInternalState()
  {
    resetReader(structPointer);
    numImages = 0;
    imagePositions = new ArrayList();
    currentImage = -1;
    image = null;
    raster = null;
    target = null;
    buffer = null;
    destROI = null;
    destinationBands = null;
    streamMetadata = null;
    imageMetadata = null;
    imageMetadataIndex = -1;
    haveSeeked = false;
    tablesOnlyChecked = false;
    iccCS = null;
    initProgressData();
  }
  
  /* Error */
  public void reset()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 802	com/sun/imageio/plugins/jpeg/JPEGImageReader:setThreadLock	()V
    //   4: aload_0
    //   5: getfield 755	com/sun/imageio/plugins/jpeg/JPEGImageReader:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;
    //   8: invokevirtual 841	com/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock:check	()V
    //   11: aload_0
    //   12: invokespecial 912	javax/imageio/ImageReader:reset	()V
    //   15: aload_0
    //   16: invokespecial 796	com/sun/imageio/plugins/jpeg/JPEGImageReader:clearThreadLock	()V
    //   19: goto +10 -> 29
    //   22: astore_1
    //   23: aload_0
    //   24: invokespecial 796	com/sun/imageio/plugins/jpeg/JPEGImageReader:clearThreadLock	()V
    //   27: aload_1
    //   28: athrow
    //   29: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	30	0	this	JPEGImageReader
    //   22	6	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   4	15	22	finally
  }
  
  private native void resetReader(long paramLong);
  
  /* Error */
  public void dispose()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 802	com/sun/imageio/plugins/jpeg/JPEGImageReader:setThreadLock	()V
    //   4: aload_0
    //   5: getfield 755	com/sun/imageio/plugins/jpeg/JPEGImageReader:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock;
    //   8: invokevirtual 841	com/sun/imageio/plugins/jpeg/JPEGImageReader$CallBackLock:check	()V
    //   11: aload_0
    //   12: getfield 748	com/sun/imageio/plugins/jpeg/JPEGImageReader:structPointer	J
    //   15: lconst_0
    //   16: lcmp
    //   17: ifeq +17 -> 34
    //   20: aload_0
    //   21: getfield 774	com/sun/imageio/plugins/jpeg/JPEGImageReader:disposerRecord	Lsun/java2d/DisposerRecord;
    //   24: invokeinterface 936 1 0
    //   29: aload_0
    //   30: lconst_0
    //   31: putfield 748	com/sun/imageio/plugins/jpeg/JPEGImageReader:structPointer	J
    //   34: aload_0
    //   35: invokespecial 796	com/sun/imageio/plugins/jpeg/JPEGImageReader:clearThreadLock	()V
    //   38: goto +10 -> 48
    //   41: astore_1
    //   42: aload_0
    //   43: invokespecial 796	com/sun/imageio/plugins/jpeg/JPEGImageReader:clearThreadLock	()V
    //   46: aload_1
    //   47: athrow
    //   48: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	49	0	this	JPEGImageReader
    //   41	6	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   4	34	41	finally
  }
  
  private static native void disposeReader(long paramLong);
  
  private synchronized void setThreadLock()
  {
    Thread localThread = Thread.currentThread();
    if (theThread != null)
    {
      if (theThread != localThread) {
        throw new IllegalStateException("Attempt to use instance of " + this + " locked on thread " + theThread + " from thread " + localThread);
      }
      theLockCount += 1;
    }
    else
    {
      theThread = localThread;
      theLockCount = 1;
    }
  }
  
  private synchronized void clearThreadLock()
  {
    Thread localThread = Thread.currentThread();
    if ((theThread == null) || (theThread != localThread)) {
      throw new IllegalStateException("Attempt to clear thread lock  form wrong thread. Locked thread: " + theThread + "; current thread: " + localThread);
    }
    theLockCount -= 1;
    if (theLockCount == 0) {
      theThread = null;
    }
  }
  
  static
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        System.loadLibrary("jpeg");
        return null;
      }
    });
    initReaderIDs(ImageInputStream.class, JPEGQTable.class, JPEGHuffmanTable.class);
  }
  
  private static class CallBackLock
  {
    private State lockState = State.Unlocked;
    
    CallBackLock() {}
    
    void check()
    {
      if (lockState != State.Unlocked) {
        throw new IllegalStateException("Access to the reader is not allowed");
      }
    }
    
    private void lock()
    {
      lockState = State.Locked;
    }
    
    private void unlock()
    {
      lockState = State.Unlocked;
    }
    
    private static enum State
    {
      Unlocked,  Locked;
      
      private State() {}
    }
  }
  
  private static class JPEGReaderDisposerRecord
    implements DisposerRecord
  {
    private long pData;
    
    public JPEGReaderDisposerRecord(long paramLong)
    {
      pData = paramLong;
    }
    
    public synchronized void dispose()
    {
      if (pData != 0L)
      {
        JPEGImageReader.disposeReader(pData);
        pData = 0L;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\plugins\jpeg\JPEGImageReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */