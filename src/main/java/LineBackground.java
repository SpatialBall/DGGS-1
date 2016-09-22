import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwindx.examples.ApplicationTemplate;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Administrator
 * Created by tbpwang
 * 2016/9/21.
 */public class LineBackground extends ApplicationTemplate
{
    public static class AppFrame extends ApplicationTemplate.AppFrame
    {
        public AppFrame()
        {
            super(true, true, false);

/***********************将经纬线顶点数据存入Lon_Lat******************************************/

            int i=1,j,k,m,levels=0,LonLat_num=(int)Math.pow(2,levels+3)+(int)Math.pow(2,levels+1)-4*levels-5+1;
            //固定的纬线2^(n+1)-1条,4条长经线,经线总和(2^(n+1)-2-n)*4条,总和
            double[][] Lon_Lat;             //定义一个float类型的2维数组
            double tLat,tLon;
            Lon_Lat=new double[LonLat_num][4];       //为它分配x行2列的空间大小,0，2为纬度，1,3为经度

            for(i=1;i<=Math.pow(2,levels+1)-1;i++)//2^(n+1)-1条纬线
            {
                Lon_Lat[i][0]=90-90/Math.pow(2,levels)*i;
                Lon_Lat[i][1]=180;
                Lon_Lat[i][2]=90-90/Math.pow(2,levels)*i;
                Lon_Lat[i][3]=-180;
            }

            for(j=0;j<=3;j++,i++)//4条经线
            {
                Lon_Lat[i][0]=90;
                Lon_Lat[i][1]=90*j;
                Lon_Lat[i][2]=-90;
                Lon_Lat[i][3]=90*j;
            }

            for(m=-180;m<=90;m=m+90)
            {
                for(j=1;j<=levels;j++)//其余经线0-90
                {
                    for(k=1;k<=Math.pow(2,j)-1;k++,i++)
                    {
                        //m=0;
                        tLat=90-90/Math.pow(2,levels-j+1);
                        tLon=m+90/(Math.pow(2,j))*k;

                        Lon_Lat[i][0]=tLat;
                        Lon_Lat[i][1]=tLon;
                        Lon_Lat[i][2]=-tLat;
                        Lon_Lat[i][3]=tLon;
                    }
                }
            }
        	/*for(j=1;j<=levels;j++)//其余经线0-90
        	{
        		for(k=1;k<=Math.pow(2,j)-1;k++,i++)
        		{
        			m=0;
        			tLat=90-90/Math.pow(2,levels-j+1);
        			tLon=m+90/(Math.pow(2,j))*k;

        			Lon_Lat[i][0]=tLat;
        			Lon_Lat[i][1]=tLon;
        			Lon_Lat[i][2]=-tLat;
        			Lon_Lat[i][3]=tLon;
        		}
        	}

        	for(j=1;j<=levels;j++)//其余经线90-180
        	{
        		for(k=1;k<=Math.pow(2,j)-1;k++,i++)
        		{
        			m=90;
        			tLat=90-90/Math.pow(2,levels-j+1);
        			tLon=m+90/(Math.pow(2,j))*k;

        			Lon_Lat[i][0]=tLat;
        			Lon_Lat[i][1]=tLon;
        			Lon_Lat[i][2]=-tLat;
        			Lon_Lat[i][3]=tLon;
        		}
        	}
        	for(j=1;j<=levels;j++)//其余经线180-270
        	{
        		for(k=1;k<=Math.pow(2,j)-1;k++,i++)
        		{
        			m=-180;
        			tLat=90-90/Math.pow(2,levels-j+1);
        			tLon=m+90/(Math.pow(2,j))*k;

        			Lon_Lat[i][0]=tLat;
        			Lon_Lat[i][1]=tLon;
        			Lon_Lat[i][2]=-tLat;
        			Lon_Lat[i][3]=tLon;
        		}
        	}
        	for(j=1;j<=levels;j++)//其余经线270-0
        	{
        		for(k=1;k<=Math.pow(2,j)-1;k++,i++)
        		{
        			m=-90;
        			tLat=90-90/Math.pow(2,levels-j+1);
        			tLon=m+90/(Math.pow(2,j))*k;

        			Lon_Lat[i][0]=tLat;
        			Lon_Lat[i][1]=tLon;
        			Lon_Lat[i][2]=-tLat;
        			Lon_Lat[i][3]=tLon;
        		}
        	}*/



            /***********************存储完毕******************************************/

            try
            {
                // Specify attributes for the foreground line.
                ShapeAttributes foregroundAttrs = new BasicShapeAttributes();
                //foregroundAttrs.setOutlineMaterial(new Material(Color.yellow));
                foregroundAttrs.setOutlineMaterial(new Material(Color.red));
                foregroundAttrs.setOutlineStipplePattern((short) 0xFFFF);
                foregroundAttrs.setOutlineStippleFactor(8);

                // Specify attributes for the background line.
               /* ShapeAttributes backgroundAttrs = new BasicShapeAttributes();
                backgroundAttrs.setOutlineMaterial(new Material(Color.yellow));
                //backgroundAttrs.setOutlineOpacity(0.1);
                backgroundAttrs.setOutlineOpacity(1);
                backgroundAttrs.setOutlineWidth(foregroundAttrs.getOutlineWidth() + 2);*/

                //for(i=1;i<=Math.pow(2,levels+1)-1+4;i++)
                for(i=1;i<=LonLat_num;i++)
                // Create the primary line as a SurfacePolyline and set its attributes.
                {SurfacePolyline si1 = new SurfacePolyline(new ArrayList<LatLon>(Arrays.asList(
                    /*LatLon.fromDegrees(33.7, 119.6),
                    LatLon.fromDegrees(33.5, 125),
                    LatLon.fromDegrees(35.1, 129.1),
                    LatLon.fromDegrees(35.8, 127.1)*/

                            LatLon.fromDegrees(Lon_Lat[i][0], Lon_Lat[i][1]),
                            LatLon.fromDegrees(Lon_Lat[i][2], Lon_Lat[i][3])
                            //LatLon.fromDegrees(90-90/Math.pow(2,levels)*i, 180),
                            //LatLon.fromDegrees(90-90/Math.pow(2,levels)*i, -180)
                    )));

                    si1.setClosed(true);
                    si1.setAttributes(foregroundAttrs);


                    // Create the background SurfacePolyline and set its attributes.
                /*SurfacePolyline si2 = new SurfacePolyline(si1.getLocations());
                si2.setClosed(true);
                si2.setAttributes(backgroundAttrs);*/

                    // Now do the same for the Polyline version, which is placed 2 degrees above the SurfacePolyline.
                    ArrayList<LatLon> plPoints = new ArrayList<LatLon>();
                    for (LatLon ll : si1.getLocations())
                    {
                        plPoints.add(ll.add(LatLon.fromDegrees(0, 0))); // add 2 degrees of latitude to separate the lines
                    }
                    Polyline pl1 = new Polyline(plPoints, 0); // the primary Polyline
                    pl1.setFollowTerrain(true);
                    pl1.setClosed(true);
                    //pl1.setPathType(Polyline.RHUMB_LINE);
                    pl1.setPathType(Polyline.LINEAR);
                    pl1.setColor(foregroundAttrs.getOutlineMaterial().getDiffuse());
                    pl1.setLineWidth(foregroundAttrs.getOutlineWidth());
                    pl1.setStipplePattern(foregroundAttrs.getOutlineStipplePattern());
                    pl1.setStippleFactor(foregroundAttrs.getOutlineStippleFactor());

               /* Polyline pl2 = new Polyline(plPoints, 0); // the background Polyline
                pl2.setFollowTerrain(true);
                pl2.setClosed(true);
                pl2.setPathType(Polyline.RHUMB_LINE);
                float[] c = backgroundAttrs.getOutlineMaterial().getDiffuse().getColorComponents(new float[3]);
                pl2.setColor(new Color(c[0], c[1], c[2], (float) backgroundAttrs.getOutlineOpacity()));
                pl2.setLineWidth(backgroundAttrs.getOutlineWidth());
                pl2.setStipplePattern(backgroundAttrs.getOutlineStipplePattern());
                pl2.setStippleFactor(backgroundAttrs.getOutlineStippleFactor());*/
/////////////////////////////////////////////经度

/////////////////////////////////////////////经度完毕


                    // Add all the lines to the scene.
                    RenderableLayer layer = new RenderableLayer();
                    layer.setName("Lines");
                    //layer.setPickEnabled(false);
                    layer.setPickEnabled(true);
                    //layer.addRenderable(si2);
                    layer.addRenderable(si1);
                    layer.addRenderable(pl1); // Must draw the primary Polyline before drawing the background Polyline
                    //layer.addRenderable(pl2);

                    insertBeforeCompass(this.getWwd(), layer);

                    this.getLayerPanel().update(this.getWwd());

                    // Move the view to the line locations.
                    //View view = getWwd().getView();
                    //view.setEyePosition(Position.fromDegrees(35.3, 124.6, 2500e3));
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args)
    {
        ApplicationTemplate.start("World Wind Line Backgrounds", AppFrame.class);
    }
}
