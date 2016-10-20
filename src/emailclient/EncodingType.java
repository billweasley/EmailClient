/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emailclient;

/**
 *
 * @author haoxuan
 */
public enum EncodingType {
    BASE64("BASE64"), QP("QUOTED-PRINTABLE"), ASCII_8("8BIT"), ASCII_7("7BIT"), BINARY("BINARY");
    private String typeName;

    @Override
    public String toString() {
        return typeName;
    }

    private EncodingType(String typeName) {
        this.typeName = typeName;
    }
}
