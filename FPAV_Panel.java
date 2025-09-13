/*
This panel sits on the frame and acts as a controller for the 
model that utilizes the binary operations.
 */

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.awt.BorderLayout;

public class FPAV_Panel extends JPanel{

    public static FPAV_Model m = new FPAV_Model();

    public static JTextArea resultArea;
    public static JTextField decimalField1;
    public static JTextField decimalField2;
    
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
        decimalField1 = new JTextField(20);
        
        JLabel userLabel2 = new JLabel("Enter decimal number 2:");
        decimalField2 = new JTextField(20);
        
        JButton addButton = new JButton("Add");
        JButton subtractButton = new JButton("Subtract");
        JButton multiplyButton = new JButton("Multiply");
        JButton divideButton = new JButton("Divide");
        
        // Add components to input panel
        inputPanel.add(userLabel1);
        inputPanel.add(decimalField1);
        inputPanel.add(userLabel2);
        inputPanel.add(decimalField2);
        inputPanel.add(new JLabel()); // Empty space
        inputPanel.add(addButton);
        inputPanel.add(subtractButton);
        inputPanel.add(multiplyButton);
        inputPanel.add(divideButton);
        
        // Result area
        JLabel resultLabel = new JLabel("Result:");
        resultArea = new JTextArea(10, 50);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        resultPanel.add(resultLabel, BorderLayout.NORTH);
        resultPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add panels to main layout
        add(inputPanel, BorderLayout.NORTH);
        add(resultPanel, BorderLayout.CENTER);
        

        addButton.addActionListener(operationButton("addButton"));
        subtractButton.addActionListener(operationButton("subtractButton"));
        multiplyButton.addActionListener(operationButton("multiplyButton"));
        divideButton.addActionListener(operationButton("divideButton"));
    }

    private ActionListener operationButton(String buttonName){
        ActionListener tmp = new ActionListener() {
            
            double num1;
            double num2;
            String binary1;
            String binary2;
            String binaryResult;
            double result;

            boolean validInput = true;

            public void actionPerformed(ActionEvent e) {
                validInput = true;
                try {
                    num1 = Double.parseDouble(decimalField1.getText());
                    num2 = Double.parseDouble(decimalField2.getText());
                    binary1 = m.decimalToIEEE754(num1);
                    binary2 = m.decimalToIEEE754(num2);
                } catch (NumberFormatException ex) {
                    resultArea.setText("Error: Please enter valid decimal numbers.");
                    validInput = false;
                }
                if (validInput){
                    //Addition
                    if(buttonName == "addButton"){
                        binaryResult = m.bitwiseAddition(binary1, binary2);
                        result = m.ieee754ToDecimal(binaryResult);
                        resultArea.setText("Binary 1: " + binary1 + 
                                    "\nBinary 2: " + binary2 + 
                                    "\nResult (Binary): " + binaryResult + 
                                    "\nResult (Decimal): " + result);
                    }
                    //Subtraction
                    if(buttonName == "subtractButton"){
                        binaryResult = m.bitwiseSubtraction(binary1, binary2);
                        result = m.ieee754ToDecimal(binaryResult);
                        resultArea.setText("Binary 1: " + binary1 + 
                                    "\nBinary 2: " + binary2 + 
                                    "\nResult (Binary): " + binaryResult + 
                                    "\nResult (Decimal): " + result);
                    }
                    //Multiplication
                    if(buttonName == "multiplyButton"){
                        binaryResult = m.bitwiseMultiplication(binary1, binary2);
                        result = m.ieee754ToDecimal(binaryResult);
                        resultArea.setText("Binary 1: " + binary1 + 
                                    "\nBinary 2: " + binary2 + 
                                    "\nResult (Binary): " + binaryResult + 
                                    "\nResult (Decimal): " + result);
                    }
                    //Division
                    if(buttonName == "divideButton"){
                        binaryResult = m.bitwiseDivision(binary1, binary2);
                        if (binaryResult == null){
                            resultArea.setText("Binary 1: " + binary1 + 
                                    "\nBinary 2: " + binary2 + 
                                    "\nResult: NaN (Cannot Divide by zero)");
                            
                        }
                        else{
                            result = m.ieee754ToDecimal(binaryResult);
                            resultArea.setText("Binary 1: " + binary1 + 
                                    "\nBinary 2: " + binary2 + 
                                    "\nResult (Binary): " + binaryResult + 
                                    "\nResult (Decimal): " + result);
                        }
                        
                    }
                        
                }
            }
                
        };
        return tmp;
    }
}
