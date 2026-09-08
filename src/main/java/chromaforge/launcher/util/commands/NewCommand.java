package chromaforge.launcher.util.commands;

import chromaforge.launcher.io.LauncherPaths;
import chromaforge.launcher.services.InstanceService;
import chromaforge.launcher.util.ConsoleUtils;
import chromaforge.launcher.util.InstanceInfo;

public class NewCommand extends Command {
    public NewCommand() {
        this.keyword = "new";
        this.args = "<name> <version>";
        this.help = "creates a new instance";
    }

    @Override
    public void execute(String[] args, LauncherPaths paths) {
        String instanceName = nextArg(args);
        String coreVersion = nextArg(args);

        try {
            InstanceService.create(
                new InstanceInfo(instanceName, coreVersion),
                paths
            );
            ConsoleUtils.success("The instance '" + instanceName + "' was successfully created on engine version '" + coreVersion + "'");
        } catch (RuntimeException e) {
            ConsoleUtils.error("Failed to create new instance: " + e.getMessage());
        }
    }
}
