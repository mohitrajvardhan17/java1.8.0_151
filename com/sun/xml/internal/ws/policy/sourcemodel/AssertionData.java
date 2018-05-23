package com.sun.xml.internal.ws.policy.sourcemodel;

import com.sun.xml.internal.ws.policy.PolicyConstants;
import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Text;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.namespace.QName;

public final class AssertionData
  implements Cloneable, Serializable
{
  private static final long serialVersionUID = 4416256070795526315L;
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(AssertionData.class);
  private final QName name;
  private final String value;
  private final Map<QName, String> attributes;
  private ModelNode.Type type;
  private boolean optional;
  private boolean ignorable;
  
  public static AssertionData createAssertionData(QName paramQName)
    throws IllegalArgumentException
  {
    return new AssertionData(paramQName, null, null, ModelNode.Type.ASSERTION, false, false);
  }
  
  public static AssertionData createAssertionParameterData(QName paramQName)
    throws IllegalArgumentException
  {
    return new AssertionData(paramQName, null, null, ModelNode.Type.ASSERTION_PARAMETER_NODE, false, false);
  }
  
  public static AssertionData createAssertionData(QName paramQName, String paramString, Map<QName, String> paramMap, boolean paramBoolean1, boolean paramBoolean2)
    throws IllegalArgumentException
  {
    return new AssertionData(paramQName, paramString, paramMap, ModelNode.Type.ASSERTION, paramBoolean1, paramBoolean2);
  }
  
  public static AssertionData createAssertionParameterData(QName paramQName, String paramString, Map<QName, String> paramMap)
    throws IllegalArgumentException
  {
    return new AssertionData(paramQName, paramString, paramMap, ModelNode.Type.ASSERTION_PARAMETER_NODE, false, false);
  }
  
  AssertionData(QName paramQName, String paramString, Map<QName, String> paramMap, ModelNode.Type paramType, boolean paramBoolean1, boolean paramBoolean2)
    throws IllegalArgumentException
  {
    name = paramQName;
    value = paramString;
    optional = paramBoolean1;
    ignorable = paramBoolean2;
    attributes = new HashMap();
    if ((paramMap != null) && (!paramMap.isEmpty())) {
      attributes.putAll(paramMap);
    }
    setModelNodeType(paramType);
  }
  
  private void setModelNodeType(ModelNode.Type paramType)
    throws IllegalArgumentException
  {
    if ((paramType == ModelNode.Type.ASSERTION) || (paramType == ModelNode.Type.ASSERTION_PARAMETER_NODE)) {
      type = paramType;
    } else {
      throw ((IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0074_CANNOT_CREATE_ASSERTION_BAD_TYPE(paramType, ModelNode.Type.ASSERTION, ModelNode.Type.ASSERTION_PARAMETER_NODE))));
    }
  }
  
  AssertionData(AssertionData paramAssertionData)
  {
    name = name;
    value = value;
    attributes = new HashMap();
    if (!attributes.isEmpty()) {
      attributes.putAll(attributes);
    }
    type = type;
  }
  
  protected AssertionData clone()
    throws CloneNotSupportedException
  {
    return (AssertionData)super.clone();
  }
  
  /* Error */
  public boolean containsAttribute(QName paramQName)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 239	com/sun/xml/internal/ws/policy/sourcemodel/AssertionData:attributes	Ljava/util/Map;
    //   4: dup
    //   5: astore_2
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 239	com/sun/xml/internal/ws/policy/sourcemodel/AssertionData:attributes	Ljava/util/Map;
    //   11: aload_1
    //   12: invokeinterface 274 2 0
    //   17: aload_2
    //   18: monitorexit
    //   19: ireturn
    //   20: astore_3
    //   21: aload_2
    //   22: monitorexit
    //   23: aload_3
    //   24: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	25	0	this	AssertionData
    //   0	25	1	paramQName	QName
    //   5	17	2	Ljava/lang/Object;	Object
    //   20	4	3	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	19	20	finally
    //   20	23	20	finally
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof AssertionData)) {
      return false;
    }
    boolean bool = true;
    AssertionData localAssertionData = (AssertionData)paramObject;
    bool = (bool) && (name.equals(name));
    bool = (bool) && (value == null ? value == null : value.equals(value));
    synchronized (attributes)
    {
      bool = (bool) && (attributes.equals(attributes));
    }
    return bool;
  }
  
  /* Error */
  public String getAttributeValue(QName paramQName)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 239	com/sun/xml/internal/ws/policy/sourcemodel/AssertionData:attributes	Ljava/util/Map;
    //   4: dup
    //   5: astore_2
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 239	com/sun/xml/internal/ws/policy/sourcemodel/AssertionData:attributes	Ljava/util/Map;
    //   11: aload_1
    //   12: invokeinterface 278 2 0
    //   17: checkcast 142	java/lang/String
    //   20: aload_2
    //   21: monitorexit
    //   22: areturn
    //   23: astore_3
    //   24: aload_2
    //   25: monitorexit
    //   26: aload_3
    //   27: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	28	0	this	AssertionData
    //   0	28	1	paramQName	QName
    //   5	20	2	Ljava/lang/Object;	Object
    //   23	4	3	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	22	23	finally
    //   23	26	23	finally
  }
  
  /* Error */
  public Map<QName, String> getAttributes()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 239	com/sun/xml/internal/ws/policy/sourcemodel/AssertionData:attributes	Ljava/util/Map;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: new 145	java/util/HashMap
    //   10: dup
    //   11: aload_0
    //   12: getfield 239	com/sun/xml/internal/ws/policy/sourcemodel/AssertionData:attributes	Ljava/util/Map;
    //   15: invokespecial 263	java/util/HashMap:<init>	(Ljava/util/Map;)V
    //   18: aload_1
    //   19: monitorexit
    //   20: areturn
    //   21: astore_2
    //   22: aload_1
    //   23: monitorexit
    //   24: aload_2
    //   25: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	26	0	this	AssertionData
    //   5	18	1	Ljava/lang/Object;	Object
    //   21	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	20	21	finally
    //   21	24	21	finally
  }
  
  /* Error */
  public Set<Map.Entry<QName, String>> getAttributesSet()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 239	com/sun/xml/internal/ws/policy/sourcemodel/AssertionData:attributes	Ljava/util/Map;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: new 146	java/util/HashSet
    //   10: dup
    //   11: aload_0
    //   12: getfield 239	com/sun/xml/internal/ws/policy/sourcemodel/AssertionData:attributes	Ljava/util/Map;
    //   15: invokeinterface 277 1 0
    //   20: invokespecial 264	java/util/HashSet:<init>	(Ljava/util/Collection;)V
    //   23: aload_1
    //   24: monitorexit
    //   25: areturn
    //   26: astore_2
    //   27: aload_1
    //   28: monitorexit
    //   29: aload_2
    //   30: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	31	0	this	AssertionData
    //   5	23	1	Ljava/lang/Object;	Object
    //   26	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	25	26	finally
    //   26	29	26	finally
  }
  
  public QName getName()
  {
    return name;
  }
  
  public String getValue()
  {
    return value;
  }
  
  public int hashCode()
  {
    int i = 17;
    i = 37 * i + name.hashCode();
    i = 37 * i + (value == null ? 0 : value.hashCode());
    synchronized (attributes)
    {
      i = 37 * i + attributes.hashCode();
    }
    return i;
  }
  
  public boolean isPrivateAttributeSet()
  {
    return "private".equals(getAttributeValue(PolicyConstants.VISIBILITY_ATTRIBUTE));
  }
  
  /* Error */
  public String removeAttribute(QName paramQName)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 239	com/sun/xml/internal/ws/policy/sourcemodel/AssertionData:attributes	Ljava/util/Map;
    //   4: dup
    //   5: astore_2
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 239	com/sun/xml/internal/ws/policy/sourcemodel/AssertionData:attributes	Ljava/util/Map;
    //   11: aload_1
    //   12: invokeinterface 279 2 0
    //   17: checkcast 142	java/lang/String
    //   20: aload_2
    //   21: monitorexit
    //   22: areturn
    //   23: astore_3
    //   24: aload_2
    //   25: monitorexit
    //   26: aload_3
    //   27: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	28	0	this	AssertionData
    //   0	28	1	paramQName	QName
    //   5	20	2	Ljava/lang/Object;	Object
    //   23	4	3	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	22	23	finally
    //   23	26	23	finally
  }
  
  public void setAttribute(QName paramQName, String paramString)
  {
    synchronized (attributes)
    {
      attributes.put(paramQName, paramString);
    }
  }
  
  public void setOptionalAttribute(boolean paramBoolean)
  {
    optional = paramBoolean;
  }
  
  public boolean isOptionalAttributeSet()
  {
    return optional;
  }
  
  public void setIgnorableAttribute(boolean paramBoolean)
  {
    ignorable = paramBoolean;
  }
  
  public boolean isIgnorableAttributeSet()
  {
    return ignorable;
  }
  
  public String toString()
  {
    return toString(0, new StringBuffer()).toString();
  }
  
  public StringBuffer toString(int paramInt, StringBuffer paramStringBuffer)
  {
    String str1 = PolicyUtils.Text.createIndent(paramInt);
    String str2 = PolicyUtils.Text.createIndent(paramInt + 1);
    String str3 = PolicyUtils.Text.createIndent(paramInt + 2);
    paramStringBuffer.append(str1);
    if (type == ModelNode.Type.ASSERTION) {
      paramStringBuffer.append("assertion data {");
    } else {
      paramStringBuffer.append("assertion parameter data {");
    }
    paramStringBuffer.append(PolicyUtils.Text.NEW_LINE);
    paramStringBuffer.append(str2).append("namespace = '").append(name.getNamespaceURI()).append('\'').append(PolicyUtils.Text.NEW_LINE);
    paramStringBuffer.append(str2).append("prefix = '").append(name.getPrefix()).append('\'').append(PolicyUtils.Text.NEW_LINE);
    paramStringBuffer.append(str2).append("local name = '").append(name.getLocalPart()).append('\'').append(PolicyUtils.Text.NEW_LINE);
    paramStringBuffer.append(str2).append("value = '").append(value).append('\'').append(PolicyUtils.Text.NEW_LINE);
    paramStringBuffer.append(str2).append("optional = '").append(optional).append('\'').append(PolicyUtils.Text.NEW_LINE);
    paramStringBuffer.append(str2).append("ignorable = '").append(ignorable).append('\'').append(PolicyUtils.Text.NEW_LINE);
    synchronized (attributes)
    {
      if (attributes.isEmpty())
      {
        paramStringBuffer.append(str2).append("no attributes");
      }
      else
      {
        paramStringBuffer.append(str2).append("attributes {").append(PolicyUtils.Text.NEW_LINE);
        Iterator localIterator = attributes.entrySet().iterator();
        while (localIterator.hasNext())
        {
          Map.Entry localEntry = (Map.Entry)localIterator.next();
          QName localQName = (QName)localEntry.getKey();
          paramStringBuffer.append(str3).append("name = '").append(localQName.getNamespaceURI()).append(':').append(localQName.getLocalPart());
          paramStringBuffer.append("', value = '").append((String)localEntry.getValue()).append('\'').append(PolicyUtils.Text.NEW_LINE);
        }
        paramStringBuffer.append(str2).append('}');
      }
    }
    paramStringBuffer.append(PolicyUtils.Text.NEW_LINE).append(str1).append('}');
    return paramStringBuffer;
  }
  
  public ModelNode.Type getNodeType()
  {
    return type;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\sourcemodel\AssertionData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */