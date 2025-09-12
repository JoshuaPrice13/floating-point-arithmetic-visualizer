import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.awt.BorderLayout;

public class FPAV_Panel extends JPanel{

    public static FPAV_Model m = new FPAV_Model();
    
    public FPAV_Panel (){
        setBackground(Color.GRAY);
        placeComponents();
    }
        

    public void placeComponents(){
        System.out.println("Placing components now");
    
        setLayout(new BorderLayout());
        
        // Create input panel for the top
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Input fields
        JLabel userLabel1 = new JLabel("Enter decimal number 1:");
        JTextField decimalField1 = new JTextField(20);
        
        JLabel userLabel2 = new JLabel("Enter decimal number 2:");
        JTextField decimalField2 = new JTextField(20);
        
        // Button
        JButton addButton = new JButton("Add");
        
        // Add components to input panel
        inputPanel.add(userLabel1);
        inputPanel.add(decimalField1);
        inputPanel.add(userLabel2);
        inputPanel.add(decimalField2);
        inputPanel.add(new JLabel()); // Empty space
        inputPanel.add(addButton);
        
        // Result area
        JLabel resultLabel = new JLabel("Result:");
        JTextArea resultArea = new JTextArea(10, 50);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        resultPanel.add(resultLabel, BorderLayout.NORTH);
        resultPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add panels to main layout
        add(inputPanel, BorderLayout.NORTH);
        add(resultPanel, BorderLayout.CENTER);
        
        // Action listener
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    double num1 = Double.parseDouble(decimalField1.getText());
                    double num2 = Double.parseDouble(decimalField2.getText());
                    String binary1 = m.decimalToIEEE754(num1);
                    String binary2 = m.decimalToIEEE754(num2);
                    String binaryResult = m.binaryAddition(binary1, binary2);
                    double result = m.ieee754ToDecimal(binaryResult);
                    
                    resultArea.setText("Binary 1: " + binary1 + 
                                    "\nBinary 2: " + binary2 + 
                                    "\nResult (Binary): " + binaryResult + 
                                    "\nResult (Decimal): " + result);
                } catch (NumberFormatException ex) {
                    resultArea.setText("Error: Please enter valid decimal numbers.");
                }
            }
        });
    }
}
