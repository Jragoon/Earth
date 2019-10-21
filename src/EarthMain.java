import edu.utc.game.*;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class EarthMain extends Game implements Scene {
	private static int ENEMY_SPEED = 1;

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
						if (button==0 && action== GLFW.GLFW_PRESS) {
							Vector2f lastClick = new Vector2f(Game.ui.getMouseLocation().x, Game.ui.getMouseLocation().y);
							if (Game.ui.keyPressed(GLFW.GLFW_KEY_B)) {
								Bunny spawn = new Bunny(lastClick, 10, 10);
								spawn.setColor(1, 1, 1);
								bunnies.add(spawn);
								gotClick = true;
							}
							else if (Game.ui.keyPressed(GLFW.GLFW_KEY_F)) {
								Fox spawn = new Fox(lastClick, 15, 15);
								spawn.setColor(1, 1, 0);
								foxes.add(spawn);
								gotClick = true;
							}
						}
					}
				});
	}

	public Scene drawFrame(int delta) {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		Vector2f coords = new Vector2f(Game.ui.getMouseLocation().x, Game.ui.getMouseLocation().y);

		/* Update */
		marker.setLocation(coords);

		/* Draw */
		marker.draw();
		draw(bunnies);
		draw(foxes);

		/* Check collisions */
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

	/* BEGIN: GameObject implementations */

	public class Target extends GameObject
	{
		private Vector2f location;
		public void setLocation(Vector2f location)
		{
			this.hitbox.setBounds((int) location.x, (int) location.y, 10, 10);
			this.location = location;
			this.setColor(1, 0, 0);
		}

		public Vector2f getLocation() {
			return this.location;
		}

		public void setColor(float r, float g, float b)
		{
			super.setColor(r, g, b);
		}
	}

	public class Flora extends GameObject {
		private Vector2f location;

		public Flora(Vector2f origin) {
			this.hitbox.setBounds((int) origin.x, (int) origin.y, 25, 25);
			this.location = origin;
			this.setColor(1, 1, 1);
		}

		public Vector2f getLocation() {
			return this.location;
		}

		@Override
		public void update(int delta) {
			this.hitbox.x = (int) this.location.x;
			this.hitbox.y = (int) this.location.y;
		}
	}

	private class Animal extends GameObject {
		protected Vector2f location;
		protected Flora target;
		protected Animal enemy;

		public Animal(Vector2f origin, int width, int height) {
			this.hitbox.x = (int) origin.x;
			this.hitbox.y = (int) origin.y;
			this.location = origin;
			this.hitbox.width = width;
			this.hitbox.height = height;
			this.setColor(0, 0 , 1);
		}

		public void setTarget(Flora target) {
			this.target = target;
		}

		public void setEnemy(Animal enemy) {
			this.enemy = enemy;
		}

		public Vector2f getLocation() {
			return location;
		}

		@Override
		public void setColor(float r, float g, float b) {
			super.setColor(r, g, b);
		}

		@Override
		public void update(int delta) {
			Vector2f direction = this.enemy.getLocation().subtract(this.location);
			direction.normalize();

			if (Math.abs(this.location.x - this.enemy.getLocation().x) > this.hitbox.width ||
					Math.abs(this.location.y - this.enemy.getLocation().y) > this.hitbox.height) {
				this.location.x += direction.x * ENEMY_SPEED;
				this.location.y += direction.y * ENEMY_SPEED;
			}

			this.hitbox.x = (int) this.location.x;
			this.hitbox.y = (int) this.location.y;
		}
	}

	private class Bunny extends Animal {
		public Bunny(Vector2f origin, int width, int height) {
			super(origin, width, height);
		}
	}

	private class Fox extends Animal {
		public Fox(Vector2f origin, int width, int height) {
			super(origin, width, height);
		}
	}
}
