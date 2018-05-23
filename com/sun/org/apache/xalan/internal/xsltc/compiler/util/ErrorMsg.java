package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Stylesheet;
import com.sun.org.apache.xalan.internal.xsltc.compiler.SyntaxTreeNode;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public final class ErrorMsg
{
  private String _code;
  private int _line;
  private String _message = null;
  private String _url = null;
  Object[] _params = null;
  private boolean _isWarningError;
  Throwable _cause;
  public static final String MULTIPLE_STYLESHEET_ERR = "MULTIPLE_STYLESHEET_ERR";
  public static final String TEMPLATE_REDEF_ERR = "TEMPLATE_REDEF_ERR";
  public static final String TEMPLATE_UNDEF_ERR = "TEMPLATE_UNDEF_ERR";
  public static final String VARIABLE_REDEF_ERR = "VARIABLE_REDEF_ERR";
  public static final String VARIABLE_UNDEF_ERR = "VARIABLE_UNDEF_ERR";
  public static final String CLASS_NOT_FOUND_ERR = "CLASS_NOT_FOUND_ERR";
  public static final String METHOD_NOT_FOUND_ERR = "METHOD_NOT_FOUND_ERR";
  public static final String ARGUMENT_CONVERSION_ERR = "ARGUMENT_CONVERSION_ERR";
  public static final String FILE_NOT_FOUND_ERR = "FILE_NOT_FOUND_ERR";
  public static final String INVALID_URI_ERR = "INVALID_URI_ERR";
  public static final String FILE_ACCESS_ERR = "FILE_ACCESS_ERR";
  public static final String MISSING_ROOT_ERR = "MISSING_ROOT_ERR";
  public static final String NAMESPACE_UNDEF_ERR = "NAMESPACE_UNDEF_ERR";
  public static final String FUNCTION_RESOLVE_ERR = "FUNCTION_RESOLVE_ERR";
  public static final String NEED_LITERAL_ERR = "NEED_LITERAL_ERR";
  public static final String XPATH_PARSER_ERR = "XPATH_PARSER_ERR";
  public static final String REQUIRED_ATTR_ERR = "REQUIRED_ATTR_ERR";
  public static final String ILLEGAL_CHAR_ERR = "ILLEGAL_CHAR_ERR";
  public static final String ILLEGAL_PI_ERR = "ILLEGAL_PI_ERR";
  public static final String STRAY_ATTRIBUTE_ERR = "STRAY_ATTRIBUTE_ERR";
  public static final String ILLEGAL_ATTRIBUTE_ERR = "ILLEGAL_ATTRIBUTE_ERR";
  public static final String CIRCULAR_INCLUDE_ERR = "CIRCULAR_INCLUDE_ERR";
  public static final String RESULT_TREE_SORT_ERR = "RESULT_TREE_SORT_ERR";
  public static final String SYMBOLS_REDEF_ERR = "SYMBOLS_REDEF_ERR";
  public static final String XSL_VERSION_ERR = "XSL_VERSION_ERR";
  public static final String CIRCULAR_VARIABLE_ERR = "CIRCULAR_VARIABLE_ERR";
  public static final String ILLEGAL_BINARY_OP_ERR = "ILLEGAL_BINARY_OP_ERR";
  public static final String ILLEGAL_ARG_ERR = "ILLEGAL_ARG_ERR";
  public static final String DOCUMENT_ARG_ERR = "DOCUMENT_ARG_ERR";
  public static final String MISSING_WHEN_ERR = "MISSING_WHEN_ERR";
  public static final String MULTIPLE_OTHERWISE_ERR = "MULTIPLE_OTHERWISE_ERR";
  public static final String STRAY_OTHERWISE_ERR = "STRAY_OTHERWISE_ERR";
  public static final String STRAY_WHEN_ERR = "STRAY_WHEN_ERR";
  public static final String WHEN_ELEMENT_ERR = "WHEN_ELEMENT_ERR";
  public static final String UNNAMED_ATTRIBSET_ERR = "UNNAMED_ATTRIBSET_ERR";
  public static final String ILLEGAL_CHILD_ERR = "ILLEGAL_CHILD_ERR";
  public static final String ILLEGAL_ELEM_NAME_ERR = "ILLEGAL_ELEM_NAME_ERR";
  public static final String ILLEGAL_ATTR_NAME_ERR = "ILLEGAL_ATTR_NAME_ERR";
  public static final String ILLEGAL_TEXT_NODE_ERR = "ILLEGAL_TEXT_NODE_ERR";
  public static final String SAX_PARSER_CONFIG_ERR = "SAX_PARSER_CONFIG_ERR";
  public static final String INTERNAL_ERR = "INTERNAL_ERR";
  public static final String UNSUPPORTED_XSL_ERR = "UNSUPPORTED_XSL_ERR";
  public static final String UNSUPPORTED_EXT_ERR = "UNSUPPORTED_EXT_ERR";
  public static final String MISSING_XSLT_URI_ERR = "MISSING_XSLT_URI_ERR";
  public static final String MISSING_XSLT_TARGET_ERR = "MISSING_XSLT_TARGET_ERR";
  public static final String ACCESSING_XSLT_TARGET_ERR = "ACCESSING_XSLT_TARGET_ERR";
  public static final String NOT_IMPLEMENTED_ERR = "NOT_IMPLEMENTED_ERR";
  public static final String NOT_STYLESHEET_ERR = "NOT_STYLESHEET_ERR";
  public static final String ELEMENT_PARSE_ERR = "ELEMENT_PARSE_ERR";
  public static final String KEY_USE_ATTR_ERR = "KEY_USE_ATTR_ERR";
  public static final String OUTPUT_VERSION_ERR = "OUTPUT_VERSION_ERR";
  public static final String ILLEGAL_RELAT_OP_ERR = "ILLEGAL_RELAT_OP_ERR";
  public static final String ATTRIBSET_UNDEF_ERR = "ATTRIBSET_UNDEF_ERR";
  public static final String ATTR_VAL_TEMPLATE_ERR = "ATTR_VAL_TEMPLATE_ERR";
  public static final String UNKNOWN_SIG_TYPE_ERR = "UNKNOWN_SIG_TYPE_ERR";
  public static final String DATA_CONVERSION_ERR = "DATA_CONVERSION_ERR";
  public static final String NO_TRANSLET_CLASS_ERR = "NO_TRANSLET_CLASS_ERR";
  public static final String NO_MAIN_TRANSLET_ERR = "NO_MAIN_TRANSLET_ERR";
  public static final String TRANSLET_CLASS_ERR = "TRANSLET_CLASS_ERR";
  public static final String TRANSLET_OBJECT_ERR = "TRANSLET_OBJECT_ERR";
  public static final String ERROR_LISTENER_NULL_ERR = "ERROR_LISTENER_NULL_ERR";
  public static final String JAXP_UNKNOWN_SOURCE_ERR = "JAXP_UNKNOWN_SOURCE_ERR";
  public static final String JAXP_NO_SOURCE_ERR = "JAXP_NO_SOURCE_ERR";
  public static final String JAXP_COMPILE_ERR = "JAXP_COMPILE_ERR";
  public static final String JAXP_INVALID_ATTR_ERR = "JAXP_INVALID_ATTR_ERR";
  public static final String JAXP_INVALID_ATTR_VALUE_ERR = "JAXP_INVALID_ATTR_VALUE_ERR";
  public static final String JAXP_SET_RESULT_ERR = "JAXP_SET_RESULT_ERR";
  public static final String JAXP_NO_TRANSLET_ERR = "JAXP_NO_TRANSLET_ERR";
  public static final String JAXP_NO_HANDLER_ERR = "JAXP_NO_HANDLER_ERR";
  public static final String JAXP_NO_RESULT_ERR = "JAXP_NO_RESULT_ERR";
  public static final String JAXP_UNKNOWN_PROP_ERR = "JAXP_UNKNOWN_PROP_ERR";
  public static final String SAX2DOM_ADAPTER_ERR = "SAX2DOM_ADAPTER_ERR";
  public static final String XSLTC_SOURCE_ERR = "XSLTC_SOURCE_ERR";
  public static final String ER_RESULT_NULL = "ER_RESULT_NULL";
  public static final String JAXP_INVALID_SET_PARAM_VALUE = "JAXP_INVALID_SET_PARAM_VALUE";
  public static final String JAXP_SET_FEATURE_NULL_NAME = "JAXP_SET_FEATURE_NULL_NAME";
  public static final String JAXP_GET_FEATURE_NULL_NAME = "JAXP_GET_FEATURE_NULL_NAME";
  public static final String JAXP_UNSUPPORTED_FEATURE = "JAXP_UNSUPPORTED_FEATURE";
  public static final String JAXP_SECUREPROCESSING_FEATURE = "JAXP_SECUREPROCESSING_FEATURE";
  public static final String COMPILE_STDIN_ERR = "COMPILE_STDIN_ERR";
  public static final String COMPILE_USAGE_STR = "COMPILE_USAGE_STR";
  public static final String TRANSFORM_USAGE_STR = "TRANSFORM_USAGE_STR";
  public static final String STRAY_SORT_ERR = "STRAY_SORT_ERR";
  public static final String UNSUPPORTED_ENCODING = "UNSUPPORTED_ENCODING";
  public static final String SYNTAX_ERR = "SYNTAX_ERR";
  public static final String CONSTRUCTOR_NOT_FOUND = "CONSTRUCTOR_NOT_FOUND";
  public static final String NO_JAVA_FUNCT_THIS_REF = "NO_JAVA_FUNCT_THIS_REF";
  public static final String TYPE_CHECK_ERR = "TYPE_CHECK_ERR";
  public static final String TYPE_CHECK_UNK_LOC_ERR = "TYPE_CHECK_UNK_LOC_ERR";
  public static final String ILLEGAL_CMDLINE_OPTION_ERR = "ILLEGAL_CMDLINE_OPTION_ERR";
  public static final String CMDLINE_OPT_MISSING_ARG_ERR = "CMDLINE_OPT_MISSING_ARG_ERR";
  public static final String WARNING_PLUS_WRAPPED_MSG = "WARNING_PLUS_WRAPPED_MSG";
  public static final String WARNING_MSG = "WARNING_MSG";
  public static final String FATAL_ERR_PLUS_WRAPPED_MSG = "FATAL_ERR_PLUS_WRAPPED_MSG";
  public static final String FATAL_ERR_MSG = "FATAL_ERR_MSG";
  public static final String ERROR_PLUS_WRAPPED_MSG = "ERROR_PLUS_WRAPPED_MSG";
  public static final String ERROR_MSG = "ERROR_MSG";
  public static final String TRANSFORM_WITH_TRANSLET_STR = "TRANSFORM_WITH_TRANSLET_STR";
  public static final String TRANSFORM_WITH_JAR_STR = "TRANSFORM_WITH_JAR_STR";
  public static final String COULD_NOT_CREATE_TRANS_FACT = "COULD_NOT_CREATE_TRANS_FACT";
  public static final String TRANSLET_NAME_JAVA_CONFLICT = "TRANSLET_NAME_JAVA_CONFLICT";
  public static final String INVALID_QNAME_ERR = "INVALID_QNAME_ERR";
  public static final String INVALID_NCNAME_ERR = "INVALID_NCNAME_ERR";
  public static final String INVALID_METHOD_IN_OUTPUT = "INVALID_METHOD_IN_OUTPUT";
  public static final String OUTLINE_ERR_TRY_CATCH = "OUTLINE_ERR_TRY_CATCH";
  public static final String OUTLINE_ERR_UNBALANCED_MARKERS = "OUTLINE_ERR_UNBALANCED_MARKERS";
  public static final String OUTLINE_ERR_DELETED_TARGET = "OUTLINE_ERR_DELETED_TARGET";
  public static final String OUTLINE_ERR_METHOD_TOO_BIG = "OUTLINE_ERR_METHOD_TOO_BIG";
  public static final String DESERIALIZE_TRANSLET_ERR = "DESERIALIZE_TEMPLATES_ERR";
  private static ResourceBundle _bundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMessages", Locale.getDefault());
  public static final String ERROR_MESSAGES_KEY = "ERROR_MESSAGES_KEY";
  public static final String COMPILER_ERROR_KEY = "COMPILER_ERROR_KEY";
  public static final String COMPILER_WARNING_KEY = "COMPILER_WARNING_KEY";
  public static final String RUNTIME_ERROR_KEY = "RUNTIME_ERROR_KEY";
  
  public ErrorMsg(String paramString)
  {
    _code = paramString;
    _line = 0;
  }
  
  public ErrorMsg(String paramString, Throwable paramThrowable)
  {
    _code = paramString;
    _message = paramThrowable.getMessage();
    _line = 0;
    _cause = paramThrowable;
  }
  
  public ErrorMsg(String paramString, int paramInt)
  {
    _code = null;
    _message = paramString;
    _line = paramInt;
  }
  
  public ErrorMsg(String paramString, int paramInt, Object paramObject)
  {
    _code = paramString;
    _line = paramInt;
    _params = new Object[] { paramObject };
  }
  
  public ErrorMsg(String paramString, Object paramObject)
  {
    this(paramString);
    _params = new Object[1];
    _params[0] = paramObject;
  }
  
  public ErrorMsg(String paramString, Object paramObject1, Object paramObject2)
  {
    this(paramString);
    _params = new Object[2];
    _params[0] = paramObject1;
    _params[1] = paramObject2;
  }
  
  public ErrorMsg(String paramString, SyntaxTreeNode paramSyntaxTreeNode)
  {
    _code = paramString;
    _url = getFileName(paramSyntaxTreeNode);
    _line = paramSyntaxTreeNode.getLineNumber();
  }
  
  public ErrorMsg(String paramString, Object paramObject, SyntaxTreeNode paramSyntaxTreeNode)
  {
    _code = paramString;
    _url = getFileName(paramSyntaxTreeNode);
    _line = paramSyntaxTreeNode.getLineNumber();
    _params = new Object[1];
    _params[0] = paramObject;
  }
  
  public ErrorMsg(String paramString, Object paramObject1, Object paramObject2, SyntaxTreeNode paramSyntaxTreeNode)
  {
    _code = paramString;
    _url = getFileName(paramSyntaxTreeNode);
    _line = paramSyntaxTreeNode.getLineNumber();
    _params = new Object[2];
    _params[0] = paramObject1;
    _params[1] = paramObject2;
  }
  
  public Throwable getCause()
  {
    return _cause;
  }
  
  private String getFileName(SyntaxTreeNode paramSyntaxTreeNode)
  {
    Stylesheet localStylesheet = paramSyntaxTreeNode.getStylesheet();
    if (localStylesheet != null) {
      return localStylesheet.getSystemId();
    }
    return null;
  }
  
  private String formatLine()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    if (_url != null)
    {
      localStringBuffer.append(_url);
      localStringBuffer.append(": ");
    }
    if (_line > 0)
    {
      localStringBuffer.append("line ");
      localStringBuffer.append(Integer.toString(_line));
      localStringBuffer.append(": ");
    }
    return localStringBuffer.toString();
  }
  
  public String toString()
  {
    String str = _params == null ? _message : null != _code ? getErrorMessage() : MessageFormat.format(getErrorMessage(), _params);
    return formatLine() + str;
  }
  
  public String toString(Object paramObject)
  {
    Object[] arrayOfObject = new Object[1];
    arrayOfObject[0] = paramObject.toString();
    String str = MessageFormat.format(getErrorMessage(), arrayOfObject);
    return formatLine() + str;
  }
  
  public String toString(Object paramObject1, Object paramObject2)
  {
    Object[] arrayOfObject = new Object[2];
    arrayOfObject[0] = paramObject1.toString();
    arrayOfObject[1] = paramObject2.toString();
    String str = MessageFormat.format(getErrorMessage(), arrayOfObject);
    return formatLine() + str;
  }
  
  private String getErrorMessage()
  {
    return _bundle.getString(_code);
  }
  
  public void setWarningError(boolean paramBoolean)
  {
    _isWarningError = paramBoolean;
  }
  
  public boolean isWarningError()
  {
    return _isWarningError;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\util\ErrorMsg.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */