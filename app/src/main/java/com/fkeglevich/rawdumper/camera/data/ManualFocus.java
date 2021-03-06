/*
 * Copyright 2017, Flávio Keglevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fkeglevich.rawdumper.camera.data;

import androidx.annotation.NonNull;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 26/10/17.
 */

public class ManualFocus implements Comparable<ManualFocus>, ParameterValue
{
    private static final int INVALID_NUMERIC_VALUE = 0;

    public static final ManualFocus DISABLED = new ManualFocus(INVALID_NUMERIC_VALUE);

    public static ManualFocus create(int numericValue)
    {
        if (numericValue <= INVALID_NUMERIC_VALUE)
            throw new IllegalArgumentException();

        return new ManualFocus(numericValue);
    }

    private final int numericValue;

    public static ManualFocus parse(String value)
    {
        int numeric = value != null ? Integer.parseInt(value) : 0;
        if (numeric == 0)
            return ManualFocus.DISABLED;

        return ManualFocus.create(numeric);
    }

    private ManualFocus(int numericValue)
    {
        this.numericValue = numericValue;
    }

    public int getNumericValue()
    {
        if (equals(DISABLED))
            throw new RuntimeException("Disabled manual focus doesn't have a numeric value!");

        return numericValue;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ManualFocus that = (ManualFocus) o;

        return numericValue == that.numericValue;
    }

    @Override
    public int hashCode()
    {
        return numericValue;
    }

    /**
     * Compares this object to the specified object to determine their relative
     * order.
     *
     * @param another the object to compare to this instance.
     * @return a negative integer if this instance is less than {@code another};
     * a positive integer if this instance is greater than
     * {@code another}; 0 if this instance has the same order as
     * {@code another}.
     * @throws ClassCastException if {@code another} cannot be converted into something
     *                            comparable to {@code this} instance.
     */
    @Override
    public int compareTo(@NonNull ManualFocus another)
    {
        int myNumeric = equals(DISABLED) ? Integer.MIN_VALUE : getNumericValue();
        int anotherNumeric = another.equals(DISABLED) ? Integer.MIN_VALUE : another.getNumericValue();
        return myNumeric - anotherNumeric;
    }

    @Override
    public String getParameterValue()
    {
        return equals(DISABLED) ? "0" : String.valueOf(getNumericValue());
    }
}
