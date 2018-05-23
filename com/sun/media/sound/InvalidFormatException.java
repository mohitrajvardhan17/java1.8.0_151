package com.sun.media.sound;

public class InvalidFormatException
  extends InvalidDataException
{
  private static final long serialVersionUID = 1L;
  
  public InvalidFormatException()
  {
    super("Invalid format!");
  }
  
  public InvalidFormatException(String paramString)
  {
    super(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\InvalidFormatException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */