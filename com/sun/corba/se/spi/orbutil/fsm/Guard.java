package com.sun.corba.se.spi.orbutil.fsm;

public abstract interface Guard
{
  public abstract Result evaluate(FSM paramFSM, Input paramInput);
  
  public static final class Complement
    extends GuardBase
  {
    private Guard guard;
    
    public Complement(GuardBase paramGuardBase)
    {
      super();
      guard = paramGuardBase;
    }
    
    public Guard.Result evaluate(FSM paramFSM, Input paramInput)
    {
      return guard.evaluate(paramFSM, paramInput).complement();
    }
  }
  
  public static final class Result
  {
    private String name;
    public static final Result ENABLED = new Result("ENABLED");
    public static final Result DISABLED = new Result("DISABLED");
    public static final Result DEFERED = new Result("DEFERED");
    
    private Result(String paramString)
    {
      name = paramString;
    }
    
    public static Result convert(boolean paramBoolean)
    {
      return paramBoolean ? ENABLED : DISABLED;
    }
    
    public Result complement()
    {
      if (this == ENABLED) {
        return DISABLED;
      }
      if (this == DISABLED) {
        return ENABLED;
      }
      return DEFERED;
    }
    
    public String toString()
    {
      return "Guard.Result[" + name + "]";
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\orbutil\fsm\Guard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */