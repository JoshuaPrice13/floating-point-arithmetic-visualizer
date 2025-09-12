import javax.swing.JFrame;
import javax.swing.JPanel;

public class FPAV_Frame extends JFrame{
    //Contrutor
    public FPAV_Frame (){
        setTitle("floating-point-arithmetic-simulator");
        setSize(900,800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        

        JPanel p = new FPAV_Panel();
        add(p);

        setVisible(true);
    }
}