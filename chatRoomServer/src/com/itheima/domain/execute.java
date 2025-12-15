package com.itheima.domain;

import java.io.IOException;

@FunctionalInterface
public interface execute {
    //定义有参无返回值的函数式接口，用于作为map映射的第二参数
    void method(Object obj) throws IOException;
}
