package pku;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

/** Exactly the two Lab1 result forms: signs and allocation sets. */
public final class CourseResult {
    private final Map<Integer, String> answers = new TreeMap<>();

    public void sign(int id, String value) {
        if (value == null || !java.util.Set.of("+", "-", "0", "+0", "-0", "+-0").contains(value))
            throw new IllegalArgumentException("Invalid sign");
        put(id, value);
    }

    public void pointsTo(int id, Collection<Integer> objects) {
        if (objects.stream().anyMatch(n -> n <= 0)) throw new IllegalArgumentException("Invalid allocation ID");
        put(id, new TreeSet<>(objects).stream().map(Object::toString).collect(Collectors.joining(" ")));
    }

    private void put(int id, String answer) {
        if (id <= 0 || answers.putIfAbsent(id, answer) != null) {
            throw new IllegalArgumentException("Invalid or duplicate query ID " + id);
        }
    }

    public void write(String output) {
        if (output == null || output.isBlank()) throw new IllegalArgumentException("SA_OUTPUT is required");
        String text = answers.entrySet().stream().map(e -> e.getKey() + ": " + e.getValue() + "\n")
                .collect(Collectors.joining());
        try {
            Files.writeString(Path.of(output), text, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
