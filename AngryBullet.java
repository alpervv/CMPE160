import java.awt.Font;
import java.awt.event.KeyEvent;
import java.lang.Math;

/**
 * Creates a simple shooting simulation game.
 * Change the angle that bullet will be shot by Up and Down arrows.
 * Change the initial speed of the bullet by Left and Right arrows.
 * Obstacles are displayed with grey rectangles, targets are displayed with orange rectangles.
 * Press "space bar" to shoot, press "r" if you want to try again!
 * @author Alper Vural, Student ID: 2023400066
 * @since Date: 03/13/2024
 */
public class AlperVural {
    public static void main(String[] args) {
        // Game Parameters
        int width = 1600; //screen width
        int height = 800; // screen height
        double gravity = 9.80665; // gravity
        double x0 = 120; // x and y coordinates of the bullet’s starting position on the platform
        double y0 = 120;
        double bulletVelocity = 180; // initial velocity
        double bulletAngle = 45.0; // initial angle
        // Box coordinates for obstacles and targets
        // Each row stores a box containing the following information:
        // x and y coordinates of the lower left rectangle corner, width, and height
        double[][] obstacleArray = {
                {1200, 0, 60, 220},
                {1000, 0, 60, 160},
                {600, 0, 60, 80},
                {600, 180, 60, 160},
                {220, 0, 120, 180}
        };
        double[][] targetArray = {
                {1160, 0, 30, 30},
                {730, 0, 30, 30},
                {150, 0, 20, 20},
                {1480, 0, 60, 60},
                {340, 80, 60, 30},
                {1500, 600, 60, 60}
        };

        // My personally created game environment
        /*
        double[][] obstacleArray = {
                {350,100,50,300},
                {700,100,50,300},
                {460,380,180,5},
                {810,60,5,180},
                {880,220,75,75},
                {1065,0,60,200},
                {140,50,99,10}
        };
        double[][] targetArray = {
                {500,250,100,100},
                {900,0,75,150},
                {1100,400,50,50},
                {1170,0,40,60},
                {400,100,30,30},
                {180,0,60,50}
        };*/


        StdDraw.enableDoubleBuffering(); // enable Double buffering for better graphics
        StdDraw.setCanvasSize(width,height); // set the canvas
        StdDraw.setXscale(0,width);
        StdDraw.setYscale(0,height);

        double gravityOriginal = gravity; // Save original values of gravity, velocity, and angle. Because they will be changed during gameplay.
        double velocityOriginal = bulletVelocity;
        double angleOriginal = bulletAngle;



        // Game code
        while (true) {
            drawStartingScreen(obstacleArray,targetArray,bulletAngle,bulletVelocity,x0,y0);
            int pauseDuration = 100; // pause duration after each keypress (in milliseconds)

            // Adjust bullet angle and velocity with arrows
            if (StdDraw.isKeyPressed(KeyEvent.VK_LEFT)) {
                bulletVelocity -= 1;
                StdDraw.pause(pauseDuration);
            }
            if (StdDraw.isKeyPressed(KeyEvent.VK_RIGHT)) {
                bulletVelocity += 1;
                StdDraw.pause(pauseDuration);
            }
            if (StdDraw.isKeyPressed(KeyEvent.VK_UP)) {
                bulletAngle += 1;
                StdDraw.pause(pauseDuration);
            }
            if (StdDraw.isKeyPressed(KeyEvent.VK_DOWN)){
                bulletAngle -= 1;
                StdDraw.pause(pauseDuration);
            }

            // Enter projectile motion function when space bar is pressed.
            if (StdDraw.isKeyPressed(KeyEvent.VK_SPACE)){
                // Assume the ball initially moves with a speed of bulletVelocity/s
                // Display the motion frame by frame
                // Frames have 0.2s between them

                // We have to scale gravity and bulletVelocity because the default values don't work properly!!!
                bulletVelocity = bulletVelocity/2;
                gravity = gravity*0.75;

                // Create conditionals regarding all possible endings
                boolean isObstacleHit = false;
                boolean isTargetHit = false;
                boolean isOutX = false; // x max reached
                boolean isGroundHit = false;

                double t = 0.0; // time
                double prevPointX = x0; // Coordinates of previous point, that is the point where the bullet was 0.2s ago
                double prevPointY = y0; // Initially set to x0 and y0, position of bullet at t=0

                StdDraw.setPenColor(StdDraw.BLACK); // Points and lines are drawn in black

                while (!(isTargetHit || isObstacleHit || isOutX || isGroundHit)){
                    // Update frames until something is hit or x max reached

                    double bulletAngleRadians = bulletAngle*Math.PI/180; // Convert angle to radians

                    // Calculate the position of the object with projectile motion formulas
                    double bulletX = x0 + bulletVelocity*Math.cos(bulletAngleRadians)*t; // x=x_0+vt
                    double bulletY = y0 + bulletVelocity*Math.sin(bulletAngleRadians)*t - gravity*t*t/2; // y=y_0+vt+1/2gt^2

                    StdDraw.setPenRadius(0.015); // Pen thickness for points
                    StdDraw.point(bulletX,bulletY); // Draw a point at the current position of bullet
                    StdDraw.setPenRadius(0.002); // Pen thickness for lines between points
                    StdDraw.line(prevPointX, prevPointY, bulletX, bulletY); // Draw a line connecting current and previous points
                    StdDraw.show();


                    // Check if something is hit or out of window
                    // Divide the line between two points into 100 subintervals, totailng to 101 points
                    // We divide the interval in order to increase accuracy
                    // Otherwise the code will not detect passing through thin objects
                    // Respectively check if each point is in obstacle, target or out of window
                    double inspectedPointX;
                    double inspectedPointY;
                    for (double i = 0.0; i<=1.0; i += 0.01){
                        inspectedPointX = prevPointX + (bulletX-prevPointX)*i; // x and y coordinates of one of the 101 points
                        inspectedPointY = prevPointY + (bulletY-prevPointY)*i;
                        if (bulletY < 0){
                            isGroundHit = true;
                            break;
                        }

                        if (bulletX > width){
                            isOutX = true;
                            break;
                        }

                        for (double[] obstacle: obstacleArray){ // check each obstacle to see if inspected point lies inside
                            double obstacleLeft = obstacle[0];
                            double obstacleRight = obstacleLeft + obstacle[2];
                            double obstacleBottom = obstacle[1];
                            double obstacleTop = obstacleBottom + obstacle[3];
                            if (obstacleLeft <= inspectedPointX && inspectedPointX <= obstacleRight && obstacleBottom <= inspectedPointY && inspectedPointY <= obstacleTop){
                                isObstacleHit = true;
                                break;
                            }
                        }
                        if (isObstacleHit) // end inspection totally as we detected that an obstacle is hit
                            break;

                        for (double[] target: targetArray){ // check each target to see if inspected point lies inside
                            double targetLeft = target[0];
                            double targetRight = targetLeft + target[2];
                            double targetBottom = target[1];
                            double targetTop = targetBottom + target[3];
                            if (targetLeft <= inspectedPointX && inspectedPointX <= targetRight && targetBottom <= inspectedPointY && inspectedPointY <= targetTop){
                                isTargetHit = true;
                                break;
                            }
                        }
                        if (isTargetHit) // end inspection totally as we detected that a target is hit
                            break;
                    } // end of the loop checking if something is hit or x max reached

                    t += 0.2; // increase time
                    prevPointX = bulletX; // set current point as previous point before proceeding to next point
                    prevPointY = bulletY;
                } // end of one shot

                Font afterShotMessageFont = new Font("Arial",Font.BOLD,20); // Font for the message at top left of the screen
                StdDraw.setPenColor(StdDraw.BLACK); // color of the message at top left of the screen
                StdDraw.setFont(afterShotMessageFont);

                // Winning condition
                if (isTargetHit){
                    StdDraw.textLeft(20,height-30,"Congratulations: You hit the target!"); // Display winning message
                    StdDraw.show();
                    while (!StdDraw.isKeyPressed(KeyEvent.VK_R))
                        continue; // Wait until r is pressed
                }

                // Losing Conditions
                if (isGroundHit){
                    StdDraw.textLeft(20,height-30,"Hit the ground. Press 'r' to shoot again."); // Display ground hit message
                    StdDraw.show();
                    while (!StdDraw.isKeyPressed(KeyEvent.VK_R))
                        continue; // Do nothing until r is pressed
                }
                if (isObstacleHit){
                    StdDraw.textLeft(20,height-30,"Hit an obstacle. Press 'r' to shoot again."); // Display obstacle hit message
                    StdDraw.show();
                    while (!StdDraw.isKeyPressed(KeyEvent.VK_R))
                        continue; // Do nothing until r is pressed
                }
                if (isOutX){
                    StdDraw.textLeft(20,height-30,"Max X reached. Press 'r' to shoot again."); // Display x max reached message
                    StdDraw.show();
                    while (!StdDraw.isKeyPressed(KeyEvent.VK_R))
                        continue; // Do nothing until r is pressed
                }

                bulletVelocity = velocityOriginal; // Change velocity, angle and gravity back to their original values as they were scaled
                bulletAngle = angleOriginal;
                gravity = gravityOriginal;
            }
        }
    }

    /** Draws the starting screen where player can adjust the angle and velocity
     * @param obstacleArray A 2d array of obstacles
     * @param targetArray A 2d array of targets
     * @param bulletAngle Angle of bullet's initial velocity makes with the ground
     * @param bulletVelocity Bullet's speed at t=0
     * @param x0 Width of the launchpad
     * @param y0 Height of the launchpad
     *
     */
    public static void drawStartingScreen(double[][] obstacleArray,double[][] targetArray,double bulletAngle,double bulletVelocity,double x0,double y0){
        // Draw the launchpad
        StdDraw.clear(StdDraw.WHITE);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.filledSquare(x0/2,y0/2,x0/2);
        StdDraw.setPenColor(StdDraw.WHITE);
        Font launchpadFont = new Font("Arial",Font.PLAIN,(int)y0/6);
        StdDraw.setFont(launchpadFont);
        StdDraw.textLeft(x0/6,y0*7/12,"a: " + bulletAngle);
        StdDraw.textLeft(x0/6,y0*5/12,"v: " + bulletVelocity);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        // Don't forget convert degrees to radians
        double bulletAngleRadians = bulletAngle*Math.PI/180;
        // Draw bullet's direction pointer
        StdDraw.line(x0,y0,x0+(60+2*bulletVelocity-2*180)*Math.cos(bulletAngleRadians),y0+(60+2*bulletVelocity-2*180)*Math.sin(bulletAngleRadians));

        // Draw the obstacles and targets
        StdDraw.setPenColor(StdDraw.DARK_GRAY);
        for (double[] obstacle: obstacleArray){
            double centerX = obstacle[0] + obstacle[2]/2;
            double centerY = obstacle[1] + obstacle[3]/2;
            StdDraw.filledRectangle(centerX,centerY,obstacle[2]/2,obstacle[3]/2);
        }
        StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE);
        for (double[] target: targetArray){
            double centerX = target[0] + target[2]/2;
            double centerY = target[1] + target[3]/2;
            StdDraw.filledRectangle(centerX,centerY,target[2]/2,target[3]/2);
        }
        StdDraw.show();
    }

}