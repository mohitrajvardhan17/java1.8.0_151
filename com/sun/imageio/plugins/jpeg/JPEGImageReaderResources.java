package com.sun.imageio.plugins.jpeg;

import java.util.ListResourceBundle;

public class JPEGImageReaderResources
  extends ListResourceBundle
{
  public JPEGImageReaderResources() {}
  
  protected Object[][] getContents()
  {
    return new Object[][] { { Integer.toString(0), "Truncated File - Missing EOI marker" }, { Integer.toString(1), "JFIF markers not allowed in JFIF JPEG thumbnail; ignored" }, { Integer.toString(2), "Embedded color profile is invalid; ignored" } };
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\plugins\jpeg\JPEGImageReaderResources.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */