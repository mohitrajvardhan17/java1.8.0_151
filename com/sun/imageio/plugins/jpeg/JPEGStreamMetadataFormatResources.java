package com.sun.imageio.plugins.jpeg;

public class JPEGStreamMetadataFormatResources
  extends JPEGMetadataFormatResources
{
  public JPEGStreamMetadataFormatResources() {}
  
  protected Object[][] getContents()
  {
    Object[][] arrayOfObject = new Object[commonContents.length][2];
    for (int i = 0; i < commonContents.length; i++)
    {
      arrayOfObject[i][0] = commonContents[i][0];
      arrayOfObject[i][1] = commonContents[i][1];
    }
    return arrayOfObject;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\plugins\jpeg\JPEGStreamMetadataFormatResources.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */