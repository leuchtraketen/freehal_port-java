package net.freehal.core.logs.receiver;

import net.freehal.core.storage.Serializer;
import net.freehal.core.util.LogUtils;

public class StackTraceElementStringSerializer implements Serializer<StackTraceElement> {

	private Serializer<String> stringSerializer = new Serializer.StringSerializer();

	@Override
	public String toString(StackTraceElement o) {
		if (o != null) {
			return o.getClassName() + "|" + o.getMethodName() + "|" + o.getFileName() + "|"
					+ o.getLineNumber();
		} else {
			LogUtils.w("unable to serialize: " + o);
			return null;
		}
	}

	@Override
	public StackTraceElement fromString(String serialized) {
		if (serialized != null) {
			String[] splitted = serialized.split("[|]");
			if (splitted.length == 4) {
				return new StackTraceElement(splitted[0], splitted[1], splitted[2],
						Integer.parseInt(splitted[3]));
			}
		}

		LogUtils.w("unable to deserialize: " + serialized);
		return null;
	}

	@Override
	public Iterable<String> toStringIterator(StackTraceElement object) {
		return stringSerializer.toStringIterator(toString(object));
	}

	@Override
	public StackTraceElement fromStringIterator(Iterable<String> serialized) {
		return fromString(stringSerializer.fromStringIterator(serialized));
	}

}
