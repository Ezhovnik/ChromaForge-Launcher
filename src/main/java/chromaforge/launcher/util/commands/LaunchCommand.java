package chromaforge.launcher.util.commands;

import java.nio.file.Path;

import chromaforge.launcher.run.Runners;
import chromaforge.launcher.util.Platform;

public class LaunchCommand extends Command {
    public LaunchCommand() {
        this.keyword = "launch";
        this.args = "<version>";
        this.help = "runs the specified engine version";
    }

    @Override
    public void execute(String[] args) {
        String tagName = nextArg(args);

        Platform.OS os = Platform.detectOS();
        Path installDir = Path.of("cores/chromaforge-v" + tagName);
        Runners.of(os).run(installDir);
    }
}
