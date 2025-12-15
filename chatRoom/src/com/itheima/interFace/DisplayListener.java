package com.itheima.interFace;

import com.itheima.domain.Display_Case;

import java.io.IOException;

//专门负责Display_Case展示框的的接口
@FunctionalInterface
public interface DisplayListener {
    void method(Display_Case d);
}
