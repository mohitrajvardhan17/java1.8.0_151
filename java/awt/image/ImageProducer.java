package java.awt.image;

public abstract interface ImageProducer
{
  public abstract void addConsumer(ImageConsumer paramImageConsumer);
  
  public abstract boolean isConsumer(ImageConsumer paramImageConsumer);
  
  public abstract void removeConsumer(ImageConsumer paramImageConsumer);
  
  public abstract void startProduction(ImageConsumer paramImageConsumer);
  
  public abstract void requestTopDownLeftRightResend(ImageConsumer paramImageConsumer);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\image\ImageProducer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */