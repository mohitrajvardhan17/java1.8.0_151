package com.sun.activation.registries;

public class MimeTypeEntry
{
  private String type;
  private String extension;
  
  public MimeTypeEntry(String paramString1, String paramString2)
  {
    type = paramString1;
    extension = paramString2;
  }
  
  public String getMIMEType()
  {
    return type;
  }
  
  public String getFileExtension()
  {
    return extension;
  }
  
  public String toString()
  {
    return "MIMETypeEntry: " + type + ", " + extension;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\activation\registries\MimeTypeEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */