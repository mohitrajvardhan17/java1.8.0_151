package com.sun.corba.se.impl.dynamicany;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
import org.omg.DynamicAny.DynFixed;

public class DynFixedImpl
  extends DynAnyBasicImpl
  implements DynFixed
{
  private DynFixedImpl()
  {
    this(null, (Any)null, false);
  }
  
  protected DynFixedImpl(ORB paramORB, Any paramAny, boolean paramBoolean)
  {
    super(paramORB, paramAny, paramBoolean);
  }
  
  protected DynFixedImpl(ORB paramORB, TypeCode paramTypeCode)
  {
    super(paramORB, paramTypeCode);
    index = -1;
  }
  
  public String get_value()
  {
    if (status == 2) {
      throw wrapper.dynAnyDestroyed();
    }
    return any.extract_fixed().toString();
  }
  
  public boolean set_value(String paramString)
    throws TypeMismatch, InvalidValue
  {
    if (status == 2) {
      throw wrapper.dynAnyDestroyed();
    }
    int i = 0;
    int j = 0;
    boolean bool = true;
    try
    {
      i = any.type().fixed_digits();
      j = any.type().fixed_scale();
    }
    catch (BadKind localBadKind) {}
    String str1 = paramString.trim();
    if (str1.length() == 0) {
      throw new TypeMismatch();
    }
    String str2 = "";
    if (str1.charAt(0) == '-')
    {
      str2 = "-";
      str1 = str1.substring(1);
    }
    else if (str1.charAt(0) == '+')
    {
      str2 = "+";
      str1 = str1.substring(1);
    }
    int k = str1.indexOf('d');
    if (k == -1) {
      k = str1.indexOf('D');
    }
    if (k != -1) {
      str1 = str1.substring(0, k);
    }
    if (str1.length() == 0) {
      throw new TypeMismatch();
    }
    int i1 = str1.indexOf('.');
    String str3;
    String str4;
    int m;
    int n;
    if (i1 == -1)
    {
      str3 = str1;
      str4 = null;
      m = 0;
      n = str3.length();
    }
    else if (i1 == 0)
    {
      str3 = null;
      str4 = str1;
      m = str4.length();
      n = m;
    }
    else
    {
      str3 = str1.substring(0, i1);
      str4 = str1.substring(i1 + 1);
      m = str4.length();
      n = str3.length() + m;
    }
    if (n > i)
    {
      bool = false;
      if (str3.length() < i) {
        str4 = str4.substring(0, i - str3.length());
      } else if (str3.length() == i) {
        str4 = null;
      } else {
        throw new InvalidValue();
      }
    }
    BigDecimal localBigDecimal;
    try
    {
      new BigInteger(str3);
      if (str4 == null)
      {
        localBigDecimal = new BigDecimal(str2 + str3);
      }
      else
      {
        new BigInteger(str4);
        localBigDecimal = new BigDecimal(str2 + str3 + "." + str4);
      }
    }
    catch (NumberFormatException localNumberFormatException)
    {
      throw new TypeMismatch();
    }
    any.insert_fixed(localBigDecimal, any.type());
    return bool;
  }
  
  public String toString()
  {
    int i = 0;
    int j = 0;
    try
    {
      i = any.type().fixed_digits();
      j = any.type().fixed_scale();
    }
    catch (BadKind localBadKind) {}
    return "DynFixed with value=" + get_value() + ", digits=" + i + ", scale=" + j;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\dynamicany\DynFixedImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */