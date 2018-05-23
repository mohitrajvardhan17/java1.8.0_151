package com.sun.media.sound;

import java.util.Arrays;

public final class ModelConnectionBlock
{
  private static final ModelSource[] no_sources = new ModelSource[0];
  private ModelSource[] sources = no_sources;
  private double scale = 1.0D;
  private ModelDestination destination;
  
  public ModelConnectionBlock() {}
  
  public ModelConnectionBlock(double paramDouble, ModelDestination paramModelDestination)
  {
    scale = paramDouble;
    destination = paramModelDestination;
  }
  
  public ModelConnectionBlock(ModelSource paramModelSource, ModelDestination paramModelDestination)
  {
    if (paramModelSource != null)
    {
      sources = new ModelSource[1];
      sources[0] = paramModelSource;
    }
    destination = paramModelDestination;
  }
  
  public ModelConnectionBlock(ModelSource paramModelSource, double paramDouble, ModelDestination paramModelDestination)
  {
    if (paramModelSource != null)
    {
      sources = new ModelSource[1];
      sources[0] = paramModelSource;
    }
    scale = paramDouble;
    destination = paramModelDestination;
  }
  
  public ModelConnectionBlock(ModelSource paramModelSource1, ModelSource paramModelSource2, ModelDestination paramModelDestination)
  {
    if (paramModelSource1 != null) {
      if (paramModelSource2 == null)
      {
        sources = new ModelSource[1];
        sources[0] = paramModelSource1;
      }
      else
      {
        sources = new ModelSource[2];
        sources[0] = paramModelSource1;
        sources[1] = paramModelSource2;
      }
    }
    destination = paramModelDestination;
  }
  
  public ModelConnectionBlock(ModelSource paramModelSource1, ModelSource paramModelSource2, double paramDouble, ModelDestination paramModelDestination)
  {
    if (paramModelSource1 != null) {
      if (paramModelSource2 == null)
      {
        sources = new ModelSource[1];
        sources[0] = paramModelSource1;
      }
      else
      {
        sources = new ModelSource[2];
        sources[0] = paramModelSource1;
        sources[1] = paramModelSource2;
      }
    }
    scale = paramDouble;
    destination = paramModelDestination;
  }
  
  public ModelDestination getDestination()
  {
    return destination;
  }
  
  public void setDestination(ModelDestination paramModelDestination)
  {
    destination = paramModelDestination;
  }
  
  public double getScale()
  {
    return scale;
  }
  
  public void setScale(double paramDouble)
  {
    scale = paramDouble;
  }
  
  public ModelSource[] getSources()
  {
    return (ModelSource[])Arrays.copyOf(sources, sources.length);
  }
  
  public void setSources(ModelSource[] paramArrayOfModelSource)
  {
    sources = (paramArrayOfModelSource == null ? no_sources : (ModelSource[])Arrays.copyOf(paramArrayOfModelSource, paramArrayOfModelSource.length));
  }
  
  public void addSource(ModelSource paramModelSource)
  {
    ModelSource[] arrayOfModelSource = sources;
    sources = new ModelSource[arrayOfModelSource.length + 1];
    System.arraycopy(arrayOfModelSource, 0, sources, 0, arrayOfModelSource.length);
    sources[(sources.length - 1)] = paramModelSource;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\ModelConnectionBlock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */