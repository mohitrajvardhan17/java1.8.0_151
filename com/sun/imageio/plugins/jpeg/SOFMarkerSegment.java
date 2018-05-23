package com.sun.imageio.plugins.jpeg;

import java.io.IOException;
import java.io.PrintStream;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class SOFMarkerSegment
  extends MarkerSegment
{
  int samplePrecision;
  int numLines;
  int samplesPerLine;
  ComponentSpec[] componentSpecs;
  
  SOFMarkerSegment(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, byte[] paramArrayOfByte, int paramInt)
  {
    super(paramBoolean2 ? 193 : paramBoolean1 ? 194 : 192);
    samplePrecision = 8;
    numLines = 0;
    samplesPerLine = 0;
    componentSpecs = new ComponentSpec[paramInt];
    for (int i = 0; i < paramInt; i++)
    {
      int j = 1;
      int k = 0;
      if (paramBoolean3)
      {
        j = 2;
        if ((i == 1) || (i == 2))
        {
          j = 1;
          k = 1;
        }
      }
      componentSpecs[i] = new ComponentSpec(paramArrayOfByte[i], j, k);
    }
  }
  
  SOFMarkerSegment(JPEGBuffer paramJPEGBuffer)
    throws IOException
  {
    super(paramJPEGBuffer);
    samplePrecision = buf[(bufPtr++)];
    numLines = ((buf[(bufPtr++)] & 0xFF) << 8);
    numLines |= buf[(bufPtr++)] & 0xFF;
    samplesPerLine = ((buf[(bufPtr++)] & 0xFF) << 8);
    samplesPerLine |= buf[(bufPtr++)] & 0xFF;
    int i = buf[(bufPtr++)] & 0xFF;
    componentSpecs = new ComponentSpec[i];
    for (int j = 0; j < i; j++) {
      componentSpecs[j] = new ComponentSpec(paramJPEGBuffer);
    }
    bufAvail -= length;
  }
  
  SOFMarkerSegment(Node paramNode)
    throws IIOInvalidTreeException
  {
    super(192);
    samplePrecision = 8;
    numLines = 0;
    samplesPerLine = 0;
    updateFromNativeNode(paramNode, true);
  }
  
  protected Object clone()
  {
    SOFMarkerSegment localSOFMarkerSegment = (SOFMarkerSegment)super.clone();
    if (componentSpecs != null)
    {
      componentSpecs = ((ComponentSpec[])componentSpecs.clone());
      for (int i = 0; i < componentSpecs.length; i++) {
        componentSpecs[i] = ((ComponentSpec)componentSpecs[i].clone());
      }
    }
    return localSOFMarkerSegment;
  }
  
  IIOMetadataNode getNativeNode()
  {
    IIOMetadataNode localIIOMetadataNode = new IIOMetadataNode("sof");
    localIIOMetadataNode.setAttribute("process", Integer.toString(tag - 192));
    localIIOMetadataNode.setAttribute("samplePrecision", Integer.toString(samplePrecision));
    localIIOMetadataNode.setAttribute("numLines", Integer.toString(numLines));
    localIIOMetadataNode.setAttribute("samplesPerLine", Integer.toString(samplesPerLine));
    localIIOMetadataNode.setAttribute("numFrameComponents", Integer.toString(componentSpecs.length));
    for (int i = 0; i < componentSpecs.length; i++) {
      localIIOMetadataNode.appendChild(componentSpecs[i].getNativeNode());
    }
    return localIIOMetadataNode;
  }
  
  void updateFromNativeNode(Node paramNode, boolean paramBoolean)
    throws IIOInvalidTreeException
  {
    NamedNodeMap localNamedNodeMap = paramNode.getAttributes();
    int i = getAttributeValue(paramNode, localNamedNodeMap, "process", 0, 2, false);
    tag = (i != -1 ? i + 192 : tag);
    i = getAttributeValue(paramNode, localNamedNodeMap, "samplePrecision", 8, 8, false);
    i = getAttributeValue(paramNode, localNamedNodeMap, "numLines", 0, 65535, false);
    numLines = (i != -1 ? i : numLines);
    i = getAttributeValue(paramNode, localNamedNodeMap, "samplesPerLine", 0, 65535, false);
    samplesPerLine = (i != -1 ? i : samplesPerLine);
    int j = getAttributeValue(paramNode, localNamedNodeMap, "numFrameComponents", 1, 4, false);
    NodeList localNodeList = paramNode.getChildNodes();
    if (localNodeList.getLength() != j) {
      throw new IIOInvalidTreeException("numFrameComponents must match number of children", paramNode);
    }
    componentSpecs = new ComponentSpec[j];
    for (int k = 0; k < j; k++) {
      componentSpecs[k] = new ComponentSpec(localNodeList.item(k));
    }
  }
  
  void write(ImageOutputStream paramImageOutputStream)
    throws IOException
  {}
  
  void print()
  {
    printTag("SOF");
    System.out.print("Sample precision: ");
    System.out.println(samplePrecision);
    System.out.print("Number of lines: ");
    System.out.println(numLines);
    System.out.print("Samples per line: ");
    System.out.println(samplesPerLine);
    System.out.print("Number of components: ");
    System.out.println(componentSpecs.length);
    for (int i = 0; i < componentSpecs.length; i++) {
      componentSpecs[i].print();
    }
  }
  
  int getIDencodedCSType()
  {
    for (int i = 0; i < componentSpecs.length; i++) {
      if (componentSpecs[i].componentId < 65) {
        return 0;
      }
    }
    switch (componentSpecs.length)
    {
    case 3: 
      if ((componentSpecs[0].componentId == 82) && (componentSpecs[0].componentId == 71) && (componentSpecs[0].componentId == 66)) {
        return 2;
      }
      if ((componentSpecs[0].componentId == 89) && (componentSpecs[0].componentId == 67) && (componentSpecs[0].componentId == 99)) {
        return 5;
      }
      break;
    case 4: 
      if ((componentSpecs[0].componentId == 82) && (componentSpecs[0].componentId == 71) && (componentSpecs[0].componentId == 66) && (componentSpecs[0].componentId == 65)) {
        return 6;
      }
      if ((componentSpecs[0].componentId == 89) && (componentSpecs[0].componentId == 67) && (componentSpecs[0].componentId == 99) && (componentSpecs[0].componentId == 65)) {
        return 10;
      }
      break;
    }
    return 0;
  }
  
  ComponentSpec getComponentSpec(byte paramByte, int paramInt1, int paramInt2)
  {
    return new ComponentSpec(paramByte, paramInt1, paramInt2);
  }
  
  class ComponentSpec
    implements Cloneable
  {
    int componentId;
    int HsamplingFactor;
    int VsamplingFactor;
    int QtableSelector;
    
    ComponentSpec(byte paramByte, int paramInt1, int paramInt2)
    {
      componentId = paramByte;
      HsamplingFactor = paramInt1;
      VsamplingFactor = paramInt1;
      QtableSelector = paramInt2;
    }
    
    ComponentSpec(JPEGBuffer paramJPEGBuffer)
    {
      componentId = buf[(bufPtr++)];
      HsamplingFactor = (buf[bufPtr] >>> 4);
      VsamplingFactor = (buf[(bufPtr++)] & 0xF);
      QtableSelector = buf[(bufPtr++)];
    }
    
    ComponentSpec(Node paramNode)
      throws IIOInvalidTreeException
    {
      NamedNodeMap localNamedNodeMap = paramNode.getAttributes();
      componentId = MarkerSegment.getAttributeValue(paramNode, localNamedNodeMap, "componentId", 0, 255, true);
      HsamplingFactor = MarkerSegment.getAttributeValue(paramNode, localNamedNodeMap, "HsamplingFactor", 1, 255, true);
      VsamplingFactor = MarkerSegment.getAttributeValue(paramNode, localNamedNodeMap, "VsamplingFactor", 1, 255, true);
      QtableSelector = MarkerSegment.getAttributeValue(paramNode, localNamedNodeMap, "QtableSelector", 0, 3, true);
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
      IIOMetadataNode localIIOMetadataNode = new IIOMetadataNode("componentSpec");
      localIIOMetadataNode.setAttribute("componentId", Integer.toString(componentId));
      localIIOMetadataNode.setAttribute("HsamplingFactor", Integer.toString(HsamplingFactor));
      localIIOMetadataNode.setAttribute("VsamplingFactor", Integer.toString(VsamplingFactor));
      localIIOMetadataNode.setAttribute("QtableSelector", Integer.toString(QtableSelector));
      return localIIOMetadataNode;
    }
    
    void print()
    {
      System.out.print("Component ID: ");
      System.out.println(componentId);
      System.out.print("H sampling factor: ");
      System.out.println(HsamplingFactor);
      System.out.print("V sampling factor: ");
      System.out.println(VsamplingFactor);
      System.out.print("Q table selector: ");
      System.out.println(QtableSelector);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\plugins\jpeg\SOFMarkerSegment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */