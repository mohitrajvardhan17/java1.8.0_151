package com.sun.imageio.plugins.common;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import javax.imageio.ImageTypeSpecifier;

public class PaletteBuilder
{
  protected static final int MAXLEVEL = 8;
  protected RenderedImage src;
  protected ColorModel srcColorModel;
  protected Raster srcRaster;
  protected int requiredSize;
  protected ColorNode root;
  protected int numNodes;
  protected int maxNodes;
  protected int currLevel;
  protected int currSize;
  protected ColorNode[] reduceList;
  protected ColorNode[] palette;
  protected int transparency;
  protected ColorNode transColor;
  
  public static RenderedImage createIndexedImage(RenderedImage paramRenderedImage)
  {
    PaletteBuilder localPaletteBuilder = new PaletteBuilder(paramRenderedImage);
    localPaletteBuilder.buildPalette();
    return localPaletteBuilder.getIndexedImage();
  }
  
  public static IndexColorModel createIndexColorModel(RenderedImage paramRenderedImage)
  {
    PaletteBuilder localPaletteBuilder = new PaletteBuilder(paramRenderedImage);
    localPaletteBuilder.buildPalette();
    return localPaletteBuilder.getIndexColorModel();
  }
  
  public static boolean canCreatePalette(ImageTypeSpecifier paramImageTypeSpecifier)
  {
    if (paramImageTypeSpecifier == null) {
      throw new IllegalArgumentException("type == null");
    }
    return true;
  }
  
  public static boolean canCreatePalette(RenderedImage paramRenderedImage)
  {
    if (paramRenderedImage == null) {
      throw new IllegalArgumentException("image == null");
    }
    ImageTypeSpecifier localImageTypeSpecifier = new ImageTypeSpecifier(paramRenderedImage);
    return canCreatePalette(localImageTypeSpecifier);
  }
  
  protected RenderedImage getIndexedImage()
  {
    IndexColorModel localIndexColorModel = getIndexColorModel();
    BufferedImage localBufferedImage = new BufferedImage(src.getWidth(), src.getHeight(), 13, localIndexColorModel);
    WritableRaster localWritableRaster = localBufferedImage.getRaster();
    for (int i = 0; i < localBufferedImage.getHeight(); i++) {
      for (int j = 0; j < localBufferedImage.getWidth(); j++)
      {
        Color localColor = getSrcColor(j, i);
        localWritableRaster.setSample(j, i, 0, findColorIndex(root, localColor));
      }
    }
    return localBufferedImage;
  }
  
  protected PaletteBuilder(RenderedImage paramRenderedImage)
  {
    this(paramRenderedImage, 256);
  }
  
  protected PaletteBuilder(RenderedImage paramRenderedImage, int paramInt)
  {
    src = paramRenderedImage;
    srcColorModel = paramRenderedImage.getColorModel();
    srcRaster = paramRenderedImage.getData();
    transparency = srcColorModel.getTransparency();
    requiredSize = paramInt;
  }
  
  private Color getSrcColor(int paramInt1, int paramInt2)
  {
    int i = srcColorModel.getRGB(srcRaster.getDataElements(paramInt1, paramInt2, null));
    return new Color(i, transparency != 1);
  }
  
  protected int findColorIndex(ColorNode paramColorNode, Color paramColor)
  {
    if ((transparency != 1) && (paramColor.getAlpha() != 255)) {
      return 0;
    }
    if (isLeaf) {
      return paletteIndex;
    }
    int i = getBranchIndex(paramColor, level);
    return findColorIndex(children[i], paramColor);
  }
  
  protected void buildPalette()
  {
    reduceList = new ColorNode[9];
    for (int i = 0; i < reduceList.length; i++) {
      reduceList[i] = null;
    }
    numNodes = 0;
    maxNodes = 0;
    root = null;
    currSize = 0;
    currLevel = 8;
    i = src.getWidth();
    int j = src.getHeight();
    for (int k = 0; k < j; k++) {
      for (int m = 0; m < i; m++)
      {
        Color localColor = getSrcColor(i - m - 1, j - k - 1);
        if ((transparency != 1) && (localColor.getAlpha() != 255))
        {
          if (transColor == null)
          {
            requiredSize -= 1;
            transColor = new ColorNode();
            transColor.isLeaf = true;
          }
          transColor = insertNode(transColor, localColor, 0);
        }
        else
        {
          root = insertNode(root, localColor, 0);
        }
        if (currSize > requiredSize) {
          reduceTree();
        }
      }
    }
  }
  
  protected ColorNode insertNode(ColorNode paramColorNode, Color paramColor, int paramInt)
  {
    if (paramColorNode == null)
    {
      paramColorNode = new ColorNode();
      numNodes += 1;
      if (numNodes > maxNodes) {
        maxNodes = numNodes;
      }
      level = paramInt;
      isLeaf = (paramInt > 8);
      if (isLeaf) {
        currSize += 1;
      }
    }
    colorCount += 1;
    red += paramColor.getRed();
    green += paramColor.getGreen();
    blue += paramColor.getBlue();
    if (!isLeaf)
    {
      int i = getBranchIndex(paramColor, paramInt);
      if (children[i] == null)
      {
        childCount += 1;
        if (childCount == 2)
        {
          nextReducible = reduceList[paramInt];
          reduceList[paramInt] = paramColorNode;
        }
      }
      children[i] = insertNode(children[i], paramColor, paramInt + 1);
    }
    return paramColorNode;
  }
  
  protected IndexColorModel getIndexColorModel()
  {
    int i = currSize;
    if (transColor != null) {
      i++;
    }
    byte[] arrayOfByte1 = new byte[i];
    byte[] arrayOfByte2 = new byte[i];
    byte[] arrayOfByte3 = new byte[i];
    int j = 0;
    palette = new ColorNode[i];
    if (transColor != null) {
      j++;
    }
    if (root != null) {
      findPaletteEntry(root, j, arrayOfByte1, arrayOfByte2, arrayOfByte3);
    }
    IndexColorModel localIndexColorModel = null;
    if (transColor != null) {
      localIndexColorModel = new IndexColorModel(8, i, arrayOfByte1, arrayOfByte2, arrayOfByte3, 0);
    } else {
      localIndexColorModel = new IndexColorModel(8, currSize, arrayOfByte1, arrayOfByte2, arrayOfByte3);
    }
    return localIndexColorModel;
  }
  
  protected int findPaletteEntry(ColorNode paramColorNode, int paramInt, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3)
  {
    if (isLeaf)
    {
      paramArrayOfByte1[paramInt] = ((byte)(int)(red / colorCount));
      paramArrayOfByte2[paramInt] = ((byte)(int)(green / colorCount));
      paramArrayOfByte3[paramInt] = ((byte)(int)(blue / colorCount));
      paletteIndex = paramInt;
      palette[paramInt] = paramColorNode;
      paramInt++;
    }
    else
    {
      for (int i = 0; i < 8; i++) {
        if (children[i] != null) {
          paramInt = findPaletteEntry(children[i], paramInt, paramArrayOfByte1, paramArrayOfByte2, paramArrayOfByte3);
        }
      }
    }
    return paramInt;
  }
  
  protected int getBranchIndex(Color paramColor, int paramInt)
  {
    if ((paramInt > 8) || (paramInt < 0)) {
      throw new IllegalArgumentException("Invalid octree node depth: " + paramInt);
    }
    int i = 8 - paramInt;
    int j = 0x1 & (0xFF & paramColor.getRed()) >> i;
    int k = 0x1 & (0xFF & paramColor.getGreen()) >> i;
    int m = 0x1 & (0xFF & paramColor.getBlue()) >> i;
    int n = j << 2 | k << 1 | m;
    return n;
  }
  
  protected void reduceTree()
  {
    for (int i = reduceList.length - 1; (reduceList[i] == null) && (i >= 0); i--) {}
    Object localObject1 = reduceList[i];
    if (localObject1 == null) {
      return;
    }
    Object localObject2 = localObject1;
    int j = colorCount;
    for (int k = 1; nextReducible != null; k++)
    {
      if (j > nextReducible.colorCount)
      {
        localObject1 = localObject2;
        j = colorCount;
      }
      localObject2 = nextReducible;
    }
    if (localObject1 == reduceList[i])
    {
      reduceList[i] = nextReducible;
    }
    else
    {
      localObject2 = nextReducible;
      nextReducible = nextReducible;
      localObject1 = localObject2;
    }
    if (isLeaf) {
      return;
    }
    int m = ((ColorNode)localObject1).getLeafChildCount();
    isLeaf = true;
    currSize -= m - 1;
    int n = level;
    for (int i1 = 0; i1 < 8; i1++) {
      children[i1] = freeTree(children[i1]);
    }
    childCount = 0;
  }
  
  protected ColorNode freeTree(ColorNode paramColorNode)
  {
    if (paramColorNode == null) {
      return null;
    }
    for (int i = 0; i < 8; i++) {
      children[i] = freeTree(children[i]);
    }
    numNodes -= 1;
    return null;
  }
  
  protected class ColorNode
  {
    public boolean isLeaf = false;
    public int childCount = 0;
    ColorNode[] children = new ColorNode[8];
    public int colorCount;
    public long red;
    public long blue;
    public long green;
    public int paletteIndex;
    public int level = 0;
    ColorNode nextReducible;
    
    public ColorNode()
    {
      for (int i = 0; i < 8; i++) {
        children[i] = null;
      }
      colorCount = 0;
      red = (green = blue = 0L);
      paletteIndex = 0;
    }
    
    public int getLeafChildCount()
    {
      if (isLeaf) {
        return 0;
      }
      int i = 0;
      for (int j = 0; j < children.length; j++) {
        if (children[j] != null) {
          if (children[j].isLeaf) {
            i++;
          } else {
            i += children[j].getLeafChildCount();
          }
        }
      }
      return i;
    }
    
    public int getRGB()
    {
      int i = (int)red / colorCount;
      int j = (int)green / colorCount;
      int k = (int)blue / colorCount;
      int m = 0xFF000000 | (0xFF & i) << 16 | (0xFF & j) << 8 | 0xFF & k;
      return m;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\plugins\common\PaletteBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */