package com.sun.imageio.plugins.jpeg;

import java.io.IOException;
import java.io.PrintStream;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

class AdobeMarkerSegment
  extends MarkerSegment
{
  int version;
  int flags0;
  int flags1;
  int transform;
  private static final int ID_SIZE = 5;
  
  AdobeMarkerSegment(int paramInt)
  {
    super(238);
    version = 101;
    flags0 = 0;
    flags1 = 0;
    transform = paramInt;
  }
  
  AdobeMarkerSegment(JPEGBuffer paramJPEGBuffer)
    throws IOException
  {
    super(paramJPEGBuffer);
    bufPtr += 5;
    version = ((buf[(bufPtr++)] & 0xFF) << 8);
    version |= buf[(bufPtr++)] & 0xFF;
    flags0 = ((buf[(bufPtr++)] & 0xFF) << 8);
    flags0 |= buf[(bufPtr++)] & 0xFF;
    flags1 = ((buf[(bufPtr++)] & 0xFF) << 8);
    flags1 |= buf[(bufPtr++)] & 0xFF;
    transform = (buf[(bufPtr++)] & 0xFF);
    bufAvail -= length;
  }
  
  AdobeMarkerSegment(Node paramNode)
    throws IIOInvalidTreeException
  {
    this(0);
    updateFromNativeNode(paramNode, true);
  }
  
  IIOMetadataNode getNativeNode()
  {
    IIOMetadataNode localIIOMetadataNode = new IIOMetadataNode("app14Adobe");
    localIIOMetadataNode.setAttribute("version", Integer.toString(version));
    localIIOMetadataNode.setAttribute("flags0", Integer.toString(flags0));
    localIIOMetadataNode.setAttribute("flags1", Integer.toString(flags1));
    localIIOMetadataNode.setAttribute("transform", Integer.toString(transform));
    return localIIOMetadataNode;
  }
  
  void updateFromNativeNode(Node paramNode, boolean paramBoolean)
    throws IIOInvalidTreeException
  {
    NamedNodeMap localNamedNodeMap = paramNode.getAttributes();
    transform = getAttributeValue(paramNode, localNamedNodeMap, "transform", 0, 2, true);
    int i = localNamedNodeMap.getLength();
    if (i > 4) {
      throw new IIOInvalidTreeException("Adobe APP14 node cannot have > 4 attributes", paramNode);
    }
    if (i > 1)
    {
      int j = getAttributeValue(paramNode, localNamedNodeMap, "version", 100, 255, false);
      version = (j != -1 ? j : version);
      j = getAttributeValue(paramNode, localNamedNodeMap, "flags0", 0, 65535, false);
      flags0 = (j != -1 ? j : flags0);
      j = getAttributeValue(paramNode, localNamedNodeMap, "flags1", 0, 65535, false);
      flags1 = (j != -1 ? j : flags1);
    }
  }
  
  void write(ImageOutputStream paramImageOutputStream)
    throws IOException
  {
    length = 14;
    writeTag(paramImageOutputStream);
    byte[] arrayOfByte = { 65, 100, 111, 98, 101 };
    paramImageOutputStream.write(arrayOfByte);
    write2bytes(paramImageOutputStream, version);
    write2bytes(paramImageOutputStream, flags0);
    write2bytes(paramImageOutputStream, flags1);
    paramImageOutputStream.write(transform);
  }
  
  static void writeAdobeSegment(ImageOutputStream paramImageOutputStream, int paramInt)
    throws IOException
  {
    new AdobeMarkerSegment(paramInt).write(paramImageOutputStream);
  }
  
  void print()
  {
    printTag("Adobe APP14");
    System.out.print("Version: ");
    System.out.println(version);
    System.out.print("Flags0: 0x");
    System.out.println(Integer.toHexString(flags0));
    System.out.print("Flags1: 0x");
    System.out.println(Integer.toHexString(flags1));
    System.out.print("Transform: ");
    System.out.println(transform);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\plugins\jpeg\AdobeMarkerSegment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */