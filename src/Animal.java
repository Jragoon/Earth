import edu.utc.game.GameObject;

import java.util.Random;

public class Animal extends GameObject {
	protected float speed;
	protected float hp;
	protected float maxHP;
	protected float attack;
	protected float hunger;
	protected float maxHunger;
	protected float viewDistance;
	public boolean isDead = false;

	protected Flora target;
	protected Animal enemy;
	protected Vector2f location;
	Random r = new Random();
	protected Vector2f direction = new Vector2f(r.nextFloat() - .5f, r.nextFloat() - .5f);

	public Animal(Vector2f origin, int width, int height) {
		this.hitbox.x = (int) origin.x;
		this.hitbox.y = (int) origin.y;
		this.location = origin;
		this.hitbox.width = width;
		this.hitbox.height = height;
	}

	public void takeDamage(float damage) {
		this.hp -= damage;
		if (this.hp <= 0) {
			this.die();
		}
	}

	public void attack(Animal enemy) {
		enemy.takeDamage(this.attack);
	}

	private void die() {
		this.speed = 0;
		this.attack = 0;
		this.isDead = true;
		this.setColor(0f, 0f, 0f);
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
		if (this.enemy != null) pursue(this.enemy);

		this.hitbox.x = (int) this.location.x;
		this.hitbox.y = (int) this.location.y;
	}

	private void pursue(Animal enemy) {
		Vector2f direction = this.enemy.getLocation().subtract(this.location);
		direction.normalize();

		if (Math.abs(this.location.x - this.enemy.getLocation().x) > this.hitbox.width ||
				Math.abs(this.location.y - this.enemy.getLocation().y) > this.hitbox.height) {
			this.location.x += direction.x * this.speed;
			this.location.y += direction.y * this.speed;
		}
	}
}