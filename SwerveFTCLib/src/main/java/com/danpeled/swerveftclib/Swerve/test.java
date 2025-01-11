package com.danpeled.swerveftclib.Swerve;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;
import com.qualcomm.robotcore.util.ElapsedTime;


import java.util.List;

@Config
@TeleOp(name="test")
public class test extends OpMode {

    private ServoImplEx sFL;
    private DcMotorEx FL;
    private AnalogInput eFL;
    private double encoder;

    public double power = 0;
    public double position  = .5;

    private ElapsedTime elapsedtime;
    private List<LynxModule> allHubs;



    @Override
    public void init() {

        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        allHubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : allHubs){
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO); //MANUAL
        }

        FL = hardwareMap.get(DcMotorEx.class, "FL");
        sFL = hardwareMap.get(ServoImplEx.class, "sFL");
        sFL.setPwmRange(new PwmControl.PwmRange(505, 2495));
        eFL = hardwareMap.get(AnalogInput.class, "eFL");

        elapsedtime = new ElapsedTime();
        elapsedtime.reset();
    }

    @Override
    public void loop() {

        double xStick = gamepad1.right_stick_x;

        position = 0.5 + (xStick * 0.49);

        sFL.setPosition(position);
        double ystick = gamepad1.left_stick_y;

        FL.setPower(ystick);


        encoder = eFL.getVoltage() / 3.3 * 360;

        telemetry.addData("Run time", getRuntime());


        telemetry.addData("Loop Times", elapsedtime.milliseconds());
        telemetry.addData("encoder", encoder);
        telemetry.addData("power", power);
        telemetry.addData("ystick", ystick);
        elapsedtime.reset();
        telemetry.update();
    }
}