package com.sun.imageio.plugins.jpeg;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.PrintStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.jpeg.JPEGHuffmanTable;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.plugins.jpeg.JPEGQTable;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;
import org.w3c.dom.Node;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;

public class JPEGImageWriter
  extends ImageWriter
{
  private boolean debug = false;
  private long structPointer = 0L;
  private ImageOutputStream ios = null;
  private Raster srcRas = null;
  private WritableRaster raster = null;
  private boolean indexed = false;
  private IndexColorModel indexCM = null;
  private boolean convertTosRGB = false;
  private WritableRaster converted = null;
  private boolean isAlphaPremultiplied = false;
  private ColorModel srcCM = null;
  private List thumbnails = null;
  private ICC_Profile iccProfile = null;
  private int sourceXOffset = 0;
  private int sourceYOffset = 0;
  private int sourceWidth = 0;
  private int[] srcBands = null;
  private int sourceHeight = 0;
  private int currentImage = 0;
  private ColorConvertOp convertOp = null;
  private JPEGQTable[] streamQTables = null;
  private JPEGHuffmanTable[] streamDCHuffmanTables = null;
  private JPEGHuffmanTable[] streamACHuffmanTables = null;
  private boolean ignoreJFIF = false;
  private boolean forceJFIF = false;
  private boolean ignoreAdobe = false;
  private int newAdobeTransform = -1;
  private boolean writeDefaultJFIF = false;
  private boolean writeAdobe = false;
  private JPEGMetadata metadata = null;
  private boolean sequencePrepared = false;
  private int numScans = 0;
  private Object disposerReferent = new Object();
  private DisposerRecord disposerRecord = new JPEGWriterDisposerRecord(structPointer);
  protected static final int WARNING_DEST_IGNORED = 0;
  protected static final int WARNING_STREAM_METADATA_IGNORED = 1;
  protected static final int WARNING_DEST_METADATA_COMP_MISMATCH = 2;
  protected static final int WARNING_DEST_METADATA_JFIF_MISMATCH = 3;
  protected static final int WARNING_DEST_METADATA_ADOBE_MISMATCH = 4;
  protected static final int WARNING_IMAGE_METADATA_JFIF_MISMATCH = 5;
  protected static final int WARNING_IMAGE_METADATA_ADOBE_MISMATCH = 6;
  protected static final int WARNING_METADATA_NOT_JPEG_FOR_RASTER = 7;
  protected static final int WARNING_NO_BANDS_ON_INDEXED = 8;
  protected static final int WARNING_ILLEGAL_THUMBNAIL = 9;
  protected static final int WARNING_IGNORING_THUMBS = 10;
  protected static final int WARNING_FORCING_JFIF = 11;
  protected static final int WARNING_THUMB_CLIPPED = 12;
  protected static final int WARNING_METADATA_ADJUSTED_FOR_THUMB = 13;
  protected static final int WARNING_NO_RGB_THUMB_AS_INDEXED = 14;
  protected static final int WARNING_NO_GRAY_THUMB_AS_INDEXED = 15;
  private static final int MAX_WARNING = 15;
  static final Dimension[] preferredThumbSizes = { new Dimension(1, 1), new Dimension(255, 255) };
  private Thread theThread = null;
  private int theLockCount = 0;
  private CallBackLock cbLock = new CallBackLock();
  
  public JPEGImageWriter(ImageWriterSpi paramImageWriterSpi)
  {
    super(paramImageWriterSpi);
    Disposer.addRecord(disposerReferent, disposerRecord);
  }
  
  /* Error */
  public void setOutput(Object paramObject)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 907	com/sun/imageio/plugins/jpeg/JPEGImageWriter:setThreadLock	()V
    //   4: aload_0
    //   5: getfield 849	com/sun/imageio/plugins/jpeg/JPEGImageWriter:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock;
    //   8: invokevirtual 944	com/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock:check	()V
    //   11: aload_0
    //   12: aload_1
    //   13: invokespecial 1034	javax/imageio/ImageWriter:setOutput	(Ljava/lang/Object;)V
    //   16: aload_0
    //   17: invokespecial 906	com/sun/imageio/plugins/jpeg/JPEGImageWriter:resetInternalState	()V
    //   20: aload_0
    //   21: aload_1
    //   22: checkcast 493	javax/imageio/stream/ImageOutputStream
    //   25: putfield 865	com/sun/imageio/plugins/jpeg/JPEGImageWriter:ios	Ljavax/imageio/stream/ImageOutputStream;
    //   28: aload_0
    //   29: aload_0
    //   30: getfield 837	com/sun/imageio/plugins/jpeg/JPEGImageWriter:structPointer	J
    //   33: invokespecial 917	com/sun/imageio/plugins/jpeg/JPEGImageWriter:setDest	(J)V
    //   36: aload_0
    //   37: invokespecial 902	com/sun/imageio/plugins/jpeg/JPEGImageWriter:clearThreadLock	()V
    //   40: goto +10 -> 50
    //   43: astore_2
    //   44: aload_0
    //   45: invokespecial 902	com/sun/imageio/plugins/jpeg/JPEGImageWriter:clearThreadLock	()V
    //   48: aload_2
    //   49: athrow
    //   50: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	51	0	this	JPEGImageWriter
    //   0	51	1	paramObject	Object
    //   43	6	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   4	36	43	finally
  }
  
  public ImageWriteParam getDefaultWriteParam()
  {
    return new JPEGImageWriteParam(null);
  }
  
  public IIOMetadata getDefaultStreamMetadata(ImageWriteParam paramImageWriteParam)
  {
    setThreadLock();
    try
    {
      JPEGMetadata localJPEGMetadata = new JPEGMetadata(paramImageWriteParam, this);
      return localJPEGMetadata;
    }
    finally
    {
      clearThreadLock();
    }
  }
  
  public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier paramImageTypeSpecifier, ImageWriteParam paramImageWriteParam)
  {
    setThreadLock();
    try
    {
      JPEGMetadata localJPEGMetadata = new JPEGMetadata(paramImageTypeSpecifier, paramImageWriteParam, this);
      return localJPEGMetadata;
    }
    finally
    {
      clearThreadLock();
    }
  }
  
  public IIOMetadata convertStreamMetadata(IIOMetadata paramIIOMetadata, ImageWriteParam paramImageWriteParam)
  {
    if ((paramIIOMetadata instanceof JPEGMetadata))
    {
      JPEGMetadata localJPEGMetadata = (JPEGMetadata)paramIIOMetadata;
      if (isStream) {
        return paramIIOMetadata;
      }
    }
    return null;
  }
  
  public IIOMetadata convertImageMetadata(IIOMetadata paramIIOMetadata, ImageTypeSpecifier paramImageTypeSpecifier, ImageWriteParam paramImageWriteParam)
  {
    setThreadLock();
    try
    {
      IIOMetadata localIIOMetadata = convertImageMetadataOnThread(paramIIOMetadata, paramImageTypeSpecifier, paramImageWriteParam);
      return localIIOMetadata;
    }
    finally
    {
      clearThreadLock();
    }
  }
  
  private IIOMetadata convertImageMetadataOnThread(IIOMetadata paramIIOMetadata, ImageTypeSpecifier paramImageTypeSpecifier, ImageWriteParam paramImageWriteParam)
  {
    Object localObject;
    if ((paramIIOMetadata instanceof JPEGMetadata))
    {
      localObject = (JPEGMetadata)paramIIOMetadata;
      if (!isStream) {
        return paramIIOMetadata;
      }
      return null;
    }
    if (paramIIOMetadata.isStandardMetadataFormatSupported())
    {
      localObject = "javax_imageio_1.0";
      Node localNode = paramIIOMetadata.getAsTree((String)localObject);
      if (localNode != null)
      {
        JPEGMetadata localJPEGMetadata = new JPEGMetadata(paramImageTypeSpecifier, paramImageWriteParam, this);
        try
        {
          localJPEGMetadata.setFromTree((String)localObject, localNode);
        }
        catch (IIOInvalidTreeException localIIOInvalidTreeException)
        {
          return null;
        }
        return localJPEGMetadata;
      }
    }
    return null;
  }
  
  public int getNumThumbnailsSupported(ImageTypeSpecifier paramImageTypeSpecifier, ImageWriteParam paramImageWriteParam, IIOMetadata paramIIOMetadata1, IIOMetadata paramIIOMetadata2)
  {
    if (jfifOK(paramImageTypeSpecifier, paramImageWriteParam, paramIIOMetadata1, paramIIOMetadata2)) {
      return Integer.MAX_VALUE;
    }
    return 0;
  }
  
  public Dimension[] getPreferredThumbnailSizes(ImageTypeSpecifier paramImageTypeSpecifier, ImageWriteParam paramImageWriteParam, IIOMetadata paramIIOMetadata1, IIOMetadata paramIIOMetadata2)
  {
    if (jfifOK(paramImageTypeSpecifier, paramImageWriteParam, paramIIOMetadata1, paramIIOMetadata2)) {
      return (Dimension[])preferredThumbSizes.clone();
    }
    return null;
  }
  
  private boolean jfifOK(ImageTypeSpecifier paramImageTypeSpecifier, ImageWriteParam paramImageWriteParam, IIOMetadata paramIIOMetadata1, IIOMetadata paramIIOMetadata2)
  {
    if ((paramImageTypeSpecifier != null) && (!JPEG.isJFIFcompliant(paramImageTypeSpecifier, true))) {
      return false;
    }
    if (paramIIOMetadata2 != null)
    {
      JPEGMetadata localJPEGMetadata = null;
      if ((paramIIOMetadata2 instanceof JPEGMetadata)) {
        localJPEGMetadata = (JPEGMetadata)paramIIOMetadata2;
      } else {
        localJPEGMetadata = (JPEGMetadata)convertImageMetadata(paramIIOMetadata2, paramImageTypeSpecifier, paramImageWriteParam);
      }
      if (localJPEGMetadata.findMarkerSegment(JFIFMarkerSegment.class, true) == null) {
        return false;
      }
    }
    return true;
  }
  
  public boolean canWriteRasters()
  {
    return true;
  }
  
  public void write(IIOMetadata paramIIOMetadata, IIOImage paramIIOImage, ImageWriteParam paramImageWriteParam)
    throws IOException
  {
    setThreadLock();
    try
    {
      cbLock.check();
      writeOnThread(paramIIOMetadata, paramIIOImage, paramImageWriteParam);
    }
    finally
    {
      clearThreadLock();
    }
  }
  
  private void writeOnThread(IIOMetadata paramIIOMetadata, IIOImage paramIIOImage, ImageWriteParam paramImageWriteParam)
    throws IOException
  {
    if (ios == null) {
      throw new IllegalStateException("Output has not been set!");
    }
    if (paramIIOImage == null) {
      throw new IllegalArgumentException("image is null!");
    }
    if (paramIIOMetadata != null) {
      warningOccurred(1);
    }
    boolean bool1 = paramIIOImage.hasRaster();
    RenderedImage localRenderedImage = null;
    if (bool1)
    {
      srcRas = paramIIOImage.getRaster();
    }
    else
    {
      localRenderedImage = paramIIOImage.getRenderedImage();
      if ((localRenderedImage instanceof BufferedImage))
      {
        srcRas = ((BufferedImage)localRenderedImage).getRaster();
      }
      else if ((localRenderedImage.getNumXTiles() == 1) && (localRenderedImage.getNumYTiles() == 1))
      {
        srcRas = localRenderedImage.getTile(localRenderedImage.getMinTileX(), localRenderedImage.getMinTileY());
        if ((srcRas.getWidth() != localRenderedImage.getWidth()) || (srcRas.getHeight() != localRenderedImage.getHeight())) {
          srcRas = srcRas.createChild(srcRas.getMinX(), srcRas.getMinY(), localRenderedImage.getWidth(), localRenderedImage.getHeight(), srcRas.getMinX(), srcRas.getMinY(), null);
        }
      }
      else
      {
        srcRas = localRenderedImage.getData();
      }
    }
    int i = srcRas.getNumBands();
    indexed = false;
    indexCM = null;
    ColorModel localColorModel = null;
    ColorSpace localColorSpace = null;
    isAlphaPremultiplied = false;
    srcCM = null;
    if (!bool1)
    {
      localColorModel = localRenderedImage.getColorModel();
      if (localColorModel != null)
      {
        localColorSpace = localColorModel.getColorSpace();
        if ((localColorModel instanceof IndexColorModel))
        {
          indexed = true;
          indexCM = ((IndexColorModel)localColorModel);
          i = localColorModel.getNumComponents();
        }
        if (localColorModel.isAlphaPremultiplied())
        {
          isAlphaPremultiplied = true;
          srcCM = localColorModel;
        }
      }
    }
    srcBands = JPEG.bandOffsets[(i - 1)];
    int j = i;
    if (paramImageWriteParam != null)
    {
      int[] arrayOfInt1 = paramImageWriteParam.getSourceBands();
      if (arrayOfInt1 != null) {
        if (indexed)
        {
          warningOccurred(8);
        }
        else
        {
          srcBands = arrayOfInt1;
          j = srcBands.length;
          if (j > i) {
            throw new IIOException("ImageWriteParam specifies too many source bands");
          }
        }
      }
    }
    boolean bool2 = j != i;
    boolean bool3 = (!bool1) && (!bool2);
    Object localObject1 = null;
    int[] arrayOfInt2;
    if (!indexed)
    {
      localObject1 = srcRas.getSampleModel().getSampleSize();
      if (bool2)
      {
        arrayOfInt2 = new int[j];
        for (m = 0; m < j; m++) {
          arrayOfInt2[m] = localObject1[srcBands[m]];
        }
        localObject1 = arrayOfInt2;
      }
    }
    else
    {
      arrayOfInt2 = srcRas.getSampleModel().getSampleSize();
      localObject1 = new int[i];
      for (m = 0; m < i; m++) {
        localObject1[m] = arrayOfInt2[0];
      }
    }
    for (int k = 0; k < localObject1.length; k++)
    {
      if ((localObject1[k] <= 0) || (localObject1[k] > 8)) {
        throw new IIOException("Illegal band size: should be 0 < size <= 8");
      }
      if (indexed) {
        localObject1[k] = 8;
      }
    }
    if (debug)
    {
      System.out.println("numSrcBands is " + i);
      System.out.println("numBandsUsed is " + j);
      System.out.println("usingBandSubset is " + bool2);
      System.out.println("fullImage is " + bool3);
      System.out.print("Band sizes:");
      for (k = 0; k < localObject1.length; k++) {
        System.out.print(" " + localObject1[k]);
      }
      System.out.println();
    }
    ImageTypeSpecifier localImageTypeSpecifier1 = null;
    if (paramImageWriteParam != null)
    {
      localImageTypeSpecifier1 = paramImageWriteParam.getDestinationType();
      if ((bool3) && (localImageTypeSpecifier1 != null))
      {
        warningOccurred(0);
        localImageTypeSpecifier1 = null;
      }
    }
    sourceXOffset = srcRas.getMinX();
    sourceYOffset = srcRas.getMinY();
    int m = srcRas.getWidth();
    int n = srcRas.getHeight();
    sourceWidth = m;
    sourceHeight = n;
    int i1 = 1;
    int i2 = 1;
    int i3 = 0;
    int i4 = 0;
    JPEGQTable[] arrayOfJPEGQTable = null;
    JPEGHuffmanTable[] arrayOfJPEGHuffmanTable1 = null;
    JPEGHuffmanTable[] arrayOfJPEGHuffmanTable2 = null;
    boolean bool4 = false;
    JPEGImageWriteParam localJPEGImageWriteParam = null;
    int i5 = 0;
    if (paramImageWriteParam != null)
    {
      localObject2 = paramImageWriteParam.getSourceRegion();
      if (localObject2 != null)
      {
        Rectangle localRectangle = new Rectangle(sourceXOffset, sourceYOffset, sourceWidth, sourceHeight);
        localObject2 = ((Rectangle)localObject2).intersection(localRectangle);
        sourceXOffset = x;
        sourceYOffset = y;
        sourceWidth = width;
        sourceHeight = height;
      }
      if (sourceWidth + sourceXOffset > m) {
        sourceWidth = (m - sourceXOffset);
      }
      if (sourceHeight + sourceYOffset > n) {
        sourceHeight = (n - sourceYOffset);
      }
      i1 = paramImageWriteParam.getSourceXSubsampling();
      i2 = paramImageWriteParam.getSourceYSubsampling();
      i3 = paramImageWriteParam.getSubsamplingXOffset();
      i4 = paramImageWriteParam.getSubsamplingYOffset();
      switch (paramImageWriteParam.getCompressionMode())
      {
      case 0: 
        throw new IIOException("JPEG compression cannot be disabled");
      case 2: 
        float f = paramImageWriteParam.getCompressionQuality();
        f = JPEG.convertToLinearQuality(f);
        arrayOfJPEGQTable = new JPEGQTable[2];
        arrayOfJPEGQTable[0] = JPEGQTable.K1Luminance.getScaledInstance(f, true);
        arrayOfJPEGQTable[1] = JPEGQTable.K2Chrominance.getScaledInstance(f, true);
        break;
      case 1: 
        arrayOfJPEGQTable = new JPEGQTable[2];
        arrayOfJPEGQTable[0] = JPEGQTable.K1Div2Luminance;
        arrayOfJPEGQTable[1] = JPEGQTable.K2Div2Chrominance;
      }
      i5 = paramImageWriteParam.getProgressiveMode();
      if ((paramImageWriteParam instanceof JPEGImageWriteParam))
      {
        localJPEGImageWriteParam = (JPEGImageWriteParam)paramImageWriteParam;
        bool4 = localJPEGImageWriteParam.getOptimizeHuffmanTables();
      }
    }
    Object localObject2 = paramIIOImage.getMetadata();
    if (localObject2 != null) {
      if ((localObject2 instanceof JPEGMetadata))
      {
        metadata = ((JPEGMetadata)localObject2);
        if (debug) {
          System.out.println("We have metadata, and it's JPEG metadata");
        }
      }
      else if (!bool1)
      {
        ImageTypeSpecifier localImageTypeSpecifier2 = localImageTypeSpecifier1;
        if (localImageTypeSpecifier2 == null) {
          localImageTypeSpecifier2 = new ImageTypeSpecifier(localRenderedImage);
        }
        metadata = ((JPEGMetadata)convertImageMetadata((IIOMetadata)localObject2, localImageTypeSpecifier2, paramImageWriteParam));
      }
      else
      {
        warningOccurred(7);
      }
    }
    ignoreJFIF = false;
    ignoreAdobe = false;
    newAdobeTransform = -1;
    writeDefaultJFIF = false;
    writeAdobe = false;
    int i6 = 0;
    int i7 = 0;
    JFIFMarkerSegment localJFIFMarkerSegment = null;
    AdobeMarkerSegment localAdobeMarkerSegment = null;
    SOFMarkerSegment localSOFMarkerSegment = null;
    if (metadata != null)
    {
      localJFIFMarkerSegment = (JFIFMarkerSegment)metadata.findMarkerSegment(JFIFMarkerSegment.class, true);
      localAdobeMarkerSegment = (AdobeMarkerSegment)metadata.findMarkerSegment(AdobeMarkerSegment.class, true);
      localSOFMarkerSegment = (SOFMarkerSegment)metadata.findMarkerSegment(SOFMarkerSegment.class, true);
    }
    iccProfile = null;
    convertTosRGB = false;
    converted = null;
    if (localImageTypeSpecifier1 != null)
    {
      if (j != localImageTypeSpecifier1.getNumBands()) {
        throw new IIOException("Number of source bands != number of destination bands");
      }
      localColorSpace = localImageTypeSpecifier1.getColorModel().getColorSpace();
      if (metadata != null)
      {
        checkSOFBands(localSOFMarkerSegment, j);
        checkJFIF(localJFIFMarkerSegment, localImageTypeSpecifier1, false);
        if ((localJFIFMarkerSegment != null) && (!ignoreJFIF) && (JPEG.isNonStandardICC(localColorSpace))) {
          iccProfile = ((ICC_ColorSpace)localColorSpace).getProfile();
        }
        checkAdobe(localAdobeMarkerSegment, localImageTypeSpecifier1, false);
      }
      else
      {
        if (JPEG.isJFIFcompliant(localImageTypeSpecifier1, false))
        {
          writeDefaultJFIF = true;
          if (JPEG.isNonStandardICC(localColorSpace)) {
            iccProfile = ((ICC_ColorSpace)localColorSpace).getProfile();
          }
        }
        else
        {
          int i8 = JPEG.transformForType(localImageTypeSpecifier1, false);
          if (i8 != -1)
          {
            writeAdobe = true;
            newAdobeTransform = i8;
          }
        }
        metadata = new JPEGMetadata(localImageTypeSpecifier1, null, this);
      }
      i6 = getSrcCSType(localImageTypeSpecifier1);
      i7 = getDefaultDestCSType(localImageTypeSpecifier1);
    }
    else if (metadata == null)
    {
      if (bool3)
      {
        metadata = new JPEGMetadata(new ImageTypeSpecifier(localRenderedImage), paramImageWriteParam, this);
        if (metadata.findMarkerSegment(JFIFMarkerSegment.class, true) != null)
        {
          localColorSpace = localRenderedImage.getColorModel().getColorSpace();
          if (JPEG.isNonStandardICC(localColorSpace)) {
            iccProfile = ((ICC_ColorSpace)localColorSpace).getProfile();
          }
        }
        i6 = getSrcCSType(localRenderedImage);
        i7 = getDefaultDestCSType(localRenderedImage);
      }
    }
    else
    {
      checkSOFBands(localSOFMarkerSegment, j);
      if (bool3)
      {
        ImageTypeSpecifier localImageTypeSpecifier3 = new ImageTypeSpecifier(localRenderedImage);
        i6 = getSrcCSType(localRenderedImage);
        if (localColorModel != null)
        {
          boolean bool5 = localColorModel.hasAlpha();
          switch (localColorSpace.getType())
          {
          case 6: 
            if (!bool5)
            {
              i7 = 1;
            }
            else if (localJFIFMarkerSegment != null)
            {
              ignoreJFIF = true;
              warningOccurred(5);
            }
            if ((localAdobeMarkerSegment != null) && (transform != 0))
            {
              newAdobeTransform = 0;
              warningOccurred(6);
            }
            break;
          case 5: 
            if (!bool5)
            {
              if (localJFIFMarkerSegment != null)
              {
                i7 = 3;
                if ((JPEG.isNonStandardICC(localColorSpace)) || (((localColorSpace instanceof ICC_ColorSpace)) && (iccSegment != null))) {
                  iccProfile = ((ICC_ColorSpace)localColorSpace).getProfile();
                }
              }
              else if (localAdobeMarkerSegment != null)
              {
                switch (transform)
                {
                case 0: 
                  i7 = 2;
                  break;
                case 1: 
                  i7 = 3;
                  break;
                default: 
                  warningOccurred(6);
                  newAdobeTransform = 0;
                  i7 = 2;
                  break;
                }
              }
              else
              {
                i10 = localSOFMarkerSegment.getIDencodedCSType();
                if (i10 != 0)
                {
                  i7 = i10;
                }
                else
                {
                  bool6 = isSubsampled(componentSpecs);
                  if (bool6) {
                    i7 = 3;
                  } else {
                    i7 = 2;
                  }
                }
              }
            }
            else
            {
              if (localJFIFMarkerSegment != null)
              {
                ignoreJFIF = true;
                warningOccurred(5);
              }
              if (localAdobeMarkerSegment != null)
              {
                if (transform != 0)
                {
                  newAdobeTransform = 0;
                  warningOccurred(6);
                }
                i7 = 6;
              }
              else
              {
                i10 = localSOFMarkerSegment.getIDencodedCSType();
                if (i10 != 0)
                {
                  i7 = i10;
                }
                else
                {
                  bool6 = isSubsampled(componentSpecs);
                  i7 = bool6 ? 7 : 6;
                }
              }
            }
            break;
          case 13: 
            if (localColorSpace == JPEG.JCS.getYCC()) {
              if (!bool5)
              {
                if (localJFIFMarkerSegment != null)
                {
                  convertTosRGB = true;
                  convertOp = new ColorConvertOp(localColorSpace, JPEG.JCS.sRGB, null);
                  i7 = 3;
                }
                else if (localAdobeMarkerSegment != null)
                {
                  if (transform != 1)
                  {
                    newAdobeTransform = 1;
                    warningOccurred(6);
                  }
                  i7 = 5;
                }
                else
                {
                  i7 = 5;
                }
              }
              else
              {
                if (localJFIFMarkerSegment != null)
                {
                  ignoreJFIF = true;
                  warningOccurred(5);
                }
                else if ((localAdobeMarkerSegment != null) && (transform != 0))
                {
                  newAdobeTransform = 0;
                  warningOccurred(6);
                }
                i7 = 10;
              }
            }
            break;
          }
        }
      }
    }
    int i9 = 0;
    int[] arrayOfInt3 = null;
    if (metadata != null)
    {
      if (localSOFMarkerSegment == null) {
        localSOFMarkerSegment = (SOFMarkerSegment)metadata.findMarkerSegment(SOFMarkerSegment.class, true);
      }
      if ((localSOFMarkerSegment != null) && (tag == 194))
      {
        i9 = 1;
        if (i5 == 3) {
          arrayOfInt3 = collectScans(metadata, localSOFMarkerSegment);
        } else {
          numScans = 0;
        }
      }
      if (localJFIFMarkerSegment == null) {
        localJFIFMarkerSegment = (JFIFMarkerSegment)metadata.findMarkerSegment(JFIFMarkerSegment.class, true);
      }
    }
    thumbnails = paramIIOImage.getThumbnails();
    int i10 = paramIIOImage.getNumThumbnails();
    forceJFIF = false;
    if (!writeDefaultJFIF) {
      if (metadata == null)
      {
        thumbnails = null;
        if (i10 != 0) {
          warningOccurred(10);
        }
      }
      else if (!bool3)
      {
        if (localJFIFMarkerSegment == null)
        {
          thumbnails = null;
          if (i10 != 0) {
            warningOccurred(10);
          }
        }
      }
      else if (localJFIFMarkerSegment == null)
      {
        if ((i7 == 1) || (i7 == 3))
        {
          if (i10 != 0)
          {
            forceJFIF = true;
            warningOccurred(11);
          }
        }
        else
        {
          thumbnails = null;
          if (i10 != 0) {
            warningOccurred(10);
          }
        }
      }
    }
    boolean bool6 = (metadata != null) || (writeDefaultJFIF) || (writeAdobe);
    boolean bool7 = true;
    boolean bool8 = true;
    DQTMarkerSegment localDQTMarkerSegment = null;
    DHTMarkerSegment localDHTMarkerSegment = null;
    int i11 = 0;
    if (metadata != null)
    {
      localDQTMarkerSegment = (DQTMarkerSegment)metadata.findMarkerSegment(DQTMarkerSegment.class, true);
      localDHTMarkerSegment = (DHTMarkerSegment)metadata.findMarkerSegment(DHTMarkerSegment.class, true);
      localObject3 = (DRIMarkerSegment)metadata.findMarkerSegment(DRIMarkerSegment.class, true);
      if (localObject3 != null) {
        i11 = restartInterval;
      }
      if (localDQTMarkerSegment == null) {
        bool7 = false;
      }
      if (localDHTMarkerSegment == null) {
        bool8 = false;
      }
    }
    if (arrayOfJPEGQTable == null) {
      if (localDQTMarkerSegment != null) {
        arrayOfJPEGQTable = collectQTablesFromMetadata(metadata);
      } else if (streamQTables != null) {
        arrayOfJPEGQTable = streamQTables;
      } else if ((localJPEGImageWriteParam != null) && (localJPEGImageWriteParam.areTablesSet())) {
        arrayOfJPEGQTable = localJPEGImageWriteParam.getQTables();
      } else {
        arrayOfJPEGQTable = JPEG.getDefaultQTables();
      }
    }
    if (!bool4) {
      if ((localDHTMarkerSegment != null) && (i9 == 0))
      {
        arrayOfJPEGHuffmanTable1 = collectHTablesFromMetadata(metadata, true);
        arrayOfJPEGHuffmanTable2 = collectHTablesFromMetadata(metadata, false);
      }
      else if (streamDCHuffmanTables != null)
      {
        arrayOfJPEGHuffmanTable1 = streamDCHuffmanTables;
        arrayOfJPEGHuffmanTable2 = streamACHuffmanTables;
      }
      else if ((localJPEGImageWriteParam != null) && (localJPEGImageWriteParam.areTablesSet()))
      {
        arrayOfJPEGHuffmanTable1 = localJPEGImageWriteParam.getDCHuffmanTables();
        arrayOfJPEGHuffmanTable2 = localJPEGImageWriteParam.getACHuffmanTables();
      }
      else
      {
        arrayOfJPEGHuffmanTable1 = JPEG.getDefaultHuffmanTables(true);
        arrayOfJPEGHuffmanTable2 = JPEG.getDefaultHuffmanTables(false);
      }
    }
    Object localObject3 = new int[j];
    int[] arrayOfInt4 = new int[j];
    int[] arrayOfInt5 = new int[j];
    int[] arrayOfInt6 = new int[j];
    for (int i12 = 0; i12 < j; i12++)
    {
      localObject3[i12] = (i12 + 1);
      arrayOfInt4[i12] = 1;
      arrayOfInt5[i12] = 1;
      arrayOfInt6[i12] = 0;
    }
    if (localSOFMarkerSegment != null) {
      for (i12 = 0; i12 < j; i12++)
      {
        if (!forceJFIF) {
          localObject3[i12] = componentSpecs[i12].componentId;
        }
        arrayOfInt4[i12] = componentSpecs[i12].HsamplingFactor;
        arrayOfInt5[i12] = componentSpecs[i12].VsamplingFactor;
        arrayOfInt6[i12] = componentSpecs[i12].QtableSelector;
      }
    }
    sourceXOffset += i3;
    sourceWidth -= i3;
    sourceYOffset += i4;
    sourceHeight -= i4;
    i12 = (sourceWidth + i1 - 1) / i1;
    int i13 = (sourceHeight + i2 - 1) / i2;
    int i14 = sourceWidth * j;
    DataBufferByte localDataBufferByte = new DataBufferByte(i14);
    int[] arrayOfInt7 = JPEG.bandOffsets[(j - 1)];
    raster = Raster.createInterleavedRaster(localDataBufferByte, sourceWidth, 1, i14, j, arrayOfInt7, null);
    clearAbortRequest();
    cbLock.lock();
    try
    {
      processImageStarted(currentImage);
    }
    finally
    {
      cbLock.unlock();
    }
    boolean bool9 = false;
    if (debug)
    {
      System.out.println("inCsType: " + i6);
      System.out.println("outCsType: " + i7);
    }
    bool9 = writeImage(structPointer, localDataBufferByte.getData(), i6, i7, j, (int[])localObject1, sourceWidth, i12, i13, i1, i2, arrayOfJPEGQTable, bool7, arrayOfJPEGHuffmanTable1, arrayOfJPEGHuffmanTable2, bool8, bool4, i5 != 0, numScans, arrayOfInt3, (int[])localObject3, arrayOfInt4, arrayOfInt5, arrayOfInt6, bool6, i11);
    cbLock.lock();
    try
    {
      if (bool9) {
        processWriteAborted();
      } else {
        processImageComplete();
      }
      ios.flush();
    }
    finally
    {
      cbLock.unlock();
    }
    currentImage += 1;
  }
  
  public boolean canWriteSequence()
  {
    return true;
  }
  
  /* Error */
  public void prepareWriteSequence(IIOMetadata paramIIOMetadata)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 907	com/sun/imageio/plugins/jpeg/JPEGImageWriter:setThreadLock	()V
    //   4: aload_0
    //   5: getfield 849	com/sun/imageio/plugins/jpeg/JPEGImageWriter:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock;
    //   8: invokevirtual 944	com/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock:check	()V
    //   11: aload_0
    //   12: aload_1
    //   13: invokespecial 927	com/sun/imageio/plugins/jpeg/JPEGImageWriter:prepareWriteSequenceOnThread	(Ljavax/imageio/metadata/IIOMetadata;)V
    //   16: aload_0
    //   17: invokespecial 902	com/sun/imageio/plugins/jpeg/JPEGImageWriter:clearThreadLock	()V
    //   20: goto +10 -> 30
    //   23: astore_2
    //   24: aload_0
    //   25: invokespecial 902	com/sun/imageio/plugins/jpeg/JPEGImageWriter:clearThreadLock	()V
    //   28: aload_2
    //   29: athrow
    //   30: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	31	0	this	JPEGImageWriter
    //   0	31	1	paramIIOMetadata	IIOMetadata
    //   23	6	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   4	16	23	finally
  }
  
  private void prepareWriteSequenceOnThread(IIOMetadata paramIIOMetadata)
    throws IOException
  {
    if (ios == null) {
      throw new IllegalStateException("Output has not been set!");
    }
    if (paramIIOMetadata != null) {
      if ((paramIIOMetadata instanceof JPEGMetadata))
      {
        JPEGMetadata localJPEGMetadata = (JPEGMetadata)paramIIOMetadata;
        if (!isStream) {
          throw new IllegalArgumentException("Invalid stream metadata object.");
        }
        if (currentImage != 0) {
          throw new IIOException("JPEG Stream metadata must precede all images");
        }
        if (sequencePrepared == true) {
          throw new IIOException("Stream metadata already written!");
        }
        streamQTables = collectQTablesFromMetadata(localJPEGMetadata);
        if (debug) {
          System.out.println("after collecting from stream metadata, streamQTables.length is " + streamQTables.length);
        }
        if (streamQTables == null) {
          streamQTables = JPEG.getDefaultQTables();
        }
        streamDCHuffmanTables = collectHTablesFromMetadata(localJPEGMetadata, true);
        if (streamDCHuffmanTables == null) {
          streamDCHuffmanTables = JPEG.getDefaultHuffmanTables(true);
        }
        streamACHuffmanTables = collectHTablesFromMetadata(localJPEGMetadata, false);
        if (streamACHuffmanTables == null) {
          streamACHuffmanTables = JPEG.getDefaultHuffmanTables(false);
        }
        writeTables(structPointer, streamQTables, streamDCHuffmanTables, streamACHuffmanTables);
      }
      else
      {
        throw new IIOException("Stream metadata must be JPEG metadata");
      }
    }
    sequencePrepared = true;
  }
  
  /* Error */
  public void writeToSequence(IIOImage paramIIOImage, ImageWriteParam paramImageWriteParam)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 907	com/sun/imageio/plugins/jpeg/JPEGImageWriter:setThreadLock	()V
    //   4: aload_0
    //   5: getfield 849	com/sun/imageio/plugins/jpeg/JPEGImageWriter:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock;
    //   8: invokevirtual 944	com/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock:check	()V
    //   11: aload_0
    //   12: getfield 845	com/sun/imageio/plugins/jpeg/JPEGImageWriter:sequencePrepared	Z
    //   15: ifne +13 -> 28
    //   18: new 471	java/lang/IllegalStateException
    //   21: dup
    //   22: ldc 25
    //   24: invokespecial 993	java/lang/IllegalStateException:<init>	(Ljava/lang/String;)V
    //   27: athrow
    //   28: aload_0
    //   29: aconst_null
    //   30: aload_1
    //   31: aload_2
    //   32: invokevirtual 935	com/sun/imageio/plugins/jpeg/JPEGImageWriter:write	(Ljavax/imageio/metadata/IIOMetadata;Ljavax/imageio/IIOImage;Ljavax/imageio/ImageWriteParam;)V
    //   35: aload_0
    //   36: invokespecial 902	com/sun/imageio/plugins/jpeg/JPEGImageWriter:clearThreadLock	()V
    //   39: goto +10 -> 49
    //   42: astore_3
    //   43: aload_0
    //   44: invokespecial 902	com/sun/imageio/plugins/jpeg/JPEGImageWriter:clearThreadLock	()V
    //   47: aload_3
    //   48: athrow
    //   49: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	50	0	this	JPEGImageWriter
    //   0	50	1	paramIIOImage	IIOImage
    //   0	50	2	paramImageWriteParam	ImageWriteParam
    //   42	6	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   4	35	42	finally
  }
  
  /* Error */
  public void endWriteSequence()
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 907	com/sun/imageio/plugins/jpeg/JPEGImageWriter:setThreadLock	()V
    //   4: aload_0
    //   5: getfield 849	com/sun/imageio/plugins/jpeg/JPEGImageWriter:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock;
    //   8: invokevirtual 944	com/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock:check	()V
    //   11: aload_0
    //   12: getfield 845	com/sun/imageio/plugins/jpeg/JPEGImageWriter:sequencePrepared	Z
    //   15: ifne +13 -> 28
    //   18: new 471	java/lang/IllegalStateException
    //   21: dup
    //   22: ldc 25
    //   24: invokespecial 993	java/lang/IllegalStateException:<init>	(Ljava/lang/String;)V
    //   27: athrow
    //   28: aload_0
    //   29: iconst_0
    //   30: putfield 845	com/sun/imageio/plugins/jpeg/JPEGImageWriter:sequencePrepared	Z
    //   33: aload_0
    //   34: invokespecial 902	com/sun/imageio/plugins/jpeg/JPEGImageWriter:clearThreadLock	()V
    //   37: goto +10 -> 47
    //   40: astore_1
    //   41: aload_0
    //   42: invokespecial 902	com/sun/imageio/plugins/jpeg/JPEGImageWriter:clearThreadLock	()V
    //   45: aload_1
    //   46: athrow
    //   47: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	48	0	this	JPEGImageWriter
    //   40	6	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   4	33	40	finally
  }
  
  /* Error */
  public synchronized void abort()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 907	com/sun/imageio/plugins/jpeg/JPEGImageWriter:setThreadLock	()V
    //   4: aload_0
    //   5: invokespecial 1031	javax/imageio/ImageWriter:abort	()V
    //   8: aload_0
    //   9: aload_0
    //   10: getfield 837	com/sun/imageio/plugins/jpeg/JPEGImageWriter:structPointer	J
    //   13: invokespecial 914	com/sun/imageio/plugins/jpeg/JPEGImageWriter:abortWrite	(J)V
    //   16: aload_0
    //   17: invokespecial 902	com/sun/imageio/plugins/jpeg/JPEGImageWriter:clearThreadLock	()V
    //   20: goto +10 -> 30
    //   23: astore_1
    //   24: aload_0
    //   25: invokespecial 902	com/sun/imageio/plugins/jpeg/JPEGImageWriter:clearThreadLock	()V
    //   28: aload_1
    //   29: athrow
    //   30: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	31	0	this	JPEGImageWriter
    //   23	6	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   4	16	23	finally
  }
  
  /* Error */
  protected synchronized void clearAbortRequest()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 907	com/sun/imageio/plugins/jpeg/JPEGImageWriter:setThreadLock	()V
    //   4: aload_0
    //   5: getfield 849	com/sun/imageio/plugins/jpeg/JPEGImageWriter:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock;
    //   8: invokevirtual 944	com/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock:check	()V
    //   11: aload_0
    //   12: invokevirtual 908	com/sun/imageio/plugins/jpeg/JPEGImageWriter:abortRequested	()Z
    //   15: ifeq +23 -> 38
    //   18: aload_0
    //   19: invokespecial 1032	javax/imageio/ImageWriter:clearAbortRequest	()V
    //   22: aload_0
    //   23: aload_0
    //   24: getfield 837	com/sun/imageio/plugins/jpeg/JPEGImageWriter:structPointer	J
    //   27: invokespecial 916	com/sun/imageio/plugins/jpeg/JPEGImageWriter:resetWriter	(J)V
    //   30: aload_0
    //   31: aload_0
    //   32: getfield 837	com/sun/imageio/plugins/jpeg/JPEGImageWriter:structPointer	J
    //   35: invokespecial 917	com/sun/imageio/plugins/jpeg/JPEGImageWriter:setDest	(J)V
    //   38: aload_0
    //   39: invokespecial 902	com/sun/imageio/plugins/jpeg/JPEGImageWriter:clearThreadLock	()V
    //   42: goto +10 -> 52
    //   45: astore_1
    //   46: aload_0
    //   47: invokespecial 902	com/sun/imageio/plugins/jpeg/JPEGImageWriter:clearThreadLock	()V
    //   50: aload_1
    //   51: athrow
    //   52: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	53	0	this	JPEGImageWriter
    //   45	6	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   4	38	45	finally
  }
  
  private void resetInternalState()
  {
    resetWriter(structPointer);
    srcRas = null;
    raster = null;
    convertTosRGB = false;
    currentImage = 0;
    numScans = 0;
    metadata = null;
  }
  
  /* Error */
  public void reset()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 907	com/sun/imageio/plugins/jpeg/JPEGImageWriter:setThreadLock	()V
    //   4: aload_0
    //   5: getfield 849	com/sun/imageio/plugins/jpeg/JPEGImageWriter:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock;
    //   8: invokevirtual 944	com/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock:check	()V
    //   11: aload_0
    //   12: invokespecial 1033	javax/imageio/ImageWriter:reset	()V
    //   15: aload_0
    //   16: invokespecial 902	com/sun/imageio/plugins/jpeg/JPEGImageWriter:clearThreadLock	()V
    //   19: goto +10 -> 29
    //   22: astore_1
    //   23: aload_0
    //   24: invokespecial 902	com/sun/imageio/plugins/jpeg/JPEGImageWriter:clearThreadLock	()V
    //   27: aload_1
    //   28: athrow
    //   29: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	30	0	this	JPEGImageWriter
    //   22	6	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   4	15	22	finally
  }
  
  /* Error */
  public void dispose()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 907	com/sun/imageio/plugins/jpeg/JPEGImageWriter:setThreadLock	()V
    //   4: aload_0
    //   5: getfield 849	com/sun/imageio/plugins/jpeg/JPEGImageWriter:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock;
    //   8: invokevirtual 944	com/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock:check	()V
    //   11: aload_0
    //   12: getfield 837	com/sun/imageio/plugins/jpeg/JPEGImageWriter:structPointer	J
    //   15: lconst_0
    //   16: lcmp
    //   17: ifeq +17 -> 34
    //   20: aload_0
    //   21: getfield 866	com/sun/imageio/plugins/jpeg/JPEGImageWriter:disposerRecord	Lsun/java2d/DisposerRecord;
    //   24: invokeinterface 1066 1 0
    //   29: aload_0
    //   30: lconst_0
    //   31: putfield 837	com/sun/imageio/plugins/jpeg/JPEGImageWriter:structPointer	J
    //   34: aload_0
    //   35: invokespecial 902	com/sun/imageio/plugins/jpeg/JPEGImageWriter:clearThreadLock	()V
    //   38: goto +10 -> 48
    //   41: astore_1
    //   42: aload_0
    //   43: invokespecial 902	com/sun/imageio/plugins/jpeg/JPEGImageWriter:clearThreadLock	()V
    //   46: aload_1
    //   47: athrow
    //   48: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	49	0	this	JPEGImageWriter
    //   41	6	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   4	34	41	finally
  }
  
  /* Error */
  void warningOccurred(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 849	com/sun/imageio/plugins/jpeg/JPEGImageWriter:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock;
    //   4: invokestatic 945	com/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock:access$000	(Lcom/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock;)V
    //   7: iload_1
    //   8: iflt +9 -> 17
    //   11: iload_1
    //   12: bipush 15
    //   14: if_icmple +13 -> 27
    //   17: new 473	java/lang/InternalError
    //   20: dup
    //   21: ldc 7
    //   23: invokespecial 995	java/lang/InternalError:<init>	(Ljava/lang/String;)V
    //   26: athrow
    //   27: aload_0
    //   28: aload_0
    //   29: getfield 829	com/sun/imageio/plugins/jpeg/JPEGImageWriter:currentImage	I
    //   32: ldc 17
    //   34: iload_1
    //   35: invokestatic 994	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   38: invokevirtual 930	com/sun/imageio/plugins/jpeg/JPEGImageWriter:processWarningOccurred	(ILjava/lang/String;Ljava/lang/String;)V
    //   41: aload_0
    //   42: getfield 849	com/sun/imageio/plugins/jpeg/JPEGImageWriter:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock;
    //   45: invokestatic 946	com/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock:access$100	(Lcom/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock;)V
    //   48: goto +13 -> 61
    //   51: astore_2
    //   52: aload_0
    //   53: getfield 849	com/sun/imageio/plugins/jpeg/JPEGImageWriter:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock;
    //   56: invokestatic 946	com/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock:access$100	(Lcom/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock;)V
    //   59: aload_2
    //   60: athrow
    //   61: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	62	0	this	JPEGImageWriter
    //   0	62	1	paramInt	int
    //   51	9	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   7	41	51	finally
  }
  
  /* Error */
  void warningWithMessage(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 849	com/sun/imageio/plugins/jpeg/JPEGImageWriter:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock;
    //   4: invokestatic 945	com/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock:access$000	(Lcom/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock;)V
    //   7: aload_0
    //   8: aload_0
    //   9: getfield 829	com/sun/imageio/plugins/jpeg/JPEGImageWriter:currentImage	I
    //   12: aload_1
    //   13: invokevirtual 924	com/sun/imageio/plugins/jpeg/JPEGImageWriter:processWarningOccurred	(ILjava/lang/String;)V
    //   16: aload_0
    //   17: getfield 849	com/sun/imageio/plugins/jpeg/JPEGImageWriter:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock;
    //   20: invokestatic 946	com/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock:access$100	(Lcom/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock;)V
    //   23: goto +13 -> 36
    //   26: astore_2
    //   27: aload_0
    //   28: getfield 849	com/sun/imageio/plugins/jpeg/JPEGImageWriter:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock;
    //   31: invokestatic 946	com/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock:access$100	(Lcom/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock;)V
    //   34: aload_2
    //   35: athrow
    //   36: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	37	0	this	JPEGImageWriter
    //   0	37	1	paramString	String
    //   26	9	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   7	16	26	finally
  }
  
  /* Error */
  void thumbnailStarted(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 849	com/sun/imageio/plugins/jpeg/JPEGImageWriter:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock;
    //   4: invokestatic 945	com/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock:access$000	(Lcom/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock;)V
    //   7: aload_0
    //   8: aload_0
    //   9: getfield 829	com/sun/imageio/plugins/jpeg/JPEGImageWriter:currentImage	I
    //   12: iload_1
    //   13: invokevirtual 913	com/sun/imageio/plugins/jpeg/JPEGImageWriter:processThumbnailStarted	(II)V
    //   16: aload_0
    //   17: getfield 849	com/sun/imageio/plugins/jpeg/JPEGImageWriter:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock;
    //   20: invokestatic 946	com/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock:access$100	(Lcom/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock;)V
    //   23: goto +13 -> 36
    //   26: astore_2
    //   27: aload_0
    //   28: getfield 849	com/sun/imageio/plugins/jpeg/JPEGImageWriter:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock;
    //   31: invokestatic 946	com/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock:access$100	(Lcom/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock;)V
    //   34: aload_2
    //   35: athrow
    //   36: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	37	0	this	JPEGImageWriter
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
    //   1: getfield 849	com/sun/imageio/plugins/jpeg/JPEGImageWriter:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock;
    //   4: invokestatic 945	com/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock:access$000	(Lcom/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock;)V
    //   7: aload_0
    //   8: fload_1
    //   9: invokevirtual 910	com/sun/imageio/plugins/jpeg/JPEGImageWriter:processThumbnailProgress	(F)V
    //   12: aload_0
    //   13: getfield 849	com/sun/imageio/plugins/jpeg/JPEGImageWriter:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock;
    //   16: invokestatic 946	com/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock:access$100	(Lcom/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock;)V
    //   19: goto +13 -> 32
    //   22: astore_2
    //   23: aload_0
    //   24: getfield 849	com/sun/imageio/plugins/jpeg/JPEGImageWriter:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock;
    //   27: invokestatic 946	com/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock:access$100	(Lcom/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock;)V
    //   30: aload_2
    //   31: athrow
    //   32: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	33	0	this	JPEGImageWriter
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
    //   1: getfield 849	com/sun/imageio/plugins/jpeg/JPEGImageWriter:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock;
    //   4: invokestatic 945	com/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock:access$000	(Lcom/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock;)V
    //   7: aload_0
    //   8: invokevirtual 904	com/sun/imageio/plugins/jpeg/JPEGImageWriter:processThumbnailComplete	()V
    //   11: aload_0
    //   12: getfield 849	com/sun/imageio/plugins/jpeg/JPEGImageWriter:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock;
    //   15: invokestatic 946	com/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock:access$100	(Lcom/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock;)V
    //   18: goto +13 -> 31
    //   21: astore_1
    //   22: aload_0
    //   23: getfield 849	com/sun/imageio/plugins/jpeg/JPEGImageWriter:cbLock	Lcom/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock;
    //   26: invokestatic 946	com/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock:access$100	(Lcom/sun/imageio/plugins/jpeg/JPEGImageWriter$CallBackLock;)V
    //   29: aload_1
    //   30: athrow
    //   31: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	32	0	this	JPEGImageWriter
    //   21	9	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   7	11	21	finally
  }
  
  private void checkSOFBands(SOFMarkerSegment paramSOFMarkerSegment, int paramInt)
    throws IIOException
  {
    if ((paramSOFMarkerSegment != null) && (componentSpecs.length != paramInt)) {
      throw new IIOException("Metadata components != number of destination bands");
    }
  }
  
  private void checkJFIF(JFIFMarkerSegment paramJFIFMarkerSegment, ImageTypeSpecifier paramImageTypeSpecifier, boolean paramBoolean)
  {
    if ((paramJFIFMarkerSegment != null) && (!JPEG.isJFIFcompliant(paramImageTypeSpecifier, paramBoolean)))
    {
      ignoreJFIF = true;
      warningOccurred(paramBoolean ? 5 : 3);
    }
  }
  
  private void checkAdobe(AdobeMarkerSegment paramAdobeMarkerSegment, ImageTypeSpecifier paramImageTypeSpecifier, boolean paramBoolean)
  {
    if (paramAdobeMarkerSegment != null)
    {
      int i = JPEG.transformForType(paramImageTypeSpecifier, paramBoolean);
      if (transform != i)
      {
        warningOccurred(paramBoolean ? 6 : 4);
        if (i == -1) {
          ignoreAdobe = true;
        } else {
          newAdobeTransform = i;
        }
      }
    }
  }
  
  private int[] collectScans(JPEGMetadata paramJPEGMetadata, SOFMarkerSegment paramSOFMarkerSegment)
  {
    ArrayList localArrayList = new ArrayList();
    int i = 9;
    int j = 4;
    Object localObject = markerSequence.iterator();
    while (((Iterator)localObject).hasNext())
    {
      MarkerSegment localMarkerSegment = (MarkerSegment)((Iterator)localObject).next();
      if ((localMarkerSegment instanceof SOSMarkerSegment)) {
        localArrayList.add(localMarkerSegment);
      }
    }
    localObject = null;
    numScans = 0;
    if (!localArrayList.isEmpty())
    {
      numScans = localArrayList.size();
      localObject = new int[numScans * i];
      int k = 0;
      for (int m = 0; m < numScans; m++)
      {
        SOSMarkerSegment localSOSMarkerSegment = (SOSMarkerSegment)localArrayList.get(m);
        localObject[(k++)] = componentSpecs.length;
        for (int n = 0; n < j; n++) {
          if (n < componentSpecs.length)
          {
            int i1 = componentSpecs[n].componentSelector;
            for (int i2 = 0; i2 < componentSpecs.length; i2++) {
              if (i1 == componentSpecs[i2].componentId)
              {
                localObject[(k++)] = i2;
                break;
              }
            }
          }
          else
          {
            localObject[(k++)] = 0;
          }
        }
        localObject[(k++)] = startSpectralSelection;
        localObject[(k++)] = endSpectralSelection;
        localObject[(k++)] = approxHigh;
        localObject[(k++)] = approxLow;
      }
    }
    return (int[])localObject;
  }
  
  private JPEGQTable[] collectQTablesFromMetadata(JPEGMetadata paramJPEGMetadata)
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = markerSequence.iterator();
    while (localIterator.hasNext())
    {
      localObject = (MarkerSegment)localIterator.next();
      if ((localObject instanceof DQTMarkerSegment))
      {
        DQTMarkerSegment localDQTMarkerSegment = (DQTMarkerSegment)localObject;
        localArrayList.addAll(tables);
      }
    }
    Object localObject = null;
    if (localArrayList.size() != 0)
    {
      localObject = new JPEGQTable[localArrayList.size()];
      for (int i = 0; i < localObject.length; i++) {
        localObject[i] = new JPEGQTable(getdata);
      }
    }
    return (JPEGQTable[])localObject;
  }
  
  private JPEGHuffmanTable[] collectHTablesFromMetadata(JPEGMetadata paramJPEGMetadata, boolean paramBoolean)
    throws IIOException
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = markerSequence.iterator();
    Object localObject2;
    int i;
    while (localIterator.hasNext())
    {
      localObject1 = (MarkerSegment)localIterator.next();
      if ((localObject1 instanceof DHTMarkerSegment))
      {
        localObject2 = (DHTMarkerSegment)localObject1;
        for (i = 0; i < tables.size(); i++)
        {
          DHTMarkerSegment.Htable localHtable = (DHTMarkerSegment.Htable)tables.get(i);
          if (tableClass == (paramBoolean ? 0 : 1)) {
            localArrayList.add(localHtable);
          }
        }
      }
    }
    Object localObject1 = null;
    if (localArrayList.size() != 0)
    {
      localObject2 = new DHTMarkerSegment.Htable[localArrayList.size()];
      localArrayList.toArray((Object[])localObject2);
      localObject1 = new JPEGHuffmanTable[localArrayList.size()];
      for (i = 0; i < localObject1.length; i++)
      {
        localObject1[i] = null;
        for (int j = 0; j < localArrayList.size(); j++) {
          if (tableID == i)
          {
            if (localObject1[i] != null) {
              throw new IIOException("Metadata has duplicate Htables!");
            }
            localObject1[i] = new JPEGHuffmanTable(numCodes, values);
          }
        }
      }
    }
    return (JPEGHuffmanTable[])localObject1;
  }
  
  private int getSrcCSType(ImageTypeSpecifier paramImageTypeSpecifier)
  {
    return getSrcCSType(paramImageTypeSpecifier.getColorModel());
  }
  
  private int getSrcCSType(RenderedImage paramRenderedImage)
  {
    return getSrcCSType(paramRenderedImage.getColorModel());
  }
  
  private int getSrcCSType(ColorModel paramColorModel)
  {
    int i = 0;
    if (paramColorModel != null)
    {
      boolean bool = paramColorModel.hasAlpha();
      ColorSpace localColorSpace = paramColorModel.getColorSpace();
      switch (localColorSpace.getType())
      {
      case 6: 
        i = 1;
        break;
      case 5: 
        if (bool) {
          i = 6;
        } else {
          i = 2;
        }
        break;
      case 3: 
        if (bool) {
          i = 7;
        } else {
          i = 3;
        }
        break;
      case 13: 
        if (localColorSpace == JPEG.JCS.getYCC()) {
          if (bool) {
            i = 10;
          } else {
            i = 5;
          }
        }
      case 9: 
        i = 4;
      }
    }
    return i;
  }
  
  private int getDestCSType(ImageTypeSpecifier paramImageTypeSpecifier)
  {
    ColorModel localColorModel = paramImageTypeSpecifier.getColorModel();
    boolean bool = localColorModel.hasAlpha();
    ColorSpace localColorSpace = localColorModel.getColorSpace();
    int i = 0;
    switch (localColorSpace.getType())
    {
    case 6: 
      i = 1;
      break;
    case 5: 
      if (bool) {
        i = 6;
      } else {
        i = 2;
      }
      break;
    case 3: 
      if (bool) {
        i = 7;
      } else {
        i = 3;
      }
      break;
    case 13: 
      if (localColorSpace == JPEG.JCS.getYCC()) {
        if (bool) {
          i = 10;
        } else {
          i = 5;
        }
      }
    case 9: 
      i = 4;
    }
    return i;
  }
  
  private int getDefaultDestCSType(ImageTypeSpecifier paramImageTypeSpecifier)
  {
    return getDefaultDestCSType(paramImageTypeSpecifier.getColorModel());
  }
  
  private int getDefaultDestCSType(RenderedImage paramRenderedImage)
  {
    return getDefaultDestCSType(paramRenderedImage.getColorModel());
  }
  
  private int getDefaultDestCSType(ColorModel paramColorModel)
  {
    int i = 0;
    if (paramColorModel != null)
    {
      boolean bool = paramColorModel.hasAlpha();
      ColorSpace localColorSpace = paramColorModel.getColorSpace();
      switch (localColorSpace.getType())
      {
      case 6: 
        i = 1;
        break;
      case 5: 
        if (bool) {
          i = 7;
        } else {
          i = 3;
        }
        break;
      case 3: 
        if (bool) {
          i = 7;
        } else {
          i = 3;
        }
        break;
      case 13: 
        if (localColorSpace == JPEG.JCS.getYCC()) {
          if (bool) {
            i = 10;
          } else {
            i = 5;
          }
        }
      case 9: 
        i = 11;
      }
    }
    return i;
  }
  
  private boolean isSubsampled(SOFMarkerSegment.ComponentSpec[] paramArrayOfComponentSpec)
  {
    int i = 0HsamplingFactor;
    int j = 0VsamplingFactor;
    for (int k = 1; k < paramArrayOfComponentSpec.length; k++) {
      if ((HsamplingFactor != i) || (HsamplingFactor != i)) {
        return true;
      }
    }
    return false;
  }
  
  private static native void initWriterIDs(Class paramClass1, Class paramClass2);
  
  private native long initJPEGImageWriter();
  
  private native void setDest(long paramLong);
  
  private native boolean writeImage(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfInt1, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, JPEGQTable[] paramArrayOfJPEGQTable, boolean paramBoolean1, JPEGHuffmanTable[] paramArrayOfJPEGHuffmanTable1, JPEGHuffmanTable[] paramArrayOfJPEGHuffmanTable2, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, int paramInt9, int[] paramArrayOfInt2, int[] paramArrayOfInt3, int[] paramArrayOfInt4, int[] paramArrayOfInt5, int[] paramArrayOfInt6, boolean paramBoolean5, int paramInt10);
  
  private void writeMetadata()
    throws IOException
  {
    if (metadata == null)
    {
      if (writeDefaultJFIF) {
        JFIFMarkerSegment.writeDefaultJFIF(ios, thumbnails, iccProfile, this);
      }
      if (writeAdobe) {
        AdobeMarkerSegment.writeAdobeSegment(ios, newAdobeTransform);
      }
    }
    else
    {
      metadata.writeToStream(ios, ignoreJFIF, forceJFIF, thumbnails, iccProfile, ignoreAdobe, newAdobeTransform, this);
    }
  }
  
  private native void writeTables(long paramLong, JPEGQTable[] paramArrayOfJPEGQTable, JPEGHuffmanTable[] paramArrayOfJPEGHuffmanTable1, JPEGHuffmanTable[] paramArrayOfJPEGHuffmanTable2);
  
  private void grabPixels(int paramInt)
  {
    Object localObject1 = null;
    Object localObject2;
    if (indexed)
    {
      localObject1 = srcRas.createChild(sourceXOffset, sourceYOffset + paramInt, sourceWidth, 1, 0, 0, new int[] { 0 });
      boolean bool = indexCM.getTransparency() != 1;
      localObject2 = indexCM.convertToIntDiscrete((Raster)localObject1, bool);
      localObject1 = ((BufferedImage)localObject2).getRaster();
    }
    else
    {
      localObject1 = srcRas.createChild(sourceXOffset, sourceYOffset + paramInt, sourceWidth, 1, 0, 0, srcBands);
    }
    if (convertTosRGB)
    {
      if (debug) {
        System.out.println("Converting to sRGB");
      }
      converted = convertOp.filter((Raster)localObject1, converted);
      localObject1 = converted;
    }
    if (isAlphaPremultiplied)
    {
      WritableRaster localWritableRaster = ((Raster)localObject1).createCompatibleWritableRaster();
      localObject2 = null;
      localObject2 = ((Raster)localObject1).getPixels(((Raster)localObject1).getMinX(), ((Raster)localObject1).getMinY(), ((Raster)localObject1).getWidth(), ((Raster)localObject1).getHeight(), (int[])localObject2);
      localWritableRaster.setPixels(((Raster)localObject1).getMinX(), ((Raster)localObject1).getMinY(), ((Raster)localObject1).getWidth(), ((Raster)localObject1).getHeight(), (int[])localObject2);
      srcCM.coerceData(localWritableRaster, false);
      localObject1 = localWritableRaster.createChild(localWritableRaster.getMinX(), localWritableRaster.getMinY(), localWritableRaster.getWidth(), localWritableRaster.getHeight(), 0, 0, srcBands);
    }
    raster.setRect((Raster)localObject1);
    if ((paramInt > 7) && (paramInt % 8 == 0))
    {
      cbLock.lock();
      try
      {
        processImageProgress(paramInt / sourceHeight * 100.0F);
      }
      finally
      {
        cbLock.unlock();
      }
    }
  }
  
  private native void abortWrite(long paramLong);
  
  private native void resetWriter(long paramLong);
  
  private static native void disposeWriter(long paramLong);
  
  private void writeOutputData(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    cbLock.lock();
    try
    {
      ios.write(paramArrayOfByte, paramInt1, paramInt2);
    }
    finally
    {
      cbLock.unlock();
    }
  }
  
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
      throw new IllegalStateException("Attempt to clear thread lock form wrong thread. Locked thread: " + theThread + "; current thread: " + localThread);
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
    initWriterIDs(JPEGQTable.class, JPEGHuffmanTable.class);
  }
  
  private static class CallBackLock
  {
    private State lockState = State.Unlocked;
    
    CallBackLock() {}
    
    void check()
    {
      if (lockState != State.Unlocked) {
        throw new IllegalStateException("Access to the writer is not allowed");
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
  
  private static class JPEGWriterDisposerRecord
    implements DisposerRecord
  {
    private long pData;
    
    public JPEGWriterDisposerRecord(long paramLong)
    {
      pData = paramLong;
    }
    
    public synchronized void dispose()
    {
      if (pData != 0L)
      {
        JPEGImageWriter.disposeWriter(pData);
        pData = 0L;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\plugins\jpeg\JPEGImageWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */