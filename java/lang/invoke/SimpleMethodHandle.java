package java.lang.invoke;

final class SimpleMethodHandle
  extends BoundMethodHandle
{
  static final BoundMethodHandle.SpeciesData SPECIES_DATA = BoundMethodHandle.SpeciesData.EMPTY;
  
  private SimpleMethodHandle(MethodType paramMethodType, LambdaForm paramLambdaForm)
  {
    super(paramMethodType, paramLambdaForm);
  }
  
  static BoundMethodHandle make(MethodType paramMethodType, LambdaForm paramLambdaForm)
  {
    return new SimpleMethodHandle(paramMethodType, paramLambdaForm);
  }
  
  public BoundMethodHandle.SpeciesData speciesData()
  {
    return SPECIES_DATA;
  }
  
  BoundMethodHandle copyWith(MethodType paramMethodType, LambdaForm paramLambdaForm)
  {
    return make(paramMethodType, paramLambdaForm);
  }
  
  String internalProperties()
  {
    return "\n& Class=" + getClass().getSimpleName();
  }
  
  public int fieldCount()
  {
    return 0;
  }
  
  final BoundMethodHandle copyWithExtendL(MethodType paramMethodType, LambdaForm paramLambdaForm, Object paramObject)
  {
    return BoundMethodHandle.bindSingle(paramMethodType, paramLambdaForm, paramObject);
  }
  
  final BoundMethodHandle copyWithExtendI(MethodType paramMethodType, LambdaForm paramLambdaForm, int paramInt)
  {
    try
    {
      return SPECIES_DATA.extendWith(LambdaForm.BasicType.I_TYPE).constructor().invokeBasic(paramMethodType, paramLambdaForm, paramInt);
    }
    catch (Throwable localThrowable)
    {
      throw MethodHandleStatics.uncaughtException(localThrowable);
    }
  }
  
  final BoundMethodHandle copyWithExtendJ(MethodType paramMethodType, LambdaForm paramLambdaForm, long paramLong)
  {
    try
    {
      return SPECIES_DATA.extendWith(LambdaForm.BasicType.J_TYPE).constructor().invokeBasic(paramMethodType, paramLambdaForm, paramLong);
    }
    catch (Throwable localThrowable)
    {
      throw MethodHandleStatics.uncaughtException(localThrowable);
    }
  }
  
  final BoundMethodHandle copyWithExtendF(MethodType paramMethodType, LambdaForm paramLambdaForm, float paramFloat)
  {
    try
    {
      return SPECIES_DATA.extendWith(LambdaForm.BasicType.F_TYPE).constructor().invokeBasic(paramMethodType, paramLambdaForm, paramFloat);
    }
    catch (Throwable localThrowable)
    {
      throw MethodHandleStatics.uncaughtException(localThrowable);
    }
  }
  
  final BoundMethodHandle copyWithExtendD(MethodType paramMethodType, LambdaForm paramLambdaForm, double paramDouble)
  {
    try
    {
      return SPECIES_DATA.extendWith(LambdaForm.BasicType.D_TYPE).constructor().invokeBasic(paramMethodType, paramLambdaForm, paramDouble);
    }
    catch (Throwable localThrowable)
    {
      throw MethodHandleStatics.uncaughtException(localThrowable);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\invoke\SimpleMethodHandle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */