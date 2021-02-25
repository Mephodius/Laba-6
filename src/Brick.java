import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class Brick {
    private int hp;
    private int width;
    private int heigth;
    private int x;
    private int y;
    private boolean isSelected=false;
    private Color color;
    Brick(Field field){
        this.hp = (int)Math.round(Math.random()*98)+1;
        System.out.println(hp);
        this.width = (int)Math.round(Math.random()*56)+100;
        this.heigth = (int)Math.round(Math.random()*56)+100;
        this.color = new Color(hp*255/99, hp*255/99, hp*255/99);
        this.x = (int)(Math.round(Math.random()*(field.getSize().getWidth()-width))+width/2);
        this.y = (int)(Math.round(Math.random()*(field.getSize().getHeight()-heigth))+heigth/2);
    }
    public void paint(Graphics2D canvas){
        this.color = new Color((99-hp)*255/99, (99-hp)*255/99, (99-hp)*255/99);
        canvas.setColor(color);
        canvas.setPaint(color);
        Rectangle2D.Double rect = new Rectangle2D.Double(x-width/2,y-heigth/2, width, heigth);
        canvas.draw(rect);
        canvas.fill(rect);
        canvas.setColor(Color.black);
        canvas.setFont(new Font("Serif", 0, 10));
         canvas.drawString(Integer.toString(hp),x,y);
    }
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    public int getWidth(){
    return width;
    }
    public int getHeight(){
        return heigth;
    }
    public void Hit(){
        hp--;
    }
    public boolean Broke(){
        if(hp<=0)
            return true;
        return false;
    }
    public void Selected(){
        isSelected=true;
    }
    public void isntSelected(){
        isSelected=false;
    }
}
