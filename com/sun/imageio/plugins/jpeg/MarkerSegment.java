package com.sun.imageio.plugins.jpeg;

import java.io.IOException;
import java.io.PrintStream;
import javax.imageio.IIOException;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

class MarkerSegment
  implements Cloneable
{
  protected static final int LENGTH_SIZE = 2;
  int tag;
  int length;
  byte[] data = null;
  boolean unknown = false;
  
  MarkerSegment(JPEGBuffer paramJPEGBuffer)
    throws IOException
  {
    paramJPEGBuffer.loadBuf(3);
    tag = (buf[(bufPtr++)] & 0xFF);
    length = ((buf[(bufPtr++)] & 0xFF) << 8);
    length |= buf[(bufPtr++)] & 0xFF;
    length -= 2;
    if (length < 0) {
      throw new IIOException("Invalid segment length: " + length);
    }
    bufAvail -= 3;
    paramJPEGBuffer.loadBuf(length);
  }
  
  MarkerSegment(int paramInt)
  {
    tag = paramInt;
    length = 0;
  }
  
  MarkerSegment(Node paramNode)
    throws IIOInvalidTreeException
  {
    tag = getAttributeValue(paramNode, null, "MarkerTag", 0, 255, true);
    length = 0;
    if ((paramNode instanceof IIOMetadataNode))
    {
      IIOMetadataNode localIIOMetadataNode = (IIOMetadataNode)paramNode;
      try
      {
        data = ((byte[])localIIOMetadataNode.getUserObject());
      }
      catch (Exception localException)
      {
        IIOInvalidTreeException localIIOInvalidTreeException = new IIOInvalidTreeException("Can't get User Object", paramNode);
        localIIOInvalidTreeException.initCause(localException);
        throw localIIOInvalidTreeException;
      }
    }
    else
    {
      throw new IIOInvalidTreeException("Node must have User Object", paramNode);
    }
  }
  
  protected Object clone()
  {
    MarkerSegment localMarkerSegment = null;
    try
    {
      localMarkerSegment = (MarkerSegment)super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException) {}
    if (data != null) {
      data = ((byte[])data.clone());
    }
    return localMarkerSegment;
  }
  
  void loadData(JPEGBuffer paramJPEGBuffer)
    throws IOException
  {
    data = new byte[length];
    paramJPEGBuffer.readData(data);
  }
  
  IIOMetadataNode getNativeNode()
  {
    IIOMetadataNode localIIOMetadataNode = new IIOMetadataNode("unknown");
    localIIOMetadataNode.setAttribute("MarkerTag", Integer.toString(tag));
    localIIOMetadataNode.setUserObject(data);
    return localIIOMetadataNode;
  }
  
  static int getAttributeValue(Node paramNode, NamedNodeMap paramNamedNodeMap, String paramString, int paramInt1, int paramInt2, boolean paramBoolean)
    throws IIOInvalidTreeException
  {
    if (paramNamedNodeMap == null) {
      paramNamedNodeMap = paramNode.getAttributes();
    }
    String str = paramNamedNodeMap.getNamedItem(paramString).getNodeValue();
    int i = -1;
    if (str == null)
    {
      if (paramBoolean) {
        throw new IIOInvalidTreeException(paramString + " attribute not found", paramNode);
      }
    }
    else
    {
      i = Integer.parseInt(str);
      if ((i < paramInt1) || (i > paramInt2)) {
        throw new IIOInvalidTreeException(paramString + " attribute out of range", paramNode);
      }
    }
    return i;
  }
  
  void writeTag(ImageOutputStream paramImageOutputStream)
    throws IOException
  {
    paramImageOutputStream.write(255);
    paramImageOutputStream.write(tag);
    write2bytes(paramImageOutputStream, length);
  }
  
  void write(ImageOutputStream paramImageOutputStream)
    throws IOException
  {
    length = (2 + (data != null ? data.length : 0));
    writeTag(paramImageOutputStream);
    if (data != null) {
      paramImageOutputStream.write(data);
    }
  }
  
  static void write2bytes(ImageOutputStream paramImageOutputStream, int paramInt)
    throws IOException
  {
    paramImageOutputStream.write(paramInt >> 8 & 0xFF);
    paramImageOutputStream.write(paramInt & 0xFF);
  }
  
  void printTag(String paramString)
  {
    System.out.println(paramString + " marker segment - marker = 0x" + Integer.toHexString(tag));
    System.out.println("length: " + length);
  }
  
  void print()
  {
    printTag("Unknown");
    int i;
    if (length > 10)
    {
      System.out.print("First 5 bytes:");
      for (i = 0; i < 5; i++) {
        System.out.print(" Ox" + Integer.toHexString(data[i]));
      }
      System.out.print("\nLast 5 bytes:");
      for (i = data.length - 5; i < data.length; i++) {
        System.out.print(" Ox" + Integer.toHexString(data[i]));
      }
    }
    else
    {
      System.out.print("Data:");
      for (i = 0; i < data.length; i++) {
        System.out.print(" Ox" + Integer.toHexString(data[i]));
      }
    }
    System.out.println();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\plugins\jpeg\MarkerSegment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */