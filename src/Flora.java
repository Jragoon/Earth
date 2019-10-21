import edu.utc.game.GameObject;

public class Flora extends GameObject {
	private Vector2f location;
	public float health = 10;
	public float satiety = 10;

	public Flora(Vector2f origin) {
		this.location = origin;
		this.hitbox.setBounds((int) origin.x, (int) origin.y, 8, 8);
		this.setColor(0, 1, 1);
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
		this.hitbox.x = (int) this.location.x;
		this.hitbox.y = (int) this.location.y;
	}
}