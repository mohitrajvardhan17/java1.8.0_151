package java.math;

class SignedMutableBigInteger
  extends MutableBigInteger
{
  int sign = 1;
  
  SignedMutableBigInteger() {}
  
  SignedMutableBigInteger(int paramInt)
  {
    super(paramInt);
  }
  
  SignedMutableBigInteger(MutableBigInteger paramMutableBigInteger)
  {
    super(paramMutableBigInteger);
  }
  
  void signedAdd(SignedMutableBigInteger paramSignedMutableBigInteger)
  {
    if (sign == sign) {
      add(paramSignedMutableBigInteger);
    } else {
      sign *= subtract(paramSignedMutableBigInteger);
    }
  }
  
  void signedAdd(MutableBigInteger paramMutableBigInteger)
  {
    if (sign == 1) {
      add(paramMutableBigInteger);
    } else {
      sign *= subtract(paramMutableBigInteger);
    }
  }
  
  void signedSubtract(SignedMutableBigInteger paramSignedMutableBigInteger)
  {
    if (sign == sign) {
      sign *= subtract(paramSignedMutableBigInteger);
    } else {
      add(paramSignedMutableBigInteger);
    }
  }
  
  void signedSubtract(MutableBigInteger paramMutableBigInteger)
  {
    if (sign == 1) {
      sign *= subtract(paramMutableBigInteger);
    } else {
      add(paramMutableBigInteger);
    }
    if (intLen == 0) {
      sign = 1;
    }
  }
  
  public String toString()
  {
    return toBigInteger(sign).toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\math\SignedMutableBigInteger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */