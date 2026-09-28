package benchmark.internal;

import java.lang.reflect.*;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/** Teacher-provided concrete execution driver, not a symbolic interpreter. */
public final class CourseReplay {
    private static Method entry(Class<?> type) {
        var found = Arrays.stream(type.getDeclaredMethods())
                .filter(m -> m.getName().equals(type.getSimpleName()) && Modifier.isPublic(m.getModifiers())
                        && Modifier.isStatic(m.getModifiers())).toList();
        if (found.size() != 1) throw new IllegalArgumentException("one public static method named after its class required");
        Method m = found.get(0);
        if (m.getReturnType() != void.class && m.getReturnType() != int.class)
            throw new IllegalArgumentException("test must return void or int");
        return m;
    }

    public static void main(String[] args) throws Exception {
        // classpath, entry class, input text, output text; --describe replaces input.
        var urls = new java.net.URL[]{Path.of(args[0]).toUri().toURL()};
        if (args[2].equals("--describe")) {
            try (var loader = new URLClassLoader(urls, ClassLoader.getPlatformClassLoader())) {
                Method m = entry(Class.forName(args[1], false, loader));
                List<String> types = new ArrayList<>();
                for (Class<?> t : m.getParameterTypes()) {
                    if (t == int.class) types.add("int");
                    else if (t == int[].class) types.add("int[]");
                    else throw new IllegalArgumentException("unsupported parameter " + t);
                }
                Files.writeString(Path.of(args[3]), String.join(" ", types), StandardCharsets.UTF_8);
            }
            return;
        }
        // Batch mode is used only by the teacher's finite-domain oracle. A fresh
        // loader resets application and Benchmark state for every input in the batch.
        var results = new ArrayList<String>();
        for (String line : Files.readAllLines(Path.of(args[2]), StandardCharsets.UTF_8)) {
            try (var loader = new URLClassLoader(urls, ClassLoader.getPlatformClassLoader())) {
                Class<?> c = Class.forName(args[1], false, loader);
                Method m = entry(c);
                String[] tokens = line.equals("()") ? new String[0] : line.split("\\t", -1);
                if (tokens.length != m.getParameterCount()) throw new IllegalArgumentException("arity");
                Object[] input = new Object[tokens.length];
                for (int i = 0; i < tokens.length; i++) {
                    String t = tokens[i];
                    Class<?> type = m.getParameterTypes()[i];
                    if (type == int.class) input[i] = Integer.parseInt(t);
                    else if (type == int[].class) {
                        if (!t.startsWith("[") || !t.endsWith("]")) throw new IllegalArgumentException("array");
                        String body = t.substring(1, t.length() - 1);
                        input[i] = body.isEmpty() ? new int[0] : Arrays.stream(body.split(","))
                                .map(String::trim).mapToInt(Integer::parseInt).toArray();
                    } else throw new IllegalArgumentException("parameter");
                }
                boolean normal;
                try { m.invoke(null, input); normal = true; }
                catch (InvocationTargetException | ExceptionInInitializerError e) { normal = false; }
                if (normal) {
                    Class<?> benchmark = Class.forName("benchmark.internal.Benchmark", false, loader);
                    Field visited = benchmark.getDeclaredField("visited");
                    visited.setAccessible(true);
                    @SuppressWarnings("unchecked") var ids = (Set<Integer>) visited.get(null);
                    results.add("normal:" + ids.stream().sorted().map(Object::toString)
                            .collect(java.util.stream.Collectors.joining(" ")));
                } else results.add("error:");
            }
        }
        Files.write(Path.of(args[3]), results, StandardCharsets.UTF_8);
    }
}
