package org.Landen.engine.objects;

import org.Landen.engine.graphics.Shader;
import org.Landen.engine.maths.Matrix4f;
import org.Landen.engine.utils.FileUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.nio.FloatBuffer;

public class Skybox {
    private int cubemapTexture;
    private int vao;
    private int vbo;
    private Shader skyShader;

    public Skybox(String skyboxname) {

        String[] CUBEMAP_FILES = {
                "/textures/skyboxes/" + skyboxname + "/right.png",
                "/textures/skyboxes/" + skyboxname + "/left.png",
                "/textures/skyboxes/" + skyboxname + "/up.png",
                "/textures/skyboxes/" + skyboxname + "/down.png",
                "/textures/skyboxes/" + skyboxname + "/front.png",
                "/textures/skyboxes/" + skyboxname + "/back.png",
        };

        skyShader = new Shader("/shaders/skybox.vert", "/shaders/skybox.frag");
        skyShader.create();

        cubemapTexture = FileUtils.loadCubemap(CUBEMAP_FILES);

        if (cubemapTexture == 0) {
            System.err.println("Failed to load cubemap!");
        }

        int[] mesh = createCubeMesh();
        vao = mesh[0];
        vbo = mesh[1];
    }

    private int[] createCubeMesh() {
        float[] vertices = {
                -1.0f,  1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f,
                1.0f,  1.0f, -1.0f, -1.0f,  1.0f, -1.0f, -1.0f, -1.0f,  1.0f, -1.0f, -1.0f, -1.0f,
                -1.0f,  1.0f, -1.0f, -1.0f,  1.0f, -1.0f, -1.0f,  1.0f,  1.0f, -1.0f, -1.0f,  1.0f,
                1.0f, -1.0f, -1.0f, 1.0f, -1.0f,  1.0f, 1.0f,  1.0f,  1.0f, 1.0f,  1.0f,  1.0f,
                1.0f,  1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f,  1.0f, -1.0f,  1.0f,  1.0f, 1.0f,  1.0f,  1.0f,
                1.0f,  1.0f,  1.0f, 1.0f, -1.0f,  1.0f, -1.0f, -1.0f,  1.0f, -1.0f,  1.0f, -1.0f, 1.0f,  1.0f, -1.0f,
                1.0f,  1.0f,  1.0f, 1.0f,  1.0f,  1.0f, -1.0f,  1.0f,  1.0f, -1.0f,  1.0f, -1.0f,
                -1.0f, -1.0f, -1.0f,-1.0f, -1.0f,  1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f,  1.0f, 1.0f, -1.0f,  1.0f
        };

        int vao = GL30.glGenVertexArrays();
        int vbo = GL15.glGenBuffers();

        if (vao == 0 || vbo == 0) {
            return new int[]{0, 0};
        }

        GL30.glBindVertexArray(vao);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);

        FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.length);
        buffer.put(vertices);
        buffer.flip();

        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);

        int bufferSize = GL15.glGetBufferParameteri(GL15.GL_ARRAY_BUFFER, GL15.GL_BUFFER_SIZE);
        int expectedSize = vertices.length * Float.BYTES;

        if (bufferSize != expectedSize) {
            System.err.println("Buffer upload failed!");
            return new int[]{0, 0};
        }

        GL20.glEnableVertexAttribArray(0);
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 3 * Float.BYTES, 0);

        // Check for errors after setup
        int error = GL11.glGetError();
        if (error != GL11.GL_NO_ERROR) {
            System.err.println("Buffer setup error: " + error);
            return new int[]{0, 0};
        }

        GL30.glBindVertexArray(0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        return new int[]{vao, vbo};
    }

    public void render(Camera camera, Matrix4f projection) {
        if (vao == 0 || vbo == 0) {
            System.err.println("Invalid VAO or VBO - cannot render skybox");
            return;
        }

        int preError = GL11.glGetError();
        if (preError != GL11.GL_NO_ERROR) {
            System.err.println("Pre-existing OpenGL error: " + preError);
        }

        int[] viewport = new int[4];
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);

        GL11.glDepthMask(false);
        GL11.glDepthFunc(GL11.GL_LEQUAL);

        skyShader.bind();

        int shaderError = GL11.glGetError();
        if (shaderError != GL11.GL_NO_ERROR) {
            System.err.println("Shader bind error: " + shaderError);
        }

        int currentProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);

        if (currentProgram == 0) {
            System.err.println("No shader program is bound!");
            return;
        }

        boolean programValid = GL20.glIsProgram(currentProgram);

        if (!programValid) {
            System.err.println("Shader program is not valid!");
            return;
        }

        int viewLocation = GL20.glGetUniformLocation(currentProgram, "view");
        int projLocation = GL20.glGetUniformLocation(currentProgram, "projection");
        int skyboxLocation = GL20.glGetUniformLocation(currentProgram, "skybox");

        if (viewLocation == -1) {
            System.err.println("'view' uniform not found in shader!");
        }
        if (projLocation == -1) {
            System.err.println("'projection' uniform not found in shader!");
        }
        if (skyboxLocation == -1) {
            System.err.println("'skybox' uniform not found in shader!");
        }

        Matrix4f view = Matrix4f.view(camera.getPosition(), camera.getRotation());
        view.removeTranslation();

        skyShader.setUniform("view", view);
        int viewError = GL11.glGetError();
        if (viewError != GL11.GL_NO_ERROR) {
            System.err.println("View uniform error: " + viewError);
        }

        skyShader.setUniform("projection", projection);
        int projError = GL11.glGetError();
        if (projError != GL11.GL_NO_ERROR) {
            System.err.println("Projection uniform error: " + projError);
        }

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        int activeTexError = GL11.glGetError();
        if (activeTexError != GL11.GL_NO_ERROR) {
            System.err.println("Active texture error: " + activeTexError);
        }

        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, cubemapTexture);
        int bindTexError = GL11.glGetError();
        if (bindTexError != GL11.GL_NO_ERROR) {
            System.err.println("Bind texture error: " + bindTexError);
        }

        skyShader.setUniform("skybox", 0);
        int skyboxUniformError = GL11.glGetError();
        if (skyboxUniformError != GL11.GL_NO_ERROR) {
            System.err.println("Skybox uniform error: " + skyboxUniformError);
        }

        // Check uniform/texture errors
        int uniformError = GL11.glGetError();
        if (uniformError != GL11.GL_NO_ERROR) {
            System.err.println("Final uniform/texture error: " + uniformError);

        }

        GL30.glBindVertexArray(vao);

        int boundVAO = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
        if (boundVAO != vao) {
            return;
        }

        boolean vaoValid = GL30.glIsVertexArray(vao);
        boolean vboValid = GL15.glIsBuffer(vbo);
        if (!vaoValid || !vboValid) {
            return;
        }

        try {
            int[] attribEnabled = new int[1];
            int[] attribSize = new int[1];
            int[] attribType = new int[1];
            int[] attribStride = new int[1];

            GL20.glGetVertexAttribiv(0, GL20.GL_VERTEX_ATTRIB_ARRAY_ENABLED, attribEnabled);
            GL20.glGetVertexAttribiv(0, GL20.GL_VERTEX_ATTRIB_ARRAY_SIZE, attribSize);
            GL20.glGetVertexAttribiv(0, GL20.GL_VERTEX_ATTRIB_ARRAY_TYPE, attribType);
            GL20.glGetVertexAttribiv(0, GL20.GL_VERTEX_ATTRIB_ARRAY_STRIDE, attribStride);

            if (attribEnabled[0] == 0) {
                System.err.println("Vertex attribute 0 is not enabled!");
                return;
            }
        } catch (Exception e) {
            System.err.println("Could not check vertex attributes: " + e.getMessage());
        }

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        int bufferSize = GL15.glGetBufferParameteri(GL15.GL_ARRAY_BUFFER, GL15.GL_BUFFER_SIZE);
        int expectedSize = 36 * 3 * Float.BYTES;

        if (bufferSize != expectedSize) {
            System.err.println("Buffer size mismatch! Expected: " + expectedSize + ", Got: " + bufferSize);
            return;
        }

        int preDrawError = GL11.glGetError();
        if (preDrawError != GL11.GL_NO_ERROR) {
            System.err.println("Pre-draw error: " + preDrawError);
        }

        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 36);

        int error = GL11.glGetError();
        if (error != GL11.GL_NO_ERROR) {
            System.err.println("Draw error: " + error);

            if (error == GL11.GL_INVALID_VALUE) {
                System.err.println("GL_INVALID_VALUE - Check draw parameters:");
                System.err.println("  Mode: GL_TRIANGLES");
                System.err.println("  First: 0");
                System.err.println("  Count: 36");
            }
        }

        GL30.glBindVertexArray(0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        skyShader.unbind();
        GL11.glDepthMask(true);
        GL11.glDepthFunc(GL11.GL_LESS);
    }

    public void cleanup() {
        if (vbo != 0) {
            GL15.glDeleteBuffers(vbo);
        }
        if (vao != 0) {
            GL30.glDeleteVertexArrays(vao);
        }
        if (cubemapTexture != 0) {
            GL11.glDeleteTextures(cubemapTexture);
        }
        skyShader.destroy();
    }
}