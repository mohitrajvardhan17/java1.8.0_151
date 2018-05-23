package com.sun.jmx.snmp;

public abstract interface SnmpUsmKeyHandler
{
  public static final int DES_KEY_SIZE = 16;
  public static final int DES_DELTA_SIZE = 16;
  
  public abstract byte[] password_to_key(String paramString1, String paramString2)
    throws IllegalArgumentException;
  
  public abstract byte[] localizeAuthKey(String paramString, byte[] paramArrayOfByte, SnmpEngineId paramSnmpEngineId)
    throws IllegalArgumentException;
  
  public abstract byte[] localizePrivKey(String paramString, byte[] paramArrayOfByte, SnmpEngineId paramSnmpEngineId, int paramInt)
    throws IllegalArgumentException;
  
  public abstract byte[] calculateAuthDelta(String paramString, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3)
    throws IllegalArgumentException;
  
  public abstract byte[] calculatePrivDelta(String paramString, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt)
    throws IllegalArgumentException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\SnmpUsmKeyHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */