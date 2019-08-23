package fr.wastemart.maven.pluginmanager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.DosFileAttributes;
import java.util.ArrayList;


public class PluginManager {

    private static Plugin[] plugins;
    private static String[] pluginsName;

    private static String pluginFolder = "plugins";
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

	public static void loadPlugins() {
        try {

            /**Chargement des plugins*/
            Class<?>[] pluginsClasses = PluginLoader.loadPluginsDirectory(getPluginFolder());
            plugins = PluginLoader.initAsPlugin(pluginsClasses);

            /**Chargement des noms des plugins*/

            pluginsName = PluginLoader.getPluginsNames(pluginFolder);

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

	public static Boolean activatePlugin(Integer choice) throws Exception {
        ArrayList<String> configFile = readConfFile();

        if(!configFile.contains(pluginsName[choice])){
            BufferedWriter writer = new BufferedWriter(new FileWriter(confFile));
            for (String confLine : configFile) {
                writer.append(confLine);
                writer.newLine();
            }
            writer.append(pluginsName[choice]);
            writer.close();
        }
        else{
            return false;
        }

        loadPlugins();
        plugins[choice].run();
        return true;
    }

    public static Boolean desactivatePlugin(Integer choice) throws Exception {
        ArrayList<String> configFile = readConfFile();
        loadPlugins();

        if(configFile.contains(pluginsName[choice])){
            for (String confLine : configFile) {
                System.out.println(confLine);
                if (confLine.equals(pluginsName[choice])) {
                    System.out.println("Do compare");
                    System.out.println(confLine + "  " + pluginsName[choice]);
                    writeConfFile(confLine);
                } else {
                    System.out.println("Does not compare with"+pluginsName[choice]);
                    System.out.println(confLine.equals(pluginsName[choice]));
                    //writer.append("");
                }

            }

            plugins[choice].close();
            return true;
        } else{
            return false;
        }

    }

    public static ArrayList<String> readConfFile() throws Exception {
        createPluginConfigFile();

        ArrayList<String> confLine = new ArrayList<String>();

        BufferedReader reader = new BufferedReader(new FileReader(getConfFile()));
        String line;
        while ((line = reader.readLine()) != null) {
            confLine.add(line);
        }
        reader.close();
        return confLine;
    }

    public static void writeConfFile(String string) throws Exception {
        BufferedWriter writer = new BufferedWriter(new FileWriter(getConfFile()));
        writer.flush();
        writer.append(string);
        writer.newLine();
        writer.close();
    }


    public static File[] fetchLocalPlugins() {
        File dir = new File(getPluginFolder());
        if(dir.isDirectory()) {
            return dir.listFiles((dir1, name) -> name.toLowerCase().endsWith(".jar"));
        }
        else return null;
    }

    public static ArrayList<String> fetchOnlinePlugins(String url) throws Exception {
        ArrayList<String> pluginsAvailable = new ArrayList<String>();

        Document document = Jsoup.connect(url).get();

        Elements links = document.select("a[href]");

        for (Element link : links) {
            if (link.text().endsWith(".jar")) {
                pluginsAvailable.add(link.text());
            }
        }

        return pluginsAvailable;
    }

    public static void installPlugin(String url, String pluginName) throws Exception {
        setup();

        url += pluginName;

        try (BufferedInputStream inputStream = new BufferedInputStream(new URL(url).openStream());
             FileOutputStream fileOS = new FileOutputStream(getPluginFolder() + "/" + pluginName)) {
            byte data[] = new byte[1024];
            int byteContent;
            while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
                fileOS.write(data, 0, byteContent);
            }
        }
    }

    public static Boolean uninstallPlugin(Integer choice) throws Exception {
        final File[] pluginsList = fetchLocalPlugins();

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

    public static void setup() throws Exception {
        createPluginFolder();
        createPluginConfigFile();
    }

    public static Boolean createPluginFolder() throws IOException {
        File pluginFolder = new File(getPluginFolder());
        if (!pluginFolder.isDirectory()) {
            //return pluginFolder.canWrite(); // The directory is writable
        //} else {
            return pluginFolder.mkdir(); // Folder is created

        }
        return false;
    }

    public static Boolean createPluginConfigFile() throws IOException {
        File configFile = new File(getConfFile());
        if (!configFile.isFile()) {
            //return configFile.canWrite(); // The file is writable
        //} else {
            return configFile.createNewFile(); // File is created
        }
        return false;
    }

    public static String getPluginFolder() {
        return PluginManager.pluginFolder;
    }

    public static void setPluginFolder(String pluginFolder) {
        PluginManager.pluginFolder = pluginFolder;
    }

    public static String getConfFile() {
        return confFile;
    }

    public static void setConfFile(String confFile) {
        PluginManager.confFile = confFile;
    }
}
