package com.sun.imageio.plugins.jpeg;

import java.awt.Graphics;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.event.IIOReadProgressListener;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class JFIFMarkerSegment
  extends MarkerSegment
{
  int majorVersion;
  int minorVersion;
  int resUnits;
  int Xdensity;
  int Ydensity;
  int thumbWidth;
  int thumbHeight;
  JFIFThumbRGB thumb = null;
  ArrayList extSegments = new ArrayList();
  ICCMarkerSegment iccSegment = null;
  private static final int THUMB_JPEG = 16;
  private static final int THUMB_PALETTE = 17;
  private static final int THUMB_UNASSIGNED = 18;
  private static final int THUMB_RGB = 19;
  private static final int DATA_SIZE = 14;
  private static final int ID_SIZE = 5;
  private final int MAX_THUMB_WIDTH = 255;
  private final int MAX_THUMB_HEIGHT = 255;
  private final boolean debug = false;
  private boolean inICC = false;
  private ICCMarkerSegment tempICCSegment = null;
  
  JFIFMarkerSegment()
  {
    super(224);
    majorVersion = 1;
    minorVersion = 2;
    resUnits = 0;
    Xdensity = 1;
    Ydensity = 1;
    thumbWidth = 0;
    thumbHeight = 0;
  }
  
  JFIFMarkerSegment(JPEGBuffer paramJPEGBuffer)
    throws IOException
  {
    super(paramJPEGBuffer);
    bufPtr += 5;
    majorVersion = buf[(bufPtr++)];
    minorVersion = buf[(bufPtr++)];
    resUnits = buf[(bufPtr++)];
    Xdensity = ((buf[(bufPtr++)] & 0xFF) << 8);
    Xdensity |= buf[(bufPtr++)] & 0xFF;
    Ydensity = ((buf[(bufPtr++)] & 0xFF) << 8);
    Ydensity |= buf[(bufPtr++)] & 0xFF;
    thumbWidth = (buf[(bufPtr++)] & 0xFF);
    thumbHeight = (buf[(bufPtr++)] & 0xFF);
    bufAvail -= 14;
    if (thumbWidth > 0) {
      thumb = new JFIFThumbRGB(paramJPEGBuffer, thumbWidth, thumbHeight);
    }
  }
  
  JFIFMarkerSegment(Node paramNode)
    throws IIOInvalidTreeException
  {
    this();
    updateFromNativeNode(paramNode, true);
  }
  
  protected Object clone()
  {
    JFIFMarkerSegment localJFIFMarkerSegment = (JFIFMarkerSegment)super.clone();
    if (!extSegments.isEmpty())
    {
      extSegments = new ArrayList();
      Iterator localIterator = extSegments.iterator();
      while (localIterator.hasNext())
      {
        JFIFExtensionMarkerSegment localJFIFExtensionMarkerSegment = (JFIFExtensionMarkerSegment)localIterator.next();
        extSegments.add(localJFIFExtensionMarkerSegment.clone());
      }
    }
    if (iccSegment != null) {
      iccSegment = ((ICCMarkerSegment)iccSegment.clone());
    }
    return localJFIFMarkerSegment;
  }
  
  void addJFXX(JPEGBuffer paramJPEGBuffer, JPEGImageReader paramJPEGImageReader)
    throws IOException
  {
    extSegments.add(new JFIFExtensionMarkerSegment(paramJPEGBuffer, paramJPEGImageReader));
  }
  
  void addICC(JPEGBuffer paramJPEGBuffer)
    throws IOException
  {
    if (!inICC)
    {
      if (iccSegment != null) {
        throw new IIOException("> 1 ICC APP2 Marker Segment not supported");
      }
      tempICCSegment = new ICCMarkerSegment(paramJPEGBuffer);
      if (!inICC)
      {
        iccSegment = tempICCSegment;
        tempICCSegment = null;
      }
    }
    else if (tempICCSegment.addData(paramJPEGBuffer) == true)
    {
      iccSegment = tempICCSegment;
      tempICCSegment = null;
    }
  }
  
  void addICC(ICC_ColorSpace paramICC_ColorSpace)
    throws IOException
  {
    if (iccSegment != null) {
      throw new IIOException("> 1 ICC APP2 Marker Segment not supported");
    }
    iccSegment = new ICCMarkerSegment(paramICC_ColorSpace);
  }
  
  IIOMetadataNode getNativeNode()
  {
    IIOMetadataNode localIIOMetadataNode1 = new IIOMetadataNode("app0JFIF");
    localIIOMetadataNode1.setAttribute("majorVersion", Integer.toString(majorVersion));
    localIIOMetadataNode1.setAttribute("minorVersion", Integer.toString(minorVersion));
    localIIOMetadataNode1.setAttribute("resUnits", Integer.toString(resUnits));
    localIIOMetadataNode1.setAttribute("Xdensity", Integer.toString(Xdensity));
    localIIOMetadataNode1.setAttribute("Ydensity", Integer.toString(Ydensity));
    localIIOMetadataNode1.setAttribute("thumbWidth", Integer.toString(thumbWidth));
    localIIOMetadataNode1.setAttribute("thumbHeight", Integer.toString(thumbHeight));
    if (!extSegments.isEmpty())
    {
      IIOMetadataNode localIIOMetadataNode2 = new IIOMetadataNode("JFXX");
      localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
      Iterator localIterator = extSegments.iterator();
      while (localIterator.hasNext())
      {
        JFIFExtensionMarkerSegment localJFIFExtensionMarkerSegment = (JFIFExtensionMarkerSegment)localIterator.next();
        localIIOMetadataNode2.appendChild(localJFIFExtensionMarkerSegment.getNativeNode());
      }
    }
    if (iccSegment != null) {
      localIIOMetadataNode1.appendChild(iccSegment.getNativeNode());
    }
    return localIIOMetadataNode1;
  }
  
  void updateFromNativeNode(Node paramNode, boolean paramBoolean)
    throws IIOInvalidTreeException
  {
    NamedNodeMap localNamedNodeMap = paramNode.getAttributes();
    if (localNamedNodeMap.getLength() > 0)
    {
      int i = getAttributeValue(paramNode, localNamedNodeMap, "majorVersion", 0, 255, false);
      majorVersion = (i != -1 ? i : majorVersion);
      i = getAttributeValue(paramNode, localNamedNodeMap, "minorVersion", 0, 255, false);
      minorVersion = (i != -1 ? i : minorVersion);
      i = getAttributeValue(paramNode, localNamedNodeMap, "resUnits", 0, 2, false);
      resUnits = (i != -1 ? i : resUnits);
      i = getAttributeValue(paramNode, localNamedNodeMap, "Xdensity", 1, 65535, false);
      Xdensity = (i != -1 ? i : Xdensity);
      i = getAttributeValue(paramNode, localNamedNodeMap, "Ydensity", 1, 65535, false);
      Ydensity = (i != -1 ? i : Ydensity);
      i = getAttributeValue(paramNode, localNamedNodeMap, "thumbWidth", 0, 255, false);
      thumbWidth = (i != -1 ? i : thumbWidth);
      i = getAttributeValue(paramNode, localNamedNodeMap, "thumbHeight", 0, 255, false);
      thumbHeight = (i != -1 ? i : thumbHeight);
    }
    if (paramNode.hasChildNodes())
    {
      NodeList localNodeList1 = paramNode.getChildNodes();
      int j = localNodeList1.getLength();
      if (j > 2) {
        throw new IIOInvalidTreeException("app0JFIF node cannot have > 2 children", paramNode);
      }
      for (int k = 0; k < j; k++)
      {
        Node localNode1 = localNodeList1.item(k);
        String str = localNode1.getNodeName();
        if (str.equals("JFXX"))
        {
          if ((!extSegments.isEmpty()) && (paramBoolean)) {
            throw new IIOInvalidTreeException("app0JFIF node cannot have > 1 JFXX node", paramNode);
          }
          NodeList localNodeList2 = localNode1.getChildNodes();
          int m = localNodeList2.getLength();
          for (int n = 0; n < m; n++)
          {
            Node localNode2 = localNodeList2.item(n);
            extSegments.add(new JFIFExtensionMarkerSegment(localNode2));
          }
        }
        if (str.equals("app2ICC"))
        {
          if ((iccSegment != null) && (paramBoolean)) {
            throw new IIOInvalidTreeException("> 1 ICC APP2 Marker Segment not supported", paramNode);
          }
          iccSegment = new ICCMarkerSegment(localNode1);
        }
      }
    }
  }
  
  int getThumbnailWidth(int paramInt)
  {
    if (thumb != null)
    {
      if (paramInt == 0) {
        return thumb.getWidth();
      }
      paramInt--;
    }
    JFIFExtensionMarkerSegment localJFIFExtensionMarkerSegment = (JFIFExtensionMarkerSegment)extSegments.get(paramInt);
    return thumb.getWidth();
  }
  
  int getThumbnailHeight(int paramInt)
  {
    if (thumb != null)
    {
      if (paramInt == 0) {
        return thumb.getHeight();
      }
      paramInt--;
    }
    JFIFExtensionMarkerSegment localJFIFExtensionMarkerSegment = (JFIFExtensionMarkerSegment)extSegments.get(paramInt);
    return thumb.getHeight();
  }
  
  BufferedImage getThumbnail(ImageInputStream paramImageInputStream, int paramInt, JPEGImageReader paramJPEGImageReader)
    throws IOException
  {
    paramJPEGImageReader.thumbnailStarted(paramInt);
    BufferedImage localBufferedImage = null;
    if ((thumb != null) && (paramInt == 0))
    {
      localBufferedImage = thumb.getThumbnail(paramImageInputStream, paramJPEGImageReader);
    }
    else
    {
      if (thumb != null) {
        paramInt--;
      }
      JFIFExtensionMarkerSegment localJFIFExtensionMarkerSegment = (JFIFExtensionMarkerSegment)extSegments.get(paramInt);
      localBufferedImage = thumb.getThumbnail(paramImageInputStream, paramJPEGImageReader);
    }
    paramJPEGImageReader.thumbnailComplete();
    return localBufferedImage;
  }
  
  void write(ImageOutputStream paramImageOutputStream, JPEGImageWriter paramJPEGImageWriter)
    throws IOException
  {
    write(paramImageOutputStream, null, paramJPEGImageWriter);
  }
  
  void write(ImageOutputStream paramImageOutputStream, BufferedImage paramBufferedImage, JPEGImageWriter paramJPEGImageWriter)
    throws IOException
  {
    int i = 0;
    int j = 0;
    int k = 0;
    int[] arrayOfInt = null;
    if (paramBufferedImage != null)
    {
      i = paramBufferedImage.getWidth();
      j = paramBufferedImage.getHeight();
      if ((i > 255) || (j > 255)) {
        paramJPEGImageWriter.warningOccurred(12);
      }
      i = Math.min(i, 255);
      j = Math.min(j, 255);
      arrayOfInt = paramBufferedImage.getRaster().getPixels(0, 0, i, j, (int[])null);
      k = arrayOfInt.length;
    }
    length = (16 + k);
    writeTag(paramImageOutputStream);
    byte[] arrayOfByte = { 74, 70, 73, 70, 0 };
    paramImageOutputStream.write(arrayOfByte);
    paramImageOutputStream.write(majorVersion);
    paramImageOutputStream.write(minorVersion);
    paramImageOutputStream.write(resUnits);
    write2bytes(paramImageOutputStream, Xdensity);
    write2bytes(paramImageOutputStream, Ydensity);
    paramImageOutputStream.write(i);
    paramImageOutputStream.write(j);
    if (arrayOfInt != null)
    {
      paramJPEGImageWriter.thumbnailStarted(0);
      writeThumbnailData(paramImageOutputStream, arrayOfInt, paramJPEGImageWriter);
      paramJPEGImageWriter.thumbnailComplete();
    }
  }
  
  void writeThumbnailData(ImageOutputStream paramImageOutputStream, int[] paramArrayOfInt, JPEGImageWriter paramJPEGImageWriter)
    throws IOException
  {
    int i = paramArrayOfInt.length / 20;
    if (i == 0) {
      i = 1;
    }
    for (int j = 0; j < paramArrayOfInt.length; j++)
    {
      paramImageOutputStream.write(paramArrayOfInt[j]);
      if ((j > i) && (j % i == 0)) {
        paramJPEGImageWriter.thumbnailProgress(j * 100.0F / paramArrayOfInt.length);
      }
    }
  }
  
  void writeWithThumbs(ImageOutputStream paramImageOutputStream, List paramList, JPEGImageWriter paramJPEGImageWriter)
    throws IOException
  {
    if (paramList != null)
    {
      JFIFExtensionMarkerSegment localJFIFExtensionMarkerSegment = null;
      if (paramList.size() == 1)
      {
        if (!extSegments.isEmpty()) {
          localJFIFExtensionMarkerSegment = (JFIFExtensionMarkerSegment)extSegments.get(0);
        }
        writeThumb(paramImageOutputStream, (BufferedImage)paramList.get(0), localJFIFExtensionMarkerSegment, 0, true, paramJPEGImageWriter);
      }
      else
      {
        write(paramImageOutputStream, paramJPEGImageWriter);
        for (int i = 0; i < paramList.size(); i++)
        {
          localJFIFExtensionMarkerSegment = null;
          if (i < extSegments.size()) {
            localJFIFExtensionMarkerSegment = (JFIFExtensionMarkerSegment)extSegments.get(i);
          }
          writeThumb(paramImageOutputStream, (BufferedImage)paramList.get(i), localJFIFExtensionMarkerSegment, i, false, paramJPEGImageWriter);
        }
      }
    }
    else
    {
      write(paramImageOutputStream, paramJPEGImageWriter);
    }
  }
  
  private void writeThumb(ImageOutputStream paramImageOutputStream, BufferedImage paramBufferedImage, JFIFExtensionMarkerSegment paramJFIFExtensionMarkerSegment, int paramInt, boolean paramBoolean, JPEGImageWriter paramJPEGImageWriter)
    throws IOException
  {
    ColorModel localColorModel = paramBufferedImage.getColorModel();
    ColorSpace localColorSpace = localColorModel.getColorSpace();
    BufferedImage localBufferedImage;
    if ((localColorModel instanceof IndexColorModel))
    {
      if (paramBoolean) {
        write(paramImageOutputStream, paramJPEGImageWriter);
      }
      if ((paramJFIFExtensionMarkerSegment == null) || (code == 17))
      {
        writeJFXXSegment(paramInt, paramBufferedImage, paramImageOutputStream, paramJPEGImageWriter);
      }
      else
      {
        localBufferedImage = ((IndexColorModel)localColorModel).convertToIntDiscrete(paramBufferedImage.getRaster(), false);
        paramJFIFExtensionMarkerSegment.setThumbnail(localBufferedImage);
        paramJPEGImageWriter.thumbnailStarted(paramInt);
        paramJFIFExtensionMarkerSegment.write(paramImageOutputStream, paramJPEGImageWriter);
        paramJPEGImageWriter.thumbnailComplete();
      }
    }
    else if (localColorSpace.getType() == 5)
    {
      if (paramJFIFExtensionMarkerSegment == null)
      {
        if (paramBoolean) {
          write(paramImageOutputStream, paramBufferedImage, paramJPEGImageWriter);
        } else {
          writeJFXXSegment(paramInt, paramBufferedImage, paramImageOutputStream, paramJPEGImageWriter);
        }
      }
      else
      {
        if (paramBoolean) {
          write(paramImageOutputStream, paramJPEGImageWriter);
        }
        if (code == 17)
        {
          writeJFXXSegment(paramInt, paramBufferedImage, paramImageOutputStream, paramJPEGImageWriter);
          paramJPEGImageWriter.warningOccurred(14);
        }
        else
        {
          paramJFIFExtensionMarkerSegment.setThumbnail(paramBufferedImage);
          paramJPEGImageWriter.thumbnailStarted(paramInt);
          paramJFIFExtensionMarkerSegment.write(paramImageOutputStream, paramJPEGImageWriter);
          paramJPEGImageWriter.thumbnailComplete();
        }
      }
    }
    else if (localColorSpace.getType() == 6)
    {
      if (paramJFIFExtensionMarkerSegment == null)
      {
        if (paramBoolean)
        {
          localBufferedImage = expandGrayThumb(paramBufferedImage);
          write(paramImageOutputStream, localBufferedImage, paramJPEGImageWriter);
        }
        else
        {
          writeJFXXSegment(paramInt, paramBufferedImage, paramImageOutputStream, paramJPEGImageWriter);
        }
      }
      else
      {
        if (paramBoolean) {
          write(paramImageOutputStream, paramJPEGImageWriter);
        }
        if (code == 19)
        {
          localBufferedImage = expandGrayThumb(paramBufferedImage);
          writeJFXXSegment(paramInt, localBufferedImage, paramImageOutputStream, paramJPEGImageWriter);
        }
        else if (code == 16)
        {
          paramJFIFExtensionMarkerSegment.setThumbnail(paramBufferedImage);
          paramJPEGImageWriter.thumbnailStarted(paramInt);
          paramJFIFExtensionMarkerSegment.write(paramImageOutputStream, paramJPEGImageWriter);
          paramJPEGImageWriter.thumbnailComplete();
        }
        else if (code == 17)
        {
          writeJFXXSegment(paramInt, paramBufferedImage, paramImageOutputStream, paramJPEGImageWriter);
          paramJPEGImageWriter.warningOccurred(15);
        }
      }
    }
    else
    {
      paramJPEGImageWriter.warningOccurred(9);
    }
  }
  
  private void writeJFXXSegment(int paramInt, BufferedImage paramBufferedImage, ImageOutputStream paramImageOutputStream, JPEGImageWriter paramJPEGImageWriter)
    throws IOException
  {
    JFIFExtensionMarkerSegment localJFIFExtensionMarkerSegment = null;
    try
    {
      localJFIFExtensionMarkerSegment = new JFIFExtensionMarkerSegment(paramBufferedImage);
    }
    catch (IllegalThumbException localIllegalThumbException)
    {
      paramJPEGImageWriter.warningOccurred(9);
      return;
    }
    paramJPEGImageWriter.thumbnailStarted(paramInt);
    localJFIFExtensionMarkerSegment.write(paramImageOutputStream, paramJPEGImageWriter);
    paramJPEGImageWriter.thumbnailComplete();
  }
  
  private static BufferedImage expandGrayThumb(BufferedImage paramBufferedImage)
  {
    BufferedImage localBufferedImage = new BufferedImage(paramBufferedImage.getWidth(), paramBufferedImage.getHeight(), 1);
    Graphics localGraphics = localBufferedImage.getGraphics();
    localGraphics.drawImage(paramBufferedImage, 0, 0, null);
    return localBufferedImage;
  }
  
  static void writeDefaultJFIF(ImageOutputStream paramImageOutputStream, List paramList, ICC_Profile paramICC_Profile, JPEGImageWriter paramJPEGImageWriter)
    throws IOException
  {
    JFIFMarkerSegment localJFIFMarkerSegment = new JFIFMarkerSegment();
    localJFIFMarkerSegment.writeWithThumbs(paramImageOutputStream, paramList, paramJPEGImageWriter);
    if (paramICC_Profile != null) {
      writeICC(paramICC_Profile, paramImageOutputStream);
    }
  }
  
  void print()
  {
    printTag("JFIF");
    System.out.print("Version ");
    System.out.print(majorVersion);
    System.out.println(".0" + Integer.toString(minorVersion));
    System.out.print("Resolution units: ");
    System.out.println(resUnits);
    System.out.print("X density: ");
    System.out.println(Xdensity);
    System.out.print("Y density: ");
    System.out.println(Ydensity);
    System.out.print("Thumbnail Width: ");
    System.out.println(thumbWidth);
    System.out.print("Thumbnail Height: ");
    System.out.println(thumbHeight);
    if (!extSegments.isEmpty())
    {
      Iterator localIterator = extSegments.iterator();
      while (localIterator.hasNext())
      {
        JFIFExtensionMarkerSegment localJFIFExtensionMarkerSegment = (JFIFExtensionMarkerSegment)localIterator.next();
        localJFIFExtensionMarkerSegment.print();
      }
    }
    if (iccSegment != null) {
      iccSegment.print();
    }
  }
  
  static void writeICC(ICC_Profile paramICC_Profile, ImageOutputStream paramImageOutputStream)
    throws IOException
  {
    int i = 2;
    int j = "ICC_PROFILE".length() + 1;
    int k = 2;
    int m = 65535 - i - j - k;
    byte[] arrayOfByte1 = paramICC_Profile.getData();
    int n = arrayOfByte1.length / m;
    if (arrayOfByte1.length % m != 0) {
      n++;
    }
    int i1 = 1;
    int i2 = 0;
    for (int i3 = 0; i3 < n; i3++)
    {
      int i4 = Math.min(arrayOfByte1.length - i2, m);
      int i5 = i4 + k + j + i;
      paramImageOutputStream.write(255);
      paramImageOutputStream.write(226);
      MarkerSegment.write2bytes(paramImageOutputStream, i5);
      byte[] arrayOfByte2 = "ICC_PROFILE".getBytes("US-ASCII");
      paramImageOutputStream.write(arrayOfByte2);
      paramImageOutputStream.write(0);
      paramImageOutputStream.write(i1++);
      paramImageOutputStream.write(n);
      paramImageOutputStream.write(arrayOfByte1, i2, i4);
      i2 += i4;
    }
  }
  
  class ICCMarkerSegment
    extends MarkerSegment
  {
    ArrayList chunks = null;
    byte[] profile = null;
    private static final int ID_SIZE = 12;
    int chunksRead;
    int numChunks;
    
    ICCMarkerSegment(ICC_ColorSpace paramICC_ColorSpace)
    {
      super();
      chunks = null;
      chunksRead = 0;
      numChunks = 0;
      profile = paramICC_ColorSpace.getProfile().getData();
    }
    
    ICCMarkerSegment(JPEGBuffer paramJPEGBuffer)
      throws IOException
    {
      super();
      bufPtr += 12;
      bufAvail -= 12;
      length -= 12;
      int i = buf[bufPtr] & 0xFF;
      numChunks = (buf[(bufPtr + 1)] & 0xFF);
      if (i > numChunks) {
        throw new IIOException("Image format Error; chunk num > num chunks");
      }
      if (numChunks == 1)
      {
        length -= 2;
        profile = new byte[length];
        bufPtr += 2;
        bufAvail -= 2;
        paramJPEGBuffer.readData(profile);
        inICC = false;
      }
      else
      {
        byte[] arrayOfByte = new byte[length];
        length -= 2;
        paramJPEGBuffer.readData(arrayOfByte);
        chunks = new ArrayList();
        chunks.add(arrayOfByte);
        chunksRead = 1;
        inICC = true;
      }
    }
    
    ICCMarkerSegment(Node paramNode)
      throws IIOInvalidTreeException
    {
      super();
      if ((paramNode instanceof IIOMetadataNode))
      {
        IIOMetadataNode localIIOMetadataNode = (IIOMetadataNode)paramNode;
        ICC_Profile localICC_Profile = (ICC_Profile)localIIOMetadataNode.getUserObject();
        if (localICC_Profile != null) {
          profile = localICC_Profile.getData();
        }
      }
    }
    
    protected Object clone()
    {
      ICCMarkerSegment localICCMarkerSegment = (ICCMarkerSegment)super.clone();
      if (profile != null) {
        profile = ((byte[])profile.clone());
      }
      return localICCMarkerSegment;
    }
    
    boolean addData(JPEGBuffer paramJPEGBuffer)
      throws IOException
    {
      bufPtr += 1;
      bufAvail -= 1;
      int i = (buf[(bufPtr++)] & 0xFF) << 8;
      i |= buf[(bufPtr++)] & 0xFF;
      bufAvail -= 2;
      i -= 2;
      bufPtr += 12;
      bufAvail -= 12;
      i -= 12;
      int j = buf[bufPtr] & 0xFF;
      if (j > numChunks) {
        throw new IIOException("Image format Error; chunk num > num chunks");
      }
      int k = buf[(bufPtr + 1)] & 0xFF;
      if (numChunks != k) {
        throw new IIOException("Image format Error; icc num chunks mismatch");
      }
      i -= 2;
      boolean bool = false;
      byte[] arrayOfByte1 = new byte[i];
      paramJPEGBuffer.readData(arrayOfByte1);
      chunks.add(arrayOfByte1);
      length += i;
      chunksRead += 1;
      if (chunksRead < numChunks)
      {
        inICC = true;
      }
      else
      {
        profile = new byte[length];
        int m = 0;
        for (int n = 1; n <= numChunks; n++)
        {
          int i1 = 0;
          for (int i2 = 0; i2 < chunks.size(); i2++)
          {
            byte[] arrayOfByte2 = (byte[])chunks.get(i2);
            if (arrayOfByte2[0] == n)
            {
              System.arraycopy(arrayOfByte2, 2, profile, m, arrayOfByte2.length - 2);
              m += arrayOfByte2.length - 2;
              i1 = 1;
            }
          }
          if (i1 == 0) {
            throw new IIOException("Image Format Error: Missing ICC chunk num " + n);
          }
        }
        chunks = null;
        chunksRead = 0;
        numChunks = 0;
        inICC = false;
        bool = true;
      }
      return bool;
    }
    
    IIOMetadataNode getNativeNode()
    {
      IIOMetadataNode localIIOMetadataNode = new IIOMetadataNode("app2ICC");
      if (profile != null) {
        localIIOMetadataNode.setUserObject(ICC_Profile.getInstance(profile));
      }
      return localIIOMetadataNode;
    }
    
    void write(ImageOutputStream paramImageOutputStream)
      throws IOException
    {}
    
    void print()
    {
      printTag("ICC Profile APP2");
    }
  }
  
  private class IllegalThumbException
    extends Exception
  {
    private IllegalThumbException() {}
  }
  
  class JFIFExtensionMarkerSegment
    extends MarkerSegment
  {
    int code;
    JFIFMarkerSegment.JFIFThumb thumb;
    private static final int DATA_SIZE = 6;
    private static final int ID_SIZE = 5;
    
    JFIFExtensionMarkerSegment(JPEGBuffer paramJPEGBuffer, JPEGImageReader paramJPEGImageReader)
      throws IOException
    {
      super();
      bufPtr += 5;
      code = (buf[(bufPtr++)] & 0xFF);
      bufAvail -= 6;
      if (code == 16)
      {
        thumb = new JFIFMarkerSegment.JFIFThumbJPEG(JFIFMarkerSegment.this, paramJPEGBuffer, length, paramJPEGImageReader);
      }
      else
      {
        paramJPEGBuffer.loadBuf(2);
        int i = buf[(bufPtr++)] & 0xFF;
        int j = buf[(bufPtr++)] & 0xFF;
        bufAvail -= 2;
        if (code == 17) {
          thumb = new JFIFMarkerSegment.JFIFThumbPalette(JFIFMarkerSegment.this, paramJPEGBuffer, i, j);
        } else {
          thumb = new JFIFMarkerSegment.JFIFThumbRGB(JFIFMarkerSegment.this, paramJPEGBuffer, i, j);
        }
      }
    }
    
    JFIFExtensionMarkerSegment(Node paramNode)
      throws IIOInvalidTreeException
    {
      super();
      NamedNodeMap localNamedNodeMap = paramNode.getAttributes();
      if (localNamedNodeMap.getLength() > 0)
      {
        code = getAttributeValue(paramNode, localNamedNodeMap, "extensionCode", 16, 19, false);
        if (code == 18) {
          throw new IIOInvalidTreeException("invalid extensionCode attribute value", paramNode);
        }
      }
      else
      {
        code = 18;
      }
      if (paramNode.getChildNodes().getLength() != 1) {
        throw new IIOInvalidTreeException("app0JFXX node must have exactly 1 child", paramNode);
      }
      Node localNode = paramNode.getFirstChild();
      String str = localNode.getNodeName();
      if (str.equals("JFIFthumbJPEG"))
      {
        if (code == 18) {
          code = 16;
        }
        thumb = new JFIFMarkerSegment.JFIFThumbJPEG(JFIFMarkerSegment.this, localNode);
      }
      else if (str.equals("JFIFthumbPalette"))
      {
        if (code == 18) {
          code = 17;
        }
        thumb = new JFIFMarkerSegment.JFIFThumbPalette(JFIFMarkerSegment.this, localNode);
      }
      else if (str.equals("JFIFthumbRGB"))
      {
        if (code == 18) {
          code = 19;
        }
        thumb = new JFIFMarkerSegment.JFIFThumbRGB(JFIFMarkerSegment.this, localNode);
      }
      else
      {
        throw new IIOInvalidTreeException("unrecognized app0JFXX child node", paramNode);
      }
    }
    
    JFIFExtensionMarkerSegment(BufferedImage paramBufferedImage)
      throws JFIFMarkerSegment.IllegalThumbException
    {
      super();
      ColorModel localColorModel = paramBufferedImage.getColorModel();
      int i = localColorModel.getColorSpace().getType();
      if (localColorModel.hasAlpha()) {
        throw new JFIFMarkerSegment.IllegalThumbException(JFIFMarkerSegment.this, null);
      }
      if ((localColorModel instanceof IndexColorModel))
      {
        code = 17;
        thumb = new JFIFMarkerSegment.JFIFThumbPalette(JFIFMarkerSegment.this, paramBufferedImage);
      }
      else if (i == 5)
      {
        code = 19;
        thumb = new JFIFMarkerSegment.JFIFThumbRGB(JFIFMarkerSegment.this, paramBufferedImage);
      }
      else if (i == 6)
      {
        code = 16;
        thumb = new JFIFMarkerSegment.JFIFThumbJPEG(JFIFMarkerSegment.this, paramBufferedImage);
      }
      else
      {
        throw new JFIFMarkerSegment.IllegalThumbException(JFIFMarkerSegment.this, null);
      }
    }
    
    void setThumbnail(BufferedImage paramBufferedImage)
    {
      try
      {
        switch (code)
        {
        case 17: 
          thumb = new JFIFMarkerSegment.JFIFThumbPalette(JFIFMarkerSegment.this, paramBufferedImage);
          break;
        case 19: 
          thumb = new JFIFMarkerSegment.JFIFThumbRGB(JFIFMarkerSegment.this, paramBufferedImage);
          break;
        case 16: 
          thumb = new JFIFMarkerSegment.JFIFThumbJPEG(JFIFMarkerSegment.this, paramBufferedImage);
        }
      }
      catch (JFIFMarkerSegment.IllegalThumbException localIllegalThumbException)
      {
        throw new InternalError("Illegal thumb in setThumbnail!", localIllegalThumbException);
      }
    }
    
    protected Object clone()
    {
      JFIFExtensionMarkerSegment localJFIFExtensionMarkerSegment = (JFIFExtensionMarkerSegment)super.clone();
      if (thumb != null) {
        thumb = ((JFIFMarkerSegment.JFIFThumb)thumb.clone());
      }
      return localJFIFExtensionMarkerSegment;
    }
    
    IIOMetadataNode getNativeNode()
    {
      IIOMetadataNode localIIOMetadataNode = new IIOMetadataNode("app0JFXX");
      localIIOMetadataNode.setAttribute("extensionCode", Integer.toString(code));
      localIIOMetadataNode.appendChild(thumb.getNativeNode());
      return localIIOMetadataNode;
    }
    
    void write(ImageOutputStream paramImageOutputStream, JPEGImageWriter paramJPEGImageWriter)
      throws IOException
    {
      length = (8 + thumb.getLength());
      writeTag(paramImageOutputStream);
      byte[] arrayOfByte = { 74, 70, 88, 88, 0 };
      paramImageOutputStream.write(arrayOfByte);
      paramImageOutputStream.write(code);
      thumb.write(paramImageOutputStream, paramJPEGImageWriter);
    }
    
    void print()
    {
      printTag("JFXX");
      thumb.print();
    }
  }
  
  abstract class JFIFThumb
    implements Cloneable
  {
    long streamPos = -1L;
    
    abstract int getLength();
    
    abstract int getWidth();
    
    abstract int getHeight();
    
    abstract BufferedImage getThumbnail(ImageInputStream paramImageInputStream, JPEGImageReader paramJPEGImageReader)
      throws IOException;
    
    protected JFIFThumb() {}
    
    protected JFIFThumb(JPEGBuffer paramJPEGBuffer)
      throws IOException
    {
      streamPos = paramJPEGBuffer.getStreamPosition();
    }
    
    abstract void print();
    
    abstract IIOMetadataNode getNativeNode();
    
    abstract void write(ImageOutputStream paramImageOutputStream, JPEGImageWriter paramJPEGImageWriter)
      throws IOException;
    
    protected Object clone()
    {
      try
      {
        return super.clone();
      }
      catch (CloneNotSupportedException localCloneNotSupportedException) {}
      return null;
    }
  }
  
  class JFIFThumbJPEG
    extends JFIFMarkerSegment.JFIFThumb
  {
    JPEGMetadata thumbMetadata = null;
    byte[] data = null;
    private static final int PREAMBLE_SIZE = 6;
    
    JFIFThumbJPEG(JPEGBuffer paramJPEGBuffer, int paramInt, JPEGImageReader paramJPEGImageReader)
      throws IOException
    {
      super(paramJPEGBuffer);
      long l = streamPos + (paramInt - 6);
      iis.seek(streamPos);
      thumbMetadata = new JPEGMetadata(false, true, iis, paramJPEGImageReader);
      iis.seek(l);
      bufAvail = 0;
      bufPtr = 0;
    }
    
    JFIFThumbJPEG(Node paramNode)
      throws IIOInvalidTreeException
    {
      super();
      if (paramNode.getChildNodes().getLength() > 1) {
        throw new IIOInvalidTreeException("JFIFThumbJPEG node must have 0 or 1 child", paramNode);
      }
      Node localNode = paramNode.getFirstChild();
      if (localNode != null)
      {
        String str = localNode.getNodeName();
        if (!str.equals("markerSequence")) {
          throw new IIOInvalidTreeException("JFIFThumbJPEG child must be a markerSequence node", paramNode);
        }
        thumbMetadata = new JPEGMetadata(false, true);
        thumbMetadata.setFromMarkerSequenceNode(localNode);
      }
    }
    
    JFIFThumbJPEG(BufferedImage paramBufferedImage)
      throws JFIFMarkerSegment.IllegalThumbException
    {
      super();
      int i = 4096;
      int j = 65527;
      try
      {
        ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(i);
        MemoryCacheImageOutputStream localMemoryCacheImageOutputStream = new MemoryCacheImageOutputStream(localByteArrayOutputStream);
        JPEGImageWriter localJPEGImageWriter = new JPEGImageWriter(null);
        localJPEGImageWriter.setOutput(localMemoryCacheImageOutputStream);
        JPEGMetadata localJPEGMetadata = (JPEGMetadata)localJPEGImageWriter.getDefaultImageMetadata(new ImageTypeSpecifier(paramBufferedImage), null);
        MarkerSegment localMarkerSegment = localJPEGMetadata.findMarkerSegment(JFIFMarkerSegment.class, true);
        if (localMarkerSegment == null) {
          throw new JFIFMarkerSegment.IllegalThumbException(JFIFMarkerSegment.this, null);
        }
        markerSequence.remove(localMarkerSegment);
        localJPEGImageWriter.write(new IIOImage(paramBufferedImage, null, localJPEGMetadata));
        localJPEGImageWriter.dispose();
        if (localByteArrayOutputStream.size() > j) {
          throw new JFIFMarkerSegment.IllegalThumbException(JFIFMarkerSegment.this, null);
        }
        data = localByteArrayOutputStream.toByteArray();
      }
      catch (IOException localIOException)
      {
        throw new JFIFMarkerSegment.IllegalThumbException(JFIFMarkerSegment.this, null);
      }
    }
    
    int getWidth()
    {
      int i = 0;
      SOFMarkerSegment localSOFMarkerSegment = (SOFMarkerSegment)thumbMetadata.findMarkerSegment(SOFMarkerSegment.class, true);
      if (localSOFMarkerSegment != null) {
        i = samplesPerLine;
      }
      return i;
    }
    
    int getHeight()
    {
      int i = 0;
      SOFMarkerSegment localSOFMarkerSegment = (SOFMarkerSegment)thumbMetadata.findMarkerSegment(SOFMarkerSegment.class, true);
      if (localSOFMarkerSegment != null) {
        i = numLines;
      }
      return i;
    }
    
    BufferedImage getThumbnail(ImageInputStream paramImageInputStream, JPEGImageReader paramJPEGImageReader)
      throws IOException
    {
      paramImageInputStream.mark();
      paramImageInputStream.seek(streamPos);
      JPEGImageReader localJPEGImageReader = new JPEGImageReader(null);
      localJPEGImageReader.setInput(paramImageInputStream);
      localJPEGImageReader.addIIOReadProgressListener(new ThumbnailReadListener(paramJPEGImageReader));
      BufferedImage localBufferedImage = localJPEGImageReader.read(0, null);
      localJPEGImageReader.dispose();
      paramImageInputStream.reset();
      return localBufferedImage;
    }
    
    protected Object clone()
    {
      JFIFThumbJPEG localJFIFThumbJPEG = (JFIFThumbJPEG)super.clone();
      if (thumbMetadata != null) {
        thumbMetadata = ((JPEGMetadata)thumbMetadata.clone());
      }
      return localJFIFThumbJPEG;
    }
    
    IIOMetadataNode getNativeNode()
    {
      IIOMetadataNode localIIOMetadataNode = new IIOMetadataNode("JFIFthumbJPEG");
      if (thumbMetadata != null) {
        localIIOMetadataNode.appendChild(thumbMetadata.getNativeTree());
      }
      return localIIOMetadataNode;
    }
    
    int getLength()
    {
      if (data == null) {
        return 0;
      }
      return data.length;
    }
    
    void write(ImageOutputStream paramImageOutputStream, JPEGImageWriter paramJPEGImageWriter)
      throws IOException
    {
      int i = data.length / 20;
      if (i == 0) {
        i = 1;
      }
      int j = 0;
      while (j < data.length)
      {
        int k = Math.min(i, data.length - j);
        paramImageOutputStream.write(data, j, k);
        j += i;
        float f = j * 100.0F / data.length;
        if (f > 100.0F) {
          f = 100.0F;
        }
        paramJPEGImageWriter.thumbnailProgress(f);
      }
    }
    
    void print()
    {
      System.out.println("JFIF thumbnail stored as JPEG");
    }
    
    private class ThumbnailReadListener
      implements IIOReadProgressListener
    {
      JPEGImageReader reader = null;
      
      ThumbnailReadListener(JPEGImageReader paramJPEGImageReader)
      {
        reader = paramJPEGImageReader;
      }
      
      public void sequenceStarted(ImageReader paramImageReader, int paramInt) {}
      
      public void sequenceComplete(ImageReader paramImageReader) {}
      
      public void imageStarted(ImageReader paramImageReader, int paramInt) {}
      
      public void imageProgress(ImageReader paramImageReader, float paramFloat)
      {
        reader.thumbnailProgress(paramFloat);
      }
      
      public void imageComplete(ImageReader paramImageReader) {}
      
      public void thumbnailStarted(ImageReader paramImageReader, int paramInt1, int paramInt2) {}
      
      public void thumbnailProgress(ImageReader paramImageReader, float paramFloat) {}
      
      public void thumbnailComplete(ImageReader paramImageReader) {}
      
      public void readAborted(ImageReader paramImageReader) {}
    }
  }
  
  class JFIFThumbPalette
    extends JFIFMarkerSegment.JFIFThumbUncompressed
  {
    private static final int PALETTE_SIZE = 768;
    
    JFIFThumbPalette(JPEGBuffer paramJPEGBuffer, int paramInt1, int paramInt2)
      throws IOException
    {
      super(paramJPEGBuffer, paramInt1, paramInt2, 768 + paramInt1 * paramInt2, "JFIFThumbPalette");
    }
    
    JFIFThumbPalette(Node paramNode)
      throws IIOInvalidTreeException
    {
      super(paramNode, "JFIFThumbPalette");
    }
    
    JFIFThumbPalette(BufferedImage paramBufferedImage)
      throws JFIFMarkerSegment.IllegalThumbException
    {
      super(paramBufferedImage);
      IndexColorModel localIndexColorModel = (IndexColorModel)thumbnail.getColorModel();
      if (localIndexColorModel.getMapSize() > 256) {
        throw new JFIFMarkerSegment.IllegalThumbException(JFIFMarkerSegment.this, null);
      }
    }
    
    int getLength()
    {
      return thumbWidth * thumbHeight + 768;
    }
    
    BufferedImage getThumbnail(ImageInputStream paramImageInputStream, JPEGImageReader paramJPEGImageReader)
      throws IOException
    {
      paramImageInputStream.mark();
      paramImageInputStream.seek(streamPos);
      byte[] arrayOfByte = new byte['̀'];
      float f = 768.0F / getLength();
      readByteBuffer(paramImageInputStream, arrayOfByte, paramJPEGImageReader, f, 0.0F);
      DataBufferByte localDataBufferByte = new DataBufferByte(thumbWidth * thumbHeight);
      readByteBuffer(paramImageInputStream, localDataBufferByte.getData(), paramJPEGImageReader, 1.0F - f, f);
      paramImageInputStream.read();
      paramImageInputStream.reset();
      IndexColorModel localIndexColorModel = new IndexColorModel(8, 256, arrayOfByte, 0, false);
      SampleModel localSampleModel = localIndexColorModel.createCompatibleSampleModel(thumbWidth, thumbHeight);
      WritableRaster localWritableRaster = Raster.createWritableRaster(localSampleModel, localDataBufferByte, null);
      return new BufferedImage(localIndexColorModel, localWritableRaster, false, null);
    }
    
    void write(ImageOutputStream paramImageOutputStream, JPEGImageWriter paramJPEGImageWriter)
      throws IOException
    {
      super.write(paramImageOutputStream, paramJPEGImageWriter);
      byte[] arrayOfByte1 = new byte['̀'];
      IndexColorModel localIndexColorModel = (IndexColorModel)thumbnail.getColorModel();
      byte[] arrayOfByte2 = new byte['Ā'];
      byte[] arrayOfByte3 = new byte['Ā'];
      byte[] arrayOfByte4 = new byte['Ā'];
      localIndexColorModel.getReds(arrayOfByte2);
      localIndexColorModel.getGreens(arrayOfByte3);
      localIndexColorModel.getBlues(arrayOfByte4);
      for (int i = 0; i < 256; i++)
      {
        arrayOfByte1[(i * 3)] = arrayOfByte2[i];
        arrayOfByte1[(i * 3 + 1)] = arrayOfByte3[i];
        arrayOfByte1[(i * 3 + 2)] = arrayOfByte4[i];
      }
      paramImageOutputStream.write(arrayOfByte1);
      writePixels(paramImageOutputStream, paramJPEGImageWriter);
    }
  }
  
  class JFIFThumbRGB
    extends JFIFMarkerSegment.JFIFThumbUncompressed
  {
    JFIFThumbRGB(JPEGBuffer paramJPEGBuffer, int paramInt1, int paramInt2)
      throws IOException
    {
      super(paramJPEGBuffer, paramInt1, paramInt2, paramInt1 * paramInt2 * 3, "JFIFthumbRGB");
    }
    
    JFIFThumbRGB(Node paramNode)
      throws IIOInvalidTreeException
    {
      super(paramNode, "JFIFthumbRGB");
    }
    
    JFIFThumbRGB(BufferedImage paramBufferedImage)
      throws JFIFMarkerSegment.IllegalThumbException
    {
      super(paramBufferedImage);
    }
    
    int getLength()
    {
      return thumbWidth * thumbHeight * 3;
    }
    
    BufferedImage getThumbnail(ImageInputStream paramImageInputStream, JPEGImageReader paramJPEGImageReader)
      throws IOException
    {
      paramImageInputStream.mark();
      paramImageInputStream.seek(streamPos);
      DataBufferByte localDataBufferByte = new DataBufferByte(getLength());
      readByteBuffer(paramImageInputStream, localDataBufferByte.getData(), paramJPEGImageReader, 1.0F, 0.0F);
      paramImageInputStream.reset();
      WritableRaster localWritableRaster = Raster.createInterleavedRaster(localDataBufferByte, thumbWidth, thumbHeight, thumbWidth * 3, 3, new int[] { 0, 1, 2 }, null);
      ComponentColorModel localComponentColorModel = new ComponentColorModel(JPEG.JCS.sRGB, false, false, 1, 0);
      return new BufferedImage(localComponentColorModel, localWritableRaster, false, null);
    }
    
    void write(ImageOutputStream paramImageOutputStream, JPEGImageWriter paramJPEGImageWriter)
      throws IOException
    {
      super.write(paramImageOutputStream, paramJPEGImageWriter);
      writePixels(paramImageOutputStream, paramJPEGImageWriter);
    }
  }
  
  abstract class JFIFThumbUncompressed
    extends JFIFMarkerSegment.JFIFThumb
  {
    BufferedImage thumbnail = null;
    int thumbWidth;
    int thumbHeight;
    String name;
    
    JFIFThumbUncompressed(JPEGBuffer paramJPEGBuffer, int paramInt1, int paramInt2, int paramInt3, String paramString)
      throws IOException
    {
      super(paramJPEGBuffer);
      thumbWidth = paramInt1;
      thumbHeight = paramInt2;
      paramJPEGBuffer.skipData(paramInt3);
      name = paramString;
    }
    
    JFIFThumbUncompressed(Node paramNode, String paramString)
      throws IIOInvalidTreeException
    {
      super();
      thumbWidth = 0;
      thumbHeight = 0;
      name = paramString;
      NamedNodeMap localNamedNodeMap = paramNode.getAttributes();
      int i = localNamedNodeMap.getLength();
      if (i > 2) {
        throw new IIOInvalidTreeException(paramString + " node cannot have > 2 attributes", paramNode);
      }
      if (i != 0)
      {
        int j = MarkerSegment.getAttributeValue(paramNode, localNamedNodeMap, "thumbWidth", 0, 255, false);
        thumbWidth = (j != -1 ? j : thumbWidth);
        j = MarkerSegment.getAttributeValue(paramNode, localNamedNodeMap, "thumbHeight", 0, 255, false);
        thumbHeight = (j != -1 ? j : thumbHeight);
      }
    }
    
    JFIFThumbUncompressed(BufferedImage paramBufferedImage)
    {
      super();
      thumbnail = paramBufferedImage;
      thumbWidth = paramBufferedImage.getWidth();
      thumbHeight = paramBufferedImage.getHeight();
      name = null;
    }
    
    void readByteBuffer(ImageInputStream paramImageInputStream, byte[] paramArrayOfByte, JPEGImageReader paramJPEGImageReader, float paramFloat1, float paramFloat2)
      throws IOException
    {
      int i = Math.max((int)(paramArrayOfByte.length / 20 / paramFloat1), 1);
      int j = 0;
      while (j < paramArrayOfByte.length)
      {
        int k = Math.min(i, paramArrayOfByte.length - j);
        paramImageInputStream.read(paramArrayOfByte, j, k);
        j += i;
        float f = j * 100.0F / paramArrayOfByte.length * paramFloat1 + paramFloat2;
        if (f > 100.0F) {
          f = 100.0F;
        }
        paramJPEGImageReader.thumbnailProgress(f);
      }
    }
    
    int getWidth()
    {
      return thumbWidth;
    }
    
    int getHeight()
    {
      return thumbHeight;
    }
    
    IIOMetadataNode getNativeNode()
    {
      IIOMetadataNode localIIOMetadataNode = new IIOMetadataNode(name);
      localIIOMetadataNode.setAttribute("thumbWidth", Integer.toString(thumbWidth));
      localIIOMetadataNode.setAttribute("thumbHeight", Integer.toString(thumbHeight));
      return localIIOMetadataNode;
    }
    
    void write(ImageOutputStream paramImageOutputStream, JPEGImageWriter paramJPEGImageWriter)
      throws IOException
    {
      if ((thumbWidth > 255) || (thumbHeight > 255)) {
        paramJPEGImageWriter.warningOccurred(12);
      }
      thumbWidth = Math.min(thumbWidth, 255);
      thumbHeight = Math.min(thumbHeight, 255);
      paramImageOutputStream.write(thumbWidth);
      paramImageOutputStream.write(thumbHeight);
    }
    
    void writePixels(ImageOutputStream paramImageOutputStream, JPEGImageWriter paramJPEGImageWriter)
      throws IOException
    {
      if ((thumbWidth > 255) || (thumbHeight > 255)) {
        paramJPEGImageWriter.warningOccurred(12);
      }
      thumbWidth = Math.min(thumbWidth, 255);
      thumbHeight = Math.min(thumbHeight, 255);
      int[] arrayOfInt = thumbnail.getRaster().getPixels(0, 0, thumbWidth, thumbHeight, (int[])null);
      writeThumbnailData(paramImageOutputStream, arrayOfInt, paramJPEGImageWriter);
    }
    
    void print()
    {
      System.out.print(name + " width: ");
      System.out.println(thumbWidth);
      System.out.print(name + " height: ");
      System.out.println(thumbHeight);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\plugins\jpeg\JFIFMarkerSegment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */