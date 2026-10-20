import java.io.IOException;
import java.nio.file.*;

class TransactionLog {

    public static void append(Path file, String line)
            throws IOException {
        Files.write(file, (line + System.lineSeparator())
                .getBytes(),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND);
    }
}
