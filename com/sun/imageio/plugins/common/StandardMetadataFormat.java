package com.sun.imageio.plugins.common;

import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadataFormatImpl;

public class StandardMetadataFormat
  extends IIOMetadataFormatImpl
{
  private void addSingleAttributeElement(String paramString1, String paramString2, int paramInt)
  {
    addElement(paramString1, paramString2, 0);
    addAttribute(paramString1, "value", paramInt, true, null);
  }
  
  public StandardMetadataFormat()
  {
    super("javax_imageio_1.0", 2);
    addElement("Chroma", "javax_imageio_1.0", 2);
    addElement("ColorSpaceType", "Chroma", 0);
    ArrayList localArrayList = new ArrayList();
    localArrayList.add("XYZ");
    localArrayList.add("Lab");
    localArrayList.add("Luv");
    localArrayList.add("YCbCr");
    localArrayList.add("Yxy");
    localArrayList.add("YCCK");
    localArrayList.add("PhotoYCC");
    localArrayList.add("RGB");
    localArrayList.add("GRAY");
    localArrayList.add("HSV");
    localArrayList.add("HLS");
    localArrayList.add("CMYK");
    localArrayList.add("CMY");
    localArrayList.add("2CLR");
    localArrayList.add("3CLR");
    localArrayList.add("4CLR");
    localArrayList.add("5CLR");
    localArrayList.add("6CLR");
    localArrayList.add("7CLR");
    localArrayList.add("8CLR");
    localArrayList.add("9CLR");
    localArrayList.add("ACLR");
    localArrayList.add("BCLR");
    localArrayList.add("CCLR");
    localArrayList.add("DCLR");
    localArrayList.add("ECLR");
    localArrayList.add("FCLR");
    addAttribute("ColorSpaceType", "name", 0, true, null, localArrayList);
    addElement("NumChannels", "Chroma", 0);
    addAttribute("NumChannels", "value", 2, true, 0, Integer.MAX_VALUE);
    addElement("Gamma", "Chroma", 0);
    addAttribute("Gamma", "value", 3, true, null);
    addElement("BlackIsZero", "Chroma", 0);
    addBooleanAttribute("BlackIsZero", "value", true, true);
    addElement("Palette", "Chroma", 0, Integer.MAX_VALUE);
    addElement("PaletteEntry", "Palette", 0);
    addAttribute("PaletteEntry", "index", 2, true, null);
    addAttribute("PaletteEntry", "red", 2, true, null);
    addAttribute("PaletteEntry", "green", 2, true, null);
    addAttribute("PaletteEntry", "blue", 2, true, null);
    addAttribute("PaletteEntry", "alpha", 2, false, "255");
    addElement("BackgroundIndex", "Chroma", 0);
    addAttribute("BackgroundIndex", "value", 2, true, null);
    addElement("BackgroundColor", "Chroma", 0);
    addAttribute("BackgroundColor", "red", 2, true, null);
    addAttribute("BackgroundColor", "green", 2, true, null);
    addAttribute("BackgroundColor", "blue", 2, true, null);
    addElement("Compression", "javax_imageio_1.0", 2);
    addSingleAttributeElement("CompressionTypeName", "Compression", 0);
    addElement("Lossless", "Compression", 0);
    addBooleanAttribute("Lossless", "value", true, true);
    addSingleAttributeElement("NumProgressiveScans", "Compression", 2);
    addSingleAttributeElement("BitRate", "Compression", 3);
    addElement("Data", "javax_imageio_1.0", 2);
    addElement("PlanarConfiguration", "Data", 0);
    localArrayList = new ArrayList();
    localArrayList.add("PixelInterleaved");
    localArrayList.add("PlaneInterleaved");
    localArrayList.add("LineInterleaved");
    localArrayList.add("TileInterleaved");
    addAttribute("PlanarConfiguration", "value", 0, true, null, localArrayList);
    addElement("SampleFormat", "Data", 0);
    localArrayList = new ArrayList();
    localArrayList.add("SignedIntegral");
    localArrayList.add("UnsignedIntegral");
    localArrayList.add("Real");
    localArrayList.add("Index");
    addAttribute("SampleFormat", "value", 0, true, null, localArrayList);
    addElement("BitsPerSample", "Data", 0);
    addAttribute("BitsPerSample", "value", 2, true, 1, Integer.MAX_VALUE);
    addElement("SignificantBitsPerSample", "Data", 0);
    addAttribute("SignificantBitsPerSample", "value", 2, true, 1, Integer.MAX_VALUE);
    addElement("SampleMSB", "Data", 0);
    addAttribute("SampleMSB", "value", 2, true, 1, Integer.MAX_VALUE);
    addElement("Dimension", "javax_imageio_1.0", 2);
    addSingleAttributeElement("PixelAspectRatio", "Dimension", 3);
    addElement("ImageOrientation", "Dimension", 0);
    localArrayList = new ArrayList();
    localArrayList.add("Normal");
    localArrayList.add("Rotate90");
    localArrayList.add("Rotate180");
    localArrayList.add("Rotate270");
    localArrayList.add("FlipH");
    localArrayList.add("FlipV");
    localArrayList.add("FlipHRotate90");
    localArrayList.add("FlipVRotate90");
    addAttribute("ImageOrientation", "value", 0, true, null, localArrayList);
    addSingleAttributeElement("HorizontalPixelSize", "Dimension", 3);
    addSingleAttributeElement("VerticalPixelSize", "Dimension", 3);
    addSingleAttributeElement("HorizontalPhysicalPixelSpacing", "Dimension", 3);
    addSingleAttributeElement("VerticalPhysicalPixelSpacing", "Dimension", 3);
    addSingleAttributeElement("HorizontalPosition", "Dimension", 3);
    addSingleAttributeElement("VerticalPosition", "Dimension", 3);
    addSingleAttributeElement("HorizontalPixelOffset", "Dimension", 2);
    addSingleAttributeElement("VerticalPixelOffset", "Dimension", 2);
    addSingleAttributeElement("HorizontalScreenSize", "Dimension", 2);
    addSingleAttributeElement("VerticalScreenSize", "Dimension", 2);
    addElement("Document", "javax_imageio_1.0", 2);
    addElement("FormatVersion", "Document", 0);
    addAttribute("FormatVersion", "value", 0, true, null);
    addElement("SubimageInterpretation", "Document", 0);
    localArrayList = new ArrayList();
    localArrayList.add("Standalone");
    localArrayList.add("SinglePage");
    localArrayList.add("FullResolution");
    localArrayList.add("ReducedResolution");
    localArrayList.add("PyramidLayer");
    localArrayList.add("Preview");
    localArrayList.add("VolumeSlice");
    localArrayList.add("ObjectView");
    localArrayList.add("Panorama");
    localArrayList.add("AnimationFrame");
    localArrayList.add("TransparencyMask");
    localArrayList.add("CompositingLayer");
    localArrayList.add("SpectralSlice");
    localArrayList.add("Unknown");
    addAttribute("SubimageInterpretation", "value", 0, true, null, localArrayList);
    addElement("ImageCreationTime", "Document", 0);
    addAttribute("ImageCreationTime", "year", 2, true, null);
    addAttribute("ImageCreationTime", "month", 2, true, null, "1", "12", true, true);
    addAttribute("ImageCreationTime", "day", 2, true, null, "1", "31", true, true);
    addAttribute("ImageCreationTime", "hour", 2, false, "0", "0", "23", true, true);
    addAttribute("ImageCreationTime", "minute", 2, false, "0", "0", "59", true, true);
    addAttribute("ImageCreationTime", "second", 2, false, "0", "0", "60", true, true);
    addElement("ImageModificationTime", "Document", 0);
    addAttribute("ImageModificationTime", "year", 2, true, null);
    addAttribute("ImageModificationTime", "month", 2, true, null, "1", "12", true, true);
    addAttribute("ImageModificationTime", "day", 2, true, null, "1", "31", true, true);
    addAttribute("ImageModificationTime", "hour", 2, false, "0", "0", "23", true, true);
    addAttribute("ImageModificationTime", "minute", 2, false, "0", "0", "59", true, true);
    addAttribute("ImageModificationTime", "second", 2, false, "0", "0", "60", true, true);
    addElement("Text", "javax_imageio_1.0", 0, Integer.MAX_VALUE);
    addElement("TextEntry", "Text", 0);
    addAttribute("TextEntry", "keyword", 0, false, null);
    addAttribute("TextEntry", "value", 0, true, null);
    addAttribute("TextEntry", "language", 0, false, null);
    addAttribute("TextEntry", "encoding", 0, false, null);
    localArrayList = new ArrayList();
    localArrayList.add("none");
    localArrayList.add("lzw");
    localArrayList.add("zip");
    localArrayList.add("bzip");
    localArrayList.add("other");
    addAttribute("TextEntry", "compression", 0, false, "none", localArrayList);
    addElement("Transparency", "javax_imageio_1.0", 2);
    addElement("Alpha", "Transparency", 0);
    localArrayList = new ArrayList();
    localArrayList.add("none");
    localArrayList.add("premultiplied");
    localArrayList.add("nonpremultiplied");
    addAttribute("Alpha", "value", 0, false, "none", localArrayList);
    addSingleAttributeElement("TransparentIndex", "Transparency", 2);
    addElement("TransparentColor", "Transparency", 0);
    addAttribute("TransparentColor", "value", 2, true, 0, Integer.MAX_VALUE);
    addElement("TileTransparencies", "Transparency", 0, Integer.MAX_VALUE);
    addElement("TransparentTile", "TileTransparencies", 0);
    addAttribute("TransparentTile", "x", 2, true, null);
    addAttribute("TransparentTile", "y", 2, true, null);
    addElement("TileOpacities", "Transparency", 0, Integer.MAX_VALUE);
    addElement("OpaqueTile", "TileOpacities", 0);
    addAttribute("OpaqueTile", "x", 2, true, null);
    addAttribute("OpaqueTile", "y", 2, true, null);
  }
  
  public boolean canNodeAppear(String paramString, ImageTypeSpecifier paramImageTypeSpecifier)
  {
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\plugins\common\StandardMetadataFormat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */