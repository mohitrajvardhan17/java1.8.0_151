package com.sun.imageio.plugins.jpeg;

import java.io.IOException;
import java.io.PrintStream;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class SOSMarkerSegment
  extends MarkerSegment
{
  int startSpectralSelection;
  int endSpectralSelection;
  int approxHigh;
  int approxLow;
  ScanComponentSpec[] componentSpecs;
  
  SOSMarkerSegment(boolean paramBoolean, byte[] paramArrayOfByte, int paramInt)
  {
    super(218);
    startSpectralSelection = 0;
    endSpectralSelection = 63;
    approxHigh = 0;
    approxLow = 0;
    componentSpecs = new ScanComponentSpec[paramInt];
    for (int i = 0; i < paramInt; i++)
    {
      int j = 0;
      if ((paramBoolean) && ((i == 1) || (i == 2))) {
        j = 1;
      }
      componentSpecs[i] = new ScanComponentSpec(paramArrayOfByte[i], j);
    }
  }
  
  SOSMarkerSegment(JPEGBuffer paramJPEGBuffer)
    throws IOException
  {
    super(paramJPEGBuffer);
    int i = buf[(bufPtr++)];
    componentSpecs = new ScanComponentSpec[i];
    for (int j = 0; j < i; j++) {
      componentSpecs[j] = new ScanComponentSpec(paramJPEGBuffer);
    }
    startSpectralSelection = buf[(bufPtr++)];
    endSpectralSelection = buf[(bufPtr++)];
    approxHigh = (buf[bufPtr] >> 4);
    approxLow = (buf[(bufPtr++)] & 0xF);
    bufAvail -= length;
  }
  
  SOSMarkerSegment(Node paramNode)
    throws IIOInvalidTreeException
  {
    super(218);
    startSpectralSelection = 0;
    endSpectralSelection = 63;
    approxHigh = 0;
    approxLow = 0;
    updateFromNativeNode(paramNode, true);
  }
  
  protected Object clone()
  {
    SOSMarkerSegment localSOSMarkerSegment = (SOSMarkerSegment)super.clone();
    if (componentSpecs != null)
    {
      componentSpecs = ((ScanComponentSpec[])componentSpecs.clone());
      for (int i = 0; i < componentSpecs.length; i++) {
        componentSpecs[i] = ((ScanComponentSpec)componentSpecs[i].clone());
      }
    }
    return localSOSMarkerSegment;
  }
  
  IIOMetadataNode getNativeNode()
  {
    IIOMetadataNode localIIOMetadataNode = new IIOMetadataNode("sos");
    localIIOMetadataNode.setAttribute("numScanComponents", Integer.toString(componentSpecs.length));
    localIIOMetadataNode.setAttribute("startSpectralSelection", Integer.toString(startSpectralSelection));
    localIIOMetadataNode.setAttribute("endSpectralSelection", Integer.toString(endSpectralSelection));
    localIIOMetadataNode.setAttribute("approxHigh", Integer.toString(approxHigh));
    localIIOMetadataNode.setAttribute("approxLow", Integer.toString(approxLow));
    for (int i = 0; i < componentSpecs.length; i++) {
      localIIOMetadataNode.appendChild(componentSpecs[i].getNativeNode());
    }
    return localIIOMetadataNode;
  }
  
  void updateFromNativeNode(Node paramNode, boolean paramBoolean)
    throws IIOInvalidTreeException
  {
    NamedNodeMap localNamedNodeMap = paramNode.getAttributes();
    int i = getAttributeValue(paramNode, localNamedNodeMap, "numScanComponents", 1, 4, true);
    int j = getAttributeValue(paramNode, localNamedNodeMap, "startSpectralSelection", 0, 63, false);
    startSpectralSelection = (j != -1 ? j : startSpectralSelection);
    j = getAttributeValue(paramNode, localNamedNodeMap, "endSpectralSelection", 0, 63, false);
    endSpectralSelection = (j != -1 ? j : endSpectralSelection);
    j = getAttributeValue(paramNode, localNamedNodeMap, "approxHigh", 0, 15, false);
    approxHigh = (j != -1 ? j : approxHigh);
    j = getAttributeValue(paramNode, localNamedNodeMap, "approxLow", 0, 15, false);
    approxLow = (j != -1 ? j : approxLow);
    NodeList localNodeList = paramNode.getChildNodes();
    if (localNodeList.getLength() != i) {
      throw new IIOInvalidTreeException("numScanComponents must match the number of children", paramNode);
    }
    componentSpecs = new ScanComponentSpec[i];
    for (int k = 0; k < i; k++) {
      componentSpecs[k] = new ScanComponentSpec(localNodeList.item(k));
    }
  }
  
  void write(ImageOutputStream paramImageOutputStream)
    throws IOException
  {}
  
  void print()
  {
    printTag("SOS");
    System.out.print("Start spectral selection: ");
    System.out.println(startSpectralSelection);
    System.out.print("End spectral selection: ");
    System.out.println(endSpectralSelection);
    System.out.print("Approx high: ");
    System.out.println(approxHigh);
    System.out.print("Approx low: ");
    System.out.println(approxLow);
    System.out.print("Num scan components: ");
    System.out.println(componentSpecs.length);
    for (int i = 0; i < componentSpecs.length; i++) {
      componentSpecs[i].print();
    }
  }
  
  ScanComponentSpec getScanComponentSpec(byte paramByte, int paramInt)
  {
    return new ScanComponentSpec(paramByte, paramInt);
  }
  
  class ScanComponentSpec
    implements Cloneable
  {
    int componentSelector;
    int dcHuffTable;
    int acHuffTable;
    
    ScanComponentSpec(byte paramByte, int paramInt)
    {
      componentSelector = paramByte;
      dcHuffTable = paramInt;
      acHuffTable = paramInt;
    }
    
    ScanComponentSpec(JPEGBuffer paramJPEGBuffer)
    {
      componentSelector = buf[(bufPtr++)];
      dcHuffTable = (buf[bufPtr] >> 4);
      acHuffTable = (buf[(bufPtr++)] & 0xF);
    }
    
    ScanComponentSpec(Node paramNode)
      throws IIOInvalidTreeException
    {
      NamedNodeMap localNamedNodeMap = paramNode.getAttributes();
      componentSelector = MarkerSegment.getAttributeValue(paramNode, localNamedNodeMap, "componentSelector", 0, 255, true);
      dcHuffTable = MarkerSegment.getAttributeValue(paramNode, localNamedNodeMap, "dcHuffTable", 0, 3, true);
      acHuffTable = MarkerSegment.getAttributeValue(paramNode, localNamedNodeMap, "acHuffTable", 0, 3, true);
    }
    
    protected Object clone()
    {
      try
      {
        return super.clone();
      }
      catch (CloneNotSupportedException localCloneNotSupportedException) {}
      return null;
    }
    
    IIOMetadataNode getNativeNode()
    {
      IIOMetadataNode localIIOMetadataNode = new IIOMetadataNode("scanComponentSpec");
      localIIOMetadataNode.setAttribute("componentSelector", Integer.toString(componentSelector));
      localIIOMetadataNode.setAttribute("dcHuffTable", Integer.toString(dcHuffTable));
      localIIOMetadataNode.setAttribute("acHuffTable", Integer.toString(acHuffTable));
      return localIIOMetadataNode;
    }
    
    void print()
    {
      System.out.print("Component Selector: ");
      System.out.println(componentSelector);
      System.out.print("DC huffman table: ");
      System.out.println(dcHuffTable);
      System.out.print("AC huffman table: ");
      System.out.println(acHuffTable);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\plugins\jpeg\SOSMarkerSegment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */