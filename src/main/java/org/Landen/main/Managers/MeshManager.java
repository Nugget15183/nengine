package org.Landen.main.Managers;

import org.Landen.engine.graphics.Material;
import org.Landen.engine.graphics.Mesh;
import org.Landen.engine.graphics.Renderer;
import org.Landen.engine.io.Input;
import org.Landen.engine.io.ModelLoader;
import org.Landen.engine.maths.Ray;
import org.Landen.engine.maths.Vector2f;
import org.Landen.engine.maths.Vector3f;
import org.Landen.engine.objects.Camera;
import org.Landen.engine.objects.GameObject;

import java.util.ArrayList;

public class MeshManager {
    public static ArrayList<Mesh> meshes = new ArrayList<>();
    public static ArrayList<GameObject> gameObjects = new ArrayList<>();

    public GameObject instanciateMesh(String filePath, Material material, Vector3f pos, Vector3f rot, Vector3f scale) {
        Mesh mesh = ModelLoader.loadModel(filePath, material);
        meshes.add(mesh);
        return updateGameObjects(mesh, pos, rot, scale);
    }

    private GameObject updateGameObjects(Mesh mesh, Vector3f pos, Vector3f rot, Vector3f scale) {
        GameObject gameObject = new GameObject(pos, rot, scale, mesh);
        gameObjects.add(gameObject);
        mesh.create();
        return gameObject;
    }

    public static GameObject raycast(Camera camera, Vector2f dimentions) {
        double mouseX = Input.getMouseX();
        double mouseY = Input.getMouseY();

        Ray ray = camera.createSimpleRayFromMouse(mouseX, mouseY, (int) dimentions.getX(), (int) dimentions.getY());

        for(GameObject g : gameObjects) {
            if(g == null) continue;
            Mesh m = g.getMesh();

            if(m.intersects(ray, g)) {
                return g;
            }
        }
        return null;
    }

    public static void render(Renderer renderer, Camera camera) {
        for(GameObject gameObject : gameObjects) {
            if(gameObject != null) {
                renderer.renderMesh(gameObject, camera);
            }
        }
    }

    public static void clear() {
        for(Mesh mesh : meshes) {
            if(mesh != null) {
                mesh.destroy();
            }
        }
        meshes.clear();
        gameObjects.clear();
    }
}