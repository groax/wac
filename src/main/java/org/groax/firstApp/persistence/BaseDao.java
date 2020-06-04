package org.groax.firstApp.persistence;

import java.util.ArrayList;

public interface BaseDao<T> {
    ArrayList<T> findAll();
    T save(T t);
    int update(T t);
    T delete(T t);
}