package sun.management;

class DiagnosticCommandArgumentInfo
{
  private final String name;
  private final String description;
  private final String type;
  private final String defaultValue;
  private final boolean mandatory;
  private final boolean option;
  private final boolean multiple;
  private final int position;
  
  String getName()
  {
    return name;
  }
  
  String getDescription()
  {
    return description;
  }
  
  String getType()
  {
    return type;
  }
  
  String getDefault()
  {
    return defaultValue;
  }
  
  boolean isMandatory()
  {
    return mandatory;
  }
  
  boolean isOption()
  {
    return option;
  }
  
  boolean isMultiple()
  {
    return multiple;
  }
  
  int getPosition()
  {
    return position;
  }
  
  DiagnosticCommandArgumentInfo(String paramString1, String paramString2, String paramString3, String paramString4, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt)
  {
    name = paramString1;
    description = paramString2;
    type = paramString3;
    defaultValue = paramString4;
    mandatory = paramBoolean1;
    option = paramBoolean2;
    multiple = paramBoolean3;
    position = paramInt;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\DiagnosticCommandArgumentInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */