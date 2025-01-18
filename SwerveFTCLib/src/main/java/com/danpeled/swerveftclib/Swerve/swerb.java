package com.danpeled.swerveftclib.Swerve;

import static com.sun.tools.javac.code.Type.map;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.ServoImplEx;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import java.util.List;
import java.util.Map;

@Config
@TeleOp(name="swerb")
public class swerb extends OpMode {

    private ServoImplEx sFl, sFr, sBl, sBr;
    private DcMotorEx FL, FR, BL, BR;
    //private AnalogInput eFL, eFr, eBl, eBr;
    //private double flEncoder, frEncoder, blEncoder, brEncoder;

    public double power = 0;
    public double angle = 0;

    public double position  = .5;

    private ElapsedTime elapsedtime;
    private List<LynxModule> allHubs;

    public static double frMin =.01;
    public static double flMin =.31;
    public static double blMin =.01;

    public static double frMax =.58;
    public static double flMax =.89;
    public static double blMax =.59;

    @Override
    public void init() {

        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        allHubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : allHubs){
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO); //MANUAL
        }

        FL = hardwareMap.get(DcMotorEx.class, "FL");
        FL.setDirection(DcMotorEx.Direction.REVERSE);
        sFl = hardwareMap.get(ServoImplEx.class, "sFl");
        sFl.setPwmRange(new PwmControl.PwmRange(505, 2495));
        //eFL = hardwareMap.get(AnalogInput.class, "eFL");

        FR = hardwareMap.get(DcMotorEx.class, "FR");
        sFr = hardwareMap.get(ServoImplEx.class, "sFr");
        sFr.setPwmRange(new PwmControl.PwmRange(505, 2495));
        //eFr = hardwareMap.get(AnalogInput.class, "eFr");

        BL = hardwareMap.get(DcMotorEx.class, "BL");
        BL.setDirection(DcMotorEx.Direction.REVERSE);
        sBl = hardwareMap.get(ServoImplEx.class, "sBl");
        sBl.setPwmRange(new PwmControl.PwmRange(505, 2495));
        //eBl = hardwareMap.get(AnalogInput.class, "eBl");

        BR = hardwareMap.get(DcMotorEx.class, "BR");
        sBr = hardwareMap.get(ServoImplEx.class, "sBr");
        sBr.setPwmRange(new PwmControl.PwmRange(505, 2495));
        //eBr = hardwareMap.get(AnalogInput.class, "eBr");


        elapsedtime = new ElapsedTime();
        elapsedtime.reset();
    }

    @Override
    public void loop() {

        double x = gamepad1.right_stick_x;
        double y = -gamepad1.right_stick_y;

        if ((x>=0 && y>=0)||(x<=0 && y>=0)) {
            power = Math.abs(Math.sqrt((x*x)+(y*y)));
        }
        else if ((x>=0 && y<=0)||(x<=0 && y<=0)) {
            power = -Math.abs(Math.sqrt((x*x)+(y*y)));
        }
        angle = Math.toDegrees(Math.atan2(y, x));

        if(angle <0 && angle > -180){
            angle += 180;
        }
        if (angle == -180){
            angle +=360;
        }
        double motorPower = Range.clip(power, -1,1);

        double frPosition = mapAngleToServo(angle, frMin, frMax);
        double flPosition = mapAngleToServo(angle, flMin, flMax);
        double blPosition = mapAngleToServo(angle, blMin, blMax);

        sFr.setPosition(frPosition);
        sFl.setPosition(flPosition);
        sBl.setPosition(blPosition);

        FR.setPower(motorPower);
        FL.setPower(motorPower);
        BL.setPower(motorPower);

        //debug
/*
        if (gamepad1.b)  {
            sFr.setPosition(frMin);
            sFl.setPosition(flMin);
            sBl.setPosition(blMin);
        }
        if (gamepad1.x){
            sFr.setPosition(frMax);
            sFl.setPosition(flMax);
            sBl.setPosition(blMax);
        }*/


        telemetry.addData("Run time", getRuntime());

        telemetry.addData("Loop Times", elapsedtime.milliseconds());
        telemetry.addData("power", power);
        telemetry.addData("motorPower", motorPower);
        telemetry.addData("y", y);
        telemetry.addData("x", x);
        telemetry.addData("angle", angle);
        telemetry.addData("frPosition", frPosition);
        telemetry.addData("flPosition", flPosition);
        telemetry.addData("blPosition", blPosition);
        elapsedtime.reset();
        telemetry.update();
    }
    public static double mapAngleToServo(double angle, double min, double max) {
        // Ensure angle is within the range [0, 180]
        angle = Math.max(0, Math.min(angle, 180));
        return min + ((angle / 180.0) * (max - min));
    }
}