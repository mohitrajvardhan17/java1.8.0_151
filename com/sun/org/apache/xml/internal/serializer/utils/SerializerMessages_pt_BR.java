package com.sun.org.apache.xml.internal.serializer.utils;

import java.util.ListResourceBundle;

public class SerializerMessages_pt_BR
  extends ListResourceBundle
{
  public SerializerMessages_pt_BR() {}
  
  public Object[][] getContents()
  {
    Object[][] arrayOfObject = { { "BAD_MSGKEY", "A chave de mensagem ''{0}'' não está na classe de mensagem ''{1}''" }, { "BAD_MSGFORMAT", "Houve falha no formato da mensagem ''{0}'' na classe de mensagem ''{1}''." }, { "ER_SERIALIZER_NOT_CONTENTHANDLER", "A classe ''{0}'' do serializador não implementa org.xml.sax.ContentHandler." }, { "ER_RESOURCE_COULD_NOT_FIND", "Não foi possível encontrar o recurso [ {0} ].\n {1}" }, { "ER_RESOURCE_COULD_NOT_LOAD", "O recurso [ {0} ] não foi carregado: {1} \n {2} \t {3}" }, { "ER_BUFFER_SIZE_LESSTHAN_ZERO", "Tamanho do buffer <=0" }, { "ER_INVALID_UTF16_SURROGATE", "Foi detectado um substituto de UTF-16 inválido: {0} ?" }, { "ER_OIERROR", "Erro de E/S" }, { "ER_ILLEGAL_ATTRIBUTE_POSITION", "Não é possível adicionar o atributo {0} depois dos nós filhos ou antes que um elemento seja produzido. O atributo será ignorado." }, { "ER_NAMESPACE_PREFIX", "O namespace do prefixo ''{0}'' não foi declarado." }, { "ER_STRAY_ATTRIBUTE", "Atributo ''{0}'' fora do elemento." }, { "ER_STRAY_NAMESPACE", "Declaração de namespace ''{0}''=''{1}'' fora do elemento." }, { "ER_COULD_NOT_LOAD_RESOURCE", "Não foi possível carregar ''{0}'' (verificar CLASSPATH); usando agora apenas os padrões" }, { "ER_ILLEGAL_CHARACTER", "Tentativa de exibir um caractere de valor integral {0} que não está representado na codificação de saída especificada de {1}." }, { "ER_COULD_NOT_LOAD_METHOD_PROPERTY", "Não foi possível carregar o arquivo de propriedade ''{0}'' para o método de saída ''{1}'' (verificar CLASSPATH)" }, { "ER_INVALID_PORT", "Número de porta inválido" }, { "ER_PORT_WHEN_HOST_NULL", "A porta não pode ser definida quando o host é nulo" }, { "ER_HOST_ADDRESS_NOT_WELLFORMED", "O host não é um endereço correto" }, { "ER_SCHEME_NOT_CONFORMANT", "O esquema não é compatível." }, { "ER_SCHEME_FROM_NULL_STRING", "Não é possível definir o esquema de uma string nula" }, { "ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE", "O caminho contém uma sequência inválida de caracteres de escape" }, { "ER_PATH_INVALID_CHAR", "O caminho contém um caractere inválido: {0}" }, { "ER_FRAG_INVALID_CHAR", "O fragmento contém um caractere inválido" }, { "ER_FRAG_WHEN_PATH_NULL", "O fragmento não pode ser definido quando o caminho é nulo" }, { "ER_FRAG_FOR_GENERIC_URI", "O fragmento só pode ser definido para um URI genérico" }, { "ER_NO_SCHEME_IN_URI", "Nenhum esquema encontrado no URI" }, { "ER_CANNOT_INIT_URI_EMPTY_PARMS", "Não é possível inicializar o URI com parâmetros vazios" }, { "ER_NO_FRAGMENT_STRING_IN_PATH", "O fragmento não pode ser especificado no caminho nem no fragmento" }, { "ER_NO_QUERY_STRING_IN_PATH", "A string de consulta não pode ser especificada no caminho nem na string de consulta" }, { "ER_NO_PORT_IF_NO_HOST", "A porta não pode ser especificada se o host não tiver sido especificado" }, { "ER_NO_USERINFO_IF_NO_HOST", "As informações do usuário não podem ser especificadas se o host não tiver sido especificado" }, { "ER_XML_VERSION_NOT_SUPPORTED", "Advertência: a versão do documento de saída deve ser obrigatoriamente ''{0}''. Esta versão do XML não é suportada. A versão do documento de saída será ''1.0''." }, { "ER_SCHEME_REQUIRED", "O esquema é obrigatório!" }, { "ER_FACTORY_PROPERTY_MISSING", "O objeto Properties especificado para a SerializerFactory não tem uma propriedade ''{0}''." }, { "ER_ENCODING_NOT_SUPPORTED", "Advertência: a codificação ''{0}'' não é suportada pelo Java runtime." } };
    return arrayOfObject;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\serializer\utils\SerializerMessages_pt_BR.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */