package xbot.common.autonomous;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.Test;

import edu.wpi.first.wpilibj.command.Scheduler;
import sun.util.logging.resources.logging;
import xbot.common.autonomous.TempCommandFactory.ExecutionCounterCommandProvider;
import xbot.common.command.XScheduler;
import xbot.common.injection.BaseWPITest;

public class BaseAutonomousScriptTest extends BaseWPITest {
    static Logger log = Logger.getLogger(BaseAutonomousScriptTest.class);

    @Test
    public void testBasicCommandExecution() {
        TempCommandFactory scriptedCommandFactory = new TempCommandFactory();
        AutonomousScriptedCommand scriptedCommand = new AutonomousScriptedCommand(
                "robot.requireCommands('CounterCommand');\n"
                + "robot.invokeCounterCommand();\n"
                + "robot.checkpointReached('commandInvoked');",
                "TestScript",
                scriptedCommandFactory);
        
        XScheduler scheduler = injector.getInstance(XScheduler.class);
        scheduler.add(scriptedCommand);
        
        for(int numLoops = 0; !scriptedCommand.hasReachedCheckpoint("commandInvoked") && numLoops < 200; numLoops++) {
            scheduler.run();
            
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                log.error("Sleep interrupted while running test... may result in unrealistic faliures!");
            }
        }
        
        assertTrue(scriptedCommand.hasReachedCheckpoint("commandInvoked"));
        
        // Once we know that it hit the checkpoint, give it a chance to run any registered commands
        scheduler.run();
        scheduler.run();
        
        ExecutionCounterCommandProvider lastCommandProvider = scriptedCommandFactory.getLastExecutionCounterCommandProvider();
        assertNotNull(lastCommandProvider);
        ExecutionCounterCommand lastCommand = lastCommandProvider.getLastCommand();
        assertNotNull(lastCommand);

        log.info("Autonomous command was initialized " + lastCommand.getInitCount() + " time(s)"
                + " and executed " + lastCommand.getExecCount() + " time(s).");
        assertTrue(lastCommand.getInitCount() >= 1);
        assertTrue(lastCommand.getExecCount() >= 1);
        
        scriptedCommand.interrupted();
    }

}
