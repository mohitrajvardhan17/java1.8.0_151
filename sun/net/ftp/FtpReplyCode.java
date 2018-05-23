package sun.net.ftp;

public enum FtpReplyCode
{
  RESTART_MARKER(110),  SERVICE_READY_IN(120),  DATA_CONNECTION_ALREADY_OPEN(125),  FILE_STATUS_OK(150),  COMMAND_OK(200),  NOT_IMPLEMENTED(202),  SYSTEM_STATUS(211),  DIRECTORY_STATUS(212),  FILE_STATUS(213),  HELP_MESSAGE(214),  NAME_SYSTEM_TYPE(215),  SERVICE_READY(220),  SERVICE_CLOSING(221),  DATA_CONNECTION_OPEN(225),  CLOSING_DATA_CONNECTION(226),  ENTERING_PASSIVE_MODE(227),  ENTERING_EXT_PASSIVE_MODE(229),  LOGGED_IN(230),  SECURELY_LOGGED_IN(232),  SECURITY_EXCHANGE_OK(234),  SECURITY_EXCHANGE_COMPLETE(235),  FILE_ACTION_OK(250),  PATHNAME_CREATED(257),  NEED_PASSWORD(331),  NEED_ACCOUNT(332),  NEED_ADAT(334),  NEED_MORE_ADAT(335),  FILE_ACTION_PENDING(350),  SERVICE_NOT_AVAILABLE(421),  CANT_OPEN_DATA_CONNECTION(425),  CONNECTION_CLOSED(426),  NEED_SECURITY_RESOURCE(431),  FILE_ACTION_NOT_TAKEN(450),  ACTION_ABORTED(451),  INSUFFICIENT_STORAGE(452),  COMMAND_UNRECOGNIZED(500),  INVALID_PARAMETER(501),  BAD_SEQUENCE(503),  NOT_IMPLEMENTED_FOR_PARAMETER(504),  NOT_LOGGED_IN(530),  NEED_ACCOUNT_FOR_STORING(532),  PROT_LEVEL_DENIED(533),  REQUEST_DENIED(534),  FAILED_SECURITY_CHECK(535),  UNSUPPORTED_PROT_LEVEL(536),  PROT_LEVEL_NOT_SUPPORTED_BY_SECURITY(537),  FILE_UNAVAILABLE(550),  PAGE_TYPE_UNKNOWN(551),  EXCEEDED_STORAGE(552),  FILE_NAME_NOT_ALLOWED(553),  PROTECTED_REPLY(631),  UNKNOWN_ERROR(999);
  
  private final int value;
  
  private FtpReplyCode(int paramInt)
  {
    value = paramInt;
  }
  
  public int getValue()
  {
    return value;
  }
  
  public boolean isPositivePreliminary()
  {
    return (value >= 100) && (value < 200);
  }
  
  public boolean isPositiveCompletion()
  {
    return (value >= 200) && (value < 300);
  }
  
  public boolean isPositiveIntermediate()
  {
    return (value >= 300) && (value < 400);
  }
  
  public boolean isTransientNegative()
  {
    return (value >= 400) && (value < 500);
  }
  
  public boolean isPermanentNegative()
  {
    return (value >= 500) && (value < 600);
  }
  
  public boolean isProtectedReply()
  {
    return (value >= 600) && (value < 700);
  }
  
  public boolean isSyntax()
  {
    return value / 10 - value / 100 * 10 == 0;
  }
  
  public boolean isInformation()
  {
    return value / 10 - value / 100 * 10 == 1;
  }
  
  public boolean isConnection()
  {
    return value / 10 - value / 100 * 10 == 2;
  }
  
  public boolean isAuthentication()
  {
    return value / 10 - value / 100 * 10 == 3;
  }
  
  public boolean isUnspecified()
  {
    return value / 10 - value / 100 * 10 == 4;
  }
  
  public boolean isFileSystem()
  {
    return value / 10 - value / 100 * 10 == 5;
  }
  
  public static FtpReplyCode find(int paramInt)
  {
    for (FtpReplyCode localFtpReplyCode : ) {
      if (localFtpReplyCode.getValue() == paramInt) {
        return localFtpReplyCode;
      }
    }
    return UNKNOWN_ERROR;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\ftp\FtpReplyCode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */