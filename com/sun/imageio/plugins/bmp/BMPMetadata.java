package com.sun.imageio.plugins.bmp;

import com.sun.imageio.plugins.common.I18N;
import com.sun.imageio.plugins.common.ImageUtil;
import java.io.UnsupportedEncodingException;
import java.util.List;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.Node;

public class BMPMetadata
  extends IIOMetadata
  implements BMPConstants
{
  public static final String nativeMetadataFormatName = "javax_imageio_bmp_1.0";
  public String bmpVersion;
  public int width;
  public int height;
  public short bitsPerPixel;
  public int compression;
  public int imageSize;
  public int xPixelsPerMeter;
  public int yPixelsPerMeter;
  public int colorsUsed;
  public int colorsImportant;
  public int redMask;
  public int greenMask;
  public int blueMask;
  public int alphaMask;
  public int colorSpace;
  public double redX;
  public double redY;
  public double redZ;
  public double greenX;
  public double greenY;
  public double greenZ;
  public double blueX;
  public double blueY;
  public double blueZ;
  public int gammaRed;
  public int gammaGreen;
  public int gammaBlue;
  public int intent;
  public byte[] palette = null;
  public int paletteSize;
  public int red;
  public int green;
  public int blue;
  public List comments = null;
  
  public BMPMetadata()
  {
    super(true, "javax_imageio_bmp_1.0", "com.sun.imageio.plugins.bmp.BMPMetadataFormat", null, null);
  }
  
  public boolean isReadOnly()
  {
    return true;
  }
  
  public Node getAsTree(String paramString)
  {
    if (paramString.equals("javax_imageio_bmp_1.0")) {
      return getNativeTree();
    }
    if (paramString.equals("javax_imageio_1.0")) {
      return getStandardTree();
    }
    throw new IllegalArgumentException(I18N.getString("BMPMetadata0"));
  }
  
  private String toISO8859(byte[] paramArrayOfByte)
  {
    try
    {
      return new String(paramArrayOfByte, "ISO-8859-1");
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException) {}
    return "";
  }
  
  private Node getNativeTree()
  {
    IIOMetadataNode localIIOMetadataNode1 = new IIOMetadataNode("javax_imageio_bmp_1.0");
    addChildNode(localIIOMetadataNode1, "BMPVersion", bmpVersion);
    addChildNode(localIIOMetadataNode1, "Width", new Integer(width));
    addChildNode(localIIOMetadataNode1, "Height", new Integer(height));
    addChildNode(localIIOMetadataNode1, "BitsPerPixel", new Short(bitsPerPixel));
    addChildNode(localIIOMetadataNode1, "Compression", new Integer(compression));
    addChildNode(localIIOMetadataNode1, "ImageSize", new Integer(imageSize));
    IIOMetadataNode localIIOMetadataNode2 = addChildNode(localIIOMetadataNode1, "PixelsPerMeter", null);
    addChildNode(localIIOMetadataNode2, "X", new Integer(xPixelsPerMeter));
    addChildNode(localIIOMetadataNode2, "Y", new Integer(yPixelsPerMeter));
    addChildNode(localIIOMetadataNode1, "ColorsUsed", new Integer(colorsUsed));
    addChildNode(localIIOMetadataNode1, "ColorsImportant", new Integer(colorsImportant));
    int i = 0;
    for (int j = 0; j < bmpVersion.length(); j++) {
      if (Character.isDigit(bmpVersion.charAt(j))) {
        i = bmpVersion.charAt(j) - '0';
      }
    }
    if (i >= 4)
    {
      localIIOMetadataNode2 = addChildNode(localIIOMetadataNode1, "Mask", null);
      addChildNode(localIIOMetadataNode2, "Red", new Integer(redMask));
      addChildNode(localIIOMetadataNode2, "Green", new Integer(greenMask));
      addChildNode(localIIOMetadataNode2, "Blue", new Integer(blueMask));
      addChildNode(localIIOMetadataNode2, "Alpha", new Integer(alphaMask));
      addChildNode(localIIOMetadataNode1, "ColorSpaceType", new Integer(colorSpace));
      localIIOMetadataNode2 = addChildNode(localIIOMetadataNode1, "CIEXYZEndPoints", null);
      addXYZPoints(localIIOMetadataNode2, "Red", redX, redY, redZ);
      addXYZPoints(localIIOMetadataNode2, "Green", greenX, greenY, greenZ);
      addXYZPoints(localIIOMetadataNode2, "Blue", blueX, blueY, blueZ);
      localIIOMetadataNode2 = addChildNode(localIIOMetadataNode1, "Intent", new Integer(intent));
    }
    if ((palette != null) && (paletteSize > 0))
    {
      localIIOMetadataNode2 = addChildNode(localIIOMetadataNode1, "Palette", null);
      j = palette.length / paletteSize;
      int k = 0;
      int m = 0;
      while (k < paletteSize)
      {
        IIOMetadataNode localIIOMetadataNode3 = addChildNode(localIIOMetadataNode2, "PaletteEntry", null);
        red = (palette[(m++)] & 0xFF);
        green = (palette[(m++)] & 0xFF);
        blue = (palette[(m++)] & 0xFF);
        addChildNode(localIIOMetadataNode3, "Red", new Byte((byte)red));
        addChildNode(localIIOMetadataNode3, "Green", new Byte((byte)green));
        addChildNode(localIIOMetadataNode3, "Blue", new Byte((byte)blue));
        if (j == 4) {
          addChildNode(localIIOMetadataNode3, "Alpha", new Byte((byte)(palette[(m++)] & 0xFF)));
        }
        k++;
      }
    }
    return localIIOMetadataNode1;
  }
  
  protected IIOMetadataNode getStandardChromaNode()
  {
    if ((palette != null) && (paletteSize > 0))
    {
      IIOMetadataNode localIIOMetadataNode1 = new IIOMetadataNode("Chroma");
      IIOMetadataNode localIIOMetadataNode2 = new IIOMetadataNode("Palette");
      int i = palette.length / paletteSize;
      localIIOMetadataNode2.setAttribute("value", "" + i);
      int j = 0;
      int k = 0;
      while (j < paletteSize)
      {
        IIOMetadataNode localIIOMetadataNode3 = new IIOMetadataNode("PaletteEntry");
        localIIOMetadataNode3.setAttribute("index", "" + j);
        localIIOMetadataNode3.setAttribute("red", "" + palette[(k++)]);
        localIIOMetadataNode3.setAttribute("green", "" + palette[(k++)]);
        localIIOMetadataNode3.setAttribute("blue", "" + palette[(k++)]);
        if ((i == 4) && (palette[k] != 0)) {
          localIIOMetadataNode3.setAttribute("alpha", "" + palette[(k++)]);
        }
        localIIOMetadataNode2.appendChild(localIIOMetadataNode3);
        j++;
      }
      localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
      return localIIOMetadataNode1;
    }
    return null;
  }
  
  protected IIOMetadataNode getStandardCompressionNode()
  {
    IIOMetadataNode localIIOMetadataNode1 = new IIOMetadataNode("Compression");
    IIOMetadataNode localIIOMetadataNode2 = new IIOMetadataNode("CompressionTypeName");
    localIIOMetadataNode2.setAttribute("value", BMPCompressionTypes.getName(compression));
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    return localIIOMetadataNode1;
  }
  
  protected IIOMetadataNode getStandardDataNode()
  {
    IIOMetadataNode localIIOMetadataNode1 = new IIOMetadataNode("Data");
    String str = "";
    if (bitsPerPixel == 24) {
      str = "8 8 8 ";
    } else if ((bitsPerPixel == 16) || (bitsPerPixel == 32)) {
      str = "" + countBits(redMask) + " " + countBits(greenMask) + countBits(blueMask) + "" + countBits(alphaMask);
    }
    IIOMetadataNode localIIOMetadataNode2 = new IIOMetadataNode("BitsPerSample");
    localIIOMetadataNode2.setAttribute("value", str);
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    return localIIOMetadataNode1;
  }
  
  protected IIOMetadataNode getStandardDimensionNode()
  {
    if ((yPixelsPerMeter > 0.0F) && (xPixelsPerMeter > 0.0F))
    {
      IIOMetadataNode localIIOMetadataNode1 = new IIOMetadataNode("Dimension");
      float f = yPixelsPerMeter / xPixelsPerMeter;
      IIOMetadataNode localIIOMetadataNode2 = new IIOMetadataNode("PixelAspectRatio");
      localIIOMetadataNode2.setAttribute("value", "" + f);
      localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
      localIIOMetadataNode2 = new IIOMetadataNode("HorizontalPhysicalPixelSpacing");
      localIIOMetadataNode2.setAttribute("value", "" + 1 / xPixelsPerMeter * 1000);
      localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
      localIIOMetadataNode2 = new IIOMetadataNode("VerticalPhysicalPixelSpacing");
      localIIOMetadataNode2.setAttribute("value", "" + 1 / yPixelsPerMeter * 1000);
      localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
      return localIIOMetadataNode1;
    }
    return null;
  }
  
  public void setFromTree(String paramString, Node paramNode)
  {
    throw new IllegalStateException(I18N.getString("BMPMetadata1"));
  }
  
  public void mergeTree(String paramString, Node paramNode)
  {
    throw new IllegalStateException(I18N.getString("BMPMetadata1"));
  }
  
  public void reset()
  {
    throw new IllegalStateException(I18N.getString("BMPMetadata1"));
  }
  
  private String countBits(int paramInt)
  {
    int i = 0;
    while (paramInt > 0)
    {
      if ((paramInt & 0x1) == 1) {
        i++;
      }
      paramInt >>>= 1;
    }
    return "" + i;
  }
  
  private void addXYZPoints(IIOMetadataNode paramIIOMetadataNode, String paramString, double paramDouble1, double paramDouble2, double paramDouble3)
  {
    IIOMetadataNode localIIOMetadataNode = addChildNode(paramIIOMetadataNode, paramString, null);
    addChildNode(localIIOMetadataNode, "X", new Double(paramDouble1));
    addChildNode(localIIOMetadataNode, "Y", new Double(paramDouble2));
    addChildNode(localIIOMetadataNode, "Z", new Double(paramDouble3));
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
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\plugins\bmp\BMPMetadata.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */