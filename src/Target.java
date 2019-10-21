import edu.utc.game.GameObject;

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