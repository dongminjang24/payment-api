package com.payment.common.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SingleResponse<T> extends CommonResponse {
	private T data;

	public SingleResponse(T data) {
		super(SUCCESS_CODE, SUCCESS_MESSAGE);
		this.data = data;
	}
}

