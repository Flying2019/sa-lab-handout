package pku;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import pascal.taie.World;

/** Exactly the two Lab1 result forms: signs and allocation sets. */
public final class CourseResult {
    private final Map<Integer, String> answers = new TreeMap<>();

    public void sign(int id, String value) {
        answers.put(id, value);
    }

    public void pointsTo(int id, Collection<Integer> objects) {
        answers.put(id, new TreeSet<>(objects).stream().map(Object::toString).collect(Collectors.joining(" ")));
    }

    public void write() {
        String text = answers.entrySet().stream().map(e -> e.getKey() + ": " + e.getValue() + "\n")
                .collect(Collectors.joining());
        try {
            Files.writeString(World.get().getOptions().getOutputFile().toPath(), text, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
