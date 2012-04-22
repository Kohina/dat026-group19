import java.awt.*;

/**
 * Class that handles the physics between two bouncing balls. The class handles
 * the impact of the gravitational force, collision of the two balls and
 * collision to the walls.
 * 
 * @author Nina Malm (910509-3646, ninam@student.chalmers.se, IT)
 * @author Marika Hansson (910305-0804, hmarika@student.chalmers.se, IT) Time
 *         spent: NM: 9.25 h, MH: 9.25 h (18:30)
 * 
 */
public class Bounce extends Animation {

	protected double deltaT, pixelsPerMeter, grav;
	protected int radius, firstTime = 1;
	protected Color color = Color.red;
	protected Ball[] balls = new Ball[2];

	protected void initAnimator() {
		deltaT = 0.005; // simulation time interval in seconds
		setDelay((int) (1000 * deltaT)); // needed for Animation superclass
		pixelsPerMeter = 40;
		grav = 9.8;

		balls[0] = new Ball(3, 3, 20, 3, 2, 9, Color.red);
		balls[1] = new Ball(1, 1, 10, 7, -1, 5, Color.blue);		
	}

	protected void paintAnimator(Graphics g) {
		clearFrame(g);
		
		handleWallCollision(balls[0]);
		handleWallCollision(balls[1]);

		handleCollision(balls[0], balls[1]);
		
		applyGravity(balls[0]);
		applyGravity(balls[1]);
		
		applyPositionChange(balls[0]);
		applyPositionChange(balls[1]);
		
		paintBall(balls[0], g);
		paintBall(balls[1], g);
	}

	private void clearFrame(Graphics g) {
		g.setColor(Color.white);
		if (firstTime == 1) {
			g.fillRect(0, 0, d.width, d.height);
			firstTime = 0;
		} // g.fillRect(0,0,d.width,d.height); // slower?
		
		int pixelX1 = (int) (pixelsPerMeter * balls[0].getX()); // screen position
		int pixelY1 = (int) (pixelsPerMeter * balls[0].getY());
		int pixelX2 = (int) (pixelsPerMeter * balls[1].getX()); // screen position
		int pixelY2 = (int) (pixelsPerMeter * balls[1].getY());
		g.fillOval(pixelX1 - balls[0].getRadius(), d.height - pixelY1
				- balls[0].getRadius(), balls[0].getRadius() * 2,
				balls[0].getRadius() * 2);
		g.fillOval(pixelX2 - balls[1].getRadius(), d.height - pixelY2
				- balls[1].getRadius(), balls[1].getRadius() * 2,
				balls[1].getRadius() * 2);
	}
	
	private void paintBall(Ball ball, Graphics g) {
		int pixelX1 = (int) (pixelsPerMeter * balls[0].getX());
		int pixelY1 = (int) (pixelsPerMeter * balls[0].getY());
		int pixelX2 = (int) (pixelsPerMeter * balls[1].getX());
		int pixelY2 = (int) (pixelsPerMeter * balls[1].getY());


		g.setColor(balls[0].getColor());
		g.fillOval(pixelX1 - balls[0].getRadius(), d.height - pixelY1
				- balls[0].getRadius(), balls[0].getRadius() * 2,
				balls[0].getRadius() * 2);
		g.setColor(balls[1].getColor());
		g.fillOval(pixelX2 - balls[1].getRadius(), d.height - pixelY2
				- balls[1].getRadius(), balls[1].getRadius() * 2,
				balls[1].getRadius() * 2);
	}
	
	private void handleWallCollision(Ball ball) {
		if (ball.getX()*pixelsPerMeter - ball.getRadius() <= 0) { // left wall
			ball.setVelocity(-ball.getVx(), ball.getVy());
		}
		if (ball.getX()*pixelsPerMeter + ball.getRadius() >= d.width) { // right wall
			ball.setVelocity(-ball.getVx(), ball.getVy());
		}
		if (ball.getY()*pixelsPerMeter - ball.getRadius() <= 0) { // bottom wall
			ball.setVelocity(ball.getVx(), -ball.getVy());
		}
		if (ball.getY()*pixelsPerMeter + ball.getRadius() >= d.height) { // top wall
			ball.setVelocity(ball.getVx(), -ball.getVy());
		}
	}
	
	private void handleCollision(Ball ball1, Ball ball2) {
		int dX = (int)((ball1.getX()-ball2.getX())*pixelsPerMeter); // Avst�nd mellan bollarna i x-led
		int dY = (int)((ball1.getY()-ball2.getY())*pixelsPerMeter); // Avst�nd mellan bollarna i y-led
		
		if (Math.sqrt(dX*dX + dY*dY) <= ball1.getRadius() + ball2.getRadius()) {
			double a = Math.atan((double) dY / dX); // vinkeln som bollarna tr�ffas i

			double V1 = Math.sqrt(Math.pow(ball1.getVx(), 2) + Math.pow(ball1.getVy(), 2));
			double aV1 = Math.atan(ball1.getVy() / ball1.getVx());
			double V2 = Math.sqrt(Math.pow(ball2.getVx(), 2) + Math.pow(ball2.getVy(), 2));
			double aV2 = Math.atan(ball2.getVy() / ball2.getVx());
			double a1 = aV1 - a;
			double a2 = aV2 - a;

			double Vf1 = V1 * Math.cos(a1);	//velocity along the line between the centers of the balls (f-axis)
			double Vg1 = V1 * Math.sin(a1); //velocity along the line perpendicular to the line between the centers of the balls (g-axis)
			double Vf2 = V2 * Math.cos(a2);
			double Vg2 = V2 * Math.sin(a2);

			int m1 = ball1.getMass();
			int m2 = ball2.getMass();
			double Uf1 = Vf1;
			double Uf2 = Vf2;
			Vf1 = (m1 * Uf1 + 2 * m2 * Uf2 - m2 * Uf1) / (m1 + m2);
			Vf2 = -Uf2 + Uf1 + Vf1;

			double b1 = Math.atan(Vg1 / Vf1); //angle between the f-axis and the new velocity
			double b2 = Math.atan(Vg2 / Vf2);
			V1 = Math.sqrt(Math.pow(Vf1, 2) + Math.pow(Vg1, 2));
			V2 = Math.sqrt(Math.pow(Vf2, 2) + Math.pow(Vg2, 2));

			ball1.setVelocity(V1 * Math.cos(a + b1), V1 * Math.sin(a + b1));
			ball2.setVelocity(V2 * Math.cos(a + b2), V2 * Math.sin(a + b2));
			
			fixOverlap(ball1, ball2);
		}
	}
	
	private void fixOverlap(Ball ball1, Ball ball2) {
		double radiusSum = (double)((ball1.getRadius() + ball2.getRadius()));
		double dX = (ball1.getX()-ball2.getX())*pixelsPerMeter;
		double dY = (ball1.getY()-ball2.getY())*pixelsPerMeter;
		
		if (dX > 0) {
			double overX = (dX - radiusSum)/pixelsPerMeter;
			if(overX < 0){
				ball1.setPosition(ball1.getX()-overX/2, ball1.getY());
				ball2.setPosition(ball1.getX()+overX/2, ball1.getY());
			}
		} else {
			double overX = (dX + radiusSum)/pixelsPerMeter;
			if(overX > 0){
				ball1.setPosition(ball1.getX()-overX/2, ball1.getY());
				ball2.setPosition(ball1.getX()+overX/2, ball1.getY());
			}
		}
		
		if (dY > 0) {
			double overY = (dY - radiusSum)/pixelsPerMeter;
			if(overY < 0){
				ball1.setPosition(ball1.getX(), ball1.getY()-overY/2);
				ball2.setPosition(ball2.getX(), ball2.getY()+overY/2);
			}
		} else {
			double overY = (dY + radiusSum)/pixelsPerMeter;
			if(overY > 0){
				ball1.setPosition(ball1.getX(), ball1.getY()-overY/2);
				ball2.setPosition(ball2.getX(), ball2.getY()+overY/2);
			}
		}
	}
	
	private void applyGravity(Ball ball) {
		ball.setVelocity(ball.getVx(), ball.getVy() - grav * deltaT);
	}
	
	private void applyPositionChange(Ball ball) {
		ball.setPosition(ball.getX() + ball.getVx() * deltaT,
				ball.getY() + ball.getVy() * deltaT);
	}
	
}