import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.util.concurrent.Flow;
import java.awt.event.ActionEvent;

import java.awt.FlowLayout;

public class FPAV_Panel extends JPanel{

    public static FPAV_Model m = new FPAV_Model();
    
    public FPAV_Panel (){
        setBackground(Color.BLACK);
    }
        

    public void paintComponent(Graphics g){
        super.paintComponent(g);

        setLayout(new FlowLayout());

        JLabel userLabel = new JLabel("Enter decimal number 1:");
        userLabel.setBounds(10, 20, 200, 25);
        add(userLabel);

        JTextField decimalField1 = new JTextField(20);
        decimalField1.setBounds(220, 20, 165, 25);
        add(decimalField1);

        JLabel userLabel2 = new JLabel("Enter decimal number 2:");
        userLabel2.setBounds(10, 50, 200, 25);
        add(userLabel2);

        JTextField decimalField2 = new JTextField(20);
        decimalField2.setBounds(220, 50, 165, 25);
        add(decimalField2);

        JLabel resultLabel = new JLabel("Result:");
        resultLabel.setBounds(10, 110, 200, 25);
        add(resultLabel);

        JTextArea resultArea = new JTextArea();
        resultArea.setBounds(220, 110, 500, 300);
        add(resultArea);

        JButton addButton = new JButton("Add");
        addButton.setBounds(10, 80, 80, 25);
        add(addButton);
        
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                double num1 = Double.parseDouble(decimalField1.getText());
                double num2 = Double.parseDouble(decimalField2.getText());
                String binary1 = decimalToIEEE754(num1);
                String binary2 = decimalToBinaryString(num2);
                String binaryResult = binaryAddition(binary1, binary2);
                double result = binaryStringToDecimal(binaryResult);
                resultArea.setText("Binary 1: " + binary1 + "\nBinary 2: " + binary2 + "\nResult (Binary): " + binaryResult + "\nResult (Decimal): " + result);
            }
        });
        

        

        repaint();
        
    }
}
