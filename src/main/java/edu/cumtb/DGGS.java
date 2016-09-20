package edu.cumtb;

import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.util.OGLUtil;
import gov.nasa.worldwindx.examples.ApplicationTemplate;

import javax.media.opengl.GL2;

/**
 * Administrator
 * Created by tbpwang
 * 2016/9/20.
 */
//public class DGGS {
//}

public abstract class DGGS extends ApplicationTemplate implements Renderable {

    protected void beginDrawing(DrawContext dc){
        GL2 gl = dc.getGL().getGL2(); // GL initialization checks for GL2 compatibility.

        int attrMask = GL2.GL_CURRENT_BIT | GL2.GL_COLOR_BUFFER_BIT;

        gl.glPushAttrib(attrMask);

        if (!dc.isPickingMode())
        {
            dc.beginStandardLighting();
            gl.glEnable(GL2.GL_BLEND);
            OGLUtil.applyBlending(gl, false);

            // Were applying a scale transform on the modelview matrix, so the normal vectors must be re-normalized
            // before lighting is computed.
            gl.glEnable(GL2.GL_NORMALIZE);
        }
    }

    protected void endDrawing(DrawContext dc) {
        GL2 gl = dc.getGL().getGL2(); // GL initialization checks for GL2 compatibility.

        if (!dc.isPickingMode())
            dc.endStandardLighting();

        gl.glPopAttrib();
    }
}
