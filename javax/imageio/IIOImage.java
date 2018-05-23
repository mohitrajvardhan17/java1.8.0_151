package javax.imageio;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.util.List;
import javax.imageio.metadata.IIOMetadata;

public class IIOImage
{
  protected RenderedImage image;
  protected Raster raster;
  protected List<? extends BufferedImage> thumbnails = null;
  protected IIOMetadata metadata;
  
  public IIOImage(RenderedImage paramRenderedImage, List<? extends BufferedImage> paramList, IIOMetadata paramIIOMetadata)
  {
    if (paramRenderedImage == null) {
      throw new IllegalArgumentException("image == null!");
    }
    image = paramRenderedImage;
    raster = null;
    thumbnails = paramList;
    metadata = paramIIOMetadata;
  }
  
  public IIOImage(Raster paramRaster, List<? extends BufferedImage> paramList, IIOMetadata paramIIOMetadata)
  {
    if (paramRaster == null) {
      throw new IllegalArgumentException("raster == null!");
    }
    raster = paramRaster;
    image = null;
    thumbnails = paramList;
    metadata = paramIIOMetadata;
  }
  
  /* Error */
  public RenderedImage getRenderedImage()
  {
    // Byte code:
    //   0: aload_0
    //   1: dup
    //   2: astore_1
    //   3: monitorenter
    //   4: aload_0
    //   5: getfield 82	javax/imageio/IIOImage:image	Ljava/awt/image/RenderedImage;
    //   8: aload_1
    //   9: monitorexit
    //   10: areturn
    //   11: astore_2
    //   12: aload_1
    //   13: monitorexit
    //   14: aload_2
    //   15: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	16	0	this	IIOImage
    //   2	11	1	Ljava/lang/Object;	Object
    //   11	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   4	10	11	finally
    //   11	14	11	finally
  }
  
  public void setRenderedImage(RenderedImage paramRenderedImage)
  {
    synchronized (this)
    {
      if (paramRenderedImage == null) {
        throw new IllegalArgumentException("image == null!");
      }
      image = paramRenderedImage;
      raster = null;
    }
  }
  
  public boolean hasRaster()
  {
    synchronized (this)
    {
      return raster != null;
    }
  }
  
  /* Error */
  public Raster getRaster()
  {
    // Byte code:
    //   0: aload_0
    //   1: dup
    //   2: astore_1
    //   3: monitorenter
    //   4: aload_0
    //   5: getfield 81	javax/imageio/IIOImage:raster	Ljava/awt/image/Raster;
    //   8: aload_1
    //   9: monitorexit
    //   10: areturn
    //   11: astore_2
    //   12: aload_1
    //   13: monitorexit
    //   14: aload_2
    //   15: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	16	0	this	IIOImage
    //   2	11	1	Ljava/lang/Object;	Object
    //   11	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   4	10	11	finally
    //   11	14	11	finally
  }
  
  public void setRaster(Raster paramRaster)
  {
    synchronized (this)
    {
      if (paramRaster == null) {
        throw new IllegalArgumentException("raster == null!");
      }
      raster = paramRaster;
      image = null;
    }
  }
  
  public int getNumThumbnails()
  {
    return thumbnails == null ? 0 : thumbnails.size();
  }
  
  public BufferedImage getThumbnail(int paramInt)
  {
    if (thumbnails == null) {
      throw new IndexOutOfBoundsException("No thumbnails available!");
    }
    return (BufferedImage)thumbnails.get(paramInt);
  }
  
  public List<? extends BufferedImage> getThumbnails()
  {
    return thumbnails;
  }
  
  public void setThumbnails(List<? extends BufferedImage> paramList)
  {
    thumbnails = paramList;
  }
  
  public IIOMetadata getMetadata()
  {
    return metadata;
  }
  
  public void setMetadata(IIOMetadata paramIIOMetadata)
  {
    metadata = paramIIOMetadata;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\imageio\IIOImage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */