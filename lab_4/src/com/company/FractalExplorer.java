package com.company;
import java.awt.*;
import javax.swing.*;
import java.awt.geom.Rectangle2D;
import java.awt.event.*;

public class FractalExplorer
{
    private int displaySize;

    private JImageDisplay image;
    private FractalGenerator fractal;
    private Rectangle2D.Double range;

    public FractalExplorer(int size) //конструктор, принимающий и сохраняющий размер изображения, а также инициализирующий
    {                               //объекты диапазона и фрактал-генератора
        displaySize = size;
        fractal = new Mandelbrot();
        range = new Rectangle2D.Double();
        fractal.getInitialRange(range);
        image = new JImageDisplay(displaySize, displaySize);
    }
    public void createAndShowGUI() //графический интерфейс Swing
    {
        image.setLayout(new BorderLayout());
        JFrame frame = new JFrame("Fractal Explorer");

        frame.add(image, BorderLayout.CENTER);
        JButton resetButton = new JButton("Reset Display"); //кнопка очистки
        ButtonHandler resetHandler = new ButtonHandler(); //кнопка сброса
        resetButton.addActionListener(resetHandler);

        MouseHandler click = new MouseHandler(); //реагирует на клики пользователя
        image.addMouseListener(click);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //закрыть окно
        JPanel myPanel = new JPanel();
        frame.add(myPanel, BorderLayout.NORTH);

        JPanel buttons = new JPanel();
        buttons.add(resetButton);
        frame.add(buttons, BorderLayout.SOUTH);

        /** запрещают изменять параметры рамки и делает саму рамку видимой **/
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
    }
    public void drawFractal()
    {
        for (int x = 0; x < displaySize; x++){
            for (int y = 0; y < displaySize; y++)
            {
                double xCoord = fractal.getCoord(range.x, range.x + range.width, displaySize, x);
                double yCoord = fractal.getCoord(range.y, range.y + range.height, displaySize, y);
                int iteration = fractal.numIterations(xCoord, yCoord); //кол-во итераций для координат в область отображения
                if (iteration == -1) //если итерация == -1, то пиксели становятся черными
                {
                    image.drawPixel(x, y, 0);
                }
                else
                {
                    float hue = 0.7f + (float) iteration / 200f; //выбираем цвет пикселя на основе числа итераций
                    int rgbColor = Color.HSBtoRGB(hue, 1f, 1f);
                    image.drawPixel(x, y, rgbColor); //обновляем цвета пикселей
                }
            }
        }
        image.repaint(); //обновленное изображение
    }
    private class ButtonHandler implements ActionListener //внутренний класс для обработки кнопок сброса
    {
        public void actionPerformed(ActionEvent e)
        {
            String command = e.getActionCommand(); //находим источник действия
            if (command.equals("Reset Display")) //если кнопка сброса - сбросить дисплей и нарисовать фрактал
            {
                fractal.getInitialRange(range);
                drawFractal();
            }
        }
    }
    private class MouseHandler extends MouseAdapter //внутренний класс для обработки дисплея
    {
        @Override
        public void mouseClicked(MouseEvent e)
        {
            int x = e.getX(); //координата в области клика мыши
            double xCoord = fractal.getCoord(range.x, range.x + range.width, displaySize, x);
            int y = e.getY(); //координата в области клика мыши
            double yCoord = fractal.getCoord(range.y, range.y + range.height, displaySize, y);
            fractal.recenterAndZoomRange(range, xCoord, yCoord, 0.5); //увеличиваем изображение с мастабом 0,5
            drawFractal(); //перерисовываем обновленный фрактал
        }
    }
}
