import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import plugin.Plugin;
import plugin.PluginLoader;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.zip.ZipFile;
import java.util.zip.*;


public class Main {

    private static Plugin[] plugins;
    private static String[] pluginsName;
    private static String path = "src/main/ressources/plugins";
    private static String confFile = "src/main/activatedPlugins.conf";


	public static void main(String[] args) {

	    initialization();


	    String choice;
		System.out.println("\tBienvenue dans l'outil de gestion de plugins \n" +
                "Que souhaitez vous faire ?\n" +
                "1 - Installer un nouveau plugin\n" +
                "2 - Désinstaller un plugin\n" +
                "3 - Activer un plugin\n" +
                "4 - Désactiver un plugin\n" +
                "5 - Terminer le programme\n" +
                "Votre choix : ");


        Scanner sc = new Scanner(System.in);
        choice = sc.nextLine();


        while (choice.compareTo("5") != 0){

            switch (choice){
                case "1":
                    installPlugin();
                    break;
                case "2":
                    uninstallPlugin();
                    break;
                case "3":
                    loadPlugins();
                    activatePlugin();
                    break;
                case "4":
                    desactivatePlugin();
                    break;
                default:
                    System.out.println("Choix invalide !");
                    break;

            }

            System.out.println(
                    "Que souhaitez vous faire ?\n" +
                    "1 - Installer un nouveau plugin\n" +
                    "2 - Désinstaller un plugin\n" +
                    "3 - Activer un plugin\n" +
                    "4 - Désactiver un plugin\n" +
                    "5 - Terminer le programme\n" +
                    "Votre choix : ");
            choice = sc.nextLine();
        }



		
	}

	public static void initialization(){
        try {

            loadPlugins();
            BufferedReader reader = new BufferedReader(new FileReader(confFile));
            String line;
            while ((line = reader.readLine()) != null) {
                for (int i =0; i< plugins.length; i++){
                    System.out.println(pluginsName[i]);
                    if(pluginsName[i].compareTo(line) == 0){
                        plugins[i].run();
                    }
                }

            }
            reader.close();
        }
        catch (Exception e)
        {
            System.err.format("Exception occurred trying to read '%s'.", confFile);
            e.printStackTrace();
        }
    }

	public static void loadPlugins(){


        try {

            /**Chargement des plugins*/
            Class<?>[] pluginsClasses = PluginLoader.loadPluginsDirectory(path);

            plugins = PluginLoader.initAsPlugin(pluginsClasses);

            /**Chargement des noms des plugins*/

            pluginsName = PluginLoader.getPluginsNames(path);



        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



	public static void activatePlugin(){

        if(pluginsName != null){

            String choice;
            int choiceNb;

            System.out.println("Veuillez séléctionner le plugins à activer : \n");

            for(int j = 0; j< pluginsName.length; j++){
                System.out.println(j + " - " + pluginsName[j]);
            }

            Scanner sc = new Scanner(System.in);
            choice = sc.nextLine();

            choiceNb = Integer.parseInt(choice);

            if(choiceNb < 0 || choiceNb >= pluginsName.length){
                System.out.println("Saisie invalide");
            }else{



                try {
                    ArrayList<String> confLine = new ArrayList<String>();

                    BufferedReader reader = new BufferedReader(new FileReader(confFile));
                    String line;

                    while ((line = reader.readLine()) != null) {
                        confLine.add(line);

                    }



                    reader.close();


                    if(confLine.contains(pluginsName[choiceNb]) == false){
                        BufferedWriter writer = new BufferedWriter(new FileWriter(confFile));

                        for(int j = 0; j < confLine.size(); j++){
                            writer.append(confLine.get(j));

                            writer.newLine();
                        }

                        writer.append(pluginsName[choiceNb]);

                        writer.close();

                        System.out.println("Plugin activé !");
                        plugins[choiceNb].run();
                    }
                    else{
                        System.out.println("Plugin déjà activé !");
                    }



                }
                catch (Exception e){
                    System.out.println("Error while writing/reading in the configuration file");
                }


            }


        }
        else{
            System.out.println("Vous n'avez aucun plugins, télécharger en un  ! ");
        }



    }

    public static void desactivatePlugin(){
        if(pluginsName != null){

            String choice;
            int choiceNb;

            System.out.println("Veuillez séléctionner le plugins à désactiver : \n");

            for(int j = 0; j< pluginsName.length; j++){
                System.out.println(j + " - " + pluginsName[j]);
            }

            Scanner sc = new Scanner(System.in);
            choice = sc.nextLine();

            choiceNb = Integer.parseInt(choice);

            if(choiceNb < 0 || choiceNb >= pluginsName.length){
                System.out.println("Saisie invalide");
            }else{



                try {
                    ArrayList<String> confLine = new ArrayList<String>();

                    BufferedReader reader = new BufferedReader(new FileReader(confFile));
                    String line;

                    while ((line = reader.readLine()) != null) {
                        confLine.add(line);

                    }



                    reader.close();


                    if(confLine.contains(pluginsName[choiceNb]) == true){
                        BufferedWriter writer = new BufferedWriter(new FileWriter(confFile));
                        writer.flush();

                        for(int j = 0; j < confLine.size(); j++){
                            if(confLine.get(j).compareTo( pluginsName[choiceNb]) == 1){
                                System.out.println(confLine.get(j) + "  " + pluginsName[choiceNb]);
                                writer.append(confLine.get(j));

                                writer.newLine();
                            }else{
                                //writer.append("");
                            }

                        }


                        writer.close();

                        System.out.println("Plugin désactivé !");
                        plugins[choiceNb].close();
                    }
                    else{
                        System.out.println("Plugin déjà désactivé !");
                    }



                }
                catch (Exception e){
                    System.out.println("Error while writing/reading in the configuration file");
                }


            }


        }
        else{
            System.out.println("Vous n'avez aucun plugins, télécharger en un ! ");
        }
    }

    public static void installPlugin(){

        String urlPlugin = "http://51.75.143.205:8000";
        ArrayList<String> pluginsDispo = new ArrayList<String>();

        try {
            Document document = Jsoup.connect(urlPlugin).get();

            Elements link = document.select("a[href]");


            for (Element links : link) {
                pluginsDispo.add(links.text());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String choice;

        System.out.println("\tQuel plugin voulez vous télécharger ? \n");

        for (int x = 0; x < pluginsDispo.size(); x++) {
            System.out.println(x +" - " + pluginsDispo.get(x));
        }


        Scanner sc = new Scanner(System.in);
        choice = sc.nextLine();


        if(Integer.parseInt(choice) >= 0 && Integer.parseInt(choice) < pluginsDispo.size()){
            String url = "http://51.75.143.205:8000/" + pluginsDispo.get(Integer.parseInt(choice));

            System.out.println(url);

            //String linkOuterH = link.outerHtml();
            // "<a href="http://example.com"><b>example</b></a>"
            //String linkInnerH = link.html(); // "<b>example</b>"


            try (BufferedInputStream inputStream = new BufferedInputStream(new URL(url).openStream());
                 FileOutputStream fileOS = new FileOutputStream("src/main/ressources/plugins/" + pluginsDispo.get(Integer.parseInt(choice)))) {
                byte data[] = new byte[1024];
                int byteContent;
                while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
                    fileOS.write(data, 0, byteContent);
                }
            } catch (IOException e) {
                // handles IO exceptions
            }
        }
        else{
            System.out.println("Mauvaise saisie !");
        }
    }

    public static void uninstallPlugin(){
	    String path = "src/main/ressources/plugins";
        File dir = new File(path);
        String choice;

        final File[] pluginsList = dir.listFiles();


        String[] names = new String[pluginsList.length];

        for (int i=0; i<pluginsList.length; i++){
            names[i] = pluginsList[i].getAbsolutePath();
            System.out.println(i + " - " + names[i]);
        }


        Scanner sc = new Scanner(System.in);
        choice = sc.nextLine();

        if(Integer.parseInt(choice) >= 0 && Integer.parseInt(choice) < pluginsList.length) {

            String p = pluginsList[Integer.parseInt(choice)].getPath();
            
            File pluginToDelete = new File(p);

            if(pluginToDelete.delete()){
                System.out.println("Plugin désinstallé !");
            }
            else{
                System.out.println("Erreur désinstallation");
            }

        }
        else{
            System.out.println("Saisie invalide ! ");
        }

    }


}
