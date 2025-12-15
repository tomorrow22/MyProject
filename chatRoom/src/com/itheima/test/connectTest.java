package com.itheima.test;

import com.itheima.util.Client;
import com.itheima.util.ComponentUtil;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.AttributedCharacterIterator;
import java.util.Scanner;

public class connectTest {
    public static void main(String[] args) throws IOException {
        Font font = new Font("宋体",Font.BOLD,17);
        for (String name : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
            System.out.println(name);
        }
    }
}
