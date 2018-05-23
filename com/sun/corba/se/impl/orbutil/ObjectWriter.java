package com.sun.corba.se.impl.orbutil;

import java.util.Arrays;

public abstract class ObjectWriter
{
  protected StringBuffer result = new StringBuffer();
  
  public static ObjectWriter make(boolean paramBoolean, int paramInt1, int paramInt2)
  {
    if (paramBoolean) {
      return new IndentingObjectWriter(paramInt1, paramInt2);
    }
    return new SimpleObjectWriter(null);
  }
  
  public abstract void startObject(Object paramObject);
  
  public abstract void startElement();
  
  public abstract void endElement();
  
  public abstract void endObject(String paramString);
  
  public abstract void endObject();
  
  public String toString()
  {
    return result.toString();
  }
  
  public void append(boolean paramBoolean)
  {
    result.append(paramBoolean);
  }
  
  public void append(char paramChar)
  {
    result.append(paramChar);
  }
  
  public void append(short paramShort)
  {
    result.append(paramShort);
  }
  
  public void append(int paramInt)
  {
    result.append(paramInt);
  }
  
  public void append(long paramLong)
  {
    result.append(paramLong);
  }
  
  public void append(float paramFloat)
  {
    result.append(paramFloat);
  }
  
  public void append(double paramDouble)
  {
    result.append(paramDouble);
  }
  
  public void append(String paramString)
  {
    result.append(paramString);
  }
  
  protected ObjectWriter() {}
  
  protected void appendObjectHeader(Object paramObject)
  {
    result.append(paramObject.getClass().getName());
    result.append("<");
    result.append(System.identityHashCode(paramObject));
    result.append(">");
    Class localClass = paramObject.getClass().getComponentType();
    if (localClass != null)
    {
      result.append("[");
      Object localObject;
      if (localClass == Boolean.TYPE)
      {
        localObject = (boolean[])paramObject;
        result.append(localObject.length);
        result.append("]");
      }
      else if (localClass == Byte.TYPE)
      {
        localObject = (byte[])paramObject;
        result.append(localObject.length);
        result.append("]");
      }
      else if (localClass == Short.TYPE)
      {
        localObject = (short[])paramObject;
        result.append(localObject.length);
        result.append("]");
      }
      else if (localClass == Integer.TYPE)
      {
        localObject = (int[])paramObject;
        result.append(localObject.length);
        result.append("]");
      }
      else if (localClass == Long.TYPE)
      {
        localObject = (long[])paramObject;
        result.append(localObject.length);
        result.append("]");
      }
      else if (localClass == Character.TYPE)
      {
        localObject = (char[])paramObject;
        result.append(localObject.length);
        result.append("]");
      }
      else if (localClass == Float.TYPE)
      {
        localObject = (float[])paramObject;
        result.append(localObject.length);
        result.append("]");
      }
      else if (localClass == Double.TYPE)
      {
        localObject = (double[])paramObject;
        result.append(localObject.length);
        result.append("]");
      }
      else
      {
        localObject = (Object[])paramObject;
        result.append(localObject.length);
        result.append("]");
      }
    }
    result.append("(");
  }
  
  private static class IndentingObjectWriter
    extends ObjectWriter
  {
    private int level;
    private int increment;
    
    public IndentingObjectWriter(int paramInt1, int paramInt2)
    {
      level = paramInt1;
      increment = paramInt2;
      startLine();
    }
    
    private void startLine()
    {
      char[] arrayOfChar = new char[level * increment];
      Arrays.fill(arrayOfChar, ' ');
      result.append(arrayOfChar);
    }
    
    public void startObject(Object paramObject)
    {
      appendObjectHeader(paramObject);
      level += 1;
    }
    
    public void startElement()
    {
      result.append("\n");
      startLine();
    }
    
    public void endElement() {}
    
    public void endObject(String paramString)
    {
      level -= 1;
      result.append(paramString);
      result.append(")");
    }
    
    public void endObject()
    {
      level -= 1;
      result.append("\n");
      startLine();
      result.append(")");
    }
  }
  
  private static class SimpleObjectWriter
    extends ObjectWriter
  {
    private SimpleObjectWriter() {}
    
    public void startObject(Object paramObject)
    {
      appendObjectHeader(paramObject);
      result.append(" ");
    }
    
    public void startElement()
    {
      result.append(" ");
    }
    
    public void endObject(String paramString)
    {
      result.append(paramString);
      result.append(")");
    }
    
    public void endElement() {}
    
    public void endObject()
    {
      result.append(")");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\orbutil\ObjectWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */