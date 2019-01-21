package edu.nju.cs.inform.core.callGraph;

/*
 * Copyright (c) 2011 - Georgios Gousios <gousiosg@gmail.com>
 *	adapted to purpose of KeywordExtracting by Simon Kaeser, University of Zurich, 2012
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


import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.MethodGen;

import java.util.Vector;

/**
 * The simplest of class visitors, invokes the method visitor class for each
 * method found.
 */
public class ClassVisitor extends EmptyVisitor {

	private JavaClass clazz;
	private ConstantPoolGen constants;
	private String classReferenceFormat;
	private String className;

	public ClassVisitor(JavaClass jc) {
		clazz = jc;
		className = clazz.getClassName();

		constants = new ConstantPoolGen(clazz.getConstantPool());
		classReferenceFormat = "C:" + className + " %s";

		/**
		 * SK - check if clazz has superclass, if yes add values to hashtable ht
		 * in JCallGraph
		 */
		if (!clazz.getSuperclassName().equals("java.lang.Object")
				&& JCallGraph.isOwnMethod(className)) {
			String tempSuperClassName = clazz.getSuperclassName();

			if (JCallGraph.superClassRelations.containsKey(tempSuperClassName)) {
				if (!JCallGraph.superClassRelations.get(tempSuperClassName)
						.contains(className)) {
					JCallGraph.superClassRelations.get(tempSuperClassName).add(
							className);
				}
			} else {
				Vector<String> vector = new Vector<String>();
				vector.add(className);
				JCallGraph.superClassRelations.put(tempSuperClassName, vector);
			}

			if (JCallGraph.superClasses.containsKey(className)) {
				if (!JCallGraph.superClasses.get(className).equals(
						tempSuperClassName)) {
					System.out
							.println("Error: class has two different superclasses!");
				}
			} else {
				JCallGraph.superClasses.put(className, tempSuperClassName);
			}
			tempSuperClassName = null;
		}

	}

	public void visitJavaClass(JavaClass jc) {
		jc.getConstantPool().accept(this);
		Method[] methods = jc.getMethods();
		for (int i = 0; i < methods.length; i++)
			methods[i].accept(this);
	}

	public void visitConstantPool(ConstantPool constantPool) {
		for (int i = 0; i < constantPool.getLength(); i++) {
			Constant constant = constantPool.getConstant(i);
			if (constant == null)
				continue;
			if (constant.getTag() == 7) {
				String referencedClass = constantPool
						.constantToString(constant);

				/** SK - adds classes to callGraph */
				if (JCallGraph.isOwnMethod(classReferenceFormat)
						&& JCallGraph.isOwnMethod(referencedClass)) {
					JCallGraph.addToCallGraph(className, referencedClass);
					JCallGraph.addToIsCalledByGraph(className, referencedClass);
				}
			}
		}
	}

	public void visitMethod(Method method) {
		MethodGen mg = new MethodGen(method, clazz.getClassName(), constants);
		MethodVisitor visitor = new MethodVisitor(mg, clazz);
		visitor.start();
	}

	public void start() {
		visitJavaClass(clazz);
	}

	/**
	 * Check if method or class is an own implemented method or class and does
	 * not contain one of the keywords in its path.
	 * 
	 * @author Simon Kaeser, University of Zurich
	 */

}
