package com.chelaile.base;

import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 所有自定义Service的顶级接口,封装常用的增删查改操作
 * <p/>
 * Model : 代表数据库中的表 映射的Java对象类型
 * PK :代表对象的主键类型
 *
 * @author cxhuan
 */
public interface BaseService<BaseDaoImpl extends BaseDao<Example, Entity, ID>, Example, Entity, ID> {
    /**
     * m 对象入库
     *
     * @param m
     * @return
     */
    int save(Entity m);

    /**
     * m 对象中属性字段不为空的入库
     *
     * @param m
     * @return
     */
    int saveSelective(Entity m);

    Entity getById(ID id);

    int deleteById(ID id);

    int deleteByExample(Example example);

    int updateById(Entity m);

    int updateByExampleSelective(Entity record, Example example);

    int updateByExample(Entity record, Example example);

    /**
     * 根据ID更新M对象中不为空的字段
     *
     * @param m
     * @return
     */
    int updateByIdSelective(Entity m);


    PageInfo<Entity> pageByExample(Example example, com.github.pagehelper.Page<Entity> page);

    List<Entity> listByExample(Example example);
}
