package javax.swing;

import java.io.Serializable;

public class SpinnerNumberModel
  extends AbstractSpinnerModel
  implements Serializable
{
  private Number stepSize;
  private Number value;
  private Comparable minimum;
  private Comparable maximum;
  
  public SpinnerNumberModel(Number paramNumber1, Comparable paramComparable1, Comparable paramComparable2, Number paramNumber2)
  {
    if ((paramNumber1 == null) || (paramNumber2 == null)) {
      throw new IllegalArgumentException("value and stepSize must be non-null");
    }
    if (((paramComparable1 != null) && (paramComparable1.compareTo(paramNumber1) > 0)) || ((paramComparable2 != null) && (paramComparable2.compareTo(paramNumber1) < 0))) {
      throw new IllegalArgumentException("(minimum <= value <= maximum) is false");
    }
    value = paramNumber1;
    minimum = paramComparable1;
    maximum = paramComparable2;
    stepSize = paramNumber2;
  }
  
  public SpinnerNumberModel(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this(Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), Integer.valueOf(paramInt4));
  }
  
  public SpinnerNumberModel(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    this(new Double(paramDouble1), new Double(paramDouble2), new Double(paramDouble3), new Double(paramDouble4));
  }
  
  public SpinnerNumberModel()
  {
    this(Integer.valueOf(0), null, null, Integer.valueOf(1));
  }
  
  public void setMinimum(Comparable paramComparable)
  {
    if (paramComparable == null ? minimum != null : !paramComparable.equals(minimum))
    {
      minimum = paramComparable;
      fireStateChanged();
    }
  }
  
  public Comparable getMinimum()
  {
    return minimum;
  }
  
  public void setMaximum(Comparable paramComparable)
  {
    if (paramComparable == null ? maximum != null : !paramComparable.equals(maximum))
    {
      maximum = paramComparable;
      fireStateChanged();
    }
  }
  
  public Comparable getMaximum()
  {
    return maximum;
  }
  
  public void setStepSize(Number paramNumber)
  {
    if (paramNumber == null) {
      throw new IllegalArgumentException("null stepSize");
    }
    if (!paramNumber.equals(stepSize))
    {
      stepSize = paramNumber;
      fireStateChanged();
    }
  }
  
  public Number getStepSize()
  {
    return stepSize;
  }
  
  private Number incrValue(int paramInt)
  {
    Object localObject;
    if (((value instanceof Float)) || ((value instanceof Double)))
    {
      double d = value.doubleValue() + stepSize.doubleValue() * paramInt;
      if ((value instanceof Double)) {
        localObject = new Double(d);
      } else {
        localObject = new Float(d);
      }
    }
    else
    {
      long l = value.longValue() + stepSize.longValue() * paramInt;
      if ((value instanceof Long)) {
        localObject = Long.valueOf(l);
      } else if ((value instanceof Integer)) {
        localObject = Integer.valueOf((int)l);
      } else if ((value instanceof Short)) {
        localObject = Short.valueOf((short)(int)l);
      } else {
        localObject = Byte.valueOf((byte)(int)l);
      }
    }
    if ((maximum != null) && (maximum.compareTo(localObject) < 0)) {
      return null;
    }
    if ((minimum != null) && (minimum.compareTo(localObject) > 0)) {
      return null;
    }
    return (Number)localObject;
  }
  
  public Object getNextValue()
  {
    return incrValue(1);
  }
  
  public Object getPreviousValue()
  {
    return incrValue(-1);
  }
  
  public Number getNumber()
  {
    return value;
  }
  
  public Object getValue()
  {
    return value;
  }
  
  public void setValue(Object paramObject)
  {
    if ((paramObject == null) || (!(paramObject instanceof Number))) {
      throw new IllegalArgumentException("illegal value");
    }
    if (!paramObject.equals(value))
    {
      value = ((Number)paramObject);
      fireStateChanged();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\SpinnerNumberModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */