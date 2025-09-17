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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.awt.BorderLayout;

public class FPAV_Panel extends JPanel{

    public static FPAV_Model m = new FPAV_Model();

    public static JTextArea resultArea;
    public static JTextField decimalField1;
    public static JTextField decimalField2;
    
    // Modern color scheme
    private static final Color BACKGROUND_COLOR = new Color(45, 45, 50);
    private static final Color PANEL_COLOR = new Color(55, 55, 60);
    private static final Color BUTTON_COLOR = new Color(70, 130, 180);
    private static final Color BUTTON_HOVER = new Color(100, 149, 237);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color FIELD_COLOR = new Color(65, 65, 70);
    
    public FPAV_Panel(){
        setBackground(BACKGROUND_COLOR);
        placeComponents();
    }
        
    public void placeComponents(){
        System.out.println("Placing components now");
    
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create main input panel
        JPanel mainInputPanel = createInputPanel();
        
        // Create result panel
        JPanel resultPanel = createResultPanel();
        
        // Add panels to main layout
        add(mainInputPanel, BorderLayout.NORTH);
        add(resultPanel, BorderLayout.CENTER);
    }
    
    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setBackground(PANEL_COLOR);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 85), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Fields panel
        JPanel fieldsPanel = new JPanel(new GridLayout(2, 2, 10, 15));
        fieldsPanel.setBackground(PANEL_COLOR);
        
        // Create styled labels and fields
        JLabel userLabel1 = createStyledLabel("Decimal Number 1:");
        decimalField1 = createStyledTextField();
        
        JLabel userLabel2 = createStyledLabel("Decimal Number 2:");
        decimalField2 = createStyledTextField();
        
        fieldsPanel.add(userLabel1);
        fieldsPanel.add(decimalField1);
        fieldsPanel.add(userLabel2);
        fieldsPanel.add(decimalField2);
        
        // Operator buttons panel - grouped together and smaller
        JPanel operatorPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 10));
        operatorPanel.setBackground(PANEL_COLOR);
        
        JButton addButton = createOperatorButton("+");
        JButton subtractButton = createOperatorButton("−");
        JButton multiplyButton = createOperatorButton("×");
        JButton divideButton = createOperatorButton("÷");
        
        operatorPanel.add(addButton);
        operatorPanel.add(subtractButton);
        operatorPanel.add(multiplyButton);
        operatorPanel.add(divideButton);
        
        // Add action listeners
        addButton.addActionListener(operationButton("addButton"));
        subtractButton.addActionListener(operationButton("subtractButton"));
        multiplyButton.addActionListener(operationButton("multiplyButton"));
        divideButton.addActionListener(operationButton("divideButton"));
        
        inputPanel.add(fieldsPanel, BorderLayout.CENTER);
        inputPanel.add(operatorPanel, BorderLayout.SOUTH);
        
        return inputPanel;
    }
    
    private JPanel createResultPanel() {
        JPanel resultPanel = new JPanel(new BorderLayout(10, 10));
        resultPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel resultLabel = createStyledLabel("Result:");
        resultLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        resultArea = new JTextArea(12, 50);
        resultArea.setEditable(false);
        resultArea.setBackground(FIELD_COLOR);
        resultArea.setForeground(TEXT_COLOR);
        resultArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        resultArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 85), 1));
        scrollPane.getViewport().setBackground(FIELD_COLOR);
        
        resultPanel.add(resultLabel, BorderLayout.NORTH);
        resultPanel.add(scrollPane, BorderLayout.CENTER);
        
        return resultPanel;
    }
    
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_COLOR);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return label;
    }
    
    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setBackground(FIELD_COLOR);
        field.setForeground(TEXT_COLOR);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 85), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setCaretColor(TEXT_COLOR);
        return field;
    }
    
    private JButton createOperatorButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(BUTTON_COLOR);
        button.setForeground(TEXT_COLOR);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(50, 40));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 85), 1));
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(BUTTON_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(BUTTON_COLOR);
            }
        });
        
        return button;
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
                    if(buttonName.equals("addButton")){
                        binaryResult = m.bitwiseAddition(binary1, binary2);
                        result = m.ieee754ToDecimal(binaryResult);
                        resultArea.setText("Binary 1: " + binary1 + 
                                    "\nBinary 2: " + binary2 + 
                                    "\nResult (Binary): " + binaryResult + 
                                    "\nResult (Decimal): " + result);
                    }
                    //Subtraction
                    if(buttonName.equals("subtractButton")){
                        binaryResult = m.bitwiseSubtraction(binary1, binary2);
                        result = m.ieee754ToDecimal(binaryResult);
                        resultArea.setText("Binary 1: " + binary1 + 
                                    "\nBinary 2: " + binary2 + 
                                    "\nResult (Binary): " + binaryResult + 
                                    "\nResult (Decimal): " + result);
                    }
                    //Multiplication
                    if(buttonName.equals("multiplyButton")){
                        binaryResult = m.bitwiseMultiplication(binary1, binary2);
                        result = m.ieee754ToDecimal(binaryResult);
                        resultArea.setText("Binary 1: " + binary1 + 
                                    "\nBinary 2: " + binary2 + 
                                    "\nResult (Binary): " + binaryResult + 
                                    "\nResult (Decimal): " + result);
                    }
                    //Division
                    if(buttonName.equals("divideButton")){
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