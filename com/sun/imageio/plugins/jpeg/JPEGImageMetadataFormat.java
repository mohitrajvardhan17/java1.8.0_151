package com.sun.imageio.plugins.jpeg;

import java.awt.color.ICC_Profile;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadataFormat;

public class JPEGImageMetadataFormat
  extends JPEGMetadataFormat
{
  private static JPEGImageMetadataFormat theInstance = null;
  
  private JPEGImageMetadataFormat()
  {
    super("javax_imageio_jpeg_image_1.0", 1);
    addElement("JPEGvariety", "javax_imageio_jpeg_image_1.0", 3);
    addElement("markerSequence", "javax_imageio_jpeg_image_1.0", 4);
    addElement("app0JFIF", "JPEGvariety", 2);
    addStreamElements("markerSequence");
    addElement("app14Adobe", "markerSequence", 0);
    addElement("sof", "markerSequence", 1, 4);
    addElement("sos", "markerSequence", 1, 4);
    addElement("JFXX", "app0JFIF", 1, Integer.MAX_VALUE);
    addElement("app0JFXX", "JFXX", 3);
    addElement("app2ICC", "app0JFIF", 0);
    addAttribute("app0JFIF", "majorVersion", 2, false, "1", "0", "255", true, true);
    addAttribute("app0JFIF", "minorVersion", 2, false, "2", "0", "255", true, true);
    ArrayList localArrayList1 = new ArrayList();
    localArrayList1.add("0");
    localArrayList1.add("1");
    localArrayList1.add("2");
    addAttribute("app0JFIF", "resUnits", 2, false, "0", localArrayList1);
    addAttribute("app0JFIF", "Xdensity", 2, false, "1", "1", "65535", true, true);
    addAttribute("app0JFIF", "Ydensity", 2, false, "1", "1", "65535", true, true);
    addAttribute("app0JFIF", "thumbWidth", 2, false, "0", "0", "255", true, true);
    addAttribute("app0JFIF", "thumbHeight", 2, false, "0", "0", "255", true, true);
    addElement("JFIFthumbJPEG", "app0JFXX", 2);
    addElement("JFIFthumbPalette", "app0JFXX", 0);
    addElement("JFIFthumbRGB", "app0JFXX", 0);
    ArrayList localArrayList2 = new ArrayList();
    localArrayList2.add("16");
    localArrayList2.add("17");
    localArrayList2.add("19");
    addAttribute("app0JFXX", "extensionCode", 2, false, null, localArrayList2);
    addChildElement("markerSequence", "JFIFthumbJPEG");
    addAttribute("JFIFthumbPalette", "thumbWidth", 2, false, null, "0", "255", true, true);
    addAttribute("JFIFthumbPalette", "thumbHeight", 2, false, null, "0", "255", true, true);
    addAttribute("JFIFthumbRGB", "thumbWidth", 2, false, null, "0", "255", true, true);
    addAttribute("JFIFthumbRGB", "thumbHeight", 2, false, null, "0", "255", true, true);
    addObjectValue("app2ICC", ICC_Profile.class, false, null);
    addAttribute("app14Adobe", "version", 2, false, "100", "100", "255", true, true);
    addAttribute("app14Adobe", "flags0", 2, false, "0", "0", "65535", true, true);
    addAttribute("app14Adobe", "flags1", 2, false, "0", "0", "65535", true, true);
    ArrayList localArrayList3 = new ArrayList();
    localArrayList3.add("0");
    localArrayList3.add("1");
    localArrayList3.add("2");
    addAttribute("app14Adobe", "transform", 2, true, null, localArrayList3);
    addElement("componentSpec", "sof", 0);
    ArrayList localArrayList4 = new ArrayList();
    localArrayList4.add("0");
    localArrayList4.add("1");
    localArrayList4.add("2");
    addAttribute("sof", "process", 2, false, null, localArrayList4);
    addAttribute("sof", "samplePrecision", 2, false, "8");
    addAttribute("sof", "numLines", 2, false, null, "0", "65535", true, true);
    addAttribute("sof", "samplesPerLine", 2, false, null, "0", "65535", true, true);
    ArrayList localArrayList5 = new ArrayList();
    localArrayList5.add("1");
    localArrayList5.add("2");
    localArrayList5.add("3");
    localArrayList5.add("4");
    addAttribute("sof", "numFrameComponents", 2, false, null, localArrayList5);
    addAttribute("componentSpec", "componentId", 2, true, null, "0", "255", true, true);
    addAttribute("componentSpec", "HsamplingFactor", 2, true, null, "1", "255", true, true);
    addAttribute("componentSpec", "VsamplingFactor", 2, true, null, "1", "255", true, true);
    ArrayList localArrayList6 = new ArrayList();
    localArrayList6.add("0");
    localArrayList6.add("1");
    localArrayList6.add("2");
    localArrayList6.add("3");
    addAttribute("componentSpec", "QtableSelector", 2, true, null, localArrayList6);
    addElement("scanComponentSpec", "sos", 0);
    addAttribute("sos", "numScanComponents", 2, true, null, localArrayList5);
    addAttribute("sos", "startSpectralSelection", 2, false, "0", "0", "63", true, true);
    addAttribute("sos", "endSpectralSelection", 2, false, "63", "0", "63", true, true);
    addAttribute("sos", "approxHigh", 2, false, "0", "0", "15", true, true);
    addAttribute("sos", "approxLow", 2, false, "0", "0", "15", true, true);
    addAttribute("scanComponentSpec", "componentSelector", 2, true, null, "0", "255", true, true);
    addAttribute("scanComponentSpec", "dcHuffTable", 2, true, null, localArrayList6);
    addAttribute("scanComponentSpec", "acHuffTable", 2, true, null, localArrayList6);
  }
  
  public boolean canNodeAppear(String paramString, ImageTypeSpecifier paramImageTypeSpecifier)
  {
    if ((paramString.equals(getRootName())) || (paramString.equals("JPEGvariety")) || (isInSubtree(paramString, "markerSequence"))) {
      return true;
    }
    return (isInSubtree(paramString, "app0JFIF")) && (JPEG.isJFIFcompliant(paramImageTypeSpecifier, true));
  }
  
  public static synchronized IIOMetadataFormat getInstance()
  {
    if (theInstance == null) {
      theInstance = new JPEGImageMetadataFormat();
    }
    return theInstance;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\plugins\jpeg\JPEGImageMetadataFormat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */