package fr.wastemart.maven.pluginmanager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.DosFileAttributes;
import java.util.ArrayList;
import java.util.Scanner;


public class PluginManager {

    private static Plugin[] plugins;
    private static String[] pluginsName;
    //private static String path = "/run/media/alexandrebis-x220/Elements/ESGI/Matières/Projet_Annuel/3AL_Java/JavaFX/3AL_ClientJavaFX/JavaFX/src/main/resources/plugins/";
    //private static String confFile = "/run/media/alexandrebis-x220/Elements/ESGI/Matières/Projet_Annuel/3AL_Java/JavaFX/3AL_ClientJavaFX/JavaFX/src/main/resources/activatedPlugins.conf";

    private static String path = "plugins";
    private static String confFile = "activatedPlugins.conf";

	public static void initialization(){
        try {

            System.out.println(confFile.length());
            if (confFile.length() != 0) {
                loadPlugins();

                BufferedReader reader = new BufferedReader(new FileReader(confFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    for (int i = 0; i < plugins.length; i++) {
                        System.out.println(pluginsName[i]);
                        if (pluginsName[i].compareTo(line) == 0) {
                            plugins[i].run();
                        }
                    }

                }
                reader.close();
            }
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
            Class<?>[] pluginsClasses = PluginLoader.loadPluginsDirectory(getPath());
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

	public static Boolean activatePlugin(String[] pluginsName, Integer choice) throws Exception {
        if(pluginsName != null){
            if(choice < 0 || choice >= pluginsName.length){
                System.out.println("Saisie invalide");
                return false;
            } else {
                try {
                    ArrayList<String> confLine = new ArrayList<String>();

                    BufferedReader reader = new BufferedReader(new FileReader(confFile));
                    String line;

                    while ((line = reader.readLine()) != null) {
                        confLine.add(line);
                    }

                    reader.close();

                    if(!confLine.contains(pluginsName[choice])){
                        BufferedWriter writer = new BufferedWriter(new FileWriter(confFile));

                        for (String aConfLine : confLine) {
                            writer.append(aConfLine);
                            writer.newLine();
                        }

                        writer.append(pluginsName[choice]);

                        writer.close();


                    }
                    else{
                        System.out.println("Plugin déjà activé !");
                        return false;
                    }
                }
                catch (Exception e){
                    System.out.println("Error while writing/reading in the configuration file");
                    return false;
                }

                try {
                    loadPlugins();
                    System.out.println("Plugin activé !");

                    plugins[choice].run();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        }
        else{
            System.out.println("Vous n'avez aucun plugins, télécharger en un  ! ");
            return false;
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

    public static ArrayList<String> fetchOnlinePlugins(String url) throws Exception {
        url = "http://51.75.143.205:8080/plugins/";
        ArrayList<String> pluginsAvailable = new ArrayList<String>();

        Document document = Jsoup.connect(url).get();

        Elements link = document.select("a[href]");

        for (int i = 2; i < link.size(); i++){
            pluginsAvailable.add(link.get(i).text());
        }

        return pluginsAvailable;
    }

    public static Boolean installPlugin(String url, String pluginName) throws IOException {
        url += pluginName;

        try (BufferedInputStream inputStream = new BufferedInputStream(new URL(url).openStream());
             FileOutputStream fileOS = new FileOutputStream(getPath() + "/" + pluginName)) {
            byte data[] = new byte[1024];
            int byteContent;
            while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
                fileOS.write(data, 0, byteContent);
            }
            return true;
        } catch (IOException e) {
            // Logger.reportError(e);
            e.printStackTrace();
        }
        System.out.println("Error occurred");
        return false;
    }

    public static Boolean uninstallPlugin(Integer choice) throws Exception {
        File dir = new File(getPath());

        final File[] pluginsList = dir.listFiles();

        if(choice >= 0 && choice < pluginsList.length) {

            String p = pluginsList[choice].getPath();

            File pluginToDelete = new File(p);

            //pluginToDelete.setExecutable(true, false);
            DosFileAttributeView dosView = Files.getFileAttributeView(pluginToDelete.toPath(),DosFileAttributeView.class);

            dosView.setArchive(true);
            DosFileAttributes e = dosView.readAttributes();
            System.out.println(e.isHidden());

            return pluginToDelete.delete();

        } else{
            return false;
        }

    }

    public static String getPath() {
        return PluginManager.path;
    }

    public static void setPath(String path) {
        PluginManager.path = path;
    }

    public static String getConfFile() {
        return confFile;
    }

    public static void setConfFile(String confFile) {
        PluginManager.confFile = confFile;
    }
}
