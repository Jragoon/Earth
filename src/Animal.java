import edu.utc.game.Game;
import edu.utc.game.GameObject;

public abstract class Animal extends GameObject {
	protected float speed;
	protected float hp;
	protected float maxHP;
	protected float attack;
	protected float hunger;
	protected float maxHunger;
	protected float viewDistance;
	protected float attentionSpan;
	public boolean isDead = false;

	protected Flora target;
	protected Animal enemy;
	protected Vector2f location;
	protected Vector2f direction = Vector2f.randomDirection();
	protected float hungerRate = 3200;

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

	protected void pursueEnemy() {
		this.direction = new Vector2f(this.enemy.location.subtract(this.location));
		this.direction.normalize();
	}

	protected void pursuePlant() {
		this.direction = new Vector2f(this.target.getLocation().subtract(this.location));
		this.direction.normalize();
	}

	protected boolean attentionChanged(int currentAttention) {
		if (currentAttention > this.attentionSpan) {
			changeDirection();
			return true;
		}
		return false;
	}

	protected boolean gainedHunger(int hungerTimer) {
		if (hungerTimer > this.hungerRate) {
			this.hunger++;
			return true;
		}
		return false;
	}

	protected void die() {
		this.speed = 0;
		this.attack = 0;
		this.isDead = true;
		this.setColor(0f, 0f, 0f);
	}

	protected void changeDirection() {
		this.direction = Vector2f.randomDirection();
	}

	protected void moveWithinConfines() {
		if (this.location.x <= 0 || this.location.x + this.hitbox.width > Game.ui.getWidth()) {
			this.direction.x = -this.direction.x;
		}
		if (this.location.y <= 0 || this.location.y + this.hitbox.height > Game.ui.getHeight()) {
			this.direction.y = -this.direction.y;
		}
		this.location.x += this.direction.x * this.speed;
		this.location.y += this.direction.y * this.speed;
		this.hitbox.x = (int) this.location.x;
		this.hitbox.y = (int) this.location.y;
	}

	protected void dieIfTooHungry() {
		if (this.hunger == this.maxHunger) {
			die();
		}
	}

	@Override
	public void setColor(float r, float g, float b) {
		super.setColor(r, g, b);
	}

	@Override
	public abstract void update(int delta);
}