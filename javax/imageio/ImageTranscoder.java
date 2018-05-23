package javax.imageio;

import javax.imageio.metadata.IIOMetadata;

public abstract interface ImageTranscoder
{
  public abstract IIOMetadata convertStreamMetadata(IIOMetadata paramIIOMetadata, ImageWriteParam paramImageWriteParam);
  
  public abstract IIOMetadata convertImageMetadata(IIOMetadata paramIIOMetadata, ImageTypeSpecifier paramImageTypeSpecifier, ImageWriteParam paramImageWriteParam);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\imageio\ImageTranscoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */