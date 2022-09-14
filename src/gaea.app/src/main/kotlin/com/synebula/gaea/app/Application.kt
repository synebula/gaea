package com.synebula.gaea.app

import com.synebula.gaea.app.cmd.ICommandApp
import com.synebula.gaea.app.query.IQueryApp
import com.synebula.gaea.data.message.HttpMessageFactory
import com.synebula.gaea.domain.service.ICommand
import com.synebula.gaea.domain.service.IService
import com.synebula.gaea.log.ILogger
import com.synebula.gaea.query.IQuery
import org.springframework.beans.factory.annotation.Autowired

/**
 * 联合服务，同时实现了ICommandApp和IQueryApp接口
 *
 * @param name 业务名称
 * @param service 业务domain服务
 * @param query 业务查询服务
 * @param logger 日志组件
 */
open class Application<TCommand : ICommand, TView, ID>(
    override var name: String,
    override var service: IService<ID>,
    override var query: IQuery<TView, ID>,
    override var logger: ILogger,
) : ICommandApp<TCommand, ID>, IQueryApp<TView, ID> {

    @Autowired
    override lateinit var httpMessageFactory: HttpMessageFactory
}