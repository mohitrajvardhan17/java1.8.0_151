package javax.swing;

import java.awt.Component;
import java.awt.Dimension;

public abstract class Spring
{
  public static final int UNSET = Integer.MIN_VALUE;
  
  protected Spring() {}
  
  public abstract int getMinimumValue();
  
  public abstract int getPreferredValue();
  
  public abstract int getMaximumValue();
  
  public abstract int getValue();
  
  public abstract void setValue(int paramInt);
  
  private double range(boolean paramBoolean)
  {
    return paramBoolean ? getPreferredValue() - getMinimumValue() : getMaximumValue() - getPreferredValue();
  }
  
  double getStrain()
  {
    double d = getValue() - getPreferredValue();
    return d / range(getValue() < getPreferredValue());
  }
  
  void setStrain(double paramDouble)
  {
    setValue(getPreferredValue() + (int)(paramDouble * range(paramDouble < 0.0D)));
  }
  
  boolean isCyclic(SpringLayout paramSpringLayout)
  {
    return false;
  }
  
  public static Spring constant(int paramInt)
  {
    return constant(paramInt, paramInt, paramInt);
  }
  
  public static Spring constant(int paramInt1, int paramInt2, int paramInt3)
  {
    return new StaticSpring(paramInt1, paramInt2, paramInt3);
  }
  
  public static Spring minus(Spring paramSpring)
  {
    return new NegativeSpring(paramSpring);
  }
  
  public static Spring sum(Spring paramSpring1, Spring paramSpring2)
  {
    return new SumSpring(paramSpring1, paramSpring2);
  }
  
  public static Spring max(Spring paramSpring1, Spring paramSpring2)
  {
    return new MaxSpring(paramSpring1, paramSpring2);
  }
  
  static Spring difference(Spring paramSpring1, Spring paramSpring2)
  {
    return sum(paramSpring1, minus(paramSpring2));
  }
  
  public static Spring scale(Spring paramSpring, float paramFloat)
  {
    checkArg(paramSpring);
    return new ScaleSpring(paramSpring, paramFloat, null);
  }
  
  public static Spring width(Component paramComponent)
  {
    checkArg(paramComponent);
    return new WidthSpring(paramComponent);
  }
  
  public static Spring height(Component paramComponent)
  {
    checkArg(paramComponent);
    return new HeightSpring(paramComponent);
  }
  
  private static void checkArg(Object paramObject)
  {
    if (paramObject == null) {
      throw new NullPointerException("Argument must not be null");
    }
  }
  
  static abstract class AbstractSpring
    extends Spring
  {
    protected int size = Integer.MIN_VALUE;
    
    AbstractSpring() {}
    
    public int getValue()
    {
      return size != Integer.MIN_VALUE ? size : getPreferredValue();
    }
    
    public final void setValue(int paramInt)
    {
      if (size == paramInt) {
        return;
      }
      if (paramInt == Integer.MIN_VALUE) {
        clear();
      } else {
        setNonClearValue(paramInt);
      }
    }
    
    protected void clear()
    {
      size = Integer.MIN_VALUE;
    }
    
    protected void setNonClearValue(int paramInt)
    {
      size = paramInt;
    }
  }
  
  static abstract class CompoundSpring
    extends Spring.StaticSpring
  {
    protected Spring s1;
    protected Spring s2;
    
    public CompoundSpring(Spring paramSpring1, Spring paramSpring2)
    {
      super();
      s1 = paramSpring1;
      s2 = paramSpring2;
    }
    
    public String toString()
    {
      return "CompoundSpring of " + s1 + " and " + s2;
    }
    
    protected void clear()
    {
      super.clear();
      min = (pref = max = Integer.MIN_VALUE);
      s1.setValue(Integer.MIN_VALUE);
      s2.setValue(Integer.MIN_VALUE);
    }
    
    protected abstract int op(int paramInt1, int paramInt2);
    
    public int getMinimumValue()
    {
      if (min == Integer.MIN_VALUE) {
        min = op(s1.getMinimumValue(), s2.getMinimumValue());
      }
      return min;
    }
    
    public int getPreferredValue()
    {
      if (pref == Integer.MIN_VALUE) {
        pref = op(s1.getPreferredValue(), s2.getPreferredValue());
      }
      return pref;
    }
    
    public int getMaximumValue()
    {
      if (max == Integer.MIN_VALUE) {
        max = op(s1.getMaximumValue(), s2.getMaximumValue());
      }
      return max;
    }
    
    public int getValue()
    {
      if (size == Integer.MIN_VALUE) {
        size = op(s1.getValue(), s2.getValue());
      }
      return size;
    }
    
    boolean isCyclic(SpringLayout paramSpringLayout)
    {
      return (paramSpringLayout.isCyclic(s1)) || (paramSpringLayout.isCyclic(s2));
    }
  }
  
  static class HeightSpring
    extends Spring.AbstractSpring
  {
    Component c;
    
    public HeightSpring(Component paramComponent)
    {
      c = paramComponent;
    }
    
    public int getMinimumValue()
    {
      return c.getMinimumSize().height;
    }
    
    public int getPreferredValue()
    {
      return c.getPreferredSize().height;
    }
    
    public int getMaximumValue()
    {
      return Math.min(32767, c.getMaximumSize().height);
    }
  }
  
  private static class MaxSpring
    extends Spring.CompoundSpring
  {
    public MaxSpring(Spring paramSpring1, Spring paramSpring2)
    {
      super(paramSpring2);
    }
    
    protected int op(int paramInt1, int paramInt2)
    {
      return Math.max(paramInt1, paramInt2);
    }
    
    protected void setNonClearValue(int paramInt)
    {
      super.setNonClearValue(paramInt);
      s1.setValue(paramInt);
      s2.setValue(paramInt);
    }
  }
  
  private static class NegativeSpring
    extends Spring
  {
    private Spring s;
    
    public NegativeSpring(Spring paramSpring)
    {
      s = paramSpring;
    }
    
    public int getMinimumValue()
    {
      return -s.getMaximumValue();
    }
    
    public int getPreferredValue()
    {
      return -s.getPreferredValue();
    }
    
    public int getMaximumValue()
    {
      return -s.getMinimumValue();
    }
    
    public int getValue()
    {
      return -s.getValue();
    }
    
    public void setValue(int paramInt)
    {
      s.setValue(-paramInt);
    }
    
    boolean isCyclic(SpringLayout paramSpringLayout)
    {
      return s.isCyclic(paramSpringLayout);
    }
  }
  
  private static class ScaleSpring
    extends Spring
  {
    private Spring s;
    private float factor;
    
    private ScaleSpring(Spring paramSpring, float paramFloat)
    {
      s = paramSpring;
      factor = paramFloat;
    }
    
    public int getMinimumValue()
    {
      return Math.round((factor < 0.0F ? s.getMaximumValue() : s.getMinimumValue()) * factor);
    }
    
    public int getPreferredValue()
    {
      return Math.round(s.getPreferredValue() * factor);
    }
    
    public int getMaximumValue()
    {
      return Math.round((factor < 0.0F ? s.getMinimumValue() : s.getMaximumValue()) * factor);
    }
    
    public int getValue()
    {
      return Math.round(s.getValue() * factor);
    }
    
    public void setValue(int paramInt)
    {
      if (paramInt == Integer.MIN_VALUE) {
        s.setValue(Integer.MIN_VALUE);
      } else {
        s.setValue(Math.round(paramInt / factor));
      }
    }
    
    boolean isCyclic(SpringLayout paramSpringLayout)
    {
      return s.isCyclic(paramSpringLayout);
    }
  }
  
  static abstract class SpringMap
    extends Spring
  {
    private Spring s;
    
    public SpringMap(Spring paramSpring)
    {
      s = paramSpring;
    }
    
    protected abstract int map(int paramInt);
    
    protected abstract int inv(int paramInt);
    
    public int getMinimumValue()
    {
      return map(s.getMinimumValue());
    }
    
    public int getPreferredValue()
    {
      return map(s.getPreferredValue());
    }
    
    public int getMaximumValue()
    {
      return Math.min(32767, map(s.getMaximumValue()));
    }
    
    public int getValue()
    {
      return map(s.getValue());
    }
    
    public void setValue(int paramInt)
    {
      if (paramInt == Integer.MIN_VALUE) {
        s.setValue(Integer.MIN_VALUE);
      } else {
        s.setValue(inv(paramInt));
      }
    }
    
    boolean isCyclic(SpringLayout paramSpringLayout)
    {
      return s.isCyclic(paramSpringLayout);
    }
  }
  
  private static class StaticSpring
    extends Spring.AbstractSpring
  {
    protected int min;
    protected int pref;
    protected int max;
    
    public StaticSpring(int paramInt)
    {
      this(paramInt, paramInt, paramInt);
    }
    
    public StaticSpring(int paramInt1, int paramInt2, int paramInt3)
    {
      min = paramInt1;
      pref = paramInt2;
      max = paramInt3;
    }
    
    public String toString()
    {
      return "StaticSpring [" + min + ", " + pref + ", " + max + "]";
    }
    
    public int getMinimumValue()
    {
      return min;
    }
    
    public int getPreferredValue()
    {
      return pref;
    }
    
    public int getMaximumValue()
    {
      return max;
    }
  }
  
  private static class SumSpring
    extends Spring.CompoundSpring
  {
    public SumSpring(Spring paramSpring1, Spring paramSpring2)
    {
      super(paramSpring2);
    }
    
    protected int op(int paramInt1, int paramInt2)
    {
      return paramInt1 + paramInt2;
    }
    
    protected void setNonClearValue(int paramInt)
    {
      super.setNonClearValue(paramInt);
      s1.setStrain(getStrain());
      s2.setValue(paramInt - s1.getValue());
    }
  }
  
  static class WidthSpring
    extends Spring.AbstractSpring
  {
    Component c;
    
    public WidthSpring(Component paramComponent)
    {
      c = paramComponent;
    }
    
    public int getMinimumValue()
    {
      return c.getMinimumSize().width;
    }
    
    public int getPreferredValue()
    {
      return c.getPreferredSize().width;
    }
    
    public int getMaximumValue()
    {
      return Math.min(32767, c.getMaximumSize().width);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\Spring.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */