package com.obduratereptile.explore;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * Created by Marc on 9/29/2016.
 */
public class XoppaShader implements Shader {
    ShaderProgram program;
    Camera camera;
    RenderContext context;
    int u_projViewTrans;
    int u_worldTrans;
    int u_colorU;
    int u_colorV;

    @Override
    public void init() {
        String vert = Gdx.files.internal("data/test.vertex.glsl").readString();
        String frag = Gdx.files.internal("data/test.fragment.glsl").readString();
        program = new ShaderProgram(vert, frag);
        if (!program.isCompiled())
            throw new GdxRuntimeException(program.getLog());
        u_projViewTrans = program.getUniformLocation("u_projViewTrans");
        u_worldTrans = program.getUniformLocation("u_worldTrans");
        u_colorU = program.getUniformLocation("u_colorU");
        u_colorV = program.getUniformLocation("u_colorV");
        }

    @Override
    public int compareTo(Shader other) {
        return 0;
    }

    @Override
    public boolean canRender(Renderable renderable) {
        return renderable.material.has(XoppaScreen.TestColorAttribute.DiffuseU | XoppaScreen.TestColorAttribute.DiffuseV);
    }

    @Override
    public void begin(Camera camera, RenderContext context) {
        this.camera = camera;
        this.context = context;
        program.begin();
        program.setUniformMatrix("u_projViewTrans", camera.combined);
        context.setDepthTest(GL20.GL_LEQUAL);
        context.setCullFace(GL20.GL_BACK);
    }

    @Override
    public void render(Renderable renderable) {
        program.setUniformMatrix(u_worldTrans, renderable.worldTransform);
        Color colorU = ((ColorAttribute)renderable.material.get(XoppaScreen.TestColorAttribute.DiffuseU)).color;
        Color colorV = ((ColorAttribute)renderable.material.get(XoppaScreen.TestColorAttribute.DiffuseV)).color;
        program.setUniformf(u_colorU, colorU.r, colorU.g, colorU.b);
        program.setUniformf(u_colorV, colorV.r, colorV.g, colorV.b);
        renderable.meshPart.render(program);
//        renderable.mesh.render(program,
//                renderable.primitiveType,
//                renderable.meshPartOffset,
//                renderable.meshPartSize);
    }

    @Override
    public void end() {
        program.end();
    }

    @Override
    public void dispose() {
        program.dispose();
    }
}
