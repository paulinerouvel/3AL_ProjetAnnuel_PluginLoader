package plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import java.lang.Class;

public class PluginLoader {




	/** Get the path of the plugins directory and load classes */
	public static Class<?>[] loadPluginsDirectory(String path) throws ClassNotFoundException, IOException {

		//Plugins Dir
		File dir = new File(path);


		final File[] pluginsList = dir.listFiles();

		//create a generic list of plugins => we don't know the classes of the plugins
		final Class<?>[] pluginsClasses = new Class<?>[pluginsList.length];

		for (int i=0; i<pluginsClasses.length; i++){
            pluginsClasses[i] = loadClassPlugin(pluginsList[i]);
        }



		return pluginsClasses;
	}


	/** For each plugins this function is execute*/
	/** Will load all the main classe of the plugin*/
	public static Class<?> loadClassPlugin(File dir) throws IOException, ClassNotFoundException {

		//instanciation of a jarFile because this is the plugin extension
		final JarFile jarFile = new JarFile(dir);


		String configPath = "config.cfg"; //each plugin must have this file to be run

		//define the entry point of the file that is specify in the config file
		final JarEntry entryPoint = jarFile.getJarEntry(configPath);


		//read the file specify in the entry point => config file
		final BufferedReader br = new BufferedReader(new InputStreamReader(jarFile.getInputStream(entryPoint)));
		
		final HashMap<String, String> confTab = new HashMap<>();
		
		String line;
		while((line = br.readLine()) != null) {
			if(line.isEmpty() || line.startsWith("#"))
				continue;
			final String[] split = line.split(" ");
			//store the confs in a hashMap
			confTab.put(split[0], split[1]);
		}
		
		jarFile.close();


		//TODO Main a changer !
		/**Reflexivité ici, on analyse la classe "main" grace à la classe Class*/
		/**La JVM creer des instances au chargement de la classe*/
		//For Name : Returns the Class object associated with the class or interface with the given string name, using the given class loader.
		//CLassLoader : A class loader is an object that is responsible for loading classes.
		return Class.forName(confTab.get("Main"), true, new URLClassLoader(new URL[]{dir.toURI().toURL()}));
	}



	

	
	public static Plugin[] initAsPlugin(Class<?>[] group) throws InstantiationException, IllegalAccessException {
		final Plugin[] plugins = new Plugin[group.length];
		for (int i=0; i<group.length; i++)

		    try{
                plugins[i] = initAsPlugin(group[i]);
            }
            catch (Exception e){
		        System.out.println(e);
            }

		
		return plugins;
	}


    public static Plugin initAsPlugin(Class<?> group) throws InstantiationException, IllegalAccessException , Exception{

        return (Plugin) group.getDeclaredConstructor().newInstance();

    }


    public static String[] getPluginsNames(String path) throws ClassNotFoundException, IOException{
		File dir = new File(path);

		final File[] pluginsList = dir.listFiles();


		String[] names = new String[pluginsList.length];

		for (int i=0; i<pluginsList.length; i++){
			names[i] = pluginsList[i].getAbsolutePath();
		}


		return names;
	}


	
}
