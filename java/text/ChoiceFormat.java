package java.text;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.util.Arrays;

public class ChoiceFormat
  extends NumberFormat
{
  private static final long serialVersionUID = 1795184449645032964L;
  private double[] choiceLimits;
  private String[] choiceFormats;
  static final long SIGN = Long.MIN_VALUE;
  static final long EXPONENT = 9218868437227405312L;
  static final long POSITIVEINFINITY = 9218868437227405312L;
  
  public void applyPattern(String paramString)
  {
    StringBuffer[] arrayOfStringBuffer = new StringBuffer[2];
    for (int i = 0; i < arrayOfStringBuffer.length; i++) {
      arrayOfStringBuffer[i] = new StringBuffer();
    }
    double[] arrayOfDouble = new double[30];
    String[] arrayOfString = new String[30];
    int j = 0;
    int k = 0;
    double d1 = 0.0D;
    double d2 = NaN.0D;
    int m = 0;
    for (int n = 0; n < paramString.length(); n++)
    {
      char c = paramString.charAt(n);
      if (c == '\'')
      {
        if ((n + 1 < paramString.length()) && (paramString.charAt(n + 1) == c))
        {
          arrayOfStringBuffer[k].append(c);
          n++;
        }
        else
        {
          m = m == 0 ? 1 : 0;
        }
      }
      else if (m != 0)
      {
        arrayOfStringBuffer[k].append(c);
      }
      else if ((c == '<') || (c == '#') || (c == '≤'))
      {
        if (arrayOfStringBuffer[0].length() == 0) {
          throw new IllegalArgumentException();
        }
        try
        {
          String str = arrayOfStringBuffer[0].toString();
          if (str.equals("∞")) {
            d1 = Double.POSITIVE_INFINITY;
          } else if (str.equals("-∞")) {
            d1 = Double.NEGATIVE_INFINITY;
          } else {
            d1 = Double.valueOf(arrayOfStringBuffer[0].toString()).doubleValue();
          }
        }
        catch (Exception localException)
        {
          throw new IllegalArgumentException();
        }
        if ((c == '<') && (d1 != Double.POSITIVE_INFINITY) && (d1 != Double.NEGATIVE_INFINITY)) {
          d1 = nextDouble(d1);
        }
        if (d1 <= d2) {
          throw new IllegalArgumentException();
        }
        arrayOfStringBuffer[0].setLength(0);
        k = 1;
      }
      else if (c == '|')
      {
        if (j == arrayOfDouble.length)
        {
          arrayOfDouble = doubleArraySize(arrayOfDouble);
          arrayOfString = doubleArraySize(arrayOfString);
        }
        arrayOfDouble[j] = d1;
        arrayOfString[j] = arrayOfStringBuffer[1].toString();
        j++;
        d2 = d1;
        arrayOfStringBuffer[1].setLength(0);
        k = 0;
      }
      else
      {
        arrayOfStringBuffer[k].append(c);
      }
    }
    if (k == 1)
    {
      if (j == arrayOfDouble.length)
      {
        arrayOfDouble = doubleArraySize(arrayOfDouble);
        arrayOfString = doubleArraySize(arrayOfString);
      }
      arrayOfDouble[j] = d1;
      arrayOfString[j] = arrayOfStringBuffer[1].toString();
      j++;
    }
    choiceLimits = new double[j];
    System.arraycopy(arrayOfDouble, 0, choiceLimits, 0, j);
    choiceFormats = new String[j];
    System.arraycopy(arrayOfString, 0, choiceFormats, 0, j);
  }
  
  public String toPattern()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; i < choiceLimits.length; i++)
    {
      if (i != 0) {
        localStringBuffer.append('|');
      }
      double d1 = previousDouble(choiceLimits[i]);
      double d2 = Math.abs(Math.IEEEremainder(choiceLimits[i], 1.0D));
      double d3 = Math.abs(Math.IEEEremainder(d1, 1.0D));
      if (d2 < d3)
      {
        localStringBuffer.append("" + choiceLimits[i]);
        localStringBuffer.append('#');
      }
      else
      {
        if (choiceLimits[i] == Double.POSITIVE_INFINITY) {
          localStringBuffer.append("∞");
        } else if (choiceLimits[i] == Double.NEGATIVE_INFINITY) {
          localStringBuffer.append("-∞");
        } else {
          localStringBuffer.append("" + d1);
        }
        localStringBuffer.append('<');
      }
      String str = choiceFormats[i];
      int j = (str.indexOf('<') >= 0) || (str.indexOf('#') >= 0) || (str.indexOf('≤') >= 0) || (str.indexOf('|') >= 0) ? 1 : 0;
      if (j != 0) {
        localStringBuffer.append('\'');
      }
      if (str.indexOf('\'') < 0) {
        localStringBuffer.append(str);
      } else {
        for (int k = 0; k < str.length(); k++)
        {
          char c = str.charAt(k);
          localStringBuffer.append(c);
          if (c == '\'') {
            localStringBuffer.append(c);
          }
        }
      }
      if (j != 0) {
        localStringBuffer.append('\'');
      }
    }
    return localStringBuffer.toString();
  }
  
  public ChoiceFormat(String paramString)
  {
    applyPattern(paramString);
  }
  
  public ChoiceFormat(double[] paramArrayOfDouble, String[] paramArrayOfString)
  {
    setChoices(paramArrayOfDouble, paramArrayOfString);
  }
  
  public void setChoices(double[] paramArrayOfDouble, String[] paramArrayOfString)
  {
    if (paramArrayOfDouble.length != paramArrayOfString.length) {
      throw new IllegalArgumentException("Array and limit arrays must be of the same length.");
    }
    choiceLimits = Arrays.copyOf(paramArrayOfDouble, paramArrayOfDouble.length);
    choiceFormats = ((String[])Arrays.copyOf(paramArrayOfString, paramArrayOfString.length));
  }
  
  public double[] getLimits()
  {
    double[] arrayOfDouble = Arrays.copyOf(choiceLimits, choiceLimits.length);
    return arrayOfDouble;
  }
  
  public Object[] getFormats()
  {
    Object[] arrayOfObject = Arrays.copyOf(choiceFormats, choiceFormats.length);
    return arrayOfObject;
  }
  
  public StringBuffer format(long paramLong, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition)
  {
    return format(paramLong, paramStringBuffer, paramFieldPosition);
  }
  
  public StringBuffer format(double paramDouble, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition)
  {
    for (int i = 0; (i < choiceLimits.length) && (paramDouble >= choiceLimits[i]); i++) {}
    i--;
    if (i < 0) {
      i = 0;
    }
    return paramStringBuffer.append(choiceFormats[i]);
  }
  
  public Number parse(String paramString, ParsePosition paramParsePosition)
  {
    int i = index;
    int j = i;
    double d1 = NaN.0D;
    double d2 = 0.0D;
    for (int k = 0; k < choiceFormats.length; k++)
    {
      String str = choiceFormats[k];
      if (paramString.regionMatches(i, str, 0, str.length()))
      {
        index = (i + str.length());
        d2 = choiceLimits[k];
        if (index > j)
        {
          j = index;
          d1 = d2;
          if (j == paramString.length()) {
            break;
          }
        }
      }
    }
    index = j;
    if (index == i) {
      errorIndex = j;
    }
    return new Double(d1);
  }
  
  public static final double nextDouble(double paramDouble)
  {
    return nextDouble(paramDouble, true);
  }
  
  public static final double previousDouble(double paramDouble)
  {
    return nextDouble(paramDouble, false);
  }
  
  public Object clone()
  {
    ChoiceFormat localChoiceFormat = (ChoiceFormat)super.clone();
    choiceLimits = ((double[])choiceLimits.clone());
    choiceFormats = ((String[])choiceFormats.clone());
    return localChoiceFormat;
  }
  
  public int hashCode()
  {
    int i = choiceLimits.length;
    if (choiceFormats.length > 0) {
      i ^= choiceFormats[(choiceFormats.length - 1)].hashCode();
    }
    return i;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    if (this == paramObject) {
      return true;
    }
    if (getClass() != paramObject.getClass()) {
      return false;
    }
    ChoiceFormat localChoiceFormat = (ChoiceFormat)paramObject;
    return (Arrays.equals(choiceLimits, choiceLimits)) && (Arrays.equals(choiceFormats, choiceFormats));
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    if (choiceLimits.length != choiceFormats.length) {
      throw new InvalidObjectException("limits and format arrays of different length.");
    }
  }
  
  public static double nextDouble(double paramDouble, boolean paramBoolean)
  {
    if (Double.isNaN(paramDouble)) {
      return paramDouble;
    }
    if (paramDouble == 0.0D)
    {
      double d = Double.longBitsToDouble(1L);
      if (paramBoolean) {
        return d;
      }
      return -d;
    }
    long l1 = Double.doubleToLongBits(paramDouble);
    long l2 = l1 & 0x7FFFFFFFFFFFFFFF;
    if (l1 > 0L == paramBoolean)
    {
      if (l2 != 9218868437227405312L) {
        l2 += 1L;
      }
    }
    else {
      l2 -= 1L;
    }
    long l3 = l1 & 0x8000000000000000;
    return Double.longBitsToDouble(l2 | l3);
  }
  
  private static double[] doubleArraySize(double[] paramArrayOfDouble)
  {
    int i = paramArrayOfDouble.length;
    double[] arrayOfDouble = new double[i * 2];
    System.arraycopy(paramArrayOfDouble, 0, arrayOfDouble, 0, i);
    return arrayOfDouble;
  }
  
  private String[] doubleArraySize(String[] paramArrayOfString)
  {
    int i = paramArrayOfString.length;
    String[] arrayOfString = new String[i * 2];
    System.arraycopy(paramArrayOfString, 0, arrayOfString, 0, i);
    return arrayOfString;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\text\ChoiceFormat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */