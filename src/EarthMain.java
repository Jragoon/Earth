import edu.utc.game.*;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class EarthMain extends Game implements Scene {
	public static void main(String[] args) {
		EarthMain game = new EarthMain();
		game.gameLoop();
	}

	private boolean gotClick = false;
	private List<Flora> flora = new ArrayList<>();
	private List<Bunny> bunnies = new ArrayList<>();
	private List<Fox> foxes = new ArrayList<>();
	private Target marker;

	public EarthMain() {
		initUI(1280,720,"Create Your EARTH");
		Game.ui.enableMouseCursor(false);
		GL11.glClearColor(0f, .3f, 0f, 0f);
		marker = new Target();
		GLFW.glfwSetMouseButtonCallback(Game.ui.getWindow(),
				new GLFWMouseButtonCallback() {
					public void invoke(long window, int button, int action, int mods)
					{
						if (button == 0 && action == GLFW.GLFW_PRESS) {
							gotClick = true;
						}
					}
				});
	}

	public Scene drawFrame(int delta) {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		Vector2f coords = new Vector2f(Game.ui.getMouseLocation().x, Game.ui.getMouseLocation().y);

		/* Spawn */
		if (gotClick) spawnAnimals(coords);

		/* Update */
		marker.setLocation(coords);
		update(bunnies, delta);
		update(foxes, delta);

		/* Draw */
		marker.draw();
		draw(bunnies);
		draw(foxes);

		/* Check encounters */
		testDeath(bunnies);
		testDeath(foxes);
		deactivate(bunnies);
		deactivate(foxes);

		gotClick = false;
		return this;
	}

	private <T extends GameObject> void update(List<T> gameObjects, int delta) {
		for (GameObject go : gameObjects) {
			go.update(delta);
		}
	}

	private <T extends GameObject> void draw(List<T> gameObjects) {
		for (GameObject go : gameObjects) {
			go.draw();
		}
	}

	private <T extends GameObject> void deactivate(List<T> objects) {
		objects.removeIf(o -> !o.isActive());
	}

	private <T extends GameObject> void testDeath(List<T> entities) {
		for (GameObject go : entities) {
			/* Logic for death? */
		}
	}

	private void spawnAnimals(Vector2f currentPos) {
		if (Game.ui.keyPressed(GLFW.GLFW_KEY_B)) {
			Bunny newbie = new Bunny(currentPos, 5, 5);
			newbie.setColor(1, 1, 1);
			bunnies.add(newbie);
		}
		else if (Game.ui.keyPressed(GLFW.GLFW_KEY_F)) {
			Fox newbie = new Fox(currentPos, 15, 15);
			newbie.setColor(1, 1, 0);
			foxes.add(newbie);
		}
	}
}
