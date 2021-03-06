package xbot.common.command;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import xbot.common.injection.RobotModule;
import xbot.common.properties.PropertyManager;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to each mode, as
 * described in the IterativeRobot documentation. If you change the name of this class or the package after creating
 * this project, you must also update the manifest file in the resource directory.
 */

public class BaseRobot extends IterativeRobot {

    static Logger log = Logger.getLogger(BaseRobot.class);

    protected PropertyManager propertyManager;
    protected XScheduler xScheduler;

    protected AbstractModule injectionModule;

    // Other than initially creating required systems, you should never use the injector again
    protected Injector injector;

    public BaseRobot() {
        super();
        // Override in constructor if you need a different module
        this.injectionModule = new RobotModule();
    }

    /**
     * This function is run when the robot is first started up and should be used for any initialization code.
     * 
     * Info on the warning suppression
     * http://help.eclipse.org/indigo/index.jsp?topic=%2Forg.eclipse.jdt.doc.user%2Ftasks%2Ftask-suppress_warnings.htm
     */
    public void robotInit() {

        // Get our logging config
        try {
            DOMConfigurator.configure("log4j.xml");
        } catch (Exception e) {
            // Had a problem loading the config. Robot should continue!
            System.out.println("Couldn't configure logging - file probably missing or malformed");
        }

        this.injector = Guice.createInjector(this.injectionModule);
        this.initializeSystems();
        SmartDashboard.putData(Scheduler.getInstance());
    }

    protected void initializeSystems() {
        // override with additional systems (but call this one too)

        // Get the property manager and get all properties from the robot disk
        propertyManager = this.injector.getInstance(PropertyManager.class);
        xScheduler = this.injector.getInstance(XScheduler.class);
    }

    @Override
    public void disabledInit() {
        log.info("Disabled init");
        propertyManager.saveOutAllProperties();
    }

    public void disabledPeriodic() {
        xScheduler.run();
    }

    public void autonomousInit() {
        log.info("Autonomous init");
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
        xScheduler.run();
    }

    public void teleopInit() {
        log.info("Teleop init");
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
        xScheduler.run();
    }

    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
        LiveWindow.run();
    }
}
