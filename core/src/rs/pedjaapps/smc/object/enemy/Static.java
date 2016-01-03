package rs.pedjaapps.smc.object.enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.object.GameObject;
import rs.pedjaapps.smc.object.World;
import rs.pedjaapps.smc.utility.Constants;
import rs.pedjaapps.smc.utility.GameSave;
import rs.pedjaapps.smc.utility.Utility;

/**
 * Created by pedja on 18.5.14..
 */
public class Static extends Enemy
{
    public static final float POS_Z = 0.094f;
    private Texture texture;
    private TextureRegion region;

    public Static(World world, Vector3 position, float width, float height, float speed, int fireResistance, float iceResistance)
    {
        super(world, position, width, height);
        mSpeed = speed;
        mCanBeHitFromShell = 0;
        mFireResistant = fireResistance;
        mIceResistance = iceResistance;
        position.z = POS_Z;
    }

    @Override
    public void initAssets()
    {
        texture = Assets.manager.get(textureName);
    }

    @Override
    public void dispose()
    {
        texture = null;
        region = null;
    }

    @Override
    public boolean isBullet()
    {
        return true;
    }

    @Override
    public void render(SpriteBatch spriteBatch)
    {
        if (texture != null)
        {
            float width = Utility.getWidth(texture, mDrawRect.height);
            float originX = width * 0.5f;
            float originY = mDrawRect.height * 0.5f;
            spriteBatch.draw(texture, mDrawRect.x, mDrawRect.y, originX, originY, width, mDrawRect.height,
                    1, 1, -mRotationZ, 0, 0, texture.getWidth(), texture.getHeight(), false, false);
        }
    }

    @Override
    public boolean canBeKilledByJumpingOnTop()
    {
        return false;
    }

    private float getRotation()
    {
        float circumference = (float) Math.PI * (mColRect.width);
        float deltaVelocity = mSpeed * Gdx.graphics.getDeltaTime();

        float step = circumference / deltaVelocity;

        float frameRotation = 360 / step;//degrees
        mRotationZ += frameRotation;
        if (mRotationZ > 360) mRotationZ = mRotationZ - 360;

        return mRotationZ;
    }

    public void update(float deltaTime)
    {
        stateTime += deltaTime;
        mRotationZ = getRotation();
		if(deadByBullet)
        {
            // Setting initial vertical acceleration
            acceleration.y = Constants.GRAVITY;

            // Convert acceleration to frame time
            acceleration.scl(deltaTime);

            // apply acceleration to change velocity
            velocity.add(acceleration);

            checkCollisionWithBlocks(deltaTime, false, false);
        }
    }

    @Override
    protected TextureRegion getDeadTextureRegion()
    {
        if(region == null)
        {
            region = new TextureRegion(texture);
        }
        return region;
    }

    @Override
    protected boolean handleCollision(GameObject object, boolean vertical)
    {
        if(object instanceof Enemy && ((Enemy)object).handleCollision)
        {
            GameSave.save.points += ((Enemy)object).mKillPoints;
            ((Enemy)object).downgradeOrDie(this, true);
        }
        return true;
    }
}
