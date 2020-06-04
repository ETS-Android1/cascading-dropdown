package com.sample.tanay.dynamicspinner;

import java.util.ArrayList;

final class DataDescriptor {

    public ArrayList<Property> properties = new ArrayList<>();

    public ArrayList<DataDescriptor> children = new ArrayList<>();

    final class Property {

        public String name;

        public String type;

        public Boolean isId;
    }
}
