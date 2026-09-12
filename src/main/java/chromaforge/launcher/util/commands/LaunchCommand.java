package chromaforge.launcher.util.commands;

import java.util.ArrayList;
import java.util.List;

import chromaforge.launcher.io.LauncherPaths;
import chromaforge.launcher.services.InstanceService;
import chromaforge.launcher.util.ConsoleUtils;
import chromaforge.launcher.util.ExitCode;
import chromaforge.launcher.util.InstanceInfo;

public class LaunchCommand extends Command {

    public LaunchCommand() {
        this.keyword = "launch";
        this.args = "<instance name> [-- <engine args>]";
        this.help = "runs the specified instance";
        this.long_help = "runs the specified instance.\n"
            + "    Everything after '--' is passed straight to the engine;\n"
            + "    e.g. 'launch test -- --headless' runs it in headless mode.";
    }

    @Override
    public ExitCode execute(String[] args, LauncherPaths paths) {
        List<String> engineArgs = new ArrayList<>();
        List<String> launcherArgs = new ArrayList<>();
        boolean afterSeparator = false;
        for (String arg : args) {
            if (afterSeparator) {
                engineArgs.add(arg);
            } else if (arg.equals("--")) {
                afterSeparator = true;
            } else {
                launcherArgs.add(arg);
            }
        }

        parser.parse(launcherArgs.toArray(new String[0]), 1);

        String instanceName = requiredArg(parser, 0, "instance name");

        InstanceInfo inst = InstanceService.get(instanceName, paths);
        if (inst == null) {
            ConsoleUtils.error("Instance '" + instanceName + "' does not exist");
            return ExitCode.USAGE;
        }

        System.out.println("Launching '" + instanceName + "'...");
        int code = InstanceService.launch(inst, paths, engineArgs);
        if (code == 0) {
            ConsoleUtils.success("Engine has terminated with code " + code);
            return ExitCode.SUCCESS;
        } else {
            ConsoleUtils.error("Engine has terminated with code " + code);
            return ExitCode.FAILURE;
        }
    }
}
