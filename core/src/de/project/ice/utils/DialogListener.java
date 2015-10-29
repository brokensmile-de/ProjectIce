package de.project.ice.utils;

public interface DialogListener<T>
{
    void onResult(T result);

    void onChange(T result);
    void onCancel();
}