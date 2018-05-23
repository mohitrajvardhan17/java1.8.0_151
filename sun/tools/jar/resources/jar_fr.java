package sun.tools.jar.resources;

import java.util.ListResourceBundle;

public final class jar_fr
  extends ListResourceBundle
{
  public jar_fr() {}
  
  protected final Object[][] getContents()
  {
    return new Object[][] { { "error.bad.cflag", "L'indicateur c requiert la spécification d'un fichier manifeste ou d'un fichier d'entrée." }, { "error.bad.eflag", "L'indicateur e et le fichier manifeste portant l'attribut Main-Class ne peuvent pas être spécifiés \nensemble." }, { "error.bad.option", "Une des options -{ctxu} doit être spécifiée." }, { "error.bad.uflag", "L'indicateur u requiert la spécification d'un fichier manifeste, d'un fichier d'entrée ou d'un indicateur e." }, { "error.cant.open", "impossible d''ouvrir : {0} " }, { "error.create.dir", "{0} : impossible de créer le répertoire" }, { "error.create.tempfile", "Impossible de créer un fichier temporaire" }, { "error.illegal.option", "Option non admise : {0}" }, { "error.incorrect.length", "longueur incorrecte lors du traitement de : {0}" }, { "error.nosuch.fileordir", "{0} : fichier ou répertoire introuvable" }, { "error.write.file", "Erreur lors de l'écriture d'un fichier JAR existant" }, { "out.added.manifest", "manifeste ajouté" }, { "out.adding", "ajout : {0}" }, { "out.create", "  créé : {0}" }, { "out.deflated", "(compression : {0} %)" }, { "out.extracted", "extrait : {0}" }, { "out.ignore.entry", "entrée {0} ignorée" }, { "out.inflated", " décompressé : {0}" }, { "out.size", "(entrée = {0}) (sortie = {1})" }, { "out.stored", "(stockage : 0 %)" }, { "out.update.manifest", "manifeste mis à jour" }, { "usage", "Syntaxe : jar {ctxui}[vfmn0PMe] [fichier-jar] [fichier-manifeste] [point-entrée] [-C rép] fichiers...\nOptions :\n    -c  crée une archive\n    -t  affiche la table des matières de l'archive\n    -x  extrait les fichiers nommés (ou tous les fichiers) de l'archive\n    -u  met à jour l'archive existante\n    -v  génère une version détaillée d'une sortie standard\n    -f  spécifie le nom du fichier archive\n    -m  inclut les informations de manifeste à partir du fichier manifeste spécifié\n    -n  effectue une normalisation Pack200 après la création d'une archive\n    -e  spécifie le point d'entrée d'une application en mode autonome \n        intégrée à un fichier JAR exécutable\n    -0  stockage uniquement, pas de compression ZIP\n    -P  préserve les signes de début '/' (chemin absolu) et \"..\" (répertoire parent) dans les noms de fichier\n    -M  ne crée pas de fichier manifeste pour les entrées\n    -i  génère les informations d'index des fichiers JAR spécifiés\n    -C  passe au répertoire spécifié et inclut le fichier suivant\nSi l'un des fichiers est un répertoire, celui-ci est traité récursivement.\nLes noms du fichier manifeste, du fichier archive et du point d'entrée sont\nspécifiés dans le même ordre que celui des indicateurs m, f et e.\n\nExemple 1 : pour archiver deux fichiers de classe dans une archive intitulée classes.jar : \n       jar cvf classes.jar Foo.class Bar.class \nExemple 2 : pour utiliser un fichier manifeste existant 'monmanifeste', puis archiver tous les\n           fichiers du répertoire foo/ dans 'classes.jar' : \n       jar cvfm classes.jar monmanifeste -C foo/ .\n" } };
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\tools\jar\resources\jar_fr.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */