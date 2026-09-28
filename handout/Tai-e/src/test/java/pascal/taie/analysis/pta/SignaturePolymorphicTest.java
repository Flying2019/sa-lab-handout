/*
 * Tai-e: A Static Analysis Framework for Java
 *
 * Copyright (C) 2022 Tian Tan <tiantan@nju.edu.cn>
 * Copyright (C) 2022 Yue Li <yueli@nju.edu.cn>
 *
 * This file is part of Tai-e.
 *
 * Tai-e is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * Tai-e is distributed in the hope that it will be useful,but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Tai-e. If not, see <https://www.gnu.org/licenses/>.
 */

package pascal.taie.analysis.pta;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import pascal.taie.Main;
import pascal.taie.World;
import pascal.taie.analysis.graph.callgraph.CallGraph;
import pascal.taie.analysis.graph.callgraph.CallGraphBuilder;
import pascal.taie.ir.stmt.Invoke;
import pascal.taie.language.classes.ClassHierarchy;
import pascal.taie.language.classes.JMethod;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests call edges to signature-polymorphic methods (JLS 15.12.3).
 * A call site to such a method may have more arguments than the
 * declared parameters of the resolved method.
 */
public class SignaturePolymorphicTest {

    @TempDir
    Path tempDir;

    @Test
    void test() throws IOException {
        // The main class is generated as a class file rather than written in
        // Java source, because such a call site cannot be expressed in Java:
        // MethodHandle.linkToStatic is package-private, and javac always
        // emits call descriptors derived from the declared method signature,
        // while the JVM accepts any descriptor for signature-polymorphic
        // methods.
        Files.write(tempDir.resolve("Entrypoint.class"), genMainClass());
        Main.main(
                "-cp", tempDir.toString(),
                "-m", "Entrypoint",
                "-a", "pta=implicit-entries:false",
                "-a", "cg");
        ClassHierarchy hierarchy = World.get().getClassHierarchy();
        CallGraph<Invoke, JMethod> cg = World.get().getResult(CallGraphBuilder.ID);
        JMethod main = hierarchy.getMethod(
                "<Entrypoint: void main(java.lang.String[])>");
        JMethod linkToStatic = hierarchy.getMethod(
                "<java.lang.invoke.MethodHandle:"
                        + " java.lang.Object linkToStatic(java.lang.Object[])>");
        assertNotNull(main);
        assertNotNull(linkToStatic);
        assertTrue(cg.contains(main));
        assertTrue(cg.contains(linkToStatic));
    }

    /**
     * Generates a class whose main method is equivalent to
     * <pre>
     * public static void main(String[] args) {
     *     MethodHandle.linkToStatic(new Object(), new Object());
     * }
     * </pre>
     * i.e., it contains the following signature-polymorphic call site:
     * <pre>
     * invokestatic java/lang/invoke/MethodHandle.linkToStatic:
     *     (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
     * </pre>
     * The call site has two arguments, while the declared
     * {@code linkToStatic} method has only one (varargs) parameter.
     */
    private static byte[] genMainClass() {
        ClassWriter cw = new ClassWriter(0);
        cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER,
                "Entrypoint", null, "java/lang/Object", null);
        MethodVisitor mv = cw.visitMethod(
                Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                "main", "([Ljava/lang/String;)V", null, null);
        mv.visitCode();
        mv.visitTypeInsn(Opcodes.NEW, "java/lang/Object");
        mv.visitInsn(Opcodes.DUP);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL,
                "java/lang/Object", "<init>", "()V", false);
        mv.visitTypeInsn(Opcodes.NEW, "java/lang/Object");
        mv.visitInsn(Opcodes.DUP);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL,
                "java/lang/Object", "<init>", "()V", false);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/invoke/MethodHandle",
                "linkToStatic",
                "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);
        mv.visitInsn(Opcodes.POP);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(2, 1);
        mv.visitEnd();
        cw.visitEnd();
        return cw.toByteArray();
    }

}
