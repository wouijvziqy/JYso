package com.qi4l.JYso.gadgets.utils.jre;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.ObjectOutputStream;

public class TCJavaObject extends ReferencableObject implements SerializedElement {
    private ObjectOutputStream objOut;

    private ByteArrayOutputStream byteOut;

    private Object obj;

    public TCJavaObject(Object obj, ByteArrayOutputStream byteOut, ObjectOutputStream objOut) {
        this.obj = obj;
        this.byteOut = byteOut;
        this.objOut = objOut;
    }

    public Object getHandleObject() {
        return this.obj;
    }

    public void doWrite(DataOutputStream out, HandleContainer handles) throws Exception {
        ObjectOutputStream oos = this.objOut;
        oos.writeObject(this.obj);
        out.write(this.byteOut.toByteArray(), 4, this.byteOut.size() - 4);
    }
}
