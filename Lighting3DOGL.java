package Lab6;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class Lighting3DOGL implements GLEventListener,MouseMotionListener {

    JFrame jf;
    GLJPanel gljpanel;
    Dimension dim=new Dimension(800,600);
    FPSAnimator animator;


    float xcamrot=0.0f;
    float lightdis=1.0f;
    float time;
    float cycletime=10.0f;
    static int framerate=60;
    float lightpos[]={50.0f,100.0f, 200.0f, 1.0f};

    public  Lighting3DOGL()
    {
        jf = new JFrame();
        gljpanel = new GLJPanel();
        gljpanel.addGLEventListener(this);
        gljpanel.requestFocusInWindow();
        jf.getContentPane().add(gljpanel);

        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setVisible(true);
        jf.setPreferredSize(dim);
        jf.pack();
        animator = new FPSAnimator(gljpanel, framerate);
        time = 0.0f;
        gljpanel.addMouseMotionListener(this);
        animator.start();


    }
    @Override
    public void init(GLAutoDrawable dr ) {
        GL2 gl2 = dr.getGL().getGL2();
        GLU glu = new GLU();
        GLUT glut = new GLUT();
        gl2.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl2.glEnable(GL2.GL_DEPTH_TEST);

        gl2.glEnable(GL2.GL_LIGHTING);

        gl2.glMatrixMode(GL2.GL_PROJECTION);
        gl2.glLoadIdentity();
        glu.gluPerspective(80.0, 1.0, 50.0, 3000.0);
        gl2.glMatrixMode(GL2.GL_MODELVIEW);
        gl2.glLoadIdentity();
        glu.gluLookAt(0.0, 80.0, 500.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);

        gl2.glEnable(GL2.GL_NORMALIZE);

    }

    public static void main(String[] args) {
        new Lighting3DOGL();
    }



    static void projectShadow(GL2 gl, float s[], float n[], float l[]) {
        float w, m;
        float mat[] = new float[4 * 4];

        w = (s[0] - l[0]) * n[0] + (s[1] - l[1]) * n[1] + (s[2] - l[2]) * n[2];
        m = l[0] * n[0] + l[1] * n[1] + l[2] * n[2];

        mat[index(0, 0)] = w + n[0] * l[0];
        mat[index(0, 1)] = n[1] * l[0];
        mat[index(0, 2)] = n[2] * l[0];
        mat[index(0, 3)] = -(w + m) * l[0];

        mat[index(1, 0)] = n[0] * l[1];
        mat[index(1, 1)] = w + n[1] * l[1];
        mat[index(1, 2)] = n[2] * l[1];
        mat[index(1, 3)] = -(w + m) * l[1];

        mat[index(2, 0)] = n[0] * l[2];
        mat[index(2, 1)] = n[1] * l[2];
        mat[index(2, 2)] = w + n[2] * l[2];
        mat[index(2, 3)] = -(w + m) * l[2];

        mat[index(3, 0)] = n[0];
        mat[index(3, 1)] = n[1];
        mat[index(3, 2)] = n[2];
        mat[index(3, 3)] = -m;

        gl.glMultMatrixf(mat, 0);

    }

    private static int index(int j, int i) {
        return j + 4 * i;
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {

    }

    @Override
    public void display(GLAutoDrawable dr) {
        // "Save the Screens"
        GL2 gl2 = dr.getGL().getGL2();
        GLU glu = new GLU();
        GLUT glut = new GLUT();


        gl2.glEnable(GL2.GL_LIGHTING);
        gl2.glPushMatrix();

        gl2.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        float ac[] = { 0.2f, 0.2f, 0.2f, 1.0f };
        gl2.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, ac, 0);
        gl2.glEnable(GL2.GL_LIGHT1);
        float dc[] = { 3.0f, 3.0f, 3.0f, 1.0f };
        float sc[] = { 3.0f, 3.0f, 3.0f, 1.0f };
        gl2.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, lightpos, 0);
        gl2.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, dc, 0);
        gl2.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPECULAR, sc, 0);

        // Draw the Cube......
        gl2.glEnable(GL2.GL_LIGHTING);
        drawcube(gl2, glu, glut, time);

        // draw the shadow
        gl2.glDisable(GL2.GL_LIGHTING);
        gl2.glEnable(GL2.GL_POLYGON_OFFSET_FILL);
        gl2.glPolygonOffset(-1.0f, -1.0f);

        float ground[] = { 0.0f, -150.0f, 0.0f };
        float groundnormal[] = { 0.0f, -1.0f, 0.0f };

        gl2.glPushMatrix();
        gl2.glColor3d(0.0, 0.0, 0.0);
        projectShadow(gl2, ground, groundnormal, lightpos);
        drawcube(gl2, glu, glut, time);

        gl2.glPopMatrix();
        gl2.glDisable(GL2.GL_POLYGON_OFFSET_FILL);
        gl2.glEnable(GL2.GL_LIGHTING);

        // draw the floor
        float dff[] = { 0.7f, 0.3f, 1.0f, 0.0f };
        gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE,
                dff, 0);
        gl2.glPushMatrix();
        gl2.glTranslated(-1.0, 1.0, 0.0);
        gl2.glBegin(GL2.GL_POLYGON);
        gl2.glVertex3d(-280.0, 4.0, -55.0);
        gl2.glVertex3d(-280.0, -450.0, -55.0);
        gl2.glVertex3d(280.0, -450.0, -55.0);
        gl2.glVertex3d(280.0, 4.0, -55.0);
        gl2.glEnd();
        gl2.glPopMatrix();

        gl2.glPushMatrix(); // draw the light source's position using a yellow
        // sphere
        gl2.glDisable(GL2.GL_LIGHTING);
        gl2.glColor3d(0.95, 0.9, 0.0);
        gl2.glTranslated(lightpos[0], lightpos[1], lightpos[2]);
        glut.glutSolidSphere(3.0, 100, 100);
        gl2.glPopMatrix();

        gl2.glFlush();

        // update the angle
        time += 1.0f / framerate;
        if (time > cycletime)
            time = 0.0f;


    }

    // Method for drawing the cube
    private void drawcube(GL2 gl2, GLU glu, GLUT glut, float time) {
        gl2.glPushMatrix();
        gl2.glRotated(time*20.0f, 0.1f, 1.0f, 0.0f);
        gl2.glRotated(xcamrot, 1.0f, 0.0f, 0.0f);
        float df[]={0.0f, 0.2f, 1.0f, 0.0f};
        float df1[]={0.0f, 1.0f, 0.4f, 0.0f};
        float df2[]={0.5f, 1.2f, 1.0f, 0.0f};
        float df3[]={0.0f, 0.8f, 1.5f, 0.0f};
        float df4[]={1.0f, 0.2f, 0.0f, 0.0f};
        float df5[]={0.0f, 1.0f, 0.5f, 0.0f};
        gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE,df,0);
        float sf[]= {1.0f,1.0f,1.0f,0.0f};
        gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK,GL2.GL_SPECULAR,sf,0);
        gl2.glMaterialf(GL2.GL_FRONT_AND_BACK,GL2.GL_SHININESS,120.0f);
        glut.glutSolidCube(40);
        gl2.glTranslated(42,0,0);
        gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE,df3,0);
        glut.glutSolidCube(40);
        gl2.glTranslated(42,0,0);
        gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE,df5,0);
        glut.glutSolidCube(40);

        gl2.glTranslated(0,-42,0);
        gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE,df1,0);
        glut.glutSolidCube(40);
        gl2.glTranslated(-42,0,0);
        gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE,df3,0);
        glut.glutSolidCube(40);
        gl2.glTranslated(-42,0,0);
        gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE,df5,0);
        glut.glutSolidCube(40);

        gl2.glTranslated(0,-42,0);
        gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE,df4,0);
        glut.glutSolidCube(40);
        gl2.glTranslated(42,0,0);
        gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE,df1,0);
        glut.glutSolidCube(40);
        gl2.glTranslated(42,0,0);
        gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE,df2,0);
        glut.glutSolidCube(40);

        gl2.glTranslated(0,0,42);
        gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE,df5,0);
        glut.glutSolidCube(40);
        gl2.glTranslated(-42,0,0);
        gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE,df2,0);
        glut.glutSolidCube(40);
        gl2.glTranslated(-42,0,0);
        gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE,df3,0);
        glut.glutSolidCube(40);

        gl2.glTranslated(0,42,0);
        gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE,df,0);
        glut.glutSolidCube(40);
        gl2.glTranslated(42,0,0);
        gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE,df1,0);
        glut.glutSolidCube(40);
        gl2.glTranslated(42,0,0);
        gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE,df2,0);
        glut.glutSolidCube(40);

        gl2.glTranslated(0,42,0);
        gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE,df2,0);
        glut.glutSolidCube(40);
        gl2.glTranslated(-42,0,0);
        gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE,df,0);
        glut.glutSolidCube(40);
        gl2.glTranslated(-42,0,0);
        gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE,df5,0);
        glut.glutSolidCube(40);

        gl2.glTranslated(0,0,42);
        gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE,df4,0);
        glut.glutSolidCube(40);
        gl2.glTranslated(42,0,0);
        gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE,df,0);
        glut.glutSolidCube(40);
        gl2.glTranslated(42,0,0);
        gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE,df2,0);
        glut.glutSolidCube(40);

        gl2.glTranslated(0,-42,0);
        gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE,df3,0);
        glut.glutSolidCube(40);
        gl2.glTranslated(-42,0,0);
        gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE,df1,0);
        glut.glutSolidCube(40);
        gl2.glTranslated(-42,0,0);
        gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE,df5,0);
        glut.glutSolidCube(40);

        gl2.glTranslated(0,-42,0);
        gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE,df2,0);
        glut.glutSolidCube(40);
        gl2.glTranslated(42,0,0);
        gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE,df3,0);
        glut.glutSolidCube(40);
        gl2.glTranslated(42,0,0);
        gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE,df4,0);
        glut.glutSolidCube(40);

        gl2.glPopMatrix();



    }

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3) {

    }


    Float xcamrotLast, lightdisLast;
    @Override
    public void mouseDragged(MouseEvent me) {

        if (xcamrotLast != null)
            xcamrot += ((((float) me.getY()) / gljpanel.getHeight()) - xcamrotLast) * 360.0f;
        xcamrotLast = (((float) me.getY()) / gljpanel.getHeight());

        if (lightdisLast != null)
            lightdis += ((((float) me.getX()) / gljpanel.getWidth()) - lightdisLast) * 10.0f;
        lightdisLast = (((float) me.getX()) / gljpanel.getWidth());

        lightpos[0] = lightdis * 50.0f;
        lightpos[1] = lightdis * 100.0f;
        lightpos[2] = lightdis * 200.0f;

        gljpanel.display();

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        xcamrotLast = null;
        lightdisLast = null;

    }
}
