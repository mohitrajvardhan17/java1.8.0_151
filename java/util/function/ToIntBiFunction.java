package java.util.function;

@FunctionalInterface
public abstract interface ToIntBiFunction<T, U>
{
  public abstract int applyAsInt(T paramT, U paramU);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\function\ToIntBiFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */