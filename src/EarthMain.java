import edu.utc.game.*;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.opengl.GL11;

import java.util.*;

public class EarthMain extends Game implements Scene {
	private static final int FOX_ATTACK_DISTANCE = 5;
	private static final int REPRODUCING_TIME = 2000;
	private static final int COOLDOWN_TIME = 8000;

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
		Vector2f coordinates = new Vector2f(Game.ui.getMouseLocation().x, Game.ui.getMouseLocation().y);

		/* Spawn */
		if (gotClick) spawnEntities(coordinates);
		spawnPlants();

		/* Update */
		marker.setLocation(coordinates);
		update(bunnies, delta);
		update(foxes, delta);

		/* Draw */
		marker.draw();
		draw(bunnies);
		draw(foxes);
		draw(flora);

		/* Notice targets */
		scoutForPrey(foxes, bunnies);
		scoutForPlants(bunnies, flora);
		noticePredators(bunnies, foxes);

		/* Check encounters */
		doAttacks(foxes);

		/* Consume the fallen (or plants) */
		consumePrey(foxes);
		consumePlants(bunnies);

		/* Reproduce..!!? The key to 勝利! */
		reproduce(bunnies);
		stopMating(bunnies);

		/* Deactivate the consumed */
		deactivate(bunnies);
		deactivate(foxes);
		deactivate(flora);

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

	private void doAttacks(List<Fox> foxes) {
		for (Fox fox : foxes) {
			if (fox.enemy == null) continue;
			float distanceToEnemy = new Vector2f(fox.enemy.location.subtract(fox.location)).magnitude();
			if (distanceToEnemy < FOX_ATTACK_DISTANCE) {
				fox.attack(fox.enemy);
			}
		}
	}

	private void scoutForPrey(List<Fox> foxes, List<Bunny> bunnies) {
		for (Fox fox : foxes) {
			if (fox.enemy != null) continue;
			for (Bunny bunny : bunnies) {
				float distanceToEnemy = new Vector2f(bunny.location.subtract(fox.location)).magnitude();
				if (distanceToEnemy <= fox.viewDistance) {
					fox.enemy = bunny;
					break;
				}
			}
		}
	}

	private void scoutForPlants(List<Bunny> bunnies, List<Flora> plants) {
		for (Bunny bunny : bunnies) {
			if (bunny.target != null) continue;
			for (Flora plant : plants) {
				float distanceToPlant = new Vector2f(plant.getLocation().subtract(bunny.location)).magnitude();
				if (distanceToPlant <= bunny.viewDistance) {
					bunny.target = plant;
					break;
				}
			}
		}
	}

	private void noticePredators(List<Bunny> bunnies, List<Fox> foxes) {
		for (Bunny bunny : bunnies) {
			for (Fox fox : foxes) {
				Vector2f toEnemy = new Vector2f(fox.location.subtract(bunny.location));
				float distanceToEnemy = toEnemy.magnitude();
				if (distanceToEnemy <= bunny.viewDistance) {
					toEnemy.normalize();
					Vector2f fleeDirection = new Vector2f(-toEnemy.x, -toEnemy.y);
					bunny.direction = fleeDirection;
					bunny.fleeing = true;
					break;
				}
			}
		}
	}

	private void consumePrey(List<Fox> foxes) {
		for (Fox fox : foxes) {
			if (fox.enemy == null) continue;
			if (fox.enemy.isDead) {
				float distanceToEnemy = new Vector2f(fox.enemy.location.subtract(fox.location)).magnitude();
				if (distanceToEnemy > FOX_ATTACK_DISTANCE) break;
				fox.consumeFlesh(fox.enemy);
				fox.changeDirection();
				fox.enemy = null;
			}
		}
	}

	private void consumePlants(List<Bunny> bunnies) {
		for (Bunny bunny : bunnies) {
			if (bunny.target == null) continue;
			if (bunny.getHitbox().intersects(bunny.target.getHitbox())) {
				bunny.consumePlant(bunny.target);
				bunny.changeDirection();
				bunny.target = null;
			}
		}
	}

	private void reproduce(List<Bunny> bunnies) {
		for (Bunny bunny : bunnies) {
			if (!bunny.fleeing && !bunny.reproducing && bunny.cooldownTimer >= COOLDOWN_TIME) {
				for (Bunny lover : bunnies) {
					if (lover == bunny || lover.cooldownTimer < COOLDOWN_TIME) continue;
					float distanceToLover = new Vector2f(lover.location.subtract(bunny.location)).magnitude();
					if (!lover.fleeing && !lover.reproducing && distanceToLover < 5) {
						bunny.mateWith(lover);
					}
				}
			}
		}
	}

	private void stopMating(List<Bunny> bunnies) {
		List<Vector2f> locationsToSpawn = new LinkedList<>();
		for (Bunny bunny : bunnies) {
			if (!bunny.reproducing) continue;
			if (bunny.reproducingTimer >= REPRODUCING_TIME) {
				bunny.stopMating();
				locationsToSpawn.add(bunny.location);
			}
		}
		for (Vector2f location : locationsToSpawn) {
			Bunny child = new Bunny(new Vector2f(location.x + 8, location.y + 8), 6, 6);
			child.setColor(.8f, .8f, .8f);
			child.changeDirection();
			bunnies.add(child);
		}
	}

	private void spawnEntities(Vector2f currentPos) {
		if (Game.ui.keyPressed(GLFW.GLFW_KEY_B)) {
			Bunny newbie = new Bunny(currentPos, 6, 6);
			newbie.setColor(1, 1, 1);
			bunnies.add(newbie);
		}
		else if (Game.ui.keyPressed(GLFW.GLFW_KEY_F)) {
			Fox newbie = new Fox(currentPos, 12, 12);
			newbie.setColor(1, .6f, 0);
			foxes.add(newbie);
		}
	}

	private void spawnPlants() {
		Random r = new Random();
		while (flora.size() < 100) {
			flora.add(new Flora(new Vector2f(r.nextFloat() * Game.ui.getWidth(), r.nextFloat() * Game.ui.getHeight())));
		}
	}
}
