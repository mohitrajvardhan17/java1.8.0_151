package sun.print;

import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.standard.Media;

public class SunAlternateMedia
  implements PrintRequestAttribute
{
  private static final long serialVersionUID = -8878868345472850201L;
  private Media media;
  
  public SunAlternateMedia(Media paramMedia)
  {
    media = paramMedia;
  }
  
  public Media getMedia()
  {
    return media;
  }
  
  public final Class getCategory()
  {
    return SunAlternateMedia.class;
  }
  
  public final String getName()
  {
    return "sun-alternate-media";
  }
  
  public String toString()
  {
    return "alternate-media: " + media.toString();
  }
  
  public int hashCode()
  {
    return media.hashCode();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\print\SunAlternateMedia.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */