package sun.misc;

import java.util.Arrays;

public class FormattedFloatingDecimal
{
  private int decExponentRounded;
  private char[] mantissa;
  private char[] exponent;
  private static final ThreadLocal<Object> threadLocalCharBuffer = new ThreadLocal()
  {
    protected Object initialValue()
    {
      return new char[20];
    }
  };
  
  public static FormattedFloatingDecimal valueOf(double paramDouble, int paramInt, Form paramForm)
  {
    FloatingDecimal.BinaryToASCIIConverter localBinaryToASCIIConverter = FloatingDecimal.getBinaryToASCIIConverter(paramDouble, paramForm == Form.COMPATIBLE);
    return new FormattedFloatingDecimal(paramInt, paramForm, localBinaryToASCIIConverter);
  }
  
  private static char[] getBuffer()
  {
    return (char[])threadLocalCharBuffer.get();
  }
  
  private FormattedFloatingDecimal(int paramInt, Form paramForm, FloatingDecimal.BinaryToASCIIConverter paramBinaryToASCIIConverter)
  {
    if (paramBinaryToASCIIConverter.isExceptional())
    {
      mantissa = paramBinaryToASCIIConverter.toJavaFormatString().toCharArray();
      exponent = null;
      return;
    }
    char[] arrayOfChar = getBuffer();
    int i = paramBinaryToASCIIConverter.getDigits(arrayOfChar);
    int j = paramBinaryToASCIIConverter.getDecimalExponent();
    boolean bool = paramBinaryToASCIIConverter.isNegative();
    int k;
    switch (paramForm)
    {
    case COMPATIBLE: 
      k = j;
      decExponentRounded = k;
      fillCompatible(paramInt, arrayOfChar, i, k, bool);
      break;
    case DECIMAL_FLOAT: 
      k = applyPrecision(j, arrayOfChar, i, j + paramInt);
      fillDecimal(paramInt, arrayOfChar, i, k, bool);
      decExponentRounded = k;
      break;
    case SCIENTIFIC: 
      k = applyPrecision(j, arrayOfChar, i, paramInt + 1);
      fillScientific(paramInt, arrayOfChar, i, k, bool);
      decExponentRounded = k;
      break;
    case GENERAL: 
      k = applyPrecision(j, arrayOfChar, i, paramInt);
      if ((k - 1 < -4) || (k - 1 >= paramInt))
      {
        paramInt--;
        fillScientific(paramInt, arrayOfChar, i, k, bool);
      }
      else
      {
        paramInt -= k;
        fillDecimal(paramInt, arrayOfChar, i, k, bool);
      }
      decExponentRounded = k;
      break;
    default: 
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
      break;
    }
  }
  
  public int getExponentRounded()
  {
    return decExponentRounded - 1;
  }
  
  public char[] getMantissa()
  {
    return mantissa;
  }
  
  public char[] getExponent()
  {
    return exponent;
  }
  
  private static int applyPrecision(int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3)
  {
    if ((paramInt3 >= paramInt2) || (paramInt3 < 0)) {
      return paramInt1;
    }
    if (paramInt3 == 0)
    {
      if (paramArrayOfChar[0] >= '5')
      {
        paramArrayOfChar[0] = '1';
        Arrays.fill(paramArrayOfChar, 1, paramInt2, '0');
        return paramInt1 + 1;
      }
      Arrays.fill(paramArrayOfChar, 0, paramInt2, '0');
      return paramInt1;
    }
    int i = paramArrayOfChar[paramInt3];
    if (i >= 53)
    {
      int j = paramInt3;
      i = paramArrayOfChar[(--j)];
      if (i == 57)
      {
        while ((i == 57) && (j > 0)) {
          i = paramArrayOfChar[(--j)];
        }
        if (i == 57)
        {
          paramArrayOfChar[0] = '1';
          Arrays.fill(paramArrayOfChar, 1, paramInt2, '0');
          return paramInt1 + 1;
        }
      }
      paramArrayOfChar[j] = ((char)(i + 1));
      Arrays.fill(paramArrayOfChar, j + 1, paramInt2, '0');
    }
    else
    {
      Arrays.fill(paramArrayOfChar, paramInt3, paramInt2, '0');
    }
    return paramInt1;
  }
  
  private void fillCompatible(int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    int i = paramBoolean ? 1 : 0;
    int j;
    if ((paramInt3 > 0) && (paramInt3 < 8))
    {
      if (paramInt2 < paramInt3)
      {
        j = paramInt3 - paramInt2;
        mantissa = create(paramBoolean, paramInt2 + j + 2);
        System.arraycopy(paramArrayOfChar, 0, mantissa, i, paramInt2);
        Arrays.fill(mantissa, i + paramInt2, i + paramInt2 + j, '0');
        mantissa[(i + paramInt2 + j)] = '.';
        mantissa[(i + paramInt2 + j + 1)] = '0';
      }
      else if (paramInt3 < paramInt2)
      {
        j = Math.min(paramInt2 - paramInt3, paramInt1);
        mantissa = create(paramBoolean, paramInt3 + 1 + j);
        System.arraycopy(paramArrayOfChar, 0, mantissa, i, paramInt3);
        mantissa[(i + paramInt3)] = '.';
        System.arraycopy(paramArrayOfChar, paramInt3, mantissa, i + paramInt3 + 1, j);
      }
      else
      {
        mantissa = create(paramBoolean, paramInt2 + 2);
        System.arraycopy(paramArrayOfChar, 0, mantissa, i, paramInt2);
        mantissa[(i + paramInt2)] = '.';
        mantissa[(i + paramInt2 + 1)] = '0';
      }
    }
    else
    {
      int k;
      if ((paramInt3 <= 0) && (paramInt3 > -3))
      {
        j = Math.max(0, Math.min(-paramInt3, paramInt1));
        k = Math.max(0, Math.min(paramInt2, paramInt1 + paramInt3));
        if (j > 0)
        {
          mantissa = create(paramBoolean, j + 2 + k);
          mantissa[i] = '0';
          mantissa[(i + 1)] = '.';
          Arrays.fill(mantissa, i + 2, i + 2 + j, '0');
          if (k > 0) {
            System.arraycopy(paramArrayOfChar, 0, mantissa, i + 2 + j, k);
          }
        }
        else if (k > 0)
        {
          mantissa = create(paramBoolean, j + 2 + k);
          mantissa[i] = '0';
          mantissa[(i + 1)] = '.';
          System.arraycopy(paramArrayOfChar, 0, mantissa, i + 2, k);
        }
        else
        {
          mantissa = create(paramBoolean, 1);
          mantissa[i] = '0';
        }
      }
      else
      {
        if (paramInt2 > 1)
        {
          mantissa = create(paramBoolean, paramInt2 + 1);
          mantissa[i] = paramArrayOfChar[0];
          mantissa[(i + 1)] = '.';
          System.arraycopy(paramArrayOfChar, 1, mantissa, i + 2, paramInt2 - 1);
        }
        else
        {
          mantissa = create(paramBoolean, 3);
          mantissa[i] = paramArrayOfChar[0];
          mantissa[(i + 1)] = '.';
          mantissa[(i + 2)] = '0';
        }
        boolean bool = paramInt3 <= 0;
        if (bool)
        {
          j = -paramInt3 + 1;
          k = 1;
        }
        else
        {
          j = paramInt3 - 1;
          k = 0;
        }
        if (j <= 9)
        {
          exponent = create(bool, 1);
          exponent[k] = ((char)(j + 48));
        }
        else if (j <= 99)
        {
          exponent = create(bool, 2);
          exponent[k] = ((char)(j / 10 + 48));
          exponent[(k + 1)] = ((char)(j % 10 + 48));
        }
        else
        {
          exponent = create(bool, 3);
          exponent[k] = ((char)(j / 100 + 48));
          j %= 100;
          exponent[(k + 1)] = ((char)(j / 10 + 48));
          exponent[(k + 2)] = ((char)(j % 10 + 48));
        }
      }
    }
  }
  
  private static char[] create(boolean paramBoolean, int paramInt)
  {
    if (paramBoolean)
    {
      char[] arrayOfChar = new char[paramInt + 1];
      arrayOfChar[0] = '-';
      return arrayOfChar;
    }
    return new char[paramInt];
  }
  
  private void fillDecimal(int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    int i = paramBoolean ? 1 : 0;
    int j;
    if (paramInt3 > 0)
    {
      if (paramInt2 < paramInt3)
      {
        mantissa = create(paramBoolean, paramInt3);
        System.arraycopy(paramArrayOfChar, 0, mantissa, i, paramInt2);
        Arrays.fill(mantissa, i + paramInt2, i + paramInt3, '0');
      }
      else
      {
        j = Math.min(paramInt2 - paramInt3, paramInt1);
        mantissa = create(paramBoolean, paramInt3 + (j > 0 ? j + 1 : 0));
        System.arraycopy(paramArrayOfChar, 0, mantissa, i, paramInt3);
        if (j > 0)
        {
          mantissa[(i + paramInt3)] = '.';
          System.arraycopy(paramArrayOfChar, paramInt3, mantissa, i + paramInt3 + 1, j);
        }
      }
    }
    else if (paramInt3 <= 0)
    {
      j = Math.max(0, Math.min(-paramInt3, paramInt1));
      int k = Math.max(0, Math.min(paramInt2, paramInt1 + paramInt3));
      if (j > 0)
      {
        mantissa = create(paramBoolean, j + 2 + k);
        mantissa[i] = '0';
        mantissa[(i + 1)] = '.';
        Arrays.fill(mantissa, i + 2, i + 2 + j, '0');
        if (k > 0) {
          System.arraycopy(paramArrayOfChar, 0, mantissa, i + 2 + j, k);
        }
      }
      else if (k > 0)
      {
        mantissa = create(paramBoolean, j + 2 + k);
        mantissa[i] = '0';
        mantissa[(i + 1)] = '.';
        System.arraycopy(paramArrayOfChar, 0, mantissa, i + 2, k);
      }
      else
      {
        mantissa = create(paramBoolean, 1);
        mantissa[i] = '0';
      }
    }
  }
  
  private void fillScientific(int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    int i = paramBoolean ? 1 : 0;
    int j = Math.max(0, Math.min(paramInt2 - 1, paramInt1));
    if (j > 0)
    {
      mantissa = create(paramBoolean, j + 2);
      mantissa[i] = paramArrayOfChar[0];
      mantissa[(i + 1)] = '.';
      System.arraycopy(paramArrayOfChar, 1, mantissa, i + 2, j);
    }
    else
    {
      mantissa = create(paramBoolean, 1);
      mantissa[i] = paramArrayOfChar[0];
    }
    int k;
    int m;
    if (paramInt3 <= 0)
    {
      k = 45;
      m = -paramInt3 + 1;
    }
    else
    {
      k = 43;
      m = paramInt3 - 1;
    }
    if (m <= 9)
    {
      exponent = new char[] { k, '0', (char)(m + 48) };
    }
    else if (m <= 99)
    {
      exponent = new char[] { k, (char)(m / 10 + 48), (char)(m % 10 + 48) };
    }
    else
    {
      int n = (char)(m / 100 + 48);
      m %= 100;
      exponent = new char[] { k, n, (char)(m / 10 + 48), (char)(m % 10 + 48) };
    }
  }
  
  public static enum Form
  {
    SCIENTIFIC,  COMPATIBLE,  DECIMAL_FLOAT,  GENERAL;
    
    private Form() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\FormattedFloatingDecimal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */