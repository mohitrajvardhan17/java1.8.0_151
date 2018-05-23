package sun.security.krb5.internal;

public abstract interface SeqNumber
{
  public abstract void randInit();
  
  public abstract void init(int paramInt);
  
  public abstract int current();
  
  public abstract int next();
  
  public abstract int step();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\SeqNumber.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */