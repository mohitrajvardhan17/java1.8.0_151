package com.sun.imageio.plugins.gif;

import javax.imageio.metadata.IIOInvalidTreeException;
import org.w3c.dom.Node;

class GIFWritableStreamMetadata
  extends GIFStreamMetadata
{
  static final String NATIVE_FORMAT_NAME = "javax_imageio_gif_stream_1.0";
  
  public GIFWritableStreamMetadata()
  {
    super(true, "javax_imageio_gif_stream_1.0", "com.sun.imageio.plugins.gif.GIFStreamMetadataFormat", null, null);
    reset();
  }
  
  public boolean isReadOnly()
  {
    return false;
  }
  
  public void mergeTree(String paramString, Node paramNode)
    throws IIOInvalidTreeException
  {
    if (paramString.equals("javax_imageio_gif_stream_1.0"))
    {
      if (paramNode == null) {
        throw new IllegalArgumentException("root == null!");
      }
      mergeNativeTree(paramNode);
    }
    else if (paramString.equals("javax_imageio_1.0"))
    {
      if (paramNode == null) {
        throw new IllegalArgumentException("root == null!");
      }
      mergeStandardTree(paramNode);
    }
    else
    {
      throw new IllegalArgumentException("Not a recognized format!");
    }
  }
  
  public void reset()
  {
    version = null;
    logicalScreenWidth = -1;
    logicalScreenHeight = -1;
    colorResolution = -1;
    pixelAspectRatio = 0;
    backgroundColorIndex = 0;
    sortFlag = false;
    globalColorTable = null;
  }
  
  protected void mergeNativeTree(Node paramNode)
    throws IIOInvalidTreeException
  {
    Node localNode = paramNode;
    if (!localNode.getNodeName().equals("javax_imageio_gif_stream_1.0")) {
      fatal(localNode, "Root must be javax_imageio_gif_stream_1.0");
    }
    for (localNode = localNode.getFirstChild(); localNode != null; localNode = localNode.getNextSibling())
    {
      String str = localNode.getNodeName();
      if (str.equals("Version"))
      {
        version = getStringAttribute(localNode, "value", null, true, versionStrings);
      }
      else if (str.equals("LogicalScreenDescriptor"))
      {
        logicalScreenWidth = getIntAttribute(localNode, "logicalScreenWidth", -1, true, true, 1, 65535);
        logicalScreenHeight = getIntAttribute(localNode, "logicalScreenHeight", -1, true, true, 1, 65535);
        colorResolution = getIntAttribute(localNode, "colorResolution", -1, true, true, 1, 8);
        pixelAspectRatio = getIntAttribute(localNode, "pixelAspectRatio", 0, true, true, 0, 255);
      }
      else if (str.equals("GlobalColorTable"))
      {
        int i = getIntAttribute(localNode, "sizeOfGlobalColorTable", true, 2, 256);
        if ((i != 2) && (i != 4) && (i != 8) && (i != 16) && (i != 32) && (i != 64) && (i != 128) && (i != 256)) {
          fatal(localNode, "Bad value for GlobalColorTable attribute sizeOfGlobalColorTable!");
        }
        backgroundColorIndex = getIntAttribute(localNode, "backgroundColorIndex", 0, true, true, 0, 255);
        sortFlag = getBooleanAttribute(localNode, "sortFlag", false, true);
        globalColorTable = getColorTable(localNode, "ColorTableEntry", true, i);
      }
      else
      {
        fatal(localNode, "Unknown child of root node!");
      }
    }
  }
  
  protected void mergeStandardTree(Node paramNode)
    throws IIOInvalidTreeException
  {
    Node localNode1 = paramNode;
    if (!localNode1.getNodeName().equals("javax_imageio_1.0")) {
      fatal(localNode1, "Root must be javax_imageio_1.0");
    }
    for (localNode1 = localNode1.getFirstChild(); localNode1 != null; localNode1 = localNode1.getNextSibling())
    {
      String str1 = localNode1.getNodeName();
      Node localNode2;
      String str2;
      if (str1.equals("Chroma"))
      {
        for (localNode2 = localNode1.getFirstChild(); localNode2 != null; localNode2 = localNode2.getNextSibling())
        {
          str2 = localNode2.getNodeName();
          if (str2.equals("Palette")) {
            globalColorTable = getColorTable(localNode2, "PaletteEntry", false, -1);
          } else if (str2.equals("BackgroundIndex")) {
            backgroundColorIndex = getIntAttribute(localNode2, "value", -1, true, true, 0, 255);
          }
        }
      }
      else if (str1.equals("Data"))
      {
        for (localNode2 = localNode1.getFirstChild(); localNode2 != null; localNode2 = localNode2.getNextSibling())
        {
          str2 = localNode2.getNodeName();
          if (str2.equals("BitsPerSample"))
          {
            colorResolution = getIntAttribute(localNode2, "value", -1, true, true, 1, 8);
            break;
          }
        }
      }
      else
      {
        int i;
        if (str1.equals("Dimension")) {
          for (localNode2 = localNode1.getFirstChild(); localNode2 != null; localNode2 = localNode2.getNextSibling())
          {
            str2 = localNode2.getNodeName();
            if (str2.equals("PixelAspectRatio"))
            {
              float f = getFloatAttribute(localNode2, "value");
              if (f == 1.0F)
              {
                pixelAspectRatio = 0;
              }
              else
              {
                i = (int)(f * 64.0F - 15.0F);
                pixelAspectRatio = Math.max(Math.min(i, 255), 0);
              }
            }
            else if (str2.equals("HorizontalScreenSize"))
            {
              logicalScreenWidth = getIntAttribute(localNode2, "value", -1, true, true, 1, 65535);
            }
            else if (str2.equals("VerticalScreenSize"))
            {
              logicalScreenHeight = getIntAttribute(localNode2, "value", -1, true, true, 1, 65535);
            }
          }
        } else if (str1.equals("Document")) {
          for (localNode2 = localNode1.getFirstChild(); localNode2 != null; localNode2 = localNode2.getNextSibling())
          {
            str2 = localNode2.getNodeName();
            if (str2.equals("FormatVersion"))
            {
              String str3 = getStringAttribute(localNode2, "value", null, true, null);
              for (i = 0; i < versionStrings.length; i++) {
                if (str3.equals(versionStrings[i]))
                {
                  version = str3;
                  break;
                }
              }
              break;
            }
          }
        }
      }
    }
  }
  
  public void setFromTree(String paramString, Node paramNode)
    throws IIOInvalidTreeException
  {
    reset();
    mergeTree(paramString, paramNode);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\plugins\gif\GIFWritableStreamMetadata.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */