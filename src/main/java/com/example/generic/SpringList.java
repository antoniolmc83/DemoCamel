package com.example.generic;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class SpringList<T> extends ArrayList{

	public static void main(String[] args) {

		SpringList<String> list = new SpringList<>();

		  Type superClassType = list.getClass().getGenericSuperclass();

		  ParameterizedType parameterizedType = (ParameterizedType)superClassType;

		  System.out.println(parameterizedType.getActualTypeArguments()[0]);

	}

	
}
