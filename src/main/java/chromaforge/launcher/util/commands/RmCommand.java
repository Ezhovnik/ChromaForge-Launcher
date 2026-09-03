package chromaforge.launcher.util.commands;

import chromaforge.launcher.debug.Logger;
import chromaforge.launcher.services.CoreService;

public class RmCommand extends Command {
    private static Logger logger = Logger.getLogger("rm-command");

    public RmCommand() {
        this.keyword = "rm";
        this.args = "<version>";
        this.help = "removes the specified engine version";
    }

    @Override
    public void execute(String[] args) {
        String version = nextArg(args);
        logger.info("Removing '" + version + "' ...");

        try {
            CoreService.removeVersion(version);
            System.out.println("Version " + version + "removed");
        } catch (Exception e) {
            System.out.println("The version" + version + "could not be removed");
        }
    }
}
