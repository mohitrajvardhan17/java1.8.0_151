package com.sun.xml.internal.ws.assembler;

import com.sun.istack.internal.logging.Logger;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.assembler.dev.ClientTubelineAssemblyContext;
import com.sun.xml.internal.ws.assembler.dev.TubeFactory;
import com.sun.xml.internal.ws.assembler.dev.TubelineAssemblyContextUpdater;
import com.sun.xml.internal.ws.resources.TubelineassemblyMessages;
import com.sun.xml.internal.ws.runtime.config.TubeFactoryConfig;

final class TubeCreator
{
  private static final Logger LOGGER = Logger.getLogger(TubeCreator.class);
  private final TubeFactory factory;
  private final String msgDumpPropertyBase;
  
  TubeCreator(TubeFactoryConfig paramTubeFactoryConfig, ClassLoader paramClassLoader)
  {
    String str = paramTubeFactoryConfig.getClassName();
    try
    {
      Class localClass1;
      if (isJDKInternal(str)) {
        localClass1 = Class.forName(str, true, null);
      } else {
        localClass1 = Class.forName(str, true, paramClassLoader);
      }
      if (TubeFactory.class.isAssignableFrom(localClass1))
      {
        Class localClass2 = localClass1;
        factory = ((TubeFactory)localClass2.newInstance());
        msgDumpPropertyBase = (factory.getClass().getName() + ".dump");
      }
      else
      {
        throw new RuntimeException(TubelineassemblyMessages.MASM_0015_CLASS_DOES_NOT_IMPLEMENT_INTERFACE(localClass1.getName(), TubeFactory.class.getName()));
      }
    }
    catch (InstantiationException localInstantiationException)
    {
      throw ((RuntimeException)LOGGER.logSevereException(new RuntimeException(TubelineassemblyMessages.MASM_0016_UNABLE_TO_INSTANTIATE_TUBE_FACTORY(str), localInstantiationException), true));
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw ((RuntimeException)LOGGER.logSevereException(new RuntimeException(TubelineassemblyMessages.MASM_0016_UNABLE_TO_INSTANTIATE_TUBE_FACTORY(str), localIllegalAccessException), true));
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw ((RuntimeException)LOGGER.logSevereException(new RuntimeException(TubelineassemblyMessages.MASM_0017_UNABLE_TO_LOAD_TUBE_FACTORY_CLASS(str), localClassNotFoundException), true));
    }
  }
  
  Tube createTube(DefaultClientTubelineAssemblyContext paramDefaultClientTubelineAssemblyContext)
  {
    return factory.createTube(paramDefaultClientTubelineAssemblyContext);
  }
  
  Tube createTube(DefaultServerTubelineAssemblyContext paramDefaultServerTubelineAssemblyContext)
  {
    return factory.createTube(paramDefaultServerTubelineAssemblyContext);
  }
  
  void updateContext(ClientTubelineAssemblyContext paramClientTubelineAssemblyContext)
  {
    if ((factory instanceof TubelineAssemblyContextUpdater)) {
      ((TubelineAssemblyContextUpdater)factory).prepareContext(paramClientTubelineAssemblyContext);
    }
  }
  
  void updateContext(DefaultServerTubelineAssemblyContext paramDefaultServerTubelineAssemblyContext)
  {
    if ((factory instanceof TubelineAssemblyContextUpdater)) {
      ((TubelineAssemblyContextUpdater)factory).prepareContext(paramDefaultServerTubelineAssemblyContext);
    }
  }
  
  String getMessageDumpPropertyBase()
  {
    return msgDumpPropertyBase;
  }
  
  private boolean isJDKInternal(String paramString)
  {
    return paramString.startsWith("com.sun.xml.internal.ws");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\assembler\TubeCreator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */