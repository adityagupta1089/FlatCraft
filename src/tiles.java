package object;

import org.andengine.entity.sprite.Sprite;



/* EDITED BY SACHIN BIJALWAN
 * CSP 203- GROUP NO - 2
 */
public class tiles extends Sprite {
 
	public boolean passable;     //specifies whether tile is passable or not
	
	Sprite tile;  // sprite for tiles

	///////////////////////////////////////////////////////////////////
	public enum categories{
		;//Initialize the variable in the format var(number)
		private int number;
		
		//getter function for number
		public int getnum(){            
			return this.number;
			}
		
		private categories(int number)      //categories constructor
		{
			this.number=number;
		}
	}      //enum for different categories of tiles
	
	//////////////////////////////////////////////////////////////////////
	
	public void initialize_tile(){      // function for initializing passable value
	    
		if(categories.*need something here*.getnum< _FIXED_CONSTANT_) //I have assumed that number value
			                                                          //of passable tiles is going to be 
		{                                                             //less than a fixed constatn
			passable = true;
		}
		else
		{
			passable = false;
		}
	}
	
  public tiles(int x,int y,ITextureRegion textureregion,int categorynumber){     //constructor
	   tile=new Sprite(x,y,textureregion,mEngine.getVertexBufferObjectManager());      // Need verification
	   /*can't figure out the way to initialize the enum using category number do it here*/
	   
  }
  
}
