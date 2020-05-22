package com.synebula.gaea.app.query

import com.synebula.gaea.log.ILogger
import com.synebula.gaea.query.IGenericQuery

/**
 * 联合服务，同时实现了ICommandApp和IQueryApp接口
 *
 * @param name 业务名称
 * @param genericQuery 业务查询服务
 * @param logger 日志组件
 */
open class QueryGenericApp<TView, TKey>(
    override var name: String,
    override var query: IGenericQuery<TView, TKey>?,
    override var logger: ILogger
) : IQueryGenericApp<TView, TKey> {
}