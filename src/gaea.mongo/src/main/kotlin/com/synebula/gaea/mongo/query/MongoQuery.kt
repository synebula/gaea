package com.synebula.gaea.mongo.query

import com.synebula.gaea.ext.fieldNames
import com.synebula.gaea.ext.firstCharLowerCase
import com.synebula.gaea.log.ILogger
import com.synebula.gaea.mongo.order
import com.synebula.gaea.mongo.select
import com.synebula.gaea.mongo.where
import com.synebula.gaea.mongo.whereId
import com.synebula.gaea.query.IQuery
import com.synebula.gaea.query.Page
import com.synebula.gaea.query.Params
import com.synebula.gaea.query.annotation.Table
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query

/**
 * 实现IQuery的Mongo查询类
 * @param template MongoRepo对象
 */

open class MongoQuery(var template: MongoTemplate, var logger: ILogger? = null) : IQuery {

    /**
     * 使用View解析是collection时是否校验存在，默认不校验
     */
    var validViewCollection = false

    override fun <TView> list(params: Map<String, Any>?, clazz: Class<TView>): List<TView> {
        val fields = clazz.fieldNames()
        val query = Query()
        query.where(params, clazz)
        query.select(fields.toTypedArray())
        return this.template.find(query, clazz, this.collection(clazz))
    }

    override fun <TView> count(params: Map<String, Any>?, clazz: Class<TView>): Int {
        val query = Query()
        return this.template.count(query.where(params, clazz), this.collection(clazz)).toInt()
    }

    override fun <TView> paging(params: Params, clazz: Class<TView>): Page<TView> {
        val query = Query()
        val fields = clazz.fieldNames()
        val result = Page<TView>(params.page, params.size)
        result.total = this.count(params.parameters, clazz)
        //如果总数和索引相同，说明该页没有数据，直接跳到上一页
        if (result.total == result.index) {
            params.page -= 1
            result.page -= 1
        }
        query.select(fields.toTypedArray())
        query.where(params.parameters, clazz)
        query.with(order(params.orders))
        query.skip(params.index).limit(params.size)
        result.data = this.template.find(query, clazz, this.collection(clazz))
        return result
    }

    override fun <TView, TKey> get(id: TKey, clazz: Class<TView>): TView? {
        return this.template.findOne(whereId(id), clazz, this.collection(clazz))
    }

    /**
     * 获取collection
     */
    fun <TView> collection(clazz: Class<TView>): String {
        val table: Table? = clazz.getDeclaredAnnotation(
            Table::class.java
        )
        return if (table != null)
            return table.name
        else {
            this.logger?.info(this, "视图类没有标记[Collection]注解，无法获取Collection名称。尝试使用View<${clazz.name}>名称解析集合")
            val name = clazz.simpleName.removeSuffix("View").firstCharLowerCase()
            if (!validViewCollection || this.template.collectionExists(name))
                name
            else {
                throw RuntimeException("找不到名为[$table]的集合")
            }
        }
    }
}
