package animation;
/*
 * Arielle Tetelbaum
 * 05/23/23
 * A program that creates a looping animation, like a loading icon
*/
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;

public class LoadingIcon extends JFrame implements ActionListener {

	public static void main(String[]args) {
		new LoadingIcon();
	}

	DrawingPanel panel = new DrawingPanel();

	private static final int PANW = 400;
	private static final int PANH = 400;

	ArrayList<Triangle> outerTri = new ArrayList<>(); //The outer triangles that attach and detach from the pentagon are stored here in the arraylist
	PointD[] pentPoints = {new PointD(175, 175), new PointD(160, 220), new PointD(200, 250), new PointD(240, 220), new PointD(225, 175)}; //Points that make up the pentagon
	Point[] moves = {new Point(0, -3), new Point(2, -1), new Point(1, 2),new Point(-1, 2),new Point(-2, -1)}; //An array of how the points of triangles will move (the triangles on the top will be moved up 3 when the triangle moves each time)
	Timer t;
	int time;
	Color color = Color.WHITE; //creating the color used for coloring the shapes as a global variable so it can be changed later in the loop

	LoadingIcon(){
		this.setTitle("Loading Icon");
		panel = new DrawingPanel();
		this.add(panel);
		this.setResizable(false);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);

		//added each triangle to the arraylist
		outerTri.add(new Triangle(225, 175, 175, 175, 200, 125));
		outerTri.add(new Triangle(240, 220, 225, 175, 275, 175));
		outerTri.add(new Triangle(200, 250, 240, 220, 250, 275));
		outerTri.add(new Triangle(160, 220, 200, 250, 150, 275));
		outerTri.add(new Triangle(175, 175, 160, 220, 125, 175));

		Timer t = new Timer(1,this);
		t.start();
	}

	class DrawingPanel extends JPanel {
		DrawingPanel(){
			this.setPreferredSize(new Dimension(PANW, PANH));
			this.setBackground(new Color(0,0,0, 35));
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setStroke(new BasicStroke(1));   

			g2.setColor(color);
			
			//Drawing the triangles from the arraylist
			for(Triangle t : outerTri) {
				Polygon tri = new Polygon();
				tri.addPoint((int)t.x1, (int)t.y1); //finding the individual points for each triangle in the arraylist from the Triangle class.
				tri.addPoint((int)t.x2,(int) t.y2); //In the triangle class, the x and y values are created as doubles so that it can move smoothly when they are rotated later....
				tri.addPoint((int)t.x3, (int)t.y3); //Which is why the x and y values are casted back into an int here
				g2.fillPolygon(tri);
			}

			//creating the pentagon
			Polygon pent = new Polygon();
			for(PointD p : pentPoints) {
				pent.addPoint((int) p.x, (int) p.y); //also have to be casted into int because the points were created as doubles in the class PointD
			}
			g2.fillPolygon(pent);
			
		}
	}

	//Rotate code was taken from quarkphysics, class notes
	PointD rotatePoint(double angle, double x, double y, double centrex, double centrey) {
		double newx = (x-centrex) * Math.cos(angle) + (y-centrey) * Math.sin(angle);
		double newy = -(x-centrex) * Math.sin(angle) + (y-centrey) * Math.cos(angle);
		PointD pd = new PointD(newx+centrex, newy+centrey); 
		return pd;
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		//for the first 24 seconds of the loop, the triangles move out from the pentagon
		if(time < 25) {
			Point move;
			for(Triangle t : outerTri) { //looping through each triangle
				move = moves[outerTri.indexOf(t)]; //grabs the corresponding point from the array of moves for the t triangle
				t.x1 += move.x; //adding the point move to the x and y values of each triangle
				t.x2 += move.x;
				t.x3 += move.x;
				t.y1 += move.y;
				t.y2 += move.y;
				t.y3 += move.y;

				panel.repaint();
			}
		}

		//triangles move 360 times each time moving 1 degree, ending up back at start, same with the pentagon
		if(time >= 25 && time < 385) {
			for(int i = 0; i < 5; i++) {
				Triangle t = outerTri.get(i);
				//recreating each point of a triangle using the rotatePoint method
				PointD pd1 = rotatePoint(Math.PI/180, t.x1, t.y1, PANW/2, PANH/2);
				PointD pd2 = rotatePoint(Math.PI/180, t.x2, t.y2, PANW/2, PANH/2);
				PointD pd3 = rotatePoint(Math.PI/180, t.x3, t.y3, PANW/2, PANH/2);
				t.x1 =  pd1.x; t.y1 =  pd1.y; //the points of the triangle now equal the new rotated points
				t.x2 =  pd2.x; t.y2 =  pd2.y;
				t.x3 =  pd3.x; t.y3 =  pd3.y;

			}
			for(int i = 0; i < 5; i++) {
				//creating point after rotation for each point of the pentagon
				PointD pd1 = rotatePoint(-Math.PI/180, pentPoints[i].x, pentPoints[i].y, PANW/2, PANH/2); //to make the pentagon rotate in the opposite direction of the triangles, added a negative sign to the degree it changes
				pentPoints[i].x = pd1.x; //pentagon point now equals the new rotated point
				pentPoints[i].y = pd1.y; 
			}
			
			panel.repaint();
		}

		//moving the trangles back to the pentagon to create star shape
		if(time > 385 && time <= 410) {
			Point move;
			for(Triangle t : outerTri) {
				move = moves[outerTri.indexOf(t)]; //same thing as the first 24 secs of the loop 
				t.x1 -= move.x; //except now we are subtracting the move from the x and y values
				t.x2 -= move.x;
				t.x3 -= move.x;
				t.y1 -= move.y;
				t.y2 -= move.y;
				t.y3 -= move.y;

				panel.repaint();
			}
		}

		time++;
		if(time > 415) { //shapes are stationary for 5 ms
			time=0; //resetting time, creating the loop
			color = Color.getHSBColor((float)Math.random(), 1.0f, 1.0f); //changes the color of the shapes to a random bright color
		}

	}

	//class to store points of the pentagon as doubles
	private class PointD {
		double x, y;
		
		PointD(double x, double y) {
			this.x = x;
			this.y = y;
		}
	}

}
