package javax.security.sasl;

public abstract interface SaslClient
{
  public abstract String getMechanismName();
  
  public abstract boolean hasInitialResponse();
  
  public abstract byte[] evaluateChallenge(byte[] paramArrayOfByte)
    throws SaslException;
  
  public abstract boolean isComplete();
  
  public abstract byte[] unwrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SaslException;
  
  public abstract byte[] wrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SaslException;
  
  public abstract Object getNegotiatedProperty(String paramString);
  
  public abstract void dispose()
    throws SaslException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\security\sasl\SaslClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */