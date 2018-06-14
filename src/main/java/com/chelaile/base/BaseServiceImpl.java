package com.chelaile.base;

import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * BaseService的实现类, 其他的自定义 ServiceImpl, 继承自它,可以获得常用的增删查改操作,
 * 未实现的方法有 子类各自实现
 * <p/>
 * Model : 代表数据库中的表 映射的Java对象类型
 * PK :代表对象的主键类型
 *
 * @author cxhuan
 */
public abstract class BaseServiceImpl<BasebaseDaoImpl extends BaseDao<Example, Entity, ID>, Example, Entity, ID>
        implements BaseService<BasebaseDaoImpl, Example, Entity, ID> {


    @Autowired
    protected BasebaseDaoImpl baseDaoImpl;

    @Override
    public int save(Entity m) {
        return baseDaoImpl.insert(m);
    }

    @Override
    public int saveSelective(Entity m) {
        return baseDaoImpl.insertSelective(m);
    }

    @Override
    public int deleteById(ID id) {
        return baseDaoImpl.deleteByPrimaryKey(id);
    }

    @Override
    public int deleteByExample(Example example) {
        return baseDaoImpl.deleteByExample(example);
    }

    @Override
    public int updateById(Entity m) {
        return baseDaoImpl.updateByPrimaryKey(m);
    }

    @Override
    public int updateByIdSelective(Entity m) {
        return baseDaoImpl.updateByPrimaryKeySelective(m);
    }

    @Override
    public int updateByExampleSelective(Entity record, Example example) {
        return baseDaoImpl.updateByExampleSelective(record, example);
    }

    @Override
    public int updateByExample(Entity record, Example example) {
        return baseDaoImpl.updateByExample(record, example);
    }

    @Override
    public Entity getById(ID id) {
        return baseDaoImpl.selectByPrimaryKey(id);
    }

    @Override
    public PageInfo<Entity> pageByExample(Example example, com.github.pagehelper.Page<Entity> page) {
        baseDaoImpl.selectByExample(example);
        return page.toPageInfo();
    }

    @Override
    public List<Entity> listByExample(Example example) {
        return baseDaoImpl.selectByExample(example);
    }
}
