package com.sun.org.apache.xalan.internal.xsltc.trax;

import java.util.Properties;

public final class OutputSettings
{
  private String _cdata_section_elements = null;
  private String _doctype_public = null;
  private String _encoding = null;
  private String _indent = null;
  private String _media_type = null;
  private String _method = null;
  private String _omit_xml_declaration = null;
  private String _standalone = null;
  private String _version = null;
  
  public OutputSettings() {}
  
  public Properties getProperties()
  {
    Properties localProperties = new Properties();
    return localProperties;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\trax\OutputSettings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */