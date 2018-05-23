package java.awt.image;

import java.util.Hashtable;

public class ImageFilter
  implements ImageConsumer, Cloneable
{
  protected ImageConsumer consumer;
  
  public ImageFilter() {}
  
  public ImageFilter getFilterInstance(ImageConsumer paramImageConsumer)
  {
    ImageFilter localImageFilter = (ImageFilter)clone();
    consumer = paramImageConsumer;
    return localImageFilter;
  }
  
  public void setDimensions(int paramInt1, int paramInt2)
  {
    consumer.setDimensions(paramInt1, paramInt2);
  }
  
  public void setProperties(Hashtable<?, ?> paramHashtable)
  {
    Hashtable localHashtable = (Hashtable)paramHashtable.clone();
    Object localObject = localHashtable.get("filters");
    if (localObject == null) {
      localHashtable.put("filters", toString());
    } else if ((localObject instanceof String)) {
      localHashtable.put("filters", (String)localObject + toString());
    }
    consumer.setProperties(localHashtable);
  }
  
  public void setColorModel(ColorModel paramColorModel)
  {
    consumer.setColorModel(paramColorModel);
  }
  
  public void setHints(int paramInt)
  {
    consumer.setHints(paramInt);
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ColorModel paramColorModel, byte[] paramArrayOfByte, int paramInt5, int paramInt6)
  {
    consumer.setPixels(paramInt1, paramInt2, paramInt3, paramInt4, paramColorModel, paramArrayOfByte, paramInt5, paramInt6);
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ColorModel paramColorModel, int[] paramArrayOfInt, int paramInt5, int paramInt6)
  {
    consumer.setPixels(paramInt1, paramInt2, paramInt3, paramInt4, paramColorModel, paramArrayOfInt, paramInt5, paramInt6);
  }
  
  public void imageComplete(int paramInt)
  {
    consumer.imageComplete(paramInt);
  }
  
  public void resendTopDownLeftRight(ImageProducer paramImageProducer)
  {
    paramImageProducer.requestTopDownLeftRightResend(this);
  }
  
  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\image\ImageFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */