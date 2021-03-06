package xbot.common.injection.wpi_factories;

import java.util.function.DoubleFunction;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

import xbot.common.controls.MockRobotIO;
import xbot.common.controls.actuators.XCompressor;
import xbot.common.controls.actuators.XDigitalOutput;
import xbot.common.controls.actuators.XServo;
import xbot.common.controls.actuators.XSolenoid;
import xbot.common.controls.actuators.XSpeedController;
import xbot.common.controls.actuators.wpi_adapters.CompressorWPIAdapter;
import xbot.common.controls.actuators.wpi_adapters.DigitalOutputWPIAdapter;
import xbot.common.controls.actuators.wpi_adapters.SolenoidWPIAdapter;
import xbot.common.controls.actuators.wpi_adapters.SpeedControllerWPIAdapter;
import xbot.common.controls.sensors.AdvancedJoystickButton;
import xbot.common.controls.sensors.AnalogDistanceSensor;
import xbot.common.controls.sensors.AnalogHIDButton;
import xbot.common.controls.sensors.DistanceSensor;
import xbot.common.controls.sensors.Lidar;
import xbot.common.controls.sensors.MockGyro;
import xbot.common.controls.sensors.XAnalogInput;
import xbot.common.controls.sensors.XDigitalInput;
import xbot.common.controls.sensors.XEncoder;
import xbot.common.controls.sensors.XGyro;
import xbot.common.controls.sensors.XJoystick;
import xbot.common.controls.sensors.XPowerDistributionPanel;
import xbot.common.controls.sensors.AnalogHIDButton.AnalogHIDDescription;
import xbot.common.controls.sensors.nav6.Nav6Gyro;
import xbot.common.controls.sensors.wpi_adapters.AnalogInputWPIAdapater;
import xbot.common.controls.sensors.wpi_adapters.DigitalInputWPIAdapter;
import xbot.common.controls.sensors.wpi_adapters.EncoderWPIAdapter;
import xbot.common.controls.sensors.wpi_adapters.JoystickWPIAdapter;
import xbot.common.controls.sensors.wpi_adapters.PowerDistributionPanelWPIAdapter;
import xbot.common.properties.PropertyManager;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.livewindow.LiveWindowSendable;

public class RealWPIFactory implements WPIFactory {

    private static Logger log = Logger.getLogger(RealWPIFactory.class);

    private PropertyManager propMan;

    @Inject
    public RealWPIFactory(PropertyManager propMan) {
        this.propMan = propMan;
    }

    public XSpeedController getSpeedController(int channel) {
        SpeedControllerWPIAdapter controller = new SpeedControllerWPIAdapter(
                channel);
        LiveWindow.addActuator("Actuators", "SpeedController:" + channel,
                (LiveWindowSendable) controller.getInternalController());
        return controller;
    }

    public XJoystick getJoystick(int number) {
        return new JoystickWPIAdapter(number);
    }

    @Override
    public XDigitalInput getDigitalInput(int channel) {
        return new DigitalInputWPIAdapter(channel);
    }

    @Override
    public XAnalogInput getAnalogInput(int channel) {
        AnalogInputWPIAdapater input = new AnalogInputWPIAdapater(channel);
        LiveWindow.addSensor("Analog inputs", "Analog:" + channel,
                (LiveWindowSendable) input.getInternalDevice());
        return input;
    }

    @Override
    public XCompressor getCompressor() {
        return new CompressorWPIAdapter();
    }

    @Override
    public XSolenoid getSolenoid(int channel) {
        return new SolenoidWPIAdapter(channel);
    }

    @Override
    public AdvancedJoystickButton getJoystickButton(XJoystick joystick, int button_number) {
        return new AdvancedJoystickButton(joystick, button_number);
    }

    @Override
    public XGyro getGyro() {
        // It's possible that the nav6 might get disconnected, and throw some
        // exceptions
        // at runtime when it can't communicate over the serial port.
        // The robot needs to protect itself from this behavior.
        try {
            return new Nav6Gyro();
        } catch (Exception e) {
            // We need to return SOMETHING so that downstream consumers don't
            // explode.
            // In this case, we just return a MockGyro that has "isBroken" set
            // to true.
            // That way, nobody throws an exception, and we can test at runtime
            // if we have a bad gyro.

            log.error("Could not create gyro! Returning a \"broken\" MockGyro instead.");

            MockGyro brokenGyro = new MockGyro(new MockRobotIO());
            brokenGyro.setIsBroken(true);
            return brokenGyro;
        }
    }

    @Override
    public XEncoder getEncoder(int aChannel, int bChannel) {
        EncoderWPIAdapter encoder = new EncoderWPIAdapter(aChannel, bChannel);
        LiveWindow.addSensor("Encoders", "Encoder:" + aChannel,
                (LiveWindowSendable) encoder.getInternalEncoder());
        return encoder;
    }

    @Override
    public XServo getServo(int channel) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DistanceSensor getLidar(Port port) {
        return new Lidar(port, propMan);
    }

    @Override
    public DistanceSensor getAnalogDistanceSensor(int channel, DoubleFunction<Double> voltageMap) {
        return new AnalogDistanceSensor(getAnalogInput(channel), voltageMap, propMan);
    }

    public XDigitalOutput getDigitalOutput(int channel) {
        DigitalOutputWPIAdapter adapter = new DigitalOutputWPIAdapter(channel);
        LiveWindow.addSensor("Digital outs", "Out:" + channel,
            (LiveWindowSendable) adapter.getWPIDigitalOutput());
        return adapter;
    }

    @Override
    public AnalogHIDButton getAnalogJoystickButton(XJoystick joystick, int axisNumber,
            double minThreshold, double maxThreshold) {
        return new AnalogHIDButton(joystick, axisNumber, minThreshold, maxThreshold);
    }
    
    @Override
    public AnalogHIDButton getAnalogJoystickButton(XJoystick joystick, AnalogHIDDescription description) {
        return new AnalogHIDButton(joystick, description);
    }

    public XPowerDistributionPanel getPDP() {
        return new PowerDistributionPanelWPIAdapter();
    }

}
