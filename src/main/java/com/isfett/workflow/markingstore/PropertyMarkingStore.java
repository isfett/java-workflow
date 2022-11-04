package com.isfett.workflow.markingstore;

import com.isfett.workflow.Marking;
import com.isfett.workflow.exception.InvalidPropertyMarkingStoreConfigurationException;
import com.isfett.workflow.exception.PropertyNotFoundInClassException;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class PropertyMarkingStore implements MarkingStoreInterface {
    private final String property;
    private Boolean singleState = false;

    public PropertyMarkingStore(@NotNull Boolean singleState, @NotNull String property) {
        this.singleState = singleState;
        this.property = property;
    }

    public PropertyMarkingStore(@NotNull String property) {
        this.property = property;
    }

    @Override
    public @NotNull Boolean isSingleState() {
        return this.singleState;
    }

    @SuppressWarnings({"unchecked","rawtypes"})
    public @NotNull Marking getMarking(@NotNull Object subject) {
        Field field;

        try {
            field = subject.getClass().getField(this.property);
        } catch (NoSuchFieldException e) {
            throw new PropertyNotFoundInClassException(this.property, subject.getClass().getName(), e);
        }

        List<String> markingValue = new ArrayList<>();

        try {
            if (this.singleState) {
                String value = null;
                if (((Class<?>) field.getGenericType()).isEnum()) {
                    Enum<?> tempValue = (Enum<?>) field.get(subject);
                    if (null != tempValue) {
                        value = tempValue.toString();
                    }
                } else {
                    value = (String) field.get(subject);
                }
                if (null != value) {
                    markingValue.add(value);
                }
            } else {
                List<String> value = null;
                if ((((Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]).isEnum())) {
                    List<Enum> tempValue = (List<Enum>) field.get(subject);
                    if (null != tempValue) {
                        value = tempValue.stream().map(Enum::toString).toList();
                    }
                } else {
                    value = (List<String>) field.get(subject);
                }
                if (null != value) {
                    markingValue = value;
                }
            }
        } catch (Exception e) {
            throw new InvalidPropertyMarkingStoreConfigurationException(field.getName(), subject.getClass().getName(), e);
        }

        return new Marking(markingValue);
    }

    @SuppressWarnings({"unchecked","rawtypes"})
    public void setMarking(@NotNull Object subject, @NotNull Marking marking) {
        List<String> places = marking.getPlaces();

        Field field;

        try {
            field = subject.getClass().getField(this.property);
        } catch (NoSuchFieldException e) {
            throw new PropertyNotFoundInClassException(this.property, subject.getClass().getName(), e);
        }

        try {
            if (this.singleState) {
                if (((Class<?>) field.getGenericType()).isEnum()) {
                    Class<Enum> classname = (Class<Enum>) field.getGenericType();
                    field.set(subject, Enum.valueOf(classname, places.get(places.size() - 1)));
                } else {
                    field.set(subject, places.get(places.size() - 1));
                }
            } else {
                if (((Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]).isEnum()) {
                    Class classname = ((Class<Enum>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]);
                    List<Enum> newPlaces = new ArrayList();
                    for (String place : places) {
                        newPlaces.add(Enum.valueOf(classname, place));
                    }
                    field.set(subject, newPlaces);
                } else {
                    field.set(subject, places);
                }
            }
        } catch (Exception e) {
            throw new InvalidPropertyMarkingStoreConfigurationException(field.getName(), subject.getClass().getName(), e);
        }
    }
}
