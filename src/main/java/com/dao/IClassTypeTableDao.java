package com.dao;

import java.util.List;

import com.model.ClassTypeTable;

public interface IClassTypeTableDao {

    List<ClassTypeTable> getClassTypeList();

    List<String> getClassTypeStringList();

    int deleteAll();

    int insertAll(List<String> classTypeList);

}