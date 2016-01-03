package rs.pedjaapps.smc.object;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;

/**
 * Created by pedja on 18.5.14..
 */
public abstract class GameObject implements Pool.Poolable
{
    public Rectangle mDrawRect;//used for draw
    public Rectangle mColRect;//used for collision detection
	public Vector3 position;
    public Vector3 velocity;
    public Vector3 acceleration;
    protected World world;
    public boolean isFront = false;// is sprite drawn after player, so that it appears like player walks behind it
    public float mRotationX, mRotationY, mRotationZ;//degrees

	public enum WorldState
    {
        WALKING, JUMPING, DYING, DUCKING
    }

    public enum TKey
    {
        stand_right("stand_right"),
        walk_right_1("walk_right-1"),
        walk_right_2("walk_right-2"),
        jump_right("jump_right"),
        fall_right("fall_right"),
        dead_right("dead_right"),
        duck_right("duck_right"),
        climb_left("climb_left"),
        climb_right("climb_right"),
        throw_right_1("throw_right_1"),
        throw_right_2("throw_right_2"),
        one("1"),
        two("2"),
        three("3"),;

        String mValue;
        TKey(String value)
        {
            mValue = value;
        }

        @Override
        public String toString()
        {
            return mValue;
        }
    }

    public GameObject(World world, Vector3 position, float width, float height)
    {
        this.mDrawRect = new Rectangle(position.x, position.y, width, height);
		this.position = position;
        this.world = world;
        velocity = new Vector3(0, 0, 0);
        acceleration = new Vector3(0, 0, 0);
    }
	
	public void updateBounds()
    {
        if (mColRect != null)
        {
            mDrawRect.x = mColRect.x;
            mDrawRect.y = mColRect.y;
        }
    }

    public abstract void _render(SpriteBatch spriteBatch);
    public abstract void _update(float delta);
    public abstract void initAssets();

    public void dispose()
    {
    }

    @Override
    public void reset()
    {
        position.set(0, 0, 0);
        velocity.set(0, 0, 0);
        acceleration.set(0, 0, 0);
        mDrawRect.set(0, 0, 0, 0);
        mColRect = null;
    }

    /**whether this object acts as bullet when hitting other objects (enemies, mario)*/
    public boolean isBullet()
    {
        return false;
    }

    @Override
    public String toString()
    {
        return "GameObject{" +
                "\n\tmDrawRect=" + mDrawRect +
                "\n\t mColRect=" + mColRect +
                "\n\t position=" + position +
                "\n\t velocity=" + velocity +
                "\n\t acceleration=" + acceleration +
                "\n\t world=" + world +
                "\n\t isFront=" + isFront +
                "\n\t mRotationX=" + mRotationX +
                "\n\t mRotationY=" + mRotationY +
                "\n\t mRotationZ=" + mRotationZ +
                "\n}";
    }
}
