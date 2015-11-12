package xbot.common.command.scripted;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import xbot.common.command.XScheduler;
import xbot.common.command.scripted.TestCommandFactory.ExecutionCounterCommandProvider;
import xbot.common.injection.BaseWPITest;

@Ignore
public class BaseScriptedCommandTest extends BaseWPITest {
    static Logger log = Logger.getLogger(BaseScriptedCommandTest.class);
    
    TestCommandFactory scriptedCommandFactory = new TestCommandFactory();
    XScheduler scheduler;
    
    @Before
    @Override
    public void setUp() {
        super.setUp();
        scheduler = injector.getInstance(XScheduler.class);
    }
    
    protected ExecutionCounterCommand getLastCounterCommand() {
        
        ExecutionCounterCommandProvider lastCommandProvider = scriptedCommandFactory.getLastExecutionCounterCommandProvider();
        if(lastCommandProvider == null)
            return null;
        
        return lastCommandProvider.getLastCommand();
    }
    
    protected ExecutionCounterCommand assertLastCounterCommand() {
        
        ExecutionCounterCommandProvider lastCommandProvider = scriptedCommandFactory.getLastExecutionCounterCommandProvider();
        assertNotNull(lastCommandProvider);
        
        ExecutionCounterCommand lastCommand = lastCommandProvider.getLastCommand();
        assertNotNull(lastCommand);
        
        return lastCommand;
    }
    
    protected void sleepThread(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            log.error("Sleep interrupted while running test... may result in unrealistic faliures!");
        }
    }
    
    protected void assertCounterExecuted(ExecutionCounterCommand command) {
        log.info("Scripted command was initialized " + command.getInitCount() + " time(s)"
                + " and executed " + command.getExecCount() + " time(s).");
        assertTrue(command.getInitCount() >= 1);
        assertTrue(command.getExecCount() >= 1);
    }
}