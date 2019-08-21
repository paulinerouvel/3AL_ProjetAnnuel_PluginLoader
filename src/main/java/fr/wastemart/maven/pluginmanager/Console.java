package fr.wastemart.maven.pluginmanager;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import static fr.wastemart.maven.pluginmanager.PluginLoader.getPluginsNames;
import static fr.wastemart.maven.pluginmanager.PluginManager.*;

public class Console {
    public static void main(String[] args) {
        String url = "http://51.75.143.205:8080/plugins/";
        String path = "plugins";
        String confFile = "activatedPlugins.conf";

        setConfFile(confFile);
        setPath(path);

        //initialization();

            String choice;
            System.out.println("\tBienvenue dans l'outil de gestion de plugins \n" +
                    "Que souhaitez vous faire ?\n" +
                    "1 - Installer un nouveau plugins\n" +
                    "2 - Désinstaller un plugins\n" +
                    "3 - Activer un plugins\n" +
                    "4 - Désactiver un plugins\n" +
                    "5 - Terminer le programme\n" +
                    "Votre choix : ");


            Scanner sc = new Scanner(System.in);
            choice = sc.nextLine();


        while (choice.compareTo("5") != 0){
            try {
                String[] names;
                switch (choice) {
                    case "1":
                        ArrayList<String> availablePlugins = fetchOnlinePlugins(url);

                        System.out.println("\tQuel plugin voulez vous télécharger ? \n");

                        for (int x = 0; x < availablePlugins.size(); x++) {
                            System.out.println(x +" - " + availablePlugins.get(x));
                        }

                        sc = new Scanner(System.in);
                        choice = sc.nextLine();

                        installPlugin(url, availablePlugins.get(Integer.parseInt(choice)));
                        break;
                    case "2":
                        File dir = new File(getPath());

                        final File[] pluginsList = dir.listFiles();

                        names = new String[pluginsList.length];

                        for (int i=0; i<pluginsList.length; i++){
                            names[i] = pluginsList[i].getAbsolutePath();
                            System.out.println(i + " - " + names[i]);
                        }

                        sc = new Scanner(System.in);
                        choice = sc.nextLine();
                        uninstallPlugin(Integer.parseInt(choice));
                        break;
                    case "3":
                        loadPlugins();

                        names = getPluginsNames("plugins");
                        int choiceNb;

                        System.out.println("Veuillez séléctionner le plugins à activer : \n");

                        for(int j = 0; j< names.length; j++){
                            System.out.println(j + " - " + names[j]);
                        }

                        sc = new Scanner(System.in);
                        choice = sc.nextLine();

                        choiceNb = Integer.parseInt(choice);

                        activatePlugin(names, choiceNb);
                        break;
                    case "4":
                        desactivatePlugin();
                        break;
                    default:
                        System.out.println("Choix invalide !");
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println(
                    "Que souhaitez vous faire ?\n" +
                    "1 - Installer un nouveau plugins\n" +
                    "2 - Désinstaller un plugins\n" +
                    "3 - Activer un plugins\n" +
                    "4 - Désactiver un plugins\n" +
                    "5 - Terminer le programme\n" +
                    "Votre choix : ");
            choice = sc.nextLine();
        }
        }
}
