package chromaforge.launcher.util.commands;

import chromaforge.launcher.io.LauncherPaths;
import chromaforge.launcher.services.CoreService;
import chromaforge.launcher.util.ConsoleUtils;
import chromaforge.launcher.util.CoreVersion;
import chromaforge.launcher.util.ExitCode;

public class RmCommand extends Command {

    public RmCommand() {
        this.keyword = "rm";
        this.args = "<version>";
        this.help = "removes the specified engine version";
    }

    @Override
    protected void registerArgs() {
        parser.flag("--force", "-f", "skip the confirmation prompt");
    }

    @Override
    public ExitCode execute(String[] args, LauncherPaths paths) {
        parser.parse(args, 1);

        String tagName = requiredArg(parser, 0, "version");
        CoreVersion version = CoreVersion.parse(tagName);

        if (!CoreService.isInstalled(version, paths)) {
            ConsoleUtils.error("Core version '" + tagName + "' is not installed");
            return ExitCode.USAGE;
        }

        boolean confirmed = parser.has("--force");
        if (!confirmed) {
            confirmed = ConsoleUtils.confirmDeletion(tagName);
        }
        if (!confirmed) {
            ConsoleUtils.tip("  Deletion cancelled");
            return ExitCode.USAGE;
        }

        ConsoleUtils.stage("Removing '" + tagName + "'...");
        try {
            CoreService.removeVersion(version, paths);
            ConsoleUtils.success("Version '" + tagName + "' removed");
        } catch (Exception e) {
            ConsoleUtils.error("Could not remove '" + tagName + "' : " + e.getMessage());
            return ExitCode.FAILURE;
        }
        return ExitCode.SUCCESS;
    }
}
