
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

class ReportGenerator {

    public static void generate(Path folder, Path report)
            throws IOException {

        long deposits = 0;
        long withdrawals = 0;

        try (DirectoryStream<Path> files =
                     Files.newDirectoryStream(folder)) {

            for (Path file : files) {
                if (!Files.isRegularFile(file)) {
                    continue;
                }

                try (BufferedReader reader =
                             Files.newBufferedReader(file)) {

                    String line;

                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(" ");

                        if (parts.length == 2) {
                            long amount = Long.parseLong(parts[1]);

                            if (parts[0].equals("DEPOSIT")) {
                                deposits += amount;
                            } else if (parts[0].equals("WITHDRAW")) {
                                withdrawals += amount;
                            }
                        }
                    }
                }
            }
        }

        StringBuilder summary = new StringBuilder();

        summary.append("End of Day Report\n");
        summary.append("Total Deposits: ").append(deposits).append("\n");
        summary.append("Total Withdrawals: ").append(withdrawals).append("\n");

        try (DirectoryStream<Path> files =
                     Files.newDirectoryStream(folder)) {

            for (Path file : files) {
                if (Files.isRegularFile(file)) {
                    BasicFileAttributes details =
                            Files.readAttributes(
                                    file, BasicFileAttributes.class);

                    summary.append("\nFile: ")
                            .append(file.getFileName()).append("\n");
                    summary.append("Size: ")
                            .append(details.size()).append(" bytes\n");
                    summary.append("Last Modified: ")
                            .append(new Date(
                                    details.lastModifiedTime().toMillis()))
                            .append("\n");
                }
            }
        }

        Files.write(report, summary.toString().getBytes(),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
    }
}
