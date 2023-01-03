package com.dao;

import java.util.List;

import com.model.ClassTypeTable;

public interface IClassTypeTableDao {

    public abstract List<ClassTypeTable> getClassTypeList();

    public abstract List<String> getClassTypeStringList();

    public abstract int deleteAll();

    public abstract int insertAll(List<String> classTypeList);

}