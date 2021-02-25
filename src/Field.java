import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.swing.JPanel;
import javax.swing.Timer;
@SuppressWarnings("serial")
public class Field extends JPanel {
    // Флаг приостановленности движения
    private boolean paused;
    // Динамический список скачущих мячей
    private ArrayList<BouncingBall> balls = new ArrayList<BouncingBall>();
    private ArrayList<Brick> bricks = new ArrayList<Brick>();
    // Класс таймер отвечает за регулярную генерацию событий ActionEvent
// При создании его экземпляра используется анонимный класс,
// реализующий интерфейс ActionListener
    private Timer repaintTimer = new Timer(1, new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
// Задача обработчика события ActionEvent - перерисовка окна
            repaint();
        }
    });


    // Конструктор класса BouncingBall
    public Field() {
// Установить цвет заднего фона белым
        setBackground(Color.white);
        this.addMouseMotionListener(new Field.MouseMotionHandler());
        this.addMouseListener(new Field.MouseHandler());
// Запустить таймер
        repaintTimer.start();
    }

    // Унаследованный от JPanel метод перерисовки компонента
    public void paintComponent(Graphics g) {
// Вызвать версию метода, унаследованную от предка
        super.paintComponent(g);
        Graphics2D canvas = (Graphics2D) g;
// Последовательно запросить прорисовку от всех мячей из списка
        for (BouncingBall ball : balls) {
            ball.paint(canvas);
        }
        for (Brick brick : bricks) {
            brick.paint(canvas);
        }
    }

    // Метод добавления нового мяча в список
    public void addBall() {
//Заключается в добавлении в список нового экземпляра BouncingBall
// Всю инициализацию положения, скорости, размера, цвета
// BouncingBall выполняет сам в конструкторе
        balls.add(new BouncingBall(this));
    }

    public void addBrick() {
        bricks.add(new Brick(this));
    }

    public ArrayList<BouncingBall> getBalls() {
        return balls;
    }

    public ArrayList<Brick> getBricks() {
        return bricks;
    }

    // Метод синхронизированный, т.е. только один поток может
// одновременно быть внутри
    public synchronized void pause() {
// Включить режим паузы
        paused = true;
    }

    // Метод синхронизированный, т.е. только один поток может
// одновременно быть внутри
    public synchronized void resume() {
// Выключить режим паузы
        paused = false;
// Будим все ожидающие продолжения потоки
        notifyAll();
    }

    // Синхронизированный метод проверки, может ли мяч двигаться
// (не включен ли режим паузы?)
    public synchronized void canMove(BouncingBall ball) throws
            InterruptedException {
        if (paused) {
// Если режим паузы включен, то поток, зашедший
// внутрь данного метода, засыпает
            wait();
        }
    }
    public void BrickBroke(int i){
        if(bricks.get(i).Broke())
            bricks.remove(i);
    }
    public class MouseHandler extends MouseAdapter {
        public MouseHandler() {
        }

        public void mouseClicked(MouseEvent ev) {
            boolean flag=true;
            if(balls.size()>0) {
                for (int i = 0; i<balls.size();i++) {
                    if (Math.hypot(ev.getX() - balls.get(i).getX(), ev.getY() - balls.get(i).getY()) < balls.get(i).getRadius()) {
                        if (ev.getButton() == 1) {
                            balls.get(i).increasing();
                            flag=false;
                        }
                        if (ev.getButton() == 3) {
                            balls.get(i).decreasing();
                            flag = false;
                        }
                    }
                }
                if (ev.getButton() == 3 && flag) {
                    balls.get(balls.size()-1).marked();
                    balls.remove(balls.size() - 1);

                }
            }
            if (ev.getButton() == 1 && flag) {
                addBall();
            }
        }

        public void mouseReleased(MouseEvent ev) {
            if (ev.getButton() == 1) {

            }
        }

        }

        public void mousePressed(MouseEvent ev) {


    }
    public class MouseMotionHandler implements MouseMotionListener {
        public MouseMotionHandler() {
        }

        public void mouseMoved(MouseEvent ev) {
            if(balls.size()>0) {
                double min = Math.hypot(getSize().getHeight(), getSize().getWidth());
                int minindex = 0;
                for (int i = 0; i < balls.size(); i++) {
                    balls.get(i).isntSelected();
                    double temp = Math.hypot(ev.getX() - balls.get(i).getX(), ev.getY() - balls.get(i).getY());
                    if (temp < min) {
                        min = temp;
                        minindex = i;
                    }
                }
                balls.get(minindex).Selected();
            }
        }

        public void mouseDragged(MouseEvent ev) {

        }
    }

}