package com.xframe.network;

public interface OnData {
    default void handle(String ds) {

    }

    default void connect(SocketManage socketManage) {

    }

    default void error(String error) {

    }

}
