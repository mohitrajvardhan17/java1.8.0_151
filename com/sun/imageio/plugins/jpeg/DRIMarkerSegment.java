package com.sun.imageio.plugins.jpeg;

import java.io.IOException;
import java.io.PrintStream;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import org.w3c.dom.Node;

class DRIMarkerSegment
  extends MarkerSegment
{
  int restartInterval = 0;
  
  DRIMarkerSegment(JPEGBuffer paramJPEGBuffer)
    throws IOException
  {
    super(paramJPEGBuffer);
    restartInterval = ((buf[(bufPtr++)] & 0xFF) << 8);
    restartInterval |= buf[(bufPtr++)] & 0xFF;
    bufAvail -= length;
  }
  
  DRIMarkerSegment(Node paramNode)
    throws IIOInvalidTreeException
  {
    super(221);
    updateFromNativeNode(paramNode, true);
  }
  
  IIOMetadataNode getNativeNode()
  {
    IIOMetadataNode localIIOMetadataNode = new IIOMetadataNode("dri");
    localIIOMetadataNode.setAttribute("interval", Integer.toString(restartInterval));
    return localIIOMetadataNode;
  }
  
  void updateFromNativeNode(Node paramNode, boolean paramBoolean)
    throws IIOInvalidTreeException
  {
    restartInterval = getAttributeValue(paramNode, null, "interval", 0, 65535, true);
  }
  
  void write(ImageOutputStream paramImageOutputStream)
    throws IOException
  {}
  
  void print()
  {
    printTag("DRI");
    System.out.println("Interval: " + Integer.toString(restartInterval));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\plugins\jpeg\DRIMarkerSegment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */