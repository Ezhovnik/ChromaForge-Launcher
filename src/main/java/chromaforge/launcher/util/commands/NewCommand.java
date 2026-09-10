package chromaforge.launcher.util.commands;

import chromaforge.launcher.io.LauncherPaths;
import chromaforge.launcher.services.InstanceService;
import chromaforge.launcher.util.ConsoleUtils;
import chromaforge.launcher.util.ExitCode;
import chromaforge.launcher.util.InstanceInfo;

public class NewCommand extends Command {
    public NewCommand() {
        this.keyword = "new";
        this.args = "<name> <version>";
        this.help = "creates a new instance";
    }

    @Override
    public ExitCode execute(String[] args, LauncherPaths paths) {
        parser.parse(args, 1);

        String instanceName = requiredArg(parser, 0, "name");
        String coreVersion = requiredArg(parser, 1, "version");

        try {
            InstanceService.create(
                new InstanceInfo(instanceName, coreVersion),
                paths
            );
            ConsoleUtils.success("The instance '" + instanceName + "' was successfully created on engine version '" + coreVersion + "'");
            return ExitCode.SUCCESS;
        } catch (RuntimeException e) {
            ConsoleUtils.error("Failed to create new instance: " + e.getMessage());
            return ExitCode.FAILURE;
        }
    }
}
