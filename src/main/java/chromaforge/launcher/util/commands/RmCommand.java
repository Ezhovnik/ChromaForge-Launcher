package chromaforge.launcher.util.commands;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;
import java.util.Iterator;

public class RmCommand extends Command {
    public RmCommand() {
        this.keyword = "rm";
        this.args = "<version>";
        this.help = "removes the specified engine version";
    }

    @Override
    public void execute(String[] args) {
        String version = nextArg(args);

        Path installDir = Path.of("cores/chromaforge-v" + version);
        if (!Files.exists(installDir)) {
            System.out.println("Version " + version + " is not installed.");
            return;
        }
        try (Stream<Path> walk = Files.walk(installDir)) {
            Iterator<Path> it = walk.sorted(Comparator.reverseOrder()).iterator();
            while (it.hasNext()) {
                Path p = it.next();
                try {
                    Files.deleteIfExists(p);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to delete " + p, e);
                }
            }
            System.out.println("Removed version " + version);
        } catch (IOException e) {
            throw new RuntimeException("Failed to traverse directory " + installDir, e);
        }
    }
}
