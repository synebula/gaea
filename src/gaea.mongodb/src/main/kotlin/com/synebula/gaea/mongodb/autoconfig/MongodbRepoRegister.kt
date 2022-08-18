package com.synebula.gaea.mongodb.autoconfig

import com.synebula.gaea.domain.repository.IRepository
import com.synebula.gaea.query.IQuery
import com.synebula.gaea.spring.autoconfig.Register
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.support.GenericBeanDefinition
import org.springframework.core.annotation.AnnotationAttributes
import org.springframework.core.type.AnnotationMetadata

class MongodbRepoRegister : Register() {
    override fun scan(metadata: AnnotationMetadata): Map<String, BeanDefinition> {
        val result = mutableMapOf<String, BeanDefinition>()

        // 获取注解参数信息:basePackages
        val attributes = AnnotationAttributes(
            metadata.getAnnotationAttributes(
                MongodbRepoScan::class.java.name
            ) ?: mapOf()
        )
        val basePackages = attributes.getStringArray("basePackages")
        val beanDefinitions = this.doScan(
            basePackages,
            arrayOf(this.interfaceFilter(arrayOf(IRepository::class.java, IQuery::class.java)))
        )
        beanDefinitions.forEach { beanDefinition ->
            // 获取实际的bean类型
            val beanClazz: Class<*> = try {
                Class.forName(beanDefinition.beanClassName)
            } catch (ex: ClassNotFoundException) {
                throw ex
            }

            // 尝试获取实际继承类型
            val implBeanDefinitions = this.doScan(basePackages, arrayOf(this.interfaceFilter(arrayOf(beanClazz))))
            implBeanDefinitions.forEach {
                it.isAutowireCandidate = false
                result[it.beanClassName!!] = it
            }

            // 构造BeanDefinition
            val builder = BeanDefinitionBuilder.genericBeanDefinition(beanClazz)
            builder.addConstructorArgValue(beanClazz)
            builder.addConstructorArgValue(this._beanFactory)
            builder.addConstructorArgValue(implBeanDefinitions.map { it.beanClassName })
            val definition = builder.rawBeanDefinition as GenericBeanDefinition
            definition.beanClass = MongodbRepoFactory::class.java
            definition.autowireMode = GenericBeanDefinition.AUTOWIRE_BY_TYPE
            result[beanClazz.name] = definition
        }
        return result
    }
}