package com.sun.imageio.plugins.wbmp;

import com.sun.imageio.plugins.common.I18N;
import com.sun.imageio.plugins.common.ImageUtil;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.Node;

public class WBMPMetadata
  extends IIOMetadata
{
  static final String nativeMetadataFormatName = "javax_imageio_wbmp_1.0";
  public int wbmpType;
  public int width;
  public int height;
  
  public WBMPMetadata()
  {
    super(true, "javax_imageio_wbmp_1.0", "com.sun.imageio.plugins.wbmp.WBMPMetadataFormat", null, null);
  }
  
  public boolean isReadOnly()
  {
    return true;
  }
  
  public Node getAsTree(String paramString)
  {
    if (paramString.equals("javax_imageio_wbmp_1.0")) {
      return getNativeTree();
    }
    if (paramString.equals("javax_imageio_1.0")) {
      return getStandardTree();
    }
    throw new IllegalArgumentException(I18N.getString("WBMPMetadata0"));
  }
  
  private Node getNativeTree()
  {
    IIOMetadataNode localIIOMetadataNode = new IIOMetadataNode("javax_imageio_wbmp_1.0");
    addChildNode(localIIOMetadataNode, "WBMPType", new Integer(wbmpType));
    addChildNode(localIIOMetadataNode, "Width", new Integer(width));
    addChildNode(localIIOMetadataNode, "Height", new Integer(height));
    return localIIOMetadataNode;
  }
  
  public void setFromTree(String paramString, Node paramNode)
  {
    throw new IllegalStateException(I18N.getString("WBMPMetadata1"));
  }
  
  public void mergeTree(String paramString, Node paramNode)
  {
    throw new IllegalStateException(I18N.getString("WBMPMetadata1"));
  }
  
  public void reset()
  {
    throw new IllegalStateException(I18N.getString("WBMPMetadata1"));
  }
  
  private IIOMetadataNode addChildNode(IIOMetadataNode paramIIOMetadataNode, String paramString, Object paramObject)
  {
    IIOMetadataNode localIIOMetadataNode = new IIOMetadataNode(paramString);
    if (paramObject != null)
    {
      localIIOMetadataNode.setUserObject(paramObject);
      localIIOMetadataNode.setNodeValue(ImageUtil.convertObjectToString(paramObject));
    }
    paramIIOMetadataNode.appendChild(localIIOMetadataNode);
    return localIIOMetadataNode;
  }
  
  protected IIOMetadataNode getStandardChromaNode()
  {
    IIOMetadataNode localIIOMetadataNode1 = new IIOMetadataNode("Chroma");
    IIOMetadataNode localIIOMetadataNode2 = new IIOMetadataNode("BlackIsZero");
    localIIOMetadataNode2.setAttribute("value", "TRUE");
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    return localIIOMetadataNode1;
  }
  
  protected IIOMetadataNode getStandardDimensionNode()
  {
    IIOMetadataNode localIIOMetadataNode1 = new IIOMetadataNode("Dimension");
    IIOMetadataNode localIIOMetadataNode2 = null;
    localIIOMetadataNode2 = new IIOMetadataNode("ImageOrientation");
    localIIOMetadataNode2.setAttribute("value", "Normal");
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    return localIIOMetadataNode1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\plugins\wbmp\WBMPMetadata.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */