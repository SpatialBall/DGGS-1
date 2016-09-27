/*
 * Copyright (C) 2016 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package edu.cumtb;

import gov.nasa.worldwind.View;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwindx.examples.ApplicationTemplate;
import lombok.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Illustrates how to display lines that stand out from the background imagery. The technique is to draw behind the line
 * a slightly wider, slightly opaque line in a contrasting color. This backing line makes the primary line stand out
 * more than it otherwise would. This example shows how to do this for both SurfacePolyline and terrain-conforming
 * Polyline.
 *
 * @author tag
 * @version $Id: LineBackground.java 2109 2014-06-30 16:52:38Z tgaskins $
 */
// come from gov.nasa.worldwindx.examples.LineBackground.java
public class Parallel extends ApplicationTemplate {
    protected static boolean initialization;

    public static class AppFrame extends ApplicationTemplate.AppFrame {
        private static final int SUBDIVISION_LEVEL = 6;
        //2 types: latitude-longitude, and sides of triangles
        // latitude-longitude:  latitudeNumber = 2*(2^Level), longitudeNumber = 4

        private static final int LAT_LON_LINES_NUMBER = (int) (4 + 2 * Math.pow(2, SUBDIVISION_LEVEL + 1) - 2);
        private ParallelLine[] latLonLines = new ParallelLine[LAT_LON_LINES_NUMBER];//(lat,lon),(lat,lon)
        private LatLon[][] intervalPoints = new LatLon[(LAT_LON_LINES_NUMBER - 4) / 2][];

        public AppFrame() {
            super(true, true, false);

            int sideEdgesNumber;

            initLines();

            try {
                // Specify attributes for the foreground line.
                ShapeAttributes foregroundAttrs = new BasicShapeAttributes();
                foregroundAttrs.setOutlineMaterial(new Material(Color.red));
                foregroundAttrs.setOutlineStipplePattern((short) 0xAAAA);
                foregroundAttrs.setOutlineStippleFactor(8);

                // Specify attributes for the background line.
                ShapeAttributes backgroundAttrs = new BasicShapeAttributes();
                backgroundAttrs.setOutlineMaterial(new Material(Color.WHITE));
                backgroundAttrs.setOutlineOpacity(0.1);
                backgroundAttrs.setOutlineWidth(foregroundAttrs.getOutlineWidth() + 2);

                for (int i = 0; i < LAT_LON_LINES_NUMBER; i++) {

                    // Create the primary line as a SurfacePolyline and set its attributes.
                    SurfacePolyline si1 = new SurfacePolyline(new ArrayList<LatLon>(Arrays.asList(
//                    LatLon.fromDegrees(33.7, 119.6),
//                    LatLon.fromDegrees(33.5, 125),
//                    LatLon.fromDegrees(35.1, 129.1),
//                    LatLon.fromDegrees(35.8, 127.1)
                            latLonLines[i].getLatLonA(), latLonLines[i].getLatLonB()

                    )));
                    si1.setClosed(true);
                    si1.setAttributes(foregroundAttrs);

                    // Create the background SurfacePolyline and set its attributes.
                    SurfacePolyline si2 = new SurfacePolyline(si1.getLocations());
                    si2.setClosed(true);
                    si2.setAttributes(backgroundAttrs);

                    // Now do the same for the Polyline version, which is placed 2 degrees above the SurfacePolyline.
                    ArrayList<LatLon> plPoints = new ArrayList<LatLon>();
                    for (LatLon ll : si1.getLocations()) {
                        plPoints.add(
                                ll.add(LatLon.fromDegrees(2, 0))); // add 2 degrees of latitude to separate the lines
                    }
                    Polyline pl1 = new Polyline(plPoints, 0); // the primary Polyline
                    pl1.setFollowTerrain(true);
                    pl1.setClosed(true);
                    pl1.setPathType(Polyline.RHUMB_LINE);
                    pl1.setColor(foregroundAttrs.getOutlineMaterial().getDiffuse());
                    pl1.setLineWidth(foregroundAttrs.getOutlineWidth());
                    pl1.setStipplePattern(foregroundAttrs.getOutlineStipplePattern());
                    pl1.setStippleFactor(foregroundAttrs.getOutlineStippleFactor());

                    Polyline pl2 = new Polyline(plPoints, 0); // the background Polyline
                    pl2.setFollowTerrain(true);
                    pl2.setClosed(true);
                    pl2.setPathType(Polyline.RHUMB_LINE);
                    float[] c = backgroundAttrs.getOutlineMaterial().getDiffuse().getColorComponents(new float[3]);
                    pl2.setColor(new Color(c[0], c[1], c[2], (float) backgroundAttrs.getOutlineOpacity()));
                    pl2.setLineWidth(backgroundAttrs.getOutlineWidth());
                    pl2.setStipplePattern(backgroundAttrs.getOutlineStipplePattern());
                    pl2.setStippleFactor(backgroundAttrs.getOutlineStippleFactor());

                    // Add all the lines to the scene.
                    RenderableLayer layer = new RenderableLayer();
                    layer.setName("Lines");
                    layer.setPickEnabled(true);
                    layer.addRenderable(si2);
                    layer.addRenderable(si1);
                    layer.addRenderable(pl1); // Must draw the primary Polyline before drawing the background Polyline
                    layer.addRenderable(pl2);

                    insertBeforeCompass(this.getWwd(), layer);

                    // Move the view to the line locations.
                    View view = getWwd().getView();
                    view.setEyePosition(Position.fromDegrees(35.3, 124.6, 1500e3));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
//            finally
//            {
//                latLonLines = null;
//            }
        }

        private void initLines() {

            int latitudeLineCount = LAT_LON_LINES_NUMBER - 4;
            for (int i = 0; i < latitudeLineCount; i++) {
                int latitudeNumber = i / 2 + 1;
//                System.out.println(
//                    (i + 1) + "条纬线为： " + rad2Angle(computeParallelLatitude(latitudeNumber, latitudeLine)));
                //90 - 90 / Math.pow(2, SUBDIVISION_LEVEL) * i;
                latLonLines[i] = new ParallelLine(
                        LatLon.fromDegrees(rad2Angle(computeParallelLatitude(latitudeNumber, latitudeLineCount)), 180),
                        LatLon.fromDegrees(rad2Angle(computeParallelLatitude(latitudeNumber, latitudeLineCount)), -180));
            }
            // set longitude line
            for (int i = LAT_LON_LINES_NUMBER - 4; i < LAT_LON_LINES_NUMBER; i++) {
                latLonLines[i] = new ParallelLine(LatLon.fromDegrees(90, 90 * (i - LAT_LON_LINES_NUMBER + 4)),
                        LatLon.fromDegrees(-90, 90 * (i - LAT_LON_LINES_NUMBER + 4)));
            }
        }

        @Data
        @AllArgsConstructor
        private static class ParallelLine {
            private LatLon latLonA;
            private LatLon latLonB;

            public ParallelLine(LatLon latLonA, LatLon latLonB) {
                this.latLonA = latLonA;
                this.latLonB = latLonB;
            }

            public LatLon getLatLonA() {
                return latLonA;
            }

            public void setLatLonA(LatLon latLonA) {
                this.latLonA = latLonA;
            }

            public LatLon getLatLonB() {
                return latLonB;
            }

            public void setLatLonB(LatLon latLonB) {
                this.latLonB = latLonB;
            }
        }

        private void computeParallelPoints(int latitudeNumber, int latitudeLineCount) {
            int latitudeCount = latitudeNumber;
            intervalPoints = new LatLon[latitudeCount + 2][];
            intervalPoints[0][0] = LatLon.fromDegrees(90, 0);
            intervalPoints[latitudeCount + 1][0] = LatLon.fromDegrees(-90, 0);
            int latitudePointCount;
            double latitude;
            for (int i = 1; i < latitudeCount + 1; i++) {
                if (i > (latitudeCount + 1) / 2) {
                    latitude = computeParallelLatitude(latitudeNumber - ((latitudeLineCount + 2) / 4),
                            latitudeLineCount);
                }
            }
        }

        private double computeParallelLatitude(int latitudeNumber, int latitudeLineCount) {
            double latitude;
            if (latitudeNumber > ((latitudeLineCount + 2) / 4)) {
                return -computeParallelLatitude(latitudeNumber - ((latitudeLineCount + 2) / 4), latitudeLineCount);
            }

            if (latitudeNumber == 0) {
                latitude = Math.PI / 2;
            } else {
                //latitude = Math.acos(Math.sqrt(1 - Math.pow(Math.sqrt(1 - Math.pow(Math.cos(computeParallelLatitude(i - 1)), 2)) - (2 * i - 1) / Math.pow(4, SUBDIVISION_LEVEL), 2)));
//                double bi1 = computeParallelLatitude(latitudeNumber - 1, latitudeLine);
//                double sinBi1 = Math.sin(bi1);
//                System.out.println(sinBi1);
//                double fraTot = (2 * latitudeNumber - 1) / Math.pow(4, SUBDIVISION_LEVEL);
//                System.out.println(fraTot);
//                double asinVal = sinBi1 - fraTot;
//                System.out.println(asinVal);
//                latitude = Math.asin(Math.sin(abc) - (1 / Math.pow(4, SUBDIVISION_LEVEL)));
                latitude = Math.asin(Math.sin(computeParallelLatitude(latitudeNumber - 1, latitudeLineCount))
                        - (2 * latitudeNumber - 1) / Math.pow(4, SUBDIVISION_LEVEL));
            }

            return latitude;
        }

        private double rad2Angle(double rad) {
            //System.out.println(rad * 180 / Math.PI);
            return rad * 180 / Math.PI;
        }
    }

    public static void main(String[] args) {
        ApplicationTemplate.start("World Wind Line Backgrounds", AppFrame.class);
    }
}
