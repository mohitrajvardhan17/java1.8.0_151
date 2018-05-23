package java.security.cert;

public enum PKIXReason
  implements CertPathValidatorException.Reason
{
  NAME_CHAINING,  INVALID_KEY_USAGE,  INVALID_POLICY,  NO_TRUST_ANCHOR,  UNRECOGNIZED_CRIT_EXT,  NOT_CA_CERT,  PATH_TOO_LONG,  INVALID_NAME;
  
  private PKIXReason() {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\cert\PKIXReason.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */