package com.it.reggie_takeaway.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 自定义的一个元数据对象处理器
 * 可以指定插入还是更新的时候做的动作(执行sql语句时会自动填装
 * name是实体类需要填装的属性名
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

	@Override
	public void insertFill(MetaObject metaObject) {
		metaObject.setValue("createTime", LocalDateTime.now());
		metaObject.setValue("updateTime", LocalDateTime.now());
		metaObject.setValue("createUser", BaseContext.getCunrrentId());
		metaObject.setValue("updateUser", BaseContext.getCunrrentId());
	}

	@Override
	public void updateFill(MetaObject metaObject) {
		metaObject.setValue("updateTime", LocalDateTime.now());
		metaObject.setValue("updateUser", BaseContext.getCunrrentId());
	}
}
