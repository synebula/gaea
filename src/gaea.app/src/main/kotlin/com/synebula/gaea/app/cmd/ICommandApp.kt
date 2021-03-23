package com.synebula.gaea.app.cmd

import com.synebula.gaea.app.IApplication
import com.synebula.gaea.app.component.HttpMessage
import com.synebula.gaea.app.component.aop.annotation.ExceptionMessage
import com.synebula.gaea.data.message.Status
import com.synebula.gaea.data.serialization.json.IJsonSerializer
import com.synebula.gaea.domain.service.ICommand
import com.synebula.gaea.domain.service.IService
import org.springframework.web.bind.annotation.*

/**
 * 应用类接口，提供向Command服务的接口
 *
 * @author alex
 * @version 0.1
 * @since 2020-05-15
 */
interface ICommandApp<TCommand : ICommand, TKey> : IApplication {
    var jsonSerializer: IJsonSerializer?

    var service: IService<TKey>?


    @PostMapping
    @ExceptionMessage("添加异常")
    fun add(@RequestBody command: TCommand): HttpMessage {
        val msg = HttpMessage()
        if (this.service != null) {
            msg.load(this.service!!.add(command))
        } else {
            msg.status = Status.Error
            msg.message = "没有对应服务，无法执行该操作"
        }
        return msg
    }

    @PutMapping("/{id:.+}")
    @ExceptionMessage("更新异常")
    fun update(@PathVariable id: TKey, @RequestBody command: TCommand): HttpMessage {
        val msg = HttpMessage()
        if (this.service != null)
            this.service!!.update(id, command)
        else {
            msg.status = Status.Error
            msg.message = "没有对应服务，无法执行该操作"
        }
        return msg
    }


    @DeleteMapping("/{id:.+}")
    @ExceptionMessage("删除异常")
    fun remove(@PathVariable id: TKey): HttpMessage {
        val msg = HttpMessage()
        if (this.service != null)
            msg.data = this.service!!.remove(id)
        else {
            msg.status = Status.Error
            msg.message = "没有对应服务，无法执行该操作"
        }
        return msg
    }
}
