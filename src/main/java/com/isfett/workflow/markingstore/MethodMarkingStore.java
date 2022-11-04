package com.isfett.workflow.markingstore;

import com.isfett.workflow.Marking;
import com.isfett.workflow.exception.InvalidMethodMarkingStoreConfigurationException;
import com.isfett.workflow.exception.MethodNotFoundInClassException;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class MethodMarkingStore implements MarkingStoreInterface {
    private final String property;
    private Boolean singleState = false;

    public MethodMarkingStore(@NotNull Boolean singleState, @NotNull String property) {
        this.singleState = singleState;
        this.property = property;
    }

    public MethodMarkingStore(@NotNull String property) {
        this.property = property;
    }

    @Override
    public @NotNull Boolean isSingleState() {
        return this.singleState;
    }

    @SuppressWarnings({"unchecked","rawtypes"})
    public @NotNull Marking getMarking(@NotNull Object subject) {
        String methodName = "get" + this.property.substring(0, 1).toUpperCase() + this.property.substring(1);
        Method methodFound = null;

        Method[] methods = subject.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName) && Modifier.isPublic(method.getModifiers())) {
                methodFound = method;
                break;
            }
        }

        if (null == methodFound) {
            throw new MethodNotFoundInClassException(methodName, subject.getClass().getName());
        }

        List<String> markingValue = new ArrayList<>();

        try {
            if (this.singleState) {
                String value = null;
                if (methodFound.getReturnType().isEnum()) {
                    Enum<?> tempValue = (Enum<?>) methodFound.invoke(subject);
                    if (null != tempValue) {
                        value = tempValue.toString();
                    }
                } else {
                    value = (String) methodFound.invoke(subject);
                }
                if (null != value) {
                    markingValue.add(value);
                }
            } else {
                List<String> value = null;
                if (((Class<?>) ((ParameterizedType) methodFound.getGenericReturnType()).getActualTypeArguments()[0]).isEnum()) {
                    List<Enum> tempValue = (List<Enum>) methodFound.invoke(subject);
                    if (null != tempValue) {
                        value = tempValue.stream().map(Enum::toString).toList();
                    }
                } else {
                    value = (List<String>) methodFound.invoke(subject);
                }
                if (null != value) {
                    markingValue = value;
                }
            }
        } catch (Exception e) {
            throw new InvalidMethodMarkingStoreConfigurationException(methodName, subject.getClass().getName(), e);
        }

        return new Marking(markingValue);
    }

    @SuppressWarnings({"unchecked","rawtypes"})
    public void setMarking(@NotNull Object subject, @NotNull Marking marking) {
        List<String> places = marking.getPlaces();

        String methodName = "set" + this.property.substring(0, 1).toUpperCase() + this.property.substring(1);

        Method methodFound = null;

        Method[] methods = subject.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName) && Modifier.isPublic(method.getModifiers())) {
                methodFound = method;
                break;
            }
        }

        if (null == methodFound) {
            throw new MethodNotFoundInClassException(methodName, subject.getClass().getName());
        }

        try {
            if (this.singleState) {
                if (Arrays.stream(methodFound.getParameterTypes()).toList().get(0).isEnum()) {
                    Class<Enum> classname = (Class<Enum>) Arrays.stream(methodFound.getParameterTypes()).toList().get(0);
                    methodFound.invoke(subject, Enum.valueOf(classname, places.get(places.size() - 1)));
                } else {
                    methodFound.invoke(subject, places.get(places.size() - 1));
                }
            } else {
                if (((Class<?>) ((ParameterizedType) Arrays.stream(methodFound.getGenericParameterTypes()).toList().get(0)).getActualTypeArguments()[0]).isEnum()) {
                    Class classname = (Class<Enum>) ((ParameterizedType) Arrays.stream(methodFound.getGenericParameterTypes()).toList().get(0)).getActualTypeArguments()[0];
                    List<Enum> newPlaces = new ArrayList();
                    for (String place : places) {
                        newPlaces.add(Enum.valueOf(classname, place));
                    }
                    methodFound.invoke(subject, newPlaces);
                } else {
                    methodFound.invoke(subject, places);
                }
            }
        } catch (Exception e) {
            throw new InvalidMethodMarkingStoreConfigurationException(methodName, subject.getClass().getName(), e);
        }
    }
}
