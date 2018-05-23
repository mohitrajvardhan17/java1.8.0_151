package com.sun.imageio.plugins.jpeg;

import java.awt.Point;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.ColorModel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javax.imageio.IIOException;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.plugins.jpeg.JPEGHuffmanTable;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class JPEGMetadata
  extends IIOMetadata
  implements Cloneable
{
  private static final boolean debug = false;
  private List resetSequence = null;
  private boolean inThumb = false;
  private boolean hasAlpha;
  List markerSequence = new ArrayList();
  final boolean isStream;
  private boolean transparencyDone;
  
  JPEGMetadata(boolean paramBoolean1, boolean paramBoolean2)
  {
    super(true, "javax_imageio_jpeg_image_1.0", "com.sun.imageio.plugins.jpeg.JPEGImageMetadataFormat", null, null);
    inThumb = paramBoolean2;
    isStream = paramBoolean1;
    if (paramBoolean1)
    {
      nativeMetadataFormatName = "javax_imageio_jpeg_stream_1.0";
      nativeMetadataFormatClassName = "com.sun.imageio.plugins.jpeg.JPEGStreamMetadataFormat";
    }
  }
  
  JPEGMetadata(boolean paramBoolean1, boolean paramBoolean2, ImageInputStream paramImageInputStream, JPEGImageReader paramJPEGImageReader)
    throws IOException
  {
    this(paramBoolean1, paramBoolean2);
    JPEGBuffer localJPEGBuffer = new JPEGBuffer(paramImageInputStream);
    localJPEGBuffer.loadBuf(0);
    if (((buf[0] & 0xFF) != 255) || ((buf[1] & 0xFF) != 216) || ((buf[2] & 0xFF) != 255)) {
      throw new IIOException("Image format error");
    }
    int i = 0;
    bufAvail -= 2;
    bufPtr = 2;
    Object localObject = null;
    while (i == 0)
    {
      localJPEGBuffer.loadBuf(1);
      localJPEGBuffer.scanForFF(paramJPEGImageReader);
      JFIFMarkerSegment localJFIFMarkerSegment;
      switch (buf[bufPtr] & 0xFF)
      {
      case 0: 
        bufAvail -= 1;
        bufPtr += 1;
        break;
      case 192: 
      case 193: 
      case 194: 
        if (paramBoolean1) {
          throw new IIOException("SOF not permitted in stream metadata");
        }
        localObject = new SOFMarkerSegment(localJPEGBuffer);
        break;
      case 219: 
        localObject = new DQTMarkerSegment(localJPEGBuffer);
        break;
      case 196: 
        localObject = new DHTMarkerSegment(localJPEGBuffer);
        break;
      case 221: 
        localObject = new DRIMarkerSegment(localJPEGBuffer);
        break;
      case 224: 
        localJPEGBuffer.loadBuf(8);
        byte[] arrayOfByte = buf;
        int j = bufPtr;
        if ((arrayOfByte[(j + 3)] == 74) && (arrayOfByte[(j + 4)] == 70) && (arrayOfByte[(j + 5)] == 73) && (arrayOfByte[(j + 6)] == 70) && (arrayOfByte[(j + 7)] == 0))
        {
          if (inThumb)
          {
            paramJPEGImageReader.warningOccurred(1);
            localJFIFMarkerSegment = new JFIFMarkerSegment(localJPEGBuffer);
          }
          else
          {
            if (paramBoolean1) {
              throw new IIOException("JFIF not permitted in stream metadata");
            }
            if (!markerSequence.isEmpty()) {
              throw new IIOException("JFIF APP0 must be first marker after SOI");
            }
            localObject = new JFIFMarkerSegment(localJPEGBuffer);
          }
        }
        else if ((arrayOfByte[(j + 3)] == 74) && (arrayOfByte[(j + 4)] == 70) && (arrayOfByte[(j + 5)] == 88) && (arrayOfByte[(j + 6)] == 88) && (arrayOfByte[(j + 7)] == 0))
        {
          if (paramBoolean1) {
            throw new IIOException("JFXX not permitted in stream metadata");
          }
          if (inThumb) {
            throw new IIOException("JFXX markers not allowed in JFIF JPEG thumbnail");
          }
          localJFIFMarkerSegment = (JFIFMarkerSegment)findMarkerSegment(JFIFMarkerSegment.class, true);
          if (localJFIFMarkerSegment == null) {
            throw new IIOException("JFXX encountered without prior JFIF!");
          }
          localJFIFMarkerSegment.addJFXX(localJPEGBuffer, paramJPEGImageReader);
        }
        else
        {
          localObject = new MarkerSegment(localJPEGBuffer);
          ((MarkerSegment)localObject).loadData(localJPEGBuffer);
        }
        break;
      case 226: 
        localJPEGBuffer.loadBuf(15);
        if ((buf[(bufPtr + 3)] == 73) && (buf[(bufPtr + 4)] == 67) && (buf[(bufPtr + 5)] == 67) && (buf[(bufPtr + 6)] == 95) && (buf[(bufPtr + 7)] == 80) && (buf[(bufPtr + 8)] == 82) && (buf[(bufPtr + 9)] == 79) && (buf[(bufPtr + 10)] == 70) && (buf[(bufPtr + 11)] == 73) && (buf[(bufPtr + 12)] == 76) && (buf[(bufPtr + 13)] == 69) && (buf[(bufPtr + 14)] == 0))
        {
          if (paramBoolean1) {
            throw new IIOException("ICC profiles not permitted in stream metadata");
          }
          localJFIFMarkerSegment = (JFIFMarkerSegment)findMarkerSegment(JFIFMarkerSegment.class, true);
          if (localJFIFMarkerSegment == null)
          {
            localObject = new MarkerSegment(localJPEGBuffer);
            ((MarkerSegment)localObject).loadData(localJPEGBuffer);
          }
          else
          {
            localJFIFMarkerSegment.addICC(localJPEGBuffer);
          }
        }
        else
        {
          localObject = new MarkerSegment(localJPEGBuffer);
          ((MarkerSegment)localObject).loadData(localJPEGBuffer);
        }
        break;
      case 238: 
        localJPEGBuffer.loadBuf(8);
        if ((buf[(bufPtr + 3)] == 65) && (buf[(bufPtr + 4)] == 100) && (buf[(bufPtr + 5)] == 111) && (buf[(bufPtr + 6)] == 98) && (buf[(bufPtr + 7)] == 101))
        {
          if (paramBoolean1) {
            throw new IIOException("Adobe APP14 markers not permitted in stream metadata");
          }
          localObject = new AdobeMarkerSegment(localJPEGBuffer);
        }
        else
        {
          localObject = new MarkerSegment(localJPEGBuffer);
          ((MarkerSegment)localObject).loadData(localJPEGBuffer);
        }
        break;
      case 254: 
        localObject = new COMMarkerSegment(localJPEGBuffer);
        break;
      case 218: 
        if (paramBoolean1) {
          throw new IIOException("SOS not permitted in stream metadata");
        }
        localObject = new SOSMarkerSegment(localJPEGBuffer);
        break;
      case 208: 
      case 209: 
      case 210: 
      case 211: 
      case 212: 
      case 213: 
      case 214: 
      case 215: 
        bufPtr += 1;
        bufAvail -= 1;
        break;
      case 217: 
        i = 1;
        bufPtr += 1;
        bufAvail -= 1;
        break;
      default: 
        localObject = new MarkerSegment(localJPEGBuffer);
        ((MarkerSegment)localObject).loadData(localJPEGBuffer);
        unknown = true;
      }
      if (localObject != null)
      {
        markerSequence.add(localObject);
        localObject = null;
      }
    }
    localJPEGBuffer.pushBack();
    if (!isConsistent()) {
      throw new IIOException("Inconsistent metadata read from stream");
    }
  }
  
  JPEGMetadata(ImageWriteParam paramImageWriteParam, JPEGImageWriter paramJPEGImageWriter)
  {
    this(true, false);
    JPEGImageWriteParam localJPEGImageWriteParam = null;
    if ((paramImageWriteParam != null) && ((paramImageWriteParam instanceof JPEGImageWriteParam)))
    {
      localJPEGImageWriteParam = (JPEGImageWriteParam)paramImageWriteParam;
      if (!localJPEGImageWriteParam.areTablesSet()) {
        localJPEGImageWriteParam = null;
      }
    }
    if (localJPEGImageWriteParam != null)
    {
      markerSequence.add(new DQTMarkerSegment(localJPEGImageWriteParam.getQTables()));
      markerSequence.add(new DHTMarkerSegment(localJPEGImageWriteParam.getDCHuffmanTables(), localJPEGImageWriteParam.getACHuffmanTables()));
    }
    else
    {
      markerSequence.add(new DQTMarkerSegment(JPEG.getDefaultQTables()));
      markerSequence.add(new DHTMarkerSegment(JPEG.getDefaultHuffmanTables(true), JPEG.getDefaultHuffmanTables(false)));
    }
    if (!isConsistent()) {
      throw new InternalError("Default stream metadata is inconsistent");
    }
  }
  
  JPEGMetadata(ImageTypeSpecifier paramImageTypeSpecifier, ImageWriteParam paramImageWriteParam, JPEGImageWriter paramJPEGImageWriter)
  {
    this(false, false);
    int i = 1;
    int j = 0;
    int k = 0;
    boolean bool1 = true;
    int m = 0;
    boolean bool2 = false;
    boolean bool3 = false;
    boolean bool4 = false;
    int n = 1;
    int i1 = 1;
    float f = 0.75F;
    byte[] arrayOfByte = { 1, 2, 3, 4 };
    int i2 = 0;
    ImageTypeSpecifier localImageTypeSpecifier = null;
    if (paramImageWriteParam != null)
    {
      localImageTypeSpecifier = paramImageWriteParam.getDestinationType();
      if ((localImageTypeSpecifier != null) && (paramImageTypeSpecifier != null))
      {
        paramJPEGImageWriter.warningOccurred(0);
        localImageTypeSpecifier = null;
      }
      if ((paramImageWriteParam.canWriteProgressive()) && (paramImageWriteParam.getProgressiveMode() == 1))
      {
        bool2 = true;
        bool3 = true;
        i1 = 0;
      }
      if ((paramImageWriteParam instanceof JPEGImageWriteParam))
      {
        localObject1 = (JPEGImageWriteParam)paramImageWriteParam;
        if (((JPEGImageWriteParam)localObject1).areTablesSet())
        {
          n = 0;
          i1 = 0;
          if ((((JPEGImageWriteParam)localObject1).getDCHuffmanTables().length > 2) || (((JPEGImageWriteParam)localObject1).getACHuffmanTables().length > 2)) {
            bool4 = true;
          }
        }
        if (!bool2)
        {
          bool3 = ((JPEGImageWriteParam)localObject1).getOptimizeHuffmanTables();
          if (bool3) {
            i1 = 0;
          }
        }
      }
      if ((paramImageWriteParam.canWriteCompressed()) && (paramImageWriteParam.getCompressionMode() == 2)) {
        f = paramImageWriteParam.getCompressionQuality();
      }
    }
    Object localObject1 = null;
    Object localObject2;
    int i3;
    boolean bool5;
    int i4;
    if (localImageTypeSpecifier != null)
    {
      localObject2 = localImageTypeSpecifier.getColorModel();
      i2 = ((ColorModel)localObject2).getNumComponents();
      i3 = ((ColorModel)localObject2).getNumColorComponents() != i2 ? 1 : 0;
      bool5 = ((ColorModel)localObject2).hasAlpha();
      localObject1 = ((ColorModel)localObject2).getColorSpace();
      i4 = ((ColorSpace)localObject1).getType();
      switch (i4)
      {
      case 6: 
        bool1 = false;
        if (i3 != 0) {
          i = 0;
        }
        break;
      case 13: 
        if (localObject1 == JPEG.JCS.getYCC())
        {
          i = 0;
          arrayOfByte[0] = 89;
          arrayOfByte[1] = 67;
          arrayOfByte[2] = 99;
          if (bool5) {
            arrayOfByte[3] = 65;
          }
        }
        break;
      case 3: 
        if (i3 != 0)
        {
          i = 0;
          if (!bool5)
          {
            j = 1;
            k = 2;
          }
        }
        break;
      case 5: 
        i = 0;
        j = 1;
        bool1 = false;
        arrayOfByte[0] = 82;
        arrayOfByte[1] = 71;
        arrayOfByte[2] = 66;
        if (bool5) {
          arrayOfByte[3] = 65;
        }
        break;
      default: 
        i = 0;
        bool1 = false;
      }
    }
    else if (paramImageTypeSpecifier != null)
    {
      localObject2 = paramImageTypeSpecifier.getColorModel();
      i2 = ((ColorModel)localObject2).getNumComponents();
      i3 = ((ColorModel)localObject2).getNumColorComponents() != i2 ? 1 : 0;
      bool5 = ((ColorModel)localObject2).hasAlpha();
      localObject1 = ((ColorModel)localObject2).getColorSpace();
      i4 = ((ColorSpace)localObject1).getType();
      switch (i4)
      {
      case 6: 
        bool1 = false;
        if (i3 != 0) {
          i = 0;
        }
        break;
      case 5: 
        if (bool5) {
          i = 0;
        }
        break;
      case 13: 
        i = 0;
        bool1 = false;
        if (localObject1.equals(ColorSpace.getInstance(1002)))
        {
          bool1 = true;
          j = 1;
          arrayOfByte[0] = 89;
          arrayOfByte[1] = 67;
          arrayOfByte[2] = 99;
          if (bool5) {
            arrayOfByte[3] = 65;
          }
        }
        break;
      case 3: 
        if (i3 != 0)
        {
          i = 0;
          if (!bool5)
          {
            j = 1;
            k = 2;
          }
        }
        break;
      case 9: 
        i = 0;
        j = 1;
        k = 2;
        break;
      case 4: 
      case 7: 
      case 8: 
      case 10: 
      case 11: 
      case 12: 
      default: 
        i = 0;
        bool1 = false;
      }
    }
    if ((i != 0) && (JPEG.isNonStandardICC((ColorSpace)localObject1))) {
      m = 1;
    }
    if (i != 0)
    {
      localObject2 = new JFIFMarkerSegment();
      markerSequence.add(localObject2);
      if (m != 0) {
        try
        {
          ((JFIFMarkerSegment)localObject2).addICC((ICC_ColorSpace)localObject1);
        }
        catch (IOException localIOException) {}
      }
    }
    if (j != 0) {
      markerSequence.add(new AdobeMarkerSegment(k));
    }
    if (n != 0) {
      markerSequence.add(new DQTMarkerSegment(f, bool1));
    }
    if (i1 != 0) {
      markerSequence.add(new DHTMarkerSegment(bool1));
    }
    markerSequence.add(new SOFMarkerSegment(bool2, bool4, bool1, arrayOfByte, i2));
    if (!bool2) {
      markerSequence.add(new SOSMarkerSegment(bool1, arrayOfByte, i2));
    }
    if (!isConsistent()) {
      throw new InternalError("Default image metadata is inconsistent");
    }
  }
  
  MarkerSegment findMarkerSegment(int paramInt)
  {
    Iterator localIterator = markerSequence.iterator();
    while (localIterator.hasNext())
    {
      MarkerSegment localMarkerSegment = (MarkerSegment)localIterator.next();
      if (tag == paramInt) {
        return localMarkerSegment;
      }
    }
    return null;
  }
  
  MarkerSegment findMarkerSegment(Class paramClass, boolean paramBoolean)
  {
    Object localObject;
    MarkerSegment localMarkerSegment;
    if (paramBoolean)
    {
      localObject = markerSequence.iterator();
      while (((Iterator)localObject).hasNext())
      {
        localMarkerSegment = (MarkerSegment)((Iterator)localObject).next();
        if (paramClass.isInstance(localMarkerSegment)) {
          return localMarkerSegment;
        }
      }
    }
    else
    {
      localObject = markerSequence.listIterator(markerSequence.size());
      while (((ListIterator)localObject).hasPrevious())
      {
        localMarkerSegment = (MarkerSegment)((ListIterator)localObject).previous();
        if (paramClass.isInstance(localMarkerSegment)) {
          return localMarkerSegment;
        }
      }
    }
    return null;
  }
  
  private int findMarkerSegmentPosition(Class paramClass, boolean paramBoolean)
  {
    ListIterator localListIterator;
    int i;
    MarkerSegment localMarkerSegment;
    if (paramBoolean)
    {
      localListIterator = markerSequence.listIterator();
      for (i = 0; localListIterator.hasNext(); i++)
      {
        localMarkerSegment = (MarkerSegment)localListIterator.next();
        if (paramClass.isInstance(localMarkerSegment)) {
          return i;
        }
      }
    }
    else
    {
      localListIterator = markerSequence.listIterator(markerSequence.size());
      for (i = markerSequence.size() - 1; localListIterator.hasPrevious(); i--)
      {
        localMarkerSegment = (MarkerSegment)localListIterator.previous();
        if (paramClass.isInstance(localMarkerSegment)) {
          return i;
        }
      }
    }
    return -1;
  }
  
  private int findLastUnknownMarkerSegmentPosition()
  {
    ListIterator localListIterator = markerSequence.listIterator(markerSequence.size());
    for (int i = markerSequence.size() - 1; localListIterator.hasPrevious(); i--)
    {
      MarkerSegment localMarkerSegment = (MarkerSegment)localListIterator.previous();
      if (unknown == true) {
        return i;
      }
    }
    return -1;
  }
  
  protected Object clone()
  {
    JPEGMetadata localJPEGMetadata = null;
    try
    {
      localJPEGMetadata = (JPEGMetadata)super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException) {}
    if (markerSequence != null) {
      markerSequence = cloneSequence();
    }
    resetSequence = null;
    return localJPEGMetadata;
  }
  
  private List cloneSequence()
  {
    if (markerSequence == null) {
      return null;
    }
    ArrayList localArrayList = new ArrayList(markerSequence.size());
    Iterator localIterator = markerSequence.iterator();
    while (localIterator.hasNext())
    {
      MarkerSegment localMarkerSegment = (MarkerSegment)localIterator.next();
      localArrayList.add(localMarkerSegment.clone());
    }
    return localArrayList;
  }
  
  public Node getAsTree(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("null formatName!");
    }
    if (isStream)
    {
      if (paramString.equals("javax_imageio_jpeg_stream_1.0")) {
        return getNativeTree();
      }
    }
    else
    {
      if (paramString.equals("javax_imageio_jpeg_image_1.0")) {
        return getNativeTree();
      }
      if (paramString.equals("javax_imageio_1.0")) {
        return getStandardTree();
      }
    }
    throw new IllegalArgumentException("Unsupported format name: " + paramString);
  }
  
  IIOMetadataNode getNativeTree()
  {
    Iterator localIterator = markerSequence.iterator();
    Object localObject1;
    Object localObject2;
    Object localObject3;
    if (isStream)
    {
      localObject1 = new IIOMetadataNode("javax_imageio_jpeg_stream_1.0");
      localObject2 = localObject1;
    }
    else
    {
      localObject3 = new IIOMetadataNode("markerSequence");
      if (!inThumb)
      {
        localObject1 = new IIOMetadataNode("javax_imageio_jpeg_image_1.0");
        IIOMetadataNode localIIOMetadataNode = new IIOMetadataNode("JPEGvariety");
        ((IIOMetadataNode)localObject1).appendChild(localIIOMetadataNode);
        JFIFMarkerSegment localJFIFMarkerSegment = (JFIFMarkerSegment)findMarkerSegment(JFIFMarkerSegment.class, true);
        if (localJFIFMarkerSegment != null)
        {
          localIterator.next();
          localIIOMetadataNode.appendChild(localJFIFMarkerSegment.getNativeNode());
        }
        ((IIOMetadataNode)localObject1).appendChild((Node)localObject3);
      }
      else
      {
        localObject1 = localObject3;
      }
      localObject2 = localObject3;
    }
    while (localIterator.hasNext())
    {
      localObject3 = (MarkerSegment)localIterator.next();
      ((IIOMetadataNode)localObject2).appendChild(((MarkerSegment)localObject3).getNativeNode());
    }
    return (IIOMetadataNode)localObject1;
  }
  
  protected IIOMetadataNode getStandardChromaNode()
  {
    hasAlpha = false;
    SOFMarkerSegment localSOFMarkerSegment = (SOFMarkerSegment)findMarkerSegment(SOFMarkerSegment.class, true);
    if (localSOFMarkerSegment == null) {
      return null;
    }
    IIOMetadataNode localIIOMetadataNode1 = new IIOMetadataNode("Chroma");
    IIOMetadataNode localIIOMetadataNode2 = new IIOMetadataNode("ColorSpaceType");
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    int i = componentSpecs.length;
    IIOMetadataNode localIIOMetadataNode3 = new IIOMetadataNode("NumChannels");
    localIIOMetadataNode1.appendChild(localIIOMetadataNode3);
    localIIOMetadataNode3.setAttribute("value", Integer.toString(i));
    if (findMarkerSegment(JFIFMarkerSegment.class, true) != null)
    {
      if (i == 1) {
        localIIOMetadataNode2.setAttribute("name", "GRAY");
      } else {
        localIIOMetadataNode2.setAttribute("name", "YCbCr");
      }
      return localIIOMetadataNode1;
    }
    AdobeMarkerSegment localAdobeMarkerSegment = (AdobeMarkerSegment)findMarkerSegment(AdobeMarkerSegment.class, true);
    if (localAdobeMarkerSegment != null)
    {
      switch (transform)
      {
      case 2: 
        localIIOMetadataNode2.setAttribute("name", "YCCK");
        break;
      case 1: 
        localIIOMetadataNode2.setAttribute("name", "YCbCr");
        break;
      case 0: 
        if (i == 3) {
          localIIOMetadataNode2.setAttribute("name", "RGB");
        } else if (i == 4) {
          localIIOMetadataNode2.setAttribute("name", "CMYK");
        }
        break;
      }
      return localIIOMetadataNode1;
    }
    if (i < 3)
    {
      localIIOMetadataNode2.setAttribute("name", "GRAY");
      if (i == 2) {
        hasAlpha = true;
      }
      return localIIOMetadataNode1;
    }
    int j = 1;
    for (int k = 0; k < componentSpecs.length; k++)
    {
      m = componentSpecs[k].componentId;
      if ((m < 1) || (m >= componentSpecs.length)) {
        j = 0;
      }
    }
    if (j != 0)
    {
      localIIOMetadataNode2.setAttribute("name", "YCbCr");
      if (i == 4) {
        hasAlpha = true;
      }
      return localIIOMetadataNode1;
    }
    if ((componentSpecs[0].componentId == 82) && (componentSpecs[1].componentId == 71) && (componentSpecs[2].componentId == 66))
    {
      localIIOMetadataNode2.setAttribute("name", "RGB");
      if ((i == 4) && (componentSpecs[3].componentId == 65)) {
        hasAlpha = true;
      }
      return localIIOMetadataNode1;
    }
    if ((componentSpecs[0].componentId == 89) && (componentSpecs[1].componentId == 67) && (componentSpecs[2].componentId == 99))
    {
      localIIOMetadataNode2.setAttribute("name", "PhotoYCC");
      if ((i == 4) && (componentSpecs[3].componentId == 65)) {
        hasAlpha = true;
      }
      return localIIOMetadataNode1;
    }
    k = 0;
    int m = componentSpecs[0].HsamplingFactor;
    int n = componentSpecs[0].VsamplingFactor;
    for (int i1 = 1; i1 < componentSpecs.length; i1++) {
      if ((componentSpecs[i1].HsamplingFactor != m) || (componentSpecs[i1].VsamplingFactor != n))
      {
        k = 1;
        break;
      }
    }
    if (k != 0)
    {
      localIIOMetadataNode2.setAttribute("name", "YCbCr");
      if (i == 4) {
        hasAlpha = true;
      }
      return localIIOMetadataNode1;
    }
    if (i == 3) {
      localIIOMetadataNode2.setAttribute("name", "RGB");
    } else {
      localIIOMetadataNode2.setAttribute("name", "CMYK");
    }
    return localIIOMetadataNode1;
  }
  
  protected IIOMetadataNode getStandardCompressionNode()
  {
    IIOMetadataNode localIIOMetadataNode1 = new IIOMetadataNode("Compression");
    IIOMetadataNode localIIOMetadataNode2 = new IIOMetadataNode("CompressionTypeName");
    localIIOMetadataNode2.setAttribute("value", "JPEG");
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    IIOMetadataNode localIIOMetadataNode3 = new IIOMetadataNode("Lossless");
    localIIOMetadataNode3.setAttribute("value", "FALSE");
    localIIOMetadataNode1.appendChild(localIIOMetadataNode3);
    int i = 0;
    Iterator localIterator = markerSequence.iterator();
    Object localObject;
    while (localIterator.hasNext())
    {
      localObject = (MarkerSegment)localIterator.next();
      if (tag == 218) {
        i++;
      }
    }
    if (i != 0)
    {
      localObject = new IIOMetadataNode("NumProgressiveScans");
      ((IIOMetadataNode)localObject).setAttribute("value", Integer.toString(i));
      localIIOMetadataNode1.appendChild((Node)localObject);
    }
    return localIIOMetadataNode1;
  }
  
  protected IIOMetadataNode getStandardDimensionNode()
  {
    IIOMetadataNode localIIOMetadataNode1 = new IIOMetadataNode("Dimension");
    IIOMetadataNode localIIOMetadataNode2 = new IIOMetadataNode("ImageOrientation");
    localIIOMetadataNode2.setAttribute("value", "normal");
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    JFIFMarkerSegment localJFIFMarkerSegment = (JFIFMarkerSegment)findMarkerSegment(JFIFMarkerSegment.class, true);
    if (localJFIFMarkerSegment != null)
    {
      float f1;
      if (resUnits == 0) {
        f1 = Xdensity / Ydensity;
      } else {
        f1 = Ydensity / Xdensity;
      }
      IIOMetadataNode localIIOMetadataNode3 = new IIOMetadataNode("PixelAspectRatio");
      localIIOMetadataNode3.setAttribute("value", Float.toString(f1));
      localIIOMetadataNode1.insertBefore(localIIOMetadataNode3, localIIOMetadataNode2);
      if (resUnits != 0)
      {
        float f2 = resUnits == 1 ? 25.4F : 10.0F;
        IIOMetadataNode localIIOMetadataNode4 = new IIOMetadataNode("HorizontalPixelSize");
        localIIOMetadataNode4.setAttribute("value", Float.toString(f2 / Xdensity));
        localIIOMetadataNode1.appendChild(localIIOMetadataNode4);
        IIOMetadataNode localIIOMetadataNode5 = new IIOMetadataNode("VerticalPixelSize");
        localIIOMetadataNode5.setAttribute("value", Float.toString(f2 / Ydensity));
        localIIOMetadataNode1.appendChild(localIIOMetadataNode5);
      }
    }
    return localIIOMetadataNode1;
  }
  
  protected IIOMetadataNode getStandardTextNode()
  {
    IIOMetadataNode localIIOMetadataNode1 = null;
    if (findMarkerSegment(254) != null)
    {
      localIIOMetadataNode1 = new IIOMetadataNode("Text");
      Iterator localIterator = markerSequence.iterator();
      while (localIterator.hasNext())
      {
        MarkerSegment localMarkerSegment = (MarkerSegment)localIterator.next();
        if (tag == 254)
        {
          COMMarkerSegment localCOMMarkerSegment = (COMMarkerSegment)localMarkerSegment;
          IIOMetadataNode localIIOMetadataNode2 = new IIOMetadataNode("TextEntry");
          localIIOMetadataNode2.setAttribute("keyword", "comment");
          localIIOMetadataNode2.setAttribute("value", localCOMMarkerSegment.getComment());
          localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
        }
      }
    }
    return localIIOMetadataNode1;
  }
  
  protected IIOMetadataNode getStandardTransparencyNode()
  {
    IIOMetadataNode localIIOMetadataNode1 = null;
    if (hasAlpha == true)
    {
      localIIOMetadataNode1 = new IIOMetadataNode("Transparency");
      IIOMetadataNode localIIOMetadataNode2 = new IIOMetadataNode("Alpha");
      localIIOMetadataNode2.setAttribute("value", "nonpremultiplied");
      localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    }
    return localIIOMetadataNode1;
  }
  
  public boolean isReadOnly()
  {
    return false;
  }
  
  public void mergeTree(String paramString, Node paramNode)
    throws IIOInvalidTreeException
  {
    if (paramString == null) {
      throw new IllegalArgumentException("null formatName!");
    }
    if (paramNode == null) {
      throw new IllegalArgumentException("null root!");
    }
    List localList = null;
    if (resetSequence == null)
    {
      resetSequence = cloneSequence();
      localList = resetSequence;
    }
    else
    {
      localList = cloneSequence();
    }
    if ((isStream) && (paramString.equals("javax_imageio_jpeg_stream_1.0"))) {
      mergeNativeTree(paramNode);
    } else if ((!isStream) && (paramString.equals("javax_imageio_jpeg_image_1.0"))) {
      mergeNativeTree(paramNode);
    } else if ((!isStream) && (paramString.equals("javax_imageio_1.0"))) {
      mergeStandardTree(paramNode);
    } else {
      throw new IllegalArgumentException("Unsupported format name: " + paramString);
    }
    if (!isConsistent())
    {
      markerSequence = localList;
      throw new IIOInvalidTreeException("Merged tree is invalid; original restored", paramNode);
    }
  }
  
  private void mergeNativeTree(Node paramNode)
    throws IIOInvalidTreeException
  {
    String str = paramNode.getNodeName();
    if (str != (isStream ? "javax_imageio_jpeg_stream_1.0" : "javax_imageio_jpeg_image_1.0")) {
      throw new IIOInvalidTreeException("Invalid root node name: " + str, paramNode);
    }
    if (paramNode.getChildNodes().getLength() != 2) {
      throw new IIOInvalidTreeException("JPEGvariety and markerSequence nodes must be present", paramNode);
    }
    mergeJFIFsubtree(paramNode.getFirstChild());
    mergeSequenceSubtree(paramNode.getLastChild());
  }
  
  private void mergeJFIFsubtree(Node paramNode)
    throws IIOInvalidTreeException
  {
    if (paramNode.getChildNodes().getLength() != 0)
    {
      Node localNode = paramNode.getFirstChild();
      JFIFMarkerSegment localJFIFMarkerSegment = (JFIFMarkerSegment)findMarkerSegment(JFIFMarkerSegment.class, true);
      if (localJFIFMarkerSegment != null) {
        localJFIFMarkerSegment.updateFromNativeNode(localNode, false);
      } else {
        markerSequence.add(0, new JFIFMarkerSegment(localNode));
      }
    }
  }
  
  private void mergeSequenceSubtree(Node paramNode)
    throws IIOInvalidTreeException
  {
    NodeList localNodeList = paramNode.getChildNodes();
    for (int i = 0; i < localNodeList.getLength(); i++)
    {
      Node localNode = localNodeList.item(i);
      String str = localNode.getNodeName();
      if (str.equals("dqt")) {
        mergeDQTNode(localNode);
      } else if (str.equals("dht")) {
        mergeDHTNode(localNode);
      } else if (str.equals("dri")) {
        mergeDRINode(localNode);
      } else if (str.equals("com")) {
        mergeCOMNode(localNode);
      } else if (str.equals("app14Adobe")) {
        mergeAdobeNode(localNode);
      } else if (str.equals("unknown")) {
        mergeUnknownNode(localNode);
      } else if (str.equals("sof")) {
        mergeSOFNode(localNode);
      } else if (str.equals("sos")) {
        mergeSOSNode(localNode);
      } else {
        throw new IIOInvalidTreeException("Invalid node: " + str, localNode);
      }
    }
  }
  
  private void mergeDQTNode(Node paramNode)
    throws IIOInvalidTreeException
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = markerSequence.iterator();
    Object localObject1;
    while (localIterator.hasNext())
    {
      localObject1 = (MarkerSegment)localIterator.next();
      if ((localObject1 instanceof DQTMarkerSegment)) {
        localArrayList.add(localObject1);
      }
    }
    int i;
    int k;
    if (!localArrayList.isEmpty())
    {
      localObject1 = paramNode.getChildNodes();
      for (i = 0; i < ((NodeList)localObject1).getLength(); i++)
      {
        Node localNode = ((NodeList)localObject1).item(i);
        k = MarkerSegment.getAttributeValue(localNode, null, "qtableId", 0, 3, true);
        Object localObject2 = null;
        int m = -1;
        for (int n = 0; n < localArrayList.size(); n++)
        {
          DQTMarkerSegment localDQTMarkerSegment = (DQTMarkerSegment)localArrayList.get(n);
          for (int i1 = 0; i1 < tables.size(); i1++)
          {
            DQTMarkerSegment.Qtable localQtable = (DQTMarkerSegment.Qtable)tables.get(i1);
            if (k == tableID)
            {
              localObject2 = localDQTMarkerSegment;
              m = i1;
              break;
            }
          }
          if (localObject2 != null) {
            break;
          }
        }
        if (localObject2 != null)
        {
          tables.set(m, ((DQTMarkerSegment)localObject2).getQtableFromNode(localNode));
        }
        else
        {
          localObject2 = (DQTMarkerSegment)localArrayList.get(localArrayList.size() - 1);
          tables.add(((DQTMarkerSegment)localObject2).getQtableFromNode(localNode));
        }
      }
    }
    else
    {
      localObject1 = new DQTMarkerSegment(paramNode);
      i = findMarkerSegmentPosition(DHTMarkerSegment.class, true);
      int j = findMarkerSegmentPosition(SOFMarkerSegment.class, true);
      k = findMarkerSegmentPosition(SOSMarkerSegment.class, true);
      if (i != -1) {
        markerSequence.add(i, localObject1);
      } else if (j != -1) {
        markerSequence.add(j, localObject1);
      } else if (k != -1) {
        markerSequence.add(k, localObject1);
      } else {
        markerSequence.add(localObject1);
      }
    }
  }
  
  private void mergeDHTNode(Node paramNode)
    throws IIOInvalidTreeException
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = markerSequence.iterator();
    Object localObject1;
    while (localIterator.hasNext())
    {
      localObject1 = (MarkerSegment)localIterator.next();
      if ((localObject1 instanceof DHTMarkerSegment)) {
        localArrayList.add(localObject1);
      }
    }
    int i;
    if (!localArrayList.isEmpty())
    {
      localObject1 = paramNode.getChildNodes();
      for (i = 0; i < ((NodeList)localObject1).getLength(); i++)
      {
        Node localNode = ((NodeList)localObject1).item(i);
        NamedNodeMap localNamedNodeMap = localNode.getAttributes();
        int m = MarkerSegment.getAttributeValue(localNode, localNamedNodeMap, "htableId", 0, 3, true);
        int n = MarkerSegment.getAttributeValue(localNode, localNamedNodeMap, "class", 0, 1, true);
        Object localObject2 = null;
        int i1 = -1;
        for (int i2 = 0; i2 < localArrayList.size(); i2++)
        {
          DHTMarkerSegment localDHTMarkerSegment = (DHTMarkerSegment)localArrayList.get(i2);
          for (int i3 = 0; i3 < tables.size(); i3++)
          {
            DHTMarkerSegment.Htable localHtable = (DHTMarkerSegment.Htable)tables.get(i3);
            if ((m == tableID) && (n == tableClass))
            {
              localObject2 = localDHTMarkerSegment;
              i1 = i3;
              break;
            }
          }
          if (localObject2 != null) {
            break;
          }
        }
        if (localObject2 != null)
        {
          tables.set(i1, ((DHTMarkerSegment)localObject2).getHtableFromNode(localNode));
        }
        else
        {
          localObject2 = (DHTMarkerSegment)localArrayList.get(localArrayList.size() - 1);
          tables.add(((DHTMarkerSegment)localObject2).getHtableFromNode(localNode));
        }
      }
    }
    else
    {
      localObject1 = new DHTMarkerSegment(paramNode);
      i = findMarkerSegmentPosition(DQTMarkerSegment.class, false);
      int j = findMarkerSegmentPosition(SOFMarkerSegment.class, true);
      int k = findMarkerSegmentPosition(SOSMarkerSegment.class, true);
      if (i != -1) {
        markerSequence.add(i + 1, localObject1);
      } else if (j != -1) {
        markerSequence.add(j, localObject1);
      } else if (k != -1) {
        markerSequence.add(k, localObject1);
      } else {
        markerSequence.add(localObject1);
      }
    }
  }
  
  private void mergeDRINode(Node paramNode)
    throws IIOInvalidTreeException
  {
    DRIMarkerSegment localDRIMarkerSegment1 = (DRIMarkerSegment)findMarkerSegment(DRIMarkerSegment.class, true);
    if (localDRIMarkerSegment1 != null)
    {
      localDRIMarkerSegment1.updateFromNativeNode(paramNode, false);
    }
    else
    {
      DRIMarkerSegment localDRIMarkerSegment2 = new DRIMarkerSegment(paramNode);
      int i = findMarkerSegmentPosition(SOFMarkerSegment.class, true);
      int j = findMarkerSegmentPosition(SOSMarkerSegment.class, true);
      if (i != -1) {
        markerSequence.add(i, localDRIMarkerSegment2);
      } else if (j != -1) {
        markerSequence.add(j, localDRIMarkerSegment2);
      } else {
        markerSequence.add(localDRIMarkerSegment2);
      }
    }
  }
  
  private void mergeCOMNode(Node paramNode)
    throws IIOInvalidTreeException
  {
    COMMarkerSegment localCOMMarkerSegment = new COMMarkerSegment(paramNode);
    insertCOMMarkerSegment(localCOMMarkerSegment);
  }
  
  private void insertCOMMarkerSegment(COMMarkerSegment paramCOMMarkerSegment)
  {
    int i = findMarkerSegmentPosition(COMMarkerSegment.class, false);
    int j = findMarkerSegment(JFIFMarkerSegment.class, true) != null ? 1 : 0;
    int k = findMarkerSegmentPosition(AdobeMarkerSegment.class, true);
    if (i != -1) {
      markerSequence.add(i + 1, paramCOMMarkerSegment);
    } else if (j != 0) {
      markerSequence.add(1, paramCOMMarkerSegment);
    } else if (k != -1) {
      markerSequence.add(k + 1, paramCOMMarkerSegment);
    } else {
      markerSequence.add(0, paramCOMMarkerSegment);
    }
  }
  
  private void mergeAdobeNode(Node paramNode)
    throws IIOInvalidTreeException
  {
    AdobeMarkerSegment localAdobeMarkerSegment1 = (AdobeMarkerSegment)findMarkerSegment(AdobeMarkerSegment.class, true);
    if (localAdobeMarkerSegment1 != null)
    {
      localAdobeMarkerSegment1.updateFromNativeNode(paramNode, false);
    }
    else
    {
      AdobeMarkerSegment localAdobeMarkerSegment2 = new AdobeMarkerSegment(paramNode);
      insertAdobeMarkerSegment(localAdobeMarkerSegment2);
    }
  }
  
  private void insertAdobeMarkerSegment(AdobeMarkerSegment paramAdobeMarkerSegment)
  {
    int i = findMarkerSegment(JFIFMarkerSegment.class, true) != null ? 1 : 0;
    int j = findLastUnknownMarkerSegmentPosition();
    if (i != 0) {
      markerSequence.add(1, paramAdobeMarkerSegment);
    } else if (j != -1) {
      markerSequence.add(j + 1, paramAdobeMarkerSegment);
    } else {
      markerSequence.add(0, paramAdobeMarkerSegment);
    }
  }
  
  private void mergeUnknownNode(Node paramNode)
    throws IIOInvalidTreeException
  {
    MarkerSegment localMarkerSegment = new MarkerSegment(paramNode);
    int i = findLastUnknownMarkerSegmentPosition();
    int j = findMarkerSegment(JFIFMarkerSegment.class, true) != null ? 1 : 0;
    int k = findMarkerSegmentPosition(AdobeMarkerSegment.class, true);
    if (i != -1) {
      markerSequence.add(i + 1, localMarkerSegment);
    } else if (j != 0) {
      markerSequence.add(1, localMarkerSegment);
    }
    if (k != -1) {
      markerSequence.add(k, localMarkerSegment);
    } else {
      markerSequence.add(0, localMarkerSegment);
    }
  }
  
  private void mergeSOFNode(Node paramNode)
    throws IIOInvalidTreeException
  {
    SOFMarkerSegment localSOFMarkerSegment1 = (SOFMarkerSegment)findMarkerSegment(SOFMarkerSegment.class, true);
    if (localSOFMarkerSegment1 != null)
    {
      localSOFMarkerSegment1.updateFromNativeNode(paramNode, false);
    }
    else
    {
      SOFMarkerSegment localSOFMarkerSegment2 = new SOFMarkerSegment(paramNode);
      int i = findMarkerSegmentPosition(SOSMarkerSegment.class, true);
      if (i != -1) {
        markerSequence.add(i, localSOFMarkerSegment2);
      } else {
        markerSequence.add(localSOFMarkerSegment2);
      }
    }
  }
  
  private void mergeSOSNode(Node paramNode)
    throws IIOInvalidTreeException
  {
    SOSMarkerSegment localSOSMarkerSegment1 = (SOSMarkerSegment)findMarkerSegment(SOSMarkerSegment.class, true);
    SOSMarkerSegment localSOSMarkerSegment2 = (SOSMarkerSegment)findMarkerSegment(SOSMarkerSegment.class, false);
    if (localSOSMarkerSegment1 != null)
    {
      if (localSOSMarkerSegment1 != localSOSMarkerSegment2) {
        throw new IIOInvalidTreeException("Can't merge SOS node into a tree with > 1 SOS node", paramNode);
      }
      localSOSMarkerSegment1.updateFromNativeNode(paramNode, false);
    }
    else
    {
      markerSequence.add(new SOSMarkerSegment(paramNode));
    }
  }
  
  private void mergeStandardTree(Node paramNode)
    throws IIOInvalidTreeException
  {
    transparencyDone = false;
    NodeList localNodeList = paramNode.getChildNodes();
    for (int i = 0; i < localNodeList.getLength(); i++)
    {
      Node localNode = localNodeList.item(i);
      String str = localNode.getNodeName();
      if (str.equals("Chroma")) {
        mergeStandardChromaNode(localNode, localNodeList);
      } else if (str.equals("Compression")) {
        mergeStandardCompressionNode(localNode);
      } else if (str.equals("Data")) {
        mergeStandardDataNode(localNode);
      } else if (str.equals("Dimension")) {
        mergeStandardDimensionNode(localNode);
      } else if (str.equals("Document")) {
        mergeStandardDocumentNode(localNode);
      } else if (str.equals("Text")) {
        mergeStandardTextNode(localNode);
      } else if (str.equals("Transparency")) {
        mergeStandardTransparencyNode(localNode);
      } else {
        throw new IIOInvalidTreeException("Invalid node: " + str, localNode);
      }
    }
  }
  
  private void mergeStandardChromaNode(Node paramNode, NodeList paramNodeList)
    throws IIOInvalidTreeException
  {
    if (transparencyDone) {
      throw new IIOInvalidTreeException("Transparency node must follow Chroma node", paramNode);
    }
    Node localNode = paramNode.getFirstChild();
    if ((localNode == null) || (!localNode.getNodeName().equals("ColorSpaceType"))) {
      return;
    }
    String str = localNode.getAttributes().getNamedItem("name").getNodeValue();
    int i = 0;
    int j = 0;
    int k = 0;
    int m = 0;
    boolean bool1 = false;
    byte[] arrayOfByte = { 1, 2, 3, 4 };
    if (str.equals("GRAY"))
    {
      i = 1;
      j = 1;
    }
    else if (str.equals("YCbCr"))
    {
      i = 3;
      j = 1;
      bool1 = true;
    }
    else if (str.equals("PhotoYCC"))
    {
      i = 3;
      k = 1;
      m = 1;
      arrayOfByte[0] = 89;
      arrayOfByte[1] = 67;
      arrayOfByte[2] = 99;
    }
    else if (str.equals("RGB"))
    {
      i = 3;
      k = 1;
      m = 0;
      arrayOfByte[0] = 82;
      arrayOfByte[1] = 71;
      arrayOfByte[2] = 66;
    }
    else if ((str.equals("XYZ")) || (str.equals("Lab")) || (str.equals("Luv")) || (str.equals("YxY")) || (str.equals("HSV")) || (str.equals("HLS")) || (str.equals("CMY")) || (str.equals("3CLR")))
    {
      i = 3;
    }
    else if (str.equals("YCCK"))
    {
      i = 4;
      k = 1;
      m = 2;
      bool1 = true;
    }
    else if (str.equals("CMYK"))
    {
      i = 4;
      k = 1;
      m = 0;
    }
    else if (str.equals("4CLR"))
    {
      i = 4;
    }
    else
    {
      return;
    }
    boolean bool2 = false;
    for (int n = 0; n < paramNodeList.getLength(); n++)
    {
      localObject1 = paramNodeList.item(n);
      if (((Node)localObject1).getNodeName().equals("Transparency"))
      {
        bool2 = wantAlpha((Node)localObject1);
        break;
      }
    }
    if (bool2)
    {
      i++;
      j = 0;
      if (arrayOfByte[0] == 82)
      {
        arrayOfByte[3] = 65;
        k = 0;
      }
    }
    JFIFMarkerSegment localJFIFMarkerSegment = (JFIFMarkerSegment)findMarkerSegment(JFIFMarkerSegment.class, true);
    Object localObject1 = (AdobeMarkerSegment)findMarkerSegment(AdobeMarkerSegment.class, true);
    SOFMarkerSegment localSOFMarkerSegment = (SOFMarkerSegment)findMarkerSegment(SOFMarkerSegment.class, true);
    SOSMarkerSegment localSOSMarkerSegment = (SOSMarkerSegment)findMarkerSegment(SOSMarkerSegment.class, true);
    if ((localSOFMarkerSegment != null) && (tag == 194) && (componentSpecs.length != i) && (localSOSMarkerSegment != null)) {
      return;
    }
    if ((j == 0) && (localJFIFMarkerSegment != null)) {
      markerSequence.remove(localJFIFMarkerSegment);
    }
    if ((j != 0) && (!isStream)) {
      markerSequence.add(0, new JFIFMarkerSegment());
    }
    if (k != 0)
    {
      if ((localObject1 == null) && (!isStream))
      {
        localObject1 = new AdobeMarkerSegment(m);
        insertAdobeMarkerSegment((AdobeMarkerSegment)localObject1);
      }
      else
      {
        transform = m;
      }
    }
    else if (localObject1 != null) {
      markerSequence.remove(localObject1);
    }
    int i1 = 0;
    int i2 = 0;
    boolean bool3 = false;
    int[] arrayOfInt1 = { 0, 1, 1, 0 };
    int[] arrayOfInt2 = { 0, 0, 0, 0 };
    int[] arrayOfInt3 = bool1 ? arrayOfInt1 : arrayOfInt2;
    SOFMarkerSegment.ComponentSpec[] arrayOfComponentSpec = null;
    Iterator localIterator1;
    Object localObject2;
    Object localObject3;
    if (localSOFMarkerSegment != null)
    {
      arrayOfComponentSpec = componentSpecs;
      bool3 = tag == 194;
      markerSequence.set(markerSequence.indexOf(localSOFMarkerSegment), new SOFMarkerSegment(bool3, false, bool1, arrayOfByte, i));
      for (int i3 = 0; i3 < arrayOfComponentSpec.length; i3++) {
        if (QtableSelector != arrayOfInt3[i3]) {
          i1 = 1;
        }
      }
      if (bool3)
      {
        i3 = 0;
        for (int i4 = 0; i4 < arrayOfComponentSpec.length; i4++) {
          if (arrayOfByte[i4] != componentId) {
            i3 = 1;
          }
        }
        if (i3 != 0)
        {
          localIterator1 = markerSequence.iterator();
          while (localIterator1.hasNext())
          {
            localObject2 = (MarkerSegment)localIterator1.next();
            if ((localObject2 instanceof SOSMarkerSegment))
            {
              localObject3 = (SOSMarkerSegment)localObject2;
              for (int i7 = 0; i7 < componentSpecs.length; i7++)
              {
                int i8 = componentSpecs[i7].componentSelector;
                for (int i9 = 0; i9 < arrayOfComponentSpec.length; i9++) {
                  if (componentId == i8) {
                    componentSpecs[i7].componentSelector = arrayOfByte[i9];
                  }
                }
              }
            }
          }
        }
      }
      else if (localSOSMarkerSegment != null)
      {
        for (i3 = 0; i3 < componentSpecs.length; i3++) {
          if ((componentSpecs[i3].dcHuffTable != arrayOfInt3[i3]) || (componentSpecs[i3].acHuffTable != arrayOfInt3[i3])) {
            i2 = 1;
          }
        }
        markerSequence.set(markerSequence.indexOf(localSOSMarkerSegment), new SOSMarkerSegment(bool1, arrayOfByte, i));
      }
    }
    else if (isStream)
    {
      i1 = 1;
      i2 = 1;
    }
    ArrayList localArrayList;
    Object localObject4;
    Object localObject5;
    if (i1 != 0)
    {
      localArrayList = new ArrayList();
      localIterator1 = markerSequence.iterator();
      while (localIterator1.hasNext())
      {
        localObject2 = (MarkerSegment)localIterator1.next();
        if ((localObject2 instanceof DQTMarkerSegment)) {
          localArrayList.add(localObject2);
        }
      }
      if ((!localArrayList.isEmpty()) && (bool1))
      {
        int i5 = 0;
        localObject2 = localArrayList.iterator();
        while (((Iterator)localObject2).hasNext())
        {
          localObject3 = (DQTMarkerSegment)((Iterator)localObject2).next();
          localObject4 = tables.iterator();
          while (((Iterator)localObject4).hasNext())
          {
            localObject5 = (DQTMarkerSegment.Qtable)((Iterator)localObject4).next();
            if (tableID == 1) {
              i5 = 1;
            }
          }
        }
        if (i5 == 0)
        {
          localObject2 = null;
          localObject3 = localArrayList.iterator();
          while (((Iterator)localObject3).hasNext())
          {
            localObject4 = (DQTMarkerSegment)((Iterator)localObject3).next();
            localObject5 = tables.iterator();
            while (((Iterator)localObject5).hasNext())
            {
              DQTMarkerSegment.Qtable localQtable = (DQTMarkerSegment.Qtable)((Iterator)localObject5).next();
              if (tableID == 0) {
                localObject2 = localQtable;
              }
            }
          }
          localObject3 = (DQTMarkerSegment)localArrayList.get(localArrayList.size() - 1);
          tables.add(((DQTMarkerSegment)localObject3).getChromaForLuma((DQTMarkerSegment.Qtable)localObject2));
        }
      }
    }
    if (i2 != 0)
    {
      localArrayList = new ArrayList();
      Iterator localIterator2 = markerSequence.iterator();
      while (localIterator2.hasNext())
      {
        localObject2 = (MarkerSegment)localIterator2.next();
        if ((localObject2 instanceof DHTMarkerSegment)) {
          localArrayList.add(localObject2);
        }
      }
      if ((!localArrayList.isEmpty()) && (bool1))
      {
        int i6 = 0;
        localObject2 = localArrayList.iterator();
        while (((Iterator)localObject2).hasNext())
        {
          localObject3 = (DHTMarkerSegment)((Iterator)localObject2).next();
          localObject4 = tables.iterator();
          while (((Iterator)localObject4).hasNext())
          {
            localObject5 = (DHTMarkerSegment.Htable)((Iterator)localObject4).next();
            if (tableID == 1) {
              i6 = 1;
            }
          }
        }
        if (i6 == 0)
        {
          localObject2 = (DHTMarkerSegment)localArrayList.get(localArrayList.size() - 1);
          ((DHTMarkerSegment)localObject2).addHtable(JPEGHuffmanTable.StdDCLuminance, true, 1);
          ((DHTMarkerSegment)localObject2).addHtable(JPEGHuffmanTable.StdACLuminance, true, 1);
        }
      }
    }
  }
  
  private boolean wantAlpha(Node paramNode)
  {
    boolean bool = false;
    Node localNode = paramNode.getFirstChild();
    if ((localNode.getNodeName().equals("Alpha")) && (localNode.hasAttributes()))
    {
      String str = localNode.getAttributes().getNamedItem("value").getNodeValue();
      if (!str.equals("none")) {
        bool = true;
      }
    }
    transparencyDone = true;
    return bool;
  }
  
  private void mergeStandardCompressionNode(Node paramNode)
    throws IIOInvalidTreeException
  {}
  
  private void mergeStandardDataNode(Node paramNode)
    throws IIOInvalidTreeException
  {}
  
  private void mergeStandardDimensionNode(Node paramNode)
    throws IIOInvalidTreeException
  {
    JFIFMarkerSegment localJFIFMarkerSegment = (JFIFMarkerSegment)findMarkerSegment(JFIFMarkerSegment.class, true);
    Object localObject;
    if (localJFIFMarkerSegment == null)
    {
      int i = 0;
      SOFMarkerSegment localSOFMarkerSegment = (SOFMarkerSegment)findMarkerSegment(SOFMarkerSegment.class, true);
      if (localSOFMarkerSegment != null)
      {
        int k = componentSpecs.length;
        if ((k == 1) || (k == 3))
        {
          i = 1;
          for (int m = 0; m < componentSpecs.length; m++) {
            if (componentSpecs[m].componentId != m + 1) {
              i = 0;
            }
          }
          localObject = (AdobeMarkerSegment)findMarkerSegment(AdobeMarkerSegment.class, true);
          if (localObject != null) {
            if (transform != (k == 1 ? 0 : 1)) {
              i = 0;
            }
          }
        }
      }
      if (i != 0)
      {
        localJFIFMarkerSegment = new JFIFMarkerSegment();
        markerSequence.add(0, localJFIFMarkerSegment);
      }
    }
    if (localJFIFMarkerSegment != null)
    {
      NodeList localNodeList = paramNode.getChildNodes();
      for (int j = 0; j < localNodeList.getLength(); j++)
      {
        Node localNode = localNodeList.item(j);
        localObject = localNode.getAttributes();
        String str1 = localNode.getNodeName();
        String str2;
        float f;
        if (str1.equals("PixelAspectRatio"))
        {
          str2 = ((NamedNodeMap)localObject).getNamedItem("value").getNodeValue();
          f = Float.parseFloat(str2);
          Point localPoint = findIntegerRatio(f);
          resUnits = 0;
          Xdensity = x;
          Xdensity = y;
        }
        else
        {
          int n;
          if (str1.equals("HorizontalPixelSize"))
          {
            str2 = ((NamedNodeMap)localObject).getNamedItem("value").getNodeValue();
            f = Float.parseFloat(str2);
            n = (int)Math.round(1.0D / (f * 10.0D));
            resUnits = 2;
            Xdensity = n;
          }
          else if (str1.equals("VerticalPixelSize"))
          {
            str2 = ((NamedNodeMap)localObject).getNamedItem("value").getNodeValue();
            f = Float.parseFloat(str2);
            n = (int)Math.round(1.0D / (f * 10.0D));
            resUnits = 2;
            Ydensity = n;
          }
        }
      }
    }
  }
  
  private static Point findIntegerRatio(float paramFloat)
  {
    float f1 = 0.005F;
    paramFloat = Math.abs(paramFloat);
    if (paramFloat <= f1) {
      return new Point(1, 255);
    }
    if (paramFloat >= 255.0F) {
      return new Point(255, 1);
    }
    int i = 0;
    if (paramFloat < 1.0D)
    {
      paramFloat = 1.0F / paramFloat;
      i = 1;
    }
    int j = 1;
    int k = Math.round(paramFloat);
    float f2 = k;
    for (float f3 = Math.abs(paramFloat - f2); f3 > f1; f3 = Math.abs(paramFloat - f2))
    {
      j++;
      k = Math.round(j * paramFloat);
      f2 = k / j;
    }
    return i != 0 ? new Point(j, k) : new Point(k, j);
  }
  
  private void mergeStandardDocumentNode(Node paramNode)
    throws IIOInvalidTreeException
  {}
  
  private void mergeStandardTextNode(Node paramNode)
    throws IIOInvalidTreeException
  {
    NodeList localNodeList = paramNode.getChildNodes();
    for (int i = 0; i < localNodeList.getLength(); i++)
    {
      Node localNode1 = localNodeList.item(i);
      NamedNodeMap localNamedNodeMap = localNode1.getAttributes();
      Node localNode2 = localNamedNodeMap.getNamedItem("compression");
      int j = 1;
      String str;
      if (localNode2 != null)
      {
        str = localNode2.getNodeValue();
        if (!str.equals("none")) {
          j = 0;
        }
      }
      if (j != 0)
      {
        str = localNamedNodeMap.getNamedItem("value").getNodeValue();
        COMMarkerSegment localCOMMarkerSegment = new COMMarkerSegment(str);
        insertCOMMarkerSegment(localCOMMarkerSegment);
      }
    }
  }
  
  private void mergeStandardTransparencyNode(Node paramNode)
    throws IIOInvalidTreeException
  {
    if ((!transparencyDone) && (!isStream))
    {
      boolean bool1 = wantAlpha(paramNode);
      JFIFMarkerSegment localJFIFMarkerSegment = (JFIFMarkerSegment)findMarkerSegment(JFIFMarkerSegment.class, true);
      AdobeMarkerSegment localAdobeMarkerSegment = (AdobeMarkerSegment)findMarkerSegment(AdobeMarkerSegment.class, true);
      SOFMarkerSegment localSOFMarkerSegment = (SOFMarkerSegment)findMarkerSegment(SOFMarkerSegment.class, true);
      SOSMarkerSegment localSOSMarkerSegment = (SOSMarkerSegment)findMarkerSegment(SOSMarkerSegment.class, true);
      if ((localSOFMarkerSegment != null) && (tag == 194)) {
        return;
      }
      if (localSOFMarkerSegment != null)
      {
        int i = componentSpecs.length;
        boolean bool2 = (i == 2) || (i == 4);
        if (bool2 != bool1)
        {
          SOFMarkerSegment.ComponentSpec[] arrayOfComponentSpec;
          int j;
          int k;
          if (bool1)
          {
            i++;
            if (localJFIFMarkerSegment != null) {
              markerSequence.remove(localJFIFMarkerSegment);
            }
            if (localAdobeMarkerSegment != null) {
              transform = 0;
            }
            arrayOfComponentSpec = new SOFMarkerSegment.ComponentSpec[i];
            for (j = 0; j < componentSpecs.length; j++) {
              arrayOfComponentSpec[j] = componentSpecs[j];
            }
            j = (byte)componentSpecs[0].componentId;
            k = (byte)(j > 1 ? 65 : 4);
            arrayOfComponentSpec[(i - 1)] = localSOFMarkerSegment.getComponentSpec(k, componentSpecs[0].HsamplingFactor, componentSpecs[0].QtableSelector);
            componentSpecs = arrayOfComponentSpec;
            SOSMarkerSegment.ScanComponentSpec[] arrayOfScanComponentSpec2 = new SOSMarkerSegment.ScanComponentSpec[i];
            for (int m = 0; m < componentSpecs.length; m++) {
              arrayOfScanComponentSpec2[m] = componentSpecs[m];
            }
            arrayOfScanComponentSpec2[(i - 1)] = localSOSMarkerSegment.getScanComponentSpec(k, 0);
            componentSpecs = arrayOfScanComponentSpec2;
          }
          else
          {
            i--;
            arrayOfComponentSpec = new SOFMarkerSegment.ComponentSpec[i];
            for (j = 0; j < i; j++) {
              arrayOfComponentSpec[j] = componentSpecs[j];
            }
            componentSpecs = arrayOfComponentSpec;
            SOSMarkerSegment.ScanComponentSpec[] arrayOfScanComponentSpec1 = new SOSMarkerSegment.ScanComponentSpec[i];
            for (k = 0; k < i; k++) {
              arrayOfScanComponentSpec1[k] = componentSpecs[k];
            }
            componentSpecs = arrayOfScanComponentSpec1;
          }
        }
      }
    }
  }
  
  public void setFromTree(String paramString, Node paramNode)
    throws IIOInvalidTreeException
  {
    if (paramString == null) {
      throw new IllegalArgumentException("null formatName!");
    }
    if (paramNode == null) {
      throw new IllegalArgumentException("null root!");
    }
    if ((isStream) && (paramString.equals("javax_imageio_jpeg_stream_1.0"))) {
      setFromNativeTree(paramNode);
    } else if ((!isStream) && (paramString.equals("javax_imageio_jpeg_image_1.0"))) {
      setFromNativeTree(paramNode);
    } else if ((!isStream) && (paramString.equals("javax_imageio_1.0"))) {
      super.setFromTree(paramString, paramNode);
    } else {
      throw new IllegalArgumentException("Unsupported format name: " + paramString);
    }
  }
  
  private void setFromNativeTree(Node paramNode)
    throws IIOInvalidTreeException
  {
    if (resetSequence == null) {
      resetSequence = markerSequence;
    }
    markerSequence = new ArrayList();
    String str = paramNode.getNodeName();
    if (str != (isStream ? "javax_imageio_jpeg_stream_1.0" : "javax_imageio_jpeg_image_1.0")) {
      throw new IIOInvalidTreeException("Invalid root node name: " + str, paramNode);
    }
    if (!isStream)
    {
      if (paramNode.getChildNodes().getLength() != 2) {
        throw new IIOInvalidTreeException("JPEGvariety and markerSequence nodes must be present", paramNode);
      }
      localNode = paramNode.getFirstChild();
      if (localNode.getChildNodes().getLength() != 0) {
        markerSequence.add(new JFIFMarkerSegment(localNode.getFirstChild()));
      }
    }
    Node localNode = isStream ? paramNode : paramNode.getLastChild();
    setFromMarkerSequenceNode(localNode);
  }
  
  void setFromMarkerSequenceNode(Node paramNode)
    throws IIOInvalidTreeException
  {
    NodeList localNodeList = paramNode.getChildNodes();
    for (int i = 0; i < localNodeList.getLength(); i++)
    {
      Node localNode = localNodeList.item(i);
      String str = localNode.getNodeName();
      if (str.equals("dqt")) {
        markerSequence.add(new DQTMarkerSegment(localNode));
      } else if (str.equals("dht")) {
        markerSequence.add(new DHTMarkerSegment(localNode));
      } else if (str.equals("dri")) {
        markerSequence.add(new DRIMarkerSegment(localNode));
      } else if (str.equals("com")) {
        markerSequence.add(new COMMarkerSegment(localNode));
      } else if (str.equals("app14Adobe")) {
        markerSequence.add(new AdobeMarkerSegment(localNode));
      } else if (str.equals("unknown")) {
        markerSequence.add(new MarkerSegment(localNode));
      } else if (str.equals("sof")) {
        markerSequence.add(new SOFMarkerSegment(localNode));
      } else if (str.equals("sos")) {
        markerSequence.add(new SOSMarkerSegment(localNode));
      } else {
        throw new IIOInvalidTreeException("Invalid " + (isStream ? "stream " : "image ") + "child: " + str, localNode);
      }
    }
  }
  
  private boolean isConsistent()
  {
    SOFMarkerSegment localSOFMarkerSegment = (SOFMarkerSegment)findMarkerSegment(SOFMarkerSegment.class, true);
    JFIFMarkerSegment localJFIFMarkerSegment = (JFIFMarkerSegment)findMarkerSegment(JFIFMarkerSegment.class, true);
    AdobeMarkerSegment localAdobeMarkerSegment = (AdobeMarkerSegment)findMarkerSegment(AdobeMarkerSegment.class, true);
    boolean bool = true;
    if (!isStream) {
      if (localSOFMarkerSegment != null)
      {
        int i = componentSpecs.length;
        int j = countScanBands();
        if ((j != 0) && (j != i)) {
          bool = false;
        }
        if (localJFIFMarkerSegment != null)
        {
          if ((i != 1) && (i != 3)) {
            bool = false;
          }
          for (int k = 0; k < i; k++) {
            if (componentSpecs[k].componentId != k + 1) {
              bool = false;
            }
          }
          if ((localAdobeMarkerSegment != null) && (((i == 1) && (transform != 0)) || ((i == 3) && (transform != 1)))) {
            bool = false;
          }
        }
      }
      else
      {
        SOSMarkerSegment localSOSMarkerSegment = (SOSMarkerSegment)findMarkerSegment(SOSMarkerSegment.class, true);
        if ((localJFIFMarkerSegment != null) || (localAdobeMarkerSegment != null) || (localSOFMarkerSegment != null) || (localSOSMarkerSegment != null)) {
          bool = false;
        }
      }
    }
    return bool;
  }
  
  private int countScanBands()
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = markerSequence.iterator();
    while (localIterator.hasNext())
    {
      MarkerSegment localMarkerSegment = (MarkerSegment)localIterator.next();
      if ((localMarkerSegment instanceof SOSMarkerSegment))
      {
        SOSMarkerSegment localSOSMarkerSegment = (SOSMarkerSegment)localMarkerSegment;
        SOSMarkerSegment.ScanComponentSpec[] arrayOfScanComponentSpec = componentSpecs;
        for (int i = 0; i < arrayOfScanComponentSpec.length; i++)
        {
          Integer localInteger = new Integer(componentSelector);
          if (!localArrayList.contains(localInteger)) {
            localArrayList.add(localInteger);
          }
        }
      }
    }
    return localArrayList.size();
  }
  
  void writeToStream(ImageOutputStream paramImageOutputStream, boolean paramBoolean1, boolean paramBoolean2, List paramList, ICC_Profile paramICC_Profile, boolean paramBoolean3, int paramInt, JPEGImageWriter paramJPEGImageWriter)
    throws IOException
  {
    if (paramBoolean2)
    {
      JFIFMarkerSegment.writeDefaultJFIF(paramImageOutputStream, paramList, paramICC_Profile, paramJPEGImageWriter);
      if ((!paramBoolean3) && (paramInt != -1) && (paramInt != 0) && (paramInt != 1))
      {
        paramBoolean3 = true;
        paramJPEGImageWriter.warningOccurred(13);
      }
    }
    Iterator localIterator = markerSequence.iterator();
    while (localIterator.hasNext())
    {
      MarkerSegment localMarkerSegment = (MarkerSegment)localIterator.next();
      Object localObject;
      if ((localMarkerSegment instanceof JFIFMarkerSegment))
      {
        if (!paramBoolean1)
        {
          localObject = (JFIFMarkerSegment)localMarkerSegment;
          ((JFIFMarkerSegment)localObject).writeWithThumbs(paramImageOutputStream, paramList, paramJPEGImageWriter);
          if (paramICC_Profile != null) {
            JFIFMarkerSegment.writeICC(paramICC_Profile, paramImageOutputStream);
          }
        }
      }
      else if ((localMarkerSegment instanceof AdobeMarkerSegment))
      {
        if (!paramBoolean3) {
          if (paramInt != -1)
          {
            localObject = (AdobeMarkerSegment)localMarkerSegment.clone();
            transform = paramInt;
            ((AdobeMarkerSegment)localObject).write(paramImageOutputStream);
          }
          else if (paramBoolean2)
          {
            localObject = (AdobeMarkerSegment)localMarkerSegment;
            if ((transform == 0) || (transform == 1)) {
              ((AdobeMarkerSegment)localObject).write(paramImageOutputStream);
            } else {
              paramJPEGImageWriter.warningOccurred(13);
            }
          }
          else
          {
            localMarkerSegment.write(paramImageOutputStream);
          }
        }
      }
      else {
        localMarkerSegment.write(paramImageOutputStream);
      }
    }
  }
  
  public void reset()
  {
    if (resetSequence != null)
    {
      markerSequence = resetSequence;
      resetSequence = null;
    }
  }
  
  public void print()
  {
    for (int i = 0; i < markerSequence.size(); i++)
    {
      MarkerSegment localMarkerSegment = (MarkerSegment)markerSequence.get(i);
      localMarkerSegment.print();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\plugins\jpeg\JPEGMetadata.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */