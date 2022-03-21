package com.company;
import java.awt.*;
import javax.swing.*;
import java.awt.geom.Rectangle2D;
import java.awt.event.*;



// Класс, позволяющий исследовать разные области фрактала благодаря его создания, отображения через графический интерфейс и обработки событий, которые вызваны взаимодействием приложения с пользователем
public class FractalExplorer
{
    // Ширина и высота отображения - это целочисленная, поэтому int
    private int displaySize;
    // JImageDisplay для обновления отображения изображения в разных методах в процессе вычисления фрактала
    private JImageDisplay display;
    // Объект, использующий ссылку на базовый класс для отображения других типов фракталов (будет на будущее)
    private FractalGenerator fractal;
     // Объект, который определяет размер текущего диапазона просмотра, то есть то, что показывает в настоящий момент
    private Rectangle2D.Double range;
     // Конструктор, принимающий размер дисплея и сохраняющий его, после чего инициализирующий объекты диапазона и генератора фрактала
    public FractalExplorer(int size) {
        // Сохраняет размер дисплея
        displaySize = size;
        // Инициализирует фрактальный генератор и объекты диапазона
        fractal = new Mandelbrot();
        range = new Rectangle2D.Double();
        fractal.getInitialRange(range);
        display = new JImageDisplay(displaySize, displaySize);

    }
     // Этот метод инициализирует графический интерфейс Swing с помощью JFrame, содержащий JImageDisplay объект и кнопку для сброса дисплея.
    public void createAndShowGUI()
    {
        display.setLayout(new BorderLayout());
        JFrame Frame = new JFrame("Fractal Explorer");
        // Добавляет и центрует объект изображения
        Frame.add(display, BorderLayout.CENTER);
        // Создание объекта кнопки сброса
        JButton resetButton = new JButton("Reset");
        Frame.add(resetButton, BorderLayout.SOUTH);
        ButtonHandler resetHandler = new ButtonHandler();
        // Обработка события нажатия на кнопку
        resetButton.addActionListener(resetHandler);
        MouseHandler click = new MouseHandler();
        display.addMouseListener(click);
        JPanel myPanel = new JPanel();
        Frame.add(myPanel, BorderLayout.NORTH);
        JPanel myBottomPanel = new JPanel();
        myBottomPanel.add(resetButton);
        Frame.add(myBottomPanel, BorderLayout.SOUTH);
        Frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Frame.pack();
        Frame.setVisible(true);
        Frame.setResizable(false);
    }
     // Данный метод является приватным, а также вспомогательным и служит для отображения фрактала. Его цикл заключается в том, что он проходит через каждый пиксель в отображении и вычисляет кол-во итераций для соответствующих координат в области отображения фракталов. Если кол-во итераций = -1, он ставит черный цвет пикселя, а иначе выбирает значение в зависимости от кол-ва итераций. По готовности обновляется дисплей
    private void drawFractal()
    {
        // Перебор каждого пикселя на имеющемся дисплее
        for (int x=0; x<displaySize; x++){
            for (int y=0; y<displaySize; y++){
                // Тут находятся координаты xCoord и yCoord в той области, где отображается фрактал
                double xCoord = fractal.getCoord(range.x,
                        range.x + range.width, displaySize, x);
                double yCoord = fractal.getCoord(range.y,
                        range.y + range.height, displaySize, y);
                // Расчет кол-ва итераций для координат в той области, где отображается фрактал
                int i = fractal.numIterations(xCoord, yCoord);
                // Если кол-во итераций = -1, поставить черный пиксель
                if (i == -1){
                    display.drawPixel(x, y, 0);
                }
                else{
                    // Взять в противном случае значение оттенка. В основу брать числа итераций
                    float hue = 0.7f + (float) i / 200f;
                    int rgbColor = Color.HSBtoRGB(hue, 1f, 1f);
                    // Обновление отображения цвета у каждого пикселя
                    display.drawPixel(x, y, rgbColor);
                }
            }
        }
        // Когда пиксели будут отрисованы, то перекрасить JImageDisplay в соответствии с текущим изображением
        display.repaint();
    }
    // Внутренний класс для обработки событий ActionListener от кнопки сброса
    private class ButtonHandler implements ActionListener
    {   // Обработчик сбросит диапазон к начальному диапазону, который задан генератором, а затем перерисует фрактал
        public void actionPerformed(ActionEvent e)
        {
            String command = e.getActionCommand();
            if (command.equals("Reset")) {
                fractal.getInitialRange(range);
                drawFractal();
            }
        }
    }
    //Внутренний класс для обработки событий MouseAdapter с дисплея
    private class MouseHandler extends MouseAdapter
    {
        // Когда идет нажатие мышкой, то происходит перемещение на указанные щелчком координаты. Приближение вполовину от нынешнего
        @Override //переопределяем метод базового класса
        public void mouseClicked(MouseEvent e)
        {
            // Принимает x координату нажатия мышью
            int x = e.getX();
            double xCoord = fractal.getCoord(range.x, range.x + range.width, displaySize, x);
            // Принимает y координату нажатия мышью
            int y = e.getY();
            double yCoord = fractal.getCoord(range.y, range.y + range.height, displaySize, y);
            // Вызыватеся метод генератора recentAndZoomRange() с координатами, по которым идет нажатие, и с масштабом 0,5
            fractal.recenterAndZoomRange(range, xCoord, yCoord, 0.5);

            // Перерисовывает фрактал после приближения отображения
            drawFractal();
        }
    }
    //Статический метод main () для запуска FractalExplorer. Инициализирует новый экземпляр FractalExplorer с размером дисплея 800, вызывает createAndShowGUI () в объекте проводника, а затем вызывает drawFractal () в проводнике, чтобы увидеть исходный вид.
    public static void main(String[] args)
    {
        FractalExplorer displayExplorer = new FractalExplorer(800);
        displayExplorer.createAndShowGUI();
        displayExplorer.drawFractal();
    }
}