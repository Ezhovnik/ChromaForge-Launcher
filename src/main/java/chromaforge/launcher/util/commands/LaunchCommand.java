package chromaforge.launcher.util.commands;

import chromaforge.launcher.io.LauncherPaths;
import chromaforge.launcher.services.InstanceService;
import chromaforge.launcher.util.ConsoleUtils;
import chromaforge.launcher.util.InstanceInfo;

public class LaunchCommand extends Command {

    public LaunchCommand() {
        this.keyword = "launch";
        this.args = "<instance name>";
        this.help = "runs the specified instance";
    }

    @Override
    public void execute(String[] args, LauncherPaths paths) {
        parser.parse(args, 1);

        String instanceName = requiredArg(parser, 0, "instance name");

        InstanceInfo inst = InstanceService.get(instanceName, paths);
        if (inst == null) {
            ConsoleUtils.error("Instance with that name does not exist");
            return;
        }

        System.out.println("Launching '" + instanceName + "'...");
        InstanceService.launch(inst, paths);
    }
}
