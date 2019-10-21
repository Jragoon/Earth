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
	private List<Bullet> bullets = new ArrayList<>();
	private List<Enemy> enemies = new ArrayList<>();
	private Target marker;
	private Player player;

	public EarthMain() {
		initUI(1280,720,"Create Your EARTH");
		Game.ui.enableMouseCursor(false);
		GL11.glClearColor(0f, 0f, 0f, 0f);
		player = new Player(new Vector2f(Game.ui.getWidth()/2, Game.ui.getHeight()/2));
		marker = new Target();
		spawnEnemies(enemies);
		GLFW.glfwSetMouseButtonCallback(Game.ui.getWindow(),
				new GLFWMouseButtonCallback() {
					public void invoke(long window, int button, int action, int mods)
					{
						if (button==0 && action== GLFW.GLFW_PRESS)
						{
							Vector2f lastClick = new Vector2f(Game.ui.getMouseLocation().x, Game.ui.getMouseLocation().y);
							Vector2f bulletDirection = lastClick.subtract(player.getLocation());
							bulletDirection.normalize();
							Bullet nb = new Bullet(player.getLocation(), bulletDirection);
							nb.setColor(0, 1, 0);
							bullets.add(nb);
							gotClick = true;
						}
					}
				});
	}

	public Scene drawFrame(int delta) {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		Vector2f coords = new Vector2f(Game.ui.getMouseLocation().x, Game.ui.getMouseLocation().y);
		if (enemies.isEmpty()) {
			spawnEnemies(enemies);
			ENEMY_SPEED++;
		}

		/* Update */
		marker.setLocation(coords);
		player.update(delta);
		update(bullets, delta);
		update(enemies, delta);

		/* Draw */
		draw(bullets);
		draw(enemies);
		marker.draw();
		player.draw();

		/* Check collisions */
		testCollisions(bullets, enemies);
		deactivate(bullets);
		deactivate(enemies);

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

	private void testCollisions(List<Bullet> bullets, List<Enemy> enemies) {
		for (Bullet b : bullets) {
			for (Enemy e : enemies) {
				if (b.getHitbox().intersects(e.getHitbox())) {
					b.deactivate();
					e.deactivate();
					break;
				}
			}
		}
	}

	private void spawnEnemies(List<Enemy> enemies) {
		for (int i = 1; i < 30; i++) {
			Enemy enemy = new Enemy(i * 30, 100, 35, 35, player);
			enemies.add(enemy);
		}
		for (int i = 1; i < 30; i++) {
			Enemy enemy = new Enemy(i * 30, Game.ui.getHeight()-100, 35, 35, player);
			enemies.add(enemy);
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

	public class Bullet extends GameObject {
		private Vector2f direction;

		public Bullet(Vector2f origin, Vector2f direction) {
			this.hitbox.setBounds((int) origin.x, (int) origin.y, 10, 10);
			this.direction = direction;
			this.setColor(1, 0, 0);
		}

		@Override
		public void setColor(float r, float g, float b) {
			super.setColor(r, g, b);
		}

		@Override
		public void update(int delta) {
			this.hitbox.x += this.direction.x * 20;
			this.hitbox.y += this.direction.y * 20;
		}
	}

	public class Player extends GameObject {
		private Vector2f location;

		public Player(Vector2f origin) {
			this.hitbox.setBounds((int) origin.x, (int) origin.y, 25, 25);
			location = origin;
			this.setColor(1, 1, 1);
		}

		public Vector2f getLocation() {
			return this.location;
		}

		@Override
		public void setColor(float r, float g, float b) {
			super.setColor(r, g, b);
		}

		@Override
		public void update(int delta) {
			if (Game.ui.keyPressed(GLFW.GLFW_KEY_A)) {
				this.location.x -= 10;
			}
			if (Game.ui.keyPressed(GLFW.GLFW_KEY_D)) {
				this.location.x += 10;
			}
			if (Game.ui.keyPressed(GLFW.GLFW_KEY_W)) {
				this.location.y -= 10;
			}
			if (Game.ui.keyPressed(GLFW.GLFW_KEY_S)) {
				this.location.y += 10;
			}
			this.hitbox.x = (int) this.location.x;
			this.hitbox.y = (int) this.location.y;
		}
	}

	private class Enemy extends GameObject
	{
		private Vector2f location;
		private Player target;

		public Enemy(int x, int y, int width, int height, Player target) {
			this.hitbox.x = x;
			this.hitbox.y = y;
			this.location = new Vector2f(this.hitbox.x, this.hitbox.y);
			this.hitbox.width = width;
			this.hitbox.height = height;
			this.setColor(0, 0 , 1);
			this.target = target;
		}

		@Override
		public void update(int delta) {
			Vector2f direction = this.target.getLocation().subtract(this.location);
			direction.normalize();

			if (Math.abs(this.location.x - this.target.getLocation().x) > this.hitbox.width ||
					Math.abs(this.location.y - this.target.getLocation().y) > this.hitbox.height) {
				this.location.x += direction.x * ENEMY_SPEED;
				this.location.y += direction.y * ENEMY_SPEED;
			}

			this.hitbox.x = (int) this.location.x;
			this.hitbox.y = (int) this.location.y;
		}
	}
}