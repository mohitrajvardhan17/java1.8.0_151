package com.sun.imageio.plugins.gif;

import com.sun.imageio.plugins.common.ReaderUtil;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.IIOException;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

public class GIFImageReader
  extends ImageReader
{
  ImageInputStream stream = null;
  boolean gotHeader = false;
  GIFStreamMetadata streamMetadata = null;
  int currIndex = -1;
  GIFImageMetadata imageMetadata = null;
  List imageStartPosition = new ArrayList();
  int imageMetadataLength;
  int numImages = -1;
  byte[] block = new byte['ÿ'];
  int blockLength = 0;
  int bitPos = 0;
  int nextByte = 0;
  int initCodeSize;
  int clearCode;
  int eofCode;
  int next32Bits = 0;
  boolean lastBlockFound = false;
  BufferedImage theImage = null;
  WritableRaster theTile = null;
  int width = -1;
  int height = -1;
  int streamX = -1;
  int streamY = -1;
  int rowsDone = 0;
  int interlacePass = 0;
  private byte[] fallbackColorTable = null;
  static final int[] interlaceIncrement = { 8, 8, 4, 2, -1 };
  static final int[] interlaceOffset = { 0, 4, 2, 1, -1 };
  Rectangle sourceRegion;
  int sourceXSubsampling;
  int sourceYSubsampling;
  int sourceMinProgressivePass;
  int sourceMaxProgressivePass;
  Point destinationOffset;
  Rectangle destinationRegion;
  int updateMinY;
  int updateYStep;
  boolean decodeThisRow = true;
  int destY = 0;
  byte[] rowBuf;
  private static byte[] defaultPalette = null;
  
  public GIFImageReader(ImageReaderSpi paramImageReaderSpi)
  {
    super(paramImageReaderSpi);
  }
  
  public void setInput(Object paramObject, boolean paramBoolean1, boolean paramBoolean2)
  {
    super.setInput(paramObject, paramBoolean1, paramBoolean2);
    if (paramObject != null)
    {
      if (!(paramObject instanceof ImageInputStream)) {
        throw new IllegalArgumentException("input not an ImageInputStream!");
      }
      stream = ((ImageInputStream)paramObject);
    }
    else
    {
      stream = null;
    }
    resetStreamSettings();
  }
  
  public int getNumImages(boolean paramBoolean)
    throws IIOException
  {
    if (stream == null) {
      throw new IllegalStateException("Input not set!");
    }
    if ((seekForwardOnly) && (paramBoolean)) {
      throw new IllegalStateException("seekForwardOnly and allowSearch can't both be true!");
    }
    if (numImages > 0) {
      return numImages;
    }
    if (paramBoolean) {
      numImages = (locateImage(Integer.MAX_VALUE) + 1);
    }
    return numImages;
  }
  
  private void checkIndex(int paramInt)
  {
    if (paramInt < minIndex) {
      throw new IndexOutOfBoundsException("imageIndex < minIndex!");
    }
    if (seekForwardOnly) {
      minIndex = paramInt;
    }
  }
  
  public int getWidth(int paramInt)
    throws IIOException
  {
    checkIndex(paramInt);
    int i = locateImage(paramInt);
    if (i != paramInt) {
      throw new IndexOutOfBoundsException();
    }
    readMetadata();
    return imageMetadata.imageWidth;
  }
  
  public int getHeight(int paramInt)
    throws IIOException
  {
    checkIndex(paramInt);
    int i = locateImage(paramInt);
    if (i != paramInt) {
      throw new IndexOutOfBoundsException();
    }
    readMetadata();
    return imageMetadata.imageHeight;
  }
  
  private ImageTypeSpecifier createIndexed(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt)
  {
    IndexColorModel localIndexColorModel;
    if (imageMetadata.transparentColorFlag)
    {
      int i = Math.min(imageMetadata.transparentColorIndex, paramArrayOfByte1.length - 1);
      localIndexColorModel = new IndexColorModel(paramInt, paramArrayOfByte1.length, paramArrayOfByte1, paramArrayOfByte2, paramArrayOfByte3, i);
    }
    else
    {
      localIndexColorModel = new IndexColorModel(paramInt, paramArrayOfByte1.length, paramArrayOfByte1, paramArrayOfByte2, paramArrayOfByte3);
    }
    Object localObject;
    if (paramInt == 8)
    {
      int[] arrayOfInt = { 0 };
      localObject = new PixelInterleavedSampleModel(0, 1, 1, 1, 1, arrayOfInt);
    }
    else
    {
      localObject = new MultiPixelPackedSampleModel(0, 1, 1, paramInt);
    }
    return new ImageTypeSpecifier(localIndexColorModel, (SampleModel)localObject);
  }
  
  public Iterator getImageTypes(int paramInt)
    throws IIOException
  {
    checkIndex(paramInt);
    int i = locateImage(paramInt);
    if (i != paramInt) {
      throw new IndexOutOfBoundsException();
    }
    readMetadata();
    ArrayList localArrayList = new ArrayList(1);
    byte[] arrayOfByte1;
    if (imageMetadata.localColorTable != null)
    {
      arrayOfByte1 = imageMetadata.localColorTable;
      fallbackColorTable = imageMetadata.localColorTable;
    }
    else
    {
      arrayOfByte1 = streamMetadata.globalColorTable;
    }
    if (arrayOfByte1 == null)
    {
      if (fallbackColorTable == null)
      {
        processWarningOccurred("Use default color table.");
        fallbackColorTable = getDefaultPalette();
      }
      arrayOfByte1 = fallbackColorTable;
    }
    int j = arrayOfByte1.length / 3;
    int k;
    if (j == 2) {
      k = 1;
    } else if (j == 4) {
      k = 2;
    } else if ((j == 8) || (j == 16)) {
      k = 4;
    } else {
      k = 8;
    }
    int m = 1 << k;
    byte[] arrayOfByte2 = new byte[m];
    byte[] arrayOfByte3 = new byte[m];
    byte[] arrayOfByte4 = new byte[m];
    int n = 0;
    for (int i1 = 0; i1 < j; i1++)
    {
      arrayOfByte2[i1] = arrayOfByte1[(n++)];
      arrayOfByte3[i1] = arrayOfByte1[(n++)];
      arrayOfByte4[i1] = arrayOfByte1[(n++)];
    }
    localArrayList.add(createIndexed(arrayOfByte2, arrayOfByte3, arrayOfByte4, k));
    return localArrayList.iterator();
  }
  
  public ImageReadParam getDefaultReadParam()
  {
    return new ImageReadParam();
  }
  
  public IIOMetadata getStreamMetadata()
    throws IIOException
  {
    readHeader();
    return streamMetadata;
  }
  
  public IIOMetadata getImageMetadata(int paramInt)
    throws IIOException
  {
    checkIndex(paramInt);
    int i = locateImage(paramInt);
    if (i != paramInt) {
      throw new IndexOutOfBoundsException("Bad image index!");
    }
    readMetadata();
    return imageMetadata;
  }
  
  private void initNext32Bits()
  {
    next32Bits = (block[0] & 0xFF);
    next32Bits |= (block[1] & 0xFF) << 8;
    next32Bits |= (block[2] & 0xFF) << 16;
    next32Bits |= block[3] << 24;
    nextByte = 4;
  }
  
  private int getCode(int paramInt1, int paramInt2)
    throws IOException
  {
    if (bitPos + paramInt1 > 32) {
      return eofCode;
    }
    int i = next32Bits >> bitPos & paramInt2;
    bitPos += paramInt1;
    while ((bitPos >= 8) && (!lastBlockFound))
    {
      next32Bits >>>= 8;
      bitPos -= 8;
      if (nextByte >= blockLength)
      {
        blockLength = stream.readUnsignedByte();
        if (blockLength == 0)
        {
          lastBlockFound = true;
          return i;
        }
        int j = blockLength;
        int k = 0;
        while (j > 0)
        {
          int m = stream.read(block, k, j);
          k += m;
          j -= m;
        }
        nextByte = 0;
      }
      next32Bits |= block[(nextByte++)] << 24;
    }
    return i;
  }
  
  public void initializeStringTable(int[] paramArrayOfInt1, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int[] paramArrayOfInt2)
  {
    int i = 1 << initCodeSize;
    for (int j = 0; j < i; j++)
    {
      paramArrayOfInt1[j] = -1;
      paramArrayOfByte1[j] = ((byte)j);
      paramArrayOfByte2[j] = ((byte)j);
      paramArrayOfInt2[j] = 1;
    }
    for (j = i; j < 4096; j++)
    {
      paramArrayOfInt1[j] = -1;
      paramArrayOfInt2[j] = 1;
    }
  }
  
  private void outputRow()
  {
    int i = Math.min(sourceRegion.width, destinationRegion.width * sourceXSubsampling);
    int j = destinationRegion.x;
    if (sourceXSubsampling == 1)
    {
      theTile.setDataElements(j, destY, i, 1, rowBuf);
    }
    else
    {
      int k = 0;
      while (k < i)
      {
        theTile.setSample(j, destY, 0, rowBuf[k] & 0xFF);
        k += sourceXSubsampling;
        j++;
      }
    }
    if (updateListeners != null)
    {
      int[] arrayOfInt = { 0 };
      processImageUpdate(theImage, j, destY, i, 1, 1, updateYStep, arrayOfInt);
    }
  }
  
  private void computeDecodeThisRow()
  {
    decodeThisRow = ((destY < destinationRegion.y + destinationRegion.height) && (streamY >= sourceRegion.y) && (streamY < sourceRegion.y + sourceRegion.height) && ((streamY - sourceRegion.y) % sourceYSubsampling == 0));
  }
  
  private void outputPixels(byte[] paramArrayOfByte, int paramInt)
  {
    if ((interlacePass < sourceMinProgressivePass) || (interlacePass > sourceMaxProgressivePass)) {
      return;
    }
    for (int i = 0; i < paramInt; i++)
    {
      if (streamX >= sourceRegion.x) {
        rowBuf[(streamX - sourceRegion.x)] = paramArrayOfByte[i];
      }
      streamX += 1;
      if (streamX == width)
      {
        rowsDone += 1;
        processImageProgress(100.0F * rowsDone / height);
        if (decodeThisRow) {
          outputRow();
        }
        streamX = 0;
        if (imageMetadata.interlaceFlag)
        {
          streamY += interlaceIncrement[interlacePass];
          if (streamY >= height)
          {
            if (updateListeners != null) {
              processPassComplete(theImage);
            }
            interlacePass += 1;
            if (interlacePass > sourceMaxProgressivePass) {
              return;
            }
            streamY = interlaceOffset[interlacePass];
            startPass(interlacePass);
          }
        }
        else
        {
          streamY += 1;
        }
        destY = (destinationRegion.y + (streamY - sourceRegion.y) / sourceYSubsampling);
        computeDecodeThisRow();
      }
    }
  }
  
  private void readHeader()
    throws IIOException
  {
    if (gotHeader) {
      return;
    }
    if (stream == null) {
      throw new IllegalStateException("Input not set!");
    }
    streamMetadata = new GIFStreamMetadata();
    try
    {
      stream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
      byte[] arrayOfByte = new byte[6];
      stream.readFully(arrayOfByte);
      StringBuffer localStringBuffer = new StringBuffer(3);
      localStringBuffer.append((char)arrayOfByte[3]);
      localStringBuffer.append((char)arrayOfByte[4]);
      localStringBuffer.append((char)arrayOfByte[5]);
      streamMetadata.version = localStringBuffer.toString();
      streamMetadata.logicalScreenWidth = stream.readUnsignedShort();
      streamMetadata.logicalScreenHeight = stream.readUnsignedShort();
      int i = stream.readUnsignedByte();
      int j = (i & 0x80) != 0 ? 1 : 0;
      streamMetadata.colorResolution = ((i >> 4 & 0x7) + 1);
      streamMetadata.sortFlag = ((i & 0x8) != 0);
      int k = 1 << (i & 0x7) + 1;
      streamMetadata.backgroundColorIndex = stream.readUnsignedByte();
      streamMetadata.pixelAspectRatio = stream.readUnsignedByte();
      if (j != 0)
      {
        streamMetadata.globalColorTable = new byte[3 * k];
        stream.readFully(streamMetadata.globalColorTable);
      }
      else
      {
        streamMetadata.globalColorTable = null;
      }
      imageStartPosition.add(Long.valueOf(stream.getStreamPosition()));
    }
    catch (IOException localIOException)
    {
      throw new IIOException("I/O error reading header!", localIOException);
    }
    gotHeader = true;
  }
  
  /* Error */
  private boolean skipImage()
    throws IIOException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 555	com/sun/imageio/plugins/gif/GIFImageReader:stream	Ljavax/imageio/stream/ImageInputStream;
    //   4: invokeinterface 653 1 0
    //   9: istore_1
    //   10: iload_1
    //   11: bipush 44
    //   13: if_icmpne +95 -> 108
    //   16: aload_0
    //   17: getfield 555	com/sun/imageio/plugins/gif/GIFImageReader:stream	Ljavax/imageio/stream/ImageInputStream;
    //   20: bipush 8
    //   22: invokeinterface 656 2 0
    //   27: pop
    //   28: aload_0
    //   29: getfield 555	com/sun/imageio/plugins/gif/GIFImageReader:stream	Ljavax/imageio/stream/ImageInputStream;
    //   32: invokeinterface 653 1 0
    //   37: istore_2
    //   38: iload_2
    //   39: sipush 128
    //   42: iand
    //   43: ifeq +25 -> 68
    //   46: iload_2
    //   47: bipush 7
    //   49: iand
    //   50: iconst_1
    //   51: iadd
    //   52: istore_3
    //   53: aload_0
    //   54: getfield 555	com/sun/imageio/plugins/gif/GIFImageReader:stream	Ljavax/imageio/stream/ImageInputStream;
    //   57: iconst_3
    //   58: iconst_1
    //   59: iload_3
    //   60: ishl
    //   61: imul
    //   62: invokeinterface 656 2 0
    //   67: pop
    //   68: aload_0
    //   69: getfield 555	com/sun/imageio/plugins/gif/GIFImageReader:stream	Ljavax/imageio/stream/ImageInputStream;
    //   72: iconst_1
    //   73: invokeinterface 656 2 0
    //   78: pop
    //   79: iconst_0
    //   80: istore_3
    //   81: aload_0
    //   82: getfield 555	com/sun/imageio/plugins/gif/GIFImageReader:stream	Ljavax/imageio/stream/ImageInputStream;
    //   85: invokeinterface 653 1 0
    //   90: istore_3
    //   91: aload_0
    //   92: getfield 555	com/sun/imageio/plugins/gif/GIFImageReader:stream	Ljavax/imageio/stream/ImageInputStream;
    //   95: iload_3
    //   96: invokeinterface 656 2 0
    //   101: pop
    //   102: iload_3
    //   103: ifgt -22 -> 81
    //   106: iconst_1
    //   107: ireturn
    //   108: iload_1
    //   109: bipush 59
    //   111: if_icmpne +5 -> 116
    //   114: iconst_0
    //   115: ireturn
    //   116: iload_1
    //   117: bipush 33
    //   119: if_icmpne +43 -> 162
    //   122: aload_0
    //   123: getfield 555	com/sun/imageio/plugins/gif/GIFImageReader:stream	Ljavax/imageio/stream/ImageInputStream;
    //   126: invokeinterface 653 1 0
    //   131: istore_2
    //   132: iconst_0
    //   133: istore_3
    //   134: aload_0
    //   135: getfield 555	com/sun/imageio/plugins/gif/GIFImageReader:stream	Ljavax/imageio/stream/ImageInputStream;
    //   138: invokeinterface 653 1 0
    //   143: istore_3
    //   144: aload_0
    //   145: getfield 555	com/sun/imageio/plugins/gif/GIFImageReader:stream	Ljavax/imageio/stream/ImageInputStream;
    //   148: iload_3
    //   149: invokeinterface 656 2 0
    //   154: pop
    //   155: iload_3
    //   156: ifgt -22 -> 134
    //   159: goto +36 -> 195
    //   162: iload_1
    //   163: ifne +5 -> 168
    //   166: iconst_0
    //   167: ireturn
    //   168: iconst_0
    //   169: istore_2
    //   170: aload_0
    //   171: getfield 555	com/sun/imageio/plugins/gif/GIFImageReader:stream	Ljavax/imageio/stream/ImageInputStream;
    //   174: invokeinterface 653 1 0
    //   179: istore_2
    //   180: aload_0
    //   181: getfield 555	com/sun/imageio/plugins/gif/GIFImageReader:stream	Ljavax/imageio/stream/ImageInputStream;
    //   184: iload_2
    //   185: invokeinterface 656 2 0
    //   190: pop
    //   191: iload_2
    //   192: ifgt -22 -> 170
    //   195: goto -195 -> 0
    //   198: astore_1
    //   199: iconst_0
    //   200: ireturn
    //   201: astore_1
    //   202: new 283	javax/imageio/IIOException
    //   205: dup
    //   206: ldc 7
    //   208: aload_1
    //   209: invokespecial 639	javax/imageio/IIOException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   212: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	213	0	this	GIFImageReader
    //   9	154	1	i	int
    //   198	1	1	localEOFException	java.io.EOFException
    //   201	8	1	localIOException	IOException
    //   37	155	2	j	int
    //   52	104	3	k	int
    // Exception table:
    //   from	to	target	type
    //   0	107	198	java/io/EOFException
    //   108	115	198	java/io/EOFException
    //   116	167	198	java/io/EOFException
    //   168	198	198	java/io/EOFException
    //   0	107	201	java/io/IOException
    //   108	115	201	java/io/IOException
    //   116	167	201	java/io/IOException
    //   168	198	201	java/io/IOException
  }
  
  private int locateImage(int paramInt)
    throws IIOException
  {
    readHeader();
    try
    {
      int i = Math.min(paramInt, imageStartPosition.size() - 1);
      Long localLong1 = (Long)imageStartPosition.get(i);
      stream.seek(localLong1.longValue());
      while (i < paramInt)
      {
        if (!skipImage())
        {
          i--;
          return i;
        }
        Long localLong2 = new Long(stream.getStreamPosition());
        imageStartPosition.add(localLong2);
        i++;
      }
    }
    catch (IOException localIOException)
    {
      throw new IIOException("Couldn't seek!", localIOException);
    }
    if (currIndex != paramInt) {
      imageMetadata = null;
    }
    currIndex = paramInt;
    return paramInt;
  }
  
  private byte[] concatenateBlocks()
    throws IOException
  {
    byte[] arrayOfByte;
    for (Object localObject = new byte[0];; localObject = arrayOfByte)
    {
      int i = stream.readUnsignedByte();
      if (i == 0) {
        break;
      }
      arrayOfByte = new byte[localObject.length + i];
      System.arraycopy(localObject, 0, arrayOfByte, 0, localObject.length);
      stream.readFully(arrayOfByte, localObject.length, i);
    }
    return (byte[])localObject;
  }
  
  /* Error */
  private void readMetadata()
    throws IIOException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 555	com/sun/imageio/plugins/gif/GIFImageReader:stream	Ljavax/imageio/stream/ImageInputStream;
    //   4: ifnonnull +13 -> 17
    //   7: new 272	java/lang/IllegalStateException
    //   10: dup
    //   11: ldc 11
    //   13: invokespecial 620	java/lang/IllegalStateException:<init>	(Ljava/lang/String;)V
    //   16: athrow
    //   17: aload_0
    //   18: new 257	com/sun/imageio/plugins/gif/GIFImageMetadata
    //   21: dup
    //   22: invokespecial 570	com/sun/imageio/plugins/gif/GIFImageMetadata:<init>	()V
    //   25: putfield 546	com/sun/imageio/plugins/gif/GIFImageReader:imageMetadata	Lcom/sun/imageio/plugins/gif/GIFImageMetadata;
    //   28: aload_0
    //   29: getfield 555	com/sun/imageio/plugins/gif/GIFImageReader:stream	Ljavax/imageio/stream/ImageInputStream;
    //   32: invokeinterface 655 1 0
    //   37: lstore_1
    //   38: aload_0
    //   39: getfield 555	com/sun/imageio/plugins/gif/GIFImageReader:stream	Ljavax/imageio/stream/ImageInputStream;
    //   42: invokeinterface 653 1 0
    //   47: istore_3
    //   48: iload_3
    //   49: bipush 44
    //   51: if_icmpne +207 -> 258
    //   54: aload_0
    //   55: getfield 546	com/sun/imageio/plugins/gif/GIFImageReader:imageMetadata	Lcom/sun/imageio/plugins/gif/GIFImageMetadata;
    //   58: aload_0
    //   59: getfield 555	com/sun/imageio/plugins/gif/GIFImageReader:stream	Ljavax/imageio/stream/ImageInputStream;
    //   62: invokeinterface 654 1 0
    //   67: putfield 491	com/sun/imageio/plugins/gif/GIFImageMetadata:imageLeftPosition	I
    //   70: aload_0
    //   71: getfield 546	com/sun/imageio/plugins/gif/GIFImageReader:imageMetadata	Lcom/sun/imageio/plugins/gif/GIFImageMetadata;
    //   74: aload_0
    //   75: getfield 555	com/sun/imageio/plugins/gif/GIFImageReader:stream	Ljavax/imageio/stream/ImageInputStream;
    //   78: invokeinterface 654 1 0
    //   83: putfield 492	com/sun/imageio/plugins/gif/GIFImageMetadata:imageTopPosition	I
    //   86: aload_0
    //   87: getfield 546	com/sun/imageio/plugins/gif/GIFImageReader:imageMetadata	Lcom/sun/imageio/plugins/gif/GIFImageMetadata;
    //   90: aload_0
    //   91: getfield 555	com/sun/imageio/plugins/gif/GIFImageReader:stream	Ljavax/imageio/stream/ImageInputStream;
    //   94: invokeinterface 654 1 0
    //   99: putfield 493	com/sun/imageio/plugins/gif/GIFImageMetadata:imageWidth	I
    //   102: aload_0
    //   103: getfield 546	com/sun/imageio/plugins/gif/GIFImageReader:imageMetadata	Lcom/sun/imageio/plugins/gif/GIFImageMetadata;
    //   106: aload_0
    //   107: getfield 555	com/sun/imageio/plugins/gif/GIFImageReader:stream	Ljavax/imageio/stream/ImageInputStream;
    //   110: invokeinterface 654 1 0
    //   115: putfield 490	com/sun/imageio/plugins/gif/GIFImageMetadata:imageHeight	I
    //   118: aload_0
    //   119: getfield 555	com/sun/imageio/plugins/gif/GIFImageReader:stream	Ljavax/imageio/stream/ImageInputStream;
    //   122: invokeinterface 653 1 0
    //   127: istore 4
    //   129: iload 4
    //   131: sipush 128
    //   134: iand
    //   135: ifeq +7 -> 142
    //   138: iconst_1
    //   139: goto +4 -> 143
    //   142: iconst_0
    //   143: istore 5
    //   145: aload_0
    //   146: getfield 546	com/sun/imageio/plugins/gif/GIFImageReader:imageMetadata	Lcom/sun/imageio/plugins/gif/GIFImageMetadata;
    //   149: iload 4
    //   151: bipush 64
    //   153: iand
    //   154: ifeq +7 -> 161
    //   157: iconst_1
    //   158: goto +4 -> 162
    //   161: iconst_0
    //   162: putfield 502	com/sun/imageio/plugins/gif/GIFImageMetadata:interlaceFlag	Z
    //   165: aload_0
    //   166: getfield 546	com/sun/imageio/plugins/gif/GIFImageReader:imageMetadata	Lcom/sun/imageio/plugins/gif/GIFImageMetadata;
    //   169: iload 4
    //   171: bipush 32
    //   173: iand
    //   174: ifeq +7 -> 181
    //   177: iconst_1
    //   178: goto +4 -> 182
    //   181: iconst_0
    //   182: putfield 503	com/sun/imageio/plugins/gif/GIFImageMetadata:sortFlag	Z
    //   185: iconst_1
    //   186: iload 4
    //   188: bipush 7
    //   190: iand
    //   191: iconst_1
    //   192: iadd
    //   193: ishl
    //   194: istore 6
    //   196: iload 5
    //   198: ifeq +35 -> 233
    //   201: aload_0
    //   202: getfield 546	com/sun/imageio/plugins/gif/GIFImageReader:imageMetadata	Lcom/sun/imageio/plugins/gif/GIFImageMetadata;
    //   205: iconst_3
    //   206: iload 6
    //   208: imul
    //   209: newarray <illegal type>
    //   211: putfield 506	com/sun/imageio/plugins/gif/GIFImageMetadata:localColorTable	[B
    //   214: aload_0
    //   215: getfield 555	com/sun/imageio/plugins/gif/GIFImageReader:stream	Ljavax/imageio/stream/ImageInputStream;
    //   218: aload_0
    //   219: getfield 546	com/sun/imageio/plugins/gif/GIFImageReader:imageMetadata	Lcom/sun/imageio/plugins/gif/GIFImageMetadata;
    //   222: getfield 506	com/sun/imageio/plugins/gif/GIFImageMetadata:localColorTable	[B
    //   225: invokeinterface 658 2 0
    //   230: goto +11 -> 241
    //   233: aload_0
    //   234: getfield 546	com/sun/imageio/plugins/gif/GIFImageReader:imageMetadata	Lcom/sun/imageio/plugins/gif/GIFImageMetadata;
    //   237: aconst_null
    //   238: putfield 506	com/sun/imageio/plugins/gif/GIFImageMetadata:localColorTable	[B
    //   241: aload_0
    //   242: aload_0
    //   243: getfield 555	com/sun/imageio/plugins/gif/GIFImageReader:stream	Ljavax/imageio/stream/ImageInputStream;
    //   246: invokeinterface 655 1 0
    //   251: lload_1
    //   252: lsub
    //   253: l2i
    //   254: putfield 519	com/sun/imageio/plugins/gif/GIFImageReader:imageMetadataLength	I
    //   257: return
    //   258: iload_3
    //   259: bipush 33
    //   261: if_icmpne +627 -> 888
    //   264: aload_0
    //   265: getfield 555	com/sun/imageio/plugins/gif/GIFImageReader:stream	Ljavax/imageio/stream/ImageInputStream;
    //   268: invokeinterface 653 1 0
    //   273: istore 4
    //   275: iload 4
    //   277: sipush 249
    //   280: if_icmpne +122 -> 402
    //   283: aload_0
    //   284: getfield 555	com/sun/imageio/plugins/gif/GIFImageReader:stream	Ljavax/imageio/stream/ImageInputStream;
    //   287: invokeinterface 653 1 0
    //   292: istore 5
    //   294: aload_0
    //   295: getfield 555	com/sun/imageio/plugins/gif/GIFImageReader:stream	Ljavax/imageio/stream/ImageInputStream;
    //   298: invokeinterface 653 1 0
    //   303: istore 6
    //   305: aload_0
    //   306: getfield 546	com/sun/imageio/plugins/gif/GIFImageReader:imageMetadata	Lcom/sun/imageio/plugins/gif/GIFImageMetadata;
    //   309: iload 6
    //   311: iconst_2
    //   312: ishr
    //   313: iconst_3
    //   314: iand
    //   315: putfield 489	com/sun/imageio/plugins/gif/GIFImageMetadata:disposalMethod	I
    //   318: aload_0
    //   319: getfield 546	com/sun/imageio/plugins/gif/GIFImageReader:imageMetadata	Lcom/sun/imageio/plugins/gif/GIFImageMetadata;
    //   322: iload 6
    //   324: iconst_2
    //   325: iand
    //   326: ifeq +7 -> 333
    //   329: iconst_1
    //   330: goto +4 -> 334
    //   333: iconst_0
    //   334: putfield 505	com/sun/imageio/plugins/gif/GIFImageMetadata:userInputFlag	Z
    //   337: aload_0
    //   338: getfield 546	com/sun/imageio/plugins/gif/GIFImageReader:imageMetadata	Lcom/sun/imageio/plugins/gif/GIFImageMetadata;
    //   341: iload 6
    //   343: iconst_1
    //   344: iand
    //   345: ifeq +7 -> 352
    //   348: iconst_1
    //   349: goto +4 -> 353
    //   352: iconst_0
    //   353: putfield 504	com/sun/imageio/plugins/gif/GIFImageMetadata:transparentColorFlag	Z
    //   356: aload_0
    //   357: getfield 546	com/sun/imageio/plugins/gif/GIFImageReader:imageMetadata	Lcom/sun/imageio/plugins/gif/GIFImageMetadata;
    //   360: aload_0
    //   361: getfield 555	com/sun/imageio/plugins/gif/GIFImageReader:stream	Ljavax/imageio/stream/ImageInputStream;
    //   364: invokeinterface 654 1 0
    //   369: putfield 488	com/sun/imageio/plugins/gif/GIFImageMetadata:delayTime	I
    //   372: aload_0
    //   373: getfield 546	com/sun/imageio/plugins/gif/GIFImageReader:imageMetadata	Lcom/sun/imageio/plugins/gif/GIFImageMetadata;
    //   376: aload_0
    //   377: getfield 555	com/sun/imageio/plugins/gif/GIFImageReader:stream	Ljavax/imageio/stream/ImageInputStream;
    //   380: invokeinterface 653 1 0
    //   385: putfield 500	com/sun/imageio/plugins/gif/GIFImageMetadata:transparentColorIndex	I
    //   388: aload_0
    //   389: getfield 555	com/sun/imageio/plugins/gif/GIFImageReader:stream	Ljavax/imageio/stream/ImageInputStream;
    //   392: invokeinterface 653 1 0
    //   397: istore 7
    //   399: goto +486 -> 885
    //   402: iload 4
    //   404: iconst_1
    //   405: if_icmpne +164 -> 569
    //   408: aload_0
    //   409: getfield 555	com/sun/imageio/plugins/gif/GIFImageReader:stream	Ljavax/imageio/stream/ImageInputStream;
    //   412: invokeinterface 653 1 0
    //   417: istore 5
    //   419: aload_0
    //   420: getfield 546	com/sun/imageio/plugins/gif/GIFImageReader:imageMetadata	Lcom/sun/imageio/plugins/gif/GIFImageMetadata;
    //   423: iconst_1
    //   424: putfield 501	com/sun/imageio/plugins/gif/GIFImageMetadata:hasPlainTextExtension	Z
    //   427: aload_0
    //   428: getfield 546	com/sun/imageio/plugins/gif/GIFImageReader:imageMetadata	Lcom/sun/imageio/plugins/gif/GIFImageMetadata;
    //   431: aload_0
    //   432: getfield 555	com/sun/imageio/plugins/gif/GIFImageReader:stream	Ljavax/imageio/stream/ImageInputStream;
    //   435: invokeinterface 654 1 0
    //   440: putfield 497	com/sun/imageio/plugins/gif/GIFImageMetadata:textGridLeft	I
    //   443: aload_0
    //   444: getfield 546	com/sun/imageio/plugins/gif/GIFImageReader:imageMetadata	Lcom/sun/imageio/plugins/gif/GIFImageMetadata;
    //   447: aload_0
    //   448: getfield 555	com/sun/imageio/plugins/gif/GIFImageReader:stream	Ljavax/imageio/stream/ImageInputStream;
    //   451: invokeinterface 654 1 0
    //   456: putfield 498	com/sun/imageio/plugins/gif/GIFImageMetadata:textGridTop	I
    //   459: aload_0
    //   460: getfield 546	com/sun/imageio/plugins/gif/GIFImageReader:imageMetadata	Lcom/sun/imageio/plugins/gif/GIFImageMetadata;
    //   463: aload_0
    //   464: getfield 555	com/sun/imageio/plugins/gif/GIFImageReader:stream	Ljavax/imageio/stream/ImageInputStream;
    //   467: invokeinterface 654 1 0
    //   472: putfield 499	com/sun/imageio/plugins/gif/GIFImageMetadata:textGridWidth	I
    //   475: aload_0
    //   476: getfield 546	com/sun/imageio/plugins/gif/GIFImageReader:imageMetadata	Lcom/sun/imageio/plugins/gif/GIFImageMetadata;
    //   479: aload_0
    //   480: getfield 555	com/sun/imageio/plugins/gif/GIFImageReader:stream	Ljavax/imageio/stream/ImageInputStream;
    //   483: invokeinterface 654 1 0
    //   488: putfield 496	com/sun/imageio/plugins/gif/GIFImageMetadata:textGridHeight	I
    //   491: aload_0
    //   492: getfield 546	com/sun/imageio/plugins/gif/GIFImageReader:imageMetadata	Lcom/sun/imageio/plugins/gif/GIFImageMetadata;
    //   495: aload_0
    //   496: getfield 555	com/sun/imageio/plugins/gif/GIFImageReader:stream	Ljavax/imageio/stream/ImageInputStream;
    //   499: invokeinterface 653 1 0
    //   504: putfield 487	com/sun/imageio/plugins/gif/GIFImageMetadata:characterCellWidth	I
    //   507: aload_0
    //   508: getfield 546	com/sun/imageio/plugins/gif/GIFImageReader:imageMetadata	Lcom/sun/imageio/plugins/gif/GIFImageMetadata;
    //   511: aload_0
    //   512: getfield 555	com/sun/imageio/plugins/gif/GIFImageReader:stream	Ljavax/imageio/stream/ImageInputStream;
    //   515: invokeinterface 653 1 0
    //   520: putfield 486	com/sun/imageio/plugins/gif/GIFImageMetadata:characterCellHeight	I
    //   523: aload_0
    //   524: getfield 546	com/sun/imageio/plugins/gif/GIFImageReader:imageMetadata	Lcom/sun/imageio/plugins/gif/GIFImageMetadata;
    //   527: aload_0
    //   528: getfield 555	com/sun/imageio/plugins/gif/GIFImageReader:stream	Ljavax/imageio/stream/ImageInputStream;
    //   531: invokeinterface 653 1 0
    //   536: putfield 495	com/sun/imageio/plugins/gif/GIFImageMetadata:textForegroundColor	I
    //   539: aload_0
    //   540: getfield 546	com/sun/imageio/plugins/gif/GIFImageReader:imageMetadata	Lcom/sun/imageio/plugins/gif/GIFImageMetadata;
    //   543: aload_0
    //   544: getfield 555	com/sun/imageio/plugins/gif/GIFImageReader:stream	Ljavax/imageio/stream/ImageInputStream;
    //   547: invokeinterface 653 1 0
    //   552: putfield 494	com/sun/imageio/plugins/gif/GIFImageMetadata:textBackgroundColor	I
    //   555: aload_0
    //   556: getfield 546	com/sun/imageio/plugins/gif/GIFImageReader:imageMetadata	Lcom/sun/imageio/plugins/gif/GIFImageMetadata;
    //   559: aload_0
    //   560: invokespecial 582	com/sun/imageio/plugins/gif/GIFImageReader:concatenateBlocks	()[B
    //   563: putfield 507	com/sun/imageio/plugins/gif/GIFImageMetadata:text	[B
    //   566: goto +319 -> 885
    //   569: iload 4
    //   571: sipush 254
    //   574: if_icmpne +51 -> 625
    //   577: aload_0
    //   578: invokespecial 582	com/sun/imageio/plugins/gif/GIFImageReader:concatenateBlocks	()[B
    //   581: astore 5
    //   583: aload_0
    //   584: getfield 546	com/sun/imageio/plugins/gif/GIFImageReader:imageMetadata	Lcom/sun/imageio/plugins/gif/GIFImageMetadata;
    //   587: getfield 511	com/sun/imageio/plugins/gif/GIFImageMetadata:comments	Ljava/util/List;
    //   590: ifnonnull +17 -> 607
    //   593: aload_0
    //   594: getfield 546	com/sun/imageio/plugins/gif/GIFImageReader:imageMetadata	Lcom/sun/imageio/plugins/gif/GIFImageMetadata;
    //   597: new 280	java/util/ArrayList
    //   600: dup
    //   601: invokespecial 636	java/util/ArrayList:<init>	()V
    //   604: putfield 511	com/sun/imageio/plugins/gif/GIFImageMetadata:comments	Ljava/util/List;
    //   607: aload_0
    //   608: getfield 546	com/sun/imageio/plugins/gif/GIFImageReader:imageMetadata	Lcom/sun/imageio/plugins/gif/GIFImageMetadata;
    //   611: getfield 511	com/sun/imageio/plugins/gif/GIFImageMetadata:comments	Ljava/util/List;
    //   614: aload 5
    //   616: invokeinterface 651 2 0
    //   621: pop
    //   622: goto +263 -> 885
    //   625: iload 4
    //   627: sipush 255
    //   630: if_icmpne +224 -> 854
    //   633: aload_0
    //   634: getfield 555	com/sun/imageio/plugins/gif/GIFImageReader:stream	Ljavax/imageio/stream/ImageInputStream;
    //   637: invokeinterface 653 1 0
    //   642: istore 5
    //   644: bipush 8
    //   646: newarray <illegal type>
    //   648: astore 6
    //   650: iconst_3
    //   651: newarray <illegal type>
    //   653: astore 7
    //   655: iload 5
    //   657: newarray <illegal type>
    //   659: astore 8
    //   661: aload_0
    //   662: getfield 555	com/sun/imageio/plugins/gif/GIFImageReader:stream	Ljavax/imageio/stream/ImageInputStream;
    //   665: aload 8
    //   667: invokeinterface 658 2 0
    //   672: aload_0
    //   673: aload 8
    //   675: iconst_0
    //   676: aload 6
    //   678: invokespecial 591	com/sun/imageio/plugins/gif/GIFImageReader:copyData	([BI[B)I
    //   681: istore 9
    //   683: aload_0
    //   684: aload 8
    //   686: iload 9
    //   688: aload 7
    //   690: invokespecial 591	com/sun/imageio/plugins/gif/GIFImageReader:copyData	([BI[B)I
    //   693: istore 9
    //   695: aload_0
    //   696: invokespecial 582	com/sun/imageio/plugins/gif/GIFImageReader:concatenateBlocks	()[B
    //   699: astore 10
    //   701: iload 9
    //   703: iload 5
    //   705: if_icmpge +49 -> 754
    //   708: iload 5
    //   710: iload 9
    //   712: isub
    //   713: istore 11
    //   715: iload 11
    //   717: aload 10
    //   719: arraylength
    //   720: iadd
    //   721: newarray <illegal type>
    //   723: astore 12
    //   725: aload 8
    //   727: iload 9
    //   729: aload 12
    //   731: iconst_0
    //   732: iload 11
    //   734: invokestatic 635	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
    //   737: aload 10
    //   739: iconst_0
    //   740: aload 12
    //   742: iload 11
    //   744: aload 10
    //   746: arraylength
    //   747: invokestatic 635	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
    //   750: aload 12
    //   752: astore 10
    //   754: aload_0
    //   755: getfield 546	com/sun/imageio/plugins/gif/GIFImageReader:imageMetadata	Lcom/sun/imageio/plugins/gif/GIFImageMetadata;
    //   758: getfield 509	com/sun/imageio/plugins/gif/GIFImageMetadata:applicationIDs	Ljava/util/List;
    //   761: ifnonnull +45 -> 806
    //   764: aload_0
    //   765: getfield 546	com/sun/imageio/plugins/gif/GIFImageReader:imageMetadata	Lcom/sun/imageio/plugins/gif/GIFImageMetadata;
    //   768: new 280	java/util/ArrayList
    //   771: dup
    //   772: invokespecial 636	java/util/ArrayList:<init>	()V
    //   775: putfield 509	com/sun/imageio/plugins/gif/GIFImageMetadata:applicationIDs	Ljava/util/List;
    //   778: aload_0
    //   779: getfield 546	com/sun/imageio/plugins/gif/GIFImageReader:imageMetadata	Lcom/sun/imageio/plugins/gif/GIFImageMetadata;
    //   782: new 280	java/util/ArrayList
    //   785: dup
    //   786: invokespecial 636	java/util/ArrayList:<init>	()V
    //   789: putfield 510	com/sun/imageio/plugins/gif/GIFImageMetadata:authenticationCodes	Ljava/util/List;
    //   792: aload_0
    //   793: getfield 546	com/sun/imageio/plugins/gif/GIFImageReader:imageMetadata	Lcom/sun/imageio/plugins/gif/GIFImageMetadata;
    //   796: new 280	java/util/ArrayList
    //   799: dup
    //   800: invokespecial 636	java/util/ArrayList:<init>	()V
    //   803: putfield 508	com/sun/imageio/plugins/gif/GIFImageMetadata:applicationData	Ljava/util/List;
    //   806: aload_0
    //   807: getfield 546	com/sun/imageio/plugins/gif/GIFImageReader:imageMetadata	Lcom/sun/imageio/plugins/gif/GIFImageMetadata;
    //   810: getfield 509	com/sun/imageio/plugins/gif/GIFImageMetadata:applicationIDs	Ljava/util/List;
    //   813: aload 6
    //   815: invokeinterface 651 2 0
    //   820: pop
    //   821: aload_0
    //   822: getfield 546	com/sun/imageio/plugins/gif/GIFImageReader:imageMetadata	Lcom/sun/imageio/plugins/gif/GIFImageMetadata;
    //   825: getfield 510	com/sun/imageio/plugins/gif/GIFImageMetadata:authenticationCodes	Ljava/util/List;
    //   828: aload 7
    //   830: invokeinterface 651 2 0
    //   835: pop
    //   836: aload_0
    //   837: getfield 546	com/sun/imageio/plugins/gif/GIFImageReader:imageMetadata	Lcom/sun/imageio/plugins/gif/GIFImageMetadata;
    //   840: getfield 508	com/sun/imageio/plugins/gif/GIFImageMetadata:applicationData	Ljava/util/List;
    //   843: aload 10
    //   845: invokeinterface 651 2 0
    //   850: pop
    //   851: goto +34 -> 885
    //   854: iconst_0
    //   855: istore 5
    //   857: aload_0
    //   858: getfield 555	com/sun/imageio/plugins/gif/GIFImageReader:stream	Ljavax/imageio/stream/ImageInputStream;
    //   861: invokeinterface 653 1 0
    //   866: istore 5
    //   868: aload_0
    //   869: getfield 555	com/sun/imageio/plugins/gif/GIFImageReader:stream	Ljavax/imageio/stream/ImageInputStream;
    //   872: iload 5
    //   874: invokeinterface 656 2 0
    //   879: pop
    //   880: iload 5
    //   882: ifgt -25 -> 857
    //   885: goto +51 -> 936
    //   888: iload_3
    //   889: bipush 59
    //   891: if_icmpne +13 -> 904
    //   894: new 273	java/lang/IndexOutOfBoundsException
    //   897: dup
    //   898: ldc 4
    //   900: invokespecial 622	java/lang/IndexOutOfBoundsException:<init>	(Ljava/lang/String;)V
    //   903: athrow
    //   904: new 283	javax/imageio/IIOException
    //   907: dup
    //   908: new 277	java/lang/StringBuilder
    //   911: dup
    //   912: invokespecial 631	java/lang/StringBuilder:<init>	()V
    //   915: ldc 13
    //   917: invokevirtual 634	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   920: iload_3
    //   921: invokevirtual 633	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   924: ldc 3
    //   926: invokevirtual 634	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   929: invokevirtual 632	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   932: invokespecial 638	javax/imageio/IIOException:<init>	(Ljava/lang/String;)V
    //   935: athrow
    //   936: goto -898 -> 38
    //   939: astore_1
    //   940: aload_1
    //   941: athrow
    //   942: astore_1
    //   943: new 283	javax/imageio/IIOException
    //   946: dup
    //   947: ldc 9
    //   949: aload_1
    //   950: invokespecial 639	javax/imageio/IIOException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   953: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	954	0	this	GIFImageReader
    //   37	215	1	l	long
    //   939	2	1	localIIOException	IIOException
    //   942	8	1	localIOException	IOException
    //   47	874	3	i	int
    //   127	504	4	j	int
    //   143	275	5	k	int
    //   581	34	5	arrayOfByte1	byte[]
    //   642	239	5	m	int
    //   194	151	6	n	int
    //   648	166	6	arrayOfByte2	byte[]
    //   397	1	7	i1	int
    //   653	176	7	arrayOfByte3	byte[]
    //   659	67	8	arrayOfByte4	byte[]
    //   681	47	9	i2	int
    //   699	145	10	localObject	Object
    //   713	30	11	i3	int
    //   723	28	12	arrayOfByte5	byte[]
    // Exception table:
    //   from	to	target	type
    //   17	257	939	javax/imageio/IIOException
    //   258	939	939	javax/imageio/IIOException
    //   17	257	942	java/io/IOException
    //   258	939	942	java/io/IOException
  }
  
  private int copyData(byte[] paramArrayOfByte1, int paramInt, byte[] paramArrayOfByte2)
  {
    int i = paramArrayOfByte2.length;
    int j = paramArrayOfByte1.length - paramInt;
    if (i > j) {
      i = j;
    }
    System.arraycopy(paramArrayOfByte1, paramInt, paramArrayOfByte2, 0, i);
    return paramInt + i;
  }
  
  private void startPass(int paramInt)
  {
    if ((updateListeners == null) || (!imageMetadata.interlaceFlag)) {
      return;
    }
    int i = interlaceOffset[interlacePass];
    int j = interlaceIncrement[interlacePass];
    int[] arrayOfInt1 = ReaderUtil.computeUpdatedPixels(sourceRegion, destinationOffset, destinationRegion.x, destinationRegion.y, destinationRegion.x + destinationRegion.width - 1, destinationRegion.y + destinationRegion.height - 1, sourceXSubsampling, sourceYSubsampling, 0, i, destinationRegion.width, (destinationRegion.height + j - 1) / j, 1, j);
    updateMinY = arrayOfInt1[1];
    updateYStep = arrayOfInt1[5];
    int[] arrayOfInt2 = { 0 };
    processPassStarted(theImage, interlacePass, sourceMinProgressivePass, sourceMaxProgressivePass, 0, updateMinY, 1, updateYStep, arrayOfInt2);
  }
  
  public BufferedImage read(int paramInt, ImageReadParam paramImageReadParam)
    throws IIOException
  {
    if (stream == null) {
      throw new IllegalStateException("Input not set!");
    }
    checkIndex(paramInt);
    int i = locateImage(paramInt);
    if (i != paramInt) {
      throw new IndexOutOfBoundsException("imageIndex out of bounds!");
    }
    clearAbortRequest();
    readMetadata();
    if (paramImageReadParam == null) {
      paramImageReadParam = getDefaultReadParam();
    }
    Iterator localIterator = getImageTypes(paramInt);
    theImage = getDestination(paramImageReadParam, localIterator, imageMetadata.imageWidth, imageMetadata.imageHeight);
    theTile = theImage.getWritableTile(0, 0);
    width = imageMetadata.imageWidth;
    height = imageMetadata.imageHeight;
    streamX = 0;
    streamY = 0;
    rowsDone = 0;
    interlacePass = 0;
    sourceRegion = new Rectangle(0, 0, 0, 0);
    destinationRegion = new Rectangle(0, 0, 0, 0);
    computeRegions(paramImageReadParam, width, height, theImage, sourceRegion, destinationRegion);
    destinationOffset = new Point(destinationRegion.x, destinationRegion.y);
    sourceXSubsampling = paramImageReadParam.getSourceXSubsampling();
    sourceYSubsampling = paramImageReadParam.getSourceYSubsampling();
    sourceMinProgressivePass = Math.max(paramImageReadParam.getSourceMinProgressivePass(), 0);
    sourceMaxProgressivePass = Math.min(paramImageReadParam.getSourceMaxProgressivePass(), 3);
    destY = (destinationRegion.y + (streamY - sourceRegion.y) / sourceYSubsampling);
    computeDecodeThisRow();
    processImageStarted(paramInt);
    startPass(0);
    rowBuf = new byte[width];
    try
    {
      initCodeSize = stream.readUnsignedByte();
      blockLength = stream.readUnsignedByte();
      int j = blockLength;
      int k = 0;
      int m;
      while (j > 0)
      {
        m = stream.read(block, k, j);
        j -= m;
        k += m;
      }
      bitPos = 0;
      nextByte = 0;
      lastBlockFound = false;
      interlacePass = 0;
      initNext32Bits();
      clearCode = (1 << initCodeSize);
      eofCode = (clearCode + 1);
      int n = 0;
      int[] arrayOfInt1 = new int['က'];
      byte[] arrayOfByte1 = new byte['က'];
      byte[] arrayOfByte2 = new byte['က'];
      int[] arrayOfInt2 = new int['က'];
      byte[] arrayOfByte3 = new byte['က'];
      initializeStringTable(arrayOfInt1, arrayOfByte1, arrayOfByte2, arrayOfInt2);
      int i1 = (1 << initCodeSize) + 2;
      int i2 = initCodeSize + 1;
      int i3 = (1 << i2) - 1;
      while (!abortRequested())
      {
        m = getCode(i2, i3);
        if (m == clearCode)
        {
          initializeStringTable(arrayOfInt1, arrayOfByte1, arrayOfByte2, arrayOfInt2);
          i1 = (1 << initCodeSize) + 2;
          i2 = initCodeSize + 1;
          i3 = (1 << i2) - 1;
          m = getCode(i2, i3);
          if (m == eofCode)
          {
            processImageComplete();
            return theImage;
          }
        }
        else
        {
          if (m == eofCode)
          {
            processImageComplete();
            return theImage;
          }
          if (m < i1)
          {
            i4 = m;
          }
          else
          {
            i4 = n;
            if (m != i1) {
              processWarningOccurred("Out-of-sequence code!");
            }
          }
          i5 = i1;
          i6 = n;
          arrayOfInt1[i5] = i6;
          arrayOfByte1[i5] = arrayOfByte2[i4];
          arrayOfByte2[i5] = arrayOfByte2[i6];
          arrayOfInt2[i6] += 1;
          i1++;
          if ((i1 == 1 << i2) && (i1 < 4096))
          {
            i2++;
            i3 = (1 << i2) - 1;
          }
        }
        int i4 = m;
        int i5 = arrayOfInt2[i4];
        for (int i6 = i5 - 1; i6 >= 0; i6--)
        {
          arrayOfByte3[i6] = arrayOfByte1[i4];
          i4 = arrayOfInt1[i4];
        }
        outputPixels(arrayOfByte3, i5);
        n = m;
      }
      processReadAborted();
      return theImage;
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
      throw new IIOException("I/O error reading image!", localIOException);
    }
  }
  
  public void reset()
  {
    super.reset();
    resetStreamSettings();
  }
  
  private void resetStreamSettings()
  {
    gotHeader = false;
    streamMetadata = null;
    currIndex = -1;
    imageMetadata = null;
    imageStartPosition = new ArrayList();
    numImages = -1;
    blockLength = 0;
    bitPos = 0;
    nextByte = 0;
    next32Bits = 0;
    lastBlockFound = false;
    theImage = null;
    theTile = null;
    width = -1;
    height = -1;
    streamX = -1;
    streamY = -1;
    rowsDone = 0;
    interlacePass = 0;
    fallbackColorTable = null;
  }
  
  private static synchronized byte[] getDefaultPalette()
  {
    if (defaultPalette == null)
    {
      BufferedImage localBufferedImage = new BufferedImage(1, 1, 13);
      IndexColorModel localIndexColorModel = (IndexColorModel)localBufferedImage.getColorModel();
      int i = localIndexColorModel.getMapSize();
      byte[] arrayOfByte1 = new byte[i];
      byte[] arrayOfByte2 = new byte[i];
      byte[] arrayOfByte3 = new byte[i];
      localIndexColorModel.getReds(arrayOfByte1);
      localIndexColorModel.getGreens(arrayOfByte2);
      localIndexColorModel.getBlues(arrayOfByte3);
      defaultPalette = new byte[i * 3];
      for (int j = 0; j < i; j++)
      {
        defaultPalette[(3 * j + 0)] = arrayOfByte1[j];
        defaultPalette[(3 * j + 1)] = arrayOfByte2[j];
        defaultPalette[(3 * j + 2)] = arrayOfByte3[j];
      }
    }
    return defaultPalette;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\plugins\gif\GIFImageReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */