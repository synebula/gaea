package com.synebula.gaea.mongodb.autoconfig

import com.synebula.gaea.spring.autoconfig.Factory
import com.synebula.gaea.spring.autoconfig.Proxy
import org.springframework.beans.factory.BeanFactory

class MongodbRepoFactory(
    supertype: Class<*>,
    var beanFactory: BeanFactory,
) : Factory(supertype) {
    override fun createProxy(): Proxy {
        return MongodbRepoProxy(supertype, this.beanFactory)
    }
}