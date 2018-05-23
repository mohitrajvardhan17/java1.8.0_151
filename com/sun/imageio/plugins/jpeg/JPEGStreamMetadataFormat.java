package com.sun.imageio.plugins.jpeg;

import javax.imageio.metadata.IIOMetadataFormat;

public class JPEGStreamMetadataFormat
  extends JPEGMetadataFormat
{
  private static JPEGStreamMetadataFormat theInstance = null;
  
  private JPEGStreamMetadataFormat()
  {
    super("javax_imageio_jpeg_stream_1.0", 4);
    addStreamElements(getRootName());
  }
  
  public static synchronized IIOMetadataFormat getInstance()
  {
    if (theInstance == null) {
      theInstance = new JPEGStreamMetadataFormat();
    }
    return theInstance;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\plugins\jpeg\JPEGStreamMetadataFormat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */