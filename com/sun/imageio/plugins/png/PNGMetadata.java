package com.sun.imageio.plugins.png;

import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.SampleModel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class PNGMetadata
  extends IIOMetadata
  implements Cloneable
{
  public static final String nativeMetadataFormatName = "javax_imageio_png_1.0";
  protected static final String nativeMetadataFormatClassName = "com.sun.imageio.plugins.png.PNGMetadataFormat";
  static final String[] IHDR_colorTypeNames = { "Grayscale", null, "RGB", "Palette", "GrayAlpha", null, "RGBAlpha" };
  static final int[] IHDR_numChannels = { 1, 0, 3, 3, 2, 0, 4 };
  static final String[] IHDR_bitDepths = { "1", "2", "4", "8", "16" };
  static final String[] IHDR_compressionMethodNames = { "deflate" };
  static final String[] IHDR_filterMethodNames = { "adaptive" };
  static final String[] IHDR_interlaceMethodNames = { "none", "adam7" };
  static final String[] iCCP_compressionMethodNames = { "deflate" };
  static final String[] zTXt_compressionMethodNames = { "deflate" };
  public static final int PHYS_UNIT_UNKNOWN = 0;
  public static final int PHYS_UNIT_METER = 1;
  static final String[] unitSpecifierNames = { "unknown", "meter" };
  static final String[] renderingIntentNames = { "Perceptual", "Relative colorimetric", "Saturation", "Absolute colorimetric" };
  static final String[] colorSpaceTypeNames = { "GRAY", null, "RGB", "RGB", "GRAY", null, "RGB" };
  public boolean IHDR_present;
  public int IHDR_width;
  public int IHDR_height;
  public int IHDR_bitDepth;
  public int IHDR_colorType;
  public int IHDR_compressionMethod;
  public int IHDR_filterMethod;
  public int IHDR_interlaceMethod;
  public boolean PLTE_present;
  public byte[] PLTE_red;
  public byte[] PLTE_green;
  public byte[] PLTE_blue;
  public int[] PLTE_order = null;
  public boolean bKGD_present;
  public int bKGD_colorType;
  public int bKGD_index;
  public int bKGD_gray;
  public int bKGD_red;
  public int bKGD_green;
  public int bKGD_blue;
  public boolean cHRM_present;
  public int cHRM_whitePointX;
  public int cHRM_whitePointY;
  public int cHRM_redX;
  public int cHRM_redY;
  public int cHRM_greenX;
  public int cHRM_greenY;
  public int cHRM_blueX;
  public int cHRM_blueY;
  public boolean gAMA_present;
  public int gAMA_gamma;
  public boolean hIST_present;
  public char[] hIST_histogram;
  public boolean iCCP_present;
  public String iCCP_profileName;
  public int iCCP_compressionMethod;
  public byte[] iCCP_compressedProfile;
  public ArrayList<String> iTXt_keyword = new ArrayList();
  public ArrayList<Boolean> iTXt_compressionFlag = new ArrayList();
  public ArrayList<Integer> iTXt_compressionMethod = new ArrayList();
  public ArrayList<String> iTXt_languageTag = new ArrayList();
  public ArrayList<String> iTXt_translatedKeyword = new ArrayList();
  public ArrayList<String> iTXt_text = new ArrayList();
  public boolean pHYs_present;
  public int pHYs_pixelsPerUnitXAxis;
  public int pHYs_pixelsPerUnitYAxis;
  public int pHYs_unitSpecifier;
  public boolean sBIT_present;
  public int sBIT_colorType;
  public int sBIT_grayBits;
  public int sBIT_redBits;
  public int sBIT_greenBits;
  public int sBIT_blueBits;
  public int sBIT_alphaBits;
  public boolean sPLT_present;
  public String sPLT_paletteName;
  public int sPLT_sampleDepth;
  public int[] sPLT_red;
  public int[] sPLT_green;
  public int[] sPLT_blue;
  public int[] sPLT_alpha;
  public int[] sPLT_frequency;
  public boolean sRGB_present;
  public int sRGB_renderingIntent;
  public ArrayList<String> tEXt_keyword = new ArrayList();
  public ArrayList<String> tEXt_text = new ArrayList();
  public boolean tIME_present;
  public int tIME_year;
  public int tIME_month;
  public int tIME_day;
  public int tIME_hour;
  public int tIME_minute;
  public int tIME_second;
  public boolean tRNS_present;
  public int tRNS_colorType;
  public byte[] tRNS_alpha;
  public int tRNS_gray;
  public int tRNS_red;
  public int tRNS_green;
  public int tRNS_blue;
  public ArrayList<String> zTXt_keyword = new ArrayList();
  public ArrayList<Integer> zTXt_compressionMethod = new ArrayList();
  public ArrayList<String> zTXt_text = new ArrayList();
  public ArrayList<String> unknownChunkType = new ArrayList();
  public ArrayList<byte[]> unknownChunkData = new ArrayList();
  
  public PNGMetadata()
  {
    super(true, "javax_imageio_png_1.0", "com.sun.imageio.plugins.png.PNGMetadataFormat", null, null);
  }
  
  public PNGMetadata(IIOMetadata paramIIOMetadata) {}
  
  public void initialize(ImageTypeSpecifier paramImageTypeSpecifier, int paramInt)
  {
    ColorModel localColorModel = paramImageTypeSpecifier.getColorModel();
    SampleModel localSampleModel = paramImageTypeSpecifier.getSampleModel();
    int[] arrayOfInt = localSampleModel.getSampleSize();
    int i = arrayOfInt[0];
    for (int j = 1; j < arrayOfInt.length; j++) {
      if (arrayOfInt[j] > i) {
        i = arrayOfInt[j];
      }
    }
    if ((arrayOfInt.length > 1) && (i < 8)) {
      i = 8;
    }
    if ((i > 2) && (i < 4)) {
      i = 4;
    } else if ((i > 4) && (i < 8)) {
      i = 8;
    } else if ((i > 8) && (i < 16)) {
      i = 16;
    } else if (i > 16) {
      throw new RuntimeException("bitDepth > 16!");
    }
    IHDR_bitDepth = i;
    if ((localColorModel instanceof IndexColorModel))
    {
      IndexColorModel localIndexColorModel = (IndexColorModel)localColorModel;
      int k = localIndexColorModel.getMapSize();
      byte[] arrayOfByte1 = new byte[k];
      localIndexColorModel.getReds(arrayOfByte1);
      byte[] arrayOfByte2 = new byte[k];
      localIndexColorModel.getGreens(arrayOfByte2);
      byte[] arrayOfByte3 = new byte[k];
      localIndexColorModel.getBlues(arrayOfByte3);
      int m = 0;
      if ((!IHDR_present) || (IHDR_colorType != 3))
      {
        m = 1;
        int n = 255 / ((1 << IHDR_bitDepth) - 1);
        for (int i1 = 0; i1 < k; i1++)
        {
          int i2 = arrayOfByte1[i1];
          if ((i2 != (byte)(i1 * n)) || (i2 != arrayOfByte2[i1]) || (i2 != arrayOfByte3[i1]))
          {
            m = 0;
            break;
          }
        }
      }
      boolean bool = localColorModel.hasAlpha();
      byte[] arrayOfByte4 = null;
      if (bool)
      {
        arrayOfByte4 = new byte[k];
        localIndexColorModel.getAlphas(arrayOfByte4);
      }
      if ((m != 0) && (bool) && ((i == 8) || (i == 16)))
      {
        IHDR_colorType = 4;
      }
      else if ((m != 0) && (!bool))
      {
        IHDR_colorType = 0;
      }
      else
      {
        IHDR_colorType = 3;
        PLTE_present = true;
        PLTE_order = null;
        PLTE_red = ((byte[])arrayOfByte1.clone());
        PLTE_green = ((byte[])arrayOfByte2.clone());
        PLTE_blue = ((byte[])arrayOfByte3.clone());
        if (bool)
        {
          tRNS_present = true;
          tRNS_colorType = 3;
          PLTE_order = new int[arrayOfByte4.length];
          byte[] arrayOfByte5 = new byte[arrayOfByte4.length];
          int i3 = 0;
          for (int i4 = 0; i4 < arrayOfByte4.length; i4++) {
            if (arrayOfByte4[i4] != -1)
            {
              PLTE_order[i4] = i3;
              arrayOfByte5[i3] = arrayOfByte4[i4];
              i3++;
            }
          }
          i4 = i3;
          for (int i5 = 0; i5 < arrayOfByte4.length; i5++) {
            if (arrayOfByte4[i5] == -1) {
              PLTE_order[i5] = (i3++);
            }
          }
          byte[] arrayOfByte6 = PLTE_red;
          byte[] arrayOfByte7 = PLTE_green;
          byte[] arrayOfByte8 = PLTE_blue;
          int i6 = arrayOfByte6.length;
          PLTE_red = new byte[i6];
          PLTE_green = new byte[i6];
          PLTE_blue = new byte[i6];
          for (int i7 = 0; i7 < i6; i7++)
          {
            PLTE_red[PLTE_order[i7]] = arrayOfByte6[i7];
            PLTE_green[PLTE_order[i7]] = arrayOfByte7[i7];
            PLTE_blue[PLTE_order[i7]] = arrayOfByte8[i7];
          }
          tRNS_alpha = new byte[i4];
          System.arraycopy(arrayOfByte5, 0, tRNS_alpha, 0, i4);
        }
      }
    }
    else if (paramInt == 1)
    {
      IHDR_colorType = 0;
    }
    else if (paramInt == 2)
    {
      IHDR_colorType = 4;
    }
    else if (paramInt == 3)
    {
      IHDR_colorType = 2;
    }
    else if (paramInt == 4)
    {
      IHDR_colorType = 6;
    }
    else
    {
      throw new RuntimeException("Number of bands not 1-4!");
    }
    IHDR_present = true;
  }
  
  public boolean isReadOnly()
  {
    return false;
  }
  
  private ArrayList<byte[]> cloneBytesArrayList(ArrayList<byte[]> paramArrayList)
  {
    if (paramArrayList == null) {
      return null;
    }
    ArrayList localArrayList = new ArrayList(paramArrayList.size());
    Iterator localIterator = paramArrayList.iterator();
    while (localIterator.hasNext())
    {
      byte[] arrayOfByte = (byte[])localIterator.next();
      localArrayList.add(arrayOfByte == null ? null : (byte[])arrayOfByte.clone());
    }
    return localArrayList;
  }
  
  public Object clone()
  {
    PNGMetadata localPNGMetadata;
    try
    {
      localPNGMetadata = (PNGMetadata)super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      return null;
    }
    unknownChunkData = cloneBytesArrayList(unknownChunkData);
    return localPNGMetadata;
  }
  
  public Node getAsTree(String paramString)
  {
    if (paramString.equals("javax_imageio_png_1.0")) {
      return getNativeTree();
    }
    if (paramString.equals("javax_imageio_1.0")) {
      return getStandardTree();
    }
    throw new IllegalArgumentException("Not a recognized format!");
  }
  
  private Node getNativeTree()
  {
    IIOMetadataNode localIIOMetadataNode1 = null;
    IIOMetadataNode localIIOMetadataNode2 = new IIOMetadataNode("javax_imageio_png_1.0");
    IIOMetadataNode localIIOMetadataNode3;
    if (IHDR_present)
    {
      localIIOMetadataNode3 = new IIOMetadataNode("IHDR");
      localIIOMetadataNode3.setAttribute("width", Integer.toString(IHDR_width));
      localIIOMetadataNode3.setAttribute("height", Integer.toString(IHDR_height));
      localIIOMetadataNode3.setAttribute("bitDepth", Integer.toString(IHDR_bitDepth));
      localIIOMetadataNode3.setAttribute("colorType", IHDR_colorTypeNames[IHDR_colorType]);
      localIIOMetadataNode3.setAttribute("compressionMethod", IHDR_compressionMethodNames[IHDR_compressionMethod]);
      localIIOMetadataNode3.setAttribute("filterMethod", IHDR_filterMethodNames[IHDR_filterMethod]);
      localIIOMetadataNode3.setAttribute("interlaceMethod", IHDR_interlaceMethodNames[IHDR_interlaceMethod]);
      localIIOMetadataNode2.appendChild(localIIOMetadataNode3);
    }
    int i;
    IIOMetadataNode localIIOMetadataNode6;
    if (PLTE_present)
    {
      localIIOMetadataNode3 = new IIOMetadataNode("PLTE");
      i = PLTE_red.length;
      for (int k = 0; k < i; k++)
      {
        localIIOMetadataNode6 = new IIOMetadataNode("PLTEEntry");
        localIIOMetadataNode6.setAttribute("index", Integer.toString(k));
        localIIOMetadataNode6.setAttribute("red", Integer.toString(PLTE_red[k] & 0xFF));
        localIIOMetadataNode6.setAttribute("green", Integer.toString(PLTE_green[k] & 0xFF));
        localIIOMetadataNode6.setAttribute("blue", Integer.toString(PLTE_blue[k] & 0xFF));
        localIIOMetadataNode3.appendChild(localIIOMetadataNode6);
      }
      localIIOMetadataNode2.appendChild(localIIOMetadataNode3);
    }
    if (bKGD_present)
    {
      localIIOMetadataNode3 = new IIOMetadataNode("bKGD");
      if (bKGD_colorType == 3)
      {
        localIIOMetadataNode1 = new IIOMetadataNode("bKGD_Palette");
        localIIOMetadataNode1.setAttribute("index", Integer.toString(bKGD_index));
      }
      else if (bKGD_colorType == 0)
      {
        localIIOMetadataNode1 = new IIOMetadataNode("bKGD_Grayscale");
        localIIOMetadataNode1.setAttribute("gray", Integer.toString(bKGD_gray));
      }
      else if (bKGD_colorType == 2)
      {
        localIIOMetadataNode1 = new IIOMetadataNode("bKGD_RGB");
        localIIOMetadataNode1.setAttribute("red", Integer.toString(bKGD_red));
        localIIOMetadataNode1.setAttribute("green", Integer.toString(bKGD_green));
        localIIOMetadataNode1.setAttribute("blue", Integer.toString(bKGD_blue));
      }
      localIIOMetadataNode3.appendChild(localIIOMetadataNode1);
      localIIOMetadataNode2.appendChild(localIIOMetadataNode3);
    }
    if (cHRM_present)
    {
      localIIOMetadataNode3 = new IIOMetadataNode("cHRM");
      localIIOMetadataNode3.setAttribute("whitePointX", Integer.toString(cHRM_whitePointX));
      localIIOMetadataNode3.setAttribute("whitePointY", Integer.toString(cHRM_whitePointY));
      localIIOMetadataNode3.setAttribute("redX", Integer.toString(cHRM_redX));
      localIIOMetadataNode3.setAttribute("redY", Integer.toString(cHRM_redY));
      localIIOMetadataNode3.setAttribute("greenX", Integer.toString(cHRM_greenX));
      localIIOMetadataNode3.setAttribute("greenY", Integer.toString(cHRM_greenY));
      localIIOMetadataNode3.setAttribute("blueX", Integer.toString(cHRM_blueX));
      localIIOMetadataNode3.setAttribute("blueY", Integer.toString(cHRM_blueY));
      localIIOMetadataNode2.appendChild(localIIOMetadataNode3);
    }
    if (gAMA_present)
    {
      localIIOMetadataNode3 = new IIOMetadataNode("gAMA");
      localIIOMetadataNode3.setAttribute("value", Integer.toString(gAMA_gamma));
      localIIOMetadataNode2.appendChild(localIIOMetadataNode3);
    }
    IIOMetadataNode localIIOMetadataNode4;
    if (hIST_present)
    {
      localIIOMetadataNode3 = new IIOMetadataNode("hIST");
      for (i = 0; i < hIST_histogram.length; i++)
      {
        localIIOMetadataNode4 = new IIOMetadataNode("hISTEntry");
        localIIOMetadataNode4.setAttribute("index", Integer.toString(i));
        localIIOMetadataNode4.setAttribute("value", Integer.toString(hIST_histogram[i]));
        localIIOMetadataNode3.appendChild(localIIOMetadataNode4);
      }
      localIIOMetadataNode2.appendChild(localIIOMetadataNode3);
    }
    if (iCCP_present)
    {
      localIIOMetadataNode3 = new IIOMetadataNode("iCCP");
      localIIOMetadataNode3.setAttribute("profileName", iCCP_profileName);
      localIIOMetadataNode3.setAttribute("compressionMethod", iCCP_compressionMethodNames[iCCP_compressionMethod]);
      Object localObject = iCCP_compressedProfile;
      if (localObject != null) {
        localObject = ((byte[])localObject).clone();
      }
      localIIOMetadataNode3.setUserObject(localObject);
      localIIOMetadataNode2.appendChild(localIIOMetadataNode3);
    }
    int j;
    if (iTXt_keyword.size() > 0)
    {
      localIIOMetadataNode3 = new IIOMetadataNode("iTXt");
      for (j = 0; j < iTXt_keyword.size(); j++)
      {
        localIIOMetadataNode4 = new IIOMetadataNode("iTXtEntry");
        localIIOMetadataNode4.setAttribute("keyword", (String)iTXt_keyword.get(j));
        localIIOMetadataNode4.setAttribute("compressionFlag", ((Boolean)iTXt_compressionFlag.get(j)).booleanValue() ? "TRUE" : "FALSE");
        localIIOMetadataNode4.setAttribute("compressionMethod", ((Integer)iTXt_compressionMethod.get(j)).toString());
        localIIOMetadataNode4.setAttribute("languageTag", (String)iTXt_languageTag.get(j));
        localIIOMetadataNode4.setAttribute("translatedKeyword", (String)iTXt_translatedKeyword.get(j));
        localIIOMetadataNode4.setAttribute("text", (String)iTXt_text.get(j));
        localIIOMetadataNode3.appendChild(localIIOMetadataNode4);
      }
      localIIOMetadataNode2.appendChild(localIIOMetadataNode3);
    }
    if (pHYs_present)
    {
      localIIOMetadataNode3 = new IIOMetadataNode("pHYs");
      localIIOMetadataNode3.setAttribute("pixelsPerUnitXAxis", Integer.toString(pHYs_pixelsPerUnitXAxis));
      localIIOMetadataNode3.setAttribute("pixelsPerUnitYAxis", Integer.toString(pHYs_pixelsPerUnitYAxis));
      localIIOMetadataNode3.setAttribute("unitSpecifier", unitSpecifierNames[pHYs_unitSpecifier]);
      localIIOMetadataNode2.appendChild(localIIOMetadataNode3);
    }
    if (sBIT_present)
    {
      localIIOMetadataNode3 = new IIOMetadataNode("sBIT");
      if (sBIT_colorType == 0)
      {
        localIIOMetadataNode1 = new IIOMetadataNode("sBIT_Grayscale");
        localIIOMetadataNode1.setAttribute("gray", Integer.toString(sBIT_grayBits));
      }
      else if (sBIT_colorType == 4)
      {
        localIIOMetadataNode1 = new IIOMetadataNode("sBIT_GrayAlpha");
        localIIOMetadataNode1.setAttribute("gray", Integer.toString(sBIT_grayBits));
        localIIOMetadataNode1.setAttribute("alpha", Integer.toString(sBIT_alphaBits));
      }
      else if (sBIT_colorType == 2)
      {
        localIIOMetadataNode1 = new IIOMetadataNode("sBIT_RGB");
        localIIOMetadataNode1.setAttribute("red", Integer.toString(sBIT_redBits));
        localIIOMetadataNode1.setAttribute("green", Integer.toString(sBIT_greenBits));
        localIIOMetadataNode1.setAttribute("blue", Integer.toString(sBIT_blueBits));
      }
      else if (sBIT_colorType == 6)
      {
        localIIOMetadataNode1 = new IIOMetadataNode("sBIT_RGBAlpha");
        localIIOMetadataNode1.setAttribute("red", Integer.toString(sBIT_redBits));
        localIIOMetadataNode1.setAttribute("green", Integer.toString(sBIT_greenBits));
        localIIOMetadataNode1.setAttribute("blue", Integer.toString(sBIT_blueBits));
        localIIOMetadataNode1.setAttribute("alpha", Integer.toString(sBIT_alphaBits));
      }
      else if (sBIT_colorType == 3)
      {
        localIIOMetadataNode1 = new IIOMetadataNode("sBIT_Palette");
        localIIOMetadataNode1.setAttribute("red", Integer.toString(sBIT_redBits));
        localIIOMetadataNode1.setAttribute("green", Integer.toString(sBIT_greenBits));
        localIIOMetadataNode1.setAttribute("blue", Integer.toString(sBIT_blueBits));
      }
      localIIOMetadataNode3.appendChild(localIIOMetadataNode1);
      localIIOMetadataNode2.appendChild(localIIOMetadataNode3);
    }
    if (sPLT_present)
    {
      localIIOMetadataNode3 = new IIOMetadataNode("sPLT");
      localIIOMetadataNode3.setAttribute("name", sPLT_paletteName);
      localIIOMetadataNode3.setAttribute("sampleDepth", Integer.toString(sPLT_sampleDepth));
      j = sPLT_red.length;
      for (int m = 0; m < j; m++)
      {
        localIIOMetadataNode6 = new IIOMetadataNode("sPLTEntry");
        localIIOMetadataNode6.setAttribute("index", Integer.toString(m));
        localIIOMetadataNode6.setAttribute("red", Integer.toString(sPLT_red[m]));
        localIIOMetadataNode6.setAttribute("green", Integer.toString(sPLT_green[m]));
        localIIOMetadataNode6.setAttribute("blue", Integer.toString(sPLT_blue[m]));
        localIIOMetadataNode6.setAttribute("alpha", Integer.toString(sPLT_alpha[m]));
        localIIOMetadataNode6.setAttribute("frequency", Integer.toString(sPLT_frequency[m]));
        localIIOMetadataNode3.appendChild(localIIOMetadataNode6);
      }
      localIIOMetadataNode2.appendChild(localIIOMetadataNode3);
    }
    if (sRGB_present)
    {
      localIIOMetadataNode3 = new IIOMetadataNode("sRGB");
      localIIOMetadataNode3.setAttribute("renderingIntent", renderingIntentNames[sRGB_renderingIntent]);
      localIIOMetadataNode2.appendChild(localIIOMetadataNode3);
    }
    IIOMetadataNode localIIOMetadataNode5;
    if (tEXt_keyword.size() > 0)
    {
      localIIOMetadataNode3 = new IIOMetadataNode("tEXt");
      for (j = 0; j < tEXt_keyword.size(); j++)
      {
        localIIOMetadataNode5 = new IIOMetadataNode("tEXtEntry");
        localIIOMetadataNode5.setAttribute("keyword", (String)tEXt_keyword.get(j));
        localIIOMetadataNode5.setAttribute("value", (String)tEXt_text.get(j));
        localIIOMetadataNode3.appendChild(localIIOMetadataNode5);
      }
      localIIOMetadataNode2.appendChild(localIIOMetadataNode3);
    }
    if (tIME_present)
    {
      localIIOMetadataNode3 = new IIOMetadataNode("tIME");
      localIIOMetadataNode3.setAttribute("year", Integer.toString(tIME_year));
      localIIOMetadataNode3.setAttribute("month", Integer.toString(tIME_month));
      localIIOMetadataNode3.setAttribute("day", Integer.toString(tIME_day));
      localIIOMetadataNode3.setAttribute("hour", Integer.toString(tIME_hour));
      localIIOMetadataNode3.setAttribute("minute", Integer.toString(tIME_minute));
      localIIOMetadataNode3.setAttribute("second", Integer.toString(tIME_second));
      localIIOMetadataNode2.appendChild(localIIOMetadataNode3);
    }
    if (tRNS_present)
    {
      localIIOMetadataNode3 = new IIOMetadataNode("tRNS");
      if (tRNS_colorType == 3)
      {
        localIIOMetadataNode1 = new IIOMetadataNode("tRNS_Palette");
        for (j = 0; j < tRNS_alpha.length; j++)
        {
          localIIOMetadataNode5 = new IIOMetadataNode("tRNS_PaletteEntry");
          localIIOMetadataNode5.setAttribute("index", Integer.toString(j));
          localIIOMetadataNode5.setAttribute("alpha", Integer.toString(tRNS_alpha[j] & 0xFF));
          localIIOMetadataNode1.appendChild(localIIOMetadataNode5);
        }
      }
      else if (tRNS_colorType == 0)
      {
        localIIOMetadataNode1 = new IIOMetadataNode("tRNS_Grayscale");
        localIIOMetadataNode1.setAttribute("gray", Integer.toString(tRNS_gray));
      }
      else if (tRNS_colorType == 2)
      {
        localIIOMetadataNode1 = new IIOMetadataNode("tRNS_RGB");
        localIIOMetadataNode1.setAttribute("red", Integer.toString(tRNS_red));
        localIIOMetadataNode1.setAttribute("green", Integer.toString(tRNS_green));
        localIIOMetadataNode1.setAttribute("blue", Integer.toString(tRNS_blue));
      }
      localIIOMetadataNode3.appendChild(localIIOMetadataNode1);
      localIIOMetadataNode2.appendChild(localIIOMetadataNode3);
    }
    if (zTXt_keyword.size() > 0)
    {
      localIIOMetadataNode3 = new IIOMetadataNode("zTXt");
      for (j = 0; j < zTXt_keyword.size(); j++)
      {
        localIIOMetadataNode5 = new IIOMetadataNode("zTXtEntry");
        localIIOMetadataNode5.setAttribute("keyword", (String)zTXt_keyword.get(j));
        int n = ((Integer)zTXt_compressionMethod.get(j)).intValue();
        localIIOMetadataNode5.setAttribute("compressionMethod", zTXt_compressionMethodNames[n]);
        localIIOMetadataNode5.setAttribute("text", (String)zTXt_text.get(j));
        localIIOMetadataNode3.appendChild(localIIOMetadataNode5);
      }
      localIIOMetadataNode2.appendChild(localIIOMetadataNode3);
    }
    if (unknownChunkType.size() > 0)
    {
      localIIOMetadataNode3 = new IIOMetadataNode("UnknownChunks");
      for (j = 0; j < unknownChunkType.size(); j++)
      {
        localIIOMetadataNode5 = new IIOMetadataNode("UnknownChunk");
        localIIOMetadataNode5.setAttribute("type", (String)unknownChunkType.get(j));
        localIIOMetadataNode5.setUserObject((byte[])unknownChunkData.get(j));
        localIIOMetadataNode3.appendChild(localIIOMetadataNode5);
      }
      localIIOMetadataNode2.appendChild(localIIOMetadataNode3);
    }
    return localIIOMetadataNode2;
  }
  
  private int getNumChannels()
  {
    int i = IHDR_numChannels[IHDR_colorType];
    if ((IHDR_colorType == 3) && (tRNS_present) && (tRNS_colorType == IHDR_colorType)) {
      i = 4;
    }
    return i;
  }
  
  public IIOMetadataNode getStandardChromaNode()
  {
    IIOMetadataNode localIIOMetadataNode1 = new IIOMetadataNode("Chroma");
    IIOMetadataNode localIIOMetadataNode2 = null;
    localIIOMetadataNode2 = new IIOMetadataNode("ColorSpaceType");
    localIIOMetadataNode2.setAttribute("name", colorSpaceTypeNames[IHDR_colorType]);
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    localIIOMetadataNode2 = new IIOMetadataNode("NumChannels");
    localIIOMetadataNode2.setAttribute("value", Integer.toString(getNumChannels()));
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    if (gAMA_present)
    {
      localIIOMetadataNode2 = new IIOMetadataNode("Gamma");
      localIIOMetadataNode2.setAttribute("value", Float.toString(gAMA_gamma * 1.0E-5F));
      localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    }
    localIIOMetadataNode2 = new IIOMetadataNode("BlackIsZero");
    localIIOMetadataNode2.setAttribute("value", "TRUE");
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    int i;
    int j;
    if (PLTE_present)
    {
      i = (tRNS_present) && (tRNS_colorType == 3) ? 1 : 0;
      localIIOMetadataNode2 = new IIOMetadataNode("Palette");
      for (j = 0; j < PLTE_red.length; j++)
      {
        IIOMetadataNode localIIOMetadataNode3 = new IIOMetadataNode("PaletteEntry");
        localIIOMetadataNode3.setAttribute("index", Integer.toString(j));
        localIIOMetadataNode3.setAttribute("red", Integer.toString(PLTE_red[j] & 0xFF));
        localIIOMetadataNode3.setAttribute("green", Integer.toString(PLTE_green[j] & 0xFF));
        localIIOMetadataNode3.setAttribute("blue", Integer.toString(PLTE_blue[j] & 0xFF));
        if (i != 0)
        {
          int m = j < tRNS_alpha.length ? tRNS_alpha[j] & 0xFF : 255;
          localIIOMetadataNode3.setAttribute("alpha", Integer.toString(m));
        }
        localIIOMetadataNode2.appendChild(localIIOMetadataNode3);
      }
      localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    }
    if (bKGD_present)
    {
      if (bKGD_colorType == 3)
      {
        localIIOMetadataNode2 = new IIOMetadataNode("BackgroundIndex");
        localIIOMetadataNode2.setAttribute("value", Integer.toString(bKGD_index));
      }
      else
      {
        localIIOMetadataNode2 = new IIOMetadataNode("BackgroundColor");
        int k;
        if (bKGD_colorType == 0)
        {
          i = j = k = bKGD_gray;
        }
        else
        {
          i = bKGD_red;
          j = bKGD_green;
          k = bKGD_blue;
        }
        localIIOMetadataNode2.setAttribute("red", Integer.toString(i));
        localIIOMetadataNode2.setAttribute("green", Integer.toString(j));
        localIIOMetadataNode2.setAttribute("blue", Integer.toString(k));
      }
      localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    }
    return localIIOMetadataNode1;
  }
  
  public IIOMetadataNode getStandardCompressionNode()
  {
    IIOMetadataNode localIIOMetadataNode1 = new IIOMetadataNode("Compression");
    IIOMetadataNode localIIOMetadataNode2 = null;
    localIIOMetadataNode2 = new IIOMetadataNode("CompressionTypeName");
    localIIOMetadataNode2.setAttribute("value", "deflate");
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    localIIOMetadataNode2 = new IIOMetadataNode("Lossless");
    localIIOMetadataNode2.setAttribute("value", "TRUE");
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    localIIOMetadataNode2 = new IIOMetadataNode("NumProgressiveScans");
    localIIOMetadataNode2.setAttribute("value", IHDR_interlaceMethod == 0 ? "1" : "7");
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    return localIIOMetadataNode1;
  }
  
  private String repeat(String paramString, int paramInt)
  {
    if (paramInt == 1) {
      return paramString;
    }
    StringBuffer localStringBuffer = new StringBuffer((paramString.length() + 1) * paramInt - 1);
    localStringBuffer.append(paramString);
    for (int i = 1; i < paramInt; i++)
    {
      localStringBuffer.append(" ");
      localStringBuffer.append(paramString);
    }
    return localStringBuffer.toString();
  }
  
  public IIOMetadataNode getStandardDataNode()
  {
    IIOMetadataNode localIIOMetadataNode1 = new IIOMetadataNode("Data");
    IIOMetadataNode localIIOMetadataNode2 = null;
    localIIOMetadataNode2 = new IIOMetadataNode("PlanarConfiguration");
    localIIOMetadataNode2.setAttribute("value", "PixelInterleaved");
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    localIIOMetadataNode2 = new IIOMetadataNode("SampleFormat");
    localIIOMetadataNode2.setAttribute("value", IHDR_colorType == 3 ? "Index" : "UnsignedIntegral");
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    String str1 = Integer.toString(IHDR_bitDepth);
    localIIOMetadataNode2 = new IIOMetadataNode("BitsPerSample");
    localIIOMetadataNode2.setAttribute("value", repeat(str1, getNumChannels()));
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    if (sBIT_present)
    {
      localIIOMetadataNode2 = new IIOMetadataNode("SignificantBitsPerSample");
      String str2;
      if ((sBIT_colorType == 0) || (sBIT_colorType == 4)) {
        str2 = Integer.toString(sBIT_grayBits);
      } else {
        str2 = Integer.toString(sBIT_redBits) + " " + Integer.toString(sBIT_greenBits) + " " + Integer.toString(sBIT_blueBits);
      }
      if ((sBIT_colorType == 4) || (sBIT_colorType == 6)) {
        str2 = str2 + " " + Integer.toString(sBIT_alphaBits);
      }
      localIIOMetadataNode2.setAttribute("value", str2);
      localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    }
    return localIIOMetadataNode1;
  }
  
  public IIOMetadataNode getStandardDimensionNode()
  {
    IIOMetadataNode localIIOMetadataNode1 = new IIOMetadataNode("Dimension");
    IIOMetadataNode localIIOMetadataNode2 = null;
    localIIOMetadataNode2 = new IIOMetadataNode("PixelAspectRatio");
    float f = pHYs_present ? pHYs_pixelsPerUnitXAxis / pHYs_pixelsPerUnitYAxis : 1.0F;
    localIIOMetadataNode2.setAttribute("value", Float.toString(f));
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    localIIOMetadataNode2 = new IIOMetadataNode("ImageOrientation");
    localIIOMetadataNode2.setAttribute("value", "Normal");
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    if ((pHYs_present) && (pHYs_unitSpecifier == 1))
    {
      localIIOMetadataNode2 = new IIOMetadataNode("HorizontalPixelSize");
      localIIOMetadataNode2.setAttribute("value", Float.toString(1000.0F / pHYs_pixelsPerUnitXAxis));
      localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
      localIIOMetadataNode2 = new IIOMetadataNode("VerticalPixelSize");
      localIIOMetadataNode2.setAttribute("value", Float.toString(1000.0F / pHYs_pixelsPerUnitYAxis));
      localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    }
    return localIIOMetadataNode1;
  }
  
  public IIOMetadataNode getStandardDocumentNode()
  {
    if (!tIME_present) {
      return null;
    }
    IIOMetadataNode localIIOMetadataNode1 = new IIOMetadataNode("Document");
    IIOMetadataNode localIIOMetadataNode2 = null;
    localIIOMetadataNode2 = new IIOMetadataNode("ImageModificationTime");
    localIIOMetadataNode2.setAttribute("year", Integer.toString(tIME_year));
    localIIOMetadataNode2.setAttribute("month", Integer.toString(tIME_month));
    localIIOMetadataNode2.setAttribute("day", Integer.toString(tIME_day));
    localIIOMetadataNode2.setAttribute("hour", Integer.toString(tIME_hour));
    localIIOMetadataNode2.setAttribute("minute", Integer.toString(tIME_minute));
    localIIOMetadataNode2.setAttribute("second", Integer.toString(tIME_second));
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    return localIIOMetadataNode1;
  }
  
  public IIOMetadataNode getStandardTextNode()
  {
    int i = tEXt_keyword.size() + iTXt_keyword.size() + zTXt_keyword.size();
    if (i == 0) {
      return null;
    }
    IIOMetadataNode localIIOMetadataNode1 = new IIOMetadataNode("Text");
    IIOMetadataNode localIIOMetadataNode2 = null;
    for (int j = 0; j < tEXt_keyword.size(); j++)
    {
      localIIOMetadataNode2 = new IIOMetadataNode("TextEntry");
      localIIOMetadataNode2.setAttribute("keyword", (String)tEXt_keyword.get(j));
      localIIOMetadataNode2.setAttribute("value", (String)tEXt_text.get(j));
      localIIOMetadataNode2.setAttribute("encoding", "ISO-8859-1");
      localIIOMetadataNode2.setAttribute("compression", "none");
      localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    }
    for (j = 0; j < iTXt_keyword.size(); j++)
    {
      localIIOMetadataNode2 = new IIOMetadataNode("TextEntry");
      localIIOMetadataNode2.setAttribute("keyword", (String)iTXt_keyword.get(j));
      localIIOMetadataNode2.setAttribute("value", (String)iTXt_text.get(j));
      localIIOMetadataNode2.setAttribute("language", (String)iTXt_languageTag.get(j));
      if (((Boolean)iTXt_compressionFlag.get(j)).booleanValue()) {
        localIIOMetadataNode2.setAttribute("compression", "zip");
      } else {
        localIIOMetadataNode2.setAttribute("compression", "none");
      }
      localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    }
    for (j = 0; j < zTXt_keyword.size(); j++)
    {
      localIIOMetadataNode2 = new IIOMetadataNode("TextEntry");
      localIIOMetadataNode2.setAttribute("keyword", (String)zTXt_keyword.get(j));
      localIIOMetadataNode2.setAttribute("value", (String)zTXt_text.get(j));
      localIIOMetadataNode2.setAttribute("compression", "zip");
      localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    }
    return localIIOMetadataNode1;
  }
  
  public IIOMetadataNode getStandardTransparencyNode()
  {
    IIOMetadataNode localIIOMetadataNode1 = new IIOMetadataNode("Transparency");
    IIOMetadataNode localIIOMetadataNode2 = null;
    localIIOMetadataNode2 = new IIOMetadataNode("Alpha");
    int i = (IHDR_colorType == 6) || (IHDR_colorType == 4) || ((IHDR_colorType == 3) && (tRNS_present) && (tRNS_colorType == IHDR_colorType) && (tRNS_alpha != null)) ? 1 : 0;
    localIIOMetadataNode2.setAttribute("value", i != 0 ? "nonpremultipled" : "none");
    localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    if (tRNS_present)
    {
      localIIOMetadataNode2 = new IIOMetadataNode("TransparentColor");
      if (tRNS_colorType == 2) {
        localIIOMetadataNode2.setAttribute("value", Integer.toString(tRNS_red) + " " + Integer.toString(tRNS_green) + " " + Integer.toString(tRNS_blue));
      } else if (tRNS_colorType == 0) {
        localIIOMetadataNode2.setAttribute("value", Integer.toString(tRNS_gray));
      }
      localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
    }
    return localIIOMetadataNode1;
  }
  
  private void fatal(Node paramNode, String paramString)
    throws IIOInvalidTreeException
  {
    throw new IIOInvalidTreeException(paramString, paramNode);
  }
  
  private String getStringAttribute(Node paramNode, String paramString1, String paramString2, boolean paramBoolean)
    throws IIOInvalidTreeException
  {
    Node localNode = paramNode.getAttributes().getNamedItem(paramString1);
    if (localNode == null)
    {
      if (!paramBoolean) {
        return paramString2;
      }
      fatal(paramNode, "Required attribute " + paramString1 + " not present!");
    }
    return localNode.getNodeValue();
  }
  
  private int getIntAttribute(Node paramNode, String paramString, int paramInt, boolean paramBoolean)
    throws IIOInvalidTreeException
  {
    String str = getStringAttribute(paramNode, paramString, null, paramBoolean);
    if (str == null) {
      return paramInt;
    }
    return Integer.parseInt(str);
  }
  
  private float getFloatAttribute(Node paramNode, String paramString, float paramFloat, boolean paramBoolean)
    throws IIOInvalidTreeException
  {
    String str = getStringAttribute(paramNode, paramString, null, paramBoolean);
    if (str == null) {
      return paramFloat;
    }
    return Float.parseFloat(str);
  }
  
  private int getIntAttribute(Node paramNode, String paramString)
    throws IIOInvalidTreeException
  {
    return getIntAttribute(paramNode, paramString, -1, true);
  }
  
  private float getFloatAttribute(Node paramNode, String paramString)
    throws IIOInvalidTreeException
  {
    return getFloatAttribute(paramNode, paramString, -1.0F, true);
  }
  
  private boolean getBooleanAttribute(Node paramNode, String paramString, boolean paramBoolean1, boolean paramBoolean2)
    throws IIOInvalidTreeException
  {
    Node localNode = paramNode.getAttributes().getNamedItem(paramString);
    if (localNode == null)
    {
      if (!paramBoolean2) {
        return paramBoolean1;
      }
      fatal(paramNode, "Required attribute " + paramString + " not present!");
    }
    String str = localNode.getNodeValue();
    if ((str.equals("TRUE")) || (str.equals("true"))) {
      return true;
    }
    if ((str.equals("FALSE")) || (str.equals("false"))) {
      return false;
    }
    fatal(paramNode, "Attribute " + paramString + " must be 'TRUE' or 'FALSE'!");
    return false;
  }
  
  private boolean getBooleanAttribute(Node paramNode, String paramString)
    throws IIOInvalidTreeException
  {
    return getBooleanAttribute(paramNode, paramString, false, true);
  }
  
  private int getEnumeratedAttribute(Node paramNode, String paramString, String[] paramArrayOfString, int paramInt, boolean paramBoolean)
    throws IIOInvalidTreeException
  {
    Node localNode = paramNode.getAttributes().getNamedItem(paramString);
    if (localNode == null)
    {
      if (!paramBoolean) {
        return paramInt;
      }
      fatal(paramNode, "Required attribute " + paramString + " not present!");
    }
    String str = localNode.getNodeValue();
    for (int i = 0; i < paramArrayOfString.length; i++) {
      if (str.equals(paramArrayOfString[i])) {
        return i;
      }
    }
    fatal(paramNode, "Illegal value for attribute " + paramString + "!");
    return -1;
  }
  
  private int getEnumeratedAttribute(Node paramNode, String paramString, String[] paramArrayOfString)
    throws IIOInvalidTreeException
  {
    return getEnumeratedAttribute(paramNode, paramString, paramArrayOfString, -1, true);
  }
  
  private String getAttribute(Node paramNode, String paramString1, String paramString2, boolean paramBoolean)
    throws IIOInvalidTreeException
  {
    Node localNode = paramNode.getAttributes().getNamedItem(paramString1);
    if (localNode == null)
    {
      if (!paramBoolean) {
        return paramString2;
      }
      fatal(paramNode, "Required attribute " + paramString1 + " not present!");
    }
    return localNode.getNodeValue();
  }
  
  private String getAttribute(Node paramNode, String paramString)
    throws IIOInvalidTreeException
  {
    return getAttribute(paramNode, paramString, null, true);
  }
  
  public void mergeTree(String paramString, Node paramNode)
    throws IIOInvalidTreeException
  {
    if (paramString.equals("javax_imageio_png_1.0"))
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
  
  private void mergeNativeTree(Node paramNode)
    throws IIOInvalidTreeException
  {
    Node localNode = paramNode;
    if (!localNode.getNodeName().equals("javax_imageio_png_1.0")) {
      fatal(localNode, "Root must be javax_imageio_png_1.0");
    }
    for (localNode = localNode.getFirstChild(); localNode != null; localNode = localNode.getNextSibling())
    {
      String str1 = localNode.getNodeName();
      if (str1.equals("IHDR"))
      {
        IHDR_width = getIntAttribute(localNode, "width");
        IHDR_height = getIntAttribute(localNode, "height");
        IHDR_bitDepth = Integer.valueOf(IHDR_bitDepths[getEnumeratedAttribute(localNode, "bitDepth", IHDR_bitDepths)]).intValue();
        IHDR_colorType = getEnumeratedAttribute(localNode, "colorType", IHDR_colorTypeNames);
        IHDR_compressionMethod = getEnumeratedAttribute(localNode, "compressionMethod", IHDR_compressionMethodNames);
        IHDR_filterMethod = getEnumeratedAttribute(localNode, "filterMethod", IHDR_filterMethodNames);
        IHDR_interlaceMethod = getEnumeratedAttribute(localNode, "interlaceMethod", IHDR_interlaceMethodNames);
        IHDR_present = true;
      }
      else
      {
        Object localObject1;
        Object localObject2;
        Object localObject4;
        int k;
        Object localObject8;
        if (str1.equals("PLTE"))
        {
          localObject1 = new byte['Ā'];
          localObject2 = new byte['Ā'];
          localObject4 = new byte['Ā'];
          k = -1;
          localObject8 = localNode.getFirstChild();
          if (localObject8 == null) {
            fatal(localNode, "Palette has no entries!");
          }
          while (localObject8 != null)
          {
            if (!((Node)localObject8).getNodeName().equals("PLTEEntry")) {
              fatal(localNode, "Only a PLTEEntry may be a child of a PLTE!");
            }
            n = getIntAttribute((Node)localObject8, "index");
            if ((n < 0) || (n > 255)) {
              fatal(localNode, "Bad value for PLTEEntry attribute index!");
            }
            if (n > k) {
              k = n;
            }
            localObject1[n] = ((byte)getIntAttribute((Node)localObject8, "red"));
            localObject2[n] = ((byte)getIntAttribute((Node)localObject8, "green"));
            localObject4[n] = ((byte)getIntAttribute((Node)localObject8, "blue"));
            localObject8 = ((Node)localObject8).getNextSibling();
          }
          int n = k + 1;
          PLTE_red = new byte[n];
          PLTE_green = new byte[n];
          PLTE_blue = new byte[n];
          System.arraycopy(localObject1, 0, PLTE_red, 0, n);
          System.arraycopy(localObject2, 0, PLTE_green, 0, n);
          System.arraycopy(localObject4, 0, PLTE_blue, 0, n);
          PLTE_present = true;
        }
        else if (str1.equals("bKGD"))
        {
          bKGD_present = false;
          localObject1 = localNode.getFirstChild();
          if (localObject1 == null) {
            fatal(localNode, "bKGD node has no children!");
          }
          localObject2 = ((Node)localObject1).getNodeName();
          if (((String)localObject2).equals("bKGD_Palette"))
          {
            bKGD_index = getIntAttribute((Node)localObject1, "index");
            bKGD_colorType = 3;
          }
          else if (((String)localObject2).equals("bKGD_Grayscale"))
          {
            bKGD_gray = getIntAttribute((Node)localObject1, "gray");
            bKGD_colorType = 0;
          }
          else if (((String)localObject2).equals("bKGD_RGB"))
          {
            bKGD_red = getIntAttribute((Node)localObject1, "red");
            bKGD_green = getIntAttribute((Node)localObject1, "green");
            bKGD_blue = getIntAttribute((Node)localObject1, "blue");
            bKGD_colorType = 2;
          }
          else
          {
            fatal(localNode, "Bad child of a bKGD node!");
          }
          if (((Node)localObject1).getNextSibling() != null) {
            fatal(localNode, "bKGD node has more than one child!");
          }
          bKGD_present = true;
        }
        else if (str1.equals("cHRM"))
        {
          cHRM_whitePointX = getIntAttribute(localNode, "whitePointX");
          cHRM_whitePointY = getIntAttribute(localNode, "whitePointY");
          cHRM_redX = getIntAttribute(localNode, "redX");
          cHRM_redY = getIntAttribute(localNode, "redY");
          cHRM_greenX = getIntAttribute(localNode, "greenX");
          cHRM_greenY = getIntAttribute(localNode, "greenY");
          cHRM_blueX = getIntAttribute(localNode, "blueX");
          cHRM_blueY = getIntAttribute(localNode, "blueY");
          cHRM_present = true;
        }
        else if (str1.equals("gAMA"))
        {
          gAMA_gamma = getIntAttribute(localNode, "value");
          gAMA_present = true;
        }
        else if (str1.equals("hIST"))
        {
          localObject1 = new char['Ā'];
          int i = -1;
          localObject4 = localNode.getFirstChild();
          if (localObject4 == null) {
            fatal(localNode, "hIST node has no children!");
          }
          while (localObject4 != null)
          {
            if (!((Node)localObject4).getNodeName().equals("hISTEntry")) {
              fatal(localNode, "Only a hISTEntry may be a child of a hIST!");
            }
            k = getIntAttribute((Node)localObject4, "index");
            if ((k < 0) || (k > 255)) {
              fatal(localNode, "Bad value for histEntry attribute index!");
            }
            if (k > i) {
              i = k;
            }
            localObject1[k] = ((char)getIntAttribute((Node)localObject4, "value"));
            localObject4 = ((Node)localObject4).getNextSibling();
          }
          k = i + 1;
          hIST_histogram = new char[k];
          System.arraycopy(localObject1, 0, hIST_histogram, 0, k);
          hIST_present = true;
        }
        else if (str1.equals("iCCP"))
        {
          iCCP_profileName = getAttribute(localNode, "profileName");
          iCCP_compressionMethod = getEnumeratedAttribute(localNode, "compressionMethod", iCCP_compressionMethodNames);
          localObject1 = ((IIOMetadataNode)localNode).getUserObject();
          if (localObject1 == null) {
            fatal(localNode, "No ICCP profile present in user object!");
          }
          if (!(localObject1 instanceof byte[])) {
            fatal(localNode, "User object not a byte array!");
          }
          iCCP_compressedProfile = ((byte[])((byte[])localObject1).clone());
          iCCP_present = true;
        }
        else
        {
          Object localObject3;
          Object localObject7;
          Object localObject9;
          if (str1.equals("iTXt"))
          {
            for (localObject1 = localNode.getFirstChild(); localObject1 != null; localObject1 = ((Node)localObject1).getNextSibling())
            {
              if (!((Node)localObject1).getNodeName().equals("iTXtEntry")) {
                fatal(localNode, "Only an iTXtEntry may be a child of an iTXt!");
              }
              localObject3 = getAttribute((Node)localObject1, "keyword");
              if (isValidKeyword((String)localObject3))
              {
                iTXt_keyword.add(localObject3);
                boolean bool = getBooleanAttribute((Node)localObject1, "compressionFlag");
                iTXt_compressionFlag.add(Boolean.valueOf(bool));
                localObject7 = getAttribute((Node)localObject1, "compressionMethod");
                iTXt_compressionMethod.add(Integer.valueOf((String)localObject7));
                localObject8 = getAttribute((Node)localObject1, "languageTag");
                iTXt_languageTag.add(localObject8);
                String str3 = getAttribute((Node)localObject1, "translatedKeyword");
                iTXt_translatedKeyword.add(str3);
                localObject9 = getAttribute((Node)localObject1, "text");
                iTXt_text.add(localObject9);
              }
            }
          }
          else if (str1.equals("pHYs"))
          {
            pHYs_pixelsPerUnitXAxis = getIntAttribute(localNode, "pixelsPerUnitXAxis");
            pHYs_pixelsPerUnitYAxis = getIntAttribute(localNode, "pixelsPerUnitYAxis");
            pHYs_unitSpecifier = getEnumeratedAttribute(localNode, "unitSpecifier", unitSpecifierNames);
            pHYs_present = true;
          }
          else if (str1.equals("sBIT"))
          {
            sBIT_present = false;
            localObject1 = localNode.getFirstChild();
            if (localObject1 == null) {
              fatal(localNode, "sBIT node has no children!");
            }
            localObject3 = ((Node)localObject1).getNodeName();
            if (((String)localObject3).equals("sBIT_Grayscale"))
            {
              sBIT_grayBits = getIntAttribute((Node)localObject1, "gray");
              sBIT_colorType = 0;
            }
            else if (((String)localObject3).equals("sBIT_GrayAlpha"))
            {
              sBIT_grayBits = getIntAttribute((Node)localObject1, "gray");
              sBIT_alphaBits = getIntAttribute((Node)localObject1, "alpha");
              sBIT_colorType = 4;
            }
            else if (((String)localObject3).equals("sBIT_RGB"))
            {
              sBIT_redBits = getIntAttribute((Node)localObject1, "red");
              sBIT_greenBits = getIntAttribute((Node)localObject1, "green");
              sBIT_blueBits = getIntAttribute((Node)localObject1, "blue");
              sBIT_colorType = 2;
            }
            else if (((String)localObject3).equals("sBIT_RGBAlpha"))
            {
              sBIT_redBits = getIntAttribute((Node)localObject1, "red");
              sBIT_greenBits = getIntAttribute((Node)localObject1, "green");
              sBIT_blueBits = getIntAttribute((Node)localObject1, "blue");
              sBIT_alphaBits = getIntAttribute((Node)localObject1, "alpha");
              sBIT_colorType = 6;
            }
            else if (((String)localObject3).equals("sBIT_Palette"))
            {
              sBIT_redBits = getIntAttribute((Node)localObject1, "red");
              sBIT_greenBits = getIntAttribute((Node)localObject1, "green");
              sBIT_blueBits = getIntAttribute((Node)localObject1, "blue");
              sBIT_colorType = 3;
            }
            else
            {
              fatal(localNode, "Bad child of an sBIT node!");
            }
            if (((Node)localObject1).getNextSibling() != null) {
              fatal(localNode, "sBIT node has more than one child!");
            }
            sBIT_present = true;
          }
          else
          {
            Object localObject5;
            int i1;
            if (str1.equals("sPLT"))
            {
              sPLT_paletteName = getAttribute(localNode, "name");
              sPLT_sampleDepth = getIntAttribute(localNode, "sampleDepth");
              localObject1 = new int['Ā'];
              localObject3 = new int['Ā'];
              localObject5 = new int['Ā'];
              localObject7 = new int['Ā'];
              localObject8 = new int['Ā'];
              i1 = -1;
              localObject9 = localNode.getFirstChild();
              if (localObject9 == null) {
                fatal(localNode, "sPLT node has no children!");
              }
              while (localObject9 != null)
              {
                if (!((Node)localObject9).getNodeName().equals("sPLTEntry")) {
                  fatal(localNode, "Only an sPLTEntry may be a child of an sPLT!");
                }
                i2 = getIntAttribute((Node)localObject9, "index");
                if ((i2 < 0) || (i2 > 255)) {
                  fatal(localNode, "Bad value for PLTEEntry attribute index!");
                }
                if (i2 > i1) {
                  i1 = i2;
                }
                localObject1[i2] = getIntAttribute((Node)localObject9, "red");
                localObject3[i2] = getIntAttribute((Node)localObject9, "green");
                localObject5[i2] = getIntAttribute((Node)localObject9, "blue");
                localObject7[i2] = getIntAttribute((Node)localObject9, "alpha");
                localObject8[i2] = getIntAttribute((Node)localObject9, "frequency");
                localObject9 = ((Node)localObject9).getNextSibling();
              }
              int i2 = i1 + 1;
              sPLT_red = new int[i2];
              sPLT_green = new int[i2];
              sPLT_blue = new int[i2];
              sPLT_alpha = new int[i2];
              sPLT_frequency = new int[i2];
              System.arraycopy(localObject1, 0, sPLT_red, 0, i2);
              System.arraycopy(localObject3, 0, sPLT_green, 0, i2);
              System.arraycopy(localObject5, 0, sPLT_blue, 0, i2);
              System.arraycopy(localObject7, 0, sPLT_alpha, 0, i2);
              System.arraycopy(localObject8, 0, sPLT_frequency, 0, i2);
              sPLT_present = true;
            }
            else if (str1.equals("sRGB"))
            {
              sRGB_renderingIntent = getEnumeratedAttribute(localNode, "renderingIntent", renderingIntentNames);
              sRGB_present = true;
            }
            else if (str1.equals("tEXt"))
            {
              for (localObject1 = localNode.getFirstChild(); localObject1 != null; localObject1 = ((Node)localObject1).getNextSibling())
              {
                if (!((Node)localObject1).getNodeName().equals("tEXtEntry")) {
                  fatal(localNode, "Only an tEXtEntry may be a child of an tEXt!");
                }
                localObject3 = getAttribute((Node)localObject1, "keyword");
                tEXt_keyword.add(localObject3);
                localObject5 = getAttribute((Node)localObject1, "value");
                tEXt_text.add(localObject5);
              }
            }
            else if (str1.equals("tIME"))
            {
              tIME_year = getIntAttribute(localNode, "year");
              tIME_month = getIntAttribute(localNode, "month");
              tIME_day = getIntAttribute(localNode, "day");
              tIME_hour = getIntAttribute(localNode, "hour");
              tIME_minute = getIntAttribute(localNode, "minute");
              tIME_second = getIntAttribute(localNode, "second");
              tIME_present = true;
            }
            else if (str1.equals("tRNS"))
            {
              tRNS_present = false;
              localObject1 = localNode.getFirstChild();
              if (localObject1 == null) {
                fatal(localNode, "tRNS node has no children!");
              }
              localObject3 = ((Node)localObject1).getNodeName();
              if (((String)localObject3).equals("tRNS_Palette"))
              {
                localObject5 = new byte['Ā'];
                int m = -1;
                localObject8 = ((Node)localObject1).getFirstChild();
                if (localObject8 == null) {
                  fatal(localNode, "tRNS_Palette node has no children!");
                }
                while (localObject8 != null)
                {
                  if (!((Node)localObject8).getNodeName().equals("tRNS_PaletteEntry")) {
                    fatal(localNode, "Only a tRNS_PaletteEntry may be a child of a tRNS_Palette!");
                  }
                  i1 = getIntAttribute((Node)localObject8, "index");
                  if ((i1 < 0) || (i1 > 255)) {
                    fatal(localNode, "Bad value for tRNS_PaletteEntry attribute index!");
                  }
                  if (i1 > m) {
                    m = i1;
                  }
                  localObject5[i1] = ((byte)getIntAttribute((Node)localObject8, "alpha"));
                  localObject8 = ((Node)localObject8).getNextSibling();
                }
                i1 = m + 1;
                tRNS_alpha = new byte[i1];
                tRNS_colorType = 3;
                System.arraycopy(localObject5, 0, tRNS_alpha, 0, i1);
              }
              else if (((String)localObject3).equals("tRNS_Grayscale"))
              {
                tRNS_gray = getIntAttribute((Node)localObject1, "gray");
                tRNS_colorType = 0;
              }
              else if (((String)localObject3).equals("tRNS_RGB"))
              {
                tRNS_red = getIntAttribute((Node)localObject1, "red");
                tRNS_green = getIntAttribute((Node)localObject1, "green");
                tRNS_blue = getIntAttribute((Node)localObject1, "blue");
                tRNS_colorType = 2;
              }
              else
              {
                fatal(localNode, "Bad child of a tRNS node!");
              }
              if (((Node)localObject1).getNextSibling() != null) {
                fatal(localNode, "tRNS node has more than one child!");
              }
              tRNS_present = true;
            }
            else if (str1.equals("zTXt"))
            {
              for (localObject1 = localNode.getFirstChild(); localObject1 != null; localObject1 = ((Node)localObject1).getNextSibling())
              {
                if (!((Node)localObject1).getNodeName().equals("zTXtEntry")) {
                  fatal(localNode, "Only an zTXtEntry may be a child of an zTXt!");
                }
                localObject3 = getAttribute((Node)localObject1, "keyword");
                zTXt_keyword.add(localObject3);
                int j = getEnumeratedAttribute((Node)localObject1, "compressionMethod", zTXt_compressionMethodNames);
                zTXt_compressionMethod.add(new Integer(j));
                String str2 = getAttribute((Node)localObject1, "text");
                zTXt_text.add(str2);
              }
            }
            else if (str1.equals("UnknownChunks"))
            {
              for (localObject1 = localNode.getFirstChild(); localObject1 != null; localObject1 = ((Node)localObject1).getNextSibling())
              {
                if (!((Node)localObject1).getNodeName().equals("UnknownChunk")) {
                  fatal(localNode, "Only an UnknownChunk may be a child of an UnknownChunks!");
                }
                localObject3 = getAttribute((Node)localObject1, "type");
                Object localObject6 = ((IIOMetadataNode)localObject1).getUserObject();
                if (((String)localObject3).length() != 4) {
                  fatal((Node)localObject1, "Chunk type must be 4 characters!");
                }
                if (localObject6 == null) {
                  fatal((Node)localObject1, "No chunk data present in user object!");
                }
                if (!(localObject6 instanceof byte[])) {
                  fatal((Node)localObject1, "User object not a byte array!");
                }
                unknownChunkType.add(localObject3);
                unknownChunkData.add(((byte[])localObject6).clone());
              }
            }
            else
            {
              fatal(localNode, "Unknown child of root node!");
            }
          }
        }
      }
    }
  }
  
  private boolean isValidKeyword(String paramString)
  {
    int i = paramString.length();
    if ((i < 1) || (i >= 80)) {
      return false;
    }
    if ((paramString.startsWith(" ")) || (paramString.endsWith(" ")) || (paramString.contains("  "))) {
      return false;
    }
    return isISOLatin(paramString, false);
  }
  
  private boolean isISOLatin(String paramString, boolean paramBoolean)
  {
    int i = paramString.length();
    for (int j = 0; j < i; j++)
    {
      int k = paramString.charAt(j);
      if (((k < 32) || (k > 255) || ((k > 126) && (k < 161))) && ((!paramBoolean) || (k != 16))) {
        return false;
      }
    }
    return true;
  }
  
  private void mergeStandardTree(Node paramNode)
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
      int i2;
      Node localNode4;
      int k;
      int i1;
      if (str1.equals("Chroma"))
      {
        for (localNode2 = localNode1.getFirstChild(); localNode2 != null; localNode2 = localNode2.getNextSibling())
        {
          str2 = localNode2.getNodeName();
          if (str2.equals("Gamma"))
          {
            float f1 = getFloatAttribute(localNode2, "value");
            gAMA_present = true;
            gAMA_gamma = ((int)(f1 * 100000.0F + 0.5D));
          }
          else if (str2.equals("Palette"))
          {
            byte[] arrayOfByte1 = new byte['Ā'];
            byte[] arrayOfByte2 = new byte['Ā'];
            byte[] arrayOfByte3 = new byte['Ā'];
            i2 = -1;
            for (localNode4 = localNode2.getFirstChild(); localNode4 != null; localNode4 = localNode4.getNextSibling())
            {
              i3 = getIntAttribute(localNode4, "index");
              if ((i3 >= 0) && (i3 <= 255))
              {
                arrayOfByte1[i3] = ((byte)getIntAttribute(localNode4, "red"));
                arrayOfByte2[i3] = ((byte)getIntAttribute(localNode4, "green"));
                arrayOfByte3[i3] = ((byte)getIntAttribute(localNode4, "blue"));
                if (i3 > i2) {
                  i2 = i3;
                }
              }
            }
            int i3 = i2 + 1;
            PLTE_red = new byte[i3];
            PLTE_green = new byte[i3];
            PLTE_blue = new byte[i3];
            System.arraycopy(arrayOfByte1, 0, PLTE_red, 0, i3);
            System.arraycopy(arrayOfByte2, 0, PLTE_green, 0, i3);
            System.arraycopy(arrayOfByte3, 0, PLTE_blue, 0, i3);
            PLTE_present = true;
          }
          else if (str2.equals("BackgroundIndex"))
          {
            bKGD_present = true;
            bKGD_colorType = 3;
            bKGD_index = getIntAttribute(localNode2, "value");
          }
          else if (str2.equals("BackgroundColor"))
          {
            k = getIntAttribute(localNode2, "red");
            int n = getIntAttribute(localNode2, "green");
            i1 = getIntAttribute(localNode2, "blue");
            if ((k == n) && (k == i1))
            {
              bKGD_colorType = 0;
              bKGD_gray = k;
            }
            else
            {
              bKGD_red = k;
              bKGD_green = n;
              bKGD_blue = i1;
            }
            bKGD_present = true;
          }
        }
      }
      else if (str1.equals("Compression"))
      {
        for (localNode2 = localNode1.getFirstChild(); localNode2 != null; localNode2 = localNode2.getNextSibling())
        {
          str2 = localNode2.getNodeName();
          if (str2.equals("NumProgressiveScans"))
          {
            k = getIntAttribute(localNode2, "value");
            IHDR_interlaceMethod = (k > 1 ? 1 : 0);
          }
        }
      }
      else if (str1.equals("Data"))
      {
        for (localNode2 = localNode1.getFirstChild(); localNode2 != null; localNode2 = localNode2.getNextSibling())
        {
          str2 = localNode2.getNodeName();
          String str4;
          StringTokenizer localStringTokenizer;
          if (str2.equals("BitsPerSample"))
          {
            str4 = getAttribute(localNode2, "value");
            localStringTokenizer = new StringTokenizer(str4);
            i1 = -1;
            while (localStringTokenizer.hasMoreTokens())
            {
              i2 = Integer.parseInt(localStringTokenizer.nextToken());
              if (i2 > i1) {
                i1 = i2;
              }
            }
            if (i1 < 1) {
              i1 = 1;
            }
            if (i1 == 3) {
              i1 = 4;
            }
            if ((i1 > 4) || (i1 < 8)) {
              i1 = 8;
            }
            if (i1 > 8) {
              i1 = 16;
            }
            IHDR_bitDepth = i1;
          }
          else if (str2.equals("SignificantBitsPerSample"))
          {
            str4 = getAttribute(localNode2, "value");
            localStringTokenizer = new StringTokenizer(str4);
            i1 = localStringTokenizer.countTokens();
            if (i1 == 1)
            {
              sBIT_colorType = 0;
              sBIT_grayBits = Integer.parseInt(localStringTokenizer.nextToken());
            }
            else if (i1 == 2)
            {
              sBIT_colorType = 4;
              sBIT_grayBits = Integer.parseInt(localStringTokenizer.nextToken());
              sBIT_alphaBits = Integer.parseInt(localStringTokenizer.nextToken());
            }
            else if (i1 == 3)
            {
              sBIT_colorType = 2;
              sBIT_redBits = Integer.parseInt(localStringTokenizer.nextToken());
              sBIT_greenBits = Integer.parseInt(localStringTokenizer.nextToken());
              sBIT_blueBits = Integer.parseInt(localStringTokenizer.nextToken());
            }
            else if (i1 == 4)
            {
              sBIT_colorType = 6;
              sBIT_redBits = Integer.parseInt(localStringTokenizer.nextToken());
              sBIT_greenBits = Integer.parseInt(localStringTokenizer.nextToken());
              sBIT_blueBits = Integer.parseInt(localStringTokenizer.nextToken());
              sBIT_alphaBits = Integer.parseInt(localStringTokenizer.nextToken());
            }
            if ((i1 >= 1) && (i1 <= 4)) {
              sBIT_present = true;
            }
          }
        }
      }
      else if (str1.equals("Dimension"))
      {
        int i = 0;
        int j = 0;
        int m = 0;
        float f2 = -1.0F;
        float f3 = -1.0F;
        float f4 = -1.0F;
        for (localNode4 = localNode1.getFirstChild(); localNode4 != null; localNode4 = localNode4.getNextSibling())
        {
          String str9 = localNode4.getNodeName();
          if (str9.equals("PixelAspectRatio"))
          {
            f4 = getFloatAttribute(localNode4, "value");
            m = 1;
          }
          else if (str9.equals("HorizontalPixelSize"))
          {
            f2 = getFloatAttribute(localNode4, "value");
            i = 1;
          }
          else if (str9.equals("VerticalPixelSize"))
          {
            f3 = getFloatAttribute(localNode4, "value");
            j = 1;
          }
        }
        if ((i != 0) && (j != 0))
        {
          pHYs_present = true;
          pHYs_unitSpecifier = 1;
          pHYs_pixelsPerUnitXAxis = ((int)(f2 * 1000.0F + 0.5F));
          pHYs_pixelsPerUnitYAxis = ((int)(f3 * 1000.0F + 0.5F));
        }
        else if (m != 0)
        {
          pHYs_present = true;
          pHYs_unitSpecifier = 0;
          for (int i4 = 1; i4 < 100; i4++)
          {
            int i5 = (int)(f4 * i4);
            if (Math.abs(i5 / i4 - f4) < 0.001D) {
              break;
            }
          }
          pHYs_pixelsPerUnitXAxis = ((int)(f4 * i4));
          pHYs_pixelsPerUnitYAxis = i4;
        }
      }
      else
      {
        Node localNode3;
        String str3;
        if (str1.equals("Document")) {
          for (localNode3 = localNode1.getFirstChild(); localNode3 != null; localNode3 = localNode3.getNextSibling())
          {
            str3 = localNode3.getNodeName();
            if (str3.equals("ImageModificationTime"))
            {
              tIME_present = true;
              tIME_year = getIntAttribute(localNode3, "year");
              tIME_month = getIntAttribute(localNode3, "month");
              tIME_day = getIntAttribute(localNode3, "day");
              tIME_hour = getIntAttribute(localNode3, "hour", 0, false);
              tIME_minute = getIntAttribute(localNode3, "minute", 0, false);
              tIME_second = getIntAttribute(localNode3, "second", 0, false);
            }
          }
        } else if (str1.equals("Text")) {
          for (localNode3 = localNode1.getFirstChild(); localNode3 != null; localNode3 = localNode3.getNextSibling())
          {
            str3 = localNode3.getNodeName();
            if (str3.equals("TextEntry"))
            {
              String str5 = getAttribute(localNode3, "keyword", "", false);
              String str6 = getAttribute(localNode3, "value");
              String str7 = getAttribute(localNode3, "language", "", false);
              String str8 = getAttribute(localNode3, "compression", "none", false);
              if (isValidKeyword(str5)) {
                if (isISOLatin(str6, true))
                {
                  if (str8.equals("zip"))
                  {
                    zTXt_keyword.add(str5);
                    zTXt_text.add(str6);
                    zTXt_compressionMethod.add(Integer.valueOf(0));
                  }
                  else
                  {
                    tEXt_keyword.add(str5);
                    tEXt_text.add(str6);
                  }
                }
                else
                {
                  iTXt_keyword.add(str5);
                  iTXt_compressionFlag.add(Boolean.valueOf(str8.equals("zip")));
                  iTXt_compressionMethod.add(Integer.valueOf(0));
                  iTXt_languageTag.add(str7);
                  iTXt_translatedKeyword.add(str5);
                  iTXt_text.add(str6);
                }
              }
            }
          }
        }
      }
    }
  }
  
  public void reset()
  {
    IHDR_present = false;
    PLTE_present = false;
    bKGD_present = false;
    cHRM_present = false;
    gAMA_present = false;
    hIST_present = false;
    iCCP_present = false;
    iTXt_keyword = new ArrayList();
    iTXt_compressionFlag = new ArrayList();
    iTXt_compressionMethod = new ArrayList();
    iTXt_languageTag = new ArrayList();
    iTXt_translatedKeyword = new ArrayList();
    iTXt_text = new ArrayList();
    pHYs_present = false;
    sBIT_present = false;
    sPLT_present = false;
    sRGB_present = false;
    tEXt_keyword = new ArrayList();
    tEXt_text = new ArrayList();
    tIME_present = false;
    tRNS_present = false;
    zTXt_keyword = new ArrayList();
    zTXt_compressionMethod = new ArrayList();
    zTXt_text = new ArrayList();
    unknownChunkType = new ArrayList();
    unknownChunkData = new ArrayList();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\plugins\png\PNGMetadata.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */