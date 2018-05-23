package sun.management;

import java.util.List;

class DiagnosticCommandInfo
{
  private final String name;
  private final String description;
  private final String impact;
  private final String permissionClass;
  private final String permissionName;
  private final String permissionAction;
  private final boolean enabled;
  private final List<DiagnosticCommandArgumentInfo> arguments;
  
  String getName()
  {
    return name;
  }
  
  String getDescription()
  {
    return description;
  }
  
  String getImpact()
  {
    return impact;
  }
  
  String getPermissionClass()
  {
    return permissionClass;
  }
  
  String getPermissionName()
  {
    return permissionName;
  }
  
  String getPermissionAction()
  {
    return permissionAction;
  }
  
  boolean isEnabled()
  {
    return enabled;
  }
  
  List<DiagnosticCommandArgumentInfo> getArgumentsInfo()
  {
    return arguments;
  }
  
  DiagnosticCommandInfo(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, boolean paramBoolean, List<DiagnosticCommandArgumentInfo> paramList)
  {
    name = paramString1;
    description = paramString2;
    impact = paramString3;
    permissionClass = paramString4;
    permissionName = paramString5;
    permissionAction = paramString6;
    enabled = paramBoolean;
    arguments = paramList;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\DiagnosticCommandInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */