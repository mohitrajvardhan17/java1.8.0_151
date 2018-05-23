package java.security.cert;

public enum CRLReason
{
  UNSPECIFIED,  KEY_COMPROMISE,  CA_COMPROMISE,  AFFILIATION_CHANGED,  SUPERSEDED,  CESSATION_OF_OPERATION,  CERTIFICATE_HOLD,  UNUSED,  REMOVE_FROM_CRL,  PRIVILEGE_WITHDRAWN,  AA_COMPROMISE;
  
  private CRLReason() {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\cert\CRLReason.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */