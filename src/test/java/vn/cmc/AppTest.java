package vn.cmc;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AppTest {
    @Test
    void greetingMatchesRequirement() {
        assertEquals("Hello Jenkins!", App.greeting());
    }

    @Test
    void mainPrintsGreetingToConsole() throws Exception {
        PrintStream original = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PrintStream captured = new PrintStream(output, true, "UTF-8")) {
            System.setOut(captured);
            App.main(new String[0]);
        } finally {
            System.setOut(original);
        }
        assertEquals("Hello Jenkins!" + System.lineSeparator(),
                output.toString(StandardCharsets.UTF_8.name()));
    }
}
