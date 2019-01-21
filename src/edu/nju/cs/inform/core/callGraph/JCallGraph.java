package edu.nju.cs.inform.core.callGraph;

/*
 * Copyright (c) 2011 - Georgios Gousios <gousiosg@gmail.com>
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import edu.nju.cs.inform.util.AppConfigure;
import org.apache.bcel.classfile.ClassParser;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Constructs a callgraph out of a JAR archive. Can combine multiple archives
 * into a single call graph.
 * 
 * @author Georgios Gousios <gousiosg@gmail.com>
 * 
 */
public class JCallGraph {
	public static Hashtable<String, Vector<String>> callGraphMap;
	public static Hashtable<String, Vector<String>> isCalledByGraphMap;
	public static Hashtable<String, Vector<String>> superClassRelations;
	public static Hashtable<String, String> superClasses;

	/**
	 * Creates a Java Call Graph
	 * 
	 * @param arg
	 */
	public JCallGraph(String arg) {
		// SK - initialize hashtables for callgraphs and caller/callee relations
		superClassRelations = new Hashtable<String, Vector<String>>();
		callGraphMap = new Hashtable<String, Vector<String>>();
		isCalledByGraphMap = new Hashtable<String, Vector<String>>();
		superClasses = new Hashtable<String, String>();
		// END - SK

		ClassParser cp;
		try {
			File f = new File(arg);

			if (!f.exists()) {
				System.err.println("Jar file " + arg + " does not exist");
			}

			JarFile jar = new JarFile(f);

			Enumeration<JarEntry> entries = jar.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				if (entry.isDirectory())
					continue;

				if (!entry.getName().endsWith(".class"))
					continue;
				cp = new ClassParser(arg, entry.getName());
				ClassVisitor visitor = new ClassVisitor(cp.parse());
				visitor.start();
			}
		} catch (IOException e) {
			System.err.println("Error while processing jar: " + e.getMessage());
			e.printStackTrace();
		}

		// System.out.println("SuperClass Relations: " +
		// superClassRelations.toString());
		// System.out.println("CallgraphMap: " +callGraphMap.toString());
		// System.out.println("IsCalledByGraphMap: " +
		// isCalledByGraphMap.toString());
		/**
		 * SK - Number of Levels is defined in KeywordExtracting.Settings.java
		 * file
		 */
		for (int i = 0; i < AppConfigure.levels; i++) {
			getNextLevel();
		}
		// System.out.println("IsCalledByGraphMap after next Levels: "+
		// isCalledByGraphMap.toString());

	}

	/**
	 * Add names of methods to callGraphMap
	 * 
	 * @author Simon Kaeser, University Of Zurich
	 */
	public static void addToCallGraph(String className2, String referencedClass) {
		// only add if it's not a call to itself
		if (!className2.equalsIgnoreCase(referencedClass)) {
			if (callGraphMap.containsKey(className2)) {
				if (!callGraphMap.get(className2).contains(referencedClass)) {
					callGraphMap.get(className2).add(referencedClass);
				}
			} else {
				Vector<String> vector = new Vector<String>();
				vector.add(referencedClass);
				callGraphMap.put(className2, vector);
			}
		}
	}

	/**
	 * Add names of methods to isCalleByGraphMap
	 * 
	 * @author Simon Kaeser, University Of Zurich
	 */
	public static void addToIsCalledByGraph(String isCalling, String isCalled) {
		// only add if it's not a call to itself
		if (!isCalling.equalsIgnoreCase(isCalled)) {
			if (isCalledByGraphMap.containsKey(isCalled)) {
				if (!isCalledByGraphMap.get(isCalled).contains(isCalling)) {
					isCalledByGraphMap.get(isCalled).add(isCalling);
				}
			} else {
				Vector<String> vector = new Vector<String>();
				vector.add(isCalling);
				isCalledByGraphMap.put(isCalled, vector);
			}
		}
	}

	/**
	 * Calculates next Level of CallGraph and saves Result in existing callGraph
	 * 
	 * @author Simon Kaeser, University Of Zurich
	 */
	public static void getNextLevel() {
		System.out.println("get next level");
		Hashtable<String, Vector<String>> tempCallGraph = new Hashtable<String, Vector<String>>();
		tempCallGraph.putAll(isCalledByGraphMap);
		Enumeration<String> e = isCalledByGraphMap.keys();

		// create new teporary hashtable and iterate through all elements and
		// search for each for new call realtions
		while (e.hasMoreElements()) {
			String str = e.nextElement();
			Vector<String> tempVec1 = isCalledByGraphMap.get(str);
			int tempVec1Size = tempVec1.size();
			for (int i = 0; i < tempVec1Size; i++) {
				String str2 = tempVec1.get(i);
				if (isCalledByGraphMap.containsKey(str2)) {
					Vector<String> tempVec = isCalledByGraphMap.get(str2);
					int tempVecSize = tempVec.size();
					for (int j = 0; j < tempVecSize; j++) {
						if (!tempVec1.contains(tempVec.elementAt(j))) {
							tempCallGraph.get(str).add(tempVec.elementAt(j));
						}
					}
				}
			}
		}
		isCalledByGraphMap = tempCallGraph;
		tempCallGraph = null;

	}
	/**
	 * Checks if Method/Class is an own Method/Class and not imported from a thirdparty
	 * package
	 * 
	 * @param formatString
	 * @return
	 * @author Simon Kaeser, University of Zurich
	 */
	public static Boolean isOwnMethod(String formatString) {
		String temp = formatString.toString();
		String[] externalNames = AppConfigure.filterExternalPackageNames;
		for (int i = 0; i < externalNames.length; i++) {
			if (temp.contains(externalNames[i])) {
				return false;
			}
		}
		return true;
	}
}