package javax.transaction.xa;

public abstract interface Xid
{
  public static final int MAXGTRIDSIZE = 64;
  public static final int MAXBQUALSIZE = 64;
  
  public abstract int getFormatId();
  
  public abstract byte[] getGlobalTransactionId();
  
  public abstract byte[] getBranchQualifier();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\transaction\xa\Xid.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */