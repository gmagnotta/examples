package org.gmagnotta;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Base64;

import com.google.protobuf.ByteString;

public class ProtoUtils {
    
    public static BigDecimal bigDecimalFromString(String encodedString) {

        int scale = 2;
        return new BigDecimal(new BigInteger(Base64.getDecoder().decode(encodedString)), scale);

    }

    public static org.gmagnotta.model.event.OrderOuterClass.BigDecimal convertToProtobuf(BigDecimal bigDecimal) {

        return org.gmagnotta.model.event.OrderOuterClass.BigDecimal.newBuilder()
                .setScale(bigDecimal.scale())
                .setPrecision(bigDecimal.precision())
                .setValue(ByteString.copyFrom(bigDecimal.unscaledValue().toByteArray()))
                .build();

    }

    public static BigDecimal fromProtoBuf(org.gmagnotta.model.event.OrderOuterClass.BigDecimal proto) {

		java.math.MathContext mc2 = new java.math.MathContext(proto.getPrecision());
		java.math.BigDecimal value = new java.math.BigDecimal(
				new java.math.BigInteger(proto.getValue().toByteArray()),
				proto.getScale(),
				mc2);

		return value;
	}
    
}
