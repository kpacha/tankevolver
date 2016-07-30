package com.github.kpacha.tankevolver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import robocode.AdvancedRobot;

public class InlineCompiler {

	private static final String BASE_PATH = "./robots/";

	public static void compile(String body, String path, String fqn)
			throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		File javaFile = new File(BASE_PATH + path);
		if (javaFile.getParentFile().exists() || javaFile.getParentFile().mkdirs()) {

			Writer writer = null;
			try {
				writer = new FileWriter(javaFile);
				writer.write(body);
				writer.flush();
			} finally {
				try {
					writer.close();
				} catch (Exception e) {
				}
			}

			/**
			 * Compilation Requirements
			 *********************************************************************************************/
			DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

			// This sets up the class path that the compiler will use.
			// I've added the .jar file that contains the DoStuff interface
			// within in it...
			List<String> optionList = new ArrayList<String>();
			optionList.add("-classpath");
			optionList.add(System.getProperty("java.class.path") + ";dist/InlineCompiler.jar");

			Iterable<? extends JavaFileObject> compilationUnit = fileManager
					.getJavaFileObjectsFromFiles(Arrays.asList(javaFile));
			JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, optionList, null,
					compilationUnit);
			/*********************************************************************************************
			 * Compilation Requirements
			 **/
			if (task.call()) {
				/**
				 * Load and execute
				 *************************************************************************************************/
				// Create a new custom class loader, pointing to the directory
				// that contains the compiled
				// classes, this should point to the top of the package
				// structure!
				URLClassLoader classLoader = new URLClassLoader(new URL[] { new File(BASE_PATH).toURI().toURL() });
				// Load the class from the classloader by name....
				Class<?> loadedClass = classLoader.loadClass(fqn);
				// Create a new instance...
				Object obj = loadedClass.newInstance();
				// Santity check
				if (obj instanceof AdvancedRobot) {
					AdvancedRobot robot = (AdvancedRobot) obj;
					System.out.println("Here we have an instance of " + robot.getClass().getCanonicalName());
				}
				/*************************************************************************************************
				 * Load and execute
				 **/
			} else {
				for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
					System.out.format("Error on line %d in %s%n", diagnostic.getLineNumber(),
							diagnostic.getSource().toUri());
				}
				if (diagnostics.getDiagnostics().size() > 0) {
					throw new InstantiationException();
				}
			}
			fileManager.close();
		}
	}

}