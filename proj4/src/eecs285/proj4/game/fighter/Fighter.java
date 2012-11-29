package eecs285.proj4.game.fighter;

import eecs285.proj4.game.Direction;
import eecs285.proj4.game.Input;
import eecs285.proj4.game.MovingObject;
import eecs285.proj4.util.Render;
import eecs285.proj4.util.Sprite;
import eecs285.proj4.util.UtilObject;

import org.newdawn.slick.Color;

public abstract class Fighter extends MovingObject {
	protected static final float GRAVITY = 100.0f;
	protected static final float MAX_HORIZONTAL = 100.0f;
	protected static final float MAX_VERTICAL = 1000.0f;
	protected static final float RECOIL_PERCENT = 0.25f;
	protected static final float RECOIL_TANGENT_PERCENT = 0.95f;
	
	protected String fighterName;
	protected FighterTrait fighterTrait;
	protected Input input;
	
	private int hitPercent;
	private int stock;
	private int kills;
	private int deaths;
	private int suicides;
	
	// All units use meters and seconds
	//protected float minWalkSpeed;
	protected float maxWalkSpeed;
	protected float forwardAcceleration;
	protected float reverseAcceleration;
	//protected float runSpeed;
	protected float firstJumpSpeed;
	protected float firstJumpMinTime;
	protected float firstJumpMaxTime;
	protected float firstJumpLiniencyTime;
	protected float secondJumpSpeed;
	protected float secondJumpMinTime;
	protected float secondJumpMaxTime;
	protected float maxFallSpeed;
	protected float maxFallSpeedHorizontal;

	protected boolean canMove;
	protected boolean facingLeft;
	protected boolean onGround;
	//protected boolean ducking;
	//protected boolean running;
	protected boolean canDoubleJump;
	protected double flightTime;		// Is flying if > 0.0f
	protected double stunTime;			// Is stunned if > 0.0f
	protected double jumpTime;			// Is jumping if > 0.0f
	public Attack currentAttack;
	protected boolean doPrimaryAttack;
	protected boolean doSecondaryAttack;
	
	protected Sprite currentSprite;
	protected float visualWidth;
	protected float visualHeight;
	protected Color playerColor;
	
	public Fighter(String name, FighterTrait trait,
			float left, float right, float top, float bottom) {
		super(left, right, top, bottom);
		
		fighterName = name;
		fighterTrait = trait;
		canMove = true;
	}
	
	public void SetInput(Input input){
		this.input = input;
	}
	
	public void SetStock(int stock){
		this.stock = stock;
	}
	
	public void SetColor(Color color){
		playerColor = color;
	}
	
	public boolean GetOnGround(){
		return onGround;
	}
	
	public boolean GetFacingLeft(){
		return facingLeft;
	}
	
	public String toString(){
		return fighterName;
	}
	
	public void getInput(double delta){
		input.getInput();
		
		// Update Primary attack
		if(!input.primaryAttack){
			doPrimaryAttack = false;
		}
		else if(!input.primaryAttackLast){
			doPrimaryAttack = true;
		}		
		
		// Update Secondary attack
		if(!input.secondaryAttack){
			doSecondaryAttack = false;
		}
		else if(!input.secondaryAttackLast){
			doSecondaryAttack = true;
		}
	}
	
	public void setSpawn(float xCenter, float yBase){
		posX = xCenter - sizeX*0.5f;
		posY = yBase - sizeY;
	}

	public void setVelocity(float velocityX, float velocityY){
		velX = velocityX;
		velY = velocityY;
	}
	
	public void step(double delta){
		lastPosX = posX;
		lastPosY = posY;
		
		if(onGround){
			canDoubleJump = true;
		}
		
		if(flightTime > 0.0f){
			flightTime -= delta;
			
			if(currentAttack != null){
				currentAttack = null;
			}
		}
		
		if(stunTime > 0.0f){
			stunTime -= delta;
		}
		
		doDuck(delta);
		doMovement(delta);
		doJump(delta);
		doAttack(delta);
		doPlayerDirection(delta);
		
		if(velX > MAX_HORIZONTAL) velX = MAX_HORIZONTAL;
		if(velX < -MAX_HORIZONTAL) velX = -MAX_HORIZONTAL;
		if(velY > MAX_VERTICAL) velY = MAX_VERTICAL;
		if(velY < -MAX_VERTICAL) velY = -MAX_VERTICAL;

		posX += delta * velX;
		posY += delta * velY;
		
		// Don't mess with on ground if we've disabled gravity.
		if(currentAttack == null || !currentAttack.GetOverrideGravity()){
			onGround = false;
		}
	}
	
	private void doJump(double delta){
		if(stunTime > 0.0f || currentAttack != null){
			return;
		}
		
		if(onGround){
			if(input.jump){
				if(jumpTime >= -firstJumpLiniencyTime && jumpTime <= 0.0d){
					jumpTime = delta;
				}
				else if(jumpTime > 0.0d && jumpTime <= firstJumpMaxTime){
					jumpTime += delta;
				}
			}
			else{
				jumpTime = 0.0d;
			}
		}
		else{
			if(input.jump){
				if(jumpTime >= -firstJumpLiniencyTime && jumpTime <= 0.0d){
					jumpTime -= delta;
				}
				else if(jumpTime > 0.0d && jumpTime <= firstJumpMaxTime){
					jumpTime += delta;
				}
			}
			else{
				if(jumpTime > 0.0d && jumpTime < firstJumpMinTime){
					jumpTime += delta;
				}
				else{
					jumpTime = 0.0d;
				}
			}
		}
		
		if(jumpTime > 0.0d && jumpTime < firstJumpMaxTime){
			velY = -firstJumpSpeed;
		}
	}
	
	private void doDuck(double delta){}
	
	private void doMovement(double delta){
		if(currentAttack != null && currentAttack.GetOverrideGravity()){
			velY = 0.0f;
		}
		else{
			velY += delta * GRAVITY;
		}
		
		if(stunTime > 0.0f || flightTime > 0.0f){
			return;
		}
		
		if(onGround){
			if(input.xAxis != 0.0f && !(currentAttack != null && (currentAttack.GetStationaryOnGround() || currentAttack.GetOverrideGravity()))){
				float desiredSpeed = input.xAxis * maxWalkSpeed;
				double rateOfChange;
				
				// Change speed faster if we are "in reverse"
				if(velX < desiredSpeed && velX < 0.0f
				|| velX > desiredSpeed && velX > 0.0f){
					rateOfChange = reverseAcceleration * delta;
				}
				else{
					rateOfChange = forwardAcceleration * delta;
				}
				
				// Move closer to desired speed
				if(velX - rateOfChange > desiredSpeed){
					velX -= rateOfChange;
				}
				else if(velX + rateOfChange < desiredSpeed){
					velX += rateOfChange;
				}
				else{
					velX = desiredSpeed;
				}
			}
			else{
				velX *= 0.75f;
			}
			
			if(velX > maxWalkSpeed) velX = maxWalkSpeed;
			if(velX < -maxWalkSpeed) velX = -maxWalkSpeed;
		}
		else{
			if(currentAttack == null || !currentAttack.GetStationaryInAir()){
				// Check if we are trying to go in the opposite direction that we are moving
				if(velX * input.xAxis < 0.0f){
					velX += (double)input.xAxis * (delta * (double)reverseAcceleration);
				}
				else{
					velX += (double)input.xAxis * (delta * (double)forwardAcceleration);
				}
			}
			
			if(velX > maxFallSpeedHorizontal) velX = maxFallSpeedHorizontal;
			if(velX < -maxFallSpeedHorizontal) velX = -maxFallSpeedHorizontal;
			if(velY > maxFallSpeed) velY = maxFallSpeed;
			if(velY < -maxFallSpeed) velY = -maxFallSpeed;
		}
	}
	
	private void doAttack(double delta){
		if(stunTime > 0.0f){
			return;
		}
		
		// Update current attack
		if(currentAttack != null){
			currentAttack.step(delta);
			if(currentAttack.isComplete()){
				currentAttack = null;
			}
		}
		
		// Pick a new attack
		if(currentAttack == null){
			if(doPrimaryAttack){
				doPrimaryAttack = false;
				
				// TODO : put this somewhere else
				CollisionBox[] boxes = new CollisionBox[1];
				boxes[0] = new CollisionBox();
				boxes[0].startBox = new UtilObject(0.0f, 1.0f, -1.0f, -0.5f);
				boxes[0].endBox = new UtilObject(0.0f, 2.0f, -1.0f, -0.5f);
				boxes[0].delay = 0.0f;
				boxes[0].duration = 0.1f;
				boxes[0].damage = 6;
				boxes[0].hitSpeedX = 10.0f;
				boxes[0].hitSpeedY = -5.0f;
				boxes[0].oppositeHitSpeedX = -3.0f;
				boxes[0].oppositeHitSpeedY = -8.0f;
				boxes[0].flightTime = 0.25f;
				boxes[0].stunTime = 0.1f;
				boxes[0].isStationaryInAir = false;
				boxes[0].isStationaryOnGround = true;
				boxes[0].isOverridingGravity = true;
				boxes[0].canChangeDirection = false;
				
				currentAttack = new Attack(boxes);
			}
		}
	}
	
	public void doGetHit(double delta, float velX, float velY, float flightTime, float stunTime, int damage){
		final float scale = 1.0f + (float)hitPercent / 100.0f;
		velX *= scale;
		velY *= scale;
		flightTime *= scale;
		stunTime *= scale;

		hitPercent += damage;
		this.flightTime = flightTime;
		this.stunTime = stunTime;
		
		// Update x vel
		if(Math.abs(this.velX) < Math.abs(velX)){
			this.velX = velX;
		}
		else{
			if(velX > 0.0f){
				this.velX = Math.abs(this.velX);
			}
			else{
				this.velX = -Math.abs(this.velX);
			}
		}
	
		// Update y vel
		if(Math.abs(this.velY) < Math.abs(velY)){
			this.velY = velY;
		}
		else{
			if(velY > 0.0f){
				this.velY = Math.abs(this.velY);
			}
			else{
				this.velY = -Math.abs(this.velY);
			}
		}
		
	}
	
	private void doPlayerDirection(double delta){
		if(stunTime > 0.0f || flightTime > 0.0f || (currentAttack != null && !currentAttack.GetCanChangeDirection())){
			return;
		}
		
		if(currentAttack != null && currentAttack.GetCanChangeDirection()){
			if(input.xAxis != 0.0f){
				facingLeft = input.xAxis < 0.0f;
			}
		}
		else{
			if(facingLeft){
				if(velX >= 0 && input.xAxis > 0.0f){
					facingLeft = false;
				}
			}
			else{
				if(velX <= 0 && input.xAxis < 0.0f){
					facingLeft = true;
				}
			}
		}
	}
	
	public void render(double delta){
		Render.render(currentSprite, getCenterX()-visualWidth*0.5f, getCenterX()+visualWidth*0.5f,
									 getBottomEdge()-visualHeight, getBottomEdge(), playerColor, facingLeft);
		
		handleRender(delta);
	}
	
	public void collideWithSolid(Direction dir, float pos){
		if(flightTime > 0.0f){
			switch(dir){
			case North:
				posY = Math.min(pos, lastPosY);
				velY = Math.abs(this.velY)*RECOIL_PERCENT;
				velX = velX*RECOIL_TANGENT_PERCENT;
				break;
			case South:
				posY = Math.max(pos - sizeY, lastPosY);
				velY = -Math.abs(this.velY)*RECOIL_PERCENT;
				velX = velX*RECOIL_TANGENT_PERCENT;
				onGround = true;
				break;
			case East:
				posX = Math.min(pos, lastPosX);
				velX = -Math.abs(this.velX)*RECOIL_PERCENT;
				velY = velY*RECOIL_TANGENT_PERCENT;
				break;
			case West:
				posX = Math.max(pos - sizeX, lastPosX);
				velX = Math.abs(this.velX)*RECOIL_PERCENT;
				velY = velY*RECOIL_TANGENT_PERCENT;
				break;
			}
		}
		else{
			switch(dir){
			case North:
				posY = Math.min(pos, lastPosY);
				velY = Math.max(velY, 0.0f);
				break;
			case South:
				posY = Math.max(pos - sizeY, lastPosY);
				velY = Math.min(velY, 0.0f);
				onGround = true;
				//if(fighterState != Flying && fighterState != Ducking){
				//	fighterState = OnGround;
				//}
				break;
			case West:
				posX = Math.min(pos, lastPosX);
				velX = Math.max(velX, 0.0f);
				break;
			case East:
				posX = Math.max(pos - sizeX, lastPosX);
				velX = Math.min(velX, 0.0f);
				break;
			}
		}
	}
	
	public void collideWithPlatform(float pos){
		if(flightTime > 0.0f){
			posY = Math.max(pos - sizeY, lastPosY);
			velY = Math.min(velY, 0.0f)*RECOIL_PERCENT;
			velX = velX*RECOIL_TANGENT_PERCENT;
			onGround = true;
		}
		else{
			if(input.yAxis <= 0.0f){
				posY = Math.max(pos - sizeY, lastPosY);
				velY = Math.min(velY, 0.0f);
				onGround = true;
			}
		}
	}

	protected abstract void handleStep(double delta);
	protected abstract void handleRender(double delta);
	protected abstract void handleCollideWithSolid(Direction dir, float pos);
	protected abstract void handleCollideWithPlatform(float pos);
}