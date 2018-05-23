package com.sun.imageio.plugins.jpeg;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

class COMMarkerSegment
  extends MarkerSegment
{
  private static final String ENCODING = "ISO-8859-1";
  
  COMMarkerSegment(JPEGBuffer paramJPEGBuffer)
    throws IOException
  {
    super(paramJPEGBuffer);
    loadData(paramJPEGBuffer);
  }
  
  COMMarkerSegment(String paramString)
  {
    super(254);
    data = paramString.getBytes();
  }
  
  COMMarkerSegment(Node paramNode)
    throws IIOInvalidTreeException
  {
    super(254);
    Object localObject;
    if ((paramNode instanceof IIOMetadataNode))
    {
      localObject = (IIOMetadataNode)paramNode;
      data = ((byte[])((IIOMetadataNode)localObject).getUserObject());
    }
    if (data == null)
    {
      localObject = paramNode.getAttributes().getNamedItem("comment").getNodeValue();
      if (localObject != null) {
        data = ((String)localObject).getBytes();
      } else {
        throw new IIOInvalidTreeException("Empty comment node!", paramNode);
      }
    }
  }
  
  String getComment()
  {
    try
    {
      return new String(data, "ISO-8859-1");
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException) {}
    return null;
  }
  
  IIOMetadataNode getNativeNode()
  {
    IIOMetadataNode localIIOMetadataNode = new IIOMetadataNode("com");
    localIIOMetadataNode.setAttribute("comment", getComment());
    if (data != null) {
      localIIOMetadataNode.setUserObject(data.clone());
    }
    return localIIOMetadataNode;
  }
  
  void write(ImageOutputStream paramImageOutputStream)
    throws IOException
  {
    length = (2 + data.length);
    writeTag(paramImageOutputStream);
    paramImageOutputStream.write(data);
  }
  
  void print()
  {
    printTag("COM");
    System.out.println("<" + getComment() + ">");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\plugins\jpeg\COMMarkerSegment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */