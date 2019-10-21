import edu.utc.game.GameObject;

public class Animal extends GameObject {
	private float speed = 1f;
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
		/*Vector2f direction = this.enemy.getLocation().subtract(this.location);
		direction.normalize();

		if (Math.abs(this.location.x - this.enemy.getLocation().x) > this.hitbox.width ||
				Math.abs(this.location.y - this.enemy.getLocation().y) > this.hitbox.height) {
			this.location.x += direction.x * this.speed;
			this.location.y += direction.y * this.speed;
		} */

		this.hitbox.x = (int) this.location.x;
		this.hitbox.y = (int) this.location.y;
	}
}