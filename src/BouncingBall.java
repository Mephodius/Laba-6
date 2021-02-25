import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;

public class BouncingBall implements Runnable {
    private static final String path = System.getProperty("user.dir");
    // Максимальный радиус, который может иметь мяч
    private static final int MAX_RADIUS = 100;
    // Минимальный радиус, который может иметь мяч
    private static final int MIN_RADIUS = 3;
    // Максимальная скорость, с которой может летать мяч
    private static final int MAX_SPEED = 15;
    private static final double e = Math.pow(10, -6);
    private static final double G = Math.pow(10, -8);
    private Field field;
    private int radius;
    private Color color;
    // Текущие координаты мяча
    private int x;
    private int y;
    // Вертикальная и горизонтальная компонента скорости
    private int speed;
    private double speedX;
    private double speedY;
    private double angle;
    private Clip clip;
    private boolean isSelected = false;
    private boolean markOfDeath = false;
    Thread thisThread;

    // Конструктор класса BouncingBall
    public BouncingBall(Field field) {
// Необходимо иметь ссылку на поле, по которому прыгает мяч,
// чтобы отслеживать выход за его пределы
// через getWidth(), getHeight()
        this.field = field;
// Радиус мяча случайного размера
        radius = new Double(Math.random() * (MAX_RADIUS -
                MIN_RADIUS)).intValue() + MIN_RADIUS;
// Абсолютное значение скорости зависит от диаметра мяча,
// чем он больше, тем медленнее
        speed = new Double(Math.round(MAX_SPEED * MAX_RADIUS / radius / 5)).intValue();
        if (speed > MAX_SPEED) {
            speed = MAX_SPEED;
        }
// Начальное направление скорости тоже случайно,
// угол в пределах от 0 до 2PI
        angle = Math.random() * 2 * Math.PI;
// Вычисляются горизонтальная и вертикальная компоненты скорости
        speedX = speed * Math.cos(angle);
        speedY = speed * Math.sin(angle);
// Цвет мяча выбирается случайно
        color = new Color((float) Math.random(), (float) Math.random(),
                (float) Math.random());
// Начальное положение мяча случайно
        x = (int) (Math.random() * (field.getSize().getWidth() - 2 * radius)) + radius;
        y = (int) (Math.random() * (field.getSize().getHeight() - 2 * radius)) + radius;
// Создаѐм новый экземпляр потока, передавая аргументом
// ссылку на класс, реализующий Runnable (т.е. на себя)
        thisThread = new Thread(this);
// Запускаем поток
        thisThread.start();
    }

    // Метод run() исполняется внутри потока. Когда он завершает работу,
// то завершается и поток
    public void run() {
        try {
// Крутим бесконечный цикл, т.е. пока нас не прервут,
// мы не намерены завершаться
            while (true) {
// Синхронизация потоков на самом объекте поля
// Если движение разрешено - управление будет
// возвращено в метод
// В противном случае - активный поток заснѐт
                field.canMove(this);

                if (field.getBalls().size() > 0) {
                    if (field.getBalls().size() > 1) {
                        for (int i = 0; i < field.getBalls().size(); i++) {
                            double distance = Math.hypot(x + speedX - field.getBalls().get(i).x - field.getBalls().get(i).speedX, y + speedY - field.getBalls().get(i).y - field.getBalls().get(i).speedY);
                            if (distance > e) {
                                double tempangle = Math.atan((field.getBalls().get(i).y + field.getBalls().get(i).speedY - y - speedY) / (field.getBalls().get(i).x + field.getBalls().get(i).speedX - x - speedX));
                                double Gravity = G * Math.pow(field.getBalls().get(i).radius, 2) * Math.PI / Math.pow(distance, 2);
                                //проверка на коллизию при следующем движении
                                if (distance < radius + field.getBalls().get(i).radius) {
                                    //System.out.println("Radius1 = "+radius + " raduis2 = "+field.getBalls().get(i).radius+" Distance "+ Math.hypot(x+speedX-field.getBalls().get(i).x-field.getBalls().get(i).speedX,y+speedY-field.getBalls().get(i).y-field.getBalls().get(i).speedY));
                                    if (clip != null && !clip.isActive()) {
                                        int rand = Math.round((float) Math.random()) + 21;
                                        File soundFile = new File(path + "\\Resources\\" + rand + ".wav"); //Звуковой файл
                                        AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
                                        clip = AudioSystem.getClip();
                                        clip.open(ais);
                                        clip.setFramePosition(0);
                                        clip.start();
                                    }
                                    if (clip == null) {
                                        int rand = Math.round((float) Math.random()) + 21;
                                        File soundFile = new File(path + "\\Resources\\" + rand + ".wav"); //Звуковой файл
                                        AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
                                        clip = AudioSystem.getClip();
                                        clip.open(ais);
                                        clip.setFramePosition(0);
                                        clip.start();
                                    }
                                    angle = Math.random() * 2 * Math.PI;
                                    speedX = speed * Math.cos(angle);
                                    speedY = speed * Math.sin(angle);
                                    if (radius >= field.getBalls().get(i).radius && x > field.getBalls().get(i).x) {
                                        x += (radius + field.getBalls().get(i).radius - distance) * Math.cos(tempangle);
                                        y += (radius + field.getBalls().get(i).radius - distance) * Math.sin(tempangle);
                                    }
                                    if (radius >= field.getBalls().get(i).radius && x <= field.getBalls().get(i).x) {
                                        x -= (radius + field.getBalls().get(i).radius - distance) * Math.cos(tempangle);
                                        y -= (radius + field.getBalls().get(i).radius - distance) * Math.sin(tempangle);
                                    }
                                }
                                System.out.println("Before: speedX " + speedX + " speedY " + speedY);
                                speedX += Gravity * Math.cos(tempangle);
                                speedY += Gravity * Math.sin(tempangle);
                                System.out.println("After: speedX " + speedX + " speedY " + speedY);
                            }
                        }
                    }
                    if (field.getBricks().size() > 0) {
                        for (int i = 0; i < field.getBricks().size(); i++) {
                            if (Math.abs(x - field.getBricks().get(i).getX()) < radius + field.getBricks().get(i).getWidth() / 2 && Math.abs(y - field.getBricks().get(i).getY()) <= field.getBricks().get(i).getHeight() / 2) {
                                speedX = -speedX;
                                angle = Math.atan(speedY / speedX);
                                field.getBricks().get(i).Hit();
                                field.BrickBroke(i);
                            }
                            if (Math.abs(y - field.getBricks().get(i).getY()) < radius + field.getBricks().get(i).getHeight() / 2 && Math.abs(x - field.getBricks().get(i).getX()) <= field.getBricks().get(i).getWidth() / 2) {
                                speedY = -speedY;
                                angle = Math.atan(speedY / speedX);
                                field.getBricks().get(i).Hit();
                                field.BrickBroke(i);
                            }
                        }
                    }
                    if (x + speedX <= radius) {
                        if (clip != null && !clip.isActive()) {
                            int rand = Math.round((float) Math.random()) + 11;
                            File soundFile = new File(path + "\\Resources\\" + rand + ".wav"); //Звуковой файл
                            AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
                            clip = AudioSystem.getClip();
                            clip.open(ais);
                            clip.setFramePosition(0);
                            clip.start();
                        }
                        if (clip == null) {
                            int rand = Math.round((float) Math.random()) + 11;
                            File soundFile = new File(path + "\\Resources\\" + rand + ".wav"); //Звуковой файл
                            AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
                            clip = AudioSystem.getClip();
                            clip.open(ais);
                            clip.setFramePosition(0);
                            clip.start();
                        }
// Достигли левой стенки, отскакиваем право
                        speedX = -speedX;
                        angle = Math.atan(speedY / speedX);
                        x = radius + 1;
                    } else if (x + speedX >= field.getWidth() - radius) {
                        if (clip != null && !clip.isActive()) {
                            int rand = Math.round((float) Math.random()) + 11;
                            File soundFile = new File(path + "\\Resources\\" + rand + ".wav"); //Звуковой файл
                            AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
                            clip = AudioSystem.getClip();
                            clip.open(ais);
                            clip.setFramePosition(0);
                            clip.start();
                        }
                        if (clip == null) {
                            int rand = Math.round((float) Math.random()) + 11;
                            File soundFile = new File(path + "\\Resources\\" + rand + ".wav"); //Звуковой файл
                            AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
                            clip = AudioSystem.getClip();
                            clip.open(ais);
                            clip.setFramePosition(0);
                            clip.start();
                        }
// Достигли правой стенки, отскок влево
                        speedX = -speedX;
                        angle = Math.atan(speedY / speedX);
                        x = new Double(field.getWidth() - radius).intValue() - 1;
                    } else if (y + speedY <= radius) {
                        if (clip != null && !clip.isActive()) {
                            int rand = Math.round((float) Math.random()) + 11;
                            File soundFile = new File(path + "\\Resources\\" + rand + ".wav"); //Звуковой файл
                            AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
                            clip = AudioSystem.getClip();
                            clip.open(ais);
                            clip.setFramePosition(0);
                            clip.start();
                        }
                        if (clip == null) {
                            int rand = Math.round((float) Math.random()) + 11;
                            File soundFile = new File(path + "\\Resources\\" + rand + ".wav"); //Звуковой файл
                            AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
                            clip = AudioSystem.getClip();
                            clip.open(ais);
                            clip.setFramePosition(0);
                            clip.start();
                        }
// Достигли верхней стенки
                        speedY = -speedY;
                        angle = Math.atan(speedY / speedX);
                        y = radius + 1;
                    } else if (y + speedY >= field.getHeight() - radius) {
                        if (clip != null && !clip.isActive()) {
                            int rand = Math.round((float) Math.random()) + 11;
                            File soundFile = new File(path + "\\Resources\\" + rand + ".wav"); //Звуковой файл
                            AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
                            clip = AudioSystem.getClip();
                            clip.open(ais);
                            clip.setFramePosition(0);
                            clip.start();
                        }
                        if (clip == null) {
                            int rand = Math.round((float) Math.random()) + 11;
                            File soundFile = new File(path + "\\Resources\\" + rand + ".wav"); //Звуковой файл
                            AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
                            clip = AudioSystem.getClip();
                            clip.open(ais);
                            clip.setFramePosition(0);
                            clip.start();
                        }
// Достигли нижней стенки
                        speedY = -speedY;
                        angle = Math.atan(speedY / speedX);
                        y = new Double(field.getHeight() - radius).intValue() - 1;
                    } else {
// Просто смещаемся
                        x += speedX;
                        y += speedY;
                    }
// Засыпаем на X миллисекунд, где X определяется
// исходя из скорости
// Скорость = 1 (медленно), засыпаем на 15 мс.
// Скорость = 15 (быстро), засыпаем на 1 мс.
                    thisThread.sleep(8);

                    if (markOfDeath) {
                        thisThread.interrupt();
                    }
                }
            }
        } catch (InterruptedException ex) {
// Если нас прервали, то ничего не делаем
// и просто выходим (завершаемся)
        } catch (LineUnavailableException lineUnavailableException) {
            lineUnavailableException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (UnsupportedAudioFileException unsupportedAudioFileException) {
            unsupportedAudioFileException.printStackTrace();
        }

    }

    public void increasing() {
        if (radius < MAX_RADIUS)
            radius++;
    }

    public void decreasing() {
        if (radius > MIN_RADIUS)
            radius--;
    }

    public int getRadius() {
        return radius;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void Selected() {
        isSelected = true;
    }

    public void isntSelected() {
        isSelected = false;
    }

    public void marked() {
        markOfDeath = true;
    }

    // Метод прорисовки самого себя
    public void paint(Graphics2D canvas) {
        canvas.setColor(color);
        canvas.setPaint(color);
        Ellipse2D.Double ball = new Ellipse2D.Double(x - radius, y - radius,
                2 * radius, 2 * radius);
        canvas.draw(ball);
        canvas.fill(ball);
        if (isSelected) {
            canvas.setPaint(Color.black);
            FontRenderContext context = canvas.getFontRenderContext();
            Rectangle2D bounds = new Font("Serif", 0, radius * 40 / MAX_RADIUS).getStringBounds(Integer.toString(radius), context);
            canvas.setFont(new Font("Serif", 0, radius * 40 / MAX_RADIUS));
            canvas.drawString(Integer.toString(radius), x, y);
        }
    }

    public void finalize() {

        clip.stop();
    }

}
