package chromaforge.launcher.util.commands;

import chromaforge.launcher.io.LauncherPaths;
import chromaforge.launcher.services.InstanceService;
import chromaforge.launcher.util.InstanceInfo;
import chromaforge.launcher.util.ConsoleUtils;
import chromaforge.launcher.util.ExitCode;

public class RmiCommand extends Command {

    public RmiCommand() {
        this.keyword = "rmi";
        this.args = "<instance>";
        this.help = "removes the specified instance";
    }

    @Override
    protected void registerArgs() {
        parser.flag("--force", "-f", "required to confirm deletion");
    }

    @Override
    public ExitCode execute(String[] args, LauncherPaths paths) {
        parser.parse(args, 1);

        String instanceName = requiredArg(parser, 0, "instance name");

        if (!parser.has("--force")) {
            ConsoleUtils.error("Deletion requires '--force'");
            return ExitCode.USAGE;
        }

        InstanceInfo inst = InstanceService.get(instanceName, paths);
        if (inst == null) {
            ConsoleUtils.error("Instance '" + instanceName + "' does not exist");
            return ExitCode.USAGE;
        }

        System.out.println("Removing '" + instanceName + "'...");
        try {
            InstanceService.remove(inst, paths);
            ConsoleUtils.success("Instance '" + instanceName + "' removed");
        } catch (Exception e) {
            ConsoleUtils.error("Could not remove '" + instanceName + "' : " + e.getMessage());
            return ExitCode.FAILURE;
        }
        return ExitCode.SUCCESS;
    }
}
