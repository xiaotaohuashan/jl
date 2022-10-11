package com.jl.core.view.dialog;

public interface DialogCallback {
    default void onOk(){}
    default void onCancel(){}
}
