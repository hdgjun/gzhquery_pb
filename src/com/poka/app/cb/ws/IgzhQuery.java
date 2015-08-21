package com.poka.app.cb.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;


@WebService
public interface IgzhQuery {
	@WebMethod(operationName = "gzhQueryList")
	@WebResult(name = "result")
	public String gzhQueryList(
			@WebParam(name = "mon") String mon);
}
