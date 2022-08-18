package com.synebula.gaea.app.autoconfig.service

import com.synebula.gaea.spring.autoconfig.Factory
import com.synebula.gaea.spring.autoconfig.Proxy
import org.springframework.beans.factory.BeanFactory

class ServiceFactory(
    supertype: Class<*>,
    var beanFactory: BeanFactory,
    var implementBeanNames: Array<String> = arrayOf()
) : Factory(supertype) {
    override fun createProxy(): Proxy {
        return ServiceProxy(supertype, this.beanFactory, this.implementBeanNames)
    }
}