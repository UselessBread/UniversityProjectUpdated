package com.company.DataTypes;

import javax.swing.*;

public class TextFieldAndDouble {
    private JTextField textField;
    private Double doubleValue;

    public TextFieldAndDouble(JTextField textField,Double doubleValue){
        this.textField=textField;
        this.doubleValue=doubleValue;
    }
    public  TextFieldAndDouble(){
        textField=null;
        doubleValue=0.0;
    }

    public Double getDoubleValue() {
        return doubleValue;
    }

    public JTextField getTextField() {
        return textField;
    }
}
