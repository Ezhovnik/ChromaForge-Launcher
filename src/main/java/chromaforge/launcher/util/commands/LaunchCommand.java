package chromaforge.launcher.util.commands;

import chromaforge.launcher.io.LauncherPaths;
import chromaforge.launcher.services.InstanceService;
import chromaforge.launcher.util.ConsoleUtils;
import chromaforge.launcher.util.ExitCode;
import chromaforge.launcher.util.InstanceInfo;

public class LaunchCommand extends Command {

    public LaunchCommand() {
        this.keyword = "launch";
        this.args = "<instance name>";
        this.help = "runs the specified instance";
    }

    @Override
    public ExitCode execute(String[] args, LauncherPaths paths) {
        parser.parse(args, 1);

        String instanceName = requiredArg(parser, 0, "instance name");

        InstanceInfo inst = InstanceService.get(instanceName, paths);
        if (inst == null) {
            ConsoleUtils.error("Instance with that name does not exist");
            return ExitCode.USAGE;
        }

        System.out.println("Launching '" + instanceName + "'...");
        int code = InstanceService.launch(inst, paths);
        if (code == 0) {
            ConsoleUtils.success("Engine has terminated with code " + code);
            return ExitCode.SUCCESS;
        } else {
            ConsoleUtils.error("Engine has terminated with code " + code);
            return ExitCode.FAILURE;
        }
    }
}
