package edu.cumtb.impl;

import edu.cumtb.model.QTM;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwindx.examples.ApplicationTemplate;

import javax.media.opengl.GL2;

/**
 * Administrator
 * Created by tbpwang
 * 2016/9/20.
 */
// method: circle of latitude
// 纬圈法
public class Parallel extends QTM {
    public Parallel(String address, int subdivisionLevel, double minLatitude, double maxLatitude, double minLongitude, double maxLongitude) {
        super(address, subdivisionLevel, minLongitude, maxLongitude, minLatitude, maxLatitude);
    }
    public Parallel(){
        implQTM = new Parallel[8];
        implQTM[0] = new Parallel("0", 0, 0, 90, 0, 90);
        implQTM[1] = new Parallel("1", 0, 0, 90, 90, 180);
        implQTM[2] = new Parallel("2", 0, 0, 90, -180, -90);
        implQTM[3] = new Parallel("3", 0, 0, 90, -90, 0);
        implQTM[4] = new Parallel("4", 0, -90, 0, 0, 90);
        implQTM[5] = new Parallel("5", 0, -90, 0, 90, 180);
        implQTM[6] = new Parallel("6", 0, -90, 0, -180, -90);
        implQTM[7] = new Parallel("7", 0, -90, 0, -90, 0);
        for (int i = 0; i < 8; i++) {
            implQTM[i].init();
        }
    }


    private int aimedSubdivisionLevel = 2;
    private LatLon[] topPoints, leftPoints, rightPoints;
    private QTM[] implQTM;

    public void createInnerTriangle() {
        topPoints = new Position[21];
        leftPoints = new Position[21];
        rightPoints = new Position[21];
        double latitudeSpan = (getMaxLatitude() - getMinLatitude()) / 20;
        double longitudeSpan = (getMaxLongitude() - getMinLongitude()) / 20;
        for (int i = 0; i < 21; i++) {
            leftPoints[i] = Position.fromDegrees(getMinLatitude() + i * latitudeSpan, getMinLongitude(), 10);// set elevation = 10m
            rightPoints[i] = Position.fromDegrees(getMinLatitude() + i * latitudeSpan, getMaxLongitude(),10);// set elevation = 10m
            if (getMaxLatitude() > 0) {
                topPoints[i] = Position.fromDegrees(getMinLatitude(), getMinLongitude() + i * longitudeSpan, 10);
            } else {
                topPoints[i] = Position.fromDegrees(getMaxLatitude(), getMinLongitude() + i * longitudeSpan, 10);
            }
        }
    }

    public void update() {
        if (getCurrentSubdivisionLevel() < aimedSubdivisionLevel) {
            if (getTopChild() == null || getCoreChild() == null || getLeftChild() == null || getRightChild() == null) {
                computeChildren();
            }
            if (getTopChild() != null) {
                getTopChild().update();
            }
            if (getCoreChild() != null) {
                getCoreChild().update();
            }
            if (getLeftChild() != null) {
                getLeftChild().update();
            }
            if (getRightChild() != null) {
                getRightChild().update();
            }
        } else {
            if (getTopChild() != null) {
                getTopChild().dispose();
                setTopChild(null);
            }
            if (getCoreChild() != null) {
                getCoreChild().dispose();
                setCoreChild(null);
            }
            if (getLeftChild() != null) {
                getLeftChild().dispose();
                setLeftChild(null);
            }
            if (getRightChild() != null) {
                getRightChild().dispose();
                setRightChild(null);
            }
            if (isInit() == false) {
                init();
            }
        }
    }

    // init children
    private void computeChildren() {
        double latitudeCenter = (getMinLatitude() + getMaxLatitude()) / 2;
        double longitudeCenter = (getMinLongitude() + getMaxLongitude()) / 2;
        Parallel child;
        if (latitudeCenter > 0) {
            if (getTopChild() == null) {
                child =  new Parallel(getAddress() + "0", getCurrentSubdivisionLevel() + 1, latitudeCenter, getMaxLatitude(), getMinLongitude(), getMaxLongitude());
                setTopChild(child);
            }
            if (getCoreChild() == null) {
                child =  new Parallel(getAddress() + "1", getCurrentSubdivisionLevel() + 1, getMinLatitude(), latitudeCenter, getMinLongitude(), getMaxLongitude());
                setCoreChild(child);
            }
            if (getLeftChild() == null) {
                child =  new Parallel(getAddress() + "2", getCurrentSubdivisionLevel() + 1, getMinLatitude(), latitudeCenter, getMinLongitude(), longitudeCenter);
                setLeftChild(child);
            }
            if (getRightChild() == null) {
                child =  new Parallel(getAddress() + "3", getCurrentSubdivisionLevel() + 1, getMinLatitude(), latitudeCenter, longitudeCenter, getMaxLongitude());
                setRightChild(child);
            }
        } else {
            if (getTopChild() == null) {
                child =  new Parallel(getAddress() + "0", getCurrentSubdivisionLevel() + 1, getMinLatitude(), latitudeCenter, getMinLongitude(), getMaxLongitude());
                setTopChild(child);
            }
            if (getCoreChild() == null) {
                child =  new Parallel(getAddress() + "1", getCurrentSubdivisionLevel() + 1, latitudeCenter, getMaxLatitude(), getMinLongitude(), getMaxLongitude());
                setCoreChild(child);
            }
            if (getLeftChild() == null) {
                child =  new Parallel(getAddress() + "2", getCurrentSubdivisionLevel() + 1, latitudeCenter, getMaxLatitude(), longitudeCenter, getMaxLongitude());
                setLeftChild(child);
            }
            if (getRightChild() == null) {
                child =  new Parallel(getAddress() + "3", getCurrentSubdivisionLevel() + 1, latitudeCenter, getMaxLatitude(), getMinLongitude(), longitudeCenter);
                setRightChild(child);
            }
        }
    }

    @Override
    public void render(DrawContext drawContext) {
        if (getTopChild() != null || getCoreChild() != null || getLeftChild() != null || getRightChild() != null) {
            getTopChild().render(drawContext);
            getCoreChild().render(drawContext);
            getLeftChild().render(drawContext);
            getRightChild().render(drawContext);
        } else {
            if (isInit() == true) {
                GL2 gl = drawContext.getGL().getGL2();
                beginDrawing(drawContext);
                gl.glBegin(GL2.GL_LINE_STIPPLE);
                for (int i = 0; i < 21; i++) {
                    gl.glVertex2dv(topPoints[i].asDegreesArray(), i);
                    gl.glVertex2dv(leftPoints[i].asDegreesArray(), i);
                    gl.glVertex2dv(rightPoints[i].asDegreesArray(), i);
                }
                gl.glEnd();
                endDrawing(drawContext);
            }
        }

    }

    protected static class AppFrame extends ApplicationTemplate.AppFrame
    {
        public AppFrame()
        {
            super(true, true, false);

            RenderableLayer layer = new RenderableLayer();
            Parallel parallel = new Parallel();
            layer.addRenderable(parallel);

            getWwd().getModel().getLayers().add(layer);
        }
    }
    public static void main(String[] args) {
        ApplicationTemplate.start("This is a QTM--Parallel", AppFrame.class);
    }
}

