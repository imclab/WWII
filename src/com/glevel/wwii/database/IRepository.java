package com.glevel.wwii.database;

import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;

public interface IRepository<T> {

    public List<T> getAll(String orderBy, String limit);

    public T getById(long id);

    public long add(T entity);

    public void update(T entity);

    public void delete(long id);

    public List<T> convertCursorToObjectList(Cursor c);

    public T convertCursorToSingleObject(Cursor c);

    public T convertCursorRowToObject(Cursor c);

    public ContentValues getContentValues(T entity);

}
