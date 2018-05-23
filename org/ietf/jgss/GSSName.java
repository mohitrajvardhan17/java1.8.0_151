package org.ietf.jgss;

public abstract interface GSSName
{
  public static final Oid NT_HOSTBASED_SERVICE = Oid.getInstance("1.2.840.113554.1.2.1.4");
  public static final Oid NT_USER_NAME = Oid.getInstance("1.2.840.113554.1.2.1.1");
  public static final Oid NT_MACHINE_UID_NAME = Oid.getInstance("1.2.840.113554.1.2.1.2");
  public static final Oid NT_STRING_UID_NAME = Oid.getInstance("1.2.840.113554.1.2.1.3");
  public static final Oid NT_ANONYMOUS = Oid.getInstance("1.3.6.1.5.6.3");
  public static final Oid NT_EXPORT_NAME = Oid.getInstance("1.3.6.1.5.6.4");
  
  public abstract boolean equals(GSSName paramGSSName)
    throws GSSException;
  
  public abstract boolean equals(Object paramObject);
  
  public abstract int hashCode();
  
  public abstract GSSName canonicalize(Oid paramOid)
    throws GSSException;
  
  public abstract byte[] export()
    throws GSSException;
  
  public abstract String toString();
  
  public abstract Oid getStringNameType()
    throws GSSException;
  
  public abstract boolean isAnonymous();
  
  public abstract boolean isMN();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\ietf\jgss\GSSName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */