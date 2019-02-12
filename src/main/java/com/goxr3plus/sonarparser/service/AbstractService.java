package main.java.com.goxr3plus.sonarparser.service;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Produces;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;

@Validated
@Produces({ MediaType.APPLICATION_JSON_VALUE })
public abstract class AbstractService {

	@Autowired
	protected ApplicationContext ctx;

//	@Autowired
//	protected Mapper dozerMapper;
//
//	protected <T, U> U map(final T source, final Class<U> destType) {
//		return source == null ? null : dozerMapper.map(source, destType);
//	}
//
//	protected <T, U> ArrayList<U> toList(final List<T> source, final Class<U> destType) {
//		return source == null ? null : DozerHelper.map(dozerMapper, source, destType);
//	}
//
//	protected <T, U> Page<U> toPage(final Page<T> source, final Class<U> destType) {
//		return source == null ? null : DozerHelper.map(dozerMapper, source, destType);
//	}

}