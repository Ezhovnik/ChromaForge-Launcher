package chromaforge.launcher.util.commands;

import java.nio.file.Files;

import chromaforge.launcher.io.LauncherPaths;
import chromaforge.launcher.services.CheckService;
import chromaforge.launcher.services.CheckService.CheckException;
import chromaforge.launcher.util.ConsoleUtils;
import chromaforge.launcher.util.CoreVersion;
import chromaforge.launcher.util.ExitCode;

public class CheckCommand extends Command {
    public CheckCommand() {
        this.keyword = "check";
        this.args = "<version>";
        this.help = "verifies the integrity of the engine files";
    }

    @Override
    public ExitCode execute(String[] args, LauncherPaths paths) {
        parser.parse(args, 1);

        String tagName = requiredArg(parser, 0, "version");
        CoreVersion version = CoreVersion.parse(tagName);

        if (!Files.isDirectory(paths.getCoreDir(version))) {
            ConsoleUtils.error("Version " + tagName + " is not installed");
            return ExitCode.USAGE;
        }
        try {
            CheckService.check(version, paths, ConsoleUtils.consoleProgress());
        } catch (CheckException e) {
            ConsoleUtils.clearLine();
            ConsoleUtils.error("The check failed: " + e.getMessage());
            ConsoleUtils.tip("  Try reinstalling with 'install " + tagName + "'");
            return ExitCode.FAILURE;
        }
        ConsoleUtils.success("Check was successful");
        return ExitCode.SUCCESS;
    }
}
