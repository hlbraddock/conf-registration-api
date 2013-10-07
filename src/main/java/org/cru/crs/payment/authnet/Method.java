package org.cru.crs.payment.authnet;

public interface Method {

	public void checkProperties(Transaction transaction) throws IllegalArgumentException;

	public String getCode();

}
