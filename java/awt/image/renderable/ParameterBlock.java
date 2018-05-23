package java.awt.image.renderable;

import java.awt.image.RenderedImage;
import java.io.Serializable;
import java.util.Vector;

public class ParameterBlock
  implements Cloneable, Serializable
{
  protected Vector<Object> sources = new Vector();
  protected Vector<Object> parameters = new Vector();
  
  public ParameterBlock() {}
  
  public ParameterBlock(Vector<Object> paramVector)
  {
    setSources(paramVector);
  }
  
  public ParameterBlock(Vector<Object> paramVector1, Vector<Object> paramVector2)
  {
    setSources(paramVector1);
    setParameters(paramVector2);
  }
  
  public Object shallowClone()
  {
    try
    {
      return super.clone();
    }
    catch (Exception localException) {}
    return null;
  }
  
  public Object clone()
  {
    ParameterBlock localParameterBlock;
    try
    {
      localParameterBlock = (ParameterBlock)super.clone();
    }
    catch (Exception localException)
    {
      return null;
    }
    if (sources != null) {
      localParameterBlock.setSources((Vector)sources.clone());
    }
    if (parameters != null) {
      localParameterBlock.setParameters((Vector)parameters.clone());
    }
    return localParameterBlock;
  }
  
  public ParameterBlock addSource(Object paramObject)
  {
    sources.addElement(paramObject);
    return this;
  }
  
  public Object getSource(int paramInt)
  {
    return sources.elementAt(paramInt);
  }
  
  public ParameterBlock setSource(Object paramObject, int paramInt)
  {
    int i = sources.size();
    int j = paramInt + 1;
    if (i < j) {
      sources.setSize(j);
    }
    sources.setElementAt(paramObject, paramInt);
    return this;
  }
  
  public RenderedImage getRenderedSource(int paramInt)
  {
    return (RenderedImage)sources.elementAt(paramInt);
  }
  
  public RenderableImage getRenderableSource(int paramInt)
  {
    return (RenderableImage)sources.elementAt(paramInt);
  }
  
  public int getNumSources()
  {
    return sources.size();
  }
  
  public Vector<Object> getSources()
  {
    return sources;
  }
  
  public void setSources(Vector<Object> paramVector)
  {
    sources = paramVector;
  }
  
  public void removeSources()
  {
    sources = new Vector();
  }
  
  public int getNumParameters()
  {
    return parameters.size();
  }
  
  public Vector<Object> getParameters()
  {
    return parameters;
  }
  
  public void setParameters(Vector<Object> paramVector)
  {
    parameters = paramVector;
  }
  
  public void removeParameters()
  {
    parameters = new Vector();
  }
  
  public ParameterBlock add(Object paramObject)
  {
    parameters.addElement(paramObject);
    return this;
  }
  
  public ParameterBlock add(byte paramByte)
  {
    return add(new Byte(paramByte));
  }
  
  public ParameterBlock add(char paramChar)
  {
    return add(new Character(paramChar));
  }
  
  public ParameterBlock add(short paramShort)
  {
    return add(new Short(paramShort));
  }
  
  public ParameterBlock add(int paramInt)
  {
    return add(new Integer(paramInt));
  }
  
  public ParameterBlock add(long paramLong)
  {
    return add(new Long(paramLong));
  }
  
  public ParameterBlock add(float paramFloat)
  {
    return add(new Float(paramFloat));
  }
  
  public ParameterBlock add(double paramDouble)
  {
    return add(new Double(paramDouble));
  }
  
  public ParameterBlock set(Object paramObject, int paramInt)
  {
    int i = parameters.size();
    int j = paramInt + 1;
    if (i < j) {
      parameters.setSize(j);
    }
    parameters.setElementAt(paramObject, paramInt);
    return this;
  }
  
  public ParameterBlock set(byte paramByte, int paramInt)
  {
    return set(new Byte(paramByte), paramInt);
  }
  
  public ParameterBlock set(char paramChar, int paramInt)
  {
    return set(new Character(paramChar), paramInt);
  }
  
  public ParameterBlock set(short paramShort, int paramInt)
  {
    return set(new Short(paramShort), paramInt);
  }
  
  public ParameterBlock set(int paramInt1, int paramInt2)
  {
    return set(new Integer(paramInt1), paramInt2);
  }
  
  public ParameterBlock set(long paramLong, int paramInt)
  {
    return set(new Long(paramLong), paramInt);
  }
  
  public ParameterBlock set(float paramFloat, int paramInt)
  {
    return set(new Float(paramFloat), paramInt);
  }
  
  public ParameterBlock set(double paramDouble, int paramInt)
  {
    return set(new Double(paramDouble), paramInt);
  }
  
  public Object getObjectParameter(int paramInt)
  {
    return parameters.elementAt(paramInt);
  }
  
  public byte getByteParameter(int paramInt)
  {
    return ((Byte)parameters.elementAt(paramInt)).byteValue();
  }
  
  public char getCharParameter(int paramInt)
  {
    return ((Character)parameters.elementAt(paramInt)).charValue();
  }
  
  public short getShortParameter(int paramInt)
  {
    return ((Short)parameters.elementAt(paramInt)).shortValue();
  }
  
  public int getIntParameter(int paramInt)
  {
    return ((Integer)parameters.elementAt(paramInt)).intValue();
  }
  
  public long getLongParameter(int paramInt)
  {
    return ((Long)parameters.elementAt(paramInt)).longValue();
  }
  
  public float getFloatParameter(int paramInt)
  {
    return ((Float)parameters.elementAt(paramInt)).floatValue();
  }
  
  public double getDoubleParameter(int paramInt)
  {
    return ((Double)parameters.elementAt(paramInt)).doubleValue();
  }
  
  public Class[] getParamClasses()
  {
    int i = getNumParameters();
    Class[] arrayOfClass = new Class[i];
    for (int j = 0; j < i; j++)
    {
      Object localObject = getObjectParameter(j);
      if ((localObject instanceof Byte)) {
        arrayOfClass[j] = Byte.TYPE;
      } else if ((localObject instanceof Character)) {
        arrayOfClass[j] = Character.TYPE;
      } else if ((localObject instanceof Short)) {
        arrayOfClass[j] = Short.TYPE;
      } else if ((localObject instanceof Integer)) {
        arrayOfClass[j] = Integer.TYPE;
      } else if ((localObject instanceof Long)) {
        arrayOfClass[j] = Long.TYPE;
      } else if ((localObject instanceof Float)) {
        arrayOfClass[j] = Float.TYPE;
      } else if ((localObject instanceof Double)) {
        arrayOfClass[j] = Double.TYPE;
      } else {
        arrayOfClass[j] = localObject.getClass();
      }
    }
    return arrayOfClass;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\image\renderable\ParameterBlock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */