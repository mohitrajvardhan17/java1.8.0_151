package sun.tools.jar.resources;

import java.util.ListResourceBundle;

public final class jar_pt_BR
  extends ListResourceBundle
{
  public jar_pt_BR() {}
  
  protected final Object[][] getContents()
  {
    return new Object[][] { { "error.bad.cflag", "flag 'c' requer que os arquivos de manifesto ou entrada sejam especificados!" }, { "error.bad.eflag", "o flag 'e' e manifesto com o atributo 'Main-Class' não podem ser especificados \njuntos!" }, { "error.bad.option", "Uma das opções -{ctxu} deve ser especificada." }, { "error.bad.uflag", "o flag 'u' requer que arquivos de manifesto, o flag 'e' ou arquivos de entrada sejam especificados!" }, { "error.cant.open", "não é possível abrir: {0} " }, { "error.create.dir", "{0} : não foi possível criar o diretório" }, { "error.create.tempfile", "Não foi possível criar um arquivo temporário" }, { "error.illegal.option", "Opção inválida: {0}" }, { "error.incorrect.length", "largura incorreta durante o processamento: {0}" }, { "error.nosuch.fileordir", "{0} : não há tal arquivo ou diretório" }, { "error.write.file", "Erro ao gravar o arquivo jar existente" }, { "out.added.manifest", "manifesto adicionado" }, { "out.adding", "adicionando: {0}" }, { "out.create", "  criado: {0}" }, { "out.deflated", "(compactado {0}%)" }, { "out.extracted", "extraído: {0}" }, { "out.ignore.entry", "ignorando entrada {0}" }, { "out.inflated", " inflado: {0}" }, { "out.size", "(entrada = {0}) (saída= {1})" }, { "out.stored", "(armazenado 0%)" }, { "out.update.manifest", "manifesto atualizado" }, { "usage", "Uso: jar {ctxui}[vfmn0Me] [jar-file] [manifest-file] [entry-point] [-C dir] arquivos ...\nOpções:\n    -c  cria novo arquivo compactado\n    -t  lista o sumário do arquivo compactado\n    -x  extrai arquivos com o nome (ou todos) do arquivo compactado\n    -u  atualiza o arquivo compactado existente\n    -v  gera saída detalhada na saída padrão\n    -f  especifica o nome do arquivo do arquivo compactado\n    -m  inclui as informações do manifesto do arquivo de manifesto especificado\n    -n   executa a normalização Pack200 após a criação de um novo arquivo compactado\n    -e  especifica o ponto de entrada da aplicativo para aplicativo stand-alone \n        empacotada em um arquivo jar executável\n    -0  armazena somente; não usa compactação ZIP\n    -P  preserva os componentes '/' inicial (caminho absoluto) e \"..\" (diretório pai) nos nomes dos arquivos\n    -M  não cria um arquivo de manifesto para as entradas\n    -i  gera informações de índice para os arquivos especificados\n    -C  passa para o diretório especificado e inclui o arquivo a seguir\nSe um arquivo também for um diretório, ele será processado repetidamente.\nO nome do arquivo de manifesto, o nome do arquivo compactado e o nome do ponto de entrada são\nespecificados na mesma ordem dos flags 'm', 'f' e 'e'.\n\nExemplo 1: para arquivar dois arquivos de classe em um arquivo compactado denominado classes.jar: \n       jar cvf classes.jar Foo.class Bar.class \nExemplo 2: use um arquivo de manifesto existente 'mymanifest' e arquive todos os\n           arquivos no diretório foo/ na 'classes.jar': \n       jar cvfm classes.jar mymanifest -C foo/ .\n" } };
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\tools\jar\resources\jar_pt_BR.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */